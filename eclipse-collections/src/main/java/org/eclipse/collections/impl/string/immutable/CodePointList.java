/*
 * Copyright (c) 2017 Goldman Sachs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.string.immutable;

import java.io.IOException;
import java.io.Serializable;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LazyIntIterable;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.block.function.primitive.IntToIntFunction;
import org.eclipse.collections.api.block.function.primitive.IntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntIntToObjectFunction;
import org.eclipse.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.block.procedure.primitive.IntIntProcedure;
import org.eclipse.collections.api.block.procedure.primitive.IntProcedure;
import org.eclipse.collections.api.iterator.IntIterator;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.IntObjectPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.primitive.AbstractIntIterable;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

/**
 * Calculates and provides the code points stored in a String as an ImmutableIntList.  This is a cleaner more OO way of
 * providing many of the iterable protocols available in StringIterate for code points.
 *
 * @since 7.0
 */
public class CodePointList extends AbstractIntIterable implements CharSequence, ImmutableIntList, Serializable
{
    private static final long serialVersionUID = 2L;

    private final ImmutableIntList codePoints;

    public CodePointList(String value)
    {
        int stringSize = value.length();
        IntArrayList list = new IntArrayList(stringSize);
        for (int i = 0; i < stringSize; )
        {
            int codePoint = value.codePointAt(i);
            i += Character.charCount(codePoint);
            list.add(codePoint);
        }
        this.codePoints = list.toImmutable();
    }

    private CodePointList(ImmutableIntList points)
    {
        this.codePoints = points;
    }

    private CodePointList(int... codePoints)
    {
        this.codePoints = IntLists.immutable.with(codePoints);
    }

    public static CodePointList from(String value)
    {
        return new CodePointList(value);
    }

    public static CodePointList from(int... codePoints)
    {
        return new CodePointList(codePoints);
    }

    public static CodePointList from(IntIterable iterable)
    {
        if (iterable instanceof ImmutableIntList)
        {
            return new CodePointList((ImmutableIntList) iterable);
        }
        return new CodePointList(iterable.toArray());
    }

    public StringBuilder toStringBuilder()
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.size(); i++)
        {
            builder.appendCodePoint(this.get(i));
        }
        return builder;
    }

    @Override
    public char charAt(int index)
    {
        int currentIndex = 0;
        for (int i = 0; i < this.codePoints.size(); i++)
        {
            int codePoint = this.codePoints.get(i);
            int charCount = Character.charCount(codePoint);
            if (index < currentIndex + charCount)
            {
                if (charCount == 1)
                {
                    return (char) codePoint;
                }
                if (index == currentIndex)
                {
                    return Character.highSurrogate(codePoint);
                }
                return Character.lowSurrogate(codePoint);
            }
            currentIndex += charCount;
        }
        throw new IndexOutOfBoundsException("Char value at " + index + " is out of bounds for length " + currentIndex);
    }

    @Override
    public int length()
    {
        int length = 0;
        for (int i = 0; i < this.codePoints.size(); i++)
        {
            length += Character.charCount(this.codePoints.get(i));
        }
        return length;
    }

    @Override
    public String subSequence(int start, int end)
    {
        StringBuilder builder = this.toStringBuilder();
        return builder.substring(start, end);
    }

    /**
     * The value of toString must be strictly implemented as defined in CharSequence.
     */
    @Override
    public String toString()
    {
        return this.toStringBuilder().toString();
    }

    @Override
    public IntIterator intIterator()
    {
        return this.codePoints.intIterator();
    }

    @Override
    public int[] toArray()
    {
        return this.codePoints.toArray();
    }

    @Override
    public boolean contains(int expected)
    {
        return this.codePoints.contains(expected);
    }

    @Override
    public void forEach(IntProcedure procedure)
    {
        this.each(procedure);
    }

    @Override
    public void each(IntProcedure procedure)
    {
        this.codePoints.each(procedure);
    }

    @Override
    public CodePointList distinct()
    {
        return new CodePointList(this.codePoints.distinct());
    }

    @Override
    public CodePointList newWith(int element)
    {
        return new CodePointList(this.codePoints.newWith(element));
    }

    @Override
    public CodePointList newWithout(int element)
    {
        return new CodePointList(this.codePoints.newWithout(element));
    }

    @Override
    public CodePointList newWithAll(IntIterable elements)
    {
        return new CodePointList(this.codePoints.newWithAll(elements));
    }

    @Override
    public CodePointList newWithoutAll(IntIterable elements)
    {
        return new CodePointList(this.codePoints.newWithoutAll(elements));
    }

    @Override
    public CodePointList toReversed()
    {
        return new CodePointList(this.codePoints.toReversed());
    }

    @Override
    public ImmutableIntList subList(int fromIndex, int toIndex)
    {
        return this.codePoints.subList(fromIndex, toIndex);
    }

    @Override
    public int get(int index)
    {
        return this.codePoints.get(index);
    }

    @Override
    public long dotProduct(IntList list)
    {
        return this.codePoints.dotProduct(list);
    }

    @Override
    public int binarySearch(int value)
    {
        return this.codePoints.binarySearch(value);
    }

    @Override
    public int lastIndexOf(int value)
    {
        return this.codePoints.lastIndexOf(value);
    }

    @Override
    public ImmutableIntList toImmutable()
    {
        return this;
    }

    @Override
    public int getLast()
    {
        return this.codePoints.getLast();
    }

    @Override
    public LazyIntIterable asReversed()
    {
        return this.codePoints.asReversed();
    }

    @Override
    public <T> T injectIntoWithIndex(T injectedValue, ObjectIntIntToObjectFunction<? super T, ? extends T> function)
    {
        return this.codePoints.injectIntoWithIndex(injectedValue, function);
    }

    @Override
    public int getFirst()
    {
        return this.codePoints.getFirst();
    }

    @Override
    public int indexOf(int value)
    {
        return this.codePoints.indexOf(value);
    }

    @Override
    public void forEachWithIndex(IntIntProcedure procedure)
    {
        this.codePoints.forEachWithIndex(procedure);
    }

    @Override
    public CodePointList select(IntPredicate predicate)
    {
        return new CodePointList(this.codePoints.select(predicate));
    }

    @Override
    public CodePointList reject(IntPredicate predicate)
    {
        return new CodePointList(this.codePoints.reject(predicate));
    }

    @Override
    public <V> ImmutableList<V> collect(IntToObjectFunction<? extends V> function)
    {
        return this.codePoints.collect(function);
    }

    public CodePointList collectInt(IntToIntFunction function)
    {
        IntArrayList collected = new IntArrayList(this.size());
        for (int i = 0; i < this.size(); i++)
        {
            int codePoint = this.get(i);
            collected.add(function.valueOf(codePoint));
        }
        return new CodePointList(collected.toImmutable());
    }

    @Override
    public int detectIfNone(IntPredicate predicate, int ifNone)
    {
        return this.codePoints.detectIfNone(predicate, ifNone);
    }

    @Override
    public int count(IntPredicate predicate)
    {
        return this.codePoints.count(predicate);
    }

    @Override
    public boolean anySatisfy(IntPredicate predicate)
    {
        return this.codePoints.anySatisfy(predicate);
    }

    @Override
    public boolean allSatisfy(IntPredicate predicate)
    {
        return this.codePoints.allSatisfy(predicate);
    }

    @Override
    public boolean noneSatisfy(IntPredicate predicate)
    {
        return this.codePoints.noneSatisfy(predicate);
    }

    @Override
    public MutableIntList toList()
    {
        return this.codePoints.toList();
    }

    @Override
    public MutableIntSet toSet()
    {
        return this.codePoints.toSet();
    }

    @Override
    public MutableIntBag toBag()
    {
        return this.codePoints.toBag();
    }

    @Override
    public <T> T injectInto(T injectedValue, ObjectIntToObjectFunction<? super T, ? extends T> function)
    {
        return this.codePoints.injectInto(injectedValue, function);
    }

    @Override
    public long sum()
    {
        return this.codePoints.sum();
    }

    @Override
    public int max()
    {
        return this.codePoints.max();
    }

    @Override
    public int min()
    {
        return this.codePoints.min();
    }

    @Override
    public int size()
    {
        return this.codePoints.size();
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            int size = this.size();
            for (int i = 0; i < size; i++)
            {
                if (i > 0)
                {
                    appendable.append(separator);
                }
                int codePoint = this.get(i);
                if (appendable instanceof StringBuilder)
                {
                    ((StringBuilder) appendable).appendCodePoint(codePoint);
                }
                else if (appendable instanceof StringBuffer)
                {
                    ((StringBuffer) appendable).appendCodePoint(codePoint);
                }
                else
                {
                    char[] chars = Character.toChars(codePoint);
                    for (int j = 0; j < chars.length; j++)
                    {
                        appendable.append(chars[j]);
                    }
                }
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object otherList)
    {
        return this.codePoints.equals(otherList);
    }

    @Override
    public int hashCode()
    {
        return this.codePoints.hashCode();
    }

    /**
     * @since 9.1.
     */
    @Override
    public ImmutableList<IntIntPair> zipInt(IntList list)
    {
        MutableList<IntIntPair> target = Lists.mutable.empty();
        int size = this.size();
        int othersize = list.size();
        for (int i = 0; i < size && i < othersize; i++)
        {
            target.add(PrimitiveTuples.pair(this.get(i), list.get(i)));
        }
        return target.toImmutable();
    }

    /**
     * @since 9.1.
     */
    @Override
    public <T> ImmutableList<IntObjectPair<T>> zip(ListIterable<T> list)
    {
        MutableList<IntObjectPair<T>> target = Lists.mutable.empty();
        int size = this.size();
        int othersize = list.size();
        for (int i = 0; i < size && i < othersize; i++)
        {
            target.add(PrimitiveTuples.pair(this.get(i), list.get(i)));
        }
        return target.toImmutable();
    }
}
