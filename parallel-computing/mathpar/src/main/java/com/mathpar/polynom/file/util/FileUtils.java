package com.mathpar.polynom.file.util;

import java.io.File;
import java.io.*;

import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.file.FPolynom;
import com.mathpar.number.*;
import java.lang.String;
/**
 *
 * @author student
 */
public class FileUtils {

    /**
      * Процедура удаления файла или рекурсивного удаления каталога. Если хотя бы один объект не удалился,
      * то возвращается <tt>false</tt>, если все удалилось успешно, то возвращается <tt>true</tt>.
      * @param file типа File, объект для удаления (файл или директория).
      * @return boolean
      * @throws IOException
      */
     public static boolean delete(File file) throws IOException{
      if(!file.exists()) return true;
      if(file.isFile()) return file.delete();
      File[] innrObj = file.listFiles();
      boolean flag = true;
      for(int i=0; i< innrObj.length; i++){
        flag = flag && delete(innrObj[i]);
      }
      flag &= file.delete();
      return flag;
    }



    public static boolean delete(File[] file) throws IOException{
      boolean flag = true;
      for(int i=0; i< file.length; i++){
        flag = flag && delete(file[i]);
      }
      return flag;
    }

    /**
    * Удаление файла с указанным путем
    * @param s типа String, файл
    */
    public static void deleteFiles(String s) throws IOException{
      File f = new File(s);
      delete(f);
    }

    public static void mkdir(File dir)
        throws IOException{
        boolean created=dir.mkdir();
        if (!created) {
            throw new IOException(String.format(
                "Cannot create directory: '%s': cannot write or exists", dir));
        }
    }


    public static void mkdirs(File dir)
        throws IOException{
        boolean created=dir.mkdirs();
        if (!created) {
            throw new IOException(String.format(
                "Cannot create directory: '%s': cannot write or exists", dir));
        }
    }


    public static void mkdirEx(File dir)
        throws IOException{
        if (dir.exists()) {
            if (dir.isFile()) {
                throw new IOException(String.format(
                    "Cannot create directory: '%s': file with this name exists", dir));
            }
        } else {
            mkdir(dir);
        }
    }


    public static void move(File f1, File f2)
        throws IOException{
        boolean ok=f1.renameTo(f2);
        if (!ok) {
            throw new IOException(
                String.format("Cannot move '%s' to '%s'", f1, f2));
        }
    }




    public static void createDir(File dir, int depth) throws IOException{
        if (depth>0) {
            if (!dir.exists()) {
                mkdirs(dir);
            } else {
                if (dir.isFile()) {
                    throw new IOException(String.format(
                        "Cannot create directory '%s': file with this name exists.", dir));
                }
            }
        } else {
            if (dir.exists()) {
                throw new IOException(String.format(
                    "Cannot create file '%s': file or directory with this name exists.", dir));
            }
        }
    }



    public static int DEFAULT_BUF_SIZE=1<<10; //1??

    public static void copyFile(File from, File to)
        throws IOException{
        copyFile(from,to,DEFAULT_BUF_SIZE);
    }

    public static void copyFile(String from, File to)
        throws IOException{
        copyFile(from,to);
    }

    public static void copyFile(File from, File to, int bufLen)
        throws IOException{
        copyFile(from,to,new byte[bufLen]);
    }

    public static void copyFile(File from, File to, byte[] buf)
        throws IOException{
        FileInputStream in=new FileInputStream(from);
        FileOutputStream out=new FileOutputStream(to);
        while (in.available()!=0) {
            int n=in.read(buf);
            out.write(buf,0,n);
        }
        in.close();
        out.close();
    }


    public static byte[] toByteArray(Object s) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(s);
            byte[] arr = baos.toByteArray();
            //System.out.println("numberOfVar = "+baos.numberOfVar());
            baos.flush(); baos.close(); out.close();
            return arr;
        } catch (IOException ex) {
            throw new RuntimeException("I/O error", ex);
        }

    }

    public static Object fromByteArr(byte[] b) throws IOException{
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object s = ois.readObject();
            bais.close(); ois.close();
            return s;
        } catch (Exception ex) {
            throw new RuntimeException("Error translate arrays to Element!", ex);
        }
    }

    /**
     * процедура переводит полином из текстового файла в формат файлового полинома Fpolynom
     * @param dirPol - директория где находится текстовый файл
     * @param dirFpol - директория, куда будет помещен файловый полином
     * @return
     * @throws Exception
     */
    public static FPolynom FiletoFPolynom(File dirPol, File dirFpol, long quantum) throws Exception{
      FileReader r_pol = new FileReader(dirPol);
      BufferedReader br = new BufferedReader(r_pol);
      String s = "";
      String  polstring = "";
      while((s = br.readLine())!=null) polstring = polstring + s;
      Polynom pol = new Polynom(polstring, Ring.ringR64xyzt);
      return pol.toFPolynom(dirFpol, NumberZ.ONE, quantum);
    }

    /**
     * процедура переводит полином из текстового файла в формат файлового полинома Fpolynom
     * @param dirPol - директория где находится текстовый файл
     * @param dirFpol - директория, куда будет помещен файловый полином
     * @return
     * @throws Exception
     */
    public static FPolynom[] FiletoFPolynoms(File dirPol, File dirFpol1, File dirFpol2, long quantum) throws Exception{
      FileReader r_pol = new FileReader(dirPol);
      BufferedReader br = new BufferedReader(r_pol);
      String s = "", text = "";
      while((s = br.readLine())!=null) text = text + s;
      char[] c = text.toCharArray();
      String[] polstring = new String[2];
      polstring[0]=""; polstring[1]="";
      int j=0, k=0;
      //for(int j=0; j<c.length-1; j++){
        while(c[j]!=';'){ polstring[0] = polstring[0] + c[j];j++;}
        j++;
        while(c[j]!=';'){ polstring[1] = polstring[1] + c[j]; j++;}
      //}
      Polynom[] pol = new Polynom[2];
      FPolynom[] fpol = new FPolynom[2];
      pol[0] = new Polynom(polstring[0], Ring.ringR64xyzt);
      pol[1] = new Polynom(polstring[1], Ring.ringR64xyzt);
      fpol[0] = pol[0].toFPolynom(dirFpol1, NumberZ.ONE, quantum);
      fpol[1] = pol[1].toFPolynom(dirFpol2, NumberZ.ONE, quantum);
      return fpol;
    }


     // считает количество байт занимаемое массивом объектов на диске
    public static int ObjectsSize(Object[] obj)throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(baos);
            for(int i=0; i<obj.length; i++) out.writeObject(obj[i]);
            int length = baos.size();
            baos.flush(); baos.close(); out.close();
            return length;
        } catch (IOException ex) {
            throw new RuntimeException("I/O error", ex);
        }
    }

    // считает количество байт занимаемое объектом на диске
    public static int ObjectSize(Object obj)throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(obj);
            int length = baos.size();
            //System.out.println("l = "+baos.numberOfVar());
            baos.flush(); baos.close(); out.close();
            return length;
        } catch (IOException ex) {
            throw new RuntimeException("I/O error", ex);
        }
    }

}
