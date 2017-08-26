/*
 * Copyright (c) 2017 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;
import java.util.RandomAccess;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.AbstractMultiReaderMutableCollection;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.lazy.ReverseIterable;
import org.eclipse.collections.impl.lazy.parallel.list.ListIterableParallelIterable;
import org.eclipse.collections.impl.lazy.parallel.list.MultiReaderParallelListIterable;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;
import org.eclipse.collections.impl.utility.LazyIterate;

import static org.eclipse.collections.impl.factory.Iterables.mList;

/**
 * MultiReadFastList provides a thread-safe wrapper around a FastList, using a ReentrantReadWriteLock.  In order to
 * provide true thread-safety, MultiReaderFastList does not implement iterator(), listIterator(), listIterator(int), or
 * get(int), as all of these methods require an external lock to be taken to provide thread-safe iteration.  All of
 * these methods are available however, if you use the withReadLockAndDelegate() or withWriteLockAndDelegate() methods.
 * Both of these methods take a parameter of type Procedure<MutableList>, and a wrapped version of the underlying
 * FastList is returned.  This wrapper guarantees that no external pointer can ever reference the underlying FastList
 * outside of a locked procedure.  In the case of the read lock method, an Unmodifiable version of the collection is
 * offered, which will throw UnsupportedOperationExceptions on any write methods like add or remove.
 */
public final class MultiReaderFastList<T>
        extends AbstractMultiReaderMutableCollection<T>
        implements RandomAccess, Externalizable, MutableList<T>
{
    private static final long serialVersionUID = 1L;

    private transient ReadWriteLock lock;
    private MutableList<T> delegate;

    /**
     * @deprecated Empty default constructor used for serialization.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public MultiReaderFastList()
    {
        // For Externalizable use only
    }

    private MultiReaderFastList(MutableList<T> newDelegate)
    {
        this(newDelegate, new ReentrantReadWriteLock());
    }

    private MultiReaderFastList(MutableList<T> newDelegate, ReadWriteLock newLock)
    {
        this.lock = newLock;
        this.delegate = newDelegate;
    }

    public static <T> MultiReaderFastList<T> newList()
    {
        return new MultiReaderFastList<>(FastList.newList());
    }

    public static <T> MultiReaderFastList<T> newList(int capacity)
    {
        return new MultiReaderFastList<>(FastList.newList(capacity));
    }

    public static <T> MultiReaderFastList<T> newList(Iterable<T> iterable)
    {
        return new MultiReaderFastList<>(FastList.newList(iterable));
    }

    public static <T> MultiReaderFastList<T> newListWith(T... elements)
    {
        return new MultiReaderFastList<>(FastList.newListWith(elements));
    }

    @Override
    protected MutableList<T> getDelegate()
    {
        return this.delegate;
    }

    @Override
    protected ReadWriteLock getLock()
    {
        return this.lock;
    }

    UntouchableMutableList<T> asReadUntouchable()
    {
        return new UntouchableMutableList<>(this.delegate.asUnmodifiable());
    }

    UntouchableMutableList<T> asWriteUntouchable()
    {
        return new UntouchableMutableList<>(this.delegate);
    }

    public void withReadLockAndDelegate(Procedure<MutableList<T>> procedure)
    {
        this.acquireReadLock();
        try
        {
            UntouchableMutableList<T> list = this.asReadUntouchable();
            procedure.value(list);
            list.becomeUseless();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    public void withWriteLockAndDelegate(Procedure<MutableList<T>> procedure)
    {
        this.acquireWriteLock();
        try
        {
            UntouchableMutableList<T> list = this.asWriteUntouchable();
            procedure.value(list);
            list.becomeUseless();
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> asSynchronized()
    {
        this.acquireReadLock();
        try
        {
            return SynchronizedMutableList.of(this);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> asUnmodifiable()
    {
        this.acquireReadLock();
        try
        {
            return UnmodifiableMutableList.of(this);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public ImmutableList<T> toImmutable()
    {
        this.acquireReadLock();
        try
        {
            return Lists.immutable.withAll(this.delegate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> clone()
    {
        this.acquireReadLock();
        try
        {
            return new MultiReaderFastList<>(this.delegate.clone());
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collect(function);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectBoolean(booleanFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectByte(byteFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectChar(charFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectDouble(doubleFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectFloat(floatFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectInt(intFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectLong(longFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectShort(shortFunction);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <V> MutableList<V> flatCollect(
            Function<? super T, ? extends Iterable<V>> function)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.flatCollect(function);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <V> MutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectIf(predicate, function);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <P, V> MutableList<V> collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.collectWith(function, parameter);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> newEmpty()
    {
        return MultiReaderFastList.newList();
    }

    @Override
    public MutableList<T> reject(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.reject(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <P> MutableList<T> rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.rejectWith(predicate, parameter);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> tap(Procedure<? super T> procedure)
    {
        this.acquireReadLock();
        try
        {
            this.forEach(procedure);
            return this;
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> select(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.select(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <P> MutableList<T> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.selectWith(predicate, parameter);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public PartitionMutableList<T> partition(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.partition(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.partitionWith(predicate, parameter);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.selectInstancesOf(clazz);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> distinct()
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.distinct();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.distinct(hashingStrategy);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    /**
     * @since 9.0.
     */
    @Override
    public <V> MutableList<T> distinctBy(Function<? super T, ? extends V> function)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.distinctBy(function);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> sortThis()
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThis();
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThis(Comparator<? super T> comparator)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThis(comparator);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public <V extends Comparable<? super V>> MutableList<T> sortThisBy(
            Function<? super T, ? extends V> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisBy(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByInt(IntFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByInt(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByBoolean(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByChar(CharFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByChar(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByByte(ByteFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByByte(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByShort(ShortFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByShort(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByFloat(FloatFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByFloat(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByLong(LongFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByLong(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> sortThisByDouble(DoubleFunction<? super T> function)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.sortThisByDouble(function);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> subList(int fromIndex, int toIndex)
    {
        this.acquireReadLock();
        try
        {
            return new MultiReaderFastList<>(this.delegate.subList(fromIndex, toIndex), this.lock);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public boolean equals(Object o)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.equals(o);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public int hashCode()
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.hashCode();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public T get(int index)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.get(index);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public Optional<T> getFirstOptional()
    {
        this.acquireReadLock();
        try
        {
            return this.getDelegate().getFirstOptional();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public Optional<T> getLastOptional()
    {
        this.acquireReadLock();
        try
        {
            return this.getDelegate().getLastOptional();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public int indexOf(Object o)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.indexOf(o);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public int lastIndexOf(Object o)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.lastIndexOf(o);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> with(T element)
    {
        this.add(element);
        return this;
    }

    @Override
    public MutableList<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    @Override
    public MutableList<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    @Override
    public MutableList<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    /**
     * This method is not supported directly on a MultiReaderFastList.  If you would like to use a ListIterator with
     * MultiReaderFastList, then you must do the following:
     * <p>
     * <pre>
     * multiReaderList.withReadLockAndDelegate(new Procedure&lt;MutableList&lt;Person&gt;&gt;()
     * {
     *     public void value(MutableList&lt;Person&gt; people)
     *     {
     *         Iterator it = people.listIterator();
     *         ....
     *     }
     * });
     * </pre>
     */
    @Override
    public ListIterator<T> listIterator()
    {
        throw new UnsupportedOperationException(
                "ListIterator is not supported for MultiReaderFastList.  "
                        + "If you would like to use a ListIterator, you must either use withReadLockAndDelegate() or withWriteLockAndDelegate().");
    }

    /**
     * This method is not supported directly on a MultiReaderFastList.  If you would like to use a ListIterator with
     * MultiReaderFastList, then you must do the following:
     * <p>
     * <pre>
     * multiReaderList.withReadLockAndDelegate(new Procedure&lt;MutableList&lt;Person&gt;&gt;()
     * {
     *     public void value(MutableList&lt;Person&gt; people)
     *     {
     *         Iterator it = people.listIterator(0);
     *         ....
     *     }
     * });
     * </pre>
     */
    @Override
    public ListIterator<T> listIterator(int index)
    {
        throw new UnsupportedOperationException(
                "ListIterator is not supported for MultiReaderFastList.  "
                        + "If you would like to use a ListIterator, you must either use withReadLockAndDelegate() or withWriteLockAndDelegate().");
    }

    @Override
    public T remove(int index)
    {
        this.acquireWriteLock();
        try
        {
            return this.delegate.remove(index);
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public T set(int index, T element)
    {
        this.acquireWriteLock();
        try
        {
            return this.delegate.set(index, element);
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        this.acquireWriteLock();
        try
        {
            return this.delegate.addAll(index, collection);
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public void add(int index, T element)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.add(index, element);
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.corresponds(other, predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super T> procedure)
    {
        this.acquireReadLock();
        try
        {
            this.delegate.forEach(startIndex, endIndex, procedure);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public int binarySearch(T key, Comparator<? super T> comparator)
    {
        this.acquireReadLock();
        try
        {
            return Collections.binarySearch(this, key, comparator);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public int binarySearch(T key)
    {
        this.acquireReadLock();
        try
        {
            return Collections.binarySearch((List<? extends Comparable<? super T>>) this, key);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public void reverseForEach(Procedure<? super T> procedure)
    {
        this.withReadLockRun(() -> this.getDelegate().reverseForEach(procedure));
    }

    @Override
    public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
    {
        this.withReadLockRun(() -> this.getDelegate().reverseForEachWithIndex(procedure));
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        this.withReadLockRun(() -> this.getDelegate().forEachWithIndex(fromIndex, toIndex, objectIntProcedure));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.delegate);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.delegate = (MutableList<T>) in.readObject();
        this.lock = new ReentrantReadWriteLock();
    }

    // Exposed for testing

    static final class UntouchableMutableList<T>
            extends UntouchableMutableCollection<T>
            implements MutableList<T>
    {
        private final MutableList<UntouchableListIterator<T>> requestedIterators = mList();
        private final MutableList<UntouchableMutableList<T>> requestedSubLists = mList();

        private UntouchableMutableList(MutableList<T> delegate)
        {
            this.delegate = delegate;
        }

        @Override
        public MutableList<T> with(T element)
        {
            this.add(element);
            return this;
        }

        @Override
        public MutableList<T> without(T element)
        {
            this.remove(element);
            return this;
        }

        @Override
        public MutableList<T> withAll(Iterable<? extends T> elements)
        {
            this.addAllIterable(elements);
            return this;
        }

        @Override
        public MutableList<T> withoutAll(Iterable<? extends T> elements)
        {
            this.removeAllIterable(elements);
            return this;
        }

        @Override
        public MutableList<T> asSynchronized()
        {
            throw new UnsupportedOperationException("Cannot call asSynchronized() on " + this.getClass().getSimpleName());
        }

        @Override
        public MutableList<T> asUnmodifiable()
        {
            throw new UnsupportedOperationException("Cannot call asUnmodifiable() on " + this.getClass().getSimpleName());
        }

        @Override
        public LazyIterable<T> asLazy()
        {
            return LazyIterate.adapt(this);
        }

        @Override
        public ImmutableList<T> toImmutable()
        {
            return this.getDelegate().toImmutable();
        }

        @Override
        public MutableList<T> clone()
        {
            return this.getDelegate().clone();
        }

        @Override
        public <V> MutableList<V> collect(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().collect(function);
        }

        @Override
        public MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }

        @Override
        public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
        {
            return this.getDelegate().collectBoolean(booleanFunction, target);
        }

        @Override
        public MutableByteList collectByte(ByteFunction<? super T> byteFunction)
        {
            return this.getDelegate().collectByte(byteFunction);
        }

        @Override
        public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
        {
            return this.getDelegate().collectByte(byteFunction, target);
        }

        @Override
        public MutableCharList collectChar(CharFunction<? super T> charFunction)
        {
            return this.getDelegate().collectChar(charFunction);
        }

        @Override
        public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
        {
            return this.getDelegate().collectChar(charFunction, target);
        }

        @Override
        public MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }

        @Override
        public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
        {
            return this.getDelegate().collectDouble(doubleFunction, target);
        }

        @Override
        public MutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
        {
            return this.getDelegate().collectFloat(floatFunction);
        }

        @Override
        public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
        {
            return this.getDelegate().collectFloat(floatFunction, target);
        }

        @Override
        public MutableIntList collectInt(IntFunction<? super T> intFunction)
        {
            return this.getDelegate().collectInt(intFunction);
        }

        @Override
        public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
        {
            return this.getDelegate().collectInt(intFunction, target);
        }

        @Override
        public MutableLongList collectLong(LongFunction<? super T> longFunction)
        {
            return this.getDelegate().collectLong(longFunction);
        }

        @Override
        public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
        {
            return this.getDelegate().collectLong(longFunction, target);
        }

        @Override
        public MutableShortList collectShort(ShortFunction<? super T> shortFunction)
        {
            return this.getDelegate().collectShort(shortFunction);
        }

        @Override
        public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
        {
            return this.getDelegate().collectShort(shortFunction, target);
        }

        @Override
        public <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
        {
            return this.getDelegate().flatCollect(function);
        }

        @Override
        public <V> MutableList<V> collectIf(
                Predicate<? super T> predicate,
                Function<? super T, ? extends V> function)
        {
            return this.getDelegate().collectIf(predicate, function);
        }

        @Override
        public <P, V> MutableList<V> collectWith(
                Function2<? super T, ? super P, ? extends V> function,
                P parameter)
        {
            return this.getDelegate().collectWith(function, parameter);
        }

        @Override
        public int detectIndex(Predicate<? super T> predicate)
        {
            return this.getDelegate().detectIndex(predicate);
        }

        @Override
        public int detectLastIndex(Predicate<? super T> predicate)
        {
            return this.getDelegate().detectLastIndex(predicate);
        }

        @Override
        public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().groupBy(function);
        }

        @Override
        public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
        {
            return this.getDelegate().groupByEach(function);
        }

        @Override
        public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().groupByUniqueKey(function);
        }

        @Override
        public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super T, ? super S> predicate)
        {
            return this.getDelegate().corresponds(other, predicate);
        }

        @Override
        public void forEach(int fromIndex, int toIndex, Procedure<? super T> procedure)
        {
            this.getDelegate().forEach(fromIndex, toIndex, procedure);
        }

        @Override
        public void reverseForEach(Procedure<? super T> procedure)
        {
            this.getDelegate().reverseForEach(procedure);
        }

        @Override
        public void reverseForEachWithIndex(ObjectIntProcedure<? super T> procedure)
        {
            this.getDelegate().reverseForEachWithIndex(procedure);
        }

        @Override
        public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super T> objectIntProcedure)
        {
            this.getDelegate().forEachWithIndex(fromIndex, toIndex, objectIntProcedure);
        }

        @Override
        public MutableList<T> newEmpty()
        {
            return this.getDelegate().newEmpty();
        }

        @Override
        public MutableList<T> reject(Predicate<? super T> predicate)
        {
            return this.getDelegate().reject(predicate);
        }

        @Override
        public MutableList<T> distinct()
        {
            return this.getDelegate().distinct();
        }

        @Override
        public MutableList<T> distinct(HashingStrategy<? super T> hashingStrategy)
        {
            return this.getDelegate().distinct(hashingStrategy);
        }

        /**
         * @since 9.0.
         */
        @Override
        public <V> MutableList<T> distinctBy(Function<? super T, ? extends V> function)
        {
            return this.getDelegate().distinctBy(function);
        }

        @Override
        public <P> MutableList<T> rejectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.getDelegate().rejectWith(predicate, parameter);
        }

        @Override
        public MutableList<T> tap(Procedure<? super T> procedure)
        {
            this.forEach(procedure);
            return this;
        }

        @Override
        public MutableList<T> select(Predicate<? super T> predicate)
        {
            return this.getDelegate().select(predicate);
        }

        @Override
        public <P> MutableList<T> selectWith(
                Predicate2<? super T, ? super P> predicate,
                P parameter)
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }

        @Override
        public PartitionMutableList<T> partition(Predicate<? super T> predicate)
        {
            return this.getDelegate().partition(predicate);
        }

        @Override
        public <P> PartitionMutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }

        @Override
        public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }

        @Override
        public MutableList<T> sortThis()
        {
            this.getDelegate().sortThis();
            return this;
        }

        @Override
        public MutableList<T> sortThis(Comparator<? super T> comparator)
        {
            this.getDelegate().sortThis(comparator);
            return this;
        }

        @Override
        public MutableList<T> toReversed()
        {
            return this.getDelegate().toReversed();
        }

        @Override
        public MutableList<T> reverseThis()
        {
            this.getDelegate().reverseThis();
            return this;
        }

        @Override
        public MutableList<T> shuffleThis()
        {
            this.getDelegate().shuffleThis();
            return this;
        }

        @Override
        public MutableList<T> shuffleThis(Random rnd)
        {
            this.getDelegate().shuffleThis(rnd);
            return this;
        }

        @Override
        public MutableStack<T> toStack()
        {
            return ArrayStack.newStack(this.delegate);
        }

        @Override
        public <V extends Comparable<? super V>> MutableList<T> sortThisBy(Function<? super T, ? extends V> function)
        {
            this.getDelegate().sortThisBy(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByInt(IntFunction<? super T> function)
        {
            this.getDelegate().sortThisByInt(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByBoolean(BooleanFunction<? super T> function)
        {
            this.getDelegate().sortThisByBoolean(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByChar(CharFunction<? super T> function)
        {
            this.getDelegate().sortThisByChar(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByByte(ByteFunction<? super T> function)
        {
            this.getDelegate().sortThisByByte(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByShort(ShortFunction<? super T> function)
        {
            this.getDelegate().sortThisByShort(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByFloat(FloatFunction<? super T> function)
        {
            this.getDelegate().sortThisByFloat(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByLong(LongFunction<? super T> function)
        {
            this.getDelegate().sortThisByLong(function);
            return this;
        }

        @Override
        public MutableList<T> sortThisByDouble(DoubleFunction<? super T> function)
        {
            this.getDelegate().sortThisByDouble(function);
            return this;
        }

        @Override
        public MutableList<T> take(int count)
        {
            return this.getDelegate().take(count);
        }

        @Override
        public MutableList<T> takeWhile(Predicate<? super T> predicate)
        {
            return this.getDelegate().takeWhile(predicate);
        }

        @Override
        public MutableList<T> drop(int count)
        {
            return this.getDelegate().drop(count);
        }

        @Override
        public MutableList<T> dropWhile(Predicate<? super T> predicate)
        {
            return this.getDelegate().dropWhile(predicate);
        }

        @Override
        public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
        {
            return this.getDelegate().partitionWhile(predicate);
        }

        @Override
        public MutableList<T> subList(int fromIndex, int toIndex)
        {
            UntouchableMutableList<T> subList = new UntouchableMutableList<>(
                    this.getDelegate().subList(fromIndex, toIndex));
            this.requestedSubLists.add(subList);
            return subList;
        }

        @Override
        public Iterator<T> iterator()
        {
            UntouchableListIterator<T> iterator = new UntouchableListIterator<>(this.delegate.iterator());
            this.requestedIterators.add(iterator);
            return iterator;
        }

        @Override
        public void add(int index, T element)
        {
            this.getDelegate().add(index, element);
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> collection)
        {
            return this.getDelegate().addAll(index, collection);
        }

        @Override
        public T get(int index)
        {
            return this.getDelegate().get(index);
        }

        @Override
        public int indexOf(Object o)
        {
            return this.getDelegate().indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o)
        {
            return this.getDelegate().lastIndexOf(o);
        }

        @Override
        public ListIterator<T> listIterator()
        {
            UntouchableListIterator<T> iterator = new UntouchableListIterator<>(this.getDelegate().listIterator());
            this.requestedIterators.add(iterator);
            return iterator;
        }

        @Override
        public ListIterator<T> listIterator(int index)
        {
            UntouchableListIterator<T> iterator = new UntouchableListIterator<>(this.getDelegate().listIterator(index));
            this.requestedIterators.add(iterator);
            return iterator;
        }

        @Override
        public T remove(int index)
        {
            return this.getDelegate().remove(index);
        }

        @Override
        public T set(int index, T element)
        {
            return this.getDelegate().set(index, element);
        }

        @Override
        public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
        {
            return this.getDelegate().zip(that);
        }

        @Override
        public MutableList<Pair<T, Integer>> zipWithIndex()
        {
            return this.getDelegate().zipWithIndex();
        }

        @Override
        public LazyIterable<T> asReversed()
        {
            return ReverseIterable.adapt(this);
        }

        @Override
        public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
        {
            return new ListIterableParallelIterable<>(this, executorService, batchSize);
        }

        @Override
        public int binarySearch(T key, Comparator<? super T> comparator)
        {
            return Collections.binarySearch(this, key, comparator);
        }

        @Override
        public int binarySearch(T key)
        {
            return Collections.binarySearch((List<? extends Comparable<? super T>>) this, key);
        }

        public void becomeUseless()
        {
            this.delegate = null;
            this.requestedSubLists.each(UntouchableMutableList::becomeUseless);
            this.requestedIterators.each(UntouchableListIterator::becomeUseless);
        }

        private MutableList<T> getDelegate()
        {
            return (MutableList<T>) this.delegate;
        }
    }

    private static final class UntouchableListIterator<T>
            implements ListIterator<T>
    {
        private Iterator<T> delegate;

        private UntouchableListIterator(Iterator<T> newDelegate)
        {
            this.delegate = newDelegate;
        }

        @Override
        public void add(T o)
        {
            ((ListIterator<T>) this.delegate).add(o);
        }

        @Override
        public boolean hasNext()
        {
            return this.delegate.hasNext();
        }

        @Override
        public boolean hasPrevious()
        {
            return ((ListIterator<T>) this.delegate).hasPrevious();
        }

        @Override
        public T next()
        {
            return this.delegate.next();
        }

        @Override
        public int nextIndex()
        {
            return ((ListIterator<T>) this.delegate).nextIndex();
        }

        @Override
        public T previous()
        {
            return ((ListIterator<T>) this.delegate).previous();
        }

        @Override
        public int previousIndex()
        {
            return ((ListIterator<T>) this.delegate).previousIndex();
        }

        @Override
        public void remove()
        {
            this.delegate.remove();
        }

        @Override
        public void set(T o)
        {
            ((ListIterator<T>) this.delegate).set(o);
        }

        public void becomeUseless()
        {
            this.delegate = null;
        }
    }

    @Override
    public int detectIndex(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.getDelegate().detectIndex(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public int detectLastIndex(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.getDelegate().detectLastIndex(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.groupBy(function);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.groupByEach(function);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.groupByUniqueKey(function);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public <S> MutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.zip(that);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<Pair<T, Integer>> zipWithIndex()
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.zipWithIndex();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> toReversed()
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.toReversed();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> reverseThis()
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.reverseThis();
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> shuffleThis()
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.shuffleThis();
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableList<T> shuffleThis(Random rnd)
    {
        this.acquireWriteLock();
        try
        {
            this.delegate.shuffleThis(rnd);
            return this;
        }
        finally
        {
            this.unlockWriteLock();
        }
    }

    @Override
    public MutableStack<T> toStack()
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.toStack();
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public RichIterable<RichIterable<T>> chunk(int size)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.chunk(size);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> take(int count)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.take(count);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.takeWhile(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> drop(int count)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.drop(count);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public MutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.dropWhile(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public PartitionMutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        this.acquireReadLock();
        try
        {
            return this.delegate.partitionWhile(predicate);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public LazyIterable<T> asReversed()
    {
        this.acquireReadLock();
        try
        {
            return ReverseIterable.adapt(this);
        }
        finally
        {
            this.unlockReadLock();
        }
    }

    @Override
    public ParallelListIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        return new MultiReaderParallelListIterable<>(this.delegate.asParallel(executorService, batchSize), this.lock);
    }
}
