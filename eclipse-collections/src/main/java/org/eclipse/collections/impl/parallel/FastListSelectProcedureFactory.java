/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.parallel;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.impl.block.procedure.FastListSelectProcedure;
import org.eclipse.collections.impl.list.mutable.FastList;

public final class FastListSelectProcedureFactory<T> implements ProcedureFactory<FastListSelectProcedure<T>>
{
    private final Predicate<? super T> predicate;
    private final int collectionSize;

    public FastListSelectProcedureFactory(Predicate<? super T> newPredicate, int newInitialCapacity)
    {
        this.predicate = newPredicate;
        this.collectionSize = newInitialCapacity;
    }

    @Override
    public FastListSelectProcedure<T> create()
    {
        return new FastListSelectProcedure<>(this.predicate, FastList.newList(this.collectionSize));
    }
}
