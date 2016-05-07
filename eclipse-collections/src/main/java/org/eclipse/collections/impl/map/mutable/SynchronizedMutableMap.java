/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
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
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.bag.PartitionMutableBag;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.SynchronizedMutableCollection;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.map.AbstractSynchronizedMapIterable;
import org.eclipse.collections.impl.set.mutable.SynchronizedMutableSet;
import org.eclipse.collections.impl.utility.LazyIterate;

/**
 * A synchronized view of a {@link MutableMap}. It is imperative that the user manually synchronize on the collection when iterating over it using the
 * standard JDK iterator or JDK 5 for loop, as per {@link Collections#synchronizedCollection(Collection)}.
 *
 * @see MutableMap#asSynchronized()
 */
public class SynchronizedMutableMap<K, V>
        extends AbstractSynchronizedMapIterable<K, V> implements MutableMap<K, V>, Serializable
{
    private static final long serialVersionUID = 2L;

    public SynchronizedMutableMap(MutableMap<K, V> newMap)
    {
        super(newMap);
    }

    public SynchronizedMutableMap(MutableMap<K, V> newMap, Object newLock)
    {
        super(newMap, newLock);
    }

    /**
     * This method will take a MutableMap and wrap it directly in a SynchronizedMutableMap.  It will
     * take any other non-GS-map and first adapt it will a MapAdapter, and then return a
     * SynchronizedMutableMap that wraps the adapter.
     */
    public static <K, V, M extends Map<K, V>> SynchronizedMutableMap<K, V> of(M map)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedMutableMap for null");
        }
        return new SynchronizedMutableMap<>(MapAdapter.adapt(map));
    }

    /**
     * This method will take a MutableMap and wrap it directly in a SynchronizedMutableMap.  It will
     * take any other non-GS-map and first adapt it will a MapAdapter, and then return a
     * SynchronizedMutableMap that wraps the adapter. Additionally, a developer specifies which lock to use
     * with the collection.
     */
    public static <K, V, M extends Map<K, V>> SynchronizedMutableMap<K, V> of(M map, Object lock)
    {
        if (map == null)
        {
            throw new IllegalArgumentException("cannot create a SynchronizedMutableMap for null");
        }
        return new SynchronizedMutableMap<>(MapAdapter.adapt(map), lock);
    }

    @Override
    public MutableMap<K, V> withKeyValue(K key, V value)
    {
        synchronized (this.lock)
        {
            this.put(key, value);
            return this;
        }
    }

    @Override
    public MutableMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValuePairs));
    }

    @Override
    public MutableMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        synchronized (this.lock)
        {
            for (Pair<? extends K, ? extends V> keyValue : keyValues)
            {
                this.getDelegate().put(keyValue.getOne(), keyValue.getTwo());
            }
            return this;
        }
    }

    @Override
    public MutableMap<K, V> withoutKey(K key)
    {
        synchronized (this.lock)
        {
            this.remove(key);
            return this;
        }
    }

    @Override
    public MutableMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        synchronized (this.lock)
        {
            for (K key : keys)
            {
                this.getDelegate().removeKey(key);
            }
            return this;
        }
    }

    @Override
    public MutableMap<K, V> newEmpty()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().newEmpty();
        }
    }

    @Override
    public MutableMap<K, V> clone()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableMap.of(this.getDelegate().clone());
        }
    }

    protected Object writeReplace()
    {
        return new SynchronizedMapSerializationProxy<>(this.getDelegate());
    }

    @Override
    protected MutableMap<K, V> getDelegate()
    {
        return (MutableMap<K, V>) super.getDelegate();
    }

    @Override
    public <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectKeysAndValues(iterable, keyFunction, function);
        }
    }

    @Override
    public MutableMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().select(predicate);
        }
    }

    @Override
    public MutableMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reject(predicate);
        }
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> pairFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collect(pairFunction);
        }
    }

    @Override
    public <R> MutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectValues(function);
        }
    }

    @Override
    public MutableMap<K, V> tap(Procedure<? super V> procedure)
    {
        synchronized (this.lock)
        {
            this.forEach(procedure);
            return this;
        }
    }

    @Override
    public MutableBag<V> select(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().select(predicate);
        }
    }

    @Override
    public <P> MutableBag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectWith(predicate, parameter);
        }
    }

    @Override
    public MutableBag<V> reject(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().reject(predicate);
        }
    }

    @Override
    public <P> MutableBag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().rejectWith(predicate, parameter);
        }
    }

    @Override
    public PartitionMutableBag<V> partition(Predicate<? super V> predicate)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().partition(predicate);
        }
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<V, Integer>> zipWithIndex()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().zipWithIndex();
        }
    }

    @Override
    public <P> PartitionMutableBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().partitionWith(predicate, parameter);
        }
    }

    @Override
    public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().selectInstancesOf(clazz);
        }
    }

    @Override
    public <A> MutableBag<A> collect(Function<? super V, ? extends A> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collect(function);
        }
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectBoolean(booleanFunction);
        }
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super V> byteFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectByte(byteFunction);
        }
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super V> charFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectChar(charFunction);
        }
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectDouble(doubleFunction);
        }
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super V> floatFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectFloat(floatFunction);
        }
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super V> intFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectInt(intFunction);
        }
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super V> longFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectLong(longFunction);
        }
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super V> shortFunction)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectShort(shortFunction);
        }
    }

    @Override
    public <P, A> MutableBag<A> collectWith(Function2<? super V, ? super P, ? extends A> function, P parameter)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectWith(function, parameter);
        }
    }

    @Override
    public <A> MutableBag<A> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends A> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().collectIf(predicate, function);
        }
    }

    @Override
    public <A> MutableBag<A> flatCollect(Function<? super V, ? extends Iterable<A>> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flatCollect(function);
        }
    }

    @Override
    public <KK> MutableBagMultimap<KK, V> groupBy(Function<? super V, ? extends KK> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupBy(function);
        }
    }

    @Override
    public <KK> MutableBagMultimap<KK, V> groupByEach(Function<? super V, ? extends Iterable<KK>> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupByEach(function);
        }
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableBag<Pair<V, S>> zip(Iterable<S> that)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().zip(that);
        }
    }

    @Override
    public <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        synchronized (this.lock)
        {
            return this.getDelegate().groupByUniqueKey(function);
        }
    }

    @Override
    public MutableMap<V, K> flipUniqueValues()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flipUniqueValues();
        }
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        synchronized (this.lock)
        {
            return this.getDelegate().flip();
        }
    }

    @Override
    public Set<K> keySet()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableSet.of(this.getDelegate().keySet(), this.lock);
        }
    }

    @Override
    public Collection<V> values()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableCollection.of(this.getDelegate().values(), this.lock);
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        synchronized (this.lock)
        {
            return SynchronizedMutableSet.of(this.getDelegate().entrySet(), this.lock);
        }
    }

    @Override
    public RichIterable<K> keysView()
    {
        return LazyIterate.adapt(this.keySet());
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return LazyIterate.adapt(this.values());
    }

    @Override
    public MutableMap<K, V> asUnmodifiable()
    {
        synchronized (this.lock)
        {
            return UnmodifiableMutableMap.of(this);
        }
    }

    @Override
    public MutableMap<K, V> asSynchronized()
    {
        return this;
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        synchronized (this.lock)
        {
            return Maps.immutable.withAll(this);
        }
    }
}
