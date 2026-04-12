// FastMath OpenCL Kernels for GPU Batch Processing
// Compile at runtime for Intel/AMD/NVIDIA GPUs

// Square root kernel
__kernel void sqrtArray(__global const double* input,
                        __global double* output,
                        const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = sqrt(input[id]);
    }
}

// Sine kernel
__kernel void sinArray(__global const double* input,
                       __global double* output,
                       const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = sin(input[id]);
    }
}

// Cosine kernel
__kernel void cosArray(__global const double* input,
                       __global double* output,
                       const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = cos(input[id]);
    }
}

// Exponential kernel
__kernel void expArray(__global const double* input,
                       __global double* output,
                       const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = exp(input[id]);
    }
}

// Natural log kernel
__kernel void logArray(__global const double* input,
                       __global double* output,
                       const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = log(input[id]);
    }
}

// Power kernel (x^y for arrays)
__kernel void powArray(__global const double* base,
                       __global const double* exp,
                       __global double* output,
                       const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = pow(base[id], exp[id]);
    }
}

// Tangent kernel
__kernel void tanArray(__global const double* input,
                       __global double* output,
                       const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = tan(input[id]);
    }
}

// Hyperbolic sine kernel
__kernel void sinhArray(__global const double* input,
                        __global double* output,
                        const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = sinh(input[id]);
    }
}

// Hyperbolic cosine kernel
__kernel void coshArray(__global const double* input,
                        __global double* output,
                        const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = cosh(input[id]);
    }
}

// Log10 kernel
__kernel void log10Array(__global const double* input,
                         __global double* output,
                         const int len) {
    int id = get_global_id(0);
    if (id < len) {
        output[id] = log10(input[id]);
    }
}
