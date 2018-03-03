package com.stgo.security.monitor.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.stgo.security.monitor.OptionStatus;
import com.stgo.security.monitor.util.NetUtil;

public class DerbyOptionStatusDao extends JdbcResourceDao<OptionStatus> {

    private static final String REMOVE_ALL_STATS_BY_GROUP = "DELETE FROM option WHERE GROUP_NAME = ?";

    private static final String REMOVE_STAT_BY_UUID = "DELETE FROM security WHERE uuid = ?";

    private static final String REMOVE_STAT_BY_LABEL = "DELETE FROM security WHERE group_uuid = ? AND label = ?";

    private static final String FIND_SECURITY_IDS_IN_GROUP = "SELECT uuid FROM security WHERE group_uuid = ?";

    private static final String UPDATE_OPTION_STATUS = "UPDATE OptionStatus set stat_value = ? where function_id = ?";

    private static final String CREATE_OPTION_STATUS =
            "INSERT INTO OptionStatus (group_name, computer_name, function_id, stat_value) VALUES (?,?,?,?)";

    private static final String QUERY_OPTION_STATUS = "SELECT * FROM  OptionStatus WHERE function_id = ?";

    private static final String TABLE_NAME = "OptionStatus";

    private static final String ID_FIELD = "ID";
    private static final String GROUP_NAME_FIELD = "GROUP_NAME";
    private static final String COMPUTER_NAME_FIELD = "COMPUTER_NAME";
    private static final String FUNCTION_ID_FIELD = "FUNCTION_ID";
    private static final String STAT_VALUE_FIELD = "STAT_VALUE";

    private static Integer FUNC_VERSION = 0;

    public DerbyOptionStatusDao(final DataSource dataSource) {
        super(dataSource);
    }

    /**
     * @see com.opentext.rest.dao.JdbcResourceDao#getSqlTableName()
     */
    protected String getSqlTableName() {
        return DerbyOptionStatusDao.TABLE_NAME;
    }

    public static void setVersion(
            String time) {
        try {
            if ("-1".equals(getVersion())) {
                jdbcTemplate.update(CREATE_OPTION_STATUS, "", "", FUNC_VERSION, time);
            } else {
                jdbcTemplate.update(UPDATE_OPTION_STATUS, time, FUNC_VERSION);
            }

        } catch (final Exception ex) {
            System.out.println(ex);
        }
    }

    public static String getVersion() {
        Object[] args = { FUNC_VERSION };
        List<OptionStatus> status = queryForList(QUERY_OPTION_STATUS, args);
        if (status != null && status.size() > 0) {
            return status.get(0).getStat_value();
        }
        return "-1";
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

        updateSql(DerbyOptionStatusDao.REMOVE_ALL_STATS_BY_GROUP, group.toString());
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

        updateSql(DerbyOptionStatusDao.REMOVE_STAT_BY_UUID, uuid.toString());
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

        updateSql(DerbyOptionStatusDao.REMOVE_STAT_BY_LABEL, group, label);
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

    public static List<OptionStatus> queryForList(
            final String sql,
            final Object[] sqlArgs) {

        List<OptionStatus> list = null;

        try {
            list = jdbcTemplate.query(sql, sqlArgs, getRowMapper());
        } catch (final DataAccessException ex) {
            NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "non-exccuted sql:" + sql + " WITH ARGS: " + sqlArgs);
        }

        return list;
    }

    protected static RowMapper<OptionStatus> getRowMapper() {
        return new SecurityRowMapper();
    }

    /* Spring JDBC inner-classes */
    public static class SecurityRowMapper implements RowMapper<OptionStatus> {

        @Override
        public OptionStatus mapRow(
                final ResultSet rs,
                final int row) throws SQLException {

            final OptionStatus option = new OptionStatus();

            option.setId(rs.getLong(DerbyOptionStatusDao.ID_FIELD));
            option.setGroup_name(rs.getString(DerbyOptionStatusDao.GROUP_NAME_FIELD));
            option.setComputer_name(rs.getString(DerbyOptionStatusDao.COMPUTER_NAME_FIELD));
            option.setFunction_id(rs.getInt(DerbyOptionStatusDao.FUNCTION_ID_FIELD));
            option.setStat_value(rs.getString(DerbyOptionStatusDao.STAT_VALUE_FIELD));

            return option;
        }
    }

}
