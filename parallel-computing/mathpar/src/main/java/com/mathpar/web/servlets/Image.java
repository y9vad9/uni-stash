package com.mathpar.web.servlets;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;

@WebServlet(name = "image", urlPatterns = {"/servlet/image"})
public class Image extends HttpServlet {
    private static final Logger LOG = getLogger(Image.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        boolean download = Boolean.parseBoolean(req.getParameter("download"));
        int sect = Integer.parseInt(req.getParameter("section_number"));
        String getFramesNumber = req.getParameter("getFramesNumber");
        Page page = (Page) req.getSession(false).getAttribute("page");
        page.currentSectionNum(sect);

        int frame = 0;
        {
            String frameStr = req.getParameter("frame");
            if (frameStr != null) {
                frame = Integer.parseInt(frameStr);
            }
        }

        if (download) {
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition",
                    String.format("filename=\"mathpar_graphics_%d%s.png\"",
                            sect, page.getFramesNumber() > 1 ? "_frame" + frame : ""));
        } else {
            resp.setContentType("image/png");
        }

        OutputStream out;
        try {
            int framesNumber = page.getFramesNumber();
            if (getFramesNumber != null) {
                resp.setContentType("text/plain");
                PrintWriter writer = resp.getWriter();
                writer.println(framesNumber);
                return;
            }
            BufferedImage image = page.getImage(frame);
            if (image == null) {
                return;
            }
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "png", tmp);
            tmp.close();
            resp.setContentLength(tmp.size());
            out = resp.getOutputStream();
            out.write(tmp.toByteArray());
            out.flush();
        } catch (IOException e) {
            LOG.error("Error getting image (section = " + sect + ")", e);
        }
    }
}
