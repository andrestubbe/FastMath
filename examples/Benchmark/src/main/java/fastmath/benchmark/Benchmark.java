package fastmath.benchmark;

import fastmath.FastMath;
import fastmath.FastMathVectors;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Benchmark {

    private double x;
    private double x1, y1, z1, x2, y2, z2;

    @Setup
    public void setup() {
        x = 1.234567;
        x1 = 1.0; y1 = 2.0; z1 = 3.0;
        x2 = 4.0; y2 = 5.0; z2 = 6.0;
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkJavaMathSin() {
        return Math.sin(x);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkFastMathSin() {
        return FastMath.sin(x);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkJavaMathSqrt() {
        return Math.sqrt(x);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkFastMathSqrt() {
        return FastMath.sqrt(x);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public double benchmarkFastMathVectorsDot3() {
        return FastMathVectors.dot3(x1, y1, z1, x2, y2, z2);
    }
}
