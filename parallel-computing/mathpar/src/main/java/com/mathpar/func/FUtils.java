package com.mathpar.func;

import com.mathpar.func.parser.Parser;
import java.util.ArrayList;
import java.util.Arrays;
import com.mathpar.number.*;
import com.mathpar.polynom.Polynom;
/**
 *
 * @author gennady
 */
public class FUtils {
    
      /**
     * Search and delete external pairs of the form ({(.... ......)})
     * @param W - input String
     * @return output string
     */
    public static String deleteExternalBrackets(String W) {
        W=W.trim();int l=W.length()-1;
        while(true){
           if(((W.charAt(0)=='(')&&(W.charAt(l)==')'))||
              ((W.charAt(0)=='{')&&(W.charAt(l)=='}'))) {W=W.substring(1, l ); l-=2;}
           else break;
        }    
        return W;
    }
    /**
     * Search and delete all the pairs of ((....((.....)).......))
     * @param inp
     * @return 
     */
    public static String deleteDoubledBrackets(String inp) {
        int size = inp.length();
        StringBuilder str1 = FUtils.cleanDoubledBrackets(new StringBuilder(inp));
        int size1 = str1.length();
        while (size1 != size) {
            size = size1;
            str1 = FUtils.cleanDoubledBrackets(str1);
        }
        return str1.toString();
    }
/**
 * Search and delete   pairs  of brackets ((.....))
 * @param inp StringBuilder
 * @return StringBuilder
 */
 public static StringBuilder cleanDoubledBrackets(StringBuilder inp) {if(inp.length()<5) return inp;
        StringBuilder res = inp;
        int index1;
        for (int pp = 0; pp < res.length(); pp++) {
            index1 = res.indexOf("((", pp); if (index1==-1) break;
            int indexC = res.indexOf(")", index1 + 2);
            int indexO = res.indexOf("(", index1 + 2);
            if ((indexC < indexO) || (indexO == -1)) {
                if ((indexC + 1 < res.length()) && (res.charAt(indexC + 1) == ')')) {
                    res.deleteCharAt(indexC);
                    res.deleteCharAt(index1);
                    pp--;
                }
            } else {
                int counter = 1;
                for (int k = indexO + 1; k < res.length(); k++) {
                    char charK = res.charAt(k);
                    if (charK == '(') {
                        counter++;
                    } else {
                        if (charK == ')') {
                            counter--;
                        }
                    }
                    if (counter == -1) {
                        if ((k + 1 < res.length()) && (res.charAt(k + 1) == ')')) {
                            res.deleteCharAt(k);
                            res.deleteCharAt(index1);
                            pp--;
                        }
                        break;
                    }
                }
            }
        }
        // удалим скобки в которых нет операций + -, а перед ними нет функции и если после скобок нет шапочки или штриха
        // нужно сохранить скобки при "(y^5)^6" 
        // и нужно сохранить \sin([х])
        int pp = -1;
        b:
        while (pp < res.length()) {
            int index = res.indexOf("(", pp);
            if (index == -1) {return res;}
//            int indexSB = res.indexOf("[", index); //удаляем ([  ]), т.е. с вектора снимаем круглые скобки.
//            if (indexSB != -1) {
//                int tmp_bl = index + 1;
//                int ttt = res.charAt(tmp_bl);
//                while ((tmp_bl < indexSB) && ((ttt == ' ') || (ttt == '\n'))) {
//                    tmp_bl++;
//                }
//                if (res.charAt(tmp_bl) == '[') {
//                    int posPareSB = posOfPareBracket(res, indexSB, '[', ']');
//                    int temp = posPareSB + 1;
//                    if ((posPareSB != -1) && (temp < res.length())) {
//                        ttt = res.charAt(temp);
//                        while ((temp < res.length()) && ((ttt == ' ') || (ttt == '\n'))) {
//                            temp++;
//                            ttt = res.charAt(temp);
//                        }
//                        if (ttt == ')') {
//                            res.deleteCharAt(temp);
//                            res.deleteCharAt(index);
//                            return res;
//                        }
//                    }
//                }
//            }
            int posPare = posOfPareBracket(res, index, '(', ')');
            int tmp_i = posPare + 1;
            while ((tmp_i < res.length()) && (res.charAt(tmp_i) == ' ')) {
                tmp_i++;
            }
            if ((tmp_i < res.length()) && (((res.charAt(tmp_i) == '^') || (res.charAt(tmp_i) == '\'')))) {
                pp = index + 1;
                continue b;
            }
            int k = index + 1;
            while (k < posPare) {
                char charK = res.charAt(k);
                if ((charK == '+') || (charK == '-')) {
                    pp = index + 1;
                    continue b;
                } else {
                    if (charK == '(') {
                        k = posOfPareBracket(res, k, '(', ')') + 1;
                    } else {
                        k++;
                    }
                }
            }
            int indexBackSlash = res.lastIndexOf("\\", index);
            if (indexBackSlash == -1 && posPare != -1 &&
                 (!((index>1)&&(res.charAt(index-1)=='{')&&(res.charAt(index-2)=='^')))) // <- показатель производной сохраняем в скобках
            {
                res.deleteCharAt(posPare);
                res.deleteCharAt(index);
                pp = index; 
            } else {
                int indexO = res.indexOf("(", indexBackSlash);
                int indexFB = res.indexOf("{", indexBackSlash);
                int posPareFB = (indexFB == -1) ? (-1) : posOfPareBracket(res, indexFB, '{', '}');
                int indexO2 = (posPareFB == -1) ? (-1) : res.indexOf("(", posPareFB);
                if ((((indexO2 != -1) && (indexO2 < index)) || ((index < posPareFB) && (index > indexFB))
                        || ((indexO < index) && ((indexO < indexFB) || (indexFB == -1))) ) &&
                     (!((index>1)&&(res.charAt(index-1)=='{')&&(res.charAt(index-2)=='^'))) ) // <- показатель производной сохраняем в скобках
                {
                    res.deleteCharAt(posPare);
                    res.deleteCharAt(index);
                    pp = index;
                } else {
                    pp = index + 1;
                }
            }
        }
        if(res.charAt(0)=='(') {int posStart = posOfPareBracket(res, 0, '(', ')');
           if(res.length()-1==posStart){ res.deleteCharAt(0);res.deleteCharAt(posStart);}
        }// удаление первой и парной последней скобки
        return (res);
    }

    /**
     * Позиция парной закрывающей скобки
     *
     * @param str -- строка, в которой происходит поиск
     * @param posOfOpenBracket -- позиция открывающей скобки
     * @param bracketOp --открывающая скобка (char)
     * @param bracketCl -pзакрывающая скобка (char)
     *
     * @return
     */
    public static int posOfPareBracket(StringBuilder str, int posOfOpenBracket,
            char bracketOp, char bracketCl) {
        int nestingLevel = 0;
        for (int pp = posOfOpenBracket + 1, len = str.length(); pp < len; pp++) {
            char charK = str.charAt(pp);
            if (charK == bracketOp) {
                nestingLevel++;
            } else if (charK == bracketCl) {
                nestingLevel--;
            }
            if (nestingLevel == -1) {
                return pp;
            }
        }
        return -1;
    }

    /**
     * Find the closing bracket
     *
     * @param str -- string for search
     * @param posOfOpenBracket
     * @param bracketOp -- char of opening bracket
     * @param bracketCl -- char of closing bracket
     *
     * @return
     */
    public static int posOfPairBracket(String str, int posOfOpenBracket,
            char bracketOp, char bracketCl) {
        int nestingLevel = 0;  int len = str.length();
        for (int pp = posOfOpenBracket + 1; pp < len; pp++) {
            char charK = str.charAt(pp);
            if (charK == bracketOp) { nestingLevel++;} 
            else if (charK == bracketCl) {nestingLevel--;}
            if (nestingLevel == -1) { return pp; }
        } return -1;
    }
    
        public static int posOfPairBracket3(String str, int posOfOpenBracket) {
        int nestingLevel = 0;  int len = str.length();
        for (int pp = posOfOpenBracket + 1; pp < len; pp++) {
            char charK = str.charAt(pp);
            if ((charK == '(')||(charK == '{')||(charK == '[')) { nestingLevel++;} 
            else if ((charK == ')')||(charK == '}')||(charK == ']')) {nestingLevel--;}
            if (nestingLevel == -1) { return pp; }
        } return -1;
    }
/**
 * Searching the position of the last sign "=", which is not in the function
 * Example:  for the string "a=b=c=\solve(h=d)" we get "5".
 * @param str
 * @return the position of the last sign "="
 */
     public static int posOfLastFreeEq(String str){
       int pEq=str.lastIndexOf('='); if (pEq==-1) return -1;
       int n=str.length();
       SubsetZ all=new SubsetZ(new int[]{0,n-1});  SubsetZ BC=null;
       int pBr=str.indexOf('{');int cBr;
       while(pBr!=-1){ cBr=posOfPairBracket(str,pBr,'{','}');  if (cBr==-1){return -2;}
            all=all.subtraction(new SubsetZ(new int[]{pBr,cBr})); pBr=str.indexOf('{',cBr+1); }
       pBr=str.indexOf('(');
       while(pBr!=-1){BC=new SubsetZ(new int[]{pBr,pBr});
           if(BC.intersection(all).equals(BC)){  cBr=posOfPairBracket(str,pBr,'(',')'); 
           if (cBr==-1){return -3;}
                 all=all.subtraction(new SubsetZ(new int[]{pBr,cBr})); pBr=str.indexOf('(',cBr+1);}
           else {pBr=str.indexOf('(',pBr+1);}       }
       pBr=str.indexOf('[');
       while(pBr!=-1){BC=new SubsetZ(new int[]{pBr,pBr});
           if(BC.intersection(all).equals(BC)){  cBr=posOfPairBracket(str,pBr,'[',']');  if (cBr==-1){return -4;}
                 all=all.subtraction(new SubsetZ(new int[]{pBr,cBr})); pBr=str.indexOf('[',cBr+1);}
           else {pBr=str.indexOf('[',pBr+1);}       }
       while(pEq!=-1){BC=new SubsetZ(new int[]{pEq,pEq});
           if(BC.intersection(all).equals(BC))return pEq;
           else {pEq=str.lastIndexOf('=',pEq-1);}       }
      return -1;
     }
    
    
    /**
     * For D and d: to string
     *
     * @param r
     *
     * @return
     */
    public static String toStringD(Element[] x, Ring r) {
        String temp = "";
        StringBuilder res = new StringBuilder();
        int xLen = x.length;
        if (xLen > 1) {
            if (x[1] instanceof F) {
                int i = 0;
                F ff = (F) x[1];
                Element[] xx;
                if ((ff).name == F.VECTORS) {
                    xx = (ff).X;
                } else {
                    xx = x;
                    i = 1;
                }
                for (; i < xLen; i++) {
                    temp += xx[i].toString(r);
                }
            }
            temp = x[1].toString(r);
            int l = temp.length();
            if (l > 1) {
                temp = "{" + temp + "}";
            }
            if (xLen == 2) {
                return res.append("{").append(x[0].toString(r)).append("'}_").append(temp).toString();
            }
            String d;
            if (xLen == 4) {
                temp = "{" + temp + "=" + x[2].toString(r) + "}";
                d = x[3].toString(r);
            } else {
                d = x[2].toString(r);
            }

            if (d.equals("0")) {
                d = "{}";
            } else if (d.equals("1")) {
                d = "{'}";
            } else if (d.equals("2")) {
                d = "{''}";
            } else if (d.equals("3")) {
                d = "{'''}";
            } else {
                d = "{(" + d + " )}";
            }
            return res.append("").append(x[0].toString(r)).append("^")
                    .append(d).append("_").append(temp).toString();
        } else {
            return x[0].toString(r) + "'";
        }
    }

    public static String toStringD_new(String w, String ind,  Ring ring) {
        String[] x = cutByCommas(w, ring); int xLen = x.length; 
        StringBuilder res = new StringBuilder();  res.append("D");   
        if (w.indexOf("[") != -1) {res.append("_{");
            if (xLen == 2) {x[1] = x[1].substring(1, x[1].length() - 1);} 
            else { x[1] = x[1].substring(1);
                x[xLen - 1] = x[xLen - 1].substring(0, x[xLen - 1].length() - 1); }
            for (int k = 1; k < xLen; k++) {res.append(x[k]); }
            res.append("}(").append(x[0]).append(")");
        } else  res.append(ind).append("(").append(w).append(")");
        return res.toString();  
    }
   
        public static String toStringD(String w, String ind,  Ring ring) {
        String[] x = cutByCommas(w, ring);
        int xLen = x.length;
        StringBuilder res = new StringBuilder();     
        if (w.indexOf("[") != -1) {res.append("\\partial_{");
            if (xLen == 2) {x[1] = x[1].substring(1, x[1].length() - 1);} 
            else { x[1] = x[1].substring(1);
                x[xLen - 1] = x[xLen - 1].substring(0, x[xLen - 1].length() - 1); }
            for (int k = 1; k < xLen; k++) {res.append(x[k]); }
            res.append("}(").append(x[0]).append(")");
        } else if(ind.length()>0) res.append("\\partial").append(ind).append("(").append(w).append(")");
               else res.append("{\\mathbf d}(").append(w).append(")");
        return res.toString();  
    }

    
    public static String toStringD_(String W, String ind, Ring ring) {
        // В параметре ind находится все, что нужно для нормального Texoвания - нижний индекс
        // Можно воспользоваться им также как в  toStringD_new  !! 
        // (но пока я никак его не использую тут )
       //    ВАЖНО, что процедура toInitCond сюда обращается тоже...
        String[] X = cutByCommas(W, ring);
        String temp;
        StringBuilder res = new StringBuilder();
        int xl = X.length;
        if (xl > 1) {String d="";
            temp = X[1];
            int l = temp.length();
            temp = "{" + temp + "}";  
            switch (X.length) {
                case 2:
                    Polynom degr=null; int cc=0;
                    try{ degr = new Polynom(X[1], ring);
                       cc=degr.coeffs.length; cc= (cc==1)?0: 1/0;      }
                    catch(Exception ee){ring.exception.append("The derivative can be defined only for the variables that are defined in the SPACE: but \" ").append(X[1])
                            .append("\" is not in it.");return "";}
                    for (int i = 0; i < degr.powers.length; i++) cc=cc+degr.powers[i];
                    d=Integer.toString(cc);
                    break;
                case 4:
                    temp="{"+temp+"="+X[2]+"}";
                    d = X[3];
                    break;
                default:
                    d = X[2];
                    break;
            }
        
            switch (d) {
                case "0":             break;
                case "1":  d = "'_";  break;
                case "2":  d = "''_"; break;
                case "3":  d = "'''_";break;
                default:   d = "^{(" + d + ")}_";
            }
            return  X[0]+d+temp;
        } else {return X[0] + "'"; }
    }
    public static String toInitCond(String W, Ring ring) {
        String[] X = cutByCommas(W, ring);
        if (X.length < 4) {return W; }
        if (X.length == 4) { return toStringD(W, "", ring); }
        int partialNum = 0;
        String dif = "";
        String point = "";
        for (int i = 1; i < X.length; i += 3) {
            if (!X[i + 1].trim().equals("")) {
                point += (X[i] + "=" + X[i + 1] + ",");
            }
            if (!X[i + 2].trim().equals("0")) {
                if (X[i + 2].equals("1")) {
                    dif += "\\partial " + X[i];
                    partialNum++;
                } else {
                    partialNum += new Integer(X[i + 2]).intValue();
                    dif += "\\partial " + (X[i] + "^{" + X[i + 2] + "}");
                }
            }
        }

        if (dif.length() == 0) {
            return X[0] + "_{" + point.substring(0, point.length() - 1) + "}";
        }
        String ff = (partialNum > 1) ? "\\frac{\\partial^{" + partialNum + "}" +
                X[0] + "} {" + dif + "}" : "\\frac{\\partial " + X[0] + "} {" + dif + "}";

        return ff + "\\vert_{" + point.substring(0, point.length() - 1) + "}";
    }

    /**
     * Is two string equals if we ignore all blank symbols
     * @param W String
     * @param U String
     * @return true if equals, false if not equals
     */
    public static boolean equalsIgnoreBlanks(String W, String U) {
        int i=0, j=0;
        char[] w= W.toCharArray(); char[] u= U.toCharArray();
          while(true){
            while((i<w.length)&&(w[i]==' '))i++;
            while((j<u.length)&&(u[j]==' '))j++;
            if((i<w.length)&&(j<u.length)){if (w[i]!=u[j])return false;i++;j++;} 
            else break;
          }if(i==w.length)while(j<u.length){if(u[j]!=' ') return false;j++;}
          else{ while(i<w.length){if(w[i]!=' ') return false;i++;}}
          return true;
    }

    private static boolean chekForcutBC(String W) {
        int posOP = W.indexOf("{");
        if (posOP != -1) {
            return W.indexOf("}") == -1;
        }
        return false;
    }

//    public static String[] cutByCommas_(String W, Ring ring) {
//        ArrayList<String> res = new ArrayList<String>();
//        String[] ll = cutByCommas(W, ring);
//        res.addAll(Arrays.asList(ll));
//        for (int i = 0; i < res.size(); i++) {
//            if (chekForcutBC(res.get(i))) {
//                String el = res.get(i) + "," + res.get(i + 1);
//                res.remove(i);
//                res.remove(i);
//                res.add(i, el);
//            }
//        }
//        String[] ress = res.toArray(new String[res.size()]);
//        int indexLast = ress.length - 1;
//        if (ress[indexLast].lastIndexOf(")") == ress[indexLast].length() - 1) {
//            ress[indexLast] = ress[indexLast].substring(0, ress[indexLast].length() - 2);
//        }
//        return res.toArray(new String[res.size()]);
//    }

    /**
     * делим строку по запятым и записываем каждый интервал в строковый массив
     * как новую строку, то есть было (),(),() и так далее, а стало [(),(),()].
     *
     * @param W
     * @param ring
     *
     * @return
     */
    public static String[] cutByCommas(String W, Ring ring) {
        int p  = W.indexOf(','); if (p==-1)return new String[]{W};
        int len = W.length(); 
        int[] zap = new int[len];
        int pB= W.indexOf('('),pBB=0,pFF=0,pQQ=0,
            pF= W.indexOf('{'),
            pQ= W.indexOf('[');
        int i = 0; // number of comma
        int BFQ=-1;
        while(p>0){
            if(pB>BFQ && pB<p)pBB = posOfPairBracket(W, pB, '(', ')');
            if(pF>BFQ && pF<p)pFF = posOfPairBracket(W, pF, '{', '}');
            if(pQ>BFQ && pQ<p)pQQ = posOfPairBracket(W, pQ, '[', ']');
            BFQ=Array.max(new int[]{pBB,pFF,pQQ});
            if(BFQ<p){zap[i++] = p; BFQ=p+1; }
            p=W.indexOf(',',BFQ);  
            pB= W.indexOf('(',BFQ); 
            pF= W.indexOf('{',BFQ);
            pQ= W.indexOf('[',BFQ); }  
        zap[i++] = len;
        String[] res = new String[i];
        int start = 0;
        for (int j = 0; j < i; j++) {
            res[j] = W.substring(start, zap[j]);
            start = ++zap[j];
        }
        return res;
    }

    /**
     * Make TeX-VectorS in the position pos of StringBuffer NN from expression
     * in brakets: [ , , , .....]. As a result Integer pos moved to the first
     * position after VectorS in NN.
     *
     * @param NN StringBuffer
     * @param pos -- position of "["
     * @param ring -- Ring
     */
    public static int makeTeXVector(StringBuilder NN, int pos, Ring ring) {
        int ll = pos;   //NN.indexOf("[");
        String SSnew = "\\left( \\begin{array}{l}";
        if (NN.lastIndexOf("SPACE", ll) < NN.lastIndexOf(";", ll)) {
            int clouse = posOfPareBracket(NN, ll, '[', ']');
            String dd = NN.substring(ll + 1, clouse);
            String[] SS = cutByCommas(dd, ring);
            for (int s = 0; s < SS.length; s++) {
                SSnew += SS[s] + "\\\\";
            }
            SSnew += " \\end{array}\\right)";
            NN.delete(ll, clouse + 1);
            NN.insert(ll, SSnew);
        }
        return ll + SSnew.length() + 1;
    }

    /**
     * делим строку по запятым, учитывая различные виды скобок, и записывает
     * интервал(полуинтервал или отрезок) в строковый массив как новую строку,
     * то есть было (],(),[] и так далее, а стало [(],(),[]].
     *
     * @param W
     * @param ring
     *
     * @return
     */
    public static String[] cutByCommasSets(String W, Ring ring) {
        int len = W.length();
        int[] zap = new int[(len == 0) ? 1 : len];
        int p = -1, i = 0, pB = 0, end = -1, posPar = 0;
        boolean newBr = true, newCom = true;
        a:
        do {
            if (newCom) { p = W.indexOf(',', p + 1); if (p == -1) { break a;} }
            if (newBr) {
                int pB1 = W.indexOf('(', end);
                int pB2 = W.indexOf('[', end);
                pB = Math.min(pB1, pB2);
                int pB3 = Math.max(pB1, pB2);
                if (pB3 == -1) {pB = len; posPar = len;} 
                else {
                    if (pB == -1) { pB = pB3; }
                    posPar = posOfPairBracket3(W, pB ); 
                    if (posPar  == -1) {
                        ring.exception.append(" Missing closing parenthesis. ");
                        return null;
                    }                          
                }
            }
            if (p < pB) { zap[i++] = p; newBr = false; newCom = true;} 
            else {
                if (p < posPar) {newBr = false; newCom = true;} 
                else { newBr = true; end = posPar;  newCom = false; }
            }
        } while (true);
        zap[i++] = len; String[] res = new String[i]; int start = 0;
        for (int j = 0; j < i; j++) { res[j] = W.substring(start, zap[j]); start = ++zap[j]; }
        return res;
    }
    
    /** is integer number between the integers or equal one of them
      * 
     * @param x  - integer
     * @param a - left border
     * @param b - right border
     * @return  false = is out of the interval [a,b]
     */
    public static boolean isIntBetweenOf(int x, int a, int b){
          return (x<a)? false : (x>b)? false: true; }
}
