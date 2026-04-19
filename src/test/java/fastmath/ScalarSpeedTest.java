package fastmath;

import java.util.function.DoubleSupplier;

/**
 * Quick scalar speed test: Java Math vs FastMath (after fix)
 */
public class ScalarSpeedTest {
    
    private static final int WARMUP = 100_000;
    private static final int ITERATIONS = 5_000_000;
    
    public static void main(String[] args) {
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("     SCALAR SPEED TEST: Java Math vs FastMath");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Iterations: " + ITERATIONS + " per test");
        System.out.println("Native available: " + FastMath.isNativeAvailable());
        System.out.println();
        
        // Warmup
        System.out.print("Warming up... ");
        for (int i = 0; i < WARMUP; i++) {
            Math.sin(i * 0.001);
            FastMath.sin(i * 0.001);
            Math.cos(i * 0.001);
            FastMath.cos(i * 0.001);
            Math.exp(i * 0.0001);
            FastMath.exp(i * 0.0001);
            Math.sqrt(i);
            FastMath.sqrt(i);
        }
        System.out.println("Done.\n");
        
        // Test sin
        test("sin(1.0)", () -> Math.sin(1.0), () -> FastMath.sin(1.0));
        
        // Test cos
        test("cos(1.0)", () -> Math.cos(1.0), () -> FastMath.cos(1.0));
        
        // Test exp
        test("exp(1.0)", () -> Math.exp(1.0), () -> FastMath.exp(1.0));
        
        // Test sqrt
        test("sqrt(2.0)", () -> Math.sqrt(2.0), () -> FastMath.sqrt(2.0));
        
        // Test fastSqrt (approximation)
        test3("sqrt fast", () -> Math.sqrt(2.0), () -> FastMath.sqrt(2.0), () -> FastMath.fastSqrt(2.0));
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("  KEY: FastMath now uses FastMathPure (polynomials)");
        System.out.println("  Should be faster or equal to Math for scalars");
        System.out.println("═══════════════════════════════════════════════════════════════\n");
    }
    
    private static void test3(String name, DoubleSupplier javaMath, DoubleSupplier fastMath, DoubleSupplier fastApprox) {
        double accumulator = 0;
        
        // Java Math
        long t1 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            accumulator += javaMath.getAsDouble();
        }
        long javaNs = (System.nanoTime() - t1) / ITERATIONS;
        
        // FastMath (now pure Java)
        long t2 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            accumulator += fastMath.getAsDouble();
        }
        long fastNs = (System.nanoTime() - t2) / ITERATIONS;
        
        // Fast approximation
        long t3 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            accumulator += fastApprox.getAsDouble();
        }
        long approxNs = (System.nanoTime() - t3) / ITERATIONS;
        
        double speedupVsJava = (double) javaNs / approxNs;
        
        System.out.printf("%-12s: Java=%2d ns | FastMath=%2d ns | fastSqrt=%2d ns | %.1fx vs Java (checksum=%.1f)%n",
            name, javaNs, fastNs, approxNs, speedupVsJava, accumulator);
    }
    
    private static void test(String name, DoubleSupplier javaMath, DoubleSupplier fastMath) {
        double accumulator = 0;  // Prevent dead code elimination
        
        // Java Math
        long t1 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            accumulator += javaMath.getAsDouble();
        }
        long javaNs = (System.nanoTime() - t1) / ITERATIONS;
        
        // FastMath  
        long t2 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            accumulator += fastMath.getAsDouble();
        }
        long fastNs = (System.nanoTime() - t2) / ITERATIONS;
        
        double speedup = (double) javaNs / fastNs;
        String result;
        if (speedup > 1.2) result = "✅ FASTER";
        else if (speedup < 0.8) result = "❌ SLOWER";
        else result = "⚡ EQUAL";
        
        System.out.printf("%-12s: Java=%4d ns/op | FastMath=%4d ns/op | %5.2fx %s (checksum=%.6f)%n",
            name, javaNs, fastNs, speedup, result, accumulator);
    }
}
