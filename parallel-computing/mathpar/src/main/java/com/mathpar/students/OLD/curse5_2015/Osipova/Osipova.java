package com.mathpar.students.OLD.curse5_2015.Osipova;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import mpi.*;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.number.NumberZ;
import com.mathpar.number.VectorS;
import com.mathpar.number.NFunctionZ32;
import com.mathpar.parallel.utils.MPITransport;


public class Osipova {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);
        int n = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);
        int length = Integer.parseInt(args[2]);
        int myrank = MPI.COMM_WORLD.getRank();
        int number = MPI.COMM_WORLD.getSize();
        int N_C = n;
        int temp = number;
        while(temp > 1) {
            temp = temp/8;
            N_C = N_C/2;
        }
        if(myrank == 0) System.out.println("crit == "+N_C);
        MatrixD A;
        VectorS V;
        if(myrank == 0) {
            A = new MatrixD(n, n, 10000, new int[] {length}, new Random(), ring);
            V = new VectorS(n, 10000, new int[] {length}, new Random(), ring);
        } else {
            A = new MatrixD(n, n, 10000, new int[] {0}, new Random(), ring);
            V = new VectorS(n, 10000, new int[] {0}, new Random(), ring);
        }
        MatrixD AA = A;
        
        if (myrank == 0) {
            long time = System.currentTimeMillis();
            Ring r = new Ring("Zp32[]");
            int block = 1;
            int count = 1;
            Object[] array_of_all_primes = new Object[100];
            int[] primes = NFunctionZ32.readBlockOfPrimesFromBack(1, Ring.ringZxyz);
            int number_of_primes = primes.length;
            array_of_all_primes[0] = primes;
            NumberZ mod = new NumberZ( primes[0]);
            NumberZ max = (NumberZ)new NumberZ(2).pow(length*(m+1)+n);//(int)(Math.log(n)/Math.log(2))+1, ring);
            for(int i=1; mod.compareTo(max, ring) < 0; i++) {
                if(i >= primes.length) {
                    i = 0;
                    block++;
                    primes = NFunctionZ32.readBlockOfPrimesFromBack(block, Ring.ringZxyz);
                    array_of_all_primes[block - 1] = primes;
                    number_of_primes += primes.length;
                }
                count++;
                mod = mod.multiply(new NumberZ(primes[i]));
            }
            System.out.println("n = "+count);
            MPI.COMM_WORLD.bcast(new int[]{count}, 1, MPI.INT, 0);
            primes = new int[number_of_primes];
            int tmp = 0;
            for(int i=0; array_of_all_primes[i] != null; i++) {
                System.arraycopy(array_of_all_primes[i], 0, primes, tmp, ((int[])array_of_all_primes[i]).length);
                tmp+=((int[])array_of_all_primes[i]).length;
            }
            
            MatrixD aa;
            MatrixD aa1;
            VectorS bb;
            VectorS[] cc = new VectorS[count];
            Ring r2 = new Ring("Z[]");
            for(int i=0; i < count; i++) {
                r.setMOD32(primes[i]);
                aa = (MatrixD)A.toNewRing(Ring.Zp32, r);
                aa1 = (MatrixD)AA.toNewRing(Ring.Zp32, r);
                bb = (VectorS)V.toNewRing(Ring.Zp32, r);
                ring = r;
                for(int j=0; j<m-1; j++) {
                    MPITransport.bcastObjectArray(new Object[]{r}, 1, 0);
                    aa1 = (MatrixD)multiplyMatrix(aa1, aa, N_C);
                }
                if(myrank != 0) {
                    aa1 = new MatrixD(new Element[n][n]);
                }
                cc[i] = new VectorS(MultMatrixVector(aa1.M, bb.V));
                cc[i] = (VectorS)cc[i].toNewRing(Ring.Z, r2);
            }
            ring = r2;
            VectorS C;
            VectorS C_ = new VectorS(new Element[cc[0].V.length]);
            VectorS C2;
            C = cc[0];
            for(int i=1; i<count; i++) {
                for(int z=0; z<C.V.length; z++) {
                    C_.V[z] = C.V[z].mod(new NumberZ(primes[i]), ring);
                }
                C2 = C;
                C = cc[i].subtract(C_, ring);
                for(int j=0; j<i; j++) {
                    C = (VectorS)C.multiply(new NumberZ(primes[j]).multiply(new NumberZ(primes[j]).modInverse(new NumberZ(primes[i])), ring), ring);
                }
                C = C.add(C2, ring);
            }
            for(int i=0; i<C.V.length; i++) {
                C.V[i] = C.V[i].Mod(mod, ring);
            }
            System.out.println("time1 = "+(System.currentTimeMillis() - time));

            time = System.currentTimeMillis();
            for(int i=0; i<m-1; i++) {
                AA = (MatrixD)AA.multiply(A, ring);
            }
            VectorS res = (VectorS) AA.multiply(V, ring);
            System.out.println("time2 = "+(System.currentTimeMillis() - time));
            if(res.subtract(C, ring).isZero(ring)) {
                System.out.println("ok!");
            } else {
                System.out.println("Wrong answer!!!");
            }
        } else {
            int[] c = new int[1];
            MPI.COMM_WORLD.bcast(c, 1, MPI.INT, 0);
            Object[] E = new Object[1];
            for(int i=0; i<c[0]; i++) {
                for(int j=0; j<m-1; j++) {
                    MPITransport.bcastObjectArray(E, 1, 0);
                    ring = (Ring) E[0];
                    multiplyMatrix(A, A, N_C);
                }
                MultMatrixVector(A.M, V.V);
            }
        }
        MPI.Finalize();
    }
    static Element[] MultMatrixVector(Element[][] A, Element[] B) throws Exception {
        int myrank = MPI.COMM_WORLD.getRank();
        int number = MPI.COMM_WORLD.getSize();
        if (myrank == 0) {
            Object[] E = new Object[A.length * A[0].length + B.length+1];
            for (int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[0].length; j++) {
                    E[i * A[0].length + j] = A[i][j];
                }
            }
            for (int i = 0; i < B.length; i++) {
                E[A.length * A[0].length + i] = B[i];
            }
            E[A.length * A[0].length + B.length] = ring;
            MPITransport.bcastObjectArray(E, E.length, 0);
            int temp;
            Element[][] result = new Element[number][0];
            for (int i = 0; i < result.length; i++) {
                if (A.length % number > i) {
                    temp = 1;
                } else {
                    temp = 0;
                }
                result[i] = new Element[A.length / number + temp];
            }
            for (int i = 0; i < result[0].length; i++) {
                result[0][i] = new NumberZ("0");
                for (int j = 0; j < B.length; j++) {
                    result[0][i] = result[0][i].add(A[i * number + myrank][j].multiply(B[j], ring), ring);
                }
            }
            byte[] arr;
            Element[] ob;
            for (int i = 1; i < number; i++) {
                Status st = MPI.COMM_WORLD.probe(i, 0);
                arr = new byte[st.getCount(MPI.BYTE)];
                MPI.COMM_WORLD.recv(arr, arr.length, MPI.BYTE, i, 0);
                ByteArrayInputStream bin = new ByteArrayInputStream(arr);
                ObjectInputStream oin = new ObjectInputStream(bin);
                ob = (Element[])oin.readObject();
                bin.close();
                oin.close();
                result[i] = ob;
            }
            Element[] output = new Element[A.length];
            int counter = 0;
            for (int i = 0; i < result[0].length; i++) {
                for (int j = 0; j < result.length; j++) {
                    if (i < result[j].length) {
                        output[counter] = result[j][i];
                        counter++;
                    }
                }
            }
            return output;
        } else {
            Object[] source = new Object[A.length * A[0].length + B.length + 1];
            MPITransport.bcastObjectArray(source, source.length, 0);
            for (int i = 0; i < A.length; i++) {
                for (int j = 0; j < A[0].length; j++) {
                    A[i][j] = (Element)source[i * A[0].length + j];
                }
            }
            for (int i = 0; i < B.length; i++) {
                B[i] = (Element)source[A.length * A[0].length + i];
            }
            ring = (Ring)source[A.length * A[0].length + B.length];
            int len = A.length / number;
            if (A.length % number > myrank) {
                len++;
            }
            Element[] res = new Element[len];
            for (int i = 0; i < res.length; i++) {
                res[i] = A[i * number + myrank][0].multiply(B[0], ring);
                for (int j = 1; j < B.length; j++) {
                    res[i] = res[i].add(A[i * number + myrank][j].multiply(B[j], ring), ring);
                }
            }
            byte[] array;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);
            oout.writeObject(res);
            oout.flush();
            array = bout.toByteArray();
            bout.close();
            oout.close();
            MPI.COMM_WORLD.send(array, array.length, MPI.BYTE, 0, 0);
        }
        return null;
    }
    
    ///////////////////////////////////////////////////////
    //////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    
    
    static int tag = 0;
    static Ring ring = new Ring("Z[]");

    
    
    public static int[] numChildProc(int rank, int shift) {
        int[] ranks = new int[8];
        for (int i = 0; i < 8; i++) {
            ranks[i] = (i << shift) | rank;
        }
        return ranks;
    }

    public static int numbParentProc(int rank, int l) {
        int temp = (rank >> l);
        int num_parent = rank - (temp << l);
        return num_parent;

    }
 
    public static int shiftProc(int rank) {

        int procs[] = new int[10];
        for (int i = 0; i < 10; i++) {
            procs[i] = (int) Math.pow(8.0, i + 1) - 1;
        }
        int res = 0;
        for (int i = 1; i < procs.length; i++) {
            if (rank > procs[i - 1] && rank <= procs[i]) {
                res = i * 3;
            }
        }
        return res;
    }

    public static MatrixD procEightProc(MatrixD A, MatrixD B, int N_crit, int rank, int shift) throws Exception {

        MatrixD[] C = new MatrixD[4];
        MatrixD Result;

        if (N_crit < A.M.length) {

            int[] ranks = numChildProc(rank, shift);

            MatrixD[] AA = split(A);
            MatrixD[] BB = split(B);

            MPITransport.sendObject(new Object[] {AA[1], BB[2]}, ranks[1], tag);
            MPITransport.sendObject(new Object[] {AA[0], BB[1]}, ranks[2], tag);
            MPITransport.sendObject(new Object[] {AA[1], BB[3]}, ranks[3], tag);
            MPITransport.sendObject(new Object[] {AA[2], BB[0]}, ranks[4], tag);
            MPITransport.sendObject(new Object[] {AA[3], BB[2]}, ranks[5], tag);
            MPITransport.sendObject(new Object[] {AA[2], BB[1]}, ranks[6], tag);
            MPITransport.sendObject(new Object[] {AA[3], BB[3]}, ranks[7], tag);

            Status st = null;
            MatrixD[] tmp = new MatrixD[8];
            tmp[0] = procEightProc(AA[0], BB[0], N_crit, rank, shift + 3);

            for (int i = 1; i < 8; i++) {
                while (st == null) {
                    st = MPI.COMM_WORLD.probe(ranks[i], tag);
                }

                tmp[i] = (MatrixD) MPITransport.recvObject(ranks[i], tag);

            }

            C[0] = tmp[0].add(tmp[1], ring);
            C[1] = tmp[2].add(tmp[3], ring);
            C[2] = tmp[4].add(tmp[5], ring);
            C[3] = tmp[6].add(tmp[7], ring);

            Result = MatrixD.join(C);
            
        } else {
            Result = (MatrixD)A.multiply(B, ring);
        }

        return Result;
    }

    public static MatrixD multiplyMatrix(MatrixD A, MatrixD B, int N_crit) throws Exception {
        int myrank = MPI.COMM_WORLD.getRank();
        MatrixD result = null;
        if (myrank == 0) {
            result = procEightProc(A, B, N_crit, myrank, 0);
        } else {
            Status st = null;

            while (st == null) {
                st = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
            }


            int shift = shiftProc(myrank);
            int parent_proc = numbParentProc(myrank, shift);
            Object[] n = (Object[])MPITransport.recvObject(parent_proc, tag);
            MatrixD a = (MatrixD) n[0];
            MatrixD b = (MatrixD) n[1];

            MatrixD temp = procEightProc(a, b, N_crit, myrank, shift + 3);
            MPITransport.sendObject(temp, parent_proc, tag);

        }
        return result;
    }
    
    static MatrixD[] split(MatrixD m) {
        MatrixD[] result = new MatrixD[4];
        int size = m.M.length/2;
        
        for(int i=0; i<result.length; i++) {
            result[i] = new MatrixD(new Element[size][size]);
        }
        
        for(int i=0; i<m.M.length; i++) {
            for(int j=0; j<m.M.length; j++) {
                if( (i<size )&&(j<size) ) {
                    result[0].M[i][j] = m.M[i][j];
                } else if( (i<size )&&(j>=size) ) {
                    result[1].M[i][j - size] = m.M[i][j];
                } else if( (i>=size )&&(j<size) ) {
                    result[2].M[i - size][j] = m.M[i][j];
                } else {
                    result[3].M[i - size][j - size] = m.M[i][j];
                }
            }
        }
        
        
        return result;
    }
}
