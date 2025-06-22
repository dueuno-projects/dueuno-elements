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
package dueuno.elements.security

import dueuno.elements.tenants.TTenant
import groovy.transform.EqualsAndHashCode
import org.grails.datastore.gorm.GormEntity

/**
 * @author Gianluca Sartori
 */
@EqualsAndHashCode(includes='username')
class TUser implements GormEntity, Serializable {

    private static final long serialVersionUID = 1

    TTenant tenant
    String apiKey

    String username
    String password
    String externalId
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    // System fields
    Boolean deletable
    TRoleGroup defaultGroup
    String note

    // User data
    String firstname
    String lastname
    String language
    String email
    String telephone

    // Session data
    Integer sessionDuration
    Integer rememberMeDuration

    // User preferences
    String decimalFormat // 'ISO_COM' (# ###,#) or 'ISO_DOT' (# ###.#)
    Boolean prefixedUnit
    Boolean symbolicCurrency
    Boolean symbolicQuantity
    Boolean invertedMonth
    Boolean twelveHours
    Boolean firstDaySunday
    Integer fontSize
    Boolean animations

    Set<TRoleGroup> getAuthorities() {
        TUserRoleGroup.findAllByUser(this)*.roleGroup
    }

    static constraints = {
        defaultGroup nullable: true
        password blank: false, password: true
        username blank: false, unique: true
        apiKey nullable: true, unique: true
        externalId nullable: true, unique: true
        firstname nullable: true
        lastname nullable: true
        email nullable: true, email: true
        telephone nullable: true
        note nullable: true, maxSize: 2000
    }

    static mapping = {
        password column: '`password`'
    }

    String getFullname() {
        if (firstname || lastname) {
            return "${firstname ?: ''}${lastname ? ' ' + lastname : ''}"
        } else {
            return username
        }
    }
}
