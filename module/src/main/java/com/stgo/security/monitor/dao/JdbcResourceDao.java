package com.stgo.security.monitor.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.springframework.core.io.AbstractResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcResourceDao<T extends AbstractResource> {

    protected static JdbcTemplate jdbcTemplate;
    protected final BidiMap fieldMapper = new TreeBidiMap();

    public JdbcResourceDao(final DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Executes an update command and returns the number of rows affected. Any exception thrown is wrapped with the
     * runtime InternalServerErrorException.
     */
    protected int updateSql(
            final String sql,
            final Object... args) {
        try {
            return jdbcTemplate.update(sql, args);
        } catch (final DataAccessException ex) {
            throw new InternalServerErrorException(ex);
        }
    }

    protected boolean canSortByField(
            final String fieldName) {
        return false;
    }

    protected String addWhereClause(
            final String sql,
            final List<Object> args,
            final ResourceListQuery<T> listQuery) {

        final StringBuilder sb = new StringBuilder(sql);
        sb.append(" where");
        int conditionCount = 0;

        // First, add parent condition, if a parent was specified.
        if (listQuery.getParentId() != null) {
            sb.append(' ');
            sb.append(getSqlParentTableName() + "." + "uuid");
            sb.append(" = ?");
            args.add(listQuery.getParentId().toString());
            conditionCount++;
        }

        // Next, add any additional search conditions.
        // TODO: Handle SearchConditions.

        // Were they NO conditions? If so, just return the original SQL.
        if (conditionCount == 0) {
            return sql;
        }

        return sb.toString();
    }

    /**
     * Returns the name of the resource's parent SQL table.
     * 
     * @return the SQL table name of the resource's parent
     */
    protected String getSqlParentTableName() {
        return null;
    }

    /**
     * Converts a {@link ResourceListQuery} to an SQL statement (with '?' placeholders) and arguments.
     * 
     * This method in effect is the bridge from {@link ResourceListQuery} to {@link JdbcTemplate}.
     * 
     * @param listQuery
     *            resource list query
     * @param baseSql
     *            base SQL query (to select all resources)
     * @return SQL and arguments (as an {@link SqlAndArgs} object)
     */
    protected SqlAndArgs convertToSql(
            final ResourceListQuery<T> listQuery,
            final String baseSql) {

        String sql = baseSql;
        final List<Object> args = new ArrayList<Object>();

        // Add where clause to the SQL, if a parent and/or search conditions
        // have been specified.
        if (listQuery.getParentId() != null || listQuery.searchConditions().hasNext()) {
            sql = addWhereClause(sql, args, listQuery);
        }

        return new SqlAndArgs(sql, args);
    }

    //
    // Inner Classes
    //
    public static class SqlAndArgs {

        private final String sql;

        private final List<Object> args;

        public SqlAndArgs(final String sql, final List<Object> args) {
            this.sql = sql;
            this.args = (args != null) ? Collections.unmodifiableList(args) : Collections.emptyList();
        }

        public String getSql() {
            return sql;
        }

        public List<Object> getArgs() {
            return args;
        }
    }
}
