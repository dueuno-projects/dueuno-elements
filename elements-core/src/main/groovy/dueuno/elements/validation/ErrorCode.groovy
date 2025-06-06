package dueuno.elements.validation

import groovy.transform.CompileStatic

@CompileStatic
class ErrorCode {

    static final String IS_BLANK = 'blank'
    static final String IS_NOT_UNIQUE = 'unique'
    static final String IS_NOT_NULLABLE = 'nullable'
    static final String IS_NOT_IN_LIST = 'not.inList'
    static final String IS_NOT_EQUAL = 'notEqual'

    static final String MIN_NOT_MET = 'min.notmet'
    static final String MAX_EXCEEDED = 'max.exceeded'
    static final String MIN_SIZE_NOT_MET = 'minSize.notmet'
    static final String MAX_SIZE_EXCEEDED = 'maxSize.exceeded'
    static final String RANGE_TOO_SMALL = 'range.toosmall'
    static final String RANGE_TOO_BIG = 'range.toobig'
    static final String SIZE_TOO_SMALL = 'size.toosmall'
    static final String SIZE_TOO_BIG = 'size.toobig'

    static final String INVALID_CREDIT_CARD_NUMBER = 'creditCard.invalid'
    static final String INVALID_EMAIL = 'email.invalid'
    static final String INVALID_PATTERN_MATCH = 'matches.invalid'
    static final String INVALID_URL = 'url.invalid'
}
