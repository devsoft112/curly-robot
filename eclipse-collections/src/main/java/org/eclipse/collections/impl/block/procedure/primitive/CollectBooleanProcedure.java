/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure.primitive;

import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;

/**
 * Applies a booleanFunction to an object and adds the result to a target boolean collection.
 */
public final class CollectBooleanProcedure<T> implements Procedure<T>
{
    private static final long serialVersionUID = 1L;

    private final BooleanFunction<? super T> booleanFunction;
    private final MutableBooleanCollection booleanCollection;

    public CollectBooleanProcedure(BooleanFunction<? super T> booleanFunction, MutableBooleanCollection targetCollection)
    {
        this.booleanFunction = booleanFunction;
        this.booleanCollection = targetCollection;
    }

    @Override
    public void value(T object)
    {
        boolean value = this.booleanFunction.booleanValueOf(object);
        this.booleanCollection.add(value);
    }

    public MutableBooleanCollection getBooleanCollection()
    {
        return this.booleanCollection;
    }
}
