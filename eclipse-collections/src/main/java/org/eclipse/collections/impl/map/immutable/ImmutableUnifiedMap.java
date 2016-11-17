/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.Immutable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collection.mutable.UnmodifiableMutableCollection;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.set.mutable.UnmodifiableMutableSet;

@Immutable
/**
 * @see ImmutableMap
 */
public class ImmutableUnifiedMap<K, V>
        extends AbstractImmutableMap<K, V> implements BatchIterable<V>, Serializable
{
    private static final long serialVersionUID = 1L;
    private final UnifiedMap<K, V> delegate;

    public ImmutableUnifiedMap(Map<K, V> delegate)
    {
        this.delegate = UnifiedMap.newMap(delegate);
    }

    public ImmutableUnifiedMap(Pair<K, V>... pairs)
    {
        this(UnifiedMap.newMapWith(pairs));
    }

    @Override
    public boolean equals(Object o)
    {
        return this.delegate.equals(o);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    @Override
    public String toString()
    {
        return this.delegate.toString();
    }

    @Override
    public int size()
    {
        return this.delegate.size();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return this.delegate.containsValue(value);
    }

    @Override
    public V get(Object key)
    {
        return this.delegate.get(key);
    }

    @Override
    public int getBatchCount(int batchSize)
    {
        return this.delegate.getBatchCount(batchSize);
    }

    @Override
    public void batchForEach(Procedure<? super V> procedure, int sectionIndex, int sectionCount)
    {
        this.delegate.batchForEach(procedure, sectionIndex, sectionCount);
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        this.delegate.forEachValue(procedure);
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        this.delegate.forEachKey(procedure);
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        this.delegate.forEachKeyValue(procedure);
    }

    @Override
    public Set<K> keySet()
    {
        return UnmodifiableMutableSet.of(this.delegate.keySet());
    }

    @Override
    public Collection<V> values()
    {
        return UnmodifiableMutableCollection.of(this.delegate.values());
    }

    @Override
    public RichIterable<K> keysView()
    {
        return this.delegate.keysView();
    }

    @Override
    public RichIterable<V> valuesView()
    {
        return this.delegate.valuesView();
    }

    @Override
    public RichIterable<Pair<K, V>> keyValuesView()
    {
        return this.delegate.keyValuesView();
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        this.delegate.forEachWithIndex(objectIntProcedure);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        this.delegate.forEachWith(procedure, parameter);
    }

    protected Object writeReplace()
    {
        return new ImmutableMapSerializationProxy<>(this);
    }

    @Override
    public <A> A ifPresentApply(K key, Function<? super V, ? extends A> function)
    {
        return this.delegate.ifPresentApply(key, function);
    }

    @Override
    public V getIfAbsent(K key, Function0<? extends V> function)
    {
        return this.delegate.getIfAbsent(key, function);
    }

    @Override
    public V getIfAbsentValue(K key, V value)
    {
        return this.delegate.getIfAbsentValue(key, value);
    }

    @Override
    public <P> V getIfAbsentWith(
            K key,
            Function<? super P, ? extends V> function,
            P parameter)
    {
        return this.delegate.getIfAbsentWith(key, function, parameter);
    }
}
