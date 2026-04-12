package fastmath;

import java.util.*;

/**
 * Complete benchmark suite for ALL FastMath modules.
 * Run: mvn test-compile exec:java -Dexec.mainClass="fastmath.AllModulesBenchmark" 
 *      -Dexec.classpathScope=test -Dexec.vmArgs="-Djava.library.path=build"
 */
public class AllModulesBenchmark {
    
    private static final int WARMUP = 5;
    private static final int ITERATIONS = 50;
    
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║          FastMath ALL MODULES Benchmark Suite                          ║");
        System.out.println("║          Java " + System.getProperty("java.version") + " | " + System.getProperty("os.arch") + "                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════╝\n");
        
        warmup();
        
        // Run all benchmarks
        benchmarkFastMathCore();
        benchmarkFastMathVectors();
        benchmarkFastMathNoise();
        benchmarkFastMathRandom();
        
        System.out.println("\n✅ ALL BENCHMARKS COMPLETE\n");
    }
    
    private static void warmup() {
        System.out.print("Warming up JVM...");
        for (int i = 0; i < WARMUP * 100; i++) {
            Math.sin(i * 0.01);
            FastMath.sin(i * 0.01);
        }
        System.out.println(" DONE\n");
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // CORE MATH BENCHMARK
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkFastMathCore() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("MODULE: FastMath (Core Math)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-12s %12s %12s %10s %15s%n", "Operation", "Math", "FastMath", "Speedup", "Implementation");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        // Scalar operations
        benchmarkScalar("sqrt", 100000, x -> Math.sqrt(x), x -> FastMath.sqrt(x));
        benchmarkScalar("sin", 100000, x -> Math.sin(x), x -> FastMath.sin(x));
        benchmarkScalar("cos", 100000, x -> Math.cos(x), x -> FastMath.cos(x));
        benchmarkScalar("exp", 100000, x -> Math.exp(x), x -> FastMath.exp(x));
        benchmarkScalar("log", 100000, x -> Math.log(x), x -> FastMath.log(x));
        benchmarkScalar("atan2", 50000, x -> Math.atan2(x, x+1), x -> FastMath.atan2(x, x+1));
        benchmarkScalar("fastInvSqrt", 100000, x -> 1.0f/(float)Math.sqrt(x), x -> FastMath.fastInvSqrt((float)x));
        
        // Array operations
        System.out.println();
        System.out.printf("%-10s %8s %12s %12s %10s %15s%n", "Array", "Size", "Math", "FastMath", "Speedup", "Backend");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        benchmarkArray("sqrt", 1000, 
            () -> { double[] a = new double[1000], b = new double[1000]; for(int i=0;i<1000;i++) a[i]=i*0.001+0.1; for(int i=0;i<1000;i++) b[i]=Math.sqrt(a[i]); },
            () -> { double[] a = new double[1000], b = new double[1000]; for(int i=0;i<1000;i++) a[i]=i*0.001+0.1; FastMath.sqrt(a, b); });
        
        benchmarkArray("sqrt", 10000, 
            () -> { double[] a = new double[10000], b = new double[10000]; for(int i=0;i<10000;i++) a[i]=i*0.0001+0.1; for(int i=0;i<10000;i++) b[i]=Math.sqrt(a[i]); },
            () -> { double[] a = new double[10000], b = new double[10000]; for(int i=0;i<10000;i++) a[i]=i*0.0001+0.1; FastMath.sqrt(a, b); });
        
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // VECTORS BENCHMARK
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkFastMathVectors() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("MODULE: FastMathVectors (SIMD Vector & Matrix)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-25s %12s %12s %10s%n", "Operation", "Java/Math", "FastMath", "Speedup");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        // Dot product batch
        int count = 5000;
        double[] va = new double[count * 3];
        double[] vb = new double[count * 3];
        double[] vout = new double[count];
        for (int i = 0; i < count * 3; i++) { va[i] = Math.random(); vb[i] = Math.random(); }
        
        long t1 = time(() -> {
            for (int i = 0; i < count; i++) {
                int idx = i * 3;
                vout[i] = va[idx]*vb[idx] + va[idx+1]*vb[idx+1] + va[idx+2]*vb[idx+2];
            }
        }, 20);
        long t2 = time(() -> FastMathVectors.dot3Batch(va, vb, vout, count), 20);
        printResult("dot3Batch(" + count + ")", t1, t2, "SIMD");
        
        // Cross product
        double[] crossOut = new double[3];
        t1 = time(() -> {
            for (int i = 0; i < 100000; i++) {
                crossOut[0] = va[1]*vb[2] - va[2]*vb[1];
                crossOut[1] = va[2]*vb[0] - va[0]*vb[2];
                crossOut[2] = va[0]*vb[1] - va[1]*vb[0];
            }
        }, 20);
        t2 = time(() -> {
            for (int i = 0; i < 100000; i++) {
                FastMathVectors.cross3(va[0], va[1], va[2], vb[0], vb[1], vb[2], crossOut);
            }
        }, 20);
        printResult("cross3(100K)", t1, t2, "SIMD");
        
        // 4x4 Matrix multiplication
        double[] ma = new double[16];
        double[] mb = new double[16];
        double[] mc = new double[16];
        FastMathVectors.identity4x4(ma);
        FastMathVectors.identity4x4(mb);
        
        t1 = time(() -> {
            for (int iter = 0; iter < 50000; iter++) {
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        mc[r*4+c] = ma[r*4]*mb[c] + ma[r*4+1]*mb[4+c] + ma[r*4+2]*mb[8+c] + ma[r*4+3]*mb[12+c];
                    }
                }
            }
        }, 10);
        t2 = time(() -> {
            for (int iter = 0; iter < 50000; iter++) {
                FastMathVectors.mul4x4(ma, mb, mc);
            }
        }, 10);
        printResult("mul4x4(50K)", t1, t2, "SIMD");
        
        // Matrix-vector batch transform
        double[] vectors = new double[10000 * 4];
        double[] mout = new double[10000 * 4];
        for (int i = 0; i < 10000 * 4; i++) vectors[i] = Math.random();
        
        t1 = time(() -> {
            for (int i = 0; i < 10000; i++) {
                int idx = i * 4;
                for (int r = 0; r < 4; r++) {
                    mout[idx + r] = ma[r*4]*vectors[idx] + ma[r*4+1]*vectors[idx+1] + ma[r*4+2]*vectors[idx+2] + ma[r*4+3]*vectors[idx+3];
                }
            }
        }, 10);
        t2 = time(() -> FastMathVectors.mul4x4VectorBatch(ma, vectors, mout, 10000), 10);
        printResult("mul4x4Batch(10K)", t1, t2, "SIMD");
        
        // Fast inverse length (Quake)
        t1 = time(() -> {
            for (int i = 0; i < 1000000; i++) {
                float len = (float)(1.0 / Math.sqrt(va[0]*va[0] + va[1]*va[1] + va[2]*va[2]));
            }
        }, 5);
        t2 = time(() -> {
            for (int i = 0; i < 1000000; i++) {
                float len = FastMathVectors.fastInvLength3((float)va[0], (float)va[1], (float)va[2]);
            }
        }, 5);
        printResult("fastInvLen(1M)", t1, t2, "Quake");
        
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // NOISE BENCHMARK
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkFastMathNoise() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("MODULE: FastMathNoise (Procedural Generation)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-20s %12s %12s %10s%n", "Operation", "Time", "", "");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        // Perlin 2D single
        long t = time(() -> {
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    FastMathNoise.perlin2D(x * 0.1, y * 0.1);
                }
            }
        }, 10);
        System.out.printf("%-20s %8.2f ms%n", "perlin2D(10K calls)", t/1e6);
        
        // Perlin grid batch
        double[] noiseMap = new double[512 * 512];
        long t1 = time(() -> {
            int w = 512, h = 512;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    noiseMap[y * w + x] = FastMathNoise.perlin2D(x * 0.01, y * 0.01);
                }
            }
        }, 5);
        long t2 = time(() -> FastMathNoise.perlinGrid(noiseMap, 512, 512, 0.01, 0, 0), 5);
        printResult("perlinGrid(262K)", t1, t2, "JNI SIMD");
        
        // Simplex noise
        t = time(() -> {
            for (int y = 0; y < 100; y++) {
                for (int x = 0; x < 100; x++) {
                    FastMathNoise.simplex2D(x * 0.1, y * 0.1);
                }
            }
        }, 10);
        System.out.printf("%-20s %8.2f ms%n", "simplex2D(10K calls)", t/1e6);
        
        // Fractal Brownian Motion
        t = time(() -> {
            for (int y = 0; y < 50; y++) {
                for (int x = 0; x < 50; x++) {
                    FastMathNoise.fBm2D(x * 0.1, y * 0.1, 4, 2.0, 0.5);
                }
            }
        }, 10);
        System.out.printf("%-20s %8.2f ms%n", "fBm2D(2.5K, 4-octave)", t/1e6);
        
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // RANDOM BENCHMARK
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkFastMathRandom() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("MODULE: FastMathRandom (High-Performance RNG)");
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.printf("%-25s %12s %12s %10s%n", "Generator", "java.util", "FastMath", "Speedup");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        
        // Xoshiro256** vs Random
        java.util.Random javaRand = new java.util.Random(12345);
        FastMathRandom.Xoshiro256StarStar fastRand = new FastMathRandom.Xoshiro256StarStar(12345);
        
        long t1 = time(() -> {
            for (int i = 0; i < 1000000; i++) javaRand.nextDouble();
        }, 20);
        long t2 = time(() -> {
            for (int i = 0; i < 1000000; i++) fastRand.nextDouble();
        }, 20);
        printResult("nextDouble(1M)", t1, t2, "Java vs Xoshiro**");
        
        // Batch generation
        double[] batch = new double[100000];
        t1 = time(() -> {
            for (int i = 0; i < batch.length; i++) batch[i] = javaRand.nextDouble();
        }, 20);
        t2 = time(() -> FastMathRandom.nextDoubleBatch(batch, 12345), 20);
        printResult("batch(100K)", t1, t2, "JNI SIMD");
        
        // PCG32
        FastMathRandom.PCG32 pcg = new FastMathRandom.PCG32(12345);
        t2 = time(() -> {
            for (int i = 0; i < 1000000; i++) pcg.nextInt();
        }, 20);
        System.out.printf("%-25s %12s %8.2f ms %6s%n", "PCG32(1M)", "-", t2/1e6, "-");
        
        // Xavier init
        double[] weights = new double[10000];
        t2 = time(() -> FastMathRandom.xavierInit(weights, 12345, 100, 100), 20);
        System.out.printf("%-25s %12s %8.2f ms %6s%n", "xavierInit(10K)", "-", t2/1e6, "-");
        
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static long time(Runnable r, int iterations) {
        for (int i = 0; i < 3; i++) r.run(); // Warmup
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) r.run();
        return (System.nanoTime() - start) / iterations;
    }
    
    private static void benchmarkScalar(String name, int iterations, 
                                       java.util.function.DoubleUnaryOperator math,
                                       java.util.function.DoubleUnaryOperator fast) {
        long t1 = time(() -> {
            for (int i = 0; i < iterations; i++) math.applyAsDouble(i * 0.0001);
        }, 20);
        long t2 = time(() -> {
            for (int i = 0; i < iterations; i++) fast.applyAsDouble(i * 0.0001);
        }, 20);
        double speedup = (double)t1 / t2;
        String impl = FastMath.isNativeAvailable() ? (name.equals("fastInvSqrt") ? "Quake" : "Java") : "Java";
        System.out.printf("%-12s %8.2f ns %8.2f ns %7.2fx %15s%n", 
            name, (double)t1/iterations, (double)t2/iterations, speedup, impl);
    }
    
    private static void benchmarkArray(String name, int size, Runnable math, Runnable fast) {
        long t1 = time(math, 20);
        long t2 = time(fast, 20);
        double speedup = (double)t1 / t2;
        String backend = FastMath.isNativeAvailable() ? "JNI SIMD" : "Java";
        System.out.printf("%-10s %,8d %8.2f ms %8.2f ms %7.2fx %15s%n",
            name, size, t1/1e6, t2/1e6, speedup, backend);
    }
    
    private static void printResult(String name, long t1, long t2, String impl) {
        double speedup = t2 > 0 ? (double)t1 / t2 : 1.0;
        System.out.printf("%-25s %8.2f ms %8.2f ms %7.2fx %15s%n", 
            name, t1/1e6, t2/1e6, speedup, impl);
    }
}
