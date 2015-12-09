/*******************************************************************************
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

package org.eclipse.collections.impl.block.procedure;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.utility.Iterate;
import org.junit.Assert;
import org.junit.Test;

public class CaseProcedureTest
{
    @Test
    public void procedure()
    {
        MutableList<String> ifOneList = Lists.mutable.of();
        MutableList<String> defaultList = Lists.mutable.of();
        MutableList<String> list = FastList.newListWith("1", "2");
        Iterate.forEach(list, new CaseProcedure<String>(defaultList::add).addCase("1"::equals, ifOneList::add));
        Assert.assertEquals(FastList.newListWith("1"), ifOneList);
        Assert.assertEquals(FastList.newListWith("2"), defaultList);
    }
}
