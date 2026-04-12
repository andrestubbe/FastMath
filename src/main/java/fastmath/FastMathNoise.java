package fastmath;

/**
 * Fast noise generation for games, AI, and simulation.
 * 
 * Implements:
 * - Perlin Noise (classic gradient noise)
 * - Simplex Noise (faster, less artifacts than Perlin)
 * - Worley Noise (cellular/Voronoi patterns)
 * - Fractal Brownian Motion (fBm) for multi-octave noise
 * 
 * SIMD-optimized batch generation via JNI.
 * Perfect for: terrain generation, procedural textures, 
 * AI movement, particle effects, water simulation.
 * 
 * @author FastMath Team
 */
public class FastMathNoise {
    
    private static final boolean NATIVE_AVAILABLE = FastMath.isNativeAvailable();
    
    // Permutation table for Perlin/Simplex noise
    private static final int[] PERM = new int[512];
    
    static {
        // Initialize permutation table (first 256 from reference implementation)
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) p[i] = i;
        // Shuffle with simple LCG
        long seed = 12345;
        for (int i = 255; i > 0; i--) {
            seed = (seed * 1103515245 + 12345) & 0x7fffffff;
            int j = (int)(seed % (i + 1));
            int temp = p[i]; p[i] = p[j]; p[j] = temp;
        }
        // Duplicate for overflow safety
        for (int i = 0; i < 512; i++) {
            PERM[i] = p[i & 255];
        }
    }
    
    /**
     * 2D Perlin noise. Returns value in range [-1, 1].
     */
    public static double perlin2D(double x, double y) {
        int X = (int)Math.floor(x) & 255;
        int Y = (int)Math.floor(y) & 255;
        
        x -= Math.floor(x);
        y -= Math.floor(y);
        
        double u = fade(x);
        double v = fade(y);
        
        int A = PERM[X] + Y;
        int B = PERM[X + 1] + Y;
        
        return lerp(v, 
            lerp(u, grad2D(PERM[A], x, y), grad2D(PERM[B], x - 1, y)),
            lerp(u, grad2D(PERM[A + 1], x, y - 1), grad2D(PERM[B + 1], x - 1, y - 1))
        );
    }
    
    /**
     * 3D Perlin noise. Returns value in range [-1, 1].
     */
    public static double perlin3D(double x, double y, double z) {
        int X = (int)Math.floor(x) & 255;
        int Y = (int)Math.floor(y) & 255;
        int Z = (int)Math.floor(z) & 255;
        
        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);
        
        double u = fade(x);
        double v = fade(y);
        double w = fade(z);
        
        int A = PERM[X] + Y;
        int AA = PERM[A] + Z;
        int AB = PERM[A + 1] + Z;
        int B = PERM[X + 1] + Y;
        int BA = PERM[B] + Z;
        int BB = PERM[B + 1] + Z;
        
        return lerp(w, 
            lerp(v, 
                lerp(u, grad3D(PERM[AA], x, y, z), grad3D(PERM[BA], x - 1, y, z)),
                lerp(u, grad3D(PERM[AB], x, y - 1, z), grad3D(PERM[BB], x - 1, y - 1, z))
            ),
            lerp(v,
                lerp(u, grad3D(PERM[AA + 1], x, y, z - 1), grad3D(PERM[BA + 1], x - 1, y, z - 1)),
                lerp(u, grad3D(PERM[AB + 1], x, y - 1, z - 1), grad3D(PERM[BB + 1], x - 1, y - 1, z - 1))
            )
        );
    }
    
    /**
     * 2D Simplex noise. Faster and less directional artifacts than Perlin.
     * Returns value in range [-1, 1].
     */
    public static double simplex2D(double xin, double yin) {
        final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
        final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
        
        double n0, n1, n2;
        
        double s = (xin + yin) * F2;
        int i = (int)Math.floor(xin + s);
        int j = (int)Math.floor(yin + s);
        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;
        
        int i1, j1;
        if (x0 > y0) { i1 = 1; j1 = 0; }
        else { i1 = 0; j1 = 1; }
        
        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;
        
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = PERM[ii + PERM[jj]] % 12;
        int gi1 = PERM[ii + i1 + PERM[jj + j1]] % 12;
        int gi2 = PERM[ii + 1 + PERM[jj + 1]] % 12;
        
        double t0 = 0.5 - x0*x0 - y0*y0;
        if (t0 < 0) n0 = 0.0;
        else {
            t0 *= t0;
            n0 = t0 * t0 * dot2D(gi0, x0, y0);
        }
        
        double t1 = 0.5 - x1*x1 - y1*y1;
        if (t1 < 0) n1 = 0.0;
        else {
            t1 *= t1;
            n1 = t1 * t1 * dot2D(gi1, x1, y1);
        }
        
        double t2 = 0.5 - x2*x2 - y2*y2;
        if (t2 < 0) n2 = 0.0;
        else {
            t2 *= t2;
            n2 = t2 * t2 * dot2D(gi2, x2, y2);
        }
        
        return 70.0 * (n0 + n1 + n2);
    }
    
    /**
     * Worley noise (cellular/Voronoi noise).
     * Returns distance to nearest feature point.
     */
    public static double worley2D(double x, double y) {
        int xInt = (int)Math.floor(x);
        int yInt = (int)Math.floor(y);
        
        double minDist = Double.MAX_VALUE;
        
        // Check neighboring cells
        for (int xCur = xInt - 1; xCur <= xInt + 1; xCur++) {
            for (int yCur = yInt - 1; yCur <= yInt + 1; yCur++) {
                double[] feature = randomFeaturePoint(xCur, yCur);
                double dx = x - (xCur + feature[0]);
                double dy = y - (yCur + feature[1]);
                double dist = Math.sqrt(dx*dx + dy*dy);
                if (dist < minDist) minDist = dist;
            }
        }
        
        return minDist;
    }
    
    /**
     * Fractal Brownian Motion - multi-octave noise.
     * Combines multiple noise octaves for detail.
     * 
     * @param octaves Number of noise layers
     * @param lacunarity Frequency multiplier per octave (typically 2.0)
     * @param gain Amplitude multiplier per octave (typically 0.5)
     */
    public static double fBm2D(double x, double y, int octaves, 
                               double lacunarity, double gain) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double maxValue = 0;
        
        for (int i = 0; i < octaves; i++) {
            total += simplex2D(x * frequency, y * frequency) * amplitude;
            maxValue += amplitude;
            amplitude *= gain;
            frequency *= lacunarity;
        }
        
        return total / maxValue;
    }
    
    /**
     * Generate noise texture/batch.
     * Fills output array with noise values.
     * Uses JNI SIMD if available for large grids.
     */
    public static void perlinGrid(double[] output, int width, int height, 
                                  double scale, double offsetX, double offsetY) {
        if (NATIVE_AVAILABLE && width * height >= 1000) {
            nativePerlinGrid(output, width, height, scale, offsetX, offsetY);
        } else {
            // Pure Java fallback
            int idx = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    output[idx++] = perlin2D(
                        (x + offsetX) * scale, 
                        (y + offsetY) * scale
                    );
                }
            }
        }
    }
    
    /**
     * Ridged multifractal - good for terrain (mountains, etc).
     */
    public static double ridgedMF2D(double x, double y, int octaves, 
                                    double lacunarity, double gain) {
        double total = 0;
        double frequency = 1;
        double amplitude = 0.5;
        double prev = 1.0;
        
        for (int i = 0; i < octaves; i++) {
            double n = simplex2D(x * frequency, y * frequency);
            n = 1.0 - Math.abs(n);  // Create ridges
            n = n * n;              // Sharpen
            total += n * amplitude * prev;
            prev = n;
            frequency *= lacunarity;
            amplitude *= gain;
        }
        
        return total;
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
    
    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
    
    private static double grad2D(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
    
    private static double grad3D(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
    
    private static double dot2D(int gi, double x, double y) {
        switch (gi) {
            case 0: return x + y;
            case 1: return -x + y;
            case 2: return x - y;
            case 3: return -x - y;
            case 4: return x;
            case 5: return -x;
            case 6: return y;
            case 7: return -y;
            default: return 0;
        }
    }
    
    private static double[] randomFeaturePoint(int x, int y) {
        // Hash cell coordinates to get deterministic feature point
        long n = x * 374761393L + y * 668265263L;
        n = (n ^ (n >> 13)) * 1274126177L;
        n = n ^ (n >> 16);
        
        double[] point = new double[2];
        point[0] = ((n & 0x7fffffff) / (double)0x7fffffff);
        n = (n * 16807) & 0x7fffffff;
        point[1] = (n / (double)0x7fffffff);
        return point;
    }
    
    // Native method declarations
    private static native void nativePerlinGrid(double[] output, int width, int height,
                                                double scale, double offsetX, double offsetY);
}
