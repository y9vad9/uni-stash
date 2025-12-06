package com.mathpar.parallel.stat.FMD.MultFMatrix;

import mpi.Group;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import com.mathpar.matrix.file.sparse.SFileMatrix;
import com.mathpar.matrix.file.utils.BaseMatrixDir;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

/**
 * Параллельное умножение двух матриц типа FileMatrixL
 *
 * @author vladimir
 */
public final class Multiplay<T extends SFileMatrix> {

    private Intracomm[] row, col;
    private int[][] proc_matrix, proc_matrix_invert;
    private int[] number_first_row;
    public Set<Integer> process = new HashSet<Integer>();

    @Deprecated
    private final Class<T> cls;

    private Constructor construct;

    /**
     * инициализирующий конструктор принимает на вход ссылку на класс того типа
     * что и generic T
     *
     * @param cls
     */
    public Multiplay(Class<T> cls) {
        this.cls = cls;
        try {
            construct = cls.getConstructor(File.class, int.class);
        } catch (Exception ex) {
            System.out.println("lf//////////////////////////////");
            java.util.logging.Logger.getLogger(Multiplay.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Умножение матрицы A на B обязательно должны иметь одинаковый depth.
     * Состоит из трех частей:
     * 1) "cutAndSendOnProcessorsMatrs" рассылает матрицу A по блочным строкам вдоль коммуникаторов row рассылает
     * матрицу B по блочным столбцам вдоль коммуникаторов col
     * 2) "OperationOnSubbloks.INSTANCE.<T>multypli_blocks" умножает поблочно 
     * 3) 
     * @param A входная матрица, квадратная
     * @param B входная матрица, квадратная
     * @param proc узел на котором находится матрица
     * @return подблок на каждом узле полученный при умножение, соостветствие
     * подблоков месту в матрице ответа задаётся proc_matrix для сборки матрицы
     * ответа использовать метод assemblage
     * @throws MPIException
     * @throws IOException
     */
    public final T mult(final T A, final T B, final int proc, final Ring ring) throws MPIException, IOException {

        final long t1 = System.currentTimeMillis();

        int dep = 0;
        final NodeModel node = new NodeModel(MPI.COMM_WORLD.getRank(), MPI.COMM_WORLD.getSize());
        if (node.rank == proc) {
            dep = A.getDepth();
        }
        dep = bcastInt(dep, proc, node);
        initProcMatrix(node, dep);
        System.out.println("procdasfdasfds" + process);
        System.out.println("I`m " + node.rank + " and i contains in " + process.contains(new Integer(node.rank)));

        if (process.contains(node.rank)) {

            for (int[] i : proc_matrix) {
                System.out.println("proc_matrix " + Arrays.toString(i));
            }

            initIntracom(node);

            List<T> fm = cutAndSendOnProcessorsMatrs(A, TypeCutMatrs.CUT_ROW, node, dep, proc);
            List<T> fm2 = cutAndSendOnProcessorsMatrs(B, TypeCutMatrs.CUT_COLS, node, dep, proc);

            MPI.COMM_WORLD.barrier();
            final long t2 = System.currentTimeMillis();
            final T subbloks = OperationOnSubbloks.INSTANCE.<T>multypli_blocks(fm, fm2, ring); // умножение блоков
            final long t3 = System.currentTimeMillis();

            MPI.COMM_WORLD.barrier();

            if (node.rank == 0) {
                System.out.println(String.format("I`m %s node: cut and sending matrs  - %s ms; \n mult blocks on node %s ms", node.rank, (t2 - t1), (t3 - t2)));
                System.out.println(String.format("I`m %s node: cut and sending matrs  - %s ms; \n mult blocks on node %s ms", node.rank, (t2 - t1), (t3 - t1)));
            }
            //final T res = AssemblyResult.INSTANCE.Assemblage(proc_matrix, subbloks, dep, proc, construct); // сборка финальной матрицы ответа
//        final MultiplyResult<T> result = new MultiplyResult<T>();
//        result.matrixInNode = subbloks;
//        result.resultMult = res;
            return subbloks;
        }
        return null;
    }

    /**
     * Умножает SfileMatrix на элемент заданный в виде SfileMatrix с глубиной 0
     * и одни элементом.
     *
     * @param A исходная матрциа
     * @param B файл матрица глубины 0 состоящая из 1 элемента
     * @param proc узел на котором распологаются входные матрицы
     * @param ring
     * @return подблок матрицы на каждом узле согласно proc_matrix, собирать
     * методом
     * @throws MPIException
     * @throws IOException
     */
    public final T multMatrsOnElement(final T A, Element B, final int proc, final Ring ring, long mod) throws MPIException, IOException {
        int dep = 0;
        final NodeModel node = new NodeModel(MPI.COMM_WORLD.getRank(), MPI.COMM_WORLD.getSize());

        if (process.contains(node.rank)) {
            if (node.rank == proc) {
                dep = A.getDepth();
            }
            dep = bcastInt(dep, proc, node);
            initProcMatrix(node, dep);
            initIntracom(node);
            //   System.out.println("Дошёл до умножение блоков");
            T block = cutAndSendOnProcessorsMatrs(A, TypeCutMatrs.CUT_BLOCKS, node, dep, proc).get(0);
            MPI.COMM_WORLD.barrier();
            B = SendReciveFileMatrixL.INSTANCE.Bcast_Element(MPI.COMM_WORLD, B, proc);
            T res = (T) block.multiplyByNumber0Paralell(B, BaseMatrixDir.getRandomDir(), mod, ring);

            return res;
        } else {
            return null;
        }

    }

    /**
     * Умножает SfileMatrix разбитую на предыдущих шагах по узлам на элемент
     * заданный в виде SfileMatrix с глубиной 0 и одни элементом.
     *
     * @param A блок входной матрицы
     * @param B множитель
     * @param proc узел на котором распологаются входные матрицы
     * @param ring
     * @return подблок матрицы на каждом узле согласно proc_matrix, собирать
     * методом
     * @throws MPIException
     * @throws IOException
     */
    public final T multMatrsOnElementOnNode(final T A, Element B, final int proc, final Ring ring, long mod) throws MPIException, IOException {
        int dep = 0;

        final NodeModel node = new NodeModel(MPI.COMM_WORLD.getRank(), MPI.COMM_WORLD.getSize());

        if (process.contains(node.rank)) {
            if (node.rank == proc) {
                dep = A.getDepth();
            }
            dep = bcastInt(dep, proc, node);
            B = SendReciveFileMatrixL.INSTANCE.Bcast_Element(MPI.COMM_WORLD, B, proc);
            T res = (T) A.multiplyByNumber0Paralell(B, BaseMatrixDir.getRandomDir(), mod, ring);
            return res;
        } else {
            return null;
        }

    }

    /**
     * востанавливает матрицу из подблоков соостветсвенно proc_matrix
     *
     * @param subblocks блок матрицы, находитя на каждом узле соответственно
     * proc_matrix
     * @param dep глубина матрицы ответа
     * @param proc узел на котором требуется выполнить сборку
     * @return возвращает собранную из подблоков матрицу
     * @throws MPIException
     * @throws IOException
     */
    public final T assemblage(final T subblocks, final int dep, final int proc) throws MPIException, IOException {

        if (process.contains(MPI.COMM_WORLD.getRank())) {
            return AssemblyResult.INSTANCE.Assemblage(proc_matrix, subblocks, dep, proc, construct);
        } else {
            return null;
        }

    }

    /**
     * Складывает разбитую на подблоки subblock после предыдущих операций и
     * входную матрицу matrix
     *
     * должен был выполнится до этого обязаительно или mult или add
     *
     * @param subblock
     * @param matrix
     * @param proc номер узла на котором находится матрциа matrix
     * @param ring
     * @return подблок полученный при складывании матриц, на каждом узле свой
     * блок, соответственно proc_matrix для сборки матрицы использовать метод
     * assemblage.
     * @throws MPIException
     * @throws IOException
     */
    public final T addMatrsOnNode(final T subblock, final T matrix, final int proc, final Ring ring) throws MPIException, IOException {
        int dep = 0;
        final NodeModel node = new NodeModel(MPI.COMM_WORLD.getRank(), MPI.COMM_WORLD.getSize());

        if (process.contains(node.rank)) {
            if (node.rank == proc) {
                dep = matrix.getDepth();
            }

            dep = bcastInt(dep, proc, node);

            T block = cutAndSendOnProcessorsMatrs(matrix, TypeCutMatrs.CUT_BLOCKS, node, dep, proc).get(0);

            T res = subblock.addForParalell(block, dep, ring);

            return res;
        } else {
            return null;
        }
    }

    /**
     * Складывает две матрциы A и B, разбивая и рассылая их на подблоки
     * соответственно proc_matrix
     *
     * @param A входные матрицы
     * @param B
     * @param proc номер узла на котором находится матрциы A и B
     * @param ring
     * @return подблок полученный при складывании матриц, на каждом узле свой
     * блок, соответственно proc_matrix для сборки матрицы использовать метод
     * assemblage.
     * @throws MPIException
     * @throws IOException
     */
    public final T add(final T A, final T B, final int proc, final Ring ring, long mod) throws MPIException, IOException {
        final long t1 = System.currentTimeMillis();
        int dep = 0;
        final NodeModel node = new NodeModel(MPI.COMM_WORLD.getRank(), MPI.COMM_WORLD.getSize());
        if (node.rank == proc) {
            dep = A.getDepth();
        }

        dep = bcastInt(dep, proc, node);
        initProcMatrix(node, dep);

        if (process.contains(node.rank)) {

            //  System.out.println("Начал"+ node.rank + " proc " + proc);
            T blockA = cutAndSendOnProcessorsMatrs(A, TypeCutMatrs.CUT_BLOCKS, node, dep, proc).get(0);
            T blockB = cutAndSendOnProcessorsMatrs(B, TypeCutMatrs.CUT_BLOCKS, node, dep, proc).get(0);
            MPI.COMM_WORLD.barrier();
            final long t2 = System.currentTimeMillis();
            T res = blockA.addForParalell(blockB, BaseMatrixDir.getRandomDir(), mod, ring);
            MPI.COMM_WORLD.barrier();
            final long t3 = System.currentTimeMillis();
            if (node.rank == 0) {
                System.out.println(String.format("I`m %s node: cut and sending matrs  - %s ms;   add blocks on node %s ms", node.rank, (t2 - t1), (t3 - t2)));
            }

            return res;
        } else {
            return null;
        }
    }

    /**
     * Метод получающий блочную строку или столбец в зависимости от выбранного
     * тип TypeCutMatrs
     *
     * @param matrs исходная матрица
     * @param typeCut способ разбивки по столбцам или строкам
     * @param node модель содержащая в себе номер узла и сколько всего узлов
     * доступно
     * @param dep глубина матрицы
     * @param proc рассылающий узел
     * @return
     * @throws MPIException
     * @throws IOException
     */
    public List<T> cutAndSendOnProcessorsMatrs(T matrs, final TypeCutMatrs typeCut, final NodeModel node, final int dep, final int proc) throws MPIException, IOException {

        switch (typeCut) {

            case CUT_ROW: {
                List<T> result = new ArrayList<T>(proc_matrix.length);
                if (node.rank == proc) {
                    for (int i = 0; i < proc_matrix.length; i++) {
                        if (number_first_row[i] != proc) {           //не посылаю рассылающему процессору
                            result = cut_FM_row(matrs, i, dep);//берём строку
                            // logger.debug(String.format("Послал send %s процессору строкой ", number_first_row[i]));
                            SendReciveFileMatrixL.INSTANCE.Send_arr(result, number_first_row[i], TagSending.TAG_SEND_ARRAY_FM);
                        } else {
                            //   logger.debug("Да проц сюда не пошёл");
                        }
                    }
                }

                if (Arrays.binarySearch(number_first_row, node.rank) >= 0 && node.rank != proc) {         //если я тот кому была до этого посылка и я не рассылающий то принимаю
                    result = SendReciveFileMatrixL.INSTANCE.Recv_arr(proc, TagSending.TAG_SEND_ARRAY_FM, construct);
                }

                if (node.rank == proc) //беру строку если я рассылаюющий процессор
                {
                    result = cut_FM_row(matrs, proc / proc_matrix.length, dep);
                }

                if (node.rank == proc) //беру строку если я рассылаюющий процессор
                {
                    for (int[] i : proc_matrix) {
                        System.out.println("proc_matrix " + Arrays.toString(i));
                    }
                }

                final int numIntr = (int) node.rank / proc_matrix.length;     //вычисляем номер коммуникатора на котором находимся

                if (Arrays.binarySearch(proc_matrix[numIntr], node.rank) >= 0) {      //если мы на прцоессорах коммуникатора то рассылаем вдоль него матрицу
                    result = SendReciveFileMatrixL.INSTANCE.Bcast_FM_arr(row[numIntr], result, 0, construct);
                }

                return result;
            }

            case CUT_COLS: {
                List<T> result = new ArrayList<T>(proc_matrix.length);
                //    logger.debug("выбран тип разбивки по столбцам ");
                if (node.rank == proc) {
                    for (int i = 0; i < proc_matrix[0].length; i++) {
                        if (proc_matrix[0][i] != proc) {
                            result = cut_FM_col(matrs, i, dep);
                            SendReciveFileMatrixL.INSTANCE.Send_arr(result, proc_matrix[0][i], TagSending.TAG_SEND_ARRAY_FM);
                        }
                    }
                }

                if (Arrays.binarySearch(proc_matrix[0], node.rank) >= 0 && node.rank != proc) {
                    result = SendReciveFileMatrixL.INSTANCE.Recv_arr(proc, TagSending.TAG_SEND_ARRAY_FM, construct);
                }

                if (node.rank == proc) {
                    result = cut_FM_col(matrs, proc / proc_matrix.length, dep);   //ПРоверить
                }
                for (int i = 0; i < proc_matrix[0].length; i++) {
                    if (Arrays.binarySearch(proc_matrix_invert[i], node.rank) >= 0) {
                        result = SendReciveFileMatrixL.INSTANCE.Bcast_FM_arr(col[i], result, 0, construct);
                    }
                }
                return result;
            }

            case CUT_BLOCKS: {
                List<T> result = new ArrayList<T>(1);
                if (node.rank == proc) {
                    for (int i = 0; i < proc_matrix.length; i++) {
                        for (int j = 0; j < proc_matrix[i].length; j++) {
                            T t = get_subblock(matrs, i, j, dep);
                            if (proc_matrix[i][j] == proc) {
                                result.add(t);
                            } else {
                                SendReciveFileMatrixL.INSTANCE.SendFM(t, proc_matrix[i][j], TagSending.TAG_SEND_FM_CUT_BLOCKS);
                            }
                        }
                    }
                } else {
                    T t = SendReciveFileMatrixL.INSTANCE.Recv(proc, TagSending.TAG_SEND_FM_CUT_BLOCKS, construct);
                    result.add(t);
                }
                return result;
            }
            default:
                throw new RuntimeException("not know this type: " + typeCut.name());
        }

    }

    /**
     * рассылка всем процессорам числа msg от процессора proc
     *
     * @param msg
     * @param proc
     * @param node
     *
     * @return
     *
     * @throws MPIException
     */
    private int bcastInt(final int msg, final int proc, final NodeModel node) throws MPIException {
        final int[] arr_msg = new int[]{1};
        if (node.rank == proc) {
            arr_msg[0] = msg;
        }
        MPI.COMM_WORLD.bcast(arr_msg,  1, MPI.INT, proc);
        // logger.debug("у всех есть bcast dep "+arr_msg[0]);
        return arr_msg[0];
    }       //проверрено

    private void initProcMatrix(final NodeModel node, final int dep) {
        System.out.println("I`m node " + node.rank + " " + " dep " + dep);
        int count_proc = (int) (Math.pow(4, dep) > node.size ? node.size : Math.pow(4, dep));

        System.out.println("count_proc" + dep);
        int k = (int) Math.pow(2, (int) (Math.log(count_proc) / Math.log(4)));// размерность матрицы k*k  кол-во процессоров
//        int y = node.size;
//        while (y > 2) {
//            y >>= 1;
//            k++;
//        }

        proc_matrix = new int[k][k];//матрица процессоров
        int t = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                proc_matrix[i][j] = t;
                process.add(t);
                t++;
            }
        }
        for (int[] i : proc_matrix) {
            System.out.println("proc" + Arrays.toString(i));
        }
        proc_matrix_invert = new int[k][k];
    }

    /**
     * инициализация коммуникаторов
     *
     * @param node
     *
     * @throws Exception
     */
    private void initIntracom(final NodeModel node) throws MPIException {
        int k = proc_matrix.length;
        final Group[] row_g = new Group[k];  //группы для строк
        final Group[] col_g = new Group[k];
        row = new Intracomm[k];
        col = new Intracomm[k];
        /*
         * создаём группы процессоров и коммуникаторы от них
         */
        for (int i = 0; i < k; i++) {
            final int[] temp_array = new int[k];

            for (int j = 0; j < k; j++) {
                temp_array[j] = proc_matrix[j][i];
            }
            proc_matrix_invert[i] = temp_array;

            col_g[i] = MPI.COMM_WORLD.getGroup().incl(temp_array);

            if (i == 0) {
                number_first_row = temp_array.clone();
            }

            row_g[i] = MPI.COMM_WORLD.getGroup().incl(proc_matrix[i]);
            row[i] = MPI.COMM_WORLD.create(row_g[i]);

            col[i] = MPI.COMM_WORLD.create(col_g[i]);
        }
    }

    /**
     * Получение блочной строки из матрицы A.
     *
     * @param A исходная матрица
     * @param numi номер строки
     *
     * @return массив FileMatrixL[] состоящий из блоков numi строки матрицы A
     *
     * @throws java.io.IOException
     */
    private List<T> cut_FM_row(final T A, final int numi, int dep) throws IOException {
        //   final T[] res = (T[]) new SFileMatrix[(int) Math.pow(2, dep)];
        int k = (int) Math.pow(2, dep);
        final List<T> res = new ArrayList<T>(k);

        for (int i = 0; i < k; i++) {
            res.add(get_subblock(A, numi, i, dep));
        }
        return res;
    }

    /**
     * Получение блочного столбца из матрицы B.
     *
     * @param B исходная матрица
     * @param numj номер столбца
     *
     * @return массив FileMatrixL[] состоящий из блоков numj столбца матрицы B
     *
     * @throws java.io.IOException
     */
    private List<T> cut_FM_col(final T B, int numj, int dep) throws IOException {
        //logger.debug("начал получать столбец из исхлдной матрицы numj " + numj + " узле ");
        int k = (int) Math.pow(2, dep);
        final List<T> res = new ArrayList<T>(k);
        for (int i = 0; i < k; i++) {
            res.add(get_subblock(B, i, numj, dep));
        }
        return res;
    }

    /**
     * Получение подблока с индексами i j в виде FileMatrixL и копирование его в
     * to.
     *
     * @param fm исходная файл матрица
     * @param i номер строки
     * @param j номер столбца
     *
     * @return FileMatrixL в дирректории to
     *
     * @throws java.io.IOException
     */
    private T get_subblock(final T fm, final int i, final int j, int dep) throws IOException {
        final int[] path = AssemblyResult.INSTANCE.path_out(i, j, dep);
        if (dep != 0) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(fm.getRoot().getAbsolutePath());
            stringBuilder.append('/');
            for (int y = 0; y < path.length; y++) {
                if (y != path.length - 1) {
                    stringBuilder.append(path[y]);
                    stringBuilder.append('/');
                } else {
                    stringBuilder.append(path[y]);
                }
            }
            try {
                final File tmp = new File(stringBuilder.toString());
                final int tmpint = dep - path.length;
                return (T) construct.newInstance(tmp, tmpint);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(Multiplay.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(Multiplay.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                java.util.logging.Logger.getLogger(Multiplay.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                java.util.logging.Logger.getLogger(Multiplay.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new RuntimeException(" попытка взять подблок у матрицы глубины 0 ");
            //return fm.getSubBlock(path[0]);
        }
        throw new RuntimeException(" не смог инициализировать новый инстанс файл матрицы ");

    }

    public final class NodeModel {

        public final int rank;
        public final int size;

        public NodeModel(int rank, int size) {
            this.rank = rank;
            this.size = size;
        }
    }
}
