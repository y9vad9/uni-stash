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
 *
 * @author oxana
 */
public class parcharpolNumber {
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
//                    if (args[i].contains("cbits")) {
//                        cbits = Integer.parseInt(args[i].replace("cbits=", ""));
//                    }
                    if (args[i].contains("size")) {
                        size = Integer.parseInt(args[i].replace("size=", ""));
                    }
                    if (args[i].contains("rnd")) {
                        rnd = Integer.parseInt(args[i].replace("rnd=", ""));
                    }
                }

                Ring r = new Ring(r_str);
                Ring ring = new Ring(rp_str);
                int [] type =new int[]{cbits};
//                int[] polArr = new int[vars + 2];
//                for (int i = 0; i < vars; i++) {
//                    polArr[i] = deg1[i];
//                }
//                polArr[polArr.length - 2] = pdens;
//                polArr[polArr.length - 1] = cbits;

                //входная матрица
                
                long mem1 = Runtime.getRuntime().freeMemory();
                long mem = Runtime.getRuntime().totalMemory();
                long allmem = Runtime.getRuntime().maxMemory();
                System.out.println("maxmemory = "+ allmem+", total memory = "+mem+" bytes, free memory = "+mem1+" bytes");
//                MatrixS a1 = new MatrixS(size, size, mdens, type, new Random(rnd), r.numberONE(), r);
//                 long mem2 = Runtime.getRuntime().freeMemory();
//                 System.out.println(rank+": making MatrixS needs "+(mem1-mem2)+" bytes");
                MatrixD a = new MatrixD(size, size, mdens, type, new Random(rnd), r);
                
                 long mem3 = Runtime.getRuntime().freeMemory();
                System.out.println("input: m00=" + a.M[0][0].toString(r));
                System.out.println(rank+": making MatrixD  needs "+(mem1-mem3)+" bytes");
                

                //рассылка колец
//                Object[] rings = new Object[]{r, ring};
//                MPI.COMM_WORLD.Bcast(rings, 0, 2, MPI.OBJECT, 0);

                //рассылка входной матрицы
//                Object[] t = new Object[]{a};
//                MPI.COMM_WORLD.Bcast(t, 0, 1, MPI.OBJECT, 0);
                //byte[] t=MPI.COMM_WORLD.Object_Serialize(new Object[]{a,r,ring},0,3,MPI.OBJECT); 
                r.cleanRing();
                ring.cleanRing(); 
                Object[] t = new Object[]{a,r,ring};
                int [] ti = new int[]{t.length};
                long mem4 = Runtime.getRuntime().freeMemory();
                 System.out.println(rank+":  MatrixDtoByte needs "+(mem4-mem3)+" bytes");
                //System.out.println("t.length= "+t.length);
                MPI.COMM_WORLD.bcast(ti,  1, MPI.INT, 0);
                //MPI.COMM_WORLD.bcast(t, t.length, MPI.BYTE, 0);
                MPITransport.bcastObjectArray(t,t.length, 0);

                // вычисляем необходимое кол-во модулей по каждой
                //переменной. в последний элемент массива записываем кол-во числ. модулей
                charPolynomMatrixD ch = new charPolynomMatrixD(a);
                int[] mod_count = ch.complexityNumberZMatrix(ring);
                for (int i = 0; i < mod_count.length; i++) {
                    System.out.print("  " + mod_count[i]);
                }

                System.out.println();
                int gen_mod_count = 1;
                //вычисляем кол-во точек
                gen_mod_count = mod_count[0];
                
                //System.out.println("всего точек="+gen_mod_count);

                //заполняем массив со значениями модулей
                //все модули
                
                // int[] data =  new int[gen_mod_count];
                //количество числовых модулей
               // Newton.initCache();
               // Newton.growMulPrims(gen_mod_count);
               // System.arraycopy(block, 0, data, 0, gen_mod_count);
//                NumberZ bound = (NumberZ)new NumberZ(2).pow(mod_count[0], ring);
//                int[] data = Newton.primesForTask(bound, 1, ring);
                long numberOfBits = ch.complexityCoeffOfCharPolNumberZMatrix(r);
                int[] data = Newton.primesForTask(numberOfBits, 1, ring);
                //System.out.println("data =  " + Array.toString(data));

                //метод вычисления рез.  по каждому модулю для данного процессора,  a - -исходная матрица,               
                //modules_res[] - результаты,

                Polynom[] modules_res = eachProcCalcModules(data, a, ring, mpiSize, rank);
                //System.out.println(rank+": "+Array.toString(modules_res));
//              
    //Разбиение полинома
                //количество частей
                int numParts = size+1<mpiSize? size+1: mpiSize;
                Polynom[][] res_parts = new Polynom[modules_res.length][numParts];
                Polynom etalon_parts[] = modules_res[0].Sub_n_polynoms(numParts);
                //System.out.println("etalon_parts.l"+etalon_parts.length);

                //пересылка всем процессорам образца деления полинома на части

                Object[] send_etalon = new Object[numParts];
                for (int i = 0; i < numParts; i++) {
                    send_etalon[i] = (Object) etalon_parts[i];
                }
               //t=MPI.COMM_WORLD.Object_Serialize(send_etalon,0,numParts,MPI.OBJECT);
                ti = new int[]{send_etalon.length};
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
                //MPI.COMM_WORLD.bcast(t, t.length, MPI.BYTE, 0);
                MPITransport.bcastObjectArray(send_etalon,send_etalon.length, 0);
                
                //разбиение остальных полиномов
                res_parts[0] = etalon_parts;
                for (int i = 1; i < modules_res.length; i++) {
                    res_parts[i] = modules_res[i].sub_monom_pol(etalon_parts, ring);

                }


                //Обмен частями полиномов: 
                //k-й процессор получает все k-ые части всех полиномов из res_parts                 
                //
                int nt = gen_mod_count/mpiSize;//точек на процессоре,  
                int np = gen_mod_count%mpiSize;//кроме первых np процессоров - у них по nt+1 точек
                Object recv_parts[] = new Object[gen_mod_count];
                Object send_parts[] = new Object[res_parts.length];
                for (int i = 0; i < numParts; i++) {                    
                    for (int j = 0; j < res_parts.length; j++) {
                        send_parts[j]= (Object) res_parts[j][i];
                    }
                    if(i==rank)
                        if(i<np) System.arraycopy(send_parts, 0, recv_parts, i*(nt+1), send_parts.length);
                        else System.arraycopy(send_parts, 0, recv_parts, np*(nt+1)+(i-np)*nt, send_parts.length);
                    else //MPI.COMM_WORLD.Isend(send_parts, 0, send_parts.length, MPI.OBJECT, i, 10);
                    MPITransport.iSendObjectArray(send_parts, 0, send_parts.length, i, 10);
                }
                if (rank<numParts)
                for (int i = 0; i < mpiSize; i++) { 
                    if(i!=rank)
                        if(i<np) //MPI.COMM_WORLD.Recv(recv_parts, i*(nt+1), nt+1, MPI.OBJECT, i, 10);
                            MPITransport.recvObjectArray(recv_parts, i*(nt+1), nt+1, i, 10);
                        else //MPI.COMM_WORLD.Recv(recv_parts, np*(nt+1)+(i-np)*nt, nt, MPI.OBJECT, i, 10);
                            MPITransport.recvObjectArray(recv_parts, np*(nt+1)+(i-np)*nt, nt, i, 10);
                }
                
                
                Polynom[] rem = new Polynom[gen_mod_count];//всего остатков
                for (int i = 0; i < gen_mod_count; i++) {
                    rem[i] = (Polynom) recv_parts[i];
                }
                long t6 = System.currentTimeMillis();
                //System.out.println(rank+": rem= "+Array.toString(rem));
                
                int nnum = data.length;//кол-во числ модулей
                
                //восстановление по числовым модулям
                //остатки result
                //модули data[0]
                
                ring.MOD32 = Integer.MAX_VALUE;
                Polynom charpol = Newton.recoveryNewtonPolynom(data, rem, ring);
                long t7 = System.currentTimeMillis();
                //System.out.println(rank+ " after recoveryNewtonPolynom");
                //System.out.println(rank + ": charpol = " + charpol.toString());
                /*
                 * charpol =
                 * 3954527713692149785z^2y^2x^2+3914710001582598882z^2yx^2+39817712109732115z^2x^2+3954527713692188670zy^2x^2+3954527713692221051zyx^3+3954527713692209169zyx^2+3954527713692220714zx^3+3914710001582578082zx^2+44221y^2x^2+3954527713692220869yx^3+3914710001582584942yx^2+x^4+3954527713692221124x^3+3954527713692144453x^2
                 *
                 */
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
                //переименование переменных
                int var = charpol.powers.length / charpol.coeffs.length;
                int[] varsMap = new int[var];

                for (int j = 0; j < var - 1; j++) {
                    varsMap[j + 1] = j;
                }
                varsMap[0] = var - 1;
                charpol = charpol.changeOrderOfVars(varsMap, ring);
                //System.out.println(rank + ": charpol+ = " + charpol.toString());
                /*
                 * charpol =
                 * z^4+3954527713692221051z^3yx+3954527713692220714z^3y+3954527713692220869z^3x+3954527713692221124z^3+3954527713692149785z^2y^2x^2+3914710001582598882z^2y^2x+39817712109732115z^2y^2+3954527713692188670z^2yx^2+3954527713692209169z^2yx+3914710001582578082z^2y+44221z^2x^2+3914710001582584942z^2x+3954527713692144453z^2
                 */
               
                long t8 = System.currentTimeMillis();
                Object send[] = new Object[]{charpol};
                Object recv[] = new Object[mpiSize];
                
                //MPI.COMM_WORLD.Gather(send, 0, 1, MPI.OBJECT, recv, 0, 1, MPI.OBJECT, 0);
                recv[0] = send[0];
                for(int i = 1; i < mpiSize; i++){
                MPITransport.recvObjectArray(recv, i, 1, i, 15);
                }
                Polynom charpolynomial = (Polynom) recv[0];
                for (int j = 1; j < recv.length; j++) {
                    charpolynomial = charpolynomial.add(((Polynom) recv[j]), r);
                }


                //System.out.println("Characteristic polynomial= " + charpolynomial.toString());
                /*
                 * Characteristic polynomial=
                 * z^4+3954527713692220818z^3yx+3954527713692221014z^3y+3954527713692220885z^3x+3954527713692221021z^3+38161z^2y^2x^2+39817712109649080z^2y^2x+39817712109620771z^2y^2+103338z^2yx^2+8908z^2yx+3914710001582535522z^2y+52494z^2x^2+3954527713692198552z^2x+3954527713692139895z^2+39817712119604252zy^3x^3+39817712116382618zy^3x^2+39817712123408087zy^3x+39817712115588043zy^3+3174030zy^2x^3+26536828zy^2x^2+39885192zy^2x+22946169zy^2+4656159zyx^3+39817712137585799zyx^2+37484025zyx+39817712126510420zy+2106204zx^3+15296478zx^2+13131476zx+3914710001581153314z+3954527713547347803y^4x^4+39817713020199409y^4x^3+39817712841342405y^4x^2+106750012y^4x+44878656y^4+647093136y^3x^4+39817714348230928y^3x^3+39817713942872901y^3x^2+79635426255143642y^3x+707278708y^3+3954527713093558116y^2x^4+3914710000559683953y^2x^3+39817713461690583y^2x^2+39817715909222343y^2x+39817714640360094y^2+3914710000499251988yx^4+3874892286456709932yx^3+3914709999234181916yx^2+39817713701224318yx+39817714289063875y+3954527713418869458x^4+3914710000515357189x^3+3914710000369591990x^2+3914710001218244761x+6044829
                 */
                long t2 = System.currentTimeMillis();
//                System.out.println(rank+": bcast= "+(t5-t1)+", Calc= "+(t3-t5)+
//                        ", alToAll= "+(t6-t3)+", Recovery= "+(t7-t6)+", Gather= "+(t2-t8)+", allTime= "+(t2-t1));
                System.out.println("times = " + (t2 - t1));
                

            } else {
                long t8 = System.currentTimeMillis();
                int [] ti = new int[1];
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
                //byte[] t = new byte[ti[0]];
                //System.out.println("ti[0]="+ti[0]);
                //MPI.COMM_WORLD.bcast(t, ti[0], MPI.BYTE, 0);
                Object[] tt=new Object[3]; // заводится массив объектов длинной 1
                //процедура обратная для Serialize
                //MPI.COMM_WORLD.Object_Deserialize(tt, t, 0, 3, MPI.OBJECT);
                MPITransport.bcastObjectArray(tt, 3, 0);
                MatrixD a = (MatrixD) tt[0];
                Ring r = (Ring) tt[1];
                Ring ring = (Ring) tt[2];
                long t4 = System.currentTimeMillis();
                charPolynomMatrixD ch = new charPolynomMatrixD(a);
                int[] mod_count = ch.complexityNumberZMatrix(ring);
                int gen_mod_count = mod_count[0];
                
                //заполняем массив со значениями модулей
                //int[] data =  new int[gen_mod_count];
                //количество числовых модулей
//                Newton.initCache();
//                Newton.growMulPrims(gen_mod_count);
//                System.arraycopy(Newton.prims, 0, data, 0, gen_mod_count);
                long numberOfBits = ch.complexityCoeffOfCharPolNumberZMatrix(r);
                int[] data = Newton.primesForTask(numberOfBits, 1, ring);
//                NumberZ bound = (NumberZ)new NumberZ(2).pow(mod_count[0], ring);
//                int[] data = Newton.primesForTask(bound, 1, ring);
                //long numberOfBits = ch.complexityCoeffOfCharPolNumberZMatrix(ma, r);
                //int[] data = Newton.primesForTask(numberOfBits, 1, ring);
                //метод вычисления рез.  по каждому модулю  a - -исходная матрица, modules_res[] - результаты, 
                 long t1 = System.currentTimeMillis();
                Polynom[] modules_res = eachProcCalcModules(data, a, ring, mpiSize, rank);
                //System.out.println(rank+" ended Modcalc");
                //System.out.println(rank+": "+Array.toString(modules_res));
                
                long t3 = System.currentTimeMillis();
                //System.out.println(rank+": ProcCalc "+(t3-t1)+"   ");

                //количество частей
                int size =a.M.length;
                int numParts = size+1<mpiSize? size+1: mpiSize;
                Polynom[][] res_parts = new Polynom[modules_res.length][numParts];
                //получение образца деления 
                      
                ti = new int[1];
                MPI.COMM_WORLD.bcast(ti, 1, MPI.INT, 0);
//                t = new byte[ti[0]];
//                MPI.COMM_WORLD.bcast(t,  ti[0], MPI.BYTE, 0);
                Object[] resv_etalon = new Object[numParts];
                //процедура обратная для Serialize
                //MPI.COMM_WORLD.Object_Deserialize(resv_etalon, t, 0, numParts, MPI.OBJECT);               
                MPITransport.bcastObjectArray(resv_etalon, 3, 0);
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
                
                int nt = gen_mod_count/mpiSize;//точек на процессоре,  
                int np = gen_mod_count%mpiSize;//кроме первых np процессоров - у них по nt+1 точек
                Object recv_parts[] = new Object[gen_mod_count];
                Object send_parts[] = new Object[res_parts.length];
                for (int i = 0; i < numParts; i++) {                    
                    for (int j = 0; j < res_parts.length; j++) {
                        send_parts[j]= (Object) res_parts[j][i];
                    }
                    if(i==rank)
                        if(i<np) System.arraycopy(send_parts, 0, recv_parts, i*(nt+1), send_parts.length);
                        else System.arraycopy(send_parts, 0, recv_parts, np*(nt+1)+(i-np)*nt, send_parts.length);
                    else {
                        //MPI.COMM_WORLD.Isend(send_parts, 0, send_parts.length, MPI.OBJECT, i, 10);
                        MPITransport.iSendObjectArray(send_parts, 0, send_parts.length, i, 10);
                        //System.out.println(rank+": I have been here");    
                    }
                    
                }
                if (rank<numParts)
                for (int i = 0; i < mpiSize; i++) { 
                    if(i!=rank)
                        if(i<np) //MPI.COMM_WORLD.Recv(recv_parts, i*(nt+1), nt+1, MPI.OBJECT, i, 10);
                        MPITransport.recvObjectArray(recv_parts, i*(nt+1), nt+1, i, 10);
                        else //MPI.COMM_WORLD.Recv(recv_parts, np*(nt+1)+(i-np)*nt, nt, MPI.OBJECT, i, 10);
                             MPITransport.recvObjectArray(recv_parts, np*(nt+1)+(i-np)*nt, nt, i, 10);
                }
               
                long t2 = System.currentTimeMillis();
                 Polynom charpol;
                if (rank<numParts){
                Polynom[] rem = new Polynom[gen_mod_count];//всего остатков
                for (int i = 0; i < gen_mod_count; i++) {
                    rem[i] = (Polynom) recv_parts[i];
                }  
                   // System.out.println(rank+ ": rem= "+Array.toString(rem));
                //восстановление по числовым модулям
                //остатки result
                //модули data[0]
                ring.MOD32 = Integer.MAX_VALUE;
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
                if (charpol.coeffs.length>0 && charpol.powers.length>0){
                int vars = charpol.powers.length / charpol.coeffs.length;
                int[] varsMap = new int[vars];
                for (int j = 0; j < vars - 1; j++) {
                    varsMap[j + 1] = j;
                }
                varsMap[0] = vars - 1;
                charpol = charpol.changeOrderOfVars(varsMap, ring);
                }
                //System.out.println(rank + ": charpol+ = " + charpol.toString());
                //System.out.println(rank+ " calculated");
                }
                
                else charpol=Polynom.polynom_zero(r.numberONE);
                long t6 = System.currentTimeMillis();
                //сборка результата на 0-м процессоре
                Object send[] = new Object[]{charpol};
                Object recv[] = new Object[1];
                //MPI.COMM_WORLD.Gather(send, 0, 1, MPI.OBJECT, recv, 0, 1, MPI.OBJECT, 0);
                MPITransport.sendObjectArray(send, 0, 1, 0, 15);
                long t7 = System.currentTimeMillis();
                // System.out.println(rank+ " Gather");
                System.out.println(rank+": bcast= "+(t4-t8)+", Calc= "+(t3-t1)+
                        ", alToAll= "+(t2-t3)+", Recovery= "+(t6-t2)+", Gather= "+(t7-t6)+", allTime= "+(t7-t8));
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