/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.collection.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import net.jcip.annotations.ThreadSafe;
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
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionMutableCollection;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A synchronized view of a {@link MutableCollection}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableCollection#asSynchronized()
 */
@ThreadSafe
public class SynchronizedMutableCollection<T>
        extends AbstractSynchronizedMutableCollection<T> implements Serializable
{
    private static final long serialVersionUID = 2L;

    SynchronizedMutableCollection(MutableCollection<T> newCollection)
    {
        this(newCollection, null);
    }

    SynchronizedMutableCollection(MutableCollection<T> newCollection, Object newLock)
    {
        super(newCollection, newLock);
    }

    /**
     * This method will take a MutableCollection and wrap it directly in a SynchronizedMutableCollection.  It will
     * take any other non-GS-collection and first adapt it will a CollectionAdapter, and then return a
     * SynchronizedMutableCollection that wraps the adapter.
     */
    public static <E, C extends Collection<E>> SynchronizedMutableCollection<E> of(C collection)
    {
        return new SynchronizedMutableCollection<E>(CollectionAdapter.adapt(collection));
    }

    /**
     * This method will take a MutableCollection and wrap it directly in a SynchronizedMutableCollection.  It will
     * take any other non-GS-collection and first adapt it will a CollectionAdapter, and then return a
     * SynchronizedMutableCollection that wraps the adapter.  Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <E, C extends Collection<E>> SynchronizedMutableCollection<E> of(C collection, Object lock)
    {
        return new SynchronizedMutableCollection<E>(CollectionAdapter.adapt(collection), lock);
    }

    protected Object writeReplace()
    {
        return new SynchronizedCollectionSerializationProxy<T>(this.getDelegate());
    }

    public MutableCollection<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public MutableCollection<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    public MutableCollection<T> withAll(Iterable<? extends T> elements)
    {
        this.addAllIterable(elements);
        return this;
    }

    public MutableCollection<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    public MutableCollection<T> asUnmodifiable()
    {
        synchronized (this.lock)
        {
            return new UnmodifiableMutableCollection<T>(this);
        }
    }

    public MutableCollection<T> asSynchronized()
    {
        return this;
    }

    public ImmutableCollection<T> toImmutable()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().toImmutable();
        }
    }

    public MutableCollection<T> newEmpty()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().newEmpty().asSynchronized();
        }
    }

    public MutableCollection<T> tap(Procedure<? super T> procedure)
    {
        synchronized (this.getLock())
        {
            this.getDelegate().each(procedure);
            return this;
        }
    }

    public MutableCollection<T> select(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().select(predicate);
        }
    }

    public <P> MutableCollection<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }
    }

    public MutableCollection<T> reject(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().reject(predicate);
        }
    }

    public <P> MutableCollection<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().rejectWith(predicate, parameter);
        }
    }

    public PartitionMutableCollection<T> partition(Predicate<? super T> predicate)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partition(predicate);
        }
    }

    public <P> PartitionMutableCollection<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }
    }

    public MutableBooleanCollection collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }
    }

    public MutableByteCollection collectByte(ByteFunction<? super T> byteFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectByte(byteFunction);
        }
    }

    public MutableCharCollection collectChar(CharFunction<? super T> charFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectChar(charFunction);
        }
    }

    public MutableDoubleCollection collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }
    }

    public MutableFloatCollection collectFloat(FloatFunction<? super T> floatFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectFloat(floatFunction);
        }
    }

    public MutableIntCollection collectInt(IntFunction<? super T> intFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectInt(intFunction);
        }
    }

    public MutableLongCollection collectLong(LongFunction<? super T> longFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectLong(longFunction);
        }
    }

    public MutableShortCollection collectShort(ShortFunction<? super T> shortFunction)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectShort(shortFunction);
        }
    }

    public MutableCollection<Pair<T, Integer>> zipWithIndex()
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().zipWithIndex();
        }
    }

    public <S> MutableCollection<S> selectInstancesOf(Class<S> clazz)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }
    }

    public <V> MutableCollection<V> collect(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collect(function);
        }
    }

    public <P, V> MutableCollection<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectWith(function, parameter);
        }
    }

    public <V> MutableCollection<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().collectIf(predicate, function);
        }
    }

    public <V> MutableCollection<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().flatCollect(function);
        }
    }

    public <V> MutableMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().groupBy(function);
        }
    }

    public <V> MutableMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().groupByEach(function);
        }
    }

    public <S> MutableCollection<Pair<T, S>> zip(Iterable<S> that)
    {
        synchronized (this.getLock())
        {
            return this.getDelegate().zip(that);
        }
    }
}
