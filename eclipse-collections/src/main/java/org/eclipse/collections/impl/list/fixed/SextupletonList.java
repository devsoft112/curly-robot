/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.list.fixed;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.impl.block.factory.Comparators;

/**
 * This is a six element immutable List which is created by calling Lists.fixedSize.of(one, two, three, four, five, six).
 */
final class SextupletonList<T>
        extends AbstractMemoryEfficientMutableList<T>
        implements Externalizable
{
    private static final long serialVersionUID = 1L;

    private T element1;
    private T element2;
    private T element3;
    private T element4;
    private T element5;
    private T element6;

    @SuppressWarnings("UnusedDeclaration")
    public SextupletonList()
    {
        // For Externalizable use only
    }

    SextupletonList(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6)
    {
        this.element1 = obj1;
        this.element2 = obj2;
        this.element3 = obj3;
        this.element4 = obj4;
        this.element5 = obj5;
        this.element6 = obj6;
    }

    @Override
    public ArrayAdapter<T> with(T value)
    {
        return ArrayAdapter.newArrayWith(
                this.element1,
                this.element2,
                this.element3,
                this.element4,
                this.element5,
                this.element6,
                value);
    }

    // Weird implementation of clone() is ok on final classes

    @Override
    public SextupletonList<T> clone()
    {
        return new SextupletonList<>(
                this.element1,
                this.element2,
                this.element3,
                this.element4,
                this.element5,
                this.element6);
    }

    @Override
    public int size()
    {
        return 6;
    }

    @Override
    public T get(int index)
    {
        switch (index)
        {
            case 0:
                return this.element1;
            case 1:
                return this.element2;
            case 2:
                return this.element3;
            case 3:
                return this.element4;
            case 4:
                return this.element5;
            case 5:
                return this.element6;
            default:
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size());
        }
    }

    @Override
    public boolean contains(Object obj)
    {
        return Comparators.nullSafeEquals(obj, this.element1)
                || Comparators.nullSafeEquals(obj, this.element2)
                || Comparators.nullSafeEquals(obj, this.element3)
                || Comparators.nullSafeEquals(obj, this.element4)
                || Comparators.nullSafeEquals(obj, this.element5)
                || Comparators.nullSafeEquals(obj, this.element6);
    }

    /**
     * set is implemented purely to allow the List to be sorted, not because this List should be considered mutable.
     */
    @Override
    public T set(int index, T element)
    {
        switch (index)
        {
            case 0:
                T previousElement1 = this.element1;
                this.element1 = element;
                return previousElement1;
            case 1:
                T previousElement2 = this.element2;
                this.element2 = element;
                return previousElement2;
            case 2:
                T previousElement3 = this.element3;
                this.element3 = element;
                return previousElement3;
            case 3:
                T previousElement4 = this.element4;
                this.element4 = element;
                return previousElement4;
            case 4:
                T previousElement5 = this.element5;
                this.element5 = element;
                return previousElement5;
            case 5:
                T previousElement6 = this.element6;
                this.element6 = element;
                return previousElement6;
            default:
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size());
        }
    }

    @Override
    public T getFirst()
    {
        return this.element1;
    }

    @Override
    public T getLast()
    {
        return this.element6;
    }

    @Override
    public T getOnly()
    {
        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }

    @Override
    public void each(Procedure<? super T> procedure)
    {
        procedure.value(this.element1);
        procedure.value(this.element2);
        procedure.value(this.element3);
        procedure.value(this.element4);
        procedure.value(this.element5);
        procedure.value(this.element6);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        objectIntProcedure.value(this.element1, 0);
        objectIntProcedure.value(this.element2, 1);
        objectIntProcedure.value(this.element3, 2);
        objectIntProcedure.value(this.element4, 3);
        objectIntProcedure.value(this.element5, 4);
        objectIntProcedure.value(this.element6, 5);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        procedure.value(this.element1, parameter);
        procedure.value(this.element2, parameter);
        procedure.value(this.element3, parameter);
        procedure.value(this.element4, parameter);
        procedure.value(this.element5, parameter);
        procedure.value(this.element6, parameter);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.element1);
        out.writeObject(this.element2);
        out.writeObject(this.element3);
        out.writeObject(this.element4);
        out.writeObject(this.element5);
        out.writeObject(this.element6);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.element1 = (T) in.readObject();
        this.element2 = (T) in.readObject();
        this.element3 = (T) in.readObject();
        this.element4 = (T) in.readObject();
        this.element5 = (T) in.readObject();
        this.element6 = (T) in.readObject();
    }
}
