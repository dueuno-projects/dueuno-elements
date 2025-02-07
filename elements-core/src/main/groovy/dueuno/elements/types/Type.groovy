package dueuno.elements.types

import groovy.transform.CompileStatic

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@CompileStatic
enum Type {
    NA(null),
    BOOL(Boolean),
    NUMBER(Number),
    TEXT(String),
    MAP(Map),
    LIST(List),
    DATETIME(LocalDateTime),
    DATE(LocalDate),
    TIME(LocalTime)

    final Class clazz

    Type(Class clazz) {
        this.clazz = clazz
    }

    String toString() {
        return name()
    }
}