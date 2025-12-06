
package com.mathpar.matrix.file.dense;

import java.io.*;
import java.util.*;
import com.mathpar.matrix.file.dm.MatrixL;
import com.mathpar.matrix.file.ops.MatrixOpsL;
import com.mathpar.matrix.file.ops.RandomParams;
import com.mathpar.number.Ring;
//import parallel.matrix.MultFMatrix.file.*;

/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006,2007</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class FileMatrixL
    extends FileMatrix {

    /**
     * Унаследованы от FileMatrix:
     * public void copyOnly(File to) throws IOException
     * public void move(File to) throws IOException
     * public void move() throws IOException
     * public void keep()
     * public void saveTo(File dir) throws IOException
     * public boolean delete() throws IOException
     * public void negateThis(long mod) throws IOException
     * public boolean equals(FileMatrix m, long mod) throws IOException
     * public File getRoot()
     * public int getDepth()
     * public static int findDepth(File root)
     * public int[] getLeafSize() throws IOException
     * public int[] getFullSize() throws IOException
     */


    public FileMatrixL(){
        ops=new MatrixOpsL();
    }


    /**
     * Инициализирующий конструктор
     * @param root File
     * @param depth int
     */
    public FileMatrixL(File root, int depth){
        this();
        constructor(root,depth);
    }


    /**
     * Создает файловую матрицу из корня. Глубина вычисляется автоматически.
     * @param root File
     */
    public FileMatrixL(File root){
        this();
        constructor(root);
    }


    /**
     * Создает из матрицы в памяти плотную файловую матрицу глубиной depth
     * с корнем в root.
     * Если depth=0, то в файл root.
     * Если depth>0, то в дерево с корнем root, глубиной depth>0.
     * @param m MatrixL
     * @param depth int
     * @param root File
     * @throws IOException
     */
    public FileMatrixL(File root, int depth, MatrixL m)
        throws IOException{
        this();
        constructor(root,depth,m);
    }


    /**
     * Случайную матрицу m x n записать в файловую матрицу с корнем root,
     * глубиной depth.
     * Если depth=0, то в файл root.
     * Если depth>0, то в дерево с корнем root, глубиной depth>0.
     * @param root File
     * @param depth int
     * @param m int
     * @param n int
     * @param den int
     * @param rnd Random
     * @param mod long
     * @throws IOException
     */
    public FileMatrixL(File root, int depth, int m, int n,
                       int den, Random rnd, long mod)
        throws IOException{
        this(); Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        constructor(root,depth,m,n,new RandomParams(den,0,mod,-1,null,rnd,null),ring);
    }


    /**
     * Складывает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param m FileMatrixL
     * @param to File
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL add(FileMatrixL m, File to, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(add0(m,to,mod,ring));
    }


    /**
     * Вычитает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param m FileMatrixL
     * @param to File
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL subtract(FileMatrixL m, File to, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(subtract0(m,to,mod,ring));
    }



    /**
     * Умножает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param m FileMatrixL
     * @param to File
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL multCU(FileMatrixL m, File to, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(multCU0(m,to,mod,ring));
    }



    /**
     * Копирует файловую матрицу this в to. Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL copy(File to)
        throws IOException{
        return toL(copy0(to));
    }




    /**
     * Копирует с отрицанием (to=-this) файловую матрицу this в to.
     * Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param to File
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL negate(File to, long mod,Ring ring)
        throws IOException{
        return toL(negate0(to, mod,ring));
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs FileMatrix[]
     * @param hasNulls boolean true - в matrs есть nullы, false - в matrs нет nullов.
     * @param to File
     * @return FileMatrix
     * @throws IOException
     */
    public static FileMatrixL joinMove(FileMatrix[] matrs, boolean hasNulls, File to,Ring ring)
        throws IOException{
        return toL(new FileMatrixL().joinMove0(matrs, hasNulls, to,ring));
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * @param matrs FileMatrix[]
     * @param hasNulls boolean true - в matrs есть nullы, false - в matrs нет nullов.
     * @param to File
     * @return FileMatrix
     * @throws IOException
     */
    public static FileMatrixL joinCopy(FileMatrix[] matrs, boolean hasNulls, File to,Ring ring)
        throws IOException{
        return toL(new FileMatrixL().joinCopy0(matrs, hasNulls, to,ring));
    }




    /**
     * Записывает нулевую матрицу m x n в файловую матрицу с корнем to,
     * глубиной depth.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param m int
     * @param n int
     * @param depth int
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL ZERO(int m, int n, int depth, File to,Ring ring)
        throws IOException{
        return toL(new FileMatrixL().ZERO0(m,n,depth,to,ring));
    }


    /**
     * Записывает единичную матрицу n x n в файловую матрицу с корнем to,
     * глубиной depth.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param n int
     * @param depth int
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL ONE(int n, int depth, File to)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(3);
        return toL(new FileMatrixL().ONE0(n,depth,to,ring));
    }



    //=======================================================================
    //============ Методы без указания директории(файла) результата =========
    //=======================================================================

    //Здесь перечислены методы с автоматической генерацией имен
    //директории(файла) для результата.
    //Корневые директории и файлы для файловых матриц будут иметь случайные
    //имена и все расположены в базовой директории, которую
    //можно менять, если нужно.



    /**
     * Создает из матрицы в памяти плотную файловую матрицу глубиной depth
     * с корнем в root.
     * Если depth=0, то в файл root.
     * Если depth>0, то в дерево с корнем root, глубиной depth>0.
     * root -- директория с уникальным именем в базовой матричной директории.
     * @param m MatrixL
     * @param depth int
     * @throws IOException
     */
    public FileMatrixL(int depth, MatrixL m)
        throws IOException{
        this();
        constructor(depth,m);
    }


    /**
     * Случайную матрицу m x n записать в файловую матрицу с корнем root,
     * глубиной depth.
     * Если depth=0, то в файл root.
     * Если depth>0, то в дерево с корнем root, глубиной depth>0.
     * root -- директория с уникальным именем в базовой матричной директории.
     * @param depth int
     * @param m int
     * @param n int
     * @param den int
     * @param rnd Random
     * @param mod long
     * @throws IOException
     */
    public FileMatrixL(int depth, int m, int n,
                       int den, Random rnd, long mod)
        throws IOException{
        this(); Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        constructor(depth,m,n,new RandomParams(den,0,mod,-1,null,rnd,null),ring);
    }


    /**
     * Складывает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param m FileMatrixL
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL add(FileMatrixL m, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(add0(m,mod, ring));
    }


    /**
     * Вычитает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param m FileMatrixL
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL subtract(FileMatrixL m, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(subtract0(m,mod,ring));
    }



    /**
     * Умножает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param m FileMatrixL
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL multCU(FileMatrixL m, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(multCU0(m,mod,ring));
    }



    /**
     * Копирует файловую матрицу this в to. Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL copy()
        throws IOException{
        return toL(copy0());
    }




    /**
     * Копирует с отрицанием (to=-this) файловую матрицу this в to.
     * Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param mod long
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL negate(long mod,Ring ring)
        throws IOException{
        return toL(negate0(mod,ring));
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param matrs FileMatrix[]
     * @param hasNulls boolean true - в matrs есть nullы, false - в matrs нет nullов.
     * @return FileMatrix
     * @throws IOException
     */
    public static FileMatrixL joinMove(FileMatrix[] matrs, boolean hasNulls,Ring ring)
        throws IOException{
        return toL(new FileMatrixL().joinMove0(matrs, hasNulls,ring));
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param matrs FileMatrix[]
     * @param hasNulls boolean true - в matrs есть nullы, false - в matrs нет nullов.
     * @return FileMatrix
     * @throws IOException
     */
    public static FileMatrixL joinCopy(FileMatrix[] matrs, boolean hasNulls,Ring ring)
        throws IOException{
        return toL(new FileMatrixL().joinCopy0(matrs, hasNulls,ring));
    }




    /**
     * Записывает нулевую матрицу m x n в файловую матрицу с корнем to,
     * глубиной depth.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param m int
     * @param n int
     * @param depth int
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL ZERO(int m, int n, int depth,Ring ring)
        throws IOException{
        return toL(new FileMatrixL().ZERO0(m,n,depth,ring));
    }


    /**
     * Записывает единичную матрицу n x n в файловую матрицу с корнем to,
     * глубиной depth.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param n int
     * @param depth int
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL ONE(int n, int depth)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(3);
        return toL(new FileMatrixL().ONE0(n,depth,ring));
    }




    //=======================================================================
    //============== Методы сохранения и восстановления =====================
    //=======================================================================

    public void finalize(){
        finalize0();
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL restoreCopy(File dir, File to)
        throws IOException{
        return toL(new FileMatrixL().restoreCopy0(dir,to));
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL restoreCopy(File dir)
        throws IOException{
        return toL(new FileMatrixL().restoreCopy0(dir));
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL restoreMove(File dir, File to)
        throws IOException{
        return toL(new FileMatrixL().restoreMove0(dir,to));
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return FileMatrixL
     * @throws IOException
     */
    public static FileMatrixL restoreMove(File dir)
        throws IOException{
        return toL(new FileMatrixL().restoreMove0(dir));
    }



    //=======================================================================
    //========================= Другие методы ===============================
    //=======================================================================


    /**
     * Преобразует файловую матрицу в матрицу в памяти. Файловая матрица
     * должна помещаться в памяти.
     * @return MatrixL
     * @throws IOException
     */
    public MatrixL toMatrixL() throws IOException{
        return (MatrixL)toMatrix();
    }




    /**
     * Возвращает i-й подблок в виде файловой матрицы. Не копирует поддерево (подфайл).
     * Новая файловая матрица указывает на поддиректорию (подфайл).
     * @param i int [0..3]
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL getSubBlock(int i)
        throws IOException{
        return toL(getSubBlock0(i));
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * @param i int
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixL getSubBlockCopy(int i, File to)
        throws IOException{
        return toL(getSubBlockCopy0(i, to));
    }




    /**
     * Разбить файловую матрицу на 4 подблока.
     * Возвращает массив из 4 файловых матриц, которые указывают на
     * 4 поддерева (подфайла).
     * @return FileMatrixL[]
     * @throws IOException
     */
    public FileMatrixL[] split()
        throws IOException{
        return toArrL(split0());
    }


    /**
     * Разбить файловую матрицу на 4 подблока с перемещением.
     * Все 4 подблока (файловые матрицы) перемещаются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова пользоваться this НЕЛЬЗЯ.
     * @return FileMatrixL[]
     * @throws IOException
     */
    public FileMatrixL[] splitMove()
        throws IOException{
        return toArrL(splitMove0());
    }


    /**
     * Разбить файловую матрицу на 4 подблока с копированием.
     * Все 4 подблока (файловые матрицы) копируются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this НЕ МЕНЯЕТСЯ.
     * @return FileMatrixL[]
     * @throws IOException
     */
    public FileMatrixL[] splitCopy()
        throws IOException{
        return toArrL(splitCopy0());
    }


    //======================== Private methods =======================

    private static FileMatrixL toL(FileMatrix fm){
        return new FileMatrixL(fm.getRoot(),fm.getDepth());
    }

    private static FileMatrixL[] toArrL(FileMatrix[] fmArr){
        //n=4
        int n=fmArr.length;
        FileMatrixL[] fmArr2=new FileMatrixL[n];
        for (int i = 0; i < n; i++) {
            fmArr2[i]=toL(fmArr[i]);
        }
        return fmArr2;
    }


}
