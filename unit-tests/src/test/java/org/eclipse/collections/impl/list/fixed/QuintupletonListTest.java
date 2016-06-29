/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Procedures2;
import org.eclipse.collections.impl.block.procedure.CollectionAddProcedure;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class QuintupletonListTest extends AbstractMemoryEfficientMutableListTestCase
{
    @Override
    protected int getSize()
    {
        return 5;
    }

    @Override
    protected Class<?> getListType()
    {
        return QuintupletonList.class;
    }

    @Test
    public void testClone()
    {
        MutableList<String> growableList = this.list.clone();
        Verify.assertEqualsAndHashCode(this.list, growableList);
        Verify.assertInstanceOf(QuintupletonList.class, growableList);
    }

    @Test
    public void testEqualsAndHashCode()
    {
        MutableList<String> one = Lists.fixedSize.of("1", "2", "3", "4", "5");
        List<String> oneA = new ArrayList<>(one);
        Verify.assertEqualsAndHashCode(one, oneA);
        Verify.assertPostSerializedEqualsAndHashCode(one);
    }

    @Test
    public void testContains()
    {
        Verify.assertContains("1", this.list);
        Verify.assertContains("2", this.list);
        Verify.assertContains("3", this.list);
        Verify.assertContains("4", this.list);
        Verify.assertContains("5", this.list);
        Verify.assertNotContains("6", this.list);
    }

    @Test
    public void testRemove()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.remove(0));
        this.assertUnchanged();
    }

    @Test
    public void testAddAtIndex()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.add(0, "1"));
        this.assertUnchanged();
    }

    @Test
    public void testAdd()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.add("1"));
        this.assertUnchanged();
    }

    @Test
    public void testAddingAllToOtherList()
    {
        List<String> newList = new ArrayList<>(this.list);
        newList.add("6");
        Verify.assertStartsWith(newList, "1", "2", "3", "4", "5", "6");
    }

    @Test
    public void testGet()
    {
        Verify.assertStartsWith(this.list, "1", "2", "3", "4", "5");
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(5));
    }

    @Test
    public void testSet()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3", "4", "5");
        Assert.assertEquals("1", list.set(0, "5"));
        Assert.assertEquals("2", list.set(1, "4"));
        Assert.assertEquals("3", list.set(2, "3"));
        Assert.assertEquals("4", list.set(3, "2"));
        Assert.assertEquals("5", list.set(4, "1"));
        Assert.assertEquals(FastList.newListWith("5", "4", "3", "2", "1"), list);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.set(5, "0"));
    }

    private void assertUnchanged()
    {
        Verify.assertSize(5, this.list);
        Verify.assertNotContains("6", this.list);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5"), this.list);
    }

    @Test
    public void testSerializableEqualsAndHashCode()
    {
        Verify.assertPostSerializedEqualsAndHashCode(this.list);
        MutableList<String> copyOfList = SerializeTestHelper.serializeDeserialize(this.list);
        Assert.assertNotSame(this.list, copyOfList);
    }

    @Test
    public void testGetFirstGetLast()
    {
        MutableList<String> list5 = Lists.fixedSize.of("1", "2", "3", "4", "5");
        Assert.assertEquals("1", list5.getFirst());
        Assert.assertEquals("5", list5.getLast());
    }

    @Test
    public void testForEach()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4", "5");
        source.forEach(CollectionAddProcedure.on(result));
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5"), result);
    }

    @Test
    public void testForEachWithIndex()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4", "5");
        source.forEachWithIndex((each, index) -> {
            result.add(each);
            indexSum[0] += index;
        });
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5"), result);
        Assert.assertEquals(10, indexSum[0]);
    }

    @Test
    public void testForEachWith()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4", "5");
        source.forEachWith(Procedures2.fromProcedure(result::add), null);
        Assert.assertEquals(FastList.newListWith("1", "2", "3", "4", "5"), result);
    }

    @Test
    public void testForLoop()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three", "four", "five");
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE", "FOUR", "FIVE");
        for (String each : list)
        {
            Verify.assertContains(each.toUpperCase(), upperList);
        }
    }

    @Test
    public void testSubList()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three", "four", "five");
        MutableList<String> subList = list.subList(0, 4);
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE", "FOUR", "FIVE");
        for (String each : subList)
        {
            Verify.assertContains(each.toUpperCase(), upperList);
        }
    }

    @Test
    public void without()
    {
        MutableList<Integer> list = new QuintupletonList<>(1, 2, 3, 2, 4);
        Assert.assertSame(list, list.without(9));
        list = list.without(2);
        Verify.assertListsEqual(FastList.newListWith(1, 3, 2, 4), list);
        Verify.assertInstanceOf(QuadrupletonList.class, list);
    }

    @Test
    public void testGetOnly()
    {
        Verify.assertThrows(IllegalStateException.class, () -> this.list.getOnly());
    }
}
