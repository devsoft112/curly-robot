/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.set.sorted;

import java.util.SortedSet;

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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.MutableSetIterable;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.Pair;

/**
 * A MutableSortedSet is an implementation of a JCF SortedSet which provides methods matching the Smalltalk Collection
 * protocol.
 *
 * @since 1.0
 */
public interface MutableSortedSet<T>
        extends MutableSetIterable<T>, SortedSetIterable<T>, SortedSet<T>, Cloneable
{
    @Override
    MutableSortedSet<T> with(T element);

    @Override
    MutableSortedSet<T> without(T element);

    @Override
    MutableSortedSet<T> withAll(Iterable<? extends T> elements);

    @Override
    MutableSortedSet<T> withoutAll(Iterable<? extends T> elements);

    @Override
    MutableSortedSet<T> newEmpty();

    MutableSortedSet<T> clone();

    @Override
    MutableSortedSet<T> tap(Procedure<? super T> procedure);

    @Override
    MutableSortedSet<T> select(Predicate<? super T> predicate);

    @Override
    <P> MutableSortedSet<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    MutableSortedSet<T> reject(Predicate<? super T> predicate);

    @Override
    <P> MutableSortedSet<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionMutableSortedSet<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableSortedSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    PartitionMutableSortedSet<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    <S> MutableSortedSet<S> selectInstancesOf(Class<S> clazz);

    @Override
    <V> MutableList<V> collect(Function<? super T, ? extends V> function);

    @Override
    MutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    MutableByteList collectByte(ByteFunction<? super T> byteFunction);

    @Override
    MutableCharList collectChar(CharFunction<? super T> charFunction);

    @Override
    MutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    MutableFloatList collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    MutableIntList collectInt(IntFunction<? super T> intFunction);

    @Override
    MutableLongList collectLong(LongFunction<? super T> longFunction);

    @Override
    MutableShortList collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> MutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> MutableList<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> MutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    @Override
    MutableSortedSet<T> distinct();

    @Override
    MutableSortedSet<T> takeWhile(Predicate<? super T> predicate);

    @Override
    MutableSortedSet<T> dropWhile(Predicate<? super T> predicate);

    /**
     * Returns an unmodifable view of the set.
     *
     * @return an unmodifiable view of this set
     */
    @Override
    MutableSortedSet<T> asUnmodifiable();

    @Override
    MutableSortedSet<T> asSynchronized();

    /**
     * Returns an immutable copy of this set. If the set is immutable, it returns itself.
     */
    @Override
    ImmutableSortedSet<T> toImmutable();

    @Override
    <V> MutableSortedSetMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    @Override
    <V> MutableSortedSetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    // TODO Return linked set
    @Override
    <S> MutableList<Pair<T, S>> zip(Iterable<S> that);

    @Override
    MutableSortedSet<Pair<T, Integer>> zipWithIndex();

    @Override
    MutableSortedSet<T> toReversed();

    @Override
    MutableSortedSet<T> take(int count);

    @Override
    MutableSortedSet<T> drop(int count);

    @Override
    MutableSortedSet<T> union(SetIterable<? extends T> set);

    @Override
    MutableSortedSet<T> intersect(SetIterable<? extends T> set);

    @Override
    MutableSortedSet<T> difference(SetIterable<? extends T> subtrahendSet);

    @Override
    MutableSortedSet<T> symmetricDifference(SetIterable<? extends T> setB);

    @Override
    MutableSortedSet<SortedSetIterable<T>> powerSet();

    @Override
    MutableSortedSet<T> subSet(T fromElement, T toElement);

    @Override
    MutableSortedSet<T> headSet(T toElement);

    @Override
    MutableSortedSet<T> tailSet(T fromElement);
}
