/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.student.Shcherbinin;

import java.util.ArrayList;
import java.util.Arrays;
import com.mathpar.students.OLD.llp2.student.message.ParallelDebug;
import mpi.*;

/**
 *
 * @author scherbinin
 */
public class Main {

    public static long max;
    public static long min;
    public static ParallelDebug pd;

    /**
     * @param args the command line arguments
     */

    //mpirun C java -cp /home/ridkeim/NetBeansProjects/mpi/build/classes:$CLASSPATH mpitest.Main
    //mpirun C java -cp /home/ridkeim/mathpar/target/classes:$CLASSPATH llp2.student.Shcherbinin.Main
    //mpirun C java -cp /home/ridkeim/mathpar/target/classes:$CLASSPATH llp2.student.message.AlltoallDebug
    public static void main(String[] args) throws MPIException {
        ArrayList list = new ArrayList();
        ArrayList[] tableplot = new ArrayList[]{
            new ArrayList<Double>(),
            new ArrayList<Long>(),
            new ArrayList<Long>(),
            new ArrayList<Long>(),
            new ArrayList<Long>(),
            new ArrayList<Long>()
        };
        min = Long.MAX_VALUE;
        max = 1;
        MPI.Init(args);
        int myrank =  MPI.COMM_WORLD.getRank();
        int np =  MPI.COMM_WORLD.getSize();
        int k = 1;
        try {
            k = Integer.parseInt(args[0].toString());
        } catch (Exception e) {
        }
        if(myrank==0){
            System.out.println("Author: Shcherbinin Anton,\n"
                    + "type: int,\n");
        }
        pd = new ParallelDebug( MPI.COMM_WORLD);
        for (int i = 1; i < 2; i = i * 2) {
            System.out.print((myrank == 0) ? "!" : "");
            int row = 2*i;
            int mult = (5000 + 5000 * i)*k;
            Double size = new Double(row*mult*np);


            tableplot[0].add(size/1000000);
            IsendIrecv a = new IsendIrecv(row, mult);
            //pd.paddEvent("после выполнения команды IsendIrecv", "first op");
            System.out.print((myrank == 0) ? "-" : "");
            //!!!! MPI.COMM_WORLD.barrier();
            IsendRecv b = new IsendRecv(row, mult);
            //pd.paddEvent("после выполнения команды IsendRecv", "second op");
            System.out.print((myrank == 0) ? "-" : "");
            //!!!! MPI.COMM_WORLD.barrier();
            SendIrecv c = new SendIrecv(row, mult);
            //pd.paddEvent("после выполнения команды SendIrecv", "third op");
            System.out.print((myrank == 0) ? "-" : "");
            //!!!! MPI.COMM_WORLD.barrier();
            SendRecv d = new SendRecv(row, mult);
            //pd.paddEvent("после выполнения команды SendRecv", "fourth op");
            System.out.print((myrank == 0) ? "-" : "");
            //!!!! MPI.COMM_WORLD.barrier();
            ScatterGather e = new ScatterGather(row, mult);
            //pd.paddEvent("после выполнения команды ScatterGather", "fifth op");
            System.out.print((myrank == 0) ? "-" : "");
            //!!!! MPI.COMM_WORLD.barrier();
            if(myrank==0){
          	 pd.paddEvent("почти готово", "near the end");   // Выбрасываем сообщение в протокол
            //
            }   pd.setDefaultBrowser("google-chrome");
                pd.generateDebugLog();
            if (myrank == 0) {
                long el = row * np * mult;
                long time1 = a.getTime();
                long time2 = b.getTime();
                long time3 = c.getTime();
                long time4 = d.getTime();
                long time5 = e.getTime();
                Object[] res = new Object[]{np, "[" + row + "][" + np * mult + "]", el, time1, time2, time3, time4, time5};
                list.add(res);
                tableplot[1].add(time1);
                tableplot[2].add(time2);
                tableplot[3].add(time3);
                tableplot[4].add(time4);
                tableplot[5].add(time5);

            }

        }
        if (myrank == 0) {


            System.out.println("!");
            System.out.println(getTable(list));
            String[] nam = new String[]{tableplot[0].get(0).toString(),tableplot[0].get(tableplot[0].size()-1).toString(),""+min,""+max};
            String[] n = new String[]{"IsendIrecv","IsendRecv","SendIrecv","SendRecv","ScatterGather"};
            String res="To plot a functions use this commands:\n"
                    + "Space=R[x,y,z];\n"
                + n[0]+"=\\tablePlot(["+tableplot[0].toString()+","+tableplot[1].toString()+"],"+Arrays.toString(nam)+");\n"
                + n[1]+"=\\tablePlot(["+tableplot[0].toString()+","+tableplot[2].toString()+"],"+Arrays.toString(nam)+");\n"
                + n[2]+"=\\tablePlot(["+tableplot[0].toString()+","+tableplot[3].toString()+"],"+Arrays.toString(nam)+");\n"
                + n[3]+"=\\tablePlot(["+tableplot[0].toString()+","+tableplot[4].toString()+"],"+Arrays.toString(nam)+");\n"
                + n[4]+"=\\tablePlot(["+tableplot[0].toString()+","+tableplot[5].toString()+"],"+Arrays.toString(nam)+");\n"
                    + "\\Showgraf("+Arrays.toString(n)+",[\"numb (*10^6)\",\"time (ms)\",\"\"]);";
            System.out.println(res);
        }
        //!!!! MPI.Finalize();



    }

    private static int[] getLength() {
        String[] a = new String[]{" Processors ", " Massive ", " Elements ", " IsendIrecv ", " IsendRecv ", " SendIrecv ", " SendRecv ", "ScatterGather"};
        int[] res = new int[a.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = a[i].length();
        }
        return res;
    }

    private static int[] getMaxLength(int[] a, Object[] b) {
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = (b[i].toString().length() > a[i]) ? b[i].toString().length() : a[i];
        }
        return res;
    }

    private static String[] toEqualSize(int[] a, Object[] b) {
        String[] res = new String[a.length];
        for (int i = 0; i < b.length; i++) {
            String r = b[i].toString();
            int k = a[i] - r.length();
            while (k != 0) {
                if (k > 1) {
                    r = " " + r + " ";
                    k = k - 2;
                } else {
                    r = r + " ";
                    k = 0;
                }
            }
            res[i] = r;

        }
        return res;
    }

    private static String getTableRow(String[] a) {
        char diff = 0x2551;
        String res = "" + diff;
        for (int i = 0; i < a.length; i++) {
            res = res + "" + a[i] + "" + diff;
        }
        return res;
    }

    private static String getDiffRow(int[] a) {
        char st = 0x2560;
        char end = 0x2563;
        char diff = 0x256C;
        String[] middle = getMiddle(a);
        String res = "" + st;
        for (int i = 0; i < a.length; i++) {
            if (i != (a.length - 1)) {
                res = res + "" + middle[i] + "" + diff;
            } else {
                res = res + "" + middle[i] + "" + end;
            }
        }
        return res;
    }

    private static String getStartRow(int[] a) {
        char st = 0x2554;
        char end = 0x2557;
        char diff = 0x2566;
        String[] middle = getMiddle(a);
        String res = "" + st;
        for (int i = 0; i < a.length; i++) {
            if (i != (a.length - 1)) {
                res = res + "" + middle[i] + "" + diff;
            } else {
                res = res + "" + middle[i] + "" + end;
            }
        }
        return res;
    }

    private static String getEndRow(int[] a) {
        char st = 0x255A;
        char end = 0x255D;
        char diff = 0x2569;
        String[] middle = getMiddle(a);
        String res = "" + st;
        for (int i = 0; i < a.length; i++) {
            if (i != (a.length - 1)) {
                res = res + "" + middle[i] + "" + diff;
            } else {
                res = res + "" + middle[i] + "" + end;
            }
        }
        return res;
    }

    private static String[] getMiddle(int[] a) {
        char mid = 0x2550;
        String[] res = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            int k = a[i];
            String r = "";
            while (k != 0) {
                r = r + "" + mid;
                k--;
            }
            res[i] = r;
        }

        return res;
    }

    public static String getTable(ArrayList<Object[]> a) {
        int[] len = getLength();
        for (int i = 0; i < a.size(); i++) {
            len = getMaxLength(len, (Object[]) a.get(i));
        }
        Object[] head = new Object[]{" Processors ", " Massive ", " Elements ", " IsendIrecv ", " IsendRecv ", " SendIrecv ", " SendRecv ", "ScatterGather"};
        String res = "";
        res = res + "" + getStartRow(len) + "\n"
                + getTableRow(toEqualSize(len, head)) + "\n"
                + getDiffRow(len) + "\n";
        for (int i = 0; i < a.size(); i++) {
            if (i != (a.size() - 1)) {
                res = res + "" + getTableRow(toEqualSize(len, a.get(i))) + "\n"
                        + "" + getDiffRow(len) + "\n";
            } else {
                res = res + "" + getTableRow(toEqualSize(len, a.get(i))) + "\n"
                        + "" + getEndRow(len);
            }
        }
        return res;
    }




}
