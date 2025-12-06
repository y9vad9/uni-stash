package com.mathpar.web.servlets;

import com.mathpar.func.Page;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Encapsulates common Mathpar variables such as page of current user.
 *
 * @author ivan
 */
public abstract class MathparHttpServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding(Page.CHARSET_DEFAULT.name());
        resp.setCharacterEncoding(Page.CHARSET_DEFAULT.name());
        resp.setContentType("application/json");

        super.service(req, resp);
    }
}
