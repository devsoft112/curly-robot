/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure.checked.primitive;

import org.eclipse.collections.impl.test.Verify;
import org.junit.Test;

public class CheckedObjectFloatProcedureSerializationTest
{
    private static final CheckedObjectFloatProcedure<?> CHECKED_OBJECT_FLOAT_PROCEDURE = new CheckedObjectFloatProcedure<Object>()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void safeValue(Object item1, float item2) throws Exception
        {
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAG1vcmcuZWNsaXBzZS5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByb2NlZHVyZS5jaGVj\n"
                        + "a2VkLnByaW1pdGl2ZS5DaGVja2VkT2JqZWN0RmxvYXRQcm9jZWR1cmVTZXJpYWxpemF0aW9uVGVz\n"
                        + "dCQxAAAAAAAAAAECAAB4cgBab3JnLmVjbGlwc2UuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcm9j\n"
                        + "ZWR1cmUuY2hlY2tlZC5wcmltaXRpdmUuQ2hlY2tlZE9iamVjdEZsb2F0UHJvY2VkdXJlAAAAAAAA\n"
                        + "AAECAAB4cA==",
                CHECKED_OBJECT_FLOAT_PROCEDURE);
    }
}
