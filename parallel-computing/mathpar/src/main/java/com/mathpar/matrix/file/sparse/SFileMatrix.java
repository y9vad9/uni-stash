
package com.mathpar.matrix.file.sparse;

import com.mathpar.matrix.file.dense.FileMatrix;
import com.mathpar.matrix.file.ops.MatrixOps;
import com.mathpar.matrix.file.ops.RandomParams;
import com.mathpar.matrix.file.spec.EMatr;
import com.mathpar.matrix.file.spec.IMatr;
import com.mathpar.matrix.file.spec.SpecMatr;
import com.mathpar.matrix.file.utils.BaseMatrixDir;
import com.mathpar.matrix.file.utils.FileUtils;
import com.mathpar.matrix.file.utils.Path;
import com.mathpar.number.Element;
import java.io.*;
import java.util.Random;
import java.util.Properties;

import com.mathpar.number.Ring;

/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006,2007,2008</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class SFileMatrix {


    /**
     * Путь к корню файловой матрицы.
     * Если root==null, то это нулевая матрица, иначе не нулевая.
     * Если глубина = 0, то это путь к файлу.
     * Если глубина > 0, то это путь к корневой директории дерева.
     */
    protected File root;
    /**
     * Глубина дерева файловой матрицы.
     * Если глубина = 0, то это файл.
     * Если глубина > 0, то это дерево.
     */
    protected int depth;


    public MatrixOps ops;

    public  Element elm;


    /**
     * Нулевая файловая матрица. Проверка на ноль isZERO().
     */
    private static SFileMatrix ZERO=new SFileMatrix(null,0);


    private static int[][] rows={{0,1},{2,3}};
    private static int[][] cols={{0,2},{1,3}};







    public SFileMatrix(){}



    /**
     * Присваивающий конструктор.
     * @param root File
     * @param depth int
     */
    public SFileMatrix(File root, int depth){
        constructor(root,depth);
    }



    /**
     * Присваивающий конструктор.
     * @param root File
     * @param depth int
     */
    protected void constructor(File root, int depth){
        this.root=root;
        this.depth=depth;
    }



    /**
     * Создать разреженную файловую матрицу из корня с удалением нулей (если clear=true).
     * @param root File корень директории (плотной или разреженной)
     * @param mod long
     * @param clear boolean =true, то будет очистка нулей, =false - не будет.
     * @throws IOException
     */
    protected void constructor(File root, long mod, Ring ring, boolean clear)
        throws IOException{
        int depth=findDepth(root);
        if (clear) {
            boolean notZero=clearRec(root,depth,mod,ring);
            if (notZero) {
                //!=0
                constructor(root, depth);
            } else {
                //==0
                root.delete();
                setZERO();
            }
        } else {
            constructor(root, depth);
        }
    }



    /**
     * Создает из плотной файловой матрицы разреженную файловую матрицу с
     * корнем to.
     * @param m FileMatrix
     * @param to File
     * @param mod long
     * @throws IOException
     */
    protected void constructor(FileMatrix m, File to, long mod,Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, m.getDepth());
        boolean notZero=clearRec(m.getRoot(),m.getDepth(),to, mod,ring,
                                 new byte[FileUtils.DEFAULT_BUF_SIZE]);
        if (notZero) {
            //!=0
            constructor(to,m.getDepth());
        } else {
            //==0
            to.delete();
            setZERO();
        }
    }


    /**
     * Генерирует случайную файловую матрицу размером m x n, плотностью params.dden (в %),
     * с корнем в root.
     *
     * @param depth int
     * @param root File
     * @param m int
     * @param n int
     * @param params RandomParams params.dden -- плотность в % [0..100]
     * @throws IOException
     */
    protected void constructor(int depth, File root, int m, int n,
                               RandomParams params,Ring ring)
        throws IOException{
        if (params.dden<0 || params.dden>100) {
            throw new IllegalArgumentException(
                String.format("Illegal density: %.2f",params.dden));
        }
        if (Math.abs(params.dden)<1E-6) {
            //=0
            setZERO();
        } else {
            //создать директорию root, если глубина>0 и если не существует
            FileUtils.createDir(root, depth);
            //создать дерево
            params.dden/=100;
            boolean notZero = construct(depth, root, m, n, params,ring);
            if (notZero) {
                constructor(root,depth);
            } else {
                //=0, удалить пустую директорию
                root.delete();
                setZERO();
            }
        }
    }



    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и случайными матрицами на концах.Результат
     * записывает в root.
     * @param paths int[][]
     * @param m int
     * @param n int
     * @param params RandomParams
     * @param root File
     * @throws IOException
     */
    protected void constructor(int[][] paths, int m, int n,
                               RandomParams params,File root,Ring ring)
        throws IOException{
        constructPaths(paths,null,root,m,n,params,ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.
     * @param paths int[][]
     * @param matrs Object[]
     * @param root File
     * @throws IOException
     */
    protected void constructor(int[][] paths, Object[] matrs, File root,Ring ring)
        throws IOException{
        constructPaths(paths,matrs,root,-1,-1,null,ring);
    }


    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * @param depth int
     * @param root File
     * @param m Object
     * @param mod long
     * @throws IOException
     */
    protected void constructor(int depth, File root, Object m, long mod,Ring ring)
        throws IOException{
        //создать директорию root, если глубина>0 и если не существует
        FileUtils.createDir(root, depth);
        //создать дерево
        boolean notZero = construct(depth, root, m, mod,ring);
        if (notZero) {
            constructor(root,depth);
        } else {
            //=0, удалить пустую директорию
            root.delete();
            setZERO();
        }
    }




    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * @param root File
     * @param depth int
     * @param n int
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix ONE0(File root, int depth, int n,Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(root, depth);
        constructONE(depth,root,n,ring);
        return new SFileMatrix(root,depth);
    }


    protected SFileMatrix ONEmultD0(File root, int depth, Object d, int n,Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(root, depth);
        constructONEd(depth,root,d,n,ring);
        return new SFileMatrix(root,depth);
    }

    public <T extends SFileMatrix> T addForParalell(T fm, long mod, Ring ring)  throws IOException{
        throw new UnsupportedOperationException("not support method addForParalel lwithout file ");
    }
    public <T extends SFileMatrix> T addForParalell(T fm, File to, long mod, Ring ring)  throws IOException {
        throw new UnsupportedOperationException("not support method addForGeneric with file");
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
    protected SFileMatrix add0(SFileMatrix m, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO()) {
            //this=0
            if (m.isZERO()) {
                return ZERO;
            } else {
                //m!=0, то результат=копия m в to
                return m.copy0(to);
            }
        } else {
            //this!=0
            if (m.isZERO()) {
                //m=0, то результат=копия this в to
                return copy0(to);
            }
        }
        //this!=0, m!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        boolean notZero=addRec(root, m.root, to, depth, mod,ring);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
        }
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
    protected SFileMatrix subtract0(SFileMatrix m, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO()) {
            //this=0
            if (m.isZERO()) {
                //m=0
                return ZERO;
            } else {
                //m!=0, то результат=-m в to
                return m.negate0(to,mod,ring);
            }
        } else {
            //this!=0
            if (m.isZERO()) {
                //m=0, то результат=копия this в to
                return copy0(to);
            }
        }
        //this!=0, m!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        boolean notZero=subRec(root, m.root, to, depth, mod,ring);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
        }
    }


    /**
     *
     * @param <T>
     * @param fm
     * @param mod
     * @param ring
     * @return
     * @throws IOException
     */
    public <T extends SFileMatrix> T multiplyForParalell(final T fm, final long mod, final Ring ring)  throws IOException{
         throw new UnsupportedOperationException("not support method multiplyForParalell without file");
    }
    /**
     *
     * @param <T>
     * @param fm
     * @param to
     * @param mod
     * @param ring
     * @return
     * @throws IOException
     */
    public <T extends SFileMatrix> T multiplyForParalell(final T fm, final File to, final long mod, final Ring ring)  throws IOException{
        throw new UnsupportedOperationException("not support method multiplyForParalell with file");
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
    protected SFileMatrix multCU0(SFileMatrix m, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO() || m.isZERO()) {
            //this==0 или m==0
            return ZERO;
        }
        //this!=0, m!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        boolean notZero=multCURec(root, m.root, to, depth, mod,ring);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
        }
    }



    protected SFileMatrix multiplyDivRecursive0(SFileMatrix m, Object div, File to, long mod,Ring ring)
        throws IOException{
        /*
        if (isZERO() || m.isZERO()) {
            //this==0 или m==0
            return ZERO;
        }
        //this!=0, m!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        boolean notZero=multiplyDivRec(root, m.root, to, div, depth, mod,ring);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
                 }*/
        SFileMatrix mmult=multCU0(m,mod,ring);
        mmult.ops=ops;
        SFileMatrix res=mmult.divideByNumber0(div,to,mod,ring);
        res.ops=ops;
        return res;
    }


    protected SFileMatrix multiplyDivMulRecursive0(SFileMatrix m, Object div, Object mult, File to, long mod,Ring ring)
        throws IOException{
        /*
        if (isZERO() || m.isZERO()) {
            //this==0 или m==0
            return ZERO;
        }
        //this!=0, m!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        boolean notZero=multiplyDivMulRec(root, m.root, to, div, mult, depth, mod);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
                 }*/
        SFileMatrix mmult=multCU0(m,mod,ring);
        mmult.ops=ops;
        SFileMatrix res=mmult.divideMultiply0(div,mult,to,mod,ring);
        res.ops=ops;
        return res;
    }



    protected SFileMatrix multCUEL0(int[] Ei, int[] Ej, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO() || Ei.length==0) {
            //this==0 или E==0
            return ZERO;
        }
        //this!=0, E!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        int[] size=getFullSize();
        int rows=size[0];
        SpecMatr spm=new EMatr(Ei,Ej,rows,rows);
        boolean notZero=multCUEILRec(spm, root, to, depth, mod,ring);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
        }
    }



    protected SFileMatrix multCUIL0(int[] Ei, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO() || Ei.length==0) {
            //this==0 или I==0
            return ZERO;
        }
        //this!=0, I!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        int[] size=getFullSize();
        int rows=size[0];
        SpecMatr spm=new IMatr(Ei,rows);
        boolean notZero=multCUEILRec(spm, root, to, depth, mod,ring);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
        }
    }


    public void multiplyByNumberThis(Object mult, long mod,Ring ring) throws IOException{
        if (!isZERO()) {
            //!=0
            multNumRec(root, mult, depth, mod,ring);
        }
    }

    public SFileMatrix multiplyByNumber0(Object mult, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO()) {
            return ZERO;
        }
        //this!=0
        FileUtils.createDir(to, depth);
        multNumRec(root,mult, to, depth, mod,ring);
        return new SFileMatrix(to, depth);
    }

    public <T extends SFileMatrix> T multiplyByNumber0Paralell(Element mult, File to, long mod,Ring ring) throws  IOException{
         throw new UnsupportedOperationException("not support method multiplyForParalell without file");
    }



    public void divideByNumberThis(Object div, long mod,Ring ring) throws IOException{
        if (!isZERO()) {
            //!=0
            divNumRec(root, div, depth, mod,ring);
        }
    }

    protected SFileMatrix divideByNumber0(Object div, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO()) {
            return ZERO;
        }
        //this!=0
        FileUtils.createDir(to, depth);
        divNumRec(root,div, to, depth, mod,ring);
        return new SFileMatrix(to, depth);
    }



    public void multiplyDivideThis(Object mult, Object div, long mod,Ring ring) throws IOException{
        if (!isZERO()) {
            //!=0
            multDivNumRec(root, mult, div, depth, mod,ring);
        }
    }

    protected SFileMatrix multiplyDivide0(Object mult, Object div, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO()) {
            return ZERO;
        }
        //this!=0
        FileUtils.createDir(to, depth);
        multDivNumRec(root, mult, div, to, depth, mod,ring);
        return new SFileMatrix(to, depth);
    }


    public void divideMultiplyThis(Object div, Object mult, long mod,Ring ring) throws IOException{
        if (!isZERO()) {
            //!=0
            divMultNumRec(root, div, mult,  depth, mod,ring);
        }
    }

    protected SFileMatrix divideMultiply0(Object div, Object mult, File to, long mod,Ring ring)
        throws IOException{
        if (isZERO()) {
            return ZERO;
        }
        //this!=0
        FileUtils.createDir(to, depth);
        divMultNumRec(root, div, mult,  to, depth, mod,ring);
        return new SFileMatrix(to, depth);
    }


    /** Вычисление ядра оператора:  Yij = -dij I + Eij^T Sij.
     *  Причем в матрице Eij^T * Sij на диагональных позициях стоят числа d=dij,
     *  Sij - входная матрица (this).  Процедура не параллелится!
     *  @param d - диагональный элемент (dij)
     *  @param Ei номера строк матрицы E
     *  @param Ej номера столбцов матрицы E
     *  @return Eij^T * S - d * I типа SFileMatrix
     */
/*    protected SFileMatrix  ES_min_dI0(Object d, int[] Ei, int[] Ej, File root, int depth, int n,long mod,Ring ring)
        throws IOException{
        SFileMatrix ES=multCUEL0(Ej,Ei,mod);
        ES.ops=ops;
        SFileMatrix dI=ONEmultD0(depth,d,n);
        dI.ops=ops;
        SFileMatrix res=ES.subtract0(dI,root, mod);
        return res;
    }
*/

    protected SFileMatrix  ES_min_dI0(Object d, int[] Ei, int[] Ej, File to,
                                      int depth, int n,long mod,Ring ring)
        throws IOException{
        if (isZERO() || Ei.length==0) {
            //this==0 или E==0
            //md=-d
            //mdI=-dI
            Object md=ops.negateNum(d,ring);
            SFileMatrix mdI=ONEmultD0(depth,md,n,ring);
            mdI.ops=ops;
            return mdI;
        }
        //this!=0, E!=0
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        int[] size=getFullSize();
        int rows=size[0];
        SpecMatr spm=new EMatr(Ej,Ei,rows,rows);
        boolean notZero=ES_min_dIRec(spm, root, to, depth,d,
                                     new Path(),new Path(),mod,ring);
        //вернуть объект файловой матрицы
        if (notZero) {
            return new SFileMatrix(to, depth);
        } else {
            //=0, удалить пустую директорию
            to.delete();
            return ZERO;
        }

    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * @param i int
     * @param to File
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix getSubBlockCopy0(int i, File to)
        throws IOException{
        if (isZERO() || depth==0) {
            throw new IllegalArgumentException("matrix=0 or depth=0");
        } else {
            //depth>0
            SFileMatrix subBl=getSubBlock0(i);
            SFileMatrix copy=subBl.copy0(to);
            return copy;
        }
    }



    /**
     * Копирует файловую матрицу this в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     *
     * @param to File новый путь к корню
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix copy0(File to) throws IOException{
        if (isZERO()) {
            return ZERO;
        }
        copyOnly(to);
        return new SFileMatrix(to, depth);
    }


    /**
     * Копирует файловую матрицу this в новое место to.
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     * @param to File
     * @throws IOException
     */
    public void copyOnly(File to) throws IOException{
        if (!isZERO()) {
            FileUtils.createDir(to, depth);
            copyRec(root, to, depth, new byte[FileUtils.DEFAULT_BUF_SIZE]);
        }
    }


    /**
     * Перемещает файловую матрицу в новое место to. Меняет файловую
     * матрицу this.
     * @param to File
     * @throws IOException
     */
    public void move(File to)
        throws IOException{
        FileUtils.move(root,to);
        root=to;
    }



    /**
     * Делает this=-this по модулю mod. Меняет файл или дерево this.
     * @param mod long
     * @throws IOException
     */
    public void negateThis(long mod,Ring ring) throws IOException{
        if (!isZERO()) {
            //!=0
            negateRec(root, depth, mod,ring);
        }
    }

    /**
     * Создает новую файловую матрицу равную (-this) по модулю mod в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * @param to File
     * @param mod long
     * @return SFileMatrixL
     * @throws IOException
     */
    protected SFileMatrix negate0(File to, long mod,Ring ring) throws IOException{
        if (isZERO()) {
            return ZERO;
        }
        //this!=0
        FileUtils.createDir(to, depth);
        negateRec(root, to, depth, mod,ring);
        return new SFileMatrix(to, depth);
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs SFileMatrix[]
     * @param to File
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix joinMove0(SFileMatrix[] matrs, File to)
        throws IOException{
        return joinCopyMoveNull(matrs, to, 2);
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * @param matrs SFileMatrix[]
     * @param to File
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix joinCopy0(SFileMatrix[] matrs, File to)
        throws IOException{
        return joinCopyMoveNull(matrs, to, 1);
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
     * @param m FileMatrix
     * @param mod long
     * @throws IOException
     */
    protected void constructor(FileMatrix m, long mod,Ring ring)
        throws IOException{
        constructor(m,BaseMatrixDir.getRandomDir(),mod,ring);
    }


    /**
     * Генерирует случайную файловую матрицу размером m x n, плотностью den (в %),
     * с корнем в root.<br>
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param m int
     * @param n int
     * @param params RandomParams params.dden -- плотность в % [0..100]
     * @throws IOException
     */
    protected void constructor(int depth, int m, int n,
                               RandomParams params,Ring ring)
        throws IOException{
        constructor(depth,BaseMatrixDir.getRandomDir(),m,n,params,ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и случайными матрицами на концах. Результат
     * записывает в root.<br>
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param paths int[][]
     * @param m int
     * @param n int
     * @param params RandomParams
     * @throws IOException
     */
    protected void constructor(int[][] paths, int m, int n,
                               RandomParams params,Ring ring)
        throws IOException {
        constructor(paths,m,n,params,BaseMatrixDir.getRandomDir(),ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.<br>
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param paths int[][]
     * @param matrs MatrixL[]
     * @throws IOException
     */
    protected void constructor(int[][] paths, Object[] matrs,Ring ring)
        throws IOException{
        constructor(paths,matrs,BaseMatrixDir.getRandomDir(),ring);
    }



    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param m Object
     * @param mod long
     * @throws IOException
     */
    protected void constructor(int depth, Object m, long mod,Ring ring)
        throws IOException{
        constructor(depth,BaseMatrixDir.getRandomDir(),m,mod,ring);
    }


    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param n int
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix ONE0(int depth, int n,Ring ring)
        throws IOException{
        return ONE0(BaseMatrixDir.getRandomDir(),depth,n,ring);
    }


    protected SFileMatrix ONEmultD0(int depth, Object d, int n,Ring ring)
        throws IOException{
        return ONEmultD0(BaseMatrixDir.getRandomDir(),depth,d,n,ring);
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
    protected SFileMatrix add0(SFileMatrix m, long mod,Ring ring)
        throws IOException{
        return add0(m,BaseMatrixDir.getRandomDir(),mod,ring);
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
    protected SFileMatrix subtract0(SFileMatrix m, long mod,Ring ring)
        throws IOException{
        return subtract0(m,BaseMatrixDir.getRandomDir(),mod,ring);
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
    protected SFileMatrix multCU0(SFileMatrix m, long mod,Ring ring)
        throws IOException{
        return multCU0(m,BaseMatrixDir.getRandomDir(),mod,ring);
    }


    protected SFileMatrix multiplyDivRecursive0(SFileMatrix m, Object div, long mod,Ring ring)
        throws IOException{
        return multiplyDivRecursive0(m,div,BaseMatrixDir.getRandomDir(),mod,ring);
    }


    protected SFileMatrix multiplyDivMulRecursive0(SFileMatrix m, Object div, Object mult, long mod,Ring ring)
        throws IOException{
        return multiplyDivMulRecursive0(m,div,mult,BaseMatrixDir.getRandomDir(),mod,ring);
    }



    protected SFileMatrix multCUEL0(int[] Ei, int[] Ej, long mod,Ring ring)
        throws IOException{
        return multCUEL0(Ei,Ej,BaseMatrixDir.getRandomDir(),mod,ring);
    }



    protected SFileMatrix multCUIL0(int[] Ei, long mod,Ring ring)
        throws IOException{
        return multCUIL0(Ei,BaseMatrixDir.getRandomDir(),mod,ring);
    }




    protected SFileMatrix multiplyByNumber0(Object mult, long mod,Ring ring)
        throws IOException{
        return multiplyByNumber0(mult,BaseMatrixDir.getRandomDir(),mod,ring);
    }



    protected SFileMatrix divideByNumber0(Object div, long mod,Ring ring)
        throws IOException{
        return divideByNumber0(div,BaseMatrixDir.getRandomDir(),mod,ring);
    }



    protected SFileMatrix multiplyDivide0(Object mult, Object div, long mod,Ring ring)
        throws IOException{
        return multiplyDivide0(mult,div,BaseMatrixDir.getRandomDir(),mod,ring);
    }

    protected SFileMatrix divideMultiply0(Object div, Object mult, long mod,Ring ring)
        throws IOException{
        return divideMultiply0(div,mult,BaseMatrixDir.getRandomDir(),mod,ring);
    }


    protected SFileMatrix  ES_min_dI0(Object d, int[] Ei, int[] Ej, int depth,int n,long mod,Ring ring)
        throws IOException{
        return ES_min_dI0(d,Ei,Ej,BaseMatrixDir.getRandomDir(),depth,n,mod,ring);
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param i int
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix getSubBlockCopy0(int i)
        throws IOException{
        return getSubBlockCopy0(i,BaseMatrixDir.getRandomDir());
    }


    /**
     * Копирует файловую матрицу this в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     *
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix copy0() throws IOException{
        return copy0(BaseMatrixDir.getRandomDir());
    }


    /**
     * Перемещает файловую матрицу в новое место to. Меняет файловую
     * матрицу this.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @throws IOException
     */
    public void move()
        throws IOException{
        move(BaseMatrixDir.getRandomDir());
    }



    /**
     * Создает новую файловую матрицу равную (-this) по модулю mod в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param mod long
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix negate0(long mod,Ring ring) throws IOException{
        return negate0(BaseMatrixDir.getRandomDir(),mod,ring);
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param matrs SFileMatrix[]
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix joinMove0(SFileMatrix[] matrs)
        throws IOException{
        return joinMove0(matrs, BaseMatrixDir.getRandomDir());
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param matrs SFileMatrix[]
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix joinCopy0(SFileMatrix[] matrs)
        throws IOException{
        return joinCopy0(matrs, BaseMatrixDir.getRandomDir());
    }




    //=======================================================================
    //============== Методы сохранения и восстановления =====================
    //=======================================================================



    private final static String MATR_DIR_NAME="matr";
    private final static String DESC_NAME="desc.txt";
    private final static String KEY_DEPTH="depth";
    private final static String KEY_CLASS="class";
    private final static String COMMENT="<==== SFileMatrix info file ====>";



//    protected void finalize0(){
//        /*System.out.printf("finalize0: class='%s', root='%s', depth=%d\n",
//                          this.getClass().getSimpleName(), root, depth);*/
//        if (root!=null) {
//            /*new Thread() {
//                public void run() {
//                    try {
//                        SFileMatrix.this.delete();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }.start();*/
//            try {
//                delete();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//            root=null;
//        }
//    }


    /**
     * Оставляет файловую матрицу this.
     * Даже если ссылок на объект не будет
     * объект будет уничтожен, а дерево останется.
     */
    public void keep(){
        root=null;
    }


    /**
     * Сохраняет все файловые матрицы из массива.
     * @param sfms SFileMatrix[]
     */
    public static void keep(SFileMatrix[] sfms){
        for (int i = 0; i < sfms.length; i++) {
            sfms[i].keep();
        }
    }


    /**
     * Сохраняет файловую матрицу this в директории dir.
     * В (dir, MATR_DIR_NAME) копирует файловую матрицу.
     * В (dir, DESC_NAME) пишет файл описания:
     * KEY_DEPTH = <глубина this>
     * KEY_CLASS = <класс>
     *
     * @param dir File
     * @throws IOException
     */
    public void saveTo(File dir)
        throws IOException{
        FileUtils.mkdirs(dir);

        //this копировать в (dir,MATR_DIR_NAME)
        copyOnly(new File(dir,MATR_DIR_NAME));

        //создать дескриптор в dir
        Properties props=new Properties();
        props.setProperty(KEY_DEPTH, depth+"");
        props.setProperty(KEY_CLASS, getClass().getSimpleName());
        FileOutputStream out=new FileOutputStream(new File(dir,DESC_NAME));
        props.store(out, COMMENT);
        out.close();
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo.
     * Возвращает файловую матрицу, которая указывает на поддиректорию (подфайл).
     * @param dir File
     * @return FileMatrix
     * @throws IOException
     */
    private SFileMatrix restore(File dir)
        throws IOException{
        //прочитать файл описания
        Properties props=new Properties();
        FileInputStream in=new FileInputStream(new File(dir,DESC_NAME));
        props.load(in);
        in.close();
        int depth=Integer.parseInt(props.getProperty(KEY_DEPTH));
        String className=props.getProperty(KEY_CLASS);

        //проверить, что класс соответствует
        if (!className.equals(getClass().getSimpleName())) {
            throw new IOException(String.format(
        "Here saved data for file matrix : '%s', not for '%s'.",
        className, getClass().getSimpleName()));
        }

        //вернуть файловую матрицу, указывающую на поддиректорию (подфайл)
        return new SFileMatrix(new File(dir, MATR_DIR_NAME), depth);
    }



    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix restoreCopy0(File dir, File to)
        throws IOException{
        SFileMatrix rest=restore(dir);
        SFileMatrix copy=rest.copy0(to);
        return copy;
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix restoreCopy0(File dir)
        throws IOException{
        return restoreCopy0(dir, BaseMatrixDir.getRandomDir());
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix restoreMove0(File dir, File to)
        throws IOException{
        //восстановить из dir
        SFileMatrix rest=restore(dir);

        //переместить в to
        //rest указывает на новое место
        rest.move(to);

        //удалить dir
        //в dir только файл описания
        new File(dir, DESC_NAME).delete();
        dir.delete();

        return rest;
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrix
     * @throws IOException
     */
    protected SFileMatrix restoreMove0(File dir)
        throws IOException{
        return restoreMove0(dir, BaseMatrixDir.getRandomDir());
    }




    //=======================================================================
    //========================= Другие методы ===============================
    //=======================================================================



    /**
     * Удаление файловой матрицы.
     * Удаляет файл или директорию со всем содержимым.
     * Если успешно, то возвращает true, иначе -- false.
     * Нулевую файловую матрицу удаляет всегда успешно.
     * @return boolean
     * @throws IOException
     */
    public boolean delete() throws IOException{
//        if (isZERO() || !root.exists()) {
//            //this=0 или корень не существует
//            return true;
//        } else {
//            //root -- существует
//            return deleteRec(root, depth);
//        }
        return true;
    }


    /**
     * Удаляет файл (depth=0) или директорию root (depth>0) данной глубины depth
     * со всем содержимым.
     * Если успешно, то возвращает true, иначе -- false.
     * @param root File
     * @param depth int
     * @return boolean
     * @throws IOException
     */
    public static boolean deleteRec(File root, int depth)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //удалить файл
            return root.delete();
            //return deleteFile(root);
        } else {
            //глубина >0
            boolean res=true;
            for (int i = 0; i < 4; i++) {
                File fi=new File(root,i+"");
                boolean resi=true;
                if (fi.exists()) {
                    resi=deleteRec(fi, depth - 1);
                }
                res &= resi;
            }
            //1) Все существующие деревья (fi,n) успешно удалены =>
            //все resi=true => res=true; все деревья (fi,n) удалены вместе с
            //корнем fi => root -- пустая директория.
            //2) Некоторые существующие деревья (fi,n) не удалены =>
            //есть resi=false => res=false; остались неудаленные деревья (fi,n) =>
            //root -- не пустая директория.
            boolean resr=root.delete();
            //1) => resr=true или resr=false
            //2) => всегда resr=false
            return res & resr;
        }
    }



    /**
     * Преобразует файловую матрицу в матрицу в памяти. Файловая матрица
     * должна помещаться в памяти.
     * @return MatrixL
     * @throws IOException
     */
    protected Object toMatrix() throws IOException{
        if (isZERO()) {
            System.out.println("LF");
            return null;
        } else {
            return toMatrixRec(root,depth);
        }
    }


    /**
     * Сравнивает 2 файловые матрицы. Если равны, то возвращает true, если
     * не равны -- false.<br>
     *
     * @param m SFileMatrixL
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    public boolean equals(SFileMatrix m, long mod,Ring ring) throws IOException{
        //проверить на нули
        if (isZERO()) {
            //this=0
            if (m.isZERO()) {
                //оба = 0
                return true;
            } else {
                //m!=0
                return false;
            }
        } else {
            //this!=0
            if (m.isZERO()) {
                //m = 0
                return false;
            }
        }
        //this!=0,m!=0
        //проверить глубины
        if (depth!=m.depth) {
            return false;
        }

        //this!=0,m!=0, глубины равны
        return equalsRec(root, m.root, depth, mod,ring);
    }


    /**
     * Делает матрицу равной нулю. Если у файловой матрицы был файл или дерево,
     * то они не изменяются.
     */
    public void setZERO(){
        root=null;
        depth=0;
    }

    /**
     * Если файловая матрица =0, то возвращает true, иначе -- false.
     * @return boolean
     */
    public boolean isZERO(){
        return root==null;
    }


    /**
     * Возвращает файл или корневую директорию файловой матрицы. Может быть
     * null, если файловая матрица =0.
     * @return File
     */
    public File getRoot(){
        return root;
    }

    /**
     * Возвращает глубину.
     * @return int
     */
    public int getDepth(){
        return depth;
    }


    /**
     * Вычисляет глубину разреженной директории root.
     * В дереве root могут быть не все файлы 0,...,3.
     * @param root File
     * @return int
     * @throws IOException
     */
    public static int findDepth(File root)
        throws IOException{
        File f=root;
        int depth=0;
        while (!f.isFile()) {
            //f - не файл
            //сделать шаг
            int branch=findFirstExist(f);
            f=new File(f,branch+"");
            depth++;
        }
        //f -- файл
        return depth;
    }


    /**
     * Вычисляет размер листовой матрицы.
     * Возвращает массив int[]: {rows, cols}
     * rows -- кол-во строк в листе,
     * cols -- кол-во столбцов в листе.
     * @return int[]
     * @throws IOException
     */
    public int[] getLeafSize()
        throws IOException{
        File f=root;
        while (!f.isFile()) {
            //f - не файл
            //сделать шаг
            int branch=findFirstExist(f);
            f=new File(f,branch+"");
        }
        //f -- файл
        int[] size=ops.getMatrSizeFromFile(f);
        return size;
    }


    /**
     * Вычисляет полный размер матрицы для файловой матрицы this.
     * Возвращает массив int[]: {rows, cols}
     * rows -- кол-во строк во всей матрице,
     * cols -- кол-во столбцов во всей матрице.
     * @return int[]
     * @throws IOException
     */
    public int[] getFullSize()
        throws IOException{
        int[] lsize=getLeafSize();
        //{m,n} = {m,n} * 2^depth
        lsize[0]<<=depth;
        lsize[1]<<=depth;
        return lsize;
    }


    /**
     * Номер 1-го существующего дочернего файла (файла или директории).
     * @param root File
     * @return int
     * @throws IOException
     */
    private static int findFirstExist(File root)
        throws IOException{
        int i=0;
        while (i<4) {
            if (new File(root,i+"").exists()) {
                return i;
            }
            i++;
        }
        throw new IOException(
        String.format("Directory '%s' has no files.",root));
    }


    /**
     * Проверяет есть ли в файловой матрице i-й подблок.<br>
     * @param i int [0..3]
     * @return boolean
     */
    public boolean hasBlock(int i){
        if (isZERO() || depth==0) {
            return false;
        } else {
            return new File(root, String.valueOf(i)).exists();
        }
    }

    /**
     * Возвращает i-й подблок в виде файловой матрицы или null, если он нулевой.
     * Не копирует поддерево (подфайл).
     * Новая файловая матрица указывает на поддиректорию (подфайл).
     * @param i int [0..3]
     * @return SFileMatrixL
     * @throws IOException
     */
    protected SFileMatrix getSubBlock0(int i)
        throws IOException{
        if (isZERO() || depth==0) {
            throw new IllegalArgumentException("matrix=0 or depth=0");
        } else {
            //depth>0
            File rooti=new File(root, String.valueOf(i));
            if (rooti.exists()) {
                return new SFileMatrix(rooti, depth-1);
            } else {
                return null;
            }
        }

    }




    /**
     * Разбить файловую матрицу на 4 подблока.
     * Возвращает массив из 4 файловых матриц, которые указывают на
     * 4 поддерева (подфайла).
     * @return SFileMatrix[] arr[i]=null, если i-й подблок нулевой
     * @throws IOException
     */
    protected SFileMatrix[] split0()
        throws IOException{
        if (isZERO() || depth==0) {
            throw new IllegalArgumentException("matrix=0 or depth=0");
        } else {
            SFileMatrix[] fms=new SFileMatrix[4];
            for (int i = 0; i < 4; i++) {
                fms[i]=getSubBlock0(i);
            }
            return fms;
        }
    }



    /**
     * Разбить файловую матрицу на 4 подблока с перемещением.
     * Все 4 подблока (файловые матрицы) перемещаются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this=0.
     * @return SFileMatrix[] arr[i]=null, если i-й подблок нулевой
     * @throws IOException
     */
    protected SFileMatrix[] splitMove0()
        throws IOException{
        if (isZERO() || depth==0) {
            throw new IllegalArgumentException("matrix=0 or depth=0");
        } else {
            SFileMatrix[] subBls=split0();
            //переместить все поддеревья в некоторые случайные имена в matrsDir
            for (int i = 0; i < 4; i++) {
                if (subBls[i]!=null) {
                    //перемещаем subBls[i] --> matrsDir, subBls[i] будет указывать
                    //на новое место
                    subBls[i].move();
                }
            }
            //т.к. глубина this >0, то удалить пустую корневую директорию root.
            root.delete();
            //this=0
            setZERO();
            return subBls;
        }
    }



    /**
     * Разбить файловую матрицу на 4 подблока с копированием.
     * Все 4 подблока (файловые матрицы) копируются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this НЕ МЕНЯЕТСЯ.
     * @return SFileMatrix[] arr[i]=null, если i-й подблок нулевой
     * @throws IOException
     */
    protected SFileMatrix[] splitCopy0()
        throws IOException{
        if (isZERO() || depth==0) {
            throw new IllegalArgumentException("matrix=0 or depth=0");
        } else {
            SFileMatrix[] subBls=split0();
            SFileMatrix[] matrs=new SFileMatrix[4];
            //копировать все поддеревья в некоторые случайные имена в matrsDir
            for (int i = 0; i < 4; i++) {
                if (subBls[i]!=null) {
                    //копируем subBls[i] --> matrsDir
                    matrs[i]=subBls[i].copy0();
                }
            }
            return matrs;
        }
    }



    //================================================================
    //======================== Private methods =======================
    //================================================================
    /**
     * @param paths int[][] если paths==null или paths.length==0 то ZERO.
     * @param matrs Object[] если matrs!=null (matrs.len==paths.len),
     * то на концах будут матрицы matrs.
     * Если ==null, то случайные матрицы m x n.
     * @param root File
     * @param m int
     * @param n int
     * @param params RandomParams
     * @throws IOException
     */
    private void constructPaths(int[][] paths, Object[] matrs,
                                File root,
                                int m, int n, RandomParams params,Ring ring)
        throws IOException{
        if (paths==null || paths.length==0) {
            //Если путей нет или массив путей пустой, то создать нулевую матрицу
            setZERO();
        } else {
            //глубина = длине любого пути
            //depth>=0
            constructor(root, paths[0].length);

            //проверить условие: matrs.len==paths.len
            if (matrs!=null) {
                if (paths.length!=matrs.length) {
                    throw new IllegalArgumentException(
                        String.format(
                            "constructPaths: paths.length(%d) != matrs.length(%d)",
                            paths.length, matrs.length
                        ));
                }
            }

            if (depth > 0) {
                //глубина>0
                FileUtils.mkdirs(root);
                for (int i = 0; i < paths.length; i++) {
                    //создать i-й путь
                    int[] path = paths[i];
                    File f = root;

                    //создать все директории (depth>1)
                    for (int j = 0; j < depth - 1; j++) {
                        f = new File(f, String.valueOf(path[j]));
                        FileUtils.mkdirEx(f);
                    }

                    //создать последнюю ветвь
                    f = new File(f, String.valueOf(path[depth - 1]));
                    //на конце матрица
                    Object matr;
                    if (matrs != null) {
                        //из matrs
                        matr = matrs[i];
                    } else {
                        //случайная матрица
                        matr = ops.random(m,n,params,ring);
                    }
                    ops.writeMatrToFile(matr, f);
                }
            } else {
                //глубина=0
                //в файл root записать матрицу
                Object matr;
                if (matrs != null) {
                    //из matrs
                    matr = matrs[0];
                } else {
                    //случайную матрицу
                    matr = ops.random(m,n,params,ring);
                }
                ops.writeMatrToFile(matr, root);
            }
        }
    }



    /**
     * если результат!=0, то res=true, файл root существует или непустая директория,<br>
     * если результат=0, то res=false, файл root не существует или директория пустая<br>
     *
     * Директория root должна существовать до вызова этого метода!
     *
     * @param depth int
     * @param root File
     * @param m int
     * @param n int
     * @param params RandomParams params.dden -- плотность 0..1
     * @return boolean
     * @throws IOException
     */
    private boolean construct(int depth, File root, int m, int n,
                              RandomParams params,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object matr=ops.random(m,n,new RandomParams((int)(10000*params.dden),-1,params.mod,
                params.nbits,params.maxpowers,params.rnd,params.randomType),ring);
            if (ops.isZERO(matr,params.mod,ring)) {
                //==0
                //System.out.println("ZERO file!");
                return false;
            } else {
                //!=0
                ops.writeMatrToFile(matr, root);
                return true;
            }
        } else {
            //глубина >0
            double p=params.dden*4;
            int minBr=findMinM(p);
            int br=rand(minBr,4, params.rnd);
            int[] branches=randSet(br,params.rnd);
            double[] denses=spreadDens(br,p);

            boolean res=false;
            for (int i = 0; i < br; i++) {
                int branch=branches[i];
                File f = new File(root, String.valueOf(branch));
                if (depth > 1) {
                    FileUtils.mkdir(f);
                }
                RandomParams pars=new RandomParams(-1,denses[i],
                    params.mod, params.nbits, params.maxpowers, params.rnd,
                    params.randomType);
                boolean notZero=construct(depth - 1, f, m / 2, n / 2, pars,ring);
                if (depth>1 && !notZero) {
                    f.delete();
                }
                res |= notZero;
            }
            return res;
        }
    }



    /**
     * если результат!=0, то res=true, файл root существует или непустая директория,<br>
     * если результат=0, то res=false, файл root не существует или директория пустая<br>
     *
     * @param depth int
     * @param root File
     * @param m Object
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean construct(int depth, File root, Object m, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            if (ops.isZERO(m,mod,ring)) {
                //==0
                return false;
            } else {
                //!=0
                ops.writeMatrToFile(m, root);
                return true;
            }
        } else {
            //глубина >0
            //m разбить на 4 подблока: mb0,...,mb3
            Object[] mb=ops.split(m);

            //записать mb0,...,mb3 --> в 4 поддерева (подфайла)
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                File f = new File(root, i+"");
                if (depth > 1) {
                    FileUtils.mkdir(f);
                }
                boolean notZero=construct(depth - 1, f, mb[i], mod,ring);
                if (depth>1 && !notZero) {
                    f.delete();
                }
                res |= notZero;
            }
            return res;
        }
    }


    /**
     * I (n x n) --> файловую матрицу root, глубиной depth.
     * Если depth>0, то root должен существовать до вызова данного метода.
     * @param depth int
     * @param root File
     * @param n int
     * @throws IOException
     */
    private void constructONE(int depth, File root, int n,Ring ring)
        throws IOException{
        if (depth==0) {
            //I (n x n) --> файл root
            Object matr=ops.one(n,ring);
            ops.writeMatrToFile(matr,root);
        } else {
            //I (n/2 x n/2) --> (root,0)
            File root0=new File(root,"0");
            if (depth>1) {
                FileUtils.mkdir(root0);
            }
            constructONE(depth-1,root0,n/2,ring);
            //I (n/2 x n/2) --> (root,3)
            File root3=new File(root,"3");
            if (depth>1) {
                FileUtils.mkdir(root3);
            }
            constructONE(depth-1,root3,n/2,ring);
        }
    }


    private void constructONEd(int depth, File root, Object d, int n,Ring ring)
        throws IOException{
        if (depth==0) {
            //I (n x n) --> файл root
            Object matr=ops.oneMultD(n,d,ring);
            ops.writeMatrToFile(matr,root);
        } else {
            //I (n/2 x n/2) --> (root,0)
            File root0=new File(root,"0");
            if (depth>1) {
                FileUtils.mkdir(root0);
            }
            constructONEd(depth-1,root0,d,n/2,ring);
            //I (n/2 x n/2) --> (root,3)
            File root3=new File(root,"3");
            if (depth>1) {
                FileUtils.mkdir(root3);
            }
            constructONEd(depth-1,root3,d,n/2,ring);
        }
    }




    /**
     * если результат!=0, то res=true, файл root существует или непустая директория,<br>
     * если результат=0, то res=false, файл root не существует или директория пустая<br>
     *
     * @param root File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean clearRec(File root, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object m=ops.readMatrFromFile(root);
            if (ops.isZERO(m,mod,ring)) {
                //==0
                FileUtils.delete(root);
                return false;
            } else {
                //!=0
                return true;
            }
        } else {
            //глубина >0
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                File fi = new File(root, i+"");
                if (fi.exists()) {
                    boolean notZero=clearRec(fi,depth - 1, mod,ring);
                    if (depth>1 && !notZero) {
                        fi.delete();
                    }
                    res |= notZero;
                }
            }
            return res;
        }
    }


    /**
     * если результат!=0, то res=true, файл to существует или непустая директория,<br>
     * если результат=0, то res=false, файл to не существует или директория пустая<br>
     *
     * @param from File
     * @param depth int
     * @param to File
     * @param mod long
     * @param buf byte[]
     * @return boolean
     * @throws IOException
     */
    private boolean clearRec(File from, int depth, File to, long mod,Ring ring, byte[] buf)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object m=ops.readMatrFromFile(from);
            if (ops.isZERO(m,mod,ring)) {
                //==0
                return false;
            } else {
                //!=0
                //from--->to
                FileUtils.copyFile(from,to,buf);
                return true;
            }
        } else {
            //глубина >0
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                File fi = new File(from, i+"");
                if (fi.exists()) {
                    //создать директорию toi, если нужно
                    File toi=new File(to,i+"");
                    if (depth>1) {
                        FileUtils.mkdir(toi);
                    }
                    //fi --> toi
                    boolean notZero=clearRec(fi,depth - 1, toi, mod,ring, buf);
                    if (depth>1 && !notZero) {
                        toi.delete();
                    }
                    res |= notZero;
                }
            }
            return res;
        }
    }


    private static void copyRec(File from, File to, int depth, byte[] buf)
        throws IOException{
        if (depth==0) {
            //глубина==0
            FileUtils.copyFile(from,to,buf);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File fromi=new File(from,i+"");
                if (fromi.exists()) {
                    File toi=new File(to,i+"");
                    if (depth>1) {
                        FileUtils.mkdir(toi);
                    }
                    copyRec(fromi, toi, depth-1,buf);
                }
            }
        }
    }



    private void negateRec(File root, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(root);
            Object m1=ops.negate(m,mod,ring);
            ops.writeMatrToFile(m1, root);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File rooti=new File(root,i+"");
                if (rooti.exists()) {
                    negateRec(rooti, depth-1, mod,ring);
                }
            }
        }
    }



    private void negateRec(File from, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(from);
            Object m1=ops.negate(m,mod,ring);
            ops.writeMatrToFile(m1, to);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File fromi=new File(from,i+"");
                if (fromi.exists()) {
                    File toi=new File(to,i+"");
                    if (depth>1) {
                        FileUtils.mkdir(toi);
                    }
                    negateRec(fromi, toi, depth-1,mod,ring);
                }
            }
        }
    }



    private void multNumRec(File root, Object mult, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(root);
            Object m1=ops.multNum(m,mult, mod,ring);
            ops.writeMatrToFile(m1, root);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File rooti=new File(root,i+"");
                if (rooti.exists()) {
                    multNumRec(rooti, mult, depth-1, mod,ring);
                }
            }
        }
    }


    protected void multNumRec(File from, Object mult, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(from);
            Object m1=ops.multNum(m,mult, mod,ring);
            ops.writeMatrToFile(m1, to);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File fromi=new File(from,i+"");
                if (fromi.exists()) {
                    File toi=new File(to,i+"");
                    if (depth>1) {
                        FileUtils.mkdir(toi);
                    }
                    multNumRec(fromi, mult, toi, depth-1,mod,ring);
                }
            }
        }
    }



    private void divNumRec(File root, Object div, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(root);
            Object m1=ops.divNum(m,div, mod,ring);
            ops.writeMatrToFile(m1, root);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File rooti=new File(root,i+"");
                if (rooti.exists()) {
                    divNumRec(rooti, div, depth-1, mod,ring);
                }
            }
        }
    }


    private void divNumRec(File from, Object div, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(from);
            Object m1=ops.divNum(m,div, mod,ring);
            ops.writeMatrToFile(m1, to);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File fromi=new File(from,i+"");
                if (fromi.exists()) {
                    File toi=new File(to,i+"");
                    if (depth>1) {
                        FileUtils.mkdir(toi);
                    }
                    divNumRec(fromi, div, toi, depth-1,mod,ring);
                }
            }
        }
    }



    private void multDivNumRec(File root, Object mult, Object div, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(root);
            Object m1=ops.multDivNum(m,mult, div, mod,ring);
            ops.writeMatrToFile(m1, root);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File rooti=new File(root,i+"");
                if (rooti.exists()) {
                    multDivNumRec(rooti, mult, div, depth-1, mod,ring);
                }
            }
        }
    }


    private void multDivNumRec(File from, Object mult, Object div, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(from);
            Object m1=ops.multDivNum(m,mult, div, mod,ring);
            ops.writeMatrToFile(m1, to);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File fromi=new File(from,i+"");
                if (fromi.exists()) {
                    File toi=new File(to,i+"");
                    if (depth>1) {
                        FileUtils.mkdir(toi);
                    }
                    multDivNumRec(fromi, mult, div, toi, depth-1,mod,ring);
                }
            }
        }
    }



    private void divMultNumRec(File root, Object div, Object mult, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(root);
            Object m1=ops.divMultNum(m, div, mult, mod,ring);
            ops.writeMatrToFile(m1, root);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File rooti=new File(root,i+"");
                if (rooti.exists()) {
                    divMultNumRec(rooti, div, mult,  depth-1, mod,ring);
                }
            }
        }
    }


    private void divMultNumRec(File from, Object div, Object mult, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина==0
            Object m=ops.readMatrFromFile(from);
            Object m1=ops.divMultNum(m, div, mult, mod,ring);
            ops.writeMatrToFile(m1, to);
        } else {
            //глубина>0
            for (int i = 0; i < 4; i++) {
                File fromi=new File(from,i+"");
                if (fromi.exists()) {
                    File toi=new File(to,i+"");
                    if (depth>1) {
                        FileUtils.mkdir(toi);
                    }
                    divMultNumRec(fromi, div, mult, toi, depth-1,mod,ring);
                }
            }
        }
    }




    private boolean equalsRec(File r1, File r2, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            return ops.eqMatrs(a,b,mod,ring);
        } else {
            //глубина >0

            for (int i = 0; i < 4; i++) {
                if (!equalsSubTrees(r1,i,r2,i,depth-1,mod,ring)){
                    return false;
                }
            }
            return true;
        }
    }


    private boolean equalsSubTrees(File r1, int a,
                                   File r2, int b,
                                   int depth, long mod,Ring ring)
        throws IOException{
        File r1a = new File(r1, String.valueOf(a));
        File r2b = new File(r2, String.valueOf(b));
        boolean r1aEx=r1a.exists();
        boolean r2bEx=r2b.exists();

        if (!r1aEx && !r2bEx) {
            //если (r1,a) и (r2,b) оба не существуют, то равны
            return true;
        } else if (r1aEx && r2bEx) {
            //если (r1,a) и (r2,b) оба существуют, то вызвать equalsRec()
            return equalsRec(r1a,r2b,depth,mod,ring);
        } else {
            //иначе один из них существует -- не равны
            return false;
        }
    }



    /**
     * 1) Если depth>0, то складывает 2 разреженных
     * дерева глубиной depth с корнем в r1 и r2
     * и получает результирующее дерево с корнем в to. (директория to должна
     * существовать до вызова этого метода!)<br>
     * 2) Если depth=0, то складывает 2 файла r1 и r2 и результат пишет в to.<br>
     * <br>
     * Если в результате сложения деревьев получились нулевые поддеревья, то
     * они не будут созданы.<br>
     * <br>
     * если результат!=0, то res=true, to файл существует или непустая директория,<br>
     * если результат=0, то res=false, to файл не существует или директория пустая<br>
     *
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean addRec(File r1, File r2, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
//            for(long[] k: ((MatrixL) a).M){
//                System.out.println("A" + Array.toString(k));
//            }
            Object c=ops.add(a,b, mod,ring);


            if (ops.isZERO(c,mod,ring)) {
                return false;
            } else {
                ops.writeMatrToFile(c, to);
                return true;
            }
        } else {
            //глубина >0

            boolean res=false;
            for (int i = 0; i < 4; i++) {
                boolean notZero=addSubTrees(r1,i,r2,i,to,i,depth-1,mod,ring);
                res |= notZero;
            }

            //depth=1
            //Если все файлы =0, то все notZeroI=false, то res=false
            // и все файлы не существуют, то to -- пустая директория.
            //Если есть файл!=0, то есть notZeroI=true, то res=true
            // и этот файл существует, то to -- не пустая директория.

            //depth>1
            //Если все поддеревья =0, то все notZeroI=false, то res=false
            // и все поддеревья не существуют (удалены), то to -- пустая директория.
            //Если есть поддеревья!=0, то есть notZeroI=true, то res=true
            // и это поддерево существует, то to -- не пустая директория.

            return res;
        }
    }



    /**
     * До вызова метода (to,c) не существует.
     * Складывает 2 поддерева (r1,a) и (r2,b) и результат пишет в (to,c).
     * если результат!=0, то res=true, (to,c) файл существует или непустая директория,
     * если результат=0, то res=false, (to,c) файл не существует или директория не существует
     *
     * (r1,a)+(r2,b):
     * 1) (r1,a) и (r2,b) не существуют
     * 2) (r1,a) и (r2,b) оба существуют
     * 3) только 1 из (r1,a), (r2,b) существует
     * @param r1 File
     * @param a int
     * @param r2 File
     * @param b int
     * @param to File
     * @param c int
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean addSubTrees(File r1, int a,
                                File r2, int b,
                                File to, int c,
                                int depth, long mod,Ring ring)
        throws IOException{
        File r1a=new File(r1, String.valueOf(a));
        File r2b=new File(r2, String.valueOf(b));
        File toc=new File(to, String.valueOf(c));

        boolean r1aEx=r1a.exists();
        boolean r2bEx=r2b.exists();

        boolean notZero;
        if (!r1aEx && !r2bEx) {
            //(r1,a) и (r2,b) не существуют
            return false;
        } else if (r1aEx && r2bEx) {
            //(r1,a) и (r2,b) оба существуют
            //сложить их: если результат!=0, то true, если =0, то false
            if (depth > 0) {
                //создать директорию до рекурсивного вызова
                FileUtils.mkdir(toc);
            }
            notZero=addRec(r1a,r2b,toc,depth,mod,ring);
            if (depth>0 && !notZero) {
                //если поддерево=0, то удалить директорию (она пустая)
                toc.delete();
            }
        } else {
            //только 1 из (r1,a), (r2,b) существует
            if (depth > 0) {
                //создать директорию до рекурсивного вызова
                FileUtils.mkdir(toc);
            }
            if (r1aEx) {
                //r1a существует, то копировать (r1,a) --> (to,c)
                copyRec(r1a, toc, depth, new byte[FileUtils.DEFAULT_BUF_SIZE]);
            } else {
                //r2b существует, то копировать (r2,b) --> (to,c)
                copyRec(r2b, toc, depth, new byte[FileUtils.DEFAULT_BUF_SIZE]);
            }
            notZero=true;
        }
        return notZero;
    }



    /**
     * если результат!=0, то res=true, to файл существует или непустая директория,<br>
     * если результат=0, то res=false, to файл не существует или директория пустая<br>
     *
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean subRec(File r1, File r2, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.subtract(a,b, mod,ring);
            if (ops.isZERO(c,mod,ring)) {
                return false;
            } else {
                ops.writeMatrToFile(c, to);
                return true;
            }
        } else {
            //глубина >0

            boolean res=false;
            for (int i = 0; i < 4; i++) {
                boolean notZero=subSubTrees(r1,i,r2,i,to,i,depth-1,mod,ring);
                res |= notZero;
            }
            return res;
        }
    }


    /**
     * если результат!=0, то res=true, (to,c) файл существует или непустая директория,
     * если результат=0, то res=false, (to,c) файл не существует или директория не существует
     * @param r1 File
     * @param a int
     * @param r2 File
     * @param b int
     * @param to File
     * @param c int
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean subSubTrees(File r1, int a,
                                File r2, int b,
                                File to, int c,
                                int depth, long mod,Ring ring)
        throws IOException{
        File r1a=new File(r1, String.valueOf(a));
        File r2b=new File(r2, String.valueOf(b));
        File toc=new File(to, String.valueOf(c));

        boolean r1aEx=r1a.exists();
        boolean r2bEx=r2b.exists();

        boolean notZero;
        if (!r1aEx && !r2bEx) {
            //(r1,a) и (r2,b) не существуют
            return false;
        } else if (r1aEx && r2bEx) {
            //(r1,a) и (r2,b) оба существуют
            //вычесть их: если результат!=0, то true, если =0, то false
            if (depth > 0) {
                //создать директорию до рекурсивного вызова
                FileUtils.mkdir(toc);
            }
            notZero=subRec(r1a,r2b,toc,depth,mod,ring);
            if (depth>0 && !notZero) {
                //если поддерево=0, то удалить директорию (она пустая)
                toc.delete();
            }
        } else {
            //только 1 из (r1,a), (r2,b) существует
            if (depth > 0) {
                //создать директорию до рекурсивного вызова
                FileUtils.mkdir(toc);
            }
            if (r1aEx) {
                //r1a существует, то копировать (r1,a) --> (to,c)
                copyRec(r1a, toc, depth, new byte[FileUtils.DEFAULT_BUF_SIZE]);
            } else {
                //r2b существует, то -(r2,b) --> (to,c)
                negateRec(r2b, toc, depth, mod,ring);
            }
            notZero=true;
        }
        return notZero;
    }



    /**
     * если результат!=0, то res=true, to файл существует или непустая директория,<br>
     * если результат=0, то res=false, to файл не существует или директория пустая<br>
     *
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean multCURec(File r1, File r2, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //c=a*b (mod,ring)
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);

            Object c=ops.multCU(a,b, mod,ring);

            if (ops.isZERO(c,mod,ring)) {
                return false;
            } else {
                ops.writeMatrToFile(c, to);
                return true;
            }
        } else {
            //глубина >0
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                int rNum=i>>>1;
                int cNum=i&1;
                int[] r=rows[rNum];
                int[] c=cols[cNum];
                boolean notZero=multRowCol(r1, r[0], r[1],
                                           r2, c[0], c[1],
                                           to, i, depth-1, mod,ring);
                res |= notZero;
            }
            return res;
        }
    }


    /**
     * Умножает строку в r1 на столбец в r2. <br>
     *  <br>
     * Обозначение: <br>
     * (ROOT, dir) -- в корневой директории ROOT, поддиректория (подфайл) dir. <br>
     *  <br>
     * Метод выполняет: (to,e) = (r1,a)*(r2,c) + (r1,b)*(r2,d) <br>
     * Подробно:<br>
     * (r1,a) * (r2,c) -> (to,10) <br>
     * (r1,b) * (r2,d) -> (to,11) <br>
     * (to,10) + (to,11) -> (to,e) <br>
     * delete (to,10) <br>
     * delete (to,11) <br>
     * если результат!=0, то res=true, (to,e) файл существует или непустая директория,
     * если результат=0, то res=false, (to,e) файл не существует или директория не существует
     *
     * (to,e) до вызова не существует.
     *
     * @param r1 File
     * @param a int
     * @param b int
     * @param r2 File
     * @param c int
     * @param d int
     * @param to File
     * @param e int
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean multRowCol(File r1, int a, int b,
                               File r2, int c, int d,
                               File to, int e,
                               int depth, long mod,Ring ring)
        throws IOException{
        File r1a=new File(r1, String.valueOf(a));
        File r1b=new File(r1, String.valueOf(b));

        File r2c=new File(r2, String.valueOf(c));
        File r2d=new File(r2, String.valueOf(d));

        File to10=new File(to, "10");
        File to11=new File(to, "11");

        //(r1,a) * (r2,c) -> (to,10)
        boolean notZero10=multSubTrees(r1a, r2c, to10, depth, mod,ring);

        //(r1,b) * (r2,d) -> (to,11)
        boolean notZero11=multSubTrees(r1b, r2d, to11, depth, mod,ring);
        //(to,10) + (to,11) -> (to,e)
        boolean notZero=addSubTrees(to,10,to,11, to, e, depth, mod,ring);
        //delete (to,10)
        if (notZero10) {
            deleteRec(to10, depth);
        }
        //delete (to,11)
        if (notZero11) {
            deleteRec(to11, depth);
        }
        return notZero;
    }



    /**
     * toc файла или директории не должно быть до вызова.
     * если результат!=0, то res=true, toc файл существует или непустая директория,
     * если результат=0, то res=false, toc файл не существует или директория не существует
     * @param r1a File
     * @param r2b File
     * @param toc File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean multSubTrees(File r1a,
                                 File r2b,
                                 File toc,
                                 int depth, long mod,Ring ring)
        throws IOException{
        boolean r1aEx=r1a.exists();
        boolean r2bEx=r2b.exists();


        boolean notZero;
        if (r1aEx && r2bEx) {
            //(r1,a) и (r2,b) оба существуют
            //умножить их: если результат!=0, то true, если =0, то false
            if (depth>0) {
                FileUtils.mkdir(toc);
            }
            notZero=multCURec(r1a,r2b,toc,depth,mod,ring);
            if (depth>0 && !notZero) {
                toc.delete();
            }
        } else {
            //(r1,a) и (r2,b) не существуют
            //только 1 из (r1,a), (r2,b) существует
            notZero=false;
        }
        return notZero;
    }





    private boolean multiplyDivRec(File r1, File r2, File to, Object div, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //c=a*b (mod,ring)
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.multiplyDiv(a,b, div, mod,ring);
            if (ops.isZERO(c,mod,ring)) {
                return false;
            } else {
                ops.writeMatrToFile(c, to);
                return true;
            }
        } else {
            //глубина >0
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                int rNum=i>>>1;
                int cNum=i&1;
                int[] r=rows[rNum];
                int[] c=cols[cNum];
                boolean notZero=multiplyDivRowCol(r1, r[0], r[1],
                                           r2, c[0], c[1],
                                           to, i, div, depth-1, mod,ring);
                res |= notZero;
            }
            return res;
        }
    }


    private boolean multiplyDivRowCol(File r1, int a, int b,
                               File r2, int c, int d,
                               File to, int e,
                               Object div, int depth, long mod,Ring ring)
        throws IOException{
        File r1a=new File(r1, String.valueOf(a));
        File r1b=new File(r1, String.valueOf(b));

        File r2c=new File(r2, String.valueOf(c));
        File r2d=new File(r2, String.valueOf(d));

        File to10=new File(to, "10");
        File to11=new File(to, "11");

        //(r1,a) * (r2,c) -> (to,10)
        boolean notZero10=multiplyDivSubTrees(r1a, r2c, to10, div, depth, mod,ring);

        //(r1,b) * (r2,d) -> (to,11)
        boolean notZero11=multiplyDivSubTrees(r1b, r2d, to11, div, depth, mod,ring);
        //(to,10) + (to,11) -> (to,e)
        boolean notZero=addSubTrees(to,10,to,11, to, e, depth, mod,ring);
        //delete (to,10)
        if (notZero10) {
            deleteRec(to10, depth);
        }
        //delete (to,11)
        if (notZero11) {
            deleteRec(to11, depth);
        }
        return notZero;
    }



    private boolean multiplyDivSubTrees(File r1a,
                                 File r2b,
                                 File toc,
                                 Object div, int depth, long mod,Ring ring)
        throws IOException{
        boolean r1aEx=r1a.exists();
        boolean r2bEx=r2b.exists();


        boolean notZero;
        if (r1aEx && r2bEx) {
            //(r1,a) и (r2,b) оба существуют
            //умножить их: если результат!=0, то true, если =0, то false
            if (depth>0) {
                FileUtils.mkdir(toc);
            }
            notZero=multiplyDivRec(r1a,r2b,toc,div, depth,mod,ring);
            if (depth>0 && !notZero) {
                toc.delete();
            }
        } else {
            //(r1,a) и (r2,b) не существуют
            //только 1 из (r1,a), (r2,b) существует
            notZero=false;
        }
        return notZero;
    }





    private boolean multiplyDivMulRec(File r1, File r2, File to, Object div, Object mult,
                                      int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //c=a*b (mod,ring)
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.multiplyDivMul(a,b, div, mult, mod,ring);
            if (ops.isZERO(c,mod,ring)) {
                return false;
            } else {
                ops.writeMatrToFile(c, to);
                return true;
            }
        } else {
            //глубина >0
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                int rNum=i>>>1;
                int cNum=i&1;
                int[] r=rows[rNum];
                int[] c=cols[cNum];
                boolean notZero=multiplyDivMulRowCol(r1, r[0], r[1],
                                           r2, c[0], c[1],
                                           to, i, div, mult, depth-1, mod,ring);
                res |= notZero;
            }
            return res;
        }
    }


    private boolean multiplyDivMulRowCol(File r1, int a, int b,
                               File r2, int c, int d,
                               File to, int e,
                               Object div, Object mult, int depth, long mod,Ring ring)
        throws IOException{
        File r1a=new File(r1, String.valueOf(a));
        File r1b=new File(r1, String.valueOf(b));

        File r2c=new File(r2, String.valueOf(c));
        File r2d=new File(r2, String.valueOf(d));

        File to10=new File(to, "10");
        File to11=new File(to, "11");

        //(r1,a) * (r2,c) -> (to,10)
        boolean notZero10=multiplyDivMulSubTrees(r1a, r2c, to10, div, mult, depth, mod,ring);

        //(r1,b) * (r2,d) -> (to,11)
        boolean notZero11=multiplyDivMulSubTrees(r1b, r2d, to11, div, mult, depth, mod,ring);
        //(to,10) + (to,11) -> (to,e)
        boolean notZero=addSubTrees(to,10,to,11, to, e, depth, mod,ring);
        //delete (to,10)
        if (notZero10) {
            deleteRec(to10, depth);
        }
        //delete (to,11)
        if (notZero11) {
            deleteRec(to11, depth);
        }
        return notZero;
    }



    private boolean multiplyDivMulSubTrees(File r1a,
                                 File r2b,
                                 File toc,
                                 Object div, Object mult, int depth, long mod,Ring ring)
        throws IOException{
        boolean r1aEx=r1a.exists();
        boolean r2bEx=r2b.exists();


        boolean notZero;
        if (r1aEx && r2bEx) {
            //(r1,a) и (r2,b) оба существуют
            //умножить их: если результат!=0, то true, если =0, то false
            if (depth>0) {
                FileUtils.mkdir(toc);
            }
            notZero=multiplyDivMulRec(r1a,r2b,toc, div, mult, depth,mod,ring);
            if (depth>0 && !notZero) {
                toc.delete();
            }
        } else {
            //(r1,a) и (r2,b) не существуют
            //только 1 из (r1,a), (r2,b) существует
            notZero=false;
        }
        return notZero;
    }





    /**
     * если результат!=0, то res=true, to файл существует или непустая директория,<br>
     * если результат=0, то res=false, to файл не существует или директория пустая<br>
     *
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean multCUEILRec(SpecMatr r1, File r2, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //c=E_I*b (mod,ring)
            Object c;
            Object b=ops.readMatrFromFile(r2);
            if (r1.isEMatr()) {
                c=ops.multEL(b,(EMatr)r1,mod,ring);
            } else if (r1.isIMatr()) {
                c=ops.multIL(b,(IMatr)r1,mod,ring);
            } else {
                throw new RuntimeException("Not I,E-matrix.");
            }
            if (ops.isZERO(c,mod,ring)) {
                return false;
            } else {
                ops.writeMatrToFile(c, to);
                return true;
            }
        } else {
            //глубина >0
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                int rNum=i>>>1;
                int cNum=i&1;
                int[] r=rows[rNum];
                int[] c=cols[cNum];
                boolean notZero=multRowCol(r1, r[0], r[1],
                                           r2, c[0], c[1],
                                           to, i, depth-1, mod,ring);
                res |= notZero;
            }
            return res;
        }
    }


    /**
     * Умножает строку в r1 на столбец в r2. <br>
     *  <br>
     * Обозначение: <br>
     * (ROOT, dir) -- в корневой директории ROOT, поддиректория (подфайл) dir. <br>
     *  <br>
     * Метод выполняет: (to,e) = (r1,a)*(r2,c) + (r1,b)*(r2,d) <br>
     * Подробно:<br>
     * (r1,a) * (r2,c) -> (to,10) <br>
     * (r1,b) * (r2,d) -> (to,11) <br>
     * (to,10) + (to,11) -> (to,e) <br>
     * delete (to,10) <br>
     * delete (to,11) <br>
     * если результат!=0, то res=true, (to,e) файл существует или непустая директория,
     * если результат=0, то res=false, (to,e) файл не существует или директория не существует
     *
     * (to,e) до вызова не существует.
     *
     * @param r1 File
     * @param a int
     * @param b int
     * @param r2 File
     * @param c int
     * @param d int
     * @param to File
     * @param e int
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean multRowCol(SpecMatr r1, int a, int b,
                               File r2, int c, int d,
                               File to, int e,
                               int depth, long mod,Ring ring)
        throws IOException{
        SpecMatr r1a=r1.getSubBlock(a);
        SpecMatr r1b=r1.getSubBlock(b);

        File r2c=new File(r2, String.valueOf(c));
        File r2d=new File(r2, String.valueOf(d));

        File to10=new File(to, "10");
        File to11=new File(to, "11");

        //(r1,a) * (r2,c) -> (to,10)
        boolean notZero10=multSubTrees(r1a, r2c, to10, depth, mod,ring);

        //(r1,b) * (r2,d) -> (to,11)
        boolean notZero11=multSubTrees(r1b, r2d, to11, depth, mod,ring);
        //(to,10) + (to,11) -> (to,e)
        boolean notZero=addSubTrees(to,10,to,11, to, e, depth, mod,ring);
        //delete (to,10)
        if (notZero10) {
            deleteRec(to10, depth);
        }
        //delete (to,11)
        if (notZero11) {
            deleteRec(to11, depth);
        }
        return notZero;
    }



    /**
     * toc файла или директории не должно быть до вызова.
     * если результат!=0, то res=true, toc файл существует или непустая директория,
     * если результат=0, то res=false, toc файл не существует или директория не существует
     * @param r1a File
     * @param r2b File
     * @param toc File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean multSubTrees(SpecMatr r1a,
                                 File r2b,
                                 File toc,
                                 int depth, long mod,Ring ring)
        throws IOException{
        boolean r1aEx=r1a.isNotZero();
        boolean r2bEx=r2b.exists();


        boolean notZero;
        if (r1aEx && r2bEx) {
            //(r1,a) и (r2,b) оба существуют
            //умножить их: если результат!=0, то true, если =0, то false
            if (depth>0) {
                FileUtils.mkdir(toc);
            }
            notZero=multCUEILRec(r1a,r2b,toc,depth,mod,ring);
            if (depth>0 && !notZero) {
                toc.delete();
            }
        } else {
            //(r1,a) и (r2,b) не существуют
            //только 1 из (r1,a), (r2,b) существует
            notZero=false;
        }
        return notZero;
    }



    /**
     * если результат!=0, то res=true, to файл существует или непустая директория,<br>
     * если результат=0, то res=false, to файл не существует или директория пустая<br>
     *
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean ES_min_dIRec(SpecMatr r1, File r2, File to, int depth,
                                 Object d, Path pathR1, Path pathR2, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object c;
            Object b=ops.readMatrFromFile(r2);
            if (pathR1.isDiagonal() && pathR2.isDiagonal()) {
                //c=E^T*S-dI
                c=ops.EnotTS_min_dI(b,(EMatr)r1,d,mod,ring);
            } else {
                //c=E^T*S
                c=ops.multEL(b,(EMatr)r1,mod,ring);
            }

            if (ops.isZERO(c,mod,ring)) {
                return false;
            } else {
                ops.writeMatrToFile(c, to);
                return true;
            }
        } else {
            //глубина >0
            boolean res=false;
            for (int i = 0; i < 4; i++) {
                int rNum=i>>>1;
                int cNum=i&1;
                int[] r=rows[rNum];
                int[] c=cols[cNum];
                boolean notZero=ES_min_dImultRowCol(r1, r[0], r[1],
                    r2, c[0], c[1],
                    to, i, depth-1,d,pathR1,pathR2,mod,ring);
                res |= notZero;
            }
            return res;
        }
    }


    /**
     * Умножает строку в r1 на столбец в r2. <br>
     *  <br>
     * Обозначение: <br>
     * (ROOT, dir) -- в корневой директории ROOT, поддиректория (подфайл) dir. <br>
     *  <br>
     * Метод выполняет: (to,e) = (r1,a)*(r2,c) + (r1,b)*(r2,d) <br>
     * Подробно:<br>
     * (r1,a) * (r2,c) -> (to,10) <br>
     * (r1,b) * (r2,d) -> (to,11) <br>
     * (to,10) + (to,11) -> (to,e) <br>
     * delete (to,10) <br>
     * delete (to,11) <br>
     * если результат!=0, то res=true, (to,e) файл существует или непустая директория,
     * если результат=0, то res=false, (to,e) файл не существует или директория не существует
     *
     * (to,e) до вызова не существует.
     *
     * @param r1 File
     * @param a int
     * @param b int
     * @param r2 File
     * @param c int
     * @param d int
     * @param to File
     * @param e int
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean ES_min_dImultRowCol(SpecMatr r1, int a, int b,
                               File r2, int c, int d,
                               File to, int e,
                               int depth, Object numd, Path pathR1, Path pathR2,
                               long mod,Ring ring)
        throws IOException{
        SpecMatr r1a=r1.getSubBlock(a);
        SpecMatr r1b=r1.getSubBlock(b);

        File r2c=new File(r2, String.valueOf(c));
        File r2d=new File(r2, String.valueOf(d));

        File to10=new File(to, "10");
        File to11=new File(to, "11");

        //(r1,a) * (r2,c) -> (to,10)
        boolean notZero10=ES_min_dImultSubTrees(r1a, r2c, to10, depth,numd,
                                                new Path(pathR1,a), new Path(pathR2,c), mod,ring);

        //(r1,b) * (r2,d) -> (to,11)
        boolean notZero11=ES_min_dImultSubTrees(r1b, r2d, to11, depth,numd,
                                                new Path(pathR1,b), new Path(pathR2,d), mod,ring);
        //(to,10) + (to,11) -> (to,e)
        boolean notZero=addSubTrees(to,10,to,11, to, e, depth, mod,ring);
        //delete (to,10)
        if (notZero10) {
            deleteRec(to10, depth);
        }
        //delete (to,11)
        if (notZero11) {
            deleteRec(to11, depth);
        }
        return notZero;
    }



    /**
     * toc файла или директории не должно быть до вызова.
     * если результат!=0, то res=true, toc файл существует или непустая директория,
     * если результат=0, то res=false, toc файл не существует или директория не существует
     * @param r1a File
     * @param r2b File
     * @param toc File
     * @param depth int
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    private boolean ES_min_dImultSubTrees(SpecMatr r1a,
                                          File r2b,
                                          File toc,
                                          int depth, Object d,
                                          Path pathR1a, Path pathR2b, long mod,Ring ring)
        throws IOException{
        boolean r1aEx=r1a.isNotZero();
        boolean r2bEx=r2b.exists();


        boolean notZero;
        if (r1aEx && r2bEx) {
            //(r1,a) и (r2,b) оба существуют
            //умножить их: если результат!=0, то true, если =0, то false
            if (depth>0) {
                FileUtils.mkdir(toc);
            }
            notZero=ES_min_dIRec(r1a,r2b,toc,depth,d,pathR1a,pathR2b,mod,ring);
            if (depth>0 && !notZero) {
                toc.delete();
            }
        } else {
            //(r1,a) и (r2,b) не существуют
            //только 1 из (r1,a), (r2,b) существует
            if (pathR1a.isDiagonal() && pathR2b.isDiagonal()) {
                //-dI-->toc
                if (depth>0) {
                    FileUtils.mkdir(toc);
                }
                ONEmultD0(toc,depth,ops.negateNum(d,ring),r1a.getRows(),ring);
                notZero=true;
            } else {
                //0
                notZero=false;
            }
        }
        return notZero;
    }



    private Object toMatrixRec(File root, int depth) throws IOException{
        if (depth==0) {
            //глубина = 0
            //прочитать из файла матрицу
            Object m=ops.readMatrFromFile(root);
            return m;
        } else {
            //глубина >0
            Object[] m=new Object[4];
            for (int i = 0; i < 4; i++) {
                File fi=new File(root,i+"");
                if (fi.exists()) {
                    m[i]=toMatrixRec(fi,depth-1);
                }
            }
            Object res=ops.join(m);
            return res;
        }
    }




    /**
     * op=1 -- copy
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * или:
     * op=2 -- move
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     *
     * Если matrs[i]=null или matrs[i]=ZERO, то i-й подблок to будет нулевым.
     * @param matrs SFileMatrix[]
     * @param to File
     * @param op int
     * @return SFileMatrix
     * @throws IOException
     */
    private SFileMatrix joinCopyMoveNull(SFileMatrix[] matrs, File to, int op)
        throws IOException{
        int pos=findFirstNotNull(matrs);
        if (pos==-1) {
            throw new IllegalArgumentException("All matrs[i]==null. Depth undefined.");
        }
        pos=findFirstNotZERO(matrs);
        if (pos==-1) {
            //все==0
            return SFileMatrix.ZERO;
        }
        //pos>=0
        FileUtils.mkdirs(to);
        int depth=matrs[pos].depth;
        for (int i = 0; i < matrs.length; i++) {
            if (matrs[i]!=null && !matrs[i].isZERO()) {
                if (op==1) {
                    //op=1 -- копировать
                    //matrs[i] --> (to,i), будет указывать на старое место
                    matrs[i].copyOnly(new File(to,i+""));
                } else {
                    //op=2 -- перемещать
                    //matrs[i] --> (to,i), будет указывать на (to,i)
                    matrs[i].move(new File(to,i+""));
                    matrs[i].keep();
                }
            }
        }
        return new SFileMatrix(to,depth+1);
    }



    /**
     * Генерирует случайное целое число [a,b].
     * @param a int a<=b
     * @param b int a<=b
     * @param rnd Random
     * @return int
     */
    private static int rand(int a, int b, Random rnd){
        //[0,b-a+1)int = [0,b-a]int -> [a,b]int
        return rnd.nextInt(b-a+1)+a;
    }


    /**
     * Возвращает br номеров ветвей, которые будут выбраны.
     * Возвращает int[] res, res.len=br, 0<=res[i]<=3, различны и по возрастанию.
     *
     * @param br int
     * @param rnd Random
     * @return int[]
     */
    private static int[] randSet(int br, Random rnd){
        boolean[] res=new boolean[4];
        //res = t,..,t,f,...,f
        for (int i = 0; i < br; i++) {
            res[i]=true;
        }
        //оптимизация: если br=4, то тасовать не нужно, все true.
        if (br<4) {
            //тасовать
            int ntimes=rnd.nextInt(100);
            for (int i = 0; i < ntimes; i++) {
                //p,q=[0..3]
                int p=rnd.nextInt(4);
                int q=rnd.nextInt(4);
                //p <--> q
                boolean temp=res[p];
                res[p]=res[q];
                res[q]=temp;
            }
        }
        //В res ровно br элементов =true. Вернем номера всех элементов = true.
        int[] nums=new int[br];
        int numsI=0;
        for (int i = 0; i < 4; i++) {
            if (res[i]) {
                nums[numsI++]=i;
            }
        }
        return nums;
    }


    /**
     * Возвращает double[] arr, arr.len=m, сумма всех элементов = p.
     * Пока решение простое: все arr[i]=p/m.
     * @param m int
     * @param p double
     * @return double[]
     */
    private static double[] spreadDens(int m, double p){
        double[] res=new double[m];
        //k1=k2=...=km=p/m
        double ki=p/m;
        for (int i = 0; i < m; i++) {
            res[i]=ki;
        }
        return res;
    }


    /**
     * Найти минимальное min=1..4 такое, что p/min<=1.
     * При m>=min, p/m<=p/min<=1.
     * 1) min>1, p/min<=1, p/(min-1)>1 ====>  min-1<p<=min
     * 2) min=1, p/1<=1, 0<p<=1.
     * min=округление p в большую сторону.
     * @param p double 0<p<=4
     * @return int
     */
    private static int findMinM(double p){
        if (p>=4) {
            //System.out.printf("findMinM: p=%f\n", p);
            return 4;
        } else {
            return (int)Math.ceil(p);
        }
    }

    private static int findFirstNotNull(Object[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]!=null) {
                return i;
            }
        }
        return -1;
    }


    private static int findFirstNotZERO(SFileMatrix[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (!arr[i].isZERO()) {
                return i;
            }
        }
        return -1;
    }


}
