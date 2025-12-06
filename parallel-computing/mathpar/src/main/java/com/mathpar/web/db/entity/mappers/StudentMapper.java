package com.mathpar.web.db.entity.mappers;

import com.mathpar.web.db.entity.Student;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentMapper implements RowMapper<Student> {
    @Override
    public Student mapRow(ResultSet rs, int i) throws SQLException {
        Student s = new Student();
        s.id = rs.getLong("id");
        s.userId = rs.getLong("id_user");
        s.groupId = rs.getLong("id_group");
        return s;
    }
}
