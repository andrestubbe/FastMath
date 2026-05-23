# FastMath — Ultra-Fast Native Math Library for Java [v0.1.0]

**A high-performance native math module for the FastJava ecosystem. SIMD-accelerated linear algebra, trigonometry, and interpolation.**

[![Status](https://img.shields.io/badge/status-v0.1.0--alpha-orange.svg)]()
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

**FastMath** delivers elite mathematical performance by leveraging native SIMD instructions (AVX2/SSE). Built for graphics engines, physics simulations, and high-performance computing.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [License](#license)

## Features
- **⚡ SIMD Accelerated**: Vector and Matrix operations via AVX2/SSE.
- **📈 Native Trig**: High-speed trigonometric functions optimized for throughput.
- **📦 Zero GC Stalls**: Minimal object creation for high-frequency math.
- **🚀 Raw Speed**: Built for developers who need maximum mathematical performance.

## Installation

### Option 1: Maven (Recommended)
Add the JitPack repository and the dependencies to your `pom.xml`:

`xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <!-- FastMath Library -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastmath</artifactId>
        <version>v0.1.0</version>
    </dependency>
    <!-- FastCore (Required Native Loader) -->
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>v0.1.0</version>
    </dependency>
</dependencies>
`

### Option 2: Gradle (via JitPack)
`groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:.1.0'
    implementation 'com.github.andrestubbe:fastcore:v0.1.0'
}
`

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1. 📦 **[fastmath-v0.1.0.jar](https://github.com/andrestubbe/FastMath/releases/download/v0.1.0/fastmath-v0.1.0.jar)** (The Core Library)
2. ⚙️ **[fastcore-v0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/v0.1.0/fastcore-v0.1.0.jar)** (The Mandatory Native Loader)

> [!IMPORTANT]
> All JARs must be in your classpath for the native JNI calls to function correctly.


## License
MIT License — See [LICENSE](LICENSE) for details.

---
**Part of the FastJava Ecosystem** — *Making the JVM faster.*


