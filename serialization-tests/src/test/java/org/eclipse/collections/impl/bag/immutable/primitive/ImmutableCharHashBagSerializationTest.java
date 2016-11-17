/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.bag.immutable.primitive;

import org.eclipse.collections.impl.test.SerializeTestHelper;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableCharHashBagSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAGxvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJhZy5pbW11dGFibGUucHJpbWl0\n"
                        + "aXZlLkltbXV0YWJsZUNoYXJIYXNoQmFnJEltbXV0YWJsZUNoYXJCYWdTZXJpYWxpemF0aW9uUHJv\n"
                        + "eHkAAAAAAAAAAQwAAHhwdxAAAAACAGEAAAABAGIAAAABeA==",
                ImmutableCharHashBag.newBagWith('a', 'b'));
    }

    @Test
    public void deserialize()
    {
        ImmutableCharHashBag immutableCharHashBag = SerializeTestHelper.serializeDeserialize(ImmutableCharHashBag.newBagWith('a', 'b'));
        Assert.assertEquals(ImmutableCharHashBag.newBagWith('a', 'b'), immutableCharHashBag);
    }

    @Test
    public void decode()
    {
        Verify.assertDeserializedForm(
                "rO0ABXNyAGxvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJhZy5pbW11dGFibGUucHJpbWl0\n"
                        + "aXZlLkltbXV0YWJsZUNoYXJIYXNoQmFnJEltbXV0YWJsZUNoYXJCYWdTZXJpYWxpemF0aW9uUHJv\n"
                        + "eHkAAAAAAAAAAQwAAHhwdxAAAAACAGEAAAABAGIAAAABeA==",
                ImmutableCharHashBag.newBagWith('a', 'b'));
    }
}
