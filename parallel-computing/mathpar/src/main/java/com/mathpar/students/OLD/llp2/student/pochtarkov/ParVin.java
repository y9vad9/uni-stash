/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.pochtarkov;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import com.mathpar.number.*;
import mpi.*;
import java.io.*;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author alexss
 */
public class ParVin {

    static ArrayList<NumberR> ArL = new ArrayList<NumberR>();
    static ArrayList<NumberR> ROOTS = new ArrayList<NumberR>();
    static Ring R = new Ring("R[x,u,v]");

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("(");
        for (int i = 0; i < ArL.size(); i++) {
            if (i % 2 != 0) {
                if (i == ArL.size() - 1) {
                    res.append(ArL.get(i) + ")");
                } else {
                    res.append(ArL.get(i) + ");(");
                }
            } else {
                res.append(ArL.get(i) + ",");
            }
        }
        return res.toString();
    }

    /**
     * Метод изменяет полином р, заменяя в нем переменную х на (u*x+v)
     * и домножая его на полином (х+1)^max, где max-максимальная степень полинома р.
     * При этом получаем полином от трех переменных.
     * @param p-первоначальный полином
     * @return измененный полином р3
     */
    public static Polynom polTransform(Polynom p) {

        int l = p.coeffs.length;
        int l_pow = p.powers.length;
        int varNumb = l_pow / l;
        int[] pow1 = new int[3 * l];
        int j = 0;
        int max = p.powers[0];
        for (int i = 0; i < l_pow; i += varNumb) {
            pow1[j] = 0;
            pow1[j + 1] = p.powers[i];
            pow1[j + 2] = max - p.powers[i];
            j += 3;
        }

        Polynom p1 = new Polynom(pow1, p.coeffs);
        //Изменим полином

        Element[] pet1 = new Element[]{
            new Polynom("x", R),
            new Polynom("u*x+v", R),
            new Polynom("x+1", R)};
        p1 = p1.ordering(R);

        Polynom p2 = (Polynom) p1.value(pet1, R);
        return p2;
        //Подставим значения up и down
    }

    /**
     * Метод подставляющий значения up и down в измененный полином для постороения
     * нового полинома от одной переменной с новыми переменными.
     * @param p-данный полином
     * @param up-верхняя граница
     * @param down-нижняя граница
     * @return полином p3.
     * @throws MPIException
     */
    public static Polynom Pr1(Polynom p, NumberR up, NumberR down) throws MPIException {
        Element[] pet2 = new Element[]{new Polynom("x", R), down, up};
        Polynom p3 = (Polynom) p.value(pet2, R);
        return p3;
    }

    /**
     * Метод находит колличество переменн знаков в полиноме р.
     * @param p-данный нам полином
     * @return число переменн знаков k
     */
    public static int Pr2(Polynom p) {


        int k = 0;
        for (int i = 0; i < p.coeffs.length - 1; i++) {
            if (p.coeffs[i].signum() != p.coeffs[i + 1].signum()) {
                k++;
            }
        }

        return k;
    }

    public static void Postr(ArrayList<Element[]> M) {
        for (int i = 0; i < M.size(); i++) {
            for (int j = 0; j < M.get(i).length; j++) {
                System.out.print("[" + i + "](" + j + ")==" + M.get(i)[j] + "   ");
            }
            System.out.println();
        }
    }

    /**
     * Метод вычисляющий границы на каждом процессоре через шаг и номер
     * процессора.
     * @param h-шаг
     * @param rank-номер процессора
     * @return
     */
    public static NumberR[] getBounds(NumberR h, int rank) {
        NumberR lowerBound = (NumberR)h.multiply(new NumberR(rank));
        return new NumberR[]{lowerBound, (NumberR)lowerBound.add(h)};
    }
    public static NumberR[][] globalBounds;

    /**
     * Метод синхронизации массива signChanges между процессорами
     * @param mySignChanges-массив с колличеством корней на каждом промежутке на данном процессоре
     * @param rank-номер процессора
     * @param size-колличество процессоров
     * @return двумерный массив состоящий из колличества корней на каждом промежутке и номера процессоров
     * @throws MPIException
     */
    public static int[][] syncSignChanges(int[] mySignChanges, int rank, int size) throws MPIException {
        int[][] signChanges = new int[size][];
        Request[] r = new Request[size - 1];
        int k = 0;
        for (int i = 0; i < size; i++) {
            if (i != rank) {
         //!!!!       r[k++] = //!!!! MPI.COMM_WORLD.Isend(mySignChanges, 0,
          //!!!!              mySignChanges.length, //!!!! MPI.INT, i, 1000);
            } else {
                signChanges[i] = mySignChanges;
            }
        }
        int mesAm = size - 1;
        while (mesAm > 0) {
         //!!!!   Status st = //!!!! MPI.COMM_WORLD.Probe(//!!!! MPI.ANY_SOURCE, 1000);
         //!!!!   signChanges[st.source] = new int[st.Get_count(//!!!! MPI.INT)];
            //!!!! MPI.COMM_WORLD.Recv(signChanges[st.source], 0, st.Get_count(//!!!! MPI.INT),
                    //!!!! MPI.INT, st.source, 1000);
            mesAm--;
        }
     //!!!!   Request.Waitall(r);
        return signChanges;
    }

    /**
     * Метод перестройки двумерного массива в одномерный путем дополнения последущего
     * "столбца" до "строки".
     * @param signChanges - данный двумерный массив
     * @return одномерный массив колличесва корней
     */
    public static int[] transformSignChanges(int[][] signChanges) {
        int len = 0;
        for (int i = 0; i < signChanges.length; i++) {
            if (signChanges[i] != null) {
                len += signChanges[i].length;
            }
        }
        int[] res = new int[len];
        for (int i = 0, k = 0; i < signChanges.length; i++) {
            for (int j = 0; (signChanges[i] != null) && (j < signChanges[i].length); j++) {
                res[k++] = signChanges[i][j];
            }
        }
        return res;
    }

    /**
     * Разбивает интервал (left, right) на rootsAmount частей и добавляет результат в ar.
     * @param left-нижняя граница
     * @param right-верхняя граница
     * @param rootsAmount-количество частей
     * @param ar-массив границ
     * @return
     */
    public static ArrayList<Element> splitInterval(NumberR left, NumberR right,
            int rootsAmount, ArrayList<Element> ar) {
        NumberR h = ((NumberR)right.subtract(left)).divide(new NumberR(rootsAmount), 3, 3);
        for (int i = 0; i < rootsAmount; i++) {
            ar.add(left);
            left = (NumberR)left.add(h);
            ar.add(left);
        }
        return ar;
    }

    /**
     * Метод выдающий массив из чисел len-колличества взятых процессором rang отрезков,
     * и offset-номером с которого будет начинаться выдача отрезков.
     * @param length-колличество всех отрезков
     * @param rank-номер процессора
     * @param size-колличество процессоров
     * @return массив int[] {offset,len}
     */
    public static int[] getOffsetAndLength(int length, int rank, int size) {
        int len = length / size + (length % size > rank ? 1 : 0);
        int offset = (rank * (length / size)) + Math.min(length % size, rank);
        return new int[]{offset, len};
    }

    /**
     * Метод построения нового массива границ.В зависимости от данных массива
     * корней rootsAmount,либо разбивает на колличество корней на данном промежутке
     * либо добавляет в ответ границы с 1 корнем либо ничего не делает с этим промежутком.
     * @param p-первоначальный полином
     * @param ar-массив границ
     * @param rootsAmount-массив корней
     * @return массив границ
     * @throws MPIException
     */
    public static ArrayList<Element> splitIntervals(Polynom p, ArrayList<Element> ar, int[] rootsAmount) throws MPIException {
        if (ar.size() != 2 * rootsAmount.length) {
            System.err.println("Bad array length");
            return null;
        }
        ArrayList<Element> res = new ArrayList<Element>();
        for (int i = 0, k = 0; i < ar.size(); i += 2, k++) {
            if (p.value(new NumberR[]{(NumberR) ar.get(i + 1)}, R) == NumberR.ZERO) {
                ArL.add((NumberR) ar.get(i + 1));
                ArL.add((NumberR) ar.get(i + 1));
            }
            if (rootsAmount[k] > 0) {
                NumberR left = (NumberR) ar.get(i),
                        right = (NumberR) ar.get(i + 1);
                if (rootsAmount[k] > 1) {
                    res = splitInterval(left, right, rootsAmount[k], res);
                } else {
                    if (rootsAmount[k] == 1) {
                        ArL.add(left);
                        ArL.add(right);
                    }
                }
            }
        }

        return res;
    }
    static int iter = 0;

    /**
     * Метод нахождения отрезков в которых лежат корни полинома.Сначала разбивает
     * область нахождения положительных корней на колличество процессоров.Далее на
     * каждом процессоре происходит вычесление колличества корней.
     * В зависимости от колличества корней происходит разбиение отрезка на колличество
     * корней или на колличество процессоров.Условие разбиения промежутков будет
     * выполняться до тех пор пока колличество корней не станет равно 1.
     * Промежутки на которых лежит 1 корень будут вноситься в массив ответов.
     * Границы являюшиеся корнем так же будут вноситься в массив ответов
     * видом (a,a).
     * @param pol-данный полином.
     * @param rank-номер процессора
     * @param size-колличество процессоров
     * @throws MPIException
     */
    public static void solve(Polynom pol, int rank, int size) throws MPIException {
        NumberR h = null;
        if (rank == 0) {
            //!!!! MPI.COMM_WORLD.Bcast(new Element[]{pol}, 0, 1, //!!!! MPI.OBJECT, 0);
        } else {
            Element[] buf = new Element[3];
            //!!!! MPI.COMM_WORLD.Bcast(buf, 0, 1, //!!!! MPI.OBJECT, 0);
            pol = (Polynom) buf[0];
        }
        Polynom ptrans = polTransform(pol);
        NumberR b = pol.U_BOUND(R),
                a = NumberR.ZERO;
        ArrayList<Element> intervals = new ArrayList<Element>();
        intervals = splitInterval(a, b, size, intervals);
        int sum = 0;
        do {
            int[] offsetLen = getOffsetAndLength(intervals.size() / 2, rank, size);
            int offset = offsetLen[0];
            int len = offsetLen[1];
            int[] mySignChanges = new int[len];
            int k = 0;
            for (int i = 0; i < len * 2; i += 2) {
                mySignChanges[k] = Pr2(Pr1(ptrans, (NumberR) intervals.get(i + 1 + offset * 2),
                        (NumberR) intervals.get(i + offset * 2)));
                k++;
            }
            int[] signChangesV = transformSignChanges(syncSignChanges(mySignChanges, rank, size));
            sum = sum(signChangesV);
            if (size > sum) {
                for (int i = 0; i < signChangesV.length; i++) {
                    if (signChangesV[i] > 1) {
                        signChangesV[i] = size;
                    }
                }
            }
            intervals = splitIntervals(pol, intervals, signChangesV);
            //!!!! MPI.COMM_WORLD.Barrier();
        } while ((sum > 0));
    }

    /**
     * Метод возвращающий число равное сумме элементов массива
     * @param ar-данный массив
     * @return число sum
     */
    public static int sum(int[] ar) {
        int sum = 0;
        for (int i : ar) {
            if (i > 1) {
                sum += i;
            }
        }
        return sum;
    }

    /**
     * Метод генерации случайного одномерного полинома не кратных корней
     * @param bitsRoot-область нахождения
     * @param degree-колличество корней
     * @return полином колличеством корней degree лежащих в области от 0 до bitsRoot
     */
    public static Polynom generateRandomPolynomial(int bitsRoot, int degree) {
        TreeSet<NumberR> usedRoots = new TreeSet<NumberR>();
        Random rnd = new Random();
        Polynom res = new Polynom(NumberR.ONE);
        Polynom mul = new Polynom("(x-1)", R);
        for (int i = 0; i < degree; i++) {
            NumberR root = new NumberR(new NumberZ(bitsRoot, rnd));
            while (usedRoots.contains(root)) {
                root = new NumberR(new NumberZ(bitsRoot, rnd));
            }
            mul.coeffs[1] = root.negate();
            res = res.mulSS(mul, R);
            usedRoots.add(root);
        }
        return res;
    }

    public static void main(String[] args) throws MPIException, FileNotFoundException, IOException, Exception {

        MPI.Init(args);
        int size =  MPI.COMM_WORLD.getSize();
        int rank =  MPI.COMM_WORLD.getRank();
        if (rank == 0) {
            Polynom p;
            int deg = Integer.parseInt(args[0]);
            int bits = Integer.parseInt(args[1]);
            p = generateRandomPolynomial(bits, deg);
            //new Polynom("(x-1)(x-2)(x-3)(x-4)(x-5)(x-6)(x-7)(x-8)(x-9)(x-10)(x-10001)(x-14)",R);
            long t1 = System.currentTimeMillis();
            solve(p, rank, size);


//            //------вычисления корней на промежутках-----
//            for (int i = 0; i < (ArL.size() / 2); i++) {
//                NumberR64 a = NumberR64.valueOf(ArL.get(2 * i));
//                NumberR64 b = NumberR64.valueOf(ArL.get(2 * i + 1));
//                NumberR64 root = Newton.Root(a, b, p);
//                //NumberR64 root=parab111.Roots(a, b, p, R);
//                //System.out.println("root==="+root);
//                NumberR roots = root.BigDecimalValue();
//                ROOTS.add(roots);
//            }
            long t2 = System.currentTimeMillis();
            //----------запись тестов в текстовый документ-------
            String s1 = "\n" + "Procs=" + size + "\n" + "Deg=" + deg + "\n" + "Bits=" + bits + "\n" + "Time=" + (t2 - t1) + "\n";
            String s11 = size + "\n" + deg + "\n" + bits + "\n" + (t2 - t1) + "\n";
            System.out.println("s11111====" + s11);
            RandomAccessFile raf = new RandomAccessFile("/home/pochtarkov/aaa.txt", "rw");
            raf.seek(raf.length());
            raf.writeUTF(s1);
            RandomAccessFile raf1 = new RandomAccessFile("/home/pochtarkov/aaa1.txt", "rw");
            raf1.seek(raf1.length());
            raf1.writeUTF(s11);

        } else {
            solve(null, rank, size);
        }
        //!!!! MPI.Finalize();
    }
}
