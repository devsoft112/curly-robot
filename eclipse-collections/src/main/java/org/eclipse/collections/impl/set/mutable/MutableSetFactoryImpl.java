/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable;

import org.eclipse.collections.api.factory.set.MutableSetFactory;
import org.eclipse.collections.api.set.MutableSet;

public enum MutableSetFactoryImpl implements MutableSetFactory
{
    INSTANCE;

    @Override
    public <T> MutableSet<T> empty()
    {
        return UnifiedSet.newSet();
    }

    @Override
    public <T> MutableSet<T> of()
    {
        return this.empty();
    }

    @Override
    public <T> MutableSet<T> with()
    {
        return this.empty();
    }

    @Override
    public <T> MutableSet<T> ofInitialCapacity(int capacity)
    {
        return this.withInitialCapacity(capacity);
    }

    @Override
    public <T> MutableSet<T> withInitialCapacity(int capacity)
    {
        return UnifiedSet.newSet(capacity);
    }

    @Override
    public <T> MutableSet<T> of(T... items)
    {
        return this.with(items);
    }

    @Override
    public <T> MutableSet<T> with(T... items)
    {
        return UnifiedSet.newSetWith(items);
    }

    @Override
    public <T> MutableSet<T> ofAll(Iterable<? extends T> items)
    {
        return this.withAll(items);
    }

    @Override
    public <T> MutableSet<T> withAll(Iterable<? extends T> items)
    {
        return UnifiedSet.newSet(items);
    }
}
