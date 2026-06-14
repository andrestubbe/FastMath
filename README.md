# FastMath 0.1.0 [ALPHA-2026-06-14] — Ultra-Fast Native Math Library for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastMath/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastMath)

---

**?A high-performance native math module for the FastJava ecosystem. SIMD-accelerated linear algebra, trigonometry, and interpolation.**

FastMath delivers elite mathematical performance by leveraging native SIMD instructions (AVX2/SSE). Built for graphics engines, physics simulations, and high-performance computing.

---

[![FastKeyboard Showcase](docs/screenshot.png)](https://www.youtube.com/watch?v=BZsqQl7WqWk)

---

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [License](#license)

---

## Quick Start

```java
import fastjson.FastJSON;
import fastjson.FastJsonValue;

public class Demo {
    public static void main(String[] args) {
        // TODO
    }
}
```

---

## Features
- **? SIMD Accelerated**: Vector and Matrix operations via AVX2/SSE.
- **?? Native Trig**: High-speed trigonometric functions optimized for throughput.
- **?? Zero GC Stalls**: Minimal object creation for high-frequency math.
- **?? Raw Speed**: Built for developers who need maximum mathematical performance.

---

## Installation

### Option 1: Maven (Recommended)
Add the JitPack repository and the dependencies to your `pom.xml`:

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
        <artifactId>fastmath</artifactId>
        <version>0.1.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>fastcore</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastmath:0.1.0'
    implementation 'com.github.andrestubbe:fastcore:0.1.0'
}
```

### Option 3: Direct Download (No Build Tool)
Download the latest JARs directly to add them to your classpath:

1. ?? **[fastmath-0.1.0.jar](https://github.com/andrestubbe/FastMath/releases/download/0.1.0/fastmath-0.1.0.jar)** (The Core Library)
2. ?? **[fastcore-0.1.0.jar](https://github.com/andrestubbe/FastCore/releases/download/0.1.0/fastcore-0.1.0.jar)** (The Mandatory Native Loader)


---

## License
MIT License � See [LICENSE](LICENSE) file for details.

---

## Related Projects
- [FastCore](https://github.com/andrestubbe/FastCore) � Native Library Loader for Java
- [FastMath](https://github.com/andrestubbe/FastMath) � High-performance RawInput engine
- [FastTheme](https://github.com/andrestubbe/FastTheme) � Advanced UI styling engine

---
**Part of the FastJava Ecosystem** � *Making the JVM faster. Small package. Maximum speed. Zero bloat. ????*







