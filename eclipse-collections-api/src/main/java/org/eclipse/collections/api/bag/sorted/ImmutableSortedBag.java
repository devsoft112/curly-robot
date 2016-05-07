/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.bag.sorted;

import net.jcip.annotations.Immutable;
import org.eclipse.collections.api.bag.ImmutableBagIterable;
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
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.sortedbag.ImmutableSortedBagMultimap;
import org.eclipse.collections.api.partition.bag.sorted.PartitionImmutableSortedBag;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * ImmutableSortedBag is the non-modifiable equivalent interface to {@link MutableSortedBag}.
 *
 * @since 4.2
 */
@Immutable
public interface ImmutableSortedBag<T>
        extends ImmutableBagIterable<T>, SortedBag<T>
{
    @Override
    ImmutableSortedBag<T> newWith(T element);

    @Override
    ImmutableSortedBag<T> newWithout(T element);

    @Override
    ImmutableSortedBag<T> newWithAll(Iterable<? extends T> elements);

    @Override
    ImmutableSortedBag<T> newWithoutAll(Iterable<? extends T> elements);

    @Override
    ImmutableSortedBag<T> selectByOccurrences(IntPredicate predicate);

    @Override
    ImmutableSortedBag<T> tap(Procedure<? super T> procedure);

    @Override
    ImmutableSortedBag<T> select(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSortedBag<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    ImmutableSortedBag<T> reject(Predicate<? super T> predicate);

    @Override
    <P> ImmutableSortedBag<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionImmutableSortedBag<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionImmutableSortedBag<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> ImmutableSortedBag<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> ImmutableList<V> collect(Function<? super T, ? extends V> function);

    @Override
    ImmutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    ImmutableByteList collectByte(ByteFunction<? super T> byteFunction);

    @Override
    ImmutableCharList collectChar(CharFunction<? super T> charFunction);

    @Override
    ImmutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    ImmutableFloatList collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    ImmutableIntList collectInt(IntFunction<? super T> intFunction);

    @Override
    ImmutableLongList collectLong(LongFunction<? super T> longFunction);

    @Override
    ImmutableShortList collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> ImmutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> ImmutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    @Override
    ImmutableSortedSet<T> distinct();

    @Override
    ImmutableSortedBag<T> takeWhile(Predicate<? super T> predicate);

    @Override
    ImmutableSortedBag<T> dropWhile(Predicate<? super T> predicate);

    @Override
    <V> ImmutableSortedBagMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> ImmutableSortedBagMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Can return an ImmutableMap that's backed by a LinkedHashMap.
     */
    @Override
    <K, V> ImmutableMap<K, V> aggregateBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator);

    /**
     * Can return an ImmutableMap that's backed by a LinkedHashMap.
     */
    @Override
    <K, V> ImmutableMap<K, V> aggregateInPlaceBy(
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator);

    @Override
    <S> ImmutableList<Pair<T, S>> zip(Iterable<S> that);

    @Override
    ImmutableSortedSet<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableSortedMap<T, Integer> toMapOfItemToCount();

    @Override
    ImmutableSortedBag<T> toReversed();

    @Override
    ImmutableSortedBag<T> take(int count);

    @Override
    ImmutableSortedBag<T> drop(int count);
}
