package com.mathpar.polynom;
import com.mathpar.func.CanonicForms;
import com.mathpar.func.*;
import java.util.ArrayList;
import com.mathpar.number.*;

/**
 * Класс решающий уравнения вида ax^4+bx^3+cx^2+dx+e=0 причем на месте переменной может стоять простейшая функция
 * А так же простейшие тригонометрические уравнения
 * @author Смирнов Роман
 * @version 0.5
 * @since 24.03.2012
 */
public class SolveEq {

//    public solveEq() {
//    }
    CanonicForms myCFs = null;

    public SolveEq(Ring ring) {
        myCFs = ring.CForm;
    }
 //------------------------------------------------------------------------------------------
 //         решение простейших тригонометрических уравнений
 //------------------------------------------------------------------------------------------
    public Element solveTrigEqu(Element func, Element var,Element Index, Ring r) {
        if(func instanceof F){
            if(((F)((F)func).X[0]).name == F.LOG){
                //right path
                Element t2 = ((F)func).X[1];
                Element t11 = new F(F.LN, new Element[]{((F)((F)func).X[0]).X[0]});
                Element t12 = new F(F.LN, new Element[]{((F)((F)func).X[0]).X[1]});
                Element t1 = new F(F.DIVIDE, new Element[]{t12, t11});
                func = new F(F.SUBTRACT, new Element[]{t1,t2});
            }
        }
        CanonicForms cfst = new CanonicForms(r, true);
        func = F.cleanOfRepeating(func,   r);
        Element func2pol = cfst.ElementToPolynom(func, false);
        var = F.cleanOfRepeating(var,   r);
        Polynom var2pol = (Polynom) cfst.ElementToPolynom(var, false);
        Polynom func2polres = (func2pol instanceof Fraction) ? (Polynom) ((Fraction) func2pol).num : (Polynom) func2pol;
        int posSearch = posSearchFunc(func2polres, var2pol, cfst);
        if (posSearch == -1) {
            return null;
        }
        Polynom[] rAlParts = leftAndRigthParts(posSearch + r.varNames.length, func2polres, r);
        Element convArg = cfst.ElementToPolynom(((F) cfst.List_of_Change.get(posSearch)).X[0], false);
        Element[] rAlPartsArg = leftAndRigthPartOfVar(var2pol.powers.length - 1, convArg, r);
        return solveSimpleTrigFunc(((F) cfst.List_of_Change.get(posSearch)).name, Index,rAlParts, rAlPartsArg,   r);
    }

    private Element solveSin(Element arg, Element Index, Element[] LRPartsFunc, Element[] LRPartsArg, Ring ring) {
       CanonicForms cf=ring.CForm;
        if (arg.isZero(ring)) {
            return new F(F.MULTIPLY, new Element[] {new Fname("\\pi"), Index});
        }
        if (arg.isMinusOne(ring)) {
            return new F(F.ADD, new Element[] {new F(F.DIVIDE, new Element[] {new F(F.MULTIPLY, new Element[] {ring.numberMINUS_ONE, new Fname("\\pi")}),
                            ring.numberONE.valOf(2, ring)}), new F(F.MULTIPLY, new Element[] {ring.numberONE.valOf(2, ring), new Fname("\\pi"), Index})});
        }
        if (arg.isOne(ring)) {
            return new F(F.ADD, new Element[] {new F(F.DIVIDE, new Element[] {new Fname("\\pi"),
                            ring.numberONE.valOf(2, ring)}), new F(F.MULTIPLY, new Element[] {ring.numberONE.valOf(2, ring), new Fname("\\pi"), Index})});
        }
        Element unConvRes=cf.ElementToF(arg);
        Element res = unConvRes.arcsn(ring);
        if(res==null) {
            res= new F(F.ARCSIN,unConvRes);
        }
        if(res.isNaN()) return Element.NAN;
        res = new F(F.MULTIPLY, new Element[] {res, new F(F.POW, new Element[] {ring.numberMINUS_ONE, Index})});
        res = F.cleanOfRepeating(res,   ring);
        Element period = new F(F.MULTIPLY, new Element[] {new Fname("\\pi"), Index});
        period = F.cleanOfRepeating(period,  ring);
        Element period_ =  cf.ElementToPolynom(period, false);
        Element tempres =  cf.ElementToPolynom(res, false).add(period_, ring);
        Element ttrr = cf.myOpAdd(tempres, LRPartsArg[1]);
        if (LRPartsArg[0] instanceof Fraction) {
            ttrr = ttrr.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr = ttrr.multiply(((Fraction) LRPartsArg[0]).denom, ring);
        } else {
            ttrr = cf.myOpDiv(ttrr, LRPartsArg[0]);
        }
        return cf.UnconvertAllLevels(ttrr);
    }

    private Element solveCos(Element arg, Element Index, Element[] LRPartsFunc, Element[] LRPartsArg,   Ring ring) {
        CanonicForms cf=ring.CForm;
        if (arg.isZero(ring)) {
            return new F(F.ADD, new Element[] {new F(F.DIVIDE, new Element[] {new Fname("\\pi"),
                            ring.numberONE.valOf(2, ring)}), new F(F.MULTIPLY, new Element[] {new Fname("\\pi"), Index})});
        }
        if (arg.isMinusOne(ring)) {
            return new F(F.ADD, new Element[] {new Fname("\\pi"), new F(F.MULTIPLY, new Element[] {ring.numberONE.valOf(2, ring), new Fname("\\pi"), Index})});
        }
        if (arg.isOne(ring)) {
            return new F(F.MULTIPLY, new Element[] {ring.numberONE.valOf(2, ring), new Fname("\\pi"), Index});
        }
        Element unConvRes=cf.ElementToF(arg);
        Element res = unConvRes.arccs(ring);
        if(res==null) {
            res= new F(F.ARCCOS,unConvRes);
        }
        if(res.isNaN()) return Element.NAN;
        res = F.cleanOfRepeating(res,   ring);
        Element period = new F(F.MULTIPLY, new Element[] {ring.numberONE.valOf(2, ring), new Fname("\\pi"), Index});
        period = F.cleanOfRepeating(period,  ring);
        Element period_ = cf.ElementToPolynom(period, false);
        Element tempres = cf.ElementToPolynom(res, false).add(period_, ring);
        Element tempres2 = cf.ElementToPolynom(res.negate(ring), false).add(period_, ring);
        Element ttrr = cf.myOpAdd(tempres, LRPartsArg[1]);
        Element ttrr2 = cf.myOpAdd(tempres2, LRPartsArg[1]);
        if (LRPartsArg[0] instanceof Fraction) {
            ttrr = ttrr.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr = ttrr.multiply(((Fraction) LRPartsArg[0]).denom, ring);
            ttrr2 = ttrr2.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr2 = ttrr2.multiply(((Fraction) LRPartsArg[0]).denom, ring);
        } else {
            ttrr = cf.myOpDiv(ttrr, LRPartsArg[0]);
            ttrr2 = cf.myOpDiv(ttrr2, LRPartsArg[0]);
        }
        return new VectorS(new Element[] {cf.UnconvertAllLevels(ttrr), cf.UnconvertAllLevels(ttrr2)});
    }
    private Element solveTan(Element arg, Element Index, Element[] LRPartsFunc, Element[] LRPartsArg,   Ring ring) {
       CanonicForms cf=ring.CForm;
        if (arg.isZero(ring)) {
            return new F(F.MULTIPLY, new Element[] {new Fname("\\pi"), Index});
        }
        Element unConvRes=cf.ElementToF(arg);
        Element res = unConvRes.arctn(ring);
        if(res==null) {
            res= new F(F.ARCTG,unConvRes);
        }
        res = F.cleanOfRepeating(res,   ring);
        Element period = new F(F.MULTIPLY, new Element[] {new Fname("\\pi"), Index});
        period = F.cleanOfRepeating(period,   ring);
        Element period_ =  cf.ElementToPolynom(period, false);
        Element tempres =  cf.ElementToPolynom(res, false).add(period_, ring);
        Element ttrr = cf.myOpAdd(tempres, LRPartsArg[1]);
        if (LRPartsArg[0] instanceof Fraction) {
            ttrr = ttrr.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr = ttrr.multiply(((Fraction) LRPartsArg[0]).denom, ring);
        } else {
            ttrr = cf.myOpDiv(ttrr, LRPartsArg[0]);
        }
        return cf.UnconvertAllLevels(ttrr);
    }

    private Element solveCtg(Element arg, Element Index, Element[] LRPartsFunc, Element[] LRPartsArg,  Ring ring) {
        CanonicForms cf=ring.CForm;
        if (arg.isZero(ring)) {
            return new F(F.ADD, new Element[] {new F(F.DIVIDE, new Element[] {new Fname("\\pi"),
                            ring.numberONE.valOf(2, ring)}), new F(F.MULTIPLY, new Element[] {new Fname("\\pi"), Index})});
        }
        Element unConvRes=cf.ElementToF(arg);
        Element res = unConvRes.arcctn(ring);
        if(res==null) {
            res= new F(F.ARCCTG,unConvRes);
        }
        res = F.cleanOfRepeating(res,  ring);
        Element period = new F(F.MULTIPLY, new Element[] {new Fname("\\pi"), Index});
        period = F.cleanOfRepeating(period,  ring);
        Element period_ =  cf.ElementToPolynom(period, false);
        Element tempres =  cf.ElementToPolynom(res, false).add(period_, ring);
        Element ttrr = cf.myOpAdd(tempres, LRPartsArg[1]);
        if (LRPartsArg[0] instanceof Fraction) {
            ttrr = ttrr.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr = ttrr.multiply(((Fraction) LRPartsArg[0]).denom, ring);
        } else {
            ttrr = cf.myOpDiv(ttrr, LRPartsArg[0]);
        }
        return cf.UnconvertAllLevels(ttrr);
    }

    private Element solveExp(Element arg, Element[] LRPartsFunc, Element[] LRPartsArg, Ring ring) {
        CanonicForms cf=ring.CForm;
        Element unConvRes=cf.ElementToF(arg);
        Element res = unConvRes.ln(ring);
        if(res==null) {
            res= new F(F.LN,unConvRes);
        }
        res = F.cleanOfRepeating(res,   ring);
        Element tempres = cf.ElementToPolynom(res, false);
        Element ttrr = cf.myOpAdd(tempres, LRPartsArg[1]);
        if (LRPartsArg[0] instanceof Fraction) {
            ttrr = ttrr.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr = ttrr.multiply(((Fraction) LRPartsArg[0]).denom, ring);
        } else {
            ttrr = cf.myOpDiv(ttrr, LRPartsArg[0]);
        }
        return cf.UnconvertAllLevels(ttrr);
    }

     private Element solveLg(Element arg, Element[] LRPartsFunc, Element[] LRPartsArg, Ring ring) {
         CanonicForms cf=ring.CForm;
        Element res;
        if(arg.isItNumber()){
          double Dres=Math.pow(10, arg.doubleValue());
          res=ring.numberONE.valOf(Dres, ring);
        }else{
          res = cf.ElementToF(arg);
        }
        if(res==null) {
           res= new F(F.POW,new Element[]{ring.numberONE.valOf(10, ring),res});
        }
        res = F.cleanOfRepeating(res, ring);
        Element tempres = cf.ElementToPolynom(res, false);
        Element ttrr = cf.myOpAdd(tempres, LRPartsArg[1]);
        if (LRPartsArg[0] instanceof Fraction) {
            ttrr = ttrr.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr = ttrr.multiply(((Fraction) LRPartsArg[0]).denom, ring);
        } else {
            ttrr = cf.myOpDiv(ttrr, LRPartsArg[0]);
        }
        return cf.UnconvertAllLevels(ttrr);
    }

    private Element solveLn(Element arg, Element[] LRPartsFunc, Element[] LRPartsArg, Ring ring) {
        CanonicForms cf=ring.CForm;
        Element unConvRes=cf.ElementToF(arg);
        Element res = unConvRes.exp(ring);
        if(res==null) {
            res= new F(F.EXP,unConvRes);
        }
        res = F.cleanOfRepeating(res,   ring);
        Element tempres = cf.ElementToPolynom(res, false);
        Element ttrr = cf.myOpAdd(tempres, LRPartsArg[1]);
        if (LRPartsArg[0] instanceof Fraction) {
            ttrr = ttrr.divide(((Fraction) LRPartsArg[0]).num, ring);
            ttrr = ttrr.multiply(((Fraction) LRPartsArg[0]).denom, ring);
        } else {
            ttrr = cf.myOpDiv(ttrr, LRPartsArg[0]);
        }
        return cf.UnconvertAllLevels(ttrr);
    }


    private Element solveSimpleTrigFunc(int nameFunc,Element Index, Element[] LRPartsFunc, Element[] LRPartsArg,   Ring r) {
        CanonicForms cf=r.CForm;
        Element fsol = cf.myOpDiv(LRPartsFunc[1], LRPartsFunc[0]);
        switch (nameFunc) {
            case F.SIN:
                return (Index==null) ? solveSin(fsol, new Fname("n"), LRPartsFunc, LRPartsArg,   r) : solveSin(fsol, Index, LRPartsFunc, LRPartsArg,   r) ;
            case F.COS:
                return (Index==null) ? solveCos(fsol, new Fname("n"), LRPartsFunc, LRPartsArg,   r) : solveCos(fsol, Index, LRPartsFunc, LRPartsArg,  r) ;
            case F.TG:
                return (Index==null) ? solveTan(fsol, new Fname("n"), LRPartsFunc, LRPartsArg,   r) : solveTan(fsol, Index, LRPartsFunc, LRPartsArg,  r) ;
            case F.CTG:
                return (Index==null) ? solveCtg(fsol, new Fname("n"), LRPartsFunc, LRPartsArg,   r) : solveCtg(fsol, Index, LRPartsFunc, LRPartsArg,  r) ;
            case F.LG:
                return solveLg(fsol, LRPartsFunc, LRPartsArg,   r);
            case F.LN:
                return solveLn(fsol, LRPartsFunc, LRPartsArg,   r);
            case F.EXP:
                return solveExp(fsol, LRPartsFunc, LRPartsArg,   r);
            case F.LOG:
            case F.POW:
            default:
                return null;
        }
    }

    private Element[] leftAndRigthPartOfVar(int posVar, Element func, Ring r) {
        if (func instanceof Fraction) {
            Element[] res = leftAndRigthParts(posVar, (Polynom) ((Fraction) func).num, r);
            res[0] = res[0].divide(((Fraction) func).denom, r);
            res[1] = res[1].divide(((Fraction) func).denom, r);
            return res;
        }
        return leftAndRigthParts(posVar, (Polynom) func, r);
    }

    private Polynom[] leftAndRigthParts(int posVar, Polynom eq, Ring r) {
        Polynom[] res = new Polynom[2];
        IntList powL = new IntList();
        IntList powR = new IntList();
        ArrayList<Element> coefL = new ArrayList<Element>();
        ArrayList<Element> coefR = new ArrayList<Element>();
        int stopIndexR = 0;
        int stopIndexL = 0;
        int step = eq.powers.length / eq.coeffs.length;
        int monomInd = -1;
        for (int i = 0; i < eq.powers.length; i += step) {
            monomInd++;
            if (eq.powers[i + posVar] == 0) {
                System.arraycopy(eq.powers, i, powR.arr, stopIndexR, step);
                stopIndexR += step;
                coefR.add(eq.coeffs[monomInd].negate(r));
            } else {
                System.arraycopy(eq.powers, i, powL.arr, stopIndexL, step);
                powL.arr[stopIndexL + posVar] = 0;
                stopIndexL += step;
                coefL.add(eq.coeffs[monomInd]);
            }
        }
        int[] lpow = new int[stopIndexL];
        System.arraycopy(powL.arr, 0, lpow, 0, lpow.length);
        int[] rpow = new int[stopIndexR];
        System.arraycopy(powR.arr, 0, rpow, 0, rpow.length);
        res[0] = new Polynom(lpow, coefL.toArray(new Element[coefL.size()])).normalNumbVar(r);
        res[1] = new Polynom(rpow, coefR.toArray(new Element[coefR.size()])).normalNumbVar(r);
        return res;
    }

    private int posSearchFunc(Polynom func, Polynom var, CanonicForms cf) {
        Element elLoC;
        for (int i = 0; i < cf.List_of_Change.size(); i++) {
            elLoC = cf.List_of_Change.get(i);
            if (elLoC instanceof F) {
                if (((F) elLoC).name == F.LN | ((F) elLoC).name == F.LG |((F) elLoC).name == F.SIN | ((F) elLoC).name == F.COS | ((F) elLoC).name == F.TG | ((F) elLoC).name == F.CTG | ((F) elLoC).name == F.EXP) {
                    Element convVar = cf.ElementToPolynom(((F) elLoC).X[0], false);
                    if (convVar instanceof Fraction) {
                        Polynom denconPol = (Polynom) ((Fraction) convVar).num;
                        if (denconPol.powers.length / denconPol.coeffs.length < var.powers.length) {
                            continue;
                        }
                        for (int j = var.powers.length - 1; j < denconPol.powers.length; j += denconPol.powers.length / denconPol.coeffs.length) {
                            if (denconPol.powers[j] != 0) {
                                return i;
                            }
                        }
                    } else {
                        Polynom conPol = (Polynom) convVar;
                        if (conPol.powers.length / conPol.coeffs.length < var.powers.length) {
                            continue;
                        }
                        for (int j = var.powers.length - 1; j < conPol.powers.length; j += conPol.powers.length / conPol.coeffs.length) {
                            if (conPol.powers[j] != 0) {
                                return i;
                            }
                        }
                    }
                }
                if(((F) elLoC).name == F.LOG | ((F) elLoC).name == F.POW){
                // Я в раздумьях)
                }
            }
        }
        return -1;
    }



 //------------------------------------------------------------------------------------------
//
//    private Element unConvert(Element el, CanonicForms cf) {
//        if (el instanceof F) {
//            Element[] newX = new Element[((F) el).X.length];
//            for (int i = 0; i < newX.length; i++) {
//                if ((((F) el).X[i] instanceof F)) {
//                    newX[i] = unConvert(((F) el).X[i], cf);
//                } else {
//                    if (((F) el).X[i] instanceof Fraction) {
//                        return new Fraction(unConvert(((Fraction) ((F) el).X[i]).num, cf), unConvert(((Fraction) ((F) el).X[i]).denom, cf));
//                    } else {
//                        if (((F) el).X[i] instanceof Complex)
//                            return new Complex(unConvert(((Complex) ((F) el).X[i]).re, cf), unConvert(((Complex) ((F) el).X[i]).im, cf),cf.RING);
//                        if (el.numbElementType() < Ring.Polynom) return ((F) el).X[i];
//                        else return cf.ElementToF(((F) el).X[i]);
//                    }
//                }
//            }
//            return new F(((F) el).name, newX);
//        } else {
//            if (el instanceof Fraction)
//                return new Fraction(unConvert(((Fraction) el).num, cf), unConvert(((Fraction) el).denom, cf));
//            else {
//                if (el instanceof Complex)
//                    return new Complex(unConvert(((Complex) el).re, cf), unConvert(((Complex) el).im, cf),cf.RING);
//                if (el.numbElementType() < Ring.Polynom)  return el;
//                else {return cf.ElementToF(el);}
//            }
//        }
//    }
    /**
     * Черт знает что.....
     * @param eq
     * @param func
     * @param r
     * @return
     */
    public Element solve(Element eq, Element func, Ring r) {
        Element inp=null; Element frDenom=null; Element res=null; 
        int n=(func instanceof Polynom)? ((Polynom) func).powers.length - 1 : -1; // номер переменной в кольце
        if  (eq.numbElementType()<=Ring.Polynom ) {inp=eq; }
        else{
          if (eq instanceof Fraction  ) { Fraction fr=(Fraction)eq; 
             inp=fr.cancel(r);
             if((inp instanceof Fraction)&&(((Fraction)inp).num.numbElementType()<=Ring.Polynom)&&(((Fraction)inp).denom.numbElementType()<=Ring.Polynom))
                {frDenom=((Fraction)inp).denom; inp= ((Fraction)inp).num;}
             else inp=null;} }
        // уравнение представляет собой полином, а func - чистый моном    
        if ((inp!=null)&&(n>-1)){if(inp instanceof Polynom) res= solvePolynomEq((Polynom) inp, n, r); else 
            return Element.NAN; }
        if(res!=null)return res; // ,??????  frDenom проверить знаменатель
        //  Теперь случай не чистых полиномов а функций и имен..... ...........///////////////
        CanonicForms CFs = (myCFs==null)? new CanonicForms(r,false) : myCFs;
        myCFs=CFs;
        Element newPorFr= CFs.ElementConvertToPolynom(eq);  
        if  (newPorFr.numbElementType()<=Ring.Polynom ) {inp=newPorFr; }
        else{
          if (newPorFr instanceof Fraction  ){ Fraction fr=(Fraction)newPorFr; 
             inp=fr.cancel(r);
             if(inp instanceof Fraction){frDenom=((Fraction)inp).denom; inp= ((Fraction)inp).num;}
             else inp=null;
          } else return Element.NAN;// Значит Авария при конвертации
        }
         if(! (inp instanceof Polynom)  )return Element.NAN;
         Polynom newPol=(Polynom)inp;     
        // уравнение представляет собой полином где коэфиценты  Fname
        if ((  n>-1) ) res = solvePolynomEq(newPol, n, r);
        else{ // уравнение относительно функции f/e: 4\\sin^2(x)+7\\sin(x)-2=0
          F.cleanOfRepeating(func,  r);// забрасываем в списки искомую функцию
          int index_var_eq= CFs.List_of_Change.indexOf(func)+r.varNames.length;
          res = solvePolynomEq(newPol, index_var_eq, r);
        } 
        // обратное преобразование ответа  /////////////////////////////
        if (res == null) {return null; }
        if (res instanceof VectorS) { Element[] VV=((VectorS) res).V;
                Element[] s = new Element[VV.length];
                for (int i = 0; i < VV.length; i++) {
                    s[i] = CFs.UnconvertAllLevels(VV[i]);
                }
                if(VV.length>1)
                return new VectorS(s); // ???  проверить на корни знаменателя
                else return s[0];
            } 
        Element result = CFs.UnconvertAllLevels(res);
        return  (result instanceof VectorS)&&(((VectorS)result).V.length==1)?
                                               ((VectorS)result).V[0]: result;
                // ???  проверить на корни знаменателя
    }
  /**
   * Метод создающий обьект типа Polynom из IntList масива степеней и ArrayList листа элементов
   * @param pow -IntList масив степеней
   * @param coef -ArrayList лист элементов
   * @param r - кольцо
   * @return
   */
  private Element createPol(IntList pow, ArrayList<Element> coef, Ring r){
      Polynom  p= new Polynom(pow.toArray(),coef.toArray(new Element[coef.size()])).normalNumbVar(r);
      return (p.isItNumber()) ? p.coeffs[0] : p;
   }

    private Element divideSE(Element n, Element d, Ring r) {if(n.isZero(r))return r.numberZERO;
        if(n.isItNumber() && d.isItNumber()) {
            if(n instanceof NumberZ && d instanceof NumberZ) return n.divideToFraction(d, r);
            else return n.divide(d, r);
        }
        if (n instanceof Polynom) {
            if (d instanceof F) return new Fraction(n, d);
            if (d instanceof Polynom) return ((Polynom) n).divideToFraction((Polynom) d, r);
            return ((Polynom) n).divideByNumberToFraction(d, r);
        }
        if (n.numbElementType() < Ring.Polynom & d.numbElementType() < Ring.Polynom) {
            return n.divide(d, r);
        }
        return new Fraction(n, d);
    }

 public Element[] BiSqr(Element A,Element B, Element C, Element D, Element E, Ring r){
  Element N64=r.posConst[16].multiply(r.posConst[4], r); Element N256=r.posConst[16].multiply(r.posConst[16], r);
  Element A2=A.pow(2, r);
  Element B2=B.pow(2, r);
  Element p=divideSE(r.posConst[8].multiply(A, r).multiply(C, r).subtract(r.posConst[3].multiply(B2, r), r)
          , r.posConst[8].multiply(A2, r), r);
  Element q=divideSE(r.posConst[16].multiply(B2, r).multiply(A, r).multiply(C, r).subtract(N64.multiply(A2, r).multiply(B, r).multiply(D, r), r).subtract(r.posConst[3].multiply(B2.pow(2, r), r), r).add(N256.multiply(A2, r).multiply(A, r).multiply(E, r), r)
    ,  N256.multiply(A2.pow(2, r), r), r);
  Element fl=p.pow(2, r).subtract(r.posConst[4].multiply(q, r), r);
  Element b4a=divideSE(B, r.posConst[4].multiply(A, r), r);

  Element[] res= new Element[4];

  if(!fl.isNegative() | fl.isZero(r)){
   Element sqr=fl.sqrt(r);
   Element ppsqrt=p.negate(r).add(sqr, r);
   Element mpsqrt=p.negate(r).subtract(sqr, r);
  if(!ppsqrt.isNegative() | ppsqrt.isZero(r)){
   res[0]=divideSE(ppsqrt, r.numberONE.valOf(2, r) , r).sqrt(r).subtract(b4a, r);
   res[1]=divideSE(ppsqrt, r.numberONE.valOf(2, r) , r).sqrt(r).negate(r).subtract(b4a, r);
  }else{
   res[0]=new Complex(b4a.negate(r),divideSE(p.subtract(sqr, r), r.numberONE.valOf(2, r) , r).sqrt(r));
   res[1]=new Complex(b4a.negate(r),divideSE(p.subtract(sqr, r), r.numberONE.valOf(2, r) , r).sqrt(r).negate(r));
  }


   if(!mpsqrt.isNegative() | mpsqrt.isZero(r)){
   res[2]=divideSE(mpsqrt, r.numberONE.valOf(2, r) , r).sqrt(r).subtract(b4a, r);
   res[3]=divideSE(mpsqrt, r.numberONE.valOf(2, r) , r).sqrt(r).negate(r).subtract(b4a, r);
  }else{
   res[2]=new Complex(b4a.negate(r),divideSE(p.add(sqr, r), r.numberONE.valOf(2, r) , r).sqrt(r));
   res[3]=new Complex(b4a.negate(r),divideSE(p.add(sqr, r), r.numberONE.valOf(2, r) , r).sqrt(r).negate(r));
  }
   return res;
  }
  Element f;
  if(p.isNegative()) {
         f=divideSE((r.numberONE.valOf(4, r).multiply(q, r).subtract(p.pow(2, r), r)).sqrt(r).negate(r),p,r).arctn(r);
     }
  else
  if(p.isZero(r)) {
         f=divideSE(p.pi(r),r.numberONE.valOf(2, r),r);
     }
  else {
         f=divideSE((r.numberONE.valOf(4, r).multiply(q, r).subtract(p.pow(2, r), r)).sqrt(r).negate(r),p,r).arctn(r).add(p.pi(r), r);
     }
  Element sqrt2q=q.sqrt(r).sqrt(r);
 // FvalOf calc=new FvalOf(r);

  Element cosf=divideSE(f,r.numberONE.valOf(2, r) , r).cos(r);
  Element sinf=divideSE(f,r.numberONE.valOf(2, r) , r).sin(r);
//  cosf=calc.valOf(new F(F.COS,divideSE(f,r.numberONE.valOf(2, r) , r)), new Element[0]);
//  sinf=calc.valOf(new F(F.SIN,divideSE(f,r.numberONE.valOf(2, r) , r)), new Element[0]);
  res[0]=new Complex(sqrt2q.multiply(cosf, r).subtract(b4a, r), sqrt2q.multiply(sinf, r) );
  res[1]=new Complex(sqrt2q.multiply(cosf, r).subtract(b4a, r), sqrt2q.multiply(sinf, r).negate(r) );

  res[2]=new Complex(sqrt2q.multiply(cosf, r).negate(r).subtract(b4a, r), sqrt2q.multiply(sinf, r) );
  res[3]=new Complex(sqrt2q.multiply(cosf, r).subtract(b4a, r), sqrt2q.multiply(sinf, r).negate(r) );

  return res;
 }




 public Element[] Ferrary(Element A,Element B, Element C, Element D, Element E, Ring r ){
     CanonicForms cf=r.CForm;
  Element A2=A.pow(2, r);
  Element AC4=r.numberONE.valOf(4, r).multiply(A, r).multiply(C, r);
  Element B2=B.pow(2, r);
  Element p=divideSE(r.numberONE.valOf(2, r).multiply(AC4, r).subtract(r.numberONE.valOf(3, r).multiply(B2, r), r)
    ,r.numberONE.valOf(8, r).multiply(A2, r) ,r);
  Element q=divideSE(r.numberONE.valOf(8, r).multiply(A2, r).multiply(D, r).add(B2.multiply(B, r), r).subtract(AC4.multiply(B, r), r)
   ,r.numberONE.valOf(8, r).multiply(A2, r).multiply(A, r), r);
  Element r_=divideSE(r.numberONE.valOf(4, r).multiply(AC4, r).multiply(B2, r).subtract(r.numberONE.valOf(64, r).multiply(A2, r).multiply(B, r).multiply(D, r), r).subtract(r.numberONE.valOf(3, r).multiply(B2.pow(2, r), r), r).add(r.numberONE.valOf(256, r).multiply(A2, r).multiply(A, r).multiply(E, r), r)
      , r.numberONE.valOf(256, r).multiply(A2, r).multiply(A2, r)  ,r);
  Element A0=r.numberONE;
  Element B0=p;
  Element C0=divideSE(p.pow(2, r).subtract(r.numberONE.valOf(4, r).multiply(r_, r), r),r.numberONE.valOf(4, r) , r);
  Element D0=divideSE(q.pow(2, r).negate(r), r.numberONE.valOf(8, r), r);
  Element[] resKardano=Kardano(A0, B0, C0, D0, r );
  if(resKardano==null) {
         return null;
     }
  Element rk1=resKardano[0];
  if(rk1.isZero(r) | rk1.isNegative()) { // предполагается, что rk1 всегда больше нуля, если q не равно нулю иначе пробуем как биквадратное
         return BiSqr(A, B, C, D, E, r);
     }
  Element rk1_2=r.numberONE.valOf(2, r).multiply(rk1, r);
  Element sqrtrk1=rk1_2.sqrt(r);
  Element b4a=divideSE(B, r.numberONE.valOf(4, r).multiply(A, r), r);

  Element D1=rk1_2.subtract(r.numberONE.valOf(4, r).multiply(divideSE(p, r.numberONE.valOf(2, r), r).add(divideSE(q, r.numberONE.valOf(2, r).multiply(sqrtrk1, r), r), r).add(rk1, r), r), r);
  Element D2=rk1_2.subtract(r.numberONE.valOf(4, r).multiply(divideSE(p, r.numberONE.valOf(2, r), r).subtract(divideSE(q, r.numberONE.valOf(2, r).multiply(sqrtrk1, r), r), r).add(rk1, r), r), r);
  Element [] res=new Element[4];

  if(!D1.isNegative() | D1.isZero(r)){
   res[0]=divideSE(sqrtrk1.add(D1.sqrt(r), r), r.numberONE.valOf(2, r), r).subtract(b4a, r);
   res[1]=divideSE(sqrtrk1.subtract(D1.sqrt(r), r), r.numberONE.valOf(2, r), r).subtract(b4a, r);
  }else{
   Element re12=divideSE(sqrtrk1, r.numberONE.valOf(2, r), r).subtract(b4a, r);
   Element im12=divideSE(D1.negate(r).sqrt(r), r.numberONE.valOf(2, r), r);
   res[0]=new Complex(re12,im12);
   res[1]=new Complex(re12,im12.negate(r));
  }

  if(!D2.isNegative() | D2.isZero(r)){
   res[2]=divideSE(D2.sqrt(r).subtract(sqrtrk1, r), r.numberONE.valOf(2, r), r).subtract(b4a, r);
   res[3]=divideSE(D2.sqrt(r).negate(r).subtract(sqrtrk1, r), r.numberONE.valOf(2, r), r).subtract(b4a, r);
  }else{
   Element re12=divideSE(sqrtrk1, r.numberONE.valOf(2, r), r).subtract(b4a, r).negate(r);
   Element im12=divideSE(D2.negate(r).sqrt(r), r.numberONE.valOf(2, r), r);
   res[2]=new Complex(re12,im12);
   res[3]=new Complex(re12,im12.negate(r));
  }
  Element[] unConvRes= new Element[res.length];
  for(int i=0;i<res.length;i++){
  unConvRes[i]=cf.UnconvertAllLevels(res[i]);
  }
  
 return  unConvRes ;
 }



 /**
  * Решение кубических уравнений методом Кардано
  * @param A - коэфф-т при третьей степени
  * @param B - коэфф-т при второй степени
  * @param C - коэфф-т при первой степени
  * @param D - коэфф-т при нулевой степени
  * @param r - кольцо
  * @return
  */
  public Element[] Kardano(Element A,Element B, Element C, Element D, Ring r){
      CanonicForms cf=r.CForm;
  Element A2=A.pow(2, r);
  Element A3=A.pow(3, r);
  Element B2=B.pow(2, r);
  Element B3=B.pow(3, r);
  Element AC=A.multiply(C, r);
  Element ABC9=AC.multiply(B, r).multiply(r.numberONE.valOf(9, r), r); // 9abc
  Element ACD27=r.numberONE.valOf(27, r).multiply(A2, r).multiply(D, r);  // 27a^2d
  Element s1= r.numberONE.valOf(3, r).multiply(AC, r).subtract(B2, r);    // 3ac-b^2
  Element p = divideSE(s1, r.numberONE.valOf(3, r).multiply(A2, r), r);  // p= (3ac-b^2)/(3a^2)
  Element hq=r.numberONE.valOf(2, r).multiply(B3, r).subtract(ABC9, r).add(ACD27, r); // 2b^3-9abc+27a^2d
  Element q = divideSE(hq, r.numberONE.valOf(27, r).multiply(A3, r), r);  // q=(2b^3-9abc+27a^2d)/(27a^3)
  Element S = divideSE( s1.pow(3, r).multiply(r.numberONE.valOf(4, r), r)
      .add(hq.pow(2, r), r) , r.numberONE.valOf(2916, r).multiply(A3.pow(2, r), r),r);
       // S={4*27(3ac-b^2)^3+(2b^3-9abc+27a^2d)^2}/{2916 a^6}   // p^3/27+q^2/4  //  2916=27^2 2^2
  if(S.isZero(r)){
      if (p.equals(q, r)) {
          Element DdivA = divideSE(D, A, r);
          Element root = DdivA.rootTheFirst(3, r);
          if (!DdivA.isNegative()) {root = root.negate(r);}
          return new Element[] {root};
      }
    if(q.isZero(r)){
     Element rootThird= divideSE(r.numberONE.valOf(3, r).multiply(B, r),A,r);
     return new Element[]{rootThird};}
    Element rootTwos=divideSE(B.multiply(C, r).subtract(r.numberONE.valOf(9, r).multiply(A, r).multiply(D, r), r),
            (AC.multiply(r.numberONE.valOf(3, r), r).subtract(B2, r)).multiply(r.numberONE.valOf(2, r), r),r);
    Element root_=divideSE((r.numberONE.valOf(9, r).multiply(A2, r).multiply(D, r)).subtract( r.numberONE.valOf(4, r).multiply(AC, r).multiply(B, r), r).add(B3, r),
            (AC.multiply(r.numberONE.valOf(3, r), r).subtract(B2, r)).multiply(A, r),r);
    return new Element[]{cf.UnconvertAllLevels(rootTwos),cf.UnconvertAllLevels(root_)};
  }else{
   if(S.isNegative()){
    Element F=(q.isZero(r))? q.pi(r).divide(r.numberONE.valOf(2, r), r):
              (q.isNegative())?
              divideSE(S.abs(r).sqrt(r).multiply(r.numberONE.valOf(-2, r), r), q, r).arctn(r):
              q.pi(r).add(divideSE(S.abs(r).sqrt(r).multiply(r.numberONE.valOf(-2, r), r), q, r).arctn(r),r);
    Element pd3=r.numberONE.valOf(2, r).multiply(divideSE(p.negate(r), r.numberONE.valOf(3, r), r).sqrt(r), r);
    Element b3a=divideSE(B.negate(r),r.numberONE.valOf(3, r).multiply(A, r) , r);
    Element f4pi=divideSE(r.numberONE.valOf(4, r).multiply(q.pi(r), r).add(F, r),r.numberONE.valOf(3, r),r);
    Element f2pi=divideSE(r.numberONE.valOf(2, r).multiply(q.pi(r), r).add(F, r),r.numberONE.valOf(3, r),r);
    Element x1=pd3.multiply(divideSE(F,r.numberONE.valOf(3, r),r).cos(r), r).add(b3a, r);
    Element x2=pd3.multiply(f2pi.cos(r), r).add(b3a, r);
    Element x3=(q.isZero(r))? b3a : pd3.multiply(f4pi.cos(r), r).add(b3a, r);
    return new Element[]{ cf.UnconvertAllLevels(x1), cf.UnconvertAllLevels(x2), cf.UnconvertAllLevels(x3)};
   }else{ Element SqrtS=S.sqrt(r);
     Element flagP=q.negate(r).divide(r.numberONE.valOf(2, r), r).add(SqrtS, r);
     Element flagM=q.negate(r).divide(r.numberONE.valOf(2, r), r).subtract(SqrtS, r);
     Element y1,y2;
      y1=(flagP.isZero(r))?     r.numberZERO:
         (flagP.isNegative())?
           divideSE(flagP.abs(r).ln(r), r.numberONE.valOf(3, r), r).exp(r).negate(r): // e^(ln(|P|)/3)
           divideSE(flagP.abs(r).ln(r), r.numberONE.valOf(3, r), r).exp(r);      // -e^(ln(|P|)/3)
      y2 =(flagM.isZero(r))? r.numberZERO:
           (flagM.isNegative()) ?
           divideSE(flagM.abs(r).ln(r), r.numberONE.valOf(3, r), r).exp(r).negate(r) // -e^(ln(|M|)/3)
           : divideSE(flagM.abs(r).ln(r), r.numberONE.valOf(3, r), r).exp(r);      // e^(ln(|M|)/3)
      Element ba3=divideSE(B,r.numberONE.valOf(3, r).multiply(A, r) ,r).negate(r); //-(3B)/A или -B/3A
      Element x1=y1.add(y2, r).add(ba3, r);                                  // y1+y2-(3B)/A                               // y1+y2-(3B)/A
      Element y1x1_2=divideSE(y1.add(y2, r),r.numberONE.valOf(2, r),r).negate(r); // -(y1+y2)/2
      Element w=divideSE(y1.subtract(y2, r).multiply(r.numberONE.valOf(3, r).sqrt(r), r),
                         r.numberONE.valOf(2, r),r); // sqrt(3(y1-y2)) / 2
      Element x2=new Complex(y1x1_2.add(ba3, r), w,r);             // (-(y1+y2)/2-(3B)/A; w)
      Element x3=new Complex(y1x1_2.add(ba3, r), w.negate(r),r);  // (-(y1+y2)/2-(3B)/A; -w)
      return new Element[]{ cf.UnconvertAllLevels(x1), cf.UnconvertAllLevels(x2), cf.UnconvertAllLevels(x3)};
   }
 }
}

  /**
   * Метод решающий квадратное уравнени
   * @param A - коэф-т при второй степени
   * @param B - коэф-т при первой степени
   * @param C - коэф-т при нулевой степени
   * @param r - кольцо
   * @return
   */
  public Element[] solveSqrEq(Element A, Element B, Element C, Ring r){
      boolean evenB=B.isEven(); B=(evenB)? B.divide(r.posConst[2], r):B;
 //   Element four=r.numberONE.valOf(4, r);  Element two=r.numberONE.valOf(2, r);
    Element A2=(evenB)? A: r.posConst[2].multiply(A, r); 
    Element BNeg=(B.isZero(r))?B:B.negate(r).divideToFraction(A2, r);
    Element min4AC=A.multiply(C, r).negate(r);    min4AC=(evenB)? min4AC: min4AC.multiply(r.posConst[4], r);
    Element D=(B.isZero(r))? min4AC: B.multiply(B, r).add(min4AC,r);
    
    if(D instanceof  Polynom){ D=((Polynom) D).normalNumbVar(r);
       if(((Polynom) D).isItNumber()) D=(((Polynom) D).isZero(r))? r.numberZERO: ((Polynom) D).coeffs[0]; }
    // System.out.println("D="+ D);
    Element sqrtD= (D instanceof Polynom)? ((Polynom)D).sqrtTheFirst(r): D.sqrt(r);
    if(sqrtD==Element.NAN)return new Element[]{Element.NAN};
    boolean flagComplex=false;
    if (sqrtD instanceof Complex) { sqrtD=((Complex)sqrtD).im; flagComplex=true;}
    Element mult;
        mult = (sqrtD.isItNumber())? sqrtD.divideToFraction(A2, r)
                : ((sqrtD instanceof F)&&( ((F)sqrtD).X[0].isItNumber() ))
                ? ( (((F)sqrtD).X.length>1)?
                   new F(F.MULTIPLY, ((F)sqrtD).X[0].divideToFraction(A2, r),((F)sqrtD).X[1]):
           //    ((F)sqrtD).X[0].divideToFraction(A2, r)):((F)sqrtD).X[0].divideToFraction(A2, r);
                divideSE(sqrtD,A2,r)): divideSE(sqrtD,A2,r) ;
    boolean flCreateComplex=BNeg.isItNumber() & mult.isItNumber();
    if(mult.isZero(r))return new Element[]{BNeg};
    return (!flagComplex)? new Element[]{BNeg.add(mult, r),BNeg.subtract(mult, r)} 
                       : (flCreateComplex) ?new Element[]{new Complex(BNeg,mult),new Complex(BNeg,mult.negate(r))}
            : new Element[]{BNeg.add(mult.multiply(r.numberI, r),r),BNeg.subtract(mult.multiply(r.numberI, r),r)};
  }

    /**
     * Метод извлекающий коэффециеты при соответствующей переменной
     * @param p
     * @param r
     * @return
     */
    public Element[] getCoeffs(Polynom p, int num, Ring r) {
        int col = p.powers.length / p.coeffs.length;
        if(num>=col) {return null;}
        IntList pow0 = new IntList();
        IntList pow1 = new IntList();
        IntList pow2 = new IntList();
        IntList pow3 = new IntList();
        IntList pow4 = new IntList();
        ArrayList<Element> coef0 = new ArrayList<Element>();
        ArrayList<Element> coef1 = new ArrayList<Element>();
        ArrayList<Element> coef2 = new ArrayList<Element>();
        ArrayList<Element> coef3 = new ArrayList<Element>();
        ArrayList<Element> coef4 = new ArrayList<Element>();
        int indexCoef = 0;
        for (int i = num; i < p.powers.length; i += col) {
            switch (p.powers[i]) {
                case 4:
                    int[] temp4 = new int[col];
                    System.arraycopy(p.powers, i-num, temp4, 0, col);
                    temp4[num] = 0;
                    pow4.add(temp4);
                    coef4.add(p.coeffs[indexCoef]);
                    break;
                case 3:
                    int[] temp3 = new int[col];
                    System.arraycopy(p.powers, i-num, temp3, 0, col);
                    temp3[num] = 0;
                    pow3.add(temp3);
                    coef3.add(p.coeffs[indexCoef]);
                    break;
                case 2:
                    int[] temp2 = new int[col];
                    System.arraycopy(p.powers, i-num , temp2, 0, col);
                    temp2[num] = 0;
                    pow2.add(temp2);
                    coef2.add(p.coeffs[indexCoef]);
                    break;
                case 1:
                    int[] temp1 = new int[col];
                    System.arraycopy(p.powers, i-num , temp1, 0, col );
                    temp1[num] = 0;
                    pow1.add(temp1);
                    coef1.add(p.coeffs[indexCoef]);
                    break;
                case 0:// собираем все что осталось
                    int[] temp = new int[col];
                    System.arraycopy(p.powers, i-num, temp, 0, col);
                    pow0.add(temp);
                    coef0.add(p.coeffs[indexCoef]);
                    break;
                default: return null;
            } indexCoef++;
        }
        Element[] res = new Element[5];
        res[0] = (coef0.isEmpty()) ? r.numberZERO : createPol(pow0, coef0, r);
        res[1] = (coef1.isEmpty()) ? r.numberZERO : createPol(pow1, coef1, r);
        res[2] = (coef2.isEmpty()) ? r.numberZERO : createPol(pow2, coef2, r);
        res[3] = (coef3.isEmpty()) ? r.numberZERO : createPol(pow3, coef3, r);
        res[4] = (coef4.isEmpty()) ? r.numberZERO : createPol(pow4, coef4, r);
        return res;
    }
  /**
   * Главная процедура для решения уравнений степени не больше чем 4
   * @param p -полином
   * @param num - индекс искомой переменной в кольце
   * @param ring - кольцо
   * @return  корни уравнения p  по переменной ring.varPolynom[num]
 они помещаются в вектор, если два или больше корня
   */
    public Element solvePolynomEq(Polynom P, int num, Ring ring) {
 //  System.out.println("SOLVE===="+P +"  "+num +ring+Array.toString(P.powers));
    if (P.isItNumber()) {return null;}
    if (P.powers.length/P.coeffs.length<=num) {return  ring.varPolynom[num]  ;}
//      System.out.println("SOLVE-1");
    if (P.coeffs.length == 1)return  ring.numberZERO;      // ax^n=0
//       System.out.println("SOLVE-2");
     // смотрим на тип уравнения и решаем соответствующим образом
   
    int rootsMax=P.degree(num); int rootsN=0; 
     FactorPol fp; 
     if (rootsMax>1){ 
         try {fp=( (ring.isExactRing()))? P.factorOfPol_inQ(false, ring):P.factorOfPol_inQ(true, ring) ;}
         catch (Exception ex) {fp=new FactorPol(new int[]{1}, new Polynom[]{P});} }
     else fp=new FactorPol(new int[]{1}, new Polynom[]{P});
    int NumberFacts=fp.multin.length;
    Element[] RES = new Element[rootsMax]; Element[] sSqr=new Element[0];
    int j=0;
    while((j<NumberFacts) && fp.multin[j].isItNumber())j++;
    for (; j < NumberFacts; j++) {Polynom p=fp.multin[j];
        Element[] massCoef = getCoeffs(p, num, ring);
 //        System.out.println("SOLVE-3");
        if(massCoef==null) {//значит степень больше чем 4
            // ищем случаи би- и прочей четности....   must add !!!!!!!!!!!!!!!!
            return null;}
//        if((ring.algebra[0]==Ring.Z)||(ring.algebra[0]==Ring.Q)){
//              Element gcd=p.GCDNumPolCoeffs(ring); if(!gcd.isOne(ring))
//                for (int i = 0; i < massCoef.length; i++) massCoef[i]=massCoef[i].divide(gcd,ring);}
        int typeEq = 0;         
        for (int i = 4; i > 0; i--) { if((massCoef[i]!=null)&&( !massCoef[i].isZero(ring)) ){typeEq = i; break;}}
        if(ring.isExactRing()&& (typeEq==1)){ 
              Polynom minus_b = massCoef[0] instanceof Polynom? (Polynom) massCoef[0].negate(ring): new Polynom(massCoef[0].negate(ring));
              sSqr=new Element[]{divideSE(minus_b, massCoef[1], ring)} ;}
        else
          switch (typeEq) {
//            case 0: // на всякий случай....
//                sSqr=new Element[]{ring.varPolynom[num]};
            case 1: // ax+b=0
                Polynom minus_b = massCoef[0] instanceof Polynom? (Polynom) massCoef[0].negate(ring): new Polynom(massCoef[0].negate(ring));
                sSqr=new Element[]{divideSE(minus_b, massCoef[1], ring)} ;break; 
            case 2: // квадратное
                 sSqr=solveSqrEq(massCoef[2], massCoef[1], massCoef[0], ring); break;            
            case 3: // кубическое и решается методом Кардано
                  sSqr=Kardano(massCoef[3], massCoef[2], massCoef[1], massCoef[0], ring ); break;       
            case 4: // четвертой степени и решается методом Феррари или как биквадратное
                if(massCoef[3].isZero(ring) & massCoef[1].isZero(ring)) {
                sSqr=BiSqr(massCoef[4], massCoef[3], massCoef[2], massCoef[1], massCoef[0], ring);
               }else{
                sSqr=Ferrary(massCoef[4], massCoef[3], massCoef[2], massCoef[1], massCoef[0], ring );}
         }   
         System.arraycopy(sSqr, 0, RES, rootsN, sSqr.length); rootsN+=sSqr.length;
         sSqr=new Element[0];
       }
       if(rootsN<rootsMax){Element[] RES1 = new Element[rootsN]; 
       System.arraycopy(RES, 0, RES1, 0, rootsN); RES=RES1; }
       return (RES.length>1)? new VectorS(RES): (RES.length==1)? RES[0]: Element.NAN; 
  }
 }
