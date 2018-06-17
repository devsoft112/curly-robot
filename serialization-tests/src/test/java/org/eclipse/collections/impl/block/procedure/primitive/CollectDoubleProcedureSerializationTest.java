/*
 * Copyright (c) 2018 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure.primitive;

import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

public class CollectDoubleProcedureSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAE1vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByb2NlZHVyZS5wcmlt\n"
                        + "aXRpdmUuQ29sbGVjdERvdWJsZVByb2NlZHVyZQAAAAAAAAABAgACTAAQZG91YmxlQ29sbGVjdGlv\n"
                        + "bnQASkxvcmcvZWNsaXBzZS9jb2xsZWN0aW9ucy9hcGkvY29sbGVjdGlvbi9wcmltaXRpdmUvTXV0\n"
                        + "YWJsZURvdWJsZUNvbGxlY3Rpb247TAAOZG91YmxlRnVuY3Rpb250AEVMb3JnL2VjbGlwc2UvY29s\n"
                        + "bGVjdGlvbnMvYXBpL2Jsb2NrL2Z1bmN0aW9uL3ByaW1pdGl2ZS9Eb3VibGVGdW5jdGlvbjt4cHBw\n",
                new CollectDoubleProcedure(null, null));
    }
}
