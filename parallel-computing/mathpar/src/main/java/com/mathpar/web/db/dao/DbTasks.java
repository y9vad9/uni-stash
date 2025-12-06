package com.mathpar.web.db.dao;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathpar.web.entity.MathparNotebook;
import com.mathpar.web.entity.MathparSection;
import com.mathpar.web.entity.TaskInEduPlan;
import com.mathpar.web.exceptions.AuthException;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class DbTasks {
    private static final Logger LOG = getLogger(DbTasks.class);

    private static final String GET_PLAN_BY_USER_ID = "" +
            "SELECT t.id, t.task_title " +
            "FROM tasks t " +
            "JOIN edu_plans p ON " +
            "  (p.id_group = (SELECT id_group FROM students WHERE id_user = :userId) " +
            "  AND t.id = p.id_task) " +
            "ORDER BY task_title ";
    private static final String GET_BY_ID = "SELECT task FROM tasks WHERE id = :taskId ";
    private static final String INSERT = "" +
            "MERGE INTO tasks (task, task_title) KEY(task_title) VALUES (:task, :taskName) ";
    private static final String INSERT_TO_EDUPLAN = "" +
            "INSERT INTO edu_plans (id_group, id_task) " +
            "VALUES (:idGroup, :idTask) ";

    private NamedParameterJdbcTemplate jdbcTpl;
    private ObjectMapper jacksonMapper = new ObjectMapper();

    @Autowired
    public void setJdbcTpl(NamedParameterJdbcTemplate jdbcTpl) {
        this.jdbcTpl = jdbcTpl;
    }

    /**
     * @param userId user ID
     * @return план пользователя с данным ID.
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<TaskInEduPlan> getPlanForUser(long userId) {
        if (userId < 0) {
            throw new AuthException("You must log in to load task.");
        }
        return jdbcTpl.query(
                GET_PLAN_BY_USER_ID,
                new MapSqlParameterSource().addValue("userId", userId),
                new TaskInEduPlan.Mapper());
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public MathparNotebook getAllSubtasks(long taskId) throws IOException {
        String taskStr = jdbcTpl.queryForObject(GET_BY_ID,
                new MapSqlParameterSource("taskId", taskId), String.class);
        return jacksonMapper.readValue(taskStr, MathparNotebook.class);
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public MathparNotebook getTasks(long taskId) throws IOException {
        List<MathparSection> sectionsWithTasks = new ArrayList<>();
        for (MathparSection s : getAllSubtasks(taskId).sections) {
            if ((s.getTask().contains("TASK"))||(s.getTask().contains("PAGE")))  {
                sectionsWithTasks.add(s);
            }
        }
        MathparNotebook res = new MathparNotebook();
        res.sections = sectionsWithTasks;
        return res;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public MathparNotebook getSolutions(long taskId, int subtaskNumber) throws IOException {
        MathparNotebook res = new MathparNotebook();
        List<MathparSection> allSections = getAllSubtasks(taskId).sections;
        List<MathparSection> resSect = new ArrayList<>();
        int i = -1;
        for (MathparSection s : allSections) {
            if (s.getTask().contains("TASK")) {
                i++;
            } else if (i == subtaskNumber) {
                resSect.add(s);
            }
        }
        res.sections = resSect;
        return res;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<String> getSolutionsAnswerAsStringList(long taskId, int subtaskNumber) throws IOException {
        List<MathparSection> solutionSections = getSolutions(taskId, subtaskNumber).sections;
        List<String> res = new ArrayList<>();
        for (MathparSection s : solutionSections) {
            res.add(s.getAnswer());
        }
        return res;
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<String> getSolutionsAsStringList(long taskId, int subtaskNumber) throws IOException {
        List<MathparSection> solutionSections = getSolutions(taskId, subtaskNumber).sections;
        List<String> res = new ArrayList<>();
        for (MathparSection s : solutionSections) {
            res.add(s.getTask());
        }
        return res;
    }

    public Long saveAsNewTask(MathparNotebook mathparNotebook, String taskName)
            throws JsonProcessingException {
        if (taskName == null || taskName.isEmpty()) {
            return null;
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String task = jacksonMapper.writeValueAsString(mathparNotebook);
        // Remove soft hyphens.
        task = task.replaceAll("\\u00AD", "");
        taskName = taskName.replaceAll("\\u00AD", "");
        jdbcTpl.update(INSERT, new MapSqlParameterSource("task", task)
                .addValue("taskName", taskName), keyHolder);
        LOG.debug("Keys: " + keyHolder.getKeys());
        if (keyHolder.getKeys() == null) {
            return null;
        }
        return (Long) keyHolder.getKeys().get("SCOPE_IDENTITY()");
    }

    public void insertTaskToEduPlan(long idGroup, long idTask) {
        jdbcTpl.update(INSERT_TO_EDUPLAN, new MapSqlParameterSource()
                .addValue("idGroup", idGroup)
                .addValue("idTask", idTask));
    }

    public void deleteById(long taskId) {
        jdbcTpl.update("DELETE FROM tasks WHERE id = :taskId", new MapSqlParameterSource()
                .addValue("taskId", taskId));
    }
}
