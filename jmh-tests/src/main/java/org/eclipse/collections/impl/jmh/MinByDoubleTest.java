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

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.jmh.domain.Position;
import org.eclipse.collections.impl.jmh.domain.Positions;
import org.eclipse.collections.impl.jmh.runner.AbstractJMHTestRunner;
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
public class MinByDoubleTest extends AbstractJMHTestRunner
{
    private static final int SIZE = 3_000_000;
    private static final int BATCH_SIZE = 10_000;

    // Comparator which autoboxes doubles: slow
    private static final Comparator<Position> MARKET_VALUE_COMPARATOR_METHODREF =
            Comparator.comparing(Position::getMarketValue);

    private static final Comparator<Position> MARKET_VALUE_COMPARATOR_LAMBDA =
            (Position p1, Position p2) -> Double.compare(p1.getMarketValue(), p2.getMarketValue());

    private final Positions positions = new Positions(SIZE).shuffle();

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
    public Position minByMarketValue_serial_lazy_direct_methodref_jdk()
    {
        return this.positions.getJdkPositions().stream().min(MARKET_VALUE_COMPARATOR_METHODREF).get();
    }

    @Benchmark
    public Position minByMarketValue_serial_lazy_direct_lambda_jdk()
    {
        return this.positions.getJdkPositions().stream().min(MARKET_VALUE_COMPARATOR_LAMBDA).get();
    }

    @Benchmark
    public Position minByMarketValue_serial_lazy_collect_methodref_jdk()
    {
        return this.positions.getJdkPositions().stream().collect(
                Collectors.minBy(MARKET_VALUE_COMPARATOR_METHODREF)).get();
    }

    @Benchmark
    public Position minByMarketValue_serial_lazy_collect_lambda_jdk()
    {
        return this.positions.getJdkPositions().stream().collect(
                Collectors.minBy(MARKET_VALUE_COMPARATOR_LAMBDA)).get();
    }

    @Benchmark
    public Position minByMarketValue_parallel_lazy_direct_methodref_jdk()
    {
        return this.positions.getJdkPositions().parallelStream().min(
                MARKET_VALUE_COMPARATOR_METHODREF).get();
    }

    @Benchmark
    public Position minByMarketValue_parallel_lazy_direct_lambda_jdk()
    {
        return this.positions.getJdkPositions().parallelStream().min(
                MARKET_VALUE_COMPARATOR_LAMBDA).get();
    }

    @Benchmark
    public Position minByMarketValue_parallel_lazy_collect_methodref_jdk()
    {
        return this.positions.getJdkPositions().parallelStream().collect(
                Collectors.minBy(MARKET_VALUE_COMPARATOR_METHODREF)).get();
    }

    @Benchmark
    public Position minByMarketValue_parallel_lazy_collect_lambda_jdk()
    {
        return this.positions.getJdkPositions().parallelStream().collect(
                Collectors.minBy(MARKET_VALUE_COMPARATOR_LAMBDA)).get();
    }

    @Benchmark
    public Position minByMarketValue_serial_eager_ec()
    {
        return this.positions.getEcPositions().minBy(Position::getMarketValue);
    }

    @Benchmark
    public Position minByMarketValue_serial_lazy_ec()
    {
        return this.positions.getEcPositions().asLazy().minBy(Position::getMarketValue);
    }

    @Benchmark
    public Position minByMarketValue_parallel_lazy_ec()
    {
        return this.positions.getEcPositions().asParallel(this.executorService, BATCH_SIZE).minBy(
                Position::getMarketValue);
    }
}
