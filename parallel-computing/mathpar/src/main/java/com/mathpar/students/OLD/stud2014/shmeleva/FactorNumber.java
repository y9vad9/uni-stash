/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.shmeleva;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import mpi.*;
import com.mathpar.number.Array;
import com.mathpar.number.NFunctionZ32;
import com.mathpar.number.NumberFileReader;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.FactorPol;
import com.mathpar.polynom.Polynom;

//mpirun C java -cp /home/mixail/mathpar/target/classes -Djava.library.path=$LD_LIBRARY_PATH student.shmeleva.FactorNumber
/**
 *
 * @author mixail
 */
public class FactorNumber {
    /**
     * Массив простых чисел
     */
    public static int primes[] = null;
    /**
     * Позиция числа в массиве простых чисел
     */
    public static int posNumber = 0;
    /**
     * Массив для хранения позиций поиска в своем блоке простых чисел для
     * каждого процессора
     */
    public static int[][] posBloksNumber = null;
    /**
     * Массив для хранения делителей
     */
    public static ArrayList<Integer> arr = new ArrayList<Integer>();
    /**
     * Массив для хранения делителей на корневом процессоре
     */
    public static ArrayList<Integer> arrRoot = new ArrayList<Integer>();
    /**
     * Массив для хранения делителей накаждом шаге вычислений
     */
    public static ArrayList<Integer> arrBuffer = new ArrayList<Integer>();
    /**
     * Флаг отвечающий за остановку вычислений по блоку простых чисел каждым
     * процессором, после того как какой-нибудь процессор найдет первым делитель
     */
    public static boolean isPrimes = false;
    /**
     * Флаг отвечающий за конец вычислительного счета на процессорах
     */
    public static boolean finishStep = false;
    /**
     * Число для факторизации
     */
    public static long number = 2097143L*2097143L;//25 * 25 * 25 * 25;//10000;
    /**
     * Корень из числа
     */
    public static int sqrt = 0;
    /**
     * Число, ограничитель задачи факторизации, если число равняется этому
     * числу, программа выполняется на root - процессоре
     */
    public static int quantum = 0;
    /**
     * Результат факторизации числа на root-процессоре
     */
    public static FactorPol resultTheRoot = null;

    /**
     * Создание файла с простыми числами и заполнение массива
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void createFile() throws FileNotFoundException, IOException {
        NumberFileReader readPrimes = new NumberFileReader(NFunctionZ32.intPrimes);
        //NFunctionZ32.doFileOfIntPrimes();
        primes = readPrimes.readtoIntArray(1 << 20);
    }

    /**
     * Поиск позиции числа в массиве
     *
     * @param sqrt - число
     */
    public static void findPos(int sqrt) {
        for (int i = 0; i < primes.length; i++) {
            if (primes[i] >= sqrt) {
                posNumber = i;
                break;
            }
        }
    }

    /**
     * Создание массива для хранения позиций поиска в своем блоке простых чисел
     * для каждого процессора
     *
     * @param size - количество процессоров
     */
    public static void createBloks(int size) {
        posBloksNumber = new int[size][];
        int shift = 0;
        for (int i = 0; i < size; i++) {
            posBloksNumber[i] = new int[2];
            posBloksNumber[i][0] = shift;
            posBloksNumber[i][1] = shift + (int) posNumber / size;
            shift = posNumber / size;
        }
    }

    /**
     * Деление числа на найденные процессорами делители.
     *
     * @param sqrt - число
     * @param a - массив с делителями
     *
     * @return
     */
    public static int dividePrimes(int sqrt, Integer[] a) {
        int n = sqrt;
        for (int i = 0; i < a.length; i++) {
            n = n / a[i];
        }
        return n;
    }

    /**
     * Получение остатка числа
     */
    public static void initNumber(ArrayList<Integer> buf, int sqrt1) {
        if (buf.size() != 0) {
            Integer[] m = new Integer[buf.size()];
            buf.toArray(m);
            number = dividePrimes(sqrt1, m);
            sqrt = (int)Math.sqrt(number);
            findPos(sqrt);
        }
    }

    /**
     * Создание овтета в виде объекта FactorPol
     *
     * @return
     */
    public static FactorPol outputResult() {
        Integer[] m = new Integer[arr.size()];
        arr.toArray(m);
        Polynom[] multin = new Polynom[m.length];
        int[] power = new int[m.length];
        for (int q = 0; q < multin.length; q++) {
            multin[q] = Polynom.polynomFromNumber(new NumberZ(m[q]), Ring.ringZxyz);
            power[q] = 1;
        }
        FactorPol fp1 = new FactorPol(power, multin);
        fp1.normalFormInField(Ring.ringZxyz);
        System.out.println("fp1 = " + fp1);
        //FactorPol fp2 = factorTwo((int) number);
        //FactorPol fp2 = resultTheRoot;
       // System.out.println("fp2 = " + fp2);
        //FactorPol fp3 = fp1.multiply(fp2, Ring.ringZxyz);
        return fp1;//fp3;
    }

    /**
     * Создание буферного массива для записи на каждом шаге делителей
     *
     * @param b
     */
    public static void createArrayBuffer(Object[] b) {
        for (int i = 0; i < b.length; i++) {
            if (b[i] != null) {
                arrBuffer.add((Integer) b[i]);
            }
        }
    }

    public static void main(String[] args) throws MPIException, FileNotFoundException, IOException {
       MPI.Init(args);

        long t1 = System.currentTimeMillis();
        sqrt = (int) Math.sqrt(number);
        System.out.println("sqrt = " + sqrt);
        int rank = MPI.COMM_WORLD.getRank();
        int size = MPI.COMM_WORLD.getSize();
        quantum = 2000000;
        if (number < quantum) {//если число меньше кванта, вычисления происходят на root-процессоре
            System.out.println("Root!");
            System.out.println("Результат = " + NFunctionZ32.factoringLong(sqrt));
        } else {
            //создание файла простых чисел
            createFile();
            //поиск позиции числа в массиве
            findPos(sqrt);
            //разбиение на блоки поиска делителей числа
            createBloks(size);
            while (finishStep == false) {
                isPrimes = false;
                arrBuffer = new ArrayList<Integer>();
                System.out.println("rank = " + rank + " pos[0] = " + posBloksNumber[rank][0] + " pos[1] = " + posBloksNumber[rank][1]);

                int start = posBloksNumber[rank][0];

                int end = posBloksNumber[rank][1];
                System.out.println("jjjjjjjjjjjj " + primes[end]);
                int k = start;
                Object[] a = new Object[size];
                Object[] b = new Object[size];
                //System.out.println("isPrimes = " + isPrimes);
                while ((k <= end) && (isPrimes == false)) {
                    //System.out.println("rank = " + rank + " primes = " + primes[k]);
                    if (number % primes[k] == 0) {
                        arr.add(primes[k]);
                        arrBuffer.add(primes[k]);
                        isPrimes = true;
                        System.out.println("good " + " rank = " + rank + " primes = " + primes[k]);
                        a[rank] = primes[k];
                    }
                    k++;
                }
 //!!!!                 MPI.COMM_WORLD.Alltoallv(a, 0, new int[] {size - 1, size - 1}, new int[] {1, 0},  MPI.OBJECT, b, 0, new int[] {size - 1, size - 1}, new int[] {1, 0},  MPI.OBJECT);
//            for (int j = 0; j < b.length; j++) {
//                System.out.println("rank = " + rank + " send = " + a[j] + " recv = " + b[j]);
//            }
                createArrayBuffer(b);
                initNumber(arrBuffer, (int)number);
                System.out.println("NUMBER = " + number);
                if (number < quantum) {
                    if (rank == 0) {
                        resultTheRoot = NFunctionZ32.factoringLong(number);
                        //factorTheRoot((int) number, 0);
                        //initNumber(arrRoot, (int) number);
                        finishStep = true;
                    } else {
                        finishStep = true;
                    }
                } else {
                    createBloks(size);
                    //sqrt = (int) number;
                }
            }
                FactorPol[] fp = new FactorPol[size];
                FactorPol[] fp1 = new FactorPol[size];
                fp[0] = outputResult();
                //!!!! MPI.COMM_WORLD.Alltoallv(fp, 0, new int[] {1, 1}, new int[] {0, 0}, //!!!! MPI.OBJECT, fp1, 0, new int[] {1, 1}, new int[] {1, 0}, //!!!! MPI.OBJECT);

            if (rank == 0) {
                System.out.println("fp = " + Array.toString(fp1));
                FactorPol result = resultTheRoot;
                System.out.println("resultTheRoot = " + resultTheRoot);
                for(int g = 0; g < fp1.length; g++){
                    if(fp1[g] != null && (fp1[g].multin.length != 0) ){
                result = result.multiply(fp1[g], Ring.ringZxyz);
            }
                }
                System.out.println("Результат = " + result);
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("TIME = " + (t2 - t1));
        //!!!! MPI.Finalize();
    }
}
