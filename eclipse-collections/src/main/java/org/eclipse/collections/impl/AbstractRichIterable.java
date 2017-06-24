/*
 * Copyright (c) 2017 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.procedure.AppendStringProcedure;
import org.eclipse.collections.impl.block.procedure.CollectIfProcedure;
import org.eclipse.collections.impl.block.procedure.CollectProcedure;
import org.eclipse.collections.impl.block.procedure.CountProcedure;
import org.eclipse.collections.impl.block.procedure.FlatCollectProcedure;
import org.eclipse.collections.impl.block.procedure.GroupByUniqueKeyProcedure;
import org.eclipse.collections.impl.block.procedure.InjectIntoProcedure;
import org.eclipse.collections.impl.block.procedure.MapCollectProcedure;
import org.eclipse.collections.impl.block.procedure.MaxByProcedure;
import org.eclipse.collections.impl.block.procedure.MaxComparatorProcedure;
import org.eclipse.collections.impl.block.procedure.MaxProcedure;
import org.eclipse.collections.impl.block.procedure.MinByProcedure;
import org.eclipse.collections.impl.block.procedure.MinComparatorProcedure;
import org.eclipse.collections.impl.block.procedure.MinProcedure;
import org.eclipse.collections.impl.block.procedure.MultimapEachPutProcedure;
import org.eclipse.collections.impl.block.procedure.MultimapPutProcedure;
import org.eclipse.collections.impl.block.procedure.RejectProcedure;
import org.eclipse.collections.impl.block.procedure.SelectProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfFloatProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfIntProcedure;
import org.eclipse.collections.impl.block.procedure.SumOfLongProcedure;
import org.eclipse.collections.impl.block.procedure.ZipWithIndexProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectBooleanProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectByteProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectCharProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectFloatProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectIntProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectLongProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.CollectShortProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoDoubleProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoFloatProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoIntProcedure;
import org.eclipse.collections.impl.block.procedure.primitive.InjectIntoLongProcedure;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.SortedMaps;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.eclipse.collections.impl.utility.internal.IterableIterate;

public abstract class AbstractRichIterable<T> implements RichIterable<T>
{
    @Override
    public boolean contains(Object object)
    {
        return this.anySatisfyWith(Predicates2.equal(), object);
    }

    @Override
    public boolean containsAllIterable(Iterable<?> source)
    {
        return Iterate.allSatisfyWith(source, Predicates2.in(), this);
    }

    @Override
    public boolean containsAllArguments(Object... elements)
    {
        return ArrayIterate.allSatisfyWith(elements, Predicates2.in(), this);
    }

    @Override
    public Object[] toArray()
    {
        Object[] result = new Object[this.size()];
        this.forEachWithIndex((each, index) -> result[index] = each);
        return result;
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        int size = this.size();
        E[] result = array.length < size
                ? (E[]) Array.newInstance(array.getClass().getComponentType(), size)
                : array;

        this.forEachWithIndex((each, index) -> result[index] = (E) each);
        if (result.length > size)
        {
            result[size] = null;
        }
        return result;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    @Override
    public MutableList<T> toList()
    {
        MutableList<T> list = Lists.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), list);
        return list;
    }

    @Override
    public <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedList(Comparators.byFunction(function));
    }

    @Override
    public MutableSortedSet<T> toSortedSet()
    {
        MutableSortedSet<T> treeSet = SortedSets.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), treeSet);
        return treeSet;
    }

    @Override
    public MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator)
    {
        MutableSortedSet<T> treeSet = SortedSets.mutable.with(comparator);
        this.forEachWith(Procedures2.addToCollection(), treeSet);
        return treeSet;
    }

    @Override
    public <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedSet(Comparators.byFunction(function));
    }

    @Override
    public MutableSet<T> toSet()
    {
        MutableSet<T> set = Sets.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), set);
        return set;
    }

    @Override
    public MutableBag<T> toBag()
    {
        MutableBag<T> bag = Bags.mutable.empty();
        this.forEachWith(Procedures2.addToCollection(), bag);
        return bag;
    }

    @Override
    public MutableSortedBag<T> toSortedBag()
    {
        MutableSortedBag<T> sortedBag = TreeBag.newBag();
        this.forEachWith(Procedures2.addToCollection(), sortedBag);
        return sortedBag;
    }

    @Override
    public MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator)
    {
        MutableSortedBag<T> sortedBag = TreeBag.newBag(comparator);
        this.forEachWith(Procedures2.addToCollection(), sortedBag);
        return sortedBag;
    }

    @Override
    public <V extends Comparable<? super V>> MutableSortedBag<T> toSortedBagBy(Function<? super T, ? extends V> function)
    {
        return this.toSortedBag(Comparators.byFunction(function));
    }

    @Override
    public <K, V> MutableMap<K, V> toMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableMap<K, V> map = Maps.mutable.empty();
        this.forEach(new MapCollectProcedure<>(map, keyFunction, valueFunction));
        return map;
    }

    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableSortedMap<K, V> sortedMap = SortedMaps.mutable.empty();
        this.forEach(new MapCollectProcedure<>(sortedMap, keyFunction, valueFunction));
        return sortedMap;
    }

    @Override
    public <K, V> MutableSortedMap<K, V> toSortedMap(
            Comparator<? super K> comparator,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        MutableSortedMap<K, V> sortedMap = SortedMaps.mutable.with(comparator);
        this.forEach(new MapCollectProcedure<>(sortedMap, keyFunction, valueFunction));
        return sortedMap;
    }

    @Override
    public <R extends Collection<T>> R select(Predicate<? super T> predicate, R target)
    {
        this.forEach(new SelectProcedure<>(predicate, target));
        return target;
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return this.select(Predicates.bind(predicate, parameter), target);
    }

    @Override
    public <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target)
    {
        this.forEach(new RejectProcedure<>(predicate, target));
        return target;
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R target)
    {
        return this.reject(Predicates.bind(predicate, parameter), target);
    }

    @Override
    public <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target)
    {
        this.forEach(new CollectProcedure<>(function, target));
        return target;
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R target)
    {
        return this.collect(Functions.bind(function, parameter), target);
    }

    @Override
    public <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        this.forEach(new CollectIfProcedure<>(target, function, predicate));
        return target;
    }

    @Override
    public <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function)
    {
        return this.detectIfNone(Predicates.bind(predicate, parameter), function);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        MinComparatorProcedure<T> procedure = new MinComparatorProcedure<>(comparator);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        MaxComparatorProcedure<T> procedure = new MaxComparatorProcedure<>(comparator);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public T min()
    {
        MinProcedure<T> procedure = new MinProcedure<>();
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public T max()
    {
        MaxProcedure<T> procedure = new MaxProcedure<>();
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        MinByProcedure<T, V> minByProcedure = new MinByProcedure<>(function);
        this.forEach(minByProcedure);
        return minByProcedure.getResult();
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        MaxByProcedure<T, V> maxByProcedure = new MaxByProcedure<>(function);
        this.forEach(maxByProcedure);
        return maxByProcedure.getResult();
    }

    @Override
    public LazyIterable<T> asLazy()
    {
        return LazyIterate.adapt(this);
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        this.forEach(new FlatCollectProcedure<>(function, target));
        return target;
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        return IterableIterate.detect(this, predicate);
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.detect(Predicates.bind(predicate, parameter));
    }

    @Override
    public Optional<T> detectOptional(Predicate<? super T> predicate)
    {
        return IterableIterate.detectOptional(this, predicate);
    }

    @Override
    public <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.detectOptional(Predicates.bind(predicate, parameter));
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return IterableIterate.anySatisfy(this, predicate);
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return IterableIterate.allSatisfy(this, predicate);
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        return IterableIterate.noneSatisfy(this, predicate);
    }

    @Override
    public <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.anySatisfy(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.allSatisfy(Predicates.bind(predicate, parameter));
    }

    @Override
    public <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.noneSatisfy(Predicates.bind(predicate, parameter));
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        CountProcedure<T> procedure = new CountProcedure<>(predicate);
        this.forEach(procedure);
        return procedure.getCount();
    }

    @Override
    public <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.count(Predicates.bind(predicate, parameter));
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        InjectIntoProcedure<IV, T> procedure = new InjectIntoProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        InjectIntoIntProcedure<T> procedure = new InjectIntoIntProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        InjectIntoLongProcedure<T> procedure = new InjectIntoLongProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        InjectIntoDoubleProcedure<T> procedure = new InjectIntoDoubleProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public <R extends Collection<T>> R into(R target)
    {
        return Iterate.addAllTo(this, target);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        InjectIntoFloatProcedure<T> procedure = new InjectIntoFloatProcedure<>(injectedValue, function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        SumOfIntProcedure<T> procedure = new SumOfIntProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        SumOfFloatProcedure<T> procedure = new SumOfFloatProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        SumOfLongProcedure<T> procedure = new SumOfLongProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        SumOfDoubleProcedure<T> procedure = new SumOfDoubleProcedure<>(function);
        this.forEach(procedure);
        return procedure.getResult();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        IterableIterate.forEachWithIndex(this, objectIntProcedure);
    }

    @Override
    public final void forEach(Procedure<? super T> procedure)
    {
        this.each(procedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        this.forEach(Procedures.bind(procedure, parameter));
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return IterableIterate.zip(this, that, target);
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        this.forEach(ZipWithIndexProcedure.create(target));
        return target;
    }

    /**
     * Returns a string with the elements of the iterable separated by commas with spaces and
     * enclosed in square brackets.
     * <p>
     * <pre>
     * Assert.assertEquals("[]", Lists.mutable.empty().toString());
     * Assert.assertEquals("[1]", Lists.mutable.with(1).toString());
     * Assert.assertEquals("[1, 2, 3]", Lists.mutable.with(1, 2, 3).toString());
     * </pre>
     * @see java.util.AbstractCollection#toString()
     *
     * @return a string representation of this collection.
     */
    @Override
    public String toString()
    {
        return this.makeString("[", ", ", "]");
    }

    @Override
    public void appendString(Appendable appendable, String separator)
    {
        AppendStringProcedure<T> appendStringProcedure = new AppendStringProcedure<>(appendable, separator);
        this.forEach(appendStringProcedure);
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        AppendStringProcedure<T> appendStringProcedure = new AppendStringProcedure<>(appendable, separator);
        try
        {
            appendable.append(start);
            this.forEach(appendStringProcedure);
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return this.containsAllIterable(collection);
    }

    @Override
    public <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target)
    {
        this.forEach(new CollectBooleanProcedure<>(booleanFunction, target));
        return target;
    }

    @Override
    public <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target)
    {
        this.forEach(new CollectByteProcedure<>(byteFunction, target));
        return target;
    }

    @Override
    public <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target)
    {
        this.forEach(new CollectCharProcedure<>(charFunction, target));
        return target;
    }

    @Override
    public <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target)
    {
        this.forEach(new CollectDoubleProcedure<>(doubleFunction, target));
        return target;
    }

    @Override
    public <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target)
    {
        this.forEach(new CollectFloatProcedure<>(floatFunction, target));
        return target;
    }

    @Override
    public <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target)
    {
        this.forEach(new CollectIntProcedure<>(intFunction, target));
        return target;
    }

    @Override
    public <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target)
    {
        this.forEach(new CollectLongProcedure<>(longFunction, target));
        return target;
    }

    @Override
    public <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target)
    {
        this.forEach(new CollectShortProcedure<>(shortFunction, target));
        return target;
    }

    /**
     * @since 9.0
     */
    @Override
    public <V> Bag<V> countBy(Function<? super T, ? extends V> function)
    {
        return this.countBy(function, Bags.mutable.empty());
    }

    /**
     * @since 9.0
     */
    @Override
    public <V, P> Bag<V> countByWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.countByWith(function, parameter, Bags.mutable.empty());
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        this.forEach(MultimapPutProcedure.on(target, function));
        return target;
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        this.forEach(MultimapEachPutProcedure.on(target, function));
        return target;
    }

    @Override
    public <V, R extends MutableMap<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target)
    {
        this.forEach(new GroupByUniqueKeyProcedure<>(target, function));
        return target;
    }
}
