
package com.mathpar.matrix.file.mmio;

import java.io.*;
import com.mathpar.number.Element;
import com.mathpar.matrix.*;
import com.mathpar.matrix.file.ops.MatrixOpsS;
import com.mathpar.matrix.file.sparse.SFileMatrixS;
import com.mathpar.matrix.file.utils.FileUtils;
import com.mathpar.number.Ring;


/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ParCA</p>
 *
 * @author Yuri Valeev
 * @version 2.0
 */
public class SFileTree {
    /**
     * Корень дерева.
     */
    private File treeRoot;
    /**
     * Размер всей матрицы. Это кол-во строк и столбцов.
     */
    private int size;
    /**
     * Глубина
     */
    private int depth;
    /**
     * Размер листа.
     */
    private int lSize;
    /**
     * Для записи в файлы дерева.
     */
    private CoordFileWriter cfw;
    /**
     * Для чтения из файлов дерева.
     */
    private GeneratorCFR gencfr;
    /**
     * Скалярный элемент для определения типа SFileMatrixS.
     */
    private Element scalElem;
    /**
     * Объект для записи файлов в SFileMatrixS.
     */
    private MatrixOpsS ops;



    public SFileTree(File treeRoot, int m, int n, int depth,
                     CoordFileWriter cfw, GeneratorCFR gencfr,
                     Element scalElem, Ring ring){
        this.treeRoot=treeRoot;
        this.size=getSize(m,n);
        this.depth=depth;
        this.lSize=size >> depth; //lSize=size/2^depth
        this.cfw=cfw;
        this.gencfr=gencfr;
        this.scalElem=scalElem;
        this.ops=new MatrixOpsS(scalElem, ring);
    }


    /**
     * Добавить строчку в SFileTree.
     * @param line CoordLine
     * @throws MMIOException
     */
    public void addLine(CoordLine line) throws MMIOException{
        //try {
            //Взять i,j (нумерация с 0),i1,j1 (нумерация с 1),el из line
            int i1 = line.i;
            int j1 = line.j;
            Element el = line.el;
            int i = i1 - 1;
            int j = j1 - 1;

            //Создать файл, если нужно
            File lFile = getFullPath(i, j);
            if (!lFile.exists()) {
                //создать директории (если нужно)
                //FileUtils.mkdirs(lFile.getParentFile());
                lFile.getParentFile().mkdirs();
                //записать заголовок
                cfw.writeHeader(lFile, lSize, lSize, -1);
            }

            //Записать в файл i2,j2 (номера строки и столбца внутри листа, нумерация с 1),el
            int i2=i%lSize+1;
            int j2=j%lSize+1;
            cfw.writeLine(lFile, i2, j2, el);
        //} catch (IOException ex) {
        //    throw new MMIOException("I/O error",ex);
        //}
    }


    public SFileMatrixS toSFileMatrixS(File to) throws MMIOException{
        try {
            FileUtils.createDir(to, depth);
            convRec(treeRoot, to, depth);
        } catch (IOException ex) {
            throw new MMIOException("I/O error",ex);
        }
        return new SFileMatrixS(to,depth,ops);
    }


    private void convRec(File from, File to, int depth)
        throws MMIOException{
        try {
            if (depth == 0) {
                //глубина==0
                CoordFileReader cfr = gencfr.getCFR(from);
                MatrixS ms = Converters.convCoordFileToMatrixS(cfr);
                ops.writeMatrToFile(ms, to);
            } else {
                //глубина>0
                for (int i = 0; i < 4; i++) {
                    File fromi = new File(from, i + "");
                    if (fromi.exists()) {
                        File toi = new File(to, i + "");
                        if (depth > 1) {
                            FileUtils.mkdir(toi);
                        }
                        convRec(fromi, toi, depth - 1);
                    }
                }
            }
        } catch (IOException ex) {
            throw new MMIOException("I/O error",ex);
        }
    }


    private static int getSize(int m, int n){
        int max=(m>n?m:n);
        //добить до следующей 2^n
        int max1bit=Integer.highestOneBit(max);
        int size=max1bit<<1;

        return size;
    }



    private File getFullPath(int i, int j){
        if (depth==0) {
            return treeRoot;
        } else {
            int ibl=i/lSize;
            int jbl=j/lSize;
            //depth=n
            //ibl=<In-1In-2...I0>
            //jbl=<Jn-1Jn-2...J0>
            //a=<a0a1...an-1>, ai=<In-i-1Jn-i-1>
            int[] a=new int[depth];
            int mask=1<<(depth-1);
            for (int k = 0; k < depth; k++) {
                //true-1, false-0
                boolean Ii=(ibl & mask)!=0;
                boolean Ji=(jbl & mask)!=0;
                if (Ii) {
                    if (Ji) {
                        //11
                        a[k]=3;
                    } else {
                        //10
                        a[k]=2;
                    }
                } else {
                    if (Ji) {
                        //01
                        a[k]=1;
                    } else {
                        //00
                        a[k]=0;
                    }
                }
                mask>>>=1;
            }

            //path=treeRoot/a0/a1/.../an-1
            String path=treeRoot.toString();
            for (int k = 0; k < depth; k++) {
                path=path+File.separator+a[k];
            }
            return new File(path);
        }
    }


}
