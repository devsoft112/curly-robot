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

import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.procedure.Procedure;

public class InjectIntoIntProcedure<T> implements Procedure<T>
{
    private static final long serialVersionUID = 1L;

    private int result;
    private final IntObjectToIntFunction<? super T> function;

    public InjectIntoIntProcedure(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        this.result = injectedValue;
        this.function = function;
    }

    @Override
    public void value(T each)
    {
        this.result = this.function.intValueOf(this.result, each);
    }

    public int getResult()
    {
        return this.result;
    }
}
