/*
 * Copyright (c) 2017 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import org.eclipse.collections.api.partition.set.sorted.PartitionMutableSortedSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.set.sorted.SortedSetIterable;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.function.NegativeIntervalFunction;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionTestCase;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.Stacks;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.AddToList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Person;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;

/**
 * Abstract JUnit test for {@link MutableSortedSet}s.
 */
public abstract class AbstractSortedSetTestCase extends AbstractCollectionTestCase
{
    @Override
    protected abstract <T> MutableSortedSet<T> newWith(T... littleElements);

    protected abstract <T> MutableSortedSet<T> newWith(Comparator<? super T> comparator, T... elements);

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();
        Verify.assertInstanceOf(ImmutableSortedSet.class, this.newWith().toImmutable());
    }

    @Override
    @Test
    public void addAll()
    {
        super.addAll();

        TreeSortedSet<Integer> expected = TreeSortedSet.newSet(FastList.newListWith(1, 2, 3));
        MutableSortedSet<Integer> collection = this.newWith();

        Assert.assertTrue(collection.addAll(FastList.newListWith(3, 2, 1)));
        Assert.assertEquals(expected, collection);

        Assert.assertFalse(collection.addAll(FastList.newListWith(1, 2, 3)));
        Assert.assertEquals(expected, collection);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        TreeSortedSet<Integer> expected = TreeSortedSet.newSet(FastList.newListWith(1, 2, 3));
        MutableSortedSet<Integer> collection = this.newWith();

        Assert.assertTrue(collection.addAllIterable(FastList.newListWith(1, 2, 3)));
        Assert.assertEquals(expected, collection);

        Assert.assertFalse(collection.addAllIterable(FastList.newListWith(1, 2, 3)));
        Assert.assertEquals(expected, collection);
    }

    @Override
    @Test
    public void testToString()
    {
        Assert.assertEquals("[1, 2]", this.newWith(1, 2).toString());
    }

    @Override
    @Test
    public void makeString()
    {
        Assert.assertEquals(this.newWith(1, 2, 3).toString(), '[' + this.newWith(1, 2, 3).makeString() + ']');
    }

    @Override
    @Test
    public void appendString()
    {
        MutableCollection<Integer> mutableCollection = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        mutableCollection.appendString(builder);
        Assert.assertEquals(mutableCollection.toString(), '[' + builder.toString() + ']');
    }

    @Override
    @Test
    public void removeIf()
    {
        MutableSortedSet<Integer> objects = this.newWith(4, 1, 3, 2);
        Assert.assertTrue(objects.removeIf(Predicates.equal(2)));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 3, 4), objects);
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        MutableSortedSet<Integer> sortedSet = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> hashSet = UnifiedSet.newSet(sortedSet);
        Verify.assertEqualsAndHashCode(hashSet, sortedSet);
    }

    @Override
    @Test
    public void tap()
    {
        super.tap();

        MutableList<Integer> tapRevResult = Lists.mutable.of();
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        Assert.assertSame(revInt, revInt.tap(tapRevResult::add));
        Assert.assertEquals(revInt.toList(), tapRevResult);
    }

    @Test
    public void corresponds()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        Assert.assertFalse(integers1.corresponds(this.newWith(1, 2, 3, 4, 5), Predicates2.alwaysTrue()));

        MutableList<Integer> integers2 = integers1.collect(integer -> integer + 1, FastList.newList());
        Assert.assertTrue(integers1.corresponds(integers2, Predicates2.lessThan()));
        Assert.assertFalse(integers1.corresponds(integers2, Predicates2.greaterThan()));

        MutableSortedSet<Integer> integers3 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        Assert.assertFalse(integers3.corresponds(integers1, Predicates2.equal()));
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        MutableSortedSet<Integer> integers = this.newWith(3, 2, 1, 4, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(3, 2, 1), integers.select(Predicates.lessThanOrEqualTo(3)));
        Verify.assertEmpty(integers.select(Predicates.greaterThan(6)));
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 3, 2, 1), revInt.select(Predicates.lessThan(4)));
    }

    @Override
    @Test
    public void selectWith()
    {
        super.selectWith();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), this.newWith(3, 1, 2, 5, 3).selectWith(Predicates2.lessThan(), 4));
    }

    @Override
    @Test
    public void selectWith_target()
    {
        super.selectWith_target();
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(1, 2, 3),
                this.newWith(3, 1, 2, 5, 3).selectWith(Predicates2.lessThan(), 4, TreeSortedSet.newSet()));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        MutableSortedSet<Integer> integers = this.newWith(4, 2, 1, 3, 5, 6);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), integers.reject(Predicates.greaterThan(4)));
        Verify.assertEmpty(integers.reject(Predicates.greaterThan(0)));
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 3, 2, 1), revInt.reject(Predicates.greaterThan(3)));
    }

    @Override
    @Test
    public void rejectWith()
    {
        super.rejectWith();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), this.newWith(3, 1, 2, 5, 4).rejectWith(Predicates2.greaterThan(), 3));
    }

    @Override
    @Test
    public void rejectWith_target()
    {
        super.rejectWith();
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith(1, 2, 3),
                this.newWith(3, 1, 2, 5, 4).rejectWith(Predicates2.greaterThan(), 3, TreeSortedSet.newSet()));
    }

    @Override
    @Test
    public void partition()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        PartitionMutableSortedSet<Integer> partition = integers.partition(IntegerPredicates.isEven());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 4, 2), partition.getSelected());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 5, 3, 1), partition.getRejected());
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void partitionWith()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        PartitionMutableSortedSet<Integer> partition = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 4, 2), partition.getSelected());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 5, 3, 1), partition.getRejected());
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getRejected().comparator());
    }

    @Test
    public void partitionWhile()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        PartitionMutableSortedSet<Integer> partition = integers.partitionWhile(Predicates.greaterThan(3));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 5, 4), partition.getSelected());
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 1), partition.getRejected());
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getSelected().comparator());
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), partition.getRejected().comparator());
    }

    @Test
    public void takeWhile()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableSortedSet<Integer> take = integers.takeWhile(Predicates.greaterThan(3));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 6, 5, 4), take);
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), take.comparator());
    }

    @Test
    public void dropWhile()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableSortedSet<Integer> drop = integers.dropWhile(Predicates.greaterThan(3));
        Verify.assertSortedSetsEqual(this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 1), drop);
        Assert.assertEquals(Comparators.<Integer>reverseNaturalOrder(), drop.comparator());
    }

    @Test
    public void distinct()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableSortedSet<Integer> distinct = integers.distinct();
        Assert.assertEquals(integers, distinct);
        Assert.assertNotSame(integers, distinct);
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        MutableSortedSet<Integer> integers = this.newWith(4, 3, 1, 6, 5, 2);
        Verify.assertListsEqual(FastList.newListWith("1", "2", "3", "4", "5", "6"), integers.collect(String::valueOf));
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 1, 2, 4, 3, 5);
        Verify.assertListsEqual(FastList.newListWith("5", "4", "3", "2", "1"), revInt.collect(String::valueOf));
    }

    @Override
    @Test
    public void collectWith()
    {
        super.collectWith();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        Function2<Integer, Integer, String> addParamFunction =
                (each, parameter) -> ((Integer) (each + parameter)).toString();
        Verify.assertListsEqual(FastList.newListWith("4", "3", "2", "1", "0"), integers.collectWith(addParamFunction, -1));
    }

    @Override
    @Test
    public void collectWith_target()
    {
        super.collectWith_target();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        Function2<Integer, Integer, String> addParamFunction =
                (each, parameter) -> ((Integer) (each + parameter)).toString();
        Verify.assertIterablesEqual(TreeSortedSet.newSetWith("4", "3", "2", "1", "0"), integers.collectWith(addParamFunction, -1, TreeSortedSet.newSet()));
    }

    @Override
    @Test
    public void flatCollect()
    {
        super.flatCollect();
        MutableSortedSet<Integer> collection = this.newWith(Collections.reverseOrder(), 2, 4, 2, 1, 3);
        Function<Integer, MutableList<String>> function =
                object -> FastList.newListWith(String.valueOf(object));
        Verify.assertListsEqual(
                FastList.newListWith("4", "3", "2", "1"),
                collection.flatCollect(function));
    }

    @Override
    @Test
    public void groupBy()
    {
        super.groupBy();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);
        MutableSortedSetMultimap<Boolean, Integer> map = integers.groupBy(isOddFunction);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 9, 7, 5, 3, 1), map.get(Boolean.TRUE));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 8, 6, 4, 2), map.get(Boolean.FALSE));
        Verify.assertSize(2, map.keysView().toList());
    }

    @Override
    @Test
    public void groupByEach()
    {
        super.groupByEach();
        MutableSortedSet<Integer> set = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6, 7);
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        MutableSortedSetMultimap<Integer, Integer> expected = this.newWith(Collections.<Integer>reverseOrder()).groupByEach(function);
        for (int i = 1; i < 8; i++)
        {
            expected.putAll(-i, Interval.fromTo(i, 7));
        }
        MutableSortedSetMultimap<Integer, Integer> actual =
                set.groupByEach(function);
        Assert.assertEquals(expected, actual);
        MutableSortedSetMultimap<Integer, Integer> actualWithTarget =
                set.groupByEach(function, this.<Integer>newWith().groupByEach(function));
        Assert.assertEquals(expected, actualWithTarget);
        for (int i = 1; i < 8; ++i)
        {
            Verify.assertSortedSetsEqual(expected.get(-i), actual.get(-i));
            Verify.assertSortedSetsEqual(expected.get(-i), actualWithTarget.get(-i).toSortedSet(Collections.reverseOrder()));
        }
        Verify.assertSize(7, actual.keysView().toList());
        Verify.assertSize(7, actualWithTarget.keysView().toList());
    }

    private static final class Holder
    {
        private final int number;

        private Holder(int i)
        {
            this.number = i;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || this.getClass() != o.getClass())
            {
                return false;
            }

            Holder holder = (Holder) o;

            return this.number == holder.number;
        }

        @Override
        public int hashCode()
        {
            return this.number;
        }

        @Override
        public String toString()
        {
            return String.valueOf(this.number);
        }
    }

    @Override
    @Test
    public void zip()
    {
        super.zip();
        MutableSortedSet<Integer> revInt = this.newWith(Collections.reverseOrder(), 2, 3, 5, 1, 4);
        MutableSortedSet<Integer> integers = this.newWith(1, 3, 2, 4, 5);
        MutableList<Pair<Integer, Integer>> zip = integers.zip(revInt);
        MutableList<Pair<Integer, Integer>> revZip = revInt.zip(integers);
        Verify.assertSize(5, zip);
        Verify.assertSize(5, revZip);
        Iterator<Pair<Integer, Integer>> zipItr = zip.iterator();
        Iterator<Pair<Integer, Integer>> revZipItr = revZip.iterator();

        for (int i = 1; i < 6; ++i)
        {
            Assert.assertEquals(Tuples.pair(i, 6 - i), zipItr.next());
            Assert.assertEquals(Tuples.pair(6 - i, i), revZipItr.next());
        }

        Person john = new Person("John", "Smith");
        Person jane = new Person("Jane", "Smith");
        Person johnDoe = new Person("John", "Doe");
        MutableSortedSet<Person> people = this.newWith(john, johnDoe);
        MutableList<Holder> list = FastList.newListWith(new Holder(1), new Holder(2), new Holder(3));
        MutableList<Pair<Person, Holder>> pairs = people.zip(list);
        Assert.assertEquals(
                FastList.newListWith(Tuples.pair(johnDoe, new Holder(1)), Tuples.pair(john, new Holder(2))),
                pairs.toList());
        Assert.assertTrue(pairs.add(Tuples.pair(new Person("Jack", "Baker"), new Holder(3))));
        Assert.assertEquals(Tuples.pair(new Person("Jack", "Baker"), new Holder(3)), pairs.getLast());
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        super.zipWithIndex();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 3, 5, 2, 4);
        Iterator<Pair<Integer, Integer>> zip = integers.zipWithIndex().iterator();
        for (int i = 5; i > 0; --i)
        {
            Assert.assertEquals(Tuples.pair(i, 5 - i), zip.next());
        }
        Person john = new Person("John", "Smith");
        Person jane = new Person("Jane", "Smith");
        Person johnDoe = new Person("John", "Doe");
        MutableSortedSet<Person> people = this.newWith(Collections.reverseOrder(), john, johnDoe, jane);
        MutableSortedSet<Pair<Person, Integer>> pairs = people.zipWithIndex();
        Verify.assertListsEqual(FastList.newListWith(Tuples.pair(john, 0), Tuples.pair(johnDoe, 1)), pairs.toList());
    }

    @Test
    public void union()
    {
        MutableSortedSet<String> set = this.newWith("d", "c", "b", "a");
        MutableSortedSet<String> union = set.union(UnifiedSet.newSetWith("a", "e", "g", "b", "f"));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("a", "b", "c", "d", "e", "f", "g"), union);
    }

    @Test
    public void unionInto()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> union = set.unionInto(UnifiedSet.newSetWith("1", "2", "5", "3"), UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "2", "3", "4", "5"), union);
        Assert.assertEquals(set, set.unionInto(UnifiedSet.newSetWith("1"), UnifiedSet.newSet()));
        MutableSortedSet<String> sortedUnion = set.unionInto(TreeSortedSet.newSetWith("5", "1", "6"), TreeSortedSet.newSet());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "2", "3", "4", "5", "6"), sortedUnion);
    }

    @Test
    public void intersect()
    {
        MutableSortedSet<String> set = this.newWith("4", "2", "1", "3");
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "2"), set.intersect(UnifiedSet.newSetWith("7", "5", "6", "1", "8", "2")));
        Verify.assertEmpty(set.intersect(UnifiedSet.newSetWith("5", "6", "8", "9")));
    }

    @Test
    public void intersectInto()
    {
        MutableSortedSet<String> set = this.newWith("2", "4", "3", "1");
        MutableSet<String> intersect = set.intersectInto(UnifiedSet.newSetWith("a", "b", "4", "c", "1"), UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "4"), intersect);
        Verify.assertEmpty(set.intersectInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));
    }

    @Test
    public void difference()
    {
        MutableSortedSet<String> set = this.newWith("5", "2", "3", "4", "1");
        MutableSortedSet<String> difference = set.difference(UnifiedSet.newSetWith("2", "3", "4", "not present"));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "5"), difference);
        Assert.assertEquals(set, set.difference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void differenceInto()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> difference = set.differenceInto(UnifiedSet.newSetWith("2", "3", "4", "not present"), UnifiedSet.newSet());
        Assert.assertEquals(UnifiedSet.newSetWith("1"), difference);
        Assert.assertEquals(set, set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));
    }

    @Test
    public void symmetricDifference()
    {
        MutableSortedSet<String> set = this.newWith("3", "4", "1", "2");
        MutableSortedSet<String> difference = set.symmetricDifference(UnifiedSet.newSetWith("6", "3", "4", "5"));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith("1", "2", "5", "6"), difference);
        Verify.assertSize(set.size() + 1, set.symmetricDifference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void symmetricDifferenceInto()
    {
        MutableSortedSet<String> set = this.newWith("2", "1", "3", "4");
        MutableSet<String> difference = set.symmetricDifferenceInto(
                UnifiedSet.newSetWith("4", "2", "3", "5"),
                UnifiedSet.newSet());
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "5"), difference);
        Verify.assertSetsEqual(UnifiedSet.newSet(set).with("not present"),
                set.symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.newSet()));
    }

    @Test
    public void isSubsetOf()
    {
        MutableSortedSet<String> set = this.newWith("3", "4", "1", "2");
        Assert.assertTrue(set.isSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
    }

    @Test
    public void isProperSubsetOf()
    {
        MutableSortedSet<String> set = this.newWith("3", "1", "4", "1", "2", "3");
        Assert.assertTrue(set.isProperSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
        Assert.assertFalse(set.isProperSubsetOf(set));
    }

    @Test
    public void powerSet()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4", "5", "test");
        MutableSortedSet<SortedSetIterable<String>> powerSet = set.powerSet();

        Verify.assertSize((int) StrictMath.pow(2, set.size()), powerSet);
        Verify.assertContains(TreeSortedSet.<String>newSet(), powerSet);
        Verify.assertContains(set, powerSet);

        MutableSortedSet<SortedSetIterable<Integer>> intPowerSet = this.newWith(1, 2, 3).powerSet();
        MutableSortedSet<SortedSetIterable<Integer>> revPowerSet = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6).powerSet();

        FastList<TreeSortedSet<Integer>> expectedSortedSet = FastList.newListWith(TreeSortedSet.newSet(), TreeSortedSet.newSetWith(1), TreeSortedSet.newSetWith(2),
                TreeSortedSet.newSetWith(3), TreeSortedSet.newSetWith(1, 2), TreeSortedSet.newSetWith(1, 3), TreeSortedSet.newSetWith(2, 3), TreeSortedSet.newSetWith(1, 2, 3));

        Verify.assertListsEqual(expectedSortedSet, intPowerSet.toList());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 1, 2, 3, 4, 5, 6), (SortedSet<Integer>) revPowerSet.getLast());
        Verify.assertInstanceOf(TreeSortedSet.class, intPowerSet);
        Verify.assertInstanceOf(TreeSortedSet.class, intPowerSet.getFirst());
    }

    @Test
    public void cartesianProduct()
    {
        MutableSortedSet<String> set = this.newWith("1", "2", "3", "4", "1", "2", "3", "4");
        LazyIterable<Pair<String, String>> cartesianProduct = set.cartesianProduct(UnifiedSet.newSetWith("One", "Two"));
        Verify.assertIterableSize(set.size() * 2, cartesianProduct);
        Assert.assertEquals(
                set,
                cartesianProduct
                        .select(Predicates.attributeEqual((Function<Pair<?, String>, String>) Pair::getTwo, "One"))
                        .collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
    }

    @Test
    public void firstLast()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        Assert.assertEquals(Integer.valueOf(1), set.first());
        Assert.assertEquals(Integer.valueOf(5), set.last());
        MutableSortedSet<Integer> set2 = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        Assert.assertEquals(Integer.valueOf(5), set2.first());
        Assert.assertEquals(Integer.valueOf(1), set2.last());
    }

    @Test
    public void testClone()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3);
        MutableSortedSet<Integer> clone = set.clone();
        Assert.assertNotSame(set, clone);
        Verify.assertSortedSetsEqual(set, clone);
    }

    @Override
    @Test
    public void toSortedSet_natural_ordering()
    {
        super.toSortedSet_natural_ordering();
        MutableSortedSet<Integer> integers = this.newWith(Collections.reverseOrder(), 1, 2, 3, 4, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4, 5), integers.toSortedSet());
    }

    @Test
    public void subSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> subSet = set.subSet(1, 4);

        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), subSet);

        subSet.clear();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(4, 5), set);

        subSet.addAll(FastList.newListWith(1, 2));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 4, 5), set);

        subSet.remove(1);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(2, 4, 5), set);

        set.clear();
        Verify.assertEmpty(subSet);

        Verify.assertThrows(IllegalArgumentException.class, () -> subSet.add(5));
        Verify.assertThrows(IllegalArgumentException.class, () -> subSet.add(0));
    }

    @Test
    public void headSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> subSet = set.headSet(4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), subSet);

        subSet.clear();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(4, 5), set);

        subSet.addAll(FastList.newListWith(1, 2));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 4, 5), set);

        subSet.remove(1);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(2, 4, 5), set);

        set.clear();
        Verify.assertEmpty(subSet);

        Verify.assertThrows(IllegalArgumentException.class, () -> subSet.add(6));
        Assert.assertTrue(subSet.add(0));
    }

    @Test
    public void tailSet()
    {
        MutableSortedSet<Integer> set = this.newWith(1, 2, 3, 4, 5);
        MutableSortedSet<Integer> subSet = set.tailSet(2);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(2, 3, 4, 5), subSet);

        subSet.clear();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1), set);

        subSet.addAll(FastList.newListWith(2, 3));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), set);

        subSet.remove(3);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2), set);

        set.clear();
        Verify.assertEmpty(subSet);

        Verify.assertThrows(IllegalArgumentException.class, () -> subSet.add(1));
        Assert.assertTrue(subSet.add(10));
    }

    @Override
    public void selectInstancesOf()
    {
        MutableSortedSet<Number> numbers = this.newWith((o1, o2) -> Double.compare(o1.doubleValue(), o2.doubleValue()), 1, 2.0, 3, 4.0, 5);
        MutableSortedSet<Integer> integers = numbers.selectInstancesOf(Integer.class);
        Assert.assertEquals(UnifiedSet.newSetWith(1, 3, 5), integers);
        Assert.assertEquals(FastList.newListWith(1, 3, 5), integers.toList());
    }

    @Test
    public void toStack()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 4, 2, 1, 3, 5, 6);
        MutableStack<Integer> stack = integers.toStack();
        Assert.assertEquals(Stacks.immutable.with(6, 5, 4, 3, 2, 1), stack);
    }

    @Override
    @Test
    public void toSortedBag_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 5, 3, 4);
        MutableSortedBag<Integer> bag = integers.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4, 5), bag);
    }

    @Override
    @Test
    public void toSortedBag_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBag(Collections.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.reverseOrder(), 4, 3, 2, 1), bag);
    }

    @Override
    @Test(expected = NullPointerException.class)
    public void toSortedBag_with_null()
    {
        this.newWith(3, 4, null, 1, 2).toSortedBag();
    }

    @Override
    @Test
    public void toSortedBagBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4), bag);
    }

    @Override
    @Test(expected = NullPointerException.class)
    public void min_null_safe()
    {
        super.min_null_safe();
    }

    @Override
    @Test(expected = NullPointerException.class)
    public void max_null_safe()
    {
        super.max_null_safe();
    }

    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableSortedSet<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        StringBuilder builder = new StringBuilder();
        integers.forEachWithIndex(5, 7, (each, index) -> builder.append(each).append(index));
        Assert.assertEquals("453627", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        integers.forEachWithIndex(5, 5, (each, index) -> builder2.append(each).append(index));
        Assert.assertEquals("45", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        integers.forEachWithIndex(0, 9, (each, index) -> builder3.append(each).append(index));
        Assert.assertEquals("90817263544536271809", builder3.toString());

        MutableList<Integer> result = Lists.mutable.of();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, new AddToList(result)));
        Verify.assertThrows(IllegalArgumentException.class, () -> integers.forEachWithIndex(7, 5, new AddToList(result)));
    }

    @Test
    public void forEachWithIndexOnRange()
    {
        MutableSortedSet<Integer> set = this.newWith(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        this.validateForEachWithIndexOnRange(set, 0, 0, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 9));
        this.validateForEachWithIndexOnRange(set, 3, 5, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 6, 5, 4));
        this.validateForEachWithIndexOnRange(set, 9, 9, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 0));
        this.validateForEachWithIndexOnRange(set, 0, 9, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0));
        Verify.assertThrows(
                IndexOutOfBoundsException.class,
                () -> this.validateForEachWithIndexOnRange(set, 10, 10, SortedSets.mutable.with(Comparators.reverseNaturalOrder())));

        Verify.assertThrows(
                IllegalArgumentException.class,
                () -> this.validateForEachWithIndexOnRange(set, 9, 0, SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)));
    }

    protected void validateForEachWithIndexOnRange(
            MutableSortedSet<Integer> set,
            int from,
            int to,
            MutableSortedSet<Integer> expectedOutput)
    {
        MutableSortedSet<Integer> outputSet = SortedSets.mutable.of(Comparators.reverseNaturalOrder());
        set.forEach(from, to, outputSet::add);
        Verify.assertSortedSetsEqual(expectedOutput, outputSet);
    }

    @Test
    public void indexOf()
    {
        MutableSortedSet<Integer> objects = this.newWith(Comparators.reverseNaturalOrder(), 3, 2, 1);
        Assert.assertEquals(0, objects.indexOf(3));
        Assert.assertEquals(1, objects.indexOf(2));
        Assert.assertEquals(2, objects.indexOf(1));
        Assert.assertEquals(-1, objects.indexOf(0));
    }

    @Test
    public void detectIndex()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        Assert.assertEquals(1, integers1.detectIndex(integer -> integer % 2 == 0));
        Assert.assertEquals(0, integers1.detectIndex(integer -> integer % 2 != 0));
        Assert.assertEquals(-1, integers1.detectIndex(integer -> integer % 5 == 0));

        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        Assert.assertEquals(0, integers2.detectIndex(integer -> integer % 2 == 0));
        Assert.assertEquals(1, integers2.detectIndex(integer -> integer % 2 != 0));
        Assert.assertEquals(-1, integers2.detectIndex(integer -> integer % 5 == 0));
    }

    @Test
    public void detectLastIndex()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        Assert.assertEquals(3, integers1.detectLastIndex(integer -> integer % 2 == 0));
        Assert.assertEquals(2, integers1.detectLastIndex(integer -> integer % 2 != 0));
        Assert.assertEquals(-1, integers1.detectLastIndex(integer -> integer % 5 == 0));

        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        Assert.assertEquals(3, integers2.detectLastIndex(integer -> integer % 2 == 0));
        Assert.assertEquals(2, integers2.detectLastIndex(integer -> integer % 2 != 0));
        Assert.assertEquals(-1, integers2.detectLastIndex(integer -> integer % 5 == 0));
    }

    @Test
    public void toReversed()
    {
        MutableSortedSet<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSortedSet<Integer> reversed = integers.toReversed();
        Assert.assertEquals(
                SortedSets.mutable.with(Comparators.reverseNaturalOrder(), 3, 2, 1),
                reversed);
    }

    @Test
    public void reverseForEach()
    {
        MutableList<Integer> list = Lists.mutable.empty();
        MutableSortedSet<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.reverseForEach(list::add);
        Assert.assertEquals(Lists.mutable.with(4, 3, 2, 1), list);
    }

    @Test
    public void reverseForEachWithIndex()
    {
        MutableList<Integer> list = Lists.mutable.empty();
        MutableSortedSet<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.reverseForEachWithIndex((each, index) -> list.add(each + index));
        Assert.assertEquals(Lists.mutable.with(7, 6, 5, 4), list);
    }

    @Test
    public void take()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        Assert.assertEquals(SortedSets.mutable.of(integers1.comparator()), integers1.take(0));
        Assert.assertSame(integers1.comparator(), integers1.take(0).comparator());
        Assert.assertEquals(SortedSets.mutable.of(integers1.comparator(), 1, 2, 3), integers1.take(3));
        Assert.assertSame(integers1.comparator(), integers1.take(3).comparator());
        Assert.assertEquals(SortedSets.mutable.of(integers1.comparator(), 1, 2, 3), integers1.take(integers1.size() - 1));

        MutableSortedSet<Integer> expectedSet = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        Assert.assertEquals(expectedSet, integers2.take(integers2.size()));
        Assert.assertEquals(expectedSet, integers2.take(10));
        Assert.assertEquals(expectedSet, integers2.take(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void take_throws()
    {
        this.newWith(1, 2, 3).take(-1);
    }

    @Test
    public void drop()
    {
        MutableSortedSet<Integer> integers1 = this.newWith(1, 2, 3, 4);
        Assert.assertEquals(integers1, integers1.drop(0));
        Assert.assertSame(integers1.comparator(), integers1.drop(0).comparator());
        Assert.assertNotSame(integers1, integers1.drop(0));
        Assert.assertEquals(SortedSets.mutable.of(integers1.comparator(), 4), integers1.drop(3));
        Assert.assertSame(integers1.comparator(), integers1.drop(3).comparator());
        Assert.assertEquals(SortedSets.mutable.of(integers1.comparator(), 4), integers1.drop(integers1.size() - 1));

        MutableSortedSet<Integer> expectedSet = SortedSets.mutable.of(Comparators.reverseNaturalOrder());
        MutableSortedSet<Integer> integers2 = this.newWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1);
        Assert.assertEquals(expectedSet, integers2.drop(integers2.size()));
        Assert.assertEquals(expectedSet, integers2.drop(10));
        Assert.assertEquals(expectedSet, integers2.drop(Integer.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void drop_throws()
    {
        this.newWith(1, 2, 3).drop(-1);
    }

    @Test
    public void getFirstOptional()
    {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 2).getFirstOptional().get());
        Assert.assertTrue(this.newWith(1, 2).getFirstOptional().isPresent());
        Assert.assertFalse(this.newWith().getFirstOptional().isPresent());
    }

    @Test
    public void getLastOptional()
    {
        Assert.assertEquals(Integer.valueOf(2), this.newWith(1, 2).getLastOptional().get());
        Assert.assertTrue(this.newWith(1, 2).getLastOptional().isPresent());
        Assert.assertFalse(this.newWith().getLastOptional().isPresent());
    }
}
