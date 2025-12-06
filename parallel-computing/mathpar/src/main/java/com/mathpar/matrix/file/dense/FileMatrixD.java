
package  com.mathpar.matrix.file.dense;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.dm.MatrixL;
import  com.mathpar.matrix.file.ops.*;
import com.mathpar.matrix.file.utils.FileUtils;
import com.mathpar.number.Element;
import com.mathpar.number.Newton;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.io.*;
import java.util.*;
import mpi.MPI;

 
//import matrix.file.*;


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
public class FileMatrixD extends FileMatrix implements Serializable {

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


    public FileMatrixD(){
        ops=new MatrixOpsD();
    }


    /**
     * Инициализирующий конструктор
     * @param root File
     * @param depth int
     */
    public FileMatrixD(File root, int depth){
        this();
        constructor(root,depth);
    }


    /**
     * Создает файловую матрицу из корня. Глубина вычисляется автоматически.
     * @param root File
     */
    public FileMatrixD(File root){
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
    public FileMatrixD(File root, int depth, MatrixD m)
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
     * @param ring ring
     * @throws IOException
     */
    public FileMatrixD(File root, int depth, int m, int n,
                       int den, Random rnd, long mod,Ring ring)
        throws IOException{
        this(); //Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        constructor(root,depth,m,n,new RandomParams(den,0,mod,-1,new int[]{2},rnd,new int[]{5}),ring);
    }
    
    //constructor for Z matrix
    public FileMatrixD(File root, int depth, int m, int n, int nbits) throws IOException{
        this(); //Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        constructor(root, depth, m, n, nbits);
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
    public FileMatrixD add(FileMatrixD m, File to, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(add0(m,to,mod,ring));
        
    }
    
    public FileMatrixD add(FileMatrixD m, File to) throws IOException{
        Ring ring=new Ring("Z[x,y,z]");    
        return toL(add0(m,to,ring));        
    }
    
    public FileMatrixD subtract(FileMatrixD m, File to) throws IOException{
        Ring ring=new Ring("Z[x,y,z]");    
        return toL(subtract0(m,to,ring));        
    }
    
    public FileMatrixD copyByMod(File to, Element mod, Ring ring)
        throws IOException{
        //создать директорию to, если глубина>0 и если не существует
        FileUtils.createDir(to, depth);
        //создать дерево
        copyByMod0(getRoot(), to, depth, mod, ring);
        //вернуть объект файловой матрицы        
        return new FileMatrixD(to, depth);
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
    public FileMatrixD subtract(FileMatrixD m, File to, long mod)
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
    public FileMatrixD multCU(FileMatrixD m, File to, long mod)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(mod);
        return toL(multCU0(m,to,mod,ring));
    }

    public FileMatrixD multCU(FileMatrixD m, File to) throws IOException{ 
        Ring ring=new Ring("Z[x,y,z]");
        return toL(multCU0(m,to,ring));
    }


    /**
     * Копирует файловую матрицу this в to. Возвращает новую файловую матрицу в to.
     * Если depth=0, то в файл to.
     * Если depth>0, то в дерево с корнем to, глубиной depth>0.
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixD copy(File to)
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
    public FileMatrixD negate(File to, long mod,Ring ring)
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
    public static FileMatrixD joinMove(FileMatrix[] matrs, boolean hasNulls, File to,Ring ring)
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
    public static FileMatrixD joinCopy(FileMatrix[] matrs, boolean hasNulls, File to,Ring ring)
        throws IOException{
        return toL(new FileMatrixD().joinCopy0(matrs, hasNulls, to,ring));
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
    public static FileMatrixD ZERO(int m, int n, int depth, File to,Ring ring)
        throws IOException{
        return toL(new FileMatrixD().ZERO0(m,n,depth,to,ring));
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
    public static FileMatrixD ONE(int n, int depth, File to)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(3);
        return toL(new FileMatrixD().ONE0(n,depth,to,ring));
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
    public FileMatrixD(int depth, MatrixL m)
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
    public FileMatrixD(int depth, int m, int n,
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
    public FileMatrixD add(FileMatrixD m, long mod)
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
    public FileMatrixD subtract(FileMatrixD m, long mod)
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
    public FileMatrixD multCU(FileMatrixD m, long mod)
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
    public FileMatrixD copy()
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
    public FileMatrixD negate(long mod,Ring ring)
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
    public static FileMatrixD joinMove(FileMatrix[] matrs, boolean hasNulls,Ring ring)
        throws IOException{
        return toL(new FileMatrixD().joinMove0(matrs, hasNulls,ring));
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
    public static FileMatrixD joinCopy(FileMatrix[] matrs, boolean hasNulls,Ring ring)
        throws IOException{
        return toL(new FileMatrixD().joinCopy0(matrs, hasNulls,ring));
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
    public static FileMatrixD ZERO(int m, int n, int depth,Ring ring)
        throws IOException{
        return toL(new FileMatrixD().ZERO0(m,n,depth,ring));
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
    public static FileMatrixD ONE(int n, int depth)
        throws IOException{ Ring ring=new Ring("Zp32[]"); ring.setMOD32(3);
        return toL(new FileMatrixD().ONE0(n,depth,ring));
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
    public static FileMatrixD restoreCopy(File dir, File to)
        throws IOException{
        return toL(new FileMatrixD().restoreCopy0(dir,to));
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
    public static FileMatrixD restoreCopy(File dir)
        throws IOException{
        return toL(new FileMatrixD().restoreCopy0(dir));
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
    public static FileMatrixD restoreMove(File dir, File to)
        throws IOException{
        return toL(new FileMatrixD().restoreMove0(dir,to));
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
    public static FileMatrixD restoreMove(File dir)
        throws IOException{
        return toL(new FileMatrixD().restoreMove0(dir));
    }

    private void copyByMod0(File r1, File to, int depth, Element mod, Ring ring)
        throws IOException{
        if (depth==0) {
            //глубина = 0
            Object a=ops.readMatrFromFile(r1);
            MatrixD res=((MatrixD)a).mod(mod, ring);            
            ops.writeMatrToFile((Object)res, to);
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
                copyByMod0(new File(r1,i+""),  fi, depth-1, mod, ring);
            }
        }
    }
    
    public void restoreByGarner(FileMatrixD [] matrixes, ArrayList<NumberZ> mods) throws Exception{        
        getRoot().mkdirs();        
        Ring ring=new Ring("Z[x,y,z]");
        int bSize=matrixes[0].toMatrixD().colNum()/2;
        for (int i=0; i<4; i++){
            MatrixD []bArr=new MatrixD[matrixes.length];
            for (int j=0; j<matrixes.length; j++){
                File tmp=new File(matrixes[j].getRoot().getAbsolutePath()+File.separatorChar+String.valueOf(i));
                bArr[j]=(MatrixD)ops.readMatrFromFile(tmp);
                //System.out.println("i="+i+" j="+j+" "+a.toString(ring));
            }
            NumberZ [][]curRes=new NumberZ[bSize][bSize];
            for (int k=0; k<bSize; k++){
                for (int l=0; l<bSize; l++){
                    NumberZ []rems=new NumberZ[mods.size()];
                    for (int z=0; z<bArr.length; z++){
                        rems[z]=(NumberZ)bArr[z].M[k][l];
                    }
                    curRes[k][l]=Newton.garnerRestore(rems, mods);
                }
            }
            MatrixD dRes=new MatrixD(curRes);
            File dest=new File(getRoot().getAbsolutePath()+File.separatorChar+String.valueOf(i));
            ops.writeMatrToFile((Object)dRes, dest);
        }        
    }
    
    public FileMatrixD copyBlockTo(int blockNumb, File to){
        int depth = getDepth();                              
        String fName=getRoot().getAbsolutePath()+createPathForBlock(blockNumb, depth-1);                
        String []addC={"0","1","2","3"};
        File tmpF=new File(to.getAbsolutePath());
        tmpF.mkdirs();
        for (int i=0; i<4; i++){
            File from=new File(fName+File.separatorChar+addC[i]);
            File toTmp=new File(to.getAbsolutePath()+File.separatorChar+addC[i]);
            try {
                FileUtils.copyFile(from, toTmp);            
            }
            catch (Exception e){}
        }
        return new FileMatrixD(to, 1);
    }
    
    static String createPathForBlock(int blockNumb, int depth){        
        int sLen=(1<<(depth-1));
        int bR=0,bC=0,actR=blockNumb/(sLen*2),actC=blockNumb%(sLen*2);
        StringBuffer res=new StringBuffer();
        int []shR={0,0,1,1};
        int []shC={0,1,0,1};
        char []addC={'0','1','2','3'};
        for (int i=0; i<depth; i++,sLen/=2){
            int mR=bR+sLen-1;
            int mC=bC+sLen-1;
            for (int j=0; j<4; j++){
                int r1=bR+sLen*shR[j];
                int c1=bC+sLen*shC[j];
                int r2=mR+sLen*shR[j];
                int c2=mC+sLen*shC[j];
                if (actR>=r1 && actR<=r2 && actC>=c1 && actC<=c2){
                    bR=r1;
                    bC=c1;
                    res.append(File.separatorChar);
                    res.append(addC[j]);
                    break;
                }
            }
        }
        return res.toString();
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
    public MatrixD toMatrixD() throws IOException{
        return (MatrixD)toMatrix();
    }




    /**
     * Возвращает i-й подблок в виде файловой матрицы. Не копирует поддерево (подфайл).
     * Новая файловая матрица указывает на поддиректорию (подфайл).
     * @param i int [0..3]
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixD getSubBlock(int i)
        throws IOException{
        return toL(getSubBlock0(i));
    }

    public void copyThisMatrixHowPartBigMatrix(int blockNumb,int depth, File to) throws Exception{
        String []addC={"0","1","2","3"};
        File parF=new File(to.getAbsolutePath()+createPathForBlock(blockNumb, depth-1));
        parF.mkdirs();  
        for (int i=0; i<4; i++){
            File curTo=new File(parF.getAbsolutePath()+File.separatorChar+addC[i]);                
            File curFrom=new File(getRoot().getAbsolutePath()+File.separatorChar+addC[i]); 
            FileUtils.copyFile(curFrom, curTo);
        }
    }

    /**
     * Возвращает i-й подблок в виде файловой матрицы.
     * Копирует поддерево (подфайл) в to.
     * @param i int
     * @param to File
     * @return FileMatrixL
     * @throws IOException
     */
    public FileMatrixD getSubBlockCopy(int i, File to)
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
    public FileMatrixD[] split()
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
    public FileMatrixD[] splitMove()
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
    public FileMatrixD[] splitCopy()
        throws IOException{
        return toArrL(splitCopy0());
    }


    //======================== Private methods =======================

    private static FileMatrixD toL(FileMatrix fm){
        return new FileMatrixD(fm.getRoot(),fm.getDepth());
    }

    private static FileMatrixD[] toArrL(FileMatrix[] fmArr){
        //n=4
        int n=fmArr.length;
        FileMatrixD[] fmArr2=new FileMatrixD[n];
        for (int i = 0; i < n; i++) {
            fmArr2[i]=toL(fmArr[i]);
        }
        return fmArr2;
    }


}