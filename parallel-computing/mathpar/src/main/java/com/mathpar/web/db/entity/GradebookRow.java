package com.mathpar.web.db.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GradebookRow {
    public long id;
    public String taskTitle;
    public long subtaskNumber;
    public CheckResult checkResult;
    public Date checkTime;

    public static class Mapper implements RowMapper<GradebookRow> {
        @Override
        public GradebookRow mapRow(ResultSet rs, int i) throws SQLException {
            GradebookRow b = new GradebookRow();
            b.id = rs.getLong("id");
            b.taskTitle = rs.getString("task_title");
            b.subtaskNumber = rs.getLong("subtask_number");
            b.checkResult = CheckResult.valueOf(rs.getString("check_result"));
            b.checkTime = new Date(rs.getTimestamp("check_time").getTime());
            return b;
        }
    }

    public long getId() {
        return id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public long getSubtaskNumber() {
        return subtaskNumber;
    }

    public CheckResult getCheckResult() {
        return checkResult;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    @Override
    public String toString() {
        return "GradebookRow{" +
                "id=" + id +
                ", taskTitle='" + taskTitle + '\'' +
                ", subtaskNumber=" + subtaskNumber +
                ", checkResult=" + checkResult +
                ", checkTime=" + checkTime +
                '}';
    }
}
