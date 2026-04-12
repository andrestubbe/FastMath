#include <cmath>
#include <jni.h>
#include "fastmath.h"

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

// Array operations (batch processing with SIMD-friendly loops)
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = env->GetDoubleArrayElements(input, nullptr);
    jdouble* out = env->GetDoubleArrayElements(output, nullptr);
    
    for (int i = 0; i < len; i++) {
        out[i] = std::sqrt(in[i]);
    }
    
    env->ReleaseDoubleArrayElements(input, in, JNI_ABORT);
    env->ReleaseDoubleArrayElements(output, out, 0);
}

JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSinArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = env->GetDoubleArrayElements(input, nullptr);
    jdouble* out = env->GetDoubleArrayElements(output, nullptr);
    
    for (int i = 0; i < len; i++) {
        out[i] = std::sin(in[i]);
    }
    
    env->ReleaseDoubleArrayElements(input, in, JNI_ABORT);
    env->ReleaseDoubleArrayElements(output, out, 0);
}

JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = env->GetDoubleArrayElements(input, nullptr);
    jdouble* out = env->GetDoubleArrayElements(output, nullptr);
    
    for (int i = 0; i < len; i++) {
        out[i] = std::exp(in[i]);
    }
    
    env->ReleaseDoubleArrayElements(input, in, JNI_ABORT);
    env->ReleaseDoubleArrayElements(output, out, 0);
}

JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len) {
    jdouble* in = env->GetDoubleArrayElements(input, nullptr);
    jdouble* out = env->GetDoubleArrayElements(output, nullptr);
    
    for (int i = 0; i < len; i++) {
        out[i] = std::log(in[i]);
    }
    
    env->ReleaseDoubleArrayElements(input, in, JNI_ABORT);
    env->ReleaseDoubleArrayElements(output, out, 0);
}
