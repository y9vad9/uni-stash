package com.mathpar.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "index", urlPatterns = {"/servlet/index"})
public class Index extends MathparHttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        final MathparUtils mu = new MathparUtils(req, resp);
        resp.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
        resp.setHeader("Pragma", "no-cache"); //HTTP 1.0
        resp.setDateHeader("Expires", -1);

        String cookieFirstVisit = mu.getCookieValue("Mathpar.firstVisit");
        if (cookieFirstVisit != null && cookieFirstVisit.equals("false")) {
            resp.sendRedirect("en/");
        } else {
            resp.sendRedirect("en/welcome.html");
        }
    }
}
