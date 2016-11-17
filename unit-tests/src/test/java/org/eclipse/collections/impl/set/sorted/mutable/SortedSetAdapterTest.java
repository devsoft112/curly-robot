/*
 * Copyright (c) 2016 Goldman Sachs.
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
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link SortedSetAdapter}.
 */
public class SortedSetAdapterTest extends AbstractSortedSetTestCase
{
    @Override
    protected <T> SortedSetAdapter<T> newWith(T... elements)
    {
        return new SortedSetAdapter<>(new TreeSet<>(FastList.newListWith(elements)));
    }

    @Override
    protected <T> SortedSetAdapter<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        TreeSet<T> set = new TreeSet<>(comparator);
        set.addAll(FastList.newListWith(elements));
        return new SortedSetAdapter<>(set);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedSet.class, SortedSetAdapter.adapt(new TreeSet<>()).asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedSet.class, this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void testClone()
    {
        super.testClone();
        MutableSortedSet<Integer> set = this.newWith(Collections.<Integer>reverseOrder()).with(1, 2, 3);
        MutableSortedSet<Integer> list2 = set.clone();
        Verify.assertSortedSetsEqual(set, list2);
    }

    @Test
    public void adapt()
    {
        SortedSet<Integer> integers = new TreeSet<>(FastList.newListWith(1, 2, 3, 4));
        MutableSortedSet<Integer> adapter1 = SortedSetAdapter.adapt(integers);
        MutableSortedSet<Integer> adapter2 = new SortedSetAdapter<Integer>(new TreeSet<>()).with(1, 2, 3, 4);
        Verify.assertEqualsAndHashCode(adapter1, adapter2);
        Verify.assertSortedSetsEqual(adapter1, adapter2);
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        SortedSetAdapter<Integer> integers = this.newWith(1, 2, 3, 4, 5);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2), integers.select(Predicates.lessThan(3)));
        Verify.assertInstanceOf(MutableSortedSet.class, this.<Integer>newWith().select(ignored1 -> true));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(), this.newWith().select(ignored -> true));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        SortedSetAdapter<Integer> integers = this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2), integers.reject(Predicates.greaterThan(2)));
        Verify.assertInstanceOf(MutableSortedSet.class, this.<Integer>newWith().select(ignored1 -> true));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(), this.newWith().reject(ignored -> true));
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        Verify.assertListsEqual(FastList.newListWith("1", "2", "3", "4"),
                this.newWith(1, 2, 3, 4).collect(String::valueOf));
        Verify.assertListsEqual(FastList.newListWith("1", "2", "3", "4"), this.newWith(1, 2, 3, 4).collect(
                String::valueOf,
                FastList.newList()));
        Verify.assertInstanceOf(FastList.class, this.newWith().collect(String::valueOf));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        MutableCollection<Integer> set1 = this.newWith(1, 2, 3);
        SortedSetAdapter<Integer> set2 = this.newWith(Collections.reverseOrder(), 1, 2, 3);
        MutableCollection<Integer> set3 = this.newWith(2, 3, 4);
        MutableSortedSet<Integer> set4 = TreeSortedSet.newSetWith(2, 3, 4);

        Verify.assertEqualsAndHashCode(set1, set1);
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3), set1);
        Assert.assertEquals(UnifiedSet.newSetWith(1, 2, 3), set2);

        Assert.assertEquals(set1, set2);
        Assert.assertNotEquals(set2, set3);
        Verify.assertEqualsAndHashCode(set3, set4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.reverseOrder(), 1, 2, 3), set2);
    }

    @Test
    public void serialization()
    {
        MutableSortedSet<Integer> collection = this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3);
        MutableSortedSet<Integer> deserialized = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertPostSerializedEqualsAndHashCode(collection);
        deserialized.add(4);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4), deserialized);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();
        MutableList<Integer> result = Lists.mutable.of();
        MutableCollection<Integer> collection = this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4);
        collection.forEachWithIndex((object, index) -> result.add(object));
        Verify.assertListsEqual(FastList.newListWith(4, 3, 2, 1), result);
    }

    @Override
    @Test
    public void getFirst()
    {
        super.getFirst();
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getFirst());
        Assert.assertEquals(Integer.valueOf(3), this.newWith(Collections.reverseOrder(), 1, 2, 3).getFirst());
        Verify.assertThrows(NoSuchElementException.class, () -> new SortedSetAdapter<>(new TreeSet<>()).getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        super.getLast();
        Assert.assertNotNull(this.newWith(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(1), this.newWith(Collections.reverseOrder(), 1, 2, 3).getLast());
        Verify.assertThrows(NoSuchElementException.class, () -> new SortedSetAdapter<>(new TreeSet<>()).getLast());
    }

    @Override
    @Test
    public void iterator()
    {
        super.iterator();
        MutableCollection<Integer> objects = this.newWith(2, 3, 1, 4, 5);
        MutableList<Integer> result = Lists.mutable.of();
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i > 0; i--)
        {
            Integer integer = iterator.next();
            result.add(integer);
        }
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 5), result);
    }

    @Test
    public void withMethods()
    {
        Verify.assertContainsAll(this.newWith().with(1), 1);
        Verify.assertContainsAll(this.newWith().with(1, 2), 1, 2);
        Verify.assertContainsAll(this.newWith().with(1, 2, 3), 1, 2, 3);
        Verify.assertContainsAll(this.newWith().with(1, 2, 3, 4), 1, 2, 3, 4);
    }

    @Test
    public void returnType()
    {
        //Type TreeSet is important here because it's not a MutableSet
        SortedSet<Integer> set = new TreeSet<>();
        MutableSortedSet<Integer> integerSetAdapter = SortedSetAdapter.adapt(set);
        Verify.assertInstanceOf(MutableSortedSet.class, integerSetAdapter.select(ignored -> true));
    }

    @Test
    public void adaptNull()
    {
        Verify.assertThrows(NullPointerException.class, () -> new SortedSetAdapter<>(null));
        Verify.assertThrows(NullPointerException.class, () -> SortedSetAdapter.adapt(null));
    }
}
