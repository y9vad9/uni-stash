
package com.mathpar.polynom.file;

import java.io.File;
import java.io.*;
import com.mathpar.polynom.file.util.FileUtils;


/**
 *
 * @author student
 */
public class BasePolynomDir {

    private static String polynomDir;

    private static File polynomDirFile;

    /** Создается базовая директория, в зависимости от типа системы */
    static {
        String osname = System.getProperty("os.name");
        String mdir;
        if (osname.indexOf("Windows") != -1) {
            mdir = "C:\\temp\\fpolynoms";
        } else {
            mdir = "/tmp/fpolynoms/";
        }
        try {
            setPolynomDirectory(mdir);
        }
        catch (IOException ex) {
            throw new RuntimeException("I/O error",ex);
        }
    }


    /**
     * устанавливает новое имя базовой файловой директории
     * @param dir String
     * @throws IOException
     */
    public static void setPolynomDirectory(String dir)
        throws IOException{
        polynomDir=dir;
        polynomDirFile=new File(polynomDir);
        if (!polynomDirFile.exists()){
          polynomDirFile.mkdirs();
        } else {
            if (polynomDirFile.isFile()) {
                throw new IOException(String.format(
                    "Cannot create base directory for file matrices '%s': file with this name exists.",
                    dir));
            }
        }
    }


    /**
     * возвращает имя базовой директории, типа File
     * @return File
     */
    public static File getPolynomDirFile(){
        return polynomDirFile;
    }

    /**
     * создается директория с заданным именем для файлового полинома
     * @return String
     * @throws IOException
     */
    public static File createPolynomDir(String dirName) throws IOException {
        File f= new File(polynomDirFile, dirName);
        FileUtils.delete(f);
        f.mkdir();
        return f;
    }

    /**
     * создается директория со случайным именем для файлового полинома
     * @return String
     * @throws IOException
     */
    public static File createRandomDir() throws IOException {
        File f=File.createTempFile("pol", "", polynomDirFile);
        f.delete();
        f.mkdir();
        return f;
    }


    /**
     * процедура удаляет содержимое базовой директории
     * @return boolean
     * @throws IOException
     */
    public static boolean clearPolynomDir() throws IOException{
        boolean res=FileUtils.delete(polynomDirFile);
        setPolynomDirectory(polynomDir);
        return res;
    }





}
