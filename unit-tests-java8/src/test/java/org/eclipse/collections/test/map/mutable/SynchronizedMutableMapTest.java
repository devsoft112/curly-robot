/*
 * Copyright (c) 2018 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable;

import java.util.Random;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.SynchronizedMutableMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.test.junit.Java8Runner;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(Java8Runner.class)
public class SynchronizedMutableMapTest implements MutableMapTestCase
{
    private static final long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    @Override
    public <T> MutableMap<Object, T> newWith(T... elements)
    {
        Random random = new Random(CURRENT_TIME_MILLIS);
        MutableMap<Object, T> result = new UnifiedMap<>();
        for (T each : elements)
        {
            assertNull(result.put(random.nextDouble(), each));
        }
        return SynchronizedMutableMap.of(result);
    }

    @Override
    public <K, V> MutableMap<K, V> newWithKeysValues(Object... elements)
    {
        if (elements.length % 2 != 0)
        {
            fail(String.valueOf(elements.length));
        }

        MutableMap<K, V> result = new UnifiedMap<>();
        for (int i = 0; i < elements.length; i += 2)
        {
            assertNull(result.put((K) elements[i], (V) elements[i + 1]));
        }
        return SynchronizedMutableMap.of(result);
    }
}
