package com.mathpar.web.servlets;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

/**
 * Gets and sets matrix for 3D cube.
 */
@WebServlet(name = "matrix3d", urlPatterns = {"/servlet/matrix3d"})
public final class Matrix3d extends MathparHttpServlet {
    private static final Logger LOG = getLogger(Matrix3d.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final MathparUtils mu = new MathparUtils(req, resp);
        final Page page = mu.page();
        resp.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
        resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
        resp.setDateHeader("Expires", -1);

        int sect = Integer.parseInt(req.getParameter("section_number"));

        try {
            page.currentSectionNum(sect);
            double[][] matrix3d = page.matrix3d();
            double[][] cube = page.xyzCube();
            String res = twoDimArrayToString(matrix3d) + "*"
                    + twoDimArrayToString(cube);
            resp.getOutputStream().print(res);
        } catch (IOException e) {
            LOG.error("Error getting matrix for 3D plot.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        final MathparUtils mu = new MathparUtils(req, resp);
        final Page page = mu.page();
        int sect = Integer.parseInt(req.getParameter("section_number"));
        String matrix = req.getParameter("matrix");

        String n[];
        n = matrix.split(",");
        double[][] matrixD = new double[4][4];
        int p = 0;
        for (int ii = 0; ii < 4; ii++) {
            for (int jj = 0; jj < 4; jj++) {
                matrixD[ii][jj] = Double.parseDouble(n[p]);
                p++;
            }
        }
        page.currentSectionNum(sect);
        page.matrix3d(matrixD);
    }

    private static String twoDimArrayToString(double[][] arr) {
        StringBuilder tmp = new StringBuilder("[");
        for (int i = 0, len = arr.length; i < len; i++) {
            tmp.append(Arrays.toString(arr[i])).append(", ");
        }
        tmp.delete(tmp.lastIndexOf(", "), tmp.length()).append("]");
        return tmp.toString();
    }
}
