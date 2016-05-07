/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.utility.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.block.function.MaxSizeFunction;
import org.eclipse.collections.impl.block.function.MinSizeFunction;
import org.eclipse.collections.impl.block.procedure.DoNothingProcedure;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.AddToList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;
import static org.eclipse.collections.impl.factory.Iterables.mList;

public class RandomAccessListIterateTest
{
    @Test(expected = IndexOutOfBoundsException.class)
    public void forEachWithNegativeFroms()
    {
        RandomAccessListIterate.forEach(FastList.newList(), -1, 1, DoNothingProcedure.DO_NOTHING);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void forEachWithNegativeTos()
    {
        RandomAccessListIterate.forEach(FastList.newList(), 1, -1, DoNothingProcedure.DO_NOTHING);
    }

    @Test
    public void forEachInBothWithNull()
    {
        RandomAccessListIterate.forEachInBoth(null, FastList.newListWith(1, 2, 3), new FailProcedure2());
        RandomAccessListIterate.forEachInBoth(FastList.newListWith(1, 2, 3), null, new FailProcedure2());
    }

    @Test(expected = IllegalArgumentException.class)
    public void forEachInBothThrowsOnMisMatchedLists()
    {
        RandomAccessListIterate.forEachInBoth(FastList.newListWith("1", 2), FastList.newListWith(1, 2, 3),
                Procedures2.fromProcedure(DoNothingProcedure.DO_NOTHING));
    }

    @Test
    public void removeIf()
    {
        Assert.assertTrue(RandomAccessListIterate.removeIf(FastList.newListWith(1, 2, 3), Predicates.greaterThan(1)));
        Assert.assertTrue(RandomAccessListIterate.removeIf(FastList.newListWith(1, 2, 3), Predicates.greaterThan(0)));
        Assert.assertFalse(RandomAccessListIterate.removeIf(FastList.newListWith(1, 2, 3), Predicates.greaterThan(4)));
        Assert.assertFalse(RandomAccessListIterate.removeIf(FastList.newList(), Predicates.greaterThan(4)));
    }

    @Test
    public void removeIfWith()
    {
        Assert.assertTrue(RandomAccessListIterate.removeIfWith(FastList.newListWith(1, 2, 3), Predicates2.greaterThan(), 1));
        Assert.assertTrue(RandomAccessListIterate.removeIfWith(FastList.newListWith(1, 2, 3), Predicates2.greaterThan(), 0));
        Assert.assertFalse(RandomAccessListIterate.removeIfWith(FastList.newListWith(1, 2, 3), Predicates2.greaterThan(), 4));
        Assert.assertFalse(RandomAccessListIterate.removeIfWith(FastList.newList(), Predicates2.greaterThan(), 1));
    }

    @Test
    public void injectInto()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        Assert.assertEquals(Integer.valueOf(7), RandomAccessListIterate.injectInto(1, list, AddFunction.INTEGER));
    }

    @Test
    public void injectIntoInt()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        Assert.assertEquals(7, RandomAccessListIterate.injectInto(1, list, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        Assert.assertEquals(7, RandomAccessListIterate.injectInto(1, list, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble()
    {
        MutableList<Double> list = Lists.fixedSize.of(1.0, 2.0, 3.0);
        Assert.assertEquals(7.0d, RandomAccessListIterate.injectInto(1.0, list, AddFunction.DOUBLE), 0.001);
    }

    @Test
    public void injectIntoString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        Assert.assertEquals("0123", RandomAccessListIterate.injectInto("0", list, AddFunction.STRING));
    }

    @Test
    public void injectIntoMaxString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "12", "123");
        Function2<Integer, String, Integer> function = MaxSizeFunction.STRING;
        Assert.assertEquals(Integer.valueOf(3), RandomAccessListIterate.injectInto(Integer.MIN_VALUE, list, function));
    }

    @Test
    public void injectIntoMinString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "12", "123");
        Function2<Integer, String, Integer> function = MinSizeFunction.STRING;
        Assert.assertEquals(Integer.valueOf(1), RandomAccessListIterate.injectInto(Integer.MAX_VALUE, list, function));
    }

    @Test
    public void collect()
    {
        Assert.assertEquals(
                iList("true", "false", "null"),
                RandomAccessListIterate.collect(mList(true, false, null), String::valueOf));
    }

    @Test
    public void collectReflective()
    {
        Assert.assertEquals(
                iList("true", "false", "null"),
                RandomAccessListIterate.collect(mList(true, false, null), String::valueOf));

        Assert.assertEquals(
                iList("true", "false", "null"),
                RandomAccessListIterate.collect(mList(true, false, null), String::valueOf, new ArrayList<>()));
    }

    @Test
    public void flattenReflective()
    {
        MutableList<MutableList<Boolean>> list = Lists.fixedSize.of(
                Lists.fixedSize.of(true, false),
                Lists.fixedSize.of(true, null));
        MutableList<Boolean> newList = RandomAccessListIterate.flatCollect(list, RichIterable::toList);
        Verify.assertListsEqual(
                FastList.newListWith(true, false, true, null),
                newList);

        MutableSet<Boolean> newSet = RandomAccessListIterate.flatCollect(list, RichIterable::toSet, UnifiedSet.newSet());
        Verify.assertSetsEqual(
                UnifiedSet.newSetWith(true, false, null),
                newSet);
    }

    @Test
    public void getLast()
    {
        MutableList<Boolean> list = Lists.fixedSize.of(true, null, false);
        Assert.assertEquals(Boolean.FALSE, RandomAccessListIterate.getLast(list));
    }

    @Test
    public void getLastOnEmpty()
    {
        List<?> list = new ArrayList<>();
        Assert.assertNull(RandomAccessListIterate.getLast(list));
    }

    @Test
    public void count()
    {
        MutableList<Integer> list = this.getIntegerList();
        int result = RandomAccessListIterate.count(list, Predicates.attributeEqual(Number::intValue, 3));
        Assert.assertEquals(1, result);
        int result2 = RandomAccessListIterate.count(list, Predicates.attributeEqual(Number::intValue, 6));
        Assert.assertEquals(0, result2);
    }

    private MutableList<Integer> getIntegerList()
    {
        return Interval.toReverseList(1, 5);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableList<Integer> list = this.getIntegerList();
        Iterate.sortThis(list);
        RandomAccessListIterate.forEachWithIndex(list, (object, index) -> Assert.assertEquals(index, object - 1));
    }

    @Test
    public void forEachFromTo()
    {
        MutableList<Integer> integers = Lists.mutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        MutableList<Integer> result = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 5, 7, result::add);
        Assert.assertEquals(Lists.immutable.with(3, 3, 2), result);

        MutableList<Integer> result2 = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 5, 5, result2::add);
        Assert.assertEquals(Lists.immutable.with(3), result2);

        MutableList<Integer> result3 = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 0, 9, result3::add);
        Assert.assertEquals(Lists.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), result3);

        MutableList<Integer> result4 = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 7, 5, result4::add);
        Assert.assertEquals(Lists.immutable.with(2, 3, 3), result4);

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEach(integers, -1, 0, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEach(integers, 0, -1, result::add));
    }

    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableList<Integer> integers = Lists.mutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        StringBuilder builder = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 5, 7, (each, index) -> builder.append(each).append(index));
        Assert.assertEquals("353627", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 5, 5, (each, index) -> builder2.append(each).append(index));
        Assert.assertEquals("35", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 0, 9, (each, index) -> builder3.append(each).append(index));
        Assert.assertEquals("40414243343536272819", builder3.toString());

        StringBuilder builder4 = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 7, 5, (each, index) -> builder4.append(each).append(index));
        Assert.assertEquals("273635", builder4.toString());

        MutableList<Integer> result = Lists.mutable.of();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEachWithIndex(integers, -1, 0, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEachWithIndex(integers, 0, -1, new AddToList(result)));
    }

    @Test
    public void forEachInBoth()
    {
        MutableList<String> list1 = Lists.fixedSize.of("1", "2");
        MutableList<String> list2 = Lists.fixedSize.of("a", "b");
        List<Pair<String, String>> list = new ArrayList<>();
        RandomAccessListIterate.forEachInBoth(list1, list2, (argument1, argument2) -> list.add(Tuples.twin(argument1, argument2)));
        Assert.assertEquals(FastList.newListWith(Tuples.twin("1", "a"), Tuples.twin("2", "b")), list);
    }

    @Test
    public void detectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertEquals(Integer.valueOf(1), RandomAccessListIterate.detectWith(list, Object::equals, 1));
        MutableList<Integer> list2 = Lists.fixedSize.of(1, 2, 2);
        Assert.assertSame(list2.get(1), RandomAccessListIterate.detectWith(list2, Object::equals, 2));
    }

    @Test
    public void selectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertSize(5, RandomAccessListIterate.selectWith(list, Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void rejectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertEmpty(RandomAccessListIterate.rejectWith(list, Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void distinct()
    {
        MutableList<Integer> list = FastList.newListWith(5, 2, 6, 2, 3, 5, 2);
        MutableList<Integer> actualList = FastList.newList();
        RandomAccessListIterate.distinct(list, actualList);
        Verify.assertListsEqual(FastList.newListWith(5, 2, 6, 3), actualList);
        Verify.assertSize(7, list);
    }

    @Test
    public void selectAndRejectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Twin<MutableList<Integer>> result = RandomAccessListIterate.selectAndRejectWith(list, Predicates2.in(), Lists.fixedSize.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Test
    public void anySatisfyWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(RandomAccessListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        Assert.assertFalse(RandomAccessListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Double.class));
    }

    @Test
    public void allSatisfyWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Assert.assertTrue(RandomAccessListIterate.allSatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        Assert.assertFalse(RandomAccessListIterate.allSatisfyWith(list, greaterThanPredicate, 2));
    }

    @Test
    public void countWith()
    {
        Assert.assertEquals(5, RandomAccessListIterate.countWith(this.getIntegerList(), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void collectIf()
    {
        MutableList<Integer> integers = Lists.fixedSize.of(1, 2, 3);
        Verify.assertContainsAll(RandomAccessListIterate.collectIf(integers, Integer.class::isInstance, String::valueOf), "1", "2", "3");
        Verify.assertContainsAll(RandomAccessListIterate.collectIf(integers, Integer.class::isInstance, String::valueOf, new ArrayList<>()), "1", "2", "3");
    }

    @Test
    public void take()
    {
        MutableList<Integer> integers = this.getIntegerList();
        Verify.assertListsEqual(integers.take(0), RandomAccessListIterate.take(integers, 0));
        Verify.assertListsEqual(integers.take(1), RandomAccessListIterate.take(integers, 1));
        Verify.assertListsEqual(integers.take(2), RandomAccessListIterate.take(integers, 2));
        Verify.assertListsEqual(integers.take(5), RandomAccessListIterate.take(integers, 5));
        Verify.assertListsEqual(
                integers.take(integers.size() - 1),
                RandomAccessListIterate.take(integers, integers.size() - 1));
        Verify.assertListsEqual(integers.take(integers.size()), RandomAccessListIterate.take(integers, integers.size()));
        Verify.assertListsEqual(integers.take(10), RandomAccessListIterate.take(integers, 10));
        Verify.assertListsEqual(integers.take(Integer.MAX_VALUE), RandomAccessListIterate.take(integers, Integer.MAX_VALUE));
        Verify.assertListsEqual(FastList.newList(), RandomAccessListIterate.take(Lists.fixedSize.of(), 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_throws()
    {
        RandomAccessListIterate.take(this.getIntegerList(), -1);
    }

    @Test
    public void take_target()
    {
        MutableList<Integer> integers = this.getIntegerList();

        MutableList<Integer> expected1 = FastList.newListWith(-1);
        expected1.addAll(integers.take(2));
        Verify.assertListsEqual(expected1, RandomAccessListIterate.take(integers, 2, FastList.newListWith(-1)));

        MutableList<Integer> expected2 = FastList.newListWith(-1);
        expected2.addAll(integers.take(0));
        Verify.assertListsEqual(expected2, RandomAccessListIterate.take(integers, 0, FastList.newListWith(-1)));

        MutableList<Integer> expected3 = FastList.newListWith(-1);
        expected3.addAll(integers.take(5));
        Verify.assertListsEqual(expected3, RandomAccessListIterate.take(integers, 5, FastList.newListWith(-1)));

        MutableList<Integer> expected4 = FastList.newListWith(-1);
        expected4.addAll(integers.take(10));
        Verify.assertListsEqual(expected4, RandomAccessListIterate.take(integers, 10, FastList.newListWith(-1)));

        MutableList<Integer> expected5 = FastList.newListWith(-1);
        expected5.addAll(integers.take(Integer.MAX_VALUE));
        Verify.assertListsEqual(expected5, RandomAccessListIterate.take(integers, Integer.MAX_VALUE, FastList.newListWith(-1)));
        Verify.assertListsEqual(FastList.newListWith(-1), RandomAccessListIterate.take(Lists.fixedSize.of(), 2, FastList.newListWith(-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take__target_throws()
    {
        RandomAccessListIterate.take(this.getIntegerList(), -1, FastList.newList());
    }

    @Test
    public void drop()
    {
        MutableList<Integer> integers = this.getIntegerList();
        Verify.assertListsEqual(integers.drop(0), RandomAccessListIterate.drop(integers, 0));
        Verify.assertListsEqual(integers.drop(1), RandomAccessListIterate.drop(integers, 1));
        Verify.assertListsEqual(integers.drop(2), RandomAccessListIterate.drop(integers, 2));
        Verify.assertListsEqual(integers.drop(5), RandomAccessListIterate.drop(integers, 5));
        Verify.assertListsEqual(integers.drop(6), RandomAccessListIterate.drop(integers, 6));
        Verify.assertListsEqual(
                integers.drop(integers.size() - 1),
                RandomAccessListIterate.drop(integers, integers.size() - 1));
        Verify.assertListsEqual(integers.drop(integers.size()), RandomAccessListIterate.drop(integers, integers.size()));
        Verify.assertListsEqual(FastList.newList(), RandomAccessListIterate.drop(Lists.fixedSize.of(), 0));
        Verify.assertListsEqual(FastList.newList(), RandomAccessListIterate.drop(Lists.fixedSize.of(), 2));
        Verify.assertListsEqual(integers.drop(Integer.MAX_VALUE), RandomAccessListIterate.drop(integers, Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_throws()
    {
        RandomAccessListIterate.drop(this.getIntegerList(), -1);
    }

    @Test
    public void drop_target()
    {
        MutableList<Integer> integers = this.getIntegerList();

        MutableList<Integer> expected1 = FastList.newListWith(-1);
        expected1.addAll(integers.drop(2));
        Verify.assertListsEqual(expected1, RandomAccessListIterate.drop(integers, 2, FastList.newListWith(-1)));

        MutableList<Integer> expected2 = FastList.newListWith(-1);
        expected2.addAll(integers.drop(5));
        Verify.assertListsEqual(expected2, RandomAccessListIterate.drop(integers, 5, FastList.newListWith(-1)));

        MutableList<Integer> expected3 = FastList.newListWith(-1);
        expected3.addAll(integers.drop(6));
        Verify.assertListsEqual(expected3, RandomAccessListIterate.drop(integers, 6, FastList.newListWith(-1)));

        MutableList<Integer> expected4 = FastList.newListWith(-1);
        expected4.addAll(integers.drop(Integer.MAX_VALUE));
        Verify.assertListsEqual(expected4, RandomAccessListIterate.drop(integers, Integer.MAX_VALUE, FastList.newListWith(-1)));

        MutableList<Integer> expected5 = FastList.newListWith(-1);
        expected5.addAll(integers.drop(0));
        Verify.assertListsEqual(expected5, RandomAccessListIterate.drop(integers, 0, FastList.newListWith(-1)));

        Verify.assertListsEqual(FastList.newListWith(-1), RandomAccessListIterate.drop(Lists.fixedSize.of(), 0, FastList.newListWith(-1)));
        Verify.assertListsEqual(FastList.newListWith(-1), RandomAccessListIterate.drop(Lists.fixedSize.of(), 2, FastList.newListWith(-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_target_throws()
    {
        RandomAccessListIterate.drop(this.getIntegerList(), -1, FastList.newList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void corresponds_throws_nonRandomAccess()
    {
        RandomAccessListIterate.corresponds(new LinkedList<>(), FastList.newList(), Predicates2.alwaysTrue());
    }

    private static class FailProcedure2 implements Procedure2<Object, Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void value(Object argument1, Integer argument2)
        {
            Assert.fail();
        }
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(RandomAccessListIterate.class);
    }
}
