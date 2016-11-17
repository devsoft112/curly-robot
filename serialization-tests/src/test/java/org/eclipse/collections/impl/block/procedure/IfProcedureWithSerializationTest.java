/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure;

import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

public class IfProcedureWithSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyADxvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByb2NlZHVyZS5JZlBy\n"
                        + "b2NlZHVyZVdpdGgAAAAAAAAAAQIAAkwACXByZWRpY2F0ZXQAN0xvcmcvZWNsaXBzZS9jb2xsZWN0\n"
                        + "aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL1ByZWRpY2F0ZTtMAAlwcm9jZWR1cmV0ADhMb3JnL2Vj\n"
                        + "bGlwc2UvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3Byb2NlZHVyZS9Qcm9jZWR1cmUyO3hwcHA=",
                new IfProcedureWith<>(null, null));
    }
}
