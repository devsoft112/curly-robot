/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.sorted.SortedMapIterable;
import org.eclipse.collections.test.OrderedIterableTestCase;
import org.eclipse.collections.test.list.TransformsToListTrait;

public interface SortedMapIterableTestCase extends MapIterableTestCase, OrderedIterableTestCase, TransformsToListTrait
{
    @Override
    <T> SortedMapIterable<Object, T> newWith(T... elements);

    @Override
    default boolean supportsNullKeys()
    {
        return false;
    }

    @Override
    default <T> ListIterable<T> getExpectedFiltered(T... elements)
    {
        return Lists.immutable.with(elements);
    }

    @Override
    default <T> MutableList<T> newMutableForFilter(T... elements)
    {
        return Lists.mutable.with(elements);
    }
}
