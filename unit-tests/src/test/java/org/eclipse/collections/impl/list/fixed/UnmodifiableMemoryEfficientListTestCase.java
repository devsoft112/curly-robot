/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.util.ListIterator;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableMutableCollectionTestCase;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Abstract JUnit test to check that {@link AbstractMemoryEfficientMutableList}s are Unmodifiable.
 */
public abstract class UnmodifiableMemoryEfficientListTestCase<T> extends UnmodifiableMutableCollectionTestCase<T>
{
    @Override
    protected abstract MutableList<T> getCollection();

    @Test
    public void listIterator()
    {
        MutableList<T> collection = this.getCollection();
        ListIterator<T> it = collection.listIterator();
        assertFalse(it.hasPrevious());
        assertEquals(-1, it.previousIndex());
        assertEquals(0, it.nextIndex());
        it.next();
        assertEquals(1, it.nextIndex());

        assertThrows(UnsupportedOperationException.class, it::remove);

        assertThrows(UnsupportedOperationException.class, () -> it.add(null));

        it.set(null);
        assertNotEquals(this.getCollection(), collection);
    }

    @Test
    public void addAllAtIndex()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.getCollection().addAll(0, FastList.<T>newList().with((T) null)));
    }

    @Test
    public void addAtIndex()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.getCollection().add(0, null));
    }

    @Test
    public void removeFromIndex()
    {
        assertThrows(UnsupportedOperationException.class, () -> this.getCollection().remove(0));
    }

    @Test
    public void subList()
    {
        MutableList<T> subList = this.getCollection().subList(0, 1);
        assertThrows(UnsupportedOperationException.class, subList::clear);
    }

    @Override
    @Test
    public void newEmpty()
    {
        MutableList<T> list = this.getCollection().newEmpty();
        list.add(null);
        Verify.assertContains(null, list);
    }

    @Test
    public void corresponds()
    {
        MutableList<T> mutableList1 = this.getCollection();
        MutableList<Integer> mutableList2 = mutableList1.collect(element -> Integer.valueOf(element.toString()) + 1);
        assertTrue(mutableList1.corresponds(mutableList2, (argument1, argument2) -> Integer.valueOf(argument1.toString()) < argument2));
        assertFalse(mutableList1.corresponds(mutableList2, (argument1, argument2) -> Integer.valueOf(argument1.toString()) > argument2));

        MutableList<Integer> mutableList3 = this.getCollection().collect(element -> Integer.valueOf(element.toString()));
        mutableList3.add(0);
        assertFalse(mutableList1.corresponds(mutableList3, Predicates2.alwaysTrue()));
    }

    @Test
    public void detectIndex()
    {
        MutableList<T> mutableList = this.getCollection();
        assertEquals(0, mutableList.detectIndex(element -> Integer.valueOf(element.toString()) == 1));
        assertEquals(-1, mutableList.detectIndex(element -> Integer.valueOf(element.toString()) == 0));
    }

    @Test
    public void detectLastIndex()
    {
        MutableList<T> mutableList = this.getCollection();
        assertEquals(0, mutableList.detectLastIndex(element -> Integer.valueOf(element.toString()) == 1));
        assertEquals(-1, mutableList.detectLastIndex(element -> Integer.valueOf(element.toString()) == 0));
    }
}
