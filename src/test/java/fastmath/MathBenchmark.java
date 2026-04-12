package fastmath;

/**
 * Comprehensive benchmark of all java.lang.Math methods.
 * Establishes baseline performance before JNI optimization.
 * 
 * Run: mvn test-compile exec:java -Dexec.mainClass="fastmath.MathBenchmark" -Dexec.classpathScope=test
 */
public class MathBenchmark {
    
    private static final int WARMUP_ITERATIONS = 100_000;
    private static final int BENCHMARK_ITERATIONS = 1_000_000;
    private static final int ARRAY_SIZE = 10_000;
    
    // Test values covering various domains
    private static final double[] TEST_DOUBLES = {
        0.0, 0.5, 1.0, 2.0, 10.0, 100.0, 1000.0, 
        Math.PI, Math.E, 
        -0.5, -1.0, -10.0,
        0.0001, 0.9999, 1.0001
    };
    
    private static final float[] TEST_FLOATS = {
        0.0f, 0.5f, 1.0f, 2.0f, 10.0f, 100.0f,
        (float)Math.PI, -1.0f, 0.0001f
    };
    
    private static final int[] TEST_INTS = {
        0, 1, 5, 10, 100, 1000, -5, -100, Integer.MAX_VALUE, Integer.MIN_VALUE
    };
    
    private static final long[] TEST_LONGS = {
        0L, 1L, 5L, 100L, 1000L, -5L, Long.MAX_VALUE, Long.MIN_VALUE
    };

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("           FastMath Baseline Benchmark - Pure Java           ");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Iterations per test: " + BENCHMARK_ITERATIONS);
        System.out.println("Warmup iterations:   " + WARMUP_ITERATIONS);
        System.out.println();
        
        // Warmup
        System.out.println("Warming up JVM...");
        runWarmup();
        System.out.println("Done.\n");
        
        // Run all benchmarks
        System.out.println("───────────────────────────────────────────────────────────────");
        System.out.println("                    BENCHMARK RESULTS                         ");
        System.out.println("───────────────────────────────────────────────────────────────\n");
        
        benchmarkTrigonometric();
        benchmarkHyperbolic();
        benchmarkExponential();
        benchmarkPower();
        benchmarkRounding();
        benchmarkAbs();
        benchmarkMinMax();
        benchmarkOther();
        benchmarkArrayOperations();
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("                    BENCHMARK COMPLETE                         ");
        System.out.println("═══════════════════════════════════════════════════════════════");
    }
    
    private static void runWarmup() {
        double sum = 0;
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            sum += Math.sqrt(i);
            sum += Math.sin(i);
            sum += Math.exp(i % 100 / 10.0);
            sum += Math.log(i % 100 + 1);
        }
        // Prevent optimization from removing calculations
        if (sum == 0) System.out.println(sum);
    }
    
    private static void benchmarkTrigonometric() {
        System.out.println("▶ TRIGONOMETRIC FUNCTIONS");
        System.out.println("──────────────────────────");
        
        benchmarkDouble1("sin",    Math::sin);
        benchmarkDouble1("cos",    Math::cos);
        benchmarkDouble1("tan",    Math::tan);
        benchmarkDouble1("asin",   Math::asin,  -1.0, 1.0);
        benchmarkDouble1("acos",   Math::acos,  -1.0, 1.0);
        benchmarkDouble1("atan",   Math::atan);
        benchmarkDouble2("atan2",  Math::atan2, -10.0, 10.0);
        
        System.out.println();
    }
    
    private static void benchmarkHyperbolic() {
        System.out.println("▶ HYPERBOLIC FUNCTIONS");
        System.out.println("───────────────────────");
        
        benchmarkDouble1("sinh", Math::sinh, -5.0, 5.0);
        benchmarkDouble1("cosh", Math::cosh, -5.0, 5.0);
        benchmarkDouble1("tanh", Math::tanh, -5.0, 5.0);
        
        System.out.println();
    }
    
    private static void benchmarkExponential() {
        System.out.println("▶ EXPONENTIAL & LOGARITHMIC");
        System.out.println("───────────────────────────");
        
        benchmarkDouble1("exp",    Math::exp,    0.0, 10.0);
        benchmarkDouble1("expm1",  Math::expm1,  0.0, 5.0);
        benchmarkDouble1("log",    Math::log,    0.1, 1000.0);
        benchmarkDouble1("log1p",  Math::log1p,  0.0, 100.0);
        benchmarkDouble1("log10",  Math::log10,  0.1, 1000.0);
        
        System.out.println();
    }
    
    private static void benchmarkPower() {
        System.out.println("▶ POWER & ROOT FUNCTIONS");
        System.out.println("────────────────────────");
        
        benchmarkDouble1("sqrt", Math::sqrt,   0.0, 10000.0);
        benchmarkDouble1("cbrt", Math::cbrt, -1000.0, 1000.0);
        benchmarkDouble2("pow",  Math::pow,    0.1, 100.0, -5.0, 5.0);
        
        System.out.println();
    }
    
    private static void benchmarkRounding() {
        System.out.println("▶ ROUNDING FUNCTIONS");
        System.out.println("─────────────────────");
        
        benchmarkDouble1("ceil",  Math::ceil,  -1000.0, 1000.0);
        benchmarkDouble1("floor", Math::floor, -1000.0, 1000.0);
        benchmarkDouble1("rint",  Math::rint,  -1000.0, 1000.0);
        benchmarkDoubleInput("round(double)", Math::round, TEST_DOUBLES);
        benchmarkFloatInput("round(float)", Math::round, TEST_FLOATS);
        
        System.out.println();
    }
    
    private static void benchmarkAbs() {
        System.out.println("▶ ABSOLUTE VALUE");
        System.out.println("─────────────────");
        
        benchmarkDouble1("abs(double)", Math::abs, -1000000.0, 1000000.0);
        benchmarkFloat1("abs(float)",  Math::abs, -1000000.0f, 1000000.0f);
        benchmarkIntInput("abs(int)",    Math::abs, TEST_INTS);
        benchmarkLongInput("abs(long)",  Math::abs, TEST_LONGS);
        
        System.out.println();
    }
    
    private static void benchmarkMinMax() {
        System.out.println("▶ MIN/MAX FUNCTIONS");
        System.out.println("────────────────────");
        
        benchmarkDouble2("max(double)", Math::max, -1000.0, 1000.0);
        benchmarkDouble2("min(double)", Math::min, -1000.0, 1000.0);
        benchmarkFloat2("max(float)",   Math::max, -1000.0f, 1000.0f);
        benchmarkFloat2("min(float)",   Math::min, -1000.0f, 1000.0f);
        benchmarkInt2("max(int)",       Math::max, -1000, 1000);
        benchmarkInt2("min(int)",       Math::min, -1000, 1000);
        benchmarkLong2("max(long)",     Math::max, -1000L, 1000L);
        benchmarkLong2("min(long)",     Math::min, -1000L, 1000L);
        
        System.out.println();
    }
    
    private static void benchmarkOther() {
        System.out.println("▶ OTHER FUNCTIONS");
        System.out.println("─────────────────");
        
        benchmarkDouble1("signum(double)", Math::signum, -100.0, 100.0);
        benchmarkFloat1("signum(float)",   Math::signum, -100.0f, 100.0f);
        benchmarkDouble2("copySign",       Math::copySign, -100.0, 100.0);
        benchmarkDouble1("ulp",            Math::ulp, 0.1, 1000.0);
        benchmarkDouble2("IEEEremainder", Math::IEEEremainder, -100.0, 100.0);
        benchmarkDouble1("toDegrees",      x -> Math.toDegrees(x), -Math.PI, Math.PI);
        benchmarkDouble1("toRadians",      x -> Math.toRadians(x), -360.0, 360.0);
        
        System.out.println();
    }
    
    private static void benchmarkArrayOperations() {
        System.out.println("▶ ARRAY/BATCH OPERATIONS (Java loops)");
        System.out.println("─────────────────────────────────────");
        System.out.println("Array size: " + ARRAY_SIZE);
        
        double[] input = new double[ARRAY_SIZE];
        double[] output = new double[ARRAY_SIZE];
        
        for (int i = 0; i < ARRAY_SIZE; i++) {
            input[i] = i * 0.001 + 0.1;
        }
        
        // Sqrt array
        long start = System.nanoTime();
        for (int iter = 0; iter < BENCHMARK_ITERATIONS / 10; iter++) {
            for (int i = 0; i < ARRAY_SIZE; i++) {
                output[i] = Math.sqrt(input[i]);
            }
        }
        long end = System.nanoTime();
        printResult("sqrt (array loop)", (end - start) / (BENCHMARK_ITERATIONS / 10));
        
        // Sin array
        start = System.nanoTime();
        for (int iter = 0; iter < BENCHMARK_ITERATIONS / 10; iter++) {
            for (int i = 0; i < ARRAY_SIZE; i++) {
                output[i] = Math.sin(input[i]);
            }
        }
        end = System.nanoTime();
        printResult("sin (array loop)", (end - start) / (BENCHMARK_ITERATIONS / 10));
        
        // Exp array
        start = System.nanoTime();
        for (int iter = 0; iter < BENCHMARK_ITERATIONS / 10; iter++) {
            for (int i = 0; i < ARRAY_SIZE; i++) {
                output[i] = Math.exp(input[i]);
            }
        }
        end = System.nanoTime();
        printResult("exp (array loop)", (end - start) / (BENCHMARK_ITERATIONS / 10));
        
        System.out.println();
    }
    
    // ═════════════════════════════════════════════════════════════════
    // BENCHMARK HELPERS
    // ═════════════════════════════════════════════════════════════════
    
    @FunctionalInterface
    interface DoubleFunction {
        double apply(double x);
    }
    
    @FunctionalInterface
    interface DoubleBinaryFunction {
        double apply(double x, double y);
    }
    
    @FunctionalInterface
    interface FloatFunction {
        float apply(float x);
    }
    
    @FunctionalInterface
    interface FloatBinaryFunction {
        float apply(float x, float y);
    }
    
    @FunctionalInterface
    interface IntFunction {
        int apply(int x);
    }
    
    @FunctionalInterface
    interface IntBinaryFunction {
        int apply(int x, int y);
    }
    
    @FunctionalInterface
    interface LongFunction {
        long apply(long x);
    }
    
    @FunctionalInterface
    interface LongBinaryFunction {
        long apply(long x, long y);
    }
    
    private static void benchmarkDouble1(String name, DoubleFunction func) {
        benchmarkDouble1(name, func, 0.0, 10.0);
    }
    
    private static void benchmarkDouble1(String name, DoubleFunction func, double min, double max) {
        double sum = 0;
        double range = max - min;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            double x = min + (i % 1000) * range / 1000.0;
            sum += func.apply(x);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkDoubleInput(String name, DoubleFunction func, double[] inputs) {
        double sum = 0;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            sum += func.apply(inputs[i % inputs.length]);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkDouble2(String name, DoubleBinaryFunction func, double min, double max) {
        double sum = 0;
        double range = max - min;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            double x = min + (i % 100) * range / 100.0;
            double y = min + ((i / 100) % 100) * range / 100.0;
            sum += func.apply(x, y);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkDouble2(String name, DoubleBinaryFunction func, 
                                          double min1, double max1, double min2, double max2) {
        double sum = 0;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            double x = min1 + (i % 100) * (max1 - min1) / 100.0;
            double y = min2 + ((i / 100) % 100) * (max2 - min2) / 100.0;
            sum += func.apply(x, y);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkFloat1(String name, FloatFunction func, float min, float max) {
        float sum = 0;
        float range = max - min;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            float x = min + (i % 1000) * range / 1000.0f;
            sum += func.apply(x);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkFloatInput(String name, FloatFunction func, float[] inputs) {
        float sum = 0;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            sum += func.apply(inputs[i % inputs.length]);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkFloat2(String name, FloatBinaryFunction func, float min, float max) {
        float sum = 0;
        float range = max - min;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            float x = min + (i % 100) * range / 100.0f;
            float sum2 = min + ((i / 100) % 100) * range / 100.0f;
            sum += func.apply(x, sum2);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkIntInput(String name, IntFunction func, int[] inputs) {
        long sum = 0;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            sum += func.apply(inputs[i % inputs.length]);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkInt2(String name, IntBinaryFunction func, int min, int max) {
        long sum = 0;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            int x = min + (i % 100);
            int y = min + ((i / 100) % 100);
            sum += func.apply(x, y);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkLongInput(String name, LongFunction func, long[] inputs) {
        long sum = 0;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            sum += func.apply(inputs[i % inputs.length]);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void benchmarkLong2(String name, LongBinaryFunction func, long min, long max) {
        long sum = 0;
        
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long x = min + (i % 100);
            long y = min + ((i / 100) % 100);
            sum += func.apply(x, y);
        }
        long end = System.nanoTime();
        
        printResult(name, (end - start));
        preventOptimization(sum);
    }
    
    private static void printResult(String name, long nanosTotal) {
        double nanosPerOp = (double) nanosTotal / BENCHMARK_ITERATIONS;
        double opsPerSecond = 1_000_000_000.0 / nanosPerOp;
        
        System.out.printf("  %-20s: %8.3f ns/op  │  %,10.0f ops/sec%n", 
            name, nanosPerOp, opsPerSecond);
    }
    
    private static void preventOptimization(double value) {
        if (value == Double.MAX_VALUE) {
            System.out.print("");
        }
    }
    
    private static void preventOptimization(float value) {
        if (value == Float.MAX_VALUE) {
            System.out.print("");
        }
    }
    
    private static void preventOptimization(long value) {
        if (value == Long.MAX_VALUE) {
            System.out.print("");
        }
    }
}
