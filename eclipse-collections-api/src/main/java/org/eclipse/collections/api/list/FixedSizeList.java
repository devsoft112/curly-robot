/*
 * Copyright (c) 2018 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.list;

import java.util.Comparator;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.FixedSizeCollection;

/**
 * A FixedSizeList is a list that may be mutated, but cannot grow or shrink in size. The typical
 * mutation allowed for a FixedSizeList implementation is a working implementation for set(int, T).
 * This will allow the FixedSizeList to be sorted.
 */
public interface FixedSizeList<T>
        extends MutableList<T>, FixedSizeCollection<T>
{
    @Override
    MutableList<T> with(T element);

    @Override
    MutableList<T> without(T element);

    @Override
    MutableList<T> withAll(Iterable<? extends T> elements);

    @Override
    MutableList<T> withoutAll(Iterable<? extends T> elements);

    @Override
    FixedSizeList<T> toReversed();

    @Override
    FixedSizeList<T> tap(Procedure<? super T> procedure);

    @Override
    default FixedSizeList<T> sortThis(Comparator<? super T> comparator)
    {
        this.sort(comparator);
        return this;
    }

    @Override
    default FixedSizeList<T> sortThis()
    {
        return this.sortThis(null);
    }
}
