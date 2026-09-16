package fastmath;

import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("? FastMath High-Performance Native Math Demo");
        System.out.println("==================================================");
        System.out.println("Native SIMD Available: " + FastMath.isNativeAvailable());
        System.out.println("GPU Acceleration Active: " + FastMath.isGpuEnabled());
        System.out.println();

        // 1. High-Speed Trigonometry & Power
        double angle = 1.234567;
        System.out.println("--- [1/3] Trigonometry & Square Root ---");
        System.out.println("Math.sin(" + angle + ")     = " + Math.sin(angle));
        System.out.println("FastMath.sin(" + angle + ") = " + FastMath.sin(angle));
        System.out.println("FastMath.cos(" + angle + ") = " + FastMath.cos(angle));
        System.out.println("FastMath.sqrt(144.0)   = " + FastMath.sqrt(144.0));
        System.out.println();

        // 2. SIMD Vector Operations (3D Dot & Cross Product)
        System.out.println("--- [2/3] SIMD Vector Operations ---");
        double x1 = 1.0, y1 = 2.0, z1 = 3.0;
        double x2 = 4.0, y2 = 5.0, z2 = 6.0;
        double dot = FastMathVectors.dot3(x1, y1, z1, x2, y2, z2);
        double[] cross = new double[3];
        FastMathVectors.cross3(x1, y1, z1, x2, y2, z2, cross);
        double length = FastMathVectors.length3(x1, y1, z1);

        System.out.println("Vector 1: (" + x1 + ", " + y1 + ", " + z1 + ")");
        System.out.println("Vector 2: (" + x2 + ", " + y2 + ", " + z2 + ")");
        System.out.println("Dot3 Product:   " + dot);
        System.out.println("Cross3 Product: " + Arrays.toString(cross));
        System.out.println("Vector 1 Length: " + length);
        System.out.println();

        // 3. FastMath Statistical Functions
        System.out.println("--- [3/3] High-Throughput Statistics ---");
        double[] data = {12.5, 24.1, 18.3, 30.7, 15.2, 22.9, 19.8};
        double mean = FastMathStats.mean(data);
        double variance = FastMathStats.variance(data);
        double stdDev = FastMathStats.stddev(data);

        System.out.println("Dataset: " + Arrays.toString(data));
        System.out.printf("Mean:     %.4f%n", mean);
        System.out.printf("StdDev:   %.4f%n", stdDev);
        System.out.printf("Variance: %.4f%n", variance);
        System.out.println();
        System.out.println("? FastMath Demo completed successfully!");
    }
}