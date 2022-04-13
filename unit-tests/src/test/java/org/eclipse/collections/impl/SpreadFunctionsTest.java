/*
 * Copyright (c) 2022 The Bank of New York Mellon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl;

import org.junit.Assert;
import org.junit.Test;

public class SpreadFunctionsTest
{
    @Test
    public void doubleSpreadOne()
    {
        Assert.assertEquals(-7831749829746778771L, SpreadFunctions.doubleSpreadOne(25.0d));
        Assert.assertEquals(3020681792480265713L, SpreadFunctions.doubleSpreadOne(-25.0d));
    }

    @Test
    public void doubleSpreadTwo()
    {
        Assert.assertEquals(-7260239113076190123L, SpreadFunctions.doubleSpreadTwo(25.0d));
        Assert.assertEquals(-2923962723742798781L, SpreadFunctions.doubleSpreadTwo(-25.0d));
    }

    @Test
    public void longSpreadOne()
    {
        Assert.assertEquals(7972739338299824895L, SpreadFunctions.longSpreadOne(12345L));
        Assert.assertEquals(5629574755565220972L, SpreadFunctions.longSpreadOne(23456L));
    }

    @Test
    public void longSpreadTwo()
    {
        Assert.assertEquals(-3823225069572514692L, SpreadFunctions.longSpreadTwo(12345L));
        Assert.assertEquals(7979914854381881740L, SpreadFunctions.longSpreadTwo(23456L));
    }

    @Test
    public void intSpreadOne()
    {
        Assert.assertEquals(-540084185L, SpreadFunctions.intSpreadOne(100));
        Assert.assertEquals(1432552655L, SpreadFunctions.intSpreadOne(101));
    }

    @Test
    public void intSpreadTwo()
    {
        Assert.assertEquals(961801704L, SpreadFunctions.intSpreadTwo(100));
        Assert.assertEquals(662527578L, SpreadFunctions.intSpreadTwo(101));
    }

    @Test
    public void floatSpreadOne()
    {
        Assert.assertEquals(-1053442875L, SpreadFunctions.floatSpreadOne(9876.0F));
        Assert.assertEquals(-640291382L, SpreadFunctions.floatSpreadOne(-9876.0F));
    }

    @Test
    public void floatSpreadTwo()
    {
        Assert.assertEquals(-1971373820L, SpreadFunctions.floatSpreadTwo(9876.0F));
        Assert.assertEquals(-1720924552L, SpreadFunctions.floatSpreadTwo(-9876.0F));
    }

    @Test
    public void shortSpreadOne()
    {
        Assert.assertEquals(-1526665035L, SpreadFunctions.shortSpreadOne((short) 123));
        Assert.assertEquals(-1120388305L, SpreadFunctions.shortSpreadOne((short) 234));
    }

    @Test
    public void shortSpreadTwo()
    {
        Assert.assertEquals(-474242978L, SpreadFunctions.shortSpreadTwo((short) 123));
        Assert.assertEquals(-1572485272L, SpreadFunctions.shortSpreadTwo((short) 234));
    }
}
