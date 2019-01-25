/*
 * Copyright (c) 2019 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.stack.mutable;

import java.util.stream.Stream;

import org.eclipse.collections.api.factory.stack.MutableStackFactory;
import org.eclipse.collections.api.stack.MutableStack;

public class MutableStackFactoryImpl implements MutableStackFactory
{
    public static final MutableStackFactory INSTANCE = new MutableStackFactoryImpl();

    @Override
    public <T> MutableStack<T> empty()
    {
        return ArrayStack.newStack();
    }

    @Override
    public <T> MutableStack<T> with(T... elements)
    {
        return ArrayStack.newStackWith(elements);
    }

    @Override
    public <T> MutableStack<T> withAll(Iterable<? extends T> elements)
    {
        return ArrayStack.newStack(elements);
    }

    @Override
    public <T> MutableStack<T> fromStream(Stream<? extends T> stream)
    {
        ArrayStack<T> stack = ArrayStack.newStack();
        stream.forEach(stack::push);
        return stack;
    }

    @Override
    public <T> MutableStack<T> withReversed(T... elements)
    {
        return ArrayStack.newStackFromTopToBottom(elements);
    }

    @Override
    public <T> MutableStack<T> withAllReversed(Iterable<? extends T> items)
    {
        return ArrayStack.newStackFromTopToBottom(items);
    }
}
