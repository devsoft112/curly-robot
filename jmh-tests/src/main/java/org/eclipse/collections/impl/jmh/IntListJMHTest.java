/*
 * Copyright (c) 2017 BNY Mellon.
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
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.list.mutable.CompositeFastList;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
public class IntListJMHTest
{
    private List<Integer> jdkList;
    private MutableList<Integer> ecList;
    private IntList ecPrimitiveList;
    private ExecutorService executorService;

    public static void main(String[] args) throws RunnerException
    {
        Options options = new OptionsBuilder().include(".*" + IntListJMHTest.class.getSimpleName() + ".*")
                .forks(2)
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.SECONDS)
                .warmupIterations(30)
                .build();
        new Runner(options).run();
    }

    @Benchmark
    public MutableList<Integer> filterECBoxedEager()
    {
        return this.ecList.select(i -> i % 2 == 0);
    }

    @Benchmark
    public MutableList<Integer> filterECBoxedLazy()
    {
        return this.ecList
                .asLazy()
                .select(i -> i % 2 == 0)
                .toList();
    }

    @Benchmark
    public MutableList<Integer> filterECParallelEager()
    {
        return ParallelIterate.select(
                this.ecList,
                i -> i % 2 == 0,
                new CompositeFastList<>(),
                false);
    }

    @Benchmark
    public MutableList<Integer> filterECParallelLazy()
    {
        return this.ecList
                .asParallel(this.executorService, 50_000)
                .select(i -> i % 2 == 0)
                .toList();
    }

    @Benchmark
    public IntList filterECPrimitiveEager()
    {
        return this.ecPrimitiveList.select(i -> i % 2 == 0);
    }

    @Benchmark
    public IntList filterECPrimitiveLazy()
    {
        return this.ecPrimitiveList
                .asLazy()
                .select(i -> i % 2 == 0)
                .toList();
    }

    @Benchmark
    public List<Integer> filterJDKBoxedParallelStream()
    {
        return this.jdkList
                .parallelStream()
                .filter(i -> i % 2 == 0)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> filterJDKBoxedStream()
    {
        return this.jdkList
                .stream()
                .filter(i -> i % 2 == 0)
                .collect(Collectors.toList());
    }

    @Benchmark
    public long filterMapSumECBoxedEager()
    {
        return this.ecList
                .select(i -> i % 2 == 0)
                .sumOfInt(i -> i * 2);
    }

    @Benchmark
    public long filterMapSumECBoxedLazy()
    {
        return this.ecList
                .asLazy()
                .select(i -> i % 2 == 0)
                .sumOfInt(i -> i * 2);
    }

    @Benchmark
    public long filterMapSumECOptimizedParallelEager()
    {
        return ParallelIterate.sumByInt(
                this.ecList,
                i -> i % 2,
                i -> i * 2).get(0);
    }

    @Benchmark
    public long filterMapSumECOptimizedParallelLazy()
    {
        return this.ecList
                .asParallel(this.executorService, 50_000)
                .sumOfInt(i -> i % 2 == 0 ? i * 2 : 0);
    }

    @Benchmark
    public long filterMapSumECParallelLazy()
    {
        return this.ecList
                .asParallel(this.executorService, 50_000)
                .select(i -> i % 2 == 0)
                .sumOfInt(i -> i * 2);
    }

    @Benchmark
    public long filterMapSumECPrimitiveEager()
    {
        return this.ecPrimitiveList
                .select(i -> i % 2 == 0)
                .collectInt(i -> i * 2, IntLists.mutable.empty())
                .sum();
    }

    @Benchmark
    public long filterMapSumECPrimitiveLazy()
    {
        return this.ecPrimitiveList
                .asLazy()
                .select(i -> i % 2 == 0)
                .collectInt(i -> i * 2)
                .sum();
    }

    @Benchmark
    public long filterMapSumJDKBoxedParallelStream()
    {
        return this.jdkList
                .parallelStream()
                .filter(i -> i % 2 == 0)
                .mapToLong(i -> (long) (i * 2))
                .sum();
    }

    @Benchmark
    public long filterMapSumJDKBoxedStream()
    {
        return this.jdkList
                .stream()
                .filter(i -> i % 2 == 0)
                .mapToLong(i -> (long) (i * 2))
                .sum();
    }

    @Benchmark
    public MutableList<Integer> mapECBoxedEager()
    {
        return this.ecList.collect(i -> i * 2);
    }

    @Benchmark
    public MutableList<Integer> mapECBoxedLazy()
    {
        return this.ecList
                .asLazy()
                .collect(i -> i * 2)
                .toList();
    }

    @Benchmark
    public MutableList<Integer> mapECParallelEager()
    {
        return ParallelIterate.collect(
                this.ecList, i -> i * 2,
                new CompositeFastList<>(),
                false);
    }

    @Benchmark
    public MutableList<Integer> mapECParallelLazy()
    {
        return this.ecList
                .asParallel(this.executorService, 50_000)
                .collect(i -> i * 2)
                .toList();
    }

    @Benchmark
    public IntList mapECPrimitiveEager()
    {
        return this.ecPrimitiveList.collectInt(i -> i * 2, IntLists.mutable.empty());
    }

    @Benchmark
    public IntList mapECPrimitiveLazy()
    {
        return this.ecPrimitiveList
                .asLazy()
                .collectInt(i -> i * 2)
                .toList();
    }

    @Benchmark
    public List<Integer> mapJDKBoxedParallelStream()
    {
        return this.jdkList
                .parallelStream()
                .map(i -> i * 2)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<Integer> mapJDKBoxedStream()
    {
        return this.jdkList
                .stream()
                .map(i -> i * 2)
                .collect(Collectors.toList());
    }

    @Setup
    public void setUp()
    {
        PrimitiveIterator.OfInt intGenerator = new Random(1L).ints(-1000, 1000).iterator();
        this.ecList = FastList.newWithNValues(1_000_000, intGenerator::nextInt);
        this.jdkList = new ArrayList<>(1_000_000);
        this.jdkList.addAll(this.ecList);
        this.ecPrimitiveList = this.ecList.collectInt(i -> i, new IntArrayList(1_000_000));
        this.executorService = Executors.newWorkStealingPool();
    }

    @Benchmark
    public long sumECBoxedEager()
    {
        return this.ecList.sumOfInt(Integer::intValue);
    }

    @Benchmark
    public long sumECBoxedLazy()
    {
        return this.ecList
                .asLazy()
                .sumOfInt(Integer::intValue);
    }

    @Benchmark
    public long sumECParallelEager()
    {
        return ParallelIterate.sumByInt(
                this.ecList,
                i -> Integer.valueOf(0),
                Integer::intValue).get(0);
    }

    @Benchmark
    public long sumECParallelLazy()
    {
        return this.ecList
                .asParallel(this.executorService, 50_000)
                .sumOfInt(Integer::intValue);
    }

    @Benchmark
    public long sumECPrimitiveEager()
    {
        return this.ecPrimitiveList.sum();
    }

    @Benchmark
    public long sumECPrimitiveLazy()
    {
        return this.ecPrimitiveList
                .asLazy()
                .sum();
    }

    @Benchmark
    public long sumJDKBoxedParallelStream()
    {
        return this.jdkList
                .parallelStream()
                .mapToLong(Integer::longValue)
                .sum();
    }

    @Benchmark
    public long sumJDKBoxedStream()
    {
        return this.jdkList
                .stream()
                .mapToLong(Integer::longValue)
                .sum();
    }
}
