package com.mathpar.web.servlets;

import com.mathpar.func.Page;
import com.mathpar.number.Ring;
import com.mathpar.web.executor.MathparResult;
import com.mathpar.web.executor.MathparRunner;
import com.mathpar.web.executor.MathparTimeoutRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MathparUtils {
    private static final String LOCALE_DEFAULT = "en";
    /**
     * Session-specific Page object.
     */
    private Page page;
    /**
     * Maps cookies names to Cookies objects.
     */
    private final Map<String, Cookie> cookiesMap = new HashMap<>();
    /**
     * Locale from query string.
     */
    private final String locale;
    //private final ResourceBundle msg;
    private final HttpServletRequest servletReq;
    private final HttpServletResponse servletResp;

    public MathparUtils(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        servletReq = req;
        servletResp = resp;
        // Get page from session attribute or store new Page (in case of new session).
        final HttpSession session = req.getSession();
        page = null;
        if (session != null) {
            page = (Page) session.getAttribute("page");
            if (page == null) {
                page = new Page(Ring.ringR64xyzt, true);
                page.ring.page=page;
                page.setSessionId(session.getId());
                session.setAttribute("page", page);
            }
        }

        // Fill cookies map.
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (int i = 0, cookiesLen = cookies.length; i < cookiesLen; i++) {
                Cookie c = cookies[i];
                cookiesMap.put(c.getName(), c);
            }
        }

        // Fill locale from query string.
        String localeParam = req.getParameter("locale");
        locale = localeParam != null ? localeParam : LOCALE_DEFAULT;
        //msg = ResourceBundle.getBundle("Messages", new Locale(locale));
    }

    public Cookie getCookie(String cookieName) {
        return cookiesMap.get(cookieName);
    }

    public String getCookieValue(String cookieName) {
        String value = null;
        Cookie cookie = cookiesMap.get(cookieName);
        if (cookie != null) {
            value = cookie.getValue();
        }
        return value;
    }

    /**
     * Returns entity corresponding to given Class and current input.
     *
     * @param <T>
     * @param classOfT
     * @return request from JSON or null in case of error.
     * @throws IOException
     */
//    public <T extends Object> T getJsonReq(Class<T> classOfT) throws IOException {
//        String input = IOUtils.toString(inStream);
//        if (input == null || input.isEmpty()) {
//            error("Empty body");
//            return null;
//        }
//        T res = null;
//        try {
//            res = gson.fromJson(input, classOfT);
//        } catch (JsonSyntaxException e) {
//            error("Incorrect JSON syntax in request", e);
//        }
//        return res;
//    }

//    public void writeResponse(Object resp) {
//        outWriter.println(gson(resp));
//    }
//
//    public void ok() {
//        outWriter.println(gson(MathparResponse.ok()));
//    }
//
//    public void ok(String result) {
//        outWriter.println(gson(MathparResponse.ok(result)));
//    }
//
//    public void ok(String result, String latex) {
//        outWriter.println(gson(MathparResponse.ok(result, latex)));
//    }
//
//    public void error() {
//        outWriter.println(gson(MathparResponse.error()));
//    }
//
//    public void error(String errorMsg) {
//        outWriter.println(gson(MathparResponse.error(errorMsg)));
//    }
//
//    public void error(String errorMsg, Throwable throwable) {
//        outWriter.println(gson(MathparResponse.error(errorMsg, throwable)));
//    }
//
//    public void warning(String warningMsg) {
//        outWriter.println(gson(MathparResponse.warning(warningMsg)));
//    }

    public MathparResult execution(String task, int sectionId) throws Exception {
        return new MathparRunner().run(page, task, sectionId);
    }

    public Page page() {
        return page;
    }

    public HttpServletResponse resp() {
        return servletResp;
    }

//    public OutputStream outStream() {
//        return outStream;
//    }

//    public PrintWriter outWriter() {
//        return outWriter;
//    }

    //public String getLocalString(String str) {
//        return msg.getString(str);
//    }

    public String getParameter(String key) {
        return servletReq.getParameter(key);
    }

    public String[] getParameterValues(String key) {
        return servletReq.getParameterValues(key);
    }

//    public boolean tryDownloadFile() {
//        if (servletReq.getParameter("download") == null) {
//            return false;
//        }
//        String filename;
//        if ((filename = servletReq.getParameter("filename")) == null) {
//            return false;
//        }
//        servletResp.setContentType("application/force-download");
//        servletResp.setHeader("Content-Transfer-Encoding", "binary");
//        servletResp.setHeader("Content-Disposition",
//                String.format("attachment; filename=\"%s\"", filename));
//        File file = new File(page.getUserUploadDir(), filename);
//        servletResp.setContentLength((int) FileUtils.sizeOf(file));
//        try {
//            FileUtils.copyFile(file, outStream);
//        } catch (IOException ex) {
//            error("Can't download file: " + filename, ex);
//            return true;
//        }
//        return true;
//    }

//    public boolean deleteFile(String filename) {
//        return FileUtils.deleteQuietly(new File(page.getUserUploadDir(), filename));
//    }
}
