package com.mathpar.number;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>Copyright: Copyright (c) 2005</p>
 */
public class NumberFileWriter extends FileOutputStream {

    public NumberFileWriter(File file, boolean append) throws FileNotFoundException {
        super(file, append);
    }

    public NumberFileWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public NumberFileWriter(String name, boolean append) throws FileNotFoundException {
        super(name, append);
    }

    public NumberFileWriter(String filename) throws FileNotFoundException {
        super(filename);
    }

    /**
     * Процедура записи массива 64-бит. чисел в файл. Преобразование в массив
     * байтов осуществляется вручную.
     *
     * @param M типа long[][], записываемый массив, длины строк которого могут различаться
     * @throws IOException
     */
    public void writeL2ByRows(long[][] M)
            throws IOException {
        byte[] pzinf = packArray(M[0], 0, M[0].length, 8),
                pzlg = packArray(new int[]{pzinf.length - 8, M.length}, 0, 2, 0);
        for (int i = 0; i < 8; i++) pzinf[i] = pzlg[i];
        write(pzinf);
        int srv;
        for (int i = 1; i < M.length; i++) {
            pzinf = packArray(M[i], 0, M[i].length, 4);
            srv = pzinf.length - 4;
            pzinf[0] = (byte) srv;
            pzinf[1] = (byte) (srv >>> 8);
            pzinf[2] = (byte) (srv >>> 16);
            pzinf[3] = (byte) (srv >>> 24);
            write(pzinf);
        }
    }

    /**
     * Процедура записи массива 64-бит. чисел в файл. Преобразование в массив
     * байтов осуществляется вручную.
     *
     * @param M типа long[][], записываемый массив, длины строк которого могут различаться
     * @throws IOException
     */
    public void write(long[] M)
            throws IOException {
        byte[] pzinf = packArray(M, 0, M.length, 0);
        write(pzinf);
    }

    /**
     * Write to the  file an integer array
     *
     * @param M         записываемый в файл массив целых чисел по 32 бита
     * @param chunkSize - размер порции (буфера), которая используется для записи
     * @throws IOException исключения при записи файла
     */

    public void write(int[] M, int chunkSize) throws IOException {
        int chunks = M.length > chunkSize ? M.length / chunkSize - 1 : 0;
        // количество циклов при записи буфера
        byte[] pzinf = new byte[4];
        for (int i = 0; i < chunks; i++) {//откусываем части, упаковываем и записываем в файл
            pzinf = packArray(M, i * chunkSize, (i + 1) * chunkSize, 0);
            write(pzinf);
        } // записываем в файл последний кусок
        pzinf = packArray(M, chunks * chunkSize, M.length, 0);
        write(pzinf);
    }

    /**
     * Процедура записи массива 64-бит. действительных чисел в файл. Преобразование в массив
     * байтов осуществляется вручную.
     *
     * @param M типа double[][], записываемый массив, длины строк которого могут различаться
     * @throws IOException
     */

    public void write(double[] M)
            throws IOException {
        byte[] pzinf = packArray(M, 0, M.length, 4);
        int pzlg = pzinf.length - 4;
        pzinf[0] = (byte) pzlg;
        pzinf[1] = (byte) (pzlg >>> 8);
        pzinf[2] = (byte) (pzlg >>> 16);
        pzinf[3] = (byte) (pzlg >>> 24);
        write(pzinf);
    }

    /**
     * Процедура записи массива 32-бит. чисел в файл. Преобразование в массив
     * байтов осуществляется вручную.
     *
     * @param M типа int[], записываемый массив, длины строк которого могут различаться
     * @throws IOException
     */
    public void write(int Mlength, int[] M)
            throws IOException {
        byte[] pzinf = packArray(M, 0, Mlength, 0);
        write(pzinf);
    }

    public void write(int[] M) throws IOException {
        write(M, M.length);
    }

    /**
     * Процедура записи массива 32-бит. чисел в файл. Преобразование в массив
     * байтов осуществляется вручную.
     *
     * @param M типа int[], записываемый массив, длины строк которого могут различаться
     * @throws IOException
     */
    public void write(int[] prefix, int[] M)
            throws IOException {
        byte[] prc = packArray(prefix, 0, prefix.length, 0);
        int num = 8 + prc.length;
        byte[] pzinf = packArray(M, 0, M.length, num);
        int pzlg = prc.length;
        pzinf[0] = (byte) pzlg;
        pzinf[1] = (byte) (pzlg >>> 8);
        pzinf[2] = (byte) (pzlg >>> 16);
        pzinf[3] = (byte) (pzlg >>> 24);
        System.arraycopy(prc, 0, pzinf, 4, prc.length);
        pzlg = pzinf.length - num;
        num -= 4;
        pzinf[num++] = (byte) pzlg;
        pzinf[num++] = (byte) (pzlg >>> 8);
        pzinf[num++] = (byte) (pzlg >>> 16);
        pzinf[num++] = (byte) (pzlg >>> 24);
        write(pzinf);
    }

    /**
     * Упаковка массива int[] в байтовый массив
     *
     * @param mas типа int[], пакуемый массив
     * @param beg типа int, позиция первого  пакуемого элемента массива
     * @param end типа int, позиция последнего пакуемого элемента
     * @param off типа int, сколько байт сначала нужно пропустить
     * @return byte[] -- байтовый массив
     */

    public static byte[] packArray(int[] mas, int beg, int end, int off) {
        byte[] res = new byte[((end - beg) << 2) + off];
        int indx = off;
        for (int i = beg; i < end; i++) {
            res[indx++] = (byte) mas[i];
            res[indx++] = (byte) (mas[i] >>> 8);
            res[indx++] = (byte) (mas[i] >>> 16);
            res[indx++] = (byte) (mas[i] >>> 24);
        }
        return res;
    }

    /**
     * Упаковка массива long[] в байтовый массив
     *
     * @param mas типа long[], пакуемый массив
     * @param beg типа int, позиция первого  пакуемого элемента массива
     * @param end типа int, позиция последнего пакуемого элемента
     * @param off типа int, сколько байт сначала нужно пропустить
     * @return byte[] -- байтовый массив
     */
    public static byte[] packArray(long[] mas, int beg, int end, int off) {
        byte[] res = new byte[((end - beg) << 3) + off];
        int indx = off;
        for (int i = beg; i < end; i++) {
            res[indx++] = (byte) mas[i];
            res[indx++] = (byte) (mas[i] >>> 8);
            res[indx++] = (byte) (mas[i] >>> 16);
            res[indx++] = (byte) (mas[i] >>> 24);
            res[indx++] = (byte) (mas[i] >>> 32);
            res[indx++] = (byte) (mas[i] >>> 40);
            res[indx++] = (byte) (mas[i] >>> 48);
            res[indx++] = (byte) (mas[i] >>> 56);
        }
        return res;
    }

    /**
     * Упаковка массива double[] в байтовый массив
     *
     * @param mas типа double[], пакуемый массив
     * @param beg типа int, позиция первого  пакуемого элемента массива
     * @param end типа int, позиция последнего пакуемого элемента
     * @param off типа int, сколько байт сначала нужно пропустить
     * @return byte[] -- байтовый массив
     */
    public static byte[] packArray(double[] mas, int beg, int end, int off) {
        byte[] res = new byte[((end - beg) << 3) + off];
        int indx = off;
        long elem;
        for (int i = beg; i < end; i++) {
            elem = Double.doubleToLongBits(mas[i]);
            res[indx++] = (byte) elem;
            res[indx++] = (byte) (elem >>> 8);
            res[indx++] = (byte) (elem >>> 16);
            res[indx++] = (byte) (elem >>> 24);
            res[indx++] = (byte) (elem >>> 32);
            res[indx++] = (byte) (elem >>> 40);
            res[indx++] = (byte) (elem >>> 48);
            res[indx++] = (byte) (elem >>> 56);
        }
        return res;
    }
}
