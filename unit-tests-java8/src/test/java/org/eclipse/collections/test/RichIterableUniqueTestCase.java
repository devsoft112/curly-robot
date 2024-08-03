/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bag.sorted.mutable.TreeBag;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.IntegerPredicates;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.block.factory.Procedures;
import org.eclipse.collections.impl.block.function.AddFunction;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.Test;

import static org.eclipse.collections.impl.test.Verify.assertPostSerializedEqualsAndHashCode;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.eclipse.collections.test.IterableTestCase.assertIterablesNotEqual;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

@SuppressWarnings("UnnecessaryCodeBlock")
public interface RichIterableUniqueTestCase
        extends RichIterableTestCase
{
    @Override
    default boolean allowsDuplicates()
    {
        return false;
    }

    @Override
    @Test
    default void Object_PostSerializedEqualsAndHashCode()
    {
        Iterable<Integer> iterable = this.newWith(3, 2, 1);
        Object deserialized = SerializeTestHelper.serializeDeserialize(iterable);
        assertNotSame(iterable, deserialized);
    }

    @Override
    @Test
    default void Object_equalsAndHashCode()
    {
        assertPostSerializedEqualsAndHashCode(this.newWith(3, 2, 1));

        assertIterablesNotEqual(this.newWith(4, 3, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 3, 2, 1));

        assertIterablesNotEqual(this.newWith(2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(2, 1));

        assertIterablesNotEqual(this.newWith(4, 2, 1), this.newWith(3, 2, 1));
        assertIterablesNotEqual(this.newWith(3, 2, 1), this.newWith(4, 2, 1));
    }

    @Test
    default void Iterable_sanity_check()
    {
        String s = "";
        assertThrows(IllegalStateException.class, () -> this.newWith(s, s));
    }

    @Override
    default void Iterable_toString()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        assertEquals("[3, 2, 1]", iterable.toString());
        assertEquals("[3, 2, 1]", iterable.asLazy().toString());
    }

    @Override
    @Test
    default void InternalIterable_forEach()
    {
        {
            RichIterable<Integer> iterable = this.newWith(3, 2, 1);
            MutableCollection<Integer> result = this.newMutableForFilter();
            iterable.forEach(Procedures.cast(i -> result.add(i + 10)));
            assertIterablesEqual(this.newMutableForFilter(13, 12, 11), result);
        }

        {
            RichIterable<Integer> iterable = this.newWith(2, 1);
            MutableCollection<Integer> result = this.newMutableForFilter();
            iterable.forEach(Procedures.cast(i -> result.add(i + 10)));
            assertIterablesEqual(this.newMutableForFilter(12, 11), result);
        }

        RichIterable<Integer> iterable = this.newWith(1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEach(Procedures.cast(i -> result.add(i + 10)));
        assertIterablesEqual(this.newMutableForFilter(11), result);

        this.newWith().forEach(Procedures.cast(each -> fail()));
    }

    @Override
    @Test
    default void RichIterable_tap()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.tap(result::add).forEach(Procedures.noop());
        assertIterablesEqual(this.newMutableForFilter(3, 2, 1), result);
        this.newWith().tap(Procedures.cast(each -> fail()));
    }

    @Override
    @Test
    default void InternalIterable_forEachWith()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);
        MutableCollection<Integer> result = this.newMutableForFilter();
        iterable.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 10);
        assertIterablesEqual(this.newMutableForFilter(13, 12, 11), result);
    }

    @Test
    default void RichIterable_size()
    {
        assertIterablesEqual(3, this.newWith(3, 2, 1).size());
    }

    @Override
    @Test
    default void RichIterable_toArray()
    {
        Object[] array = this.newWith(3, 2, 1).toArray();
        assertArrayEquals(new Object[]{3, 2, 1}, array);
    }

    @Override
    @Test
    default void RichIterable_select_reject()
    {
        {
            RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

            assertIterablesEqual(
                    this.getExpectedFiltered(4, 2),
                    iterable.select(IntegerPredicates.isEven()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.select(IntegerPredicates.isEven(), target);
                assertIterablesEqual(this.getExpectedFiltered(4, 2), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(4, 3),
                    iterable.selectWith(Predicates2.greaterThan(), 2));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.selectWith(Predicates2.greaterThan(), 2, target);
                assertIterablesEqual(this.getExpectedFiltered(4, 3), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(4, 2),
                    iterable.reject(IntegerPredicates.isOdd()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.reject(IntegerPredicates.isOdd(), target);
                assertIterablesEqual(this.getExpectedFiltered(4, 2), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(4, 3),
                    iterable.rejectWith(Predicates2.lessThan(), 3));

            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.rejectWith(Predicates2.lessThan(), 3, target);
            assertIterablesEqual(this.getExpectedFiltered(4, 3), result);
            assertSame(target, result);
        }

        {
            RichIterable<Integer> iterable = this.newWith(3, 2, 1);

            assertIterablesEqual(
                    this.getExpectedFiltered(2),
                    iterable.select(IntegerPredicates.isEven()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.select(IntegerPredicates.isEven(), target);
                assertIterablesEqual(this.getExpectedFiltered(2), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(3),
                    iterable.selectWith(Predicates2.greaterThan(), 2));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.selectWith(Predicates2.greaterThan(), 2, target);
                assertIterablesEqual(this.getExpectedFiltered(3), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(2),
                    iterable.reject(IntegerPredicates.isOdd()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.reject(IntegerPredicates.isOdd(), target);
                assertIterablesEqual(this.getExpectedFiltered(2), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(3),
                    iterable.rejectWith(Predicates2.lessThan(), 3));

            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.rejectWith(Predicates2.lessThan(), 3, target);
            assertIterablesEqual(this.getExpectedFiltered(3), result);
            assertSame(target, result);
        }

        {
            RichIterable<Integer> iterable = this.newWith(2, 1);

            assertIterablesEqual(
                    this.getExpectedFiltered(2),
                    iterable.select(IntegerPredicates.isEven()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.select(IntegerPredicates.isEven(), target);
                assertIterablesEqual(this.getExpectedFiltered(2), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(),
                    iterable.selectWith(Predicates2.greaterThan(), 2));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.selectWith(Predicates2.greaterThan(), 2, target);
                assertIterablesEqual(this.getExpectedFiltered(), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(2),
                    iterable.reject(IntegerPredicates.isOdd()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.reject(IntegerPredicates.isOdd(), target);
                assertIterablesEqual(this.getExpectedFiltered(2), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(),
                    iterable.rejectWith(Predicates2.lessThan(), 3));

            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.rejectWith(Predicates2.lessThan(), 3, target);
            assertIterablesEqual(this.getExpectedFiltered(), result);
            assertSame(target, result);
        }

        {
            RichIterable<Integer> iterable = this.newWith(1);

            assertIterablesEqual(
                    this.getExpectedFiltered(),
                    iterable.select(IntegerPredicates.isEven()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.select(IntegerPredicates.isEven(), target);
                assertIterablesEqual(this.getExpectedFiltered(), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(1),
                    iterable.select(IntegerPredicates.isOdd()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.select(IntegerPredicates.isOdd(), target);
                assertIterablesEqual(this.getExpectedFiltered(1), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(),
                    iterable.selectWith(Predicates2.greaterThan(), 2));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.selectWith(Predicates2.greaterThan(), 2, target);
                assertIterablesEqual(this.getExpectedFiltered(), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(1),
                    iterable.selectWith(Predicates2.greaterThan(), 0));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.selectWith(Predicates2.greaterThan(), 0, target);
                assertIterablesEqual(this.getExpectedFiltered(1), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(),
                    iterable.reject(IntegerPredicates.isOdd()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.reject(IntegerPredicates.isOdd(), target);
                assertIterablesEqual(this.getExpectedFiltered(), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(1),
                    iterable.reject(IntegerPredicates.isEven()));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.reject(IntegerPredicates.isEven(), target);
                assertIterablesEqual(this.getExpectedFiltered(1), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(),
                    iterable.rejectWith(Predicates2.lessThan(), 3));

            {
                MutableCollection<Integer> target = this.newMutableForFilter();
                MutableCollection<Integer> result = iterable.rejectWith(Predicates2.lessThan(), 3, target);
                assertIterablesEqual(this.getExpectedFiltered(), result);
                assertSame(target, result);
            }

            assertIterablesEqual(
                    this.getExpectedFiltered(1),
                    iterable.rejectWith(Predicates2.lessThan(), 0));

            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.rejectWith(Predicates2.lessThan(), 0, target);
            assertIterablesEqual(this.getExpectedFiltered(1), result);
            assertSame(target, result);
        }

        RichIterable<Integer> iterable = this.newWith();

        assertIterablesEqual(
                this.getExpectedFiltered(),
                iterable.select(IntegerPredicates.isEven()));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.select(IntegerPredicates.isEven(), target);
            assertIterablesEqual(this.getExpectedFiltered(), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(),
                iterable.selectWith(Predicates2.greaterThan(), 2));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.selectWith(Predicates2.greaterThan(), 2, target);
            assertIterablesEqual(this.getExpectedFiltered(), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(),
                iterable.reject(IntegerPredicates.isOdd()));

        {
            MutableCollection<Integer> target = this.newMutableForFilter();
            MutableCollection<Integer> result = iterable.reject(IntegerPredicates.isOdd(), target);
            assertIterablesEqual(this.getExpectedFiltered(), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFiltered(),
                iterable.rejectWith(Predicates2.lessThan(), 3));

        MutableCollection<Integer> target = this.newMutableForFilter();
        MutableCollection<Integer> result = iterable.rejectWith(Predicates2.lessThan(), 3, target);
        assertIterablesEqual(this.getExpectedFiltered(), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_partition()
    {
        RichIterable<Integer> iterable = this.newWith(-3, -2, -1, 0, 1, 2, 3);

        PartitionIterable<Integer> partition = iterable.partition(IntegerPredicates.isEven());
        assertIterablesEqual(this.getExpectedFiltered(-2, 0, 2), partition.getSelected());
        assertIterablesEqual(this.getExpectedFiltered(-3, -1, 1, 3), partition.getRejected());

        PartitionIterable<Integer> partitionWith = iterable.partitionWith(Predicates2.greaterThan(), 0);
        assertIterablesEqual(this.getExpectedFiltered(1, 2, 3), partitionWith.getSelected());
        assertIterablesEqual(this.getExpectedFiltered(-3, -2, -1, 0), partitionWith.getRejected());
    }

    @Override
    @Test
    default void RichIterable_selectInstancesOf()
    {
        RichIterable<Number> iterable = this.newWith(1, 2.0, 3, 4.0);

        assertIterablesEqual(this.getExpectedFiltered(), iterable.selectInstancesOf(String.class));
        assertIterablesEqual(this.getExpectedFiltered(1, 3), iterable.selectInstancesOf(Integer.class));
        assertIterablesEqual(this.getExpectedFiltered(1, 2.0, 3, 4.0), iterable.selectInstancesOf(Number.class));
    }

    @Override
    @Test
    default void RichIterable_collect()
    {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        assertIterablesEqual(
                this.getExpectedTransformed(3, 2, 1, 3, 2, 1),
                iterable.collect(i -> i % 10));

        {
            MutableCollection<Integer> target = this.newMutableForTransform();
            MutableCollection<Integer> result = iterable.collect(i -> i % 10, target);
            assertIterablesEqual(this.getExpectedTransformed(3, 2, 1, 3, 2, 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedTransformed(3, 2, 1, 3, 2, 1),
                iterable.collectWith((i, mod) -> i % mod, 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = iterable.collectWith((i, mod) -> i % mod, 10, target);
        assertIterablesEqual(this.getExpectedTransformed(3, 2, 1, 3, 2, 1), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_collectIf()
    {
        assertIterablesEqual(
                this.getExpectedTransformed(3, 1, 3, 1),
                this.newWith(13, 12, 11, 3, 2, 1).collectIf(i -> i % 2 != 0, i -> i % 10));

        MutableCollection<Integer> target = this.newMutableForTransform();
        MutableCollection<Integer> result = this.newWith(13, 12, 11, 3, 2, 1).collectIf(i -> i % 2 != 0, i -> i % 10, target);
        assertIterablesEqual(this.newMutableForTransform(3, 1, 3, 1), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_collectPrimitive()
    {
        assertIterablesEqual(
                this.getExpectedBoolean(false, true, false),
                this.newWith(3, 2, 1).collectBoolean(each -> each % 2 == 0));

        {
            MutableBooleanCollection target = this.newBooleanForTransform();
            MutableBooleanCollection result = this.newWith(3, 2, 1).collectBoolean(each -> each % 2 == 0, target);
            assertIterablesEqual(this.getExpectedBoolean(false, true, false), result);
            assertSame(target, result);
        }

        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        assertIterablesEqual(
                this.getExpectedByte((byte) 3, (byte) 2, (byte) 1, (byte) 3, (byte) 2, (byte) 1),
                iterable.collectByte(each -> (byte) (each % 10)));

        {
            MutableByteCollection target = this.newByteForTransform();
            MutableByteCollection result = iterable.collectByte(each -> (byte) (each % 10), target);
            assertIterablesEqual(this.getExpectedByte((byte) 3, (byte) 2, (byte) 1, (byte) 3, (byte) 2, (byte) 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedChar((char) 3, (char) 2, (char) 1, (char) 3, (char) 2, (char) 1),
                iterable.collectChar(each -> (char) (each % 10)));

        {
            MutableCharCollection target = this.newCharForTransform();
            MutableCharCollection result = iterable.collectChar(each -> (char) (each % 10), target);
            assertIterablesEqual(this.getExpectedChar((char) 3, (char) 2, (char) 1, (char) 3, (char) 2, (char) 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedDouble(3.0, 2.0, 1.0, 3.0, 2.0, 1.0),
                iterable.collectDouble(each -> (double) (each % 10)));

        {
            MutableDoubleCollection target = this.newDoubleForTransform();
            MutableDoubleCollection result = iterable.collectDouble(each -> (double) (each % 10), target);
            assertIterablesEqual(this.getExpectedDouble(3.0, 2.0, 1.0, 3.0, 2.0, 1.0), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedFloat(3.0f, 2.0f, 1.0f, 3.0f, 2.0f, 1.0f),
                iterable.collectFloat(each -> (float) (each % 10)));

        {
            MutableFloatCollection target = this.newFloatForTransform();
            MutableFloatCollection result = iterable.collectFloat(each -> (float) (each % 10), target);
            assertIterablesEqual(this.getExpectedFloat(3.0f, 2.0f, 1.0f, 3.0f, 2.0f, 1.0f), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedInt(3, 2, 1, 3, 2, 1),
                iterable.collectInt(each -> each % 10));

        {
            MutableIntCollection target = this.newIntForTransform();
            MutableIntCollection result = iterable.collectInt(each -> each % 10, target);
            assertIterablesEqual(this.getExpectedInt(3, 2, 1, 3, 2, 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedLong(3, 2, 1, 3, 2, 1),
                iterable.collectLong(each -> each % 10));

        {
            MutableLongCollection target = this.newLongForTransform();
            MutableLongCollection result = iterable.collectLong(each -> each % 10, target);
            assertIterablesEqual(this.getExpectedLong(3, 2, 1, 3, 2, 1), result);
            assertSame(target, result);
        }

        assertIterablesEqual(
                this.getExpectedShort((short) 3, (short) 2, (short) 1, (short) 3, (short) 2, (short) 1),
                iterable.collectShort(each -> (short) (each % 10)));

        MutableShortCollection target = this.newShortForTransform();
        MutableShortCollection result = iterable.collectShort(each -> (short) (each % 10), target);
        assertIterablesEqual(this.getExpectedShort((short) 3, (short) 2, (short) 1, (short) 3, (short) 2, (short) 1), result);
        assertSame(target, result);
    }

    @Override
    @Test
    default void RichIterable_flatCollect()
    {
        assertIterablesEqual(
                this.getExpectedTransformed(1, 2, 3, 1, 2, 1),
                this.newWith(3, 2, 1).flatCollect(Interval::oneTo));

        assertIterablesEqual(
                this.getExpectedTransformed(1, 2, 3, 1, 2, 1),
                this.newWith(3, 2, 1).flatCollect(Interval::oneTo, this.newMutableForTransform()));

        assertIterablesEqual(
                this.getExpectedTransformed(3, 2, 1, 2, 1, 1),
                this.newWith(3, 2, 1).flatCollectWith(Interval::fromTo, 1));

        assertIterablesEqual(
                this.newMutableForTransform(3, 2, 1, 2, 1, 1),
                this.newWith(3, 2, 1).flatCollectWith(Interval::fromTo, 1, this.newMutableForTransform()));
    }

    @Override
    @Test
    default void RichIterable_flatCollect_primitive()
    {
        {
            MutableBooleanCollection target = this.newBooleanForTransform();
            MutableBooleanCollection result = this
                    .newWith(3, 2, 1)
                    .flatCollectBoolean(each -> BooleanLists.immutable.with(each % 2 == 0, each % 2 == 0), target);
            assertIterablesEqual(this.getExpectedBoolean(false, false, true, true, false, false), result);
            assertSame(target, result);
        }

        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        {
            MutableByteCollection target = this.newByteForTransform();
            MutableByteCollection result = iterable.flatCollectByte(
                    each -> ByteLists.immutable.with((byte) (each % 10), (byte) (each % 10)),
                    target);
            assertIterablesEqual(
                    this.getExpectedByte((byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1, (byte) 3, (byte) 3, (byte) 2, (byte) 2, (byte) 1, (byte) 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableCharCollection target = this.newCharForTransform();
            MutableCharCollection result = iterable.flatCollectChar(
                    each -> CharLists.immutable.with((char) (each % 10), (char) (each % 10)),
                    target);
            assertIterablesEqual(
                    this.getExpectedChar((char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1, (char) 3, (char) 3, (char) 2, (char) 2, (char) 1, (char) 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableDoubleCollection target = this.newDoubleForTransform();
            MutableDoubleCollection result = iterable.flatCollectDouble(each -> DoubleLists.immutable.with(
                    (double) (each % 10),
                    (double) (each % 10)), target);
            assertIterablesEqual(
                    this.getExpectedDouble(3.0, 3.0, 2.0, 2.0, 1.0, 1.0, 3.0, 3.0, 2.0, 2.0, 1.0, 1.0),
                    result);
            assertSame(target, result);
        }

        {
            MutableFloatCollection target = this.newFloatForTransform();
            MutableFloatCollection result = iterable.flatCollectFloat(each -> FloatLists.immutable.with(
                    (float) (each % 10),
                    (float) (each % 10)), target);
            assertIterablesEqual(
                    this.getExpectedFloat(3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f, 3.0f, 3.0f, 2.0f, 2.0f, 1.0f, 1.0f),
                    result);
            assertSame(target, result);
        }

        {
            MutableIntCollection target = this.newIntForTransform();
            MutableIntCollection result =
                    iterable.flatCollectInt(each -> IntLists.immutable.with(each % 10, each % 10), target);
            assertIterablesEqual(
                    this.getExpectedInt(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableLongCollection target = this.newLongForTransform();
            MutableLongCollection result =
                    iterable.flatCollectLong(each -> LongLists.immutable.with(each % 10, each % 10), target);
            assertIterablesEqual(
                    this.getExpectedLong(3, 3, 2, 2, 1, 1, 3, 3, 2, 2, 1, 1),
                    result);
            assertSame(target, result);
        }

        {
            MutableShortCollection target = this.newShortForTransform();
            MutableShortCollection result = iterable.flatCollectShort(each -> ShortLists.immutable.with(
                    (short) (each % 10),
                    (short) (each % 10)), target);
            assertIterablesEqual(
                    this.getExpectedShort((short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1, (short) 3, (short) 3, (short) 2, (short) 2, (short) 1, (short) 1),
                    result);
            assertSame(target, result);
        }
    }

    @Override
    @Test
    default void RichIterable_count()
    {
        RichIterable<Integer> iterable = this.newWith(3, 2, 1);

        assertIterablesEqual(1, iterable.count(Integer.valueOf(3)::equals));
        assertIterablesEqual(1, iterable.count(Integer.valueOf(2)::equals));
        assertIterablesEqual(1, iterable.count(Integer.valueOf(1)::equals));
        assertIterablesEqual(0, iterable.count(Integer.valueOf(0)::equals));
        assertIterablesEqual(2, iterable.count(i -> i % 2 != 0));
        assertIterablesEqual(3, iterable.count(i -> i > 0));

        assertIterablesEqual(1, iterable.countWith(Object::equals, 3));
        assertIterablesEqual(1, iterable.countWith(Object::equals, 2));
        assertIterablesEqual(1, iterable.countWith(Object::equals, 1));
        assertIterablesEqual(0, iterable.countWith(Object::equals, 0));
        assertIterablesEqual(3, iterable.countWith(Predicates2.greaterThan(), 0));
    }

    @Override
    @Test
    default void RichIterable_groupBy()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        Function<Integer, Boolean> groupByFunction = object -> IntegerPredicates.isOdd().accept(object);

        MutableMap<Boolean, RichIterable<Integer>> groupByExpected =
                UnifiedMap.newWithKeysValues(
                        Boolean.TRUE, this.newMutableForFilter(3, 1),
                        Boolean.FALSE, this.newMutableForFilter(4, 2));

        assertIterablesEqual(groupByExpected, iterable.groupBy(groupByFunction).toMap());

        Function<Integer, Boolean> function = (Integer object) -> true;
        MutableMultimap<Boolean, Integer> target = this.<Integer>newWith().groupBy(function).toMutable();
        MutableMultimap<Boolean, Integer> multimap2 = iterable.groupBy(groupByFunction, target);
        assertIterablesEqual(groupByExpected, multimap2.toMap());
        assertSame(target, multimap2);

        Function<Integer, Iterable<Integer>> groupByEachFunction = integer -> Interval.fromTo(-1, -integer);

        MutableMap<Integer, RichIterable<Integer>> expectedGroupByEach =
                UnifiedMap.newWithKeysValues(
                        -4, this.newMutableForFilter(4),
                        -3, this.newMutableForFilter(4, 3),
                        -2, this.newMutableForFilter(4, 3, 2),
                        -1, this.newMutableForFilter(4, 3, 2, 1));

        assertIterablesEqual(expectedGroupByEach, iterable.groupByEach(groupByEachFunction).toMap());

        MutableMultimap<Integer, Integer> target2 = this.<Integer>newWith().groupByEach(groupByEachFunction).toMutable();
        Multimap<Integer, Integer> actualWithTarget = iterable.groupByEach(groupByEachFunction, target2);
        assertIterablesEqual(expectedGroupByEach, actualWithTarget.toMap());
        assertSame(target2, actualWithTarget);
    }

    @Override
    @Test
    default void RichIterable_aggregateBy_aggregateInPlaceBy_reduceBy()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        MapIterable<String, Integer> aggregateBy = iterable.aggregateBy(
                Object::toString,
                () -> 0,
                (integer1, integer2) -> integer1 + integer2);

        assertIterablesEqual(4, aggregateBy.get("4").intValue());
        assertIterablesEqual(3, aggregateBy.get("3").intValue());
        assertIterablesEqual(2, aggregateBy.get("2").intValue());
        assertIterablesEqual(1, aggregateBy.get("1").intValue());

        MapIterable<String, AtomicInteger> aggregateInPlaceBy = iterable.aggregateInPlaceBy(
                String::valueOf,
                AtomicInteger::new,
                AtomicInteger::addAndGet);
        assertIterablesEqual(4, aggregateInPlaceBy.get("4").intValue());
        assertIterablesEqual(3, aggregateInPlaceBy.get("3").intValue());
        assertIterablesEqual(2, aggregateInPlaceBy.get("2").intValue());
        assertIterablesEqual(1, aggregateInPlaceBy.get("1").intValue());

        MapIterable<String, Integer> reduceBy = iterable.reduceBy(
                Object::toString,
                (integer1, integer2) -> integer1 + integer2);

        assertIterablesEqual(4, reduceBy.get("4").intValue());
        assertIterablesEqual(3, reduceBy.get("3").intValue());
        assertIterablesEqual(2, reduceBy.get("2").intValue());
        assertIterablesEqual(1, reduceBy.get("1").intValue());
    }

    @Override
    @Test
    default void RichIterable_sumOfPrimitive()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals(10.0f, iterable.sumOfFloat(Integer::floatValue), 0.001);
        assertEquals(10.0, iterable.sumOfDouble(Integer::doubleValue), 0.001);
        assertEquals(10, iterable.sumOfInt(integer -> integer));
        assertEquals(10L, iterable.sumOfLong(Integer::longValue));
    }

    @Override
    default void RichIterable_sumByPrimitive()
    {
        RichIterable<String> iterable = this.newWith("4", "3", "2", "1");

        assertIterablesEqual(
                ObjectLongMaps.immutable.with(0, 6L).newWithKeyValue(1, 4L),
                iterable.sumByInt(s -> Integer.parseInt(s) % 2, Integer::parseInt));

        assertIterablesEqual(
                ObjectLongMaps.immutable.with(0, 6L).newWithKeyValue(1, 4L),
                iterable.sumByLong(s -> Integer.parseInt(s) % 2, Long::parseLong));

        assertIterablesEqual(
                ObjectDoubleMaps.immutable.with(0, 6.0d).newWithKeyValue(1, 4.0d),
                iterable.sumByDouble(s -> Integer.parseInt(s) % 2, Double::parseDouble));

        assertIterablesEqual(
                ObjectDoubleMaps.immutable.with(0, 6.0d).newWithKeyValue(1, 4.0d),
                iterable.sumByFloat(s -> Integer.parseInt(s) % 2, Float::parseFloat));
    }

    @Override
    @Test
    default void RichIterable_summarizePrimitive()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals(10.0f, iterable.summarizeFloat(Integer::floatValue).getSum(), 0.001);
        assertEquals(10.0, iterable.summarizeDouble(Integer::doubleValue).getSum(), 0.001);
        assertEquals(10, iterable.summarizeInt(Integer::intValue).getSum());
        assertEquals(10L, iterable.summarizeLong(Integer::longValue).getSum());
    }

    @Override
    @Test
    default void RichIterable_reduceInPlaceCollector()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);
        MutableBag<Integer> result = iterable.reduceInPlace(Collectors.toCollection(Bags.mutable::empty));
        assertEquals(Bags.immutable.with(1, 2, 3), result);

        String joining = result.collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(result.collect(Object::toString).makeString(","), joining);

        String joining2 = result.toImmutable().collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(result.toImmutable().collect(Object::toString).makeString(","), joining2);

        String joining3 = result.asLazy().collect(Object::toString).reduceInPlace(Collectors.joining(","));
        assertEquals(result.asLazy().collect(Object::toString).makeString(","), joining3);

        Map<Boolean, List<Integer>> expected =
                iterable.toList().stream().collect(Collectors.partitioningBy(each -> each % 2 == 0));
        Map<Boolean, List<Integer>> actual =
                iterable.reduceInPlace(Collectors.partitioningBy(each -> each % 2 == 0));
        assertEquals(expected, actual);

        Map<String, List<Integer>> groupByJDK =
                iterable.toList().stream().collect(Collectors.groupingBy(Object::toString));
        Map<String, List<Integer>> groupByEC =
                result.reduceInPlace(Collectors.groupingBy(Object::toString));
        assertEquals(groupByJDK, groupByEC);
    }

    @Override
    @Test
    default void RichIterable_reduceInPlace()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);
        MutableBag<Integer> result =
                iterable.reduceInPlace(Bags.mutable::empty, MutableBag::add);
        assertEquals(Bags.immutable.with(1, 2, 3), result);

        String joining =
                result.collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(result.collect(Object::toString).makeString(""), joining);

        ImmutableBag<Integer> immutableBag = result.toImmutable();
        String joining2 =
                immutableBag.collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(immutableBag.collect(Object::toString).makeString(""), joining2);

        String joining3 =
                result.asLazy().collect(Object::toString).reduceInPlace(StringBuilder::new, StringBuilder::append).toString();
        assertEquals(result.asLazy().collect(Object::toString).makeString(""), joining3);

        int atomicAdd = iterable.reduceInPlace(AtomicInteger::new, AtomicInteger::addAndGet).get();
        assertEquals(6, atomicAdd);
    }

    @Override
    @Test
    default void RichIterable_reduceOptional()
    {
        RichIterable<Integer> iterable = this.newWith(1, 2, 3);
        Optional<Integer> result =
                iterable.reduce(Integer::sum);
        assertEquals(6, result.get().intValue());

        Optional<Integer> max =
                iterable.reduce(Integer::max);
        assertEquals(3, max.get().intValue());

        Optional<Integer> min =
                iterable.reduce(Integer::min);
        assertEquals(1, min.get().intValue());

        RichIterable<Integer> iterableEmpty = this.newWith();
        Optional<Integer> resultEmpty =
                iterableEmpty.reduce(Integer::sum);
        assertFalse(resultEmpty.isPresent());
    }

    @Override
    @Test
    default void RichIterable_injectInto()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(Integer.valueOf(11), iterable.injectInto(1, new Function2<Integer, Integer, Integer>()
        {
            private static final long serialVersionUID = 1L;

            public Integer value(Integer argument1, Integer argument2)
            {
                return argument1 + argument2;
            }
        }));
        assertIterablesEqual(Integer.valueOf(10), iterable.injectInto(0, new Function2<Integer, Integer, Integer>()
        {
            private static final long serialVersionUID = 1L;

            public Integer value(Integer argument1, Integer argument2)
            {
                return argument1 + argument2;
            }
        }));
    }

    @Override
    @Test
    default void RichIterable_injectInto_primitive()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertEquals(11, iterable.injectInto(1, AddFunction.INTEGER_TO_INT));
        assertEquals(10, iterable.injectInto(0, AddFunction.INTEGER_TO_INT));

        assertEquals(11L, iterable.injectInto(1, AddFunction.INTEGER_TO_LONG));
        assertEquals(10L, iterable.injectInto(0, AddFunction.INTEGER_TO_LONG));

        assertEquals(11.0d, iterable.injectInto(1, AddFunction.INTEGER_TO_DOUBLE), 0.001);
        assertEquals(10.0d, iterable.injectInto(0, AddFunction.INTEGER_TO_DOUBLE), 0.001);

        assertEquals(11.0f, iterable.injectInto(1, AddFunction.INTEGER_TO_FLOAT), 0.001f);
        assertEquals(10.0f, iterable.injectInto(0, AddFunction.INTEGER_TO_FLOAT), 0.001f);
    }

    @Override
    @Test
    default void RichIterable_makeString_appendString()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual("4, 3, 2, 1", iterable.makeString());
        assertIterablesEqual("4/3/2/1", iterable.makeString("/"));
        assertIterablesEqual("[4/3/2/1]", iterable.makeString("[", "/", "]"));

        StringBuilder stringBuilder1 = new StringBuilder();
        iterable.appendString(stringBuilder1);
        assertIterablesEqual("4, 3, 2, 1", stringBuilder1.toString());

        StringBuilder stringBuilder2 = new StringBuilder();
        iterable.appendString(stringBuilder2, "/");
        assertIterablesEqual("4/3/2/1", stringBuilder2.toString());

        StringBuilder stringBuilder3 = new StringBuilder();
        iterable.appendString(stringBuilder3, "[", "/", "]");
        assertIterablesEqual("[4/3/2/1]", stringBuilder3.toString());
    }

    @Override
    @Test
    default void RichIterable_toList()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);
        assertIterablesEqual(
                Lists.immutable.with(4, 3, 2, 1),
                iterable.toList());

        MutableList<Integer> target = Lists.mutable.empty();
        iterable.each(target::add);
        assertIterablesEqual(
                target,
                iterable.toList());
    }

    @Override
    @Test
    default void RichIterable_into()
    {
        assertIterablesEqual(
                Lists.immutable.with(4, 3, 2, 1),
                this.newWith(4, 3, 2, 1).into(Lists.mutable.empty()));
    }

    @Override
    @Test
    default void RichIterable_toSortedList()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(
                Lists.immutable.with(1, 2, 3, 4),
                iterable.toSortedList());

        assertIterablesEqual(
                Lists.immutable.with(4, 3, 2, 1),
                iterable.toSortedList(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                Lists.immutable.with(1, 2, 3, 4),
                iterable.toSortedListBy(Functions.identity()));

        assertIterablesEqual(
                Lists.immutable.with(4, 3, 2, 1),
                iterable.toSortedListBy(each -> each * -1));
    }

    @Override
    @Test
    default void RichIterable_toSet()
    {
        assertIterablesEqual(
                Sets.immutable.with(4, 3, 2, 1),
                this.newWith(4, 3, 2, 1).toSet());
    }

    @Override
    @Test
    default void RichIterable_toSortedSet()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(
                SortedSets.immutable.with(1, 2, 3, 4),
                iterable.toSortedSet());

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.reverseNaturalOrder(), 4, 3, 2, 1),
                iterable.toSortedSet(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4),
                iterable.toSortedSetBy(Functions.identity()));

        assertIterablesEqual(
                SortedSets.immutable.with(Comparators.byFunction((Integer each) -> each * -1), 4, 3, 2, 1),
                iterable.toSortedSetBy(each -> each * -1));
    }

    @Override
    @Test
    default void RichIterable_toBag()
    {
        assertIterablesEqual(
                Bags.immutable.with(4, 3, 2, 1),
                this.newWith(4, 3, 2, 1).toBag());
    }

    @Override
    @Test
    default void RichIterable_toSortedBag()
    {
        RichIterable<Integer> iterable = this.newWith(4, 3, 2, 1);

        assertIterablesEqual(
                TreeBag.newBagWith(1, 2, 3, 4),
                iterable.toSortedBag());

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 4, 3, 2, 1),
                iterable.toSortedBag(Comparators.reverseNaturalOrder()));

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.byFunction(Functions.identity()), 1, 2, 3, 4),
                iterable.toSortedBagBy(Functions.identity()));

        assertIterablesEqual(
                TreeBag.newBagWith(Comparators.byFunction((Integer each) -> each * -1), 4, 3, 2, 1),
                iterable.toSortedBagBy(each -> each * -1));
    }

    @Override
    @Test
    default void RichIterable_toMap()
    {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        assertIterablesEqual(
                UnifiedMap.newMapWith(
                        Tuples.pair("13", 3),
                        Tuples.pair("12", 2),
                        Tuples.pair("11", 1),
                        Tuples.pair("3", 3),
                        Tuples.pair("2", 2),
                        Tuples.pair("1", 1)),
                iterable.toMap(Object::toString, each -> each % 10));
    }

    @Override
    @Test
    default void RichIterable_toSortedMap()
    {
        RichIterable<Integer> iterable = this.newWith(13, 12, 11, 3, 2, 1);

        Pair<String, Integer>[] pairs = new Pair[]
                {
                        Tuples.pair("13", 3),
                        Tuples.pair("12", 2),
                        Tuples.pair("11", 1),
                        Tuples.pair("3", 3),
                        Tuples.pair("2", 2),
                        Tuples.pair("1", 1),
                };
        assertIterablesEqual(
                TreeSortedMap.newMapWith(pairs),
                iterable.toSortedMap(Object::toString, each -> each % 10));

        assertIterablesEqual(
                TreeSortedMap.newMapWith(
                        Comparators.reverseNaturalOrder(),
                        pairs),
                iterable.toSortedMap(Comparators.reverseNaturalOrder(), Object::toString, each -> each % 10));

        assertIterablesEqual(
                TreeSortedMap.newMapWith(
                        Comparators.naturalOrder(),
                        pairs),
                iterable.toSortedMapBy(Functions.getStringPassThru(), Object::toString, each -> each % 10));
    }
}
