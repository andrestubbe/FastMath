# Building from Source

## Prerequisites

- JDK 17+
- Maven 3.9+
- **Windows:** Visual Studio 2019+ or Build Tools
- **Linux:** GCC 9+ or Clang 10+
- **macOS:** Xcode Command Line Tools

## Build

### Windows

```bash
compile.bat
mvn clean package
```

### Linux/macOS

```bash
chmod +x compile.sh
./compile.sh
mvn clean package
```

The build script auto-detects CPU features (AVX2, AVX-512, FMA3) and compiles with optimal flags.

## Run Examples

```bash
cd examples/00-basic-usage
mvn compile exec:java
```

## Run Benchmarks

```bash
mvn test -Dtest=Benchmark
```

## Installation

### JitPack (Recommended)

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
        <version>v1.1.0</version>
    </dependency>
</dependencies>
```

### Gradle (JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:fastmath:v1.1.0'
}
```

## Download Pre-built JAR

See [Releases Page](https://github.com/andrestubbe/FastMath/releases)

## Troubleshooting

### JNI UnsatisfiedLinkError

If you get `UnsatisfiedLinkError`, the native library was not found:

1. Check that the DLL/so/dylib exists in the project root or build output
2. On Windows, ensure the DLL is in PATH or copy to `C:\Windows\System32`
3. On Linux/macOS, set `LD_LIBRARY_PATH` or `DYLD_LIBRARY_PATH`

### GPU Acceleration Not Available

- Verify GPU drivers are up to date
- Check CUDA/OpenCL support for your GPU
- Use CPU fallback mode if GPU not available
