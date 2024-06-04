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
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class SqlUtils {

    //
    // CRUD OPERATIONS
    //
    static GroovyRowResult get(DataSource dataSource, String table, Map keys) {
        Sql sql = new Sql(dataSource)
        String query = generateSelect(false, table, keys)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query)
        sql.close()
        return results[0]
    }

    static List<GroovyRowResult> list(DataSource dataSource, String table, Map filters = [:], Map params = [:]) {
        return list(dataSource, table, filters, [], params)
    }

    static List<GroovyRowResult> list(DataSource dataSource, String table, Map filters, List fieldsInLike, Map params = [:]) {
        Sql sql = new Sql(dataSource)
        String query = generateSelect(false, table, filters, fieldsInLike, params)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query, params.offset as Integer ?: 0, params.max as Integer ?: 0)
        sql.close()
        return results
    }

    static Integer count(DataSource dataSource, String table, Map filters = [:]) {
        Sql sql = new Sql(dataSource)
        String query = generateSelect(true, table, filters)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query)
        sql.close()
        return results[0][0] as Integer
    }

    static List<List<Object>> create(DataSource dataSource, String table, Map values) {
        Sql sql = new Sql(dataSource)
        String query = generateInsert(table, values)
        log.trace(query)
        List results = sql.executeInsert(query)
        sql.close()
        return results
    }

    static Integer update(DataSource dataSource, String table, Map keys, Map values) {
        Sql sql = new Sql(dataSource)
        String query = generateUpdate(table, keys, values)
        log.trace(query)
        sql.executeUpdate(query)
        sql.close()
        return sql.updateCount
    }

    static Integer delete(DataSource dataSource, String table, Map keys) {
        Sql sql = new Sql(dataSource)
        String query = generateDelete(table, keys)
        log.trace(query)
        sql.execute(query)
        sql.close()
        return sql.updateCount
    }

    static List<GroovyRowResult> select(DataSource dataSource, String query, Map params = [:]) {
        Sql sql = new Sql(dataSource)
        log.trace(query)
        List<GroovyRowResult> results = sql.rows(query, params.offset as Integer ?: 0, params.max as Integer ?: 0)
        sql.close()
        return results
    }

    static Boolean execute(DataSource dataSource, String query) {
        Sql sql = new Sql(dataSource)
        log.trace(query)
        String result = sql.execute(query)
        sql.close()
        return result
    }

    static List<List<Object>> executeInsert(DataSource dataSource, String query) {
        Sql sql = new Sql(dataSource)
        log.trace(query)
        List<List<Object>> result = sql.executeInsert(query)
        sql.close()
        return result
    }

    private static String generateWhere(Map<String, Object> filters, List<String> fieldsInLike = []) {
        String query = ''
        if (filters) {
            query += " WHERE "
            query += filters.collect { field, value ->
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

    private static String generateSelect(Boolean count, String table, Map filters, List fieldsInLike = [], Map params = [:]) {
        String query
        query = count ? "SELECT COUNT(*) " : "SELECT * "
        query += "FROM $table"
        query += generateWhere(filters, fieldsInLike)
        if (params.sort) {
            query += " ORDER BY ${params.sort} ${params.order ? params.order.toString().toUpperCase() : 'ASC'}"
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
            return "'${value}'"
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
