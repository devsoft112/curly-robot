/*
 * Copyright (c) 2016 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.map.strategy.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.collections.api.block.HashingStrategy;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.block.procedure.Procedure;
import org.eclipse.collections.api.block.procedure.Procedure2;
import org.eclipse.collections.api.block.procedure.primitive.ObjectIntProcedure;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.UnsortedMapIterable;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.block.factory.Predicates;
import org.eclipse.collections.impl.block.procedure.MapCollectProcedure;
import org.eclipse.collections.impl.factory.HashingStrategyMaps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.AbstractMutableMap;
import org.eclipse.collections.impl.parallel.BatchIterable;
import org.eclipse.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import org.eclipse.collections.impl.tuple.ImmutableEntry;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * UnifiedMapWithHashingStrategy stores key/value pairs in a single array, where alternate slots are keys and values.
 * This is nicer to CPU caches as consecutive memory addresses are very cheap to access.  Entry objects are not stored in the
 * table like in java.util.HashMap. Instead of trying to deal with collisions in the main array using Entry objects,
 * we put a special object in the key slot and put a regular Object[] in the value slot. The array contains the key value
 * pairs in consecutive slots, just like the main array, but it's a linear list with no hashing.
 * <p>
 * The difference between UnifiedMap and UnifiedMapWithHashingStrategy is that a HashingStrategy based UnifiedMap
 * does not rely on the hashCode or equality of the object at the key, but instead relies on a HashingStrategy
 * implementation provided by a developer to compute the hashCode and equals for the objects stored in the map.
 */

@SuppressWarnings("ObjectEquality")
public class UnifiedMapWithHashingStrategy<K, V> extends AbstractMutableMap<K, V>
        implements Externalizable, BatchIterable<V>
{
    protected static final Object NULL_KEY = new Object()
    {
        @Override
        public boolean equals(Object obj)
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public int hashCode()
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public String toString()
        {
            return "UnifiedMapWithHashingStrategy.NULL_KEY";
        }
    };

    protected static final Object CHAINED_KEY = new Object()
    {
        @Override
        public boolean equals(Object obj)
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public int hashCode()
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public String toString()
        {
            return "UnifiedMapWithHashingStrategy.CHAINED_KEY";
        }
    };

    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;

    protected static final int DEFAULT_INITIAL_CAPACITY = 8;

    private static final long serialVersionUID = 1L;

    protected transient Object[] table;

    protected transient int occupied;

    protected float loadFactor = DEFAULT_LOAD_FACTOR;

    protected int maxSize;

    private HashingStrategy<? super K> hashingStrategy;

    /**
     * @deprecated No argument default constructor used for serialization. Instantiating an UnifiedMapWithHashingStrategyMultimap with
     * this constructor will have a null hashingStrategy and throw NullPointerException when used.
     */
    @Deprecated
    public UnifiedMapWithHashingStrategy()
    {
    }

    public UnifiedMapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy)
    {
        this.hashingStrategy = hashingStrategy;
        this.allocate(DEFAULT_INITIAL_CAPACITY << 1);
    }

    public UnifiedMapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy, int initialCapacity)
    {
        this(hashingStrategy, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public UnifiedMapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy, int initialCapacity, float loadFactor)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("initial capacity cannot be less than 0");
        }
        if (loadFactor <= 0.0)
        {
            throw new IllegalArgumentException("load factor cannot be less than or equal to 0");
        }
        if (loadFactor > 1.0)
        {
            throw new IllegalArgumentException("load factor cannot be greater than 1");
        }

        this.hashingStrategy = hashingStrategy;
        this.loadFactor = loadFactor;
        this.init(this.fastCeil(initialCapacity / loadFactor));
    }

    public UnifiedMapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy, Map<? extends K, ? extends V> map)
    {
        this(hashingStrategy, Math.max(map.size(), DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);

        this.putAll(map);
    }

    public UnifiedMapWithHashingStrategy(HashingStrategy<? super K> hashingStrategy, Pair<K, V>... pairs)
    {
        this(hashingStrategy, Math.max(pairs.length, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        ArrayIterate.forEach(pairs, new MapCollectProcedure<Pair<K, V>, K, V>(
                this,
                Functions.firstOfPair(),
                Functions.secondOfPair()));
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newMap(HashingStrategy<? super K> hashingStrategy)
    {
        return new UnifiedMapWithHashingStrategy<>(hashingStrategy);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newMap(
            HashingStrategy<? super K> hashingStrategy,
            int size)
    {
        return new UnifiedMapWithHashingStrategy<>(hashingStrategy, size);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newMap(
            HashingStrategy<? super K> hashingStrategy,
            int size,
            float loadFactor)
    {
        return new UnifiedMapWithHashingStrategy<>(hashingStrategy, size, loadFactor);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newMap(
            HashingStrategy<? super K> hashingStrategy,
            Map<? extends K, ? extends V> map)
    {
        return new UnifiedMapWithHashingStrategy<>(hashingStrategy, map);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newMapWith(HashingStrategy<? super K> hashingStrategy, Iterable<Pair<K, V>> inputIterable)
    {
        UnifiedMapWithHashingStrategy<K, V> outputMap = UnifiedMapWithHashingStrategy.newMap(hashingStrategy);

        for (Pair<K, V> single : inputIterable)
        {
            outputMap.add(single);
        }
        return outputMap;
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newMap(UnifiedMapWithHashingStrategy<K, V> map)
    {
        return new UnifiedMapWithHashingStrategy<>(map.hashingStrategy, map);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newMapWith(
            HashingStrategy<? super K> hashingStrategy,
            Pair<K, V>... pairs)
    {
        return new UnifiedMapWithHashingStrategy<>(hashingStrategy, pairs);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newWithKeysValues(
            HashingStrategy<? super K> hashingStrategy,
            K key, V value)
    {
        return new UnifiedMapWithHashingStrategy<K, V>(hashingStrategy, 1).withKeysValues(key, value);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newWithKeysValues(
            HashingStrategy<? super K> hashingStrategy,
            K key1, V value1,
            K key2, V value2)
    {
        return new UnifiedMapWithHashingStrategy<K, V>(hashingStrategy, 2).withKeysValues(key1, value1, key2, value2);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newWithKeysValues(
            HashingStrategy<? super K> hashingStrategy,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        return new UnifiedMapWithHashingStrategy<K, V>(hashingStrategy, 3).withKeysValues(key1, value1, key2, value2, key3, value3);
    }

    public static <K, V> UnifiedMapWithHashingStrategy<K, V> newWithKeysValues(
            HashingStrategy<? super K> hashingStrategy,
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        return new UnifiedMapWithHashingStrategy<K, V>(hashingStrategy, 4).withKeysValues(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    public UnifiedMapWithHashingStrategy<K, V> withKeysValues(K key, V value)
    {
        this.put(key, value);
        return this;
    }

    public UnifiedMapWithHashingStrategy<K, V> withKeysValues(K key1, V value1, K key2, V value2)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        return this;
    }

    public UnifiedMapWithHashingStrategy<K, V> withKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        this.put(key3, value3);
        return this;
    }

    public UnifiedMapWithHashingStrategy<K, V> withKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        this.put(key1, value1);
        this.put(key2, value2);
        this.put(key3, value3);
        this.put(key4, value4);
        return this;
    }

    public HashingStrategy<? super K> hashingStrategy()
    {
        return this.hashingStrategy;
    }

    @Override
    public UnifiedMapWithHashingStrategy<K, V> clone()
    {
        return new UnifiedMapWithHashingStrategy<>(this.hashingStrategy, this);
    }

    @Override
    public MutableMap<K, V> newEmpty()
    {
        return new UnifiedMapWithHashingStrategy<>(this.hashingStrategy);
    }

    @Override
    public MutableMap<K, V> newEmpty(int capacity)
    {
        return UnifiedMapWithHashingStrategy.newMap(this.hashingStrategy, capacity);
    }

    private int fastCeil(float v)
    {
        int possibleResult = (int) v;
        if (v - possibleResult > 0.0F)
        {
            possibleResult++;
        }
        return possibleResult;
    }

    protected int init(int initialCapacity)
    {
        int capacity = 1;
        while (capacity < initialCapacity)
        {
            capacity <<= 1;
        }

        return this.allocate(capacity);
    }

    protected int allocate(int capacity)
    {
        this.allocateTable(capacity << 1); // the table size is twice the capacity to handle both keys and values
        this.computeMaxSize(capacity);

        return capacity;
    }

    protected void allocateTable(int sizeToAllocate)
    {
        this.table = new Object[sizeToAllocate];
    }

    protected void computeMaxSize(int capacity)
    {
        // need at least one free slot for open addressing
        this.maxSize = Math.min(capacity - 1, (int) (capacity * this.loadFactor));
    }

    protected final int index(K key)
    {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        int h = this.hashingStrategy.computeHashCode(key);
        h ^= h >>> 20 ^ h >>> 12;
        h ^= h >>> 7 ^ h >>> 4;
        return (h & (this.table.length >> 1) - 1) << 1;
    }

    @Override
    public void clear()
    {
        if (this.occupied == 0)
        {
            return;
        }
        this.occupied = 0;
        Object[] set = this.table;

        for (int i = set.length; i-- > 0; )
        {
            set[i] = null;
        }
    }

    @Override
    public V put(K key, V value)
    {
        int index = this.index(key);
        Object cur = this.table[index];
        if (cur == null)
        {
            this.table[index] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            this.table[index + 1] = value;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
            return null;
        }
        if (cur != CHAINED_KEY && this.nonNullTableObjectEquals(cur, key))
        {
            V result = (V) this.table[index + 1];
            this.table[index + 1] = value;
            return result;
        }
        return this.chainedPut(key, index, value);
    }

    private V chainedPut(K key, int index, V value)
    {
        if (this.table[index] == CHAINED_KEY)
        {
            Object[] chain = (Object[]) this.table[index + 1];
            for (int i = 0; i < chain.length; i += 2)
            {
                if (chain[i] == null)
                {
                    chain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                    chain[i + 1] = value;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash(this.table.length);
                    }
                    return null;
                }
                if (this.nonNullTableObjectEquals(chain[i], key))
                {
                    V result = (V) chain[i + 1];
                    chain[i + 1] = value;
                    return result;
                }
            }
            Object[] newChain = new Object[chain.length + 4];
            System.arraycopy(chain, 0, newChain, 0, chain.length);
            this.table[index + 1] = newChain;
            newChain[chain.length] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            newChain[chain.length + 1] = value;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
            return null;
        }
        Object[] newChain = new Object[4];
        newChain[0] = this.table[index];
        newChain[1] = this.table[index + 1];
        newChain[2] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
        newChain[3] = value;
        this.table[index] = CHAINED_KEY;
        this.table[index + 1] = newChain;
        if (++this.occupied > this.maxSize)
        {
            this.rehash(this.table.length);
        }
        return null;
    }

    @Override
    public V updateValue(K key, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        int index = this.index(key);
        Object cur = this.table[index];
        if (cur == null)
        {
            this.table[index] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            V result = function.valueOf(factory.value());
            this.table[index + 1] = result;
            ++this.occupied;
            return result;
        }
        if (cur != CHAINED_KEY && this.nonNullTableObjectEquals(cur, key))
        {
            V oldValue = (V) this.table[index + 1];
            V newValue = function.valueOf(oldValue);
            this.table[index + 1] = newValue;
            return newValue;
        }
        return this.chainedUpdateValue(key, index, factory, function);
    }

    private V chainedUpdateValue(K key, int index, Function0<? extends V> factory, Function<? super V, ? extends V> function)
    {
        if (this.table[index] == CHAINED_KEY)
        {
            Object[] chain = (Object[]) this.table[index + 1];
            for (int i = 0; i < chain.length; i += 2)
            {
                if (chain[i] == null)
                {
                    chain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                    V result = function.valueOf(factory.value());
                    chain[i + 1] = result;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash(this.table.length);
                    }
                    return result;
                }
                if (this.nonNullTableObjectEquals(chain[i], key))
                {
                    V oldValue = (V) chain[i + 1];
                    V result = function.valueOf(oldValue);
                    chain[i + 1] = result;
                    return result;
                }
            }
            Object[] newChain = new Object[chain.length + 4];
            System.arraycopy(chain, 0, newChain, 0, chain.length);
            this.table[index + 1] = newChain;
            newChain[chain.length] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            V result = function.valueOf(factory.value());
            newChain[chain.length + 1] = result;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
            return result;
        }
        Object[] newChain = new Object[4];
        newChain[0] = this.table[index];
        newChain[1] = this.table[index + 1];
        newChain[2] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
        V result = function.valueOf(factory.value());
        newChain[3] = result;
        this.table[index] = CHAINED_KEY;
        this.table[index + 1] = newChain;
        if (++this.occupied > this.maxSize)
        {
            this.rehash(this.table.length);
        }
        return result;
    }

    @Override
    public <P> V updateValueWith(K key, Function0<? extends V> factory, Function2<? super V, ? super P, ? extends V> function, P parameter)
    {
        int index = this.index(key);
        Object cur = this.table[index];
        if (cur == null)
        {
            this.table[index] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            V result = function.value(factory.value(), parameter);
            this.table[index + 1] = result;
            ++this.occupied;
            return result;
        }
        if (cur != CHAINED_KEY && this.nonNullTableObjectEquals(cur, key))
        {
            V oldValue = (V) this.table[index + 1];
            V newValue = function.value(oldValue, parameter);
            this.table[index + 1] = newValue;
            return newValue;
        }
        return this.chainedUpdateValueWith(key, index, factory, function, parameter);
    }

    private <P> V chainedUpdateValueWith(
            K key,
            int index,
            Function0<? extends V> factory,
            Function2<? super V, ? super P, ? extends V> function,
            P parameter)
    {
        if (this.table[index] == CHAINED_KEY)
        {
            Object[] chain = (Object[]) this.table[index + 1];
            for (int i = 0; i < chain.length; i += 2)
            {
                if (chain[i] == null)
                {
                    chain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                    V result = function.value(factory.value(), parameter);
                    chain[i + 1] = result;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash(this.table.length);
                    }
                    return result;
                }
                if (this.nonNullTableObjectEquals(chain[i], key))
                {
                    V oldValue = (V) chain[i + 1];
                    V result = function.value(oldValue, parameter);
                    chain[i + 1] = result;
                    return result;
                }
            }
            Object[] newChain = new Object[chain.length + 4];
            System.arraycopy(chain, 0, newChain, 0, chain.length);
            this.table[index + 1] = newChain;
            newChain[chain.length] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            V result = function.value(factory.value(), parameter);
            newChain[chain.length + 1] = result;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
            return result;
        }
        Object[] newChain = new Object[4];
        newChain[0] = this.table[index];
        newChain[1] = this.table[index + 1];
        newChain[2] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
        V result = function.value(factory.value(), parameter);
        newChain[3] = result;
        this.table[index] = CHAINED_KEY;
        this.table[index + 1] = newChain;
        if (++this.occupied > this.maxSize)
        {
            this.rehash(this.table.length);
        }
        return result;
    }

    @Override
    public V getIfAbsentPut(K key, Function0<? extends V> function)
    {
        int index = this.index(key);
        Object cur = this.table[index];

        if (cur == null)
        {
            V result = function.value();
            this.table[index] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            this.table[index + 1] = result;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
            return result;
        }
        if (cur != CHAINED_KEY && this.nonNullTableObjectEquals(cur, key))
        {
            return (V) this.table[index + 1];
        }
        return this.chainedGetIfAbsentPut(key, index, function);
    }

    private V chainedGetIfAbsentPut(K key, int index, Function0<? extends V> function)
    {
        V result = null;
        if (this.table[index] == CHAINED_KEY)
        {
            Object[] chain = (Object[]) this.table[index + 1];
            int i = 0;
            for (; i < chain.length; i += 2)
            {
                if (chain[i] == null)
                {
                    result = function.value();
                    chain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                    chain[i + 1] = result;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash(this.table.length);
                    }
                    break;
                }
                if (this.nonNullTableObjectEquals(chain[i], key))
                {
                    result = (V) chain[i + 1];
                    break;
                }
            }
            if (i == chain.length)
            {
                result = function.value();
                Object[] newChain = new Object[chain.length + 4];
                System.arraycopy(chain, 0, newChain, 0, chain.length);
                newChain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                newChain[i + 1] = result;
                this.table[index + 1] = newChain;
                if (++this.occupied > this.maxSize)
                {
                    this.rehash(this.table.length);
                }
            }
        }
        else
        {
            result = function.value();
            Object[] newChain = new Object[4];
            newChain[0] = this.table[index];
            newChain[1] = this.table[index + 1];
            newChain[2] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            newChain[3] = result;
            this.table[index] = CHAINED_KEY;
            this.table[index + 1] = newChain;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
        }
        return result;
    }

    @Override
    public V getIfAbsentPut(K key, V value)
    {
        int index = this.index(key);
        Object cur = this.table[index];

        if (cur == null)
        {
            this.table[index] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            this.table[index + 1] = value;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
            return value;
        }
        if (cur != CHAINED_KEY && this.nonNullTableObjectEquals(cur, key))
        {
            return (V) this.table[index + 1];
        }
        return this.chainedGetIfAbsentPut(key, index, value);
    }

    private V chainedGetIfAbsentPut(K key, int index, V value)
    {
        V result = value;
        if (this.table[index] == CHAINED_KEY)
        {
            Object[] chain = (Object[]) this.table[index + 1];
            int i = 0;
            for (; i < chain.length; i += 2)
            {
                if (chain[i] == null)
                {
                    chain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                    chain[i + 1] = value;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash(this.table.length);
                    }
                    break;
                }
                if (this.nonNullTableObjectEquals(chain[i], key))
                {
                    result = (V) chain[i + 1];
                    break;
                }
            }
            if (i == chain.length)
            {
                Object[] newChain = new Object[chain.length + 4];
                System.arraycopy(chain, 0, newChain, 0, chain.length);
                newChain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                newChain[i + 1] = value;
                this.table[index + 1] = newChain;
                if (++this.occupied > this.maxSize)
                {
                    this.rehash(this.table.length);
                }
            }
        }
        else
        {
            Object[] newChain = new Object[4];
            newChain[0] = this.table[index];
            newChain[1] = this.table[index + 1];
            newChain[2] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            newChain[3] = value;
            this.table[index] = CHAINED_KEY;
            this.table[index + 1] = newChain;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
        }
        return result;
    }

    @Override
    public <P> V getIfAbsentPutWith(K key, Function<? super P, ? extends V> function, P parameter)
    {
        int index = this.index(key);
        Object cur = this.table[index];

        if (cur == null)
        {
            V result = function.valueOf(parameter);
            this.table[index] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            this.table[index + 1] = result;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
            return result;
        }
        if (cur != CHAINED_KEY && this.nonNullTableObjectEquals(cur, key))
        {
            return (V) this.table[index + 1];
        }
        return this.chainedGetIfAbsentPutWith(key, index, function, parameter);
    }

    private <P> V chainedGetIfAbsentPutWith(K key, int index, Function<? super P, ? extends V> function, P parameter)
    {
        V result = null;
        if (this.table[index] == CHAINED_KEY)
        {
            Object[] chain = (Object[]) this.table[index + 1];
            int i = 0;
            for (; i < chain.length; i += 2)
            {
                if (chain[i] == null)
                {
                    result = function.valueOf(parameter);
                    chain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                    chain[i + 1] = result;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash(this.table.length);
                    }
                    break;
                }
                if (this.nonNullTableObjectEquals(chain[i], key))
                {
                    result = (V) chain[i + 1];
                    break;
                }
            }
            if (i == chain.length)
            {
                result = function.valueOf(parameter);
                Object[] newChain = new Object[chain.length + 4];
                System.arraycopy(chain, 0, newChain, 0, chain.length);
                newChain[i] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
                newChain[i + 1] = result;
                this.table[index + 1] = newChain;
                if (++this.occupied > this.maxSize)
                {
                    this.rehash(this.table.length);
                }
            }
        }
        else
        {
            result = function.valueOf(parameter);
            Object[] newChain = new Object[4];
            newChain[0] = this.table[index];
            newChain[1] = this.table[index + 1];
            newChain[2] = UnifiedMapWithHashingStrategy.toSentinelIfNull(key);
            newChain[3] = result;
            this.table[index] = CHAINED_KEY;
            this.table[index + 1] = newChain;
            if (++this.occupied > this.maxSize)
            {
                this.rehash(this.table.length);
            }
        }
        return result;
    }

    public int getCollidingBuckets()
    {
        int count = 0;
        for (int i = 0; i < this.table.length; i += 2)
        {
            if (this.table[i] == CHAINED_KEY)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of JVM words that is used by this map.  A word is 4 bytes in a 32bit VM and 8 bytes in a 64bit
     * VM. Each array has a 2 word header, thus the formula is:
     * words = (internal table length + 2) + sum (for all chains (chain length + 2))
     *
     * @return the number of JVM words that is used by this map.
     */
    public int getMapMemoryUsedInWords()
    {
        int headerSize = 2;
        int sizeInWords = this.table.length + headerSize;
        for (int i = 0; i < this.table.length; i += 2)
        {
            if (this.table[i] == CHAINED_KEY)
            {
                sizeInWords += headerSize + ((Object[]) this.table[i + 1]).length;
            }
        }
        return sizeInWords;
    }

    protected void rehash(int newCapacity)
    {
        int oldLength = this.table.length;
        Object[] old = this.table;
        this.allocate(newCapacity);
        this.occupied = 0;

        for (int i = 0; i < oldLength; i += 2)
        {
            Object cur = old[i];
            if (cur == CHAINED_KEY)
            {
                Object[] chain = (Object[]) old[i + 1];
                for (int j = 0; j < chain.length; j += 2)
                {
                    if (chain[j] != null)
                    {
                        this.put(this.nonSentinel(chain[j]), (V) chain[j + 1]);
                    }
                }
            }
            else if (cur != null)
            {
                this.put(this.nonSentinel(cur), (V) old[i + 1]);
            }
        }
    }

    @Override
    public V get(Object key)
    {
        int index = this.index((K) key);
        Object cur = this.table[index];
        if (cur != null)
        {
            Object val = this.table[index + 1];
            if (cur == CHAINED_KEY)
            {
                return this.getFromChain((Object[]) val, (K) key);
            }
            if (this.nonNullTableObjectEquals(cur, (K) key))
            {
                return (V) val;
            }
        }
        return null;
    }

    private V getFromChain(Object[] chain, K key)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object k = chain[i];
            if (k == null)
            {
                return null;
            }
            if (this.nonNullTableObjectEquals(k, key))
            {
                return (V) chain[i + 1];
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key)
    {
        int index = this.index((K) key);
        Object cur = this.table[index];
        if (cur == null)
        {
            return false;
        }
        if (cur != CHAINED_KEY && this.nonNullTableObjectEquals(cur, (K) key))
        {
            return true;
        }
        return cur == CHAINED_KEY && this.chainContainsKey((Object[]) this.table[index + 1], (K) key);
    }

    private boolean chainContainsKey(Object[] chain, K key)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object k = chain[i];
            if (k == null)
            {
                return false;
            }
            if (this.nonNullTableObjectEquals(k, key))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value)
    {
        for (int i = 0; i < this.table.length; i += 2)
        {
            if (this.table[i] == CHAINED_KEY)
            {
                if (this.chainedContainsValue((Object[]) this.table[i + 1], (V) value))
                {
                    return true;
                }
            }
            else if (this.table[i] != null)
            {
                if (UnifiedMapWithHashingStrategy.nullSafeEquals(value, this.table[i + 1]))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean chainedContainsValue(Object[] chain, V value)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            if (chain[i] == null)
            {
                return false;
            }
            if (UnifiedMapWithHashingStrategy.nullSafeEquals(value, chain[i + 1]))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void forEachKeyValue(Procedure2<? super K, ? super V> procedure)
    {
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                this.chainedForEachEntry((Object[]) this.table[i + 1], procedure);
            }
            else if (cur != null)
            {
                procedure.value(this.nonSentinel(cur), (V) this.table[i + 1]);
            }
        }
    }

    @Override
    public V getFirst()
    {
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                Object[] chain = (Object[]) this.table[i + 1];
                return (V) chain[1];
            }
            if (cur != null)
            {
                return (V) this.table[i + 1];
            }
        }
        return null;
    }

    @Override
    public <E> MutableMap<K, V> collectKeysAndValues(
            Iterable<E> iterable,
            Function<? super E, ? extends K> keyFunction,
            Function<? super E, ? extends V> valueFunction)
    {
        Iterate.forEach(iterable, new MapCollectProcedure<>(this, keyFunction, valueFunction));
        return this;
    }

    @Override
    public V removeKey(K key)
    {
        return this.remove(key);
    }

    private void chainedForEachEntry(Object[] chain, Procedure2<? super K, ? super V> procedure)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return;
            }
            procedure.value(this.nonSentinel(cur), (V) chain[i + 1]);
        }
    }

    @Override
    public int getBatchCount(int batchSize)
    {
        return Math.max(1, this.table.length / 2 / batchSize);
    }

    @Override
    public void batchForEach(Procedure<? super V> procedure, int sectionIndex, int sectionCount)
    {
        int sectionSize = this.table.length / sectionCount;
        int start = sectionIndex * sectionSize;
        int end = sectionIndex == sectionCount - 1 ? this.table.length : start + sectionSize;
        if (start % 2 == 0)
        {
            start++;
        }
        for (int i = start; i < end; i += 2)
        {
            Object value = this.table[i];
            if (value instanceof Object[])
            {
                this.chainedForEachValue((Object[]) value, procedure);
            }
            else if (value == null && this.table[i - 1] != null || value != null)
            {
                procedure.value((V) value);
            }
        }
    }

    @Override
    public void forEachKey(Procedure<? super K> procedure)
    {
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                this.chainedForEachKey((Object[]) this.table[i + 1], procedure);
            }
            else if (cur != null)
            {
                procedure.value(this.nonSentinel(cur));
            }
        }
    }

    private void chainedForEachKey(Object[] chain, Procedure<? super K> procedure)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return;
            }
            procedure.value(this.nonSentinel(cur));
        }
    }

    @Override
    public void forEachValue(Procedure<? super V> procedure)
    {
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                this.chainedForEachValue((Object[]) this.table[i + 1], procedure);
            }
            else if (cur != null)
            {
                procedure.value((V) this.table[i + 1]);
            }
        }
    }

    private void chainedForEachValue(Object[] chain, Procedure<? super V> procedure)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return;
            }
            procedure.value((V) chain[i + 1]);
        }
    }

    @Override
    public boolean isEmpty()
    {
        return this.occupied == 0;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        if (map instanceof UnifiedMapWithHashingStrategy<?, ?>)
        {
            this.copyMap((UnifiedMapWithHashingStrategy<K, V>) map);
        }
        else if (map instanceof UnsortedMapIterable)
        {
            MapIterable<K, V> mapIterable = (MapIterable<K, V>) map;
            mapIterable.forEachKeyValue(this::put);
        }
        else
        {
            Iterator<? extends Entry<? extends K, ? extends V>> iterator = this.getEntrySetFrom(map).iterator();
            while (iterator.hasNext())
            {
                Entry<? extends K, ? extends V> entry = iterator.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private Set<? extends Entry<? extends K, ? extends V>> getEntrySetFrom(Map<? extends K, ? extends V> map)
    {
        Set<? extends Entry<? extends K, ? extends V>> entries = map.entrySet();
        if (entries != null)
        {
            return entries;
        }
        if (map.isEmpty())
        {
            return Sets.immutable.<Entry<K, V>>of().castToSet();
        }
        throw new IllegalStateException("Entry set was null and size was non-zero");
    }

    protected void copyMap(UnifiedMapWithHashingStrategy<K, V> unifiedMap)
    {
        for (int i = 0; i < unifiedMap.table.length; i += 2)
        {
            Object cur = unifiedMap.table[i];
            if (cur == CHAINED_KEY)
            {
                this.copyChain((Object[]) unifiedMap.table[i + 1]);
            }
            else if (cur != null)
            {
                this.put(this.nonSentinel(cur), (V) unifiedMap.table[i + 1]);
            }
        }
    }

    private void copyChain(Object[] chain)
    {
        for (int j = 0; j < chain.length; j += 2)
        {
            Object cur = chain[j];
            if (cur == null)
            {
                break;
            }
            this.put(this.nonSentinel(cur), (V) chain[j + 1]);
        }
    }

    @Override
    public V remove(Object key)
    {
        int index = this.index((K) key);
        Object cur = this.table[index];
        if (cur != null)
        {
            Object val = this.table[index + 1];
            if (cur == CHAINED_KEY)
            {
                return this.removeFromChain((Object[]) val, (K) key, index);
            }
            if (this.nonNullTableObjectEquals(cur, (K) key))
            {
                this.table[index] = null;
                this.table[index + 1] = null;
                this.occupied--;
                return (V) val;
            }
        }
        return null;
    }

    private V removeFromChain(Object[] chain, K key, int index)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object k = chain[i];
            if (k == null)
            {
                return null;
            }
            if (this.nonNullTableObjectEquals(k, key))
            {
                V val = (V) chain[i + 1];
                this.overwriteWithLastElementFromChain(chain, index, i);
                return val;
            }
        }
        return null;
    }

    private void overwriteWithLastElementFromChain(Object[] chain, int index, int i)
    {
        int j = chain.length - 2;
        for (; j > i; j -= 2)
        {
            if (chain[j] != null)
            {
                chain[i] = chain[j];
                chain[i + 1] = chain[j + 1];
                break;
            }
        }
        chain[j] = null;
        chain[j + 1] = null;
        if (j == 0)
        {
            this.table[index] = null;
            this.table[index + 1] = null;
        }
        this.occupied--;
    }

    @Override
    public int size()
    {
        return this.occupied;
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return new EntrySet();
    }

    @Override
    public Set<K> keySet()
    {
        return new KeySet();
    }

    @Override
    public Collection<V> values()
    {
        return new ValuesCollection();
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (!(object instanceof Map))
        {
            return false;
        }

        Map<?, ?> other = (Map<?, ?>) object;
        if (this.size() != other.size())
        {
            return false;
        }

        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                if (!this.chainedEquals((Object[]) this.table[i + 1], other))
                {
                    return false;
                }
            }
            else if (cur != null)
            {
                K key = this.nonSentinel(cur);
                V value = (V) this.table[i + 1];
                Object otherValue = other.get(key);
                if (!UnifiedMapWithHashingStrategy.nullSafeEquals(otherValue, value) || (value == null && otherValue == null && !other.containsKey(key)))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean chainedEquals(Object[] chain, Map<?, ?> other)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return true;
            }
            K key = this.nonSentinel(cur);
            V value = (V) chain[i + 1];
            Object otherValue = other.get(key);
            if (!UnifiedMapWithHashingStrategy.nullSafeEquals(otherValue, value) || (value == null && otherValue == null && !other.containsKey(key)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hashCode = 0;
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                hashCode += this.chainedHashCode((Object[]) this.table[i + 1]);
            }
            else if (cur != null)
            {
                Object value = this.table[i + 1];
                hashCode += this.hashingStrategy.computeHashCode(this.nonSentinel(cur)) ^ (value == null ? 0 : value.hashCode());
            }
        }
        return hashCode;
    }

    private int chainedHashCode(Object[] chain)
    {
        int hashCode = 0;
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return hashCode;
            }
            Object value = chain[i + 1];
            hashCode += this.hashingStrategy.computeHashCode(this.nonSentinel(cur)) ^ (value == null ? 0 : value.hashCode());
        }
        return hashCode;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append('{');

        this.forEachKeyValue(new Procedure2<K, V>()
        {
            private boolean first = true;

            public void value(K key, V value)
            {
                if (this.first)
                {
                    this.first = false;
                }
                else
                {
                    builder.append(", ");
                }

                builder.append(key == UnifiedMapWithHashingStrategy.this ? "(this Map)" : key);
                builder.append('=');
                builder.append(value == UnifiedMapWithHashingStrategy.this ? "(this Map)" : value);
            }
        });

        builder.append('}');
        return builder.toString();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        this.hashingStrategy = (HashingStrategy<? super K>) in.readObject();
        int size = in.readInt();
        this.loadFactor = in.readFloat();
        this.init(Math.max((int) (size / this.loadFactor) + 1,
                DEFAULT_INITIAL_CAPACITY));
        for (int i = 0; i < size; i++)
        {
            this.put((K) in.readObject(), (V) in.readObject());
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(this.hashingStrategy);
        out.writeInt(this.size());
        out.writeFloat(this.loadFactor);
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object o = this.table[i];
            if (o != null)
            {
                if (o == CHAINED_KEY)
                {
                    this.writeExternalChain(out, (Object[]) this.table[i + 1]);
                }
                else
                {
                    out.writeObject(this.nonSentinel(o));
                    out.writeObject(this.table[i + 1]);
                }
            }
        }
    }

    private void writeExternalChain(ObjectOutput out, Object[] chain) throws IOException
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return;
            }
            out.writeObject(this.nonSentinel(cur));
            out.writeObject(chain[i + 1]);
        }
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super V> objectIntProcedure)
    {
        int index = 0;
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                index = this.chainedForEachValueWithIndex((Object[]) this.table[i + 1], objectIntProcedure, index);
            }
            else if (cur != null)
            {
                objectIntProcedure.value((V) this.table[i + 1], index++);
            }
        }
    }

    private int chainedForEachValueWithIndex(Object[] chain, ObjectIntProcedure<? super V> objectIntProcedure, int index)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return index;
            }
            objectIntProcedure.value((V) chain[i + 1], index++);
        }
        return index;
    }

    @Override
    public <P> void forEachWith(Procedure2<? super V, ? super P> procedure, P parameter)
    {
        for (int i = 0; i < this.table.length; i += 2)
        {
            Object cur = this.table[i];
            if (cur == CHAINED_KEY)
            {
                this.chainedForEachValueWith((Object[]) this.table[i + 1], procedure, parameter);
            }
            else if (cur != null)
            {
                procedure.value((V) this.table[i + 1], parameter);
            }
        }
    }

    private <P> void chainedForEachValueWith(
            Object[] chain,
            Procedure2<? super V, ? super P> procedure,
            P parameter)
    {
        for (int i = 0; i < chain.length; i += 2)
        {
            Object cur = chain[i];
            if (cur == null)
            {
                return;
            }
            procedure.value((V) chain[i + 1], parameter);
        }
    }

    @Override
    public <R> MutableMap<K, R> collectValues(Function2<? super K, ? super V, ? extends R> function)
    {
        UnifiedMapWithHashingStrategy<K, R> target = UnifiedMapWithHashingStrategy.newMap(this.hashingStrategy);
        target.loadFactor = this.loadFactor;
        target.occupied = this.occupied;
        target.allocate(this.table.length >> 1);

        for (int i = 0; i < target.table.length; i += 2)
        {
            target.table[i] = this.table[i];

            if (this.table[i] == CHAINED_KEY)
            {
                Object[] chainedTable = (Object[]) this.table[i + 1];
                Object[] chainedTargetTable = new Object[chainedTable.length];
                for (int j = 0; j < chainedTargetTable.length; j += 2)
                {
                    if (chainedTable[j] != null)
                    {
                        chainedTargetTable[j] = chainedTable[j];
                        chainedTargetTable[j + 1] = function.value(this.nonSentinel(chainedTable[j]), (V) chainedTable[j + 1]);
                    }
                }
                target.table[i + 1] = chainedTargetTable;
            }
            else if (this.table[i] != null)
            {
                target.table[i + 1] = function.value(this.nonSentinel(this.table[i]), (V) this.table[i + 1]);
            }
        }

        return target;
    }

    protected class KeySet implements Set<K>, Serializable, BatchIterable<K>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean add(K key)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean addAll(Collection<? extends K> collection)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public void clear()
        {
            UnifiedMapWithHashingStrategy.this.clear();
        }

        @Override
        public boolean contains(Object o)
        {
            return UnifiedMapWithHashingStrategy.this.containsKey(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            for (Object aCollection : collection)
            {
                if (!UnifiedMapWithHashingStrategy.this.containsKey(aCollection))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isEmpty()
        {
            return UnifiedMapWithHashingStrategy.this.isEmpty();
        }

        @Override
        public Iterator<K> iterator()
        {
            return new KeySetIterator();
        }

        @Override
        public boolean remove(Object key)
        {
            int oldSize = UnifiedMapWithHashingStrategy.this.occupied;
            UnifiedMapWithHashingStrategy.this.remove(key);
            return UnifiedMapWithHashingStrategy.this.occupied != oldSize;
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            int oldSize = UnifiedMapWithHashingStrategy.this.occupied;
            for (Object object : collection)
            {
                UnifiedMapWithHashingStrategy.this.remove(object);
            }
            return oldSize != UnifiedMapWithHashingStrategy.this.occupied;
        }

        public void putIfFound(Object key, Map<K, V> other)
        {
            int index = UnifiedMapWithHashingStrategy.this.index((K) key);
            Object cur = UnifiedMapWithHashingStrategy.this.table[index];
            if (cur != null)
            {
                Object val = UnifiedMapWithHashingStrategy.this.table[index + 1];
                if (cur == CHAINED_KEY)
                {
                    this.putIfFoundFromChain((Object[]) val, (K) key, other);
                    return;
                }
                if (UnifiedMapWithHashingStrategy.this.nonNullTableObjectEquals(cur, (K) key))
                {
                    other.put(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) val);
                }
            }
        }

        private void putIfFoundFromChain(Object[] chain, K key, Map<K, V> other)
        {
            for (int i = 0; i < chain.length; i += 2)
            {
                Object k = chain[i];
                if (k == null)
                {
                    return;
                }
                if (UnifiedMapWithHashingStrategy.this.nonNullTableObjectEquals(k, key))
                {
                    other.put(UnifiedMapWithHashingStrategy.this.nonSentinel(k), (V) chain[i + 1]);
                }
            }
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            int retainedSize = collection.size();
            UnifiedMapWithHashingStrategy<K, V> retainedCopy = new UnifiedMapWithHashingStrategy<>(
                    UnifiedMapWithHashingStrategy.this.hashingStrategy, retainedSize, UnifiedMapWithHashingStrategy.this.loadFactor);
            for (Object key : collection)
            {
                this.putIfFound(key, retainedCopy);
            }
            if (retainedCopy.size() < this.size())
            {
                UnifiedMapWithHashingStrategy.this.maxSize = retainedCopy.maxSize;
                UnifiedMapWithHashingStrategy.this.occupied = retainedCopy.occupied;
                UnifiedMapWithHashingStrategy.this.table = retainedCopy.table;
                return true;
            }
            return false;
        }

        @Override
        public int size()
        {
            return UnifiedMapWithHashingStrategy.this.size();
        }

        @Override
        public void forEach(Procedure<? super K> procedure)
        {
            UnifiedMapWithHashingStrategy.this.forEachKey(procedure);
        }

        @Override
        public int getBatchCount(int batchSize)
        {
            return UnifiedMapWithHashingStrategy.this.getBatchCount(batchSize);
        }

        @Override
        public void batchForEach(Procedure<? super K> procedure, int sectionIndex, int sectionCount)
        {
            Object[] map = UnifiedMapWithHashingStrategy.this.table;
            int sectionSize = map.length / sectionCount;
            int start = sectionIndex * sectionSize;
            int end = sectionIndex == sectionCount - 1 ? map.length : start + sectionSize;
            if (start % 2 != 0)
            {
                start++;
            }
            for (int i = start; i < end; i += 2)
            {
                Object cur = map[i];
                if (cur == CHAINED_KEY)
                {
                    UnifiedMapWithHashingStrategy.this.chainedForEachKey((Object[]) map[i + 1], procedure);
                }
                else if (cur != null)
                {
                    procedure.value(UnifiedMapWithHashingStrategy.this.nonSentinel(cur));
                }
            }
        }

        protected void copyKeys(Object[] result)
        {
            Object[] table = UnifiedMapWithHashingStrategy.this.table;
            int count = 0;
            for (int i = 0; i < table.length; i += 2)
            {
                Object x = table[i];
                if (x != null)
                {
                    if (x == CHAINED_KEY)
                    {
                        Object[] chain = (Object[]) table[i + 1];
                        for (int j = 0; j < chain.length; j += 2)
                        {
                            Object cur = chain[j];
                            if (cur == null)
                            {
                                break;
                            }
                            result[count++] = UnifiedMapWithHashingStrategy.this.nonSentinel(cur);
                        }
                    }
                    else
                    {
                        result[count++] = UnifiedMapWithHashingStrategy.this.nonSentinel(x);
                    }
                }
            }
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Set)
            {
                Set<?> other = (Set<?>) obj;
                if (other.size() == this.size())
                {
                    return this.containsAll(other);
                }
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            int hashCode = 0;
            Object[] table = UnifiedMapWithHashingStrategy.this.table;
            for (int i = 0; i < table.length; i += 2)
            {
                Object x = table[i];
                if (x != null)
                {
                    if (x == CHAINED_KEY)
                    {
                        Object[] chain = (Object[]) table[i + 1];
                        for (int j = 0; j < chain.length; j += 2)
                        {
                            Object cur = chain[j];
                            if (cur == null)
                            {
                                break;
                            }
                            hashCode += UnifiedMapWithHashingStrategy.this.hashingStrategy.computeHashCode(UnifiedMapWithHashingStrategy.this.nonSentinel(cur));
                        }
                    }
                    else
                    {
                        hashCode += UnifiedMapWithHashingStrategy.this.hashingStrategy.computeHashCode(UnifiedMapWithHashingStrategy.this.nonSentinel(x));
                    }
                }
            }
            return hashCode;
        }

        @Override
        public String toString()
        {
            return Iterate.makeString(this, "[", ", ", "]");
        }

        @Override
        public Object[] toArray()
        {
            int size = UnifiedMapWithHashingStrategy.this.size();
            Object[] result = new Object[size];
            this.copyKeys(result);
            return result;
        }

        @Override
        public <T> T[] toArray(T[] result)
        {
            int size = UnifiedMapWithHashingStrategy.this.size();
            if (result.length < size)
            {
                result = (T[]) Array.newInstance(result.getClass().getComponentType(), size);
            }
            this.copyKeys(result);
            if (size < result.length)
            {
                result[size] = null;
            }
            return result;
        }

        protected Object writeReplace()
        {
            UnifiedSetWithHashingStrategy<K> replace = UnifiedSetWithHashingStrategy.newSet(
                    UnifiedMapWithHashingStrategy.this.hashingStrategy, UnifiedMapWithHashingStrategy.this.size());
            for (int i = 0; i < UnifiedMapWithHashingStrategy.this.table.length; i += 2)
            {
                Object cur = UnifiedMapWithHashingStrategy.this.table[i];
                if (cur == CHAINED_KEY)
                {
                    this.chainedAddToSet((Object[]) UnifiedMapWithHashingStrategy.this.table[i + 1], replace);
                }
                else if (cur != null)
                {
                    replace.add(UnifiedMapWithHashingStrategy.this.nonSentinel(cur));
                }
            }
            return replace;
        }

        private void chainedAddToSet(Object[] chain, UnifiedSetWithHashingStrategy<K> replace)
        {
            for (int i = 0; i < chain.length; i += 2)
            {
                Object cur = chain[i];
                if (cur == null)
                {
                    return;
                }
                replace.add(UnifiedMapWithHashingStrategy.this.nonSentinel(cur));
            }
        }
    }

    protected abstract class PositionalIterator<T> implements Iterator<T>
    {
        protected int count;
        protected int position;
        protected int chainPosition;
        protected boolean lastReturned;

        @Override
        public boolean hasNext()
        {
            return this.count < UnifiedMapWithHashingStrategy.this.size();
        }

        @Override
        public void remove()
        {
            if (!this.lastReturned)
            {
                throw new IllegalStateException("next() must be called as many times as remove()");
            }
            this.count--;
            UnifiedMapWithHashingStrategy.this.occupied--;

            if (this.chainPosition != 0)
            {
                this.removeFromChain();
                return;
            }

            int pos = this.position - 2;
            Object cur = UnifiedMapWithHashingStrategy.this.table[pos];
            if (cur == CHAINED_KEY)
            {
                this.removeLastFromChain((Object[]) UnifiedMapWithHashingStrategy.this.table[pos + 1], pos);
                return;
            }
            UnifiedMapWithHashingStrategy.this.table[pos] = null;
            UnifiedMapWithHashingStrategy.this.table[pos + 1] = null;
            this.position = pos;
            this.lastReturned = false;
        }

        protected void removeFromChain()
        {
            Object[] chain = (Object[]) UnifiedMapWithHashingStrategy.this.table[this.position + 1];
            int pos = this.chainPosition - 2;
            int replacePos = this.chainPosition;
            while (replacePos < chain.length - 2 && chain[replacePos + 2] != null)
            {
                replacePos += 2;
            }
            chain[pos] = chain[replacePos];
            chain[pos + 1] = chain[replacePos + 1];
            chain[replacePos] = null;
            chain[replacePos + 1] = null;
            this.chainPosition = pos;
            this.lastReturned = false;
        }

        protected void removeLastFromChain(Object[] chain, int tableIndex)
        {
            int pos = chain.length - 2;
            while (chain[pos] == null)
            {
                pos -= 2;
            }
            if (pos == 0)
            {
                UnifiedMapWithHashingStrategy.this.table[tableIndex] = null;
                UnifiedMapWithHashingStrategy.this.table[tableIndex + 1] = null;
            }
            else
            {
                chain[pos] = null;
                chain[pos + 1] = null;
            }
            this.lastReturned = false;
        }
    }

    protected class KeySetIterator extends PositionalIterator<K>
    {
        protected K nextFromChain()
        {
            Object[] chain = (Object[]) UnifiedMapWithHashingStrategy.this.table[this.position + 1];
            Object cur = chain[this.chainPosition];
            this.chainPosition += 2;
            if (this.chainPosition >= chain.length
                    || chain[this.chainPosition] == null)
            {
                this.chainPosition = 0;
                this.position += 2;
            }
            this.lastReturned = true;
            return UnifiedMapWithHashingStrategy.this.nonSentinel(cur);
        }

        @Override
        public K next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException("next() called, but the iterator is exhausted");
            }
            this.count++;
            Object[] table = UnifiedMapWithHashingStrategy.this.table;
            if (this.chainPosition != 0)
            {
                return this.nextFromChain();
            }
            while (table[this.position] == null)
            {
                this.position += 2;
            }
            Object cur = table[this.position];
            if (cur == CHAINED_KEY)
            {
                return this.nextFromChain();
            }
            this.position += 2;
            this.lastReturned = true;
            return UnifiedMapWithHashingStrategy.this.nonSentinel(cur);
        }
    }

    private static boolean nullSafeEquals(Object value, Object other)
    {
        if (value == null)
        {
            if (other == null)
            {
                return true;
            }
        }
        else if (other == value || value.equals(other))
        {
            return true;
        }
        return false;
    }

    protected class EntrySet implements Set<Entry<K, V>>, Serializable, BatchIterable<Entry<K, V>>
    {
        private static final long serialVersionUID = 1L;
        private transient WeakReference<UnifiedMapWithHashingStrategy<K, V>> holder = new WeakReference<>(UnifiedMapWithHashingStrategy.this);

        @Override
        public boolean add(Entry<K, V> entry)
        {
            throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> collection)
        {
            throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
        }

        @Override
        public void clear()
        {
            UnifiedMapWithHashingStrategy.this.clear();
        }

        public boolean containsEntry(Entry<?, ?> entry)
        {
            return this.getEntry(entry) != null;
        }

        private Entry<K, V> getEntry(Entry<?, ?> entry)
        {
            K key = (K) entry.getKey();
            V value = (V) entry.getValue();
            int index = UnifiedMapWithHashingStrategy.this.index(key);

            Object cur = UnifiedMapWithHashingStrategy.this.table[index];
            Object curValue = UnifiedMapWithHashingStrategy.this.table[index + 1];
            if (cur == CHAINED_KEY)
            {
                return this.chainGetEntry((Object[]) curValue, key, value);
            }
            if (cur == null)
            {
                return null;
            }
            if (UnifiedMapWithHashingStrategy.this.nonNullTableObjectEquals(cur, key))
            {
                if (UnifiedMapWithHashingStrategy.nullSafeEquals(value, curValue))
                {
                    return ImmutableEntry.of(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) curValue);
                }
            }
            return null;
        }

        private Entry<K, V> chainGetEntry(Object[] chain, K key, V value)
        {
            for (int i = 0; i < chain.length; i += 2)
            {
                Object cur = chain[i];
                if (cur == null)
                {
                    return null;
                }
                if (UnifiedMapWithHashingStrategy.this.nonNullTableObjectEquals(cur, key))
                {
                    Object curValue = chain[i + 1];
                    if (UnifiedMapWithHashingStrategy.nullSafeEquals(value, curValue))
                    {
                        return ImmutableEntry.of(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) curValue);
                    }
                }
            }
            return null;
        }

        @Override
        public boolean contains(Object o)
        {
            return o instanceof Entry && this.containsEntry((Entry<?, ?>) o);
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            for (Object obj : collection)
            {
                if (!this.contains(obj))
                {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean isEmpty()
        {
            return UnifiedMapWithHashingStrategy.this.isEmpty();
        }

        @Override
        public Iterator<Entry<K, V>> iterator()
        {
            return new EntrySetIterator(this.holder);
        }

        @Override
        public boolean remove(Object e)
        {
            if (!(e instanceof Entry))
            {
                return false;
            }
            Entry<?, ?> entry = (Entry<?, ?>) e;
            K key = (K) entry.getKey();
            V value = (V) entry.getValue();

            int index = UnifiedMapWithHashingStrategy.this.index(key);

            Object cur = UnifiedMapWithHashingStrategy.this.table[index];
            if (cur != null)
            {
                Object val = UnifiedMapWithHashingStrategy.this.table[index + 1];
                if (cur == CHAINED_KEY)
                {
                    return this.removeFromChain((Object[]) val, key, value, index);
                }
                if (UnifiedMapWithHashingStrategy.this.nonNullTableObjectEquals(cur, key) && UnifiedMapWithHashingStrategy.nullSafeEquals(value, val))
                {
                    UnifiedMapWithHashingStrategy.this.table[index] = null;
                    UnifiedMapWithHashingStrategy.this.table[index + 1] = null;
                    UnifiedMapWithHashingStrategy.this.occupied--;
                    return true;
                }
            }
            return false;
        }

        private boolean removeFromChain(Object[] chain, K key, V value, int index)
        {
            for (int i = 0; i < chain.length; i += 2)
            {
                Object k = chain[i];
                if (k == null)
                {
                    return false;
                }
                if (UnifiedMapWithHashingStrategy.this.nonNullTableObjectEquals(k, key))
                {
                    V val = (V) chain[i + 1];
                    if (UnifiedMapWithHashingStrategy.nullSafeEquals(val, value))
                    {
                        UnifiedMapWithHashingStrategy.this.overwriteWithLastElementFromChain(chain, index, i);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            boolean changed = false;
            for (Object obj : collection)
            {
                if (this.remove(obj))
                {
                    changed = true;
                }
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            int retainedSize = collection.size();
            UnifiedMapWithHashingStrategy<K, V> retainedCopy = new UnifiedMapWithHashingStrategy<>(
                    UnifiedMapWithHashingStrategy.this.hashingStrategy, retainedSize, UnifiedMapWithHashingStrategy.this.loadFactor);

            for (Object obj : collection)
            {
                if (obj instanceof Entry)
                {
                    Entry<?, ?> otherEntry = (Entry<?, ?>) obj;
                    Entry<K, V> thisEntry = this.getEntry(otherEntry);
                    if (thisEntry != null)
                    {
                        retainedCopy.put(thisEntry.getKey(), thisEntry.getValue());
                    }
                }
            }
            if (retainedCopy.size() < this.size())
            {
                UnifiedMapWithHashingStrategy.this.maxSize = retainedCopy.maxSize;
                UnifiedMapWithHashingStrategy.this.occupied = retainedCopy.occupied;
                UnifiedMapWithHashingStrategy.this.table = retainedCopy.table;
                return true;
            }
            return false;
        }

        @Override
        public int size()
        {
            return UnifiedMapWithHashingStrategy.this.size();
        }

        @Override
        public void forEach(Procedure<? super Entry<K, V>> procedure)
        {
            for (int i = 0; i < UnifiedMapWithHashingStrategy.this.table.length; i += 2)
            {
                Object cur = UnifiedMapWithHashingStrategy.this.table[i];
                if (cur == CHAINED_KEY)
                {
                    this.chainedForEachEntry((Object[]) UnifiedMapWithHashingStrategy.this.table[i + 1], procedure);
                }
                else if (cur != null)
                {
                    procedure.value(ImmutableEntry.of(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) UnifiedMapWithHashingStrategy.this.table[i + 1]));
                }
            }
        }

        private void chainedForEachEntry(Object[] chain, Procedure<? super Entry<K, V>> procedure)
        {
            for (int i = 0; i < chain.length; i += 2)
            {
                Object cur = chain[i];
                if (cur == null)
                {
                    return;
                }
                procedure.value(ImmutableEntry.of(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) chain[i + 1]));
            }
        }

        @Override
        public int getBatchCount(int batchSize)
        {
            return UnifiedMapWithHashingStrategy.this.getBatchCount(batchSize);
        }

        @Override
        public void batchForEach(Procedure<? super Entry<K, V>> procedure, int sectionIndex, int sectionCount)
        {
            Object[] map = UnifiedMapWithHashingStrategy.this.table;
            int sectionSize = map.length / sectionCount;
            int start = sectionIndex * sectionSize;
            int end = sectionIndex == sectionCount - 1 ? map.length : start + sectionSize;
            if (start % 2 != 0)
            {
                start++;
            }
            for (int i = start; i < end; i += 2)
            {
                Object cur = map[i];
                if (cur == CHAINED_KEY)
                {
                    this.chainedForEachEntry((Object[]) map[i + 1], procedure);
                }
                else if (cur != null)
                {
                    procedure.value(ImmutableEntry.of(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) map[i + 1]));
                }
            }
        }

        protected void copyEntries(Object[] result)
        {
            Object[] table = UnifiedMapWithHashingStrategy.this.table;
            int count = 0;
            for (int i = 0; i < table.length; i += 2)
            {
                Object x = table[i];
                if (x != null)
                {
                    if (x == CHAINED_KEY)
                    {
                        Object[] chain = (Object[]) table[i + 1];
                        for (int j = 0; j < chain.length; j += 2)
                        {
                            Object cur = chain[j];
                            if (cur == null)
                            {
                                break;
                            }
                            result[count++] =
                                    new WeakBoundEntry<>(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) chain[j + 1], this.holder,
                                            UnifiedMapWithHashingStrategy.this.hashingStrategy);
                        }
                    }
                    else
                    {
                        result[count++] = new WeakBoundEntry<>(UnifiedMapWithHashingStrategy.this.nonSentinel(x), (V) table[i + 1], this.holder,
                                UnifiedMapWithHashingStrategy.this.hashingStrategy);
                    }
                }
            }
        }

        @Override
        public Object[] toArray()
        {
            Object[] result = new Object[UnifiedMapWithHashingStrategy.this.size()];
            this.copyEntries(result);
            return result;
        }

        @Override
        public <T> T[] toArray(T[] result)
        {
            int size = UnifiedMapWithHashingStrategy.this.size();
            if (result.length < size)
            {
                result = (T[]) Array.newInstance(result.getClass().getComponentType(), size);
            }
            this.copyEntries(result);
            if (size < result.length)
            {
                result[size] = null;
            }
            return result;
        }

        private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();
            this.holder = new WeakReference<>(UnifiedMapWithHashingStrategy.this);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Set)
            {
                Set<?> other = (Set<?>) obj;
                if (other.size() == this.size())
                {
                    return this.containsAll(other);
                }
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return UnifiedMapWithHashingStrategy.this.hashCode();
        }
    }

    protected class EntrySetIterator extends PositionalIterator<Entry<K, V>>
    {
        private final WeakReference<UnifiedMapWithHashingStrategy<K, V>> holder;

        protected EntrySetIterator(WeakReference<UnifiedMapWithHashingStrategy<K, V>> holder)
        {
            this.holder = holder;
        }

        protected Entry<K, V> nextFromChain()
        {
            Object[] chain = (Object[]) UnifiedMapWithHashingStrategy.this.table[this.position + 1];
            Object cur = chain[this.chainPosition];
            Object value = chain[this.chainPosition + 1];
            this.chainPosition += 2;
            if (this.chainPosition >= chain.length
                    || chain[this.chainPosition] == null)
            {
                this.chainPosition = 0;
                this.position += 2;
            }
            this.lastReturned = true;
            return new WeakBoundEntry<>(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) value, this.holder,
                    UnifiedMapWithHashingStrategy.this.hashingStrategy);
        }

        @Override
        public Entry<K, V> next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException("next() called, but the iterator is exhausted");
            }
            this.count++;
            Object[] table = UnifiedMapWithHashingStrategy.this.table;
            if (this.chainPosition != 0)
            {
                return this.nextFromChain();
            }
            while (table[this.position] == null)
            {
                this.position += 2;
            }
            Object cur = table[this.position];
            Object value = table[this.position + 1];
            if (cur == CHAINED_KEY)
            {
                return this.nextFromChain();
            }
            this.position += 2;
            this.lastReturned = true;
            return new WeakBoundEntry<>(UnifiedMapWithHashingStrategy.this.nonSentinel(cur), (V) value, this.holder,
                    UnifiedMapWithHashingStrategy.this.hashingStrategy);
        }
    }

    protected static class WeakBoundEntry<K, V> implements Map.Entry<K, V>
    {
        protected final K key;
        protected V value;
        protected final WeakReference<UnifiedMapWithHashingStrategy<K, V>> holder;
        protected final HashingStrategy<? super K> hashingStrategy;

        protected WeakBoundEntry(K key, V value, WeakReference<UnifiedMapWithHashingStrategy<K, V>> holder,
                HashingStrategy<? super K> hashingStrategy)
        {
            this.key = key;
            this.value = value;
            this.holder = holder;
            this.hashingStrategy = hashingStrategy;
        }

        @Override
        public K getKey()
        {
            return this.key;
        }

        @Override
        public V getValue()
        {
            return this.value;
        }

        @Override
        public V setValue(V value)
        {
            this.value = value;
            UnifiedMapWithHashingStrategy<K, V> map = this.holder.get();
            if (map != null && map.containsKey(this.key))
            {
                return map.put(this.key, value);
            }
            return null;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof Entry)
            {
                Entry<?, ?> other = (Entry<?, ?>) obj;
                K otherKey = (K) other.getKey();
                V otherValue = (V) other.getValue();
                return this.hashingStrategy.equals(this.key, otherKey)
                        && UnifiedMapWithHashingStrategy.nullSafeEquals(this.value, otherValue);
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return this.hashingStrategy.computeHashCode(this.key)
                    ^ (this.value == null ? 0 : this.value.hashCode());
        }

        @Override
        public String toString()
        {
            return this.key + "=" + this.value;
        }
    }

    protected class ValuesCollection extends ValuesCollectionCommon<V>
            implements Serializable, BatchIterable<V>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void clear()
        {
            UnifiedMapWithHashingStrategy.this.clear();
        }

        @Override
        public boolean contains(Object o)
        {
            return UnifiedMapWithHashingStrategy.this.containsValue(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection)
        {
            // todo: this is N^2. if c is large, we should copy the values to a set.
            return Iterate.allSatisfy(collection, Predicates.in(this));
        }

        @Override
        public boolean isEmpty()
        {
            return UnifiedMapWithHashingStrategy.this.isEmpty();
        }

        @Override
        public Iterator<V> iterator()
        {
            return new ValuesIterator();
        }

        @Override
        public boolean remove(Object o)
        {
            // this is so slow that the extra overhead of the iterator won't be noticeable
            if (o == null)
            {
                for (Iterator<V> it = this.iterator(); it.hasNext(); )
                {
                    if (it.next() == null)
                    {
                        it.remove();
                        return true;
                    }
                }
            }
            else
            {
                for (Iterator<V> it = this.iterator(); it.hasNext(); )
                {
                    V o2 = it.next();
                    if (o == o2 || o2.equals(o))
                    {
                        it.remove();
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection)
        {
            // todo: this is N^2. if c is large, we should copy the values to a set.
            boolean changed = false;

            for (Object obj : collection)
            {
                if (this.remove(obj))
                {
                    changed = true;
                }
            }
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> collection)
        {
            boolean modified = false;
            Iterator<V> e = this.iterator();
            while (e.hasNext())
            {
                if (!collection.contains(e.next()))
                {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public int size()
        {
            return UnifiedMapWithHashingStrategy.this.size();
        }

        @Override
        public void forEach(Procedure<? super V> procedure)
        {
            UnifiedMapWithHashingStrategy.this.forEachValue(procedure);
        }

        @Override
        public int getBatchCount(int batchSize)
        {
            return UnifiedMapWithHashingStrategy.this.getBatchCount(batchSize);
        }

        @Override
        public void batchForEach(Procedure<? super V> procedure, int sectionIndex, int sectionCount)
        {
            UnifiedMapWithHashingStrategy.this.batchForEach(procedure, sectionIndex, sectionCount);
        }

        protected void copyValues(Object[] result)
        {
            int count = 0;
            for (int i = 0; i < UnifiedMapWithHashingStrategy.this.table.length; i += 2)
            {
                Object x = UnifiedMapWithHashingStrategy.this.table[i];
                if (x != null)
                {
                    if (x == CHAINED_KEY)
                    {
                        Object[] chain = (Object[]) UnifiedMapWithHashingStrategy.this.table[i + 1];
                        for (int j = 0; j < chain.length; j += 2)
                        {
                            Object cur = chain[j];
                            if (cur == null)
                            {
                                break;
                            }
                            result[count++] = chain[j + 1];
                        }
                    }
                    else
                    {
                        result[count++] = UnifiedMapWithHashingStrategy.this.table[i + 1];
                    }
                }
            }
        }

        @Override
        public Object[] toArray()
        {
            int size = UnifiedMapWithHashingStrategy.this.size();
            Object[] result = new Object[size];
            this.copyValues(result);
            return result;
        }

        @Override
        public <T> T[] toArray(T[] result)
        {
            int size = UnifiedMapWithHashingStrategy.this.size();
            if (result.length < size)
            {
                result = (T[]) Array.newInstance(result.getClass().getComponentType(), size);
            }
            this.copyValues(result);
            if (size < result.length)
            {
                result[size] = null;
            }
            return result;
        }

        protected Object writeReplace()
        {
            FastList<V> replace = FastList.newList(UnifiedMapWithHashingStrategy.this.size());
            for (int i = 0; i < UnifiedMapWithHashingStrategy.this.table.length; i += 2)
            {
                Object cur = UnifiedMapWithHashingStrategy.this.table[i];
                if (cur == CHAINED_KEY)
                {
                    this.chainedAddToList((Object[]) UnifiedMapWithHashingStrategy.this.table[i + 1], replace);
                }
                else if (cur != null)
                {
                    replace.add((V) UnifiedMapWithHashingStrategy.this.table[i + 1]);
                }
            }
            return replace;
        }

        private void chainedAddToList(Object[] chain, FastList<V> replace)
        {
            for (int i = 0; i < chain.length; i += 2)
            {
                Object cur = chain[i];
                if (cur == null)
                {
                    return;
                }
                replace.add((V) chain[i + 1]);
            }
        }

        @Override
        public String toString()
        {
            return Iterate.makeString(this, "[", ", ", "]");
        }
    }

    protected class ValuesIterator extends PositionalIterator<V>
    {
        protected V nextFromChain()
        {
            Object[] chain = (Object[]) UnifiedMapWithHashingStrategy.this.table[this.position + 1];
            V val = (V) chain[this.chainPosition + 1];
            this.chainPosition += 2;
            if (this.chainPosition >= chain.length
                    || chain[this.chainPosition] == null)
            {
                this.chainPosition = 0;
                this.position += 2;
            }
            this.lastReturned = true;
            return val;
        }

        @Override
        public V next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException("next() called, but the iterator is exhausted");
            }
            this.count++;
            Object[] table = UnifiedMapWithHashingStrategy.this.table;
            if (this.chainPosition != 0)
            {
                return this.nextFromChain();
            }
            while (table[this.position] == null)
            {
                this.position += 2;
            }
            Object cur = table[this.position];
            Object val = table[this.position + 1];
            if (cur == CHAINED_KEY)
            {
                return this.nextFromChain();
            }
            this.position += 2;
            this.lastReturned = true;
            return (V) val;
        }
    }

    private K nonSentinel(Object key)
    {
        return key == NULL_KEY ? null : (K) key;
    }

    private static Object toSentinelIfNull(Object key)
    {
        if (key == null)
        {
            return NULL_KEY;
        }
        return key;
    }

    private boolean nonNullTableObjectEquals(Object cur, K key)
    {
        return cur == key || (cur == NULL_KEY ? key == null : this.hashingStrategy.equals(this.nonSentinel(cur), key));
    }

    @Override
    public ImmutableMap<K, V> toImmutable()
    {
        return HashingStrategyMaps.immutable.withAll(this);
    }
}
