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
package dueuno.elements.audit

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

/**
 * @author Gianluca Sartori
 */
class TAuditLog implements GormEntity, MultiTenant<TAuditLog> {

    LocalDateTime dateCreated

    String ip
    String userAgent
    String username
    AuditOperation operation
    String message

    String objectName
    String stateBefore
    String stateAfter

    static constraints = {
        ip nullable: true
        userAgent nullable: true
        message nullable: true
        objectName nullable: true
        stateBefore nullable: true, maxSize: 2000
        stateAfter nullable: true, maxSize: 2000
    }
}
