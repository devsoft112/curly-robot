/*
 * Copyright (c) 2024 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.map.mutable;

import java.util.Map;

import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.block.factory.Predicates2;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.test.FixedSizeIterableTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public interface UnmodifiableMutableMapIterableTestCase
        extends MutableMapIterableTestCase, FixedSizeIterableTestCase
{
    @Override
    default void MutableMapIterable_removeKey()
    {
        MutableMapIterable<Object, Object> map = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map.removeKey(2));
    }

    @Override
    @Test
    default void MutableMapIterable_removeAllKeys()
    {
        MutableMapIterable<Object, Object> map = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map.removeAllKeys(Sets.mutable.empty()));
    }

    @Override
    default void MutableMapIterable_removeIf()
    {
        MutableMapIterable<Object, Object> map1 = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map1.removeIf(null));

        MutableMapIterable<Object, String> map2 = this.newWith("Three", "Two", "One");
        assertThrows(UnsupportedOperationException.class, () -> map2.removeIf(Predicates2.alwaysFalse()));
    }

    @Override
    default void Map_remove()
    {
        MutableMapIterable<Object, Object> map = this.newWith();
        assertThrows(UnsupportedOperationException.class, () -> map.remove(2));
    }

    @Override
    default void Map_entrySet_remove()
    {
        MutableMapIterable<Object, Object> map = this.newWithKeysValues();
        assertThrows(UnsupportedOperationException.class, () -> map.entrySet().remove(ImmutableEntry.of(null, null)));
    }

    @Override
    default void Map_clear()
    {
        MutableMapIterable<Object, String> map = this.newWith("Three", "Two", "One");
        assertThrows(UnsupportedOperationException.class, map::clear);
    }

    @Override
    default void Iterable_remove()
    {
        FixedSizeIterableTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void Map_put()
    {
        MutableMapIterable<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "Two", 1, "One");
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "Four"));
        assertThrows(UnsupportedOperationException.class, () -> map.put(1, "One"));
        assertThrows(UnsupportedOperationException.class, () -> map.put(5, null));
        assertThrows(UnsupportedOperationException.class, () -> map.put(null, "Six"));
        assertIterablesEqual(this.newWithKeysValues(3, "Three", 2, "Two", 1, "One"), map);
    }

    @Test
    @Override
    default void Map_putAll()
    {
        MutableMapIterable<Integer, String> map = this.newWithKeysValues(3, "Three", 2, "2");
        MutableMapIterable<Integer, String> toAdd = this.newWithKeysValues(2, "Two", 1, "One");

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(toAdd));

        MutableMapIterable<Integer, String> expected = this.newWithKeysValues(3, "Three", 2, "2");
        assertIterablesEqual(expected, map);

        assertThrows(UnsupportedOperationException.class, () -> map.putAll(null));
        assertThrows(UnsupportedOperationException.class, () -> map.putAll(Map.of()));
    }

    @Override
    @Test
    default void MutableMapIterable_getIfAbsentPut()
    {
        MutableMapIterable<String, Integer> map = this.newWithKeysValues("3", 3, "2", 2, "1", 1);
        assertIterablesEqual(3, map.getIfAbsentPut("3", () -> {
            throw new AssertionError();
        }));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut("4", () -> {
            throw new AssertionError();
        }));

        assertIterablesEqual(3, map.getIfAbsentPut("3", 4));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPut("4", 4));

        assertIterablesEqual(3, map.getIfAbsentPutWithKey("3", key -> {
            throw new AssertionError();
        }));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWithKey("4", key -> {
            throw new AssertionError();
        }));

        assertIterablesEqual(3, map.getIfAbsentPutWith("3", x -> x + 10, 4));
        assertIterablesEqual(this.newWithKeysValues("3", 3, "2", 2, "1", 1), map);

        assertThrows(UnsupportedOperationException.class, () -> map.getIfAbsentPutWith("4", x -> x + 10, 4));
    }

    @Override
    default void MutableMapIterable_updateValue()
    {
        MutableMapIterable<String, Integer> map = this.newWithKeysValues("One", 1);
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValue("One", () -> 0, ignored -> {
                    throw new AssertionError();
                }));
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValue("Two", () -> 0, ignored -> {
                    throw new AssertionError();
                }));

        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValueWith("One", () -> 0, (ignored, parameter) -> {
                    throw new AssertionError();
                }, "test"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> map.updateValueWith("Two", () -> 0, (ignored, parameter) -> {
                    throw new AssertionError();
                }, "test"));
    }

    @Override
    default void Map_merge()
    {
        MutableMapIterable<Integer, String> map = this.newWithKeysValues(1, "1", 2, "2", 3, "3");
        assertThrows(UnsupportedOperationException.class, () -> map.merge(3, "4", (v1, v2) -> {
            fail("Expected lambda not to be called on unmodifiable map");
            return null;
        }));
        assertThrows(UnsupportedOperationException.class, () -> map.merge(4, "4", (v1, v2) -> {
            fail("Expected lambda not to be called on unmodifiable map");
            return null;
        }));
        assertEquals(this.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }
}
