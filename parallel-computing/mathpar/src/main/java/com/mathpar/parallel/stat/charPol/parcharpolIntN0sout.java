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
 * mpirun -np 2 java -cp /home/peresl/allMathpar/mathpar/target/classes com/mathpar/parallel/charPol.parcharpolIntN0sout
 * @author oxana
 */
public class parcharpolIntN0sout {
    public static void main(String[] args) throws IOException, ClassNotFoundException, CloneNotSupportedException {
        try {
            MPI.Init(new String[0]);
            int rank = MPI.COMM_WORLD.getRank();
            int mpiSize = MPI.COMM_WORLD.getSize();

            if (rank == 0) {
                long t1 = System.currentTimeMillis();
                int size = 8;
                int mdens = 100;
                String r_str = "Z[x]";
                String rp_str = "Zp32[x]";
//                int pdens = 100;
//                int vars = 1;
                int cbits = 7;
//                int deg = 1;
//                int deg1[] = {1};
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
//                    if (args[i].contains("deg")) {
//                        deg = Integer.parseInt(args[i].replace("deg=", ""));
//                    }
//                    if (args[i].contains("pdens")) {
//                        pdens = Integer.parseInt(args[i].replace("pdens=", ""));
//                    }
//                    if (args[i].contains("vars")) {
//                        vars = Integer.parseInt(args[i].replace("vars=", ""));
//                    }
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
               
                //входная матрица
                
                long mem1 = Runtime.getRuntime().freeMemory();
//                long mem = Runtime.getRuntime().totalMemory();
//                long allmem = Runtime.getRuntime().maxMemory();
//                System.out.println("maxmemory = "+ allmem+", total memory = "+mem+" bytes, free memory = "+mem1+" bytes");
//           
                Random ran = new Random(rnd);
                int arr [] = new int [size*size];
                int el;
                int cb = (1<<cbits)-1;
                for (int i = 0; i < arr.length; i++) {
                    el = ran.nextInt();
                    arr[i]= el%cb;
                }
                
                long mem3 = Runtime.getRuntime().freeMemory();
                System.out.println(rank+": making Matrix needs "+(mem1-mem3)+" bytes");
                //рассылка входной матрицы
                  MPI.COMM_WORLD.bcast(new int []{size}, 1, MPI.INT, 0); 
                  MPI.COMM_WORLD.bcast(arr, arr.length, MPI.INT, 0);                                               
               
                //рассылка колец
                //byte[] t=MPI.COMM_WORLD.Object_Serialize(new Object[]{r,ring},0,2,MPI.OBJECT);
                  r.cleanRing();
                  ring.cleanRing();
                Object[] t = new Object[]{r, ring};
                System.out.println("t = "+ Array.toString(t)+ "rank = "+ rank);
                int [] ti = new int[]{t.length};                
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
                //MPI.COMM_WORLD.bcast(t,  t.length, MPI.BYTE, 0);
                MPITransport.bcastObjectArray(t,t.length, 0);
                long t5 = System.currentTimeMillis();
                 int [][] ma = new int[size][size];
                 int beg = 0;
                 for (int i = 0; i < size; i++) {
                     System.arraycopy(arr, beg, ma[i], 0, size);
                     beg+=size;
                 }
                //System.out.println("A[0][0]= " + ma[0][0]);
                //MatrixD a = new MatrixD(ma, r);
                 //System.out.println("ma= "+a);
                //long mem2 = Runtime.getRuntime().freeMemory();
                //System.out.println(rank+": making int Ma[][]  needs "+(mem3-mem2)+" bytes");
                // вычисляем необходимое кол-во модулей по каждой
                //переменной. в последний элемент массива записываем кол-во числ. модулей
                charPolynomMatrixD ch = new charPolynomMatrixD();
                int[] mod_count = ch.complexityNumberZMatrix(ma, r);
                //кол-во бит в наибольшем числовом коэффициенте характ. полинома
                long numberOfBits = ch.complexityCoeffOfCharPolNumberZMatrix(ma, r);
                //System.out.println("кол-во бит в наибольшем числовом коэффициенте характ. полинома numberOfBits = "+numberOfBits );
//                for (int i = 0; i < mod_count.length; i++) {
//                    System.out.print("number of modules = " + mod_count[i]);
//                }

                System.out.println();
                

                //заполняем массив со значениями модулей
                //все модули
                
                //int[] data = new int[gen_mod_count];
                //количество числовых модулей
               /*
                NumberZ bound = (NumberZ)new NumberZ(2).pow(mod_count[0], ring);
                System.out.println("???количество числовых модулей bound = "+bound);
                int[] data = Newton.primesForTask(bound,1, ring);
                */
               //заполняем массив со значениями модулей
               int[] data = Newton.primesForTask(numberOfBits, 1, ring);
               mod_count[0]=data.length;
               int gen_mod_count = mod_count[0];               
                //вычисляем кол-во точек
                //gen_mod_count = mod_count[0];
                
                //System.out.println("всего точек="+gen_mod_count);
//                Newton.initCache();
//                Newton.growMulPrims(gen_mod_count);
//                System.arraycopy(Newton.prims, 0, data, 0, gen_mod_count);
                //System.out.println("data =  " + Array.toString(data));

                //метод вычисления рез.  по каждому модулю для данного процессора,  a - -исходная матрица,               
                //modules_res[] - результаты,

                Polynom[] modules_res = eachProcCalcModules(data, ma, ring, mpiSize, rank);
                long t3 = System.currentTimeMillis();
                //System.out.println(rank+": "+Array.toString(modules_res));
                //System.out.println(rank + ":  eachProcCalcModules");
//       =======================       
    //Разбиение полинома
                //количество частей
                //количество частей= number of monoms> number of processors -1?
                //number of monoms: number of processors -1
                int numParts = (size + 1) < (mpiSize - 1) ? (size + 1) : (mpiSize - 1);
                Polynom[][] res_parts = new Polynom[modules_res.length][numParts];
                //делим один полином на части -- образец деления
                Polynom etalon_parts[] = modules_res[0].Sub_n_polynoms(numParts);
                //System.out.println("etalon_parts.l= "+etalon_parts.length);

                //пересылка всем процессорам образца деления полинома на части

                Object[] send_etalon = new Object[numParts];
                //System.out.println("numParts = "+ numParts+ "etalon_parts.length="+etalon_parts.length);
                for (int i = 0; i < numParts; i++) {
                    send_etalon[i] = (Object) etalon_parts[i];
                }
                 //t=MPI.COMM_WORLD.Object_Serialize(send_etalon,0,numParts,MPI.OBJECT);
                ti = new int[]{send_etalon.length};
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
                //MPI.COMM_WORLD.bcast(t, t.length, MPI.BYTE, 0);
                MPITransport.bcastObjectArray(send_etalon, send_etalon.length, 0);
                //MPI.COMM_WORLD.Bcast(send_etalon, 0, mpiSize, MPI.OBJECT, 0);
                
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
                int nt = gen_mod_count/mpiSize;//точек на процессоре,  
                int np = gen_mod_count%mpiSize;//кроме первых np процессоров - у них по nt+1 точек
                Object recv_parts[] = new Object[gen_mod_count];
                Object send_parts[] = new Object[res_parts.length];
                for (int i = 0; i < numParts; i++) {                    
                    for (int j = 0; j < res_parts.length; j++) {
                        send_parts[j]= (Object) res_parts[j][i];
                    }
//                    if(i==(rank-1))
//                        if(i<=np) System.arraycopy(send_parts, 0, recv_parts, (i-1)*(nt+1), send_parts.length);
//                        else System.arraycopy(send_parts, 0, recv_parts, np*(nt+1)+(i-np-1)*nt, send_parts.length);
//                    else 
                    //MPI.COMM_WORLD.Isend(send_parts, 0, send_parts.length, MPI.OBJECT, i + 1, 10);
                      MPITransport.iSendObjectArray(send_parts, 0, send_parts.length, i+1, 10);
                }
                 System.out.println(rank + ":  send all parts"+(System.currentTimeMillis()-t1)+" sec");
                
                //0 processor recive parts of characteristic polynomial
                //receiving numbers of parts
                //int npProc[] = new int[numParts];
                Object[] npProc = new Object[numParts];
                for (int i = 1; i <= numParts; i++) {
                    //MPI.COMM_WORLD.Recv(npProc, i - 1, 1, MPI.INT, i, 0);
                    MPITransport.recvObjectArray(npProc, i - 1, 1, i, 0);
                }
                System.out.println(rank + ":  recv numbers of parts");
                
                String name = "/home/peresl/allMathpar/experiments" + "/charPol";
                File file = new File(name);
                FileOutputStream fileOut = new FileOutputStream(file);
                //!!!СЧИТАЕМ, ЧТО НА ВСЕХ ПРОЦЕССОРАХ ОДИНАКОВОЕ КОЛ-ВО ЧАСТЕЙ И ОНО=np
                np = (int)npProc[0];
                //receiving parts and writing them in file
                Object charpolObj[] = new Object[1];
                for (int i = 0; i < np; i++) {
                    for (int j = 1; j <= numParts; j++) {
                        if (j%128==0) System.out.print(", "+j);
                        //receiving part
                        //MPI.COMM_WORLD.Recv(charpolObj, 0, 1, MPI.OBJECT, j, i);
                        MPITransport.recvObjectArray(charpolObj, 0, 1, j, i);
                        Polynom charpol = (Polynom) charpolObj[0];
                        String s = charpol.toString(r);
                        //writing in file
                        fileOut.write(s.getBytes());
                }

               // NumberZ mod2 = mod.divide(new NumberZ(2));
               // for (int j = 0; j < charpol.coeffs.length; j++) {
                //    if ((charpol.coeffs[j].abs(r)).compareTo(mod2) != -1) {
                //        if (charpol.coeffs[j].isNegative()) {
                //            charpol.coeffs[j] = charpol.coeffs[j].add(mod, r);
                //        } else {
                //            charpol.coeffs[j] = charpol.coeffs[j].subtract(mod, r);
                        }
                fileOut.close();
                long t2 = System.currentTimeMillis();
                System.out.println();
                System.out.println("times= " + (t2 - t1));
                
//
            } else {
                long t8 = System.currentTimeMillis();
//                int [] ti = new int[1];
//                MPI.COMM_WORLD.Bcast(ti, 0, 1, MPI.INT, 0);
//                byte[] t = new byte[ti[0]];
//                //System.out.println("ti[0]="+ti[0]);
//                MPI.COMM_WORLD.Bcast(t, 0, ti[0], MPI.BYTE, 0);
//                Object[] tt=new Object[3]; // заводится массив объектов длинной 1
//                //процедура обратная для Serialize
//                MPI.COMM_WORLD.Object_Deserialize(tt, t, 0, 3, MPI.OBJECT);
                              
                int [] ti = new int[1];                
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
                int size = ti[0];
                int[] arr = new int[size*size];
                MPI.COMM_WORLD.bcast(arr, arr.length, MPI.INT, 0);
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
                 byte [] t = new byte [ti[0]];
//                MPI.COMM_WORLD.bcast(t, t.length, MPI.BYTE, 0);
                Object[] tt=new Object[2];
                //MPI.COMM_WORLD.Object_Deserialize(tt, t, 0, 2, MPI.OBJECT);
                //прием колец
                MPITransport.bcastObjectArray(tt, 2, 0);
                Ring r = (Ring) tt[0];
                Ring ring = (Ring) tt[1];
                int [][] ma = new int[size][size];
                 int beg = 0;
                 for (int i = 0; i < size; i++) {
                     System.arraycopy(arr, beg, ma[i], 0, size);
                     beg+=size;
                 }
                 //MatrixD a = new MatrixD(ma, r);
                
                //System.out.println(rank+": a= "+a.toString());
                
                long t4 = System.currentTimeMillis();
                charPolynomMatrixD ch = new charPolynomMatrixD();
                //старая оценка для 28-битных модулей
                int[] mod_count = ch.complexityNumberZMatrix(ma, ring);
                //int gen_mod_count = mod_count[0];
                
                //кол-во бит в наибольшем числовом коэффициенте характ. полинома
                long numberOfBits = ch.complexityCoeffOfCharPolNumberZMatrix(ma, r);
                //System.out.println("кол-во бит в наибольшем числовом коэффициенте характ. полинома numberOfBits = "+numberOfBits );
                
                //заполняем массив со значениями модулей
                int[] data = Newton.primesForTask(numberOfBits, 1, ring);
                mod_count[0]=data.length;
               int gen_mod_count = mod_count[0];    
               //int[] data =  new int[gen_mod_count];
                //количество числовых модулей
//                Newton.initCache();
//                Newton.growMulPrims(gen_mod_count);
//                System.arraycopy(Newton.prims, 0, data, 0, gen_mod_count);
               // NumberZ bound = (NumberZ)new NumberZ(2).pow(mod_count[0], ring);
                //int[] data = Newton.primesForTask(bound, 1, ring);
                
                 //System.out.println(rank+": data= "+Array.toString(data));
                //метод вычисления рез.  по каждому модулю  a - -исходная матрица, modules_res[] - результаты, 
                 long t1 = System.currentTimeMillis();
                Polynom[] modules_res = eachProcCalcModules(data, ma, ring, mpiSize, rank);
                //System.out.println(rank+" ended Modcalc");
                //System.out.println(rank+": полиномы "+Array.toString(modules_res));
                
                long t3 = System.currentTimeMillis();
                //System.out.println(rank+": ProcCalc "+(t3-t1)+"   ");

                //количество частей

                int numParts = (size + 1) < (mpiSize - 1) ? (size + 1) : (mpiSize - 1);
                Polynom[][] res_parts = new Polynom[modules_res.length][numParts];
                //получение образца деления 
                      
                ti = new int[1];
                MPI.COMM_WORLD.bcast(ti,  1, MPI.INT, 0);
                //t = new byte[ti[0]];
                // t = new byte[ti[0]];
//                MPI.COMM_WORLD.bcast(t, ti[0], MPI.BYTE, 0);
                Object[] resv_etalon = new Object[numParts];
                //процедура обратная для Serialize
                //MPI.COMM_WORLD.Object_Deserialize(resv_etalon, t, 0, numParts, MPI.OBJECT);               
                MPITransport.bcastObjectArray(resv_etalon, ti[0], 0);               
                Polynom[] etalon_parts = new Polynom[numParts];
                for (int i = 0; i < numParts; i++) {
                    etalon_parts[i] = (Polynom) resv_etalon[i];
                }
                
                //деление остатков-полиномов на части
                for (int i = 0; i < modules_res.length; i++) {
                    res_parts[i] = modules_res[i].sub_monom_pol(etalon_parts, ring);
                }

                //Обмен частями полиномов: 
                //k-й процессор получает все k-ые части всех полиномов из res_parts
                
                int nt = gen_mod_count / (mpiSize);//точек было на процессоре, 
                //System.out.println("nt= "+nt);
                int np = gen_mod_count % (mpiSize);//кроме первых np процессоров - у них по nt+1 точек
                Object recv_parts[] = new Object[gen_mod_count];
                Object send_parts[] = new Object[res_parts.length];
                for (int i = 0; i < numParts; i++) {                    
                    for (int j = 0; j < res_parts.length; j++) {
                        send_parts[j]= (Object) res_parts[j][i];
                    }
                    if (rank == (i + 1)) {
                        if (i < np) {
                            System.arraycopy(send_parts, 0, recv_parts, rank * (nt + 1), send_parts.length);
                            //System.out.println(rank+": copy1 ");
                        } else {

                            System.arraycopy(send_parts, 0, recv_parts, np * (nt + 1) + (rank - np) * nt, send_parts.length);
                            //System.out.println(rank+": copy1 ");
                            //System.out.println("ttttttt");
                    }
                    } else {
                        //MPI.COMM_WORLD.Isend(send_parts, 0, send_parts.length, MPI.OBJECT, i + 1, 10);
                        MPITransport.iSendObjectArray(send_parts, 0, send_parts.length, i + 1, 10);
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
                                //System.out.println(rank+": recv1 "+i);  
                            } else {
                                //System.out.println(rank+": I have been here2");
                                //MPI.COMM_WORLD.Recv(recv_parts, np * (nt + 1) + (i - np) * nt, nt, MPI.OBJECT, i, 10);
                                MPITransport.recvObjectArray(recv_parts, np*(nt+1)+(i-np)*nt, nt, i, 10);
                                //System.out.println(rank+": recv2 "+i);
                }
                        }
               
                    }
                }
                System.out.println(rank+" is before recovering");
                Polynom charpol;
                long t2 = System.currentTimeMillis();
                if (rank <= numParts) {
                Polynom[] rem = new Polynom[gen_mod_count];//всего остатков
                for (int i = 0; i < gen_mod_count; i++) {
                    rem[i] = (Polynom) recv_parts[i];
                }  
//                    System.out.println(rank + ": rem= " + rem.length);
                    // System.out.println("rem= "+ Array.toString(rem));
//                
                    int nnum = data.length;//кол-во числ модулей

                //восстановление по числовым модулям
                //остатки result
                //модули data[0]
                    //int d0l = data[0].length;
                    int s = 200;//number of monoms in part
                    np = rem[0].coeffs.length > s ?//number of parts
                            rem[0].coeffs.length / s : 1;
                    //MPI.COMM_WORLD.iSend(new int[]{np}, 0,1, MPI.INT, 0, 0);
                    MPITransport.iSendObject(np, 0, 0);
                    Polynom[][] result_parts = new Polynom[nnum][np];
                    result_parts[0] = rem[0].Sub_n_polynoms(np);
                    //partitioning
                    for (int i = 1; i < nnum; i++) {
                        result_parts[i] = rem[i].sub_monom_pol(result_parts[0], ring);
                    }
                    //System.out.println("result_parts= "+Array.toString(result_parts[nnum-1]));
                    //recovery

                    for (int i = 0; i < np; i++) {
                ring.MOD32 = Integer.MAX_VALUE;
                        for (int j = 0; j < nnum; j++) {
                            rem[j] = result_parts[j][i];
                        }
                        //System.out.println(rank+" процессор: rem= "+Array.toString(rem));
                        System.out.println(rank+" процессор: "+rem.length+"остатков , "+data.length+ " модулей");
                charpol = Newton.recoveryNewtonPolynom(data, rem, ring);
                
                //coeffs in [-mod/2;mod/2]

                int narr[] = new int[data.length];
                System.arraycopy(data, 0, narr, 0, data.length);
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
//                        int vars = charpol.powers.length / charpol.coeffs.length;
//                        int[] varsMap = new int[vars];
//                        for (int j = 0; j < vars - 1; j++) {
//                            varsMap[j + 1] = j;
//                        }
//                        varsMap[0] = vars - 1;
//                        charpol = charpol.changeOrderOfVars(varsMap, ring);
                        //send to 0 processor
                        send_parts = new Object[1];
                        send_parts[0] = (Object) charpol;
                        
                        //MPI.COMM_WORLD.Isend(send_parts, 0, 1, MPI.OBJECT, 0, i);
                        MPITransport.iSendObjectArray(send_parts, 0, 1, 0, i);
                        System.out.println(rank + ": send "+i+"-part of charpol+ = " );
                        //System.out.println(rank+ " calculated");
                }
                }
                
            }

            MPI.Finalize();
        } catch (MPIException e) {
            System.out.println("error=" + e.toString() + "  " + e.getMessage());
        }

    }

     public static Polynom[] eachProcCalcModules(int[] data,
            MatrixD a, Ring r, int proc_count, int rank) {

        // получаем общее кол-во модулей
        int generalcount = data.length;
        int np = generalcount/proc_count;
        int n1 = generalcount%proc_count;
        int counts = rank<n1? np+1:np;//кол-во модулей
        //стартовая позиция в массиве модулей
        int index = rank<n1? rank*(np+1): np*rank + n1;//(np+1)*n1+np(rank-n1)

        Polynom res[] = new Polynom[counts];
        MatrixD mm = new MatrixD();
        int size = a.M.length;
        long[][] res1 = new long[size][size];//!+
        //System.out.println("ring= "+r.toString());
        charPolynomMatrixD ch = new charPolynomMatrixD();
         for (int k = 0; k < counts; k++) {
            r.setMOD32(data[k+index]);
            mm = (MatrixD) a.toNewRing(r.algebra[0], r);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                  res1 [i][j]= ((NumberZp32) mm.M[i][j]).longValue();  
                }
                
            }
            
            //System.out.println(rank+"mm= "+mm.toString());
            //вычисление характеристических полиномов матриц
            res[k] = Polynom.polynom_one(r.numberONE);// .polynomZone;//new Polynom(new int[]{0}, new NumberZp32[]{NumberZp32.ONE});
            ch.charPolDanil(res1, res[k], r);
        }
        return res;
    
     }
     
      public static Polynom[] eachProcCalcModules(int[] data,
            int[][] ma, Ring r, int proc_count, int rank) {

        // получаем общее кол-во модулей
        int generalcount = data.length;
        int np = generalcount/proc_count;
        int n1 = generalcount%proc_count;
        int counts = rank<n1? np+1:np;//кол-во модулей
        //стартовая позиция в массиве модулей
        int index = rank<n1? rank*(np+1): np*rank + n1;//(np+1)*n1+np(rank-n1)

        Polynom res[] = new Polynom[counts];
        //MatrixD mm = new MatrixD();
        int size = ma.length;
        long[][] res1 = new long[size][size];//!+
        //System.out.println("ring= "+r.toString());
        charPolynomMatrixD ch = new charPolynomMatrixD();
         for (int k = 0; k < counts; k++) {
            r.setMOD32(data[k+index]);
            int p = data[k+index];
            //mm = (MatrixD) a.toNewRing(r.algebra[0], r);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                  res1 [i][j]= ma[i][j]%p;  
                }
                
            }
            
            //System.out.println(rank+"mm= "+mm.toString());
            //вычисление характеристических полиномов матриц
            res[k] = Polynom.polynom_one(r.numberONE());// .polynomZone;//new Polynom(new int[]{0}, new NumberZp32[]{NumberZp32.ONE});
            ch.charPolDanil(res1, res[k], r);
        }
        return res;
    
     }
//     public static int[] indexFor(int proc, int count_proc, int[] data) {
//        int g_count = data.length;
//        
//
//        int cfp = g_count / count_proc;
//        int rfp = g_count % count_proc;
//        int step = 0;
//        if (proc <= rfp) {
//            step = proc * (cfp + 1) - 1;
//        } else {
//            step = (cfp + 1) * rfp + (proc - rfp) * cfp;
//        }
//        int[] indexes = new int[data.length];
//        int k = data.length - 1;
//        indexes[i - 1] = indexes[i] / data[k].length;
//        indexes[i] = indexes[i] % data.length;          
//        
//        // for (int i=0; i<indexes.length; i++) System.out.println("indexes["+i+"]="+indexes[i]);
//
//        return indexes;
//    }
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
}