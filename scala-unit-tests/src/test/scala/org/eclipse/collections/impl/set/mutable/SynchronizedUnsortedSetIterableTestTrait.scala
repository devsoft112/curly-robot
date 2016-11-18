/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.mutable

import org.eclipse.collections.api.set.UnsortedSetIterable
import org.eclipse.collections.impl.set.SynchronizedSetIterableTestTrait
import org.junit.Test

trait SynchronizedUnsortedSetIterableTestTrait extends SynchronizedSetIterableTestTrait
{
    val classUnderTest: UnsortedSetIterable[String]

    @Test
    def powerSet_synchronized
    {
        this.assertSynchronized(this.classUnderTest.powerSet())
    }
}
