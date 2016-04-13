/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.mutable;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.impl.collection.mutable.AbstractCollectionTestCase;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class MultiReaderHashBagAsWriteUntouchableTest extends AbstractCollectionTestCase
{
    @Override
    protected <T> MutableBag<T> newWith(T... littleElements)
    {
        return MultiReaderHashBag.newBagWith(littleElements).asWriteUntouchable();
    }

    @Override
    public void asSynchronized()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newWith().asSynchronized());
    }

    @Override
    public void asUnmodifiable()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newWith().asUnmodifiable());
    }

    @Test
    public void addOccurrences()
    {
        MutableBag<Integer> bag = this.newWith(1, 1);
        Assert.assertEquals(4, bag.addOccurrences(1, 2));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1), bag);
        Assert.assertEquals(0, bag.addOccurrences(2, 0));
        Assert.assertEquals(2, bag.addOccurrences(2, 2));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1, 2, 2), bag);
    }

    @Override
    @Test
    public void makeString()
    {
        Assert.assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }

    @Override
    @Test
    public void appendString()
    {
        Appendable builder = new StringBuilder();
        MultiReaderHashBag.newBagWith(1, 1, 2, 3).appendString(builder);
        Assert.assertEquals("1, 1, 2, 3", builder.toString());
    }

    @Override
    @Test
    public void testToString()
    {
        Assert.assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }
}
