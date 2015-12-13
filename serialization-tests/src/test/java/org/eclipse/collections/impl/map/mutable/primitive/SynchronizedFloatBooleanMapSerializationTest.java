/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.mutable.primitive;

import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

public class SynchronizedFloatBooleanMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAE5vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5tdXRhYmxlLnByaW1pdGl2\n"
                        + "ZS5TeW5jaHJvbml6ZWRGbG9hdEJvb2xlYW5NYXAAAAAAAAAAAQIAAkwABGxvY2t0ABJMamF2YS9s\n"
                        + "YW5nL09iamVjdDtMAANtYXB0AEJMb3JnL2VjbGlwc2UvY29sbGVjdGlvbnMvYXBpL21hcC9wcmlt\n"
                        + "aXRpdmUvTXV0YWJsZUZsb2F0Qm9vbGVhbk1hcDt4cHEAfgADc3IARm9yZy5lY2xpcHNlLmNvbGxl\n"
                        + "Y3Rpb25zLmltcGwubWFwLm11dGFibGUucHJpbWl0aXZlLkZsb2F0Qm9vbGVhbkhhc2hNYXAAAAAA\n"
                        + "AAAAAQwAAHhwdwgAAAAAPwAAAHg=",
                new SynchronizedFloatBooleanMap(new FloatBooleanHashMap()));
    }
}
