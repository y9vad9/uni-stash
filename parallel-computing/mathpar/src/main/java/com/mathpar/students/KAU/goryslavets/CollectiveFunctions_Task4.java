package com.mathpar.students.KAU.goryslavets;

import mpi.MPI;
import mpi.MPIException;

import java.util.Arrays;

/*

Task:

4. Напишiть програму для пересилання масиву чисел iз
процесора номер 2 iншим процесорам групи. Причому
процесор 0 повинен отримати одне число, процесор 1 – два
числа, процесор 2 – чотири числа i так далi. Протестуйте
програму на 4, 8, 12 процесорах.

*/

/*

Run commands:

$ mpirun --hostfile /home/dmytro/dap/hostfile -np 4 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task4
$ mpirun --hostfile /home/dmytro/dap/hostfile -np 8 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task4
$ mpirun --hostfile /home/dmytro/dap/hostfile -np 12 java -cp /home/dmytro/dap/target/classes com/mathpar/students/KAU/goryslavets/CollectiveFunctions_Task4

*/

public class CollectiveFunctions_Task4 {
    public static void main(String[] args) throws MPIException {

        MPI.Init(args);

        int myRank = MPI.COMM_WORLD.getRank();
        int np = MPI.COMM_WORLD.getSize();


        int[] a = new int[(int) Math.pow(2, np) - 1];  // array to send
        int[] sizesArray = new int[np];                // array of sizes of arrays
        int[] displaysArray = new int[np];             // array of indents


        for (int i = 0; i < np; i++) {
            sizesArray[i] = (int) Math.pow(2, i);

            if (i == 0) displaysArray[i] = 0;
            else displaysArray[i] = displaysArray[i - 1] + (int) Math.pow(2, i - 1);
        }

        int[] receiveBuffer = new int[sizesArray[myRank]];

        if (myRank == 2) {
            for (int i = 0; i < a.length; i++) {
                a[i] = i;
            }
            System.out.println("myRank = " + myRank + " : a = " + Arrays.toString(a));
        }

        MPI.COMM_WORLD.scatterv(a, sizesArray, displaysArray, MPI.INT,
                receiveBuffer, receiveBuffer.length, MPI.INT, 2);

        System.out.println("myRank = " + myRank + " : received = " + Arrays.toString(receiveBuffer));

        MPI.Finalize();
    }
}

/*

Output for 4 cores:

myRank = 2 : a = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
myRank = 2 : received = [3, 4, 5, 6]
myRank = 0 : received = [0]
myRank = 3 : received = [7, 8, 9, 10, 11, 12, 13, 14]
myRank = 1 : received = [1, 2]


Output for 8 cores:

myRank = 2 : a = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254]
myRank = 2 : received = [3, 4, 5, 6]
myRank = 0 : received = [0]
myRank = 6 : received = [63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126]
myRank = 4 : received = [15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30]
myRank = 3 : received = [7, 8, 9, 10, 11, 12, 13, 14]
myRank = 7 : received = [127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254]
myRank = 1 : received = [1, 2]
myRank = 5 : received = [31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62]

*/
