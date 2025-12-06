 
    
package com.mathpar.webWithout;
import com.mathpar.func.CanonicForms;
import com.mathpar.func.F;
import com.mathpar.func.Page;
import com.mathpar.number.*;
import com.mathpar.webWithout.tensors.TensorFacade;

/**
 * This is tool for chenew_fcking task, which is written in Mathpar language. You can
 * copy-past here text of Mathpar program. \\int(7\\ln(x -5))
 *^
 * @author gennadi (7\i/2* (\ln(\abs(x+\i)))- (7\i/2* (\ln(\abs(x-\i)))))
 *                 x=-2;y=2; z=\\pi/2 ;p=x +y+z; h=\\value(\\sin(\\value(p)))
 *  (-1* \\arctg((-x)/t))/ t     a = \set((-2,1),[2,5),(5.75,6],{8});
 * a=
 */



public class Play_On_Ground {
    public static void main(String[] args) throws Exception {
        String txt
                =
                //convolution
               //  "{}_{c}^{b}A_{xqq}^{nqq}; ";
  "SPACE = R64[x];" +
"M=[[1,2, 1,4],[10,2, 11,4],[1,12, 1,14],[10,2, 10,4]]; A=[1,2,3,4]^T;  F=M*A;" +
"  F ;  ";

                //adding
                //"{}_{c}^{b}A_{x}^{n} + {}_{c}^{b}B_{x}^{n}"

                //subtraction
                //"{}_{c}^{b}A_{x}^{n} - {}_{c}^{b}B_{x}^{n} - {}_{c}^{b}C_{x}^{n}"

                //multiplication
                //"B_{x}^{saa} * C_{x}^{a}"

                //combined
                //"A_{xx}^{saaa} + B_{x}^{saa} * C_{x}^{a} - D_{xx}^{saaa}"

                //invalid add operation
                //"{}_{c}^{b}A_{xaaaaa}^{n} + {}_{c}^{b}B_{x}^{n}"

                //invalid input string
                //"{}_{c}^{b}A_{xqqqq}^{nqq}}}}"


                //  put your program here like this: "SPACE=Z[x, y];"---  \\exp(x)+\\exp(-x)
//                  "SPACE = Q[x,y];\n"+
//                                 "\\int( \\exp(x)*\\sin(x))d x;"
                //      "WW=\\solveHDE( y^2\\d(y,x)  = xy\\d(y,x));"
                //    "l = \\Factor(g);"
                startDebug(txt);
       // startDebug(new TensorFacade().performOperationsOnString(txt));
    }

    
    
  public  static void startDebug(String txt) throws Exception {
        String out;
        Page page = new Page(Ring.ringR64xyzt, true);
        page.ring.page=page;
        Page.addTexBlanks(new StringBuilder(txt));
        page.execution(txt, 0);
        out = page.data.section[1].toString();
        System.out.println("Result: " + out);
        String latex = page.strToTexStr(txt, false);
        System.out.println("LaTeX: " + latex);
        System.out.println(page.ring.exception.length() != 0
                ? "ring.exception: " + page.ring.exception : "No ring.exception");
                String latex1 = page.strToTexStr
        (page.data.section[0] + "\nout:\n"
                + page.data.section[1], false)
                .replaceAll("\\\\unicode\\{xB0\\}", "^{\\\\circ}\\\\!")
                 .replaceAll("([^'A-Za-z0-9\\)\\]\\}])''", "$1\\\\hbox{}")
                .replaceAll("'([^\n;]+?)'", "\\\\hbox{$1}");
        System.out.println("Latex: " + latex1);
    }
}
// SPACE=R64[x,y]; f=\\solveDESV(\\systLDE(\\sin(y^2)/\\sin(y)=\\d(y,x)));
