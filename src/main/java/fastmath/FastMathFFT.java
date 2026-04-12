package fastmath;

/**
 * FastMathFFT - High-performance Fast Fourier Transform (FFT).
 * 
 * 1D and 2D FFT implementations using:
 * - AVX2 SIMD acceleration (radix-2, radix-4)
 * - GPU acceleration via OpenCL (large arrays)
 * - Pure Java fallback (small arrays)
 * 
 * Applications: Audio processing, image analysis, signal processing,
 * machine learning (convolution), physics simulations.
 * 
 * @author Andre Stubbe
 * @since 1.0.0
 */
public class FastMathFFT {
    
    // Thresholds for backend selection
    private static final int SIMD_THRESHOLD = 1024;   // Use SIMD above this
    private static final int GPU_THRESHOLD = 65536; // Use GPU above this (2^16)
    
    // Native method handles
    private static final boolean nativeAvailable;
    
    static {
        boolean available = false;
        try {
            System.loadLibrary("fastmath");
            available = true;
        } catch (UnsatisfiedLinkError e) {
            System.err.println("FastMathFFT: Native library not available, using Java fallback");
        }
        nativeAvailable = available;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // 1D FFT - Complex Numbers (interleaved: real, imag, real, imag...)
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Perform 1D Complex FFT in-place.
     * Input/Output format: [r0, i0, r1, i1, ... rN-1, iN-1]
     * Array length must be power of 2.
     * 
     * @param data Complex data array (interleaved real/imag), modified in-place
     * @param inverse true for IFFT, false for FFT
     * @throws IllegalArgumentException if length is not power of 2
     */
    public static void fft1D(double[] data, boolean inverse) {
        if (data == null || data.length < 2) return;
        if ((data.length & (data.length - 1)) != 0) {
            throw new IllegalArgumentException("FFT length must be power of 2, got " + data.length);
        }
        
        int n = data.length / 2; // Number of complex samples
        
        // Select optimal backend
        if (nativeAvailable && n >= GPU_THRESHOLD && FastMathInspector.hasGPU()) {
            nativeFFT1DGPU(data, inverse);
        } else if (nativeAvailable && n >= SIMD_THRESHOLD) {
            nativeFFT1DSIMD(data, inverse);
        } else {
            fft1DJava(data, inverse);
        }
    }
    
    /**
     * Perform 1D Real FFT (output is complex).
     * Input: real samples, Output: N/2+1 complex frequencies (interleaved)
     * 
     * @param realInput Real input samples, length must be power of 2
     * @param complexOutput Output buffer, length must be (realInput.length + 2)
     */
    public static void fft1DReal(double[] realInput, double[] complexOutput, boolean inverse) {
        int n = realInput.length;
        if ((n & (n - 1)) != 0) {
            throw new IllegalArgumentException("FFT length must be power of 2");
        }
        
        // Pack real into complex (imag = 0)
        for (int i = 0; i < n; i++) {
            complexOutput[i * 2] = realInput[i];
            complexOutput[i * 2 + 1] = 0.0;
        }
        
        fft1D(complexOutput, inverse);
    }
    
    /**
     * Batch FFT for multiple signals.
     * Efficient for processing many small FFTs (e.g., audio frames).
     * 
     * @param signals Array of signals, each power-of-2 length
     * @param inverse true for IFFT
     */
    public static void fft1DBatch(double[][] signals, boolean inverse) {
        if (nativeAvailable && signals.length >= 100) {
            nativeFFT1DBatch(signals, inverse);
        } else {
            for (double[] signal : signals) {
                fft1D(signal, inverse);
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // 2D FFT - For Images and Matrices
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Perform 2D Complex FFT on a matrix.
     * Input format: row-major, interleaved complex [rows][cols*2]
     * Both dimensions must be power of 2.
     * 
     * @param data 2D array [rows][cols*2] interleaved complex
     * @param inverse true for IFFT
     */
    public static void fft2D(double[][] data, boolean inverse) {
        int rows = data.length;
        int cols = data[0].length / 2; // Complex samples per row
        
        // Verify dimensions
        if ((rows & (rows - 1)) != 0 || (cols & (cols - 1)) != 0) {
            throw new IllegalArgumentException("2D FFT dimensions must be power of 2");
        }
        
        // Row-wise FFT
        for (int i = 0; i < rows; i++) {
            fft1D(data[i], inverse);
        }
        
        // Column-wise FFT (transpose, FFT, transpose back)
        // For efficiency, we process columns in place
        double[] column = new double[rows * 2];
        for (int j = 0; j < cols; j++) {
            // Extract column
            for (int i = 0; i < rows; i++) {
                column[i * 2] = data[i][j * 2];
                column[i * 2 + 1] = data[i][j * 2 + 1];
            }
            // FFT column
            fft1D(column, inverse);
            // Store back
            for (int i = 0; i < rows; i++) {
                data[i][j * 2] = column[i * 2];
                data[i][j * 2 + 1] = column[i * 2 + 1];
            }
        }
    }
    
    /**
     * 2D Real FFT for grayscale images.
     * 
     * @param image Input image [rows][cols], dimensions power of 2
     * @param spectrum Output [rows][cols/2+1] complex (interleaved)
     */
    public static void fft2DReal(double[][] image, double[][][] spectrum, boolean inverse) {
        int rows = image.length;
        int cols = image[0].length;
        
        // Convert to complex and do 2D FFT
        double[][] complex = new double[rows][cols * 2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                complex[i][j * 2] = image[i][j];
                complex[i][j * 2 + 1] = 0.0;
            }
        }
        
        fft2D(complex, inverse);
        
        // Extract spectrum (only positive frequencies in x)
        int outCols = cols / 2 + 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < outCols; j++) {
                spectrum[i][j][0] = complex[i][j * 2];
                spectrum[i][j][1] = complex[i][j * 2 + 1];
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Spectrogram & Audio Analysis
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Compute spectrogram from audio signal.
     * Creates a time-frequency representation (e.g., for visualizer).
     * 
     * @param audio Audio samples
     * @param windowSize FFT window size (power of 2, e.g., 1024)
     * @param hopSize Samples between consecutive windows
     * @param spectrogram Output [timeFrames][freqBins] magnitude
     */
    public static void spectrogram(double[] audio, int windowSize, int hopSize, 
                                   double[][] spectrogram) {
        int numFrames = (audio.length - windowSize) / hopSize + 1;
        double[] window = new double[windowSize * 2]; // Complex buffer
        double[] hamming = createHammingWindow(windowSize);
        
        for (int frame = 0; frame < numFrames; frame++) {
            // Apply window function
            int start = frame * hopSize;
            for (int i = 0; i < windowSize; i++) {
                window[i * 2] = audio[start + i] * hamming[i]; // Real
                window[i * 2 + 1] = 0.0; // Imag
            }
            
            // FFT
            fft1D(window, false);
            
            // Compute magnitude spectrum (only positive frequencies)
            int freqBins = windowSize / 2 + 1;
            for (int k = 0; k < freqBins; k++) {
                double re = window[k * 2];
                double im = window[k * 2 + 1];
                spectrogram[frame][k] = Math.sqrt(re * re + im * im);
            }
        }
    }
    
    /**
     * Convolve two signals using FFT (fast convolution).
     * Much faster than direct convolution for large signals.
     * 
     * @param signal Input signal
     * @param kernel Filter/kernel (e.g., impulse response)
     * @param output Convolved output (length = signal.length + kernel.length - 1)
     */
    public static void convolveFFT(double[] signal, double[] kernel, double[] output) {
        int n = signal.length + kernel.length - 1;
        int fftSize = nextPowerOf2(n);
        
        double[] signalPadded = new double[fftSize * 2];
        double[] kernelPadded = new double[fftSize * 2];
        
        // Zero-pad
        for (int i = 0; i < signal.length; i++) {
            signalPadded[i * 2] = signal[i];
        }
        for (int i = 0; i < kernel.length; i++) {
            kernelPadded[i * 2] = kernel[i];
        }
        
        // FFT both
        fft1D(signalPadded, false);
        fft1D(kernelPadded, false);
        
        // Multiply in frequency domain (complex multiplication)
        for (int i = 0; i < fftSize; i++) {
            double a = signalPadded[i * 2];
            double b = signalPadded[i * 2 + 1];
            double c = kernelPadded[i * 2];
            double d = kernelPadded[i * 2 + 1];
            signalPadded[i * 2] = a * c - b * d;     // Real
            signalPadded[i * 2 + 1] = a * d + b * c; // Imag
        }
        
        // IFFT
        fft1D(signalPadded, true);
        
        // Extract real part
        for (int i = 0; i < n; i++) {
            output[i] = signalPadded[i * 2] / fftSize; // Normalize
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Utility Functions
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Create frequency bins for an FFT of given size and sample rate.
     * Useful for labeling spectrograms.
     * 
     * @param fftSize FFT window size
     * @param sampleRate Audio sample rate (Hz)
     * @return Array of frequency values for each bin
     */
    public static double[] createFrequencyBins(int fftSize, double sampleRate) {
        double[] freqs = new double[fftSize / 2 + 1];
        double binWidth = sampleRate / fftSize;
        for (int i = 0; i < freqs.length; i++) {
            freqs[i] = i * binWidth;
        }
        return freqs;
    }
    
    /**
     * Convert magnitude spectrum to dB scale.
     * 
     * @param magnitude Input magnitudes
     * @param dB Output in dB (20 * log10(magnitude))
     */
    public static void magnitudeToDB(double[] magnitude, double[] dB) {
        for (int i = 0; i < magnitude.length; i++) {
            dB[i] = 20.0 * Math.log10(magnitude[i] + 1e-10); // Avoid log(0)
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Java Implementation (Cooley-Tukey Radix-2 FFT)
    // ═══════════════════════════════════════════════════════════════════════
    
    private static void fft1DJava(double[] data, boolean inverse) {
        int n = data.length / 2;
        
        // Bit-reversal permutation
        bitReverse(data, n);
        
        // Cooley-Tukey iterations
        for (int len = 2; len <= n; len <<= 1) {
            double angle = 2.0 * Math.PI / len * (inverse ? 1 : -1);
            double wlenCos = Math.cos(angle);
            double wlenSin = Math.sin(angle);
            
            for (int i = 0; i < n; i += len) {
                double wCos = 1.0;
                double wSin = 0.0;
                
                for (int j = 0; j < len / 2; j++) {
                    int idx1 = (i + j) * 2;
                    int idx2 = (i + j + len / 2) * 2;
                    
                    double uRe = data[idx1];
                    double uIm = data[idx1 + 1];
                    double vRe = data[idx2] * wCos - data[idx2 + 1] * wSin;
                    double vIm = data[idx2] * wCos + data[idx2 + 1] * wSin;
                    
                    data[idx1] = uRe + vRe;
                    data[idx1 + 1] = uIm + vIm;
                    data[idx2] = uRe - vRe;
                    data[idx2 + 1] = uIm - vIm;
                    
                    // Update w (multiply by wlen)
                    double nextWCos = wCos * wlenCos - wSin * wlenSin;
                    double nextWSin = wCos * wlenSin + wSin * wlenCos;
                    wCos = nextWCos;
                    wSin = nextWSin;
                }
            }
        }
        
        // Normalize for inverse
        if (inverse) {
            double scale = 1.0 / n;
            for (int i = 0; i < data.length; i++) {
                data[i] *= scale;
            }
        }
    }
    
    private static void bitReverse(double[] data, int n) {
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (i < j) {
                // Swap complex samples
                double tempRe = data[i * 2];
                double tempIm = data[i * 2 + 1];
                data[i * 2] = data[j * 2];
                data[i * 2 + 1] = data[j * 2 + 1];
                data[j * 2] = tempRe;
                data[j * 2 + 1] = tempIm;
            }
            
            int bit = n >> 1;
            while (j >= bit) {
                j -= bit;
                bit >>= 1;
            }
            j += bit;
        }
    }
    
    private static double[] createHammingWindow(int size) {
        double[] window = new double[size];
        for (int i = 0; i < size; i++) {
            window[i] = 0.54 - 0.46 * Math.cos(2.0 * Math.PI * i / (size - 1));
        }
        return window;
    }
    
    private static int nextPowerOf2(int n) {
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Native Methods (SIMD and GPU)
    // ═══════════════════════════════════════════════════════════════════════
    
    private static native void nativeFFT1DSIMD(double[] data, boolean inverse);
    private static native void nativeFFT1DGPU(double[] data, boolean inverse);
    private static native void nativeFFT1DBatch(double[][] signals, boolean inverse);
    
    // ═══════════════════════════════════════════════════════════════════════
    // Demo / Test
    // ═══════════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           FastMathFFT Demo                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Demo 1: Simple 1D FFT
        System.out.println("Demo 1: 1D FFT (8-point)");
        double[] signal = new double[16]; // 8 complex samples = 16 doubles
        // Create a simple sine wave
        for (int i = 0; i < 8; i++) {
            signal[i * 2] = Math.sin(2.0 * Math.PI * i / 8.0);
        }
        
        System.out.print("Input:  ");
        for (int i = 0; i < 8; i++) {
            System.out.printf("%.3f ", signal[i * 2]);
        }
        System.out.println();
        
        fft1D(signal, false);
        
        System.out.print("FFT:    ");
        for (int i = 0; i < 8; i++) {
            System.out.printf("%.3f+%.3fi ", signal[i * 2], signal[i * 2 + 1]);
        }
        System.out.println();
        System.out.println();
        
        // Demo 2: Spectrogram
        System.out.println("Demo 2: Spectrogram (simulated audio)");
        int sampleRate = 44100;
        double duration = 0.1; // 100ms
        int samples = (int)(sampleRate * duration);
        double[] audio = new double[samples];
        
        // Create chirp signal (frequency sweep)
        for (int i = 0; i < samples; i++) {
            double t = i / (double)sampleRate;
            double freq = 440.0 + 880.0 * t; // 440Hz → 1320Hz
            audio[i] = Math.sin(2.0 * Math.PI * freq * t);
        }
        
        int windowSize = 1024;
        int hopSize = 512;
        int numFrames = (samples - windowSize) / hopSize + 1;
        int freqBins = windowSize / 2 + 1;
        double[][] spec = new double[numFrames][freqBins];
        
        long start = System.nanoTime();
        spectrogram(audio, windowSize, hopSize, spec);
        long elapsed = System.nanoTime() - start;
        
        System.out.printf("Spectrogram: %d frames × %d bins%n", numFrames, freqBins);
        System.out.printf("Time: %.2f ms%n", elapsed / 1_000_000.0);
        System.out.println();
        
        // Demo 3: Fast Convolution
        System.out.println("Demo 3: Fast Convolution");
        double[] sig = {1, 2, 3, 4, 5};
        double[] kern = {1, 0, -1}; // Simple derivative filter
        double[] convResult = new double[sig.length + kern.length - 1];
        
        convolveFFT(sig, kern, convResult);
        
        System.out.print("Signal:  ");
        for (double v : sig) System.out.printf("%.0f ", v);
        System.out.println();
        System.out.print("Kernel:  ");
        for (double v : kern) System.out.printf("%.0f ", v);
        System.out.println();
        System.out.print("Result:  ");
        for (double v : convResult) System.out.printf("%.1f ", v);
        System.out.println();
        System.out.println();
        
        System.out.println("✅ FFT Demo complete!");
        System.out.println("   Native available: " + nativeAvailable);
        if (nativeAvailable) {
            System.out.println("   SIMD threshold: " + SIMD_THRESHOLD);
            System.out.println("   GPU threshold:  " + GPU_THRESHOLD);
        }
    }
}
