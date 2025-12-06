package com.mathpar.web.db.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GradebookListRow {
    public long studentId;
    public String studentEmail;
    public String studentName;
    public String groupName;

    public static class Mapper implements RowMapper<GradebookListRow> {
        @Override
        public GradebookListRow mapRow(ResultSet rs, int i) throws SQLException {
            GradebookListRow b = new GradebookListRow();
            b.studentId = rs.getLong("id_student");
            b.studentEmail = rs.getString("email");
            b.studentName = rs.getString("username");
            b.groupName = rs.getString("group_name");
            return b;
        }
    }

    public long getStudentId() {
        return studentId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getGroupName() {
        return groupName;
    }
}
