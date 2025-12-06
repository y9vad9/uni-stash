package com.mathpar.number;

/**
 * tsu <p>Title: Polynomial Calculator</p> <p>Description: </p> <p>Copyright:
 * Copyright (c) 2004-2013</p> <p>Company: ParCA Tambov</p>
 *
 * @author not attributable
 * @version 0.5 - 4.7
 */
import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;

import com.mathpar.func.Page;
import com.mathpar.polynom.*;

// ///////////////////////////////////////////////////////////////////////////////////
// Содержит 64 простых числа и статические методы:
// Inverse - вычисление обратного по модулю р.
// mod     - вычисляет (x mod m) в интервале [-(m-1)/2,(m-1)/2], (либо все int, либо long)
// ///////////////////////////////////////////////////////////////////////////////////
public class NFunctionZ32 {
    public static final File primesFile = new File(Newton.FILE_WITH_PRIMES+"primes");
    /**
     * The title of file for all primes which less then Integer.MaxValue.
     * It is used in the the methods: doFileOfIntPrimes
     */
    public static final File intPrimes = new File(Newton.FILE_WITH_PRIMES+"intPrimes");
    /**
     * The title of file with LABELS in the file intPrimes.
     * Its length is equals even number. Its 2k and 2k+1 elements are:
     * 2k: position of the last prime in the k-block of the file "intPrimes"
     * 2k+1: value of the last prime in the k-block of the file "intPrimes"
     * One block has (1 << 16) primes  (2^16).
     */
    public static final File intPrimesLabs = new File(Newton.FILE_WITH_PRIMES+"intPrimesLabs");
    static int blockSizeOfIntegers = 1 << 16;

// For factorization the number which less then 2097143*2097143 we needs
// arrays: 50000*8+150000*4=1Mb. По три простых числа в одном лонге помещаются до сюда.
    static int prodNumb = 51351;
    static int primNumb = 155610;
    static int lastPrime = 2097143;
    /**
     * first 155610 primes: 3,5,.., the last is 2097143.
     */
    public static int[] Primes = null;      //
    /**
     * products of Primes up to 63 bit-word. (51351-lenght)
     */
    public static long[] prodPrimes = null;
    /**
     * position of the last prime in each product. (51351-lenght)
     */
    public static int[] prodInd = null;
    /**
     * The quantity of all primes.
     * Last prim has value = 2147483629;
     */
    static int intPrimeNumb = 105097563;
    /**
     * Positions AND values of prime number in the file intPrimes
     * 2k: position of the last prime in the file "intPrimes"
     * 2k+1: value of the last prime in the file "intPrimes"
     */
    public static int[] indexBlocks = null;   //  length 32768*2;


    /**
     * The short list of 64 primes (old version) which is less then 268435455. (28 bits)
     *
     */
    public final static int[] primes = {
        268435399, 268435367, 268435361, 268435337, 268435331,
        268435313, 268435291, 268435273,
        268435243, 268435183, 268435171, 268435157, 268435147,
        268435133, 268435129, 268435121,
        268435109, 268435091, 268435067, 268435043, 268435039,
        268435033, 268435019, 268435009,
        268435007, 268434997, 268434979, 268434977, 268434961,
        268434949, 268434941, 268434937,
        268434857, 268434841, 268434827, 268434821, 268434787,
        268434781, 268434779, 268434773,
        268434731, 268434721, 268434713, 268434707, 268434703,
        268434697, 268434659, 268434623,
        268434619, 268434581, 268434577, 268434563, 268434557,
        268434547, 268434511, 268434499,
        268434479, 268434461, 268434401, 268434391, 268434389,
        268434373, 268434289, 268434281};
    // все простые числа. Первые 64 совпадают с primes, последние --...,11,7,5,3.
    public static int[] allPrimes;

    public NFunctionZ32() {}

    /**
     * Read one block  of primes, starting from the end of the file intPrimes.
     * Set the parameter  numbOfBlockFromBack=1 to obtain the largest primes.
     * Set the parameter  numbOfBlockFromBack=indexBlocks.length/2
     *                                         to obtain the smallest primes (3,5,7..).
     *
     * @param numbOfBlockFromBack  The number of block starting from the end  (1,2,3...)
     *                                          which is reading into the integer array
     * @param ring  Ring
     * @return   array of primes in one block. The array length is equals
     *           3027, 3093, 3032,..,6541 for numbOfBlockFromBack=1,2,..,indexBlocks.length/2
     * @throws IOException
     *
     */
    public static int[] readBlockOfPrimesFromBack(int numbOfBlockFromBack, Ring ring) throws IOException {
        NumberFileReader readLabs = new NumberFileReader(intPrimesLabs);
        NumberFileReader read  = new NumberFileReader(intPrimes);
        indexBlocks= readLabs.readIntArray(blockSizeOfIntegers);
        int k=indexBlocks.length-2*numbOfBlockFromBack;
        int skipAmount=(k-2<0)?0:indexBlocks[k-2];
        int NumbOfPrimesInBlock=indexBlocks[k]-skipAmount;
       return read.readtoIntArray(NumbOfPrimesInBlock, skipAmount,  ring);
    }

/*
  public NFunctionZ32 getAllPrimes(int len) throws FileNotFoundException,
      IOException {
      matrix.MatrixReader reader = new matrix.MatrixReader(
          "primes"); // продумать, где будет храниться файл
      allPrimes = reader.readI1(0, len << 2, 1 << 23);
      reader.close();
      return this;
  }

*/

  /**  Порождение списка простых чисел в пределах 2^31.
   *
   * @param lim четная верхняя граница для списка простых чисел
   * @param startSize стартовый размер блока
   * @param blockSize размер блока при добавлении части решета Эратосфена.
   * @return список простых чисел, начиная с 3,5,7 и до числа, не превосходящего lim
   */

  public static int[] eratosphen(int lim, int startSize, int blockSize) {
        if ((startSize & 1) == 1) {
            startSize++;
        }
        int[] start = eratosphenStart(startSize);
        if ((lim & 1) == 1) {
            lim++;
        }
        return eratosphenAppend(lim, start, startSize, blockSize);
    }

    /**
     * Программа для решета Эратосфена для чисел от 0 до <tt>end-1</tt>
     *
     * @param end четное число, меньше которого будут найдены все простые числа.
     *
     * @return int[] , массив простых чисел строго меньших чем <tt>end </tt>
     */
    private static int[] eratosphenStart(int end) {
        int[] num = new int[end]; // нули в нечетных позициях -- будущие простые
        int sqrtend = (int) Math.sqrt(end);
        if ((sqrtend & 1) == 1) {
            sqrtend++;
        }
        for (int i = 3; i < sqrtend; i += 2) {
            if (num[i] == 0) {
                int j = 3 * i;
                int step = i << 1;
                for (; j < end; j += step) {
                    num[j] = 1;
                }
            }
        }
        int[] num1 = new int[end / 2];
        int pos = 0;
        for (int i = 3; i < end; i += 2) {
            if (num[i] == 0) {
                num1[pos++] = i;
            }
        }
        int[] primes = new int[pos];
        System.arraycopy(num1, 0, primes, 0, pos); // 2, 3, 5...
        return primes;
    }

//  totalPrimesNumb=105127303
//numbRecords=28673
//labP=57344
// 0=14630842 1=268435399 2=14634229 3=268500977 4=14637628 5=268566509 last=
// 0=105082266 1=2147155951 2=105085341 3=2147221487 4=105088411 5=2147287019 6=105091443 7=2147352563 8=105094536 9=2147418083 10=105127303 11=2147483645time==58855
//                                          214748364
    /**
     *
     * Программа для решета Эратосфена для чисел от 0 до <tt>End-1</tt>
     *
     * @param End четное число, меньше которого будут найдены все простые числа.
     * @param stPrimes , массив простых чисел строго меньших чем <tt>end </tt>
     * @param end четное число, меньше которого заданы все простые числа в
     * массиве stPrimes
     * @param
     * @return int[] , массив простых чисел строго меньших чем <tt>Еnd </tt>
     *
     */
    private static int[] eratosphenAppend(int End, int[] stPrimes, int end, int blockSize) {
        int len; // stPrimes.length -- число простых чисел в текущем списке
        if (end >= End) {
            return stPrimes;
        }
        int sqrtend = (int) Math.sqrt(End);
        if ((sqrtend & 1) == 1) {
            sqrtend++;
        }
        while (sqrtend > end) {
            stPrimes = eratosphenAppend(end * end, stPrimes, end, blockSize);
            end = end * end;
        }
        int sub = End - end;
        int parts = sub / blockSize;
        int[][] newPrs = new int[parts + 1][];
        int move = end;
        int move1 = end + blockSize;
        int sqrt = (int) Math.sqrt(move1);
        int memStep = 0;
        len = stPrimes.length;
        for (int partN = 0; partN < parts + 1; partN++) {
            int piece = (partN == parts) ? (sub - blockSize * (parts)) : blockSize;
            int[] pr = new int[piece];
            for (int i = 0; i < len; i++) {
                int j = stPrimes[i];
                int step = j << 1;
                int ss = end % j;
                int s = ((ss & 1) == 1) ? step - ss : j - ss;
                if (sqrt < j) {
                    break;
                }
                for (; s < piece; s += step) {
                    pr[s] = 1;
                }
            }
            int[] pr1 = new int[piece / 2];
            int pos = 0;
            for (int i = 1; i < piece; i += 2) {
                if (pr[i] == 0) {
                    pr1[pos++] = i + move;
                }
            }
            // Считали из массива pr1 все новые простые числа
            move = move1;
            move1 += blockSize;
            if (move1 < 0) {
                move1 = Integer.MAX_VALUE;
            }
            sqrt = (int) Math.sqrt(move1);
            int[] primes = new int[pos];
            System.arraycopy(pr1, 0, primes, 0, pos);
            newPrs[memStep++] = primes;
            end = move;
        }
        int size = len;
        for (int i = 0; i < memStep; i++) {
            size += newPrs[i].length;
        }
        int[] primes1 = new int[size];
        System.arraycopy(stPrimes, 0, primes1, 0, len);
        int lenn = len;
        for (int i = 0; i < memStep; i++) {
            int lenI = newPrs[i].length;
            System.arraycopy(newPrs[i], 0, primes1, lenn, lenI);
            lenn += lenI;
        }
        return primes1;
    }

    /**
     * Создать массив (long) prodPrimes произведений простых чисел и массив
     * индексов (int) prodInd для списка простых чисел. Индекс указывает на
     * номер самого большого простого числа, которое участвовало в произведении.
     * Простые числа берутся из файла (int) PPII[0].
     *
     * int[] Primes = first 155610 primes: 3,5,.., the last is 2097143. long[]
     * prodPrimes = products of Primes up to 63 bit-word (51351-lenght) int[]
     * prodInd = position of the last prime in each product (51351-lenght)
     */
    public static void doStaticPrimesProdAndInd() throws IOException {
        Primes = eratosphen(lastPrime + 1, 1 << 6, 1 << 11);
        prodPrimes = new long[prodNumb];
        prodInd = new int[prodNumb];
        long M = 0, M1 = 0;
        int wr = 0;
        NumberZ N = NumberZ.ONE, PR = null;
        for (int j = 0; j < primNumb; j++) {
            PR = new NumberZ(Primes[j]);
            M = M1;
            N = N.multiply(PR);
            M1 = N.longValue();
            if ((N.mag.length == 3) || (M1 < 0)) {
                prodPrimes[wr] = M;
                prodInd[wr++] = j - 1;
                N = PR;
            }
        }
        prodPrimes[wr] = M1;
        prodInd[wr] = primNumb - 1;
        if (!intPrimes.exists()) {
            doFileOfIntPrimes();
        }
    }

    /**
     * Do File Of Integer Primes from 3 upto Integer.MAX_VALUE-1. (The new file
     * "primesInt" will have size 420 640 272 bytes) Total number of
     * primes=105097563 The working time is about 42 sec. The method is similar
     * to eratosphenAppend().
     *
     * @throws IOException
     */
    public static void doFileOfIntPrimes() throws IOException {
        indexBlocks = new int[32768 * 2]; //Array for the indeces and vlues of prime
                                          //at the end of each block in the file of primes
        int labP = 0;
        int End = Integer.MAX_VALUE - 1; // количество всех число подлежащих просеиванию
        int end = blockSizeOfIntegers;  // количество уже просеенных

        int[] stPrimes = eratosphenStart(end);
        int len; // stPrimes.length -- число простых чисел в текущем списке
        //     int  sqrtend = (int) Math.sqrt(End); if ((sqrtend&1)==1) sqrtend++;
        //     while (sqrtend>end) {stPrimes=eratosphenAppend(end*end, stPrimes, end, blockSize); end=end*end;}
        int sub = End - end;
        int parts = sub / blockSizeOfIntegers;
        int move = end;
        int move1 = end + blockSizeOfIntegers;
        int sqrt = (int) Math.sqrt(move1);
        int memStep = 0;
        len = stPrimes.length;
        NumberFileWriter writer = new NumberFileWriter(intPrimes);
        NumberFileWriter writerLabs = new NumberFileWriter(intPrimesLabs);
        writer.write(stPrimes); // записываем в файл первую часть
        indexBlocks[0] = len;
        indexBlocks[1] = stPrimes[len - 1];
        int totalPrimesNumb = len;
        int numbRecords = 1;
        for (int partN = 0; partN < parts + 1; partN++) {
            int piece = (partN == parts) ? (sub - blockSizeOfIntegers * (parts)) : blockSizeOfIntegers;
            int[] pr = new int[piece];
            for (int i = 0; i < len; i++) {
                int j = stPrimes[i];
                int step = j << 1;
                int ss = end % j;
                int s = ((ss & 1) == 1) ? step - ss : j - ss;
                if (sqrt < j) {break;}
                for (; s < piece; s += step) {pr[s] = 1;}
            }
            int[] pr1 = new int[piece / 2];
            int pos = 0;
            for (int ii = 1; ii < piece; ii += 2) {
                if (pr[ii] == 0) {pr1[pos++] = ii + move;}
            }
            // Считали из массива pr1 все новые простые числа
            move = move1;
            move1 += blockSizeOfIntegers;
            if (move1 < 0) {move1 = Integer.MAX_VALUE;}
            sqrt = (int) Math.sqrt(move1);
            int[] primes = new int[pos];
            System.arraycopy(pr1, 0, primes, 0, pos);
            writer.write(primes); // записываем блоками по 1Mbytes
            indexBlocks[labP + 2] = pos + indexBlocks[labP]; // индекс последнего простого числа в файле
            indexBlocks[labP + 3] = primes[pos - 1];// значение последнего простого числа в блоке
            labP += 2;
            memStep++;
            end = move;
            totalPrimesNumb += pos;
            numbRecords += 1;
        }
        writerLabs.write(indexBlocks);
   //     System.out.println("totalPrimesNumb=" + totalPrimesNumb + " numbRecords=" + numbRecords);
        writer.close();
        writerLabs.close();
        // totalPrimesNumb=105097563,
        //last prime= 2147483629; 2^32-1=2147483647.
    }

    /**
     * Создать массив произведений простых чисел и массив индексов для списка
     * простых чисел
     *
     * @param end четная граница для массива простых чисел
     * @param indeces -- массив индексов (indeces[0]) и списко простых чисел
     * (indeces[1]), начиная с 3,5,7...
     *
     * @return произведения простых чисел, которые помещаются в long
     */
    public static long[] primeProducts(int end, int[][] indeces) {
        //  51351 - the length of Products Long-array up to 3 multipliers
        int[] primes = eratosphen(end, 1 << 6, 1 << 11);
        //  System.out.println("pimes="+Array.toString(primes));
        long M = 0, M1 = 0;
        int wr = 0;
        NumberZ N = NumberZ.ONE;
        int primesN = primes.length;
        long[] prProd = new long[primesN / 2];
        int[] index = new int[primesN / 2];
        for (int i = 0; i < primesN; i++) {
            M = M1;
            N = N.multiply(new NumberZ(primes[i]));
            M1 = N.longValue();
            if ((N.mag.length == 3) || (M1 < 0)) {
                prProd[wr] = M;
                index[wr++] = i - 1;
                N = new NumberZ(primes[i]);
            }
        }
        prProd[wr] = N.longValue();
        index[wr++] = primesN - 1;
        long[] prProd1 = new long[wr];
        System.arraycopy(prProd, 0, prProd1, 0, wr);
        int[] index1 = new int[wr];
        System.arraycopy(index, 0, index1, 0, wr);
        indeces[0] = index1;
        indeces[1] = primes;
        return prProd1;
    }

    /**
     * Решето Эратосфена, используются 2 массива для вычеркивания, вместо
     * одного. Для экономии памяти, четные числа изначально не рассматриваются.
     * Будут возвращены простые числа от 3 до 2<tt>end</tt>.
     *
     * @param end -- половина границы простых чисел
     *
     * @return int[]
     */
    public static int[] eratosphen2x2(int end) {
        // инициализация
        int[] num1 = new int[end / 2 - 1];
        int[] num2 = new int[end - end / 2 - 1];
        int end2 = end >>> 1, endm2 = end - 2;
        int nums1 = num1.length, nums2 = num2.length,
                sqrtend = (int) Math.sqrt(end), foundPrimes = 0, temp, j;
        for (int i = 0; i < nums1; i++) {
            num1[i] = (i << 1) + 3;
        }
        for (int i = 0; i < nums2; i++) {
            num2[i] = (i << 1) + 1 + end;
        }
        // занулим все непростые элементы
        for (int i = 0; i < sqrtend; i++) {
            if (num1[i] != 0) { // корень из числа меньше его половины, поэтому по второму массиву идти нет смысла.
                temp = num1[i];
                foundPrimes++;
                j = i + temp;
                for (; j < nums1; j += temp) {
                    num1[j] = 0;
                }
                for (; j < endm2; j += temp) {
                    num2[j - end2 + 1] = 0;
                }
            }
        }
        for (int i = sqrtend; i < nums1; i++) {
            if (num1[i] != 0) {
                foundPrimes++;
            }
        }
        for (int i = 0; i < nums2; i++) {
            if (num2[i] != 0) {
                foundPrimes++;
            }
        }
        // ненулевые элементы -- простые, перепишем их в отдельный массив по убыванию
        int[] prims = new int[foundPrimes];
        foundPrimes = 0;
        for (int i = nums2 - 1; i >= 0; i--) {
            if (num2[i] > 0) {
                prims[foundPrimes++] = num2[i];
            }
        }
        for (int i = nums1 - 1; i >= 0; i--) {
            if (num1[i] > 0) {
                prims[foundPrimes++] = num1[i];
            }
        }
        return prims;
    }

    /**
     * Создает файл с простыми числами 2,...,2^28. ACHTUNG: Если у вас меньше
     * 600 Мб свободной памяти, процедура не сможет работать. Попросите
     * сгенерировать файл кого-нибудь другого.
     *
     * @throws IOException / public static void createPrimesFile() throws
     * IOException { int[] prim = eratosphen2x2(1 << 27);
     * matrix.NumberFileWriter writer = new matrix.NumberFileWriter("primes");
     * writer.write(prim, 1 << 20); //writer.write(prim); writer.close(); }
     *
     *
     */
//______________________________________________________________________________
//Нахождение следующего за first простого числа (по убывающей). Вероятность
//того, что найденное число простое равна (1 - 1/(2^certainty)).
// -- Ну очень хитро -- через BigInteger.! (нужно исправить)
//______________________________________________________________________________
    public static int NextPrime(int first, int certainty) {
        do {
            first -= 2;
        } while (!NumberZ.valueOf(first).isProbablePrime(certainty));
        return first;
    }

// /////////////////////////////////////////////////////////////////////////////
// вычисляет обратное к b по простому модулю m (случай b=1 не проверяется)
//   xy[0] содержит b^{-1} mod p. При обращении q1, xy[0] и xy[1] нужно занулить
////////////////////////////////////////////////////////////////////////////////
    public static void m_Inverse(int m, int b, int q1, int[] xy) {
        int r, q, temp;
        r = m % b;
        q = (m - r) / b;
        if (r == -1) {
            xy[0] = -1;
            xy[1] = q;
        } else if (r == 1) {
            xy[0] = 1;
            xy[1] = -q;
        } else {
            m_Inverse(b, r, q, xy);
        }
        temp = xy[0];
        xy[0] = xy[1];
        xy[1] = temp - q1 * xy[1];
    }

// /////////////////////////////////////////////////////////////
// вычисляет обратное к a по простому модулю p  (общий случай) //
    public static int p_Inverse(int a, int p) {
        if (a == 1) {
            return 1;
        } else if (a == -1) {
            return -1;
        } else {
            int xy[] = new int[2];
            xy[0] = 0;
            xy[1] = 1;
            int qq = 0;
            m_Inverse(p, a, qq, xy);
            return xy[0];
        }
    }

/////////////////////////////////////////////////////////////////
// Обратное к a по простому модулю  p.
    public static long p_Inverse(long a, long p) {
        int aa = (int) (a % p);
        int pp = (int) p;
        if (aa == 1) {
            return 1;
        } else if (aa == -1) {
            return -1;
        } else {
            int xy[] = new int[2];
            xy[0] = 0;
            xy[1] = 1;
            int qq = 0;
            m_Inverse(pp, aa, qq, xy);
            return (long) xy[0];
        }
    }

    /////////////////////////////////////////////////////////////////
// Обратное к a по простому модулю  p.
    public static long p_Inverse(NumberZ a, long p) {
        int aa = (a.mod(NumberZ.valueOf(p))).intValue();
        int pp = (int) p;
        if (aa == 1) {
            return 1;
        } else if (aa == -1) {
            return -1;
        } else {
            int xy[] = new int[2];
            xy[0] = 0;
            xy[1] = 1;
            int qq = 0;
            m_Inverse(pp, aa, qq, xy);
            return (long) xy[0];
        }
    }

//______________________________________________________________________________
//
//Возвращает значение (x mod p) в интервале [-(p-1)/2,+(p-1)/2].
    public static long mod(long x, long p) {
        long z;
        z = x % p;
        if (Math.abs(z) < (p + 1) / 2) {
            return z;
        } else if (z > 0) {
            return z - p;
        } else {
            return z + p;
        }
    }

    public static void m_Inverse(NumberZ m, NumberZ b, NumberZ q1, NumberZ[] xy) {
        NumberZ r, q, temp;
        r = m.mod(b);
        q = (m.subtract(r)).divide(b);
        if (r.equals(NumberZ.ONE.negate())) {
            xy[0] = (NumberZ) NumberZ.ONE.negate();
            xy[1] = q;
        } else if (r.equals(NumberZ.ONE)) {
            xy[0] = NumberZ.ONE;
            xy[1] = (NumberZ) q.negate();
        } else {
            if (r.equals(NumberZ.ZERO)) {
                throw new ArithmeticException("m_Invers: WARNING: divide by zero");

            }
            m_Inverse(b, r, q, xy);
        }
        temp = xy[0];
        xy[0] = xy[1];
        xy[1] = temp.subtract(q1.multiply(xy[1]));
    }

    public static NumberZ p_Inverse(NumberZ a, NumberZ p) {
        NumberZ aa = a.mod(p);
        if (aa.equals(NumberZ.ONE)) {
            return NumberZ.ONE;
        } else if (aa.equals(NumberZ.ONE.negate())) {
            return (NumberZ) NumberZ.ONE.negate();
        } else {
            NumberZ xy[] = new NumberZ[2];
            xy[0] = NumberZ.ZERO;
            xy[1] = NumberZ.ONE;
            NumberZ qq = NumberZ.ZERO;
            m_Inverse(p, aa, qq, xy);
            return xy[0];
        }
    }

    /**
     * Метод возвращающий обратное число к a по модулю p (алгоритм Евклида)
     *
     * @param a число типа BigInteger
     * @param p число типа BigInteger
     *
     * @return обратное число к a по модулю p [a^{-1} mod p]
     */
    public static NumberZ inversBI(NumberZ a, NumberZ p) {
        a = a.mod(p);
        if (a.equals(NumberZ.ONE)) {
            return NumberZ.ONE;
        } else if (a.equals(NumberZ.ONE.negate())) {
            return (NumberZ) NumberZ.ONE.negate();
        }
        NumberZ ofA, tempA = NumberZ.ZERO, result;
        NumberZ[] b = new NumberZ[2];
        NumberZ[] q = new NumberZ[2];
        b[1] = a;
        q[1] = p;
        //Первое деление чисел
        b = b[1].divideAndRemainder(q[1]);
        if (b[1].signum() != 0) {
            result = NumberZ.ONE;
        } else {
            result = NumberZ.ZERO;
        }
        // Деления двух чисел по алгоритму Евклида
        while (!b[1].equals(NumberZ.ONE) && !q[1].equals(NumberZ.ONE)
                && !b[1].equals(NumberZ.ZERO) && !q[1].equals(NumberZ.ZERO)) {
            if ((b[1]).compareTo(q[1]) == 1) {
                b = b[1].divideAndRemainder(q[1]);
                if (b[1].equals(NumberZ.ZERO)) {
                    break;
                }
                //нахождение обратного по модулю
                ofA = tempA;
                tempA = result;
                result = ofA.subtract(b[0].multiply(result));
            } else {
                q = q[1].divideAndRemainder(b[1]);
                if (q[1].equals(NumberZ.ZERO)) {
                    break;
                }
                //нахождение обратного по модулю
                ofA = tempA;
                tempA = result;
                result = ofA.subtract(q[0].multiply(result));
            }
        }
        //вывод результата
        return result;
    }

//______________________________________________________________________________
//
//Возвращает значение (x mod m) в интервале [-(m-1)/2,+(m-1)/2].
    public static int mod(int x, int p) {
        int z;
        z = x % p;
        if (Math.abs(z) < (p + 1) / 2) {
            return z;
        } else if (z > 0) {
            return z - p;
        } else {
            return z + p;
        }
    }

    /////////////////////////////////////////////////////////////////
//______________________________________________________________________________
//
//Возвращает значение (x mod p) в интервале [-(p-1)/2,+(p-1)/2].
    public static NumberZ mod(NumberZ x, NumberZ p) {
        NumberZ z = x.mod(p);
        NumberZ two = new NumberZ("2");
        NumberZ dim = p.add(NumberZ.ONE).divide(two);
        if (z.abs().compareTo(dim) == -1) {
            return z;
        } else if (z.signum() == 1) {
            return z.subtract(p);
        } else {
            return z.add(p);
        }
    }

//______________________________________________________________________________
//
//Возвращает НОД чисел типа long
    public static long gcd(long a, long b) {
        if (a == b) {
            return a;
        }
        while (a > 0 && b > 0) {
            if (a > b) {
                a = a % b;
            } else {
                b = b % a;
            }
        }
        if (a == 0) {
            return b;
        } else {
            return a;
        }
    }

    /**
     * Involution of the diagonal matrix ei of order N. Algorithm: any diagonal
     * element changes its value (1 -> 0 and 0 -> 1).
     *
     * @param ei входная матрица -- diagonal matrix
     * @param N order of the matrix
     *
     * @return <tt> involution </tt> -- complimentary diagonal matrix
     *
     */
    public static int[] involution(int[] ei, int N) {
        int len = ei.length;
        int[] ind = new int[N];
        int[] Dei = new int[N - len];
        if (len < N) {
            for (int j = 0; j < len; j++) {
                ind[ei[j]] = 1;
            }
            int k = 0;
            for (int j = 0; j < N; j++) {
                if (ind[j] == 0) {
                    Dei[k++] = j;
                }
            }
        }
        return Dei;
    }

//______________________________________________________________________________
// Not bad code, bat the cases n=0 and n=1 may be done better!!
//Возвращает число типа long возведённое в степень типа int по модулю mod
    public static long pow(long a, int n, long mod) {
        //   a=mod(a,mod);
        if (a == 1) {
            return 1;
        }
        if (a == -1) {
            return ((n & 1) == 1) ? -1 : 1;
        }
        //обрезание степени по теореме Ferma
        if (n > mod) {
            n %= mod - 1;
        }
        //проверка степени на равенство 1
        if (n == 1) {
            return a;
        }
        //проверка степени на равенство 0
        if (n == 0) {
            return 1;
        }

        long res = 1, temp = a;
        if ((n & 1) == 1) {
            res = a;
        }
        n >>>= 1;
        while (n != 0) {
            temp = (temp * temp) % mod;
            if ((n & 1) == 1) {
                res = (res * temp) % mod;
            }
            n >>>= 1;
        }
        return res;
    }

    //______________________________________________________________________________
// Terribl PROGRAM ?????????????????     !!!!! PLEASE CHANGE IT!!!!!!!
//____________________________________________________________________________
//Возвращает число типа long возведённое в степень типа int по модулю mod
    public static long powFromBig(long a, int n, long mod) {
        //обрезание степени по теореме
        if (n > mod) {
            n %= mod - 1;
        }
        NumberZ a1 = NumberZ.valueOf(a);
        NumberZ mod1 = NumberZ.valueOf(mod);
        NumberZ res1 = (NumberZ) a1.pow(n);
        return ((res1.mod(mod1))).longValue();

    }

    /**
     * Создает файл с простыми числами 2,...,2^28. ACHTUNG: Если у вас меньше
     * 600 Мб свободной памяти, процедура не сможет работать. Попросите
     * сгенерировать файл кого-нибудь другого.
     *
     * @throws IOException
     */
    public static void createPrimesFile(int endPrime) throws IOException {
        //      File  CheckPrime = new File(MATHPAR_DIR, sessionId);
        int[] prim = eratosphen(endPrime, 1 << 6, 1 << 11); // 1<<27
        NumberFileWriter writer = new NumberFileWriter(primesFile);
        writer.write(prim, 1 << 20); // записываем блоками по 1Mbytes
        //      NumberFileReader reader = new  NumberFileReader("primes");
        //    int[] prim1= reader.readIntArray();
        //    System.out.println("prim1="+prim1[0]+"  "+prim1[prim1.length-1]+"numbers="+prim1.length);
        //writer.write(prim);
        writer.close();
    }

    /**
     * Создает файл с простыми числами 2,...,2^28. ACHTUNG: Если у вас меньше
     * 600 Мб свободной памяти, процедура не сможет работать. Попросите
     * сгенерировать файл кого-нибудь другого.
     *
     * @throws IOException
     */
    public static void appendPrimesFile(int endPrime) throws IOException {
        NumberFileReader reader = new NumberFileReader(NFunctionZ32.primesFile);
        int[] prim1 = reader.readIntArray(1 << 16);
        int[] prim = eratosphen2x2((1 << 26) + (1 << 25)); // 1<<27
        //   int lastPrime=2;
        NumberFileWriter writer = new NumberFileWriter(primesFile);
        writer.write(prim, 1 << 20); // записываем блоками по 1Mbytes
        //   NumberFileReader reader1 = new  NumberFileReader("primes");
        //   int[] prim2= reader1.readIntArray();
        //     System.out.println("prim1="+prim1[0]+"  "+prim1[prim1.length-1]+"numbers="+prim1.length);
        //writer.write(prim);
        writer.close();
    }

    /**
     * factoring the Long number
     *
     * @param x is a long number to factoring
     *
     * @return FactorPol with factors and powers
     */
    public static FactorPol factoringLong(long x) throws IOException {
        if (!intPrimes.exists()) {
            doFileOfIntPrimes();
        }
        if (prodPrimes == null) {
            doStaticPrimesProdAndInd();
        }
//    long   tt3=System.currentTimeMillis();
//    int[][] indeces=new int[2][0];
        int sqrtend = (int) Math.sqrt(x);
        if ((sqrtend & 1) == 1) {
            sqrtend++;
        }
        //   long[] prProd=primeProducts(lastPrime+1 , indeces);
//      long   tt4=System.currentTimeMillis();
//      System.out.println("TIME+++="+(tt4-tt3));
//
        //   int[]  index=indeces[0]; int[] primes=indeces[1];
        //   System.out.println("primes.length="+primes.length);
        //   System.out.println(primes[0]+"  prProd="+prProd.length);//+Array.toString(prProd));
        //   System.out.println("index1="+primes.length+"  "+  primes[primes.length-1] );//+Array.toString(primes));
        int n = prodPrimes.length;
        int fN = 0; // couner for factors (1 )
        long[] factor = new long[30];   // for factors
        int[] pow = new int[30];        // powers for factors
        if ((x & 1) == 0) {
            factor[0] = 2;
            int s = 0;
            while ((x & 1) == 0) {
                x >>= 1;
                s++;
            }
            pow[0] = s;
            fN = 1;
        }
        int[] mulP = new int[40]; // больше в один лонг не помещается простых чисел
        long[] ser = new long[60];
        int i = 0;
        int nn = 0, predIn = Primes.length;
        predIn = -1;
        while ((x != 1) && (i < prodInd.length)) {
            long gcd = 0;
            int sN = 0;
            while ((i < prodPrimes.length) && (gcd != 1)) {
                long a = prodPrimes[i];
                gcd = gcd(x, a);
                ser[sN++] = gcd;
                x /= gcd;
            }
            if (sN > 1) {
                nn = prodInd[i] - predIn;
                System.arraycopy(Primes, predIn + 1, mulP, 0, nn);
            }
            predIn = prodInd[i++];
            for (int j = 1; j < sN; j++) {
                long aj = ser[j - 1] / ser[j];
                int k = nn - 1;
                while ((k > -1) && (aj != 1)) {
                    if (aj % mulP[k] == 0) {
                        aj /= mulP[k];
                        factor[fN] = mulP[k];
                        pow[fN++] = j;
                    }
                    k--;
                }
            }
        }
        if (x != 1) {
            pow[fN] = 1;
            factor[fN++] = x;
        }
        Polynom[] factor1 = new Polynom[fN];
        for (int j = 0; j < fN; j++) {
            factor1[j] = Polynom.polynomFromNumber(new NumberZ(factor[j]), Ring.ringZxyz);
        }
        int[] pow1 = new int[fN];
        System.arraycopy(pow, 0, pow1, 0, fN);
        return new FactorPol(pow1, factor1);
    }

    
    
    
    /**
     * factoring the Long number
     *
     * @param x is a long number to factoring
     *
     * @return FactorPol with factors and powers
     */
    public static FactorPol factoringLong(long x, int start, int end) throws IOException {
        if (!intPrimes.exists()) {
            doFileOfIntPrimes();
        }
        if (prodPrimes == null) {
            doStaticPrimesProdAndInd();
        }
//    long   tt3=System.currentTimeMillis();
//    int[][] indeces=new int[2][0];
        int sqrtend = (int) Math.sqrt(x);
        if ((sqrtend & 1) == 1) {sqrtend++;}
        //   long[] prProd=primeProducts(lastPrime+1 , indeces);
//      long   tt4=System.currentTimeMillis();
//      System.out.println("TIME+++="+(tt4-tt3));
//
        //   int[]  index=indeces[0]; int[] primes=indeces[1];
        //   System.out.println("primes.length="+primes.length);
        //   System.out.println(primes[0]+"  prProd="+prProd.length);//+Array.toString(prProd));
        //   System.out.println("index1="+primes.length+"  "+  primes[primes.length-1] );//+Array.toString(primes));
        int n = prodPrimes.length;
        int fN = 0; // counter for factors (1 )
        long[] factor = new long[30];   // for factors
        int[] pow = new int[30];        // powers for factors
        if ((x & 1) == 0) {
            factor[0] = 2;
            int s = 0;
            while ((x & 1) == 0) {x >>= 1;s++;}
            pow[0] = s;
            fN = 1;
        }
        int[] mulP = new int[40]; // больше в один лонг не помещается простых чисел
        long[] ser = new long[60];
        int i = start;
        int nn = 0, predIn = end;
        predIn = -1;
        while ((x != 1) && (i < end)) {
            long gcd = 0;
            int sN = 0;
            while ((i < end) && (gcd != 1)) {
                long a = prodPrimes[i];
                gcd = gcd(x, a);
                ser[sN++] = gcd;
                x /= gcd;
            }
            if (sN > 1) {
                nn = prodInd[i] - predIn;
                System.arraycopy(Primes, predIn + 1, mulP, 0, nn);
            }
            predIn = prodInd[i++];
            for (int j = 1; j < sN; j++) {
                long aj = ser[j - 1] / ser[j];
                int k = nn - 1;
                while ((k > -1) && (aj != 1)) {
                    if (aj % mulP[k] == 0) {
                        aj /= mulP[k];
                        factor[fN] = mulP[k];
                        pow[fN++] = j;
                    }
                    k--;
                }
            }
        }
        if (x != 1) {
            pow[fN] = 1;
            factor[fN++] = x;
        }
        Polynom[] factor1 = new Polynom[fN];
        for (int j = 0; j < fN; j++) {
            factor1[j] = Polynom.polynomFromNumber(new NumberZ(factor[j]), Ring.ringZxyz);
        }
        int[] pow1 = new int[fN];
        System.arraycopy(pow, 0, pow1, 0, fN);
        return new FactorPol(pow1, factor1);
    }

    public static FactorPol factLong(long x, Ring ring) throws IOException {
        try {
            return factLong(x, 1, 0); // int size, int rank
        } catch (FileNotFoundException ex) {
            doFileOfIntPrimes();
            doStaticPrimesProdAndInd();
            ring.exception.append("We have done files with prime numbers");
        } catch (IOException ex) {
            ring.exception.append("Problems with IOException: " + ex);
        }
        return new FactorPol(new NumberZ(x), ring);
    }

    /**
     * Factoring the Long number. primesInt -- file of primes; prodPrimsInt --
     * file of primes products; indexProdInt -- file of index of primes products
     * in file of primes;
     *
     * @param x is a long number to factoring
     *
     * @return FactorPol with factors and powers
     */
    public static FactorPol factLong(long x, int size, int rank) throws IOException {
        NumberFileReader readPrimes = new NumberFileReader(intPrimes);
        ArrayList<int[]> ALprimes = new ArrayList<int[]>();
        int sqrtend = (int) Math.sqrt(x);
        if ((sqrtend & 1) == 1) {
            sqrtend++;
        }
        int lastPr = 0;
        int blockNumb = 0;
        int Primes[] = null;
        long Prod[] = null;
        int Ind[] = null;
        int[] IndBl = indexBlocks;

        if (indexBlocks == null) {
            doFileOfIntPrimes();
            IndBl = indexBlocks;
        }
        //   System.out.println("len IndBl="+IndBl.length+ "  "+ IndBl[IndBl.length-1]+"  "+ Integer.MAX_VALUE );
        // IndBl[i] хранит значение последнего простого числа в i-том блоке.
        // найдем число блоков, которые требуются для факторизации заданного простого
        int i = 0;
        for (; i < IndBl.length; i++) {
            System.out.println("IndBl[i] "+IndBl[i]);
            if (IndBl[i] >= sqrtend) {
                break;
            }
        }
        if (i == IndBl.length) {
            System.out.println("Too big number for factoring ... not supported now!");
            return //FactorPol.ONE.m
                    new FactorPol(new NumberZ(x), Ring.ringZxyz);
        }
        System.out.println("IndBl.length "+IndBl.length);
        int totalProds = (1 << 17) * (i + 1);
        int prodsForOne = totalProds / size;
        // число произвелдений простых на 1 процессор
        int myFirst = rank * totalProds / size;
        int nextFirst = (rank + 1) * totalProds / size;
        //  while (lastPr < sqrtend) {
        Primes = readPrimes.readtoIntArray(1 << 20);
          Prod  = readPrimes.readtoLongArray(1 << 16);
          Ind  = readPrimes.readtoIntArray(1 << 16);
//        ALprimes.add(Primes);
//        ALprod.add(Prod);
//        ALindex.add(Ind);
        lastPr = Primes[Primes.length - 1];
        blockNumb++;
        //}
//
//        for (int j = 51349; j < 51351; j++) {
//
//
//            System.out.println(j + " ##=" + (Ind[j]) + " " + Primes[Ind[j]]);
//        }
        int tempBlokN = blockNumb - 1;
        int pos;
        for (pos = 0; pos < Primes.length; pos++) {

            while ((pos > 0) && (Primes[pos] > sqrtend)) {
                pos--;
            }
            if (pos == -1) {
                if (--tempBlokN < 0) {
                    break;
                }
                Primes = ALprimes.get(tempBlokN);
            } else {
                break;
            }
        }
        if (tempBlokN == -1) {
            return new FactorPol(new int[] {1},
                    new Polynom[] {Polynom.polynomFromNot0Number(NumberZ.valueOf(x))});
        }
        int numbOfPrime = tempBlokN * (1 << 16) + pos;

        int tempBlokIndex = blockNumb - 1;
        int indexSqrt = 0;
        while (true) {
            pos = Ind.length - 1;
            while ((pos > 0) && (Ind[pos] > numbOfPrime)) {
                pos--;
            }
            if (pos == -1) {
                if (--tempBlokIndex < 0) {
                    break;
                }
                Ind = null; //ALindex.get(tempBlokIndex);
            } else {
                break;
            }
        }
        if (tempBlokIndex > -1) {
            indexSqrt = tempBlokIndex * (1 << 15) + pos;
        } else {
            pos = 0;
        }

        int q = indexSqrt / size;
        int r = indexSqrt % size;
        int myInd0 = q * rank;
        if (rank > r) {
            myInd0 += r;
        } else {
            myInd0 += rank;
        }
        int myInd1 = myInd0 + q;
        if (rank < r) {
            myInd1++;
        }

        //   System.out.println("primes.length="+primes.length)
        //   System.out.println(primes[0]+"  prProd="+Array.toString(prProd));
        //   System.out.println("index1="+Array.toString(primes));
        int fN = 0; // couner for factors (1 )
        long[] factor = new long[30];   // for factors
        int[] pow = new int[30];        // powers for factors
        if ((x & 1) == 0) {
            factor[0] = 2;
            int s = 0;
            while ((x & 1) == 0) {
                x >>= 1;
                s++;
            }
            pow[0] = s;
            fN = 1;
        }
        int[] mulP = new int[40]; // больше в один лонг не помещается простых чисел
        long[] ser = new long[60];
        int partNind = myInd0 / (1 << 15);
        int partNindLast = myInd1 / (1 << 15);
        long gcd = 0;
        int sN = 0;
        for (int k = partNind; k < partNindLast + 1; k++) {
            //Ind = null; //ALindex.get(k);
            //Prod = null; //ALprod.get(k);
            int border = (k == partNindLast) ? myInd1 % (1 << 15) + 1 : 1 << 15;
            i = (k == partNind) ? myInd0 % (1 << 15) : 0;
            int nn = 0, predIn = primes.length;
            predIn = -1;
            while (i < border) {
                while ((i < 30) && (gcd != 1)) {
                    long a = Prod[i];
                    gcd = gcd(x, a);
                    ser[sN++] = gcd;
                    x /= gcd;
                }
                if (sN > 1) {
                    nn = Ind[i] - predIn;
                    System.arraycopy(Primes, predIn + 1, mulP, 0, nn);
                }
                predIn = Ind[i++];
                for (int j = 1; j < sN; j++) {
                    long aj = ser[j - 1] / ser[j];
                    int kk = nn - 1;
                    while ((kk > -1) && (aj != 1)) {
                        if (aj % mulP[kk] == 0) {
                            aj /= mulP[k];
                            factor[fN] = mulP[kk];
                            pow[fN++] = j;
                        }
                        kk--;
                    }
                }
            }
        }
        if (x != 1) {
            pow[fN] = 1;
            factor[fN++] = x;
        }
        Polynom[] factor1 = new Polynom[fN];
        for (int j = 0; j < fN; j++) {
            factor1[j] = Polynom.polynomFromNumber(new NumberZ(factor[j]), Ring.ringZxyz);
        }
        int[] pow1 = new int[fN];
        System.arraycopy(pow, 0, pow1, 0, fN);
        return new FactorPol(pow1, factor1);



    }
    
    public static NumberZ[] primesBigLimit(Element limit, int min_count) throws IOException{
        if(!intPrimesLabs.exists() && !intPrimes.exists()){
            System.out.print("Generating Primes"+"\n");
            doFileOfIntPrimes();
        }
        ArrayList<Element> primesNumbers = new ArrayList<>();
        Element limit_current = Ring.ringZxyz.numberONE();
        int block = 1;
        int attempt = 0;
        try{
            int[] blockOfPrimesFromBack = readBlockOfPrimesFromBack(block, Ring.ringZxyz);
            int position = blockOfPrimesFromBack.length;
            do{
                if(0==position){
                    block++;
                    blockOfPrimesFromBack = readBlockOfPrimesFromBack(block, Ring.ringZxyz);
                    position = blockOfPrimesFromBack.length;
                }
                position--;
                Element prime = new NumberZ(blockOfPrimesFromBack[position]);
                primesNumbers.add(prime);
                limit_current = limit_current.multiply(prime, Ring.ringZxyz);    
            }
            while(limit.compareTo(limit_current, 2, Ring.ringZxyz)); 
            int size_array = primesNumbers.size();
            int rem = size_array%min_count;
            if(rem>0){
                int t_rem = min_count-rem;
                while(t_rem>0){
                    if(0==position){
                        block++;
                        blockOfPrimesFromBack = readBlockOfPrimesFromBack(block, Ring.ringZxyz);
                        position = blockOfPrimesFromBack.length;
                    }
                    position--;
                    Element prime = new NumberZ(blockOfPrimesFromBack[position]);
                    primesNumbers.add(prime);
                    t_rem--;
                }
            }
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Error while generating primes");
//            if(intPrimes.exists()){
//                intPrimes.delete();
//            }
//            if(intPrimesLabs.exists()){
//                intPrimesLabs.delete();
//            }
//            if(primesFile.exists()){
//                primesFile.delete();
//            }            
//            System.out.print("Regenerating primes"+"\n");
//            doFileOfIntPrimes();
        }
        return (NumberZ[]) primesNumbers.toArray(new NumberZ[primesNumbers.size()]);
    };
    /**
     * 
     * @param count - количество возвращаемых простых чисел
     * @return массив простых чисел, которые меньше Integer.MAX_VALUE,
     * @throws IOException 
     */
    public static NumberZ[] primesBig(int count) throws IOException{
        if(!intPrimesLabs.exists() && !intPrimes.exists()){            
//            System.out.println("genereting primes file");
//            System.out.println("primesFile= "+primesFile.getAbsolutePath());
//            System.out.println("intPrimes= "+intPrimes.getAbsolutePath());
            doFileOfIntPrimes();
        }
        NumberZ[] primes = new NumberZ[count];
        int block = 1;
        int[] blockOfPrimesFromBack = readBlockOfPrimesFromBack(block, Ring.ringZxyz);
        int position = blockOfPrimesFromBack.length;
        for (int i = 0; i < primes.length; i++) {
            if(0==position){
                block++;
                blockOfPrimesFromBack = readBlockOfPrimesFromBack(block, Ring.ringZxyz);
                position = blockOfPrimesFromBack.length;
            }
            position--;
            primes[i] = new NumberZ(blockOfPrimesFromBack[position]);
        }
        return primes;
    };    

    public static void main1(String[] args) throws IOException {
        //    System.out.println("hello File"); Element ee=new Element();
        //   try {
//            long t1=System.currentTimeMillis();
//           //  createPrimesFile(1<<26);
//    int[][] indeces=new int[2][0];
//    long[] prProd=primeProducts(128, indeces);
//    int[]  index=indeces[0]; int[] primes=indeces[1];
//    System.out.println(primes[38]+"  prProd="+Array.toString(prProd));
//    System.out.println("index="+Array.toString(indeces[0]));
//
//            FactorPol fp= factoringLong( 253 );
//            System.out.println("fp="+fp);
        // int[] prr=eratosphenStart();
        //       System.out.println("start!!="+Array.toString(prr));
        //   System.out.println("start=len="+  prr.length+"  "+ prr[prr.length-1]);
        //      int[] prr1=eratosphenAppend(1<<7,prr, 1<<3);
        //     System.out.println("start=len="+  prr1.length+"  "+ prr1[prr1.length-1]);
        for (int i = (1 << 18) + 7440; i < 1 << 19//(int)((1L<<30)-2);  (i>0)
                ; i = (i << 1)) {
            for (int j = 512; j < 513; j = (j << 1)) {
                int i2 = i >> 1;

                //     System.out.println(Integer.MAX_VALUE);//



                long tt2 = System.currentTimeMillis();
                //    int[]    mmm1=  eratosphen ( i  , 1<<9 , 1<<16 );
                //      int[]    mmm1=  eratosphen ( i  , 1<<4 , 1<<11 );
                //         long   tt3=System.currentTimeMillis();
                //         System.out.println("start="+j+" time="+(tt3-tt2)+"   len="+mmm1.length+" lastPrime="+ mmm1[mmm1.length-1]);
                //         System.out.println(i +"   "+  (1<<9)+ "    "+ (1<<16) );
                //         long t2=System.currentTimeMillis();
                //     mmm1=  eratosphen2x2(i2 );//, 100000);
                //   doFileOfIntPrimes();
                long tt3 = System.currentTimeMillis();
                //      int[] eratosphen1=eratosphen(((Integer.MAX_VALUE-1)/2)  ,1<<9,1<<16);
                //            System.out.println(((Integer.MAX_VALUE-1)/2) +"  len="+eratosphen1.length+"  "+ eratosphen1[eratosphen1.length-1]);
                //     doStaticPrimesProdAndInd();
                System.out.println("time==" + (tt3 - tt2));

//            Ring ring = new Ring("Z[]");
//          long tt6=System.currentTimeMillis();
                //  FactorPol fff= factoringLong(120);//2147483629);// 2147483645L);// 4398008762449L);
                //       long tt3=System.currentTimeMillis();
                //        System.out.println("i="+i+" time="+(tt3-tt6)+"   len=  lastPrime=" );
                //    System.out.println(fff+"  "+Integer.MAX_VALUE) ; //}
                // 10000000: we==840, 2x2=695
                // 1000000: we==78, 2x2=321
//           ("primesInt");
//    NumberFileReader readProd = new  NumberFileReader("prodPrimsInt");
//    NumberFileReader readInd =  new  NumberFileReader("indexProdInt");

                //    createPrimesFile(Integer.MAX_VALUE-1);


            }
            //      eratosphenAppend
            // blockSize=110 is the best
        }
        NumberR rr = new NumberR("0.000012322");
        int ii = rr.digitLength();
        ii -= rr.scale;
        System.out.println("ii=" + ii);

        System.out.println(
                    factoringLong(512));
    }
//
//FileInputStream in = null;
//FileOutputStream out = null;
//try {
// in = new FileInputStream("a.txt");
// out = new FileOutputStream("b.txt");
// int c;
// while ((c = in.read()) != -1) { out.write(c); }
//} finally {
// if (in != null) { in.close(); }
// if (out != null) { out.close(); }
//}

    public static void main(String[] args) throws IOException {

         doFileOfIntPrimes();
       // THIS opeerator you have to used only one time. As the result you
       // obtain file of primes in you Hard Drive
       // Then you need onle "readBlockOfPrimesFromBack" function, which returns you
       // array of primes that you want.

       // Нужно вызывать readBlockOfPrimesFromBack(1, ring). будет получен массив из
       //  3027 самых больших простых чисел, упорядоченных от младших к старшим.
        // Его нужно использовать выбирая элементы в обычном порядке 0, 1, 2,..
        // Если окажется, что их мало, то нужно считать следующий блок
         // readBlockOfPrimesFromBack(2, Ring) и брать числа из него. И так далее.
         // Вот тут примеры
         int[]fff= readBlockOfPrimesFromBack(1, Ring.ringZxyz);
        for (int i = fff.length-5; i < fff.length; i++) {
            System.out.println("fff"+i+"="+fff[i]);

        System.out.println("fff len="+fff.length);
        int[]fff2= readBlockOfPrimesFromBack(2, Ring.ringZxyz);
        System.out.println("fff2 len="+fff2.length);
                int[]fff3= readBlockOfPrimesFromBack(3, Ring.ringZxyz);
        int n=indexBlocks.length/2;
        System.out.println("fffl3 len="+fff3.length);
            int[]fffn= readBlockOfPrimesFromBack(n, Ring.ringZxyz);
        System.out.println(" n="+n);
                System.out.println(" len="+fffn.length);
        System.out.println(" fffn="+fffn[0]);
        System.out.println(" fffn="+fffn[1]);

        }
}
}
