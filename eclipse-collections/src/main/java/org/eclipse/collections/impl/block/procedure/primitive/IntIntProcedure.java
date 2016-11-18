/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.procedure.primitive;

import java.io.Serializable;

/**
 * An IntIntProcedure is a two argument Closure which has no return argument and takes an int as the first and
 * second arguments.
 *
 * @deprecated since 3.0 use {@link org.eclipse.collections.api.block.procedure.primitive.IntIntProcedure}
 */
@Deprecated
public interface IntIntProcedure extends Serializable
{
    void value(int each, int index);
}
