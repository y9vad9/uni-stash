/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.stat.FMD.MultFMatrix;

import java.io.Serializable;

import com.mathpar.matrix.file.sparse.SFileMatrix;

/**
 *
 * @author vladimir
 */
public final class MultiplyResult<T extends SFileMatrix> implements Serializable{
    /**
     * матрица ответа доступна только на рассылающем процессоре
     */
    T resultMult;
     /**
      * матрица подблок доступна на каждом процессоре
      */
    T matrixInNode;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.resultMult != null ? this.resultMult.hashCode() : 0);
        hash = 37 * hash + (this.matrixInNode != null ? this.matrixInNode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MultiplyResult other = (MultiplyResult) obj;
        if (this.resultMult != other.resultMult && (this.resultMult == null || !this.resultMult.equals(other.resultMult))) {
            return false;
        }
        if (this.matrixInNode != other.matrixInNode && (this.matrixInNode == null || !this.matrixInNode.equals(other.matrixInNode))) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "MultiplyResult{" + "resultMult=" + resultMult + ", matrixInNode=" + matrixInNode + '}';
    }



}
