package com.mathpar.students.OLD.llp2.students;

import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.matrix.*;

class tugareva1 {

    /**
     * Алгоритм нахождения жордановой нормальной формы методом JordanForm: 1.
     * Находим спектр оператора, для этого составляем характеристическое
     * уравнение и решаем его. 2. После нахождения собственных значений и
     * соответствующих им кратностей, вычисляем количество жордановых клеток для
     * каждого собственного значения. При этом учитываем, что суммарный порядок
     * всех жордановых клеток, соответствующих конкретному собственному значению
     * не может быть больше кратности этого собственного значения. 3. Записываем
     * все получившиеся жордановы клетки для всех корней в массив и передаем
     * этот массив как входной параметр методу makeBlockDiagonalMatrix, который
     * строит окончательный вариант жордановой нормальной формы.
     *
     * @param A - матрица (тип MatrixS)
     * @param ring - кольцо (тип Ring)
     * @return Final - матрица типа MatrixS - жорданова нормальная форма
     * исходной матрицы
     */
    public static MatrixS JordanForm(MatrixS A, Ring ring) throws Exception {
        int NumbAlgebra = ring.algebra[0];
        System.out.println("NumbAlgebra=" + NumbAlgebra);

        MatrixS Final = null;//итоговая матрица
        int str = A.size; //количество строк исходной матрицы
        MatrixD A1 = new MatrixD(A, true, ring);//создаем матрицу A1 типа MatrixD, значение параметра на второй позиции: если true-матрица достраивается до квадратной нулями, если false-остается прямоугольной
        int k = 1;
        int m = 2;
        MatrixS NewA = null;
        MatrixS AA = null;
        int kletka = 0;// количество жордановых клеток определенного порядка для i-го корня
        int sum = 0;  //суммарное количество строк в итоговой матрице в случае с корнями кратности 1
        int sum1 = 0; //количество строк в итоговой матрице (кратность >1)
        MatrixS Ji = null; // жордановы клетки
        int Npos = 0;// порядковый номер блока i-го корня
        int Npos1 = 0;
        int blNiRoot = 0;       //счетчик
        int BlRoot = 0;

        Polynom p = A1.characteristicPolynomP(ring); // характеристический полином
        if (p.coeffs[0].isNegative()) {//если первый коэффициент характеристического полинома отрицательный, меняем знак
            p = (Polynom) p.negate(ring);
        }

        System.out.println("p=" + p.toString(ring));

        FactorPol pp = p.factorOfPol_inC(ring);
        Polynom[] P = pp.multin;//массив множителей характеристического полинома
        Element[] k1 = new Element[P.length];

        for (int i = 0; i < P.length; i++) {
            k1[i] = P[i].coeffs[1].negate(ring); //корни полинома
            System.out.println("K1" + i + "=" + k1[i]);
        }

        int[] powers = pp.powers;//степени множителей характеристического полинома
        int ed = 0; //счетчик, отвечающий за количество корней хар. полинома кратности 1
        int rootNum = P.length; // количество множителей хар. полинома == число корней

        // цикл for  по всем корням полинома, подсчитывающий количество корней кратности 1
        for (int i = 0; i < rootNum; i++) {
            if (powers[i] == 1) {
                ed++;
            }
        }


        int[] pow2 = new int[rootNum - ed];
        Element[] roots1 = new Element[ed];
        Element[] roots2 = new Element[rootNum - ed];
        MatrixS[] Klrow = new MatrixS[ed];
        MatrixS[] Klrow1 = new MatrixS[str];
        Klrow = new MatrixS[ed];

        //цикл for по всем корням характеристического полинома делит все корни на 2 части:
        //1- группа корней кратности 1, 2 - корни с кратностью >1
        if (rootNum == ed) {
            roots1 = k1;
        } else {
            int s1 = 0;
            int s2 = 0;
            for (int i = 0; i < rootNum; i++) {
                if (powers[i] == 1) {
                    roots1[s1++] = k1[i];
                } else {
                    roots2[s2] = k1[i];
                    pow2[s2++] = powers[i];
                }
            }
        }

        // цикл по корням кратности 1, формирующий для них жордановы клетки
        for (int i = 0; i < ed; i++) {
            // параметры: 1 - порядок жорд. клетки, roots1[i]-собственное значение этой клетки, ring - кольцо
            Ji = MatrixS.zhordan(1, roots1[i], ring);
            System.out.println("root=" + roots1[i]);
            System.out.println("kratnost=1");
            System.out.println("ji=" + Ji);
            Klrow[Npos1] = Ji;
            Npos1++;
            BlRoot++;
            sum1++;
        }
        MatrixS[][] Kl = new MatrixS[P.length][]; //массив жордановых клеток для всех корней
        for (int j = 0; j < ed; j++) {
            Kl[j] = new MatrixS[]{Klrow[j]};
        }
        int acc = 5;
        // цикл по корням кратности >1, формирующий для них жордановы клетки
        for (int i = 0; i < rootNum - ed; i++) {
            Npos = 0;
            int flag = 0;// 0 - "хорошо", 1 - попадаем в исключительный случай
            int kratnost = pow2[i];// кратность i-го корня
            System.out.println("KR=" + kratnost);
            System.out.println("roots2=" + roots2[i]);
            Klrow1 = new MatrixS[kratnost];
            int q = 0;  // счетчик, определяющий суммарный порядок всех жордановых клеток i-го корня
            //System.out.println("root=" + roots2[i]);
            //System.out.println("krat=" + kratnost);

            NewA = MatrixS.scalarMatrix(str, roots2[i], ring);// матрица lambda*E
            AA = A.subtract(NewA, ring);//матрица A-lambda*E
            AA = (MatrixS) AA.toNewRing(ring.algebra[0], ring);

            int rank1 = str;
            MatrixS AA_pow=AA;
            int rank2 = AA.rank(ring);
            System.out.println("AA=" + AA);
            System.out.println("rank1=" + rank1 + "rank2=" + rank2);

// цикл for, отвечающий за порядок жордановой клетки
            for (int j = 1; j < kratnost + 1; j++) {
                //System.out.println("djhkdf");
                MatrixS AA_newpow = (MatrixS) AA_pow.multiply(AA, ring);
                //System.out.println("A2=" + A2);
                int rank3 = AA_newpow.rank(ring);
               // System.out.println("det=" + AA.toEchelonForm(ring));
                //System.out.println("ring1=" + ring);
                System.out.println("rank3=" + rank3);
                //int count = 1; // флаг: 1- "все хорошо", 0 - ранг начинает возрастать
                while (rank3 > rank2) {

                    acc *= 5;
                    ring.setAccuracy(acc);
                    ring.setMachineEpsilonR(acc -15);
                    System.out.println("Accuracy=" + acc);
                    rank2 = ((MatrixS) AA.pow(j, ring)).rank(ring);
                    System.out.println("22222="+rank2);
                    rank3=((MatrixS) AA.pow(j+1, ring)).rank(ring);
                    System.out.println("333333="+rank3);
                }



                kletka = rank1 - 2 * rank2 + rank3; //вычисление для i-го корня кол-ва жорд. клеток j-го порядка
                System.out.println("kletka=" + kletka);

                //если количество жордановых клеток j-го порядка >0, начинаем построение
                if (kletka > 0) {
                    blNiRoot += kletka;//счетчик, показывающий количество жорд. клеток
                    int poryadok = j;//порядок жордановой клетки
                    Ji = MatrixS.zhordan(poryadok, roots2[i], ring);//построение жордановых клеток
                    //System.out.println("Ji=" + Ji);
                    for (int v = 0; v < kletka; v++) {
                        Klrow1[Npos] = Ji;//массив жордановых клеток для каждого конкретного корня

                        // System.out.println("Klrow=" + Array.toString(Klrow1) + "   " + i + "   " + Npos);
                        Npos++;
                        sum = sum + poryadok;//подсчет строк итоговой матрицы
                    }
                }

                q += kletka * j; // число равное произведению порядка клетки на количество клеток
                // если q совпадает с кратностью, значит жордановых клеток для данного корня больше нет
                if (q == kratnost) {
                    j = kratnost;
                } else {
                    rank1 = rank2;
                    rank2 = rank3;
                    AA_pow=AA_newpow;
                }
                //если посчитали все клетки для i-го корня, и они все null, значит опять "подкручиваем"
                //машинный ноль
                if ((j == kratnost) && ((Klrow1[0] == null) || (q<kratnost))) {
//                    if (m < 100) {
//                        m++;
//                        ring.setMachineEpsilonR64(m);
//                        System.out.println("m=" + m);
//                        flag = 1;
//                    } else {
//                        ring = new Ring("R[x]");
//                        System.out.println("ring=" + ring);
                        acc *= 5;
                        ring.setAccuracy(acc);
                        ring.setMachineEpsilonR(acc - 15);
                        System.out.println("setZero=" + acc);
                        flag = 1;

                    }
                }
           // }

            if (flag == 1) {
                i--;
            }
            Kl[i + ed] = Klrow1;
            //System.out.println("KL="+Kl[i+ed][0]);

        }
        int SUM = sum + sum1;// количество строк итоговой матрицы
        int COUNT = BlRoot + blNiRoot;// число ВСЕХ жордановых клеток
        //System.out.println("");
        // System.out.println("------------------------");
        //System.out.println("KLtttt=" + Array.toString(Kl));
//        for (int j = 0; j < P.length; j++) {
//            if (j < ed) {
//                Kl[j] = new MatrixS[]{Klrow[j]};
//            } else {
//                Kl[j] = Klrow1;
//                System.out.println("KLtttt=" + Array.toString(Kl[j]) + "    " + j);
//            }
//        }
        //System.out.println("Kllll="+Array.toString(Kl));
        Final = MatrixS.makeBlockDiagonalMatrix(Kl, SUM);//построение жордановой нормальной формы матрицы
        // если размер итоговой матрицы отличается от размера исходной, возвращаем null
        if ((Final.size < A.size) || (Final.size > A.size)) {
            return null;
        }
        return Final;
    }
}
