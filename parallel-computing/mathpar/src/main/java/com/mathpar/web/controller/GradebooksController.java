package com.mathpar.web.controller;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;
import com.mathpar.web.db.dao.DbGradebooks;
import com.mathpar.web.db.dao.DbUser;
import com.mathpar.web.db.entity.GradebookListRow;
import com.mathpar.web.db.entity.GradebookRow;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/view/gradebook")
public class GradebooksController {
    private static final Logger LOG = getLogger(GradebooksController.class);

    private DbGradebooks dbGradebooks;
    private DbUser dbUser;

    @Autowired
    public void setDbGradebooks(DbGradebooks dbGradebooks) {
        this.dbGradebooks = dbGradebooks;
    }

    @Autowired
    public void setDbUser(DbUser dbUser) {
        this.dbUser = dbUser;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getGradebook(@PageParam Page page) {
        long studentId = dbUser.getStudentIdByUserId(page.getUserId());
        String studentName = dbUser.getUsernameByUserId(page.getUserId());
        List<GradebookRow> gradebookRows = dbGradebooks.getGradebook(studentId);

        LOG.debug("For student {} with ID = {} got {} gradebook records",
                studentName, studentId, gradebookRows.size());

        Map<String, Object> model = new HashMap<>();
        model.put("student_name", studentName);
        model.put("gradebookRows", gradebookRows);
        return new ModelAndView("gradebook", model);
    }

    // TODO: allow this only for teachers and admins.
    @RequestMapping(value = "/{studentId}", method = RequestMethod.GET)
    public ModelAndView getGradebook(@PathVariable("studentId") long studentId) {
        String studentName = dbUser.getUsernameByStudentId(studentId);
        List<GradebookRow> gradebookRows = dbGradebooks.getGradebook(studentId);

        Map<String, Object> model = new HashMap<>();
        model.put("student_name", studentName);
        model.put("gradebookRows", gradebookRows);
        return new ModelAndView("gradebook", model);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ModelAndView getGradebooksList() {
        List<GradebookListRow> gradebookListRows = dbGradebooks.getGradebookList();
        LOG.info("Got gradebook rows: {}", gradebookListRows);
        return new ModelAndView("gradebooks_list", "gradebooksListRows", gradebookListRows);
    }
}
