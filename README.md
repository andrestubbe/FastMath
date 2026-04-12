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

Run the baseline benchmark:
```bash
mvn compile exec:java -Dexec.mainClass="fastmath.MathBenchmark"
```

Then compare with FastMath implementation (coming soon).

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
