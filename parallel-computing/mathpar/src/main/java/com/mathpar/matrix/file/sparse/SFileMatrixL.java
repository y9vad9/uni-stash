
package com.mathpar.matrix.file.sparse;

import com.mathpar.matrix.file.dense.FileMatrixL;
import java.io.*;
import java.util.Random;

import com.mathpar.matrix.file.dm.MatrixL;
import com.mathpar.matrix.file.ops.MatrixOpsL;
import com.mathpar.matrix.file.ops.RandomParams;
import com.mathpar.matrix.file.utils.FileUtils;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,..,2011</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author yuri, gennadi
 * @version 0.5- 3.97
 */
public class SFileMatrixL extends SFileMatrix{
    private static Ring ring=new Ring("Zp[]");

    /**
     * Унаследованы от SFileMatrix:
     * public void copyOnly(File to) throws IOException
     * public void move(File to) throws IOException
     * public void move() throws IOException
     * public void keep()
     * public void saveTo(File dir) throws IOException
     * public boolean delete() throws IOException
     * public void negateThis(long mod) throws IOException
     * public boolean equals(SFileMatrix m, long mod) throws IOException
     * public File getRoot()
     * public int getDepth()
     * public static int findDepth(File root)
     * public int[] getLeafSize() throws IOException
     * public int[] getFullSize() throws IOException
     * public boolean hasBlock(int i)
     * public void setZERO()
     * public boolean isZERO()
     */



    /**
     * Нулевая файловая матрица. Проверка на ноль isZERO().
     */
    public static SFileMatrixL ZERO=new SFileMatrixL((File)null,0);




    public SFileMatrixL(){
        ops=new MatrixOpsL();
    }


    /**
     * Присваивающий конструктор.
     * @param root File
     * @param depth int
     */
    public SFileMatrixL(File root, int depth){
        this();
        constructor(root,depth);
    }


    /**
     * Создать разреженную файловую матрицу из корня с удалением нулей (если clear=true).
     * @param root File корень директории (плотной или разреженной)
     * @param mod long
     * @param clear boolean =true, то будет очистка нулей, =false - не будет.
     * @throws IOException
     */
    public SFileMatrixL(File root, long mod, boolean clear )
        throws IOException{
        this(); ring.setMOD32(mod);
        constructor(root,mod,ring,clear);
    }



    /**
     * Создает из плотной файловой матрицы разреженную файловую матрицу с
     * корнем to.
     * @param m FileMatrixL
     * @param to File
     * @param mod long
     * @throws IOException
     */
    public SFileMatrixL(FileMatrixL m, File to, long mod)
        throws IOException{
        this();
        constructor(m,to,mod, ring);
    }


    /**
     * Генерирует случайную файловую матрицу размером m x n, плотностью dden (в %),
     * с корнем в root.
     *
     * @param m int
     * @param n int
     * @param depth int
     * @param root File
     * @param dden double плотность в % [0..100]
     * @param rnd Random
     * @param mod long
     * @throws IOException
     */
    public SFileMatrixL(int depth, File root, int m, int n,
                        double dden, Random rnd, long mod)
        throws IOException{
        this(); ring.setMOD32(mod);
        constructor(depth,root,m,n,new RandomParams(-1,dden,mod,-1,null,rnd,null),ring );
    }



    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и случайными матрицами на концах.Результат
     * записывает в root.
     *
     * @param paths int[][]
     * @param m int
     * @param n int
     * @param den int
     * @param rnd Random
     * @param mod long
     * @param root File
     * @throws IOException
     */
    public SFileMatrixL(int[][] paths, int m, int n,
                        int den, Random rnd, long mod,File root)
        throws IOException{
        this(); ring.setMOD32(mod);
        constructor(paths,m,n,new RandomParams(den,-1,mod,-1,null,rnd,null),root,ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.
     *
     * @param paths int[][]
     * @param matrs MatrixL[]
     * @param root File
     * @throws IOException
     */
    public SFileMatrixL(int[][] paths, MatrixL[] matrs, File root, Ring ring1)
        throws IOException{
        this();
        constructor(paths,matrs,root,ring1);
    }


    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * @param depth int
     * @param root File
     * @param m MatrixL
     * @param mod long
     * @throws IOException
     */
    public SFileMatrixL(int depth, File root, MatrixL m, long mod, Ring ring1)
        throws IOException{
        this();
        constructor(depth,root,m,mod,ring1);
    }


    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * @param root File
     * @param depth int
     * @param n int
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL ONE(File root, int depth, int n, Ring ring1)
        throws IOException{
        return toL(new SFileMatrixL().ONE0(root,depth,n, ring1));
    }



    /**
     * Складывает файловые матрицы this и m по модулю mod. Результат
     * записывает в to.<br>
     * Если глубина = 0, то складывает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то складывает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this=0 и m=0, то результат = 0. Если ровно одна из файловых матриц =0,
     * то результатом будет другая матрица, скопированная в to.<br>
     * @param m SFileMatrixL
     * @param to File
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL add(SFileMatrixL m, File to, long mod)
        throws IOException{
        ring.setMOD32(mod);
        return toL(add0(m,to,mod,ring));
    }



    /**
     * Вычитает файловые матрицы this и m по модулю mod. Результат
     * записывает в to.<br>
     * Если глубина = 0, то вычитает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то вычитает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 и m==0, то результат = 0.<br>
     * Если this==0, m!=0, то результатом будет матрица -m, записанная в to.<br>
     * Если this!=0, m==0, то результатом будет матрица this, записанная в to.<br>
     * @param m SFileMatrixL
     * @param to File
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL subtract(SFileMatrixL m, File to, long mod)
        throws IOException{ ring.setMOD32(mod);
        return toL(subtract0(m,to,mod,ring));
    }



    /**
     * Умножает файловые матрицы this и m по модулю mod. Результат
     * записывает в to.<br>
     * Если глубина = 0, то умножает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то умножает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 или m==0, то результат = 0.<br>
     * @param m SFileMatrixL
     * @param to File
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL multCU(SFileMatrixL m, File to, long mod)
        throws IOException{ ring.setMOD32(mod);
        return toL(multCU0(m,to,mod,ring));
    }



    /**
     * Копирует файловую матрицу в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     *
     * @param to File новый путь к корню
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL copy(File to) throws IOException{
        return toL(copy0(to));
    }


    /**
     * Создает новую файловую матрицу равную (-this) по модулю mod в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * @param to File
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL negate(File to, long mod) throws IOException{
        return toL(negate0(to,mod,ring));
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs SFileMatrixL[]
     * @param to File
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL joinMove(SFileMatrixL[] matrs, File to)
        throws IOException{
        return toL(new SFileMatrixL().joinMove0(matrs,to));
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * @param matrs SFileMatrixL[]
     * @param to File
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL joinCopy(SFileMatrixL[] matrs, File to)
        throws IOException{
        return toL(new SFileMatrixL().joinCopy0(matrs,to));
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * @param i int
     * @param to File
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL getSubBlockCopy(int i, File to)
        throws IOException{
        return toL(getSubBlockCopy0(i,to));
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
     * Создает из плотной файловой матрицы разреженную файловую матрицу с
     * корнем to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param m FileMatrixL
     * @param mod long
     * @throws IOException
     */
    public SFileMatrixL(FileMatrixL m, long mod)
        throws IOException{
        this();  ring.setMOD32(mod);
        constructor(m,mod, ring);
    }



    /**
     * Генерирует случайную файловую матрицу размером m x n, плотностью den (в %),
     * с корнем в root.<br>
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param m int
     * @param n int
     * @param depth int
     * @param dden double плотность в % [0..100]
     * @param rnd Random
     * @param mod long
     * @throws IOException
     */
    public SFileMatrixL(int depth, int m, int n,
                        double dden, Random rnd, long mod)
        throws IOException{
        this(); ring.setMOD32(mod);
        constructor(depth,m,n,new RandomParams(-1,dden,mod,-1,null,rnd,null), ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и случайными матрицами на концах.Результат
     * записывает в root.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @param paths int[][]
     * @param m int
     * @param n int
     * @param den int
     * @param rnd Random
     * @param mod long
     * @throws IOException
     */
    public SFileMatrixL(int[][] paths, int m, int n,
                        int den, Random rnd, long mod)
        throws IOException{
        this(); ring.setMOD32(mod);
        constructor(paths,m,n,new RandomParams(den,-1,mod,-1,null,rnd,null), ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @param paths int[][]
     * @param matrs MatrixL[]
     * @throws IOException
     */
    public SFileMatrixL(int[][] paths, MatrixL[] matrs, Ring ring1)
        throws IOException{
        this();
        constructor(paths,matrs,ring1);
    }

    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param m MatrixL
     * @param mod long
     * @throws IOException
     */
    public SFileMatrixL(int depth, MatrixL m, long mod)
        throws IOException{
        this(); ring.setMOD32(mod);
        constructor(depth,m,mod,ring);
    }


    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param n int
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL ONE(int depth, int n, Ring ring1)
        throws IOException{
        return toL(new SFileMatrixL().ONE0(depth,n,ring1));
    }


    /**
     * Складывает файловые матрицы this и m по модулю mod. Результат
     * записывает в to.<br>
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * Если глубина = 0, то складывает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то складывает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this=0 и m=0, то результат = 0. Если ровно одна из файловых матриц =0,
     * то результатом будет другая матрица, скопированная в to.<br>
     * @param m SFileMatrixL
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL add(SFileMatrixL m, long mod)
        throws IOException{ ring.setMOD32(mod);
        return toL(add0(m,mod,ring));
    }

    @Override
    public <T extends SFileMatrix> T addForParalell(T fm, File to, long mod, Ring ring)  throws IOException{
        return (T) add((SFileMatrixL) fm, to, mod);
    }

    @Override
    public <T extends SFileMatrix> T addForParalell(T fm, long mod, Ring ring)  throws IOException{
       return (T) add((SFileMatrixL) fm,  mod);
    }





    /**
     * Вычитает файловые матрицы this и m по модулю mod. Результат
     * записывает в to.<br>
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * Если глубина = 0, то вычитает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то вычитает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 и m==0, то результат = 0.<br>
     * Если this==0, m!=0, то результатом будет матрица -m, записанная в to.<br>
     * Если this!=0, m==0, то результатом будет матрица this, записанная в to.<br>
     * @param m SFileMatrixL
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL subtract(SFileMatrixL m, long mod)
        throws IOException{ ring.setMOD32(mod);
        return toL(subtract0(m,mod,ring));
    }



    /**
     * Умножает файловые матрицы this и m по модулю mod. Результат
     * записывает в to.<br>
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * Если глубина = 0, то умножает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то умножает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 или m==0, то результат = 0.<br>
     * @param m SFileMatrixL
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL multCU(SFileMatrixL m, long mod)
        throws IOException{ ring.setMOD32(mod);
        return toL(multCU0(m,mod,ring));
    }

    @Override
    public <T extends SFileMatrix> T multiplyForParalell(T fm, long mod, Ring ring)  throws IOException{
        SFileMatrixL fileMatrixL = (SFileMatrixL) fm;
        return (T) multCU(fileMatrixL, mod); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends SFileMatrix> T multiplyForParalell(T fm, File to, long mod, Ring ring)  throws IOException{
        SFileMatrixL fileMatrixL = (SFileMatrixL) fm;
        return (T) multCU(fileMatrixL, to, mod); //To change body of generated methods, choose Tools | Templates.
    }


    public <T extends SFileMatrix> T multiplyByNumber0Paralell(Element mult, File to, long mod,Ring ring) throws IOException {
        if (isZERO()) {
            return (T)ZERO;
        }
        //this!=0
        FileUtils.createDir(to, depth);
        multNumRec(root,mult, to, depth, mod,ring);
        return (T) new SFileMatrixL(to, depth);
    }




    /**
     * Копирует файловую матрицу в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL copy() throws IOException{
        return toL(copy0());
    }



    /**
     * Создает новую файловую матрицу равную (-this) по модулю mod в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL negate(long mod) throws IOException{
        return toL(negate0(mod,ring));
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs SFileMatrixL[]
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL joinMove(SFileMatrixL[] matrs)
        throws IOException{
        return toL(new SFileMatrixL().joinMove0(matrs));
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param matrs SFileMatrixL[]
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL joinCopy(SFileMatrixL[] matrs)
        throws IOException{
        return toL(new SFileMatrixL().joinCopy0(matrs));
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param i int
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL getSubBlockCopy(int i)
        throws IOException{
        return toL(getSubBlockCopy0(i));
    }


    //=======================================================================
    //============== Методы сохранения и восстановления =====================
    //=======================================================================





    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL restoreCopy(File dir, File to)
        throws IOException{
        return toL(new SFileMatrixL().restoreCopy0(dir,to));
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL restoreCopy(File dir)
        throws IOException{
        return toL(new SFileMatrixL().restoreCopy0(dir));
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL restoreMove(File dir, File to)
        throws IOException{
        return toL(new SFileMatrixL().restoreMove0(dir,to));
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrixL
     * @throws IOException
     */
    public static SFileMatrixL restoreMove(File dir)
        throws IOException{
        return toL(new SFileMatrixL().restoreMove0(dir));
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
     * @return SFileMatrixL
     * @throws IOException
     */
    public SFileMatrixL getSubBlock(int i)
        throws IOException{
        return toL(getSubBlock0(i));
    }



    /**
     * Разбить файловую матрицу на 4 подблока.
     * Возвращает массив из 4 файловых матриц, которые указывают на
     * 4 поддерева (подфайла).
     * @return SFileMatrixL[] arr[i]=null, если i-й подблок нулевой
     * @throws IOException
     */
    public SFileMatrixL[] split()
        throws IOException{
        return toArrL(split0());
    }



    /**
     * Разбить файловую матрицу на 4 подблока с перемещением.
     * Все 4 подблока (файловые матрицы) перемещаются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this=0.
     * @return SFileMatrixL[] arr[i]=null, если i-й подблок нулевой
     * @throws IOException
     */
    public SFileMatrixL[] splitMove()
        throws IOException{
        return toArrL(splitMove0());
    }



    /**
     * Разбить файловую матрицу на 4 подблока с копированием.
     * Все 4 подблока (файловые матрицы) копируются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this НЕ МЕНЯЕТСЯ.
     * @return SFileMatrixL[] arr[i]=null, если i-й подблок нулевой
     * @throws IOException
     */
    public SFileMatrixL[] splitCopy()
        throws IOException{
        return toArrL(splitCopy0());
    }



    //======================== Private methods =======================

    private static SFileMatrixL toL(SFileMatrix fm){
       // System.out.println("fm.getRoot()" + fm.getDepth());
        return new SFileMatrixL(fm.getRoot(),fm.getDepth());
    }


    //могут быть null, т.е. fmArr[i]=null
    private static SFileMatrixL[] toArrL(SFileMatrix[] fmArr){
        //n=4
        int n=fmArr.length;
        SFileMatrixL[] fmArr2=new SFileMatrixL[n];
        for (int i = 0; i < n; i++) {
            if (fmArr[i]!=null) {
                fmArr2[i]=toL(fmArr[i]);
            }
        }
        return fmArr2;
    }


}
