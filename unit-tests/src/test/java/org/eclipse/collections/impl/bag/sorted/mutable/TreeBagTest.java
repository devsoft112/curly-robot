/*
 * Copyright (c) 2018 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.eclipse.collections.api.LazyIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link TreeBag}.
 *
 * @since 4.2
 */
public class TreeBagTest extends AbstractMutableSortedBagTestCase
{
    @Override
    protected <T> MutableSortedBag<T> newWith(T... littleElements)
    {
        return TreeBag.newBagWith(littleElements);
    }

    @Override
    protected <T> MutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return TreeBag.newBagWith(comparator, elements);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedBag.class, this.newWith().asSynchronized());
    }

    @Test
    public void sortedBagIterableConstructor()
    {
        TreeBag<Integer> sortedBagA = TreeBag.newBag(Collections.reverseOrder());
        TreeBag<Integer> sortedBagB = TreeBag.newBag(sortedBagA.with(1).with(2, 3).with(4, 5, 6).with(1, 1, 1, 1));
        Verify.assertSortedBagsEqual(sortedBagA, sortedBagB);
        Assert.assertTrue(sortedBagA.getFirst().equals(sortedBagB.getFirst()) && sortedBagB.getFirst() == 6);
        Verify.assertSortedBagsEqual(sortedBagB, TreeBag.newBag(sortedBagB));
    }

    @Test
    public void sortedBagConstructor()
    {
        MutableSortedBag<String> bagA = TreeBag.newBag(FastList.newListWith("a", "c", "b", "d"));
        Verify.assertSortedBagsEqual(bagA, TreeBag.newBag(bagA));
        Verify.assertSortedBagsEqual(bagA, TreeBag.newBag(bagA));
    }

    @Test
    public void iterableConstructor()
    {
        LazyIterable<Integer> integerLazyIterable = FastList.newListWith(2, 4, 1, 3).asLazy();
        TreeBag<Integer> sortedBag = TreeBag.newBag(Comparators.reverseNaturalOrder(), integerLazyIterable);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 2, 3, 4), sortedBag);
    }

    @Override
    @Test
    public void serialization()
    {
        MutableSortedBag<Integer> bag = this.newWith(1, 2, 3, 4, 5);
        Verify.assertPostSerializedEqualsAndHashCode(bag);
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

    @Override
    @Test
    public void collectWithOccurrences()
    {
        Bag<Integer> bag1 = this.newWith(3, 3, 3, 2, 2, 1);
        Bag<ObjectIntPair<Integer>> actual1 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Bags.mutable.empty());
        Assert.assertEquals(
                Bags.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                actual1);
        Assert.assertEquals(
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(1), 1),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(3), 3)),
                bag1.collectWithOccurrences(PrimitiveTuples::pair));

        Set<ObjectIntPair<Integer>> actual2 =
                bag1.collectWithOccurrences(PrimitiveTuples::pair, Sets.mutable.empty());
        Assert.assertEquals(
                Sets.immutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                actual2);

        Bag<Integer> bag2 = this.newWith(Comparator.reverseOrder(), 3, 3, 3, 2, 2, 1);
        Assert.assertEquals(
                Lists.mutable.with(
                        PrimitiveTuples.pair(Integer.valueOf(3), 3),
                        PrimitiveTuples.pair(Integer.valueOf(2), 2),
                        PrimitiveTuples.pair(Integer.valueOf(1), 1)),
                bag2.collectWithOccurrences(PrimitiveTuples::pair));

        Bag<Integer> bag3 = this.newWith(3, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1, 1, 4, 5, 7);
        Assert.assertEquals(
                Lists.mutable.with(6, 5, 8, 5, 6, 8),
                bag3.collectWithOccurrences((each, index) -> each + index));
    }
}
