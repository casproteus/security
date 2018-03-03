package com.stgo.security.monitor.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;
import javax.ws.rs.InternalServerErrorException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.stgo.security.monitor.SecurityLog;

public class DerbySecurityLogDao extends JdbcResourceDao<SecurityLog> {

    private static final String CREATE_LOG =
            "INSERT INTO SecurityLog (computer_name, label, description, created, status) VALUES (?,?,?,?,?)";

    private static final String REMOVE_STAT_BY_UUID = "DELETE FROM security WHERE uuid = ?";

    private static final String REMOVE_STAT_BY_LABEL = "DELETE FROM security WHERE group_uuid = ? AND label = ?";

    public static final String LABEL_DESCRIPTION_SEPERATER = "_stgo_";

    private static final String REMOVE_ALL_STATS_BY_GROUP = "DELETE FROM SecurityLog WHERE GROUP_NAME = ?";

    private static final String TABLE_NAME = "SecurityLog";

    private static final String ID_FIELD = "ID";
    private static final String GROUP_NAME_FIELD = "GROUP_NAME";
    private static final String COMPUTER_NAME_FIELD = "COMPUTER_NAME";
    private static final String LABEL_FIELD = "LABEL";
    private static final String DESCRIPTION_FIELD = "DESCRIPTION";
    private static final String CREATED_FIELD = "CREATED";

    public DerbySecurityLogDao(final DataSource dataSource) {
        super(dataSource);
    }

    /**
     * @see com.opentext.rest.dao.JdbcResourceDao#getSqlTableName()
     */
    protected String getSqlTableName() {
        return DerbySecurityLogDao.TABLE_NAME;
    }

    public static void writeLog(
            final String computerName,
            final String label,
            final String msg,
            final Date time,
            final int status) {
        try {
            jdbcTemplate.update(CREATE_LOG, computerName, label, msg, time, status);
        } catch (final Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * Delete all security from the given group.
     * 
     * @param ancestorId
     *            [0] the group uuid
     */
    public void deleteAll(
            final UUID... ancestorId) {
        checkExactVarArgs(1, ancestorId);

        final UUID group = ancestorId[0];

        updateSql(DerbySecurityLogDao.REMOVE_ALL_STATS_BY_GROUP, group.toString());
    }

    /**
     * Delete the security with the given uuid
     * 
     * @param id
     *            [0] the security uuid
     */
    public void deleteById(
            final UUID id) {
        checkExactVarArgs(1, id);

        final UUID uuid = id;

        updateSql(DerbySecurityLogDao.REMOVE_STAT_BY_UUID, uuid.toString());
    }

    /**
     * Delete the security with the given label in the specified group.
     * 
     * @param args
     *            [0] the security group [1] the security label
     */
    public void deleteByLabel(
            final String[] args) {
        checkExactVarArgs(2, args);

        final String group = args[0];
        final String label = args[1];

        updateSql(DerbySecurityLogDao.REMOVE_STAT_BY_LABEL, group, label);
    }

    /* Helper methods */
    protected void checkExactVarArgs(
            final int count,
            final UUID... args) {
        _checkExactVarArgs(count, (Object[]) args);
    }

    protected void checkExactVarArgs(
            final int count,
            final String... args) {
        _checkExactVarArgs(count, (Object[]) args);
    }

    protected void _checkExactVarArgs(
            final int count,
            final Object... args) {
        if (args == null || args.length != count) {
            throw new IllegalArgumentException(String.format("Expecting exactly %d argument. Got %s", count, args));
        }
    }

    public List<SecurityLog> queryForList(
            final String sql,
            final Object[] sqlArgs,
            final RowMapper<SecurityLog> rowMapper) {

        List<SecurityLog> list = null;

        try {
            list = jdbcTemplate.query(sql, sqlArgs, rowMapper);
        } catch (final DataAccessException ex) {
            throw new InternalServerErrorException(ex);
        }

        return list;
    }

    protected RowMapper<SecurityLog> getRowMapper() {
        return new SecurityRowMapper();
    }

    /* Spring JDBC inner-classes */
    public static class SecurityRowMapper implements RowMapper<SecurityLog> {

        @Override
        public SecurityLog mapRow(
                final ResultSet rs,
                final int row) throws SQLException {

            final SecurityLog securityLog = new SecurityLog();

            securityLog.setId(rs.getInt(DerbySecurityLogDao.ID_FIELD));
            securityLog.setGroup_name(rs.getString(DerbySecurityLogDao.GROUP_NAME_FIELD));
            securityLog.setComputer_name(rs.getString(DerbySecurityLogDao.COMPUTER_NAME_FIELD));
            securityLog.setLabel(rs.getString(DerbySecurityLogDao.LABEL_FIELD));
            securityLog.setDescription(rs.getString(DerbySecurityLogDao.DESCRIPTION_FIELD));
            securityLog.setCreated(new Date(rs.getTimestamp(DerbySecurityLogDao.CREATED_FIELD).getTime()));

            return securityLog;
        }
    }

}
