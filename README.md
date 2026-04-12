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
// Quick Start — Drop-in replacement for Math
import fastmath.FastMath;

// Scalar operations — JNI SIMD (always faster)
double result = FastMath.sqrt(2.0);
double sin = FastMath.sin(Math.PI / 2);

// Batch operations — GPU auto-dispatch for large arrays
double[] input = new double[10000];
double[] output = new double[10000];
FastMath.sqrt(input, output);  // Uses Intel Iris via OpenCL
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

**Sample Results (1M iterations):**

| Method | Java Math | FastMath JNI | Speedup | Status |
|--------|-----------|---------------|---------|--------|
| `sin(x)` | 14 ns | 12 ns | 1.18x | ✅ FASTER |
| `exp(x)` | 13 ns | 11 ns | 1.18x | ✅ FASTER |
| `sinh(x)` | 20 ns | 17 ns | 1.18x | ✅ FASTER |
| `tanh(x)` | 17 ns | 15 ns | 1.13x | ✅ FASTER |
| `sqrt(x)` | 8 ns | 12 ns | 0.67x | 📊 Optimizing |
| `log(x)` | 10 ns | 18 ns | 0.56x | 📊 Optimizing |

*Note: JNI call overhead is ~10-15ns. Simple ops like `sqrt` need SIMD batching to show gains.*

### Optimization Roadmap

| Phase | Optimization | Expected Speedup | Status |
|-------|--------------|------------------|--------|
| 1 | Native JNI (baseline) | 0.5-1.2x | ✅ DONE |
| 2 | SIMD Vectorization (AVX2) | 2-4x | 🚧 NEXT |
| 3 | Batch Array Processing | 3-8x | 📋 PLANNED |
| 4 | OpenCL GPU Dispatch | 10-100x | 📋 PLANNED |

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
