/*
 * Copyright (c) 2017 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.stack;

import java.util.Collection;

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
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.multimap.list.MutableListMultimap;
import org.eclipse.collections.api.partition.stack.PartitionMutableStack;
import org.eclipse.collections.api.stack.primitive.MutableBooleanStack;
import org.eclipse.collections.api.stack.primitive.MutableByteStack;
import org.eclipse.collections.api.stack.primitive.MutableCharStack;
import org.eclipse.collections.api.stack.primitive.MutableDoubleStack;
import org.eclipse.collections.api.stack.primitive.MutableFloatStack;
import org.eclipse.collections.api.stack.primitive.MutableIntStack;
import org.eclipse.collections.api.stack.primitive.MutableLongStack;
import org.eclipse.collections.api.stack.primitive.MutableShortStack;
import org.eclipse.collections.api.tuple.Pair;

public interface MutableStack<T> extends StackIterable<T>
{
    /**
     * Adds an item to the top of the stack.
     */
    void push(T item);

    /**
     * Removes and returns the top element of the stack.
     */
    T pop();

    /**
     * Removes and returns a ListIterable of the number of elements specified by the count, beginning with the top of the stack.
     */
    ListIterable<T> pop(int count);

    /**
     * Removes and returns a ListIterable of the number of elements specified by the count,
     * beginning with the top of the stack and puts them into the targeted collection type.
     */
    <R extends Collection<T>> R pop(int count, R targetCollection);

    /**
     * Removes and returns a ListIterable of the number of elements specified by the count,
     * beginning with the top of the stack and puts them into a new stack.
     */
    <R extends MutableStack<T>> R pop(int count, R targetStack);

    void clear();

    @Override
    MutableStack<T> takeWhile(Predicate<? super T> predicate);

    @Override
    MutableStack<T> dropWhile(Predicate<? super T> predicate);

    @Override
    PartitionMutableStack<T> partitionWhile(Predicate<? super T> predicate);

    @Override
    MutableStack<T> distinct();

    MutableStack<T> asUnmodifiable();

    MutableStack<T> asSynchronized();

    @Override
    MutableStack<T> tap(Procedure<? super T> procedure);

    @Override
    MutableStack<T> select(Predicate<? super T> predicate);

    @Override
    <P> MutableStack<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    MutableStack<T> reject(Predicate<? super T> predicate);

    @Override
    <P> MutableStack<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <S> MutableStack<S> selectInstancesOf(Class<S> clazz);

    @Override
    PartitionMutableStack<T> partition(Predicate<? super T> predicate);

    @Override
    <P> PartitionMutableStack<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    @Override
    <V> MutableStack<V> collect(Function<? super T, ? extends V> function);

    @Override
    MutableBooleanStack collectBoolean(BooleanFunction<? super T> booleanFunction);

    @Override
    MutableByteStack collectByte(ByteFunction<? super T> byteFunction);

    @Override
    MutableCharStack collectChar(CharFunction<? super T> charFunction);

    @Override
    MutableDoubleStack collectDouble(DoubleFunction<? super T> doubleFunction);

    @Override
    MutableFloatStack collectFloat(FloatFunction<? super T> floatFunction);

    @Override
    MutableIntStack collectInt(IntFunction<? super T> intFunction);

    @Override
    MutableLongStack collectLong(LongFunction<? super T> longFunction);

    @Override
    MutableShortStack collectShort(ShortFunction<? super T> shortFunction);

    @Override
    <P, V> MutableStack<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    @Override
    <V> MutableStack<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    @Override
    <V> MutableStack<V> collectWithIndex(ObjectIntToObjectFunction<? super T, ? extends V> function);

    @Override
    <V> MutableStack<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <V> MutableObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function);

    @Override
    <V> MutableObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function);

    @Override
    <V> MutableObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function);

    @Override
    <V> MutableObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function);

    @Override
    <V> MutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    /**
     * @since 9.0
     */
    @Override
    default <V> MutableBag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.asLazy().<V>collect(function).toBag();
    }

    /**
     * @since 9.0
     */
    @Override
    default <V, P> MutableBag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.asLazy().<P, V>collectWith(function, parameter).toBag();
    }

    @Override
    <V> MutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    @Override
    <V> MutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function);

    @Override
    <S> MutableStack<Pair<T, S>> zip(Iterable<S> that);

    @Override
    MutableStack<Pair<T, Integer>> zipWithIndex();

    @Override
    <K, V> MutableMap<K, V> aggregateInPlaceBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super T> mutatingAggregator);

    @Override
    <K, V> MutableMap<K, V> aggregateBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Function2<? super V, ? super T, ? extends V> nonMutatingAggregator);
}
