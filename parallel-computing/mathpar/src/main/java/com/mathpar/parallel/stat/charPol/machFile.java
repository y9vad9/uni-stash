package com.mathpar.parallel.stat.charPol;

import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author Ribakov M
 */
public class machFile {

    /**
     * создание mach-файла для запуска задач на кластере
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        mach("/home/oxana", 6000, 8);
    }

    /**
     * пример запуска mach("/home/oxana",4096,2);
     *
     * @param path путь к файлу
     * @param mpiSize всего процессоров
     * @param procNode по сколько процессоров на узле
     * @throws Exception
     */
    public static void mach(String path, int mpiSize, int procNode) throws Exception {
        String name = path + "/mach" + mpiSize + "x" + procNode;
        File file = new File(name);
        FileOutputStream fileOut = new FileOutputStream(file);

        for (int i = 1; i <= mpiSize; i++) {
            //System.out.println("node"+i+":2"); 
            String s = "node" + i + ":" + procNode + "\n";
            fileOut.write(s.getBytes());
        }
        fileOut.close();
    }
}
