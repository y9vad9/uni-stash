package com.mathpar.web.db.dao;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.mathpar.web.db.entity.User;
import com.mathpar.web.db.entity.mappers.UserMapper;
import com.mathpar.web.db.util.SecurityUtil;
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
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class DbUser {
    private static final Logger LOG = getLogger(DbUser.class);

    private NamedParameterJdbcTemplate jdbcTpl;
    private UserMapper userMapper = new UserMapper();

    private static final String INSERT_USER = "" +
            "INSERT INTO mathpar_users (email, username, password, salt, registration_date, user_role_id) " +
            "VALUES (:email, :username, :password, :salt, :regDate, :userRoleId)";
    private static final String INSERT_USER_TO_STUDENTS = "" +
            "INSERT INTO students (id_user, id_group) " +
            "VALUES (:idUser, :idGroup) ;";
    private static final String GET_USER_BY_EMAIL = "" +
            "SELECT id, username, email, password, salt, registration_date, user_role_id " +
            "FROM mathpar_users WHERE email = :email";

    @Autowired
    public void setJdbcTpl(NamedParameterJdbcTemplate jdbcTpl) {
        this.jdbcTpl = jdbcTpl;
    }

    public long save(User user) {
        LOG.debug("Inserting new user: {}", user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTpl.update(INSERT_USER, new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("username", user.getUsername())
                .addValue("password", user.getPassword())
                .addValue("salt", user.getSalt())
                .addValue("regDate", user.getRegistrationDate())
                .addValue("userRoleId", user.getRole().ordinal()), keyHolder);
        return (Long) keyHolder.getKeys().get("SCOPE_IDENTITY()");
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public User getUser(String email, String password) throws IOException,
            NoSuchAlgorithmException {
        User user;
        try {
            user = jdbcTpl.queryForObject(GET_USER_BY_EMAIL,
                    new MapSqlParameterSource().addValue("email", email), userMapper);
        } catch (Exception e) {
            throw new AuthException("Invalid email or password.", e);
        }
        byte[] salt = SecurityUtil.base64ToByte(user.getSalt());
        if (!Arrays.equals(SecurityUtil.getHash(password, salt),
                SecurityUtil.base64ToByte(user.getPassword()))) {
            throw new AuthException("Invalid email or password.");
        } else {
            return user;
        }
    }

    public void insertIntoStudents(long userId, long idGroup) {
        jdbcTpl.update(INSERT_USER_TO_STUDENTS, new MapSqlParameterSource()
                .addValue("idUser", userId)
                .addValue("idGroup", idGroup));
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public long getStudentIdByUserId(long userId) {
        List<Long> tmp = jdbcTpl.queryForList(
                "SELECT id FROM students WHERE id_user = :userId",
                new MapSqlParameterSource("userId", userId), Long.class);
        if (tmp != null && tmp.size() == 1) {
            return tmp.get(0);
        } else {
            return -1;
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public String getUsernameByUserId(long userId) {
        return jdbcTpl.queryForObject(
                "SELECT username FROM mathpar_users WHERE id = :userId",
                new MapSqlParameterSource("userId", userId), String.class);
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public String getUsernameByStudentId(long studentId) {
        return jdbcTpl.queryForObject("" +
                        "SELECT username FROM mathpar_users u " +
                        "JOIN (SELECT id_user FROM students WHERE id = :studentId) s ON (s.id_user = u.id)",
                new MapSqlParameterSource("studentId", studentId), String.class);
    }
}
