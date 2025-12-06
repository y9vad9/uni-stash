package com.mathpar.web.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskInEduPlan {
    public long id;
    public String taskName;

    public TaskInEduPlan() {
    }

    public TaskInEduPlan(long id, String taskName) {
        this.id = id;
        this.taskName = taskName;
    }

    public static class Mapper implements RowMapper<TaskInEduPlan> {
        @Override
        public TaskInEduPlan mapRow(ResultSet rs, int i) throws SQLException {
            return new TaskInEduPlan(rs.getLong("id"), rs.getString("task_title"));
        }
    }
}
