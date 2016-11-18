/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.collection.mutable;

import org.eclipse.collections.impl.collection.mutable.AbstractMultiReaderMutableCollection;
import org.junit.Test;

public interface MultiReaderMutableCollectionTestCase extends MutableCollectionTestCase
{
    @Override
    <T> AbstractMultiReaderMutableCollection<T> newWith(T... elements);

    @Test(expected = UnsupportedOperationException.class)
    default void Iterable_iterator_throws()
    {
        this.newWith(3, 2, 1).iterator();
    }

    @Test
    @Override
    default void Iterable_remove()
    {
        // Multi-reader collections don't support iterator()
    }

    @Test
    @Override
    default void Iterable_next()
    {
        // Multi-reader collections don't support iterator()
    }

    @Test
    @Override
    default void Iterable_hasNext()
    {
        // Multi-reader collections don't support iterator()
    }

    @Test
    @Override
    default void Iterable_next_throws_at_end()
    {
        // Multi-reader collections don't support iterator()
    }

    @Test
    @Override
    default void Iterable_next_throws_on_empty()
    {
        // Multi-reader collections don't support iterator()
    }

    @Test
    @Override
    default void RichIterable_getFirst()
    {
        // Does not support iterator outside withReadLockAndDelegate
    }

    @Test
    @Override
    default void RichIterable_getLast()
    {
        // Does not support iterator outside withReadLockAndDelegate
    }

    @Test
    @Override
    default void RichIterable_getOnly()
    {
        // Does not support iterator outside withReadLockAndDelegate
    }
}
