package com.mathpar.parallel.stat.FMD.MultFMatrix;

import com.mathpar.matrix.file.utils.BaseMatrixDir;
import com.mathpar.matrix.file.utils.FileUtils;
import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import com.mathpar.matrix.file.sparse.SFileMatrix;

/**
 * Created with IntelliJ IDEA. User: vladimir Date: 12.01.14 Time: 12:20 To
 * change this template use File | Settings | File Templates.
 */
public enum AssemblyResult{

    INSTANCE;
  //  Logger logger = Ex2.logger;

    /**
     * Сборка финальной матрицы ответа на узле proc
     *
     * @param proc номер узла на котором будет сборка матрицы
     * @return матрицу результат при умножении A*B в базовой матричной
     * директории
     * <p/>
     * Создаётся директория с случайным именем в базовой матричной директории, и
     * в ней при помощи метода folder(File root, int depth) выстраивается дерево
     * папок для матрицы ответа. Далее метод начинает приёмку блоков финальной
     * матрицы от других узлов, и распологает их в итоговой матрицы исходя из
     * номера узла и положения этогоузла в proc_matrix
     * @throws Exception
     */
    public final <T extends SFileMatrix> T Assemblage(final int[][] proc_matrix, final T matrix, final int dep, final int proc, final Constructor  constructor) throws MPIException, IOException {
         if(MPI.COMM_WORLD.getRank() != proc)
            SendReciveFileMatrixL.INSTANCE.SendFM(matrix, proc, TagSending.TAG_SEND_FINAl_IN_PROC_0);

  //      MPI.COMM_WORLD.Barrier();
        if (MPI.COMM_WORLD.getRank() == proc) {
            try {
                final long t2 = System.currentTimeMillis();
                final File u = BaseMatrixDir.getRandomDir();
                u.mkdirs();
             //   System.out.println("dep" + dep);
                Tree_empty_folders(u, dep);
//                logger.debug("создал куда писать  " + u.getAbsolutePath());
                for (int i = 0; i < proc_matrix.length; i++) {
                    for (int j = 0; j < proc_matrix[i].length; j++) {
                        final T p;
                        if (proc_matrix[i][j] != proc) {
//                            logger.debug("начал приёмку от " + proc_matrix[i][j]);
                            p = SendReciveFileMatrixL.INSTANCE.Recv(proc_matrix[i][j], TagSending.TAG_SEND_FINAl_IN_PROC_0, constructor);
                        } else {
                            p = matrix;
                        }
                        final int[] path = path_out(i, j,dep);
                        set_block(u, p, path);
                    }
                }

                return (T) constructor.newInstance(u, dep);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(SendReciveFileMatrixL.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(" не смог инициализировать новый инстанс файл матрицы ", ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(SendReciveFileMatrixL.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(" не смог инициализировать новый инстанс файл матрицы ", ex);
            } catch (IllegalArgumentException ex) {
                java.util.logging.Logger.getLogger(SendReciveFileMatrixL.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(" не смог инициализировать новый инстанс файл матрицы ", ex);
            } catch (InvocationTargetException ex) {
                java.util.logging.Logger.getLogger(SendReciveFileMatrixL.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(" не смог инициализировать новый инстанс файл матрицы ", ex);
            }
        }
        return null;
    }

    /**
     * строит дерево пустых папок для выходной файл матрицы
     *
     * @param root корневая папка
     * @param depth глубина
     */
    private void Tree_empty_folders(final File root, final int depth) throws IOException {
        if (depth != 1) {
            for (int i = 0; i < 4; i++) {
                final File fold = new File(root.getAbsolutePath() + "/" + i);
                FileUtils.mkdir(fold);
                Tree_empty_folders(fold, depth - 1);
            }
        }
    }

    /**
     * берём путь до нужной ячейки
     *
     * @param i номер строки
     * @param j номер столбца
     * @param coun на сколько блоков разбита 2*coun
     * @return путь к блоку в виде массива int[] состоящий из номеров блоков на
     * каждом уровне матрицы
     */
    public final int[] path_out(final int i, final int j, final int coun) {
        final char[] pi = (Integer.toBinaryString(i)).toCharArray();
        final char[] pj = (Integer.toBinaryString(j)).toCharArray();
        final char[][] path = new char[coun][2];

        int numi = pi.length - 1;
        int numj = pj.length - 1;

        for (int k = coun - 1; k >= 0; k--) {
            if (numi >= 0) {
                path[k][0] = pi[numi];
                numi--;
            } else {
                path[k][0] = '0';
            }
            if (numj >= 0) {
                path[k][1] = pj[numj];
                numj--;
            } else {
                path[k][1] = '0';
            }
        }
        final int[] res = new int[coun];
        for (int k = 0; k < coun; k++) {
            res[k] = Integer.parseInt((path[k][0] + "" + path[k][1]), 2);
        }
        return res;
    }

    /**
     * Запись блока в директорию.
     * <p/>
     * Установка блока в FileMatrixL с корнем в file по пути path
     *
     * @param f корень FileMatrixL
     * @param matrix блок который требуется вставить
     * @param path путь к блоку
     * @throws Exception
     */
    private <T extends SFileMatrix> void set_block(final File f, final T matrix, final int[] path) throws IOException {
        StringBuilder builder = new StringBuilder(f.getAbsolutePath());
        for (int pat : path) {
            builder.append('/');
            builder.append(pat);
        }

        write(new File(builder.toString()), matrix);

        // new FileMatrixL(new File(builder.toString()), 0, matrix);
    }

    private <T extends SFileMatrix> void write(File to, T m) throws IOException {
//        logger.error("!!!!!!!!!!!!!!!" + to.getAbsolutePath());
        m.copyOnly(to);
      //   m.ops.writeMatrToFile(m, to);
//        ObjectOutputStream out = null;
//        try {
//            //записать в файл:
//            //rows(int),cols(int),M(long[][])
//            out = new ObjectOutputStream(new FileOutputStream(to));
//            out.writeInt(m.M.length);
//            out.writeInt(matr.M[0].length);
//            out.writeObject(matr.M);
//            out.flush();
//            out.close();
//        } finally {
//            if (out != null) {
//                out.flush();
//                out.close();
//            }
//        }
    }

}
