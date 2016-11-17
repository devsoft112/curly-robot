/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.parallel.list;

import org.eclipse.collections.api.list.ParallelListIterable;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

public class ImmutableListParallelListIterableTest extends ParallelListIterableTestCase
{
    @Override
    protected ParallelListIterable<Integer> classUnderTest()
    {
        return this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
    }

    @Override
    protected ParallelListIterable<Integer> newWith(Integer... littleElements)
    {
        return Lists.immutable.with(littleElements).asParallel(this.executorService, this.batchSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void asParallel_small_batch()
    {
        Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4).asParallel(this.executorService, 0);
    }

    @Test(expected = NullPointerException.class)
    public void asParallel_null_executorService()
    {
        Lists.immutable.with(1, 2, 2, 3, 3, 3, 4, 4, 4, 4).asParallel(null, 2);
    }
}
