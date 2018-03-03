package com.stgo.security.monitor.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.jooq.SortField;
import org.springframework.core.io.AbstractResource;

/**
 * A class representing a query for a list of resources.
 */
public class ResourceListQuery<T extends AbstractResource> {
    //
    // Instance Variables
    //

    private UUID parentId;

    private final List<SortField> sortFields = new ArrayList<SortField>();

    private final List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();

    //
    // Constructors
    //

    private ResourceListQuery() {
        /* enforce use of Builder */
    }

    //
    // Methods
    //

    /**
     * Returns the parent id of the queried resources.
     * 
     * @return parent id (UUID) of the queried resources or <code>null</code> if none
     */
    public UUID getParentId() {

        return parentId;
    }

    /**
     * Returns an iterator over the fields by which the queried resources should be sorted.
     */
    public Iterator<SortField> sortFields() {

        return sortFields.iterator();
    }

    /**
     * Get an iterator over the search conditions.
     * 
     * @return Iterator over the search conditions.
     */
    public Iterator<SearchCondition> searchConditions() {
        return searchConditions.iterator();
    }

    public void setSearchConditions(
            SearchCondition[] conditions) {
        searchConditions.clear();
        searchConditions.addAll(Arrays.asList(conditions));
    }

    //
    // Inner Classes
    //

    public static class Builder<T extends AbstractResource> {

        private UUID parentId;

        private final List<SortField> sortFields = new ArrayList<SortField>();

        private final List<SearchCondition> searchConditions = new ArrayList<SearchCondition>();

        public static <R extends AbstractResource> Builder<R> newBuilder(
                final Class<R> resourceType) {

            return new Builder<R>();
        }

        /**
         * Communicates to the {@link Builder} the parent of the resources to be listed.
         * 
         * @param parentId
         *            the parent id of the queried resources
         * @return the builder itself (for chaining)
         */
        public Builder<T> withParent(
                final UUID parentId) {

            this.parentId = parentId;

            return this;
        }

        /**
         * Adds a list of conditions to the search.
         * 
         * @param conditions
         *            Conditions to be added.
         * @return The builder itself, for chaining.
         */
        public Builder<T> searchFor(
                final SearchCondition... conditions) {
            searchConditions.addAll(Arrays.asList(conditions));
            return this;
        }

        /**
         * Adds a list of conditions from a query string. Same as calling searchFor(query, defaultField, false).
         * 
         * @param query
         *            Query to parse and add conditions from.
         * @param defaultField
         *            Default field used for queries with no field while parsing query.
         * @return The builder itself, for chaining.
         */
        public Builder<T> searchFor(
                final String query,
                final String defaultField) {
            searchConditions.addAll(QueryParser.parseQuery(query, defaultField, false));
            return this;
        }

        /**
         * Adds a list of conditions from a query string.
         * 
         * @param query
         *            Query to parse and add conditions from.
         * @param defaultField
         *            Default field used for queries with no field while parsing query.
         * @param lowerCaseExpandedTerms
         *            Whether or not to put wildcard match conditions to lowercase while parsing query.
         * @return The builder itself, for chaining.
         */
        public Builder<T> searchFor(
                final String query,
                final String defaultField,
                final boolean lowerCaseExpandedTerms) {
            searchConditions.addAll(QueryParser.parseQuery(query, defaultField, lowerCaseExpandedTerms));
            return this;
        }

        /**
         * Builds the query.
         * 
         * @return the query, a {@link ResourceListQuery} instance
         */
        public ResourceListQuery<T> build() {

            final ResourceListQuery<T> query = new ResourceListQuery<T>();
            query.parentId = parentId;
            query.searchConditions.addAll(searchConditions);
            query.sortFields.addAll(sortFields);

            validate(query);

            return query;
        }

        /**
         * Validates the query built by the {@link Builder}.
         * 
         * @param query
         *            to be validated
         * @throws IllegalStateException
         *             if the query is in an invalid state
         */
        private void validate(
                final ResourceListQuery<T> query) {
            /* nothing to validate -- for future use */
        }
    }
}
