
package com.mathpar.matrix.file.mmio;

import com.mathpar.matrix.*;
import com.mathpar.matrix.file.sparse.SFileMatrixS;
import com.mathpar.number.*;
import java.io.*;


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
public class Converters {

    private static MatrixDynS convCoordFileToMDynS(CoordFileReader in)
        throws MMIOException{
        //прочитать заголовок и создать MatrixDynS
        int[] h=in.readHeader();
        int m=h[0];
        MatrixDynS mdyns=new MatrixDynS(m);

        //добавить все строки в MatrixDynS
        CoordLine line;
        while ((line=in.readLine())!=null) {
            //пока есть строчки --> mdyns
            mdyns.addLine(line);
        }
        //нет больше строчек
        in.close();
        return mdyns;
    }

    public static MatrixS convCoordFileToMatrixS(CoordFileReader in)
        throws MMIOException{
        return convCoordFileToMDynS(in).toMatrixS();
    }


    private static SFileTree convCoordFileToSFileTree(CoordFileReader in,
        File treeRoot, int depth, CoordFileWriter cfw, GeneratorCFR gencfr,
        Element elem, Ring ring)
        throws MMIOException{
        //прочитать заголовок и создать SFileTree
        int[] h=in.readHeader();
        int m=h[0];
        int n=h[1];
        SFileTree tree=new SFileTree(treeRoot,m,n,depth,cfw, gencfr,elem, ring);

        //добавить все строки в MatrixDynS
        CoordLine line;
        while ((line=in.readLine())!=null) {
            //пока есть строчки --> mdyns
            tree.addLine(line);
        }
        //нет больше строчек
        in.close();
        return tree;
    }


    public static SFileMatrixS convCoordFileToSFileMatrixS(CoordFileReader in,
        File treeRoot, int depth, CoordFileWriter cfw, GeneratorCFR gencfr,
        Element elem, File to, Ring ring)
        throws MMIOException{
        return convCoordFileToSFileTree(in,treeRoot,depth,cfw,gencfr,elem, ring).toSFileMatrixS(to);
    }


}
