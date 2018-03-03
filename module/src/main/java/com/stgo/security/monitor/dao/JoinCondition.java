package com.stgo.security.monitor.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.apache.commons.lang.ArrayUtils;
import org.jooq.JoinType;
import org.jooq.SortField;

/**
 * This class represents a join between 2 tables, used when DAOs need to query data from multiple tables in order to
 * gather required information.
 */
public class JoinCondition {
    private final String tableName;

    private final JoinType joinType;

    private final List<SearchCondition> conditions = new ArrayList<SearchCondition>();

    private final List<SearchCondition> extraConditions = new ArrayList<SearchCondition>();

    private final List<SortField> extraSortFields = new ArrayList<SortField>();

    private final List<String> extraFields = new ArrayList<String>();

    private final BidiMap fieldMapper = new TreeBidiMap();

    /**
     * Constructs a <code>JoinCondition</code> object with specific values.
     * 
     * @param tableName
     *            Name of the table on which to join.
     * @param joinType
     *            Type of join to use, default to inner join.
     * @param conditions
     *            Conditions on which to join.
     * @param extraConditions
     *            Conditions to add on select about fields in the joined table.
     * @param extraSortFields
     *            Sorting to add on fields from joined table.
     * @param extraFields
     *            Fields to be selected, from the joined table.
     */
    @SuppressWarnings("unchecked")
    public JoinCondition(final String tableName, final JoinType joinType, final BidiMap fieldMap,
            final SearchCondition[] conditions, final SearchCondition[] extraConditions,
            final SortField[] extraSortFields, final String... extraFields) {
        this.tableName = tableName;
        this.joinType = joinType;
        if (!ArrayUtils.isEmpty(conditions)) {
            this.conditions.addAll(Arrays.asList(conditions));
        }
        if (!ArrayUtils.isEmpty(extraConditions)) {
            this.extraConditions.addAll(Arrays.asList(extraConditions));
        }
        if (!ArrayUtils.isEmpty(extraSortFields)) {
            this.extraSortFields.addAll(Arrays.asList(extraSortFields));
        }
        if (!ArrayUtils.isEmpty(extraFields)) {
            this.extraFields.addAll(Arrays.asList(extraFields));
        }
        fieldMapper.putAll(fieldMap);
    }

    /**
     * Returns the <code>tableName</code> value.
     * 
     * @return The <code>tableName</code>.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Returns the <code>joinType</code> value.
     * 
     * @return The <code>joinType</code>.
     */
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * Returns the <code>conditions</code> value.
     * 
     * @return The <code>conditions</code>.
     */
    public Iterator<SearchCondition> getConditions() {
        return conditions.iterator();
    }

    /**
     * Returns the <code>extraConditions</code> value.
     * 
     * @return The <code>extraConditions</code>.
     */
    public Iterator<SearchCondition> getExtraConditions() {
        return extraConditions.iterator();
    }

    /**
     * Returns the <code>extraSortFields</code> value.
     * 
     * @return The <code>extraSortFields</code>.
     */
    public Iterator<SortField> getExtraSortFields() {
        return extraSortFields.iterator();
    }

    /**
     * Returns the <code>extraFields</code> value.
     * 
     * @return The <code>extraFields</code>.
     */
    public Iterator<String> getExtraFields() {
        return extraFields.iterator();
    }

    /**
     * Returns the <code>fieldMapper</code> value.
     * 
     * @return The <code>fieldMapper</code>.
     */
    protected BidiMap getFieldMapper() {
        return fieldMapper;
    }
}
