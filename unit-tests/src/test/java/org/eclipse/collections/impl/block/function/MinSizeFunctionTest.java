/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.function;

import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.Assert;
import org.junit.Test;

/**
 * Junit test for {@link MinSizeFunction}.
 */
public class MinSizeFunctionTest
{
    @Test
    public void minSizeCollection()
    {
        Assert.assertEquals(Integer.valueOf(2), MinSizeFunction.COLLECTION.value(2, FastList.newListWith(1, 2, 3)));
        Assert.assertEquals(Integer.valueOf(2), MinSizeFunction.COLLECTION.value(3, FastList.newListWith(1, 2)));
    }

    @Test
    public void minSizeMap()
    {
        Assert.assertEquals(Integer.valueOf(2), MinSizeFunction.MAP.value(2, Maps.mutable.of(1, 1, 2, 2, 3, 3)));
        Assert.assertEquals(Integer.valueOf(2), MinSizeFunction.MAP.value(3, Maps.mutable.of(1, 1, 2, 2)));
    }
}
