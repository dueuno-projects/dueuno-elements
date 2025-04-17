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
package dueuno.elements.core

import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class LinkDefinition implements Serializable {

    /** The namespace of the controller to use in the link */
    String namespace

    /** The name of the controller to use in the link; if not specified the current controller will be linked */
    String controller

    /** The name of the action to use in the link; if not specified the default action will be linked */
    String action

    /** A Map of request parameters */
    Map params

    /** The link fragment (often called anchor tag) to use */
    String fragment

    /** An absolute path (starting with '/') or relative path  */
    String path

    /** Link may point to a specific URL. If specified it has precedence over controller/action pair. */
    String url

    /** Whether to render the whole html page (or raw http body) or a Transition */
    Boolean direct

    /** List of the names of the components to submit; if not specified the container component will be passed */
    List<String> submit

    /** The target page to display the link into */
    String target

    /** Render properties */
    PageRenderProperties renderProperties

    /** If true a waiting screen will be displayed while the link gets retrieved */
    Boolean loading

    /** If specified an info message will pop up, the link will never be executed */
    String infoMessage
    List infoMessageArgs

    /** If specified a confirm message will pop up giving the user a chance to cancel the action */
    String confirmMessage
    List confirmMessageArgs


    LinkDefinition(Map args) {
        namespace = args.namespace ?: ''
        controller = args.controller ?: ''
        action = args.action ?: ''
        fragment = args.fragment ?: ''
        path = args.path ?: ''
        url = args.url ?: ''
        if (url) {
            // URLs are handled as direct links by default
            direct = args.direct == null ? true : args.direct as Boolean
        } else {
            direct = args.direct
        }

        params = args.params as Map ?: [:]

        args.submit in List
                ? setSubmit(args.submit as List<String>)
                : setSubmit(args.submit as String)

        target = args.target
        targetNew = args.targetNew
        loading = args.loading

        infoMessage = args.infoMessage
        infoMessageArgs = args.infoMessageArgs as List
        confirmMessage = args.confirmMessage
        confirmMessageArgs = args.confirmMessageArgs as List

        if (args.renderProperties)
            renderProperties = (PageRenderProperties) args.renderProperties
        else
            renderProperties = new PageRenderProperties(args)
    }

    Boolean getTargetNew() {
        return target == '_blank'
    }

    void setTargetNew(Boolean value) {
        if (value) {
            direct = true
            target = '_blank'
        }
    }

    void setTarget(String value) {
        if (value && value == '_self') {
            direct = true
            target = value
        }
    }

    void setSubmit(String value) {
        if (!value) {
            return
        }

        submit = [value]
    }

    void setSubmit(List<String> value) {
        if (!value) {
            return
        }

        submit = value
    }

    Map asMap() {
        return [
                namespace: namespace,
                controller: controller,
                action: action,
                url: url,
                params: params,
                submit: submit,
                direct: direct,
                target: target,
                loading: loading,
                infoMessage: infoMessage,
                confirmMessage: confirmMessage,
                renderProperties: renderProperties.asMap(),
        ]
    }
}
