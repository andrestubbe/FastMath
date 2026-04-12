#include <cmath>
#include <jni.h>
#include <immintrin.h>  // AVX2 intrinsics
#include <xmmintrin.h>  // SSE intrinsics for _mm_prefetch
#include "fastmath.h"

// SIMD CONFIGURATION
#define SIMD_WIDTH 4  // AVX2 processes 4 doubles per iteration
#define UNROLL_FACTOR 4  // Loop unrolling for scalar functions

// Trigonometric functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSin(JNIEnv *env, jclass cls, jdouble x) {
    return std::sin(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCos(JNIEnv *env, jclass cls, jdouble x) {
    return std::cos(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeTan(JNIEnv *env, jclass cls, jdouble x) {
    return std::tan(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAsin(JNIEnv *env, jclass cls, jdouble x) {
    return std::asin(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAcos(JNIEnv *env, jclass cls, jdouble x) {
    return std::acos(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAtan(JNIEnv *env, jclass cls, jdouble x) {
    return std::atan(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAtan2(JNIEnv *env, jclass cls, jdouble y, jdouble x) {
    return std::atan2(y, x);
}

// Hyperbolic functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSinh(JNIEnv *env, jclass cls, jdouble x) {
    return std::sinh(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCosh(JNIEnv *env, jclass cls, jdouble x) {
    return std::cosh(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeTanh(JNIEnv *env, jclass cls, jdouble x) {
    return std::tanh(x);
}

// Exponential and logarithmic functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeExp(JNIEnv *env, jclass cls, jdouble x) {
    return std::exp(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeExpm1(JNIEnv *env, jclass cls, jdouble x) {
    return std::expm1(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog(JNIEnv *env, jclass cls, jdouble x) {
    return std::log(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog1p(JNIEnv *env, jclass cls, jdouble x) {
    return std::log1p(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog10(JNIEnv *env, jclass cls, jdouble x) {
    return std::log10(x);
}

// Power and root functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSqrt(JNIEnv *env, jclass cls, jdouble x) {
    return std::sqrt(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCbrt(JNIEnv *env, jclass cls, jdouble x) {
    return std::cbrt(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativePow(JNIEnv *env, jclass cls, jdouble x, jdouble y) {
    return std::pow(x, y);
}

// Rounding functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCeil(JNIEnv *env, jclass cls, jdouble x) {
    return std::ceil(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeFloor(JNIEnv *env, jclass cls, jdouble x) {
    return std::floor(x);
}

JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeRint(JNIEnv *env, jclass cls, jdouble x) {
    return std::rint(x);
}

// Array operations with AVX2 SIMD vectorization
// Uses GetPrimitiveArrayCritical to avoid array copy overhead
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    // Critical section: direct pointer access, no array copy, GC disabled
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (in == nullptr || out == nullptr) {
        // Fallback if critical section failed
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int i = 0;
    int simdEnd = len - (SIMD_WIDTH - 1);
    
    // AVX2 vectorized path: 4x throughput with prefetching
    // Prefetch 8 cache lines ahead (512 bytes = 64 doubles)
    for (; i < simdEnd; i += SIMD_WIDTH) {
        // Prefetch next iteration's data (8 cache lines ahead)
        if (i + 64 < simdEnd) {
            _mm_prefetch((const char*)&in[i + 64], _MM_HINT_T0);
        }
        
        __m256d vec = _mm256_loadu_pd(&in[i]);      // Load 4 doubles
        __m256d result = _mm256_sqrt_pd(vec);       // 4 sqrt operations in 1 instruction
        _mm256_storeu_pd(&out[i], result);          // Store 4 results
    }
    
    // Scalar cleanup for remaining 0-3 elements
    for (; i < len; i++) {
        out[i] = std::sqrt(in[i]);
    }
    
    // Release critical sections (0 = copy back and release)
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// sin() with 4x unrolled loop using critical sections
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSinArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (in == nullptr || out == nullptr) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int i = 0;
    int unrollEnd = len - (UNROLL_FACTOR - 1);
    
    for (; i < unrollEnd; i += UNROLL_FACTOR) {
        // Prefetch 8 cache lines ahead
        if (i + 64 < unrollEnd) {
            _mm_prefetch((const char*)&in[i + 64], _MM_HINT_T0);
        }
        out[i] = std::sin(in[i]);
        out[i+1] = std::sin(in[i+1]);
        out[i+2] = std::sin(in[i+2]);
        out[i+3] = std::sin(in[i+3]);
    }
    
    for (; i < len; i++) {
        out[i] = std::sin(in[i]);
    }
    
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// cos() with 4x unrolled loop using critical sections
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeCosArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (in == nullptr || out == nullptr) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int i = 0;
    int unrollEnd = len - (UNROLL_FACTOR - 1);
    
    for (; i < unrollEnd; i += UNROLL_FACTOR) {
        // Prefetch 8 cache lines ahead
        if (i + 64 < unrollEnd) {
            _mm_prefetch((const char*)&in[i + 64], _MM_HINT_T0);
        }
        out[i] = std::cos(in[i]);
        out[i+1] = std::cos(in[i+1]);
        out[i+2] = std::cos(in[i+2]);
        out[i+3] = std::cos(in[i+3]);
    }
    
    for (; i < len; i++) {
        out[i] = std::cos(in[i]);
    }
    
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// exp() with 4x loop unrolling using critical sections
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (in == nullptr || out == nullptr) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int i = 0;
    int unrollEnd = len - (UNROLL_FACTOR - 1);
    
    for (; i < unrollEnd; i += UNROLL_FACTOR) {
        // Prefetch 8 cache lines ahead
        if (i + 64 < unrollEnd) {
            _mm_prefetch((const char*)&in[i + 64], _MM_HINT_T0);
        }
        out[i] = std::exp(in[i]);
        out[i+1] = std::exp(in[i+1]);
        out[i+2] = std::exp(in[i+2]);
        out[i+3] = std::exp(in[i+3]);
    }
    
    for (; i < len; i++) {
        out[i] = std::exp(in[i]);
    }
    
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// log() with 4x loop unrolling using critical sections
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (in == nullptr || out == nullptr) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int i = 0;
    int unrollEnd = len - (UNROLL_FACTOR - 1);
    
    for (; i < unrollEnd; i += UNROLL_FACTOR) {
        // Prefetch 8 cache lines ahead
        if (i + 64 < unrollEnd) {
            _mm_prefetch((const char*)&in[i + 64], _MM_HINT_T0);
        }
        out[i] = std::log(in[i]);
        out[i+1] = std::log(in[i+1]);
        out[i+2] = std::log(in[i+2]);
        out[i+3] = std::log(in[i+3]);
    }
    
    for (; i < len; i++) {
        out[i] = std::log(in[i]);
    }
    
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// ═══════════════════════════════════════════════════════════════════════════════
// OPENCL GPU ACCELERATION
// Compile kernels at runtime for Intel/AMD/NVIDIA GPUs
// ═══════════════════════════════════════════════════════════════════════════════

#ifdef _WIN32
#include <windows.h>
#endif

// OpenCL function pointer types
typedef int (*clGetPlatformIDs_fn)(int, void**, int*);
typedef int (*clGetDeviceIDs_fn)(void*, int, int, void**, int*);
typedef void* (*clCreateContext_fn)(void*, int, void**, void*, void*, int*);
typedef int (*clReleaseContext_fn)(void*);
typedef void* (*clCreateCommandQueue_fn)(void*, void*, int, int*);
typedef int (*clReleaseCommandQueue_fn)(void*);
typedef void* (*clCreateBuffer_fn)(void*, int, size_t, void*, int*);
typedef int (*clReleaseMemObject_fn)(void*);
typedef int (*clEnqueueWriteBuffer_fn)(void*, void*, int, size_t, size_t, void*, int, void*, void**);
typedef int (*clEnqueueReadBuffer_fn)(void*, void*, int, size_t, size_t, void*, int, void*, void**);
typedef void* (*clCreateProgramWithSource_fn)(void*, int, const char**, const size_t*, int*);
typedef int (*clBuildProgram_fn)(void*, int, void**, const char*, void*, void*);
typedef void* (*clCreateKernel_fn)(void*, const char*, int*);
typedef int (*clReleaseKernel_fn)(void*);
typedef int (*clSetKernelArg_fn)(void*, int, size_t, void*);
typedef int (*clEnqueueNDRangeKernel_fn)(void*, void*, int, const size_t*, const size_t*, const size_t*, int, void*, void**);
typedef int (*clFinish_fn)(void*);
typedef int (*clGetProgramBuildInfo_fn)(void*, void*, int, size_t, void*, size_t*);

// OpenCL constants
#define CL_PLATFORM_NOT_FOUND_KHR -1001
#define CL_DEVICE_TYPE_GPU 0x4
#define CL_DEVICE_TYPE_DEFAULT 0x1
#define CL_MEM_READ_ONLY 0x10
#define CL_MEM_WRITE_ONLY 0x20
#define CL_MEM_READ_WRITE 0x1
#define CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE 0x1
#define CL_PROGRAM_BUILD_LOG 0x1183
#define CL_TRUE 1

// Global OpenCL state (singleton)
static bool g_openclInitialized = false;
static void* g_clContext = nullptr;
static void* g_clQueue = nullptr;
static void* g_clProgram = nullptr;
static HMODULE g_openclDLL = nullptr;

// Function pointers
static clGetPlatformIDs_fn clGetPlatformIDs = nullptr;
static clGetDeviceIDs_fn clGetDeviceIDs = nullptr;
static clCreateContext_fn clCreateContext = nullptr;
static clReleaseContext_fn clReleaseContext = nullptr;
static int (*clReleaseProgram_fn)(void*) = nullptr;
static clCreateCommandQueue_fn clCreateCommandQueue = nullptr;
static clReleaseCommandQueue_fn clReleaseCommandQueue = nullptr;
static clCreateBuffer_fn clCreateBuffer = nullptr;
static clReleaseMemObject_fn clReleaseMemObject = nullptr;
static clEnqueueWriteBuffer_fn clEnqueueWriteBuffer = nullptr;
static clEnqueueReadBuffer_fn clEnqueueReadBuffer = nullptr;
static clCreateProgramWithSource_fn clCreateProgramWithSource = nullptr;
static clBuildProgram_fn clBuildProgram = nullptr;
static clCreateKernel_fn clCreateKernel = nullptr;
static clReleaseKernel_fn clReleaseKernel = nullptr;
static clSetKernelArg_fn clSetKernelArg = nullptr;
static clEnqueueNDRangeKernel_fn clEnqueueNDRangeKernel = nullptr;
static clFinish_fn clFinish = nullptr;
static clGetProgramBuildInfo_fn clGetProgramBuildInfo = nullptr;

// Kernel handles
static void* g_kernelSqrt = nullptr;
static void* g_kernelSin = nullptr;
static void* g_kernelCos = nullptr;
static void* g_kernelExp = nullptr;
static void* g_kernelLog = nullptr;

// Load OpenCL DLL and function pointers
static bool loadOpenCL() {
#ifdef _WIN32
    g_openclDLL = LoadLibraryA("OpenCL.dll");
    if (!g_openclDLL) {
        return false;
    }
    
    clGetPlatformIDs = (clGetPlatformIDs_fn)GetProcAddress(g_openclDLL, "clGetPlatformIDs");
    clGetDeviceIDs = (clGetDeviceIDs_fn)GetProcAddress(g_openclDLL, "clGetDeviceIDs");
    clCreateContext = (clCreateContext_fn)GetProcAddress(g_openclDLL, "clCreateContext");
    clReleaseContext = (clReleaseContext_fn)GetProcAddress(g_openclDLL, "clReleaseContext");
    clCreateCommandQueue = (clCreateCommandQueue_fn)GetProcAddress(g_openclDLL, "clCreateCommandQueue");
    clReleaseCommandQueue = (clReleaseCommandQueue_fn)GetProcAddress(g_openclDLL, "clReleaseCommandQueue");
    clCreateBuffer = (clCreateBuffer_fn)GetProcAddress(g_openclDLL, "clCreateBuffer");
    clReleaseMemObject = (clReleaseMemObject_fn)GetProcAddress(g_openclDLL, "clReleaseMemObject");
    clEnqueueWriteBuffer = (clEnqueueWriteBuffer_fn)GetProcAddress(g_openclDLL, "clEnqueueWriteBuffer");
    clEnqueueReadBuffer = (clEnqueueReadBuffer_fn)GetProcAddress(g_openclDLL, "clEnqueueReadBuffer");
    clCreateProgramWithSource = (clCreateProgramWithSource_fn)GetProcAddress(g_openclDLL, "clCreateProgramWithSource");
    clBuildProgram = (clBuildProgram_fn)GetProcAddress(g_openclDLL, "clBuildProgram");
    clCreateKernel = (clCreateKernel_fn)GetProcAddress(g_openclDLL, "clCreateKernel");
    clReleaseKernel = (clReleaseKernel_fn)GetProcAddress(g_openclDLL, "clReleaseKernel");
    clSetKernelArg = (clSetKernelArg_fn)GetProcAddress(g_openclDLL, "clSetKernelArg");
    clEnqueueNDRangeKernel = (clEnqueueNDRangeKernel_fn)GetProcAddress(g_openclDLL, "clEnqueueNDRangeKernel");
    clFinish = (clFinish_fn)GetProcAddress(g_openclDLL, "clFinish");
    clGetProgramBuildInfo = (clGetProgramBuildInfo_fn)GetProcAddress(g_openclDLL, "clGetProgramBuildInfo");
    clReleaseProgram_fn = (int (*)(void*))GetProcAddress(g_openclDLL, "clReleaseProgram");
    
    return clGetPlatformIDs && clGetDeviceIDs && clCreateContext && clCreateCommandQueue;
#else
    return false; // Linux/Mac not yet supported
#endif
}

// Initialize OpenCL context and compile kernels
JNIEXPORT jboolean JNICALL Java_fastmath_FastMath_initOpenCL(JNIEnv *env, jclass cls) {
    if (g_openclInitialized) {
        return JNI_TRUE;
    }
    
    if (!loadOpenCL()) {
        return JNI_FALSE;
    }
    
    // Get platform
    void* platform = nullptr;
    int numPlatforms = 0;
    int err = clGetPlatformIDs(1, &platform, &numPlatforms);
    if (err != 0 || numPlatforms == 0) {
        return JNI_FALSE;
    }
    
    // Get GPU device
    void* device = nullptr;
    int numDevices = 0;
    err = clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, &numDevices);
    if (err != 0 || numDevices == 0) {
        // Try default device
        err = clGetDeviceIDs(platform, CL_DEVICE_TYPE_DEFAULT, 1, &device, &numDevices);
        if (err != 0 || numDevices == 0) {
            return JNI_FALSE;
        }
    }
    
    // Create context
    g_clContext = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &err);
    if (err != 0 || !g_clContext) {
        return JNI_FALSE;
    }
    
    // Create command queue
    g_clQueue = clCreateCommandQueue(g_clContext, device, 0, &err);
    if (err != 0 || !g_clQueue) {
        clReleaseContext(g_clContext);
        return JNI_FALSE;
    }
    
    // Kernel source (embedded for now - could be loaded from file)
    const char* kernelSource = 
        "__kernel void sqrtArray(__global const double* input, __global double* output, const int len) {\n"
        "    int id = get_global_id(0);\n"
        "    if (id < len) output[id] = sqrt(input[id]);\n"
        "}\n"
        "__kernel void sinArray(__global const double* input, __global double* output, const int len) {\n"
        "    int id = get_global_id(0);\n"
        "    if (id < len) output[id] = sin(input[id]);\n"
        "}\n"
        "__kernel void cosArray(__global const double* input, __global double* output, const int len) {\n"
        "    int id = get_global_id(0);\n"
        "    if (id < len) output[id] = cos(input[id]);\n"
        "}\n"
        "__kernel void expArray(__global const double* input, __global double* output, const int len) {\n"
        "    int id = get_global_id(0);\n"
        "    if (id < len) output[id] = exp(input[id]);\n"
        "}\n"
        "__kernel void logArray(__global const double* input, __global double* output, const int len) {\n"
        "    int id = get_global_id(0);\n"
        "    if (id < len) output[id] = log(input[id]);\n"
        "}\n";
    
    // Create program
    size_t sourceLen = strlen(kernelSource);
    g_clProgram = clCreateProgramWithSource(g_clContext, 1, &kernelSource, &sourceLen, &err);
    if (err != 0 || !g_clProgram) {
        clReleaseCommandQueue(g_clQueue);
        clReleaseContext(g_clContext);
        return JNI_FALSE;
    }
    
    // Build program with fast-math optimizations
    // -cl-fast-relaxed-math: allows faster but less precise math
    // -cl-mad-enable: allow mad (multiply-add) optimizations
    const char* buildOptions = "-cl-fast-relaxed-math -cl-mad-enable";
    err = clBuildProgram(g_clProgram, 1, &device, buildOptions, nullptr, nullptr);
    if (err != 0) {
        clReleaseProgram_fn(g_clProgram);
        clReleaseCommandQueue(g_clQueue);
        clReleaseContext(g_clContext);
        return JNI_FALSE;
    }
    
    // Create kernels
    g_kernelSqrt = clCreateKernel(g_clProgram, "sqrtArray", &err);
    g_kernelSin = clCreateKernel(g_clProgram, "sinArray", &err);
    g_kernelCos = clCreateKernel(g_clProgram, "cosArray", &err);
    g_kernelExp = clCreateKernel(g_clProgram, "expArray", &err);
    g_kernelLog = clCreateKernel(g_clProgram, "logArray", &err);
    
    g_openclInitialized = true;
    return JNI_TRUE;
}

// GPU-accelerated sqrt array
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    if (!g_openclInitialized || !g_kernelSqrt) {
        // Fallback to CPU
        Java_fastmath_FastMath_nativeSqrtArray(env, cls, input, output, len);
        return;
    }
    
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (!in || !out) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    // Create OpenCL buffers
    int err;
    void* inBuffer = clCreateBuffer(g_clContext, CL_MEM_READ_ONLY, len * sizeof(double), nullptr, &err);
    void* outBuffer = clCreateBuffer(g_clContext, CL_MEM_WRITE_ONLY, len * sizeof(double), nullptr, &err);
    
    // Copy data to GPU
    clEnqueueWriteBuffer(g_clQueue, inBuffer, CL_TRUE, 0, len * sizeof(double), in, 0, nullptr, nullptr);
    
    // Set kernel arguments
    clSetKernelArg(g_kernelSqrt, 0, sizeof(void*), &inBuffer);
    clSetKernelArg(g_kernelSqrt, 1, sizeof(void*), &outBuffer);
    clSetKernelArg(g_kernelSqrt, 2, sizeof(int), &len);
    
    // Execute kernel with optimal work group size
    // 256 threads per work group is optimal for most GPUs (good occupancy)
    size_t globalSize = ((len + 255) / 256) * 256;  // Round up to multiple of 256
    size_t localSize = 256;
    clEnqueueNDRangeKernel(g_clQueue, g_kernelSqrt, 1, nullptr, &globalSize, &localSize, 0, nullptr, nullptr);
    clFinish(g_clQueue);
    
    // Read results
    clEnqueueReadBuffer(g_clQueue, outBuffer, CL_TRUE, 0, len * sizeof(double), out, 0, nullptr, nullptr);
    
    // Cleanup
    clReleaseMemObject(inBuffer);
    clReleaseMemObject(outBuffer);
    
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// GPU-accelerated sin array
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuSinArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    if (!g_openclInitialized || !g_kernelSin) {
        Java_fastmath_FastMath_nativeSinArray(env, cls, input, output, len);
        return;
    }
    
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (!in || !out) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int err;
    void* inBuffer = clCreateBuffer(g_clContext, CL_MEM_READ_ONLY, len * sizeof(double), nullptr, &err);
    void* outBuffer = clCreateBuffer(g_clContext, CL_MEM_WRITE_ONLY, len * sizeof(double), nullptr, &err);
    
    clEnqueueWriteBuffer(g_clQueue, inBuffer, CL_TRUE, 0, len * sizeof(double), in, 0, nullptr, nullptr);
    clSetKernelArg(g_kernelSin, 0, sizeof(void*), &inBuffer);
    clSetKernelArg(g_kernelSin, 1, sizeof(void*), &outBuffer);
    clSetKernelArg(g_kernelSin, 2, sizeof(int), &len);
    
    size_t globalSize = ((len + 255) / 256) * 256;
    size_t localSize = 256;
    clEnqueueNDRangeKernel(g_clQueue, g_kernelSin, 1, nullptr, &globalSize, &localSize, 0, nullptr, nullptr);
    clFinish(g_clQueue);
    
    clEnqueueReadBuffer(g_clQueue, outBuffer, CL_TRUE, 0, len * sizeof(double), out, 0, nullptr, nullptr);
    
    clReleaseMemObject(inBuffer);
    clReleaseMemObject(outBuffer);
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// GPU-accelerated cos array
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuCosArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    if (!g_openclInitialized || !g_kernelCos) {
        Java_fastmath_FastMath_nativeCosArray(env, cls, input, output, len);
        return;
    }
    
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (!in || !out) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int err;
    void* inBuffer = clCreateBuffer(g_clContext, CL_MEM_READ_ONLY, len * sizeof(double), nullptr, &err);
    void* outBuffer = clCreateBuffer(g_clContext, CL_MEM_WRITE_ONLY, len * sizeof(double), nullptr, &err);
    
    clEnqueueWriteBuffer(g_clQueue, inBuffer, CL_TRUE, 0, len * sizeof(double), in, 0, nullptr, nullptr);
    clSetKernelArg(g_kernelCos, 0, sizeof(void*), &inBuffer);
    clSetKernelArg(g_kernelCos, 1, sizeof(void*), &outBuffer);
    clSetKernelArg(g_kernelCos, 2, sizeof(int), &len);
    
    size_t globalSize = ((len + 255) / 256) * 256;
    size_t localSize = 256;
    clEnqueueNDRangeKernel(g_clQueue, g_kernelCos, 1, nullptr, &globalSize, &localSize, 0, nullptr, nullptr);
    clFinish(g_clQueue);
    
    clEnqueueReadBuffer(g_clQueue, outBuffer, CL_TRUE, 0, len * sizeof(double), out, 0, nullptr, nullptr);
    
    clReleaseMemObject(inBuffer);
    clReleaseMemObject(outBuffer);
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// GPU-accelerated exp array
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    if (!g_openclInitialized || !g_kernelExp) {
        Java_fastmath_FastMath_nativeExpArray(env, cls, input, output, len);
        return;
    }
    
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (!in || !out) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int err;
    void* inBuffer = clCreateBuffer(g_clContext, CL_MEM_READ_ONLY, len * sizeof(double), nullptr, &err);
    void* outBuffer = clCreateBuffer(g_clContext, CL_MEM_WRITE_ONLY, len * sizeof(double), nullptr, &err);
    
    clEnqueueWriteBuffer(g_clQueue, inBuffer, CL_TRUE, 0, len * sizeof(double), in, 0, nullptr, nullptr);
    clSetKernelArg(g_kernelExp, 0, sizeof(void*), &inBuffer);
    clSetKernelArg(g_kernelExp, 1, sizeof(void*), &outBuffer);
    clSetKernelArg(g_kernelExp, 2, sizeof(int), &len);
    
    size_t globalSize = ((len + 255) / 256) * 256;
    size_t localSize = 256;
    clEnqueueNDRangeKernel(g_clQueue, g_kernelExp, 1, nullptr, &globalSize, &localSize, 0, nullptr, nullptr);
    clFinish(g_clQueue);
    
    clEnqueueReadBuffer(g_clQueue, outBuffer, CL_TRUE, 0, len * sizeof(double), out, 0, nullptr, nullptr);
    
    clReleaseMemObject(inBuffer);
    clReleaseMemObject(outBuffer);
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// GPU-accelerated log array
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    if (!g_openclInitialized || !g_kernelLog) {
        Java_fastmath_FastMath_nativeLogArray(env, cls, input, output, len);
        return;
    }
    
    jdouble* in = (jdouble*) env->GetPrimitiveArrayCritical(input, nullptr);
    jdouble* out = (jdouble*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (!in || !out) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int err;
    void* inBuffer = clCreateBuffer(g_clContext, CL_MEM_READ_ONLY, len * sizeof(double), nullptr, &err);
    void* outBuffer = clCreateBuffer(g_clContext, CL_MEM_WRITE_ONLY, len * sizeof(double), nullptr, &err);
    
    clEnqueueWriteBuffer(g_clQueue, inBuffer, CL_TRUE, 0, len * sizeof(double), in, 0, nullptr, nullptr);
    clSetKernelArg(g_kernelLog, 0, sizeof(void*), &inBuffer);
    clSetKernelArg(g_kernelLog, 1, sizeof(void*), &outBuffer);
    clSetKernelArg(g_kernelLog, 2, sizeof(int), &len);
    
    size_t globalSize = ((len + 255) / 256) * 256;
    size_t localSize = 256;
    clEnqueueNDRangeKernel(g_clQueue, g_kernelLog, 1, nullptr, &globalSize, &localSize, 0, nullptr, nullptr);
    clFinish(g_clQueue);
    
    clEnqueueReadBuffer(g_clQueue, outBuffer, CL_TRUE, 0, len * sizeof(double), out, 0, nullptr, nullptr);
    
    clReleaseMemObject(inBuffer);
    clReleaseMemObject(outBuffer);
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}

// ═══════════════════════════════════════════════════════════════════════════════
// FAST INVERSE SQUARE ROOT (Quake III Arena algorithm)
// Legendary bit-hack trick: ~10x faster than 1.0f/sqrtf(x)
// ═══════════════════════════════════════════════════════════════════════════════

// The famous Q_rsqrt from Quake III Arena - adapted for JNI
// Uses Newton-Raphson iteration after initial approximation via bit manipulation
JNIEXPORT jfloat JNICALL Java_fastmath_FastMath_nativeFastInvSqrt(JNIEnv *env, jclass cls, jfloat x) {
    // Initial guess via bit-hacking magic
    long i;
    float x2 = x * 0.5f;
    float y = x;
    
    // Evil floating point bit level hacking
    i = *(long*)&y;
    i = 0x5f3759df - (i >> 1);  // The magic number!
    y = *(float*)&i;
    
    // Two iterations of Newton's method for precision (~1% error)
    y = y * (1.5f - (x2 * y * y));
    y = y * (1.5f - (x2 * y * y));
    
    return y;
}

// Array version with 8x unrolling
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeFastInvSqrtArray(JNIEnv *env, jclass cls, jfloatArray input, jfloatArray output, jint len) {
    jfloat* in = (jfloat*) env->GetPrimitiveArrayCritical(input, nullptr);
    jfloat* out = (jfloat*) env->GetPrimitiveArrayCritical(output, nullptr);
    
    if (in == nullptr || out == nullptr) {
        if (in) env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
        if (out) env->ReleasePrimitiveArrayCritical(output, out, 0);
        return;
    }
    
    int i = 0;
    int unrollEnd = len - 7;
    
    for (; i < unrollEnd; i += 8) {
        for (int j = 0; j < 8; j++) {
            float x = in[i+j];
            long bits = *(long*)&x;
            bits = 0x5f3759df - (bits >> 1);
            float y = *(float*)&bits;
            float xhalf = x * 0.5f;
            y = y * (1.5f - (xhalf * y * y));
            y = y * (1.5f - (xhalf * y * y));
            out[i+j] = y;
        }
    }
    
    for (; i < len; i++) {
        float x = in[i];
        long bits = *(long*)&x;
        bits = 0x5f3759df - (bits >> 1);
        float y = *(float*)&bits;
        float xhalf = x * 0.5f;
        y = y * (1.5f - (xhalf * y * y));
        y = y * (1.5f - (xhalf * y * y));
        out[i] = y;
    }
    
    env->ReleasePrimitiveArrayCritical(input, in, JNI_ABORT);
    env->ReleasePrimitiveArrayCritical(output, out, 0);
}
