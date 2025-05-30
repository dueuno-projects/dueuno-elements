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

import grails.validation.Validateable

/**
 * User profile validator
 *
 * @author Gianluca Sartori
 */
class UserProfileValidator implements Validateable {
    String username
    String firstname
    String lastname
    String email
    String note
    String newPassword
    String confirmNewPassword

    static constraints = {
        newPassword nullable: true, minSize: 4, validator: { val, obj, errors ->
            if (obj.confirmNewPassword != val) errors.rejectValue('newPassword', 'userProfile.newPassword.noMatch')
        }
        confirmNewPassword nullable: true, minSize: 4, validator: { val, obj, errors ->
            if (obj.newPassword != val) errors.rejectValue('confirmNewPassword', 'userProfile.confirmNewPassword.noMatch')
        }
        username blank: false, unique: true
        firstname nullable: true
        lastname nullable: true
        email nullable: true, email: true
        note nullable: true
    }
}