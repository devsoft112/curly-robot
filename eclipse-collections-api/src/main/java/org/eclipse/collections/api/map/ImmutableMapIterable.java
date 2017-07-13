/*
 * Copyright (c) 2017 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.map;

import java.util.Map;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.api.partition.PartitionImmutableCollection;
import org.eclipse.collections.api.tuple.Pair;

public interface ImmutableMapIterable<K, V> extends MapIterable<K, V>
{
    Map<K, V> castToMap();

    ImmutableMapIterable<K, V> newWithKeyValue(K key, V value);

    ImmutableMapIterable<K, V> newWithAllKeyValues(Iterable<? extends Pair<? extends K, ? extends V>> keyValues);

    ImmutableMapIterable<K, V> newWithAllKeyValueArguments(Pair<? extends K, ? extends V>... keyValuePairs);

    ImmutableMapIterable<K, V> newWithoutKey(K key);

    ImmutableMapIterable<K, V> newWithoutAllKeys(Iterable<? extends K> keys);

    // TODO
    // ImmutableSetIterable<K> keySet();

    @Override
    ImmutableMapIterable<K, V> tap(Procedure<? super V> procedure);

    @Override
    ImmutableMapIterable<V, K> flipUniqueValues();

    @Override
    ImmutableMultimap<V, K> flip();

    @Override
    ImmutableMapIterable<K, V> select(Predicate2<? super K, ? super V> predicate);

    @Override
    ImmutableMapIterable<K, V> reject(Predicate2<? super K, ? super V> predicate);

    @Override
    <K2, V2> ImmutableMapIterable<K2, V2> collect(Function2<? super K, ? super V, Pair<K2, V2>> function);

    @Override
    <R> ImmutableMapIterable<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function);

    @Override
    ImmutableCollection<V> select(Predicate<? super V> predicate);

    @Override
    <P> ImmutableCollection<V> selectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    ImmutableCollection<V> reject(Predicate<? super V> predicate);

    @Override
    <P> ImmutableCollection<V> rejectWith(Predicate2<? super V, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableCollection<V> partition(Predicate<? super V> predicate);

    @Override
    <S> ImmutableCollection<S> selectInstancesOf(Class<S> clazz);

    /**
     * @since 9.0
     */
    @Override
    default <V1> ImmutableBag<V1> countBy(Function<? super V, ? extends V1> function)
    {
        return this.asLazy().<V1>collect(function).toBag().toImmutable();
    }

    /**
     * @since 9.0
     */
    @Override
    default <V1, P> ImmutableBag<V1> countByWith(Function2<? super V, ? super P, ? extends V1> function, P parameter)
    {
        return this.asLazy().<P, V1>collectWith(function, parameter).toBag().toImmutable();
    }

    @Override
    <V1> ImmutableMultimap<V1, V> groupBy(Function<? super V, ? extends V1> function);

    @Override
    <V1> ImmutableMultimap<V1, V> groupByEach(Function<? super V, ? extends Iterable<V1>> function);

    @Override
    <V1> ImmutableMapIterable<V1, V> groupByUniqueKey(Function<? super V, ? extends V1> function);

    @Override
    <S> ImmutableCollection<Pair<V, S>> zip(Iterable<S> that);

    @Override
    ImmutableCollection<Pair<V, Integer>> zipWithIndex();

    @Override
    <KK, VV> ImmutableMapIterable<KK, VV> aggregateInPlaceBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Procedure2<? super VV, ? super V> mutatingAggregator);

    @Override
    <KK, VV> ImmutableMapIterable<KK, VV> aggregateBy(Function<? super V, ? extends KK> groupBy, Function0<? extends VV> zeroValueFactory, Function2<? super VV, ? super V, ? extends VV> nonMutatingAggregator);
}
