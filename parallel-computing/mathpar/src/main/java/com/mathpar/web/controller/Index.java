package com.mathpar.web.controller;

import org.springframework.stereotype.Controller;
import com.mathpar.web.servlets.MathparUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class Index {
    //@RequestMapping(value = "/(ru|en)?", method = RequestMethod.GET)
    public void index(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
