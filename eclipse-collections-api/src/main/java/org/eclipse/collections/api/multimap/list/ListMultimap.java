/*******************************************************************************
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

package org.eclipse.collections.api.multimap.list;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.multimap.bag.BagMultimap;
import org.eclipse.collections.api.multimap.bag.UnsortedBagMultimap;
import org.eclipse.collections.api.multimap.ordered.ReversibleIterableMultimap;
import org.eclipse.collections.api.tuple.Pair;

public interface ListMultimap<K, V>
        extends ReversibleIterableMultimap<K, V>
{
    ListMultimap<K, V> newEmpty();

    ListIterable<V> get(K key);

    MutableListMultimap<K, V> toMutable();

    ImmutableListMultimap<K, V> toImmutable();

    UnsortedBagMultimap<V, K> flip();

    ListMultimap<K, V> selectKeysValues(Predicate2<? super K, ? super V> predicate);

    ListMultimap<K, V> rejectKeysValues(Predicate2<? super K, ? super V> predicate);

    ListMultimap<K, V> selectKeysMultiValues(Predicate2<? super K, ? super Iterable<V>> predicate);

    ListMultimap<K, V> rejectKeysMultiValues(Predicate2<? super K, ? super Iterable<V>> predicate);

    <K2, V2> BagMultimap<K2, V2> collectKeysValues(Function2<? super K, ? super V, Pair<K2, V2>> function);

    <V2> ListMultimap<K, V2> collectValues(Function<? super V, ? extends V2> function);
}
