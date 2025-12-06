//package com.mathpar.students.babych;/*
// * JCusolver - Java bindings for CUSOLVER, the NVIDIA CUDA solver
// * library, to be used with JCuda
// *
// * Copyright (c) 2010-2015 Marco Hutter - http://www.jcuda.org
// */
//
//import de.javagl.matrixmarketreader.CSR;
//import de.javagl.matrixmarketreader.MatrixMarketCSR;
//import jcuda.Pointer;
//import jcuda.Sizeof;
//import jcuda.jcusolver.JCusolver;
//import jcuda.jcusolver.cusolverSpHandle;
//import jcuda.jcusparse.JCusparse;
//import jcuda.jcusparse.cusparseHandle;
//import jcuda.jcusparse.cusparseMatDescr;
//import jcuda.runtime.JCuda;
//import jcuda.runtime.cudaStream_t;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//import static jcuda.jcusolver.JCusolverSp.*;
//import static jcuda.jcusparse.JCusparse.*;
//import static jcuda.jcusparse.cusparseIndexBase.CUSPARSE_INDEX_BASE_ONE;
//import static jcuda.jcusparse.cusparseIndexBase.CUSPARSE_INDEX_BASE_ZERO;
//import static jcuda.jcusparse.cusparseMatrixType.CUSPARSE_MATRIX_TYPE_GENERAL;
//import static jcuda.jcusparse.cusparseOperation.CUSPARSE_OPERATION_NON_TRANSPOSE;
//import static jcuda.runtime.JCuda.*;
//import static jcuda.runtime.cudaMemcpyKind.*;
//
//public class sp
//{
//    public static void main(String[] args)
//    {
//        String testFunc = "qr";
//        /** Decomposition type:
//         * chol - Cholesky
//         * lu - LU
//         * qr - QR
//         * null - всі
//          */
//
//        /**
//         * Matrix reorder to reduce the number of non-zeros:
//         * symrcm - symmetric multiple minimum degree - http://old.exponenta.ru/soft/matlab/potemkin/book2/chapter9/symrcm.asp
//         * symamd (better) - symmetric approximate multiple minimum degree - http://old.exponenta.ru/soft/matlab/potemkin/book2/chapter9/symmmd.asp
//         * null - не впорядковуват
//         */
//        String reorderType = "null";
//
//        JCuda.setExceptionsEnabled(true);
//        JCusparse.setExceptionsEnabled(true);
//        JCusolver.setExceptionsEnabled(true);
//
//        cusolverSpHandle handle = new cusolverSpHandle();
//        cusparseHandle cusparseHandle = new cusparseHandle();
//        cudaStream_t stream = new cudaStream_t();
//        cusparseMatDescr descrA = new cusparseMatDescr();
//
//        int rowsA; // number of rows of A
//        int colsA; // number of columns of A
//        int nnzA; // number of nonzeros of A
//        int baseA; // base index in CSR format
//
//        // CSR(A) from I/O
//        int[] h_csrRowPtrA, h_csrColIndA;
//        double[] h_csrValA;
//
//        double[] h_x; // x = A \ b
//        double[] h_b; // b = ones(m,1)
//        double[] h_r; // r = b - A*x
//
//        int[] h_Q; // <int> n
//        // Q = symrcm(A) or Q = symamd(A)
//        // B = Q*A*Q^T
//        int[] h_csrRowPtrB; // <int> n+1
//        int[] h_csrColIndB; // <int> nnzA
//        double[] h_csrValB; // <double> nnzA
//        int[] h_mapBfromA;  // <int> nnzA
//
//        long size_perm;
//        byte[] buffer_cpu; // working space for permutation: B = Q*A*Q^T
//
//        Pointer d_csrRowPtrA = new Pointer(); // int
//        Pointer d_csrColIndA = new Pointer(); // int
//        Pointer d_csrValA = new Pointer(); // double
//        Pointer d_x = new Pointer(); // x = A \ b // double
//        Pointer d_b = new Pointer(); // a copy of h_b // double
//        Pointer d_r = new Pointer(); // r = b - A*x // double
//
//        double tol = 1.e-12;
//        int reorder = 0; // no reordering
//        int singularity; // -1 if A is invertible under tol.
//
//        // the constants are used in residual evaluation, r = b - A*x
//        double minus_one = -1.0;
//        double one = 1.0;
//
//        double x_inf;
//        double r_inf;
//        double A_inf;
//        int issym;
//
//        long start, stop;
//        double time_solve_cpu;
//        double time_solve_gpu;
//
//        CSR csr;
//        try
//        {
//            csr = MatrixMarketCSR.readCSR(
//                    new FileInputStream("lap3D_7pt_n20.mtx"));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            return;
//        }
//        rowsA = csr.numRows;
//        colsA = csr.numCols;
//        nnzA = csr.values.length;
//        h_csrValA = csr.values;
//        h_csrRowPtrA = csr.rowPointers;
//        h_csrColIndA = csr.columnIndices;
//
//        baseA = h_csrRowPtrA[0]; // baseA = {0,1}
//        System.out.printf("sparse matrix A is %d x %d with %d nonzeros, base=%d\n",
//                rowsA, colsA, nnzA, baseA);
//
//        if ( rowsA != colsA )
//        {
//            System.err.println("Error: only support square matrix");
//            return;
//        }
//
//        cusolverSpCreate(handle);
//        cusparseCreate(cusparseHandle);
//
//        cudaStreamCreate(stream);
//        cusolverSpSetStream(handle, stream);
//
//        cusparseSetStream(cusparseHandle, stream);
//
//        cusparseCreateMatDescr(descrA);
//
//        cusparseSetMatType(descrA, CUSPARSE_MATRIX_TYPE_GENERAL);
//
//        if (baseA != 0)
//        {
//            cusparseSetMatIndexBase(descrA, CUSPARSE_INDEX_BASE_ONE);
//        }
//        else
//        {
//            cusparseSetMatIndexBase(descrA, CUSPARSE_INDEX_BASE_ZERO);
//        }
//
//        h_x = new double[colsA];
//        h_b = new double[rowsA];
//        h_r = new double[rowsA];
//
//        cudaMalloc(d_csrRowPtrA, Sizeof.INT*(rowsA+1));
//        cudaMalloc(d_csrColIndA, Sizeof.INT*nnzA);
//        cudaMalloc(d_csrValA   , Sizeof.DOUBLE*nnzA);
//        cudaMalloc(d_x, Sizeof.DOUBLE*colsA);
//        cudaMalloc(d_b, Sizeof.DOUBLE*rowsA);
//        cudaMalloc(d_r, Sizeof.DOUBLE*rowsA);
//
//        // verify if A has symmetric pattern or not
//        int[] issymArray = new int[]{-1};
//        cusolverSpXcsrissymHost(
//                handle, rowsA, nnzA, descrA, Pointer.to(h_csrRowPtrA),
//                Pointer.to(h_csrRowPtrA).withByteOffset(Sizeof.INT),
//                Pointer.to(h_csrColIndA), Pointer.to(issymArray));
//        issym = issymArray[0];
//        if (testFunc.equals("chol"))
//        {
//            if (issym != 1)
//            {
//                System.err.print("Error: A has no symmetric pattern, please use LU or QR \n");
//                return;
//            }
//        }
//
//        System.out.print("step 2: reorder the matrix A to minimize zero fill-in\n");
//        System.out.print("        if the user choose a reordering by symrcm or symamd\n");
//        System.out.print("        The reordering will overwrite A such that \n");
//        System.out.print("            A := A(Q,Q) where Q = symrcm(A) or Q = symamd(A)\n");
//        if (reorderType != null && !reorderType.equals("null"))
//        {
//            h_Q          = new int[colsA];
//            h_csrRowPtrB = new int[rowsA+1];
//            h_csrColIndB = new int[nnzA];
//            h_csrValB    = new double[nnzA];
//            h_mapBfromA  = new int[nnzA];
//
//            if (reorderType.equalsIgnoreCase("symrcm"))
//            {
//                cusolverSpXcsrsymrcmHost(
//                        handle, rowsA, nnzA,
//                        descrA, Pointer.to(h_csrRowPtrA), Pointer.to(h_csrColIndA),
//                        Pointer.to(h_Q));
//            }
//            else if (reorderType.equalsIgnoreCase("symamd"))
//            {
//                cusolverSpXcsrsymamdHost(
//                        handle, rowsA, nnzA,
//                        descrA, Pointer.to(h_csrRowPtrA), Pointer.to(h_csrColIndA),
//                        Pointer.to(h_Q));
//            }
//            else
//            {
//                System.out.printf("Error: %s is unknown reordering\n", reorderType);
//                return;
//            }
//
//            // B = Q*A*Q^T
//            memcpy(h_csrRowPtrB, h_csrRowPtrA, rowsA+1);
//            memcpy(h_csrColIndB, h_csrColIndA, nnzA);
//
//            long[] size_permArray = {-1};
//            cusolverSpXcsrperm_bufferSizeHost(
//                    handle, rowsA, colsA, nnzA,
//                    descrA, Pointer.to(h_csrRowPtrB), Pointer.to(h_csrColIndB),
//                    Pointer.to(h_Q), Pointer.to(h_Q),
//                    size_permArray);
//            size_perm = size_permArray[0];
//
//            buffer_cpu = new byte[(int)size_perm];
//
//            // h_mapBfromA = Identity
//            for(int j = 0 ; j < nnzA ; j++)
//            {
//                h_mapBfromA[j] = j;
//            }
//            cusolverSpXcsrpermHost(
//                    handle, rowsA, colsA, nnzA,
//                    descrA, Pointer.to(h_csrRowPtrB), Pointer.to(h_csrColIndB),
//                    Pointer.to(h_Q), Pointer.to(h_Q),
//                    Pointer.to(h_mapBfromA),
//                    Pointer.to(buffer_cpu));
//
//            // B = A( mapBfromA )
//            for(int j = 0 ; j < nnzA ; j++)
//            {
//                h_csrValB[j] = h_csrValA[ h_mapBfromA[j] ];
//            }
//
//            // A := B
//            memcpy(h_csrRowPtrA, h_csrRowPtrB, rowsA+1);
//            memcpy(h_csrColIndA, h_csrColIndB, nnzA);
//            memcpy(h_csrValA   , h_csrValB   , nnzA);
//        }
//
//        System.out.print("step 2: set right hand side vector (b) to 1\n");
//        for(int row = 0 ; row < rowsA ; row++)
//        {
//            h_b[row] = 1.0;
//        }
//
//
//
//        System.out.print("step 3: prepare data on device\n");
//        cudaMemcpy(d_csrRowPtrA, Pointer.to(h_csrRowPtrA), Sizeof.INT*(rowsA+1), cudaMemcpyHostToDevice);
//        cudaMemcpy(d_csrColIndA, Pointer.to(h_csrColIndA), Sizeof.INT*nnzA     , cudaMemcpyHostToDevice);
//        cudaMemcpy(d_csrValA   , Pointer.to(h_csrValA)   , Sizeof.DOUBLE*nnzA  , cudaMemcpyHostToDevice);
//        cudaMemcpy(d_b, Pointer.to(h_b), Sizeof.DOUBLE*rowsA, cudaMemcpyHostToDevice);
//
//        System.out.print("step 4: solve A*x = b on CPU\n");
//        // A and b are read-only
//        start = System.nanoTime();
//
//        int[] singularityArray = {-1};
//        if (testFunc.equals("chol"))
//        {
//            cusolverSpDcsrlsvcholHost(
//                    handle, rowsA, nnzA,
//                    descrA, Pointer.to(h_csrValA), Pointer.to(h_csrRowPtrA), Pointer.to(h_csrColIndA),
//                    Pointer.to(h_b), tol, reorder, Pointer.to(h_x), singularityArray);
//        }
//        else if (testFunc.equals("lu"))
//        {
//            cusolverSpDcsrlsvluHost(
//                    handle, rowsA, nnzA,
//                    descrA, Pointer.to(h_csrValA), Pointer.to(h_csrRowPtrA), Pointer.to(h_csrColIndA),
//                    Pointer.to(h_b), tol, reorder, Pointer.to(h_x), singularityArray);
//
//        }
//        else if (testFunc.equals("qr"))
//        {
//            cusolverSpDcsrlsvqrHost(
//                    handle, rowsA, nnzA,
//                    descrA, Pointer.to(h_csrValA), Pointer.to(h_csrRowPtrA), Pointer.to(h_csrColIndA),
//                    Pointer.to(h_b), tol, reorder, Pointer.to(h_x), singularityArray);
//
//        }
//        else
//        {
//            System.out.printf("Error: %s is unknown function\n", testFunc);
//            return;
//        }
//        stop = System.nanoTime();
//
//        time_solve_cpu = (stop - start) / 1e9;
//
//        singularity = singularityArray[0];
//        if (0 <= singularity)
//        {
//            System.out.printf("WARNING: the matrix is singular at row %d under tol (%E)\n", singularity, tol);
//        }
//
//        System.out.print("step 5: evaluate residual r = b - A*x (result on CPU)\n");
//        cudaMemcpy(d_r, Pointer.to(h_b), Sizeof.DOUBLE*rowsA, cudaMemcpyHostToDevice);
//        cudaMemcpy(d_x, Pointer.to(h_x), Sizeof.DOUBLE*colsA, cudaMemcpyHostToDevice);
//        cusparseDcsrmv(cusparseHandle,
//                CUSPARSE_OPERATION_NON_TRANSPOSE,
//                rowsA,
//                colsA,
//                nnzA,
//                Pointer.to(new double[] { minus_one }),
//                descrA,
//                d_csrValA,
//                d_csrRowPtrA,
//                d_csrColIndA,
//                d_x,
//                Pointer.to(new double[] { one }),
//                d_r);
//        cudaMemcpy(Pointer.to(h_r), d_r, Sizeof.DOUBLE*rowsA, cudaMemcpyDeviceToHost);
//
//        x_inf = vec_norminf(colsA, h_x);
//        r_inf = vec_norminf(rowsA, h_r);
//        A_inf = csr_mat_norminf(rowsA, descrA, h_csrValA, h_csrRowPtrA);
//
//        System.out.printf("(CPU) |b - A*x| = %E \n", r_inf);
//        System.out.printf("(CPU) |A| = %E \n", A_inf);
//        System.out.printf("(CPU) |x| = %E \n", x_inf);
//        System.out.printf("(CPU) |b - A*x|/(|A|*|x|) = %E \n", r_inf/(A_inf * x_inf));
//
//        System.out.print("step 6: solve A*x = b on GPU\n");
//        // d_A and d_b are read-only
//        start = System.nanoTime();
//
//        if (testFunc.equals("chol") )
//        {
//            cusolverSpDcsrlsvchol(
//                    handle, rowsA, nnzA,
//                    descrA, d_csrValA, d_csrRowPtrA, d_csrColIndA,
//                    d_b, tol, reorder, d_x, singularityArray);
//
//        }
//        else if (testFunc.equals("lu"))
//        {
//            System.out.print("WARNING: no LU available on GPU \n");
//        }
//        else if (testFunc.equals("qr") )
//        {
//            cusolverSpDcsrlsvqr(
//                    handle, rowsA, nnzA,
//                    descrA, d_csrValA, d_csrRowPtrA, d_csrColIndA,
//                    d_b, tol, reorder, d_x, singularityArray);
//        }
//        else
//        {
//            System.out.printf("Error: %s is unknow function\n", testFunc);
//            return ;
//        }
//        cudaDeviceSynchronize();
//        stop = System.nanoTime();
//
//        time_solve_gpu = (stop - start) / 1e9;
//
//        singularity = singularityArray[0];
//        if (0 <= singularity)
//        {
//            System.out.printf("WARNING: the matrix is singular at row %d under tol (%E)\n", singularity, tol);
//        }
//
//
//        System.out.print("step 7: evaluate residual r = b - A*x (result on GPU)\n");
//        cudaMemcpy(d_r, d_b, Sizeof.DOUBLE*rowsA, cudaMemcpyDeviceToDevice);
//
//        cusparseDcsrmv(cusparseHandle,
//                CUSPARSE_OPERATION_NON_TRANSPOSE,
//                rowsA,
//                colsA,
//                nnzA,
//                Pointer.to(new double[] { minus_one }),
//                descrA,
//                d_csrValA,
//                d_csrRowPtrA,
//                d_csrColIndA,
//                d_x,
//                Pointer.to(new double[] { one }),
//                d_r);
//
//        cudaMemcpy(Pointer.to(h_x), d_x, Sizeof.DOUBLE*colsA, cudaMemcpyDeviceToHost);
//        cudaMemcpy(Pointer.to(h_r), d_r, Sizeof.DOUBLE*rowsA, cudaMemcpyDeviceToHost);
//
//        x_inf = vec_norminf(colsA, h_x);
//        r_inf = vec_norminf(rowsA, h_r);
//
//        if (!testFunc.equals("lu"))
//        {
//            // only cholesky and qr have GPU version
//            System.out.printf("(GPU) |b - A*x| = %E \n", r_inf);
//            System.out.printf("(GPU) |A| = %E \n", A_inf);
//            System.out.printf("(GPU) |x| = %E \n", x_inf);
//            System.out.printf("(GPU) |b - A*x|/(|A|*|x|) = %E \n", r_inf/(A_inf * x_inf));
//        }
//
//        System.out.printf("timing %s: CPU = %10.6f sec , GPU = %10.6f sec\n",
//                testFunc, time_solve_cpu, time_solve_gpu);
//        cudaDeviceReset();
//
//    }
//
//    private static void memcpy(int[] dst, int[] src, int n)
//    {
//        System.arraycopy(src, 0, dst, 0, n);
//    }
//    private static void memcpy(double[] dst, double[] src, int n)
//    {
//        System.arraycopy(src, 0, dst, 0, n);
//    }
//
//    private static double vec_norminf(int n, double[] x) {
//        double norminf = 0;
//        for (int j = 0; j < n; j++)
//        {
//            double x_abs = Math.abs(x[j]);
//            norminf = (norminf > x_abs) ? norminf : x_abs;
//        }
//        return norminf;
//    }
//
//    private static double csr_mat_norminf(int m, cusparseMatDescr descrA, double[] csrValA, int[] csrRowPtrA) {
//        int baseA = (CUSPARSE_INDEX_BASE_ONE ==
//            cusparseGetMatIndexBase(descrA))? 1:0;
//        double norminf = 0;
//        for (int i = 0; i < m; i++)
//        {
//            double sum = 0.0;
//            int start = csrRowPtrA[i] - baseA;
//            int end = csrRowPtrA[i + 1] - baseA;
//            for (int colidx = start; colidx < end; colidx++)
//            {
//                // const int j = csrColIndA[colidx] - baseA;
//                double A_abs = Math.abs(csrValA[colidx]);
//                sum += A_abs;
//            }
//            norminf = (norminf > sum) ? norminf : sum;
//        }
//        return norminf;
//    }
//}
