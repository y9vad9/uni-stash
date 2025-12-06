package com.mathpar.parallel.stat.FMD.MultFMatrix;

import com.mathpar.matrix.file.dense.FileMatrixL;
import com.mathpar.matrix.file.dm.MatrixL;
import com.mathpar.matrix.file.utils.BaseMatrixDir;

import java.io.IOException;
import java.util.List;

import com.mathpar.matrix.file.sparse.SFileMatrix;
import com.mathpar.number.Ring;

/**
 * Created with IntelliJ IDEA.
 * User: vladimir
 * Date: 12.01.14
 * Time: 2:01
 * To change this template use File | Settings | File Templates.
 */
public enum OperationOnSubbloks {

    INSTANCE;

    /**
     * Умножение блоков на узлах.
     * <p/>
     * Запускается на каждом узле. Осуществляется умножение двух массивов блоков
     * полученных при разбиениии исходной матрицы.
     * @param fm1 массив строк
     * @param fm2 массив столбцов
     * @throws IOException
     */

    public final <T extends SFileMatrix> T multypli_blocks(final List<T> fm1, final List<T> fm2, final Ring ring) throws IOException {
        T res = fm1.get(0).multiplyForParalell(fm2.get(0), BaseMatrixDir.getRandomDir(), Long.MAX_VALUE, ring);
        for (int i = 1; i < fm1.size(); i++) {
            final T temp = fm1.get(i).multiplyForParalell(fm2.get(i), Long.MAX_VALUE, ring);
            res = temp.addForParalell(res, Long.MAX_VALUE, ring);
        }
        return res;
    }
}
