/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.function.PassThruFunction0;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link ImmutableQuadrupletonMap}.
 */
public class ImmutableQuadrupletonMapTest extends ImmutableMemoryEfficientMapTestCase
{
    @Override
    protected ImmutableMap<Integer, String> classUnderTest()
    {
        return new ImmutableQuadrupletonMap<>(1, "1", 2, "2", 3, "3", 4, "4");
    }

    @Override
    protected int size()
    {
        return 4;
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        ImmutableMap<Integer, String> map1 = new ImmutableQuadrupletonMap<>(1, "One", 2, "Two", 3, "Three", 4, "Four");
        ImmutableMap<Integer, String> map2 = new ImmutableQuadrupletonMap<>(1, "One", 2, "Two", 3, "Three", 4, "Four");
        Verify.assertEqualsAndHashCode(map1, map2);
    }

    @Override
    @Test
    public void forEachValue()
    {
        super.forEachValue();
        MutableList<String> collection = Lists.mutable.of();
        this.classUnderTest().forEachValue(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4"), collection);
    }

    @Override
    @Test
    public void forEachKey()
    {
        super.forEachKey();
        MutableList<Integer> collection = Lists.mutable.of();
        this.classUnderTest().forEachKey(CollectionAddProcedure.on(collection));
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), collection);
    }

    @Override
    @Test
    public void getIfAbsent_function()
    {
        super.getIfAbsent_function();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(5));
        Assert.assertEquals("5", map.getIfAbsent(5, new PassThruFunction0<>("5")));
        Assert.assertNull(map.get(5));
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        super.getIfAbsent();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.get(5));
        Assert.assertEquals("5", map.getIfAbsentValue(5, "5"));
        Assert.assertNull(map.get(5));
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        super.ifPresentApply();
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assert.assertNull(map.ifPresentApply(5, Functions.getPassThru()));
        Assert.assertEquals("1", map.ifPresentApply(1, Functions.getPassThru()));
        Assert.assertEquals("2", map.ifPresentApply(2, Functions.getPassThru()));
        Assert.assertEquals("3", map.ifPresentApply(3, Functions.getPassThru()));
        Assert.assertEquals("4", map.ifPresentApply(4, Functions.getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        super.notEmpty();
        Assert.assertTrue(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();
        MutableList<Integer> result = Lists.mutable.of();
        ImmutableMap<Integer, Integer> map = new ImmutableQuadrupletonMap<>(1, 1, 2, 2, 3, 3, 4, 4);
        map.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 10);
        Assert.assertEquals(FastList.newListWith(11, 12, 13, 14), result);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableQuadrupletonMap<>(1, "One", 2, "Two", 3, "Three", 4, "Four");
        map.forEachWithIndex((value, index) -> {
            result.add(value);
            result.add(String.valueOf(index));
        });
        Assert.assertEquals(FastList.newListWith("One", "0", "Two", "1", "Three", "2", "Four", "3"), result);
    }

    @Override
    @Test
    public void keyValuesView()
    {
        super.keyValuesView();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableQuadrupletonMap<>(1, "One", 2, "Two", 3, "Three", 4, "Four");
        for (Pair<Integer, String> keyValue : map.keyValuesView())
        {
            result.add(keyValue.getTwo());
        }
        Assert.assertEquals(FastList.newListWith("One", "Two", "Three", "Four"), result);
    }

    @Override
    @Test
    public void valuesView()
    {
        super.valuesView();
        MutableList<String> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableQuadrupletonMap<>(1, "One", 2, "Two", 3, "Three", 4, "Four");
        for (String value : map.valuesView())
        {
            result.add(value);
        }
        Assert.assertEquals(FastList.newListWith("One", "Two", "Three", "Four"), result);
    }

    @Override
    @Test
    public void keysView()
    {
        super.keysView();
        MutableList<Integer> result = Lists.mutable.of();
        ImmutableMap<Integer, String> map = new ImmutableQuadrupletonMap<>(1, "One", 2, "Two", 3, "Three", 4, "Four");
        for (Integer key : map.keysView())
        {
            result.add(key);
        }
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), result);
    }

    @Override
    @Test
    public void testToString()
    {
        ImmutableMap<Integer, String> map = new ImmutableQuadrupletonMap<>(1, "One", 2, "Two", 3, "Three", 4, "Four");
        Assert.assertEquals("{1=One, 2=Two, 3=Three, 4=Four}", map.toString());
    }

    @Test
    public void getOnly()
    {
        Verify.assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOnly());
    }

    @Override
    public void select()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        ImmutableMap<Integer, String> empty = map.select((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(ImmutableEmptyMap.class, empty);

        ImmutableMap<Integer, String> full = map.select((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableQuadrupletonMap.class, full);
        Assert.assertEquals(map, full);

        ImmutableMap<Integer, String> one = map.select((argument1, argument2) -> "1".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, one);
        Assert.assertEquals(new ImmutableSingletonMap<>(1, "1"), one);

        ImmutableMap<Integer, String> two = map.select((argument1, argument2) -> "2".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, two);
        Assert.assertEquals(new ImmutableSingletonMap<>(2, "2"), two);

        ImmutableMap<Integer, String> three = map.select((argument1, argument2) -> "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, three);
        Assert.assertEquals(new ImmutableSingletonMap<>(3, "3"), three);

        ImmutableMap<Integer, String> four = map.select((argument1, argument2) -> "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, four);
        Assert.assertEquals(new ImmutableSingletonMap<>(4, "4"), four);

        ImmutableMap<Integer, String> oneAndFour = map.select((argument1, argument2) -> "1".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndFour);
        Assert.assertEquals(new ImmutableDoubletonMap<>(1, "1", 4, "4"), oneAndFour);

        ImmutableMap<Integer, String> oneAndThree = map.select((argument1, argument2) -> "1".equals(argument2) || "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<>(1, "1", 3, "3"), oneAndThree);

        ImmutableMap<Integer, String> oneAndTwo = map.select((argument1, argument2) -> "1".equals(argument2) || "2".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndTwo);
        Assert.assertEquals(new ImmutableDoubletonMap<>(1, "1", 2, "2"), oneAndTwo);

        ImmutableMap<Integer, String> twoAndFour = map.select((argument1, argument2) -> "2".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, twoAndFour);
        Assert.assertEquals(new ImmutableDoubletonMap<>(2, "2", 4, "4"), twoAndFour);

        ImmutableMap<Integer, String> twoAndThree = map.select((argument1, argument2) -> "2".equals(argument2) || "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, twoAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<>(2, "2", 3, "3"), twoAndThree);

        ImmutableMap<Integer, String> threeAndFour = map.select((argument1, argument2) -> "3".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, threeAndFour);
        Assert.assertEquals(new ImmutableDoubletonMap<>(3, "3", 4, "4"), threeAndFour);

        ImmutableMap<Integer, String> twoThreeFour = map.select((argument1, argument2) -> "2".equals(argument2) || "3".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, twoThreeFour);
        Assert.assertEquals(new ImmutableTripletonMap<>(2, "2", 3, "3", 4, "4"), twoThreeFour);

        ImmutableMap<Integer, String> oneThreeFour = map.select((argument1, argument2) -> "1".equals(argument2) || "3".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, oneThreeFour);
        Assert.assertEquals(new ImmutableTripletonMap<>(1, "1", 3, "3", 4, "4"), oneThreeFour);

        ImmutableMap<Integer, String> oneTwoFour = map.select((argument1, argument2) -> "1".equals(argument2) || "2".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, oneTwoFour);
        Assert.assertEquals(new ImmutableTripletonMap<>(1, "1", 2, "2", 4, "4"), oneTwoFour);

        ImmutableMap<Integer, String> oneTwoThree = map.select((argument1, argument2) -> "1".equals(argument2) || "2".equals(argument2) || "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, oneTwoThree);
        Assert.assertEquals(new ImmutableTripletonMap<>(1, "1", 2, "2", 3, "3"), oneTwoThree);
    }

    @Override
    public void reject()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        ImmutableMap<Integer, String> empty = map.reject((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableEmptyMap.class, empty);

        ImmutableMap<Integer, String> full = map.reject((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(ImmutableQuadrupletonMap.class, full);
        Assert.assertEquals(map, full);

        ImmutableMap<Integer, String> one = map.reject((argument1, argument2) -> "2".equals(argument2) || "3".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, one);
        Assert.assertEquals(new ImmutableSingletonMap<>(1, "1"), one);

        ImmutableMap<Integer, String> two = map.reject((argument1, argument2) -> "1".equals(argument2) || "3".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, two);
        Assert.assertEquals(new ImmutableSingletonMap<>(2, "2"), two);

        ImmutableMap<Integer, String> three = map.reject((argument1, argument2) -> "1".equals(argument2) || "2".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, three);
        Assert.assertEquals(new ImmutableSingletonMap<>(3, "3"), three);

        ImmutableMap<Integer, String> four = map.reject((argument1, argument2) -> "1".equals(argument2) || "2".equals(argument2) || "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableSingletonMap.class, four);
        Assert.assertEquals(new ImmutableSingletonMap<>(4, "4"), four);

        ImmutableMap<Integer, String> oneAndFour = map.reject((argument1, argument2) -> "2".equals(argument2) || "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndFour);
        Assert.assertEquals(new ImmutableDoubletonMap<>(1, "1", 4, "4"), oneAndFour);

        ImmutableMap<Integer, String> oneAndThree = map.reject((argument1, argument2) -> "2".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<>(1, "1", 3, "3"), oneAndThree);

        ImmutableMap<Integer, String> oneAndTwo = map.reject((argument1, argument2) -> "3".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, oneAndTwo);
        Assert.assertEquals(new ImmutableDoubletonMap<>(1, "1", 2, "2"), oneAndTwo);

        ImmutableMap<Integer, String> twoAndFour = map.reject((argument1, argument2) -> "1".equals(argument2) || "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, twoAndFour);
        Assert.assertEquals(new ImmutableDoubletonMap<>(2, "2", 4, "4"), twoAndFour);

        ImmutableMap<Integer, String> twoAndThree = map.reject((argument1, argument2) -> "1".equals(argument2) || "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, twoAndThree);
        Assert.assertEquals(new ImmutableDoubletonMap<>(2, "2", 3, "3"), twoAndThree);

        ImmutableMap<Integer, String> threeAndFour = map.reject((argument1, argument2) -> "1".equals(argument2) || "2".equals(argument2));
        Verify.assertInstanceOf(ImmutableDoubletonMap.class, threeAndFour);
        Assert.assertEquals(new ImmutableDoubletonMap<>(3, "3", 4, "4"), threeAndFour);

        ImmutableMap<Integer, String> twoThreeFour = map.reject((argument1, argument2) -> "1".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, twoThreeFour);
        Assert.assertEquals(new ImmutableTripletonMap<>(2, "2", 3, "3", 4, "4"), twoThreeFour);

        ImmutableMap<Integer, String> oneThreeFour = map.reject((argument1, argument2) -> "2".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, oneThreeFour);
        Assert.assertEquals(new ImmutableTripletonMap<>(1, "1", 3, "3", 4, "4"), oneThreeFour);

        ImmutableMap<Integer, String> oneTwoFour = map.reject((argument1, argument2) -> "3".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, oneTwoFour);
        Assert.assertEquals(new ImmutableTripletonMap<>(1, "1", 2, "2", 4, "4"), oneTwoFour);

        ImmutableMap<Integer, String> oneTwoThree = map.reject((argument1, argument2) -> "4".equals(argument2));
        Verify.assertInstanceOf(ImmutableTripletonMap.class, oneTwoThree);
        Assert.assertEquals(new ImmutableTripletonMap<>(1, "1", 2, "2", 3, "3"), oneTwoThree);
    }

    @Override
    public void detect()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        Pair<Integer, String> one = map.detect((ignored1, ignored2) -> true);
        Assert.assertEquals(Tuples.pair(1, "1"), one);

        Pair<Integer, String> two = map.detect((argument1, argument2) -> "2".equals(argument2));
        Assert.assertEquals(Tuples.pair(2, "2"), two);

        Pair<Integer, String> three = map.detect((argument1, argument2) -> "3".equals(argument2));
        Assert.assertEquals(Tuples.pair(3, "3"), three);

        Pair<Integer, String> four = map.detect((argument1, argument2) -> "4".equals(argument2));
        Assert.assertEquals(Tuples.pair(4, "4"), four);

        Assert.assertNull(map.detect((ignored1, ignored2) -> false));
    }

    @Override
    protected <K, V> ImmutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return new ImmutableQuadrupletonMap<>(key1, value1, key2, value2, key3, value3, key4, value4);
    }
}
