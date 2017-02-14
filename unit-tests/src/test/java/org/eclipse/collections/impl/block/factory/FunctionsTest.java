/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.block.factory;

import java.io.IOException;
import java.util.Map;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.function.primitive.IntegerFunctionImpl;
import org.eclipse.collections.impl.block.function.primitive.LongFunctionImpl;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.SetAdapter;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.test.Verify;
import org.eclipse.collections.impl.test.domain.Person;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.Assert;
import org.junit.Test;

import static org.eclipse.collections.impl.factory.Iterables.iList;

public class FunctionsTest
{
    private static final Function<String, Integer> STRING_LENGTH = String::length;

    private static final Function<Integer, Boolean> IS_ODD = object -> Boolean.valueOf(object.intValue() % 2 != 0);

    private static final Function<Boolean, String> BOOLEAN_STRING = String::valueOf;

    @Test
    public void throwing()
    {
        Verify.assertThrowsWithCause(
                RuntimeException.class,
                IOException.class,
                () -> { Functions.throwing(a -> { throw new IOException(); }).valueOf(null); });
    }

    @Test
    public void throwingWithUserSpecifiedException()
    {
        Verify.assertThrowsWithCause(
                RuntimeException.class,
                IOException.class,
                () -> { Functions.throwing(
                        a -> { throw new IOException(); },
                        (each, ce) -> new RuntimeException(ce)).valueOf(null); });
        Verify.assertThrowsWithCause(
                MyRuntimeException.class,
                IOException.class,
                () -> { Functions.throwing(
                        a -> { throw new IOException(); },
                        this::throwMyException).valueOf(null); });
        Verify.assertThrows(
                NullPointerException.class,
                () -> { Functions.throwing(
                        a -> { throw new NullPointerException(); },
                        this::throwMyException).valueOf(null); });
    }

    private MyRuntimeException throwMyException(Object each, Throwable exception)
    {
        return new MyRuntimeException(String.valueOf(each), exception);
    }

    @Test
    public void getPassThru()
    {
        Object object = new Object();
        Assert.assertSame(object, Functions.getPassThru().valueOf(object));
    }

    @Test
    public void getFixedValue()
    {
        Assert.assertEquals(Integer.valueOf(5), Functions.getFixedValue(5).valueOf(null));
    }

    @Test
    public void getToClass()
    {
        Assert.assertSame(Integer.class, Functions.getToClass().valueOf(0));
    }

    @Test
    public void getMathSinFunction()
    {
        Function<Number, Double> function = Functions.getMathSinFunction();
        Assert.assertEquals(Math.sin(1.0), function.valueOf(1), 0.0);
    }

    @Test
    public void getNumberPassThru()
    {
        Function<Number, Number> function = Functions.getNumberPassThru();
        Assert.assertEquals(1, function.valueOf(1));
    }

    @Test
    public void getIntegerPassThru()
    {
        Function<Integer, Integer> function = Functions.getIntegerPassThru();
        Assert.assertEquals(Integer.valueOf(1), function.valueOf(1));
        Assert.assertEquals("IntegerPassThruFunction", function.toString());
    }

    @Test
    public void getLongPassThru()
    {
        Function<Long, Long> function = Functions.getLongPassThru();
        Assert.assertEquals(Long.valueOf(1), function.valueOf(1L));
        Assert.assertEquals(Long.valueOf(1L), Long.valueOf(((LongFunction<Long>) function).longValueOf(1L)));
        Assert.assertEquals("LongPassThruFunction", function.toString());
    }

    @Test
    public void getDoublePassThru()
    {
        Function<Double, Double> function = Functions.getDoublePassThru();
        Assert.assertEquals(Double.valueOf(1).doubleValue(), function.valueOf(1.0).doubleValue(), 0.0);
        Assert.assertEquals(Double.valueOf(1).doubleValue(), ((DoubleFunction<Double>) function).doubleValueOf(1.0), 0.0);
        Assert.assertEquals("DoublePassThruFunction", function.toString());
    }

    @Test
    public void getStringPassThru()
    {
        Function<String, String> function = Functions.getStringPassThru();
        Assert.assertEquals("hello", function.valueOf("hello"));
    }

    @Test
    public void getStringTrim()
    {
        Assert.assertEquals("hello", Functions.getStringTrim().valueOf(" hello  "));
    }

    @Test
    public void getToString()
    {
        Function<Object, String> function = Functions.getToString();
        Assert.assertEquals("1", function.valueOf(1));
        Assert.assertEquals("null", function.valueOf(null));
    }

    @Test
    public void getDefaultToString()
    {
        Function<Object, String> function = Functions.getNullSafeToString("N/A");
        Assert.assertEquals("1", function.valueOf(1));
        Assert.assertEquals("N/A", function.valueOf(null));
    }

    @Test
    public void getStringToInteger()
    {
        Function<String, Integer> function = Functions.getStringToInteger();
        Assert.assertEquals(Integer.valueOf(1), function.valueOf("1"));
    }

    @Test
    public void firstNotNullValue()
    {
        Function<Object, Integer> function1 =
                Functions.firstNotNullValue(Functions.getFixedValue(null), Functions.getFixedValue(1), Functions.getFixedValue(2));
        Assert.assertEquals(Integer.valueOf(1), function1.valueOf(null));
        Function<Object, Integer> function2 =
                Functions.firstNotNullValue(Functions.getFixedValue(null), Functions.getFixedValue(null));
        Assert.assertNull(function2.valueOf(null));
    }

    @Test
    public void firstNotEmptyStringValue()
    {
        Function<Object, String> function1 =
                Functions.firstNotEmptyStringValue(Functions.getFixedValue(""), Functions.getFixedValue("hello"), Functions.getFixedValue(""));
        Assert.assertEquals("hello", function1.valueOf(null));
        Function<Object, String> function2 =
                Functions.firstNotEmptyStringValue(Functions.getFixedValue(""), Functions.getFixedValue(""));
        Assert.assertNull(function2.valueOf(null));
    }

    @Test
    public void firstNotEmptyCollectionValue()
    {
        Function<Object, ImmutableList<String>> function1 = Functions.firstNotEmptyCollectionValue(
                Functions.getFixedValue(Lists.immutable.of()),
                Functions.getFixedValue(Lists.immutable.of("hello")),
                Functions.getFixedValue(Lists.immutable.of()));
        Assert.assertEquals(iList("hello"), function1.valueOf(null));

        Function<Object, ImmutableList<String>> function2 = Functions.firstNotEmptyCollectionValue(
                Functions.getFixedValue(Lists.immutable.of()),
                Functions.getFixedValue(Lists.immutable.of()));
        Assert.assertNull(function2.valueOf(null));
    }

    @Test
    public void ifTrue()
    {
        String result = "1";
        Assert.assertSame(result, Functions.ifTrue(Predicates.alwaysTrue(), Functions.getPassThru()).valueOf(result));
        Assert.assertNull(result, Functions.ifTrue(Predicates.alwaysFalse(), Functions.getPassThru()).valueOf(result));
    }

    @Test
    public void ifElse()
    {
        String result1 = "1";
        String result2 = "2";
        Assert.assertSame(result1, Functions.ifElse(Predicates.alwaysTrue(), Functions.getFixedValue(result1), Functions.getFixedValue(result2)).valueOf(null));
        Assert.assertSame(result2, Functions.ifElse(Predicates.alwaysFalse(), Functions.getFixedValue(result1), Functions.getFixedValue(result2)).valueOf(null));
        Verify.assertContains("IfFunction", Functions.ifElse(Predicates.alwaysTrue(), Functions.getFixedValue(result1), Functions.getFixedValue(result2)).toString());
    }

    @Test
    public void synchronizedEach()
    {
        Function<Integer, String> function = Functions.synchronizedEach(Object::toString);
        Verify.assertSetsEqual(
                UnifiedSet.newSetWith("1", "2", "3"),
                UnifiedSet.newSetWith(1, 2, 3).collect(function));
    }

    @Test
    public void chains()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Function<Object, String> toString = String::valueOf;

        Assert.assertEquals("42", Functions.chain(toInteger, toString).valueOf("42"));
        Assert.assertEquals(Integer.valueOf(42), Functions.chain(toString, toInteger).valueOf(42));

        Function<String, Integer> chain = Functions.chain(toInteger, toString).chain(toInteger);
        Assert.assertEquals(Integer.valueOf(42), chain.valueOf("42"));
        Assert.assertEquals("42", Functions.chain(toString, toInteger).chain(toString).valueOf(42));

        Assert.assertEquals("42", Functions.chain(toInteger, toString).chain(toInteger).chain(toString).valueOf("42"));
        Assert.assertEquals(Integer.valueOf(42), Functions.chain(toString, toInteger).chain(toString).chain(toInteger).valueOf(42));

        Assert.assertEquals(Integer.valueOf(42), Functions.chain(toInteger, toString).chain(toInteger).chain(toString).chain(toInteger).valueOf("42"));
        Assert.assertEquals(Integer.valueOf(42), Functions.chain(toString, toInteger).chain(toString).chain(toInteger).chain(toString).chain(toInteger).valueOf(42));
    }

    @Test
    public void chain_two()
    {
        Function<Boolean, Integer> chain = Functions.chain(BOOLEAN_STRING, STRING_LENGTH);
        Assert.assertEquals(Integer.valueOf(5), chain.valueOf(Boolean.FALSE));
    }

    @Test
    public void chain_three()
    {
        Function<String, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        Assert.assertEquals("true", chain.valueOf("foo"));
    }

    @Test
    public void chain_four()
    {
        Function<Integer, Boolean> chain = Functions.chain(IS_ODD, BOOLEAN_STRING).chain(STRING_LENGTH).chain(IS_ODD);
        Assert.assertEquals(Boolean.TRUE, chain.valueOf(Integer.valueOf(4)));
    }

    @Test
    public void chainBoolean()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Functions.BooleanFunctionChain<String, Integer> booleanFunctionChain = Functions.chainBoolean(toInteger, integerObject -> integerObject.intValue() >= 0);
        Assert.assertTrue(booleanFunctionChain.booleanValueOf("45"));
        Assert.assertFalse(booleanFunctionChain.booleanValueOf("-45"));
    }

    @Test
    public void chainByte()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Functions.ByteFunctionChain<String, Integer> byteFunctionChain = Functions.chainByte(toInteger, Integer::byteValue);
        Assert.assertEquals((byte) 45, byteFunctionChain.byteValueOf("45"));
        Assert.assertEquals((byte) -45, byteFunctionChain.byteValueOf("-45"));
    }

    @Test
    public void chainChar()
    {
        Function<Object, String> toString = String::valueOf;
        Functions.CharFunctionChain<Object, String> charFunctionChain = Functions.chainChar(toString, stringObject -> stringObject.charAt(0));
        Assert.assertEquals('e', charFunctionChain.charValueOf("example string"));
        Assert.assertEquals('-', charFunctionChain.charValueOf("-4"));
    }

    @Test
    public void chainDouble()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Functions.DoubleFunctionChain<String, Integer> doubleFunctionChain = Functions.chainDouble(toInteger, Integer::doubleValue);
        Assert.assertEquals(146.0, doubleFunctionChain.doubleValueOf("146"), 0.0);
        Assert.assertEquals(-456.0, doubleFunctionChain.doubleValueOf("-456"), 0.0);
    }

    @Test
    public void chainFloat()
    {
        Functions.FloatFunctionChain<Integer, String> floatFunctionChain = Functions.chainFloat(String::valueOf, stringObject -> Float.valueOf(stringObject).floatValue());
        Assert.assertEquals(146.0, floatFunctionChain.floatValueOf(146), 0.0);
        Assert.assertEquals(-456.0, floatFunctionChain.floatValueOf(-456), 0.0);
    }

    @Test
    public void chainInt()
    {
        Function<Float, String> toString = String::valueOf;

        IntFunction<String> stringToLength = new IntegerFunctionImpl<String>()
        {
            public int intValueOf(String stringObject)
            {
                return stringObject.length();
            }
        };
        Functions.IntFunctionChain<Float, String> intFunctionChain = Functions.chainInt(toString, stringToLength);
        Assert.assertEquals(5, intFunctionChain.intValueOf(Float.valueOf(145)));
        Assert.assertEquals(6, intFunctionChain.intValueOf(Float.valueOf(-145)));
    }

    @Test
    public void chainLong()
    {
        Function<Float, String> toString = String::valueOf;

        LongFunction<String> stringToLengthLong = stringObject -> Long.valueOf(stringObject.length()).longValue();
        Functions.LongFunctionChain<Float, String> longFunctionChain = Functions.chainLong(toString, stringToLengthLong);
        Assert.assertEquals(5L, longFunctionChain.longValueOf(Float.valueOf(145)));
        Assert.assertEquals(6L, longFunctionChain.longValueOf(Float.valueOf(-145)));
    }

    @Test
    public void chainShort()
    {
        Functions.ShortFunctionChain<Integer, String> shortFunctionChain = Functions.chainShort(String::valueOf, stringObject -> Short.valueOf(stringObject).shortValue());
        Assert.assertEquals((short) 145, shortFunctionChain.shortValueOf(145));
        Assert.assertEquals((short) -145, shortFunctionChain.shortValueOf(-145));
    }

    @Test
    public void chain_two_chainBoolean()
    {
        Functions.FunctionChain<Boolean, String, Integer> chain = Functions.chain(String::valueOf, STRING_LENGTH);
        Functions.BooleanFunctionChain<Boolean, Integer> booleanChain = chain.chainBoolean(integerObject -> integerObject.intValue() >= 0);
        Assert.assertTrue(booleanChain.booleanValueOf(Boolean.TRUE));
    }

    @Test
    public void chain_two_chainByte()
    {
        Functions.FunctionChain<Boolean, String, Integer> chain = Functions.chain(String::valueOf, STRING_LENGTH);
        Functions.ByteFunctionChain<Boolean, Integer> byteChain = chain.chainByte(Integer::byteValue);
        Assert.assertEquals((byte) 5, byteChain.byteValueOf(Boolean.FALSE));
    }

    @Test
    public void chain_three_chainChar()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        Functions.CharFunctionChain<String, String> charChain = chain.chainChar(stringObject -> stringObject.charAt(0));
        Assert.assertEquals('t', charChain.charValueOf("foo"));
    }

    @Test
    public void chain_three_chainDouble()
    {
        Functions.FunctionChain<Boolean, String, Integer> chain = Functions.chain(String::valueOf, STRING_LENGTH);
        Functions.DoubleFunctionChain<Boolean, Integer> doubleChain = chain.chainDouble(Integer::doubleValue);
        Assert.assertEquals(4.0, doubleChain.doubleValueOf(Boolean.TRUE), 0.0);
    }

    @Test
    public void chain_three_chainFloat()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        Functions.FloatFunctionChain<String, String> floatChain = chain.chainFloat(stringObject -> Integer.valueOf(stringObject.length()).floatValue());
        Assert.assertEquals(5.0, floatChain.floatValueOf("12.2"), 0);
    }

    @Test
    public void chain_three_chainInt()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        IntFunction<String> stringToLength = new IntegerFunctionImpl<String>()
        {
            public int intValueOf(String stringObject)
            {
                return stringObject.length();
            }
        };
        Functions.IntFunctionChain<String, String> intChain = chain.chainInt(stringToLength);
        Assert.assertEquals(4, intChain.intValueOf("abc"));
        Assert.assertNotEquals(4, intChain.intValueOf("kata"));
    }

    @Test
    public void chain_three_chainLong()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        LongFunction<String> stringToLengthLong = stringObject -> Long.valueOf(stringObject.length()).longValue();
        Functions.LongFunctionChain<String, String> longChain = chain.chainLong(stringToLengthLong);
        Assert.assertEquals(4L, longChain.longValueOf("abc"));
        Assert.assertNotEquals(4L, longChain.longValueOf("kata"));
    }

    @Test
    public void chain_three_chainShort()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        ShortFunction<String> stringToShort = stringObject -> Integer.valueOf(stringObject.length()).shortValue();
        Functions.ShortFunctionChain<String, String> shortChain = chain.chainShort(stringToShort);
        Assert.assertEquals((short) 4, shortChain.shortValueOf("abc"));
        Assert.assertNotEquals((short) 4, shortChain.shortValueOf("kata"));
    }

    @Test
    public void intValueFunctionToComparator()
    {
        MutableList<Integer> list = Interval.oneTo(100).toList().shuffleThis();
        Function<Integer, Integer> function = Integer::intValue;
        list.sortThis(Comparators.byFunction(function));
        Assert.assertEquals(Interval.oneTo(100).toList(), list);
    }

    @Test
    public void doubleValueFunctionToComparator()
    {
        MutableList<Double> list = FastList.newListWith(5.0, 4.0, 3.0, 2.0, 1.0).shuffleThis();
        Function<Double, Double> function = Double::doubleValue;
        list.sortThis(Comparators.byFunction(function));
        Assert.assertEquals(FastList.newListWith(1.0, 2.0, 3.0, 4.0, 5.0), list);
    }

    @Test
    public void longValueFunctionToComparator()
    {
        MutableList<Long> list = FastList.newListWith(5L, 4L, 3L, 2L, 1L).shuffleThis();
        list.sortThis(Comparators.byFunction(new LongFunctionImpl<Long>()
        {
            public long longValueOf(Long each)
            {
                return each.longValue();
            }
        }));
        Assert.assertEquals(FastList.newListWith(1L, 2L, 3L, 4L, 5L), list);
    }

    @Test
    public void classFunctionToString()
    {
        Assert.assertEquals("object.getClass()", Functions.getToClass().toString());
    }

    @Test
    public void mathSinToString()
    {
        Assert.assertEquals("Math.sin()", Functions.getMathSinFunction().toString());
    }

    @Test
    public void mathStringToIntegerToString()
    {
        Assert.assertEquals("stringToInteger", Functions.getStringToInteger().toString());
    }

    @Test
    public void pair()
    {
        Person john = new Person("John", "Smith");
        Person jane = new Person("Jane", "Smith");
        Person johnDoe = new Person("John", "Doe");
        MutableList<Person> people = FastList.newListWith(john, jane, johnDoe);
        MutableList<Person> sorted = people.sortThisBy(Functions.pair(Person.TO_LAST, Person.TO_FIRST));
        Assert.assertEquals(FastList.newListWith(johnDoe, jane, john), sorted);
    }

    @Test
    public void key()
    {
        MutableMap<String, Integer> map = UnifiedMap.newWithKeysValues("One", 1);
        MutableSet<Map.Entry<String, Integer>> entries = SetAdapter.adapt(map.entrySet());
        MutableSet<String> keys = entries.collect(Functions.getKeyFunction());
        Assert.assertEquals(UnifiedSet.newSetWith("One"), keys);
    }

    @Test
    public void value()
    {
        MutableMap<String, Integer> map = UnifiedMap.newWithKeysValues("One", 1);
        MutableSet<Map.Entry<String, Integer>> entries = SetAdapter.adapt(map.entrySet());
        MutableSet<Integer> values = entries.collect(Functions.getValueFunction());
        Assert.assertEquals(UnifiedSet.newSetWith(1), values);
    }

    @Test
    public void size()
    {
        ImmutableList<ImmutableList<Integer>> list = Lists.immutable.of(Lists.immutable.of(1), Lists.immutable.of(1, 2), Lists.immutable.of(1, 2, 3));
        ImmutableList<Integer> sizes = list.collect(Functions.getSizeOf());
        Assert.assertEquals(FastList.newListWith(1, 2, 3), sizes);
    }

    @Test
    public void squaredCollection()
    {
        MutableCollection<Integer> squareCollection = FastList.newListWith(1, 2, 3, 4, 5).collect(Functions.squaredInteger());
        Verify.assertContainsAll(squareCollection, 1, 4, 9, 16, 25);
    }

    @Test
    public void withDefault()
    {
        Object expected = new Object();
        Assert.assertSame(expected, Functions.withDefault(Functions.getFixedValue(null), expected).valueOf(new Object()));

        Object expected2 = new Object();
        Assert.assertSame(expected2, Functions.withDefault(Functions.getFixedValue(expected2), expected).valueOf(new Object()));
    }

    @Test
    public void nullSafe()
    {
        Object expected = new Object();
        Function<Object, Object> throwsFunction = new ThrowsFunction();
        Assert.assertSame(expected, Functions.nullSafe(throwsFunction, expected).valueOf(null));
        Assert.assertSame(expected, Functions.nullSafe(Functions.getFixedValue(expected)).valueOf(new Object()));
        Assert.assertNull(Functions.nullSafe(throwsFunction).valueOf(null));
    }

    @Test
    public void classForName()
    {
        Class<?> objectClass = Functions.classForName().valueOf("java.lang.Object");
        Assert.assertSame(Object.class, objectClass);
    }

    @Test
    public void bind_function2_parameter()
    {
        MutableCollection<Integer> multiplied = FastList.newListWith(1, 2, 3, 4, 5).collect(Functions.bind((value, parameter) -> value * parameter, 2));
        Verify.assertContainsAll(multiplied, 2, 4, 6, 8, 10);
    }

    @Test
    public void swappedPair()
    {
        Pair<Integer, String> pair1 = Tuples.pair(1, "One");
        Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
        Pair<Integer, String> pair3 = Tuples.pair(3, "Three");
        Pair<Integer, String> pair4 = Tuples.pair(4, "Four");

        MutableList<Pair<Integer, String>> testList = FastList.newListWith(pair1, pair2, pair3, pair4);
        MutableList<Pair<String, Integer>> actual = testList.collect(Functions.swappedPair());

        MutableList<Pair<String, Integer>> expected = FastList.newListWith(Tuples.pair("One", 1), Tuples.pair("Two", 2), Tuples.pair("Three", 3), Tuples.pair("Four", 4));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getTrue()
    {
        Assert.assertTrue(Functions.getTrue().valueOf(false));
    }

    @Test
    public void getFalse()
    {
        Assert.assertFalse(Functions.getFalse().valueOf(true));
    }

    private static class ThrowsFunction implements Function<Object, Object>
    {
        @Override
        public Object valueOf(Object object)
        {
            throw new RuntimeException();
        }
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Functions.class);
    }

    private static class MyRuntimeException extends RuntimeException
    {
        MyRuntimeException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}
