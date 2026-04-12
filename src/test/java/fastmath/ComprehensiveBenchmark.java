package fastmath;

import java.util.*;

/**
 * Comprehensive benchmark suite for FastMath.
 * Generates statistics for GitHub README and performance reports.
 * 
 * Run: mvn test-compile exec:java -Dexec.mainClass="fastmath.ComprehensiveBenchmark" 
 *      -Dexec.classpathScope=test -Dexec.vmArgs="-Djava.library.path=build -Dfastmath.gpu=true"
 */
public class ComprehensiveBenchmark {
    
    private static final int WARMUP = 10;
    private static final int ITERATIONS = 100;
    private static final int[] ARRAY_SIZES = {100, 1000, 10000, 100000, 1000000};
    
    // Results storage
    private static final List<BenchmarkResult> results = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║          FastMath Comprehensive Benchmark Suite                        ║");
        System.out.println("║          GitHub: https://github.com/andrestubbe/FastMath             ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // System info
        System.out.println("System Configuration:");
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        System.out.println("  Available Processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("  Native Library: " + (FastMath.isNativeAvailable() ? "✅ Loaded" : "❌ Not available"));
        System.out.println("  GPU OpenCL: " + (FastMath.isGpuEnabled() ? "✅ Enabled" : "❌ Not enabled"));
        System.out.println("  GPU Threshold: " + FastMath.GPU_THRESHOLD + " elements");
        System.out.println();
        
        // Warmup JVM
        System.out.print("Warming up JVM...");
        warmup();
        System.out.println(" Done!\n");
        
        // Run benchmarks
        benchmarkScalarOperations();
        benchmarkArrayOperations();
        benchmarkVectorOperations();
        benchmarkNoiseOperations();
        
        // Generate reports
        generateMarkdownTable();
        generateCSVOutput();
        generateGitHubStats();
    }
    
    private static void warmup() {
        for (int i = 0; i < WARMUP * 1000; i++) {
            Math.sin(i * 0.001);
            FastMath.sin(i * 0.001);
        }
    }
    
    private static void benchmarkScalarOperations() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("SCALAR OPERATIONS (ns per operation)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-15s %12s %12s %10s%n", "Operation", "Math.*", "FastMath", "Speedup");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        benchmarkScalar("sin", ITERATIONS * 100, 
            x -> Math.sin(x), x -> FastMath.sin(x));
        benchmarkScalar("cos", ITERATIONS * 100,
            x -> Math.cos(x), x -> FastMath.cos(x));
        benchmarkScalar("exp", ITERATIONS * 100,
            x -> Math.exp(x), x -> FastMath.exp(x));
        benchmarkScalar("log", ITERATIONS * 100,
            x -> Math.log(x), x -> FastMath.log(x));
        benchmarkScalar("sqrt", ITERATIONS * 100,
            x -> Math.sqrt(x), x -> FastMath.sqrt(x));
        benchmarkScalar("atan2", ITERATIONS * 100,
            x -> Math.atan2(x, x + 1), x -> FastMath.atan2(x, x + 1));
        benchmarkScalar("pow", ITERATIONS * 50,
            x -> Math.pow(x, 2.5), x -> FastMath.pow(x, 2.5));
        benchmarkScalar("fastInvSqrt", ITERATIONS * 100,
            x -> 1.0f / (float)Math.sqrt(x), x -> FastMath.fastInvSqrt((float)x));
        
        System.out.println();
    }
    
    private static void benchmarkArrayOperations() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("ARRAY OPERATIONS (time in ms, lower is better)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-8s %10s %12s %12s %10s %12s%n", 
            "Size", "Array", "Java Math", "FastMath", "Speedup", "Backend");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        for (int size : ARRAY_SIZES) {
            double[] input = createArray(size);
            double[] output = new double[size];
            
            // sqrt
            benchmarkArray("sqrt", size, input, output,
                () -> { for (int i = 0; i < size; i++) output[i] = Math.sqrt(input[i]); },
                () -> FastMath.sqrt(input, output));
            
            // sin
            benchmarkArray("sin", size, input, output,
                () -> { for (int i = 0; i < size; i++) output[i] = Math.sin(input[i]); },
                () -> FastMath.sin(input, output));
            
            // exp
            benchmarkArray("exp", size, input, output,
                () -> { for (int i = 0; i < size; i++) output[i] = Math.exp(input[i]); },
                () -> FastMath.exp(input, output));
        }
        
        System.out.println();
    }
    
    private static void benchmarkVectorOperations() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("VECTOR & MATRIX OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-25s %12s %12s %10s%n", "Operation", "Java/Math", "FastMath", "Speedup");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        int count = 10000;
        
        // Dot product batch
        double[] va = createArray(count * 3);
        double[] vb = createArray(count * 3);
        double[] vout = new double[count];
        
        long t1 = time(() -> {
            for (int i = 0; i < count; i++) {
                int idx = i * 3;
                vout[i] = va[idx]*vb[idx] + va[idx+1]*vb[idx+1] + va[idx+2]*vb[idx+2];
            }
        }, 10);
        
        long t2 = time(() -> FastMathVectors.dot3Batch(va, vb, vout, count), 10);
        
        double speedup = (double)t1 / t2;
        System.out.printf("%-25s %8.2f ms %8.2f ms %7.2fx%n", 
            "dot3Batch(" + count + ")", t1/1e6, t2/1e6, speedup);
        results.add(new BenchmarkResult("dot3Batch", count, t1, t2, speedup, "JNI SIMD"));
        
        // Matrix-vector batch
        double[] matrix = new double[16];
        double[] vectors = createArray(count * 4);
        double[] mout = new double[count * 4];
        FastMathVectors.identity4x4(matrix);
        
        t1 = time(() -> {
            for (int i = 0; i < count; i++) {
                int idx = i * 4;
                for (int row = 0; row < 4; row++) {
                    mout[idx + row] = matrix[row*4]*vectors[idx] + 
                                     matrix[row*4+1]*vectors[idx+1] + 
                                     matrix[row*4+2]*vectors[idx+2] + 
                                     matrix[row*4+3]*vectors[idx+3];
                }
            }
        }, 10);
        
        t2 = time(() -> FastMathVectors.mul4x4VectorBatch(matrix, vectors, mout, count), 10);
        
        speedup = (double)t1 / t2;
        System.out.printf("%-25s %8.2f ms %8.2f ms %7.2fx%n", 
            "mul4x4Batch(" + count + ")", t1/1e6, t2/1e6, speedup);
        results.add(new BenchmarkResult("mul4x4Batch", count, t1, t2, speedup, "JNI SIMD"));
        
        // 4x4 matrix multiply
        double[] ma = new double[16];
        double[] mb = new double[16];
        double[] mc = new double[16];
        FastMathVectors.identity4x4(ma);
        FastMathVectors.identity4x4(mb);
        
        t1 = time(() -> {
            for (int i = 0; i < 100000; i++) {
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        mc[r*4+c] = ma[r*4]*mb[c] + ma[r*4+1]*mb[4+c] + 
                                   ma[r*4+2]*mb[8+c] + ma[r*4+3]*mb[12+c];
                    }
                }
            }
        }, 10);
        
        t2 = time(() -> {
            for (int i = 0; i < 100000; i++) {
                FastMathVectors.mul4x4(ma, mb, mc);
            }
        }, 10);
        
        speedup = (double)t1 / t2;
        System.out.printf("%-25s %8.2f ms %8.2f ms %7.2fx%n", 
            "mul4x4(100K ops)", t1/1e6, t2/1e6, speedup);
        results.add(new BenchmarkResult("mul4x4", 100000, t1, t2, speedup, "JNI SIMD"));
        
        System.out.println();
    }
    
    private static void benchmarkNoiseOperations() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("NOISE GENERATION (1024x1024 = 1M pixels)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        
        int width = 1024;
        int height = 1024;
        double[] noiseMap = new double[width * height];
        
        long t1 = time(() -> {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    noiseMap[y * width + x] = FastMathNoise.perlin2D(x * 0.01, y * 0.01);
                }
            }
        }, 3);
        
        long t2 = time(() -> FastMathNoise.perlinGrid(noiseMap, width, height, 0.01, 0, 0), 3);
        
        double speedup = (double)t1 / t2;
        System.out.printf("%-25s %8.2f ms %8.2f ms %7.2fx%n", 
            "perlinGrid(1M)", t1/1e6, t2/1e6, speedup);
        results.add(new BenchmarkResult("perlinGrid", width*height, t1, t2, speedup, "JNI SIMD"));
        
        // Simplex noise scalar
        t1 = time(() -> {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    noiseMap[y * width + x] = FastMathNoise.simplex2D(x * 0.01, y * 0.01);
                }
            }
        }, 3);
        System.out.printf("%-25s %8.2f ms %12s %7s%n", 
            "simplex2D(1M)", t1/1e6, "-", "-");
        
        System.out.println();
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // REPORT GENERATION
    // ═════════════════════════════════════════════════════════════════════════
    
    private static void generateMarkdownTable() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("MARKDOWN TABLE FOR README.md");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("| Operation | Size | Java (ms) | FastMath (ms) | Speedup | Backend |");
        System.out.println("|-----------|------|-----------|---------------|---------|---------|");
        
        for (BenchmarkResult r : results) {
            System.out.printf("| %-9s | %,6d | %9.2f | %13.2f | %6.2fx | %-7s |%n",
                r.operation, r.size, r.javaTime/1e6, r.fastTime/1e6, r.speedup, r.backend);
        }
        System.out.println();
    }
    
    private static void generateCSVOutput() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("CSV OUTPUT (for charts/graphs)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("operation,size,java_ms,fastmath_ms,speedup,backend");
        
        for (BenchmarkResult r : results) {
            System.out.printf("%s,%d,%.2f,%.2f,%.2f,%s%n",
                r.operation, r.size, r.javaTime/1e6, r.fastTime/1e6, r.speedup, r.backend);
        }
        System.out.println();
    }
    
    private static void generateGitHubStats() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("GITHUB BADGE STATS");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println();
        
        // Calculate averages
        double avgArraySpeedup = results.stream()
            .filter(r -> r.size > 1)
            .mapToDouble(r -> r.speedup)
            .average()
            .orElse(0);
        
        double maxSpeedup = results.stream()
            .mapToDouble(r -> r.speedup)
            .max()
            .orElse(0);
        
        long totalOps = results.stream()
            .mapToLong(r -> r.size * ITERATIONS)
            .sum();
        
        System.out.println("Key Statistics:");
        System.out.printf("  🚀 Maximum Speedup: %.1fx%n", maxSpeedup);
        System.out.printf("  📊 Average Array Speedup: %.1fx%n", avgArraySpeedup);
        System.out.printf("  ⚡ Total Operations Tested: %,d%n", totalOps);
        System.out.println();
        System.out.println("Shield Badges:");
        System.out.printf("[![Speedup](https://img.shields.io/badge/speedup-%.0fx-blue)]%n", maxSpeedup);
        System.out.printf("[![Benchmark](https://img.shields.io/badge/benchmark-%.0fM_ops-green)]%n", totalOps / 1_000_000.0);
        System.out.println();
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═════════════════════════════════════════════════════════════════════════
    
    private static double[] createArray(int size) {
        double[] arr = new double[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i * 0.001 + 0.1;
        }
        return arr;
    }
    
    private static long time(Runnable r, int iterations) {
        // Warmup
        for (int i = 0; i < 3; i++) r.run();
        
        // Measure
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            r.run();
        }
        return (System.nanoTime() - start) / iterations;
    }
    
    private static void benchmarkScalar(String name, int iterations,
                                       java.util.function.DoubleUnaryOperator math,
                                       java.util.function.DoubleUnaryOperator fast) {
        // Warmup
        double sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += math.applyAsDouble(i * 0.001);
            sum += fast.applyAsDouble(i * 0.001);
        }
        
        // Benchmark Java Math
        long t1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            sum += math.applyAsDouble(i * 0.001);
        }
        t1 = System.nanoTime() - t1;
        
        // Benchmark FastMath
        long t2 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            sum += fast.applyAsDouble(i * 0.001);
        }
        t2 = System.nanoTime() - t2;
        
        double mathNs = (double) t1 / iterations;
        double fastNs = (double) t2 / iterations;
        double speedup = mathNs / fastNs;
        
        System.out.printf("%-15s %8.2f ns %8.2f ns %7.2fx%n", 
            name, mathNs, fastNs, speedup);
        
        // Prevent dead code elimination
        if (sum == 0) System.out.print("");
        
        results.add(new BenchmarkResult(name, 1, t1, t2, speedup, 
            FastMath.isNativeAvailable() ? "JNI" : "Java"));
    }
    
    private static void benchmarkArray(String name, int size, double[] input, double[] output,
                                        Runnable math, Runnable fast) {
        // Warmup
        for (int i = 0; i < 5; i++) {
            math.run();
            fast.run();
        }
        
        // Benchmark Java
        long t1 = time(math, 10);
        
        // Benchmark FastMath
        long t2 = time(fast, 10);
        
        double speedup = (double) t1 / t2;
        String backend = FastMath.isGpuEnabled() && size >= FastMath.GPU_THRESHOLD ? "GPU" :
                        FastMath.isNativeAvailable() ? "SIMD" : "Java";
        
        System.out.printf("%,8d %8s %8.2f ms %8.2f ms %7.2fx %12s%n",
            size, name, t1/1e6, t2/1e6, speedup, backend);
        
        results.add(new BenchmarkResult(name + "[" + size + "]", size, t1, t2, speedup, backend));
    }
    
    static class BenchmarkResult {
        final String operation;
        final int size;
        final long javaTime;
        final long fastTime;
        final double speedup;
        final String backend;
        
        BenchmarkResult(String op, int size, long javaT, long fastT, double speed, String back) {
            this.operation = op;
            this.size = size;
            this.javaTime = javaT;
            this.fastTime = fastT;
            this.speedup = speed;
            this.backend = backend;
        }
    }
}
