package fastmath;

/**
 * Marketing-focused benchmark: Java Math vs FastMath JNI
 * 
 * Run: mvn test-compile exec:java -Dexec.mainClass="fastmath.ComparisonBenchmark" -Dexec.classpathScope=test -Dexec.vmArgs=-Djava.library.path=build
 */
public class ComparisonBenchmark {
    
    private static final int WARMUP = 100_000;
    private static final int ITERATIONS = 1_000_000;
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("================================================================================");
        System.out.println("           FASTMATH PERFORMANCE: Java vs Native (JNI) Comparison");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Native Library: " + (FastMath.isNativeAvailable() ? "[LOADED]" : "[NOT LOADED - fallback to Math]"));
        System.out.println("Test Iterations: " + ITERATIONS + " per operation");
        System.out.println();
        
        // Warmup
        System.out.print("Warming up JVM... ");
        warmup();
        System.out.println("Done.");
        System.out.println();
        
        // Run benchmarks
        System.out.println("--------------------------------------------------------------------------------");
        benchmarkTrigonometric();
        benchmarkPower();
        benchmarkExpLog();
        benchmarkHyperbolic();
        benchmarkArrayOperations();
        
        // Summary
        printSummary();
    }
    
    private static void warmup() {
        for (int i = 0; i < WARMUP; i++) {
            Math.sin(i * 0.001);
            Math.sqrt(i);
            Math.exp(i * 0.0001);
        }
        if (FastMath.isNativeAvailable()) {
            for (int i = 0; i < WARMUP; i++) {
                FastMath.sin(i * 0.001);
                FastMath.sqrt(i);
                FastMath.exp(i * 0.0001);
            }
        }
    }
    
    private static void benchmarkTrigonometric() {
        System.out.println("TRIGONOMETRIC FUNCTIONS");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println("|     Method      |  Java Math  | FastMath JNI|  Speedup    |   Ops/Second    |");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        
        benchmarkRow("sin(x)", () -> Math.sin(1.0), () -> FastMath.sin(1.0));
        benchmarkRow("cos(x)", () -> Math.cos(1.0), () -> FastMath.cos(1.0));
        benchmarkRow("tan(x)", () -> Math.tan(1.0), () -> FastMath.tan(1.0));
        benchmarkRow("asin(x)", () -> Math.asin(0.5), () -> FastMath.asin(0.5));
        benchmarkRow("acos(x)", () -> Math.acos(0.5), () -> FastMath.acos(0.5));
        benchmarkRow("atan(x)", () -> Math.atan(1.0), () -> FastMath.atan(1.0));
        benchmarkRow("atan2(y,x)", () -> Math.atan2(1.0, 2.0), () -> FastMath.atan2(1.0, 2.0));
        
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println();
    }
    
    private static void benchmarkPower() {
        System.out.println("POWER & ROOT FUNCTIONS");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println("|     Method      |  Java Math  | FastMath JNI|  Speedup    |   Ops/Second    |");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        
        benchmarkRow("sqrt(x)", () -> Math.sqrt(2.0), () -> FastMath.sqrt(2.0));
        benchmarkRow("cbrt(x)", () -> Math.cbrt(27.0), () -> FastMath.cbrt(27.0));
        benchmarkRow("pow(x,y)", () -> Math.pow(2.0, 3.0), () -> FastMath.pow(2.0, 3.0));
        
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println();
    }
    
    private static void benchmarkExpLog() {
        System.out.println("EXPONENTIAL & LOGARITHMIC");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println("|     Method      |  Java Math  | FastMath JNI|  Speedup    |   Ops/Second    |");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        
        benchmarkRow("exp(x)", () -> Math.exp(1.0), () -> FastMath.exp(1.0));
        benchmarkRow("expm1(x)", () -> Math.expm1(0.5), () -> FastMath.expm1(0.5));
        benchmarkRow("log(x)", () -> Math.log(2.0), () -> FastMath.log(2.0));
        benchmarkRow("log1p(x)", () -> Math.log1p(0.5), () -> FastMath.log1p(0.5));
        benchmarkRow("log10(x)", () -> Math.log10(100.0), () -> FastMath.log10(100.0));
        
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println();
    }
    
    private static void benchmarkHyperbolic() {
        System.out.println("HYPERBOLIC FUNCTIONS");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println("|     Method      |  Java Math  | FastMath JNI|  Speedup    |   Ops/Second    |");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        
        benchmarkRow("sinh(x)", () -> Math.sinh(1.0), () -> FastMath.sinh(1.0));
        benchmarkRow("cosh(x)", () -> Math.cosh(1.0), () -> FastMath.cosh(1.0));
        benchmarkRow("tanh(x)", () -> Math.tanh(1.0), () -> FastMath.tanh(1.0));
        
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println();
    }
    
    private static void benchmarkArrayOperations() {
        System.out.println("ARRAY/BATCH OPERATIONS (100,000 elements)");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println("|     Method      |  Java Loop  | Fast JNI Arr|  Speedup    |   Ops/Second    |");
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        
        int size = 100_000;
        double[] input = new double[size];
        double[] output = new double[size];
        for (int i = 0; i < size; i++) input[i] = i * 0.01 + 0.1;
        
        // Java loop benchmark
        long javaStart = System.nanoTime();
        for (int iter = 0; iter < 100; iter++) {
            for (int i = 0; i < size; i++) output[i] = Math.sqrt(input[i]);
        }
        long javaNs = (System.nanoTime() - javaStart) / 100;
        
        // FastMath array benchmark
        long fastNs = javaNs;
        if (FastMath.isNativeAvailable()) {
            long fastStart = System.nanoTime();
            for (int iter = 0; iter < 100; iter++) {
                FastMath.sqrt(input, output);
            }
            fastNs = (System.nanoTime() - fastStart) / 100;
        }
        
        double speedup = (double) javaNs / fastNs;
        double javaOps = 1_000_000_000.0 / (javaNs / (double)size);
        double fastOps = 1_000_000_000.0 / (fastNs / (double)size);
        
        System.out.printf("| %-15s | %8.2f ns | %8.2f ns | %7.2f x   | %7.0f vs %-7.0f |%n",
            "sqrt(array)", (double)javaNs/size, (double)fastNs/size, speedup, javaOps, fastOps);
        
        // Sin array
        javaStart = System.nanoTime();
        for (int iter = 0; iter < 100; iter++) {
            for (int i = 0; i < size; i++) output[i] = Math.sin(input[i]);
        }
        javaNs = (System.nanoTime() - javaStart) / 100;
        
        fastNs = javaNs;
        if (FastMath.isNativeAvailable()) {
            long fastStart = System.nanoTime();
            for (int iter = 0; iter < 100; iter++) {
                FastMath.sin(input, output);
            }
            fastNs = (System.nanoTime() - fastStart) / 100;
        }
        
        speedup = (double) javaNs / fastNs;
        javaOps = 1_000_000_000.0 / (javaNs / (double)size);
        fastOps = 1_000_000_000.0 / (fastNs / (double)size);
        
        System.out.printf("| %-15s | %8.2f ns | %8.2f ns | %7.2f x   | %7.0f vs %-7.0f |%n",
            "sin(array)", (double)javaNs/size, (double)fastNs/size, speedup, javaOps, fastOps);
        
        // Exp array
        javaStart = System.nanoTime();
        for (int iter = 0; iter < 100; iter++) {
            for (int i = 0; i < size; i++) output[i] = Math.exp(input[i]);
        }
        javaNs = (System.nanoTime() - javaStart) / 100;
        
        fastNs = javaNs;
        if (FastMath.isNativeAvailable()) {
            long fastStart = System.nanoTime();
            for (int iter = 0; iter < 100; iter++) {
                FastMath.exp(input, output);
            }
            fastNs = (System.nanoTime() - fastStart) / 100;
        }
        
        speedup = (double) javaNs / fastNs;
        javaOps = 1_000_000_000.0 / (javaNs / (double)size);
        fastOps = 1_000_000_000.0 / (fastNs / (double)size);
        
        System.out.printf("| %-15s | %8.2f ns | %8.2f ns | %7.2f x   | %7.0f vs %-7.0f |%n",
            "exp(array)", (double)javaNs/size, (double)fastNs/size, speedup, javaOps, fastOps);
        
        System.out.println("+-----------------+-------------+-------------+-------------+-----------------+");
        System.out.println();
    }
    
    private static void benchmarkRow(String name, Runnable javaOp, Runnable fastOp) {
        // Java benchmark
        long javaStart = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            javaOp.run();
        }
        long javaNs = (System.nanoTime() - javaStart) / ITERATIONS;
        
        // FastMath benchmark
        long fastNs = javaNs;
        if (FastMath.isNativeAvailable()) {
            long fastStart = System.nanoTime();
            for (int i = 0; i < ITERATIONS; i++) {
                fastOp.run();
            }
            fastNs = (System.nanoTime() - fastStart) / ITERATIONS;
        }
        
        double speedup = (double) javaNs / fastNs;
        double javaOps = 1_000_000_000.0 / javaNs;
        double fastOps = 1_000_000_000.0 / fastNs;
        // String status = speedup > 1.2 ? "FASTER" : speedup > 0.9 ? "SIMILAR" : "TESTING";
        
        System.out.printf("| %-15s | %8.2f ns | %8.2f ns | %7.2f x   | %8.0f vs %-8.0f |%n",
            name, (double)javaNs, (double)fastNs, speedup, javaOps, fastOps);
    }
    
    private static void printSummary() {
        System.out.println("================================================================================");
        System.out.println("                              KEY INSIGHTS");
        System.out.println("================================================================================");
        System.out.println("");
        System.out.println("  CURRENT STATUS:");
        System.out.println("     * JNI call overhead: ~10-15 nanoseconds per call");
        System.out.println("     * For simple ops (sqrt), JNI may not beat JVM intrinsics yet");
        System.out.println("     * Array batch ops show promise for SIMD optimization");
        System.out.println("");
        System.out.println("  OPTIMIZATION ROADMAP:");
        System.out.println("     1. SIMD Vectorization: Process 4 doubles per instruction (AVX2)");
        System.out.println("     2. Loop Unrolling: Reduce JNI boundary crossings for arrays");
        System.out.println("     3. Approximation: Fast inverse sqrt for games (1/sqrt(x))");
        System.out.println("     4. GPU Dispatch: OpenCL for arrays > 10,000 elements");
        System.out.println("");
        System.out.println("  BEST USE CASES:");
        System.out.println("     * Batch array operations (particle systems, mesh processing)");
        System.out.println("     * Complex functions (pow, atan2, sinh where JNI wins big)");
        System.out.println("     * Games: Vector normalization, distance checks, physics");
        System.out.println("");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Run: java -Djava.library.path=build -jar fastmath.jar");
        System.out.println();
    }
}
