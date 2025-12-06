package com.mathpar.students.OLD.curse5_2015.korabelnikov_kurdymova;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.logging.Level;
import com.mathpar.matrix.MatrixS;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.file.sparse.*;
import mpi.MPI;
import mpi.MPIException;
import com.mathpar.number.*;
import com.mathpar.parallel.ddp.engine.*;
import com.mathpar.parallel.stat.FMD.MultFMatrix.*;
import com.mathpar.parallel.utils.MPITransport;

public class GraphMult extends AbstractGraphOfTask{
    Ring ring = new Ring("Z[]");
    
    public GraphMult() {
        SetTotalVertex(8);
        SetTypesOfVertex(Tools.ArrayListCreator(new int[]{0,0,0,0,0,0,0,0}));
        SetArcs(Tools.ArrayListCreator(new int[][]{{},{},{},{},{},{},{},{}}));
    }

    @Override
    public void InitVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
        try {
        com.mathpar.matrix.file.sparse.SFileMatrixS[] a1 = ((TaskMult)currentTask).A.split();
        com.mathpar.matrix.file.sparse.SFileMatrixS[] a2 = ((TaskMult)currentTask).B.split();
        
        if(numb == 0) {
            ((TaskMult)allVertex[0]).A = a1[0];
            ((TaskMult)allVertex[0]).B = a2[0];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        if(numb == 1) {
            ((TaskMult)allVertex[1]).A = a1[1];
            ((TaskMult)allVertex[1]).B = a2[2];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        if(numb == 2) {
            ((TaskMult)allVertex[2]).A = a1[0];
            ((TaskMult)allVertex[2]).B = a2[1];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        if(numb == 3) {
            ((TaskMult)allVertex[3]).A = a1[1];
            ((TaskMult)allVertex[3]).B = a2[3];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        if(numb == 4) {
            ((TaskMult)allVertex[4]).A = a1[2];
            ((TaskMult)allVertex[4]).B = a2[0];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        if(numb == 5) {
            ((TaskMult)allVertex[5]).A = a1[3];
            ((TaskMult)allVertex[5]).B = a2[2];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        if(numb == 6) {
            ((TaskMult)allVertex[6]).A = a1[2];
            ((TaskMult)allVertex[6]).B = a2[1];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        if(numb == 7) {
            ((TaskMult)allVertex[7]).A = a1[3];
            ((TaskMult)allVertex[7]).B = a2[3];
            //System.out.println("graph:initvertex  a  "+((TaskMult)currentTask).A.toMatrixS()+"    b "+((TaskMult)currentTask).B.toMatrixS());
        }
        } catch(Exception e) {System.out.println("errinitvertex  "+e);System.exit(123);}
    }

    @Override
    public void FinalizeVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
        
    }

    @Override
    public void FinalizeGraph(AbstractTask currentTask, AbstractTask[] allVertex) {
        try {
            com.mathpar.matrix.file.sparse.SFileMatrixS c1 = ((TaskMult)allVertex[0]).AB.add(((TaskMult)allVertex[1]).AB, ring);
            com.mathpar.matrix.file.sparse.SFileMatrixS c2 = ((TaskMult)allVertex[2]).AB.add(((TaskMult)allVertex[3]).AB, ring);
            com.mathpar.matrix.file.sparse.SFileMatrixS c3 = ((TaskMult)allVertex[4]).AB.add(((TaskMult)allVertex[5]).AB, ring);
            com.mathpar.matrix.file.sparse.SFileMatrixS c4 = ((TaskMult)allVertex[6]).AB.add(((TaskMult)allVertex[7]).AB, ring);
            ((TaskMult)currentTask).AB = c1.joinCopy(new com.mathpar.matrix.file.sparse.SFileMatrixS[]{c1, c2, c3, c4});
        } catch(Exception e) {System.out.println("errfinalizegraph"+e);System.exit(123);}
    }
    
}