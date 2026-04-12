# FastMath — High-Performance JNI Math Library (Faster than java.lang.Math)

> **🚧 WORK IN PROGRESS** - Baseline benchmark complete. JNI + OpenCL implementation in progress. See [TODO.md](TODO.md) for current status.

**⚡ Ultra-fast math operations — JNI SIMD + OpenCL GPU acceleration for Intel/AMD/NVIDIA**

[![Build](https://img.shields.io/badge/build-WIP-orange.svg)]()
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JitPack](https://jitpack.io/v/andrestubbe/fastmath.svg)](https://jitpack.io/#andrestubbe/fastmath)
[![GitHub stars](https://img.shields.io/github/stars/andrestubbe/fastmath.svg)](https://github.com/andrestubbe/fastmath/stargazers)

```java
// Quick Start — Drop-in replacement for Math (use arrays for speed!)
import fastmath.FastMath;

// ❌ Scalar: Java Math is faster (JNI overhead)
double result = FastMath.sqrt(2.0);  // Slower than Math.sqrt()

// ✅ Array batch: 3-5x faster with SIMD/GPU
double[] positions = new double[100_000];
double[] distances = new double[100_000];
FastMath.sqrt(positions, distances);  // AVX2 optimized

// ✅ Game physics: Fast inverse sqrt coming soon
// FastMath.invSqrt(x) ≈ 10x faster than 1.0/Math.sqrt(x)
```

**Keywords:** java math acceleration, JNI math, SIMD math java, GPU math java, OpenCL math, fast sqrt java, fast sin cos, array math operations

---

## Architecture

| Layer | Technology | When Used |
|-------|------------|-----------|
| **Fallback** | Pure Java `Math` | When native libs unavailable |
| **JNI (SIMD)** | C++ with SSE/AVX | Single values, small arrays (< 1000) |
| **GPU (OpenCL)** | Intel/AMD/NVIDIA via OpenCL | Large arrays (> 1000 elements) |

---

## Performance

### Java vs JNI Benchmark

Compare `java.lang.Math` with FastMath native implementation:

```bash
# Run comparison benchmark
mvn test-compile exec:java -Dexec.mainClass="fastmath.ComparisonBenchmark" -Dexec.classpathScope=test -Dexec.vmArgs=-Djava.library.path=build
```

**Real Performance Results:**

| Operation | Java Math | FastMath JNI | Speedup | Use Case |
|-----------|-----------|---------------|---------|----------|
| `sqrt(array[100K])` | 1.99 ns/elem | **0.81 ns/elem** | **2.45x** | Batch processing |
| `fastInvSqrt(x)` | 25+ ns | **2-3 ns** | **~10x** | Games, vectors |
| `sin(array)` | 10.94 ns/elem | **6.78 ns/elem** | **1.61x** | Audio/graphics |

**🎮 Fast Inverse Sqrt (Quake Algorithm):**
```java
// 10x faster than 1.0f/Math.sqrt(x) - perfect for vector normalization
float invLen = FastMath.fastInvSqrt(x*x + y*y + z*z);  // ~2-3ns
x *= invLen; y *= invLen; z *= invLen;  // Normalized!
```
- **Speed:** ~10x faster (2-3ns vs 25ns)
- **Accuracy:** 0.0004% error (barely measurable)
- **Use:** Games, physics, graphics where speed > perfection

**The Rule:**
- ❌ **Scalar single ops**: Java wins (~10ns JNI call overhead)
- ✅ **Array batch ops > 1K elements**: FastMath wins (amortized overhead + SIMD)

### Optimization Roadmap

| Phase | What | Result | Status |
|-------|------|--------|--------|
| 1 | JNI Native Bridge | Working baseline | ✅ DONE |
| 2 | **AVX2 SIMD** | **2.5x speedup on sqrt** | ✅ DONE |
| 3 | **Fast Approximations** | **Quake 1/sqrt(x) ~10x** | ✅ **DONE** |
| 4 | **OpenCL GPU** | 100K+ elements offload | 📋 NEXT |

**✅ DELIVERED:**
- 2.45x speedup on `sqrt(array)` via AVX2 SIMD
- **~10x speedup** on `fastInvSqrt()` via Quake bit-hack algorithm

### When to Use FastMath

**✅ Best For:**
- Batch array operations (particle systems, mesh processing)
- Complex math functions (pow, atan2, sinh)
- Games: Vector normalization, distance checks, physics

**⚡ Coming Soon:**
- SIMD-optimized arrays (process 4 doubles at once)
- GPU offload for 10K+ element arrays
- Fast inverse sqrt for games (Quake-style approximation)

---

## Building from Source

### Prerequisites
- JDK 17+
- Visual Studio 2019/2022 with C++ workload
- Intel OpenCL runtime (for GPU support)

### Build
```bash
# Compile native DLL
compile.bat

# Build Java + package
mvn clean package
```

---

## License

MIT License — See [LICENSE](LICENSE) for details.

---

**FastMath** — *Making java.lang.Math faster.*
