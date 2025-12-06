
package com.mathpar.matrix.file.utils;


/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ParCA</p>
 *
 * @author Yuri Valeev
 * @version 2.0
 */
public class Path {
    private int[] path;

    /**
     * Пустой путь.
     */
    public Path(){
        path=new int[0];
    }

    /**
     * К пути добавляет число.
     * @param prevPath Path
     * @param a int
     */
    public Path(Path prevPath, int a){
        int[] ppath=prevPath.path;
        path=new int[ppath.length+1];
        System.arraycopy(ppath,0,path,0,ppath.length);
        path[path.length-1]=a;
    }

    /**
     * Является ли путем к диагональному блоку <=> путь состоит из 0,3.
     * @return boolean
     */
    public boolean isDiagonal(){
        for (int i = 0; i < path.length; i++) {
            if (path[i]!=0 && path[i]!=3) {
                return false;
            }
        }
        return true;
    }
}
