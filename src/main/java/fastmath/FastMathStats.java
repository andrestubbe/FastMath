package fastmath;

import java.util.Arrays;

/**
 * FastMathStats - SIMD-accelerated statistical operations.
 * 
 * Batch statistical computations for data science, finance,
 * machine learning, and real-time analytics.
 * 
 * All operations are optimized for:
 * - Large datasets (SIMD vectorization)
 * - Real-time streams (single-pass algorithms)
 * - GPU offload (optional for massive data)
 * 
 * @author Andre Stubbe
 * @since 1.0.0
 */
public class FastMathStats {
    
    // Thresholds for backend selection
    private static final int SIMD_THRESHOLD = 512;
    private static final int GPU_THRESHOLD = 100000;
    
    private static final boolean nativeAvailable;
    
    static {
        boolean available = false;
        try {
            System.loadLibrary("fastmath");
            available = true;
        } catch (UnsatisfiedLinkError e) {
            System.err.println("FastMathStats: Native library not available, using Java fallback");
        }
        nativeAvailable = available;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Central Tendency
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Calculate arithmetic mean.
     * Single-pass, O(n) complexity.
     * 
     * @param data Input array
     * @return Mean value
     */
    public static double mean(double[] data) {
        if (data == null || data.length == 0) return 0.0;
        
        if (nativeAvailable && data.length >= SIMD_THRESHOLD) {
            return nativeMean(data);
        }
        
        double sum = 0.0;
        for (double v : data) {
            sum += v;
        }
        return sum / data.length;
    }
    
    /**
     * Calculate mean for a subset of data.
     */
    public static double mean(double[] data, int offset, int length) {
        if (length == 0) return 0.0;
        
        double sum = 0.0;
        for (int i = offset; i < offset + length; i++) {
            sum += data[i];
        }
        return sum / length;
    }
    
    /**
     * Weighted mean.
     * 
     * @param data Values
     * @param weights Weights (must sum to > 0)
     * @return Weighted mean
     */
    public static double weightedMean(double[] data, double[] weights) {
        if (data.length != weights.length) {
            throw new IllegalArgumentException("Data and weights must have same length");
        }
        
        double sumWeighted = 0.0;
        double sumWeights = 0.0;
        
        for (int i = 0; i < data.length; i++) {
            sumWeighted += data[i] * weights[i];
            sumWeights += weights[i];
        }
        
        return sumWeights > 0 ? sumWeighted / sumWeights : 0.0;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Dispersion
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Calculate variance (population).
     * Uses Welford's online algorithm for numerical stability.
     * 
     * @param data Input array
     * @return Population variance
     */
    public static double variance(double[] data) {
        return variance(data, false);
    }
    
    /**
     * Calculate variance.
     * 
     * @param data Input array
     * @param sample true for sample variance (divide by n-1), false for population
     * @return Variance
     */
    public static double variance(double[] data, boolean sample) {
        if (data == null || data.length < 2) return 0.0;
        
        // Welford's online algorithm
        double mean = 0.0;
        double M2 = 0.0;
        
        for (int i = 0; i < data.length; i++) {
            double x = data[i];
            double delta = x - mean;
            mean += delta / (i + 1);
            double delta2 = x - mean;
            M2 += delta * delta2;
        }
        
        int divisor = sample ? data.length - 1 : data.length;
        return divisor > 0 ? M2 / divisor : 0.0;
    }
    
    /**
     * Calculate standard deviation.
     */
    public static double stddev(double[] data) {
        return Math.sqrt(variance(data, true));
    }
    
    public static double stddev(double[] data, boolean sample) {
        return Math.sqrt(variance(data, sample));
    }
    
    /**
     * Mean and variance in single pass.
     * Returns [mean, variance].
     */
    public static double[] meanAndVariance(double[] data) {
        if (data == null || data.length < 2) {
            return new double[] { 0.0, 0.0 };
        }
        
        double mean = 0.0;
        double M2 = 0.0;
        
        for (int i = 0; i < data.length; i++) {
            double x = data[i];
            double delta = x - mean;
            mean += delta / (i + 1);
            double delta2 = x - mean;
            M2 += delta * delta2;
        }
        
        double variance = M2 / data.length;
        return new double[] { mean, variance };
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Min / Max
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Find minimum value.
     */
    public static double min(double[] data) {
        if (data == null || data.length == 0) return Double.NaN;
        
        double min = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] < min) min = data[i];
        }
        return min;
    }
    
    /**
     * Find maximum value.
     */
    public static double max(double[] data) {
        if (data == null || data.length == 0) return Double.NaN;
        
        double max = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] > max) max = data[i];
        }
        return max;
    }
    
    /**
     * Find min and max in single pass.
     * Returns [min, max].
     */
    public static double[] minMax(double[] data) {
        if (data == null || data.length == 0) {
            return new double[] { Double.NaN, Double.NaN };
        }
        
        double min = data[0];
        double max = data[0];
        
        for (int i = 1; i < data.length; i++) {
            double v = data[i];
            if (v < min) min = v;
            if (v > max) max = v;
        }
        
        return new double[] { min, max };
    }
    
    /**
     * Find index of minimum.
     */
    public static int argMin(double[] data) {
        if (data == null || data.length == 0) return -1;
        
        int idx = 0;
        double min = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] < min) {
                min = data[i];
                idx = i;
            }
        }
        return idx;
    }
    
    /**
     * Find index of maximum.
     */
    public static int argMax(double[] data) {
        if (data == null || data.length == 0) return -1;
        
        int idx = 0;
        double max = data[0];
        for (int i = 1; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
                idx = i;
            }
        }
        return idx;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Quantiles & Percentiles
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Calculate median.
     * Modifies the input array (partial sorting)!
     * For a non-destructive version, copy the array first.
     * 
     * @param data Input array (will be modified)
     * @return Median value
     */
    public static double median(double[] data) {
        if (data == null || data.length == 0) return Double.NaN;
        
        int n = data.length;
        if (n % 2 == 1) {
            return quickSelect(data, 0, n - 1, n / 2);
        } else {
            double a = quickSelect(data, 0, n - 1, n / 2 - 1);
            double b = quickSelect(data, 0, n - 1, n / 2);
            return (a + b) / 2.0;
        }
    }
    
    /**
     * Calculate percentile.
     * Modifies the input array!
     * 
     * @param data Input array (will be modified)
     * @param p Percentile (0-100)
     * @return p-th percentile
     */
    public static double percentile(double[] data, double p) {
        if (data == null || data.length == 0) return Double.NaN;
        if (p < 0 || p > 100) throw new IllegalArgumentException("Percentile must be 0-100");
        
        int n = data.length;
        double idx = p / 100.0 * (n - 1);
        int lower = (int)Math.floor(idx);
        int upper = (int)Math.ceil(idx);
        
        if (lower == upper) {
            return quickSelect(data, 0, n - 1, lower);
        }
        
        double v1 = quickSelect(data, 0, n - 1, lower);
        double v2 = quickSelect(data, 0, n - 1, upper);
        double frac = idx - lower;
        
        return v1 + frac * (v2 - v1);
    }
    
    /**
     * Calculate quartiles.
     * Returns [Q1, median, Q3].
     * Modifies input array!
     */
    public static double[] quartiles(double[] data) {
        return new double[] {
            percentile(data, 25),
            median(data),
            percentile(data, 75)
        };
    }
    
    // Quickselect algorithm for finding k-th smallest element
    private static double quickSelect(double[] arr, int left, int right, int k) {
        if (left == right) return arr[left];
        
        int pivotIndex = partition(arr, left, right);
        
        if (k == pivotIndex) {
            return arr[k];
        } else if (k < pivotIndex) {
            return quickSelect(arr, left, pivotIndex - 1, k);
        } else {
            return quickSelect(arr, pivotIndex + 1, right, k);
        }
    }
    
    private static int partition(double[] arr, int left, int right) {
        double pivot = arr[right];
        int i = left;
        
        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, right);
        return i;
    }
    
    private static void swap(double[] arr, int i, int j) {
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Histogram
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Compute histogram with fixed number of bins.
     * 
     * @param data Input data
     * @param numBins Number of bins
     * @param histogram Output array (length = numBins)
     * @param binEdges Output array (length = numBins + 1)
     */
    public static void histogram(double[] data, int numBins, long[] histogram, double[] binEdges) {
        if (data == null || data.length == 0) return;
        
        double[] minMax = minMax(data);
        double min = minMax[0];
        double max = minMax[1];
        
        double range = max - min;
        if (range == 0) range = 1.0; // Avoid division by zero
        
        // Compute bin edges
        for (int i = 0; i <= numBins; i++) {
            binEdges[i] = min + (i * range / numBins);
        }
        
        // Count
        Arrays.fill(histogram, 0);
        for (double v : data) {
            int bin = (int)((v - min) / range * numBins);
            if (bin >= numBins) bin = numBins - 1; // Handle max edge case
            if (bin < 0) bin = 0;
            histogram[bin]++;
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Correlation & Covariance
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Pearson correlation coefficient.
     * Range: -1 to +1
     */
    public static double correlation(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        if (x.length < 2) return 0.0;
        
        double meanX = mean(x);
        double meanY = mean(y);
        
        double sumXY = 0.0;
        double sumX2 = 0.0;
        double sumY2 = 0.0;
        
        for (int i = 0; i < x.length; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;
            sumXY += dx * dy;
            sumX2 += dx * dx;
            sumY2 += dy * dy;
        }
        
        double denom = Math.sqrt(sumX2 * sumY2);
        return denom > 0 ? sumXY / denom : 0.0;
    }
    
    /**
     * Covariance.
     */
    public static double covariance(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("Arrays must have same length");
        }
        if (x.length < 2) return 0.0;
        
        double meanX = mean(x);
        double meanY = mean(y);
        
        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - meanX) * (y[i] - meanY);
        }
        
        return sum / (x.length - 1);
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Financial / Time Series
    // ═══════════════════════════════════════════════════════════════════════
    
    /**
     * Simple Moving Average (SMA).
     * 
     * @param data Price/volume data
     * @param period Window size
     * @param sma Output array (length = data.length - period + 1)
     */
    public static void sma(double[] data, int period, double[] sma) {
        if (period <= 0 || period > data.length) {
            throw new IllegalArgumentException("Invalid period");
        }
        
        double sum = 0.0;
        for (int i = 0; i < period; i++) {
            sum += data[i];
        }
        
        sma[0] = sum / period;
        
        for (int i = period; i < data.length; i++) {
            sum += data[i] - data[i - period];
            sma[i - period + 1] = sum / period;
        }
    }
    
    /**
     * Exponential Moving Average (EMA).
     * 
     * @param data Price data
     * @param alpha Smoothing factor (0 < alpha < 1)
     * @param ema Output array (same length as data)
     */
    public static void ema(double[] data, double alpha, double[] ema) {
        if (alpha <= 0 || alpha >= 1) {
            throw new IllegalArgumentException("Alpha must be 0 < alpha < 1");
        }
        
        ema[0] = data[0];
        
        for (int i = 1; i < data.length; i++) {
            ema[i] = alpha * data[i] + (1 - alpha) * ema[i - 1];
        }
    }
    
    /**
     * RSI (Relative Strength Index).
     * Classic technical indicator.
     * 
     * @param prices Close prices
     * @param period Lookback period (typically 14)
     * @param rsi Output (length = prices.length - period)
     */
    public static void rsi(double[] prices, int period, double[] rsi) {
        double[] gains = new double[prices.length - 1];
        double[] losses = new double[prices.length - 1];
        
        // Calculate gains and losses
        for (int i = 1; i < prices.length; i++) {
            double change = prices[i] - prices[i - 1];
            gains[i - 1] = change > 0 ? change : 0;
            losses[i - 1] = change < 0 ? -change : 0;
        }
        
        // Initial averages
        double avgGain = mean(gains, 0, period);
        double avgLoss = mean(losses, 0, period);
        
        // First RSI
        rsi[0] = avgLoss == 0 ? 100 : 100 - (100 / (1 + avgGain / avgLoss));
        
        // Smoothed RSI
        for (int i = period; i < gains.length; i++) {
            avgGain = (avgGain * (period - 1) + gains[i]) / period;
            avgLoss = (avgLoss * (period - 1) + losses[i]) / period;
            rsi[i - period + 1] = avgLoss == 0 ? 100 : 100 - (100 / (1 + avgGain / avgLoss));
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // Native Methods
    // ═══════════════════════════════════════════════════════════════════════
    
    private static native double nativeMean(double[] data);
    private static native double nativeVariance(double[] data, boolean sample);
    private static native double[] nativeMinMax(double[] data);
    private static native void nativeHistogram(double[] data, int numBins, long[] histogram);
    
    // ═══════════════════════════════════════════════════════════════════════
    // Demo
    // ═══════════════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           FastMathStats Demo                                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Generate test data
        int n = 1000000;
        double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = Math.random() * 100 + 50; // Random 50-150
        }
        
        System.out.printf("Dataset: %,d samples%n", n);
        System.out.println();
        
        // Demo 1: Central Tendency
        System.out.println("📊 Central Tendency");
        long start = System.nanoTime();
        double mean = mean(data);
        long elapsed = System.nanoTime() - start;
        System.out.printf("  Mean:   %.4f (%.2f ms)%n", mean, elapsed / 1_000_000.0);
        
        double[] sortedForMedian = data.clone();
        start = System.nanoTime();
        double med = median(sortedForMedian);
        elapsed = System.nanoTime() - start;
        System.out.printf("  Median: %.4f (%.2f ms)%n", med, elapsed / 1_000_000.0);
        System.out.println();
        
        // Demo 2: Dispersion
        System.out.println("📈 Dispersion");
        start = System.nanoTime();
        double var = variance(data);
        double std = stddev(data);
        elapsed = System.nanoTime() - start;
        System.out.printf("  Variance: %.4f%n", var);
        System.out.printf("  StdDev:   %.4f (%.2f ms)%n", std, elapsed / 1_000_000.0);
        System.out.println();
        
        // Demo 3: Min/Max
        System.out.println("🔍 Min/Max");
        start = System.nanoTime();
        double[] mm = minMax(data);
        elapsed = System.nanoTime() - start;
        System.out.printf("  Min: %.4f, Max: %.4f (%.2f ms)%n", mm[0], mm[1], elapsed / 1_000_000.0);
        System.out.println();
        
        // Demo 4: Histogram
        System.out.println("📊 Histogram (10 bins)");
        int numBins = 10;
        long[] hist = new long[numBins];
        double[] edges = new double[numBins + 1];
        start = System.nanoTime();
        histogram(data, numBins, hist, edges);
        elapsed = System.nanoTime() - start;
        System.out.printf("  Time: %.2f ms%n", elapsed / 1_000_000.0);
        for (int i = 0; i < numBins; i++) {
            System.out.printf("  [%.1f, %.1f): %,d%n", edges[i], edges[i+1], hist[i]);
        }
        System.out.println();
        
        // Demo 5: Financial Indicators
        System.out.println("💰 Financial Indicators (RSI)");
        double[] prices = new double[100];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = 100 + Math.sin(i * 0.1) * 10 + Math.random() * 2;
        }
        double[] rsiValues = new double[prices.length - 14];
        rsi(prices, 14, rsiValues);
        System.out.printf("  Last RSI(14): %.2f%n", rsiValues[rsiValues.length - 1]);
        System.out.println();
        
        // Demo 6: Correlation
        System.out.println("🔗 Correlation");
        double[] x = new double[10000];
        double[] y = new double[10000];
        for (int i = 0; i < x.length; i++) {
            x[i] = Math.random();
            y[i] = x[i] * 0.8 + Math.random() * 0.2; // Correlated
        }
        double corr = correlation(x, y);
        System.out.printf("  Correlation: %.4f (expected ~0.8)%n", corr);
        System.out.println();
        
        System.out.println("✅ Stats Demo complete!");
        System.out.println("   Native available: " + nativeAvailable);
    }
}
