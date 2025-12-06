package com.mathpar.web.controller;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;
import com.mathpar.web.entity.IMathparResponse;
import com.mathpar.web.entity.MathparResponse;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/files")
public class UploadedFilesController {
    private static final Logger LOG = getLogger(UploadedFilesController.class);

    @RequestMapping(method = RequestMethod.GET)
    public IMathparResponse getFileListing(@PageParam Page page) {
        return MathparResponse.ok().filenames(page.getUploadedFilesListingStr());
    }

    @RequestMapping(method = RequestMethod.GET, params = {"filename"})
    public IMathparResponse downloadFile(
            @RequestParam(value = "filename", required = true) String filename,
            @PageParam Page page,
            HttpServletResponse resp) throws UnsupportedEncodingException {
        resp.setContentType("application/force-download");
        resp.setHeader("Content-Transfer-Encoding", "binary");
        resp.setHeader("Content-Disposition",
                String.format("attachment; filename*=UTF-8''%s",
                        URLEncoder.encode(filename, "UTF-8")));
        File file = new File(page.getUserUploadDir(), filename);
        resp.setContentLength((int) FileUtils.sizeOf(file));
        try {
            FileUtils.copyFile(file, resp.getOutputStream());
        } catch (IOException ex) {
            LOG.error("Error downloading filename " + filename
                    + " for user " + page.getSessionId(), ex);
        }
        return MathparResponse.ok().filenames(page.getUploadedFilesListingStr());
    }

    @RequestMapping(method = RequestMethod.POST)
    public     IMathparResponse handleFileUpload(
            @RequestParam(value = "import_txt", required = false) String importTxt,
            @RequestParam("file") MultipartFile file,
            @PageParam Page page) throws IOException {
        String filename = file.getOriginalFilename();
        if (importTxt != null) {
            return MathparResponse.ok(IOUtils.toString(file.getInputStream(), Page.CHARSET_DEFAULT));
        }
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(
                        new File(page.getUserUploadDir(), filename)))) {
                    stream.write(bytes);
                }
                return MathparResponse.ok().filenames(page.getUploadedFilesListingStr());
            } catch (Exception e) {
                return MathparResponse.error("Faile to upload file " + filename, e);
            }
        } else {
            return MathparResponse.error("Can't upload - file is empty: " + filename);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, params = {"filename"})
    public  IMathparResponse deleteFile(
            @PageParam Page page,
            @RequestParam(value = "filename", required = true) String filename) {
        if (FileUtils.deleteQuietly(new File(page.getUserUploadDir(), filename))) {
            return MathparResponse.ok().filenames(page.getUploadedFilesListingStr());
        } else {
            return MathparResponse.error("Can't delete file: " + filename);
        }
    }
}
