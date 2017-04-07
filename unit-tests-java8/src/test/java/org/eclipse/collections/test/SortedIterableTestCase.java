/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test;

import org.eclipse.collections.api.ordered.SortedIterable;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.junit.Test;

import static org.eclipse.collections.impl.test.Verify.assertThrows;
import static org.junit.Assert.assertSame;

public interface SortedIterableTestCase extends OrderedIterableTestCase, NoDetectOptionalNullTestCase
{
    @Override
    <T> SortedIterable<T> newWith(T... elements);

    @Test
    default void SortedIterable_comparator()
    {
        assertSame(Comparators.reverseNaturalOrder(), this.newWith().comparator());
    }

    @Override
    default void RichIterable_min_max_non_comparable()
    {
        assertThrows(ClassCastException.class, () -> this.newWith(new Object()));
    }

    @Override
    @Test
    default void RichIterable_minOptional_maxOptional_non_comparable()
    {
        assertThrows(ClassCastException.class, () -> this.newWith(new Object()));
    }
}
