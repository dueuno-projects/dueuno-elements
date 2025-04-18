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
package dueuno.elements.components


import dueuno.elements.core.LinkDefinition
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class Link extends Label {

    /** The link */
    LinkDefinition linkDefinition

    Link(Map args) {
        super(args)

        args.confirmMessage = message(args.confirmMessage as String, args.confirmMessageArgs)
        args.infoMessage = message(args.infoMessage as String, args.infoMessageArgs)

        linkDefinition = new LinkDefinition(args)
        linkDefinition.action = args.action ?: 'index'
        linkDefinition.loading = args.loading

        tag = args.tag == null ? false : args.tag

        html = args.html
        containerSpecs.label = ''
        containerSpecs.help = ''

        setOnClickEvent(args.onClick as String)
    }

    private void setOnClickEvent(String onClick = null) {
        on(linkDefinition.properties + [
                event: 'click',
                action: onClick ?: linkDefinition.action,
                infoMessage: message(linkDefinition.infoMessage, linkDefinition.infoMessageArgs),
                confirmMessage: message(linkDefinition.confirmMessage, linkDefinition.confirmMessageArgs),
        ])
    }

    @Override
    String getPropertiesAsJSON(Map properties = [:]) {
        Map thisProperties = [
                loading: loading,
        ]
        return super.getPropertiesAsJSON(thisProperties + properties)
    }

    String toString() {
        String ns = linkDefinition.namespace ?: ''
        String path = "${ns}/${linkDefinition.controller}/${linkDefinition.action}"
        String queryParams = linkDefinition.params.collect { "${it.key}=${it.value}" }.join('&')
        String queryString = queryParams ? "?${queryParams}" : ''
        return "Link: ${path}${queryString}"
    }

    String getDevUrl() {
        if (url) return url
        if (controller && action) return "/${controller}/${action}"
        return null
    }

    String getTarget() {
        return linkDefinition.target
    }

    void setTarget(String value) {
        linkDefinition.target = value
    }

    String getNamespace() {
        return linkDefinition.namespace
    }

    void setNamespace(String value) {
        linkDefinition.namespace = value
        setOnClickEvent()
    }

    String getController() {
        return linkDefinition.controller
    }

    void setController(String value) {
        linkDefinition.controller = value
        setOnClickEvent()
    }

    String getAction() {
        return linkDefinition.action
    }

    void setAction(String value) {
        linkDefinition.action = value
        setOnClickEvent()
    }

    Map getParams() {
        return linkDefinition.params
    }

    void setParams(Map value) {
        linkDefinition.params = value
        setOnClickEvent()
    }

    String getFragment() {
        return linkDefinition.fragment
    }

    void setFragment(String value) {
        linkDefinition.fragment = value
        setOnClickEvent()
    }

    String getPath() {
        return linkDefinition.path
    }

    void setPath(String value) {
        linkDefinition.path = value
        setOnClickEvent()
    }

    String getUrl() {
        return linkDefinition.url
    }

    void setUrl(String value) {
        linkDefinition.url = value
        setOnClickEvent()
    }

    List<String> getSubmit() {
        return linkDefinition.submit
    }

    void setSubmit(String value) {
        setSubmit([value])
    }

    void setSubmit(List<String> value) {
        linkDefinition.submit = value
        setOnClickEvent()
    }

    Boolean getDirect() {
        return linkDefinition.direct
    }

    void setDirect(Boolean value) {
        linkDefinition.direct = value
    }

    Boolean getModal() {
        return linkDefinition.renderProperties.modal
    }

    void setModal(Boolean value) {
        linkDefinition.renderProperties.modal = value
        setOnClickEvent()
    }

    Boolean getWide() {
        return linkDefinition.renderProperties.wide
    }

    void setWide(Boolean value) {
        linkDefinition.renderProperties.wide = value
        setOnClickEvent()
    }

    String getAnimate() {
        return linkDefinition.renderProperties.animate
    }

    void setAnimate(String value) {
        linkDefinition.renderProperties.animate = value
        setOnClickEvent()
    }

    Boolean getCloseButton() {
        return linkDefinition.renderProperties.closeButton
    }

    void setCloseButton(Boolean value) {
        linkDefinition.renderProperties.closeButton = value
        setOnClickEvent()
    }

    String getScroll() {
        return linkDefinition.renderProperties.scroll
    }

    void setScroll(String value) {
        linkDefinition.renderProperties.scroll = value
        setOnClickEvent()
    }

    Boolean getTargetNew() {
        return linkDefinition.targetNew
    }

    void setTargetNew(Boolean value) {
        linkDefinition.targetNew = value
        setOnClickEvent()
    }

    Boolean getLoading() {
        return linkDefinition.loading
    }

    void setLoading(Boolean value) {
        linkDefinition.loading = value
        setOnClickEvent()
    }

    String getInfoMessage() {
        return linkDefinition.infoMessage
    }

    void setInfoMessage(String value) {
        linkDefinition.infoMessage = value
        setOnClickEvent()
    }

    void setInfoMessageArgs(List value) {
        linkDefinition.infoMessageArgs = value
        setOnClickEvent()
    }

    String getConfirmMessage() {
        return linkDefinition.confirmMessage
    }

    void setConfirmMessage(String value) {
        linkDefinition.confirmMessage = value
        setOnClickEvent()
    }

    void setConfirmMessageArgs(List value) {
        linkDefinition.confirmMessageArgs = value
        setOnClickEvent()
    }

    String getPrettyHtml() {
        return prettyPrint(html, prettyPrinterProperties)
    }
}
