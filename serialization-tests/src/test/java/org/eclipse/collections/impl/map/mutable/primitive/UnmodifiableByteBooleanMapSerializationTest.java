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

public class UnmodifiableByteBooleanMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAE1vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5tdXRhYmxlLnByaW1pdGl2\n"
                        + "ZS5Vbm1vZGlmaWFibGVCeXRlQm9vbGVhbk1hcAAAAAAAAAABAgABTAADbWFwdABBTG9yZy9lY2xp\n"
                        + "cHNlL2NvbGxlY3Rpb25zL2FwaS9tYXAvcHJpbWl0aXZlL011dGFibGVCeXRlQm9vbGVhbk1hcDt4\n"
                        + "cHNyAEVvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5tdXRhYmxlLnByaW1pdGl2ZS5C\n"
                        + "eXRlQm9vbGVhbkhhc2hNYXAAAAAAAAAAAQwAAHhwdwgAAAAAPwAAAHg=",
                new UnmodifiableByteBooleanMap(new ByteBooleanHashMap()));
    }

    @Test
    public void keySetSerializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAEZvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLnNldC5tdXRhYmxlLnByaW1pdGl2\n"
                        + "ZS5Vbm1vZGlmaWFibGVCeXRlU2V0AAAAAAAAAAECAAB4cgBcb3JnLmVjbGlwc2UuY29sbGVjdGlv\n"
                        + "bnMuaW1wbC5jb2xsZWN0aW9uLm11dGFibGUucHJpbWl0aXZlLkFic3RyYWN0VW5tb2RpZmlhYmxl\n"
                        + "Qnl0ZUNvbGxlY3Rpb24AAAAAAAAAAQIAAUwACmNvbGxlY3Rpb250AEhMb3JnL2VjbGlwc2UvY29s\n"
                        + "bGVjdGlvbnMvYXBpL2NvbGxlY3Rpb24vcHJpbWl0aXZlL011dGFibGVCeXRlQ29sbGVjdGlvbjt4\n"
                        + "cHNyAFNvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLm1hcC5tdXRhYmxlLnByaW1pdGl2ZS5B\n"
                        + "YnN0cmFjdE11dGFibGVCeXRlS2V5U2V0JFNlclJlcAAAAAAAAAABDAAAeHB3BAAAAAB4",
                new UnmodifiableByteBooleanMap(new ByteBooleanHashMap()).keySet());
    }
}
