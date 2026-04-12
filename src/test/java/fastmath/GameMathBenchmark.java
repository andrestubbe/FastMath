package fastmath;

/**
 * Game-focused benchmark for sqrt-heavy operations.
 * Tests vector normalization, distance calculations, and physics simulations.
 * 
 * Run: mvn test-compile exec:java -Dexec.mainClass="fastmath.GameMathBenchmark" -Dexec.classpathScope=test
 */
public class GameMathBenchmark {
    
    private static final int WARMUP_ITERATIONS = 100_000;
    private static final int BENCHMARK_ITERATIONS = 1_000_000;
    private static final int PARTICLE_COUNT = 100_000;  // Large particle system
    
    // Simulated game entities
    private static final int ENTITY_COUNT = 10_000;
    private static float[] entityX = new float[ENTITY_COUNT];
    private static float[] entityY = new float[ENTITY_COUNT];
    private static float[] entityZ = new float[ENTITY_COUNT];
    private static float[] velocityX = new float[ENTITY_COUNT];
    private static float[] velocityY = new float[ENTITY_COUNT];
    private static float[] velocityZ = new float[ENTITY_COUNT];
    
    static {
        // Initialize random game state
        java.util.Random rnd = new java.util.Random(12345);
        for (int i = 0; i < ENTITY_COUNT; i++) {
            entityX[i] = rnd.nextFloat() * 1000 - 500;
            entityY[i] = rnd.nextFloat() * 1000 - 500;
            entityZ[i] = rnd.nextFloat() * 1000 - 500;
            velocityX[i] = rnd.nextFloat() * 20 - 10;
            velocityY[i] = rnd.nextFloat() * 20 - 10;
            velocityZ[i] = rnd.nextFloat() * 20 - 10;
        }
    }

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("           🎮 GAME MATH BENCHMARK - Sqrt Focus");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Testing sqrt performance for game development scenarios");
        System.out.println("Iterations: " + BENCHMARK_ITERATIONS);
        System.out.println("Native available: " + FastMath.isNativeAvailable());
        System.out.println();
        
        // Warmup
        System.out.println("Warming up...");
        runWarmup();
        System.out.println("Done.\n");
        
        System.out.println("───────────────────────────────────────────────────────────────");
        System.out.println("                   GAME SCENARIO BENCHMARKS");
        System.out.println("───────────────────────────────────────────────────────────────\n");
        
        // Vector operations (3D games)
        benchmarkVector2DNormalization();
        benchmarkVector3DNormalization();
        
        // Distance calculations (collision, AI, rendering)
        benchmarkDistance2D();
        benchmarkDistance3D();
        
        // Physics (velocity magnitude, acceleration)
        benchmarkVelocityMagnitude();
        
        // Bulk operations (particle systems, vertex processing)
        benchmarkParticleSystemSqrt();
        benchmarkArraySqrt();
        
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("                      SUMMARY");
        System.out.println("═══════════════════════════════════════════════════════════════");
        printRecommendations();
        System.out.println();
    }
    
    private static void runWarmup() {
        // Warmup Java
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            double x = Math.random() * 100;
            double y = Math.random() * 100;
            double z = Math.random() * 100;
            
            // 2D normalize
            double len2d = Math.sqrt(x * x + y * y);
            if (len2d > 0) { x /= len2d; y /= len2d; }
            
            // 3D normalize
            double len3d = Math.sqrt(x * x + y * y + z * z);
            if (len3d > 0) { x /= len3d; y /= len3d; z /= len3d; }
        }
        
        // Warmup FastMath if available
        if (FastMath.isNativeAvailable()) {
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                double x = Math.random() * 100;
                FastMath.sqrt(x);
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // VECTOR NORMALIZATION (Games: camera, movement, physics)
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkVector2DNormalization() {
        System.out.println("▶ 2D Vector Normalization (Movement/Input)");
        System.out.println("───────────────────────────────────────────");
        
        // Java
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            float x = entityX[i % ENTITY_COUNT];
            float y = entityY[i % ENTITY_COUNT];
            float len = (float) Math.sqrt(x * x + y * y);
            if (len > 0.0001f) {
                x /= len;
                y /= len;
            }
            // Prevent dead code elimination
            if (x == 0 && y == 0) System.out.print("");
        }
        long javaTime = (System.nanoTime() - start) / BENCHMARK_ITERATIONS;
        
        // FastMath
        long fastStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            float x = entityX[i % ENTITY_COUNT];
            float y = entityY[i % ENTITY_COUNT];
            float len = (float) FastMath.sqrt(x * x + y * y);
            if (len > 0.0001f) {
                x /= len;
                y /= len;
            }
            if (x == 0 && y == 0) System.out.print("");
        }
        long fastTime = (System.nanoTime() - fastStart) / BENCHMARK_ITERATIONS;
        
        printComparison("2D Normalize", javaTime, fastTime);
        System.out.println();
    }
    
    private static void benchmarkVector3DNormalization() {
        System.out.println("▶ 3D Vector Normalization (Camera/Lighting)");
        System.out.println("───────────────────────────────────────────");
        
        // Java
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            float x = entityX[i % ENTITY_COUNT];
            float y = entityY[i % ENTITY_COUNT];
            float z = entityZ[i % ENTITY_COUNT];
            float len = (float) Math.sqrt(x * x + y * y + z * z);
            if (len > 0.0001f) {
                x /= len;
                y /= len;
                z /= len;
            }
            if (x == 0 && y == 0 && z == 0) System.out.print("");
        }
        long javaTime = (System.nanoTime() - start) / BENCHMARK_ITERATIONS;
        
        // FastMath
        long fastStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            float x = entityX[i % ENTITY_COUNT];
            float y = entityY[i % ENTITY_COUNT];
            float z = entityZ[i % ENTITY_COUNT];
            float len = (float) FastMath.sqrt(x * x + y * y + z * z);
            if (len > 0.0001f) {
                x /= len;
                y /= len;
                z /= len;
            }
            if (x == 0 && y == 0 && z == 0) System.out.print("");
        }
        long fastTime = (System.nanoTime() - fastStart) / BENCHMARK_ITERATIONS;
        
        printComparison("3D Normalize", javaTime, fastTime);
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // DISTANCE CALCULATIONS (Collision detection, AI, culling)
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkDistance2D() {
        System.out.println("▶ 2D Distance (Collision/AI/Target)");
        System.out.println("──────────────────────────────────");
        
        // Java
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            int idx1 = i % ENTITY_COUNT;
            int idx2 = (i + 1) % ENTITY_COUNT;
            float dx = entityX[idx1] - entityX[idx2];
            float dy = entityY[idx1] - entityY[idx2];
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < 0) System.out.print(""); // Prevent elimination
        }
        long javaTime = (System.nanoTime() - start) / BENCHMARK_ITERATIONS;
        
        // FastMath
        long fastStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            int idx1 = i % ENTITY_COUNT;
            int idx2 = (i + 1) % ENTITY_COUNT;
            float dx = entityX[idx1] - entityX[idx2];
            float dy = entityY[idx1] - entityY[idx2];
            float dist = (float) FastMath.sqrt(dx * dx + dy * dy);
            if (dist < 0) System.out.print("");
        }
        long fastTime = (System.nanoTime() - fastStart) / BENCHMARK_ITERATIONS;
        
        printComparison("2D Distance", javaTime, fastTime);
        System.out.println();
    }
    
    private static void benchmarkDistance3D() {
        System.out.println("▶ 3D Distance (Collision/LOD/Culling)");
        System.out.println("──────────────────────────────────────");
        
        // Java
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            int idx1 = i % ENTITY_COUNT;
            int idx2 = (i + 1) % ENTITY_COUNT;
            float dx = entityX[idx1] - entityX[idx2];
            float dy = entityY[idx1] - entityY[idx2];
            float dz = entityZ[idx1] - entityZ[idx2];
            float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 0) System.out.print("");
        }
        long javaTime = (System.nanoTime() - start) / BENCHMARK_ITERATIONS;
        
        // FastMath
        long fastStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            int idx1 = i % ENTITY_COUNT;
            int idx2 = (i + 1) % ENTITY_COUNT;
            float dx = entityX[idx1] - entityX[idx2];
            float dy = entityY[idx1] - entityY[idx2];
            float dz = entityZ[idx1] - entityZ[idx2];
            float dist = (float) FastMath.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 0) System.out.print("");
        }
        long fastTime = (System.nanoTime() - fastStart) / BENCHMARK_ITERATIONS;
        
        printComparison("3D Distance", javaTime, fastTime);
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // PHYSICS (Velocity magnitude, force calculations)
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkVelocityMagnitude() {
        System.out.println("▶ Velocity Magnitude (Speed Checks)");
        System.out.println("─────────────────────────────────────");
        
        // Java
        long start = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            int idx = i % ENTITY_COUNT;
            float vx = velocityX[idx];
            float vy = velocityY[idx];
            float vz = velocityZ[idx];
            float speed = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
            if (speed > 100.0f) {
                // Cap velocity
                float scale = 100.0f / speed;
                vx *= scale;
                vy *= scale;
                vz *= scale;
            }
            if (vx == 0 && vy == 0 && vz == 0) System.out.print("");
        }
        long javaTime = (System.nanoTime() - start) / BENCHMARK_ITERATIONS;
        
        // FastMath
        long fastStart = System.nanoTime();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            int idx = i % ENTITY_COUNT;
            float vx = velocityX[idx];
            float vy = velocityY[idx];
            float vz = velocityZ[idx];
            float speed = (float) FastMath.sqrt(vx * vx + vy * vy + vz * vz);
            if (speed > 100.0f) {
                float scale = 100.0f / speed;
                vx *= scale;
                vy *= scale;
                vz *= scale;
            }
            if (vx == 0 && vy == 0 && vz == 0) System.out.print("");
        }
        long fastTime = (System.nanoTime() - fastStart) / BENCHMARK_ITERATIONS;
        
        printComparison("Velocity Mag", javaTime, fastTime);
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // BULK OPERATIONS (Particle systems, vertex processing)
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void benchmarkParticleSystemSqrt() {
        System.out.println("▶ Particle System (" + PARTICLE_COUNT + " particles)");
        System.out.println("─────────────────────────────────────────────────");
        
        // Generate particle data (use double for native array operations)
        double[] particles = new double[PARTICLE_COUNT];
        double[] output = new double[PARTICLE_COUNT];
        java.util.Random rnd = new java.util.Random(42);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles[i] = rnd.nextDouble() * 1000;
        }
        
        // Java loop
        long start = System.nanoTime();
        for (int iter = 0; iter < 100; iter++) {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                output[i] = Math.sqrt(particles[i]);
            }
        }
        long javaTime = (System.nanoTime() - start) / 100;
        
        // FastMath array operation
        long fastStart = System.nanoTime();
        for (int iter = 0; iter < 100; iter++) {
            FastMath.sqrt(particles, output);
        }
        long fastTime = (System.nanoTime() - fastStart) / 100;
        
        printComparison("Particle sqrt", javaTime, fastTime);
        System.out.println("  (per particle: Java=" + (javaTime / PARTICLE_COUNT) + "ns, Fast=" + (fastTime / PARTICLE_COUNT) + "ns)");
        System.out.println();
    }
    
    private static void benchmarkArraySqrt() {
        System.out.println("▶ Bulk Array Sqrt (10K elements x 1000 iterations)");
        System.out.println("───────────────────────────────────────────────────");
        
        int size = 10000;
        double[] input = new double[size];
        double[] output = new double[size];
        for (int i = 0; i < size; i++) {
            input[i] = i * 0.1 + 0.5;
        }
        
        // Java
        long start = System.nanoTime();
        for (int iter = 0; iter < 1000; iter++) {
            for (int i = 0; i < size; i++) {
                output[i] = Math.sqrt(input[i]);
            }
        }
        long javaTime = (System.nanoTime() - start) / 1000;
        
        // FastMath
        long fastStart = System.nanoTime();
        for (int iter = 0; iter < 1000; iter++) {
            FastMath.sqrt(input, output);
        }
        long fastTime = (System.nanoTime() - fastStart) / 1000;
        
        printComparison("Array sqrt", javaTime, fastTime);
        System.out.println();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITIES
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void printComparison(String name, long javaNs, long fastNs) {
        double javaOps = 1_000_000_000.0 / javaNs;
        double fastOps = 1_000_000_000.0 / fastNs;
        double speedup = (double) javaNs / fastNs;
        
        System.out.printf("  %-18s: %6.2f ns/op  │  %,9.0f ops/sec%n", 
            "Java " + name, (double) javaNs, javaOps);
        System.out.printf("  %-18s: %6.2f ns/op  │  %,9.0f ops/sec", 
            "Fast " + name, (double) fastNs, fastOps);
        
        if (FastMath.isNativeAvailable()) {
            System.out.printf("  │  %.2fx speedup%n", speedup);
        } else {
            System.out.println("  │  (native not loaded)");
        }
    }
    
    private static void printRecommendations() {
        System.out.println("Game Development Recommendations:");
        System.out.println();
        System.out.println("  🎯 PRIORITY USE CASES for FastMath:");
        System.out.println("     • Particle systems (10K+ particles)");
        System.out.println("     • Vertex normal calculation (mesh processing)");
        System.out.println("     • Physics simulation (velocity, force vectors)");
        System.out.println("     • Collision detection (distance checks)");
        System.out.println();
        System.out.println("  ⚡ NEXT OPTIMIZATIONS:");
        System.out.println("     • OpenCL GPU dispatch for arrays > 1000 elements");
        System.out.println("     • SIMD vectorization in native code");
        System.out.println("     • Fast inverse sqrt approximation (1/sqrt(x))");
        System.out.println();
        
        if (!FastMath.isNativeAvailable()) {
            System.out.println("  ⚠️  To enable native acceleration:");
            System.out.println("     java -Djava.library.path=build -jar yourgame.jar");
        }
    }
}
