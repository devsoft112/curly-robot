/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api.partition.stack;

import org.eclipse.collections.api.stack.ImmutableStack;

/**
 * A PartitionImmutableStack is the result of splitting an immutable stack into two immutable stacks based on a Predicate.
 * The results that answer true for the Predicate will be returned from the getSelected() method and the results that answer
 * false for the predicate will be returned from the getRejected() method.
 */
public interface PartitionImmutableStack<T> extends PartitionStack<T>
{
    @Override
    ImmutableStack<T> getSelected();

    @Override
    ImmutableStack<T> getRejected();
}
