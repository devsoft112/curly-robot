/*
 * Copyright (c) 2018 Two Sigma.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.ordered.mutable;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
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
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.map.ImmutableOrderedMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableOrderedMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.block.procedure.MutatingAggregationProcedure;
import org.eclipse.collections.impl.block.procedure.NonMutatingAggregationProcedure;
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
import org.eclipse.collections.impl.collection.mutable.CollectionAdapter;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.map.AbstractMapIterable;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectDoubleHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectLongHashMap;
import org.eclipse.collections.impl.multimap.list.FastListMultimap;
import org.eclipse.collections.impl.partition.list.PartitionFastList;
import org.eclipse.collections.impl.set.mutable.SetAdapter;
import org.eclipse.collections.impl.tuple.AbstractImmutableEntry;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.eclipse.collections.impl.utility.MapIterate;

/**
 * @since 9.2
 */
public class OrderedMapAdapter<K, V>
        extends AbstractMapIterable<K, V>
        implements MutableOrderedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Map<K, V> delegate;

    protected OrderedMapAdapter(Map<K, V> delegate)
    {
        if (delegate == null)
        {
            throw new NullPointerException("OrderedMapAdapter may not wrap null");
        }
        this.delegate = delegate;
    }

    public static <K, V> MutableOrderedMap<K, V> adapt(Map<K, V> map)
    {
        return map instanceof MutableOrderedMap<?, ?> ? (MutableOrderedMap<K, V>) map : new OrderedMapAdapter<>(map);
    }

    @Override
    public boolean equals(Object o)
    {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public MutableOrderedMap<K, V> newEmpty()
    {
        return OrderedMapAdapter.adapt(new LinkedHashMap<>());
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        MapIterate.forEachKeyValue(this.delegate, procedure);
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    @Override
    public Iterator<V> iterator()
    {
        return this.delegate.values().iterator();
    }

    @Override
    public V remove(Object key)
    {
        return this.delegate.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        this.delegate.putAll(map);
    }

    @Override
    public MutableCollection<V> values()
    {
        return CollectionAdapter.adapt(this.delegate.values());
    }

    // TODO: OrderedSet
    @Override
    public MutableSet<Entry<K, V>> entrySet()
    {
        return SetAdapter.adapt(this.delegate.entrySet());
    }

    // TODO: OrderedSet
    @Override
    public MutableSet<K> keySet()
    {
        return SetAdapter.adapt(this.delegate.keySet());
    }

    @Override
    public void clear()
    {
        this.delegate.clear();
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    @Override
    public V put(K key, V value)
    {
        return this.delegate.put(key, value);
    }

    @Override
    public V removeKey(K key)
    {
        return this.delegate.remove(key);
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public MutableOrderedMap<K, V> clone()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableOrderedMap<K, V> toReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toReversed() not implemented yet");
    }

    @Override
    public MutableOrderedMap<K, V> take(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        MutableOrderedMap<K, V> output = OrderedMapAdapter.adapt(new LinkedHashMap<>(Math.min(this.size(), count)));
        Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        int countCopy = count;
        while (iterator.hasNext() && countCopy-- > 0)
        {
            Entry<K, V> next = iterator.next();
            output.put(next.getKey(), next.getValue());
        }
        return output;
    }

    @Override
    public MutableOrderedMap<K, V> takeWhile(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".takeWhile() not implemented yet");
    }

    @Override
    public MutableOrderedMap<K, V> drop(int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("Count must be greater than zero, but was: " + count);
        }

        MutableOrderedMap<K, V> output = OrderedMapAdapter.adapt(new LinkedHashMap<>(this.size() - Math.min(this.size(), count)));
        Iterator<Entry<K, V>> iterator = this.delegate.entrySet().iterator();
        int start = Math.min(count, this.size());
        if (start == this.size())
        {
            return output;
        }
        int i = 0;
        while (iterator.hasNext())
        {
            if (i >= start)
            {
                Entry<K, V> next = iterator.next();
                output.put(next.getKey(), next.getValue());
            }
            else
            {
                iterator.next();
            }
            i++;
        }
        return output;
    }

    @Override
    public MutableOrderedMap<K, V> dropWhile(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".dropWhile() not implemented yet");
    }

    @Override
    public PartitionMutableList<V> partitionWhile(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".partitionWhile() not implemented yet");
    }

    @Override
    public MutableList<V> distinct()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".distinct() not implemented yet");
    }

    @Override
    public MutableOrderedMap<K, V> withKeyValue(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    @Override
    public MutableOrderedMap<K, V> withMap(Map<? extends K, ? extends V> map)
    {
        this.putAll(map);
        return this;
    }

    @Override
    public void putAllMapIterable(MapIterable<? extends K, ? extends V> mapIterable)
    {
        mapIterable.forEachKeyValue(this::put);
    }

    @Override
    public MutableOrderedMap<K, V> withAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues)
    {
        keyValues.forEach(keyVal -> this.put(keyVal.getOne(), keyVal.getTwo()));
        return this;
    }

    @Override
    public MutableOrderedMap<K, V> withAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValues)
    {
        return this.withAllKeyValues(ArrayAdapter.adapt(keyValues));
    }

    @Override
    public MutableOrderedMap<K, V> withoutKey(K key)
    {
        this.removeKey(key);
        return this;
    }

    @Override
    public MutableOrderedMap<K, V> withoutAllKeys(Iterable<? extends K> keys)
    {
        keys.forEach(this::removeKey);
        return this;
    }

    @Override
    public MutableOrderedMap<K, V> asUnmodifiable()
    {
        return UnmodifiableMutableOrderedMap.of(this);
    }

    @Override
    public ImmutableOrderedMap<K, V> toImmutable()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toImmutable() not implemented yet");
    }

    @Override
    public MutableOrderedMap<K, V> asSynchronized()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asSynchronized() not implemented yet");
    }

    @Override
    public MutableListMultimap<V, K> flip()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".flip() not implemented yet");
    }

    @Override
    public MutableBooleanList collectBoolean(BooleanFunction<? super V> booleanFunction)
    {
        BooleanArrayList result = new BooleanArrayList(this.size());
        this.forEach(new CollectBooleanProcedure<>(booleanFunction, result));
        return result;
    }

    @Override
    public MutableByteList collectByte(ByteFunction<? super V> byteFunction)
    {
        ByteArrayList result = new ByteArrayList(this.size());
        this.forEach(new CollectByteProcedure<>(byteFunction, result));
        return result;
    }

    @Override
    public MutableCharList collectChar(CharFunction<? super V> charFunction)
    {
        CharArrayList result = new CharArrayList(this.size());
        this.forEach(new CollectCharProcedure<>(charFunction, result));
        return result;
    }

    @Override
    public MutableDoubleList collectDouble(DoubleFunction<? super V> doubleFunction)
    {
        DoubleArrayList result = new DoubleArrayList(this.size());
        this.forEach(new CollectDoubleProcedure<>(doubleFunction, result));
        return result;
    }

    @Override
    public MutableFloatList collectFloat(FloatFunction<? super V> floatFunction)
    {
        FloatArrayList result = new FloatArrayList(this.size());
        this.forEach(new CollectFloatProcedure<>(floatFunction, result));
        return result;
    }

    @Override
    public MutableIntList collectInt(IntFunction<? super V> intFunction)
    {
        IntArrayList result = new IntArrayList(this.size());
        this.forEach(new CollectIntProcedure<>(intFunction, result));
        return result;
    }

    @Override
    public MutableLongList collectLong(LongFunction<? super V> longFunction)
    {
        LongArrayList result = new LongArrayList(this.size());
        this.forEach(new CollectLongProcedure<>(longFunction, result));
        return result;
    }

    @Override
    public MutableShortList collectShort(ShortFunction<? super V> shortFunction)
    {
        ShortArrayList result = new ShortArrayList(this.size());
        this.forEach(new CollectShortProcedure<>(shortFunction, result));
        return result;
    }

    @Override
    public <R> MutableOrderedMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        return MapIterate.collectValues(
                this,
                function,
                OrderedMapAdapter.adapt(new LinkedHashMap<>(this.size())));
    }

    @Override
    public MutableOrderedMap<K, V> tap(Procedure<? super V> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    @Override
    public MutableOrderedMap<K, V> select(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.selectMapOnEntry(this, predicate, this.newEmpty());
    }

    @Override
    public MutableOrderedMap<K, V> reject(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.rejectMapOnEntry(this, predicate, this.newEmpty());
    }

    @Override
    public <R> MutableList<R> collect(Function<? super V, ? extends R> function)
    {
        return this.collect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <P, VV> MutableList<VV> collectWith(Function2<? super V, ? super P, ? extends VV> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    @Override
    public <R> MutableList<R> collectIf(Predicate<? super V> predicate, Function<? super V, ? extends R> function)
    {
        return this.collectIf(predicate, function, Lists.mutable.empty());
    }

    @Override
    public <R> MutableList<R> flatCollect(Function<? super V, ? extends Iterable<R>> function)
    {
        return this.flatCollect(function, Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public MutableList<V> select(Predicate<? super V> predicate)
    {
        return this.select(predicate, Lists.mutable.empty());
    }

    @Override
    public MutableList<V> reject(Predicate<? super V> predicate)
    {
        return this.reject(predicate, Lists.mutable.empty());
    }

    @Override
    public <P> MutableList<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P> MutableList<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    @Override
    public PartitionMutableList<V> partition(Predicate<? super V> predicate)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionProcedure<>(predicate, partitionMutableList));
        return partitionMutableList;
    }

    @Override
    public <P> PartitionMutableList<V> partitionWith(Predicate2<? super V, ? super P> predicate, P parameter)
    {
        PartitionMutableList<V> partitionMutableList = new PartitionFastList<>();
        this.forEach(new PartitionPredicate2Procedure<>(predicate, parameter, partitionMutableList));
        return partitionMutableList;
    }

    @Override
    public <S> MutableList<S> selectInstancesOf(Class<S> clazz)
    {
        MutableList<S> result = Lists.mutable.withInitialCapacity(this.size());
        this.forEach(new SelectInstancesOfProcedure<>(clazz, result));
        return result;
    }

    @Override
    public <S> MutableList<Pair<V, S>> zip(Iterable<S> that)
    {
        return this.zip(that, Lists.mutable.withInitialCapacity(Math.min(this.size(), Iterate.sizeOf(that))));
    }

    @Override
    public MutableList<Pair<V, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(Lists.mutable.withInitialCapacity(this.size()));
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupBy(Function<? super V, ? extends VV> function)
    {
        return this.groupBy(function, FastListMultimap.newMultimap());
    }

    @Override
    public <VV> MutableListMultimap<VV, V> groupByEach(Function<? super V, ? extends Iterable<VV>> function)
    {
        return this.groupByEach(function, FastListMultimap.newMultimap());
    }

    @Override
    public <VV> MutableOrderedMap<VV, V> groupByUniqueKey(Function<? super V, ? extends VV> function)
    {
        // TODO: Make this work in a major version by changing {@link org.eclipse.collections.api.RichIterable#groupByUniqueKey}'s type R from MutableMap to MutableMapIterable.
        // return this.groupByUniqueKey(function, this.newEmpty());
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".groupByUniqueKey() not implemented yet");
    }

    @Override
    public LazyIterable<V> asReversed()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".asReversed() not implemented yet");
    }

    @Override
    public int detectLastIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectLastIndex() not implemented yet");
    }

    @Override
    public int indexOf(Object object)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".indexOf() not implemented yet");
    }

    @Override
    public <S> boolean corresponds(OrderedIterable<S> other, Predicate2<? super V, ? super S> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".corresponds() not implemented yet");
    }

    @Override
    public void forEach(int startIndex, int endIndex, Procedure<? super V> procedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEach() not implemented yet");
    }

    @Override
    public void forEachWithIndex(int fromIndex, int toIndex, ObjectIntProcedure<? super V> objectIntProcedure)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEachWithIndex() not implemented yet");
    }

    @Override
    public MutableStack<V> toStack()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".toStack() not implemented yet");
    }

    @Override
    public int detectIndex(Predicate<? super V> predicate)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".detectIndex() not implemented yet");
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = function.value();
            this.put(key, result);
        }
        return result;
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = value;
            this.put(key, result);
        }
        return result;
    }

    @Override
    public V getIfAbsentPutWithKey(K key, Function<? super K, ? extends V> function)
    {
        return this.getIfAbsentPutWith(key, function, key);
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        V result = this.get(key);
        if (this.isAbsent(result, key))
        {
            result = function.valueOf(parameter);
            this.put(key, result);
        }
        return result;
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        V oldValue = this.getIfAbsent(key, factory);
        V newValue = function.valueOf(oldValue);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        V oldValue = this.getIfAbsent(key, factory);
        V newValue = function.value(oldValue, parameter);
        this.put(key, newValue);
        return newValue;
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> aggregateInPlaceBy(
            Function<? super V, ? extends K2> groupBy,
            Function0<? extends V2> zeroValueFactory,
            Procedure2<? super V2, ? super V> mutatingAggregator)
    {
        MutableMap<K2, V2> map = Maps.mutable.empty();
        this.forEach(new MutatingAggregationProcedure<>(map, groupBy, zeroValueFactory, mutatingAggregator));
        return map;
    }

    @Override
    public <K2, V2> MutableMap<K2, V2> aggregateBy(
            Function<? super V, ? extends K2> groupBy,
            Function0<? extends V2> zeroValueFactory,
            Function2<? super V2, ? super V, ? extends V2> nonMutatingAggregator)
    {
        MutableMap<K2, V2> map = Maps.mutable.empty();
        this.forEach(new NonMutatingAggregationProcedure<>(map, groupBy, zeroValueFactory, nonMutatingAggregator));
        return map;
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
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return LazyIterate.adapt(this.entrySet()).collect(AbstractImmutableEntry.getPairFunction());
    }

    @Override
    public <K2, V2> MutableOrderedMap<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function)
    {
        return MapIterate.collect(this, function, OrderedMapAdapter.adapt(new LinkedHashMap<>(this.size())));
    }

    @Override
    public MutableOrderedMap<V, K> flipUniqueValues()
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".flipUniqueValues() not implemented yet");
    }

    @Override
    public Pair<K, V> detect(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.detect(this, predicate);
    }

    @Override
    public Optional<Pair<K, V>> detectOptional(Predicate2<? super K, ? super V> predicate)
    {
        return MapIterate.detectOptional(this, predicate);
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByInt(Function<? super V, ? extends V1> groupBy, IntFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongHashMap.newMap();
        return this.injectInto(result, PrimitiveFunctions.sumByIntFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByFloat(Function<? super V, ? extends V1> groupBy, FloatFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleHashMap.newMap();
        return this.injectInto(result, PrimitiveFunctions.sumByFloatFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectLongMap<V1> sumByLong(Function<? super V, ? extends V1> groupBy, LongFunction<? super V> function)
    {
        MutableObjectLongMap<V1> result = ObjectLongHashMap.newMap();
        return this.injectInto(result, PrimitiveFunctions.sumByLongFunction(groupBy, function));
    }

    @Override
    public <V1> MutableObjectDoubleMap<V1> sumByDouble(Function<? super V, ? extends V1> groupBy, DoubleFunction<? super V> function)
    {
        MutableObjectDoubleMap<V1> result = ObjectDoubleHashMap.newMap();
        return this.injectInto(result, PrimitiveFunctions.sumByDoubleFunction(groupBy, function));
    }

    @Override
    public <V1> MutableBag<V1> countBy(Function<? super V, ? extends V1> function)
    {
        return this.collect(function, Bags.mutable.empty());
    }

    @Override
    public <V1, P> MutableBag<V1> countByWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        return this.collectWith(function, parameter, Bags.mutable.empty());
    }

    /**
     * @since 10.0.0
     */
    @Override
    public <V1> MutableBag<V1> countByEach(Function<? super V, ? extends Iterable<V1>> function)
    {
        return this.flatCollect(function, Bags.mutable.empty());
    }
}
