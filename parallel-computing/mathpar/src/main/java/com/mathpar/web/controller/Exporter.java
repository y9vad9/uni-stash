package com.mathpar.web.controller;

import com.mathpar.func.Page;
import com.mathpar.web.HtmlGenerator;
import com.mathpar.web.entity.ExportResponse;
import com.mathpar.web.entity.IMathparResponse;
import com.mathpar.web.entity.MathparResponse;
import com.mathpar.web.exceptions.MathparException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mathpar.func.Page.CHARSET_DEFAULT;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author ivan
 */
@Controller
@RequestMapping(value = "/api/export")
public class Exporter {
    private static final Logger LOG = getLogger(Exporter.class);
    private static final FileFormat DEFAULT_FORMAT = FileFormat.PDF;
    /**
     * Freemarker configuration.
     */
    private static final Configuration tplCfg = new Configuration(HtmlGenerator.V);

    static {
        tplCfg.setClassForTemplateLoading(Page.class, "/templates");
    }

    public enum FileFormat {
        PDF("application/pdf", ".pdf"),
        TEX("application/x-tex", ".tex"),
        TXT("text/plain", ".txt");

        private final String contentType;
        private final String extension;

        private FileFormat(String contentType, String extension) {
            this.contentType = contentType;
            this.extension = extension;
        }

        public String getContentType() {
            return contentType;
        }

        public String getExtension() {
            return extension;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity downloadFile(
            @RequestParam(value = "format", required = false) String formatStr,
            @RequestParam("filename") String filename,
            HttpServletResponse resp,
            @PageParam Page page) throws IOException {
        FileFormat format;
        if (formatStr == null) {
            format = DEFAULT_FORMAT;
        } else {
            format = FileFormat.valueOf(formatStr.trim().toUpperCase());
        }

        File outFile = new File(page.getUserDir(), filename);
        resp.setContentType(format.getContentType());
        String contentDisposition = String.format("attachment; filename=\"mathpar%s\"",
                format.getExtension());
        resp.setHeader("Content-Disposition", contentDisposition);
        resp.setContentLength((int) outFile.length());
        IOUtils.copy(new FileInputStream(outFile), resp.getOutputStream());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public IMathparResponse createExportingFile(
            @RequestParam("format") String formatStr,
            @RequestParam("pdf_page_width") String pageWstr,
            @RequestParam("pdf_page_height") String pageHstr,
            HttpServletRequest req,
            HttpServletResponse resp,
            @PageParam Page page
    ) throws IOException {
        FileFormat format;
        if (formatStr == null) {
            format = DEFAULT_FORMAT;
        } else {
            format = FileFormat.valueOf(formatStr.trim().toUpperCase());
        }
        double pageW, pageH;

        switch (format) {
            case PDF:
                try {
                    if (pageWstr == null || pageHstr == null) {
                        pageW = 0.0;
                        pageH = 0.0;
                    } else {
                        pageW = Double.parseDouble(pageWstr);
                        pageH = Double.parseDouble(pageHstr);
                    }
                    if (pageW <= 0.0 || pageH <= 0.0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    return MathparResponse.error("PDF page dimensions must be positive numbers", nfe);
                }
                try {
                    String pdfFilename = saveAsPdf(page, pageW, pageH, req.getParameterValues("latex"));
                    return new ExportResponse(pdfFilename);
                } catch (IOException ioe) {
                    return new ExportResponse("pdf_export.pdf", "Failed to compile PDF.");
                }
            case TXT:
                String[] tasks = req.getParameterValues("task");
                String[] answers = req.getParameterValues("answer");
                String txtFilename = saveAsTxt(page, tasks, answers);
                return new ExportResponse(txtFilename);
            default:
                return MathparResponse.error("Wrong export format: " + formatStr);
        }
    }

    /**
     * Compiles PDF file with pdflatex. Uses standard temporary file
     * method + HTTP session ID and section number to identify user sections.
     *
     * @param page          page
     * @param sectionsLatex sections to export
     * @param width         paper width (in cm).
     * @param height        paper height (in cm).
     * @return absolute path for compiled PDF file.
     * @throws IOException if something bad happened during file operations.
     */
    private String saveAsPdf(Page page, double width, double height,
                             String... sectionsLatex) throws IOException {
        File outFile = new File(page.getUserDir(), "pdf_export.tex");
        String outFileName = outFile.getPath();
        Template latexTpl = tplCfg.getTemplate("pdf_export.ftl");
        Map<String, Object> tplData = new HashMap<>();
        tplData.put("sectionsLatex", sectionsLatex);
        tplData.put("pageWidth", width);
        tplData.put("pageHeight", height);
        try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(outFile), CHARSET_DEFAULT)) {
            latexTpl.process(tplData, fileWriter);
        } catch (TemplateException ex) {
            throw new MathparException("Error processing PDF template.", ex);
        }
        ProcessBuilder pb = new ProcessBuilder(
                "pdflatex",
                "-interaction",
                "nonstopmode",
                "-halt-on-error",
                outFileName);
        pb.directory(outFile.getParentFile());
        Process p = pb.start();
        StringWriter latexOutWriter = new StringWriter();
        char[] buf = new char[4096];
        BufferedReader bre = new BufferedReader(
                new InputStreamReader(p.getErrorStream(), CHARSET_DEFAULT));
        int charsRead;
        latexOutWriter.write("LaTeX error output:");
        while ((charsRead = bre.read(buf)) != -1) {
            latexOutWriter.write(buf, 0, charsRead);
        }
        bre = new BufferedReader(new InputStreamReader(p.getInputStream(), CHARSET_DEFAULT));
        latexOutWriter.write("\n====================\nLaTeX standard output:");
        while ((charsRead = bre.read(buf)) != -1) {
            latexOutWriter.write(buf, 0, charsRead);
        }
        latexOutWriter.close();
        bre.close();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException("Waiting for latex to finish was interrupted", ex);
        }
        if (p.exitValue() != 0) {
            LOG.error("Failed to compile PDF from LaTeX file (Session "
                    + page.getSessionId() + ")\n" + latexOutWriter.toString());
            throw new IOException("Failed to compile PDF from LaTeX file (Session "
                    + page.getSessionId() + ")\n" + latexOutWriter.toString());
        }
        return outFile.getName().replace(".tex", FileFormat.PDF.getExtension());
    }

    private String saveAsTxt(Page page, String[] tasks, String[] answers) throws IOException {
        Template txtTpl = tplCfg.getTemplate("txt_export.ftl");
        Map<String, Object> tplData = new HashMap<>();
        List<Map<String, String>> sections = new ArrayList<>();
        for (int i = 0; i < answers.length; i++) {
            Map<String, String> section = new HashMap<>();
            section.put("task", tasks[i]);
            section.put("answer", answers[i]);
            sections.add(section);
        }
        tplData.put("sections", sections);
        File outFile = new File(page.getUserDir(), "txt_export.txt");
        try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(outFile), CHARSET_DEFAULT)) {
            txtTpl.process(tplData, fileWriter);
        } catch (TemplateException ex) {
            throw new MathparException("Error at txt export", ex);
        }
        return outFile.getName().replace(".txt", FileFormat.TXT.getExtension());
    }
}
