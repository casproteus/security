/*
 * This file is part of OTSP.
 * (C) 2011-2011 - Open Text Corporation
 * All rights reserved.
 */
package com.stgo.security.monitor.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a condition to apply for searching.
 */
public class SearchCondition {
    /**
     * This enum represents the supported search operators.
     */
    public enum SearchOperator {
        EXACT_MATCH, WILDCARD_MATCH, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, IN
    }

    /**
     * Name of the field to search on.
     */
    private final String fieldName;

    /**
     * Whether the condition is negated or not.
     */
    private final boolean negated;

    /**
     * Operator to use.
     */
    private final SearchOperator operator;

    /**
     * List of values to look for, using the operator, on the field.
     */
    private final List<Object> values = new ArrayList<>();

    /**
     * Constructs a <code>SearchCondition</code> object with specific values.
     *
     * @param fieldName
     *            Name of the field to search on.
     * @param negated
     *            Whether the condition is negated or not.
     * @param operator
     *            Operator to use.
     * @param values
     *            List of values to look for, using the operator, on the field.
     */
    public SearchCondition(final String fieldName, final boolean negated, final SearchOperator operator,
            final Object... values) {
        this.fieldName = fieldName;
        this.negated = negated;
        this.operator = operator;
        this.values.addAll(Arrays.asList(values));
    }

    /**
     * Returns the <code>fieldName</code> value.
     *
     * @return The <code>fieldName</code>.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the <code>negated</code> value.
     *
     * @return The <code>negated</code>.
     */
    public boolean isNegated() {
        return negated;
    }

    /**
     * Returns the <code>operator</code> value.
     *
     * @return The <code>operator</code>.
     */
    public SearchOperator getOperator() {
        return operator;
    }

    /**
     * Returns the <code>values</code> value.
     *
     * @return The <code>values</code>.
     */
    public Object[] getValues() {
        return values.toArray();
    }
}
