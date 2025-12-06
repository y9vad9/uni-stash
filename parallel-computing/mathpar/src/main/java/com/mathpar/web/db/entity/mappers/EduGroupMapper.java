package com.mathpar.web.db.entity.mappers;

import com.mathpar.web.db.entity.EduGroup;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EduGroupMapper implements RowMapper<EduGroup> {
    @Override
    public EduGroup mapRow(ResultSet rs, int i) throws SQLException {
        EduGroup g = new EduGroup();
        g.id = rs.getLong("id");
        g.groupName = rs.getString("group_name");
        return null;
    }
}
