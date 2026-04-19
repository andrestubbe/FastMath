package fastmath;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Realistic sqrt comparison: speed vs accuracy
 */
public class SqrtComparison {
    
    private static final int WARMUP = 200_000;
    private static final int ITERATIONS = 2_000_000;
    private static final double[] testValues = new double[ITERATIONS];
    private static double[] results = new double[ITERATIONS];
    
    public static void main(String[] args) {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("     SQRT SPEED & ACCURACY TEST");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        // Generate varied test data (prevents constant folding)
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < ITERATIONS; i++) {
            testValues[i] = rng.nextDouble(0.001, 1000.0);
        }
        results = new double[ITERATIONS];
        
        System.out.println("Iterations: " + ITERATIONS);
        System.out.println("Native available: " + FastMath.isNativeAvailable());
        System.out.println();
        
        // Warmup
        System.out.print("Warming up... ");
        for (int i = 0; i < WARMUP; i++) {
            results[i % 1000] = Math.sqrt(testValues[i % 1000]);
            results[i % 1000] = FastMath.sqrt(testValues[i % 1000]);
            results[i % 1000] = FastMath.fastSqrt(testValues[i % 1000]);
        }
        System.out.println("Done.\n");
        
        // Test 1: Math.sqrt
        long t1 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            results[i] = Math.sqrt(testValues[i]);
        }
        long mathTime = (System.nanoTime() - t1);
        double mathNs = (double) mathTime / ITERATIONS;
        
        // Store Math.sqrt results as "ground truth"
        double[] groundTruth = results.clone();
        
        // Test 2: FastMath.sqrt (now delegates to Math.sqrt)
        long t2 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            results[i] = FastMath.sqrt(testValues[i]);
        }
        long fastTime = (System.nanoTime() - t2);
        double fastNs = (double) fastTime / ITERATIONS;
        
        // Test 3: FastMath.fastSqrt (approximation)
        long t3 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            results[i] = FastMath.fastSqrt(testValues[i]);
        }
        long approxTime = (System.nanoTime() - t3);
        double approxNs = (double) approxTime / ITERATIONS;
        
        // Calculate accuracy
        double maxError = 0;
        double sumError = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            double error = Math.abs(results[i] - groundTruth[i]);
            maxError = Math.max(maxError, error);
            sumError += error;
        }
        double avgError = sumError / ITERATIONS;
        double avgRelError = avgError / (sumError / ITERATIONS + 1e-10);
        
        // Print results
        System.out.println("Speed Results:");
        System.out.println("+----------------+----------+----------+------------------");
        System.out.println("| Method         |  ns/op   | vs Math  | Notes            ");
        System.out.println("+----------------+----------+----------+------------------");
        System.out.printf("| Math.sqrt()    | %8.2f | %8s | Hardware SQRTSD  |%n", mathNs, "1.00x");
        System.out.printf("| FastMath.sqrt  | %8.2f | %8s | Delegates to Math|%n", fastNs, String.format("%.2fx", fastNs/mathNs));
        System.out.printf("| FastMath.fastSqrt| %8.2f | %8s | Quake bit-hack   |%n", approxNs, String.format("%.2fx", approxNs/mathNs));
        System.out.println("+----------------+----------+----------+------------------");
        
        System.out.println("\nAccuracy of fastSqrt (vs Math.sqrt ground truth):");
        System.out.printf("  Max absolute error: %.6f%n", maxError);
        System.out.printf("  Avg absolute error: %.6f%n", avgError);
        System.out.printf("  Max relative error: %.4f%%%n", 100 * maxError / Math.sqrt(1000));
        
        // Sample some actual values
        System.out.println("\nSample values (input → Math.sqrt → fastSqrt → error):");
        for (int i = 0; i < 5; i++) {
            double x = testValues[i];
            double math = Math.sqrt(x);
            double fast = FastMath.fastSqrt(x);
            double err = fast - math;
            System.out.printf("  %.4f → %.6f vs %.6f (err=%+.6f)%n", x, math, fast, err);
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("  CONCLUSION:");
        if (approxNs < mathNs * 0.8) {
            System.out.printf("  ✅ fastSqrt is %.1fx faster than Math.sqrt%n", mathNs/approxNs);
            System.out.println("  ⚠️  But with ~1-2% error — use only for games/graphics");
        } else if (approxNs > mathNs * 1.2) {
            System.out.println("  ❌ fastSqrt is SLOWER — Math.sqrt is already optimal");
        } else {
            System.out.println("  ⚡ Similar speed — use Math.sqrt for full precision");
        }
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }
}
