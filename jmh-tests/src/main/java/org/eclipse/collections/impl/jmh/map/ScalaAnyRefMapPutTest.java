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
import scala.collection.mutable.AnyRefMap;
import scala.collection.mutable.Map;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class ScalaAnyRefMapPutTest extends AbstractJMHTestRunner
{
    private static final int RANDOM_COUNT = 9;

    @Param({"250000", "500000", "750000", "1000000", "1250000", "1500000", "1750000", "2000000", "2250000", "2500000", "2750000", "3000000",
            "3250000", "3500000", "3750000", "4000000", "4250000", "4500000", "4750000", "5000000", "5250000", "5500000", "5750000", "6000000",
            "6250000", "6500000", "6750000", "7000000", "7250000", "7500000", "7750000", "8000000", "8250000", "8500000", "8750000", "9000000",
            "9250000", "9500000", "9750000", "10000000"})
    public int size;
    @Param({"true", "false"})
    public boolean isPresized;
    @Param("0.75")
    public float loadFactor; //Adding a loadFactor for only ease of data plots
    private String[] elements;

    @Setup
    public void setUp()
    {
        Random random = new Random(123456789012345L);

        this.elements = new String[this.size];

        for (int i = 0; i < this.size; i++)
        {
            this.elements[i] = RandomStringUtils.random(RANDOM_COUNT, 0, 0, false, true, null, random);
        }
    }

    @Benchmark
    public Map<String, String> scalaAnyRef()
    {
        int localSize = this.size;
        String[] localElements = this.elements;

        Map<String, String> scalaAnyRefMap = this.isPresized ? new AnyRefMap<>(localSize) : new AnyRefMap<>();

        for (int i = 0; i < localSize; i++)
        {
            scalaAnyRefMap.put(localElements[i], "dummy");
        }
        return scalaAnyRefMap;
    }
}
