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

**Real Performance Results (100K element arrays):**

| Operation | Java Math | FastMath JNI | Speedup | Implementation |
|-----------|-----------|---------------|---------|----------------|
| `sqrt(array)` | 1.99 ns/elem | **0.81 ns/elem** | **2.45x** | AVX2 SIMD `_mm256_sqrt_pd` |
| `sin(array)` | 10.94 ns/elem | **6.78 ns/elem** | **1.61x** | 4x unrolled loop |
| `exp(array)` | 9.25 ns/elem | 12.86 ns/elem | 0.72x | JNI overhead high for exp |

**Key:** `GetPrimitiveArrayCritical` eliminates array copy overhead. AVX2 processes 4 doubles per instruction.

**The Rule:**
- ❌ **Scalar single ops**: Java wins (~10ns JNI call overhead)
- ✅ **Array batch ops > 1K elements**: FastMath wins (amortized overhead + SIMD)

### Optimization Roadmap

| Phase | What | Result | Status |
|-------|------|--------|--------|
| 1 | JNI Native Bridge | Working baseline | ✅ DONE |
| 2 | **AVX2 SIMD** | **2.5x speedup on sqrt** | ✅ **DONE** |
| 3 | **Fast Approximations** | Quake 1/sqrt(x) ~10x | � NEXT |
| 4 | **OpenCL GPU** | 100K+ elements offload | 📋 QUEUED |

**Delivered:** 2.45x speedup on `sqrt(array)` via AVX2 + `GetPrimitiveArrayCritical`

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
