/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.set.fixed;

import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

public class SingletonSetSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyADNvcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLnNldC5maXhlZC5TaW5nbGV0b25T\n"
                        + "ZXQAAAAAAAAAAQwAAHhwcHg=",
                Sets.fixedSize.of(null));
    }
}
