package com.mathpar.parallel.stat.FMD.MultFMatrix;

import com.mathpar.matrix.file.dense.FileMatrixD;
import com.mathpar.matrix.file.dm.MatrixL;
import com.mathpar.matrix.file.sparse.SFileMatrix;
import com.mathpar.matrix.file.utils.BaseMatrixDir;
import com.mathpar.number.Element;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import mpi.Intracomm;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import org.apache.commons.io.FileUtils;

/**
 * Created with IntelliJ IDEA. User: vladimir Date: 12.01.14 Time: 1:31 To
 * change this template use File | Settings | File Templates.
 */
public enum SendReciveFileMatrixL {

    INSTANCE;

  //  Logger logger = Ex2.logger;

    /**
     * ПРиёмка массива FileMatrixL[] от процессора proc, отправленого методом
     * ISend_arr
     *
     * @param proc
     * @param tag
     * @return
     * @throws Exception
     */
    public final <T extends SFileMatrix> List<T> Recv_arr(final int proc, final int tag, final Constructor<T> constructor) throws MPIException, IOException {

        final int[] k = new int[1];
        MPI.COMM_WORLD.recv(k, 1, MPI.INT, proc, TagSending.TAG_SEND_ARRAY_LEN);
       // logger.debug("начал получать сообщения от " + proc);
        List<T> res = new ArrayList<T>(k[0]);
        for (int i = 0; i < k[0]; i++) {
            res.add((T) Recv(proc, tag + i, constructor));
        }
        return res;
    }

    /**
     * Отправка массива FileMatrixL узлу proc.
     * <p/>
     * Блокирующая послыка.
     *
     * @param m посылаемый массив
     * @param proc номер узла которому посылается
     * @param tag
     * @throws Exception
     *
     *
     */
    public final <T extends SFileMatrix> void Send_arr(final List<T> m, final int proc, final int tag) throws MPIException, IOException {
        MPI.COMM_WORLD.send(new int[]{m.size()}, 1, MPI.INT, proc, TagSending.TAG_SEND_ARRAY_LEN);  //послали длинну массива
        for (int i = 0; i < m.size(); i++) {
            SendFM(m.get(i), proc, tag + i);
        }
    }

    /**
     * Посылка FileMatrixL узлу proc.
     * <p/>
     * блокирующая посылка матрицы. Вызывает рекурсивный метод Send0
     *
     * @param m исходная матрциа
     * @param proc узел которому посылается матрица
     * @param tag
     * @throws Exception
     */
    public final <T extends SFileMatrix> void SendFM(final T m, final int proc, final int tag)
            throws MPIException, IOException {
        int depth = m.getDepth();
      //  logger.debug("послал depth " + depth + "от " + MPI.COMM_WORLD.Rank() + " to " + proc);
        MPI.COMM_WORLD.send(new int[]{depth}, 1, MPI.INT, proc, tag);        //послал глубину матрицы   1

        Send0(m, proc, tag);
    }
    
    public final <T extends FileMatrixD> void SendFM(final T m, final int proc, final int tag)
            throws MPIException, IOException {
        int depth = m.getDepth();
      //  logger.debug("послал depth " + depth + "от " + MPI.COMM_WORLD.Rank() + " to " + proc);
        MPI.COMM_WORLD.send(new int[]{depth}, 1, MPI.INT, proc, tag);        //послал глубину матрицы   1

        Send0(m, proc, tag);
    }
    
    /**
    послать лист с заданным номером указанному процессу
    листья нумеруются с 0 и до 4^depth-1
    например:
    глубина 1:
    0 1
    2 3
    глубина 2:
    0 1 2 3
    4 5 6 7
    8 9 10 11
    12 13 14 15
    */
    public final <T extends FileMatrixD> void sendBlockFM(final T m,int blockNumb, final int proc, final int tag)
            throws MPIException, IOException {
        int depth = m.getDepth();                              
        String fName=m.getRoot().getAbsolutePath()+createPathForBlock(blockNumb, depth-1);                
        String []addC={"0","1","2","3"};
        for (int i=0; i<4; i++){
            File f=new File(fName+File.separatorChar+addC[i]);
            mySendFile(f, proc, tag+i);
        }
    }
    
    public final <T extends FileMatrixD> void SendFMWithSingleDepthHowMatrixPart(final T m, final int proc, final int tag)
            throws MPIException, IOException {
        String fName=m.getRoot().getAbsolutePath();
        String []addC={"0","1","2","3"};
        for (int i=0; i<4; i++){
            File f=new File(fName+File.separatorChar+addC[i]);
            mySendFile(f, proc, tag+i);
        }
    }
    
    public final void RecvBlockFMHowPartOfMatrix(File f, int blockNumb, int depth, final int proc, final int tag,final Constructor constructor){
        String []addC={"0","1","2","3"};
        File parF=new File(f.getAbsolutePath()+createPathForBlock(blockNumb, depth-1));
        parF.mkdirs();
        for (int i=0; i<4; i++){
            try{
                File tmpF=new File(parF.getAbsolutePath()+File.separatorChar+addC[i]);
                final int n = MPI.COMM_WORLD.probe(proc, tag+i).getCount(MPI.BYTE);
                final byte[] arr = new byte[n];                
                MPI.COMM_WORLD.recv(arr, arr.length, MPI.BYTE, proc, tag+i);
                PrintStream ps = new PrintStream(new FileOutputStream(tmpF));           
                ps.write(arr, 0, n);
                ps.flush();
                ps.close();
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }
    }
    
    public final <T extends FileMatrixD> T RecvBlockFMHowNewMatrix(File f, final int proc, final int tag,final Constructor constructor)
    throws Exception{        
        String []addC={"0","1","2","3"};
        f.mkdirs();
        for (int i=0; i<4; i++){
            try{              
                File tmpF=new File(f.getAbsolutePath()+File.separatorChar+addC[i]);                
                final int n = MPI.COMM_WORLD.probe(proc, tag+i).getCount(MPI.BYTE);                
                final byte[] arr = new byte[n];
                MPI.COMM_WORLD.recv(arr, arr.length, MPI.BYTE, proc, tag+i);                
                PrintStream ps = new PrintStream(new FileOutputStream(tmpF));           
                ps.write(arr, 0, n);
                ps.flush();
                ps.close();
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }
        return (T)constructor.newInstance(f, 1);
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
   
    
     private <T extends FileMatrixD> void Send0(final T fm, final int proc, final int tag) throws MPIException, IOException {
        int depth = fm.getDepth();
        MPI.COMM_WORLD.send(new int[]{depth},  1, MPI.INT, proc, tag);     //послал глубину матрицы   2
        if (depth == 0) {
            sendFile(fm.getRoot(), proc, tag);
        } else {
        //    logger.debug("Да был я тут же, хотя не должен был бы");

            for (int i = 0; i < 4; i++) {
                File fromi = new File(fm.getRoot(), i + "");
                if (fromi.exists()) {
                    MPI.COMM_WORLD.send(new int[]{1},  1, MPI.INT, proc, tag);
                    MPI.COMM_WORLD.send((fromi.getName()).toCharArray(),  (fromi.getName()).toCharArray().length, MPI.CHAR, proc, tag);  //послал имя   1
                    Send0((T) new FileMatrixD(fromi, fm.getDepth() - 1), proc, tag);
                } else {
                    MPI.COMM_WORLD.send(new int[]{0},  1, MPI.INT, proc, tag);
                }
            }
        }
    }

    private <T extends SFileMatrix> void Send0(final T fm, final int proc, final int tag) throws MPIException, IOException {
        int depth = fm.getDepth();
        MPI.COMM_WORLD.send(new int[]{depth},  1, MPI.INT, proc, tag);     //послал глубину матрицы   2
        if (depth == 0) {
            sendFile(fm.getRoot(), proc, tag);
        } else {
        //    logger.debug("Да был я тут же, хотя не должен был бы");

            for (int i = 0; i < 4; i++) {
                File fromi = new File(fm.getRoot(), i + "");
                if (fromi.exists()) {
                    MPI.COMM_WORLD.send(new int[]{1},  1, MPI.INT, proc, tag);
                    MPI.COMM_WORLD.send((fromi.getName()).toCharArray(),  (fromi.getName()).toCharArray().length, MPI.CHAR, proc, tag);  //послал имя   1
                    Send0((T) new SFileMatrix(fromi, fm.getDepth() - 1), proc, tag);
                } else {
                    MPI.COMM_WORLD.send(new int[]{0},  1, MPI.INT, proc, tag);
                }
            }
        }
    }

    /**
     * Приёмка FileMatrixL от узла proc отправленного методом SendFM(FileMatrixL
     * m, int proc, int tag).
     * <p/>
     * Вызывает рекурсивный метод Recv_final(File dir, int proc, int tag)
     *
     * @param proc узел от которого идёт посылка
     * @param tag
     * @return возвращает FileMatrixL в базовой матричной дирректории
     * @throws Exception
     */
    public final <T extends SFileMatrix> T Recv(final int proc, final int tag, final Constructor constructor) throws MPIException, IOException {
        T res = null;
        int d[] = new int[1];
        MPI.COMM_WORLD.recv(d, 1, MPI.INT, proc, tag);     //принял глубину матрицы  1
        int depth = d[0];
        if (depth == 0) {
            try {
                MPI.COMM_WORLD.recv(d,  1, MPI.INT, proc, tag);

                File dir = BaseMatrixDir.getRandomDir();
            //    logger.error("name " + dir.getAbsolutePath());

                recvFile(dir, proc, tag, false);
                res = (T) constructor.newInstance(dir, depth);
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

        } else {
            try {
                File dir = BaseMatrixDir.getRandomDir();
                dir.mkdir();
                Recv_final(dir, proc, tag);
                res = (T) constructor.newInstance(dir, depth);
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
        return res;
    }
    
    public final <T extends FileMatrixD> T RecvD(final int proc, final int tag, final Constructor constructor) throws MPIException, IOException {
        T res = null;
        int d[] = new int[1];
        MPI.COMM_WORLD.recv(d, 1, MPI.INT, proc, tag);     //принял глубину матрицы  1
        int depth = d[0];
        if (depth == 0) {
            try {
                MPI.COMM_WORLD.recv(d,  1, MPI.INT, proc, tag);

                File dir = BaseMatrixDir.getRandomDir();
            //    logger.error("name " + dir.getAbsolutePath());

                recvFile(dir, proc, tag, false);
                res = (T) constructor.newInstance(dir, depth);
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

        } else {
            try {
                File dir = BaseMatrixDir.getRandomDir();
                dir.mkdir();
                Recv_final(dir, proc, tag);
                res = (T) constructor.newInstance(dir, depth);
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
        return res;
    }

    /**
     * Приёмка файла от узла proc.
     * <p/>
     * Принимает файл от метода sendFile(File file, int proc, int tag) в
     * дирректорию filePath имя файла приходит вместе с файлом
     *
     * @param filePath дирректорию куда принимается
     * @param proc узел от которого идёт посылка
     * @param tag
     * @throws Exception
     */
    private void recvFile(final File filePath, final int proc, final int tag, final boolean isName)
            throws MPIException, IOException {

        PrintStream ps = null;
        FileOutputStream fos = null;
        try {
         //   logger.debug("Начал писать в файл " + filePath.getAbsolutePath());
            final int n = MPI.COMM_WORLD.probe(proc, tag).getCount(MPI.BYTE);
            final byte[] arr = new byte[n];
            MPI.COMM_WORLD.recv(arr, arr.length, MPI.BYTE, proc, tag);    //принял файл
            char[] name_a = new char[MPI.COMM_WORLD.probe(proc, tag).getCount(MPI.CHAR)];
            MPI.COMM_WORLD.recv(name_a,  name_a.length, MPI.CHAR, proc, tag);   //принял имя 2
            final String name = new String(name_a);
         //   logger.debug("name принял куда писать " + name + "  arr  " + arr.length);

            if (isName) {

                fos = new FileOutputStream(filePath.getAbsolutePath() + "/" + name);
                ps = new PrintStream(fos);
           //     logger.error("записал файл в + name" + filePath.getAbsolutePath() + "/" + name);
            } else {
                fos = new FileOutputStream(filePath.getAbsolutePath());
                ps = new PrintStream(fos);
            //    logger.debug("записал файл в " + filePath.getAbsolutePath());
            }
            ps.write(arr, 0, n);
            ps.flush();
        } finally {
            if (ps != null) {
                ps.flush();

                ps.close();
                ps = null;
         //       logger.debug("закрыл ахахахахаххахаздывпмвчаыеьдэл");
            }
        }

    }

    private void Recv_final(final File dir, final int proc, final int tag) throws MPIException, IOException {
     //   logger.error("косяк");
        int[] depth_a = new int[1];
        MPI.COMM_WORLD.recv(depth_a, 1, MPI.INT, proc, tag);   //принял глубину матрицы  2
        int depth = depth_a[0];
        if (depth == 0) {
            recvFile(dir, proc, tag, true);
        } else {
            for (int i = 0; i < 4; i++) {
                final int check_a[] = new int[1];
                MPI.COMM_WORLD.recv(check_a, 1, MPI.INT, proc, tag);
                final int check = check_a[0];
                if (check == 1) {
                    Status s = MPI.COMM_WORLD.probe(proc, tag);
                    char[] kl = new char[s.getCount(MPI.CHAR)];
                    MPI.COMM_WORLD.recv(kl, kl.length, MPI.CHAR, proc, tag);  //принял имя   1
                    File toi = new File(new String(kl));
                    File temp = new File("" + dir);
                    if (depth > 1) {
                        temp = new File("" + dir + "/" + toi);
                        temp.mkdir();
                    }
                    Recv_final(temp, proc, tag);
                }
            }
        }
    }

    public final void SendMatrixL(final MatrixL m, final int proc, final int tag) throws MPIException {

        if (MPI.COMM_WORLD.getRank() != proc) {
        //    logger.debug("Я начал отправлять в 0");
            MPI.COMM_WORLD.send(new int[]{m.M.length}, 1, MPI.INT, proc, tag);
            for (long[] M : m.M) {
                LongBuffer buf=MPI.newLongBuffer(M.length);
                buf.put(M);
                MPI.COMM_WORLD.iSend(buf,  M.length, MPI.LONG, proc, 456);
            }
        }
    }

    public final MatrixL RecvMatrixL(final int proc, final int tag) throws MPIException {
     //   logger.debug("Да начал приёмку");
        int[] len = new int[1];
        MPI.COMM_WORLD.recv(len,  1, MPI.INT, proc, tag);
     //   logger.debug("принял len " + len[0]);
        long[][] matr = new long[len[0]][];
      //  logger.debug("mars.length " + matr.length);
        for (int i = 0; i < len[0]; i++) {

            matr[i] = new long[MPI.COMM_WORLD.probe(proc, 456).getCount(MPI.LONG)];
            MPI.COMM_WORLD.recv(matr[i], matr[i].length, MPI.LONG, proc, 456);
        }
      //  logger.debug("прорвался");

        return new MatrixL(matr);
    }

    /**
     * Отправка файла узлу proc.
     * <p/>
     * Перекидвает файл в массив byte и отправляет узлу proc. Принимать методом
     * recvFile(File filePath, int proc, int tag)
     *
     * @param file исходный файл
     * @param proc узел которому передаётся файл
     * @param tag
     * @throws Exception
     */
    private void sendFile(final File file, final int proc, final int tag) throws IOException, MPIException {
        final byte[] buf = getBytesFromFile(file);        
        MPI.COMM_WORLD.send(buf, buf.length, MPI.BYTE, proc, tag); //послал файл 1
        final char[] name = file.getName().intern().toCharArray();        
        MPI.COMM_WORLD.send(name,  name.length, MPI.CHAR, proc, tag);   //послал имя 2
    }
    
    private void mySendFile(final File file, final int proc, final int tag) throws IOException, MPIException {
        final byte[] buf = getBytesFromFile(file);        
        MPI.COMM_WORLD.send(buf, buf.length, MPI.BYTE, proc, tag); //послал файл 1        
    }
    
    private void iSendFile(final File file, final int proc, final int tag) throws IOException, MPIException {
        ByteBuffer buf = getBytesFromFileForIsend(file);                
        MPI.COMM_WORLD.iSend(buf, buf.capacity(), MPI.BYTE, proc, tag); //послал файл 1
        final char[] name = file.getName().intern().toCharArray();
        CharBuffer nameBuf=MPI.newCharBuffer(name.length);
        nameBuf.put(name);
        MPI.COMM_WORLD.iSend(nameBuf,  nameBuf.capacity(), MPI.CHAR, proc, tag);   //послал имя 2
    }
    
    public final byte[] getBytesFromFile(final File path) throws IOException {

        InputStream in = null;
        try {
            in = new FileInputStream(path);
            final byte[] buf = new byte[in.available()];
            in.read(buf);
            return buf;
        } finally {
            if (in != null) {
                in.close();
                in = null;
             //   logger.debug("Pакрыл выходной поток");
            }
        }

    }
    
    public final ByteBuffer getBytesFromFileForIsend(final File path) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(path);            
            ByteBuffer buf=MPI.newByteBuffer(in.available());            
            for (int i=0; i<buf.capacity(); i++){
                byte tmp=(byte)in.read();
                buf.put(tmp);
            }
            return buf;
        } finally {
            if (in != null) {
                in.close();
                in = null;
             //   logger.debug("Pакрыл выходной поток");
            }
        }

    }

    /**
     * рассылка массива FileMatrixL[] всем процессорам коммуникатора com от
     * процессора proc.
     * <p/>
     * Запускать на всех процессорах коммуникатора com
     *
     * @param com текущий коммункатор
     * @param mes посылаемая матрица
     * @param proc рассылающий процессор в данном коммуникаторе
     * @return
     * @throws Exception
     */
    public final <T extends SFileMatrix> List<T> Bcast_FM_arr(final Intracomm com, final List<T> mes, final int proc, Constructor<T> constructor) throws MPIException, IOException {

        int[] len = new int[1];
        if (com.getRank() == proc) {
            len[0] = mes.size();
        }
        com.bcast(len, 1, MPI.INT, proc);
        List<T> res = new ArrayList<T>(len[0]);
        for (int i = 0; i < len[0]; i++) {
            res.add((T) Bcast_FM(com, i < mes.size() ? mes.get(i) : null, proc, constructor));
        }
        return res;
    }

    /**
     * рассылка файл матрицы всем процессорам внутри группы коммуникатора com
     *
     * @param com текущий коммуникатор
     * @param mes посылаемая файл матрица
     * @param proc рассылающий процессор в данном коммуникаторе
     */
    public <T extends SFileMatrix> T Bcast_FM(final Intracomm com, final T mes, final int proc, Constructor<T> constructor) throws MPIException, IOException {
        final int depth[] = new int[1];
        final int rank = com.getRank();
        if (rank == proc) {
            depth[0] = mes.getDepth();
        }
        com.bcast(depth, 1, MPI.INT, proc);

        if (depth[0] == 0) {
            final File dir = BaseMatrixDir.getRandomDir();
            File in = null;
            if (com.getRank() == proc) {
                in = mes.getRoot();
            }
            Bcast_File(com, in, dir, proc);

            if (rank == proc) {
                return mes;
            } else {
                try {
                    return (T) constructor.newInstance(dir, 0);
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
        } else {

            int[] len = new int[1];
            if (rank == proc) {
                len[0] = mes.getRoot().getName().toCharArray().length;
            }
            com.bcast(len, 1, MPI.INT, proc);
            char[] nam = new char[len[0]];
            if (rank == proc) {
                nam = mes.getRoot().getName().toCharArray();
            }
            com.bcast(nam, nam.length, MPI.CHAR, proc);
            File dir = null;
            if (rank != proc) {
                dir = new File(BaseMatrixDir.getMatrixDirFile().getAbsolutePath() + "/" + new String(nam));
            } else {
                dir = mes.getRoot();
            }
            if (rank != proc) {
                if (!dir.exists()) {
                    FileUtils.forceMkdir(dir);
                }
            }
            Bcast_Fm_recursiv(com, dir, mes, depth[0], proc, constructor);

            if (rank == proc) {
                return mes;
            } else {
                try {
                    return (T) constructor.newInstance(dir, depth[0]);
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

        }

    }

    public Element Bcast_Element(final Intracomm com, final Element mes, final int proc) throws MPIException, IOException {
        byte[] mail = null;
        int[] length = new int[1];
        if(MPI.COMM_WORLD.getRank() == proc){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(mes);
            mail = bos.toByteArray();
            length[0] = mail.length;

        }
        MPI.COMM_WORLD.bcast(length,  1, MPI.INT, proc);
        if(MPI.COMM_WORLD.getRank() != proc){
           mail = new byte[length[0]];
        }
        MPI.COMM_WORLD.bcast(mail,  mail.length, MPI.BYTE, proc);
        if(MPI.COMM_WORLD.getRank() != proc){
            ByteArrayInputStream bis = new ByteArrayInputStream(mail);
            ObjectInputStream ois = new ObjectInputStream(bis);
            try {
                return (Element) ois.readObject();
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(SendReciveFileMatrixL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            return mes;
        }
        return null;

    }

    /**
     * рекурсивная рассылка файл матрицы в коммуникаторе com в папку dir
     *
     * @param com
     * @param dir
     * @param mes
     * @param depth
     * @param proc
     * @throws Exception
     */
    private <T extends SFileMatrix> void Bcast_Fm_recursiv(final Intracomm com, final File dir, final T mes, final int depth, final int proc, Constructor<T> constructor) throws MPIException, IOException {
        final int[] depth_new = new int[1];
        if (com.getRank() == proc) {
            depth_new[0] = mes.getDepth();
        }
        com.bcast(depth_new,  1, MPI.INT, proc);
        if (depth_new[0] == 0) { //если рекурсия дошла до файла
            final File in;
            if (proc == com.getRank()) {
                in = new File(dir.getAbsolutePath());
            } else {
                in = null;
            }

            Bcast_File(com, in, dir, proc);
        } else {
            for (int i = 0; i < 4; i++) {
                File fromi = null;
                if (MPI.COMM_WORLD.getRank() == proc) {
                    fromi = new File(mes.getRoot(), i + "");
                }
                final int[] len_name = new int[1];
                if (MPI.COMM_WORLD.getRank() == proc) {
                    len_name[0] = mes.getRoot().getName().toCharArray().length;
                }
                com.bcast(len_name, 1, MPI.INT, proc);
                char[] name_a = new char[len_name[0]];
                if (MPI.COMM_WORLD.getRank() == proc) {
                    name_a = mes.getRoot().getName().toCharArray();
                }
                com.bcast(name_a, name_a.length, MPI.CHAR, proc);
                final File temp = new File("" + dir.getAbsolutePath() + "/" + i);
                if (depth_new[0] > 1) {
                    if (!temp.exists()) {
                        FileUtils.forceMkdir(temp);
                    }
                }
                T r = null;
                if (MPI.COMM_WORLD.getRank() == proc) {
                    try {
                        r = (T) constructor.newInstance(fromi, mes.getDepth() - 1);
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
                Bcast_Fm_recursiv(com, temp, r, depth, proc, constructor);
            }
        }
    }

    /**
     * Метод рассылки файла dir внутри группы коммуникатора com.
     * <p/>
     * Запускать на всех узлах в коммуникаторе com
     *
     * @param com
     * @param dir
     * @param proc
     * @throws Exception
     */
    private void Bcast_File(final Intracomm com, final File in, final File dir, final int proc) throws MPIException, IOException {
        FileOutputStream fos = null;
        try {
            final int rank = com.getRank();
            byte[] buf = null;

            if (rank == proc) {
                buf = getBytesFromFile(in);
//                logger.debug("считал " + in.getAbsolutePath());
            }

            com.barrier();

            final int[] countBytes = new int[1];
            if (rank == proc) {
                countBytes[0] = buf.length;
            }

            com.bcast(countBytes,  1, MPI.INT, proc);

            if (rank != proc) {
                buf = new byte[countBytes[0]];
            }
            com.bcast(buf,  buf.length, MPI.BYTE, proc);

            if (rank != proc) {
            //    logger.debug(String.format("Начал создавать в дирректории dir %s на процессоре %s com (%s MPI)", dir.getAbsolutePath(), rank, MPI.COMM_WORLD.Rank()));
                fos = new FileOutputStream(dir);
                fos.write(buf, 0, buf.length);
                fos.flush();

            }
        } catch(Exception e )
        {
            System.out.println("{EQ");
            e.printStackTrace();
        }finally {
            if (fos != null) {
                fos.flush();
                fos.close();
                fos = null;
            }

        }
    }

}
