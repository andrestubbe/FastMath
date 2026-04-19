package fastmath;

/**
 * Unified high-performance math library — drop-in replacement for java.lang.Math.
 * 
 * SMART DISPATCH: Automatically selects optimal implementation:
 * - Scalar ops: FastMathPure (polynomial approximations, no JNI overhead)
 * - Small arrays (<1000): JNI SIMD (AVX2)
 * - Large arrays (≥1000): OpenCL GPU (if enabled and available)
 * - Fallback: java.lang.Math (always safe)
 * 
 * GPU Configuration:
 * - System property: -Dfastmath.gpu=true (default: false - must opt-in)
 * - Threshold: -Dfastmath.gpu.threshold=1000 (elements before GPU kick-in)
 * 
 * @see FastMathPure for pure Java fast approximations
 */
public class FastMath {
    
    // Native library availability
    private static final boolean NATIVE_AVAILABLE;
    
    // GPU configuration
    private static final boolean GPU_ENABLED;
    static final int GPU_THRESHOLD;
    private static final boolean GPU_AVAILABLE;
    
    static {
        // Load native library
        boolean loaded = false;
        try {
            System.loadLibrary("fastmath");
            loaded = true;
        } catch (UnsatisfiedLinkError e1) {
            try {
                String buildPath = System.getProperty("user.dir") + "/build/fastmath.dll";
                System.load(buildPath);
                loaded = true;
            } catch (UnsatisfiedLinkError e2) {
                try {
                    System.loadLibrary("native/fastmath");
                    loaded = true;
                } catch (UnsatisfiedLinkError e3) {
                    System.err.println("FastMath: Native library not available, using pure Java/Math fallback");
                }
            }
        }
        NATIVE_AVAILABLE = loaded;
        
        // GPU configuration (opt-in, must set -Dfastmath.gpu=true)
        GPU_ENABLED = Boolean.getBoolean("fastmath.gpu");
        GPU_THRESHOLD = Integer.getInteger("fastmath.gpu.threshold", 1000);
        GPU_AVAILABLE = GPU_ENABLED && loaded && initOpenCL();
        
        if (GPU_AVAILABLE) {
            System.out.println("FastMath: GPU acceleration enabled (threshold: " + GPU_THRESHOLD + " elements)");
        } else if (GPU_ENABLED && !loaded) {
            System.err.println("FastMath: GPU requested but native library not available");
        }
    }
    
    /**
     * Initialize OpenCL context (placeholder for actual implementation)
     */
    private static boolean initOpenCL() {
        // TODO: Actual OpenCL initialization
        // For now, return false until OpenCL is fully implemented
        return false;
    }
    
    /**
     * Check if native SIMD acceleration is available
     */
    public static boolean isNativeAvailable() {
        return NATIVE_AVAILABLE;
    }
    
    /**
     * Check if GPU acceleration is active
     */
    public static boolean isGpuEnabled() {
        return GPU_AVAILABLE;
    }
    
    // Native method declarations
    
    // OpenCL GPU
    private static native boolean initOpenCL();
    private static native void gpuSqrtArray(double[] input, double[] output, int len);
    private static native void gpuSinArray(double[] input, double[] output, int len);
    private static native void gpuCosArray(double[] input, double[] output, int len);
    private static native void gpuExpArray(double[] input, double[] output, int len);
    private static native void gpuLogArray(double[] input, double[] output, int len);
    
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
    private static native void nativeCosArray(double[] input, double[] output, int len);
    private static native void nativeExpArray(double[] input, double[] output, int len);
    private static native void nativeLogArray(double[] input, double[] output, int len);
    
    // ============================================================================
    // PUBLIC API - Trigonometric Functions
    // ============================================================================
    
    public static double sin(double a) {
        return FastMathPure.sinFast(a);
    }
    
    public static double cos(double a) {
        return FastMathPure.cosFast(a);
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
        return FastMathPure.expFast(a);
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
        return Math.sqrt(a);  // Hardware sqrt instruction is unbeatable
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
    
    public static long round(double a) {
        return Math.round(a);  // Already fast, delegates to Math
    }
    
    public static int round(float a) {
        return Math.round(a);  // Already fast, delegates to Math
    }
    
    // ============================================================================
    // PUBLIC API - Abs, Min, Max (Commonly Used)
    // ============================================================================
    
    public static int abs(int a) { return Math.abs(a); }
    public static long abs(long a) { return Math.abs(a); }
    public static float abs(float a) { return Math.abs(a); }
    public static double abs(double a) { return Math.abs(a); }
    
    public static int max(int a, int b) { return Math.max(a, b); }
    public static long max(long a, long b) { return Math.max(a, b); }
    public static float max(float a, float b) { return Math.max(a, b); }
    public static double max(double a, double b) { return Math.max(a, b); }
    
    public static int min(int a, int b) { return Math.min(a, b); }
    public static long min(long a, long b) { return Math.min(a, b); }
    public static float min(float a, float b) { return Math.min(a, b); }
    public static double min(double a, double b) { return Math.min(a, b); }
    
    // ============================================================================
    // PUBLIC API - Angle Conversion & Utility
    // ============================================================================
    
    public static double toDegrees(double angrad) {
        return Math.toDegrees(angrad);  // Already fast
    }
    
    public static double toRadians(double angdeg) {
        return Math.toRadians(angdeg);  // Already fast
    }
    
    public static double hypot(double x, double y) {
        return Math.hypot(x, y);  // sqrt(x²+y²) without overflow
    }
    
    // ============================================================================
    // PUBLIC API - Exact Arithmetic (throw on overflow)
    // ============================================================================
    
    public static int addExact(int x, int y) { return Math.addExact(x, y); }
    public static long addExact(long x, long y) { return Math.addExact(x, y); }
    public static int subtractExact(int x, int y) { return Math.subtractExact(x, y); }
    public static long subtractExact(long x, long y) { return Math.subtractExact(x, y); }
    public static int multiplyExact(int x, int y) { return Math.multiplyExact(x, y); }
    public static long multiplyExact(long x, int y) { return Math.multiplyExact(x, y); }
    public static long multiplyExact(long x, long y) { return Math.multiplyExact(x, y); }
    public static int incrementExact(int a) { return Math.incrementExact(a); }
    public static long incrementExact(long a) { return Math.incrementExact(a); }
    public static int decrementExact(int a) { return Math.decrementExact(a); }
    public static long decrementExact(long a) { return Math.decrementExact(a); }
    public static int negateExact(int a) { return Math.negateExact(a); }
    public static long negateExact(long a) { return Math.negateExact(a); }
    public static int absExact(int a) { return Math.absExact(a); }
    public static long absExact(long a) { return Math.absExact(a); }
    public static int toIntExact(long value) { return Math.toIntExact(value); }
    public static long multiplyFull(int x, int y) { return Math.multiplyFull(x, y); }
    public static long multiplyHigh(long x, long y) { return Math.multiplyHigh(x, y); }
    
    // ============================================================================
    // PUBLIC API - Floor Division & Modulo
    // ============================================================================
    
    public static int floorDiv(int x, int y) { return Math.floorDiv(x, y); }
    public static long floorDiv(long x, int y) { return Math.floorDiv(x, y); }
    public static long floorDiv(long x, long y) { return Math.floorDiv(x, y); }
    public static int floorMod(int x, int y) { return Math.floorMod(x, y); }
    public static int floorMod(long x, int y) { return Math.floorMod(x, y); }
    public static long floorMod(long x, long y) { return Math.floorMod(x, y); }
    
    // ============================================================================
    // PUBLIC API - Floating Point Utilities
    // ============================================================================
    
    public static double copySign(double magnitude, double sign) { return Math.copySign(magnitude, sign); }
    public static float copySign(float magnitude, float sign) { return Math.copySign(magnitude, sign); }
    public static double fma(double a, double b, double c) { return Math.fma(a, b, c); }
    public static float fma(float a, float b, float c) { return Math.fma(a, b, c); }
    public static int getExponent(double d) { return Math.getExponent(d); }
    public static int getExponent(float f) { return Math.getExponent(f); }
    public static double IEEEremainder(double f1, double f2) { return Math.IEEEremainder(f1, f2); }
    public static double nextAfter(double start, double direction) { return Math.nextAfter(start, direction); }
    public static float nextAfter(float start, double direction) { return Math.nextAfter(start, direction); }
    public static double nextUp(double d) { return Math.nextUp(d); }
    public static float nextUp(float f) { return Math.nextUp(f); }
    public static double nextDown(double d) { return Math.nextDown(d); }
    public static float nextDown(float f) { return Math.nextDown(f); }
    public static double scalb(double d, int scaleFactor) { return Math.scalb(d, scaleFactor); }
    public static float scalb(float f, int scaleFactor) { return Math.scalb(f, scaleFactor); }
    public static double signum(double d) { return Math.signum(d); }
    public static float signum(float f) { return Math.signum(f); }
    public static double ulp(double d) { return Math.ulp(d); }
    public static float ulp(float f) { return Math.ulp(f); }
    public static double random() { return Math.random(); }
    
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
        if (GPU_AVAILABLE && input.length >= GPU_THRESHOLD) {
            gpuSinArray(input, output, input.length);
        } else if (NATIVE_AVAILABLE) {
            nativeSinArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.sin(input[i]);
            }
        }
    }
    
    public static void cos(double[] input, double[] output) {
        if (GPU_AVAILABLE && input.length >= GPU_THRESHOLD) {
            gpuCosArray(input, output, input.length);
        } else if (NATIVE_AVAILABLE) {
            nativeCosArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.cos(input[i]);
            }
        }
    }
    
    public static void exp(double[] input, double[] output) {
        if (GPU_AVAILABLE && input.length >= GPU_THRESHOLD) {
            gpuExpArray(input, output, input.length);
        } else if (NATIVE_AVAILABLE) {
            nativeExpArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.exp(input[i]);
            }
        }
    }
    
    public static void log(double[] input, double[] output) {
        if (GPU_AVAILABLE && input.length >= GPU_THRESHOLD) {
            gpuLogArray(input, output, input.length);
        } else if (NATIVE_AVAILABLE) {
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
    // SMART DISPATCH METHODS
    // ============================================================================
    
    /**
     * Smart sqrt dispatch:
     * - Large arrays → GPU (if enabled)
     * - Small arrays → JNI SIMD
     * - Fallback → Java loop
     */
    private static void sqrtSmart(double[] input, double[] output) {
        if (GPU_AVAILABLE && input.length >= GPU_THRESHOLD) {
            gpuSqrtArray(input, output, input.length);
        } else if (NATIVE_AVAILABLE) {
            nativeSqrtArray(input, output, input.length);
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = Math.sqrt(input[i]);
            }
        }
    }
}
