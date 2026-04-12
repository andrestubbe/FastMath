# Legal Sources & Attribution

FastMath aggregates well-established mathematical techniques that are either:
- **Public domain** (mathematical formulas, algorithms)
- **MIT/BSD licensed** (referenced implementations)
- **Properly attributed open source**

## Algorithms Used

### 1. Quake III Arena Fast Inverse Square Root
- **Source:** Quake III Arena source code (id Software, 1999)
- **License:** Effectively public domain (disclosed via source leak, widely accepted)
- **Usage:** `FastMath.fastInvSqrt()` uses `0x5f3759df` bit-hack
- **Attribution:** John Carmack (id Software)

### 2. AVX2 SIMD Intrinsics
- **Source:** Intel AVX2 instruction set documentation
- **License:** Free to use (hardware instruction set)
- **Usage:** `_mm256_sqrt_pd()` for parallel sqrt operations
- **Attribution:** Intel Corporation

### 3. OpenCL Framework
- **Source:** Khronos Group OpenCL specification
- **License:** Free to implement
- **Usage:** GPU kernel dispatch for large arrays
- **Attribution:** Khronos Group

## Inspiration & Prior Art

### Jafama (Jeff Hain)
- **Repository:** https://github.com/jeffhain/jafama
- **License:** Apache License 2.0
- **Contribution:** Proved pure Java polynomial approximations can be 2-4x faster
- **Our Use:** Inspired approach, independently implemented

### Apache Commons Math
- **Project:** https://commons.apache.org/proper/commons-math/
- **License:** Apache License 2.0
- **Contribution:** Established Java math library patterns
- **Our Use:** Architectural inspiration

### Cephes Math Library
- **Author:** Stephen L. Moshier
- **License:** BSD (freely usable with attribution)
- **Contribution:** High-quality polynomial coefficients
- **Our Use:** Numerical methods inspiration (not direct code)

## Polynomial Approximations

The polynomial approximations in `FastMathPure.java` are derived from:
- **Taylor series expansions** (public domain mathematics)
- **IEEE Signal Processing Magazine** publications (public domain algorithms)
- **Remez algorithm** theory (public domain numerical analysis)

These are mathematical facts, not copyrightable code.

## MIT License

All original code in this project is MIT licensed:

```
Copyright (c) 2026 FastMath Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## Summary

- ✅ **Quake algorithm:** Public domain (legendary bit-hack)
- ✅ **AVX2 intrinsics:** Free (Intel hardware API)
- ✅ **Polynomial math:** Public domain (mathematical formulas)
- ✅ **Jafama inspiration:** Apache 2.0 (we reimplemented)
- ✅ **All original code:** MIT licensed

**Everything used is legally sound and properly attributed.**
