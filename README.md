# FastMath 0.1.0 [ALPHA-2026-08-31]: Ultra-Fast Native Math Library for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastMath/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-0.1.0-green.svg)](https://jitpack.io/#andrestubbe/FastMath)

---

**High-performance native math module for the FastJava ecosystem: SIMD-accelerated linear algebra, trigonometry, statistics, and 3D vector geometry.**

FastMath delivers mathematical performance for the JVM by combining pure-Java polynomial approximations (zero JNI boundary overhead for scalar calls) with native AVX2 SIMD kernels and optional OpenCL GPU offloading for batch array processing. Designed specifically for game physics, graphics pipelines, robotics, and scientific computing.

---

## Quick Start

```java
import fastmath.FastMath;
import fastmath.FastMathVectors;
import fastmath.FastMathStats;

public class Demo {
    public static void main(String[] args) {
        // 1. High-speed trigonometry & roots (polynomial fast approximations)
        double sinVal = FastMath.sin(1.234567);
        double sqrtVal = FastMath.sqrt(144.0);
        System.out.println("FastMath sin: " + sinVal + ", sqrt: " + sqrtVal);

        // 2. SIMD-accelerated 3D vector operations
        double dot = FastMathVectors.dot3(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        double[] cross = new double[3];
        FastMathVectors.cross3(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, cross);
        System.out.println("Dot3: " + dot + ", Cross3: [" + cross[0] + ", " + cross[1] + ", " + cross[2] + "]");

        // 3. Batch statistics over double arrays
        double[] samples = { 10.5, 20.0, 15.5, 25.0, 30.0 };
        double mean = FastMathStats.mean(samples);
        double stdDev = FastMathStats.stddev(samples);
        System.out.println("Mean: " + mean + ", StdDev: " + stdDev);
    }
}
```

---

## Table of Contents

- [Why FastMath?](#why-fastmath)
- [Quick Start](#quick-start)
- [Key Features](#key-features)
- [Performance & Benchmarks](#performance--benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [Related Projects](#related-projects)
- [License](#license)

---

## Why FastMath?

Standard `java.lang.Math` relies on standard C libm or HotSpot intrinsics that prioritize strict IEEE 754 compliance across all edge cases over raw calculation throughput. In compute-intensive simulation loops, physics engines, or game rendering pipelines, standard Java math introduces significant hurdles:

- **Scalar Overhead**: Trigonometric operations like `Math.sin()` or `Math.cos()` can take 30 to 80 cycles, stalling CPU pipelines in inner loops.
- **No Vectorized Primitives**: Standard Java offers no built-in primitives for 3D dot products, cross products, or batch array transformations without allocating temporary heap objects.
- **Naive JNI Traps**: Simple JNI wrappers around native math libraries often run *slower* than pure Java because of the JNI call transition cost (10–15 ns per invocation).

FastMath solves this by employing **Smart Tiered Dispatch**:

- **Zero-JNI Scalar Approximations**: Scalar trigonometry (`sin`, `cos`, `atan2`) uses pure-Java minimax polynomial approximations (`FastMathPure`), completely eliminating JNI overhead.
- **Hardware AVX2 Vector Operations**: Vector and batch operations leverage hand-tuned AVX2 SIMD instructions to process multiple floats and doubles in parallel.
- **Zero GC Allocation**: Linear algebra methods operate directly on primitive primitives and pre-allocated buffers with zero heap garbage.
- **Optional GPU Offloading**: For massive datasets (>1,000 elements), FastMath can optionally offload computations to OpenCL GPU compute kernels.

| Feature | Standard `java.lang.Math` | Apache Commons Math | FastMath |
|:---|:---|:---|:---|
| **Scalar Trig Strategy** | Strict IEEE libm / intrinsic | Accurate pure Java (slow) | **Minimax polynomial (ultra-fast, zero JNI)** |
| **Vector / 3D Geometry** | None (manual loops) | Heap-allocated objects | **SIMD AVX2 primitives (zero-allocation)** |
| **Batch Array Ops** | Iterative scalar loop | Iterative scalar loop | **Native AVX2 vectorized + OpenCL GPU** |
| **Garbage Collection** | Minimal | High (temporary Vector3D objects) | **Zero GC pressure** |
| **JVM Footprint** | Built-in | Heavy (>2 MB JAR) | **Lightweight single-purpose library** |

---

## Key Features

- ⚡ **SIMD-Accelerated Geometry**: 2D, 3D, and 4D dot products, cross products, reflections, and normalization accelerated with AVX2.
- 📈 **High-Throughput Trigonometry**: Minimax polynomial approximations for `sin`, `cos`, `tan`, `atan2`, and roots.
- 📊 **High-Performance Statistics**: Single-pass Welford algorithms and SIMD batch mean, variance, and standard deviation.
- 📦 **Zero Heap Allocation**: Functions accept primitive arguments or write into caller-supplied output buffers.
- 🌐 **Tiered Dispatch Architecture**: Automatically routes between pure Java fast algorithms, JNI SIMD, and OpenCL GPU.

---

## Performance & Benchmarks

FastMath includes a built-in JMH benchmark suite comparing throughput against `java.lang.Math`. In official JMH throughput runs:

```text
Benchmark                               Mode  Cnt         Score   Units
Benchmark.benchmarkFastMathSin         thrpt    3  110512.431  ops/ms
Benchmark.benchmarkJavaMathSin         thrpt    3   24890.112  ops/ms
Benchmark.benchmarkFastMathVectorsDot3 thrpt    3  185240.890  ops/ms
```

> **Over 4x Faster Trigonometry**: `FastMath.sin()` delivers more than 100,000,000 operations per second on modern x86-64 hardware with negligible precision loss.

---

## API Quick Reference

| Class / Method | Return Type | Description | Docs |
|:---|:---|:---|:---|
| `FastMath.sin(x)` | `double` | Ultra-fast polynomial sine approximation. | [Reference](docs/REFERENCE.md) |
| `FastMath.cos(x)` | `double` | Ultra-fast polynomial cosine approximation. | [Reference](docs/REFERENCE.md) |
| `FastMath.sqrt(x)` | `double` | Accelerated square root computation. | [Reference](docs/REFERENCE.md) |
| `FastMathVectors.dot3(x1,y1,z1, x2,y2,z2)` | `double` | SIMD dot product of two 3D vectors. | [Reference](docs/REFERENCE.md) |
| `FastMathVectors.cross3(x1,y1,z1, x2,y2,z2, out)` | `void` | SIMD cross product of two 3D vectors. | [Reference](docs/REFERENCE.md) |
| `FastMathVectors.length3(x,y,z)` | `double` | 3D vector length / magnitude. | [Reference](docs/REFERENCE.md) |
| `FastMathStats.mean(data)` | `double` | Arithmetic mean of double array. | [Reference](docs/REFERENCE.md) |
| `FastMathStats.variance(data)` | `double` | Welford numerical variance. | [Reference](docs/REFERENCE.md) |
| `FastMathStats.stddev(data)` | `double` | Sample or population standard deviation. | [Reference](docs/REFERENCE.md) |

---

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|:---|:---|:---|:---|
| **FastMath Interactive Demo** | [Demo.java](examples/Demo/src/main/java/fastmath/Demo.java) | `run-demo.bat` | Terminal demonstration of trigonometry, 3D vectors, and statistics. |
| **JMH Benchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastmath/benchmark/Benchmark.java) | `run-benchmark.bat` | JMH throughput benchmarks comparing FastMath vs. standard Java Math. |

---

## Installation

### Option 1: Maven (Recommended)

Add the JitPack repository and dependencies to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastMath</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastCore</artifactId>
        <version>0.1.1</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastMath:0.1.0'
    implementation 'com.github.andrestubbe:FastCore:0.1.1'
}
```

### Option 3: Direct Download (No Build Tool)

Download pre-compiled release JARs directly from [GitHub Releases](https://github.com/andrestubbe/FastMath/releases/tag/0.1.0):

* 📦 **[FastMath-0.1.0.jar](https://github.com/andrestubbe/FastMath/releases/download/0.1.0/FastMath-0.1.0.jar)** (Core Library)
* ⚙️ **[FastCore-0.1.1.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.1/FastCore-0.1.1.jar)** (Native Loader)

---

## Documentation

* **[REFERENCE.md](docs/REFERENCE.md)**: Full API reference and function signatures.
* **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Design philosophy, polynomial approximation errors, and dispatch logic.
* **[COMPILE.md](docs/COMPILE.md)**: Native C++ and MSVC compilation instructions.
* **[CHANGELOG.md](docs/CHANGELOG.md)**: Version history and release notes.
* **[ROADMAP.md](docs/ROADMAP.md)**: Future development milestones.

---

## Platform Support

| Platform | Architecture | Status | Notes |
|:---|:---|:---|:---|
| Windows | x86-64 | Supported | Fully tested on Windows 10/11 with AVX2 SIMD acceleration |
| Linux | x86-64 | Planned | Native AVX2 library compilation in progress |
| macOS | Apple Silicon (arm64) | Planned | ARM NEON vector engine planned |
| macOS | Intel (x86-64) | Planned | Planned x86-64 support |

---

## Related Projects

- [FastCore](https://github.com/andrestubbe/FastCore) — Unified JNI loader and platform abstraction
- [FastSIMD](https://github.com/andrestubbe/FastSIMD) — Low-level SIMD intrinsics and hardware vector operations
- [FastFloat](https://github.com/andrestubbe/FastFloat) — Ultra-fast IEEE 754 floating-point operations
- [FastTheme](https://github.com/andrestubbe/FastTheme) — Advanced UI styling and theme engine

---

## License

This project is licensed under the MIT License: see the [LICENSE](LICENSE) file for details.

---

**Part of the FastJava Ecosystem** — *Making the JVM faster.* 🚀