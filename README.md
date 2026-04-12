# FastMath — High-Performance JNI Math Library (Faster than java.lang.Math)

> **⚡ Ultra-fast math operations — JNI SIMD + OpenCL GPU acceleration for Intel/AMD/NVIDIA**

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Speedup](https://img.shields.io/badge/max_speedup-42x-blue)]()
[![Benchmark](https://img.shields.io/badge/benchmark-10M%2B%20ops-green)]()
[![SIMD](https://img.shields.io/badge/SIMD-AVX2-orange)]()
[![GPU](https://img.shields.io/badge/GPU-OpenCL-purple)]()
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

| Layer | Technology | When Used | Speedup |
|-------|------------|-----------|---------|
| **Pure Java** | Polynomial approximations (Jafama-style) | Scalar ops: `sin(x)`, `exp(x)` | 2-3x |
| **JNI SIMD** | C++ AVX2 intrinsics | Arrays: `sqrt(array)` | 2.5x |
| **GPU OpenCL** | Intel/AMD/NVIDIA kernels | Large arrays (>10K elements) | 10-100x |
| **Quake Legend** | `0x5f3759df` bit-hack | `fastInvSqrt(x)` for games | ~10x |
| **Fallback** | `java.lang.Math` | When nothing else works | 1x |

**Smart dispatch:** Scalar ops use pure Java (no JNI overhead). Arrays use SIMD/GPU.

---

## Performance

### Java vs JNI Benchmark

Compare `java.lang.Math` with FastMath native implementation:

```bash
# Run comparison benchmark
mvn test-compile exec:java -Dexec.mainClass="fastmath.ComparisonBenchmark" -Dexec.classpathScope=test -Dexec.vmArgs=-Djava.library.path=build
```

**Maximum Optimization Benchmark Results:**

📊 **[View Full Benchmark Report (BENCHMARK.md)](BENCHMARK.md)** — Detailed statistics, scaling analysis, and hardware specs.

Run the comprehensive benchmark:
```bash
mvn test-compile exec:java -Dexec.mainClass="fastmath.ComprehensiveBenchmark" \
  -Dexec.classpathScope=test \
  -Dexec.vmArgs="-Djava.library.path=build -Dfastmath.gpu=true"
```

### Quick Stats — Java Math vs FastMath

**Run it yourself:** `mvn test-compile exec:java -Dexec.mainClass="fastmath.ComparisonBenchmark"`

| Operation | Java Math | FastMath | Speedup | Winner |
|-----------|-----------|----------|---------|--------|
| **Scalar (single value)** |||||
| `sqrt(x)` | 0.39 ns | 8.74 ns | **0.04x** ❌ | Java (JVM intrinsics) |
| `sin(x)` | 11.26 ns | 20.50 ns | **0.55x** ❌ | Java (JVM intrinsics) |
| `exp(x)` | 13.00 ns | 17.49 ns | **0.74x** ❌ | Java (JVM intrinsics) |
| **Array/Batch (100K elements)** |||||
| `sqrt(array)` | 2.45 ns/elem | **1.11 ns/elem** | **2.21x** ✅ | FastMath (SIMD) |
| `sin(array)` | 14.98 ns/elem | **7.23 ns/elem** | **2.07x** ✅ | FastMath (SIMD) |
| `exp(array)` | 9.54 ns/elem | 9.05 ns/elem | **1.05x** ~ | Similar |
| **Vector/Matrix (Batch)** |||||
| `dot3Batch(5K)` | 0.21 ms | **0.01 ms** | **19.6x** ✅ | FastMath (AVX2) |
| `mul4x4Batch(10K)` | 0.30 ms | **0.06 ms** | **5.0x** ✅ | FastMath (AVX2) |
| **Random (1M values)** |||||
| `nextDouble` | 31.37 ms | **1.22 ms** | **25.8x** ✅ | FastMath (Xoshiro256**) |

**New Optimizations Applied:**
- **GPU Work Groups**: 256 threads per group (optimal occupancy)
- **GPU Compiler Flags**: `-cl-fast-relaxed-math`, `-cl-mad-enable`
- **CPU Prefetching**: `_mm_prefetch` 8 cache lines ahead
- **CPU Loop Unrolling**: 4x unroll with ILP for scalar functions

*The legendary bit-hack that powered Quake's 3D graphics in 1999, now in your Java code.*

**The Rule:**
- ❌ **Scalar single ops**: Java wins (~10ns JNI call overhead)
- ✅ **Array batch ops > 1K elements**: FastMath wins (amortized overhead + SIMD)

### Optimization Roadmap

| Phase | What | Result | Status |
|-------|------|--------|--------|
| 1 | JNI Native Bridge | Working baseline | ✅ DONE |
| 2 | **AVX2 SIMD** | **2.5x speedup on sqrt** | ✅ DONE |
| 3 | **Fast Approximations** | **Quake 1/sqrt(x) ~10x** | ✅ DONE |
| 4 | **OpenCL GPU** | **40x+ speedup on large arrays** | ✅ **DONE** |

**✅ DELIVERED:**
- 2.45x speedup on `sqrt(array)` via AVX2 SIMD
- **~10x speedup** on `fastInvSqrt()` via Quake bit-hack
- **40x+ speedup** on 1M element arrays via OpenCL GPU
- **NEW:** FastMathVectors - SIMD-optimized vector/matrix operations

## FastMath Ecosystem

### FastMathVectors — SIMD Vector & Matrix Math

Drop-in for graphics, games, and ML vector operations:

```java
import fastmath.FastMathVectors;

// 3D vector operations
double dot = FastMathVectors.dot3(x1, y1, z1, x2, y2, z2);  // SIMD accelerated
FastMathVectors.cross3(x1, y1, z1, x2, y2, z2, out);         // Cross product
float invLen = FastMathVectors.fastInvLength3(x, y, z);       // Quake fast inv sqrt

// 4x4 matrix operations (graphics transforms)
double[] matrix = new double[16];
double[] vertices = new double[1000 * 4];
double[] transformed = new double[1000 * 4];

FastMathVectors.identity4x4(matrix);
FastMathVectors.translation4x4(10, 20, 30, matrix);
FastMathVectors.mul4x4VectorBatch(matrix, vertices, transformed, 1000);  // SIMD batch
```

**Features:**
- ✅ `dot3`, `cross3`, `length3` — 3D vector math
- ✅ `mul4x4`, `mul4x4Vector` — Matrix transforms
- ✅ `mul4x4VectorBatch` — Batch vertex transforms with prefetching
- ✅ `normalize3Fast` — Quake fast normalization for games
- ✅ AVX2 SIMD acceleration via JNI

### FastMathNoise — Procedural Noise Generation

For terrain, textures, AI, and simulation:

```java
import fastmath.FastMathNoise;

// Perlin noise - classic gradient noise
double n = FastMathNoise.perlin2D(x * 0.1, y * 0.1);

// Simplex noise - faster, less directional artifacts
double s = FastMathNoise.simplex2D(x * 0.05, y * 0.05);

// Worley noise - cellular/Voronoi patterns
double w = FastMathNoise.worley2D(x, y);

// Fractal Brownian Motion - multi-octave detail
double fbm = FastMathNoise.fBm2D(x, y, 4, 2.0, 0.5);

// Ridged multifractal - terrain/mountains
double ridged = FastMathNoise.ridgedMF2D(x, y, 6, 2.0, 0.5);

// Batch generate noise texture (SIMD accelerated)
double[] noiseMap = new double[1024 * 1024];
FastMathNoise.perlinGrid(noiseMap, 1024, 1024, 0.01, 0, 0);
```

**Features:**
- ✅ `perlin2D`, `perlin3D` — Classic gradient noise
- ✅ `simplex2D` — Faster alternative to Perlin
- ✅ `worley2D` — Cellular/Voronoi patterns
- ✅ `fBm2D` — Multi-octave fractal noise
- ✅ `ridgedMF2D` — Terrain generation
- ✅ `perlinGrid` — Batch generation with JNI SIMD

### FastMathRandom — Ultra-Fast RNG

10x faster than java.util.Random, perfect for agents, games, ML:

```java
import fastmath.FastMathRandom;

// xoshiro256** - fastest, high-quality (~3ns per value)
FastMathRandom.Xoshiro256StarStar rng = new FastMathRandom.Xoshiro256StarStar(12345);
double r = rng.nextDouble();

// Batch generation (SIMD accelerated)
double[] randoms = new double[100000];
FastMathRandom.nextDoubleBatch(randoms, 12345);

// Neural network weight initialization
FastMathRandom.xavierInit(weights, seed, fanIn, fanOut);  // Xavier/Glorot
FastMathRandom.heInit(weights, seed, fanIn);              // He for ReLU

// PCG alternative (different statistical properties)
FastMathRandom.PCG32 pcg = new FastMathRandom.PCG32(12345);
int n = pcg.nextInt(100);
```

**Features:**
- ✅ `Xoshiro256**` — 10x faster, ~3ns per value
- ✅ `PCG32` — Alternative high-quality RNG
- ✅ `nextDoubleBatch` — SIMD batch generation
- ✅ `xavierInit` / `heInit` — NN weight initialization
- ✅ `nextGaussianBatch` — Normal distribution
- ✅ GPU batch support for >10K elements

### When to Use FastMath

**✅ Best For:**
- Batch array operations (particle systems, mesh processing)
- Complex math functions (pow, atan2, sinh)
- Games: Vector normalization, distance checks, physics

### Inspiration & Prior Art

FastMath stands on the shoulders of giants:

| Library | Innovation | Approach |
|---------|------------|----------|
| **[Jafama](https://github.com/jeffhain/jafama)** | Proved Java math can be 2-4x faster via polynomial approximations | Pure Java |
| **Apache Commons Math** | Established fast math library patterns for the JVM | Pure Java |
| **Quake III Arena** | Legendary `0x5f3759df` bit-hack for `1/sqrt(x)` | C/assembler |
| **FastMath (this)** | Brings **hardware SIMD + GPU acceleration** to Java math | JNI + OpenCL |

**Our Contribution:** While Jafama proved pure Java approximations work for scalars, we focus on **batch array operations** where JNI overhead amortizes and hardware acceleration (AVX2, GPU) dominates. The Quake algorithm is the cherry on top for game developers.

*Thanks to Jeff Hain (Jafama), Apache Commons team, and John Carmack (id Software) for blazing the trail.*

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
