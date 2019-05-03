/*
 * Copyright (c) 2019 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.factory.set.MultiReaderSetFactory;
import org.eclipse.collections.api.set.MultiReaderSet;

public enum MultiReaderMutableSetFactory implements MultiReaderSetFactory
{
    INSTANCE;

    @Override
    public <T> MultiReaderSet<T> empty()
    {
        return MultiReaderUnifiedSet.newSet();
    }

    @Override
    public <T> MultiReaderSet<T> with(T... items)
    {
        return MultiReaderUnifiedSet.newSetWith(items);
    }

    @Override
    public <T> MultiReaderSet<T> withInitialCapacity(int capacity)
    {
        if (capacity < 0)
        {
            throw new IllegalArgumentException("initial capacity cannot be less than 0");
        }

        return MultiReaderUnifiedSet.newSet(capacity);
    }

    @Override
    public <T> MultiReaderSet<T> withAll(Iterable<? extends T> iterable)
    {
        return MultiReaderUnifiedSet.newSet((Iterable<T>) iterable);
    }

    @Override
    public <T> MultiReaderSet<T> fromStream(Stream<? extends T> stream)
    {
        return stream.collect(Collectors.toCollection(MultiReaderUnifiedSet::newSet));
    }
}
