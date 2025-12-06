
package com.mathpar.matrix.file.mmio;

import java.io.*;
import com.mathpar.number.*;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class CFRText implements CoordFileReader{

    protected BufferedReader reader;

    protected CFRText(InputStream is){
        reader=new BufferedReader(new InputStreamReader(is));
    }

    protected CFRText(Reader r){
        reader=new BufferedReader(r);
    }


    protected CFRText(File in) throws MMIOException{
        try {
            reader = new BufferedReader(new FileReader(in));
        } catch (FileNotFoundException ex) {
            throw new MMIOException(String.format("File '%s' not found", in),ex);
        }
    }

    /**
     * Возвращает {m,n,nz}, m,n -- кол-во строк и столбцов,
     * nz -- кол-во ненулевых.
     * @return int[]
     */
    public int[] readHeader() throws MMIOException{
        //прочитать строку и проверить на пустоту файла
        String line=readNextLineSkip();
        if (line==null) {
            throw new MMIOException("File is empty: only empty lines and comments");
        }
        //разбить по пробелам и проверить, что 3 подстроки
        String[] substrs=line.split("\\s+");
        if (substrs.length!=3) {
            throw new MMIOException(String.format(
                "In header must be 3 int numbers, but found %d numbers", substrs.length));
        }
        //{"m","n","nz"} --> {m,n,nz} (int[])
        int[] res=new int[3];
        try {
            for (int i = 0; i < 3; i++) {
                res[i] = Integer.parseInt(substrs[i]);
            }
        } catch (NumberFormatException ex) {
            throw new MMIOException("In header must be 3 int numbers",ex);
        }
        return res;
    }


    /**
     * Возвращает следующую строку.
     * null -- нет строк,
     * объект CoordLine -- следующая строка.
     * @return CoordLine
     */
    public CoordLine readLine() throws MMIOException{
        //Прочитать следующую строку line. Если ее нет, то null.
        String line=readNextLineSkip();
        if (line==null) {
            return null;
        }
        //line=">=3 подстроки" или "<3 подстроки"
        //line="i j a_{ij}"
        //разбить line по пробелам на <=3 подстроки: "i","j","a_{ij}"
        String[] substrs=line.split("\\s+",3);
        if (substrs.length<3) {
            throw new MMIOException(String.format(
                "Must be: i j a_{ij}, but found %d elems", substrs.length));
        }

        //"i","j","a_{ij}" --> i(int),j(int),a_{ij}(Element)
        int i = 0;
        int j = 0;
        try {
            i = Integer.parseInt(substrs[0]);
            j = Integer.parseInt(substrs[1]);
        } catch (NumberFormatException ex) {
            throw new MMIOException("Must be integer", ex);
        }
        Element el=toScalar(substrs[2]);
        return new CoordLine(i,j,el);
    }

    /**
     * Закрыть.
     */
    public void close()  throws MMIOException{
        try {
            reader.close();
        } catch (IOException ex) {
            throw new MMIOException("I/O error", ex);
        }
    }


    /**
     * Читает следующую строку, пропуская:
     * 1) %%текст -- комментарий
     * 2) пустые строки
     * @return String если нет строк -- null, если есть, то не null.
     */
    private String readNextLineSkip() throws MMIOException{
        try {
            String line;
            while ( (line = reader.readLine()) != null &&
                   (isSpaces(line) || isComment(line))) {
                //пока есть строчки (line!=null) и line -- пробелы или коммент.
            }
            //line==null(нет строк) или
            //line!=null (есть строчка) и line -- не пробелы и не комментарий
            return line;
        } catch (IOException ex) {
            throw new MMIOException("I/O error", ex);
        }
    }


    //комментарий <=> %%...
    private static boolean isComment(String s){
        return s.startsWith("%%");
    }

    //пробелы <=> s.trim() -- пустая
    private static boolean isSpaces(String s){
        return s.trim().length()==0;
    }


    /**
     * Конвертирует строку a_ij --> Element.
     * Например:
     * "double" --> NumberR
     * "double" "double" --> NumberC
     * @param str String
     * @return Element
     */
    protected abstract Element toScalar(String str) throws MMIOException;


}
