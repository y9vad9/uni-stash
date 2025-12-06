package com.mathpar.parallel.dap.SVD.UTV.seqModel.transport;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Ring;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SomeTest {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        serializationDeserializationTest();


    }

    public static void serializationDeserializationTest() throws IOException, ClassNotFoundException {

        MatrixS[] data = IntStream.range(0, 10)
                .mapToObj(i -> matrix(16)).toArray(MatrixS[]::new);



        int[] sizes = new int[data.length];
        int[] displs = new int[data.length];
        int totalSize = 0;

        byte[][] bytes = new byte[data.length][];
        for (int i = 0; i < data.length; i++) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data[i]);
            bytes[i] = bos.toByteArray();
            int currentTotalSize = bytes[i].length;
            sizes[i] = currentTotalSize;
            displs[i] = totalSize;
            totalSize += currentTotalSize;
        }

        byte[] objects = new byte[totalSize];

        for (int i = 0; i < data.length; i++) {
            System.arraycopy(bytes[i], 0, objects, displs[i], sizes[i]);
        }


        for (int i = 0; i < data.length; i++) {
            int objectSize = sizes[i];
            byte[] arr = new byte[objectSize];
//            ByteBuffer rbuf = ByteBuffer.allocate(objectSize + 20);
//            System.out.println(String.format("len=%d displs=%d size=%d buf=%d", totalSize, displs[i], objectSize, rbuf.capacity()));
//            rbuf.put(objects, displs[i], objectSize);

//            System.out.println("buff size "+ rbuf.position());

            System.arraycopy(objects, displs[i], arr, 0, sizes[i]);


//            rbuf.get(arr, 0, objectSize);
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            ObjectInput in = new ObjectInputStream(bis);

            MatrixS matrix = (MatrixS) in.readObject();

            System.out.println("data is equal "+matrix.equals(data[i], new Ring("R64[]")));
        }

    }

    public void test() throws IOException {
        List<MatrixS> list =  IntStream.range(0, 2)
                .mapToObj(i -> matrix(4096))
                .collect(Collectors.toList());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(list.get(0));

        int size1 = bos.size();

        oos.writeObject(list.get(1));

        int size2 = bos.size();

        int megaByte = 1024*1024;

        System.out.println(String.format("elem1=%d MB elem2=%d MB diff=%d B", size1/megaByte, (size2 - size1)/megaByte, Math.abs(size2 - size1 - size1)));
    }


    public static MatrixS matrix(int size) {
        Ring ring = new Ring("R64[]");
        return new MatrixS(size, size, 8000, new int[]{5}, new Random(), ring.numberONE(), ring);
    }
}
