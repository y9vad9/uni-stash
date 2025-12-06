
package com.mathpar.matrix.file.utils;

import java.io.File;
import java.io.IOException;

/**
 * <p>Title: ParCA</p>
 * <p/>
 * <p>Description: ParCA - parallel computer algebra system</p>
 * <p/>
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006,2007</p>
 * <p/>
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class BaseMatrixDir {

    public static String RANDOM_PREFIX = "matr";
    /**
     * Директория, в которую сохраняются матрицы в методах с автоматической
     * генерацией имен.
     */
    private static String matrsDir;
    private static File matrsDirFile;



    /**
     * Изменить базовую директорию для файловых матриц.
     *
     * @param dir String
     * @throws IOException
     */
    public static void setMatrixDirectory(String dir)
            throws IOException {
        matrsDir = dir;
        matrsDirFile = new File(matrsDir);
        if (!matrsDirFile.exists()) {
            FileUtils.mkdirs(matrsDirFile);
        } else {
            if (matrsDirFile.isFile()) {
                throw new IOException(String.format(
                        "Cannot create base directory for file matrices '%s': file with this name exists.",
                        dir));
            }
        }
    }

    /**
     * Возвращает базовую директорию.
     *
     * @return File
     */
    public static File getMatrixDirFile() {
        return matrsDirFile;
    }

    /**
     * Очистить базовую директорию.
     *
     * @return boolean
     * @throws IOException
     */
    public static boolean clearMatrixDir() throws IOException {
        boolean res = FileUtils.deleteRecursive(matrsDirFile);
        setMatrixDirectory(matrsDir);
        return res;
    }

    /**
     * Создает директорию со случайным и уникальным именем в базовой директории
     * и возвращает ее файловый объект.
     *
     * @return String
     * @throws IOException
     */
    public static File createRandomDir() throws IOException {
        File f = File.createTempFile(RANDOM_PREFIX, "", matrsDirFile);
        f.delete();
        FileUtils.mkdir(f);
        return f;
    }

    /**
     * Возвращает файловый объект для файла со случайным и уникальным именем в
     * базовой директории.
     *
     * @return String
     * @throws IOException
     */
    public static File getRandomDir() throws IOException {
        File f = File.createTempFile(RANDOM_PREFIX, "", matrsDirFile);
        f.delete();
        return f;
    }

}
