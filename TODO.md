# FastMath - Development TODO

## Current Status: 🚧 Work in Progress

### ✅ Completed
- [x] Project structure (Maven, LICENSE, .gitignore)
- [x] Baseline benchmark for all `java.lang.Math` methods
- [x] Performance analysis showing which methods need optimization
- [x] JNI C++ header file (`native/fastmath.h`)
- [x] JNI C++ implementation (`native/fastmath.cpp`)
- [x] OpenCL kernel file (`native/kernels/fastmath.cl`)
- [x] Compile script (`compile.bat`) with AVX2 optimization
- [x] Library loader with fallback paths
- [x] Java API with native method bindings
- [x] Native library test (`NativeTest.java`)
- [x] **AVX2 SIMD: 2.45x speedup on `sqrt(array)`** using `GetPrimitiveArrayCritical` + `_mm256_sqrt_pd`

### 🔧 In Progress (Phase 4)
- [ ] OpenCL context initialization for Intel/AMD/NVIDIA
- [ ] OpenCL kernel compilation at runtime
- [ ] Smart dispatch: JNI SIMD vs OpenCL GPU based on array size

### 📋 To Do

#### Phase 1: JNI Foundation (SIMD) ✅ COMPLETE
- [x] Implement `nativeSqrt()` - baseline test
- [x] Implement `nativeSin()`, `nativeCos()`, `nativeTan()`
- [x] Implement `nativeExp()`, `nativeLog()`
- [x] Implement `nativePow()`, `nativeAtan2()` - high priority (slow in Java)
- [x] Compile script for Visual Studio / cl.exe
- [x] Library loader with fallback paths

#### Phase 2: Array/Batch Operations ✅ SIMD DONE
- [x] JNI array methods: `nativeSqrtArray()`, `nativeSinArray()`, etc.
- [x] **AVX2 SIMD vectorization: 4 doubles per iteration**
- [x] `GetPrimitiveArrayCritical` for zero-copy array access

#### Phase 3: Fast Approximations ✅ COMPLETE
- [x] **Fast inverse sqrt (Quake algorithm)** - ~10x speedup, 0.0004% error
- [x] 8x unrolled loop for array processing
- [x] Scalar and array versions (`fastInvSqrt()`, `fastInvSqrtArray()`)

#### Phase 4: Full API Coverage (Optional)
- [ ] All trigonometric array functions (sin, cos, tan with SIMD)
- [ ] Fast approximations for sin/cos (for audio synthesis)
- [ ] Rounding functions (may skip - already fast in Java)
- [ ] Min/max/abs (may skip - already fast in Java)

#### Phase 5: Vector & Matrix Math 💡 IDEA
- [ ] `Vector2D`, `Vector3D`, `Vector4D` classes
- [ ] `Matrix3x3`, `Matrix4x4` for graphics/physics
- [ ] Use `fastInvSqrt()` for lightning-fast normalization
- [ ] Dot product, cross product, distance, lerp
- [ ] Transform, rotate, scale operations
- [ ] SIMD batch: `Vector3D.normalize(arrayOfVectors)`

*Perfect for game engines, physics simulations, 3D graphics*

#### Phase 4: Testing & Optimization
- [ ] Unit tests for correctness (JNI vs Java Math)
- [ ] Performance regression tests
- [ ] OpenCL crossover threshold tuning (target: ~1000 elements)
- [ ] Memory alignment optimizations
- [ ] AVX2/AVX-512 dispatch

### 🎯 Priority Matrix

| Method | Java ns/op | Priority | Strategy |
|--------|-----------|----------|----------|
| `sqrt` | 4.66 | Low | May not beat Java |
| `sin` | 14.1 | Medium | SIMD via C++ std::sin |
| `exp` | 12.7 | Medium | SIMD approximation |
| `pow` | 34.9 | **High** | JNI + fast path for x^2, x^0.5 |
| `atan2` | 51.5 | **High** | JNI - very slow in Java |
| `sinh` | 42.6 | **High** | JNI - slow in Java |
| Array ops | N/A | **Critical** | OpenCL GPU for >1000 elements |

### 📊 Target Performance

| Category | Target Speedup |
|----------|---------------|
| Slow scalars (atan2, pow) | 2-5x faster |
| Array operations | 10-100x faster (GPU) |
| Already fast ops (sqrt, abs) | Match or skip |

### 🖥️ Platform Support

| Platform | Status |
|----------|--------|
| Windows 11 | 🚧 In development |
| Windows 10 | 🚧 In development |
| Linux | ❌ Not planned |
| macOS | ❌ Not planned |

---

Last updated: 2026-04-12
