package com.stgo.security.monitor.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import com.stgo.security.monitor.dao.SearchCondition.SearchOperator;

/**
 * Class used to parse string queries.
 */
public final class QueryParser {
    /**
     * Hide constructor.
     */
    private QueryParser() {
    }

    /**
     * This is a utility method for parsing a query into a list of conditions to use by DAOs, same as calling
     * parseQUery(query, defaultField, true).
     *
     * @param query
     *            Query to parse.
     * @param defaultField
     *            Default field to use when none is specified in the query.
     * @return List of conditions representing the query.
     *
     * @throws InvalidQueryException
     *             If the query was not well formed (field1:value1,field2:value2-1|value2-2)
     */
    public static List<SearchCondition> parseQuery(
            final String query,
            final String defaultField) {
        return QueryParser.parseQuery(query, defaultField, true);
    }

    /**
     * This is a utility method for parsing a query into a list of conditions to use by DAOs.
     *
     * @param query
     *            Query to parse.
     * @param defaultField
     *            Default field to use when none is specified in the query.
     * @param lowerCaseExpandedTerms
     *            Whether or not to lower case the terms part of wildcard queries.
     * @return List of conditions representing the query.
     *
     * @throws InvalidQueryException
     *             If the query was not well formed (field1:value1,field2:value2-1|value2-2)
     */
    public static List<SearchCondition> parseQuery(
            final String query,
            final String defaultField,
            final boolean lowerCaseExpandedTerms) {
        return parseQuery(query, defaultField, lowerCaseExpandedTerms, true);
    }

    public static List<SearchCondition> parseQuery(
            final String query,
            final String defaultField,
            final boolean lowerCaseExpandedTerms,
            final boolean allowLeadingWildcard) {
        final List<SearchCondition> conditions = new ArrayList<>();
        final Analyzer analyzer = new WhitespaceAnalyzer();
        final org.apache.lucene.queryParser.QueryParser luceneParser =
                new org.apache.lucene.queryParser.QueryParser(defaultField == null ? "label" : defaultField, analyzer);
        luceneParser.setAllowLeadingWildcard(allowLeadingWildcard);
        luceneParser.setLowercaseExpandedTerms(lowerCaseExpandedTerms);
        try {
            final Query luceneQuery = luceneParser.parse(query);
            conditions.addAll(QueryParser.extractConditionsFromQuery(luceneQuery, false));
        } catch (final ParseException e) {
            throw new InvalidQueryException(e);
        }
        return conditions;
    }

    /**
     * Extract the conditions from the specified lucene query.
     *
     * @param luceneQuery
     *            Parsed query.
     * @param prohibited
     *            Whether or not to negate the specified query.
     * @return List of conditions matching the query.
     */
    private static List<SearchCondition> extractConditionsFromQuery(
            final Query luceneQuery,
            final boolean prohibited) {
        final List<SearchCondition> conditions = new ArrayList<>();
        if (luceneQuery instanceof TermQuery) {
            conditions.addAll(QueryParser.extractConditionsFromTermQuery((TermQuery) luceneQuery, prohibited));
        } else if (luceneQuery instanceof BooleanQuery) {
            conditions.addAll(QueryParser.extractConditionsFromBooleanQuery((BooleanQuery) luceneQuery, prohibited));
        } else if (luceneQuery instanceof WildcardQuery) {
            conditions.addAll(QueryParser.extractConditionsFromWildcardQuery((WildcardQuery) luceneQuery, prohibited));
        } else if (luceneQuery instanceof PrefixQuery) {
            conditions.addAll(QueryParser.extractConditionsFromPrefixQuery((PrefixQuery) luceneQuery, prohibited));
        } else if (luceneQuery instanceof PhraseQuery) {
            conditions.addAll(QueryParser.extractConditionsFromPhraseQuery((PhraseQuery) luceneQuery, prohibited));
        } else if (luceneQuery instanceof ConstantScoreRangeQuery) {
            conditions.addAll(QueryParser.extractConditionsFromRangeQuery((ConstantScoreRangeQuery) luceneQuery,
                    prohibited));
        }
        return conditions;
    }

    /**
     * Extracts conditions from a lucene term query.
     *
     * @param luceneQuery
     *            Lucene query to process.
     * @param prohibited
     *            Whether or not to negate the resulting conditions.
     * @return List of conditions found.
     */
    private static Collection<? extends SearchCondition> extractConditionsFromTermQuery(
            final TermQuery luceneQuery,
            final boolean prohibited) {
        final List<SearchCondition> conditions = new ArrayList<SearchCondition>();
        final Term term = luceneQuery.getTerm();
        conditions.add(new SearchCondition(term.field(), prohibited, SearchOperator.EXACT_MATCH, term.text()));
        return conditions;
    }

    /**
     * Extracts conditions from a lucene boolean query.
     *
     * @param luceneQuery
     *            Lucene query to process.
     * @param prohibited
     *            Whether or not to negate the resulting conditions.
     * @return List of conditions found.
     */
    private static Collection<? extends SearchCondition> extractConditionsFromBooleanQuery(
            final BooleanQuery luceneQuery,
            final boolean prohibited) {
        final List<SearchCondition> conditions = new ArrayList<SearchCondition>();
        for (final Object clause : (luceneQuery).clauses()) {
            // XOR on the prohibited flags, so that it will be true if &
            // only if one is true, but false if both are.
            conditions.addAll(QueryParser.extractConditionsFromQuery(((BooleanClause) clause).getQuery(), prohibited
                    ^ ((BooleanClause) clause).isProhibited()));
        }
        return conditions;
    }

    /**
     * Extracts conditions from a lucene wildcard query.
     *
     * @param luceneQuery
     *            Lucene query to process.
     * @param prohibited
     *            Whether or not to negate the resulting conditions.
     * @return List of conditions found.
     */
    private static Collection<? extends SearchCondition> extractConditionsFromWildcardQuery(
            final WildcardQuery luceneQuery,
            final boolean prohibited) {
        final List<SearchCondition> conditions = new ArrayList<SearchCondition>();
        final Term term = (luceneQuery).getTerm();
        conditions.add(new SearchCondition(term.field(), prohibited, SearchOperator.WILDCARD_MATCH, term.text()));
        return conditions;
    }

    /**
     * Extracts conditions from a lucene prefix query.
     *
     * @param luceneQuery
     *            Lucene query to process.
     * @param prohibited
     *            Whether or not to negate the resulting conditions.
     * @return List of conditions found.
     */
    private static Collection<? extends SearchCondition> extractConditionsFromPrefixQuery(
            final PrefixQuery luceneQuery,
            final boolean prohibited) {
        final List<SearchCondition> conditions = new ArrayList<>();
        // Lucene will remove the trailing wildcard for prefix queries, need
        // to put it back when creating condition.
        final Term term = (luceneQuery).getPrefix();
        conditions.add(new SearchCondition(term.field(), prohibited, SearchOperator.WILDCARD_MATCH, term.text() + "*"));
        return conditions;
    }

    /**
     * Extracts conditions from a lucene phrase query.
     *
     * @param luceneQuery
     *            Lucene query to process.
     * @param prohibited
     *            Whether or not to negate the resulting conditions.
     * @return List of conditions found.
     */
    private static Collection<? extends SearchCondition> extractConditionsFromPhraseQuery(
            final PhraseQuery luceneQuery,
            final boolean prohibited) {
        final List<SearchCondition> conditions = new ArrayList<SearchCondition>();
        // Lucene splits the terms of a phrase query, need to put them back
        // together to create a single condition
        final Term[] terms = (luceneQuery).getTerms();
        final StringBuilder text = new StringBuilder();
        String field = null;
        for (final Term term : terms) {
            if (field == null) {
                field = term.field();
            }
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(term.text());
        }
        conditions.add(new SearchCondition(field, prohibited, SearchOperator.EXACT_MATCH, text.toString()));
        return conditions;
    }

    /**
     * Extracts conditions from a lucene range query.
     *
     * @param luceneQuery
     *            Lucene query to process.
     * @param prohibited
     *            Whether or not to negate the resulting conditions.
     * @return List of conditions found.
     */
    private static Collection<? extends SearchCondition> extractConditionsFromRangeQuery(
            final ConstantScoreRangeQuery luceneQuery,
            final boolean prohibited) {
        final List<SearchCondition> conditions = new ArrayList<SearchCondition>();
        final ConstantScoreRangeQuery range = luceneQuery;
        conditions.add(new SearchCondition(range.getField(), prohibited,
                (range.includesLower() ? SearchOperator.GREATER_THAN_OR_EQUAL : SearchOperator.GREATER_THAN), range
                        .getLowerVal()));
        conditions.add(new SearchCondition(range.getField(), prohibited,
                (range.includesUpper() ? SearchOperator.LESS_THAN_OR_EQUAL : SearchOperator.LESS_THAN), range
                        .getUpperVal()));
        return conditions;
    }
}
