/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.web.executor.api3d;

//import com.mathpar.Graphic3D.explicit.ParametricSurfaces;
//import com.mathpar.Graphic3D.explicit.SurfaceBuilder;
import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.F;
import com.mathpar.func.Fname;
import com.mathpar.func.Page;
import com.mathpar.number.Element;
import com.mathpar.web.exceptions.MathparException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.apache.logging.log4j.Logger;

/**
 * render multiple surfaces
 * @author artem
 */
public class RenderMultipleSurface implements Callable<List<double[]>> {
    private static final Logger LOG = getLogger(RenderMultipleSurface.class);
    
    private final Page page;
    private final String task;
    private final int sectionId;
    
    public RenderMultipleSurface(Page page, String task, int sectionId) {
        this.page = page;
        this.task = task;
        this.sectionId = sectionId;
    }
    
    @Override
    public List<double[]> call() throws Exception {
        
        List<double[]> geometry = new ArrayList();
        List<double[]> settings = new ArrayList();
        
        String ignore = page.execution(task, sectionId);
        // TODO: better function detection - it could be not the last.
        final Element expr = page.expr.get(page.expr.size() - 1);
        if (expr instanceof Fname) {
            Element[] exprArgs = ((Fname) expr).X;
            if (exprArgs.length == 1 && exprArgs[0] instanceof F
                    && ((F) exprArgs[0]).name == F.SHOW_3D) {
                
                final F renderSurfaces = (F) exprArgs[0];
                int argsLength = renderSurfaces.X.length;
                
                if (argsLength < 2) {
                    throw new MathparException(
                        String.format("argsLength > 2 for renderSurface")
                    );
                }
                
                int numberSurfaces = renderSurfaces.X[0].intValue();
                System.out.println("numberSurfaces: " + numberSurfaces);
                settings.add(new double[] {numberSurfaces}); // number of surface
                
                F[] surfaceFunctions = new F[numberSurfaces];
                
                int offset = 1; // shift to the first function
                for (int i = 0; i < numberSurfaces; i++) {
                    surfaceFunctions[i] = (F) renderSurfaces.X[offset + i];
                }
                
                // iterate functions and define types
                for (int i = 0; i < surfaceFunctions.length; i++) {
                    if (surfaceFunctions[i].X[0] instanceof Fname) {
                        Element[] childExprArgs = ((Fname) surfaceFunctions[i].X[0]).X;
                        if (childExprArgs.length == 1 && childExprArgs[0] instanceof F
                            && ((F) childExprArgs[0]).name == F.PARAMETRIC_PLOT3D) { // if function is equal PARAMETRIC_PLOT3D
                            System.out.println("build parametric plot 3d surface");
                            // entry point to parametricPlot3d
                            List<double[]> vertice = null; //this.getParametricPlot3dGeometry(childExprArgs);
                            int numberVertice = vertice.size(); // vert[0] - settings
                            settings.add(new double[] {F.PARAMETRIC_PLOT3D, numberVertice});
                            
                            System.out.println("function: " + F.PARAMETRIC_PLOT3D);
                            System.out.println("number vertices: " + numberVertice);
                            
                            geometry.addAll(vertice);
                        }
                    }
                    
                    if (surfaceFunctions[i].X[0] instanceof Fname) {
                        Element[] childExprArgs = ((Fname) surfaceFunctions[i].X[0]).X;
                        if (childExprArgs.length == 1 && childExprArgs[0] instanceof F
                            && ((F) childExprArgs[0]).name == F.EXPLICIT_PLOT3D) { // if function is equal PARAMETRIC_PLOT3D
                            System.out.println("build explicit plot 3d surface");
                            // entry point to explicitPlot3d
                            List<double[]> vertice = this.getExplicitPlot3dGeometry(childExprArgs);
                            int numberVertice = vertice.size(); // vert[0] - settings
                            settings.add(new double[] {F.EXPLICIT_PLOT3D, numberVertice});
                            
                            System.out.println("function: " + F.EXPLICIT_PLOT3D);
                            System.out.println("number vertices: " + numberVertice);
                            
                            geometry.addAll(vertice);
                        }
                    }
                }
            }
            settings.addAll(geometry);
            return settings;
        }
        
        LOG.warn("Can't get parameters for {} function. Return null.", F.FUNC_NAMES[F.SHOW_3D]);
        return null;
    }
    
    private List<double[]> getExplicitPlot3dGeometry(Element[] exprArgs) {
        final F explPlot3d = (F) exprArgs[0];
        F funcToPlot = (F) explPlot3d.X[0];
        final int argsLen = explPlot3d.X.length;
        double xMin = 1; // SurfaceBuilder.DEFAULT_X_MIN;
        double xMax =  1; // SurfaceBuilder.DEFAULT_X_MAX;
        double yMin =  1; // SurfaceBuilder.DEFAULT_Y_MIN;
        double yMax = 1; //  SurfaceBuilder.DEFAULT_Y_MAX;
        double zMin = 1; //  SurfaceBuilder.DEFAULT_Z_MIN;
        double zMax =  1; // SurfaceBuilder.DEFAULT_Z_MAX;
        int gridSize = 4;

        if (argsLen == 7) {
            xMin = explPlot3d.X[1].doubleValue();
            xMax = explPlot3d.X[2].doubleValue();
            yMin = explPlot3d.X[3].doubleValue();
            yMax = explPlot3d.X[4].doubleValue();
            zMin = explPlot3d.X[5].doubleValue();
            zMax = explPlot3d.X[6].doubleValue();
        }

        if (argsLen > 7) {
            gridSize = explPlot3d.X[7].intValue();
        }

        if (xMax <= xMin) {
            throw new MathparException(String.format(
                    "xMax (%f) must be > xMin (%f) for explicitPlot3d", xMax, xMin));
        }
        if (yMax <= yMin) {
            throw new MathparException(String.format(
                    "yMax (%f) must be > yMin (%f) for explicitPlot3d", yMax, yMin));
        }
        if (zMax <= zMin) {
            throw new MathparException(String.format(
                    "zMax (%f) must be > zMin (%f) for explicitPlot3d", zMax, zMin));
        }

        if (gridSize < 3) {
            throw new MathparException(String.format(
                    "gridSize (%d) for explicitPlot3d must be >= 3", gridSize));
        }

        Element funcToPlotEl = funcToPlot.ExpandFnameOrId();
        funcToPlot = (funcToPlotEl instanceof F) ? (F) funcToPlotEl : new F(funcToPlotEl);
//        SurfaceBuilder surface = new SurfaceBuilder(page.ring(), funcToPlot,
//                xMin, xMax, yMin,
//                yMax, zMin, zMax,
//                gridSize);
        
        return null; //surface.generateVertices();
    }
    
//    private List<double[]> getParametricPlot3dGeometry(Element[] exprArgs) {
//        double xMin = ParametricSurfaces.DEFAULT_X_MIN;
//        double xMax = ParametricSurfaces.DEFAULT_X_MAX;
//        double yMin = ParametricSurfaces.DEFAULT_Y_MIN;
//        double yMax = ParametricSurfaces.DEFAULT_Y_MAX;
//        double zMin = ParametricSurfaces.DEFAULT_Z_MIN;
//        double zMax = ParametricSurfaces.DEFAULT_Z_MAX;
//
//        final F paramPlot3d = (F) exprArgs[0];
//
//        final int argsLen = paramPlot3d.X.length;
//
//        if (argsLen < 8) {
//            throw new MathparException(String.format(
//                    "argsLen > 7 for parametricPlot3d", xMax, xMin));
//        }
//
//        F xFunc = (F) paramPlot3d.X[0];
//        F yFunc = (F) paramPlot3d.X[1];
//        F zFunc = (F) paramPlot3d.X[2];
//        
//        System.out.println(xFunc.name);
//        
//        // init U V
//        double uMin = paramPlot3d.X[3].doubleValue();
//        double uMax = paramPlot3d.X[4].doubleValue();
//        double vMin = paramPlot3d.X[5].doubleValue();
//        double vMax = paramPlot3d.X[6].doubleValue();
//        int gridSize = paramPlot3d.X[7].intValue();
//
//        if (gridSize < 3) {
//            throw new MathparException(String.format(
//                    "gridSize (%d) for parametricPlot3d must be >= 3", gridSize));
//        }
//
//        Element xFuncEl = xFunc.ExpandFnameOrId();
//        Element yFuncEl = yFunc.ExpandFnameOrId();
//        Element zFuncEl = zFunc.ExpandFnameOrId();
//
//        xFunc = (xFuncEl instanceof F) ? (F) xFuncEl : new F(xFuncEl);
//        yFunc = (yFuncEl instanceof F) ? (F) yFuncEl : new F(yFuncEl);
//        zFunc = (zFuncEl instanceof F) ? (F) zFuncEl : new F(zFuncEl);
//
////        ParametricSurfaces surface = new ParametricSurfaces(
////                page.ring(),
////                xFunc,
////                yFunc,
////                zFunc,
////                uMin,
////                uMax,
////                vMin,
////                vMax,
////                xMin, xMax, yMin,
////                yMax, zMin, zMax,
////                gridSize);
//        
//        return surface.generateVertices();
//    }
}
