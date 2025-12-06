package com.mathpar.students.OLD.curse5_2015.gladyshev;

import com.mathpar.parallel.ddp.engine.*;
import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Ring;
import com.mathpar.number.Element;



class graph extends AbstractGraphOfTask {

	public graph() {
		SetTotalVertex(7);
		SetTypesOfVertex(Tools.ArrayListCreator(new int[]{0,0,0,0,0,0,0}));
		SetArcs(Tools.ArrayListCreator(new int[][]{{},{},{},{},{},{},{}}));
	}

	public void InitVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
		MatrixD[] AA = split( ((task) currentTask).A );
		MatrixD[] BB = split( ((task) currentTask).B );
		switch (numb) {
			case 0:
				((task)allVertex[numb]).A = AA[0].add(AA[3], ((task)currentTask).ring);
				((task)allVertex[numb]).B = BB[0].add(BB[3], ((task)currentTask).ring);
				((task)allVertex[numb]).ring = ((task)currentTask).ring;
				break;
			case 1:
				((task)allVertex[numb]).A = AA[2].add(AA[3], ((task)currentTask).ring);
				((task)allVertex[numb]).B = BB[0];
				((task)allVertex[numb]).ring = ((task)currentTask).ring;
				break;
			case 2:
				((task)allVertex[numb]).A = AA[0];
				((task)allVertex[numb]).B = BB[1].subtract(BB[3], ((task)currentTask).ring);
				((task)allVertex[numb]).ring = ((task)currentTask).ring;
				break;
			case 3:
				((task)allVertex[numb]).A = AA[3];
				((task)allVertex[numb]).B = BB[2].subtract(BB[0], ((task)currentTask).ring);
				((task)allVertex[numb]).ring = ((task)currentTask).ring;
				break;
			case 4:
				((task)allVertex[numb]).A = AA[0].add(AA[1], ((task)currentTask).ring);
				((task)allVertex[numb]).B = BB[3];
				((task)allVertex[numb]).ring = ((task)currentTask).ring;
				break;
			case 5:
				((task)allVertex[numb]).A = AA[2].subtract(AA[0], ((task)currentTask).ring);
				((task)allVertex[numb]).B = BB[0].add(BB[1], ((task)currentTask).ring);
				((task)allVertex[numb]).ring = ((task)currentTask).ring;
				break;
			case 6:
				((task)allVertex[numb]).A = AA[1].subtract(AA[3], ((task)currentTask).ring);
				((task)allVertex[numb]).B = BB[2].add(BB[3], ((task)currentTask).ring);
				((task)allVertex[numb]).ring = ((task)currentTask).ring;
				break;
		}
	}

	public void FinalizeVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
	}

	public void FinalizeGraph(AbstractTask currentTask, AbstractTask[] allVertex) {
		MatrixD[] CC = new MatrixD[4];
		Ring ring = ((task)allVertex[0]).ring;
		CC[0] = ((task)allVertex[0]).C.add(((task)allVertex[3]).C, ring);
		CC[0] = CC[0].subtract(((task)allVertex[4]).C, ring);
		CC[0] = CC[0].add(((task)allVertex[6]).C, ring);

		CC[1] = ((task)allVertex[2]).C.add(((task)allVertex[4]).C, ring);

		CC[2] = ((task)allVertex[1]).C.add(((task)allVertex[3]).C, ring);

		CC[3] = ((task)allVertex[0]).C.subtract(((task)allVertex[1]).C, ring);
		CC[3] = CC[3].add(((task)allVertex[2]).C, ring);
		CC[3] = CC[3].add(((task)allVertex[5]).C, ring);

		((task)currentTask).C = MatrixD.join(CC);

	}


    static MatrixD[] split(MatrixD m) {
        MatrixD[] result = new MatrixD[4];
        int size = m.M.length/2;
        
        for(int i=0; i<result.length; i++) {
            result[i] = new MatrixD(new Element[size][size]);
        }
        
        for(int i=0; i<m.M.length; i++) {
            for(int j=0; j<m.M.length; j++) {
                if( (i<size )&&(j<size) ) {
                    result[0].M[i][j] = m.M[i][j];
                } else if( (i<size )&&(j>=size) ) {
                    result[1].M[i][j - size] = m.M[i][j];
                } else if( (i>=size )&&(j<size) ) {
                    result[2].M[i - size][j] = m.M[i][j];
                } else {
                    result[3].M[i - size][j - size] = m.M[i][j];
                }
            }
        }
        
        return result;
    }
}
