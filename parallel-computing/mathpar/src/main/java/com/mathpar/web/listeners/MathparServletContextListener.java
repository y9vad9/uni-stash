package com.mathpar.web.listeners;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

/**
 * Creates\removes application wide temporary dir.
 */
@WebListener
public final class MathparServletContextListener implements ServletContextListener {
    private static final Logger LOG = getLogger(MathparServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            if (!Page.MATHPAR_DIR.exists()) {
                Page.MATHPAR_DIR.mkdir();
                LOG.info("Global Mathpar tempdir created. {}", Page.MATHPAR_DIR);
            }
        } catch (Exception e) {
            LOG.error("Global Mathpar tempdir creation error.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            FileUtils.deleteDirectory(Page.MATHPAR_DIR);
            LOG.info("Global Mathpar tempdir deleted: {}", Page.MATHPAR_DIR);
        } catch (Exception e) {
            LOG.error("Global Mathpar tempdir delete error.", e);
        }
    }
}
