package com.mathpar.students.OLD.llp2.students;

import mpi.*;
import com.mathpar.number.*;
import com.mathpar.polynom.Polynom;
import com.mathpar.number.SubsetZ;

public class MultPol {

    /**
     * метод, который делит полином на q подполиномов, минимально отличающихся по длинне
     * @param p - полином, который будет разбит на части
     * @param q - количество полученных подполиномов
     * @return - массив, содержащий все q подполиномов
     */
    public Polynom[] SubQuantPolynoms(Polynom p, int q) {

        int y = 0;
        Polynom[] p1 = new Polynom[q];

        SubsetZ sub = new SubsetZ(new int[]{0, p.coeffs.length});
        SubsetZ[] numS1 = sub.divideOnParts(q);

        for (int i = 0; i < numS1.length; i++) {
            if (i == numS1.length - 1) {
                y = numS1[i].toArray()[1];
            } else {
                y = numS1[i].toArray()[1] + 1;
            }
            p1[i] = p.subPolynom(numS1[i].toArray()[0], y);
        }
        return p1;

    }

    /**
     * метод, возвращающий номера оставшихся в группе процессоров
     * @param ranks - массив исходных номеров процесоров
     * @return - массив новых номеров
     */
    public int[] SubArrOfProc(int[] ranks) {
        int[] fin = new int[ranks.length / 2];
        int k = 0;
        for (int i = 0; i < ranks.length; i++) {
            if (i % 2 == 0) {
                fin[k] = ranks[i];
                k = k + 1;
                if (k == ranks.length / 2) {
                    break;
                }
            } else {
                continue;
            }
        }
        return fin;
    }

    /**
     * метод, параллельно умножающий 2 полинома, исходя из минимального кванта их разбиения
     * @param s1 - 1-й полином
     * @param s2 - 2-ой полином
     * @param quant - квант разбиения полиномов
     * @param mpi - представитель класса MPI
     * @param res - массив одного элемента, в котором будет хранится ответ
     * @throws MPIException
     */
    public void MultPolynomPar_all(Polynom s1, Polynom s2, int quant, MPI mpi, Polynom[] res) throws MPIException {

        Ring ring = Ring.ringR64xyzt;
        Polynom ab = new Polynom("0", ring);
        int myrank = MPI.COMM_WORLD.getRank(); //номер процессора
        int p =  MPI.COMM_WORLD.getSize(); //количество процессоров

        System.out.println("");
        long time1 = 0;
        int rabproc = 0;
        int q1;
        int q2;
        int ness;
        int[] newranks = null;
        int[] newrab_recv = new int[1];
        int kp0 = (int) (Math.sqrt((double) p));
        int newp = kp0 * kp0;//в случае когда проц меньше чем нужно это новое кол-во работающих процесоров
        Polynom[] s1d = null;
        Polynom[] s2d = null;

        Object[] new1 = new Object[1];
        Object[] new3 = new Object[1];

//--------------------------------------------------------------------------------------
//ПРОГРАММА НУЛЕВОГО ПРОЦЕССОРА

        if (myrank == 0) {
//определяем количество частей, на которые будет разбит каждый полином
            if (s1.coeffs.length <= quant) {
                q1 = 1;
            }//если длина полинома s1 меньше или равна кванту, то он берется целиком
            else {
                if ((s1.coeffs.length) % quant == 0) {
                    q1 = (s1.coeffs.length) / quant;
                } else {
                    q1 = (s1.coeffs.length) / quant + 1;
                }
            }
            if (s2.coeffs.length <= quant) {
                q2 = 1;
            }//если длина полинома s2 меньше или равна кванту, то он берется целиком
            else {
                if ((s2.coeffs.length) % quant == 0) {
                    q2 = (s2.coeffs.length) / quant;
                } else {
                    q2 = (s2.coeffs.length) / quant + 1;
                }
            }
//определяем необходимое количество процессоров и разбиваем полиномы на части
            ness = q1 * q2;
            System.out.println("Квант разбиения полиномов=" + quant);
            System.out.println("Необходимое количество процессоров для вычислений с данным квантом=" + ness);
            System.out.println("Имеющееся количество процессоров=" + p);
            if (p < ness) {
                rabproc = newp;
                System.out.println("Процессоров не достаточно, квант разбиения определяется автоматически");
                System.out.println("Квант разбиения=" + kp0);
                s1d = SubQuantPolynoms(s1, kp0);
                s2d = SubQuantPolynoms(s2, kp0);
                s1 = null;
                s2 = null;
            } else {
                rabproc = ness;
                System.out.println("Разбиваем полиномы на " + q1 + " и " + q2 + " части");
                s1d = SubQuantPolynoms(s1, q1);
                s2d = SubQuantPolynoms(s2, q2);
                s1 = null;
                s2 = null;
            }

            time1 = System.currentTimeMillis();
//отсылаем всем процессорам полученное число задействованных в дальнейшем процессоров
            for (int j = 1; j < p; j++) {
                //!!!! MPI.COMM_WORLD.Send(new int[]{rabproc}, 0, 1, //!!!! MPI.INT, j, 000);
            }
        } //--------------------------------------------------------------------------------------
        //ПРОГРАММА ОСТАЛЬНЫХ ПРОЦЕССОРОВ
        else {//принимаем от 0 проц число работающих процессоров
            //!!!! MPI.COMM_WORLD.Recv(newrab_recv, 0, 1, //!!!! MPI.INT, 0, 000);
            rabproc = newrab_recv[0];
        }

//--------------------------------------------------------------------------------------
//ПРОГРАММА ВСЕХ ПРОЦЕССОРОВ

        //определяем массив номеров задействованных процессоров
        newranks = new int[rabproc];
        for (int i = 0; i < newranks.length; i++) {
            newranks[i] = i;
        }
/*
        //определяем новую группу работающих процессоров
        Group g = //!!!! MPI.COMM_WORLD.Group().Incl(newranks);
      //!!!!  Intracomm COMM_NEW =  MPI.COMM_WORLD.Creat(g);
       //!!!! int newrank = g.Rank();
       //!!!! int size = g.Size();

//--------------------------------------------------------------------------------------
//ПРОГРАММА ПРОЦЕССОРОВ ЗАДЕЙСТВОВАННЫХ В ВЫЧИСЛЕНИЯХ
        if (newrank >= 0) {
            //----------------
            //нулевой процессор
            if (newrank == 0) {

//рассылка нулевым процессором каждому процессору(кроме 0) соответствующих частей
                int k = 0;
                Object[] send = null;
                for (int j = 0; j < s1d.length; j++) {
                    for (int r = 0; r < s2d.length; r++) {
                        if ((j == 0) && (r == 0)) {
                            continue;
                        }
                        send = new Object[]{s1d[j], s2d[r]};
                        //!!!! MPI.COMM_WORLD.Send(send, 0, 2, //!!!! MPI.OBJECT, r + k, 111);
                        send = null;
                    }
                    k = k + s2d.length;
                }
                //вычисление своего куска
                Polynom a1 = s1d[0];
                Polynom b1 = s2d[0];
                ab = a1.multiply(b1, ring);
                s1d = null;
                s2d = null;
                byte[] temp1 = //!!!! MPI.COMM_WORLD.Object_Serialize(new Object[]{ab}, 0, 1, //!!!! MPI.OBJECT);
                System.out.println("RES size=" + temp1.length);
            } //--------------------
            //остальные процессоры
            else {
//прием своих частей от 0 процессора
                Object[] m1 = new Object[2];
                //!!!! MPI.COMM_WORLD.Recv(m1, 0, 2, //!!!! MPI.OBJECT, 0, 111);
//перемножаем полученные от нулевого процессора элементы
                Polynom a = (Polynom) m1[0];
                Polynom b = (Polynom) m1[1];
                m1 = null;
                ab = ab.add(a.multiply(b, ring), ring);
                byte[] temp1 = //!!!! MPI.COMM_WORLD.Object_Serialize(new Object[]{ab}, 0, 1, //!!!! MPI.OBJECT);
                System.out.println("RES size=" + temp1.length);
            }

            //---------------
            //общая программа


            int[] vvvv = null;
            vvvv = newranks;
            for (int i = 0; i < size / 2; i++) {//количество шагов
                Group g_new = COMM_NEW.Group().Incl(vvvv);
                Intracomm C_NEW = COMM_NEW.Creat(g_new);
                int my_rank = g_new.Rank();
                int my_size = g_new.Size();
                if (my_rank >= 0) {
//                    if(my_size%2==0){//четное количество процессоров
                    //System.out.println("четное количество процессоров");

                    if (my_rank % 2 == 0) {
                        if (my_rank != my_size - 1) {
                            Object[] m1 = new Object[1];
                            C_NEW.Recv(m1, 0, 1, //!!!! MPI.OBJECT, my_rank + 1, 123);
                            //System.out.println("proc "+ my_rank +" or"+ newrank +"recv from " + (my_rank+1));
                            Polynom a = (Polynom) m1[0];
                            ab = ab.add(a, ring);
                            m1 = null;
                            byte[] temp1 = //!!!! MPI.COMM_WORLD.Object_Serialize(new Object[]{ab}, 0, 1, //!!!! MPI.OBJECT);
                            System.out.println("RES size=" + temp1.length);
                            if ((my_size % 2 == 1) && (my_rank == 0)) {
                                Object[] m2 = new Object[1];
                                C_NEW.Recv(m2, 0, 1, //!!!! MPI.OBJECT, my_size - 1, 1234);
                                Polynom a1 = (Polynom) m2[0];
                                ab = ab.add(a1, ring);
                                m2 = null;
                                byte[] temp = //!!!! MPI.COMM_WORLD.Object_Serialize(new Object[]{ab}, 0, 1, //!!!! MPI.OBJECT);
                                System.out.println("RES size=" + temp.length);
                            }
                        } else {
                            Object[] new2 = new Object[]{ab};
                            C_NEW.Send(new2, 0, 1, //!!!! MPI.OBJECT, 0, 1234);
                            new2 = null;
                            ab = null;
                        }
                    } else {
                        Object[] new2 = new Object[]{ab};
                        C_NEW.Send(new2, 0, 1, //!!!! MPI.OBJECT, my_rank - 1, 123);
                        new2 = null;
                        ab = null;
                        //System.out.println("proc "+ my_rank +" or"+ newrank + "send to " + (my_rank-1));
                    }

                    vvvv = SubArrOfProc(vvvv);

                    if (vvvv.length == 1) {
                        break;
                    }
                } else {;
                }


            }
        } //---------------------------------------------------------------------------------------------------------
        //НЕЗАДЕЙСТВОВАННЫЕ ПРОЦЕССОРЫ
        else {;
        }

        if (myrank == 0) {
            res[0] = ab;
            long time2 = System.currentTimeMillis() - time1;
            System.out.println("time2=" + time2);
        }

    }
*/
    /**
     * метод, параллельно умножающий 2 полинома, исходя из минимального кванта их разбиения
     * @param s1 - 1-й полином
     * @param s2 - 2-ой полином
     * @param quant - квант разбиения полиномов
     * @param mpi - представитель класса MPI
     * @param res - массив одного элемента, в котором будет хранится ответ
     * @throws MPIException
     */

  /*  public void MultPolynomPar_all_new(Polynom s1, Polynom s2, int quant, MPI mpi, Polynom[] res) throws MPIException {

        Ring ring = Ring.ringR64xyzt;
        Polynom ab = new Polynom("0", ring);
        int myrank = //!!!! MPI.COMM_WORLD.getRank(); //номер процессора
        int p = //!!!! MPI.COMM_WORLD.Size(); //количество процессоров

        System.out.println("");
        long time1 = 0;
        int rabproc = 0;
        int q1;
        int q2;
        int ness;
        int[] newranks = null;
        int[] newrab_recv = new int[1];
        int kp0 = (int) (Math.sqrt((double) p));
        int newp = kp0 * kp0;//в случае когда проц меньше чем нужно это новое кол-во работающих процесоров
        Polynom[] s1d = null;
        Polynom[] s2d = null;

        Object[] new1 = new Object[1];
        Object[] new3 = new Object[1];

//--------------------------------------------------------------------------------------
//ПРОГРАММА НУЛЕВОГО ПРОЦЕССОРА

        if (myrank == 0) {
//определяем количество частей, на которые будет разбит каждый полином
            if (s1.coeffs.length <= quant) {
                q1 = 1;
            }//если длина полинома s1 меньше или равна кванту, то он берется целиком
            else {
                if ((s1.coeffs.length) % quant == 0) {
                    q1 = (s1.coeffs.length) / quant;
                } else {
                    q1 = (s1.coeffs.length) / quant + 1;
                }
            }
            if (s2.coeffs.length <= quant) {
                q2 = 1;
            }//если длина полинома s2 меньше или равна кванту, то он берется целиком
            else {
                if ((s2.coeffs.length) % quant == 0) {
                    q2 = (s2.coeffs.length) / quant;
                } else {
                    q2 = (s2.coeffs.length) / quant + 1;
                }
            }
//определяем необходимое количество процессоров и разбиваем полиномы на части
            ness = q1 * q2;
            System.out.println("Квант разбиения полиномов=" + quant);
            System.out.println("Необходимое количество процессоров для вычислений с данным квантом=" + ness);
            System.out.println("Имеющееся количество процессоров=" + p);
            if (p < ness) {
                rabproc = newp;
                System.out.println("Процессоров не достаточно, квант разбиения определяется автоматически");
                System.out.println("Квант разбиения=" + kp0);
                s1d = SubQuantPolynoms(s1, kp0);
                s2d = SubQuantPolynoms(s2, kp0);
            } else {
                rabproc = ness;
                System.out.println("Разбиваем полиномы на " + q1 + " и " + q2 + " части");
                s1d = SubQuantPolynoms(s1, q1);
                s2d = SubQuantPolynoms(s2, q2);
            }

            time1 = System.currentTimeMillis();
//отсылаем всем процессорам полученное число задействованных в дальнейшем процессоров
            for (int j = 1; j < p; j++) {
                //!!!! MPI.COMM_WORLD.Send(new int[]{rabproc}, 0, 1, //!!!! MPI.INT, j, 000);
            }
        } //--------------------------------------------------------------------------------------
        //ПРОГРАММА ОСТАЛЬНЫХ ПРОЦЕССОРОВ
        else {//принимаем от 0 проц число работающих процессоров
            //!!!! MPI.COMM_WORLD.Recv(newrab_recv, 0, 1, //!!!! MPI.INT, 0, 000);
            rabproc = newrab_recv[0];
        }

//--------------------------------------------------------------------------------------
//ПРОГРАММА ВСЕХ ПРОЦЕССОРОВ

        //определяем массив номеров задействованных процессоров
        newranks = new int[rabproc];
        for (int i = 0; i < newranks.length; i++) {
            newranks[i] = i;
        }

        //определяем новую группу работающих процессоров
        Group g = //!!!! MPI.COMM_WORLD.Group().Incl(newranks);
        Intracomm COMM_NEW = //!!!! MPI.COMM_WORLD.Creat(g);
        int newrank = g.Rank();
        int size = g.Size();

//--------------------------------------------------------------------------------------
//ПРОГРАММА ПРОЦЕССОРОВ ЗАДЕЙСТВОВАННЫХ В ВЫЧИСЛЕНИЯХ
        if (newrank >= 0) {
            //----------------
            //нулевой процессор
            if (newrank == 0) {

//рассылка нулевым процессором каждому процессору(кроме 0) соответствующих частей
                int k = 0;
                Object[] send = null;
                for (int j = 0; j < s1d.length; j++) {
                    for (int r = 0; r < s2d.length; r++) {
                        if ((j == 0) && (r == 0)) {
                            continue;
                        }
                        send = new Object[]{s1d[j], s2d[r]};
                        //!!!! MPI.COMM_WORLD.Send(send, 0, 2, //!!!! MPI.OBJECT, r + k, 111);
                    }
                    k = k + s2d.length;
                }
                //вычисление своего куска
                Polynom a1 = s1d[0];
                Polynom b1 = s2d[0];
                ab = a1.multiply(b1, ring);


//принимаем результаты процессоров
                for (int i = 1; i < size; i++) {
                    Object[] recv = new Object[1];
                    COMM_NEW.Recv(recv, 0, 1, //!!!! MPI.OBJECT, //!!!! MPI.ANY_SOURCE, //!!!! MPI.ANY_TAG);
                    Polynom aa = (Polynom) recv[0];
                    ab = ab.add(aa, ring);
                }


            } //--------------------
            //остальные процессоры
            else {
//прием своих частей от 0 процессора
                Object[] m1 = new Object[2];
                //!!!! MPI.COMM_WORLD.Recv(m1, 0, 2, //!!!! MPI.OBJECT, 0, 111);
//перемножаем полученные от нулевого процессора элементы
                Polynom a = (Polynom) m1[0];
                Polynom b = (Polynom) m1[1];
                ab = ab.add(a.multiply(b, ring), ring);

//отсылаем нулевому полученные результаты
                Object[] send1 = new Object[]{ab};
                COMM_NEW.Send(send1, 0, 1, //!!!! MPI.OBJECT, 0, //!!!! MPI.ANY_TAG);
            }
            //---------------
            //общая программа



        } //---------------------------------------------------------------------------------------------------------
        //НЕЗАДЕЙСТВОВАННЫЕ ПРОЦЕССОРЫ
        else {;
        }
        if (myrank == 0) {
            res[0] = ab;
            long time2 = System.currentTimeMillis() - time1;
            System.out.println("time2=" + time2);
        }

    }*/
}
}
