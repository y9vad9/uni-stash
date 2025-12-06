package com.mathpar.web.db.dao;

import com.mathpar.web.db.entity.CheckResult;
import com.mathpar.web.db.entity.GradebookListRow;
import com.mathpar.web.db.entity.GradebookRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class DbGradebooks {
    private final GradebookRow.Mapper gradebookMapper = new GradebookRow.Mapper();
    private final GradebookListRow.Mapper gradebookListRowMapper = new GradebookListRow.Mapper();
    private NamedParameterJdbcTemplate jdbcTpl;

    @Autowired
    public void setJdbcTpl(NamedParameterJdbcTemplate jdbcTpl) {
        this.jdbcTpl = jdbcTpl;
    }



    public void insertRecord(long idStudent, long idTask, int subtaskNumber, CheckResult checkResult) {
        if (idStudent < 0 || idTask < 0 || subtaskNumber < 0) {
            throw new IllegalArgumentException("Can't insert Gradebook record.");
        }
        jdbcTpl.update("INSERT INTO gradebooks "
                + "(id_student, id_task, subtask_number, check_result, check_time) "
                + "VALUES "
                + "(:idStudent, :idTask, :subtaskNumber, :checkResult, :checkTime) ;",
                new MapSqlParameterSource()
                .addValue("idStudent", idStudent)
                .addValue("idTask", idTask)
                .addValue("subtaskNumber", subtaskNumber)
                .addValue("checkResult", checkResult.name())
                .addValue("checkTime", new Date()));
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<GradebookRow> getGradebook(long studentId) {
        return jdbcTpl.query(""
                + "SELECT gb.id, task_title, subtask_number, check_result, check_time FROM "
                + "(SELECT * FROM gradebooks WHERE id_student = :studentId ORDER BY check_time) gb "
                + "JOIN tasks t ON (t.id = gb.id_task) ",
                new MapSqlParameterSource()
                .addValue("studentId", studentId), gradebookMapper);
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<GradebookListRow> getGradebookList() {
        return jdbcTpl.query(
                "SELECT email, id_student, username, group_name FROM \n"
                + "(SELECT DISTINCT(id_student) FROM gradebooks) gb\n"
                + "JOIN students s ON (gb.id_student = s.id)\n"
                + "JOIN mathpar_users u ON (s.id_user = u.id)\n"
                + "JOIN edu_groups g ON (s.id_group = g.id)\n"
                + "ORDER BY group_name", new MapSqlParameterSource(), gradebookListRowMapper);
    }
}
