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
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeCosArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);

// Fast inverse square root (Quake-style) - ~10x faster for games
JNIEXPORT jfloat JNICALL Java_fastmath_FastMath_nativeFastInvSqrt(JNIEnv *env, jclass cls, jfloat x);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeFastInvSqrtArray(JNIEnv *env, jclass cls, jfloatArray input, jfloatArray output, jint len);

// OpenCL GPU initialization and dispatch
JNIEXPORT jboolean JNICALL Java_fastmath_FastMath_initOpenCL(JNIEnv *env, jclass cls);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuSinArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuCosArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);

// FastMathVectors - Vector and Matrix operations (SIMD optimized)
JNIEXPORT jdouble JNICALL Java_fastmath_FastMathVectors_nativeDot3(JNIEnv *env, jclass cls,
    jdouble x1, jdouble y1, jdouble z1, jdouble x2, jdouble y2, jdouble z2);
JNIEXPORT void JNICALL Java_fastmath_FastMathVectors_nativeCross3(JNIEnv *env, jclass cls,
    jdouble x1, jdouble y1, jdouble z1, jdouble x2, jdouble y2, jdouble z2,
    jdoubleArray out);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMathVectors_nativeLength3(JNIEnv *env, jclass cls,
    jdouble x, jdouble y, jdouble z);

JNIEXPORT void JNICALL Java_fastmath_FastMathVectors_nativeMul4x4(JNIEnv *env, jclass cls,
    jdoubleArray a, jdoubleArray b, jdoubleArray c);
JNIEXPORT void JNICALL Java_fastmath_FastMathVectors_nativeMul4x4Vector(JNIEnv *env, jclass cls,
    jdoubleArray m, jdoubleArray v, jdoubleArray out);
JNIEXPORT void JNICALL Java_fastmath_FastMathVectors_nativeMul4x4VectorBatch(JNIEnv *env, jclass cls,
    jdoubleArray m, jdoubleArray vectors, jdoubleArray out, jint count);

JNIEXPORT void JNICALL Java_fastmath_FastMathVectors_nativeDot3Batch(JNIEnv *env, jclass cls,
    jdoubleArray a, jdoubleArray b, jdoubleArray out, jint count);
JNIEXPORT void JNICALL Java_fastmath_FastMathVectors_nativeLength3Batch(JNIEnv *env, jclass cls,
    jdoubleArray vectors, jdoubleArray out, jint count);

// FastMathNoise - Procedural noise generation
JNIEXPORT void JNICALL Java_fastmath_FastMathNoise_nativePerlinGrid(JNIEnv *env, jclass cls,
    jdoubleArray output, jint width, jint height, jdouble scale, jdouble offsetX, jdouble offsetY);

// FastMathRandom - High-performance RNG
JNIEXPORT void JNICALL Java_fastmath_FastMathRandom_nativeNextDoubleBatch(JNIEnv *env, jclass cls,
    jdoubleArray output, jlong seed);
JNIEXPORT void JNICALL Java_fastmath_FastMathRandom_nativeNextFloatBatch(JNIEnv *env, jclass cls,
    jfloatArray output, jlong seed);
JNIEXPORT void JNICALL Java_fastmath_FastMathRandom_nativeNextLongBatch(JNIEnv *env, jclass cls,
    jlongArray output, jlong seed);
JNIEXPORT void JNICALL Java_fastmath_FastMathRandom_gpuNextDoubleBatch(JNIEnv *env, jclass cls,
    jdoubleArray output, jlong seed, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMathRandom_gpuNextFloatBatch(JNIEnv *env, jclass cls,
    jfloatArray output, jlong seed, jint len);

#ifdef __cplusplus
}
#endif

#endif // FASTMATH_H
