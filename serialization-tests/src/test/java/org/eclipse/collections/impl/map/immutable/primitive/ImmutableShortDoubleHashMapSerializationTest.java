/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.immutable.primitive;

import org.eclipse.collections.impl.map.mutable.primitive.ShortDoubleHashMap;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

public class ImmutableShortDoubleHashMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAHpvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5pbW11dGFibGUucHJpbWl0\n"
                        + "aXZlLkltbXV0YWJsZVNob3J0RG91YmxlSGFzaE1hcCRJbW11dGFibGVTaG9ydERvdWJsZU1hcFNl\n"
                        + "cmlhbGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHB3GAAAAAIAAT/wAAAAAAAAAAJAAAAAAAAAAHg=\n",
                new ImmutableShortDoubleHashMap(ShortDoubleHashMap.newWithKeysValues((short) 1, 1.0, (short) 2, 2.0)));
    }
}
