package com.mathpar.web.listeners;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

/**
 * Removes session specific temporary dir after session invalidation.
 */
@WebListener
public final class MathparSessionListener implements HttpSessionListener {
    private static final Logger LOG = getLogger(MathparSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        LOG.info("Session Created: {}", session.getId());
    }

    /**
     *
     * @param event
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        Page page = (Page) session.getAttribute("page");
        try {
            if (page != null && page.getUserDir() != null) {
                FileUtils.deleteDirectory(page.getUserDir());
                LOG.info("User directory deleted: {}", page.getUserDir());
            }
        } catch (Exception e) {
            LOG.error("Error deleting user directory", e);
        }
        LOG.info("Session {} destroyed.", session.getId());
    }
}
