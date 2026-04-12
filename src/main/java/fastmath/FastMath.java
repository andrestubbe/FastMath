package fastmath;

/**
 * High-performance drop-in replacement for java.lang.Math.
 * 
 * Uses JNI with SIMD intrinsics for scalar operations.
 * Uses OpenCL GPU acceleration for batch array operations.
 */
public class FastMath {
    
    private static final boolean NATIVE_AVAILABLE;
    
    static {
        boolean loaded = false;
        try {
            // Try System.loadLibrary first (for when DLL is in java.library.path)
            System.loadLibrary("fastmath");
            loaded = true;
        } catch (UnsatisfiedLinkError e1) {
            try {
                // Fallback: try to load from build directory (development mode)
                String buildPath = System.getProperty("user.dir") + "/build/fastmath.dll";
                System.load(buildPath);
                loaded = true;
            } catch (UnsatisfiedLinkError e2) {
                try {
                    // Second fallback: resources directory (packaged mode)
                    System.loadLibrary("native/fastmath");
                    loaded = true;
                } catch (UnsatisfiedLinkError e3) {
                    System.err.println("FastMath: Native library not available, falling back to Math");
                }
            }
        }
        NATIVE_AVAILABLE = loaded;
    }
    
    // Native method declarations
    
    // Trigonometric
    private static native double nativeSin(double x);
    private static native double nativeCos(double x);
    private static native double nativeTan(double x);
    private static native double nativeAsin(double x);
    private static native double nativeAcos(double x);
    private static native double nativeAtan(double x);
    private static native double nativeAtan2(double y, double x);
    
    // Hyperbolic
    private static native double nativeSinh(double x);
    private static native double nativeCosh(double x);
    private static native double nativeTanh(double x);
    
    // Exponential & Logarithmic
    private static native double nativeExp(double x);
    private static native double nativeExpm1(double x);
    private static native double nativeLog(double x);
    private static native double nativeLog1p(double x);
    private static native double nativeLog10(double x);
    
    // Power & Root
    private static native double nativeSqrt(double x);
    private static native double nativeCbrt(double x);
    private static native double nativePow(double x, double y);
    
    // Rounding
    private static native double nativeCeil(double x);
    private static native double nativeFloor(double x);
    private static native double nativeRint(double x);
    
    // Array operations
    private static native void nativeSqrtArray(double[] input, double[] output, int len);
    private static native void nativeSinArray(double[] input, double[] output, int len);
    private static native void nativeExpArray(double[] input, double[] output, int len);
    private static native void nativeLogArray(double[] input, double[] output, int len);
    
    // ============================================================================
    // PUBLIC API - Trigonometric Functions
    // ============================================================================
    
    public static double sin(double a) {
        return NATIVE_AVAILABLE ? nativeSin(a) : Math.sin(a);
    }
    
    public static double cos(double a) {
        return NATIVE_AVAILABLE ? nativeCos(a) : Math.cos(a);
    }
    
    public static double tan(double a) {
        return NATIVE_AVAILABLE ? nativeTan(a) : Math.tan(a);
    }
    
    public static double asin(double a) {
        return NATIVE_AVAILABLE ? nativeAsin(a) : Math.asin(a);
    }
    
    public static double acos(double a) {
        return NATIVE_AVAILABLE ? nativeAcos(a) : Math.acos(a);
    }
    
    public static double atan(double a) {
        return NATIVE_AVAILABLE ? nativeAtan(a) : Math.atan(a);
    }
    
    public static double atan2(double y, double x) {
        return NATIVE_AVAILABLE ? nativeAtan2(y, x) : Math.atan2(y, x);
    }
    
    // ============================================================================
    // PUBLIC API - Hyperbolic Functions
    // ============================================================================
    
    public static double sinh(double a) {
        return NATIVE_AVAILABLE ? nativeSinh(a) : Math.sinh(a);
    }
    
    public static double cosh(double a) {
        return NATIVE_AVAILABLE ? nativeCosh(a) : Math.cosh(a);
    }
    
    public static double tanh(double a) {
        return NATIVE_AVAILABLE ? nativeTanh(a) : Math.tanh(a);
    }
    
    // ============================================================================
    // PUBLIC API - Exponential & Logarithmic
    // ============================================================================
    
    public static double exp(double a) {
        return NATIVE_AVAILABLE ? nativeExp(a) : Math.exp(a);
    }
    
    public static double expm1(double a) {
        return NATIVE_AVAILABLE ? nativeExpm1(a) : Math.expm1(a);
    }
    
    public static double log(double a) {
        return NATIVE_AVAILABLE ? nativeLog(a) : Math.log(a);
    }
    
    public static double log1p(double a) {
        return NATIVE_AVAILABLE ? nativeLog1p(a) : Math.log1p(a);
    }
    
    public static double log10(double a) {
        return NATIVE_AVAILABLE ? nativeLog10(a) : Math.log10(a);
    }
    
    // ============================================================================
    // PUBLIC API - Power & Root
    // ============================================================================
    
    public static double sqrt(double a) {
        return NATIVE_AVAILABLE ? nativeSqrt(a) : Math.sqrt(a);
    }
    
    public static double cbrt(double a) {
        return NATIVE_AVAILABLE ? nativeCbrt(a) : Math.cbrt(a);
    }
    
    public static double pow(double a, double b) {
        return NATIVE_AVAILABLE ? nativePow(a, b) : Math.pow(a, b);
    }
    
    // ============================================================================
    // PUBLIC API - Rounding
    // ============================================================================
    
    public static double ceil(double a) {
        return NATIVE_AVAILABLE ? nativeCeil(a) : Math.ceil(a);
    }
    
    public static double floor(double a) {
        return NATIVE_AVAILABLE ? nativeFloor(a) : Math.floor(a);
    }
    
    public static double rint(double a) {
        return NATIVE_AVAILABLE ? nativeRint(a) : Math.rint(a);
    }
    
    // ============================================================================
    // PUBLIC API - Array Operations (Batch Processing)
    // ============================================================================
    
    public static void sqrt(double[] input, double[] output) {
        if (NATIVE_AVAILABLE) {
            nativeSqrtArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.sqrt(input[i]);
            }
        }
    }
    
    public static void sin(double[] input, double[] output) {
        if (NATIVE_AVAILABLE) {
            nativeSinArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.sin(input[i]);
            }
        }
    }
    
    public static void exp(double[] input, double[] output) {
        if (NATIVE_AVAILABLE) {
            nativeExpArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.exp(input[i]);
            }
        }
    }
    
    public static void log(double[] input, double[] output) {
        if (NATIVE_AVAILABLE) {
            nativeLogArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.log(input[i]);
            }
        }
    }
    
    // ============================================================================
    // FAST INVERSE SQUARE ROOT (Quake III Arena Algorithm)
    // ============================================================================
    // Legendary ~10x faster 1/sqrt(x) for games and graphics
    // Uses bit-hacking magic: 0x5f3759df - (bits >> 1)
    
    private static native float nativeFastInvSqrt(float x);
    private static native void nativeFastInvSqrtArray(float[] input, float[] output, int len);
    
    /**
     * Fast inverse square root - ~10x faster than 1.0f/(float)Math.sqrt(x)
     * Quake III Arena algorithm with 2 Newton-Raphson iterations
     * Error: ~1% (good enough for games, normalize vectors, etc.)
     * 
     * @param x Input value (must be > 0)
     * @return Approximate 1/sqrt(x)
     */
    public static float fastInvSqrt(float x) {
        if (x <= 0) return Float.POSITIVE_INFINITY;
        return NATIVE_AVAILABLE ? nativeFastInvSqrt(x) : 1.0f / (float)Math.sqrt(x);
    }
    
    /**
     * Fast inverse sqrt for entire array - essential for vector normalization
     * Batch processes 100K+ vectors at ~10x speed
     * 
     * @param input Array of values (must be > 0)
     * @param output Array to store 1/sqrt(input[i])
     */
    public static void fastInvSqrt(float[] input, float[] output) {
        if (input.length != output.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        if (NATIVE_AVAILABLE) {
            nativeFastInvSqrtArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = 1.0f / (float)Math.sqrt(input[i]);
            }
        }
    }
    
    // ============================================================================
    // UTILITY
    // ============================================================================
    
    public static boolean isNativeAvailable() {
        return NATIVE_AVAILABLE;
    }
}
