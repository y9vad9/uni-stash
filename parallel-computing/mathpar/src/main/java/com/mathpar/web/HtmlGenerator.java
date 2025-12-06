package com.mathpar.web;
import static org.apache.logging.log4j.LogManager.getLogger;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.logging.log4j.Logger;

/**
 * Generates static HTML files from templates and l10n resource bundles.
 *
 * @author ivan
 */
public class HtmlGenerator {
    public static final Version V = new Version(2, 3, 21);

    /**
     * Output dirs for each locale.
     */
    public static final String[] OUTPUT_PATHS = new String[]{
            "src/main/webapp/en",
            "src/main/webapp/ru"
    };
    public static final Locale[] LOCALES = new Locale[]{
            new Locale("en"),
            new Locale("ru")
    };
    private static final String[] TEMPLATES = new String[]{
            "index",
            "welcome",
            "contact"
    };
    private static final Logger LOG = getLogger(HtmlGenerator.class);

    /**
     * Render each template for each locale.
     *
     * @param args
     */
    public static void main(String[] args) {
        Configuration cfg = new Configuration(V);
        cfg.setClassForTemplateLoading(HtmlGenerator.class, "/templates");

        for (int i = 0; i < LOCALES.length; i++) {
            LOG.info("====== Using locale: " + LOCALES[i]);
            ResourceBundle msg = ResourceBundle.getBundle("MessagesHtml", LOCALES[i]);
            File outputDir = new File(OUTPUT_PATHS[i]);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            for (String tplName : TEMPLATES) {
                Map<String, Object> data = new HashMap<>();
                data.put("_", new ResourceBundleModel(msg, new BeansWrapperBuilder(V).build()));
                data.put("path_prefix", "../");
                renderTemplate(cfg, tplName, data, outputDir);
            }
        }
    }

    private static void renderTemplate(Configuration cfg, String tplName,
                                       Object data, File outputDir) {
        Writer fileWriter = null;
        try {
            Template template = cfg.getTemplate(tplName + ".ftl");
            fileWriter = new FileWriter(new File(outputDir, tplName + ".html"));
            LOG.info("Generating: " + tplName + ".html");
            template.process(data, fileWriter);
            fileWriter.flush();
        } catch (TemplateException | IOException e) {
            e.printStackTrace(System.err);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {
                    e2.printStackTrace(System.err);
                }
            }
        }
    }
}
