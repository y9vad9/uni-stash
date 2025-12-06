
package com.mathpar.matrix.file.utils;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006,2007</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class FileUtils {


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


    public static void delete(File f)
        throws IOException{
//        boolean ok=f.delete();
//        if (!ok) {
//            throw new IOException(
//                String.format("Cannot delete '%s'", f));
//        }
    }



    /**
     * Создать директорию для файловой матрицы.<br>
     * Если глубина > 0, то если не существует, то создать, если существует, то
     * возможны 2 варианта:<br>
     * 1) существует файл -- это ошибка,<br>
     * 2) существует директория -- ничего не делать.
     * @param dir File
     * @param depth int
     * @throws IOException
     */
    public static void createDir(File dir, int depth) throws IOException{
        //создать директорию dir, если глубина>0 и если не существует
        if (depth>0) {
            if (!dir.exists()) {
                mkdirs(dir);
            } else {
                //существует
                if (dir.isFile()) {
                    throw new IOException(String.format(
                        "Cannot create directory '%s': file with this name exists.", dir));
                }
            }
        } else {
            //глубина = 0, то проверить, что файл dir не существует
            if (dir.exists()) {
                throw new IOException(String.format(
                    "Cannot create file '%s': file or directory with this name exists.", dir));
            }
        }
    }



    public static int DEFAULT_BUF_SIZE=1<<10; //1Мб

    public static void copyFile(File from, File to)
        throws IOException{
        copyFile(from,to,DEFAULT_BUF_SIZE);
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



    /**
     * Процедура удаления файла или рекурсивного удаления каталога. Если хотя бы один объект не удалился,
     * то возвращается <tt>false</tt>, если все удалилось успешно, то возвращается <tt>true</tt>.
     * @param file типа File, объект для удаления (файл или директория).
     * @return boolean
     * @throws IOException
     */
    public static boolean deleteRecursive(File file) throws IOException {
        if (!file.exists()){
            return true;
        }
        if (file.isFile())
            return file.delete();
        File[] innrObj = file.listFiles();
        boolean flag = true;
        for (int i = 0; i < innrObj.length; i++) {
            flag = flag && deleteRecursive(innrObj[i]);
        }
        flag &= file.delete();
        return flag;
    }

}
