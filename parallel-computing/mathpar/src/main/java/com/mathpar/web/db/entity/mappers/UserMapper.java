package com.mathpar.web.db.entity.mappers;

import com.mathpar.web.db.entity.User;
import com.mathpar.web.db.entity.UserRole;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    public static final UserRole[] USER_ROLES = UserRole.values();

    @Override
    public User mapRow(ResultSet rs, int i) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setSalt(rs.getString("salt"));
        u.setRegistrationDate(rs.getDate("registration_date"));
        u.setRole(USER_ROLES[rs.getInt("user_role_id")]);
        return u;
    }
}
