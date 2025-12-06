package com.mathpar.parallel.webCluster.engine;

import java.io.File;
import java.util.ResourceBundle;

/**
 *
 * @author r1d1
 * <br>
 * В этом классе хранятся все константы, используемые системой webCluster.
 * Общие по смыслу константы имеют одинаковый префикс.
 * Часть констант импортируется из текстового файла с помощью
 * ResourseBundle. Этот текстовый файл лежит в /src/main/resources (в netbeans
 * его можно посмотреть в папке Other Sources.
 */
public class AlgorithmsConfig {
    public static final ResourceBundle MAIN_CONFIG = ResourceBundle.getBundle("webcluster"+File.separator+"main");
    public static final boolean WRITE_SERVER_EXTENTED_LOG;
    private static final String CONFIG_FILE_NAME;
    static {
       WRITE_SERVER_EXTENTED_LOG = Boolean.parseBoolean(MAIN_CONFIG.getString("m.writeServerExtendedLog")); 
       CONFIG_FILE_NAME=MAIN_CONFIG.getString("m.propertiesFileName");
    }
    public static final boolean IS_NEED_SSH_CHEKING;
    
    public static final int CNF_SERV_PORT;    
    public static final String CNF_DATA_PATH;   
    public static final String CNF_MATHPAR_CLASSES;
    public static final String CNF_SERV_HOST;    
    public static final String CNF_SERV_HOST_FOR_NODE;  
    public static final String CNF_MPIEXEC_PATH;
    public static final String CNF_MPICOMPILER_PATH;
    public static final String CNF_MPIJAR_PATH;
    
    public static final String CNF_RUN_MPI_METHOD_NAME;
    public static final String CNF_RUN_COMPILE_METHOD_NAME;
    public static final String CNF_JAVA_PATH;
    public static final String CNF_JAVA_OPTIONS;
    public static final int CNF_MAX_MEMORY;            
    public static final int CNF_MAX_TOTAL_NODES;
    public static final int CNF_MAX_PROCESSES_ON_NODE;
    public static final int CNF_MAX_WALL_TIME;    
    
    public static final long CNF_TASK_LIFETIME;
    public static final long CNF_FILE_LIFETIME;
    public static final long CNF_BACKUP_MAKING_PERIOD;
    public static final long CNF_TRASH_REMOVING_PERIOD;
    
    public static final ResourceBundle EXTRA_CONFIG = ResourceBundle.getBundle("webcluster"+File.separator+CONFIG_FILE_NAME);
    static {        
        IS_NEED_SSH_CHEKING=Boolean.parseBoolean(EXTRA_CONFIG.getString("wp.is_need_ssh_checking"));
        
        CNF_SERV_PORT = Integer.parseInt(EXTRA_CONFIG.getString("cl.port"));        
        CNF_DATA_PATH = EXTRA_CONFIG.getString("cl.dataPath");
        CNF_MATHPAR_CLASSES = EXTRA_CONFIG.getString("cl.mathparClasses");
        CNF_SERV_HOST = EXTRA_CONFIG.getString("cl.serverHost");
        CNF_SERV_HOST_FOR_NODE = EXTRA_CONFIG.getString("cl.serverHostForNode");
        CNF_MPIEXEC_PATH = EXTRA_CONFIG.getString("cl.mpiExec");
        CNF_MPIJAR_PATH = EXTRA_CONFIG.getString("cl.mpiJARPath");
        CNF_MPICOMPILER_PATH=EXTRA_CONFIG.getString("cl.mpiCompiler");
        
        CNF_RUN_COMPILE_METHOD_NAME=EXTRA_CONFIG.getString("s.runCompileMethod");       
        CNF_RUN_MPI_METHOD_NAME=EXTRA_CONFIG.getString("s.runMPIMethod");
        CNF_JAVA_PATH = EXTRA_CONFIG.getString("s.javaExec");                
        CNF_JAVA_OPTIONS = EXTRA_CONFIG.getString("s.javaOptions");
        CNF_MAX_MEMORY=Integer.parseInt(EXTRA_CONFIG.getString("s.maxMemory"));        
        CNF_MAX_TOTAL_NODES = Integer.parseInt(EXTRA_CONFIG.getString("s.npMax"));
        CNF_MAX_PROCESSES_ON_NODE = Integer.parseInt(EXTRA_CONFIG.getString("s.ppnMax"));
        CNF_MAX_WALL_TIME = Integer.parseInt(EXTRA_CONFIG.getString("s.walltimeMax"));
        
        CNF_TASK_LIFETIME=Long.parseLong(EXTRA_CONFIG.getString("s.taskDataLifetime"));        
        CNF_FILE_LIFETIME=Long.parseLong(EXTRA_CONFIG.getString("s.fileFiletime"));
        CNF_BACKUP_MAKING_PERIOD=Long.parseLong(EXTRA_CONFIG.getString("s.backupMakingPeriod"));
        CNF_TRASH_REMOVING_PERIOD=Long.parseLong(EXTRA_CONFIG.getString("s.trashRemovingPeriod"));

    }

    public final static int RES_SUCCESS = 0;
    public final static int RES_SERIALIZE_ERROR = 1;
    public final static int RES_DATA_WRITING_ERROR = 2;
    public final static int RES_RESULT_WRITING_ERROR = 3;
    public final static int RES_WRONG_DATA_ERROR = 4;
    public final static int RES_WRONG_ALGO_CONFIG_ERROR = 5;
    public final static int RES_CREATE_PBS_FILE_ERROR = 6;
    public final static int RES_CONNECT_ERROR = 7;
    public final static int RES_SEND_DATA_ERROR = 8;
    public final static int RES_RECV_RESULT_ERROR = 9;
    public final static int RES_DATA_READING_ERROR = 10;
    public final static int RES_NO_RESULT_ERROR = 11;
    public final static int RES_NO_TASK_ERROR = 12;
    public final static int RES_READING_RESULT_FILE_ERROR = 13;
    public final static int RES_CRASH_TASK_ERROR=14;
    public final static int RES_FILE_NOT_FOUND_ERROR=15;
    public final static int RES_SSH_CHECKING_ERROR=16;
    public final static int RES_INCORRECT_NUMBER_OF_NODES=17;
    public final static int RES_INCORRECT_NUMBER_PROC_PER_NODE=18;
    public final static int RES_INCORRECT_MEMORY_SETTINGS=19;
    public final static int RES_INCORRECT_WALL_TIME=20;
    public final static String[] respNames = {
        "Successful  launch",
        "Serialize  error",
        "Data  writing  error",
        "Result  writing  error",
        "Incorrect  data  error",
        "Incorrect  algorithm  configuration  error",
        "Creating  PBS  file  error",
        "Connection  to  server  error",
        "Sending  of  data  to  server  error",
        "Receiving  of  data  from  server  error",
        "Data  reading  error",
        "Result  isn't  ready",
        "This  task  not  exist",
        "Reading  result  file  error",
        "Stderr  is  not  empty,  check  it  with  getErr  function",
        "This  file  not  found  on  cluster  in  your  folder",
        "This  user  is  not  a  valid  user  of  unihub  cluster",
        "Incorrect number of nodes",
        "Incorrect number of processes per node",
        "Incorrect memory settings",
        "Incorrect wall time settings"
    };

    public final static int ECHO_CONST=999999;
    public final static String RAW_TEXT="  text  ";
 /**
  *
  * @param userID ID пользователя
  * @param taskID ID задачи
  * @return строка, хранящая путь до папки с заданной задачей.
  */
    static public String getPathForUserFold(Integer userID, Integer taskID) {
        String path=AlgorithmsConfig.CNF_DATA_PATH;        
        path += "/user" + String.valueOf(userID) + "/task" + String.valueOf(taskID);
        return path;
    }
    
    /**
  *
  * @param userID ID пользователя
  * @return строка, хранящая путь до папки заданного пользователя.
  */
    static public String getPathForUserFold(Integer userID) {
        String path=AlgorithmsConfig.CNF_DATA_PATH;        
        path += "/user" + String.valueOf(userID);
        return path;
    }

    /* Команды mathpar:
     * \matMultPar1x8(M1,M2)  матричное умножение 1х8 \matMultPar1x8([[1,2],[3,4]],[[1,0],[0,1]]);
     * \polFactorPar(P)       факторизация полинома
     * \charPolPar(M)         хар. полином для матрицы   SPACE = Z[x];
     *                                                   \charPolPar([[1, 4], [4, 5]]);
     * \getStatus(taskID)     узнать статус задачи
     * \getCalcResult(taskID) получить результат вычислений
     * \runClass(class with main path,param1,param2,...); выполнить параллельно выбранный класс \runClass(parallel.webCluster.algorithms.test.Test,123,456,789);
     * \ uploadToCluster(file name); - скопировать файл с вебчасти на кластер
     * \runUploadedClass(archieve name, class path, param0, param1,...); запустить загруженный класс \runUploadedClass(test.zip,test.err,23,252);
     * \getErr(task numb); получить содержимое файла, в который был перенаправлен поток ошибок
     * \getOut(task numb); получить содержимое файла, в который был перенаправлен стандартный поток вывода
     * \showTaskList();
     * \showFileList();
     */
    //состояния задач
    public static final int ST_IN_QUEUE = 0;
    public static final int ST_RUNNING = 1;
    public static final int ST_FINISHED = 2;
    public static final int ST_NOT_EXIST = 3;
    public static final int ST_CRASH=4;
    public static String[] stateNames = {
        "Task  is  in  queue",
        "Task  is  running",
        "Task  is  finished",
        "This  task  is  not  exist",
        "stderr  is  not  empty,  check  it  with  getErr  function"
    };
    //типы запросов к серверу
    public static final int QS_ADD_TASK = 0;
    public static final int QS_GET_STATE = 1;
    public static final int QS_GET_RESULT = 2;
    public static final int QS_CANCEL_TASK = 3;
    public static final int QS_CHANGE_STATE = 4;
    public static final int QS_GET_DATA_FOR_CALC = 5;
    public static final int QS_SAVE_CALC_RESULT = 6;
    public static final int QS_SEND_CALC_RESULT_TO_WEB = 7;
    public static final int QS_UPLOAD_FILE_TO_CLUSTER = 8;
    public static final int QS_GET_STATE_LIST=9;
    public static final int QS_GET_FILE_CONTENT=10;
    public static final int QS_GET_FILE_LIST=11;
    public static final int QS_UPLOAD_FILE_TO_CLUSTER_AND_COMPILE = 12;
    public static final int QS_SEND_MESSAGE_ABOUT_FINISH = 13;
    //добавление новых алгоритмов - нужно добавить 2 константы для каждого (номер и путь к его файлам
    public static final int AN_MULT_MATRIX_1x8 = 0;
    public static final int AN_MULT_MATRIX_2x4 = 1;
    public static final int AN_FACTOR_POL = 2;
    public static final int AN_CHAR_POL = 3;    
    public static final int AN_RUN_UPLOADED_CLASS = 5;
    public static final int AN_MULT_POLYNOM = 6;
    public static final int AN_ADJOINT_DET = 7;
    public static final int AN_BELLMAN_EQUATION1 = 8;
    public static final int AN_BELLMAN_EQUATION2 = 9;
    public static final int AN_BELLMAN_INEQUALITY1 = 10;
    public static final int AN_BELLMAN_INEQUALITY2 = 11;
    public static final int AN_ETD = 12;
    public static final String MULT_MATRIX_1x8_PATH = "com.mathpar.parallel.webCluster.algorithms.multMatrix1x8.Main";
    public static final String FACTOR_POL_PATH = "com.mathpar.parallel.webCluster.algorithms.polFactorization.test_1";
    public static final String CHAR_POL_PATH = "com.mathpar.parallel.webCluster.algorithms.characteristicPol.characteristicPolynomial";
    public static final String MULT_POLYNOM_PATH = "com.mathpar.parallel.webCluster.algorithms.multPolynom.Main";
    public static final String ADJOINT_DET_PATH = "com.mathpar.parallel.webCluster.algorithms.Adjoint.AdjointDetParallel";
    public static final String BELLMAN_EQUATION1_PATH = "com.mathpar.parallel.webCluster.algorithms.tropic.bellmanEquation1";
    public static final String BELLMAN_EQUATION2_PATH = "com.mathpar.parallel.webCluster.algorithms.tropic.bellmanEquation2";
    public static final String BELLMAN_INEQUALITY1_PATH = "com.mathpar.parallel.webCluster.algorithms.tropic.bellmanInequality1";
    public static final String BELLMAN_INEQUALITY2_PATH = "com.mathpar.parallel.webCluster.algorithms.tropic.bellmanInequality2";
    public static final String ETD_PATH = "com.mathpar.matrix.LSU.ETD";
}
