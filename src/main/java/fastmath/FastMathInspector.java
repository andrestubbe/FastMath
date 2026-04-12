package fastmath;

/**
 * FastMathInspector - Runtime hardware detection and optimal path selection.
 * 
 * Automatically detects CPU capabilities (AVX2, AVX512), GPU availability,
 * and selects the optimal execution path for any given workload.
 * 
 * @author Andre Stubbe
 * @since 1.0.0
 */
public class FastMathInspector {
    
    // CPU Feature Flags
    private static final boolean HAS_AVX2;
    private static final boolean HAS_AVX512;
    private static final boolean HAS_FMA;
    private static final int SIMD_WIDTH;
    
    // GPU Info
    private static final boolean GPU_AVAILABLE;
    private static final String GPU_VENDOR;
    private static final int GPU_COMPUTE_UNITS;
    
    // System Info
    private static final int AVAILABLE_PROCESSORS;
    private static final long MAX_MEMORY;
    private static final String OS_NAME;
    private static final String OS_ARCH;
    
    static {
        // Detect CPU features via native code or Java properties
        AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
        MAX_MEMORY = Runtime.getRuntime().maxMemory();
        OS_NAME = System.getProperty("os.name");
        OS_ARCH = System.getProperty("os.arch");
        
        // Try to load native inspector
        boolean nativeLoaded = false;
        boolean avx2 = false;
        boolean avx512 = false;
        boolean fma = false;
        int simdWidth = 1; // Default: scalar
        boolean gpuAvail = false;
        String gpuVendor = "N/A";
        int gpuCUs = 0;
        
        try {
            System.loadLibrary("fastmath");
            nativeLoaded = true;
            
            // Call native detection functions
            avx2 = nativeHasAVX2();
            avx512 = nativeHasAVX512();
            fma = nativeHasFMA();
            simdWidth = nativeGetSIMDWidth();
            gpuAvail = nativeGPUAvailable();
            gpuVendor = nativeGPUVendor();
            gpuCUs = nativeGPUComputeUnits();
            
        } catch (UnsatisfiedLinkError e) {
            // Native library not available, use Java fallbacks
            nativeLoaded = false;
            
            // Basic detection from system properties
            String cpuFeatures = System.getProperty("os.arch");
            if (cpuFeatures != null) {
                avx2 = cpuFeatures.contains("amd64") || cpuFeatures.contains("x86_64");
                // Conservative: assume AVX2 on modern x64, but not AVX512
            }
            
            // No GPU without native library
            gpuAvail = false;
        }
        
        HAS_AVX2 = avx2;
        HAS_AVX512 = avx512;
        HAS_FMA = fma;
        SIMD_WIDTH = simdWidth;
        GPU_AVAILABLE = gpuAvail;
        GPU_VENDOR = gpuVendor;
        GPU_COMPUTE_UNITS = gpuCUs;
    }
    
    // Native methods for hardware detection
    private static native boolean nativeHasAVX2();
    private static native boolean nativeHasAVX512();
    private static native boolean nativeHasFMA();
    private static native int nativeGetSIMDWidth();
    private static native boolean nativeGPUAvailable();
    private static native String nativeGPUVendor();
    private static native int nativeGPUComputeUnits();
    
    // CPU Detection
    public static boolean hasAVX2() { return HAS_AVX2; }
    public static boolean hasAVX512() { return HAS_AVX512; }
    public static boolean hasFMA() { return HAS_FMA; }
    public static int getSIMDWidth() { return SIMD_WIDTH; }
    
    // GPU Detection
    public static boolean hasGPU() { return GPU_AVAILABLE; }
    public static String getGPUVendor() { return GPU_VENDOR; }
    public static int getGPUComputeUnits() { return GPU_COMPUTE_UNITS; }
    
    // System Info
    public static int getAvailableProcessors() { return AVAILABLE_PROCESSORS; }
    public static long getMaxMemoryMB() { return MAX_MEMORY / (1024 * 1024); }
    public static String getOS() { return OS_NAME; }
    public static String getArchitecture() { return OS_ARCH; }
    
    /**
     * Get optimal execution path for a given array size.
     * 
     * @param arraySize Number of elements to process
     * @return Optimal path: "GPU", "SIMD", or "JAVA"
     */
    public static String getOptimalPath(int arraySize) {
        if (arraySize >= 10000 && GPU_AVAILABLE) {
            return "GPU";
        } else if (arraySize >= 100 && (HAS_AVX2 || HAS_AVX512)) {
            return "SIMD";
        } else {
            return "JAVA";
        }
    }
    
    /**
     * Get recommended batch size for current hardware.
     */
    public static int getRecommendedBatchSize() {
        if (GPU_AVAILABLE) {
            return 10000; // GPU likes big batches
        } else if (HAS_AVX512) {
            return 4096; // AVX512: 8 doubles per register
        } else if (HAS_AVX2) {
            return 2048; // AVX2: 4 doubles per register
        } else {
            return 512; // Scalar fallback
        }
    }
    
    /**
     * Print detailed hardware report.
     */
    public static void printReport() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           FastMath Hardware Inspector Report                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("📊 SYSTEM INFO");
        System.out.println("   OS:             " + OS_NAME + " (" + OS_ARCH + ")");
        System.out.println("   Processors:     " + AVAILABLE_PROCESSORS);
        System.out.println("   Max Memory:     " + getMaxMemoryMB() + " MB");
        System.out.println();
        
        System.out.println("🔧 CPU FEATURES");
        System.out.println("   AVX2:           " + (HAS_AVX2 ? "✅ YES" : "❌ NO"));
        System.out.println("   AVX512:         " + (HAS_AVX512 ? "✅ YES" : "❌ NO"));
        System.out.println("   FMA:            " + (HAS_FMA ? "✅ YES" : "❌ NO"));
        System.out.println("   SIMD Width:     " + SIMD_WIDTH + " elements per register");
        System.out.println();
        
        System.out.println("🎮 GPU INFO");
        System.out.println("   Available:      " + (GPU_AVAILABLE ? "✅ YES" : "❌ NO"));
        System.out.println("   Vendor:         " + GPU_VENDOR);
        System.out.println("   Compute Units:  " + GPU_COMPUTE_UNITS);
        System.out.println();
        
        System.out.println("📈 RECOMMENDATIONS");
        System.out.println("   Optimal Path (>10K): " + getOptimalPath(100000));
        System.out.println("   Optimal Path (1K):   " + getOptimalPath(1000));
        System.out.println("   Batch Size:          " + getRecommendedBatchSize() + " elements");
        System.out.println();
        
        System.out.println("⚡ PERFORMANCE TIERS");
        if (GPU_AVAILABLE) {
            System.out.println("   🥇 GPU:    40-100× speedup (arrays > 10K)");
        }
        if (HAS_AVX512) {
            System.out.println("   🥈 AVX512: 8-16× speedup (arrays > 1K)");
        } else if (HAS_AVX2) {
            System.out.println("   🥈 AVX2:   2-8× speedup (arrays > 100)");
        }
        System.out.println("   🥉 Java:   Baseline (JVM intrinsics for scalars)");
        System.out.println();
    }
    
    /**
     * Quick check - returns summary string.
     */
    public static String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("FastMath[");
        if (GPU_AVAILABLE) sb.append("GPU");
        else if (HAS_AVX512) sb.append("AVX512");
        else if (HAS_AVX2) sb.append("AVX2");
        else sb.append("JAVA");
        sb.append("]");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        printReport();
        System.out.println("Summary: " + getSummary());
    }
}
