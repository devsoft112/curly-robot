/*
 * Copyright (c) 2021 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.tuple.primitive;

import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit test for {@link ObjectBooleanPairImpl}.
 */
public class ObjectBooleanPairImplTest
{
    @Test
    public void testEqualsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(PrimitiveTuples.pair("true", false), PrimitiveTuples.pair("true", false));
        assertNotEquals(PrimitiveTuples.pair("false", true), PrimitiveTuples.pair("true", false));
        assertEquals(Tuples.pair("true", false).hashCode(), PrimitiveTuples.pair("true", false).hashCode());
    }

    @Test
    public void getOne()
    {
        assertEquals("true", PrimitiveTuples.pair("true", false).getOne());
        assertEquals("false", PrimitiveTuples.pair("false", true).getOne());
    }

    @Test
    public void getTwo()
    {
        assertTrue(PrimitiveTuples.pair("false", true).getTwo());
        assertFalse(PrimitiveTuples.pair("true", false).getTwo());
    }

    @Test
    public void testToString()
    {
        assertEquals("true:false", PrimitiveTuples.pair("true", false).toString());
        assertEquals("true:true", PrimitiveTuples.pair("true", true).toString());
    }

    @Test
    public void compareTo()
    {
        assertEquals(1, PrimitiveTuples.pair("true", true).compareTo(PrimitiveTuples.pair("true", false)));
        assertEquals(0, PrimitiveTuples.pair("true", false).compareTo(PrimitiveTuples.pair("true", false)));
        assertEquals(-1, PrimitiveTuples.pair("true", false).compareTo(PrimitiveTuples.pair("true", true)));
    }
}
