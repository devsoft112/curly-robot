/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.api;

import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.function.primitive.BooleanFunction;
import org.eclipse.collections.api.block.function.primitive.ByteFunction;
import org.eclipse.collections.api.block.function.primitive.CharFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import org.eclipse.collections.api.block.function.primitive.FloatFunction;
import org.eclipse.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.IntObjectToIntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.block.function.primitive.LongObjectToLongFunction;
import org.eclipse.collections.api.block.function.primitive.ShortFunction;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.ordered.OrderedIterable;
import org.eclipse.collections.api.partition.PartitionIterable;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;

/**
 * RichIterable is an interface which extends the InternalIterable interface with several internal iterator methods, from
 * the Smalltalk Collection protocol.  These include select, reject, detect, collect, injectInto, anySatisfy,
 * allSatisfy. The API also includes converter methods to convert a RichIterable to a List (toList), to a sorted
 * List (toSortedList), to a Set (toSet), and to a Map (toMap).
 *
 * @since 1.0
 */
public interface RichIterable<T>
        extends InternalIterable<T>
{
    /**
     * Returns the number of items in this iterable.
     *
     * @since 1.0
     */
    int size();

    /**
     * Returns true if this iterable has zero items.
     *
     * @since 1.0
     */
    boolean isEmpty();

    /**
     * The English equivalent of !this.isEmpty()
     *
     * @since 1.0
     */
    default boolean notEmpty()
    {
        return !this.isEmpty();
    }

    /**
     * Returns the first element of an iterable.  In the case of a List it is the element at the first index.  In the
     * case of any other Collection, it is the first element that would be returned during an iteration.  If the
     * iterable is empty, null is returned.  If null is a valid element of the container, then a developer would need to
     * check to see if the iterable is empty to validate that a null result was not due to the container being empty.
     * <p>
     * The order of Sets are not guaranteed (except for TreeSets and other Ordered Set implementations), so if you use
     * this method, the first element could be any element from the Set.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#getFirst()} instead.
     */
    @Deprecated
    T getFirst();

    /**
     * Returns the last element of an iterable.  In the case of a List it is the element at the last index.  In the case
     * of any other Collection, it is the last element that would be returned during an iteration.  If the iterable is
     * empty, null is returned.  If null is a valid element of the container, then a developer would need to check to
     * see if the iterable is empty to validate that a null result was not due to the container being empty.
     * <p>
     * The order of Sets are not guaranteed (except for TreeSets and other Ordered Set implementations), so if you use
     * this method, the last element could be any element from the Set.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#getLast()} instead.
     */
    @Deprecated
    T getLast();

    /**
     * Returns the element if the iterable has exactly one element. Otherwise, throw {@link IllegalStateException}.
     *
     * @return an element of an iterable.
     * @throws IllegalStateException if iterable is empty or has multiple elements.
     * @since 8.0
     */
    default T getOnly()
    {
        if (this.size() == 1)
        {
            return this.getFirst();
        }

        throw new IllegalStateException("Size must be 1 but was " + this.size());
    }

    /**
     * Returns true if the iterable has an element which responds true to element.equals(object).
     *
     * @since 1.0
     */
    boolean contains(Object object);

    /**
     * Returns true if all elements in source are contained in this collection.
     *
     * @since 1.0
     */
    boolean containsAllIterable(Iterable<?> source);

    /**
     * Returns true if all elements in source are contained in this collection.
     *
     * @see Collection#containsAll(Collection)
     * @since 1.0
     */
    boolean containsAll(Collection<?> source);

    /**
     * Returns true if all elements in the specified var arg array are contained in this collection.
     *
     * @since 1.0
     */
    boolean containsAllArguments(Object... elements);

    /**
     * Executes the Procedure for each element in the iterable and returns {@code this}.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; tapped =
     *     people.<b>tap</b>(person -> LOGGER.info(person.getName()));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * RichIterable&lt;Person&gt; tapped =
     *     people.<b>tap</b>(new Procedure&lt;Person&gt;()
     *     {
     *         public void value(Person person)
     *         {
     *             LOGGER.info(person.getName());
     *         }
     *     });
     * </pre>
     *
     * @see #each(Procedure)
     * @see #forEach(Procedure)
     * @since 6.0
     */
    RichIterable<T> tap(Procedure<? super T> procedure);

    /**
     * The procedure is executed for each element in the iterable.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * people.each(person -> LOGGER.info(person.getName()));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * people.each(new Procedure&lt;Person&gt;()
     * {
     *     public void value(Person person)
     *     {
     *         LOGGER.info(person.getName());
     *     }
     * });
     * </pre>
     * This method is a variant of {@link InternalIterable#forEach(Procedure)}
     * that has a signature conflict with {@link Iterable#forEach(java.util.function.Consumer)}.
     *
     * @see InternalIterable#forEach(Procedure)
     * @see Iterable#forEach(java.util.function.Consumer)
     * @since 6.0
     */
    @SuppressWarnings("UnnecessaryFullyQualifiedName")
    void each(Procedure<? super T> procedure);

    /**
     * Returns all elements of the source collection that return true when evaluating the predicate.  This method is also
     * commonly called filter.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; selected =
     *     people.<b>select</b>(person -> person.getAddress().getCity().equals("London"));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * RichIterable&lt;Person&gt; selected =
     *     people.<b>select</b>(new Predicate&lt;Person&gt;()
     *     {
     *         public boolean accept(Person person)
     *         {
     *             return person.getAddress().getCity().equals("London");
     *         }
     *     });
     *
     * @since 1.0
     */
    RichIterable<T> select(Predicate<? super T> predicate);

    /**
     * Same as the select method with one parameter but uses the specified target collection for the results.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; selected =
     *     people.select(person -> person.person.getLastName().equals("Smith"), Lists.mutable.empty());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * MutableList&lt;Person&gt; selected =
     *     people.select(new Predicate&lt;Person&gt;()
     *     {
     *         public boolean accept(Person person)
     *         {
     *             return person.person.getLastName().equals("Smith");
     *         }
     *     }, Lists.mutable.empty());
     * </pre>
     * <p>
     *
     * @param predicate a {@link Predicate} to use as the select criteria
     * @param target    the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code predicate}
     * @return {@code target}, which contains appended elements as a result of the select criteria
     * @see #select(Predicate)
     * @since 1.0
     */
    <R extends Collection<T>> R select(Predicate<? super T> predicate, R target);

    /**
     * Similar to {@link #select(Predicate)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; selected =
     *     people.selectWith((Person person, Integer age) -> person.getAge() >= age, Integer.valueOf(18));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * RichIterable&lt;Person&gt; selected =
     *     people.selectWith(new Predicate2&lt;Person, Integer&gt;()
     *     {
     *         public boolean accept(Person person, Integer age)
     *         {
     *             return person.getAge() >= age;
     *         }
     *     }, Integer.valueOf(18));
     * </pre>
     *
     * @param predicate a {@link Predicate2} to use as the select criteria
     * @param parameter a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @see #select(Predicate)
     * @since 5.0
     */
    <P> RichIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Similar to {@link #select(Predicate, Collection)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; selected =
     *     people.selectWith((Person person, Integer age) -> person.getAge() >= age, Integer.valueOf(18), Lists.mutable.empty());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * MutableList&lt;Person&gt; selected =
     *     people.selectWith(new Predicate2&lt;Person, Integer&gt;()
     *     {
     *         public boolean accept(Person person, Integer age)
     *         {
     *             return person.getAge() >= age;
     *         }
     *     }, Integer.valueOf(18), Lists.mutable.empty());
     * </pre>
     *
     * @param predicate        a {@link Predicate2} to use as the select criteria
     * @param parameter        a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @param targetCollection the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code predicate}
     * @return {@code targetCollection}, which contains appended elements as a result of the select criteria
     * @see #select(Predicate)
     * @see #select(Predicate, Collection)
     * @since 1.0
     */
    <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection);

    /**
     * Returns all elements of the source collection that return false when evaluating of the predicate.  This method is also
     * sometimes called filterNot and is the equivalent of calling iterable.select(Predicates.not(predicate)).
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; rejected =
     *     people.reject(person -> person.person.getLastName().equals("Smith"));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * RichIterable&lt;Person&gt; rejected =
     *     people.reject(new Predicate&lt;Person&gt;()
     *     {
     *         public boolean accept(Person person)
     *         {
     *             return person.person.getLastName().equals("Smith");
     *         }
     *     });
     * </pre>
     *
     * @param predicate a {@link Predicate} to use as the reject criteria
     * @return a RichIterable that contains elements that cause {@link Predicate#accept(Object)} method to evaluate to false
     * @since 1.0
     */
    RichIterable<T> reject(Predicate<? super T> predicate);

    /**
     * Similar to {@link #reject(Predicate)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Person&gt; rejected =
     *     people.rejectWith((Person person, Integer age) -> person.getAge() < age, Integer.valueOf(18));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * MutableList&lt;Person&gt; rejected =
     *     people.rejectWith(new Predicate2&lt;Person, Integer&gt;()
     *     {
     *         public boolean accept(Person person, Integer age)
     *         {
     *             return person.getAge() < age;
     *         }
     *     }, Integer.valueOf(18));
     * </pre>
     *
     * @param predicate a {@link Predicate2} to use as the select criteria
     * @param parameter a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @see #select(Predicate)
     * @since 5.0
     */
    <P> RichIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Same as the reject method with one parameter but uses the specified target collection for the results.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; rejected =
     *     people.reject(person -> person.person.getLastName().equals("Smith"), Lists.mutable.empty());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * MutableList&lt;Person&gt; rejected =
     *     people.reject(new Predicate&lt;Person&gt;()
     *     {
     *         public boolean accept(Person person)
     *         {
     *             return person.person.getLastName().equals("Smith");
     *         }
     *     }, Lists.mutable.empty());
     * </pre>
     *
     * @param predicate a {@link Predicate} to use as the reject criteria
     * @param target    the Collection to append to for all elements in this {@code RichIterable} that cause {@code Predicate#accept(Object)} method to evaluate to false
     * @return {@code target}, which contains appended elements as a result of the reject criteria
     * @since 1.0
     */
    <R extends Collection<T>> R reject(Predicate<? super T> predicate, R target);

    /**
     * Similar to {@link #reject(Predicate, Collection)}, except with an evaluation parameter for the second generic argument in {@link Predicate2}.
     * <p>
     * E.g. return a {@link Collection} of Person elements where the person has an age <b>greater than or equal to</b> 18 years
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;Person&gt; rejected =
     *     people.rejectWith((Person person, Integer age) -> person.getAge() < age, Integer.valueOf(18), Lists.mutable.empty());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * MutableList&lt;Person&gt; rejected =
     *     people.rejectWith(new Predicate2&lt;Person, Integer&gt;()
     *     {
     *         public boolean accept(Person person, Integer age)
     *         {
     *             return person.getAge() < age;
     *         }
     *     }, Integer.valueOf(18), Lists.mutable.empty());
     * </pre>
     *
     * @param predicate        a {@link Predicate2} to use as the reject criteria
     * @param parameter        a parameter to pass in for evaluation of the second argument {@code P} in {@code predicate}
     * @param targetCollection the Collection to append to for all elements in this {@code RichIterable} that cause {@code Predicate#accept(Object)} method to evaluate to false
     * @return {@code targetCollection}, which contains appended elements as a result of the reject criteria
     * @see #reject(Predicate)
     * @see #reject(Predicate, Collection)
     * @since 1.0
     */
    <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection);

    /**
     * Filters a collection into a PartitionedIterable based on the evaluation of the predicate.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * PartitionIterable&lt;Person&gt; newYorkersAndNonNewYorkers =
     *     people.<b>partition</b>(person -> person.getAddress().getState().getName().equals("New York"));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * PartitionIterable&lt;Person&gt; newYorkersAndNonNewYorkers =
     *     people.<b>partition</b>(new Predicate&lt;Person&gt;()
     *     {
     *         public boolean accept(Person person)
     *         {
     *             return person.getAddress().getState().getName().equals("New York");
     *         }
     *     });
     * </pre>
     *
     * @since 1.0.
     */
    PartitionIterable<T> partition(Predicate<? super T> predicate);

    /**
     * Filters a collection into a PartitionIterable based on the evaluation of the predicate.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * PartitionIterable&lt;Person>&gt newYorkersAndNonNewYorkers =
     *     people.<b>partitionWith</b>((Person person, String state) -> person.getAddress().getState().getName().equals(state), "New York");
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * PartitionIterable&lt;Person>&gt newYorkersAndNonNewYorkers =
     *     people.<b>partitionWith</b>(new Predicate2&lt;Person, String&gt;()
     *     {
     *         public boolean accept(Person person, String state)
     *         {
     *             return person.getAddress().getState().getName().equals(state);
     *         }
     *     }, "New York");
     * </pre>
     *
     * @since 5.0.
     */
    <P> PartitionIterable<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns all elements of the source collection that are instances of the Class {@code clazz}.
     * <p>
     * <pre>
     * RichIterable&lt;Integer&gt; integers =
     *     List.mutable.with(new Integer(0), new Long(0L), new Double(0.0)).selectInstancesOf(Integer.class);
     * </pre>
     * @since 2.0
     */
    <S> RichIterable<S> selectInstancesOf(Class<S> clazz);

    /**
     * Returns a new collection with the results of applying the specified function on each element of the source
     * collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;String&gt; names =
     *     people.collect(person -> person.getFirstName() + " " + person.getLastName());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * RichIterable&lt;String&gt; names =
     *     people.collect(new Function&lt;Person, String&gt;()
     *     {
     *         public String valueOf(Person person)
     *         {
     *             return person.getFirstName() + " " + person.getLastName();
     *         }
     *     });
     * </pre>
     *
     * @since 1.0
     */
    <V> RichIterable<V> collect(Function<? super T, ? extends V> function);

    /**
     * Same as {@link #collect(Function)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableList&lt;String&gt; names =
     *     people.collect(person -> person.getFirstName() + " " + person.getLastName(), Lists.mutable.empty());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * MutableList&lt;String&gt; names =
     *     people.collect(new Function&lt;Person, String&gt;()
     *     {
     *         public String valueOf(Person person)
     *         {
     *             return person.getFirstName() + " " + person.getLastName();
     *         }
     *     }, Lists.mutable.empty());
     * </pre>
     *
     * @param function a {@link Function} to use as the collect transformation function
     * @param target   the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code function}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @see #collect(Function)
     * @since 1.0
     */
    <V, R extends Collection<V>> R collect(Function<? super T, ? extends V> function, R target);

    /**
     * Returns a new primitive {@code boolean} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * BooleanIterable licenses =
     *     people.collectBoolean(person -> person.hasDrivingLicense());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * BooleanIterable licenses =
     *     people.collectBoolean(new BooleanFunction&lt;Person&gt;()
     *     {
     *         public boolean booleanValueOf(Person person)
     *         {
     *             return person.hasDrivingLicense();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    BooleanIterable collectBoolean(BooleanFunction<? super T> booleanFunction);

    /**
     * Same as {@link #collectBoolean(BooleanFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * BooleanArrayList licenses =
     *     people.collectBoolean(person -> person.hasDrivingLicense(), new BooleanArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * BooleanArrayList licenses =
     *     people.collectBoolean(new BooleanFunction&lt;Person&gt;()
     *     {
     *         public boolean booleanValueOf(Person person)
     *         {
     *             return person.hasDrivingLicense();
     *         }
     *     }, new BooleanArrayList());
     * </pre>
     *
     * @param booleanFunction a {@link BooleanFunction} to use as the collect transformation function
     * @param target          the MutableBooleanCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableBooleanCollection> R collectBoolean(BooleanFunction<? super T> booleanFunction, R target);

    /**
     * Returns a new primitive {@code byte} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ByteIterable bytes =
     *     people.collectByte(person -> person.getCode());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * ByteIterable bytes =
     *     people.collectByte(new ByteFunction&lt;Person&gt;()
     *     {
     *         public byte byteValueOf(Person person)
     *         {
     *             return person.getCode();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    ByteIterable collectByte(ByteFunction<? super T> byteFunction);

    /**
     * Same as {@link #collectByte(ByteFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ByteArrayList bytes =
     *     people.collectByte(person -> person.getCode(), new ByteArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * ByteArrayList bytes =
     *     people.collectByte(new ByteFunction&lt;Person&gt;()
     *     {
     *         public byte byteValueOf(Person person)
     *         {
     *             return person.getCode();
     *         }
     *     }, new ByteArrayList());
     * </pre>
     *
     * @param byteFunction a {@link ByteFunction} to use as the collect transformation function
     * @param target       the MutableByteCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableByteCollection> R collectByte(ByteFunction<? super T> byteFunction, R target);

    /**
     * Returns a new primitive {@code char} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * CharIterable chars =
     *     people.collectChar(person -> person.getMiddleInitial());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * CharIterable chars =
     *     people.collectChar(new CharFunction&lt;Person&gt;()
     *     {
     *         public char charValueOf(Person person)
     *         {
     *             return person.getMiddleInitial();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    CharIterable collectChar(CharFunction<? super T> charFunction);

    /**
     * Same as {@link #collectChar(CharFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * CharArrayList chars =
     *     people.collectChar(person -> person.getMiddleInitial(), new CharArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * CharArrayList chars =
     *     people.collectChar(new CharFunction&lt;Person&gt;()
     *     {
     *         public char charValueOf(Person person)
     *         {
     *             return person.getMiddleInitial();
     *         }
     *     }, new CharArrayList());
     * </pre>
     *
     * @param charFunction a {@link CharFunction} to use as the collect transformation function
     * @param target       the MutableCharCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableCharCollection> R collectChar(CharFunction<? super T> charFunction, R target);

    /**
     * Returns a new primitive {@code double} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * DoubleIterable doubles =
     *     people.collectDouble(person -> person.getMilesFromNorthPole());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * DoubleIterable doubles =
     *     people.collectDouble(new DoubleFunction&lt;Person&gt;()
     *     {
     *         public double doubleValueOf(Person person)
     *         {
     *             return person.getMilesFromNorthPole();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    DoubleIterable collectDouble(DoubleFunction<? super T> doubleFunction);

    /**
     * Same as {@link #collectDouble(DoubleFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * DoubleArrayList doubles =
     *     people.collectDouble(person -> person.getMilesFromNorthPole(), new DoubleArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * DoubleArrayList doubles =
     *     people.collectDouble(new DoubleFunction&lt;Person&gt;()
     *     {
     *         public double doubleValueOf(Person person)
     *         {
     *             return person.getMilesFromNorthPole();
     *         }
     *     }, new DoubleArrayList());
     * </pre>
     *
     * @param doubleFunction a {@link DoubleFunction} to use as the collect transformation function
     * @param target         the MutableDoubleCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableDoubleCollection> R collectDouble(DoubleFunction<? super T> doubleFunction, R target);

    /**
     * Returns a new primitive {@code float} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * FloatIterable floats =
     *     people.collectFloat(person -> person.getHeightInInches());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * FloatIterable floats =
     *     people.collectFloat(new FloatFunction&lt;Person&gt;()
     *     {
     *         public float floatValueOf(Person person)
     *         {
     *             return person.getHeightInInches();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    FloatIterable collectFloat(FloatFunction<? super T> floatFunction);

    /**
     * Same as {@link #collectFloat(FloatFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * FloatArrayList floats =
     *     people.collectFloat(person -> person.getHeightInInches(), new FloatArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * FloatArrayList floats =
     *     people.collectFloat(new FloatFunction&lt;Person&gt;()
     *     {
     *         public float floatValueOf(Person person)
     *         {
     *             return person.getHeightInInches();
     *         }
     *     }, new FloatArrayList());
     * </pre>
     *
     * @param floatFunction a {@link FloatFunction} to use as the collect transformation function
     * @param target        the MutableFloatCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableFloatCollection> R collectFloat(FloatFunction<? super T> floatFunction, R target);

    /**
     * Returns a new primitive {@code int} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * IntIterable ints =
     *     people.collectInt(person -> person.getAge());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * IntIterable ints =
     *     people.collectInt(new IntFunction&lt;Person&gt;()
     *     {
     *         public int intValueOf(Person person)
     *         {
     *             return person.getAge();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    IntIterable collectInt(IntFunction<? super T> intFunction);

    /**
     * Same as {@link #collectInt(IntFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * IntArrayList ints =
     *     people.collectInt(person -> person.getAge(), new IntArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * IntArrayList ints =
     *     people.collectInt(new IntFunction&lt;Person&gt;()
     *     {
     *         public int intValueOf(Person person)
     *         {
     *             return person.getAge();
     *         }
     *     }, new IntArrayList());
     * </pre>
     *
     * @param intFunction a {@link IntFunction} to use as the collect transformation function
     * @param target      the MutableIntCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableIntCollection> R collectInt(IntFunction<? super T> intFunction, R target);

    /**
     * Returns a new primitive {@code long} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * LongIterable longs =
     *     people.collectLong(person -> person.getGuid());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * LongIterable longs =
     *     people.collectLong(new LongFunction&lt;Person&gt;()
     *     {
     *         public long longValueOf(Person person)
     *         {
     *             return person.getGuid();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    LongIterable collectLong(LongFunction<? super T> longFunction);

    /**
     * Same as {@link #collectLong(LongFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * LongArrayList longs =
     *     people.collectLong(person -> person.getGuid(), new LongArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * LongArrayList longs =
     *     people.collectLong(new LongFunction&lt;Person&gt;()
     *     {
     *         public long longValueOf(Person person)
     *         {
     *             return person.getGuid();
     *         }
     *     }, new LongArrayList());
     * </pre>
     *
     * @param longFunction a {@link LongFunction} to use as the collect transformation function
     * @param target       the MutableLongCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableLongCollection> R collectLong(LongFunction<? super T> longFunction, R target);

    /**
     * Returns a new primitive {@code short} iterable with the results of applying the specified function on each element
     * of the source collection.  This method is also commonly called transform or map.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ShortIterable shorts =
     *     people.collectShort(person -> person.getNumberOfJunkMailItemsReceivedPerMonth());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * ShortIterable shorts =
     *     people.collectShort(new ShortFunction&lt;Person&gt;()
     *     {
     *         public short shortValueOf(Person person)
     *         {
     *             return person.getNumberOfJunkMailItemsReceivedPerMonth();
     *         }
     *     });
     * </pre>
     *
     * @since 4.0
     */
    ShortIterable collectShort(ShortFunction<? super T> shortFunction);

    /**
     * Same as {@link #collectShort(ShortFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * ShortArrayList shorts =
     *     people.collectShort(person -> person.getNumberOfJunkMailItemsReceivedPerMonth, new ShortArrayList());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * ShortArrayList shorts =
     *     people.collectShort(new ShortFunction&lt;Person&gt;()
     *     {
     *         public short shortValueOf(Person person)
     *         {
     *             return person.getNumberOfJunkMailItemsReceivedPerMonth;
     *         }
     *     }, new ShortArrayList());
     * </pre>
     *
     * @param shortFunction a {@link ShortFunction} to use as the collect transformation function
     * @param target        the MutableShortCollection to append to for all elements in this {@code RichIterable}
     * @return {@code target}, which contains appended elements as a result of the collect transformation
     * @since 5.0
     */
    <R extends MutableShortCollection> R collectShort(ShortFunction<? super T> shortFunction, R target);

    /**
     * Same as {@link #collect(Function)} with a {@code Function2} and specified parameter which is passed to the block.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * RichIterable&lt;Integer&gt; integers =
     *     Lists.mutable.with(1, 2, 3).collectWith((each, parameter) -> each + parameter, Integer.valueOf(1));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * Function2&lt;Integer, Integer, Integer&gt; addParameterFunction =
     *     new Function2&lt;Integer, Integer, Integer&gt;()
     *     {
     *         public Integer value(Integer each, Integer parameter)
     *         {
     *             return each + parameter;
     *         }
     *     };
     * RichIterable&lt;Integer&gt; integers =
     *     Lists.mutable.with(1, 2, 3).collectWith(addParameterFunction, Integer.valueOf(1));
     * </pre>
     *
     * @param function  A {@link Function2} to use as the collect transformation function
     * @param parameter A parameter to pass in for evaluation of the second argument {@code P} in {@code function}
     * @return A new {@code RichIterable} that contains the transformed elements returned by {@link Function2#value(Object, Object)}
     * @see #collect(Function)
     * @since 5.0
     */
    <P, V> RichIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter);

    /**
     * Same as collectWith but with a targetCollection parameter to gather the results.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * MutableSet&lt;Integer&gt; integers =
     *     Lists.mutable.with(1, 2, 3).collectWith((each, parameter) -> each + parameter, Integer.valueOf(1), Sets.mutable.empty());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * Function2&lt;Integer, Integer, Integer&gt; addParameterFunction =
     *     new Function2&lt;Integer, Integer, Integer&gt;()
     *     {
     *         public Integer value(final Integer each, final Integer parameter)
     *         {
     *             return each + parameter;
     *         }
     *     };
     * MutableSet&lt;Integer&gt; integers =
     *     Lists.mutable.with(1, 2, 3).collectWith(addParameterFunction, Integer.valueOf(1), Sets.mutable.empty());
     * </pre>
     *
     * @param function         a {@link Function2} to use as the collect transformation function
     * @param parameter        a parameter to pass in for evaluation of the second argument {@code P} in {@code function}
     * @param targetCollection the Collection to append to for all elements in this {@code RichIterable} that meet select criteria {@code function}
     * @return {@code targetCollection}, which contains appended elements as a result of the collect transformation
     * @since 1.0
     */
    <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function,
            P parameter,
            R targetCollection);

    /**
     * Returns a new collection with the results of applying the specified function on each element of the source
     * collection, but only for those elements which return true upon evaluation of the predicate.  This is the
     * the optimized equivalent of calling iterable.select(predicate).collect(function).
     * <p>
     * Example using a Java 8 lambda and method reference:
     * <pre>
     * RichIterable&lt;String&gt; strings = Lists.mutable.with(1, 2, 3).collectIf(e -> e != null, Object::toString);
     * </pre>
     * <p>
     * Example using Predicates factory:
     * <pre>
     * RichIterable&lt;String&gt; strings = Lists.mutable.with(1, 2, 3).collectIf(Predicates.notNull(), Functions.getToString());
     * </pre>
     *
     * @since 1.0
     */
    <V> RichIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function);

    /**
     * Same as the collectIf method with two parameters but uses the specified target collection for the results.
     *
     * @param predicate a {@link Predicate} to use as the select criteria
     * @param function  a {@link Function} to use as the collect transformation function
     * @param target    the Collection to append to for all elements in this {@code RichIterable} that meet the collect criteria {@code predicate}
     * @return {@code targetCollection}, which contains appended elements as a result of the collect criteria and transformation
     * @see #collectIf(Predicate, Function)
     * @since 1.0
     */
    <V, R extends Collection<V>> R collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target);

    /**
     * {@code flatCollect} is a special case of {@link #collect(Function)}. With {@code collect}, when the {@link Function} returns
     * a collection, the result is a collection of collections. {@code flatCollect} outputs a single "flattened" collection
     * instead.  This method is commonly called flatMap.
     * <p>
     * Consider the following example where we have a {@code Person} class, and each {@code Person} has a list of {@code Address} objects.  Take the following {@link Function}:
     * <pre>
     * Function&lt;Person, List&lt;Address&gt;&gt; addressFunction = Person::getAddresses;
     * RichIterable&lt;Person&gt; people = ...;
     * </pre>
     * Using {@code collect} returns a collection of collections of addresses.
     * <pre>
     * RichIterable&lt;List&lt;Address&gt;&gt; addresses = people.collect(addressFunction);
     * </pre>
     * Using {@code flatCollect} returns a single flattened list of addresses.
     * <pre>
     * RichIterable&lt;Address&gt; addresses = people.flatCollect(addressFunction);
     * </pre>
     *
     * @param function The {@link Function} to apply
     * @return a new flattened collection produced by applying the given {@code function}
     * @since 1.0
     */
    <V> RichIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Same as flatCollect, only the results are collected into the target collection.
     *
     * @param function The {@link Function} to apply
     * @param target   The collection into which results should be added.
     * @return {@code target}, which will contain a flattened collection of results produced by applying the given {@code function}
     * @see #flatCollect(Function)
     */
    <V, R extends Collection<V>> R flatCollect(Function<? super T, ? extends Iterable<V>> function, R target);

    /**
     * Returns the first element of the iterable for which the predicate evaluates to true or null in the case where no
     * element returns true.  This method is commonly called find.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Person person =
     *     people.detect(person -> person.getFirstName().equals("John") && person.getLastName().equals("Smith"));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * Person person =
     *     people.detect(new Predicate&lt;Person&gt;()
     *     {
     *         public boolean accept(Person person)
     *         {
     *             return person.getFirstName().equals("John") && person.getLastName().equals("Smith");
     *         }
     *     });
     * </pre>
     *
     * @since 1.0
     */
    T detect(Predicate<? super T> predicate);

    /**
     * Returns the first element that evaluates to true for the specified predicate2 and parameter, or null if none
     * evaluate to true.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Person person =
     *     people.detectWith((person, fullName) -> person.getFullName().equals(fullName), "John Smith");
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * Person person =
     *     people.detectWith(new Predicate2&lt;Person, String&gt;()
     *     {
     *         public boolean accept(Person person, String fullName)
     *         {
     *             return person.getFullName().equals(fullName);
     *         }
     *     }, "John Smith");
     * </pre>
     *
     * @since 5.0
     */
    <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns the first element of the iterable for which the predicate evaluates to true as an Optional.  This method is commonly called find.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Person person =
     *     people.detectOptional(person -> person.getFirstName().equals("John") && person.getLastName().equals("Smith"));
     * </pre>
     * <p>
     *
     * @throws NullPointerException if the element selected is null
     * @since 8.0
     */
    Optional<T> detectOptional(Predicate<? super T> predicate);

    /**
     * Returns the first element that evaluates to true for the specified predicate2 and parameter as an Optional.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * Optional&lt;Person&gt; person =
     *     people.detectWithOptional((person, fullName) -> person.getFullName().equals(fullName), "John Smith");
     * </pre>
     * <p>
     *
     * @throws NullPointerException if the element selected is null
     * @since 8.0
     */
    <P> Optional<T> detectWithOptional(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns the first element of the iterable for which the predicate evaluates to true.  If no element matches
     * the predicate, then returns the value of applying the specified function.
     *
     * @since 1.0
     */
    default T detectIfNone(Predicate<? super T> predicate, Function0<? extends T> function)
    {
        T result = this.detect(predicate);
        return result == null ? function.value() : result;
    }

    /**
     * Returns the first element of the iterable that evaluates to true for the specified predicate2 and parameter, or
     * returns the value of evaluating the specified function.
     *
     * @since 5.0
     */
    <P> T detectWithIfNone(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            Function0<? extends T> function);

    /**
     * Return the total number of elements that answer true to the specified predicate.
     * <p>
     * Example using a Java 8 lambda expression:
     * <pre>
     * int count =
     *     people.<b>count</b>(person -> person.getAddress().getState().getName().equals("New York"));
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * int count =
     *     people.<b>count</b>(new Predicate&lt;Person&gt;()
     *     {
     *         public boolean accept(Person person)
     *         {
     *             return person.getAddress().getState().getName().equals("New York");
     *         }
     *     });
     * </pre>
     *
     * @since 1.0
     */
    int count(Predicate<? super T> predicate);

    /**
     * Returns the total number of elements that evaluate to true for the specified predicate.
     * <p>
     * <pre>e.g.
     * return lastNames.<b>countWith</b>(Predicates2.equal(), "Smith");
     * </pre>
     */
    <P> int countWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if the predicate evaluates to true for any element of the iterable.
     * Returns false if the iterable is empty, or if no element returned true when evaluating the predicate.
     *
     * @since 1.0
     */
    boolean anySatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if the predicate evaluates to true for any element of the collection, or return false.
     * Returns false if the collection is empty.
     *
     * @since 5.0
     */
    <P> boolean anySatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if the predicate evaluates to true for every element of the iterable or if the iterable is empty.
     * Otherwise, returns false.
     *
     * @since 1.0
     */
    boolean allSatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if the predicate evaluates to true for every element of the collection, or returns false.
     *
     * @since 5.0
     */
    <P> boolean allSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns true if the predicate evaluates to false for every element of the iterable or if the iterable is empty.
     * Otherwise, returns false.
     *
     * @since 3.0
     */
    boolean noneSatisfy(Predicate<? super T> predicate);

    /**
     * Returns true if the predicate evaluates to false for every element of the collection, or return false.
     * Returns true if the collection is empty.
     *
     * @since 5.0
     */
    <P> boolean noneSatisfyWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Returns the final result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.  This method is commonly called fold or sometimes reduce.
     *
     * @since 1.0
     */
    <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function);

    /**
     * Returns the final int result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 1.0
     */
    int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function);

    /**
     * Returns the final long result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 1.0
     */
    long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function);

    /**
     * Returns the final float result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 2.0
     */
    float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function using each element of the iterable and the previous evaluation
     * result as the parameters. The injected value is used for the first parameter of the first evaluation, and the current
     * item in the iterable is used as the second parameter.
     *
     * @since 1.0
     */
    double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function);

    /**
     * Adds all the elements in this iterable to the specific target Collection.
     *
     * @since 8.0
     */
    <R extends Collection<T>> R into(R target);

    /**
     * Converts the collection to a MutableList implementation.
     *
     * @since 1.0
     */
    MutableList<T> toList();

    /**
     * Converts the collection to a MutableList implementation and sorts it using the natural order of the elements.
     *
     * @since 1.0
     */
    default MutableList<T> toSortedList()
    {
        return this.toList().sortThis();
    }

    /**
     * Converts the collection to a MutableList implementation and sorts it using the specified comparator.
     *
     * @since 1.0
     */
    default MutableList<T> toSortedList(Comparator<? super T> comparator)
    {
        return this.toList().sortThis(comparator);
    }

    /**
     * Converts the collection to a MutableList implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 1.0
     */
    <V extends Comparable<? super V>> MutableList<T> toSortedListBy(Function<? super T, ? extends V> function);

    /**
     * Converts the collection to a MutableSet implementation.
     *
     * @since 1.0
     */
    MutableSet<T> toSet();

    /**
     * Converts the collection to a MutableSortedSet implementation and sorts it using the natural order of the
     * elements.
     *
     * @since 1.0
     */
    MutableSortedSet<T> toSortedSet();

    /**
     * Converts the collection to a MutableSortedSet implementation and sorts it using the specified comparator.
     *
     * @since 1.0
     */
    MutableSortedSet<T> toSortedSet(Comparator<? super T> comparator);

    /**
     * Converts the collection to a MutableSortedSet implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 1.0
     */
    <V extends Comparable<? super V>> MutableSortedSet<T> toSortedSetBy(Function<? super T, ? extends V> function);

    /**
     * Converts the collection to the default MutableBag implementation.
     *
     * @since 1.0
     */
    MutableBag<T> toBag();

    /**
     * Converts the collection to a MutableSortedBag implementation and sorts it using the natural order of the
     * elements.
     *
     * @since 6.0
     */
    MutableSortedBag<T> toSortedBag();

    /**
     * Converts the collection to the MutableSortedBag implementation and sorts it using the specified comparator.
     *
     * @since 6.0
     */
    MutableSortedBag<T> toSortedBag(Comparator<? super T> comparator);

    /**
     * Converts the collection to a MutableSortedBag implementation and sorts it based on the natural order of the
     * attribute returned by {@code function}.
     *
     * @since 6.0
     */
    <V extends Comparable<? super V>> MutableSortedBag<T> toSortedBagBy(Function<? super T, ? extends V> function);

    /**
     * Converts the collection to a MutableMap implementation using the specified key and value functions.
     *
     * @since 1.0
     */
    <NK, NV> MutableMap<NK, NV> toMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts the collection to a MutableSortedMap implementation using the specified key and value functions
     * sorted by the key elements' natural ordering.
     *
     * @since 1.0
     */
    <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction);

    /**
     * Converts the collection to a MutableSortedMap implementation using the specified key and value functions
     * sorted by the given comparator.
     *
     * @since 1.0
     */
    <NK, NV> MutableSortedMap<NK, NV> toSortedMap(
            Comparator<? super NK> comparator,
            Function<? super T, ? extends NK> keyFunction,
            Function<? super T, ? extends NV> valueFunction);

    /**
     * Returns a lazy (deferred) iterable, most likely implemented by calling LazyIterate.adapt(this).
     *
     * @since 1.0.
     */
    LazyIterable<T> asLazy();

    /**
     * Converts this iterable to an array.
     *
     * @see Collection#toArray()
     * @since 1.0
     */
    Object[] toArray();

    /**
     * Converts this iterable to an array using the specified target array, assuming the target array is as long
     * or longer than the iterable.
     *
     * @see Collection#toArray(Object[])
     * @since 1.0
     */
    <T> T[] toArray(T[] target);

    /**
     * Returns the minimum element out of this container based on the comparator.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T min(Comparator<? super T> comparator);

    /**
     * Returns the maximum element out of this container based on the comparator.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T max(Comparator<? super T> comparator);

    /**
     * Returns the minimum element out of this container based on the comparator as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the minimum element is null
     * @since 8.2
     */
    default Optional<T> minOptional(Comparator<? super T> comparator)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.min(comparator));
    }

    /**
     * Returns the maximum element out of this container based on the comparator as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the maximum element is null
     * @since 8.2
     */
    default Optional<T> maxOptional(Comparator<? super T> comparator)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.max(comparator));
    }

    /**
     * Returns the minimum element out of this container based on the natural order.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T min();

    /**
     * Returns the maximum element out of this container based on the natural order.
     *
     * @throws ClassCastException     if the elements are not {@link Comparable}
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    T max();

    /**
     * Returns the minimum element out of this container based on the natural order as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws ClassCastException   if the elements are not {@link Comparable}
     * @throws NullPointerException if the minimum element is null
     * @since 8.2
     */
    default Optional<T> minOptional()
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.min());
    }

    /**
     * Returns the maximum element out of this container based on the natural order as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws ClassCastException   if the elements are not {@link Comparable}
     * @throws NullPointerException if the maximum element is null
     * @since 8.2
     */
    default Optional<T> maxOptional()
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.max());
    }

    /**
     * Returns the minimum elements out of this container based on the natural order of the attribute returned by Function.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function);

    /**
     * Returns the maximum elements out of this container based on the natural order of the attribute returned by Function.
     *
     * @throws NoSuchElementException if the RichIterable is empty
     * @since 1.0
     */
    <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function);

    /**
     * Returns the minimum elements out of this container based on the natural order of the attribute returned by Function as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the minimum element is null
     * @since 8.2
     */
    default <V extends Comparable<? super V>> Optional<T> minByOptional(Function<? super T, ? extends V> function)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.minBy(function));
    }

    /**
     * Returns the maximum elements out of this container based on the natural order of the attribute returned by Function as an Optional.
     * If the container is empty {@link Optional#empty()} is returned.
     *
     * @throws NullPointerException if the maximum element is null
     * @since 8.2
     */
    default <V extends Comparable<? super V>> Optional<T> maxByOptional(Function<? super T, ? extends V> function)
    {
        if (this.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.maxBy(function));
    }

    /**
     * Returns the final long result of evaluating function for each element of the iterable and adding the results
     * together.
     *
     * @since 2.0
     */
    long sumOfInt(IntFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function for each element of the iterable and adding the results
     * together. It uses Kahan summation algorithm to reduce numerical error.
     *
     * @since 2.0
     */
    double sumOfFloat(FloatFunction<? super T> function);

    /**
     * Returns the final long result of evaluating function for each element of the iterable and adding the results
     * together.
     *
     * @since 2.0
     */
    long sumOfLong(LongFunction<? super T> function);

    /**
     * Returns the final double result of evaluating function for each element of the iterable and adding the results
     * together. It uses Kahan summation algorithm to reduce numerical error.
     *
     * @since 2.0
     */
    double sumOfDouble(DoubleFunction<? super T> function);

    /**
     * Returns the result of summarizing the value returned from applying the IntFunction to
     * each element of the iterable.
     * <p>
     * <pre>
     * IntSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeInt(Integer::intValue);
     * </pre>
     *
     * @since 8.0
     */
    default IntSummaryStatistics summarizeInt(IntFunction<? super T> function)
    {
        IntSummaryStatistics stats = new IntSummaryStatistics();
        this.each(each -> stats.accept(function.intValueOf(each)));
        return stats;
    }

    /**
     * Returns the result of summarizing the value returned from applying the FloatFunction to
     * each element of the iterable.
     * <p>
     * <pre>
     * DoubleSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeFloat(Integer::floatValue);
     * </pre>
     *
     * @since 8.0
     */
    default DoubleSummaryStatistics summarizeFloat(FloatFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        this.each(each -> stats.accept(function.floatValueOf(each)));
        return stats;
    }

    /**
     * Returns the result of summarizing the value returned from applying the LongFunction to
     * each element of the iterable.
     * <p>
     * <pre>
     * LongSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeLong(Integer::longValue);
     * </pre>
     *
     * @since 8.0
     */
    default LongSummaryStatistics summarizeLong(LongFunction<? super T> function)
    {
        LongSummaryStatistics stats = new LongSummaryStatistics();
        this.each(each -> stats.accept(function.longValueOf(each)));
        return stats;
    }

    /**
     * Returns the result of summarizing the value returned from applying the DoubleFunction to
     * each element of the iterable.
     * <p>
     * <pre>
     * DoubleSummaryStatistics stats =
     *     Lists.mutable.with(1, 2, 3).summarizeDouble(Integer::doubleValue);
     * </pre>
     *
     * @since 8.0
     */
    default DoubleSummaryStatistics summarizeDouble(DoubleFunction<? super T> function)
    {
        DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
        this.each(each -> stats.accept(function.doubleValueOf(each)));
        return stats;
    }

    /**
     * This method produces the equivalent result as {@link Stream#collect(Collector)}.
     * <p>
     * <pre>
     * MutableObjectLongMap&lt;Integer&gt; map2 =
     *     Lists.mutable.with(1, 2, 3, 4, 5).reduceInPlace(Collectors2.sumByInt(i -> Integer.valueOf(i % 2), Integer::intValue));
     * </pre>
     * @since 8.0
     */
    default <R, A> R reduceInPlace(Collector<? super T, A, R> collector)
    {
        A mutableResult = collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = collector.accumulator();
        this.each(each -> accumulator.accept(mutableResult, each));
        return collector.finisher().apply(mutableResult);
    }

    /**
     * This method produces the equivalent result as {@link Stream#collect(Supplier, BiConsumer, BiConsumer)}.
     * The combiner used in collect is unnecessary in the serial case, so is not included in the API.
     *
     * @since 8.0
     */
    default <R> R reduceInPlace(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator)
    {
        R result = supplier.get();
        this.each(each -> accumulator.accept(result, each));
        return result;
    }

    /**
     * This method produces the equivalent result as {@link Stream#reduce(BinaryOperator)}.
     *
     * @since 8.0
     */
    default Optional<T> reduce(BinaryOperator<T> accumulator)
    {
        boolean[] seenOne = new boolean[1];
        T[] result = (T[]) new Object[1];
        this.each(each ->
        {
            if (seenOne[0])
            {
                result[0] = accumulator.apply(result[0], each);
            }
            else
            {
                seenOne[0] = true;
                result[0] = each;
            }
        });
        return seenOne[0] ? Optional.of(result[0]) : Optional.empty();
    }

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectLongMap<V> sumByInt(Function<? super T, ? extends V> groupBy, IntFunction<? super T> function);

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectDoubleMap<V> sumByFloat(Function<? super T, ? extends V> groupBy, FloatFunction<? super T> function);

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectLongMap<V> sumByLong(Function<? super T, ? extends V> groupBy, LongFunction<? super T> function);

    /**
     * Groups and sums the values using the two specified functions.
     *
     * @since 6.0
     */
    <V> ObjectDoubleMap<V> sumByDouble(Function<? super T, ? extends V> groupBy, DoubleFunction<? super T> function);

    /**
     * Returns a string representation of this collection by delegating to {@link #makeString(String)} and defaulting
     * the separator parameter to the characters <tt>", "</tt> (comma and space).
     *
     * @return a string representation of this collection.
     * @since 1.0
     */
    default String makeString()
    {
        return this.makeString(", ");
    }

    /**
     * Returns a string representation of this collection by delegating to {@link #makeString(String, String, String)}
     * and defaulting the start and end parameters to <tt>""</tt> (the empty String).
     *
     * @return a string representation of this collection.
     * @since 1.0
     */
    default String makeString(String separator)
    {
        return this.makeString("", separator, "");
    }

    /**
     * Returns a string representation of this collection with the elements separated by the specified
     * separator and enclosed between the start and end strings.
     *
     * @return a string representation of this collection.
     * @since 1.0
     */
    default String makeString(String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        this.appendString(stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}.  Prints the string returned
     * by {@link #makeString()}.
     *
     * @since 1.0
     */
    default void appendString(Appendable appendable)
    {
        this.appendString(appendable, ", ");
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}.  Prints the string returned
     * by {@link #makeString(String)}.
     *
     * @since 1.0
     */
    default void appendString(Appendable appendable, String separator)
    {
        this.appendString(appendable, "", separator, "");
    }

    /**
     * Prints a string representation of this collection onto the given {@code Appendable}.  Prints the string returned
     * by {@link #makeString(String, String, String)}.
     *
     * @since 1.0
     */
    void appendString(Appendable appendable, String start, String separator, String end);

    /**
     * For each element of the iterable, the function is evaluated and the results of these evaluations are collected
     * into a new multimap, where the transformed value is the key and the original values are added to the same (or similar)
     * species of collection as the source iterable.
     * <p>
     * Example using a Java 8 method reference:
     * <pre>
     * Multimap&lt;String, Person&gt; peopleByLastName =
     *     people.groupBy(Person::getLastName);
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * Multimap&lt;String, Person&gt; peopleByLastName =
     *     people.groupBy(new Function&lt;Person, String&gt;()
     *     {
     *         public String valueOf(Person person)
     *         {
     *             return person.getLastName();
     *         }
     *     });
     * </pre>
     *
     * @since 1.0
     */
    <V> Multimap<V, T> groupBy(Function<? super T, ? extends V> function);

    /**
     * Same as {@link #groupBy(Function)}, except that the results are gathered into the specified {@code target}
     * multimap.
     * <p>
     * Example using a Java 8 method reference:
     * <pre>
     * FastListMultimap&lt;String, Person&gt; peopleByLastName =
     *     people.groupBy(Person::getLastName, new FastListMultimap&lt;String, Person&gt;());
     * </pre>
     * <p>
     * Example using an anonymous inner class:
     * <pre>
     * FastListMultimap&lt;String, Person&gt; peopleByLastName =
     *     people.groupBy(new Function&lt;Person, String&gt;()
     *     {
     *         public String valueOf(Person person)
     *         {
     *             return person.getLastName();
     *         }
     *     }, new FastListMultimap&lt;String, Person&gt;());
     * </pre>
     *
     * @since 1.0
     */
    <V, R extends MutableMultimap<V, T>> R groupBy(Function<? super T, ? extends V> function, R target);

    /**
     * Similar to {@link #groupBy(Function)}, except the result of evaluating function will return a collection of keys
     * for each value.
     *
     * @since 1.0
     */
    <V> Multimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);

    /**
     * Same as {@link #groupByEach(Function)}, except that the results are gathered into the specified {@code target}
     * multimap.
     *
     * @since 1.0
     */
    <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target);

    /**
     * For each element of the iterable, the function is evaluated and he results of these evaluations are collected
     * into a new map, where the transformed value is the key. The generated keys must each be unique, or else an
     * exception is thrown.
     *
     * @throws IllegalStateException if the keys returned by the function are not unique
     * @see #groupBy(Function)
     * @since 5.0
     */
    <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function);

    /**
     * Same as {@link #groupByUniqueKey(Function)}, except that the results are gathered into the specified {@code target}
     * map.
     *
     * @throws IllegalStateException if the keys returned by the function are not unique
     * @see #groupByUniqueKey(Function)
     * @since 6.0
     */
    <V, R extends MutableMap<V, T>> R groupByUniqueKey(
            Function<? super T, ? extends V> function,
            R target);

    /**
     * Returns a string with the elements of this iterable separated by commas with spaces and
     * enclosed in square brackets.
     * <p>
     * <pre>
     * Assert.assertEquals("[]", Lists.mutable.empty().toString());
     * Assert.assertEquals("[1]", Lists.mutable.with(1).toString());
     * Assert.assertEquals("[1, 2, 3]", Lists.mutable.with(1, 2, 3).toString());
     * </pre>
     * @see java.util.AbstractCollection#toString()
     *
     * @return a string representation of this RichIterable
     * @since 1.0
     */
    @Override
    String toString();

    /**
     * Returns a {@code RichIterable} formed from this {@code RichIterable} and another {@code RichIterable} by
     * combining corresponding elements in pairs. If one of the two {@code RichIterable}s is longer than the other, its
     * remaining elements are ignored.
     *
     * @param that The {@code RichIterable} providing the second half of each result pair
     * @param <S>  the type of the second half of the returned pairs
     * @return A new {@code RichIterable} containing pairs consisting of corresponding elements of this {@code
     * RichIterable} and that. The length of the returned {@code RichIterable} is the minimum of the lengths of
     * this {@code RichIterable} and that.
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable)} instead.
     */
    @Deprecated
    <S> RichIterable<Pair<T, S>> zip(Iterable<S> that);

    /**
     * Same as {@link #zip(Iterable)} but uses {@code target} for output.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zip(Iterable, Collection)} instead;
     */
    @Deprecated
    <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target);

    /**
     * Zips this {@code RichIterable} with its indices.
     *
     * @return A new {@code RichIterable} containing pairs consisting of all elements of this {@code RichIterable}
     * paired with their index. Indices start at 0.
     * @see #zip(Iterable)
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex()} instead.
     */
    @Deprecated
    RichIterable<Pair<T, Integer>> zipWithIndex();

    /**
     * Same as {@link #zipWithIndex()} but uses {@code target} for output.
     *
     * @since 1.0
     * @deprecated in 6.0. Use {@link OrderedIterable#zipWithIndex(Collection)} instead.
     */
    @Deprecated
    <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target);

    /**
     * Partitions elements in fixed size chunks.
     *
     * @param size the number of elements per chunk
     * @return A {@code RichIterable} containing {@code RichIterable}s of size {@code size}, except the last will be
     * truncated if the elements don't divide evenly.
     * @since 1.0
     */
    RichIterable<RichIterable<T>> chunk(int size);

    /**
     * Applies an aggregate procedure over the iterable grouping results into a Map based on the specific groupBy function.
     * Aggregate results are required to be mutable as they will be changed in place by the procedure.  A second function
     * specifies the initial "zero" aggregate value to work with (i.e. new AtomicInteger(0)).
     *
     * @since 3.0
     */
    <K, V> MapIterable<K, V> aggregateInPlaceBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Procedure2<? super V, ? super T> mutatingAggregator);

    /**
     * Applies an aggregate function over the iterable grouping results into a map based on the specific groupBy function.
     * Aggregate results are allowed to be immutable as they will be replaced in place in the map.  A second function
     * specifies the initial "zero" aggregate value to work with (i.e. Integer.valueOf(0)).
     *
     * @since 3.0
     */
    <K, V> MapIterable<K, V> aggregateBy(Function<? super T, ? extends K> groupBy, Function0<? extends V> zeroValueFactory, Function2<? super V, ? super T, ? extends V> nonMutatingAggregator);
}
