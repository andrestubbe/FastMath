/**
 * @file fastmath.h
 * @brief FastMath JNI Header - High-performance math library for Java
 *
 * @details Provides accelerated mathematical operations via multiple backends:
 * - Native C++ scalar operations
 * - AVX2 SIMD vectorized operations (256-bit vectors)
 * - OpenCL GPU acceleration for batch operations
 *
 * @par Function Categories
 * - Trigonometric: sin, cos, tan, asin, acos, atan, atan2
 * - Hyperbolic: sinh, cosh, tanh
 * - Exponential: exp, expm1, log, log1p, log10
 * - Power/Root: sqrt, cbrt, pow
 * - Rounding: ceil, floor, rint
 * - Special: Fast inverse square root (Quake algorithm)
 *
 * @par SIMD Optimization
 * - Auto-detects AVX2 support at runtime
 * - Falls back to scalar if SIMD unavailable
 * - Memory-aligned batch processing for best performance
 *
 * @par GPU Acceleration (OpenCL)
 * - Batch operations offloaded to GPU
 * - Automatic device detection and initialization
 * - Fallback to CPU if GPU unavailable
 *
 * @par Vector/Matrix Operations
 * - 3D vector: dot, cross, length
 * - 4x4 matrix: multiplication, vector transformation
 * - Batch operations for particle systems
 *
 * @author FastJava Team
 * @version 1.1.0
 * @copyright MIT License
 */

#ifndef FASTMATH_H
#define FASTMATH_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/** @defgroup Trigonometric Trigonometric Functions
 *  @brief Standard trigonometric operations
 *  @{ */
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSin(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCos(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeTan(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAsin(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAcos(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAtan(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeAtan2(JNIEnv *env, jclass cls, jdouble y, jdouble x);

/** @} */

/** @defgroup Hyperbolic Hyperbolic Functions
 *  @brief Hyperbolic sine, cosine, tangent
 *  @{ */
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSinh(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCosh(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeTanh(JNIEnv *env, jclass cls, jdouble x);

/** @} */

/** @defgroup Exponential Exponential and Logarithmic
 *  @brief exp, log, and related functions
 *  @{ */
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeExp(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeExpm1(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog1p(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeLog10(JNIEnv *env, jclass cls, jdouble x);

/** @} */

/** @defgroup Power Power and Root Functions
 *  @brief Square root, cube root, and power
 *  @{ */
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeSqrt(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCbrt(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativePow(JNIEnv *env, jclass cls, jdouble x, jdouble y);

/** @} */

/** @defgroup Rounding Rounding Functions
 *  @brief Ceiling, floor, and nearest integer
 *  @{ */
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeCeil(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeFloor(JNIEnv *env, jclass cls, jdouble x);
JNIEXPORT jdouble JNICALL Java_fastmath_FastMath_nativeRint(JNIEnv *env, jclass cls, jdouble x);

/** @} */

/** @defgroup BatchScalar Batch Operations (Scalar)
 *  @brief Array-based operations using scalar fallback
 *  @{ */
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeSinArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeCosArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);

/** @} */

/** @defgroup FastInvSqrt Fast Inverse Square Root
 *  @brief Quake III Arena algorithm - ~10x faster for games
 *  @details Uses bit manipulation and Newton-Raphson iteration
 *           for extremely fast 1/sqrt(x) approximation.
 *  @{ */
JNIEXPORT jfloat JNICALL Java_fastmath_FastMath_nativeFastInvSqrt(JNIEnv *env, jclass cls, jfloat x);
JNIEXPORT void JNICALL Java_fastmath_FastMath_nativeFastInvSqrtArray(JNIEnv *env, jclass cls, jfloatArray input, jfloatArray output, jint len);

/** @} */

/** @defgroup OpenCL GPU Acceleration (OpenCL)
 *  @brief OpenCL-based GPU batch operations
 *  @details Requires OpenCL-compatible GPU. Falls back to CPU
 *           if GPU unavailable.
 *  @{ */
JNIEXPORT jboolean JNICALL Java_fastmath_FastMath_initOpenCL(JNIEnv *env, jclass cls);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuSqrtArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuSinArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuCosArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuExpArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);
JNIEXPORT void JNICALL Java_fastmath_FastMath_gpuLogArray(JNIEnv *env, jclass cls, jdoubleArray input, jdoubleArray output, jint len);

/** @} */

/** @defgroup Vectors 3D Vector Operations
 *  @brief Vector math optimized with SIMD
 *  @{ */
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

/** @} */

/** @defgroup Noise Procedural Noise
 *  @brief Perlin and other noise generation
 *  @{ */
JNIEXPORT void JNICALL Java_fastmath_FastMathNoise_nativePerlinGrid(JNIEnv *env, jclass cls,
    jdoubleArray output, jint width, jint height, jdouble scale, jdouble offsetX, jdouble offsetY);

/** @} */

/** @defgroup Random Random Number Generation
 *  @brief High-performance RNG with GPU support
 *  @{ */
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

/** @} */

#ifdef __cplusplus
}
#endif

#endif // FASTMATH_H
