
package com.mathpar.matrix.file.sparse;

import com.mathpar.matrix.file.ops.MatrixOpsL;
import com.mathpar.matrix.file.ops.RandomParams;
import java.io.IOException;
import java.io.File;
import com.mathpar.matrix.file.dm.MatrixZ;
import java.util.Random;
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
 * @author Yuri, gennadi
 * @version 0.5- 3.97
 */
public class SFileMatrixZ extends SFileMatrix {
    private static Ring ring=Ring.ringZxyz;
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
    public static SFileMatrixZ ZERO=new SFileMatrixZ((File)null,0);




    public SFileMatrixZ(){
        ops=new MatrixOpsL();
    }


    /**
     * Присваивающий конструктор.
     * @param root File
     * @param depth int
     */
    public SFileMatrixZ(File root, int depth){
        this();
        constructor(root,depth);
    }


    /**
     * Создать разреженную файловую матрицу из корня с удалением нулей (если clear=true).
     * @param root File корень директории (плотной или разреженной)
     * @param clear boolean =true, то будет очистка нулей, =false - не будет.
     * @throws IOException
     */
    public SFileMatrixZ(File root, boolean clear)
        throws IOException{
        this();
        constructor(root,-1,ring,clear);
    }



    /**
     * Создает из плотной файловой матрицы разреженную файловую матрицу с
     * корнем to.
     * @param m FileMatrixZ
     * @param to File
     * @throws IOException
     */
/*    public SFileMatrixZ(FileMatrixZ m, File to)
        throws IOException{
        this();
        constructor(m,to,-1);
    }
*/

    /**
     * Генерирует случайную файловую матрицу размером m x n, плотностью dden (в %),
     * с корнем в root.
     *
     * @param depth int
     * @param root File
     * @param m int
     * @param n int
     * @param dden double плотность в % [0..100]
     * @param nbits int
     * @param rnd Random
     * @throws IOException
     */
    public SFileMatrixZ(int depth, File root, int m, int n,
                        double dden, int nbits, Random rnd)
        throws IOException{
        this();
        constructor(depth,root,m,n,new RandomParams(-1,dden,-1,nbits,null,rnd,null), ring);
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
     * @param nbits int
     * @param root File
     * @throws IOException
     */
    public SFileMatrixZ(int[][] paths, int m, int n,
                        int den, Random rnd, int nbits, File root)
        throws IOException{
        this();
        constructor(paths,m,n,new RandomParams(den,-1,-1,nbits,null,rnd,null),root , ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.
     *
     * @param paths int[][]
     * @param matrs MatrixZ[]
     * @param root File
     * @throws IOException
     */
    public SFileMatrixZ(int[][] paths, MatrixZ[] matrs, File root)
        throws IOException{
        this();
        constructor(paths,matrs,root, ring);
    }


    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * @param depth int
     * @param root File
     * @param m MatrixZ
     * @throws IOException
     */
    public SFileMatrixZ(int depth, File root, MatrixZ m)
        throws IOException{
        this();
        constructor(depth,root,m,-1, ring);
    }


    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * @param root File
     * @param depth int
     * @param n int
     * @return SFileMatrixZ
     * @throws IOException
     */
    public static SFileMatrixZ ONE(File root, int depth, int n)
        throws IOException{
        return toZ(new SFileMatrixZ().ONE0(root,depth,n, ring));
    }



    /**
     * Складывает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * Если глубина = 0, то складывает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то складывает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this=0 и m=0, то результат = 0. Если ровно одна из файловых матриц =0,
     * то результатом будет другая матрица, скопированная в to.<br>
     * @param m SFileMatrixZ
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ add(SFileMatrixZ m, File to)
        throws IOException{
        return toZ(add0(m,to,-1, ring));
    }



    /**
     * Вычитает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * Если глубина = 0, то вычитает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то вычитает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 и m==0, то результат = 0.<br>
     * Если this==0, m!=0, то результатом будет матрица -m, записанная в to.<br>
     * Если this!=0, m==0, то результатом будет матрица this, записанная в to.<br>
     * @param m SFileMatrixZ
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ subtract(SFileMatrixZ m, File to)
        throws IOException{
        return toZ(subtract0(m,to,-1, ring));
    }



    /**
     * Умножает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * Если глубина = 0, то умножает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то умножает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 или m==0, то результат = 0.<br>
     * @param m SFileMatrixZ
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ multCU(SFileMatrixZ m, File to)
        throws IOException{
        return toZ(multCU0(m,to,-1, ring));
    }



    /**
     * Копирует файловую матрицу в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     *
     * @param to File новый путь к корню
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ copy(File to) throws IOException{
        return toZ(copy0(to));
    }



    /**
     * Делает this=-this. Меняет файл или дерево this.
     * @throws IOException
     */
    public void negateThis() throws IOException{
        negateThis(-1,ring);
    }


    /**
     * Создает новую файловую матрицу равную (-this) в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ negate(File to) throws IOException{
        return toZ(negate0(to,-1,ring));
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs SFileMatrixZ[]
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ joinMove(SFileMatrixZ[] matrs, File to)
        throws IOException{
        return toZ(joinMove0(matrs,to));
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * @param matrs SFileMatrixZ[]
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ joinCopy(SFileMatrixZ[] matrs, File to)
        throws IOException{
        return toZ(joinCopy0(matrs,to));
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * @param i int
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ getSubBlockCopy(int i, File to)
        throws IOException{
        return toZ(getSubBlockCopy0(i,to));
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
     * @param m FileMatrixZ
     * @throws IOException
     */
/*    public SFileMatrixZ(FileMatrixZ m)
        throws IOException{
        this();
        constructor(m,-1);
    }
*/


    /**
     * Генерирует случайную файловую матрицу размером m x n, плотностью den (в %),
     * с корнем в root.<br>
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @param depth int
     * @param m int
     * @param n int
     * @param dden double плотность в % [0..100]
     * @param nbits int
     * @param rnd Random
     * @throws IOException
     */
    public SFileMatrixZ(int depth, int m, int n,
                        double dden, int nbits, Random rnd)
        throws IOException{
        this();
        constructor(depth,m,n,new RandomParams(-1,dden,-1,nbits,null,rnd,null), ring);
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
     * @param nbits int
     * @throws IOException
     */
    public SFileMatrixZ(int[][] paths, int m, int n,
                        int den, Random rnd, int nbits)
        throws IOException{
        this();
        constructor(paths,m,n,new RandomParams(den,-1,-1,nbits,null,rnd,null), ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @param paths int[][]
     * @param matrs MatrixZ[]
     * @throws IOException
     */
    public SFileMatrixZ(int[][] paths, MatrixZ[] matrs)
        throws IOException{
        this();
        constructor(paths,matrs, ring);
    }

    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param m MatrixZ
     * @throws IOException
     */
    public SFileMatrixZ(int depth, MatrixZ m)
        throws IOException{
        this();
        constructor(depth,m,-1, ring);
    }


    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param n int
     * @return SFileMatrixZ
     * @throws IOException
     */
    public static SFileMatrixZ ONE(int depth, int n)
        throws IOException{
        return toZ(new SFileMatrixZ().ONE0(depth,n, ring));
    }


    /**
     * Складывает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * Если глубина = 0, то складывает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то складывает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this=0 и m=0, то результат = 0. Если ровно одна из файловых матриц =0,
     * то результатом будет другая матрица, скопированная в to.<br>
     * @param m SFileMatrixZ
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ add(SFileMatrixZ m)
        throws IOException{
        return toZ(add0(m,-1, ring));
    }



    /**
     * Вычитает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * Если глубина = 0, то вычитает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то вычитает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 и m==0, то результат = 0.<br>
     * Если this==0, m!=0, то результатом будет матрица -m, записанная в to.<br>
     * Если this!=0, m==0, то результатом будет матрица this, записанная в to.<br>
     * @param m SFileMatrixZ
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ subtract(SFileMatrixZ m)
        throws IOException{
        return toZ(subtract0(m,-1, ring));
    }



    /**
     * Умножает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * Если глубина = 0, то умножает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то умножает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 или m==0, то результат = 0.<br>
     * @param m SFileMatrixZ
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ multCU(SFileMatrixZ m)
        throws IOException{
        return toZ(multCU0(m,-1, ring));
    }

    @Override
    public <T extends SFileMatrix> T multiplyForParalell(T fm, long mod, Ring ring) throws IOException {
        return (T) multCU((SFileMatrixZ)fm); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends SFileMatrix> T multiplyForParalell(T fm, File to, long mod,  Ring ring) throws IOException {
        return (T) multCU((SFileMatrixZ)fm, to); //To change body of generated methods, choose Tools | Templates.
    }




    /**
     * Копирует файловую матрицу в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ copy() throws IOException{
        return toZ(copy0());
    }



    /**
     * Создает новую файловую матрицу равную (-this) в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ negate() throws IOException{
        return toZ(negate0(-1,ring));
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs SFileMatrixZ[]
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ joinMove(SFileMatrixZ[] matrs)
        throws IOException{
        return toZ(joinMove0(matrs));
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param matrs SFileMatrixZ[]
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ joinCopy(SFileMatrixZ[] matrs)
        throws IOException{
        return toZ(joinCopy0(matrs));
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param i int
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ getSubBlockCopy(int i)
        throws IOException{
        return toZ(getSubBlockCopy0(i));
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
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ restoreCopy(File dir, File to)
        throws IOException{
        return toZ(restoreCopy0(dir,to));
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ restoreCopy(File dir)
        throws IOException{
        return toZ(restoreCopy0(dir));
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ restoreMove(File dir, File to)
        throws IOException{
        return toZ(restoreMove0(dir,to));
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ restoreMove(File dir)
        throws IOException{
        return toZ(restoreMove0(dir));
    }


    //=======================================================================
    //========================= Другие методы ===============================
    //=======================================================================

    /**
     * Преобразует файловую матрицу в матрицу в памяти. Файловая матрица
     * должна помещаться в памяти.
     * @return MatrixZ
     * @throws IOException
     */
    public MatrixZ toMatrixZ() throws IOException{
        return (MatrixZ)toMatrix();
    }



    /**
     * Сравнивает 2 файловые матрицы. Если равны, то возвращает true, если
     * не равны -- false.<br>
     *
     * @param m SFileMatrixZ
     * @return boolean
     * @throws IOException
     */
    public boolean equals(SFileMatrixZ m) throws IOException{
        return equals(m,-1, ring);
    }


    /**
     * Возвращает i-й подблок в виде файловой матрицы. Не копирует поддерево (подфайл).
     * Новая файловая матрица указывает на поддиректорию (подфайл).
     * @param i int [0..3]
     * @return SFileMatrixZ
     * @throws IOException
     */
    public SFileMatrixZ getSubBlock(int i)
        throws IOException{
        return toZ(getSubBlock0(i));
    }



    /**
     * Разбить файловую матрицу на 4 подблока.
     * Возвращает массив из 4 файловых матриц, которые указывают на
     * 4 поддерева (подфайла).
     * @return SFileMatrixZ[]
     * @throws IOException
     */
    public SFileMatrixZ[] split()
        throws IOException{
        return toArrZ(split0());
    }



    /**
     * Разбить файловую матрицу на 4 подблока с перемещением.
     * Все 4 подблока (файловые матрицы) перемещаются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this=0.
     * @return SFileMatrixZ[]
     * @throws IOException
     */
    public SFileMatrixZ[] splitMove()
        throws IOException{
        return toArrZ(splitMove0());
    }



    /**
     * Разбить файловую матрицу на 4 подблока с копированием.
     * Все 4 подблока (файловые матрицы) копируются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this НЕ МЕНЯЕТСЯ.
     * @return SFileMatrixZ[]
     * @throws IOException
     */
    public SFileMatrixZ[] splitCopy()
        throws IOException{
        return toArrZ(splitCopy0());
    }



    //======================== Private methods =======================

    private static SFileMatrixZ toZ(SFileMatrix fm){
        return new SFileMatrixZ(fm.getRoot(),fm.getDepth());
    }


    //могут быть null, т.е. fmArr[i]=null
    private static SFileMatrixZ[] toArrZ(SFileMatrix[] fmArr){
        //n=4
        int n=fmArr.length;
        SFileMatrixZ[] fmArr2=new SFileMatrixZ[n];
        for (int i = 0; i < n; i++) {
            if (fmArr[i]!=null) {
                fmArr2[i]=toZ(fmArr[i]);
            }
        }
        return fmArr2;
    }

}
