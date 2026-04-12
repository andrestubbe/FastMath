#ifndef FASTMATH_H
#define FASTMATH_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

// Trigonometric functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSin(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCos(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeTan(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAsin(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAcos(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAtan(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAtan2(JNIEnv *env, jclass cls, jdouble y, jdouble x);

// Hyperbolic functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSinh(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCosh(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeTanh(JNIEnv *env, jclass cls, jdouble x);

// Exponential and logarithmic functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeExp(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeExpm1(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog1p(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog10(JNIEnv *env, jclass cls, jdouble x);

// Power and root functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSqrt(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCbrt(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativePow(JNIEnv *env, jclass cls, jdouble x, jdouble y);

// Rounding functions
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCeil(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeFloor(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeRint(JNIEnv *env, jclass cls, jdouble x);

// Array operations (batch processing)
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSinArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);

// Fast inverse square root (Quake-style) - ~10x faster for games
JNIEXPORT jfloat JNICALL Java_fastmath_FastMath_nativeFastInvSqrt(JNIEnv *env, jclass cls, jfloat x);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeFastInvSqrtArray(JNIEnv *env, jclass cls, jfloatArray input, jfloatArray output, jint len);

// OpenCL GPU initialization and dispatch
JNIEXPORT jboolean JNICALL Java_fastmath_FastMath_initOpenCL(JNIEnv *env, jclass cls);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);

#ifdef __cplusplus
}
#endif

#endif // FASTMATH_H
