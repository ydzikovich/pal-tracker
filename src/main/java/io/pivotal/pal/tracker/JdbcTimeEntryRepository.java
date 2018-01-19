package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;


public class JdbcTimeEntryRepository implements TimeEntryRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String createStatement = "INSERT INTO time_entries ( `date`, hours, project_id, user_id) VALUES ( ?,?,?,? )";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(createStatement,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(timeEntry.getDate()));
            ps.setInt(2, timeEntry.getHours());
            ps.setLong(3, timeEntry.getProjectId());
            ps.setLong(4, timeEntry.getUserId());
            return ps;
        }, generatedKeyHolder);

        return this.find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long id) {
        try {
            return this.jdbcTemplate.queryForObject("SELECT * FROM time_entries WHERE id = ?", mapper, id);
        } catch (EmptyResultDataAccessException e ) {
            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT * FROM time_entries", mapper);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String updateStatement = "UPDATE time_entries SET `date` = ?, hours = ?, project_id = ?, user_id = ? WHERE id = ?";
        jdbcTemplate.update(updateStatement,  Date.valueOf(timeEntry.getDate()), timeEntry.getHours(), timeEntry.getProjectId(), timeEntry.getUserId(), id);

        return find(id);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id=?", id);
    }

    private RowMapper<TimeEntry> mapper = (rs, rowNum) -> {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(rs.getLong("id"));
        timeEntry.setProjectId(rs.getLong("project_id"));
        timeEntry.setUserId(rs.getLong("user_id"));
        timeEntry.setDate(rs.getDate("date").toLocalDate());
        timeEntry.setHours(rs.getInt("hours"));
        return timeEntry;
    };

}
