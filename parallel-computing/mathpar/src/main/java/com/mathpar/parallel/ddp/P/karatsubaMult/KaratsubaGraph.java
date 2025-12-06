/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.parallel.ddp.P.karatsubaMult;
import com.mathpar.number.Element;
import com.mathpar.parallel.ddp.engine.AbstractTask;
import com.mathpar.parallel.ddp.engine.AbstractGraphOfTask;
import com.mathpar.parallel.ddp.engine.Tools;
import com.mathpar.polynom.Polynom;

import static com.mathpar.parallel.ddp.P.karatsubaMult.Main.karatsubaMult;



public class KaratsubaGraph extends AbstractGraphOfTask{

    public KaratsubaGraph(){
        SetTotalVertex(3);
        SetTypesOfVertex(Tools.ArrayListCreator(new int[]{0,0,0}));
        SetArcs(Tools.ArrayListCreator(new int[][]{{},{},{}}));
    }
    @Override
    public void InitVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
        KaratsubaT parent=(KaratsubaT)currentTask;
        KaratsubaT curT=(KaratsubaT)allVertex[numb];
        curT.ring=parent.ring;
        if (!parent.isIndexesFinded){
            int a0BorderInd = Math.min(parent.a.findLastMonomWithMainVar() + 1, parent.a.coeffs.length / 2);
            int b0BorderInd = Math.min(parent.b.findLastMonomWithMainVar() + 1, parent.b.coeffs.length / 2);
            parent.a1 = parent.a.subPolynom1(0, a0BorderInd);
            parent.a0 = parent.a.subPolynom1(a0BorderInd, parent.a.coeffs.length);
            parent.b1 = parent.b.subPolynom1(0, b0BorderInd);
            parent.b0 = parent.b.subPolynom1(b0BorderInd, parent.b.coeffs.length);
          /*  System.out.println("a0=" + parent.a0.toString(parent.ring));
            System.out.println("a1=" + parent.a1.toString(parent.ring));
            System.out.println("b0=" + parent.b0.toString(parent.ring));
            System.out.println("b1=" + parent.b1.toString(parent.ring));*/
            parent.reduceValue = Math.min(parent.a1.powers[parent.a1.powers.length - 1], parent.b1.powers[parent.b1.powers.length - 1]);
            //System.out.println("aBorderInd=" + a0BorderInd + " bBorderInd=" + b0BorderInd + " reduceValue=" + parent.reduceValue);
            parent.a1.reduceMainVarPowers(parent.reduceValue);
            parent.b1.reduceMainVarPowers(parent.reduceValue);
            parent.isIndexesFinded=true;
        }        
        switch (numb) {
            case 0: {
                curT.a = parent.a0;
                curT.b = parent.b0;
                break;
            }
            case 1: {
                curT.a = parent.a1;
                curT.b = parent.b1;
                break;
            }
            case 2: {
                curT.a = parent.a0.add(parent.a1, parent.ring);
                curT.b = parent.b0.add(parent.b1, parent.ring);
                break;
            }
        }
        
    }

    @Override
    public void FinalizeVertex(int numb, AbstractTask currentTask, AbstractTask[] allVertex) {
        
    }

    @Override
    public void FinalizeGraph(AbstractTask currentTask, AbstractTask[] allVertex) {
        KaratsubaT parentT=(KaratsubaT)currentTask;
        Polynom a0b0=((KaratsubaT)allVertex[0]).c;
        //System.out.println("a0b0="+a0b0.toString(parentT.ring));
        Polynom a1b1=((KaratsubaT)allVertex[1]).c;
        //System.out.println("a1b1="+a1b1.toString(parentT.ring));
        Polynom sumPol=((KaratsubaT)allVertex[2]).c;
        //System.out.println("sumPol="+sumPol.toString(parentT.ring));
        Polynom bracketPol=sumPol.subtract(a0b0, parentT.ring).subtract(a1b1, parentT.ring);
        //System.out.println("bracketPol="+bracketPol.toString(parentT.ring));
        int []xMultPowers=new int[parentT.a.powers.length/parentT.a.coeffs.length];
        xMultPowers[xMultPowers.length-1]=parentT.reduceValue;
        Element []xCoeff=new Element[1];
        xCoeff[0]=parentT.ring.numberONE;
        int []x2MultPowers=new int[parentT.a.powers.length/parentT.a.coeffs.length];
        x2MultPowers[x2MultPowers.length-1]=parentT.reduceValue*2;        
        Polynom x=new Polynom(xMultPowers,xCoeff);
        Polynom x2=new Polynom(x2MultPowers,xCoeff);
        bracketPol=bracketPol.multiply(x, parentT.ring); //*x
        //System.out.println("bracketPol after multon by x="+bracketPol.toString(parentT.ring));
        a1b1=a1b1.multiply(x2, parentT.ring); //*x^2
        //System.out.println("a1b1 after multon by x^2="+a1b1.toString(parentT.ring));
        Polynom res=a0b0.add(bracketPol, parentT.ring).add(a1b1, parentT.ring);
        //System.out.println("KAR OUTPUT: res="+res.toString(parentT.ring));
        parentT.c=res;
    }




}
