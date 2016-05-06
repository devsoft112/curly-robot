/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.predicate;

import org.eclipse.collections.api.block.predicate.Predicate;

public class DropIterablePredicate<T> implements Predicate<T>
{
    private static final long serialVersionUID = 1L;

    private final int count;
    private int index;

    public DropIterablePredicate(int count)
    {
        this.count = count;
        this.index = 0;
    }

    @Override
    public boolean accept(T each)
    {
        return this.index++ >= this.count;
    }
}
