@echo off
setlocal EnableDelayedExpansion

echo ===========================================
echo FastMath v1.0 - Native Build Script
echo ===========================================
echo.

:: Check for Java
if not defined JAVA_HOME (
    :: Try to find Java automatically
    if exist "C:\Program Files\Java\jdk-25\include\jni.h" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-25"
    ) else if exist "C:\Program Files\Java\jdk-21\include\jni.h" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) else if exist "C:\Program Files\Java\jdk-17\include\jni.h" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    ) else if exist "C:\Program Files\Java\jdk-11\include\jni.h" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-11"
    ) else (
        echo ERROR: Cannot find Java installation with jni.h
        echo Please set JAVA_HOME environment variable
        pause
        exit /b 1
    )
)

if not exist "%JAVA_HOME%\include\jni.h" (
    echo ERROR: Cannot find jni.h in %JAVA_HOME%\include
    echo Please check your Java installation
    echo JAVA_HOME is set to: %JAVA_HOME%
    pause
    exit /b 1
)

echo Found Java at: %JAVA_HOME%

:: Use vswhere to find Visual Studio
set "VSWHERE=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"

if not exist "%VSWHERE%" (
    echo ERROR: vswhere.exe not found!
    echo Visual Studio Installer might be missing.
    echo.
    pause
    exit /b 1
)

:: Find VS installation path
for /f "usebackq tokens=*" %%i in (`"%VSWHERE%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do (
    set "VS_INSTALL=%%i"
)

if not defined VS_INSTALL (
    echo ERROR: Visual Studio with C++ tools not found!
    echo.
    pause
    exit /b 1
)

echo Found Visual Studio at: %VS_INSTALL%

:: Setup VS environment
set "VCVARS=%VS_INSTALL%\VC\Auxiliary\Build\vcvars64.bat"

echo Setting up Visual Studio environment...
call "%VCVARS%"
if errorlevel 1 (
    echo ERROR: Failed to setup VS environment
    pause
    exit /b 1
)

:: Create build directory
if not exist build mkdir build

:: Check for Intel OpenCL SDK
if exist "C:\Program Files (x86)\Intel\oneAPI\compiler\latest\windows\include\CL\cl.h" (
    echo Found Intel OpenCL SDK
    set "OPENCL_INCLUDE=C:\Program Files (x86)\Intel\oneAPI\compiler\latest\windows\include"
    set "OPENCL_LIB=C:\Program Files (x86)\Intel\oneAPI\compiler\latest\windows\lib"
) else if exist "C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v12.0\include\CL\cl.h" (
    echo Found NVIDIA OpenCL
    set "OPENCL_INCLUDE=C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v12.0\include"
    set "OPENCL_LIB=C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v12.0\lib\x64"
) else (
    echo WARNING: OpenCL SDK not found - building without GPU support
    set "OPENCL_INCLUDE="
    set "OPENCL_LIB="
)

:: Compile native DLL
echo.
echo Compiling FastMath v1.0 with SIMD optimizations...
echo =====================================================
echo.

:: Check for OpenCL and add flags if available
set "OPENCL_FLAGS="
if defined OPENCL_INCLUDE (
    set "OPENCL_FLAGS=/I\"%OPENCL_INCLUDE%\""
)

:: Compile the DLL
cl /LD /Fe:build\fastmath.dll ^
    native\fastmath.cpp ^
    /I"%JAVA_HOME%\include" ^
    /I"%JAVA_HOME%\include\win32" ^
    %OPENCL_FLAGS% ^
    /EHsc /std:c++17 /O2 /arch:AVX2 /W3

:: Check result
if %errorlevel% neq 0 (
    echo.
    echo =====================================================
    echo COMPILATION FAILED
    echo =====================================================
    echo Check errors above
    pause
    exit /b 1
)

:: Copy to resources for Maven packaging
if not exist src\main\resources mkdir src\main\resources
if not exist src\main\resources\native mkdir src\main\resources\native
copy build\fastmath.dll src\main\resources\native\fastmath.dll

:: Copy OpenCL kernels if they exist
if exist native\kernels (
    if not exist src\main\resources\native\kernels mkdir src\main\resources\native\kernels
    copy native\kernels\*.cl src\main\resources\native\kernels\
)

:: Success
echo.
echo =====================================================
echo COMPILATION SUCCESSFUL!
echo =====================================================
echo.
echo FastMath v1.0 DLL created with:
echo - AVX2 optimizations enabled
echo - C++17 standard
echo - /O2 maximum speed optimization
echo.
echo Output: build\fastmath.dll
echo.
echo You can now build with Maven:
echo   mvn clean package
echo.
echo Or run benchmark:
echo   mvn test-compile exec:java -Dexec.mainClass=fastmath.MathBenchmark -Dexec.classpathScope=test
echo.

pause
