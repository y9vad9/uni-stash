package com.mathpar.web.controller;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.F;
import com.mathpar.func.Page;
import com.mathpar.web.entity.MathparRequest;
import com.mathpar.web.executor.ImplicitPlot3dTimeoutRunner;
import com.mathpar.web.executor.ExplicitPlot3dTimeoutRunner;
import com.mathpar.web.executor.ParametricPlot3dTimeoutRunner;
import com.mathpar.web.executor.api3d.RenderExecutor;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Plots {
    private static final Logger LOG = getLogger(Plots.class);

    @RequestMapping(value = "/api/plot3dimplicit", method = RequestMethod.POST)
    public ResponseEntity<List<double[]>> getMeshForImplicit3dPlot(
            @RequestBody MathparRequest mpReq, @PageParam Page page) {
        final String task = mpReq.getTask();
        if (!task.contains(F.FUNC_NAMES[F.IMPLICIT_PLOT3D])) {
            LOG.warn("Input task '{}' doesn't contain function {}.",
                    task, F.FUNC_NAMES[F.IMPLICIT_PLOT3D]);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final List<double[]> geometry = new ImplicitPlot3dTimeoutRunner()
                .run(page, task, mpReq.getSectionId());
        if (geometry == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(geometry, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/plot3dexplicit", method = RequestMethod.POST)
    public ResponseEntity<List<double[]>> getMeshForExplicit3dPlot(
            @RequestBody MathparRequest mpReq, @PageParam Page page) {
        final String task = mpReq.getTask();
        if (!task.contains(F.FUNC_NAMES[F.EXPLICIT_PLOT3D])) {
            LOG.warn("Input task '{}' doesn't contain function {}.",
                    task, F.FUNC_NAMES[F.EXPLICIT_PLOT3D]);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final List<double[]> geometry = new ExplicitPlot3dTimeoutRunner()
                .run(page, task, mpReq.getSectionId());
        if (geometry == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(geometry, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/plot3dparametric", method = RequestMethod.POST)
    public ResponseEntity<List<double[]>> getMeshForParametric3dPlot(
            @RequestBody MathparRequest mpReq, @PageParam Page page) {
        final String task = mpReq.getTask();
        if (!task.contains(F.FUNC_NAMES[F.PARAMETRIC_PLOT3D])) {
            LOG.warn("Input task '{}' doesn't contain function {}.",
                    task, F.FUNC_NAMES[F.PARAMETRIC_PLOT3D]);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final List<double[]> geometry = new ParametricPlot3dTimeoutRunner()
                .run(page, task, mpReq.getSectionId());
        if (geometry == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(geometry, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/render-surfaces", method = RequestMethod.POST)
    public ResponseEntity<List<double[]>> RenderingMultipleSurface (
            @RequestBody MathparRequest mpReq, @PageParam Page page
    ) {
        String task = mpReq.getTask();
        
        if (!task.contains(F.FUNC_NAMES[F.SHOW_3D])) {
            LOG.warn("Input task '{}' doesn't contain function {}.",
                    task, F.FUNC_NAMES[F.SHOW_3D]);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        RenderExecutor Render = new RenderExecutor();
        final List<double[]> geometry = Render.run(page, task, mpReq.getSectionId());
        
        if (geometry == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<>(geometry, HttpStatus.OK);
    }
}
