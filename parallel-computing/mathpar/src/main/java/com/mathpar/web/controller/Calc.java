package com.mathpar.web.controller;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.func.Page;
import com.mathpar.web.db.dao.DbGradebooks;
import com.mathpar.web.db.dao.DbTasks;
import com.mathpar.web.db.dao.DbUser;
import com.mathpar.web.db.entity.CheckResult;
import com.mathpar.web.entity.*;
import com.mathpar.web.exceptions.MathparException;
import com.mathpar.web.executor.MathparResult;
import com.mathpar.web.executor.MathparTimeoutRunner;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class Calc {
    private static final Logger LOG = getLogger(Calc.class);
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final int MEGABYTES = 1024 * 1024;

    private DbGradebooks dbGradebooks;
    private DbTasks dbTasks;
    private DbUser dbUser;

    @Autowired
    public Calc(DbGradebooks dbGradebooks, DbTasks dbTasks, DbUser dbUser) {
        this.dbGradebooks = dbGradebooks;
        this.dbTasks = dbTasks;
        this.dbUser = dbUser;
    }

    @RequestMapping(value = "/api/check", method = RequestMethod.POST)
    public IMathparResponse check(@RequestBody MathparCheckRequest checkRequest,
                                  @PageParam Page page) throws IOException {
        List<String> dbSolutionSectionsAnswer = dbTasks.getSolutionsAnswerAsStringList(
                checkRequest.taskId, checkRequest.subtaskNumber);
        CheckResult checkResult = page.check(checkRequest.getUserAnswer(), dbSolutionSectionsAnswer);
        long idStudent = dbUser.getStudentIdByUserId(page.getUserId());
        dbGradebooks.insertRecord(idStudent, checkRequest.taskId, checkRequest.subtaskNumber, checkResult);
        return MathparResponse.ok(checkResult.getDescription(), checkResult.getDescription());
    }

    @RequestMapping(value = "/api/giveup", method = RequestMethod.POST)
    public MathparNotebook giveup(@RequestBody MathparCheckRequest checkRequest,
                                  @PageParam Page page) throws IOException {
        long idStudent = dbUser.getStudentIdByUserId(page.getUserId());
        dbGradebooks.insertRecord(idStudent, checkRequest.taskId, checkRequest.subtaskNumber, CheckResult.GAVE_UP);
        return dbTasks.getSolutions(checkRequest.taskId, checkRequest.subtaskNumber);
    }

    @RequestMapping(value = "/api/calc", method = RequestMethod.POST)
    public MathparResponse calc(@RequestBody MathparRequest mpReq, @PageParam Page page) {
        String latex;
        String result;
        String task = mpReq.getTask().trim();
        String ringException;
        try {
            LOG.info("Executing task (section #{}):\n{}", mpReq.getSectionId(), task);
            MathparResult mpres = new MathparTimeoutRunner().run(page, task, mpReq.getSectionId());
            result = mpres.result;
            latex = mpres.latex;
            ringException = page.ring().exception.toString();
            if (!ringException.isEmpty()) {
                LOG.error("Ring.exception is not empty after execution: {}", ringException);
                return MathparResponse.error(ringException).sectionId(mpReq.getSectionId());
            }
            return MathparResponse.ok(result, latex).sectionId(mpReq.getSectionId()).task(mpReq.getTask());
        } catch (MathparException mpe) {
            LOG.error("Mathpar exception.", mpe);
            return MathparResponse.error(mpe.getLocalizedMessage(), mpe).sectionId(mpReq.getSectionId());
        } catch (Exception ex) {
            ringException = page.ring().exception.toString();
            LOG.error("Exception executing task:\n{}", task);
            LOG.error("Unexpected exception.", ex);
            if (!ringException.isEmpty()) {
                LOG.error("Ring.exception is not empty after execution: {}", ringException);
                return MathparResponse.error(ringException).sectionId(mpReq.getSectionId());
            }
            return MathparResponse.error("Unexpected error: " + ex.getLocalizedMessage(), ex)
                    .sectionId(mpReq.getSectionId());
        }
    }

    @RequestMapping(value = "/api/space-memory", method = RequestMethod.POST)
    public SpaceMemoryResponse getSpaceAndMemory(@PageParam Page page) {
        return new SpaceMemoryResponse(page.ring().toString(), memoryInfo());
    }

    /**
     * @return string with memory info: free and total in megabytes.
     */
    private static String memoryInfo() {
        long maxMemory = RUNTIME.maxMemory();
        long freeMem = (maxMemory - RUNTIME.totalMemory() + RUNTIME.freeMemory())
                / MEGABYTES;
        return freeMem + " / " + (maxMemory / MEGABYTES) + "MB";
    }
}
