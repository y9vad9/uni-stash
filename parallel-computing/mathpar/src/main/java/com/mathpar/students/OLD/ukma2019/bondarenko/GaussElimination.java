package com.mathpar.students.OLD.ukma2019.bondarenko;

import org.jocl.*;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

import static org.jocl.CL.*;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;

public class GaussElimination {
    private static String path = "src\\main\\java\\com\\mathpar\\students\\student\\bondarenko\\";
    private float[] matrixAB;
    private int size;

    private cl_context context;
    private cl_command_queue commandQueue;
    private cl_program program;

    private GaussElimination(String dataSource) {
        readDataSource(dataSource);
    }

    private void readDataSource(String dataSource) {
        try
        {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(dataSource)));

            size = Integer.valueOf(br.readLine());
            matrixAB = new float[size * (size + 1)];
            for (int i = 0; i < size; i++) {
                String line;
                try {
                    line = br.readLine().replace(" ", "");
                } catch (NullPointerException e) {
                    System.out.println(i);
                    throw new RuntimeException(e);
                }

                int j = 0;
                for (int start = 0; start < line.length();) {
                    int end = start;
                    while (end < line.length() && !Objects.equals(String.valueOf(line.charAt(end)), ",")) {
                        end++;
                    }
                    matrixAB[i * (size + 1) + j] = Float.valueOf(line.substring(start, end));
                    j++;
                    start = end + 1;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String readKernelStr(String kernelPath) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(kernelPath)));
            String line = br.readLine();
            String source = "";
            while (line != null) {
                source += line.replace("\n", "");
                line = br.readLine();
            }
            return source;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return "";
    }

    private void initCL(String kernelPath, long deviceType) {
        final int platformIndex = 0;
        final int deviceIndex = 0;

        String source = readKernelStr(kernelPath);

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain matrixAB platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain matrixAB device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create matrixAB context for the selected device
        context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);
        commandQueue = clCreateCommandQueueWithProperties(context, device, null, null);

        program = clCreateProgramWithSource(
                context,
                1,
                new String[]{ source },
                null,
                null);
        clBuildProgram(program, 0, null, "-DN=" + String.valueOf(size), null, null);
    }

    private float[] elimination() {
        float[] result = new float[size];
        int i_pivot = 0;
        while (i_pivot < size) {
            if (matrixAB[i_pivot * (size + 1) + i_pivot] == 0) {
                while (i_pivot < size && matrixAB[i_pivot * (size + 1) + i_pivot] == 0) {
                    i_pivot++;
                }
                if (i_pivot == size) {
                    return result;
                }
            }
            for (int i = 0; i < size; i++) {
                if (i == i_pivot) {
                    continue;
                }
                float multiplier = matrixAB[i * (size + 1) + i_pivot] / matrixAB[i_pivot * (size + 1) + i_pivot];
                for (int j = 0; j < size + 1; j++) {
                    if (j == i_pivot) {
                        matrixAB[i * (size + 1) + j] = 0f;
                    } else {
                        matrixAB[i * (size + 1) + j] = matrixAB[i * (size + 1) + j] - multiplier * matrixAB[i_pivot * (size + 1) + j];
                    }
                }
            }
            i_pivot++;
        }

        for (int i = 0; i < size; i++) {
            result[i] = matrixAB[i * (size + 1) + size] / matrixAB[i * (size + 1) + i];
        }
        return result;
    }

    /**
     * Parallel implementation of classic Gauss elimination algorithm
     *
     * 1. Initialize OpenCL (load kernels, build program, connect to execution device)
     * 2. Create kernels and copy data to CPU/GPU memory registry
     * 3. Start computing:
     *      3.1 Direct walk. For all rows from 0 to N:
     *          1) Find pivot row. On one kernel iterate throw a column until the non
     *          zero element is found. If swap is need to be done, copy whose matrix
     *          to local kernel memory, swap rows and copy result to global memory.
     *          2) Eliminate rows from pivot + 1 to N and update matrix. Operation is
     *          done on all free kernels.
     *      3.2 Reverse walk. Compute solution on one kernel from N to 0 row using
     *      previously found solutions.
     * 4. Copy solution from CPU/GPU registry memory and print.
     */
    private float[] parallelElimination(long deviceType) {
        long tinit =  System.currentTimeMillis();

        initCL(path  + "kernelGaussElimination.cl", deviceType);
        // Create the kernels
        cl_kernel swapKernel = clCreateKernel(program, "swap", null);
        cl_kernel directKernel = clCreateKernel(program, "direct", null);
        cl_kernel reverseKernel = clCreateKernel(program, "reverse", null);

        cl_mem memObject = clCreateBuffer(
                context,
                CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * matrixAB.length,
                Pointer.to(matrixAB),
                null);

        float[] solution = new float[size];

        cl_mem memSolutionObject = clCreateBuffer(
                context,
                CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * solution.length,
                Pointer.to(solution),
                null);

        long tdirect =  System.currentTimeMillis();
        long tmemory_read = 0;
        long tmemory_write = 0;
        long tkernel_swap = 0;
        long tkernel_direct = 0;
        long tkernel_reverse = 0;

        long t_start, t_end;
        long[] global_work_size;

        // Direct walk
        for(int i=0; i<(size - 1); i++){
            clSetKernelArg(swapKernel, 0, Sizeof.cl_mem, Pointer.to(memObject));
            clSetKernelArg(swapKernel, 1, Sizeof.cl_int, Pointer.to(new int[]{i}));

            // find pivot row and do swap if needed
            t_start = System.currentTimeMillis();
            clEnqueueNDRangeKernel(
                    commandQueue,
                    swapKernel,
                    1,
                    null,
                    new long[]{1},
                    new long[]{1},
                    0,
                    null,
                    null);
            t_end = System.currentTimeMillis();
            tkernel_swap += t_end - t_start;


            clSetKernelArg(directKernel, 0, Sizeof.cl_mem, Pointer.to(memObject));
            clSetKernelArg(directKernel, 1, Sizeof.cl_int, Pointer.to(new int[]{i}));

            global_work_size = new long[]{size - i - 1};

            // elimination step
            t_start = System.currentTimeMillis();
            clEnqueueNDRangeKernel(
                    commandQueue,
                    directKernel,
                    global_work_size.length,
                    null,
                    global_work_size,
                    null,
                    0,
                    null,
                    null);
            t_end = System.currentTimeMillis();
            tkernel_direct += t_end - t_start;
        }

        long treverse =  System.currentTimeMillis();

        // reverse walk
        for(int i=size - 1; i >= 0; i--){
            clSetKernelArg(reverseKernel, 0, Sizeof.cl_mem, Pointer.to(memObject));
            clSetKernelArg(reverseKernel, 1, Sizeof.cl_int, Pointer.to(new int[]{i}));
            clSetKernelArg(reverseKernel, 2, Sizeof.cl_mem, Pointer.to(memSolutionObject));

            t_start = System.currentTimeMillis();
            clEnqueueNDRangeKernel(
                    commandQueue,
                    reverseKernel,
                    1,
                    null,
                    new long[]{size},
                    null,
                    0,
                    null,
                    null);
            t_end = System.currentTimeMillis();
            tkernel_reverse += t_end - t_start;
        }

        t_start = System.currentTimeMillis();
        clEnqueueReadBuffer(
                commandQueue,
                memSolutionObject,
                CL_TRUE,
                0,
                solution.length * Sizeof.cl_float,
                Pointer.to(solution),
                0,
                null,
                null);
        t_end = System.currentTimeMillis();
        tmemory_read += t_end - t_start;

        System.out.println("init time: " + (tdirect - tinit));
        System.out.println("total kernel time: " + (tkernel_swap + tkernel_direct + tkernel_reverse) +
                " swap: " + tkernel_swap + " direct: " + tkernel_direct + " reverse: " + tkernel_reverse);
        System.out.println("total memory buffer time: " + (tmemory_read + tmemory_write) +
                " read: " + tmemory_read + " write: " + tmemory_write);

        releaseAll();

        return solution;
    }

    private float[] kholetsky() {
        float[][] lowerTriangular = new float[this.size][this.size];
        float[][] upperTriangular = new float[this.size][this.size];
        float[] y = new float[size];
        float[] x = new float[size];

        for (int i_row = 0; i_row < size; i_row++) {
            // compute L
            for (int j_col = 0; j_col < i_row + 1; j_col++) {
                float sumLU = 0f;
                for (int k = 0; k < j_col; k++) {
                    sumLU += lowerTriangular[i_row][k] * upperTriangular[k][j_col];
                }
                lowerTriangular[i_row][j_col] = matrixAB[i_row * (this.size + 1) + j_col] - sumLU;
            }
            // compute U
            upperTriangular[i_row][i_row] = 1.0f;
            for (int j_col = i_row + 1; j_col < size; j_col++) {
                float sumLU = 0f;
                for(int k = 0; k < i_row; k++) {
                    sumLU += lowerTriangular[i_row][k] * upperTriangular[k][j_col];
                }
                upperTriangular[i_row][j_col] = (matrixAB[i_row * (this.size + 1) + j_col] - sumLU) / lowerTriangular[i_row][i_row];
            }
            float sumLy = 0f;
            for (int k = 0; k < i_row; k++) {
                sumLy += lowerTriangular[i_row][k] * y[k];
            }
            y[i_row] = (matrixAB[i_row * (this.size + 1) + size] - sumLy) / lowerTriangular[i_row][i_row];

        }

        for (int i = size - 1; i >= 0; i--) {
            float sumUx = 0f;
            for (int k = i + 1; k < size; k++) {
                sumUx += upperTriangular[i][k] * x[k];
            }
            x[i] = y[i] - sumUx;
        }
        return x;
    }

    private void releaseAll() {
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
    }


    public static void main(String... args) {
        String file = "matrix.txt";

        GaussElimination gauss = new GaussElimination(path + file);
        long start =  System.currentTimeMillis();
        float[] res = gauss.elimination();
        long end =  System.currentTimeMillis();
        System.out.println("Gauss: " + (end - start) + " ms");
        System.out.println(Arrays.toString(res));

        gauss = new GaussElimination(path + file);
        start =  System.currentTimeMillis();
        res = gauss.kholetsky();
        end =  System.currentTimeMillis();
        System.out.println("Kholetsky: " + (end - start) + " ms");
        System.out.println(Arrays.toString(res));

        gauss = new GaussElimination(path + file);
        start =  System.currentTimeMillis();
        res = gauss.parallelElimination(CL_DEVICE_TYPE_CPU);
        end =  System.currentTimeMillis();
        System.out.println("Parallel Gauss CPU: " + (end - start) + " ms");
        System.out.println(Arrays.toString(res));

        gauss = new GaussElimination(path + file);
        start =  System.currentTimeMillis();
        res = gauss.parallelElimination(CL_DEVICE_TYPE_GPU);
        end =  System.currentTimeMillis();
        System.out.println("Parallel Gauss GPU: " + (end - start) + " ms");
        System.out.println(Arrays.toString(res));
    }
}
