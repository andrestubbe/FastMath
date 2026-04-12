#include <cmath>
#include <jni.h>
#include <immintrin.h>  // AVX2 intrinsics
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
    
    // AVX2 vectorized path: 4x throughput
    for (; i < simdEnd; i += SIMD_WIDTH) {
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
