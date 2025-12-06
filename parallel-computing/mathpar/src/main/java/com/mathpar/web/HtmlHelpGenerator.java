package com.mathpar.web;

import com.mathpar.func.Page;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mathpar.web.HtmlGenerator.LOCALES;
import static com.mathpar.web.HtmlGenerator.V;

public class HtmlHelpGenerator {
    static final Logger LOG = Logger.getLogger("HtmlHelpGenerator");
    static final FilenameFilter fn = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith("tex");
        }
    };

    static final Configuration CFG = new Configuration(V);
    static final String IN_DIR_DEFAULT = "src/main/resources/user-guide/";
    static final String OUT_DIR_DEFAULT = "src/main/webapp/";
    static final String PATH_PREFIX = "../../";
    static int exampleNum = 0;
    static String[][] list;
    static int[] currsec;
    static int index_list = 0;

    static {
        CFG.setClassForTemplateLoading(HtmlHelpGenerator.class, "/templates");
    }

    public static void main(String[] args) throws IOException {
        LOG.info("Start building HTML");
        int totalNumber = 0;
        for (Locale currLocale : LOCALES) {
            ResourceBundle msg = ResourceBundle.getBundle("MessagesHtml", currLocale);
            LOG.log(Level.INFO, "LANGUAGE: {0}", currLocale);
            String lang = currLocale.getLanguage();
            String outputDirPath = OUT_DIR_DEFAULT + lang + File.separator + "help/";
            File outputDir = new File(outputDirPath);
            if (!outputDir.exists()) {
                LOG.log(Level.WARNING, "No output dir: {0}", outputDirPath);
                outputDir.mkdirs();
                LOG.log(Level.INFO, "Made output dir: {0}", outputDirPath);
            }
            index_list = 0;
            File dir = new File(IN_DIR_DEFAULT + lang + File.separator);
            if (!dir.exists()) {
                LOG.log(Level.SEVERE, "Input dir doesn't exist: {0}", dir);
            }
            String files[] = dir.list(fn);
            Arrays.sort(files);
            list = new String[files.length][100];
            currsec = new int[files.length];
            for (int ii = 0; ii < files.length; ii++) {
                String texFullFilename = IN_DIR_DEFAULT + lang + File.separator + files[ii];
                String htmlFilename = files[ii].replace("tex", "html");
                InputStreamReader isr =
                        new InputStreamReader(new FileInputStream(texFullFilename), Page.CHARSET_DEFAULT);
                BufferedReader in = new BufferedReader(isr);
                String str;
                String fileString = "";
                while ((str = in.readLine()) != null) {
                    fileString = fileString + "\n" + str;
                }
                in.close();

                int bd = fileString.indexOf("%begindelete");
                while (bd > 0) {
                    int ed = fileString.indexOf("%enddelete", bd);
                    fileString = cut(fileString, bd, ed + 10);
                    bd = fileString.indexOf("%begindelete");
                }

                //порядок методов важен
                fileString = fileString.replaceAll("&", "&amp;");
                fileString = fileString.replaceAll("<", "&lt;");
                fileString = fileString.replaceAll(">", "&gt;");
                fileString = fileString.replaceAll("\n\n", "</p><p>");

                fileString = fileString.replaceAll(
                        "\\\\underline[\u007b][А-Яа-я]+[.]+[\\s]+[\u007d]", "");
                fileString = fileString.replaceAll(
                        "\\\\underline[\u007b][А-Яа-я]+[.]+[\u007d]", "");
                fileString = fileString.replaceAll(
                        "\\\\underline[\u007b][A-Za-z]+[.]+[\\s]+[\u007d]", "");
                fileString = fileString.replaceAll(
                        "\\\\underline[\u007b][A-Za-z]+[.]+[\u007d]", "");

                fileString = fileString.replaceAll("<p><br></p>", "");
                fileString = doChapter(fileString);
                fileString = fileString.replaceAll("(?s)%code\\s*\\\\begin\\{verbatim\\}(.*?)\\\\end\\{verbatim\\}",
                        "<pre>$1</pre>");
                fileString = doExample(msg, fileString);
                fileString = doSection(fileString);
                fileString = doSubsection(fileString);
                fileString = dobf(fileString);
                fileString = doit(fileString);

                fileString = clear(fileString);
                fileString = doComm(fileString);
                fileString = doComm2(fileString);

                Map<String, Object> data = new HashMap<>();
                data.put("_", new ResourceBundleModel(msg, new BeansWrapperBuilder(V).build()));
                data.put("curr_page_name", htmlFilename);
                data.put("path_prefix", PATH_PREFIX);
                data.put("help_content", fileString);

                fileString = renderTemplate(CFG, "help/help", data);

                File outputFile = new File(outputDirPath + htmlFilename);
                FileOutputStream fileoutstream = new FileOutputStream(outputFile);
                totalNumber++;
                LOG.log(Level.INFO, "Done: {0}{1}", new Object[]{outputDirPath, htmlFilename});
                Writer writer = new OutputStreamWriter(fileoutstream, Page.CHARSET_DEFAULT);
                writer.write(fileString);
                writer.close();
            }

            List<Map<String, Object>> tableOfContents = new ArrayList<>();
            for (int i = 0; i < list.length; i++) {
                Map<String, Object> currTopic = new HashMap<>();
                String currFileName = files[i].replace("tex", "html");
                currTopic.put("filename", currFileName);
                currTopic.put("title", list[i][0]);
                List<Map<String, Object>> subtopics = new ArrayList<>();
                for (int k = 0; k < currsec[i]; k++) {
                    Map<String, Object> currSubtopic = new HashMap<>();
                    currSubtopic.put("anchor", k);
                    currSubtopic.put("title", list[i][k + 2]);
                    subtopics.add(currSubtopic);
                }
                currTopic.put("subtopics", subtopics);
                tableOfContents.add(currTopic);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("_", new ResourceBundleModel(msg, new BeansWrapperBuilder(V).build()));
            data.put("path_prefix", PATH_PREFIX);
            data.put("help_toc", tableOfContents);
            String articlesList = renderTemplate(CFG, "help/help_toc", data);

            FileOutputStream fileoutstream = new FileOutputStream(outputDirPath + "index.html");
            Writer writer = new OutputStreamWriter(fileoutstream, Page.CHARSET_DEFAULT);
            writer.write(articlesList);
            writer.close();
        }
        LOG.log(Level.INFO, "End of building html files. Total number = {0}", totalNumber);
    }

    public static String cut(String str, int startI, int endI) {
        String begin = str.substring(0, startI);
        String end = str.substring(endI, str.length());
        return begin + end;
    }

    //порядок важен
    public static String clear(String str) {
        str = str.replaceAll("\\\\[a-z]space\\*[\u007b][\\S]*[\\d]+mm[\u007d]", "");
        str = str.replaceAll("\\\\smallskip", "");
        str = str.replaceAll("\\\\bigskip", "");
        str = str.replaceAll("\\\\ ", " ");

        str = str.replaceAll("\\\\[\u007b]", "\u007b");
        str = str.replaceAll("\\\\[\u007d]", "\u007d");
        str = str.replaceAll("\\\\_", "_");

        str = str.replaceAll("~?-?--", " &mdash; ");
        str = str.replaceAll("<<", "&laquo;");
        str = str.replaceAll(">>", "&raquo;");
        str = str.replaceAll("\\\\\\\\", " ");
        str = str.replaceAll("\\\\ldots", "&hellip;");

        return str;
    }

    public static String doChapter(String str) {
        boolean flag = true;
        while (flag) {
            String bv = "\\chapter\u007b";
            String ev = "\u007d";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                String end = str.substring(contentEND + 1);
                String title = str.substring(contentSTART + 9, contentEND);
                content = "<h1>" + title + "</h1><p>";
                list[index_list][0] = title;
                index_list++;
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String doSection(String str) {
        boolean flag = true;
        int k = 0;
        while (flag) {
            String bv = "\\section\u007b";
            String ev = "\u007d";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                k++;
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                String end = str.substring(contentEND + 1);
                String sec_title = str.substring(contentSTART + 9, contentEND);
                list[index_list - 1][k + 1] = sec_title;
                currsec[index_list - 1] = k;
                content = MessageFormat.format("</p><h2 id=\"{0}\">{1}.{2} {3}</h2><p>",
                        k - 1, index_list, k, sec_title);
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String doSubsection(String str) {
        boolean flag = true;
        while (flag) {
            String bv = "\\subsection\u007b";
            String ev = "\u007d";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                String end = str.substring(contentEND + 1);
                content = "</p><h3>" + str.substring(contentSTART + 12, contentEND) + "</h3><p>";
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String dobf(String str) {
        boolean flag = true;
        while (flag) {
            String bv = "\u007b\\bf";
            String ev = "\u007d";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                String end = str.substring(contentEND + 1);
                content = "<b>" + str.substring(contentSTART + 4, contentEND) + "</b>";
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String doit(String str) {
        boolean flag = true;
        while (flag) {
            String bv = "\u007b\\it";
            String ev = "\u007d";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                String end = str.substring(contentEND + 1);
                content = "<i>" + str.substring(contentSTART + 4, contentEND) + "</i>";
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String doExample(ResourceBundle msg, String str) {
        exampleNum = 0;
        boolean haveVerbatim = true;
        String bv = "\\begin\u007bverbatim\u007d";
        String ev = "\\end\u007bverbatim\u007d";
        while (haveVerbatim) {
            String content;
            haveVerbatim = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                String end = str.substring(contentEND + ev.length());
                String currExample = str.substring(contentSTART + bv.length(), contentEND);
                int rowsCnt = currExample.split("\n").length;
                Map<String, Object> data = new HashMap<>();
                data.put("_", new ResourceBundleModel(msg, new BeansWrapperBuilder(V).build()));
                data.put("sectionNum", exampleNum);
                data.put("rowsCnt", rowsCnt);
                data.put("exampleString", currExample);
                content = renderTemplate(CFG, "help/example", data);

                exampleNum++;
                haveVerbatim = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String doComm(String str) {
        boolean flag = true;
        while (flag) {
            String bv = "\\comm\u007b";
            String ev = "\u007d";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                contentEND = str.indexOf(ev, contentEND + 1);
                String end = str.substring(contentEND + 1);
                content = str.substring(contentSTART + 6, contentEND)
                        .replaceAll("[\u007d][\u007b]", "");
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String doComm2(String str) {
        boolean flag = true;
        while (flag) {
            String bv = "\\comm \u007b";
            String ev = "\u007d";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                contentEND = str.indexOf(ev, contentEND + 1);
                String end = str.substring(contentEND + 1);
                content = str.substring(contentSTART + 6, contentEND)
                        .replaceAll("[\u007d]\\s*[\u007b]", "");
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String doComments(String str) {
        boolean flag = true;
        while (flag) {
            String bv = "\\%";
            String ev = "\n";
            String content;
            flag = false;
            int contentSTART = str.indexOf(bv);
            int contentEND;
            if (contentSTART > 0) {
                String begin = str.substring(0, contentSTART);
                contentEND = str.indexOf(ev, contentSTART);
                String end = str.substring(contentEND + 1);
                content = "";
                flag = true;
                str = begin + content + end;
            }
        }
        return str;
    }

    public static String renderTemplate(Configuration cfg, String tplName,
                                        Object data) {
        StringWriter writer = new StringWriter();
        try {
            Template template = cfg.getTemplate(tplName + ".ftl");
            template.process(data, writer);
        } catch (IOException | TemplateException ex) {
            ex.printStackTrace(System.err);
        }
        return writer.toString();
    }
}
