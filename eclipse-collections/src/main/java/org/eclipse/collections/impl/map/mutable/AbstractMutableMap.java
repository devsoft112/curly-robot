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

import java.util.Collection;

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
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.BooleanHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.ByteHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.CharHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.DoubleHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.FloatHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.IntHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.LongHashBag;
import org.eclipse.collections.impl.bag.mutable.primitive.ShortHashBag;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.procedure.PartitionPredicate2Procedure;
import org.eclipse.collections.impl.block.procedure.PartitionProcedure;
import org.eclipse.collections.impl.block.procedure.SelectInstancesOfProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectBooleanProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectByteProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectCharProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectFloatProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectIntProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectLongProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectShortProcedure;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap;
import org.eclipse.collections.impl.partition.bag.PartitionHashBag;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.utility.MapIterate;

public abstract class AbstractMutableMap<K, V> extends AbstractMutableMapIterable<K, V>
        implements MutableMap<K, V>
{
    @Override
    @SuppressWarnings("AbstractMethodOverridesAbstractMethod")
    public abstract MutableMap<K, V> clone();

    /**
     * Creates a new instance of the same type, using the given capacity and the default growth parameters.
     */
    public abstract <K, V> MutableMap<K, V> newEmpty(int capacity);

    @Override
    public MutableMap<K, V> asUnmodifiable()
    {
        return UnmodifiableMutableMap.of(this);
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return Maps.immutable.withAll(this);
    }

    @Override
    public MutableMap<K, V> asSynchronized()
    {
        return SynchronizedMutableMap.of(this);
    }

    @Override
    public MutableSetMultimap<V, K> flip()
    {
        return MapIterate.flip(this);
    }

    @Override
    public <R> MutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return MapIterate.collectValues(this, function, this.newEmpty(this.size()));
    }

    @Override
    public MutableMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.selectMapOnEntry(this, predicate, this.newEmpty());
    }

    @Override
    public MutableMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.rejectMapOnEntry(this, predicate, this.newEmpty());
    }

    @Override
    public <R> MutableBag<R> collect(Function<? super V, ? extends R> function)
    {
        return this.collect(function, HashBag.newBag());
    }

    @Override
    public MutableBooleanBag collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        MutableBooleanBag result = new BooleanHashBag();
        this.forEach(new CollectBooleanProcedure<>(booleanFunction, result));
        return result;
    }

    @Override
    public MutableByteBag collectByte(ByteFunction<? super V> byteFunction)
    {
        MutableByteBag result = new ByteHashBag();
        this.forEach(new CollectByteProcedure<>(byteFunction, result));
        return result;
    }

    @Override
    public MutableCharBag collectChar(CharFunction<? super V> charFunction)
    {
        MutableCharBag result = new CharHashBag();
        this.forEach(new CollectCharProcedure<>(charFunction, result));
        return result;
    }

    @Override
    public MutableDoubleBag collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        MutableDoubleBag result = new DoubleHashBag();
        this.forEach(new CollectDoubleProcedure<>(doubleFunction, result));
        return result;
    }

    @Override
    public MutableFloatBag collectFloat(FloatFunction<? super V> floatFunction)
    {
        MutableFloatBag result = new FloatHashBag();
        this.forEach(new CollectFloatProcedure<>(floatFunction, result));
        return result;
    }

    @Override
    public MutableIntBag collectInt(IntFunction<? super V> intFunction)
    {
        MutableIntBag result = new IntHashBag();
        this.forEach(new CollectIntProcedure<>(intFunction, result));
        return result;
    }

    @Override
    public MutableLongBag collectLong(LongFunction<? super V> longFunction)
    {
        MutableLongBag result = new LongHashBag();
        this.forEach(new CollectLongProcedure<>(longFunction, result));
        return result;
    }

    @Override
    public MutableShortBag collectShort(ShortFunction<? super V> shortFunction)
    {
        MutableShortBag result = new ShortHashBag();
        this.forEach(new CollectShortProcedure<>(shortFunction, result));
        return result;
    }

    @Override
    public <P, VV> MutableBag<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public <R> MutableBag<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.collectIf(predicate, function, new HashBag<>());
    }

    @Override
    public <R> MutableBag<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.flatCollect(function, new HashBag<>());
    }

    @Override
    public MutableBag<V> select(Predicate<? super V> predicate)
    {
        return this.select(predicate, new HashBag<>());
    }

    @Override
    public MutableMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public <P> MutableBag<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public MutableBag<V> reject(Predicate<? super V> predicate)
    {
        return this.reject(predicate, new HashBag<>());
    }

    @Override
    public <P> MutableBag<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public PartitionMutableBag<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableBag<V> partitionMutableBag = new PartitionHashBag<>();
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableBag));
        return partitionMutableBag;
    }

    @Override
    public <P> PartitionMutableBag<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        PartitionMutableBag<V> partitionMutableBag = new PartitionHashBag<>();
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableBag));
        return partitionMutableBag;
    }

    @Override
    public <S> MutableBag<S> selectInstancesOf(Class<S> clazz)
    {
        MutableBag<S> result = HashBag.newBag();
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result;
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Override
    @Deprecated
    public <S> MutableBag<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.zip(that, new HashBag<>(this.size()));
    }

    /**
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Override
    @Deprecated
    public MutableSet<Pair<V, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(new UnifiedSet<>(this.size()));
    }

    @Override
    public MutableMap<K, V> withKeyValue(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    @Override
    public MutableMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        for (Pair<? extends K, ? extends V> keyVal : keyValues)
        {
            this.put(keyVal.getOne(), keyVal.getTwo());
        }
        return this;
    }

    @Override
    public MutableMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValues)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValues));
    }

    @Override
    public MutableMap<K, V> withoutKey(K key)
    {
        this.removeKey(key);
        return this;
    }

    @Override
    public MutableMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        for (K key : keys)
        {
            this.removeKey(key);
        }
        return this;
    }

    /**
     * Trait-style class that is used to capture commonalities between ValuesCollection class implementations in order to
     * avoid code duplication.
     */
    protected abstract static class ValuesCollectionCommon<V> implements Collection<V>
    {
        @Override
        public boolean add(V v)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean addAll(Collection<? extends V> collection)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }
    }

    @Override
    public <VV> MutableBagMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.groupBy(function, HashBagMultimap.newMultimap());
    }

    @Override
    public <VV> MutableBagMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.groupByEach(function, HashBagMultimap.newMultimap());
    }

    @Override
    public <VV> MutableMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        return this.groupByUniqueKey(function, UnifiedMap.newMap());
    }
}
