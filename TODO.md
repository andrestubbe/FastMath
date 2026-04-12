# FastMath - Development TODO

## Current Status: 🚧 Work in Progress

### ✅ Completed
- [x] Project structure (Maven, LICENSE, .gitignore)
- [x] Baseline benchmark for all `java.lang.Math` methods
- [x] Performance analysis showing which methods need optimization

### 🔧 In Progress
- [ ] JNI C++ header file (`native/fastmath.h`)
- [ ] JNI C++ implementation (`native/fastmath.cpp`)
- [ ] OpenCL kernel file (`native/kernels/fastmath.cl`)

### 📋 To Do

#### Phase 1: JNI Foundation (SIMD)
- [ ] Implement `nativeSqrt()` - baseline test
- [ ] Implement `nativeSin()`, `nativeCos()`, `nativeTan()`
- [ ] Implement `nativeExp()`, `nativeLog()`
- [ ] Implement `nativePow()`, `nativeAtan2()` - high priority (slow in Java)
- [ ] Compile script for Visual Studio / cl.exe
- [ ] Library loader with fallback paths

#### Phase 2: Array/Batch Operations
- [ ] JNI array methods: `nativeSqrtArray()`, `nativeSinArray()`, etc.
- [ ] OpenCL context initialization for Intel/AMD/NVIDIA
- [ ] OpenCL kernel compilation at runtime
- [ ] Smart dispatch: JNI vs OpenCL based on array size

#### Phase 3: Full API Coverage
- [ ] All trigonometric functions (scalar + array)
- [ ] All hyperbolic functions
- [ ] All exponential/logarithmic functions
- [ ] All power/root functions
- [ ] Rounding functions (may skip - already fast in Java)
- [ ] Min/max/abs (may skip - already fast in Java)
- [ ] Special functions: `IEEEremainder`, `copySign`, `ulp`

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
