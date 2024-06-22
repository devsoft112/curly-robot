/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable.primitive;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.block.function.primitive.BooleanToObjectFunction;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.impl.bag.mutable.HashBag;
import org.eclipse.collections.impl.collection.mutable.primitive.AbstractMutableBooleanCollectionTestCase;
import org.eclipse.collections.impl.factory.primitive.BooleanBags;
import org.eclipse.collections.impl.factory.primitive.BooleanSets;
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Abstract JUnit test for {@link MutableBooleanBag}.
 */
public abstract class AbstractMutableBooleanBagTestCase extends AbstractMutableBooleanCollectionTestCase
{
    @Override
    protected abstract MutableBooleanBag classUnderTest();

    @Override
    protected abstract MutableBooleanBag newWith(boolean... elements);

    @Override
    protected MutableBooleanBag newMutableCollectionWith(boolean... elements)
    {
        return BooleanHashBag.newBagWith(elements);
    }

    @Override
    protected MutableBag<Object> newObjectCollectionWith(Object... elements)
    {
        return HashBag.newBagWith(elements);
    }

    @Test
    public void sizeDistinct()
    {
        assertEquals(0L, this.newWith().sizeDistinct());
        assertEquals(1L, this.newWith(true).sizeDistinct());
        assertEquals(1L, this.newWith(true, true, true).sizeDistinct());
        assertEquals(2L, this.newWith(true, false, true, false, true).sizeDistinct());
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();
        MutableBooleanBag bag = this.newWith();
        assertTrue(bag.addAll(BooleanArrayList.newListWith(true, false, true, false, true)));
        assertFalse(bag.addAll(new BooleanArrayList()));
        assertEquals(BooleanHashBag.newBagWith(true, false, true, false, true), bag);
        assertTrue(bag.addAll(BooleanHashBag.newBagWith(true, false, true, false, true)));
        assertEquals(BooleanHashBag.newBagWith(false, false, false, false, true, true, true, true, true, true), bag);
    }

    @Test
    public void addOccurrences()
    {
        MutableBooleanBag bag = this.newWith();
        bag.addOccurrences(false, 3);
        assertEquals(BooleanHashBag.newBagWith(false, false, false), bag);
        bag.addOccurrences(false, 2);
        assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false), bag);
        bag.addOccurrences(false, 0);
        assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false), bag);
        bag.addOccurrences(true, 0);
        assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false), bag);
        bag.addOccurrences(true, 1);
        assertEquals(BooleanHashBag.newBagWith(false, false, false, false, false, true), bag);
    }

    @Test
    public void addOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().addOccurrences(true, -1));
    }

    @Test
    public void removeOccurrences()
    {
        MutableBooleanBag bag1 = this.newWith();
        assertFalse(bag1.removeOccurrences(true, 5));
        bag1.addOccurrences(true, 5);
        assertTrue(bag1.removeOccurrences(true, 2));
        assertEquals(BooleanHashBag.newBagWith(true, true, true), bag1);
        assertFalse(bag1.removeOccurrences(true, 0));
        assertEquals(BooleanHashBag.newBagWith(true, true, true), bag1);
        assertTrue(bag1.removeOccurrences(true, 5));
        assertEquals(new BooleanHashBag(), bag1);
        assertFalse(bag1.removeOccurrences(true, 5));
        assertEquals(new BooleanHashBag(), bag1);

        MutableBooleanBag bag2 = this.newWith();
        assertFalse(bag2.removeOccurrences(false, 5));
        bag2.addOccurrences(false, 5);
        assertTrue(bag2.removeOccurrences(false, 2));
        assertEquals(BooleanHashBag.newBagWith(false, false, false), bag2);
        assertFalse(bag2.removeOccurrences(false, 0));
        assertEquals(BooleanHashBag.newBagWith(false, false, false), bag2);
        assertTrue(bag2.removeOccurrences(false, 5));
        assertEquals(new BooleanHashBag(), bag2);
        assertFalse(bag2.removeOccurrences(false, 5));
        assertEquals(new BooleanHashBag(), bag2);
    }

    @Test
    public void removeOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().removeOccurrences(true, -1));
    }

    @Test
    public void forEachWithOccurrences()
    {
        StringBuilder stringBuilder = new StringBuilder();
        this.classUnderTest().forEachWithOccurrences((argument1, argument2) -> stringBuilder.append(argument1).append(argument2));
        String string = stringBuilder.toString();
        assertTrue("true2false1".equals(string)
                || "false1true2".equals(string));
    }

    @Override
    @Test
    public void size()
    {
        super.size();
        Verify.assertSize(3, this.classUnderTest());
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();
        long[] count = {0};
        MutableBooleanBag bag = this.newWith(false, true, false);
        assertTrue(bag.anySatisfy(value ->
        {
            count[0]++;
            return value;
        }));
        assertEquals(2L, count[0]);
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();
        int[] count = {0};
        MutableBooleanBag bag = this.newWith(false, true, false);
        assertFalse(bag.allSatisfy(value ->
        {
            count[0]++;
            return !value;
        }));
        assertEquals(2L, count[0]);
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        super.noneSatisfy();
        MutableBooleanBag bag = this.newWith(false, true, false);
        assertFalse(bag.noneSatisfy(value -> value));
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        MutableBooleanBag bag = this.newWith(true, false, false, true, true, true);
        BooleanToObjectFunction<String> stringValueOf = parameter -> parameter ? "true" : "false";
        assertEquals(HashBag.newBagWith("true", "false", "false", "true", "true", "true"), bag.collect(stringValueOf));
        MutableBooleanBag bag1 = this.newWith(false, false);
        assertEquals(HashBag.newBagWith("false", "false"), bag1.collect(stringValueOf));
        MutableBooleanBag bag2 = this.newWith(true, true);
        assertEquals(HashBag.newBagWith("true", "true"), bag2.collect(stringValueOf));
    }

    @Override
    @Test
    public void testEquals()
    {
        super.testEquals();
        MutableBooleanCollection collection1 = this.newWith(true, false, true, false);
        MutableBooleanCollection collection2 = this.newWith(true, false, false, true);
        MutableBooleanCollection collection3 = this.newWith(true, false);
        MutableBooleanCollection collection4 = this.newWith(true, true, false);
        assertEquals(collection1, collection2);
        assertNotEquals(collection3, collection4);
    }

    @Override
    @Test
    public void testHashCode()
    {
        super.testHashCode();
        MutableBooleanCollection collection1 = this.newWith(true, false, true, false);
        MutableBooleanCollection collection2 = this.newWith(true, false, false, true);
        MutableBooleanCollection collection3 = this.newWith(true, false);
        MutableBooleanCollection collection4 = this.newWith(true, true, false);
        Verify.assertEqualsAndHashCode(collection1, collection2);
        assertNotEquals(collection3.hashCode(), collection4.hashCode());
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();
        assertEquals("[true, true, true]", BooleanHashBag.newBagWith(true, true, true).toString());
    }

    @Override
    @Test
    public void makeString()
    {
        super.makeString();
        assertEquals("true, true, true", BooleanHashBag.newBagWith(true, true, true).makeString());
    }

    @Override
    @Test
    public void appendString()
    {
        super.appendString();
        StringBuilder appendable1 = new StringBuilder();
        this.newWith(true, true, true).appendString(appendable1);
        assertEquals("true, true, true", appendable1.toString());

        StringBuilder appendable2 = new StringBuilder();
        MutableBooleanBag bag1 = this.newWith(false, false, true);
        bag1.appendString(appendable2);
        assertTrue(appendable2.toString(), "false, false, true".equals(appendable2.toString())
                || "true, false, false".equals(appendable2.toString())
                || "false, true, false".equals(appendable2.toString()));
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        MutableBooleanList list = this.newWith(false, false, true).toList();
        assertTrue(list.equals(BooleanArrayList.newListWith(false, false, true))
                || list.equals(BooleanArrayList.newListWith(true, false, false))
                || list.equals(BooleanArrayList.newListWith(false, true, false)));
    }

    @Test
    public void toImmutable()
    {
        assertEquals(this.classUnderTest(), this.classUnderTest().toImmutable());
        assertNotSame(this.classUnderTest(), this.classUnderTest().toImmutable());
        Verify.assertInstanceOf(ImmutableBooleanBag.class, this.classUnderTest().toImmutable());
    }

    @Test
    public void topOccurrences()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().topOccurrences(-1));
        assertTrue(this.newWith().topOccurrences(1).isEmpty());
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(true, 2)),
                this.newWith(true, true, false).topOccurrences(1));
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(false, 2)),
                this.newWith(false, true, false).topOccurrences(1));
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(true, 2), PrimitiveTuples.pair(false, 1)),
                this.newWith(true, true, false).topOccurrences(2));
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(true, 1), PrimitiveTuples.pair(false, 1)).toBag(),
                this.newWith(true, false).topOccurrences(1).toBag());
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(false, 2), PrimitiveTuples.pair(true, 1)).toBag(),
                this.newWith(true, false, false).topOccurrences(2).toBag());
    }

    @Test
    public void bottomOccurrences()
    {
        assertThrows(IllegalArgumentException.class, () -> this.newWith().bottomOccurrences(-1));
        assertTrue(this.newWith().bottomOccurrences(1).isEmpty());
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(false, 1)),
                this.newWith(true, true, false).bottomOccurrences(1));
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(true, 1)),
                this.newWith(false, true, false).bottomOccurrences(1));
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(true, 1), PrimitiveTuples.pair(false, 2)),
                this.newWith(false, true, false).bottomOccurrences(2));
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(true, 1), PrimitiveTuples.pair(false, 1)),
                this.newWith(true, false).bottomOccurrences(1));
        assertEquals(
                Lists.mutable.with(PrimitiveTuples.pair(false, 1), PrimitiveTuples.pair(true, 2)),
                this.newWith(true, true, false).bottomOccurrences(2));
    }

    @Test
    public void selectUnique()
    {
        MutableBooleanBag bag = BooleanBags.mutable.with(false, false, true);
        MutableBooleanSet expected = BooleanSets.mutable.with(true);
        MutableBooleanSet actual = bag.selectUnique();
        assertEquals(expected, actual);

        MutableBooleanBag bag2 = BooleanBags.mutable.with(false, false, true, true);
        MutableBooleanSet expected2 = BooleanSets.mutable.empty();
        MutableBooleanSet actual2 = bag2.selectUnique();
        assertEquals(expected2, actual2);
    }
}
