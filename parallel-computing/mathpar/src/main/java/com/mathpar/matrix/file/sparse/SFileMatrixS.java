
package com.mathpar.matrix.file.sparse;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import com.mathpar.matrix.file.ops.RandomParams;
import com.mathpar.matrix.file.ops.MatrixOpsS;
import com.mathpar.matrix.file.ops.MatrixOps;
import com.mathpar.matrix.MatrixS;

import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SFileMatrixS extends SFileMatrix {

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
   public static SFileMatrixS ZERO=new SFileMatrixS((File)null,0, NumberZ.ZERO, new Ring(Ring.Z, 1));




    /**
     * Конструктор
     */
    public SFileMatrixS(Element elem, Ring ring){
        ops=new MatrixOpsS(elem, ring);
        elm=elem;

    }





    /**
     * Присваивающий конструктор.
     * @param root File
     * @param depth int
     */
    public SFileMatrixS(File root, int depth, Element elem,Ring ring){
        this(elem,ring);
        constructor(root,depth);
    }

     /**
     * Присваивающий конструктор.
     * @param root File
     * @param depth int
     */
    public SFileMatrixS(File root, int depth){
        ops=new MatrixOpsS();
        constructor(root,depth);
    }



    /**
     * Присваивающий конструктор.
     * @param root File
     * @param depth int
     */
    public SFileMatrixS(File root, int depth, MatrixOps ops){
        this.root=root;
        this.depth=depth;
        this.ops=ops;
    }


    /**
     * Создать разреженную файловую матрицу из корня с удалением нулей (если clear=true).
     * @param root File корень директории (плотной или разреженной)
     * @param clear boolean =true, то будет очистка нулей, =false - не будет.
     * @throws IOException
     */
    public SFileMatrixS(File root, boolean clear, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(root,-1,ring,clear);
    }



    /**
     * Создает из плотной файловой матрицы разреженную файловую матрицу с
     * корнем to.
     * @param m FileMatrixS
     * @param to File
     * @throws IOException
     */
/*    public SFileMatrixS(FileMatrixS m, File to)
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
    public SFileMatrixS(int depth, File root, int m, int n,
                        double dden, Random rnd, int[] randomType, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(depth,root,m,n,new RandomParams(-1,dden,-1,-1,null,rnd,randomType),ring);
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
    public SFileMatrixS(int[][] paths, int m, int n,
                        int den, Random rnd, File root, int[] randomType, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(paths,m,n,new RandomParams(den,-1,-1,-1,null,rnd, randomType),root,ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.
     *
     * @param paths int[][]
     * @param matrs MatrixS[]
     * @param root File
     * @throws IOException
     */
    public SFileMatrixS(int[][] paths, MatrixS[] matrs, File root, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(paths,matrs,root,ring);
    }


    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * @param depth int
     * @param root File
     * @param m MatrixS
     * @throws IOException
     */
    public SFileMatrixS(int depth, File root, MatrixS m, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(depth,root,m,-1,ring);
    }


    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * @param root File
     * @param depth int
     * @param n int
     * @return SFileMatrixS
     * @throws IOException
     */
    public static SFileMatrixS ONE(File root, int depth, int n, Element elem,Ring ring)
        throws IOException{
        return toS(new SFileMatrixS(elem,ring).ONE0(root,depth,n,ring), new MatrixOpsS(elem,ring));
    }


    public static SFileMatrixS ONEmultD(File root, int depth, Element d, int n, Element elem,Ring ring)
        throws IOException{
        return toS(new SFileMatrixS(elem,ring).ONEmultD0(root,depth,d,n,ring), new MatrixOpsS(elem,ring));
    }



    /**
     * Складывает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * Если глубина = 0, то складывает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то складывает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this=0 и m=0, то результат = 0. Если ровно одна из файловых матриц =0,
     * то результатом будет другая матрица, скопированная в to.<br>
     * @param m SFileMatrixS
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS add(SFileMatrixS m, File to,Ring ring)
        throws IOException{
        return toS(add0(m,to,-1,ring),ops);
    }

    @Override
    public <T extends SFileMatrix> T addForParalell(T fm, long mod, Ring ring)  throws IOException{
        return (T) toS(add0(fm,mod,ring),ops);
    }
    public <T extends SFileMatrix> T addForParalell(T fm, File to, long mod, Ring ring)  throws IOException {
       return (T) toS(add0(fm,to,mod,ring),ops);
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
     * @param m SFileMatrixS
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS subtract(SFileMatrixS m, File to,Ring ring)
        throws IOException{
        return toS(subtract0(m,to,-1,ring),ops);
    }



    /**
     * Умножает файловые матрицы this и m. Результат
     * записывает в to.<br>
     * Если глубина = 0, то умножает файлы и результат пишет в файл to.<br>
     * Если глубина > 0, то умножает деревья и результатом будет дерево
     * с корнем в to.<br>
     * Если this==0 или m==0, то результат = 0.<br>
     * @param m SFileMatrixS
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS multiplyRecursive(SFileMatrixS m, File to,Ring ring)
        throws IOException{
        return toS(multCU0(m,to,-1,ring),ops);
    }


    public SFileMatrixS multiplyDivRecursive(SFileMatrix m, Element div, File to,Ring ring)
        throws IOException{
        return toS(multiplyDivRecursive0(m,div,to,-1,ring),ops);
    }

    public SFileMatrixS multiplyDivMulRecursive(SFileMatrix m, Element div, Element mult, File to,Ring ring)
        throws IOException{
        return toS(multiplyDivMulRecursive0(m,div,mult,to,-1,ring),ops);
    }



    public SFileMatrixS multiplyLeftE(int[] Ei, int[] Ej, File to,Ring ring)
        throws IOException{
        return toS(multCUEL0(Ei,Ej,to,-1,ring),ops);
    }


    public SFileMatrixS multiplyLeftI(int[] Ei, File to,Ring ring)
        throws IOException{
        return toS(multCUIL0(Ei,to,-1,ring),ops);
    }



    public SFileMatrixS multiplyByNumber(Element mult, File to,Ring ring)
        throws IOException{
        return toS(multiplyByNumber0(mult,to,-1,ring),ops);
    }

    @Override
    public <T extends SFileMatrix> T multiplyByNumber0Paralell(Element mult, File to, long mod, Ring ring) throws IOException {
        return (T) multiplyByNumber(mult, to,   ring); //To change body of generated methods, choose Tools | Templates.
    }




    public SFileMatrixS divideByNumber(Element div, File to,Ring ring)
        throws IOException{
        return toS(divideByNumber0(div,to,-1,ring),ops);
    }



    public SFileMatrixS multiplyDivide(Element mult, Element div, File to,Ring ring)
        throws IOException{
        return toS(multiplyDivide0(mult, div,to,-1,ring),ops);
    }

    public SFileMatrixS divideMultiply(Element div, Element mult, File to,Ring ring)
        throws IOException{
        return toS(divideMultiply0(div, mult, to,-1,ring),ops);
    }


    public SFileMatrixS  ES_min_dI(Element d, int[] Ei, int[] Ej, int depth, int n, File root,Ring ring)
        throws IOException{
        return toS(ES_min_dI0(d,Ei,Ej,root,depth,n,-1,ring),ops);
    }




    /**
     * Копирует файловую матрицу в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     *
     * @param to File новый путь к корню
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS copy(File to) throws IOException{
        return toS(copy0(to),ops);
    }



    /**
     * Делает this=-this. Меняет файл или дерево this.
     * @throws IOException
     */
    public void negateThis(Ring ring) throws IOException{
        negateThis(-1,ring);
    }


    /**
     * Создает новую файловую матрицу равную (-this) в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS negate(File to,Ring ring) throws IOException{
        return toS(negate0(to,-1,ring),ops);
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs SFileMatrixS[]
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS joinMove(SFileMatrixS[] matrs, File to)
        throws IOException{
        return toS(joinMove0(matrs,to),ops);
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * @param matrs SFileMatrixS[]
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS joinCopy(SFileMatrixS[] matrs, File to)
        throws IOException{
        return toS(joinCopy0(matrs,to),ops);
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * @param i int
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS getSubBlockCopy(int i, File to)
        throws IOException{
        return toS(getSubBlockCopy0(i,to),ops);
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
     * @param m FileMatrixS
     * @throws IOException
     */
/*    public SFileMatrixS(FileMatrixS m)
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
    public SFileMatrixS(int depth, int m, int n,
                        double dden, Random rnd, int[] randomType, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(depth,m,n,new RandomParams(-1,dden,-1,-1,null,rnd, randomType),ring);
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
    public SFileMatrixS(int[][] paths, int m, int n,
                        int den, Random rnd, int nbits, int[] randomType, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(paths,m,n,new RandomParams(den,-1,-1,-1,null,rnd,randomType),ring);
    }


    /**
     * Генерирует разреженную файловую матрицу с фиксированной структурой
     * и фиксированными матрицами на концах. Результат
     * записывает в root.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @param paths int[][]
     * @param matrs MatrixS[]
     * @throws IOException
     */
    public SFileMatrixS(int[][] paths, MatrixS[] matrs, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(paths,matrs,ring);
    }

    /**
     * Создает разреженную файловую матрицу (root,depth) из матрицы в памяти m.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param m MatrixS
     * @throws IOException
     */
    public SFileMatrixS(int depth, MatrixS m, Element elem,Ring ring)
        throws IOException{
        this(elem,ring);
        constructor(depth,m,-1,ring);
    }


    /**
     * Единичную матрицу n x n записывает в файловую матрицу root, глубиной depth.
     * root - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param depth int
     * @param n int
     * @return SFileMatrixS
     * @throws IOException
     */
    public static SFileMatrixS ONE(int depth, int n, Element elem,Ring ring)
        throws IOException{
        return toS(new SFileMatrixS(elem,ring).ONE0(depth,n,ring), new MatrixOpsS(elem,ring));
    }

    public static SFileMatrixS ONEmultD(int depth, Element d, int n, Element elem,Ring ring)
        throws IOException{
        return toS(new SFileMatrixS(elem,ring).ONEmultD0(depth,d,n,ring), new MatrixOpsS(elem,ring));
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
     * @param m SFileMatrixS
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS add(SFileMatrixS m,Ring ring)
        throws IOException{
        return toS(add0(m,-1,ring),ops);
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
     * @param m SFileMatrixS
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS subtract(SFileMatrixS m,Ring ring)
        throws IOException{
        return toS(subtract0(m,-1,ring),ops);
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
     * @param m SFileMatrixS
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS multiplyRecursive(SFileMatrixS m,Ring ring)
        throws IOException{
        return toS(multCU0(m,-1,ring),ops);
    }


    @Override
    public <T extends SFileMatrix> T multiplyForParalell(T fm, long mod, Ring ring)  throws IOException{
        SFileMatrixS fileMatrixL = (SFileMatrixS) fm;
        return (T) multiplyRecursive(fileMatrixL, ring); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends SFileMatrix> T multiplyForParalell(T fm, File to, long mod, Ring ring)  throws IOException{
        SFileMatrixS fileMatrixL = (SFileMatrixS) fm;
        return (T) multiplyRecursive(fileMatrixL, to, ring); //To change body of generated methods, choose Tools | Templates.
    }


    public SFileMatrixS multiplyDivRecursive(SFileMatrix m, Element div,Ring ring)
        throws IOException{
        return toS(multiplyDivRecursive0(m,div,-1,ring),ops);
    }

    public SFileMatrixS multiplyDivMulRecursive(SFileMatrix m, Element div, Element mult,Ring ring)
        throws IOException{
        return toS(multiplyDivMulRecursive0(m,div,mult,-1,ring),ops);
    }



    public SFileMatrixS multiplyLeftE(int[] Ei, int[] Ej,Ring ring)
        throws IOException{
        return toS(multCUEL0(Ei,Ej,-1,ring),ops);
    }



    public SFileMatrixS multiplyLeftI(int[] Ei,Ring ring)
        throws IOException{
        return toS(multCUIL0(Ei,-1,ring),ops);
    }




    public SFileMatrixS multiplyByNumber(Element mult,Ring ring)
        throws IOException{
        return toS(multiplyByNumber0(mult,-1,ring),ops);
    }



    public SFileMatrixS divideByNumber(Element div,Ring ring)
        throws IOException{
        return toS(divideByNumber0(div,-1,ring),ops);
    }



    public SFileMatrixS multiplyDivide(Element mult, Element div,Ring ring)
        throws IOException{
        return toS(multiplyDivide0(mult, div,-1,ring),ops);
    }

    public SFileMatrixS divideMultiply(Element div, Element mult,Ring ring)
        throws IOException{
        return toS(divideMultiply0(div, mult, -1,ring),ops);
    }


    public SFileMatrixS  ES_min_dI(Element d, int[] Ei, int[] Ej, int depth, int n,Ring ring)
        throws IOException{
        //System.out.println("-------"+d+","+depth+","+n+","+this.toMatrixS());
        return toS(ES_min_dI0(d,Ei,Ej,depth,n,-1,ring),ops);
    }


    /**
     * Копирует файловую матрицу в новое место to и возвращает новую
     * файловую матрицу с корнем to.<br>
     * Если глубина=0, то копирует файл в to.<br>
     * Если глубина>0, то копирует дерево матрицы в to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     *
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS copy() throws IOException{
        return toS(copy0(),ops);
    }



    /**
     * Создает новую файловую матрицу равную (-this) в
     * новом месте to и возвращает новую файловую матрицу с корнем to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS negate(Ring ring) throws IOException{
        return toS(negate0(-1,ring),ops);
    }



    /**
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs SFileMatrixS[]
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS joinMove(SFileMatrixS[] matrs)
        throws IOException{
        return toS(joinMove0(matrs),ops);
    }



    /**
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param matrs SFileMatrixS[]
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS joinCopy(SFileMatrixS[] matrs)
        throws IOException{
        return toS(joinCopy0(matrs),ops);
    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * to - это файл или директория с уникальным именем
     * в базовой матричной директории.<br>
     * @param i int
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS getSubBlockCopy(int i)
        throws IOException{
        return toS(getSubBlockCopy0(i),ops);
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
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS restoreCopy(File dir, File to)
        throws IOException{
        return toS(restoreCopy0(dir,to),ops);
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS restoreCopy(File dir)
        throws IOException{
        return toS(restoreCopy0(dir),ops);
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS restoreMove(File dir, File to)
        throws IOException{
        return toS(restoreMove0(dir,to),ops);
    }


    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS restoreMove(File dir)
        throws IOException{
        return toS(restoreMove0(dir),ops);
    }


    //=======================================================================
    //========================= Другие методы ===============================
    //=======================================================================

    /**
     * Преобразует файловую матрицу в матрицу в памяти. Файловая матрица
     * должна помещаться в памяти.
     * @return MatrixS
     * @throws IOException
     */
    public MatrixS toMatrixS() throws IOException{
        return (MatrixS)toMatrix();
    }



    /**
     * Сравнивает 2 файловые матрицы. Если равны, то возвращает true, если
     * не равны -- false.<br>
     *
     * @param m SFileMatrixS
     * @return boolean
     * @throws IOException
     */
    public boolean equals(SFileMatrixS m,Ring ring) throws IOException{
        return equals(m,-1,ring);
    }


    /**
     * Возвращает i-й подблок в виде файловой матрицы. Не копирует поддерево (подфайл).
     * Новая файловая матрица указывает на поддиректорию (подфайл).
     * @param i int [0..3]
     * @return SFileMatrixS
     * @throws IOException
     */
    public SFileMatrixS getSubBlock(int i)
        throws IOException{
        return toS(getSubBlock0(i),ops);
    }



    /**
     * Разбить файловую матрицу на 4 подблока.
     * Возвращает массив из 4 файловых матриц, которые указывают на
     * 4 поддерева (подфайла).
     * @return SFileMatrixS[]
     * @throws IOException
     */
    public SFileMatrixS[] split()
        throws IOException{
        return toArrS(split0(),ops);
    }



    public SFileMatrixS[] splitNoNulls()
        throws IOException{
        return toArrSNoNulls(split0(),ops);
    }


    /**
     * Разбить файловую матрицу на 4 подблока с перемещением.
     * Все 4 подблока (файловые матрицы) перемещаются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this=0.
     * @return SFileMatrixS[]
     * @throws IOException
     */
    public SFileMatrixS[] splitMove()
        throws IOException{
        return toArrS(splitMove0(),ops);
    }



    /**
     * Разбить файловую матрицу на 4 подблока с копированием.
     * Все 4 подблока (файловые матрицы) копируются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this НЕ МЕНЯЕТСЯ.
     * @return SFileMatrixS[]
     * @throws IOException
     */
    public SFileMatrixS[] splitCopy()
        throws IOException{
        return toArrS(splitCopy0(),ops);
    }



    //======================== Private methods =======================

    private static SFileMatrixS toS(SFileMatrix fm, MatrixOps ops){
        return new SFileMatrixS(fm.getRoot(),fm.getDepth(),ops);
    }


    //могут быть null, т.е. fmArr[i]=null
    private static SFileMatrixS[] toArrS(SFileMatrix[] fmArr, MatrixOps ops){
        //n=4
        int n=fmArr.length;
        SFileMatrixS[] fmArr2=new SFileMatrixS[n];
        for (int i = 0; i < n; i++) {
            if (fmArr[i]!=null) {
                fmArr2[i]=toS(fmArr[i],ops);
            }
        }
        return fmArr2;
    }


    //могут быть null, т.е. fmArr[i]=null
    //null-->ZERO
    private static SFileMatrixS[] toArrSNoNulls(SFileMatrix[] fmArr, MatrixOps ops){
        //n=4
        int n=fmArr.length;
        SFileMatrixS[] fmArr2=new SFileMatrixS[n];
        for (int i = 0; i < n; i++) {
            if (fmArr[i]!=null) {
                fmArr2[i]=toS(fmArr[i],ops);
            } else {
                fmArr2[i]=SFileMatrixS.ZERO;
            }
        }
        return fmArr2;
    }


}
