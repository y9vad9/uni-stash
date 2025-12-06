package com.mathpar.parallel.utils.parallel_debugger;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import mpi.*;

/**
 * Класс для параллельной отладки программ.
 * В данном классе реализованы методы, которые позволяют
 * добавить событие в протокол отладки. Протокол сначала
 * пишется по каждому процессору отдельно,
 * затем происходит их слияние в один файл.
 *
 * Пример программного кода:
 * <pre>
 * {@code
 * ParallelDebug.init(MPI.COMM_WORLD);
 * ParallelDebug.addEvent("some label", "some text");
 * try {
 * //некоторая программная ллогика
 * //......
 * //......
 * } catch (Exception ex) {
 * ex.printStackTrace();
 * }
 * //эта функция должна быть вызвана на каждом процессоре коммуникатора
 * ParalellDebug.generateDebugLog();
 * </pre>
 */
public class ParallelDebug {

    public int size, //номер текущего процессора
               rank; //количество процессоров в коммуникаторе            
    String debugFile = "/tmp/proc",//папка для записи файлов событий на каждом процессоре
           debugFilePostFix = "pdb";//расширение файлов с событиями
    /**время начала отладки */
    private long debugStartTimeStamp;
    /**закрыт ли поток записи в файл */
    private boolean isClosed = false;
    /** поток записи в файл */
    ObjectOutputStream outStream;
    /** браузер для открытия протокола */
    private static String defaultBrowser = "firefox";

    public void setDefaultBrowser(String defaultBrowser) {
        ParallelDebug.defaultBrowser = defaultBrowser;
    }

    /**
     * Инициализация параллельной отладки
     * @param comm - коммуникатор,
     * в котором выполняется отладка
     */


    /**
     * Добавление события в протокол
     * @param label - метка
     * @param event - текст события
     */


    /**
     * Генерирование протокола отладки в формате
     * xhtml 1.0 Transitional
     * с последующим открытием файла в браузере
     */
    public void generateDebugLog_ex() throws MPIException, java.io.IOException {
        // if (isDebugActive) {
        //    stdDebug.pgenerateDebugLog(true);
        //}
        if (rank == 0) {
            for (int i = 1; i < size; i++) {
                char[] message = new char[30];
                MPI.COMM_WORLD.recv(message, message.length,  MPI.CHAR, i, 50);
                System.out.println("pos=" + i);
            }
            pgenerateDebugLog(true);
        } else {
            pgenerateDebugLog(true);
            char[] message = ("from " + rank).toCharArray();
            MPI.COMM_WORLD.send(message, message.length,  MPI.CHAR, 0, 50);
            System.out.println("ot=" + rank);
        }
    }

    
    public void generateDebugLog() throws MPIException{
        // if (isDebugActive) {
        //    stdDebug.pgenerateDebugLog(true);
        //}
        if (rank == 0) {
            for (int i = 1; i < size; i++) {
                char[] message = new char[30];
                MPI.COMM_WORLD.recv(message, message.length,  MPI.CHAR, i, 50);
                System.out.println("pos=" + i);
            }
            pgenerateDebugLog(true);
        } else {
            pgenerateDebugLog(true);
            char[] message = ("from " + rank).toCharArray();
            MPI.COMM_WORLD.send(message, message.length,  MPI.CHAR, 0, 50);
            System.out.println("ot=" + rank);
        }
    }

    /**
     * Генерирование протокола отладки в формате
     * xhtml 1.0 Transitional
     * @param isWantOpen - нужно ли
     * открывать файл в браузере.
     */


//-------------------------Частные методы------------------------

    /*
     * конструктор класса
     * принимает коммуникатор,
     * в котором выполняется отладка кода
     */
    public ParallelDebug(Intracomm comm)
            throws MPIException {
        this.size = comm.getSize();
        this.rank = comm.getRank();
        debugStartTimeStamp = System.currentTimeMillis();
        try {
            outStream =
                    new ObjectOutputStream(
                    new FileOutputStream(
                    debugFile + rank + "." + debugFilePostFix));
        } catch (IOException ex) {
            Logger.getLogger(
                    ParallelDebug.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    /*
     * Добавление события в протокол
     */
    public void paddEvent_ex(String label, String event) throws java.io.IOException {
        outStream.writeObject(
                    new DebugEvent(label, event, rank,                           
                            System.currentTimeMillis()-debugStartTimeStamp));
//        } catch (IOException ex) {
//            Logger.getLogger(ParallelDebug.class.getName()).log(Level.SEVERE, null, ex);}
    }

     public void paddEvent(String label, String event) {
        try{outStream.writeObject(
                    new DebugEvent(label, event, rank,                           
                            System.currentTimeMillis()-debugStartTimeStamp));
        } catch (IOException ex) {
            Logger.getLogger(ParallelDebug.class.getName()).log(Level.SEVERE, null, ex);}
    }
    /*
     * Закрытие протокола записи в файл
     */
    public void pcloseDebug() {
        paddEvent("system", "Debug closed in programm");
        try {outStream.close(); isClosed = true;
        } catch (IOException ex) {
            Logger.getLogger(ParallelDebug.class.getName()).log(Level.SEVERE, null, ex);}
    }
    /**
     * Вывод протокола отладки в файл runlog.html      */
    public void pgenerateDebugLog(boolean isWantOpen){
        if (!isClosed) { pcloseDebug(); }
        if (rank == 0) {
            try { PrintWriter out = new PrintWriter( new FileOutputStream("runlog.html"));
                DebugEvent[] res = psort(pgetDebugData());
//------------- пишем html шапку
                out.println(
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                        + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0"
                    + " Transitional//EN\""
                        + " \"http://www.w3.org/TR/xhtml1/"
                        + "DTD/xhtml1-transitional.dtd\">"
                        + "<html xmlns=\"http://www.w3.org/1999/xhtml\" "
                        + "xml:lang=\"ru\" "
                        + "lang=\"ru\">");
                out.println("<head><title>Run log</title></head>");
                out.println("<body>");
                out.println("<table style=\"width:100% \">");
                out.println("<tr><td>Time stamp, ms</td>"
                        + "<td>Processor rank"
                        + "</td><td>Label</td><td>Message</td></tr>");
                String[] colors = new String[]{
                    "background-color: #d1e187",
                    "background-color: white"};
                int k = 0;
                for (DebugEvent de : res) {
                    out.print(de.toString(colors[k]));
                    k = (k + 1) & 1;
                }
                out.println("</table>");
                out.println("</body></html>");
                out.close();
                if (isWantOpen) {
                    try {
                        Runtime.getRuntime().
                                exec(defaultBrowser + " runlog.html");
                    } catch (IOException ex) {
                        Logger.getLogger(ParallelDebug.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ParallelDebug.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

        }

    }

    /**
     * Сортировка массива событий со всех процессоров
     */
    public DebugEvent[] psort(ArrayList<DebugEvent> data) {
        DebugEvent[] ar = new DebugEvent[data.size()];
        data.toArray(ar);
        Arrays.sort(ar);
        return ar;
    }

    /**
     * Чтение файлов событий с каждого  процессора в массив  */
    public ArrayList<DebugEvent> pgetDebugData() {
        ArrayList<DebugEvent> data = new ArrayList<DebugEvent>();
        for (int i = 0; i < size; i++) {
            try {ObjectInputStream in = new ObjectInputStream(
                        new FileInputStream(debugFile+i+"."+debugFilePostFix));
                try {while (true) {
                        try { data.add((DebugEvent) in.readObject());
                        } catch (ClassNotFoundException ex) {
                                Logger.getLogger(ParallelDebug.class.getName()).log(Level.SEVERE, null, ex);}}
                } catch (EOFException ex1) {}
            } catch (IOException ex) {Logger.getLogger(ParallelDebug.class.getName()).log(Level.SEVERE,null, ex); }
        }return data;
    }
}