/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.mathpar.students.ukma17i41.bosa.parallel.engine;
package com.mathpar.parallel.dap.core;

import com.mathpar.matrix.MatrixS;
import com.mathpar.parallel.dap.adjmatrix.MatrixS.MatrSAdjMatrix;
import com.mathpar.parallel.dap.cholesky.MatrixD.MatrDCholFact4;
import com.mathpar.parallel.dap.cholesky.MatrixD.MatrDCholFactStrassWin7;
import com.mathpar.parallel.dap.cholesky.MatrixS.MatrSCholFact4;
import com.mathpar.parallel.dap.cholesky.MatrixS.MatrSCholFactStrassWin7;
import com.mathpar.parallel.dap.inverseTriangle.MatrixD.MatrDTriangInv4;
import com.mathpar.parallel.dap.inverseTriangle.MatrixD.MatrDTriangInvStrassWin7;
import com.mathpar.parallel.dap.inverseTriangle.MatrixS.MatrSTriangInv4;
import com.mathpar.parallel.dap.inverseTriangle.MatrixS.MatrSTriangInvStrassWin7;
import com.mathpar.log.MpiLogger;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.QR.QPDecomposition;
import com.mathpar.parallel.dap.QR.QRDecomposition;
import com.mathpar.parallel.dap.ldumw.LdumwFact;
import com.mathpar.parallel.dap.multiply.*;
import com.mathpar.parallel.dap.multiply.MatrixD.*;
import com.mathpar.parallel.dap.multiply.MatrixS.*;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVar;
import com.mathpar.parallel.dap.multiply.multiplyVar.MultiplyVarConfig;

import java.io.Serializable;
import java.util.ArrayList;



public abstract class Drop implements Serializable {
    private final static MpiLogger LOGGER = MpiLogger.getLogger(Drop.class);
    protected static int leafSize = 4;
    protected static double leafdensity = 0.1;
    /**inData - an array of input data to which the initial task data is transmitted
     *
     * outData - the output array of the drop is the result.

     * arcs - an array of dependencies in a graph (arcs) is a two-dimensional array whose positions are drop numbers in the graph.
     * This array shows the connections in the graph between the drops. Each array contains the drop number, which depends on the current (on its output data),
     * the position in the output list of the current drop and the position in the input list of the drop, which depends on it, ie where and where to write data.
     * The zero element of the array is responsible for the input function of the drop, and the latter for the output, and it is always empty.
     *
     * type - drop type. Each drop has a corresponding type of numeric value, which indicates the type of task it performs.
     * Each drop of the appropriate type must inherit the abstract Drop-Task class and implement its methods according to its specification.
     * With the appearance of a new type of drop, it is necessary to take the following, not yet used numerical value for it.
     *
     * resultForOutFunctionLength - the length of the input array for the output function of the drop.
     *
     * inputDataLength - the length of the input array of the drop (the amount of all input data).
     *
     * numberOfDaughterProc - the number of the child processor to which this drop was sent for calculation.
     * It is necessary that in case we sent a drop task only with the main components,
     * it was known to whom to send other data for calculation. By default, this value is -2.
     * The field can acquire the following values:
     * - 2 - the drop has not yet been added to the list of available tasks and has not been sent to the child processor
     * - 1 - drop added to the list of available, but not yet sent;
     * - 0..n - the number of the processor to which this drop was sent.
     *
     * aminId - the number of the amine in which this drop is located.
     * This is necessary in order to quickly find out the number of the amine and the number drop from which the task is taken.
     * First, this value is -1, after the creation of the amine, its droplets write the number (position) on the pine
     *
     * myAmin - the number of the amine on the pine, which was deployed from this drop
     *
     * dropId - the number of this drop in the amine
     *
     * procId - the number of the processor in which this drop was created and is on its pine.
     *
     * recNum - recursion level - at what level the solution of the problem is (calculated according to the amount of input data).
     *
     * number - unique number of drop
     *
     * fullDrop - boolean variable, which shows whether the drop is complete, ie whether it has all input components (main and additional).
     *
     */
    protected int resultForOutFunctionLength;
    protected int inputDataLength;
    protected int outputDataLength;
    protected int numberOfDaughterProc = -2;
    protected int aminId = -1;
    private int myAmin = -1;
    protected int dropId = -1;
    protected int procId = -1;
    protected int recNum = 0;
    protected int number;
    //boolean fullDrop = false;
    protected static int cnum = 0;
    protected byte[] config;
    protected Element[] inData;
    protected Element[] outData;
    protected int[][] arcs;
    protected int type;
    public int key = 0;

    public abstract ArrayList<Drop> doAmin();

    public abstract Element[] inputFunction(Element[] input, Amin amin, Ring ring);
    public abstract Element[] outputFunction(Element[] input, Ring ring);

    public abstract void sequentialCalc(Ring ring);

    void setNumbOfMyAmine(int numOfAmin)
    {
        myAmin = numOfAmin;
    }

    int getNumbOfMyAmine()
    {
        return myAmin;
    }

    int getRecNum() {return recNum;}

    public void setLeafSize(int dataSize) {
        leafSize = dataSize;
    }

    public  boolean isItLeaf() {
        MatrixS ms = (MatrixS)inData[0];
        return (ms.isItLeaf(leafSize, leafdensity));
    }

    public void setLeafDensity(double ldensity) {
        leafdensity = ldensity;
    }

    public boolean hasFullInputData(){
        for (int j = 0; j < inputDataLength; j++) {
            // LOGGER.trace("inData[j] = " + inData[j]);
            if (inData[j] == null) {
                return false;
            }
        }

        return true;
    }

    public void independentCalc(Ring ring, Amin amin){return;};
    public  Element[] recentCalc(Ring ring){return outData;};

    public void setVars(){return;}



    @Override
        public String toString() {
           String str = "";
//           for (Element i: inData) {
//                 str +=i + " ";
//           }
//            str+= " OutData = ";
//           for (Element i: outData) {
//                 str +=i + " ";
//           }
            str+= inData.toString();//Utils.matrixSArrayToString(inData);
            str+= outData.toString();//Utils.matrixSArrayToString(outData);
           str+= " type = "+type;
           str+= " numberOfDaughterProc = "+numberOfDaughterProc;
           str+= " myAmin = "+myAmin;
           str+= " dropId = "+dropId;
           str+= " aminId = "+aminId;
           str+= " procId = "+procId;
           str+= " number = "+number;
           return str;
        }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Drop drop = (Drop) obj;

        return dropId == drop.dropId && aminId == drop.aminId ;// && procId == drop.procId;
    }

    public static Drop doNewDrop(
            int type,
            int key,
            byte[] config,
            int aminId,
            int dropId,
            int procId,
            int recNum,
            Element[] data
    ) {
        Drop newDrop = getDropObject(type, config);
        newDrop.aminId = aminId;
        newDrop.dropId = dropId;
        newDrop.procId = procId;
        newDrop.recNum = recNum;
        newDrop.key = key;
        newDrop.setVars();
        newDrop.inData = data;

        return newDrop;
    }

    /**
     Стандартне множення матриць в класі MatrіxS схема 8
     Multiply type = 0 ++  (AB)

     Стандартне множення матриць в класі MatrixS разом з додаванням третьої матриці
     MultiplyAdd type = 1  (AB+C)

     Стандартне множення матриць в класі MatrixS зі зміною знака всіх елементів
     MultiplyMinus type = 2  (-AB)

     Стандартне множення матриць в класі MatrixS з додатковими конфігураціями
     MultiplyVar type = 2 (Ilya  QR)

     Розклад матриць в класі MatrixS QP
     QPDecomposition type = 3

     Разклад матриць в класі MatrixS QR
     QRDecomposition type = 4

     Стандартне множення матриць в класі MatrixD
     MatrDMult4 type = 10

     Множення матриць за Виноград-Штрассеном в класі MatrixD
     matrDMultStrassWin type = 11 (!!!)

     Обернення трикутної матриці в класі MatrixS за Виноград-Штрассеном
     matrDTriangInvStrassWin type = 18

     Розклад Холецького в класі MatrixD з використанням множення за Виноград-Штрассеном
     matrDCholFactStrassWin type = 22

     Стандартне множення матриць в класі MatrіxS схема 4 з ключем
     Multiply4 type = 5

     Дроп типу A*B+C*D
     MultiplyScalar type = 7

     */
    public static Drop getDropObject(int type, byte[] config) {
        Drop task = null;
        switch (type) {
            case 0:
                task = new Multiply();
                break;
            case 1:
                task = new MultiplyAdd();
                break;
            case 2:
                task = new MultiplyVar(new MultiplyVarConfig(config));
                break;
            case 3:
                task = new QPDecomposition();
                break;
            case 4:
                task = new QRDecomposition();
                break;
            /////////////////////////////////////////////////
            case 5:
                task = new MatrSMult4();
                break;
            case 6:
                task = new MatrSMultStrassWin7();
                break;
            case 7:
                task = new MatrSMultiplyScalar();
                break;

            case 9:
                task = new MultiplyExtendedSWin();
                break;
            case 10:
                task = new MatrDMult4();
                break;
            case 11:
                task = new MatrDMultStrassWin7();
                break;
            case 12:
                task = new MatrDMultiplyScalar();
                break;
            case 13:
                task = new MultiplyExtendedD4();
                break;
            case 14:
                task = new MultiplyExtendedDWin();
                break;
            case 15:
                task = new MatrSTriangInv4();
                break;
            case 16:
                task = new MatrSTriangInvStrassWin7();
                break;
            case 17:
                task = new MatrDTriangInv4();
                break;
            case 18:
                task = new MatrDTriangInvStrassWin7();
                break;
            case 19:
                task = new MatrSCholFact4();
                break;
            case 20:
                task = new MatrSCholFactStrassWin7();
                break;
            case 21:
                task = new MatrDCholFact4();
                break;
            case 22:
                task = new MatrDCholFactStrassWin7();
                break;
            case 7701:
                task = new MatrSAdjMatrix();
                break;



            case 23:
                task = new LdumwFact();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        return task;

    }
}
