/*
 * Copyright (c) 2022 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.test.list;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.test.CollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.eclipse.collections.test.IterableTestCase.assertIterablesEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface ListTestCase extends CollectionTestCase
{
    @Override
    <T> List<T> newWith(T... elements);

    @Override
    default boolean allowsDuplicates()
    {
        return true;
    }

    @Test
    @Override
    default void Iterable_remove()
    {
        List<Integer> list = this.newWith(3, 3, 3, 2, 2, 1);
        Iterator<Integer> iterator = list.iterator();
        iterator.next();
        iterator.remove();
        assertIterablesEqual(this.newWith(3, 3, 2, 2, 1), list);
    }

    @Override
    default void Iterable_toString()
    {
        Iterable<Integer> iterable = this.newWith(3, 3, 3, 2, 2, 1);
        assertEquals("[3, 3, 3, 2, 2, 1]", iterable.toString());
    }

    @Test
    default void List_get()
    {
        List<Integer> list = this.newWith(1, 2, 3);
        assertIterablesEqual(Integer.valueOf(1), list.get(0));
        assertIterablesEqual(Integer.valueOf(2), list.get(1));
        assertIterablesEqual(Integer.valueOf(3), list.get(2));
    }

    @Test
    default void List_get_negative()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> this.newWith(1, 2, 3).get(-1));
    }

    @Test
    default void List_get_out_of_bounds()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> this.newWith(1, 2, 3).get(4));
    }

    @Test
    default void List_set()
    {
        List<Integer> list = this.newWith(3, 2, 1);
        assertIterablesEqual(3, list.set(0, 4));
        assertIterablesEqual(this.newWith(4, 2, 1), list);
        assertIterablesEqual(2, list.set(1, 4));
        assertIterablesEqual(this.newWith(4, 4, 1), list);
        assertIterablesEqual(1, list.set(2, 4));
        assertIterablesEqual(this.newWith(4, 4, 4), list);

        assertThrows(IndexOutOfBoundsException.class, () -> list.set(-1, 4));
        assertIterablesEqual(this.newWith(4, 4, 4), list);
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(3, 4));
        assertIterablesEqual(this.newWith(4, 4, 4), list);
    }

    @Test
    default void List_subList_subList_remove()
    {
        List<String> list = this.newWith("A", "B", "C", "D");
        List<String> sublist = list.subList(0, 3);
        List<String> sublist2 = sublist.subList(0, 2);
        assertIterablesEqual(Lists.immutable.with("A", "B", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("A", "B"), sublist2);

        sublist2.add("X");

        assertIterablesEqual(Lists.immutable.with("A", "B", "X", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("A", "B", "X"), sublist2);

        assertIterablesEqual("B", sublist2.remove(1));
        assertIterablesEqual(Lists.immutable.with("A", "X", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("A", "X"), sublist2);

        assertIterablesEqual(Lists.immutable.with("A", "X", "C", "D"), list);
    }

    @Test
    default void List_subList_subList_iterator_add_remove()
    {
        List<String> list = this.newWith("A", "B", "C", "D");
        List<String> sublist = list.subList(0, 3);
        List<String> sublist2 = sublist.subList(0, 2);
        assertIterablesEqual(Lists.immutable.with("A", "B", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("A", "B"), sublist2);

        ListIterator<String> iterator = sublist2.listIterator();
        iterator.add("X");
        assertIterablesEqual(Lists.immutable.with("X", "A", "B", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("X", "A", "B"), sublist2);

        ListIterator<String> iterator2 = sublist2.listIterator();
        iterator2.next();
        iterator2.remove();
        assertIterablesEqual(Lists.immutable.with("A", "B", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("A", "B"), sublist2);

        assertIterablesEqual(Lists.immutable.with("A", "B", "C", "D"), list);
    }

    @Test
    default void List_subList_subList_addAll()
    {
        List<String> list = this.newWith("A", "B", "C", "D");
        List<String> sublist = list.subList(0, 3);
        List<String> sublist2 = sublist.subList(0, 2);
        assertIterablesEqual(Lists.immutable.with("A", "B", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("A", "B"), sublist2);

        sublist2.addAll(Lists.mutable.of("D", "E"));
        assertIterablesEqual(Lists.immutable.with("A", "B", "D", "E", "C"), sublist);
        assertIterablesEqual(Lists.immutable.with("A", "B", "D", "E"), sublist2);

        sublist2.clear();
        assertIterablesEqual(Lists.immutable.with("C"), sublist);
        assertIterablesEqual(Lists.immutable.with(), sublist2);

        assertIterablesEqual(Lists.immutable.with("C", "D"), list);
    }

    @Test
    default void List_subList_subList_clear()
    {
        List<String> list = this.newWith("A", "B", "C", "D", "E", "F");
        List<String> sublist = list.subList(3, 6);
        List<String> sublist2 = sublist.subList(0, 2);
        assertIterablesEqual(Lists.immutable.with("D", "E", "F"), sublist);
        assertIterablesEqual(Lists.immutable.with("D", "E"), sublist2);

        sublist2.clear();
        assertIterablesEqual(Lists.immutable.with("A", "B", "C", "F"), list);
        assertIterablesEqual(Lists.immutable.with("F"), sublist);
        assertIterablesEqual(Lists.immutable.with(), sublist2);

        sublist2.add("J");
        assertIterablesEqual(Lists.immutable.with("A", "B", "C", "J", "F"), list);
        assertIterablesEqual(Lists.immutable.with("J", "F"), sublist);
        assertIterablesEqual(Lists.immutable.with("J"), sublist2);
    }
}
