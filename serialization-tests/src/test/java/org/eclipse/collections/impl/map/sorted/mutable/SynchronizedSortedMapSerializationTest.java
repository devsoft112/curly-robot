/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.sorted.mutable;

import org.eclipse.collections.impl.factory.SortedMaps;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

public class SynchronizedSortedMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                2L,
                "rO0ABXNyAEpvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5tdXRhYmxlLlN5bmNocm9u\n"
                        + "aXplZE1hcFNlcmlhbGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHBzcgA9b3JnLmVjbGlwc2UuY29s\n"
                        + "bGVjdGlvbnMuaW1wbC5tYXAuc29ydGVkLm11dGFibGUuVHJlZVNvcnRlZE1hcAAAAAAAAAABDAAA\n"
                        + "eHBwdwQAAAAAeHg=",
                SynchronizedSortedMap.of(SortedMaps.mutable.of()));
    }
}
