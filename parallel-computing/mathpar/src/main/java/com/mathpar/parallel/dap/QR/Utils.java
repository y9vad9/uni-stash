package com.mathpar.parallel.dap.QR;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class Utils {

    public static String byteToString(byte value){
        return String.format("%8s", Integer.toBinaryString(value & 0xff)).replace(' ', '0');
    }

    public static String byteArrayToString(byte[] array){
        StringBuilder sb = new StringBuilder();

        sb.append("[ ");
        for (int i = 0; i < array.length; i++) {
            sb.append("(");
            sb.append(i);
            sb.append(") ");
            sb.append(byteToString(array[i]));
            sb.append(" ");
        }
        sb.append("]");

        return sb.toString();
    }

    public static void printByteArray(byte[] array){
        System.out.println(byteArrayToString(array));
    }


    public static String matrixSArrayToString(Element[] array){
        StringBuilder sb = new StringBuilder();

        sb.append("[ ");
        for (int i = 0; i < array.length; i++) {
            sb.append("(");
            sb.append(i);
            sb.append(") ");
            if(array[i] != null){
                sb.append("m");
            }else{
                sb.append("null");
            }
            if(i != array.length-1)
                sb.append(" | ");
        }
        sb.append("]");

        return sb.toString();
    }

    public static void saveMatrixSArrayToFile(String fileName, MatrixS[] array){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(array);
            out.flush();
            byte[] bytes = bos.toByteArray();

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(bytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static MatrixS[] readMatrixArrayFromFile(String fileName){
        ObjectInput in = null;
        try {
            byte[] fileContent = Files.readAllBytes(new File(fileName).toPath());

            ByteArrayInputStream bis = new ByteArrayInputStream(fileContent);
            in = new ObjectInputStream(bis);
            return (MatrixS[]) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return new MatrixS[0];
    }
}
