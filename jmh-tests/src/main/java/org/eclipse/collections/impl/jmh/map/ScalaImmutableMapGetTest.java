/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh.map;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.collections.impl.jmh.runner.AbstractJMHTestRunner;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import scala.collection.immutable.HashMap$;
import scala.collection.immutable.Map;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ScalaImmutableMapGetTest extends AbstractJMHTestRunner
{
    private static final int RANDOM_COUNT = 9;

    @Param({"250000", "500000", "750000", "1000000", "1250000", "1500000", "1750000", "2000000", "2250000", "2500000", "2750000", "3000000",
            "3250000", "3500000", "3750000", "4000000", "4250000", "4500000", "4750000", "5000000", "5250000", "5500000", "5750000", "6000000",
            "6250000", "6500000", "6750000", "7000000", "7250000", "7500000", "7750000", "8000000", "8250000", "8500000", "8750000", "9000000",
            "9250000", "9500000", "9750000", "10000000"})
    public int size;
    private String[] elements;
    private Map<String, String> scalaMap;

    @Setup
    public void setUp()
    {
        Random random = new Random(123456789012345L);

        this.elements = new String[this.size];
        Map<String, String> map = HashMap$.MODULE$.empty();

        for (int i = 0; i < this.size; i++)
        {
            String element = RandomStringUtils.random(RANDOM_COUNT, 0, 0, false, true, null, random);
            this.elements[i] = element;
            map = map.updated(element, "dummy");
        }

        this.scalaMap = map;
    }

    @Benchmark
    public void get()
    {
        int localSize = this.size;
        String[] localElements = this.elements;
        Map<String, String> localScalaMap = this.scalaMap;

        for (int i = 0; i < localSize; i++)
        {
            if (!localScalaMap.get(localElements[i]).isDefined())
            {
                throw new AssertionError(i);
            }
        }
    }
}
