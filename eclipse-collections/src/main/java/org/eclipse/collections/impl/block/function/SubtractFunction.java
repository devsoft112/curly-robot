/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.function;

import org.eclipse.collections.api.block.function.Function2;

/**
 * Provides static Function2s which can be used by Iterate.injectInto for adding primitives or to a collection
 */
public final class SubtractFunction
{
    public static final Function2<Integer, Integer, Integer> INTEGER = new SubtractIntegerFunction();
    public static final Function2<Double, Double, Double> DOUBLE = new SubtractDoubleFunction();
    public static final Function2<Long, Long, Long> LONG = new SubtractLongFunction();

    private SubtractFunction()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    private static class SubtractIntegerFunction implements Function2<Integer, Integer, Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Integer value(Integer argument1, Integer argument2)
        {
            return argument1 - argument2;
        }
    }

    private static class SubtractDoubleFunction implements Function2<Double, Double, Double>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Double value(Double argument1, Double argument2)
        {
            return argument1 - argument2;
        }
    }

    private static class SubtractLongFunction implements Function2<Long, Long, Long>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Long value(Long argument1, Long argument2)
        {
            return argument1 - argument2;
        }
    }
}
