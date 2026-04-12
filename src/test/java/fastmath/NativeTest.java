package fastmath;

/**
 * Quick test to verify native library loading.
 */
public class NativeTest {
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("           FastMath Native Library Test");
        System.out.println("═══════════════════════════════════════════════════");
        
        System.out.println("Native available: " + FastMath.isNativeAvailable());
        
        if (FastMath.isNativeAvailable()) {
            System.out.println("\nTesting native methods:");
            
            double testValue = 2.0;
            System.out.println("sqrt(" + testValue + ") = " + FastMath.sqrt(testValue));
            System.out.println("sin(" + testValue + ") = " + FastMath.sin(testValue));
            System.out.println("exp(" + testValue + ") = " + FastMath.exp(testValue));
            System.out.println("log(" + testValue + ") = " + FastMath.log(testValue));
            
            System.out.println("\nTesting array operations:");
            double[] input = {1.0, 2.0, 3.0, 4.0, 5.0};
            double[] output = new double[5];
            FastMath.sqrt(input, output);
            System.out.print("sqrt array: ");
            for (double v : output) {
                System.out.print(v + " ");
            }
            System.out.println();
            
            System.out.println("\nTesting fast inverse sqrt (Quake algorithm):");
            float test = 4.0f;
            float invSqrt = FastMath.fastInvSqrt(test);
            float expected = 1.0f / (float)Math.sqrt(test);  // Should be 0.5
            System.out.println("fastInvSqrt(" + test + ") = " + invSqrt + " (expected: " + expected + ")");
            System.out.println("Error: " + (Math.abs(invSqrt - expected) / expected * 100) + "%");
            
            System.out.println("\n✅ Native library working!");
        } else {
            System.out.println("\n⚠️  Native library NOT loaded - using Math fallback");
            System.out.println("To enable native:");
            System.out.println("  1. Ensure build\\fastmath.dll exists");
            System.out.println("  2. Add -Djava.library.path=build to JVM args");
        }
        
        System.out.println("═══════════════════════════════════════════════════");
    }
}
