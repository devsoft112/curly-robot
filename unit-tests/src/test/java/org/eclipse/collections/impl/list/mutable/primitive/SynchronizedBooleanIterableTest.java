/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.mutable.primitive;

import java.util.NoSuchElementException;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.iterator.BooleanIterator;
import org.eclipse.collections.impl.collection.mutable.primitive.AbstractBooleanIterableTestCase;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.primitive.SynchronizedBooleanIterable;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link SynchronizedBooleanIterable}s
 */
public class SynchronizedBooleanIterableTest extends AbstractBooleanIterableTestCase
{
    @Override
    protected BooleanIterable classUnderTest()
    {
        return SynchronizedBooleanIterable.of(BooleanArrayList.newListWith(true, false, true));
    }

    @Override
    protected BooleanIterable newWith(boolean... elements)
    {
        return SynchronizedBooleanIterable.of(BooleanArrayList.newListWith(elements));
    }

    @Override
    protected BooleanIterable newMutableCollectionWith(boolean... elements)
    {
        return BooleanArrayList.newListWith(elements);
    }

    @Override
    protected RichIterable<Object> newObjectCollectionWith(Object... elements)
    {
        return FastList.newListWith(elements);
    }

    @Test(expected = IllegalArgumentException.class)
    public void null_iterable_throws()
    {
        SynchronizedBooleanIterable iterable = SynchronizedBooleanIterable.of(null);
    }

    @Override
    @Test
    public void booleanIterator()
    {
        BooleanIterable iterable = this.newWith(true, true, false, false);
        BooleanArrayList list = BooleanArrayList.newListWith(true, true, false, false);
        BooleanIterator iterator = iterable.booleanIterator();
        for (int i = 0; i < 4; i++)
        {
            Assert.assertTrue(iterator.hasNext());
            Assert.assertTrue(list.remove(iterator.next()));
        }
        Verify.assertEmpty(list);
        Assert.assertFalse(iterator.hasNext());

        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }

    @Override
    @Test
    public void testEquals()
    {
        //Testing equals() is not applicable.
    }

    @Override
    public void testHashCode()
    {
        //Testing hashCode() is not applicable.
    }

    @Override
    public void newCollection()
    {
        //Testing newCollection() is not applicable.
    }
}
