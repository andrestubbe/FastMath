@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo [1/3] Building FastMath...
call mvn clean install -DskipTests "-Dgpg.skip=true" -q
if %ERRORLEVEL% NEQ 0 ( echo [ERROR] Build failed! & pause & exit /b %ERRORLEVEL% )

echo [2/3] Building Benchmark Uber-JAR...
cd examples\Benchmark
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 ( echo [ERROR] Benchmark build failed! & pause & exit /b %ERRORLEVEL% )

echo [3/3] Running JMH Benchmarks...
java --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow "-Djava.library.path=..\..\build;build;src\main\resources\native" -jar target\benchmarks.jar -f 1 -wi 2 -i 3 -tu ms -bm thrpt
cd ..\..
pause