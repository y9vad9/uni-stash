package com.mathpar.web.executor;

//import com.mathpar.Graphic3D.explicit.ParametricSurfaces;
import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.F;
import com.mathpar.func.Fname;
import com.mathpar.func.Page;
import com.mathpar.number.Element;
import com.mathpar.web.exceptions.MathparException;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.Logger;

public class ParametricPlot3dCallable implements Callable<List<double[]>> {
    private static final Logger LOG = getLogger(ParametricPlot3dCallable.class);

    private final Page page;
    private final String task;
    private final int sectionId;

    public ParametricPlot3dCallable(Page page, String task, int sectionId) {
        this.page = page;
        this.task = task;
        this.sectionId = sectionId;
    }

    @Override
    public List<double[]> call() throws Exception {
//        String ignore = page.execution(task, sectionId);
//        // TODO: better function detection - it could be not the last.
//        final Element expr = page.expr.get(page.expr.size() - 1);
//        if (expr instanceof Fname) {
//            Element[] exprArgs = ((Fname) expr).X;
//            if (exprArgs.length == 1 && exprArgs[0] instanceof F
//                    && ((F) exprArgs[0]).name == F.PARAMETRIC_PLOT3D) {
//                
//                double xMin = ParametricSurfaces.DEFAULT_X_MIN;
//                double xMax = ParametricSurfaces.DEFAULT_X_MAX;
//                double yMin = ParametricSurfaces.DEFAULT_Y_MIN;
//                double yMax = ParametricSurfaces.DEFAULT_Y_MAX;
//                double zMin = ParametricSurfaces.DEFAULT_Z_MIN;
//                double zMax = ParametricSurfaces.DEFAULT_Z_MAX;
//              
//                final F paramPlot3d = (F) exprArgs[0];
//                
//                final int argsLen = paramPlot3d.X.length;
//                
//                if (argsLen < 8) {
//                    throw new MathparException(String.format(
//                            "argsLen > 7 for parametricPlot3d", xMax, xMin));
//                }
//                
//                // init x y z functions
//                F xFunc = (F) paramPlot3d.X[0];
//                F yFunc = (F) paramPlot3d.X[1];
//                F zFunc = (F) paramPlot3d.X[2];
//                
//                // init U V
//                double uMin = paramPlot3d.X[3].doubleValue();
//                double uMax = paramPlot3d.X[4].doubleValue();
//                double vMin = paramPlot3d.X[5].doubleValue();
//                double vMax = paramPlot3d.X[6].doubleValue();
//                int gridSize = paramPlot3d.X[7].intValue();
//                
//                if (gridSize < 3) {
//                    throw new MathparException(String.format(
//                            "gridSize (%d) for parametricPlot3d must be >= 3", gridSize));
//                }
//
//                Element xFuncEl = xFunc.ExpandFnameOrId();
//                Element yFuncEl = yFunc.ExpandFnameOrId();
//                Element zFuncEl = zFunc.ExpandFnameOrId();
//                
//                xFunc = (xFuncEl instanceof F)? (F) xFuncEl: new F(xFuncEl);
//                yFunc = (yFuncEl instanceof F)? (F) yFuncEl: new F(yFuncEl);
//                zFunc = (zFuncEl instanceof F)? (F) zFuncEl: new F(zFuncEl);
//                
//                ParametricSurfaces surface = new ParametricSurfaces(
//                        page.ring(), 
//                        xFunc,
//                        yFunc,
//                        zFunc,
//                        uMin,
//                        uMax,
//                        vMin,
//                        vMax,
//                        xMin, xMax, yMin,
//                        yMax, zMin, zMax,
//                        gridSize);
  //              return null; //surface.generateVertices();
  //          }
   //     }
//        LOG.warn("Can't get parameters for {} function. Return null.", F.FUNC_NAMES[F.IMPLICIT_PLOT3D]);
        return null;
    }
}
