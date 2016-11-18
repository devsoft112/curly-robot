/*
 * Copyright (c) 2015 Goldman Sachs.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.eclipse.collections.impl.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.collections.impl.jmh.runner.AbstractJMHTestRunner;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.junit.Assert;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class NoneSatisfyTest extends AbstractJMHTestRunner
{
    private static final int SIZE = 1_000_000;
    private static final int BATCH_SIZE = 10_000;
    private final List<Integer> integersJDK = new ArrayList<>(Interval.oneTo(SIZE));
    private final FastList<Integer> integersEC = new FastList<>(Interval.oneTo(SIZE));

    private ExecutorService executorService;

    @Setup
    public void setUp()
    {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @TearDown
    public void tearDown() throws InterruptedException
    {
        this.executorService.shutdownNow();
        this.executorService.awaitTermination(1L, TimeUnit.SECONDS);
    }

    @Benchmark
    public void short_circuit_middle_serial_lazy_jdk()
    {
        Assert.assertFalse(this.integersJDK.stream().noneMatch(each -> each > SIZE / 2));
    }

    @Benchmark
    public void short_circuit_middle_serial_lazy_streams_ec()
    {
        Assert.assertFalse(this.integersEC.stream().noneMatch(each -> each > SIZE / 2));
    }

    @Benchmark
    public void process_none_serial_lazy_jdk()
    {
        Assert.assertTrue(this.integersJDK.stream().noneMatch(each -> each < 0));
    }

    @Benchmark
    public void process_none_serial_lazy_streams_ec()
    {
        Assert.assertTrue(this.integersEC.stream().noneMatch(each -> each < 0));
    }

    @Benchmark
    public void short_circuit_middle_serial_eager_ec()
    {
        Assert.assertFalse(this.integersEC.noneSatisfy(each -> each > SIZE / 2));
    }

    @Benchmark
    public void process_none_serial_eager_ec()
    {
        Assert.assertTrue(this.integersEC.noneSatisfy(each -> each < 0));
    }

    @Benchmark
    public void short_circuit_middle_serial_lazy_ec()
    {
        Assert.assertFalse(this.integersEC.asLazy().noneSatisfy(each -> each > SIZE / 2));
    }

    @Benchmark
    public void process_none_serial_lazy_ec()
    {
        Assert.assertTrue(this.integersEC.asLazy().noneSatisfy(each -> each < 0));
    }

    @Benchmark
    public void short_circuit_middle_parallel_lazy_jdk()
    {
        Assert.assertFalse(this.integersJDK.parallelStream().noneMatch(each -> each == SIZE / 2 - 1));
    }

    @Benchmark
    public void process_all_parallel_lazy_jdk()
    {
        Assert.assertTrue(this.integersJDK.parallelStream().noneMatch(each -> each < 0));
    }

    @Benchmark
    public void short_circuit_middle_parallel_lazy_ec()
    {
        Assert.assertFalse(this.integersEC.asParallel(this.executorService, BATCH_SIZE).noneSatisfy(each -> each == SIZE / 2 - 1));
    }

    @Benchmark
    public void process_all_parallel_lazy_ec()
    {
        Assert.assertTrue(this.integersEC.asParallel(this.executorService, BATCH_SIZE).noneSatisfy(each -> each < 0));
    }
}
