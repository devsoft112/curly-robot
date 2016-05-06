/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.lazy.parallel.bag;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.ParallelBag;
import org.eclipse.collections.impl.bag.mutable.HashBag;

public class ParallelCollectBagTest extends ParallelBagTestCase
{
    @Override
    protected ParallelBag<Integer> classUnderTest()
    {
        return this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
    }

    @Override
    protected ParallelBag<Integer> newWith(Integer... littleElements)
    {
        return HashBag.newBagWith(littleElements)
                .asParallel(this.executorService, this.batchSize)
                .collect(String::valueOf)
                .collect(string -> "null".equals(string) ? null : Integer.valueOf(string));
    }

    @Override
    protected MutableBag<Integer> getExpectedWith(Integer... littleElements)
    {
        return HashBag.newBagWith(littleElements)
                .collect(String::valueOf)
                .collect(string -> "null".equals(string) ? null : Integer.valueOf(string));
    }
}
