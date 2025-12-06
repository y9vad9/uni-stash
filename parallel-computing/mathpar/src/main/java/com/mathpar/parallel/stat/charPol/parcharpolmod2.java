/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.charPol;

import java.io.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import com.mathpar.matrix.*;
import mpi.MPI;
import mpi.MPIException;
import mpi.Request;
import mpi.Status;
import com.mathpar.number.*;
import com.mathpar.parallel.utils.MPITransport;
import com.mathpar.polynom.Polynom;

/**
 * запуск в lam: 
 * mpirun C java -Xmx1000m -cp
 * /home/oxana/program/mathpar18August2012/target/classes
 * -Djava.library.path=$LD_LIBRARY_PATH parallelProg.parcharpolmod
 * Нулевой процессор не восстанавливает. 
 * Нулевой процессор принимает от всех результаты и пишет их в файл
 * @author
 */
public class parcharpolmod2 {

    public static void main(String[] args) throws IOException, ClassNotFoundException, CloneNotSupportedException {
        try {
            MPI.Init(new String[0]);
            int rank = MPI.COMM_WORLD.getRank();
            int mpiSize = MPI.COMM_WORLD.getSize();

            if (rank == 0) {
                long t1 = System.currentTimeMillis();
                int size = 8;
                int mdens = 100;
                String r_str = "Z[x,y,z]";
                String rp_str = "Zp32[x,y,z]";
                int pdens = 100;
                int vars = 1;
                int cbits = 7;
                int deg = 1;
                int deg1[] = {1};
                int rnd = 2;


                for (int i = 0; i < args.length; i++) {
                    System.out.println("args[i]=" + args[i]);
                    if (args[i].contains("size")) {
                        size = Integer.parseInt(args[i].replace("size=", ""));
                    }
                    if (args[i].contains("mdens")) {
                        mdens = Integer.parseInt(args[i].replace("mdens=", ""));
                    }
                    if (args[i].contains("ring")) {
                        r_str = (args[i].replace("ring=", ""));
                        rp_str = r_str.replace("Z", "Zp32");
                    }
                    if (args[i].contains("deg")) {
                        deg = Integer.parseInt(args[i].replace("deg=", ""));
                    }
                    if (args[i].contains("pdens")) {
                        pdens = Integer.parseInt(args[i].replace("pdens=", ""));
                    }
                    if (args[i].contains("vars")) {
                        vars = Integer.parseInt(args[i].replace("vars=", ""));
                    }
                    if (args[i].contains("cbits")) {
                        cbits = Integer.parseInt(args[i].replace("cbits=", ""));
                    }
                    if (args[i].contains("size")) {
                        size = Integer.parseInt(args[i].replace("size=", ""));
                    }
                    if (args[i].contains("rnd")) {
                        rnd = Integer.parseInt(args[i].replace("rnd=", ""));
                    }
                }

                Ring r = new Ring(r_str);
                Ring ring = new Ring(rp_str);
                int[] polArr = new int[vars + 2];
                for (int i = 0; i < vars; i++) {
                    polArr[i] = deg1[i];
                }
                polArr[polArr.length - 2] = pdens;
                polArr[polArr.length - 1] = cbits;

                //входная матрица
                MatrixS a1 = new MatrixS(size, size, mdens, polArr, new Random(rnd), r.numberONE(), r);
                MatrixD a = new MatrixD(a1, r);
                System.out.println("input: m00=" + a.M[0][0].toString(r));

                //рассылка колец
//                Object[] rings = new Object[]{r, ring};
//                MPI.COMM_WORLD.Bcast(rings, 0, 2, MPI.OBJECT, 0);

                //рассылка входной матрицы и колец
                r.cleanRing();
                ring.cleanRing(); 
                Object[] t = new Object[]{a,r,ring};
                //byte[] t = MPI.COMM_WORLD.Object_Serialize(new Object[]{a, r, ring}, 0, 3, MPI.OBJECT);
                int[] ti = new int[]{t.length};
                //System.out.println("t.length= "+t.length);
                MPI.COMM_WORLD.bcast(ti,  1, MPI.INT, 0);
                //MPI.COMM_WORLD.Bcast(t, 0, t.length, MPI.BYTE, 0);
                MPITransport.bcastObjectArray(t,t.length, 0);
                /*
                byte[] temp=MPI.COMM_WORLD.Object_Serialize(new Object[]{a},0,1,MPI.OBJECT); //метод Object_Serialize() превращает массив объектов в массив байтов
                MPI.COMM_WORLD.Isend(temp, 0, temp.length, MPI.BYTE, proc, tag);  //, MPI.BYTE= тип передаваемых данных
                
                 */
                // вычисляем необходимое кол-во модулей по каждой
                //переменной. в последний элемент массива записываем кол-во числ. модулей
                charPolynomMatrixD ch = new charPolynomMatrixD(a);
                int[] mod_count = ch.complexityPolynomialMatrix(ring);
                for (int i = 0; i < mod_count.length; i++) {
                    System.out.print("  " + mod_count[i]);
                }

                System.out.println();
                int gen_mod_count = 1;
                //вычисляем кол-во точек
                for (int i = 0; i < mod_count.length; i++) {
                    gen_mod_count = gen_mod_count * mod_count[i];
                }
                //System.out.println("всего точек="+gen_mod_count);

                //заполняем массив со значениями модулей
                //все модули

                int[][] data = setOfAllModules(mod_count, mpiSize, rank);
                //количество числовых модулей
                int cmn = mod_count[mod_count.length - 1];
                long numberOfBits = ch.complexityCoeffOfCharPolPolynomialMatrix(r);
                data[0] = Newton.primesForTask(numberOfBits, 1, ring);
//                 NumberZ bound = (NumberZ)new NumberZ(2).pow(cmn, ring);
//                data[0] = Newton.primesForTask(bound, 1, ring);
//                Newton.initCache();
//                Newton.growMulPrims(cmn);
//                data[0] = new int[cmn];
//                System.arraycopy(Newton.prims, 0, data[0], 0, cmn);
                //System.out.println("data =  " + a.toString(data));

                //метод вычисления рез.  по каждому модулю для данного процессора,  a - -исходная матрица,               
                //modules_res[] - результаты,
//                Runtime rt = Runtime.getRuntime();
//                System.out.println(rank+":до Полный объем памяти: "+ rt.totalMemory());
//                long n1 = rt.freeMemory();
//                System.out.println(rank+":до Свободная память: "+ rt.freeMemory());
                long t5 = System.currentTimeMillis();
                Polynom[] modules_res = eachProcCalcModules(mod_count, data, a, ring, mpiSize, rank);
//                System.out.println(rank+":после Свободная память: "+ rt.freeMemory());
                //System.out.println(rank + "modules_res.length" + modules_res.length);
                //for (int i = 0; i < modules_res.length; i++) {
                long t3 = System.currentTimeMillis();

                /*
                 * 0-й процессор не восстанавливает, он принимает от остальных
                 * части хар. полинома и записывает их в файл
                 */

                //Разбиение полинома
                //количество частей= number of monoms> number of processors -1?
                //number of monoms: number of processors -1
                int numParts = (size + 1) < (mpiSize - 1) ? (size + 1) : (mpiSize - 1);
                Polynom[][] res_parts = new Polynom[modules_res.length][numParts];
                //делим один полином на части -- образец деления
                Polynom etalon_parts[] = modules_res[0].Sub_n_polynoms(numParts);
                //System.out.println("etalon_parts.l= "+etalon_parts.length);

                //пересылка всем процессорам образца деления полинома на части

                Object[] send_etalon = new Object[numParts];
                for (int i = 0; i < numParts; i++) {
                    send_etalon[i] = (Object) etalon_parts[i];
                }
                //t = MPI.COMM_WORLD.Object_Serialize(send_etalon, 0, numParts, MPI.OBJECT);
                ti = new int[]{t.length};
                MPI.COMM_WORLD.bcast(ti,  1, MPI.INT, 0);
                //MPI.COMM_WORLD.Bcast(t, 0, t.length, MPI.BYTE, 0);
                //MPI.COMM_WORLD.Bcast(send_etalon, 0, mpiSize, MPI.OBJECT, 0);
                MPITransport.bcastObjectArray(send_etalon,send_etalon.length, 0);

                //разбиение остальных полиномов
                res_parts[0] = etalon_parts;
                for (int i = 1; i < modules_res.length; i++) {
                    res_parts[i] = modules_res[i].sub_monom_pol(etalon_parts, ring);

//                                    for (int j = 0; j < res_parts[i].length; j++) {
//                                      System.out.print(rank+":     "+res_parts[i][j].toString()+"   ");
//                                  }
//                                    System.out.println();
                }



                //Обмен частями полиномов: 
                //k-й процессор получает все (k-1)-ые части всех полиномов из res_parts
                //k=1,2,...,mpiSize-1

                //
                int nt = gen_mod_count / (mpiSize - 1);//точек на процессоре,  
                int np = gen_mod_count % (mpiSize - 1);//кроме первых np процессоров - у них по nt+1 точек
                Object recv_parts[] = new Object[gen_mod_count];
                Object send_parts[] = new Object[res_parts.length];

                for (int i = 0; i < numParts; i++) {
                    for (int j = 0; j < res_parts.length; j++) {
                        send_parts[j] = (Object) res_parts[j][i];
                    }
//                    if(i==(rank-1))
//                        if(i<=np) System.arraycopy(send_parts, 0, recv_parts, (i-1)*(nt+1), send_parts.length);
//                        else System.arraycopy(send_parts, 0, recv_parts, np*(nt+1)+(i-np-1)*nt, send_parts.length);
//                    else 
                    //MPI.COMM_WORLD.Isend(send_parts, 0, send_parts.length, MPI.OBJECT, i + 1, 10);
                    MPITransport.iSendObjectArray(send_parts, 0, send_parts.length, i+1, 10);
                }

                //0 processor recive parts of characteristic polynomial
                //receiving numbers of parts
//                int npProc[] = new int[numParts];
//                for (int i = 1; i <= numParts; i++) {
//                    MPI.COMM_WORLD.Recv(npProc, i - 1, 1, MPI.INT, i, 0);
//                }
                Object[] npProc = new Object[numParts];
                for (int i = 1; i <= numParts; i++) {
                    //MPI.COMM_WORLD.Recv(npProc, i - 1, 1, MPI.INT, i, 0);
                    MPITransport.recvObjectArray(npProc, i - 1, 1, i, 0);
                }
                String name = "/home/oxana" + "/charPol";
                File file = new File(name);
                FileOutputStream fileOut = new FileOutputStream(file);
                //!!!СЧИТАЕМ, ЧТО НА ВСЕХ ПРОЦЕССОРАХ ОДИНАКОВОЕ КОЛ-ВО ЧАСТЕЙ И ОНО=np
                np = (int)npProc[0];
                //receiving parts and writing them in file
                Object charpolObj[] = new Object[1];
                for (int i = 0; i < np; i++) {
                    for (int j = 1; j <= numParts; j++) {
                        //receiving part
                        //MPI.COMM_WORLD.Recv(charpolObj, 0, 1, MPI.OBJECT, j, i);
                        MPITransport.recvObjectArray(charpolObj, 0, 1, j, i);
                        Polynom charpol = (Polynom) charpolObj[0];
                        String s = charpol.toString(r);
                        //writing in file
                        fileOut.write(s.getBytes());
                    }
                }
                fileOut.close();



            } else {
                long t8 = System.currentTimeMillis();
                int[] ti = new int[1];
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
                byte[] t = new byte[ti[0]];
                //System.out.println("ti[0]="+ti[0]);
                //MPI.COMM_WORLD.bcast(t, ti[0], MPI.BYTE, 0);
                Object[] tt = new Object[3]; // заводится массив объектов длинной 1
                //процедура обратная для Serialize
                //MPI.COMM_WORLD.Object_Deserialize(tt, t, 0, 3, MPI.OBJECT);
                MPITransport.bcastObjectArray(tt, 3, 0);
                MatrixD a = (MatrixD) tt[0];
                Ring r = (Ring) tt[1];
                Ring ring = (Ring) tt[2];
                long t4 = System.currentTimeMillis();
                charPolynomMatrixD ch = new charPolynomMatrixD(a);
                int[] mod_count = ch.complexityPolynomialMatrix(ring);
                int gen_mod_count = 1;
                for (int i = 0; i < mod_count.length; i++) {
                    gen_mod_count = gen_mod_count * mod_count[i];
                }
                //заполняем массив со значениями модулей
                int[][] data = setOfAllModules(mod_count, mpiSize, rank);
                //количество числовых модулей
                int cmn = mod_count[mod_count.length - 1];
                long numberOfBits = ch.complexityCoeffOfCharPolPolynomialMatrix(r);
                data[0] = Newton.primesForTask(numberOfBits, 1, ring);
//                NumberZ bound = (NumberZ)new NumberZ(2).pow(cmn, ring);
//                data[0] = Newton.primesForTask(bound, 1, ring);
//                Newton.initCache();
//                Newton.growMulPrims(cmn);
//                data[0] = new int[cmn];
//                System.arraycopy(Newton.prims, 0, data[0], 0, cmn);
                //System.out.println(rank+": data "+a.toString(data));

                //метод вычисления рез.  по каждому модулю  a - -исходная матрица, modules_res[] - результаты, 
                long t1 = System.currentTimeMillis();
                Polynom[] modules_res = eachProcCalcModules(mod_count, data, a, ring, mpiSize, rank);
//                for (int i = 0; i < modules_res.length; i++) {
//                    System.out.println(rank+": pol =  "+modules_res[i].coeffs.length);
//                }
                // System.out.println("rank= "+rank+": mod_res.len= "+modules_res.length);
//              for (int i = 0; i < modules_res.length; i++) {
//                    System.out.println(rank+": pol =  "+modules_res[i].toString());
//                }
                long t3 = System.currentTimeMillis();
                //System.out.println(rank+": ProcCalc "+(t3-t1)+"   ");

                //количество частей
                int size = a.M.length;
                int numParts = (size + 1) < (mpiSize - 1) ? (size + 1) : (mpiSize - 1);
                Polynom[][] res_parts = new Polynom[modules_res.length][numParts];
                //получение образца деления 

                ti = new int[1];
                MPI.COMM_WORLD.bcast(ti,  1, MPI.INT, 0);
//                t = new byte[ti[0]];
//                MPI.COMM_WORLD.bcast(t,  ti[0], MPI.BYTE, 0);
                Object[] resv_etalon = new Object[numParts];
                MPITransport.bcastObjectArray(resv_etalon, resv_etalon.length, 0);
                //процедура обратная для Serialize
                //MPI.COMM_WORLD.Object_Deserialize(resv_etalon, t, 0, numParts, MPI.OBJECT);
                Polynom[] etalon_parts = new Polynom[numParts];
                for (int i = 0; i < numParts; i++) {
                    etalon_parts[i] = (Polynom) resv_etalon[i];
                    //System.out.println("etalon_parts[i] = "+etalon_parts[i]);
                }

                //деление остатков-полиномов на части
                for (int i = 0; i < modules_res.length; i++) {
                    res_parts[i] = modules_res[i].sub_monom_pol(etalon_parts, ring);
                    //System.out.println("res_parts[i] = "+Array.toString(res_parts[i]));
                }

                //Обмен частями полиномов: 
                //k-й процессор получает все k-ые части всех полиномов из res_parts

                int nt = gen_mod_count / (mpiSize);//точек было на процессоре, 
                //System.out.println("nt= "+nt);
                int np = gen_mod_count % (mpiSize);//кроме первых np процессоров - у них по nt+1 точек
                Object recv_parts[] = new Object[gen_mod_count];
                Object send_parts[] = new Object[res_parts.length];
                for (int i = 0; i < numParts; i++) {
                    //System.out.println(rank+": i= "+i);
                    for (int j = 0; j < res_parts.length; j++) {
                        send_parts[j] = (Object) res_parts[j][i];
                    }
                    if (rank == (i + 1)) {
                        if (i < np) {
                            System.arraycopy(send_parts, 0, recv_parts, rank * (nt + 1), send_parts.length);
                            //System.out.println(rank+": copy1 ");
                        } else {
                            
                            System.arraycopy(send_parts, 0, recv_parts, np *(nt+1) + (rank - np) * nt, send_parts.length);
                            //System.out.println(rank+": copy1 ");
                            //System.out.println("ttttttt");
                        }
                    } else {
                        //MPI.COMM_WORLD.Isend(send_parts, 0, send_parts.length, MPI.OBJECT, i + 1, 10);
                        MPITransport.iSendObjectArray(send_parts, 0, send_parts.length, i+1, 10);
                        //System.out.println(rank+": send "+(i+1));    
                    }

                }
                if (rank <= numParts)//1,2,...,numParts processors recive 
                {
                    for (int i = 0; i < mpiSize; i++) { //from all processors
                        if (i != rank)//except processor rank
                        {
                            if (i < np) {
                                //System.out.println(rank+": I have been here1");
                                //MPI.COMM_WORLD.Recv(recv_parts, i * (nt + 1), nt + 1, MPI.OBJECT, i, 10);
                                MPITransport.recvObjectArray(recv_parts, i*(nt+1), nt+1, i, 10);
                                
                            } else {
                                //System.out.println(rank+": I have been here2");
                                //MPI.COMM_WORLD.Recv(recv_parts, np * (nt + 1) + (i - np) * nt, nt, MPI.OBJECT, i, 10);
                                MPITransport.recvObjectArray(recv_parts, np*(nt+1)+(i-np)*nt, nt, i, 10);
                            }
                        }

                    }
                }
                Polynom charpol;
                long t2 = System.currentTimeMillis();
                if (rank <= numParts) {
                    Polynom[] rem = new Polynom[gen_mod_count];//всего остатков
                    for (int i = 0; i < gen_mod_count; i++) {
                        rem[i] = (Polynom) recv_parts[i];
                    }
//                    System.out.println(rank + ": rem= " + rem.length);
//                    System.out.println("rem= "+ Array.toString(rem));
//                
                    //восстановление по полиномиальным модулям
                    int nnum = data[0].length;//кол-во числ модулей
                    int nr = rem.length / nnum;//кол-во остатков, которые надо восстановить по полин модулям
                    Polynom[] result = new Polynom[nnum];
                    for (int number = 0; number < nnum; number++) {//по числ модулям
                        ring.setMOD32(data[0][number]);
                        Polynom[] arem = new Polynom[nr];//массив текущих остатков
                        System.arraycopy(rem, number * nr, arem, 0, nr);//в arem записываем остатки
                        int nr1 = nr;
                        int p = data[0][number];//текущий числовой модуль
                        for (int j = 1; j < data.length; j++) {//пробегаем по каждой переменной
                            int len = data[j].length;
                            int s = 0;//счетчик для массива восстановленных полиномов
                            int k = 0;
                            Polynom[] newrem = new Polynom[nr1 / len];
                            //System.out.println("len= "+len);
                            while (k < arem.length) {
                                Polynom rr[] = new Polynom[len];//остатки
//                            System.out.println("from arem.len= "+arem.length+" begin= "
//                                    +k+" in rr.len= "+rr.length);
                                System.arraycopy(arem, k, rr, 0, len);
                                //восстановленный полином
                                Polynom pol = Polynom.recoveryOfLin(rr, 1, j, p, ring)[0];
                                newrem[s] = pol;
                                s++;
                                k += len;

                            }
                            arem = new Polynom[s];
                            System.arraycopy(newrem, 0, arem, 0, s);
                            nr1 /= len;
                        }
                        result[number] = arem[0];
                        //System.out.println(rank+":  "+result[number].coeffs.length);
                        //System.out.println(rank + ":  mod="+data[0][number] +",  resultModNuvberMod= " + result[number].toString());

                    }


                    //восстановление по числовым модулям
                    //остатки result
                    //модули data[0]
                    //int d0l = data[0].length;
                    int s = 200;//number of monoms in part
                    np = result[0].coeffs.length > s ?//number of parts
                            result[0].coeffs.length / s : 1;
                    //MPI.COMM_WORLD.iSend(new int[]{np}, 0 1, MPI.INT, 0, 0);
                    MPITransport.iSendObject(np, 0, 0);
                    Polynom[][] result_parts = new Polynom[nnum][np];
                    result_parts[0] = result[0].Sub_n_polynoms(np);
                    //partitioning
                   for (int i = 1; i < nnum; i++) {                            
                            result_parts[i] = result[i].sub_monom_pol(result_parts[0], ring);
                        }
                    //System.out.println("result_parts= "+Array.toString(result_parts[nnum-1]));
                    //recovery
                    
                    for (int i = 0; i < np; i++) {
                        ring.MOD32 = Integer.MAX_VALUE;
                        for (int j = 0; j < nnum; j++) {
                            result[j] = result_parts[j][i];
                        }
                        //System.out.println("rem= "+Array.toString(result));
                        charpol = Newton.recoveryNewtonPolynom(data[0], result, ring);

                        //coeffs in [-mod/2;mod/2]

                        int narr[] = new int[nnum];
                        System.arraycopy(data[0], 0, narr, 0, nnum);
                        NumberZ mod = new NumberZ(narr[0]);
                        for (int j = 1; j < narr.length; j++) {
                            mod = mod.multiply(new NumberZ(narr[j]));
                        }
                        NumberZ mod2 = mod.divide(new NumberZ(2));
                        for (int j = 0; j < charpol.coeffs.length; j++) {
                            if ((charpol.coeffs[j].abs(r)).compareTo(mod2) != -1) {
                                if (charpol.coeffs[j].isNegative()) {
                                    charpol.coeffs[j] = charpol.coeffs[j].add(mod, r);
                                } else {
                                    charpol.coeffs[j] = charpol.coeffs[j].subtract(mod, r);
                                }
                            }
                        }
                        //System.out.println(rank+": charpol = "+charpol.toString());
                        int vars = charpol.powers.length / charpol.coeffs.length;
                        int[] varsMap = new int[vars];
                        for (int j = 0; j < vars - 1; j++) {
                            varsMap[j + 1] = j;
                        }
                        varsMap[0] = vars - 1;
                        charpol = charpol.changeOrderOfVars(varsMap, ring);
                        //send to 0 processor
                        send_parts = new Object[1];
                        send_parts[0] = (Object) charpol;
                        //MPI.COMM_WORLD.Isend(send_parts, 0, 1, MPI.OBJECT, 0, i);
                        MPITransport.iSendObjectArray(send_parts, 0, 1, 0, i);
                        //System.out.println(rank + ": charpol+ = " + charpol.toString());
                        //System.out.println(rank+ " calculated");
                    }
                }
                //else charpol=Polynom.polynom_zero(r.numberONE);
                long t6 = System.currentTimeMillis();
                //сборка результата на 0-м процессоре
//                Object send[] = new Object[]{charpol};
//                Object recv[] = new Object[1];
//                MPI.COMM_WORLD.Gather(send, 0, 1, MPI.OBJECT, recv, 0, 1, MPI.OBJECT, 0);
//                long t7 = System.currentTimeMillis();
//                // System.out.println(rank+ " Gather");
//                System.out.println(rank+": bcast= "+(t4-t8)+", Calc= "+(t3-t1)+
//                        ", alToAll= "+(t2-t3)+", Recovery= "+(t6-t2)+", Gather= "+(t7-t6)+", allTime= "+(t7-t8));
            }

            MPI.Finalize();
        } catch (MPIException e) {
            System.out.println("error=" + e.toString() + "  " + e.getMessage());
        }

    }

//
//    public static Element[] getElements(MatrixS m, int start, int count,){
//        int r=0;
//        int c=0;
//        for(int i=0; i<m.M.length; i++){
//            c=start-m.M[i].length;
//            if(c<0) break;
//        }
//
//
//
//        return null;
//    }
    public static void sendMatrixElements(MatrixD m, int index, int count, int rank, int mpiSize, Ring ring, Element[][] allRResult) throws MPIException, IOException {
        int mecpp_r = count % mpiSize;
        int r = 0;
        int c = 0;


        for (int drank = mpiSize - 1; drank > -1; drank--) {
            int mec_pp = count / mpiSize;
            if (drank < mecpp_r) {
                mec_pp++;
            }
            Element[] buf = new Element[mec_pp];
            for (int i = 0; i < mec_pp; i++) {
                buf[i] = m.M[r][c];
                c++;
                if (c > m.M[r].length - 1) {
                    c = 0;
                    r++;
                }

            }
            //после заполнения буфера - делаем посылку,  если попадаем на свой процессор, то не посылаем а сразу записываем
            if (drank != rank) {
                MPITransport.iSendObject(buf, drank, index); //MPI.COMM_WORLD.Isend(buf, 0, buf.length-1, MPI.OBJECT, drank, index);
            } //send
            else {
                int cfp = allRResult.length / mpiSize;
                int rfp = allRResult.length % mpiSize;
                int step = 0;
                if (rank <= rfp) {
                    step = rank * (cfp + 1);
                } else {
                    step = (cfp + 1) * rfp + (rank - rfp) * cfp;
                }
                allRResult[step + index] = buf;


            }
        }

    }

    public static void recvMatrixElements(int count, int rank, int mpiSize, Ring ring, Element[][] allRResult) throws MPIException, IOException, ClassNotFoundException {
        int mecpp_r = count % mpiSize;
        int r = 0;
        int c = 0;
        // for (int k=0; k<mpiSize-1; k++){
        //     System.out.println("k="+k);
//           for (int i=0; i<mec_pp; i++){
//               buf[i]=m.M[r][c];
//               c++;
//               if(c>m.M[r].length-1) {c=0; r++;}
//
//           }
        //после заполнения буфера - делаем посылку,  если попадаем на свой процессор, то не посылаем а сразу записываем
        // if (k != rank) {

        //System.out.println("probe");
        Status s = MPI.COMM_WORLD.probe(MPI.ANY_SOURCE, MPI.ANY_TAG);
        int n = s.getCount(MPI.BYTE);
        // System.out.println("probe good");
        int mec_pp = count / mpiSize;
        if (s.getSource() < mecpp_r) {
            mec_pp++;
        }
        Element[] buf;
//                  System.out.println("source="+s.source);
//                  System.out.println("tag="+s.tag);
        // System.out.println("recv");
        buf = (Element[]) MPITransport.recvObject(s.getSource(), s.getTag());
        // MPI.COMM_WORLD.Recv(buf, 0, buf.length-1, MPI.OBJECT, s.source, s.tag);
        // System.out.println("recv good");
        int cfp = allRResult.length / mpiSize;
        int rfp = allRResult.length % mpiSize;
        int step = 0;
        if (s.getSource() <= rfp) {
            step = s.getSource() * (cfp + 1);
        } else {
            step = (cfp + 1) * rfp + (s.getSource() - rfp) * cfp;
        }
        //System.out.println("step="+(step+s.tag));
        allRResult[step + s.getTag()] = buf;

        //send
        //  } else {
        //  System.out.println("no");
//                 int cfp = allRResult.length / mpiSize;
//                 int rfp = allRResult.length % mpiSize;
//                 int step = 0;
//                 if (rank <= rfp) {
//                     step = rank * ( cfp + 1 ) ;
//                 } else {
//                     step = ( cfp + 1 ) * rfp + ( rank - rfp ) * cfp;
//                 }
//                 System.out.println("step="+(step+index));
//                 allRResult[step+index]=buf;


        // }
        //  }

    }

    public static int[] indexFor(int proc, int count_proc, int[][] data) {
        int g_count = 1;
        for (int i = 0; i < data.length; i++) {
            g_count = g_count * data[i].length;
        }

        int cfp = g_count / count_proc;
        int rfp = g_count % count_proc;
        int step = 0;
        if (proc <= rfp) {
            step = proc * (cfp + 1) - 1;
        } else {
            step = (cfp + 1) * rfp + (proc - rfp) * cfp;
        }
        int[] indexes = new int[data.length];
        int k = data.length - 1;
        for (int i = data.length - 1; i > 0; i--) {
            if (i == data.length - 1) {
                int temp = step;
                if (proc <= rfp) {
                    temp++;
                }
                indexes[i - 1] = temp / data[k].length;
                indexes[i] = temp % data[k].length;
            } else {
                indexes[i - 1] = indexes[i] / data[k].length;
                indexes[i] = indexes[i] % data[k].length;
            }
            k--;
        }
        // for (int i=0; i<indexes.length; i++) System.out.println("indexes["+i+"]="+indexes[i]);

        return indexes;
    }

    //вычисление матриц по указанным модулям
    public static Polynom[] calcModules(MatrixD m, int[][] data, int[] indexes, int counts, Ring r) {
        Polynom res[] = new Polynom[counts];
        MatrixD mm = new MatrixD();
        long[][] res1 = new long[m.M.length][m.M.length];//!+
        //System.out.println("ring= "+r.toString());
        charPolynomMatrixD ch = new charPolynomMatrixD();
        for (int k = 0; k < counts; k++) {
            //Ring rm = new Ring("Zp32[x,y]");
            r.setMOD32(data[0][indexes[0]]);
            //System.out.println("ring= "+r.toString());
            mm = (MatrixD) m.toNewRing(r.algebra[0], r);
            //System.out.println(r.algebra[0] + "gggg "+ ((Polynom)mm[k].M[0][0]).coeffs[0].numbElementType());
            NumberZp32[] vars = new NumberZp32[indexes.length - 1];
            for (int h = 0; h < vars.length; h++) {
                vars[h] = new NumberZp32(data[h + 1][indexes[h + 1]]);
                //System.out.print("  "+ vars[h]);
            }
//System.out.println();
//System.out.println("mm=    "+(mm[k].M[16][47] instanceof NumberZp32));
            //вычисление полиномиальной матрицы в точке и по модулю
            res1 = mm.valueOf(vars, r);
            //System.out.println("    "+mm[k].toString(res1[k]));//+

//-            for (int i = 0; i < m.M.length; i++) {
//               for (int j = 0; j < m.M[i].length; j++) {
//                    //-res[k].M[i][j] = new Polynom(((Polynom) res[k].M[i][j]).value(vars, r));
//                    res[k].M[i][j] = (((Polynom) res[k].M[i][j]).value(vars, r));                   
//                }
//-            }
            //-res[k]=res[k].adjoint(r);

            //вычисление характеристических полиномов матриц
            res[k] = (Polynom) m.M[0][0].myOne(r);
            ch.charPolDanil(res1, res[k], r);//+
            //System.out.println("pol = "+res[k].toString(r));
            nextIndexes(data, indexes);

        }


        return res;
    }

    public static void nextIndexes(int data[][], int[] indexes) {
        int k = data.length - 1;
        for (int i = data.length - 1; i > 0; i--) {
            if (i == data.length - 1) {
                indexes[i - 1] = indexes[i - 1] + (indexes[i] + 1) / data[k].length;
                indexes[i] = (indexes[i] + 1) % data[k].length;
            } else {
                indexes[i - 1] = indexes[i - 1] + (indexes[i]) / data[k].length;
                indexes[i] = (indexes[i]) % data[k].length;
            }
            k--;
        }
        //  for (int i=0; i<indexes.length; i++) System.out.println("indexes["+i+"]="+indexes[i]);
    }

    public static int[][] setOfAllModules(int[] mod_count, int proc_count, int rank) {
        //заведем данные по модулям
        modDATA md = new modDATA();
        //кол-во числовых модулей
        //int cmn = mod_count[mod_count.length - 1];
        //заполняем массив простыми числовыми модулями модулями 

        //int[] num_modules = md.getModules(cmn, 0);
        //  for (int i=0; i<num_modules.length; i++) System.out.println("rank="+rank+" | "+num_modules[i]);
        //двумерный массив - числовые модули + модули по каждой переменной
        int[][] data = new int[mod_count.length][];
        //в нулевую стоку записываем числовые модули (передаем ссылку)
        //data[0] = num_modules;
        ///вычисляем кол-во модулей всего - сначала полиномиальных
        int generalcount = 1;
        for (int i = 0; i < mod_count.length - 1; i++) {
            int m = 1;
            data[i + 1] = new int[mod_count[i]];
            generalcount = generalcount * mod_count[i];
            for (int k = 0; k < mod_count[i]; k++) {
                data[i + 1][k] = m;
                m++;
            }
        }
        return data;
    }

    public static Polynom[] eachProcCalcModules(int[] mod_count, int[][] data,
            MatrixD a, Ring r, int proc_count, int rank) {

        // получаем общее кол-во модулей
        int generalcount = 1;
        for (int i = 0; i < data.length; i++) {
            generalcount = generalcount * data[i].length;
        }

//              for (int i=0; i<data.length; i++){
//                  for(int k=0; k<data[i].length; k++) {System.out.print(" "+data[i][k]); }
//                  System.out.println("");
//              }
        //вычисляем стартовые позиции модулей в массиве всех модулей для каждого процессора
        int[] indexes = indexFor(rank, proc_count, data);

//        for (int i = 0; i < indexes.length; i++) {
//            System.out.print("  "+indexes[i]);
//        }
//        System.out.println(" ");

        //вычисляем сколько модулей надо посчитать на каждом процессоре
        int eachProcModCount = generalcount / proc_count;//целое число моделй
        int rr = generalcount % proc_count; // распределяем остаток модулей
        if (rank < rr) {
            eachProcModCount++;
        }
        //System.out.println("calc count modules="+eachProcModCount);
        Polynom[] res = calcModules(a, data, indexes, eachProcModCount, r);

        //частичное восстановление


        return res;
    }

    //================
    //частичное восстановление по полиномиальным модулям
    public static Polynom[] particularRecovery(Polynom[] rem, int[][] data,
            Ring ring, int i) {
        int nnum = data[0].length;//кол-во числ модулей
        int nr = rem.length / nnum;//кол-во остатков, которые надо восстановить по полин модулям
        Polynom[] result = new Polynom[nnum];
        for (int number = 0; number < nnum; number++) {//по числ модулям
            //System.out.println(rank+": number= "+number);
            int p = data[0][number];//текущий числовой модуль
            ring.setMOD32(p);
            Polynom[] arem = new Polynom[nr];//массив текущих остатков
            System.arraycopy(rem, number * nr, arem, 0, nr);//в arem записываем остатки
            int nr1 = nr;
            for (int j = 1; j < data.length; j++) {//пробегаем по каждой переменной
                //System.out.println(rank+": run for variable "+j);
                int len = data[j].length;
                //System.out.println("len= "+len);
                int s = 0;//счетчик для массива восстановленных полиномов
                //int p = data[0][number];//текущий числовой модуль
                int k = 0;
                Polynom[] newrem = new Polynom[nr1 / len];
                while (k < arem.length) {
                    Polynom rr[] = new Polynom[len];//остатки
                    //System.out.println(rank+": number= "+number+"  j= "+j+" от "+k+"  "+len+"штук"+ p);
                    System.arraycopy(arem, k, rr, 0, len);

                    //восстановленный полином
                    //if(number==0)System.out.println(rank+": rr[0]= "+rr[0].toString());
                    Polynom pol = Polynom.recoveryOfLin(rr, 1, j, p, ring)[0];
                    //if(number==0)System.out.println(rank+":pol= "+pol.toString());
                    newrem[s] = pol;
                    s++;
                    k += len;
                    //System.out.println(rank+": number= "+number+": j="+j+
                    // "   pol.coeffs.l= "+newrem[s-1].coeffs.length);
                }

                arem = new Polynom[s];
                System.arraycopy(newrem, 0, arem, 0, s);
                nr1 /= len;
            }
            result[number] = arem[0];
        }
        return new Polynom[]{new Polynom()};
    }
    //==================================

//    public static void sendArrElement(Element[] a, int proc, int tag) throws IOException, MPIException {
//
//        int[] number = new int[]{a.length};
//        MPI.COMM_WORLD.Send(number, 0, 1, MPI.INT, proc, tag);
//
//        Object o[] = new Object[a.length];
//        for (int i = 0; i < o.length; i++) {
//            o[i] = a[i].clone();
//        }
//        System.out.println("send");
//        MPI.COMM_WORLD.Send(o, 0, o.length, MPI.OBJECT, proc, tag);
//        System.out.println("sended");
//    }

//    public static Element[] recvArrElement(int proc, int tag) throws MPIException, IOException, ClassNotFoundException, CloneNotSupportedException {
//
//
//        int[] number = new int[1];
//        MPI.COMM_WORLD.Recv(number, 0, 1, MPI.INT, proc, tag);
//        System.out.println("nnnn=" + number[0]);
//        Object[] o = new Object[number[0]];
//        Element[] a = new Element[number[0]];
//        MPI.COMM_WORLD.Recv(o, 0, o.length, MPI.OBJECT, proc, tag);
//        //   System.out.println("recv");
//        for (int i = 0; i < o.length; i++) {
//            a[i] = (Element) (o[i]);
//        }
//        return a;
        // Status s=MPI.COMM_WORLD.Probe(proc,tag);
//        int n=5950;
//        System.out.println("nnnnnnn2="+n);
//        byte[] arr=new byte[n];// заводится байт массив с нужным числом элементов
//        MPI.COMM_WORLD.Recv(arr, 0, n,MPI.BYTE, proc, tag);//Recv = блокирующий прием массива из буффера ввода в массив arr
//        ByteArrayInputStream bais=new ByteArrayInputStream(arr);
//        ObjectInputStream is=new ObjectInputStream(bais);
//        System.out.println("test");
//        Element[] recv=new Element[number[0]];
//        for(int i=0; i<recv.length; i++){
//          recv[i]=(Element)is.readObject();
//        }
//        System.out.println("final");
//        return recv;
//    }

//    public static void isendObject(Object a, int proc, int tag) throws MPIException, IOException {
//        /**
//         * MPIException нужен т.к. мы пользуемся в этом методе обращением к МPI
//         * Object a = посылаемый объект, имеет интерфейс serilizeable, т.е.
//         * существует механизм превращающий его в массив байтов и обратно int
//         * proc = номер процессора которому посылаем объект int tag = таг
//         * который пошлем с этим объектом
//         */
//        byte[] temp = MPI.COMM_WORLD.Object_Serialize(new Object[]{a}, 0, 1, MPI.OBJECT); //метод Object_Serialize() превращает массив объектов в массив байтов
//        MPI.COMM_WORLD.Isend(temp, 0, temp.length, MPI.BYTE, proc, tag);  //, MPI.BYTE= тип передаваемых данных
//
//        /**
//         * Isend = неблокирующая пересылка temp = массив который посылаем 0 =
//         * номер элемента массива с которого начинам temp.length = число
//         * посылаемых элементов массива MPI.BYTE = тип элементов в массиве proc
//         * = номер процессора которыму посылаем сообщение tag = таг сообщение
//         */
//    }

//    public static void sendObject(Object a, int proc, int tag) throws MPIException, IOException {
//        /**
//         * MPIException нужен т.к. мы пользуемся в этом методе обращением к МPI
//         * Object a = посылаемый объект, имеет интерфейс serilizeable, т.е.
//         * существует механизм превращающий его в массив байтов и обратно int
//         * proc = номер процессора которому посылаем объект int tag = таг
//         * который пошлем с этим объектом
//         */
//        byte[] temp = MPI.COMM_WORLD.Object_Serialize(new Object[]{a}, 0, 1, MPI.OBJECT); //метод Object_Serialize() превращает массив объектов в массив байтов
//        Request r = MPI.COMM_WORLD.Isend(temp, 0, temp.length, MPI.BYTE, proc, tag);  //, MPI.BYTE= тип передаваемых данных
//        r.Wait();
//        /**
//         * Isend = неблокирующая пересылка, r.Wait - ждет конца послыки temp =
//         * массив который посылаем 0 = номер элемента массива с которого начинам
//         * temp.length = число посылаемых элементов массива MPI.BYTE = тип
//         * элементов в массиве proc = номер процессора которыму посылаем
//         * сообщение tag = таг сообщение
//         */
//    }
//
//    public static Object recvObject(int proc, int tag, int n) throws MPIException, IOException, ClassNotFoundException {
//        /**
//         * proc = номер процессора от которого получаем объект tag = таг который
//         * пришел с объектом
//         */
//        // Status s=MPI.COMM_WORLD.Probe(proc, tag);  //команда считывает статус буфера для приема сообщения от процессора proc с тагом tag
//        // int n=s.Get_count(MPI.BYTE); //динамический метод класса статус который подсчитывает количество элементов в буфере (в данном случае MPI.BYTE)
//        //  Status s=MPI.COMM_WORLD.Probe(proc, tag);
//        // n=s.Get_count(MPI.BYTE);
//        byte[] arr = new byte[n];// заводится байт массив с нужным числом элементов
//        MPI.COMM_WORLD.Recv(arr, 0, n, MPI.BYTE, proc, tag);//Recv = блокирующий прием массива из буффера ввода в массив arr
////         ByteArrayInputStream bais=new ByteArrayInputStream(arr);
////         ObjectInputStream is=new ObjectInputStream(bais);
////         Object a=is.readObject();
//        Object[] res = new Object[1]; // заводится массив объектов длинной 1
//        MPI.COMM_WORLD.Object_Deserialize(res, arr, 0, 1, MPI.OBJECT);//процедура обратная для Serialize
//        return res[0]; // передаем на выход процедуры полученный объект
//
//    }
//     public static void sendArrayOfObjects(Object[] a , int proc, int tag) throws MPIException{
//            for(int i=0;i<a.length;i++) sendObject(a[i], proc, tag+i);
//            System.out.println("New send 4 matrices to proc N"+ proc);
//    }
////вычисление матриц по указанным модулям
//    public static Polynom[] calcModules(MatrixD m, int[][] data,  int counts, Ring r) {
//        Polynom res [] = new Polynom [data[0].length];
//        
//        long[][][] res1 = new long[counts][][];//!+
//        //System.out.println("ring= "+r.toString());
//        charPolynomMatrixD ch = new charPolynomMatrixD();
//        for (int k = 0; k < data[0].length; k++) {
//            //Ring rm = new Ring("Zp32[x,y]");
//            r.setMOD32(data[0][k]);
//            MatrixD[] mm = new MatrixD[ data[data.length-1].length];
//            //System.out.println("ring= "+r.toString());
//            mm[k] = (MatrixD) m.toNewRing(r.algebra[0], r);
//            //System.out.println(r.algebra[0] + "gggg "+ ((Polynom)mm[k].M[0][0]).coeffs[0].numbElementType());
//            NumberZp32[] vars = new NumberZp32[indexes.length - 1];
//            for (int h = 0; h < vars.length; h++) {
//                vars[h] = new NumberZp32(data[h + 1][indexes[h + 1]]);
//            }
//            
//            //вычисление полиномиальной матрицы в точке и по модулю
//            res1[k]= mm[k].valueOf(vars, r);
//            //System.out.println("    "+mm[k].toString(res1[k]));//+
//
////-            for (int i = 0; i < m.M.length; i++) {
////               for (int j = 0; j < m.M[i].length; j++) {
////                    //-res[k].M[i][j] = new Polynom(((Polynom) res[k].M[i][j]).value(vars, r));
////                    res[k].M[i][j] = (((Polynom) res[k].M[i][j]).value(vars, r));                   
////                }
////-            }
//            //-res[k]=res[k].adjoint(r);
//            
//            //вычисление характеристических полиномов матриц
//            res[k] = (Polynom) m.M[0][0].myOne(r);
//            ch.charPolDanil(res1[k], res[k], r);//+
//            //System.out.println("pol = "+res[k].toString(r));
//            nextIndexes(data, indexes);
//
//        }
//
//
//        return res;
//    }
}
