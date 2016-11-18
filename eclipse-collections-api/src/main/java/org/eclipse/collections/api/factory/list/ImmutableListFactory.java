/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.factory.list;

import org.eclipse.collections.api.list.ImmutableList;

public interface ImmutableListFactory
{
    /**
     * @since 6.0
     */
    <T> ImmutableList<T> empty();

    /**
     * Same as {@link #empty()}.
     */
    <T> ImmutableList<T> of();

    /**
     * Same as {@link #empty()}.
     */
    <T> ImmutableList<T> with();

    /**
     * Same as {@link #with(Object)}.
     */
    <T> ImmutableList<T> of(T one);

    <T> ImmutableList<T> with(T one);

    /**
     * Same as {@link #with(Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two);

    <T> ImmutableList<T> with(T one, T two);

    /**
     * Same as {@link #with(Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three);

    <T> ImmutableList<T> with(T one, T two, T three);

    /**
     * Same as {@link #with(Object, Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three, T four);

    <T> ImmutableList<T> with(T one, T two, T three, T four);

    /**
     * Same as {@link #with(Object, Object, Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three, T four, T five);

    <T> ImmutableList<T> with(T one, T two, T three, T four, T five);

    /**
     * Same as {@link #with(Object, Object, Object, Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six);

    <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six);

    /**
     * Same as {@link #with(Object, Object, Object, Object, Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven);

    <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven);

    /**
     * Same as {@link #with(Object, Object, Object, Object, Object, Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven, T eight);

    <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven, T eight);

    /**
     * Same as {@link #with(Object, Object, Object, Object, Object, Object, Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine);

    <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine);

    /**
     * Same as {@link #with(Object, Object, Object, Object, Object, Object, Object, Object, Object, Object)}.
     */
    <T> ImmutableList<T> of(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine, T ten);

    <T> ImmutableList<T> with(T one, T two, T three, T four, T five, T six, T seven, T eight, T nine, T ten);

    /**
     * Same as {@link #with(Object[])}.
     */
    <T> ImmutableList<T> of(T... items);

    <T> ImmutableList<T> with(T... items);

    /**
     * Same as {@link #withAll(Iterable)}.
     */
    <T> ImmutableList<T> ofAll(Iterable<? extends T> items);

    <T> ImmutableList<T> withAll(Iterable<? extends T> items);
}
