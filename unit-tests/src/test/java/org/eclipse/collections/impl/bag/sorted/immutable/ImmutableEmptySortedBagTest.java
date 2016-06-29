/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.immutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;

import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.partition.bag.sorted.PartitionImmutableSortedBag;
import org.eclipse.collections.api.partition.bag.sorted.PartitionSortedBag;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
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
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.PrimitiveFunctions;
import org.eclipse.collections.impl.factory.Iterables;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.SortedBags;
import org.eclipse.collections.impl.factory.SortedMaps;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.Stacks;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableEmptySortedBagTest extends AbstractImmutableSortedBagTestCase
{
    @Override
    protected ImmutableSortedBag<Integer> classUnderTest()
    {
        return SortedBags.immutable.empty();
    }

    @Override
    protected <T> MutableCollection<T> newMutable()
    {
        return SortedBags.mutable.empty();
    }

    @Override
    protected ImmutableSortedBag<Integer> classUnderTest(Comparator<? super Integer> comparator)
    {
        return SortedBags.immutable.empty(comparator);
    }

    @Override
    protected <T> ImmutableSortedBag<T> newWith(T... elements)
    {
        return (ImmutableSortedBag<T>) ImmutableEmptySortedBag.INSTANCE;
    }

    @Override
    protected <T> ImmutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return SortedBags.immutable.empty(comparator);
    }

    @Override
    @Test
    public void corresponds()
    {
        //Evaluates true for all empty lists and false for all non-empty lists

        Assert.assertTrue(this.classUnderTest().corresponds(Lists.mutable.of(), Predicates2.alwaysFalse()));

        ImmutableSortedBag<Integer> integers = this.classUnderTest().newWith(Integer.valueOf(1));
        Assert.assertFalse(this.classUnderTest().corresponds(integers, Predicates2.alwaysTrue()));
    }

    @Override
    @Test
    public void compareTo()
    {
        Assert.assertEquals(0, this.classUnderTest().compareTo(this.classUnderTest()));
        Assert.assertEquals(0, this.classUnderTest(Comparator.reverseOrder()).compareTo(this.classUnderTest(Comparator.reverseOrder())));
        Assert.assertEquals(0, this.classUnderTest(Comparator.naturalOrder()).compareTo(this.classUnderTest(Comparator.reverseOrder())));
        Assert.assertEquals(-1, this.classUnderTest().compareTo(TreeBag.newBagWith(1)));
        Assert.assertEquals(-1, this.classUnderTest(Comparator.reverseOrder()).compareTo(TreeBag.newBagWith(Comparator.reverseOrder(), 1)));
        Assert.assertEquals(-5, this.classUnderTest().compareTo(TreeBag.newBagWith(1, 2, 2, 3, 4)));
        Assert.assertEquals(0, this.classUnderTest().compareTo(TreeBag.newBag()));
        Assert.assertEquals(0, this.classUnderTest().compareTo(TreeBag.newBag(Comparator.reverseOrder())));
    }

    @Override
    @Test
    public void contains()
    {
        Assert.assertFalse(this.classUnderTest().contains(1));
        Assert.assertFalse(this.classUnderTest(Comparator.reverseOrder()).contains(1));
    }

    @Test(expected = NullPointerException.class)
    public void contains_null()
    {
        this.classUnderTest().contains(null);
        this.classUnderTest(Comparator.naturalOrder()).contains(null);
    }

    @Override
    public void allSatisfyWith()
    {
        Assert.assertTrue(this.classUnderTest().allSatisfyWith(Predicates2.alwaysFalse(), "false"));
        Assert.assertTrue(this.classUnderTest(Comparators.reverseNaturalOrder()).allSatisfyWith(Predicates2.alwaysFalse(), false));
    }

    @Override
    public void anySatisfyWith()
    {
        Assert.assertFalse(this.classUnderTest().anySatisfyWith(Predicates2.alwaysFalse(), "false"));
        Assert.assertFalse(this.classUnderTest(Comparators.reverseNaturalOrder()).anySatisfyWith(Predicates2.alwaysFalse(), false));
    }

    @Override
    public void noneSatisfyWith()
    {
        Assert.assertTrue(this.classUnderTest().noneSatisfyWith(Predicates2.alwaysFalse(), "false"));
        Assert.assertTrue(this.classUnderTest(Comparators.reverseNaturalOrder()).noneSatisfyWith(Predicates2.alwaysFalse(), false));
    }

    @Override
    public void noneSatisfy()
    {
        Assert.assertTrue(this.classUnderTest().noneSatisfy(Predicates.alwaysFalse()));
        Assert.assertTrue(this.classUnderTest(Comparators.reverseNaturalOrder()).noneSatisfy(Predicates.alwaysFalse()));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        Assert.assertFalse(this.classUnderTest().containsAllIterable(FastList.newListWith(1, 2, 3)));
        Assert.assertFalse(this.classUnderTest(Comparator.reverseOrder()).containsAllIterable(FastList.newListWith(1, 2, 3)));
    }

    @Override
    @Test
    public void containsAll()
    {
        Assert.assertFalse(this.classUnderTest().containsAll(FastList.newListWith(1, 2, 3)));
        Assert.assertFalse(this.classUnderTest(Comparator.reverseOrder()).containsAll(FastList.newListWith(1, 2, 3)));
    }

    @Override
    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableList<Integer> mutableList = Lists.mutable.empty();
        this.classUnderTest().forEachWithIndex(0, 0, (each, index) -> mutableList.add(each + index));
        Verify.assertEmpty(mutableList);
    }

    @Override
    @Test
    public void chunk_large_size()
    {
        Assert.assertEquals(Lists.immutable.empty(), this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void detect()
    {
        Assert.assertNull(this.classUnderTest().detect(each -> each % 2 == 0));
        Assert.assertNull(this.classUnderTest(Comparator.naturalOrder()).detect(each -> each % 2 == 0));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        Assert.assertTrue(this.classUnderTest().allSatisfy(each -> each % 2 == 0));
        Assert.assertTrue(this.classUnderTest(Comparators.reverseNaturalOrder()).allSatisfy(each -> each % 2 == 0));
    }

    @Override
    @Test
    public void detectWith()
    {
        Assert.assertNull(this.classUnderTest().detectWith(Predicates2.greaterThan(), 3));
        Assert.assertNull(this.classUnderTest(Comparators.reverseNaturalOrder()).detectWith(Predicates2.greaterThan(), 3));
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max()
    {
        this.classUnderTest(Comparators.reverseNaturalOrder()).max();
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void max_without_comparator()
    {
        this.classUnderTest().max();
        this.classUnderTest().max(Comparator.naturalOrder());
    }

    @Test(expected = NoSuchElementException.class)
    public void max_with_comparator()
    {
        this.classUnderTest().max(Comparator.naturalOrder());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void maxBy()
    {
        this.classUnderTest().maxBy(Functions.getToString());
        this.classUnderTest(Comparators.reverseNaturalOrder()).maxBy(Functions.getToString());
    }

    @Override
    @Test
    public void toSortedBag()
    {
        Assert.assertEquals(TreeBag.newBag(), this.classUnderTest().toSortedBag());
        Assert.assertEquals(TreeBag.newBag(Comparators.reverseNaturalOrder()), this.classUnderTest(Comparators.reverseNaturalOrder()).toSortedBag());
    }

    @Override
    @Test
    public void toSortedMap()
    {
        Assert.assertEquals(SortedMaps.mutable.empty(), this.classUnderTest().toSortedMap(Functions.getIntegerPassThru(), Functions.getIntegerPassThru()));
    }

    @Override
    @Test
    public void toSortedMap_with_comparator()
    {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMap(Comparators.reverseNaturalOrder(),
                Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
        Assert.assertEquals(Comparators.<String>reverseNaturalOrder(), map.comparator());
    }

    @Override
    @Test
    public void toStack()
    {
        Assert.assertEquals(Stacks.immutable.empty(), this.classUnderTest().toStack());
        Assert.assertEquals(Stacks.immutable.empty(), this.classUnderTest(Comparators.reverseNaturalOrder()).toStack());
    }

    @Override
    @Test
    public void toStringOfItemToCount()
    {
        Assert.assertEquals("{}", this.classUnderTest().toStringOfItemToCount());
        Assert.assertEquals("{}", this.classUnderTest(Comparator.reverseOrder()).toStringOfItemToCount());
    }

    @Override
    @Test
    public void groupByUniqueKey()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(Maps.mutable.empty(), bag.groupByUniqueKey(integer -> integer));
    }

    @Override
    @Test
    public void groupByUniqueKey_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                UnifiedMap.newWithKeysValues(0, 0),
                bag.groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Override
    @Test
    public void zip()
    {
        Assert.assertEquals(Lists.immutable.empty(), this.classUnderTest().zip(Iterables.iBag()));
        Assert.assertEquals(Lists.immutable.empty(), this.classUnderTest().zip(Iterables.iBag(), FastList.newList()));
        Assert.assertEquals(Lists.immutable.empty(), this.classUnderTest(Comparators.reverseNaturalOrder()).zip(Iterables.iBag()));
    }

    @Override
    public void zipWithIndex()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest(Comparator.reverseOrder());
        ImmutableSortedSet<Pair<Integer, Integer>> actual = bag.zipWithIndex();
        Assert.assertEquals(SortedSets.immutable.empty(), actual);
        Assert.assertSame(SortedSets.immutable.empty(Comparator.<Integer>reverseOrder()).comparator(), actual.comparator());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min()
    {
        this.classUnderTest(Comparator.reverseOrder()).min();
        this.classUnderTest(Comparator.reverseOrder()).min(Comparator.naturalOrder());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void min_without_comparator()
    {
        this.classUnderTest().min();
    }

    @Test(expected = NoSuchElementException.class)
    public void min_with_comparator()
    {
        this.classUnderTest().min(Comparator.naturalOrder());
    }

    @Override
    @Test(expected = NoSuchElementException.class)
    public void minBy()
    {
        this.classUnderTest().minBy(Functions.getToString());
    }

    @Override
    @Test
    public void newWithTest()
    {
        Assert.assertEquals(SortedBags.immutable.of(1), this.classUnderTest().newWith(1));
        Assert.assertEquals(SortedBags.immutable.of(Comparators.reverseNaturalOrder(), 1), this.classUnderTest(Comparators.reverseNaturalOrder()).newWith(1));
    }

    @Override
    @Test
    public void newWithAll()
    {
        Assert.assertEquals(SortedBags.immutable.ofAll(FastList.newListWith(1, 2, 3, 3)), this.classUnderTest().newWithAll(FastList.newListWith(1, 2, 3, 3)));
        Assert.assertEquals(SortedBags.immutable.ofAll(Comparators.reverseNaturalOrder(), FastList.newListWith(1, 2, 3, 3)), this.classUnderTest(Comparators.reverseNaturalOrder()).newWithAll(FastList.newListWith(1, 2, 3, 3)));
    }

    @Override
    @Test
    public void newWithout()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().newWithout(1));
        Assert.assertEquals(this.classUnderTest(Comparators.reverseNaturalOrder()), this.classUnderTest(Comparators.reverseNaturalOrder()).newWithout(1));
    }

    @Override
    @Test
    public void partition()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest(Collections.reverseOrder());
        PartitionImmutableSortedBag<Integer> partition = bag.partition(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assert.assertSame(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertSame(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void partitionWhile()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest(Collections.reverseOrder());
        PartitionSortedBag<Integer> partition = bag.partitionWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assert.assertSame(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertSame(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void toMapOfItemToCount()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest(Comparators.reverseNaturalOrder());
        TreeSortedMap<Object, Object> expectedMap = TreeSortedMap.newMap(Comparators.reverseNaturalOrder());
        MutableSortedMap<Integer, Integer> actualMap = bag.toMapOfItemToCount();
        Verify.assertSortedMapsEqual(expectedMap, actualMap);
        Assert.assertSame(expectedMap.comparator(), actualMap.comparator());
    }

    @Override
    @Test
    public void partitionWith()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest(Collections.reverseOrder());
        PartitionImmutableSortedBag<Integer> partition = bag.partitionWith(Predicates2.lessThan(), 4);
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void reject()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().reject(each -> each % 2 == 0));
        Assert.assertEquals(this.classUnderTest(Comparators.reverseNaturalOrder()), this.classUnderTest(Comparators.reverseNaturalOrder()).reject(each -> each % 2 == 0));
    }

    @Override
    @Test
    public void rejectWith()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().rejectWith(Predicates2.alwaysFalse(), 2));
        Assert.assertEquals(this.classUnderTest(Comparators.reverseNaturalOrder()), this.classUnderTest(Comparators.reverseNaturalOrder()).rejectWith(Predicates2.alwaysFalse(), 2));
    }

    @Override
    @Test
    public void rejectToTarget()
    {
        ImmutableSortedBag<Integer> integers = this.classUnderTest();
        Verify.assertEmpty(
                integers.reject(Predicates.lessThan(integers.size() + 1), FastList.newList()));
        Verify.assertListsEqual(integers.toList(),
                integers.reject(Predicates.greaterThan(integers.size()), FastList.newList()));

        ImmutableSortedBag<Integer> integers2 = this.classUnderTest();
        Verify.assertEmpty(
                integers2.reject(Predicates.lessThan(integers2.size() + 1), new HashBag<>()));
    }

    @Override
    public void toSortedSet()
    {
        ImmutableSortedBag<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Assert.assertEquals(SortedSets.immutable.empty(), set);
    }

    @Override
    public void toSortedSetWithComparator()
    {
        ImmutableSortedBag<Integer> integers = this.classUnderTest(Comparators.reverseNaturalOrder());
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Assert.assertEquals(SortedSets.immutable.of(Comparator.<Integer>reverseOrder()), set);
    }

    @Override
    @Test
    public void select()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().select(each -> each % 2 == 0));
        Assert.assertEquals(this.classUnderTest(Comparators.reverseNaturalOrder()), this.classUnderTest(Comparators.reverseNaturalOrder()).select(each -> each % 2 == 0));
    }

    @Override
    @Test
    public void selectWith()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().selectWith(Predicates2.alwaysFalse(), "false"));
    }

    @Override
    @Test
    public void takeWhile()
    {
        ImmutableSortedBag<Integer> set = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedBag<Integer> take = set.takeWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(take);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), take.comparator());
    }

    @Override
    @Test
    public void distinct()
    {
        Assert.assertEquals(SortedSets.immutable.empty(), this.classUnderTest().distinct());
        ImmutableSortedSet<Object> expected = SortedSets.immutable.with(Comparators.reverseNaturalOrder());
        ImmutableSortedSet<Integer> actual = this.classUnderTest(Comparators.reverseNaturalOrder()).distinct();
        Assert.assertEquals(expected, actual);
        Assert.assertSame(expected.comparator(), actual.comparator());
    }

    @Override
    @Test
    public void dropWhile()
    {
        ImmutableSortedBag<Integer> set = this.classUnderTest(Collections.reverseOrder());
        ImmutableSortedBag<Integer> drop = set.dropWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(drop);
        Assert.assertEquals(Collections.<Integer>reverseOrder(), drop.comparator());
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        ImmutableSortedBag<Integer> immutable = this.classUnderTest();
        Verify.assertEqualsAndHashCode(HashBag.newBag(), immutable);
        Verify.assertPostSerializedIdentity(immutable);
        Assert.assertNotEquals(Lists.mutable.empty(), immutable);

        ImmutableSortedBag<Integer> bagWithComparator = this.classUnderTest(Comparators.reverseNaturalOrder());
        Verify.assertEqualsAndHashCode(HashBag.newBag(), bagWithComparator);
        Verify.assertPostSerializedEqualsAndHashCode(bagWithComparator);
    }

    @Override
    @Test
    public void getLast()
    {
        Assert.assertNull(this.classUnderTest().getLast());
        Assert.assertNull(this.classUnderTest(Comparators.reverseNaturalOrder()).getLast());
    }

    @Override
    @Test
    public void getFirst()
    {
        Assert.assertNull(this.classUnderTest().getFirst());
        Assert.assertNull(this.classUnderTest(Comparators.reverseNaturalOrder()).getFirst());
    }

    @Test
    public void getOnly()
    {
        Verify.assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOnly());
    }

    @Override
    public void detectIndex()
    {
        Assert.assertEquals(-1, this.classUnderTest().detectIndex(each -> each > 1));
    }

    @Override
    public void indexOf()
    {
        Assert.assertEquals(-1, this.classUnderTest().indexOf(1));
    }

    @Override
    @Test
    public void occurrencesOf()
    {
        Assert.assertEquals(0, this.classUnderTest().occurrencesOf(1));
    }

    @Override
    @Test
    public void isEmpty()
    {
        Assert.assertTrue(this.classUnderTest().isEmpty());
        Assert.assertTrue(this.classUnderTest(Comparators.reverseNaturalOrder()).isEmpty());
    }

    @Override
    @Test
    public void anySatisfy()
    {
        Assert.assertFalse(this.classUnderTest().anySatisfy(each -> each * 2 == 4));
        Assert.assertFalse(this.classUnderTest(Comparators.reverseNaturalOrder()).anySatisfy(each -> each * 2 == 4));
    }

    @Override
    @Test
    public void collectIfToTarget()
    {
        ImmutableSortedBag<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers.toBag(), integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru(), HashBag.newBag()));
    }

    @Override
    @Test
    public void topOccurrences()
    {
        Assert.assertEquals(0, this.classUnderTest().topOccurrences(5).size());
    }

    @Override
    @Test
    public void bottomOccurrences()
    {
        Assert.assertEquals(0, this.newWith().bottomOccurrences(5).size());
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new BooleanArrayList(),
                bag.collectBoolean(each -> false));
    }

    @Override
    @Test
    public void collectByte()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new ByteArrayList(),
                bag.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Override
    @Test
    public void collectChar()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new CharArrayList(),
                bag.collectChar(PrimitiveFunctions.unboxIntegerToChar()));
    }

    @Override
    @Test
    public void collectDouble()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new DoubleArrayList(),
                bag.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Override
    @Test
    public void collectFloat()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new FloatArrayList(),
                bag.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Override
    @Test
    public void collectInt()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new IntArrayList(),
                bag.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Override
    @Test
    public void collectLong()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new LongArrayList(),
                bag.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Override
    @Test
    public void collectShort()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new ShortArrayList(),
                bag.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Override
    @Test
    public void collectBoolean_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new BooleanArrayList(),
                bag.collectBoolean(each -> false, new BooleanArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new BooleanHashBag(),
                bag2.collectBoolean(each -> false, new BooleanHashBag()));
    }

    @Override
    @Test
    public void collectByte_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new ByteArrayList(),
                bag.collectByte(PrimitiveFunctions.unboxIntegerToByte(), new ByteArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new ByteHashBag(),
                bag2.collectByte(PrimitiveFunctions.unboxIntegerToByte(), new ByteHashBag()));
    }

    @Override
    @Test
    public void collectChar_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new CharArrayList(),
                bag.collectChar(PrimitiveFunctions.unboxIntegerToChar(), new CharArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new CharHashBag(),
                bag2.collectChar(PrimitiveFunctions.unboxIntegerToChar(), new CharHashBag()));
    }

    @Override
    @Test
    public void collectDouble_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new DoubleArrayList(),
                bag.collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), new DoubleArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new DoubleHashBag(),
                bag2.collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), new DoubleHashBag()));
    }

    @Override
    @Test
    public void collectFloat_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new FloatArrayList(),
                bag.collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), new FloatArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new FloatHashBag(),
                bag2.collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), new FloatHashBag()));
    }

    @Override
    @Test
    public void collectInt_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new IntArrayList(),
                bag.collectInt(PrimitiveFunctions.unboxIntegerToInt(), new IntArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new IntHashBag(),
                bag2.collectInt(PrimitiveFunctions.unboxIntegerToInt(), new IntHashBag()));
    }

    @Override
    @Test
    public void collectLong_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new LongArrayList(),
                bag.collectLong(PrimitiveFunctions.unboxIntegerToLong(), new LongArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new LongHashBag(),
                bag2.collectLong(PrimitiveFunctions.unboxIntegerToLong(), new LongHashBag()));
    }

    @Override
    @Test
    public void collectShort_target()
    {
        ImmutableSortedBag<Integer> bag = this.classUnderTest();
        Assert.assertEquals(
                new ShortArrayList(),
                bag.collectShort(PrimitiveFunctions.unboxIntegerToShort(), new ShortArrayList()));

        ImmutableSortedBag<Integer> bag2 = this.classUnderTest();
        Assert.assertEquals(
                new ShortHashBag(),
                bag2.collectShort(PrimitiveFunctions.unboxIntegerToShort(), new ShortHashBag()));
    }

    @Override
    @Test
    public void toArray()
    {
        ImmutableSortedBag<Integer> integers = this.classUnderTest(Collections.reverseOrder());
        MutableList<Integer> copy = FastList.newList(integers);
        Assert.assertArrayEquals(integers.toArray(), copy.toArray());
        Assert.assertArrayEquals(integers.toArray(new Integer[integers.size()]), copy.toArray(new Integer[integers.size()]));
    }

    @Override
    @Test
    public void take()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().take(2));
    }

    @Override
    @Test
    public void drop()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().drop(2));
    }
}
