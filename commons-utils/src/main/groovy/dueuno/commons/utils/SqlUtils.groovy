/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dueuno.commons.utils

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.sql.DataSource

/**
 * Utility class for performing CRUD (Create, Read, Update, Delete) operations
 * on a relational database using a {@link DataSource}.
 * <p>
 * Provides methods for simple queries, inserts, updates, deletes, and counting records.
 * </p>
 *
 * <h3>Example usage:</h3>
 * <pre>
 * DataSource ds = ...
 * GroovyRowResult row = SqlUtils.get(ds, "person", [id: 1])
 * List<GroovyRowResult> people = SqlUtils.list(ds, "person", [name: "John"])
 * </pre>
 *
 * Author: Gianluca Sartori
 */
@Slf4j
@CompileStatic
class SqlUtils {

    /**
     * Retrieves a single row from a table using the specified key values.
     *
     * @param dataSource the data source to query
     * @param table the table name
     * @param keys a map of column names to values used as keys
     * @return a {@link GroovyRowResult} representing the first matching row
     */
    static GroovyRowResult get(DataSource dataSource, String table, Map keys) {
        Sql sql = new Sql(dataSource)
        String query = generateSelect(false, table, [], keys)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query)
        sql.close()
        return results[0]
    }

    /**
     * Retrieves a list of rows from a table using optional filter and fetch parameters.
     *
     * @param dataSource the data source to query
     * @param table the table name
     * @param filterParams optional map of column names to filter values
     * @param fetchParams optional map with pagination and sorting parameters
     * @return a list of {@link GroovyRowResult} objects
     */
    static List<GroovyRowResult> list(DataSource dataSource, String table, Map filterParams = [:], Map fetchParams = [:]) {
        return list(dataSource, table, [], filterParams, fetchParams)
    }

    /**
     * Retrieves a list of rows from a table using optional filters and LIKE fields.
     *
     * @param dataSource the data source to query
     * @param table the table name
     * @param fieldsInLike list of columns that should use LIKE matching
     * @param filterParams map of column names to filter values
     * @param fetchParams map of pagination/sorting parameters
     * @return a list of {@link GroovyRowResult} objects
     */
    static List<GroovyRowResult> list(DataSource dataSource, String table, List fieldsInLike, Map filterParams = [:], Map fetchParams = [:]) {
        Sql sql = new Sql(dataSource)
        String query = generateSelect(false, table, fieldsInLike, filterParams, fetchParams)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query, fetchParams.offset as Integer ?: 0, fetchParams.max as Integer ?: 0)
        sql.close()
        return results
    }

    /**
     * Counts the number of rows in a table that match the specified filters.
     *
     * @param dataSource the data source to query
     * @param table the table name
     * @param filterParams optional map of column names to filter values
     * @return the number of matching rows
     */
    static Integer count(DataSource dataSource, String table, Map filterParams = [:]) {
        Sql sql = new Sql(dataSource)
        String query = generateSelect(true, table, [], filterParams)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query)
        sql.close()
        return results[0][0] as Integer
    }

    /**
     * Inserts a new row into the specified table.
     *
     * @param dataSource the data source to query
     * @param table the table name
     * @param values a map of column names to values to insert
     * @return a list of generated keys
     */
    static List<List<Object>> create(DataSource dataSource, String table, Map values) {
        Sql sql = new Sql(dataSource)
        String query = generateInsert(table, values)
        log.trace(query)
        List results = sql.executeInsert(query)
        sql.close()
        return results
    }

    /**
     * Updates rows in the specified table matching the given keys.
     *
     * @param dataSource the data source to query
     * @param table the table name
     * @param keys map of column names to match rows
     * @param values map of column names to new values
     * @return the number of rows updated
     */
    static Integer update(DataSource dataSource, String table, Map keys, Map values) {
        Sql sql = new Sql(dataSource)
        String query = generateUpdate(table, keys, values)
        log.trace(query)
        sql.executeUpdate(query)
        sql.close()
        return sql.updateCount
    }

    /**
     * Deletes rows in the specified table matching the given keys.
     *
     * @param dataSource the data source to query
     * @param table the table name
     * @param keys map of column names to match rows
     * @return the number of rows deleted
     */
    static Integer delete(DataSource dataSource, String table, Map keys) {
        Sql sql = new Sql(dataSource)
        String query = generateDelete(table, keys)
        log.trace(query)
        sql.execute(query)
        sql.close()
        return sql.updateCount
    }

    /**
     * Executes a SELECT query with optional fetch parameters (pagination).
     *
     * @param dataSource the data source to query
     * @param query the SQL query string
     * @param fetchParams optional map with offset and max for pagination
     * @return a list of {@link GroovyRowResult} objects
     */
    static List<GroovyRowResult> select(DataSource dataSource, String query, Map fetchParams = [:]) {
        Sql sql = new Sql(dataSource)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query, fetchParams.offset as Integer ?: 0, fetchParams.max as Integer ?: 0)
        sql.close()
        return results
    }

    /**
     * Executes an arbitrary SQL statement.
     *
     * @param dataSource the data source to query
     * @param query the SQL statement
     * @return true if the statement executed successfully
     */
    static Boolean execute(DataSource dataSource, String query) {
        Sql sql = new Sql(dataSource)
        log.trace(query)
        Boolean result = sql.execute(query)
        sql.close()
        return result
    }

    /**
     * Executes a SELECT query and returns the resulting rows.
     *
     * @param dataSource the data source to query
     * @param query the SQL SELECT query
     * @return a list of {@link GroovyRowResult} objects
     */
    static List<GroovyRowResult> executeSelect(DataSource dataSource, String query) {
        Sql sql = new Sql(dataSource)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query)
        sql.close()
        return results
    }

    // --- Private helper methods ---

    private static String generateWhere(Map<String, Object> filterParams, List<String> fieldsInLike = []) {
        String query = ''
        if (filterParams) {
            query += " WHERE "
            query += filterParams.collect { field, value ->
                if (field in fieldsInLike) {
                    return "$field LIKE '%$value%'"

                } else if (value in List) {
                    String result = "(1 = 0 "
                    for (v in value) {
                        result += " OR (${field} = ${v})"
                    }
                    result += ')'
                    return result

                } else if (value == null) {
                    return "$field IS NULL"

                } else {
                    return "$field = '$value'"
                }
            }.join(" AND ")
        }
        return query
    }

    private static String generateSelect(Boolean count, String table, List fieldsInLike = [], Map filterParams = [:], Map fetchParams = [:]) {
        String query
        query = count ? "SELECT COUNT(*) " : "SELECT * "
        query += "FROM $table"
        query += generateWhere(filterParams, fieldsInLike)
        if (fetchParams.sort) {
            query += " ORDER BY "
            query += (fetchParams.sort as Map).collect { "${it.key} ${it.value}" }.join(', ')
        }
        return query
    }

    private static String generateInsert(String table, Map<String, Object> values) {
        String vars = values.keySet().join(', ')
        String vals = values.collect { field, Object value ->
            if (value == null) {
                return "null"
            }
            if (value in String) {
                value = (value as String).replace("'", "''")
            }
            return (value in Boolean) ? "${value}" : "'${value}'"
        }.join(", ")

        String query = "INSERT INTO $table ($vars) VALUES ($vals)"
        return query
    }

    private static String generateUpdate(String table, Map<String, Object> keys, Map<String, Object> values) {
        String query
        query = "UPDATE $table SET "
        query += values.collect { field, Object value ->
            if (value == null) {
                return "${field} = null"
            }
            if (value in Boolean) {
                return "${field} = ${value}"
            }
            if (value in String) {
                value = (value as String).replace("'", "''")
            }
            return "${field} = '${value}'"
        }.join(", ")

        query += generateWhere(keys)
        return query
    }

    private static String generateDelete(String table, Map keys) {
        String query
        query = "DELETE FROM $table "
        query += generateWhere(keys)
        return query
    }

}
