package com.mathpar.students.OLD.llp2.student.Shcherbinin;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ridkeim
 */
public class testMass {
        public static boolean test(int[][] a, int[][] b){
        boolean flag = true;
            for (int i = 0; i < b.length; i++) {
                  for (int j = 0; j < b[i].length; j++) {
                    if (a[i][j] != b[i][j]){
                        flag = false;
                        break;
                    }
                  }
                  if(!flag){
                      break;
                  }
            }
            return flag;
    }
}
