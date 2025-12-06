package com.mathpar.number;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p>Description: Matrix algebra component</p>
 * <p/>
 * <p>Copyright: Copyright (c) 2005</p>
 */
public class NumberFileReader extends FileInputStream {
    public NumberFileReader(String filename) throws FileNotFoundException {
        super(filename);
    }

    public NumberFileReader(File file) throws FileNotFoundException {
        super(file);
    }

    public String readS() {
        int[] file = new int[0];
        try {
            file = new int[(int) this.getChannel().size()];
            for (int i = 0; i < file.length; i++) {
                file[i] = read();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String s = new String(file, 0, file.length);
        return s;
    }

    /**
     * Процедура чтения массива 64-битных целых чисел из файла, записанного с помощью процедуры
     * writeL2ByRows.
     *
     * @return long[][], прочитанный массив
     * @throws IOException
     */
    public long[][] readL2ByRows()
            throws IOException {
        int moff = 8;
        byte[] ILength = new byte[moff];
        read(ILength, 0, moff);
        int[] ilen = unpackIArray(ILength);
        byte[] t = new byte[ilen[0]];
        read(t, 0, ilen[0]);
        int matrLen = ilen[1];
        long[][] res = new long[matrLen][];
        res[0] = unpackLArray(t);
        int srv;
        for (int i = 1; i < matrLen; i++) {
            moff = 4;
            ILength = new byte[moff]; // потом -- вынести за цикл эту строчкку и предыдущую
            read(ILength, 0, moff);
            srv = (ILength[0] & 0xFF) | ((ILength[1] & 0xFF) << 8) |
                    ((ILength[2] & 0xFF) << 16) | ((ILength[3] & 0xFF) << 24);
            t = new byte[srv];
            read(t, 0, srv);
            res[i] = unpackLArray(t);
        }
        return res;
    }

    /**
     * Процедура чтения массива 64-битных чисел из файла, записанного с помощью процедуры
     * writeD2ByRows.
     *
     * @return long[][], прочитанный массив
     * @throws IOException
     */
    public double[][] readD2ByRows()
            throws IOException {
        int moff = 8;
        byte[] ILength = new byte[moff];
        read(ILength, 0, moff);
        int[] ilen = unpackIArray(ILength);
        byte[] t = new byte[ilen[0]];
        read(t, 0, ilen[0]);
        int matrLen = ilen[1];
        double[][] res = new double[matrLen][];
        res[0] = unpackRArray(t);
        int srv;
        for (int i = 1; i < matrLen; i++) {
            moff = 4;
            ILength = new byte[moff]; // потом -- вынести за цикл эту строчкку и предыдущую
            read(ILength, 0, moff);
            srv = (ILength[0] & 0xFF) | ((ILength[1] & 0xFF) << 8) |
                    ((ILength[2] & 0xFF) << 16) | ((ILength[3] & 0xFF) << 24);
            t = new byte[srv];
            read(t, 0, srv);
            res[i] = unpackRArray(t);
        }
        return res;
    }

    /**
     * Процедура чтения массива 64-битных целых чисел из файла, записанного с помощью процедуры
     * write.
     *
     * @return long[], прочитанный массив
     * @throws IOException
     */
    public long[] readL1()
            throws IOException {
        byte[] ILength = new byte[4];
        read(ILength, 0, 4);
        int ilen = (ILength[0] & 0xFF) | ((ILength[1] & 0xFF) << 8) |
                ((ILength[2] & 0xFF) << 16) | ((ILength[3] & 0xFF) << 24);
        byte[] t = new byte[ilen];
        read(t, 0, ilen);
        return unpackLArray(t);
    }

    /**
     * Процедура чтения массива 64-битных целых чисел из файла, записанного с помощью процедуры
     * write.
     *
     * @return double[], прочитанный массив
     * @throws IOException
     */
    public double[] readD1()
            throws IOException {
        byte[] ILength = new byte[4];
        read(ILength, 0, 4);
        int ilen = (ILength[0] & 0xFF) | ((ILength[1] & 0xFF) << 8) |
                ((ILength[2] & 0xFF) << 16) | ((ILength[3] & 0xFF) << 24);
        byte[] t = new byte[ilen];
        read(t, 0, ilen);
        return unpackRArray(t);
    }

    /**
     * Процедура чтения массива 64-битных целых чисел из файла,
     *
     * @return longNub считывается не более, чем столько long.
     * @throws IOException
     */
    public long[] readtoLongArray(int longNumb) throws IOException {
        byte[] t = new byte[longNumb << 3];
        int numbByte = read(t, 0, longNumb << 3);
        return unpackLArray(t, numbByte);  // распаковываем из байт в longs
    }

    /**
     * Процедура чтения массива 32-битных целых чисел из файла,
     *
     * @return intNub считывается не более, чем столько int
     * @throws IOException
     */
    public int[] readtoIntArray(int intNumb) throws IOException {
        byte[] t = new byte[intNumb << 2]; // теперь читаем все остальное
        int numbByte = read(t, 0, intNumb << 2);
        return unpackIArray(t, numbByte);  // распаковываем из байт в инты
    }

    /**
     * Процедура чтения массива 32-битных целых чисел из файла,
     *
     * @return intNub считывается не более, чем столько int
     * @throws IOException
     */
    public int[] readtoIntArray(int intNumb, int offset, Ring ring) throws IOException {
        long l = skip(((long) offset) << 2);
        if ((((int) l / 4) != offset) || (l % 4 != 0)) ring.exception.append(
                "Exception at the file reading: Inpossible to skip " + offset +
                        " integers in the file:" + this.toString() + ". It has just " + l / 4 + " integers.");
        byte[] t = new byte[intNumb << 2]; // теперь читаем все остальное
        int numbByte = read(t, 0, intNumb << 2);
        return unpackIArray(t, numbByte);  // распаковываем из байт в инты
    }

//  /**
//  * Процедура чтения массива 32-битных целых чисел из файла,
//  * записанного с помощью процедуры
//  * write.
//  * @return int[], прочитанный массив
//  * @throws IOException
//  */
// public int[] readIntArray() throws IOException{
//   byte[] ILength=new byte[4];// первые 4 байта хранят длину в байтах оставшейся части
//   read(ILength,0,4);
//   int ilen=(ILength[0]&0xFF)|((ILength[1]&0xFF)<<8)|((ILength[2]&0xFF)<<16)|((ILength[3]&0xFF)<<24);
//   byte[] t = new byte[ilen]; // теперь читаем все остальное
//   read(t,0,ilen);
//   return  unpackIArray(t);  // распаковываем из байт в инты
// }

    /**
     * Считать из потока ввода целочисленный массив int[] блоками по chunkSize байт
     *
     * @param chunkSize размер блока считывания в байтах
     * @return массив int[], который может оказаться пустым, если в потоке менее 4х байт.
     * @throws IOException
     */
    public int[] readIntArray(int chunkSize) throws IOException {
        byte[] t;
        int[][] res = new int[1000][];
        int numByte = chunkSize;
        int i = 0;
        while (numByte == chunkSize) {
            t = new byte[chunkSize];
            numByte = read(t, 0, chunkSize);
            if (numByte > 3) res[i++] = unpackIArray(t, numByte);
        }
        int len = 0;
        for (int j = 0; j < i; j++) {
            len += res[j].length;
        }
        int[] arr = new int[len];
        int pos = 0;
        for (int j = 0; j < i; j++) {
            System.arraycopy(res[j], 0, arr, pos, res[j].length);
            pos += res[j].length;
        }
        return arr;
    }

    public int[] readI1(int off, int len, int chunkSize)
            throws IOException {
        // byte[] ILength=new byte[4];
        //read(ILength,0,4);
  /*int ilen = (ILength[0]&0xFF) | ((ILength[1]&0xFF) << 8) |
      ((ILength[2]&0xFF) << 16) | ((ILength[3]&0xFF) << 24);*/
        int chunks = chunkSize > len ? 0 : len / chunkSize - 1, c4 = chunkSize > len ? len >>> 2 : chunkSize >>> 2;
        byte[] t;
        int[] res = new int[len >>> 2], dec;
        int fc = chunkSize > len ? len : chunkSize;
        t = new byte[fc];
        skip(off + 4);
        read(t, 0, fc);
        dec = unpackIArray(t);
        System.arraycopy(dec, 0, res, 0, c4);
        if (chunkSize < len) {
            for (int i = 1; i < chunks; i++) {
                t = new byte[chunkSize];
                read(t, 0, chunkSize);
                dec = unpackIArray(t);
                System.arraycopy(dec, 0, res, i * c4, c4);
            }
            t = new byte[len - chunks * chunkSize];
            read(t, 0, t.length);
            dec = unpackIArray(t);
            System.arraycopy(dec, 0, res, chunks * c4, dec.length);
        }
        return res;
    }

    /**
     * Распаковка из байтового массива в массива типа long[].
     *
     * @param pk типа byte[], байтовый массив
     * @return массив типа long[], распакованный массив
     */

    public static long[] unpackLArray(byte[] pk) {
        return unpackLArray(pk, pk.length);
    }

    /**
     * Распаковка из байтового массива в массива типа long[].
     *
     * @param pk типа byte[], байтовый массив
     * @return массив типа long[], распакованный массив
     */
    public static long[] unpackLArray(byte[] pk, int numbB) {
        long[] res = new long[numbB >>> 3];
        int indx = 0;
        long rs, halfNum;
        for (int i = 0; i < res.length; i++) {
            rs = 0;
            rs |= pk[indx++] & 0xFF;
            rs |= (pk[indx++] & 0xFF) << 8;
            rs |= (pk[indx++] & 0xFF) << 16;
            rs |= (pk[indx++] & 0xFFL) << 24;
            halfNum = 0;
            halfNum |= (pk[indx++] & 0xFF);
            halfNum |= (pk[indx++] & 0xFF) << 8;
            halfNum |= (pk[indx++] & 0xFF) << 16;
            halfNum |= (pk[indx++] & 0xFFL) << 24;
            //rs = ((rs<<32) >>>32);
            //rs = ((rs&0xFFFF) | (((rs>>>16)&0xFFFF) <<16)); // здесь написано rs=rs, но без этой строки ответ будет неправильным
            res[i] = rs | halfNum << 32;
        }
        return res;
    }

    /**
     * Распаковка из байтового массива массива типа long[].
     *
     * @param pk типа byte[], байтовый массив
     * @return массив типа double[], распакованный массив
     */
    public static double[] unpackRArray(byte[] pk) {
        double[] res = new double[pk.length >>> 3];
        int indx = 0;
        long rs, halfNum;
        for (int i = 0; i < res.length; i++) {
            rs = 0;
            rs |= pk[indx++] & 0xFF;
            rs |= (pk[indx++] & 0xFF) << 8;
            rs |= (pk[indx++] & 0xFF) << 16;
            rs |= (pk[indx++] & 0xFF) << 24; // после этого в rs может появиться отрицательный знаковый бит
            halfNum = 0;
            halfNum |= (pk[indx++] & 0xFF);
            halfNum |= (pk[indx++] & 0xFF) << 8;
            halfNum |= (pk[indx++] & 0xFF) << 16;
            halfNum |= (pk[indx++] & 0xFF) << 24;
            rs = ((rs << 32) >>> 32);
            //rs = ((rs&0xFFFF) | (((rs>>>16)&0xFFFF) <<16)); // rs -- переменная типа long, и преобразован из int
            // (см. 5 первых строк от начала цикла) копированием знакового бита на 32 старшие позиции.
            // Данный код обнуляет старшие 32 бита. Это очень хорошо, если rs -- отрицательна.
            // Код типа rs=rs&0xFFFFFFFF при 0xF..F > 16^7 (кол-во F-ок больше 7) не работает, поэтому
            // накладываем маску по частям
            res[i] = Double.longBitsToDouble(rs | (halfNum) << 32);
        }
        return res;
    }

    /**
     * Распаковка из байтового массива массива типа int[].
     * Если остаются в конце лишние байты (1,2 или 3), то они теряются
     *
     * @param pk      типа byte[], массив -- упаковка
     * @param numByte число байт, которые нужно распаковать, может быть меньше, чем длина pk.
     * @return массив типа int[], распакованный массив
     */
    public static int[] unpackIArray(byte[] pk, int numByte) {
        int[] res = new int[numByte >>> 2];
        int rs, indx = 0;
        for (int i = 0; i < res.length; i++) {
            rs = 0;
            rs |= pk[indx++] & 0xFF;
            rs |= (pk[indx++] & 0xFF) << 8;
            rs |= (pk[indx++] & 0xFF) << 16;
            rs |= (pk[indx++] & 0xFF) << 24;
            res[i] = rs;
        }
        return res;
    }

    public static int[] unpackIArray(byte[] pk) {
        return unpackIArray(pk, pk.length);
    }

}
