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

import org.eclipse.collections.api.annotation.Beta;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.lazy.parallel.AbstractBatch;
import org.eclipse.collections.impl.lazy.parallel.Batch;

@Beta
public class CollectUnsortedBagBatch<T, V> extends AbstractBatch<V> implements UnsortedBagBatch<V>
{
    private final Batch<T> unsortedBagBatch;
    private final Function<? super T, ? extends V> function;

    public CollectUnsortedBagBatch(Batch<T> unsortedBagBatch, Function<? super T, ? extends V> function)
    {
        this.unsortedBagBatch = unsortedBagBatch;
        this.function = function;
    }

    @Override
    public void forEach(Procedure<? super V> procedure)
    {
        this.unsortedBagBatch.forEach(Functions.bind(procedure, this.function));
    }

    @Override
    public void forEachWithOccurrences(ObjectIntProcedure<? super V> procedure)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /*
    public <VV> BagBatch<VV> collect(Function<? super V, ? extends VV> function)
    {
        return new CollectBagBatch<T, VV>(this.bagBatch, Functions.chain(this.function, function));
    }
    */

    @Override
    public UnsortedBagBatch<V> select(Predicate<? super V> predicate)
    {
        return new SelectUnsortedBagBatch<>(this, predicate);
    }

    @Override
    public <VV> UnsortedBagBatch<VV> collect(Function<? super V, ? extends VV> function)
    {
        return new CollectUnsortedBagBatch<>(this, function);
    }

    @Override
    public <V1> UnsortedBagBatch<V1> flatCollect(Function<? super V, ? extends Iterable<V1>> function)
    {
        return new FlatCollectUnsortedBagBatch<>(this, function);
    }
}
