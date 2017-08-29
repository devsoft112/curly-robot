/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.fixed;

import org.eclipse.collections.api.factory.map.FixedSizeMapFactory;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.impl.block.factory.Comparators;

public enum FixedSizeMapFactoryImpl implements FixedSizeMapFactory
{
    INSTANCE;

    private static final FixedSizeMap<?, ?> EMPTY_MAP = new EmptyMap<>();

    @Override
    public <K, V> FixedSizeMap<K, V> empty()
    {
        return (FixedSizeMap<K, V>) EMPTY_MAP;
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of()
    {
        return this.empty();
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with()
    {
        return this.empty();
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of(K key, V value)
    {
        return this.with(key, value);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with(K key, V value)
    {
        return new SingletonMap<>(key, value);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of(K key1, V value1, K key2, V value2)
    {
        return this.with(key1, value1, key2, value2);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with(K key1, V value1, K key2, V value2)
    {
        if (Comparators.nullSafeEquals(key1, key2))
        {
            return this.of(key1, value2);
        }
        return new DoubletonMap<>(key1, value1, key2, value2);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return this.with(key1, value1, key2, value2, key3, value3);
    }

    @Override
    public <K, V> FixedSizeMap<K, V> with(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        if (Comparators.nullSafeEquals(key1, key2) && Comparators.nullSafeEquals(key2, key3))
        {
            return this.of(key1, value3);
        }
        if (Comparators.nullSafeEquals(key1, key2))
        {
            return this.of(key1, value2, key3, value3);
        }
        if (Comparators.nullSafeEquals(key1, key3))
        {
            return this.of(key2, value2, key1, value3);
        }
        if (Comparators.nullSafeEquals(key2, key3))
        {
            return this.of(key1, value1, key2, value3);
        }
        return new TripletonMap<>(key1, value1, key2, value2, key3, value3);
    }
}
