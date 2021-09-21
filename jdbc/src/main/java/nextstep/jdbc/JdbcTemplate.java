package nextstep.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class JdbcTemplate {

    private final DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void update(String sql, Object ... parameters) {
        AbstractJdbcTemplate abstractJdbcTemplate = new AbstractJdbcTemplate() {
            @Override
            public Object mapRow(ResultSet rs) {
                return null;
            }

            @Override
            protected String createQuery() {
                return sql;
            }

            @Override
            protected DataSource getDataSource() {
                return dataSource;
            }

            @Override
            public void setValues(PreparedStatement pstmt) throws SQLException {
                for (int i=0;i<parameters.length;i++) {
                    pstmt.setObject(i+1, parameters[i]);
                }
                pstmt.executeUpdate();
            }
        };
        abstractJdbcTemplate.update();
    }

    public <T> T query(String sql, RowMapper<T> rowMapper, Object ... parameters) {
        AbstractJdbcTemplate abstractJdbcTemplate = new AbstractJdbcTemplate() {
            @Override
            public T mapRow(ResultSet rs) throws SQLException {
                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }
                throw new IllegalArgumentException();
            }

            @Override
            protected String createQuery() {
                return sql;
            }

            @Override
            protected DataSource getDataSource() {
                return dataSource;
            }

            @Override
            public void setValues(PreparedStatement pstmt) throws SQLException {
                for (int i=0;i<parameters.length;i++) {
                    pstmt.setObject(i+1, parameters[i]);
                }
            }
        };
        return abstractJdbcTemplate.query();
    }

    public <T> List<T> query (String sql, RowMapper<T> rowMapper) {
        AbstractJdbcTemplate abstractJdbcTemplate = new AbstractJdbcTemplate() {
            @Override
            protected String createQuery() {
                return sql;
            }

            @Override
            protected DataSource getDataSource() {
                return dataSource;
            }

            @Override
            public List<T> mapRow(ResultSet rs) throws SQLException {
                List<T> users = new ArrayList<>();
                while(rs.next()) {
                    users.add(rowMapper.mapRow(rs));
                }
                return users;
            }
        };
        return abstractJdbcTemplate.query();
    }
}
