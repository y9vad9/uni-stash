
package com.mathpar.matrix.file.dense;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.ops.MatrixOps;
import com.mathpar.matrix.file.ops.RandomParams;
import com.mathpar.matrix.file.utils.BaseMatrixDir;
import com.mathpar.matrix.file.utils.FileUtils;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import java.io.*;
import java.util.*;



/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2008</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class FileMatrix {

    /**
     * Путь к корню файловой матрицы.
     * Если глубина = 0, то это путь к файлу.
     * Если глубина > 0, то это путь к корневой директории дерева.
     */
    private File root;
    /**
     * Глубина дерева файловой матрицы.
     * Если глубина = 0, то это файл.
     * Если глубина > 0, то это дерево.
     */
    protected int depth;

    protected MatrixOps ops;


    private static int[][] rows={{0,1},{2,3}};
    private static int[][] cols={{0,2},{1,3}};


    protected FileMatrix(){}


    /**
     * Инициализирующий конструктор
     * @param root File
     * @param depth int
     */
    protected FileMatrix(File root, int depth){
        constructor(root,depth);
    }

    /**
     * Инициализирующий конструктор
     * @param root File
     * @param depth int
     */
    protected void constructor(File root, int depth){
        this.root=root;
        this.depth=depth;
    }


    /**
     * Создает файловую матрицу из корня. Глубина вычисляется автоматически.
     * @param root File
     */
    protected void constructor(File root){
        constructor(root, findDepth(root));
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
    protected void constructor(File root, int depth, Object m)
        throws IOException{
        constructor(root, depth);
        //создать директорию root, если глубина>0 и если не существует
        FileUtils.createDir(root, depth);
        //создать дерево
        construct(m, depth, root);
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
     * @param params RandomParams
     * @throws IOException
     */
    protected void constructor(File root, int depth, int m, int n,
                               RandomParams params, Ring ring)
        throws IOException{
        constructor(root, depth);
        //создать директорию root, если глубина>0 и если не существует
        FileUtils.createDir(root, depth);
        //создать дерево
        construct(depth, root, m, n, params, ring);
    }
    
    protected void constructor(File root, int depth, int m, int n, int nbits)
        throws IOException{
        constructor(root, depth);
        //создать директорию root, если глубина>0 и если не существует
        FileUtils.createDir(root, depth);
        //создать дерево
        construct(root,depth, m,n,nbits);
    }


    /**
     * Складывает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param m FileMatrix
     * @param to File
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    public FileMatrix add0(FileMatrix m, File to, long mod, Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        addRec(root, m.root, to, depth, mod, ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }

    public FileMatrix add0(FileMatrix m, File to,  Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        addRec(root, m.root, to, depth,  ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }
    
    public FileMatrix subtract0(FileMatrix m, File to,  Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        subtractRec(root, m.root, to, depth,  ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }
    
    
    /**
     * Вычитает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param m FileMatrix
     * @param to File
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix subtract0(FileMatrix m, File to, long mod, Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        subRec(root, m.root, to, depth, mod, ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }



    /**
     * Умножает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param m FileMatrix
     * @param to File
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    public FileMatrix multCU0(FileMatrix m, File to, long mod, Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        multCURec(root, m.root, to, depth, mod, ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }

    public FileMatrix multCU0(FileMatrix m, File to,  Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        multCURec(root, m.root, to, depth,  ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }


    /**
     * Копирует файловую матрицу this в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param to File
     * @throws IOException
     */
    public void copyOnly(File to)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево в to
        copyRec(root, to, depth, new byte[FileUtils.DEFAULT_BUF_SIZE]);
    }


    /**
     * Копирует файловую матрицу this в to. Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param to File
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix copy0(File to)
        throws IOException{
        copyOnly(to);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }



    /**
     * Перемещает файловую матрицу this в новое место to. Меняет файловую
     * матрицу this, чтобы она указывала на новое место.
     * @param to File
     * @throws IOException
     */
    public void move(File to)
        throws IOException{
        FileUtils.move(root, to);
        root=to;
    }


    /**
     * Копирует с отрицанием (to=-this) файловую матрицу this в to.
     * Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param to File
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix negate0(File to, long mod,Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево в to
        negateRec(root, to, depth,mod,ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
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
    protected FileMatrix joinMove0(FileMatrix[] matrs, boolean hasNulls, File to,Ring ring)
        throws IOException{
        if (hasNulls) {
            return joinCopyMoveNull(matrs, to, 2,ring);
        } else {
            return joinCopyMoveNotNull(matrs, to, 2);
        }
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
    protected FileMatrix joinCopy0(FileMatrix[] matrs, boolean hasNulls, File to,Ring ring)
        throws IOException{
        if (hasNulls) {
            return joinCopyMoveNull(matrs, to, 1,ring);
        } else {
            return joinCopyMoveNotNull(matrs, to, 1);
        }
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
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix ZERO0(int m, int n, int depth, File to,Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        constructZERO(m, n, depth, to,ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
    }


    /**
     * Записывает единичную матрицу n x n в файловую матрицу с корнем to,
     * глубиной depth.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param n int
     * @param depth int
     * @param to File
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix ONE0(int n, int depth, File to, Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        constructONE(n, depth, to, ring);
        //вернуть объект файловой матрицы
        return new FileMatrix(to, depth);
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
    protected void constructor(int depth, Object m)
        throws IOException{
        constructor(BaseMatrixDir.getRandomDir(),depth,m);
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
     * @param params RandomParams
     * @throws IOException
     */
    protected void constructor(int depth, int m,int n, RandomParams params, Ring ring)
        throws IOException{
        constructor(BaseMatrixDir.getRandomDir(),depth,m,n, params, ring);
    }


    /**
     * Складывает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param m FileMatrix
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    public FileMatrix add0(FileMatrix m, long mod, Ring ring)
        throws IOException{
        return add0(m,BaseMatrixDir.getRandomDir(),mod, ring);
    }


    /**
     * Вычитает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param m FileMatrix
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix subtract0(FileMatrix m, long mod, Ring ring)
        throws IOException{
        return subtract0(m,BaseMatrixDir.getRandomDir(),mod, ring);
    }




    /**
     * Умножает 2 файловые матрицы this и m глубины this.depth, результат
     * в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param m FileMatrix
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    public FileMatrix multCU0(FileMatrix m, long mod, Ring ring)
        throws IOException{
        return multCU0(m,BaseMatrixDir.getRandomDir(),mod, ring);
    }


    /**
     * Копирует файловую матрицу this в to. Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix copy0()
        throws IOException{
        return copy0(BaseMatrixDir.getRandomDir());
    }


    /**
     * Перемещает файловую матрицу в новое место to. Меняет файловую
     * матрицу this, чтобы она указывала на новое место.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @throws IOException
     */
    public void move()
        throws IOException{
        move(BaseMatrixDir.getRandomDir());
    }


    /**
     * Копирует с отрицанием (to=-this) файловую матрицу this в to.
     * Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param mod long
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix negate0(long mod,Ring ring)
        throws IOException{
        return negate0(BaseMatrixDir.getRandomDir(),mod,ring);
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
    protected FileMatrix joinMove0(FileMatrix[] matrs, boolean hasNulls,Ring ring)
        throws IOException{
        return joinMove0(matrs, hasNulls, BaseMatrixDir.getRandomDir(),ring);
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
    protected FileMatrix joinCopy0(FileMatrix[] matrs, boolean hasNulls,Ring ring)
        throws IOException{
        return joinCopy0(matrs, hasNulls, BaseMatrixDir.getRandomDir(),ring);
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
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix ZERO0(int m, int n, int depth,Ring ring)
        throws IOException{
        return ZERO0(m,n,depth,BaseMatrixDir.getRandomDir(),ring);
    }


    /**
     * Записывает единичную матрицу n x n в файловую матрицу с корнем to,
     * глубиной depth.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param n int
     * @param depth int
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix ONE0(int n, int depth, Ring ring)
        throws IOException{
        return ONE0(n,depth,BaseMatrixDir.getRandomDir(), ring);
    }


    //=======================================================================
    //============== Методы сохранения и восстановления =====================
    //=======================================================================



    private final static String MATR_DIR_NAME="matr";
    private final static String DESC_NAME="desc.txt";
    private final static String KEY_DEPTH="depth";
    private final static String KEY_CLASS="class";
    private final static String COMMENT="<==== FileMatrix info file ====>";



    protected void finalize0(){
        /*System.out.printf("finalize0: class='%s', root='%s', depth=%d\n",
                          this.getClass().getSimpleName(), root, depth);*/
        if (root!=null) {
            /*new Thread() {
                public void run() {
                    try {
                        FileMatrix.this.delete();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();*/
//            try {
//                delete();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
            root=null;
        }
    }


    /**
     * Оставляет файловую матрицу this.
     * Даже если ссылок на объект не будет
     * объект будет уничтожен, а дерево останется.
     */
    public void keep(){
        root=null;
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
    private FileMatrix restore(File dir)
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
        return new FileMatrix(new File(dir, MATR_DIR_NAME), depth);
    }



    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix restoreCopy0(File dir, File to)
        throws IOException{
        FileMatrix rest=restore(dir);
        FileMatrix copy=rest.copy0(to);
        return copy;
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo и копирует в to.
     * Возвращает файловую матрицу в to.
     * to -- директория с уникальным именем в базовой матричной директории.
     * @param dir File
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix restoreCopy0(File dir)
        throws IOException{
        return restoreCopy0(dir, BaseMatrixDir.getRandomDir());
    }

    /**
     * Восстанавливает файловую матрицу из директории dir, которая была
     * создана с помощью saveTo, перемещает в to и удаляет хранилище dir.
     * Возвращает файловую матрицу в to.
     * @param dir File
     * @param to File
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix restoreMove0(File dir, File to)
        throws IOException{
        //восстановить из dir
        FileMatrix rest=restore(dir);

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
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix restoreMove0(File dir)
        throws IOException{
        return restoreMove0(dir, BaseMatrixDir.getRandomDir());
    }


    //=======================================================================
    //========================= Другие методы ===============================
    //=======================================================================

    /**
     * Удаляет файловую матрицу вместе с корневой директорией.
     * @return boolean
     * @throws IOException
     */
    public boolean delete() throws IOException{
        if (root==null || !root.exists()) {
            return true;
        }
        return deleteRec(root, depth);
        //return acm.delete(root);
    }


    /**
     * Удаляет файловую матрицу с корнем root, глубиной depth.
     * Возвращает:
     * true -- успешно удалено,
     * false -- не успешно.
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
        } else {
            //глубина >0
            //вызвать рекурсивно данный метод для деревьев
            //с корнем в f0,...,f3 глубиной depth-1.
            boolean res=true;
            for (int i = 0; i < 4; i++) {
                res &= deleteRec(new File(root, i+""), depth-1);
            }
            //1) все поддеревья глубины depth=n удалены успешно, все true =>
            //=> res=true, root -- пустая директория.
            //2) некоторые поддеревья глубины depth=n не удалены, есть false =>
            //=> res=false, root -- НЕ пустая директория.
            res &= root.delete();
            return res;
        }
    }


    /**
     * this=-this.
     * @param mod long
     * @throws IOException
     */
    public void negateThis(long mod,Ring ring)
        throws IOException{
        negateThisRec(root, depth,mod,ring);
    }



    /**
     * Сравнивает 2 файловые матрицы.
     * Если равны, то возвращает true,
     * а если не равны, то false.
     * @param m FileMatrix
     * @param mod long
     * @return boolean
     * @throws IOException
     */
    public boolean equals(FileMatrix m, long mod, Ring ring)
        throws IOException{
        if (depth!=m.depth) {
            return false;
        }
        return equalsRec(root, m.root, depth, mod, ring);
    }


    /**
     * Преобразует файловую матрицу в матрицу в памяти. Файловая матрица
     * должна помещаться в памяти.
     * @return MatrixL
     * @throws IOException
     */
    protected Object toMatrix() throws IOException{
        return toMatrixRec(root,depth);
    }




    /**
     * Возвращает корень файловой матрицы.
     * @return File
     */
    public File getRoot(){
        return root;
    }

    /**
     * Возвращает глубину файловой матрицы.
     * @return int
     */
    public int getDepth(){
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
            //сделать шаг
            f=new File(f,"0");
        }
        //f -- файл
        int[] size=ops.getMatrSizeFromFile(f);
        return size;
    }

    /**
     * Вычисляет полный размер матрицы для файловой матрицы this.
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
     * Вычисляет глубину директории root.
     * @param root File
     * @return int
     */
    public static int findDepth(File root){
        File f=root;
        int depth=0;
        while (!f.isFile()) {
            //f - не файл
            //сделать шаг
            f=new File(f,"0");
            depth++;
        }
        //f -- файл
        return depth;
    }




    /**
     * Возвращает i-й подблок в виде файловой матрицы. Не копирует поддерево (подфайл).
     * Новая файловая матрица указывает на поддиректорию (подфайл).
     * @param i int [0..3]
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix getSubBlock0(int i)
        throws IOException{
        if (depth==0) {
            throw new IllegalArgumentException("depth=0");
        } else {
            //depth>0
            return new FileMatrix(new File(root, String.valueOf(i)), depth-1);
        }

    }



    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * @param i int
     * @param to File
     * @return FileMatrix
     * @throws IOException
     */
    protected FileMatrix getSubBlockCopy0(int i, File to)
        throws IOException{
        if (depth==0) {
            throw new IllegalArgumentException("depth=0");
        } else {
            //depth>0
            FileMatrix subBl=getSubBlock0(i);
            FileMatrix copy=subBl.copy0(to);
            return copy;
        }
    }



    /**
     * Разбить файловую матрицу на 4 подблока.
     * Возвращает массив из 4 файловых матриц, которые указывают на
     * 4 поддерева (подфайла).
     * @return FileMatrix[]
     * @throws IOException
     */
    protected FileMatrix[] split0()
        throws IOException{
        if (depth==0) {
            throw new IllegalArgumentException("depth=0");
        } else {
            FileMatrix[] fms=new FileMatrix[4];
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
     * После вызова пользоваться this НЕЛЬЗЯ.
     * @return FileMatrix[]
     * @throws IOException
     */
    protected FileMatrix[] splitMove0()
        throws IOException{
        if (depth == 0) {
            throw new IllegalArgumentException("depth=0");
        } else {
            FileMatrix[] subBls=split0();
            //переместить все поддеревья в некоторые случайные имена в matrsDir
            for (int i = 0; i < subBls.length; i++) {
                //перемещаем subBls[i] --> matrsDir, subBls[i] будет указывать
                //на новое место
                subBls[i].move();
            }
            //this=(null,0)
            //т.к. глубина this >0, то удалить пустую корневую директорию root.
            root.delete();
            root=null;
            depth=0;
            return subBls;
        }
    }


    /**
     * Разбить файловую матрицу на 4 подблока с копированием.
     * Все 4 подблока (файловые матрицы) копируются
     * в случайные имена в базовой директории.
     * Возвращается массив из 4 файловых матриц в новых местах.
     * После вызова this НЕ МЕНЯЕТСЯ.
     * @return FileMatrix[]
     * @throws IOException
     */
    protected FileMatrix[] splitCopy0()
        throws IOException{
        if (depth == 0) {
            throw new IllegalArgumentException("depth=0");
        } else {
            FileMatrix[] subBls=split0();
            FileMatrix[] matrs=new FileMatrix[4];
            //копировать все поддеревья в некоторые случайные имена в matrsDir
            for (int i = 0; i < subBls.length; i++) {
                //копируем subBls[i] --> matrsDir
                matrs[i]=subBls[i].copy0();
            }
            return matrs;
        }
    }


    //======================== Private methods =======================


    /**
     * op=1 -- copy
     * Объединить 4 файловые матрицы с копированием в to.
     * Файловые матрицы в массиве будут указывать на старые места.
     * или:
     * op=2 -- move
     * Объединить 4 файловые матрицы с перемещением в to.
     * Файловые матрицы в массиве будут указывать на поддеревья в to.
     * @param matrs FileMatrix[]
     * @param to File
     * @param op int
     * @return FileMatrix
     * @throws IOException
     */
    private static FileMatrix joinCopyMoveNotNull(FileMatrix[] matrs, File to, int op)
        throws IOException{
        FileUtils.mkdirs(to);
        int depth=matrs[0].depth;
        for (int i = 0; i < matrs.length; i++) {
            if (op == 1) {
                //op=1 -- копировать
                //matrs[i] --> (to,i), будет указывать на старое место
                matrs[i].copyOnly(new File(to, i + ""));
            } else {
                //op=2 -- перемещать
                //matrs[i] --> (to,i), будет указывать на (to,i)
                matrs[i].move(new File(to, i + ""));
            }
        }
        return new FileMatrix(to,depth+1);
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
     * Если matrs[i]=null, то i-й подблок to будет нулевым.
     * @param matrs FileMatrix[]
     * @param to File
     * @param op int
     * @return FileMatrix
     * @throws IOException
     */
    private FileMatrix joinCopyMoveNull(FileMatrix[] matrs, File to, int op,Ring ring)
        throws IOException{
        FileUtils.mkdirs(to);
        int pos=findFirstNotNull(matrs);
        if (pos==-1) {
            throw new IllegalArgumentException("All matrs[i]==null. Depth undefined.");
        }
        //pos>=0
        int depth=matrs[pos].depth;
        int[] size=matrs[pos].getFullSize();
        int m=size[0];
        int n=size[1];
        for (int i = 0; i < matrs.length; i++) {
            if (matrs[i]!=null) {
                if (op==1) {
                    //op=1 -- копировать
                    //matrs[i] --> (to,i), будет указывать на старое место
                    matrs[i].copyOnly(new File(to,i+""));
                } else {
                    //op=2 -- перемещать
                    //matrs[i] --> (to,i), будет указывать на (to,i)
                    matrs[i].move(new File(to,i+""));
                }
            } else {
                //=null, ZERO m x n ---> (to,i), глубиной=depth
                FileMatrix zero=ZERO0(m,n,depth,new File(to,i+""),ring);
                //zero.keep();
            }
        }
        return new FileMatrix(to,depth+1);
    }


    /**
     * Конструирует:
     * дерево для матрицы типа MatrixX глубиной depth>0 с корнем в директории root
     * (корневая директория root должна быть создана до вызова данного метода)
     * или
     * файл для матрицы типа MatrixX глубиной depth=0 с путем равным root.
     * @param m MatrixL
     * @param depth int
     * @param root File
     * @throws IOException
     */
    private void construct(Object m, int depth, File root)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            ops.writeMatrToFile(m, root);
        } else {
            //глубина >0
            //разбить матрицу m на 4 подблока: mb0, mb1, mb2, mb3.
            Object[] mb=ops.split(m);
            //вызвать рекурсивно данный метод для создания деревьев
            //для mb0,...,mb3 с корнем в f0,...,f3 глубиной depth-1.
            for (int i = 0; i < 4; i++) {
                File fi=new File(root, String.valueOf(i));
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                construct(mb[i], depth-1, fi);
            }
        }
    }


    /**
     * 1) Если depth>0, то конструирует дерево с корнем в root, глубиной depth
     * для случайной матрицы m x n.<br>
     * (корневая директория root должна быть создана до вызова данного метода)
     * 2) Если depth=0, то конструирует файл с путем root для случайной матрицы m x n.
     * @param depth int
     * @param root File
     * @param m int
     * @param n int
     * @param params RandomParams
     * @throws IOException
     */
    private void construct(int depth, File root, int m, int n,
                           RandomParams params, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object matr=ops.random(m,n,params,ring);
            ops.writeMatrToFile(matr, root);
        } else {
            //глубина >0
            //вызвать рекурсивно данный метод для создания деревьев
            //с корнем в f0,...,f3 глубиной depth-1.
            for (int i = 0; i < 4; i++) {
                File fi=new File(root, String.valueOf(i));
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                construct(depth-1, fi, m/2,n/2, params, ring);
            }
        }
    }
    
    private void construct(File root, int depth, int m, int n, int nbits) throws IOException{
        if (depth==0) {
            //глубина = 0
            Object matr=ops.random(m,n,nbits);
            ops.writeMatrToFile(matr, root);
        } else {
            //глубина >0
            //вызвать рекурсивно данный метод для создания деревьев
            //с корнем в f0,...,f3 глубиной depth-1.
            for (int i = 0; i < 4; i++) {
                File fi=new File(root, String.valueOf(i));
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                construct(fi,depth-1,m/2,n/2,nbits);
            }
        }
    }



    private void constructZERO(int m, int n, int depth, File root,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object matr=ops.zero(m,n,ring);
            ops.writeMatrToFile(matr, root);
        } else {
            //глубина >0
            //вызвать рекурсивно данный метод для создания деревьев
            //с корнем в f0,...,f3 глубиной depth-1.
            for (int i = 0; i < 4; i++) {
                File fi=new File(root, String.valueOf(i));
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                constructZERO(m/2,n/2, depth-1, fi,ring);
            }
        }
    }


    private void constructONE(int n, int depth, File root, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object matr=ops.one(n,ring);
            ops.writeMatrToFile(matr, root);
        } else {
            //глубина >0
            //вызвать рекурсивно данный метод для создания деревьев
            //с корнем в f0,...,f3 глубиной depth-1.
            for (int i = 0; i < 4; i++) {
                File fi=new File(root, String.valueOf(i));
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                if (i==0 || i==3) {
                    constructONE(n/2, depth-1, fi, ring);
                } else {
                    constructZERO(n/2, n/2, depth-1, fi,ring);
                }
            }
        }
    }



    /**
     * 1) Если depth>0, то складывает 2 дерева глубиной depth с корнем в r1 и r2
     * и получает результирующее дерево с корнем в to. (директория to должна
     * существовать до вызова этого метода!)<br>
     * 2) Если depth=0, то складывает 2 файла r1 и r2 и результат пишет в to.
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @throws IOException
     */
    private void addRec(File r1, File r2, File to, int depth, long mod, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.add(a,b, mod, ring);
            ops.writeMatrToFile(c, to);
        } else {
            //глубина >0
            for (int i = 0; i < 4; i++) {
                File fi = new File(to, i+"");
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                //вызвать рекурсивно данный метод для создания деревьев
                //с корнем в f0,...,f3 глубиной depth-1.
                //r1/0+r2/0 -> f0
                //r1/1+r2/1 -> f1
                //r1/2+r2/2 -> f2
                //r1/3+r2/3 -> f3
                addRec(new File(r1,i+""), new File(r2,i+""), fi, depth-1, mod, ring);
            }
        }
    }
    
   
    
    private void addRec(File r1, File r2, File to, int depth,  Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.add(a,b, ring);
            ops.writeMatrToFile(c, to);
        } else {
            //глубина >0
            for (int i = 0; i < 4; i++) {
                File fi = new File(to, i+"");
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                //вызвать рекурсивно данный метод для создания деревьев
                //с корнем в f0,...,f3 глубиной depth-1.
                //r1/0+r2/0 -> f0
                //r1/1+r2/1 -> f1
                //r1/2+r2/2 -> f2
                //r1/3+r2/3 -> f3
                addRec(new File(r1,i+""), new File(r2,i+""), fi, depth-1,  ring);
            }
        }
    }
    
    private void subtractRec(File r1, File r2, File to, int depth,  Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.subtract(a,b, ring);
            ops.writeMatrToFile(c, to);
        } else {
            //глубина >0
            for (int i = 0; i < 4; i++) {
                File fi = new File(to, i+"");
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                //вызвать рекурсивно данный метод для создания деревьев
                //с корнем в f0,...,f3 глубиной depth-1.
                //r1/0+r2/0 -> f0
                //r1/1+r2/1 -> f1
                //r1/2+r2/2 -> f2
                //r1/3+r2/3 -> f3
                subtractRec(new File(r1,i+""), new File(r2,i+""), fi, depth-1,  ring);
            }
        }
    }

   


    /**
     * 1) Если depth>0, то вычитает 2 дерева глубиной depth с корнем в r1 и r2
     * и получает результирующее дерево с корнем в to. (директория to должна
     * существовать до вызова этого метода!)<br>
     * 2) Если depth=0, то вычитает 2 файла r1 и r2 и результат пишет в to.
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @throws IOException
     */
    private void subRec(File r1, File r2, File to, int depth, long mod, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.subtract(a,b, mod,ring);
            ops.writeMatrToFile(c, to);
        } else {
            //глубина >0
            for (int i = 0; i < 4; i++) {
                File fi = new File(to, i + "");
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до рекурсивного вызова данного метода.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                //вызвать рекурсивно данный метод для создания деревьев
                //с корнем в f0,...,f3 глубиной depth-1.
                //r1/0-r2/0 -> f0
                //r1/1-r2/1 -> f1
                //r1/2-r2/2 -> f2
                //r1/3-r2/3 -> f3
                subRec(new File(r1, i + ""), new File(r2, i + ""), fi,
                       depth - 1, mod,ring);
            }
        }
    }



    /**
     * 1) Если depth>0, то умножает 2 дерева глубиной depth с корнем в r1 и r2
     * и получает результирующее дерево с корнем в to. (директория to должна
     * существовать до вызова этого метода!)<br>
     * 2) Если depth=0, то умножает 2 файла r1 и r2 и результат пишет в to.
     * @param r1 File
     * @param r2 File
     * @param to File
     * @param depth int
     * @param mod long
     * @throws IOException
     */
    private void multCURec(File r1, File r2, File to, int depth, long mod, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //c=a*b (mod)
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.multCU(a,b, mod, ring);
            ops.writeMatrToFile(c, to);
        } else {
            //глубина >0
            for (int i = 0; i < 4; i++) {
                File fi = new File(to, i + "");
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до вызова multRowCol.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                //вызвать рекурсивно данный метод для создания деревьев
                //с корнем в f0,...,f3 глубиной depth-1.
                int rNum=i>>>1;
                int cNum=i&1;
                int[] r=rows[rNum];
                int[] c=cols[cNum];
                multRowCol(r1, r[0], r[1], r2, c[0], c[1], to, i, depth-1, mod, ring);
            }
        }
    }

    private void multCURec(File r1, File r2, File to, int depth, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //c=a*b (mod)
            Object a=ops.readMatrFromFile(r1);
            Object b=ops.readMatrFromFile(r2);
            Object c=ops.multCU(a,b, ring);
            ops.writeMatrToFile(c, to);
        } else {
            //глубина >0
            for (int i = 0; i < 4; i++) {
                File fi = new File(to, i + "");
                //Если глубина=1, то дочерние вершины -- файлы и их не нужно создавать,
                //а если глубина>1, то дочерние вершины -- директории и их нужно
                //создать до вызова multRowCol.
                if (depth > 1) {
                    FileUtils.mkdir(fi);
                }
                //вызвать рекурсивно данный метод для создания деревьев
                //с корнем в f0,...,f3 глубиной depth-1.
                int rNum=i>>>1;
                int cNum=i&1;
                int[] r=rows[rNum];
                int[] c=cols[cNum];
                multRowCol(r1, r[0], r[1], r2, c[0], c[1], to, i, depth-1,  ring);
            }
        }
    }

    /**
     * Умножает строку в r1 на столбец в r2. <br>
     *  <br>
     * Обозначение: <br>
     * (ROOT, dir) -- в корневой директории ROOT, поддиректория dir. <br>
     *  <br>
     * Метод выполняет: (to,e) = (r1,a)*(r2,c) + (r1,b)*(r2,d) <br>
     * Подробно:<br>
     * (r1,a) * (r2,c) -> (to,d1) <br>
     * (r1,b) * (r2,d) -> (to,d2) <br>
     * (to,d1) + (to,d2) -> (to,e) <br>
     * delete (to,d1) <br>
     * delete (to,d2) <br>
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
     * @throws IOException
     */
    private void multRowCol(File r1, int a, int b,
                            File r2, int c, int d,
                            File to, int e,
                            int depth, long mod, Ring ring)
        throws IOException{
        File r1a=new File(r1, String.valueOf(a));
        File r1b=new File(r1, String.valueOf(b));

        File r2c=new File(r2, String.valueOf(c));
        File r2d=new File(r2, String.valueOf(d));

        File tod1=new File(to, "d1");
        File tod2=new File(to, "d2");
        File toe=new File(to, String.valueOf(e));
        if (depth>0) {
            FileUtils.mkdir(tod1);
            FileUtils.mkdir(tod2);
        }

        //(r1,a) * (r2,c) -> (to,d1)
        multCURec(r1a, r2c, tod1, depth, mod, ring);
        //(r1,b) * (r2,d) -> (to,d2)
        multCURec(r1b, r2d, tod2, depth, mod, ring);
        //(to,d1) + (to,d2) -> (to,e)
        addRec(tod1, tod2, toe, depth, mod, ring);
        //delete (to,d1)
        deleteRec(tod1, depth);
        //delete (to,d2)
        deleteRec(tod2, depth);
    }
    
    private void multRowCol(File r1, int a, int b,
                            File r2, int c, int d,
                            File to, int e,
                            int depth,  Ring ring)
        throws IOException{
        File r1a=new File(r1, String.valueOf(a));
        File r1b=new File(r1, String.valueOf(b));

        File r2c=new File(r2, String.valueOf(c));
        File r2d=new File(r2, String.valueOf(d));

        File tod1=new File(to, "d1");
        File tod2=new File(to, "d2");
        File toe=new File(to, String.valueOf(e));
        if (depth>0) {
            FileUtils.mkdir(tod1);
            FileUtils.mkdir(tod2);
        }

        //(r1,a) * (r2,c) -> (to,d1)
        multCURec(r1a, r2c, tod1, depth,  ring);
        //(r1,b) * (r2,d) -> (to,d2)
        multCURec(r1b, r2d, tod2, depth, ring);
        //(to,d1) + (to,d2) -> (to,e)
        addRec(tod1, tod2, toe, depth,  ring);
        //delete (to,d1)
        deleteRec(tod1, depth);
        //delete (to,d2)
        deleteRec(tod2, depth);
    }




    private Object toMatrixRec(File root, int depth) throws IOException{
        if (depth==0) {
            //глубина = 0
            //прочитать из файла матрицу
            Object m=ops.readMatrFromFile(root);
            return m;
        } else {
            //глубина >0
            Object[] matrs=new Object[4];
            //вызвать рекурсивно данный метод для деревьев
            //с корнем в f0,...,f3 глубиной depth-1.
            for (int i = 0; i < 4; i++) {
                matrs[i]=toMatrixRec(new File(root, i + ""),depth-1);
            }
            Object res=ops.join(matrs);
            return res;
        }
    }



    private boolean equalsRec(File r1, File r2, int depth, long mod, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object m1=ops.readMatrFromFile(r1);
            Object m2=ops.readMatrFromFile(r2);
            return ops.eqMatrs(m1,m2,mod, ring);
        } else {
            //глубина > 0
            for (int i = 0; i < 4; i++) {
                if (!equalsRec(new File(r1,i+""), new File(r2,i+""), depth-1,mod, ring)) {
                    return false;
                }
            }
            return true;
        }
    }




    private static void copyRec(File root, File to, int depth, byte[] buf)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            //root --> to
            FileUtils.copyFile(root,to, buf);
        } else {
            //глубина > 0
            for (int i = 0; i < 4; i++) {
                File toi=new File(to,i+"");
                if (depth>1) {
                    FileUtils.mkdir(toi);
                }
                copyRec(new File(root,i+""), toi, depth-1, buf);
            }
        }
    }



    private void negateRec(File root, File to, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object m=ops.readMatrFromFile(root);
            Object m2=ops.negate(m,mod,ring);
            ops.writeMatrToFile(m2, to);
        } else {
            //глубина > 0
            for (int i = 0; i < 4; i++) {
                File toi=new File(to,i+"");
                if (depth>1) {
                    FileUtils.mkdir(toi);
                }
                negateRec(new File(root,i+""), toi, depth-1,mod,ring);
            }
        }
    }


    private void negateThisRec(File root, int depth, long mod,Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object m=ops.readMatrFromFile(root);
            Object m2=ops.negate(m,mod,ring);
            ops.writeMatrToFile(m2, root);
        } else {
            //глубина > 0
            for (int i = 0; i < 4; i++) {
                negateThisRec(new File(root,i+""), depth-1, mod,ring);
            }
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



}
