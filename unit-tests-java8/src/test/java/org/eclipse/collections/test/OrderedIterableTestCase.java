/*
 * Copyright (c) 2017 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.Iterator;
import java.util.Optional;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.collections.test.IterableTestCase.assertEquals;
import static org.junit.Assert.assertSame;

public interface OrderedIterableTestCase extends RichIterableTestCase
{
    @Test
    default void OrderedIterable_getFirst()
    {
        assertEquals(Integer.valueOf(3), this.newWith(3, 3, 3, 2, 2, 1).getFirst());
    }

    @Test
    default void OrderedIterable_getFirstOptional_empty()
    {
        assertSame(Optional.empty(), ((OrderedIterable<?>) this.newWith()).getFirstOptional());
    }

    @Test
    default void OrderedIterable_getFirstOptional()
    {
        assertEquals(Optional.of(Integer.valueOf(3)), ((OrderedIterable<?>) this.newWith(3, 3, 3, 2, 2, 1)).getFirstOptional());
    }

    @Test(expected = NullPointerException.class)
    default void OrderedIterable_getFirstOptional_null_element()
    {
        ((OrderedIterable<?>) this.newWith(new Object[]{null})).getFirstOptional();
    }

    @Test
    default void OrderedIterable_getLast()
    {
        assertEquals(Integer.valueOf(1), this.newWith(3, 3, 3, 2, 2, 1).getLast());
    }

    @Test
    default void OrderedIterable_getLastOptional_empty()
    {
        assertSame(Optional.empty(), ((OrderedIterable<?>) this.newWith()).getLastOptional());
    }

    @Test
    default void OrderedIterable_getLastOptional()
    {
        assertEquals(Optional.of(Integer.valueOf(1)), ((OrderedIterable<?>) this.newWith(3, 3, 3, 2, 2, 1)).getLastOptional());
    }

    @Test(expected = NullPointerException.class)
    default void OrderedIterable_getLastOptional_null_element()
    {
        ((OrderedIterable<?>) this.newWith(new Object[]{null})).getLastOptional();
    }

    @Test
    default void OrderedIterable_next()
    {
        Iterator<Integer> iterator = this.newWith(3, 2, 1).iterator();
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(1), iterator.next());
    }

    @Test
    default void OrderedIterable_min()
    {
        Holder<Integer> first = new Holder<>(-1);
        Holder<Integer> second = new Holder<>(-1);
        assertSame(first, this.newWith(new Holder<>(2), first, new Holder<>(0), second).min());
    }

    @Test
    default void OrderedIterable_max()
    {
        Holder<Integer> first = new Holder<>(1);
        Holder<Integer> second = new Holder<>(1);
        assertSame(first, this.newWith(new Holder<>(-2), first, new Holder<>(0), second).max());
    }

    @Test
    default void OrderedIterable_min_comparator()
    {
        Holder<Integer> first = new Holder<>(1);
        Holder<Integer> second = new Holder<>(1);
        assertSame(first, this.newWith(new Holder<>(-2), first, new Holder<>(0), second).min(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void OrderedIterable_max_comparator()
    {
        Holder<Integer> first = new Holder<>(-1);
        Holder<Integer> second = new Holder<>(-1);
        assertSame(first, this.newWith(new Holder<>(2), first, new Holder<>(0), second).max(Comparators.reverseNaturalOrder()));
    }

    @Test
    default void OrderedIterable_zipWithIndex()
    {
        RichIterable<Integer> iterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Assert.assertEquals(
                Lists.immutable.with(
                        Tuples.pair(4, 0),
                        Tuples.pair(4, 1),
                        Tuples.pair(4, 2),
                        Tuples.pair(4, 3),
                        Tuples.pair(3, 4),
                        Tuples.pair(3, 5),
                        Tuples.pair(3, 6),
                        Tuples.pair(2, 7),
                        Tuples.pair(2, 8),
                        Tuples.pair(1, 9)),
                iterable.zipWithIndex().toList());
    }

    @Test
    default void OrderedIterable_zipWithIndex_target()
    {
        RichIterable<Integer> iterable = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        MutableList<Pair<Integer, Integer>> target = Lists.mutable.empty();
        MutableList<Pair<Integer, Integer>> result = iterable.zipWithIndex(target);
        Assert.assertEquals(
                Lists.immutable.with(
                        Tuples.pair(4, 0),
                        Tuples.pair(4, 1),
                        Tuples.pair(4, 2),
                        Tuples.pair(4, 3),
                        Tuples.pair(3, 4),
                        Tuples.pair(3, 5),
                        Tuples.pair(3, 6),
                        Tuples.pair(2, 7),
                        Tuples.pair(2, 8),
                        Tuples.pair(1, 9)),
                result);
        assertSame(target, result);
    }
}
