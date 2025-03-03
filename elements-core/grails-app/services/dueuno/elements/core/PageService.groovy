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

import dueuno.commons.utils.FileUtils
import dueuno.elements.contents.ContentHeader
import dueuno.elements.security.SecurityService
import dueuno.elements.tenants.TenantPropertyService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.gsp.GroovyPagesTemplateEngine

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class PageService implements WebRequestAware, LinkGeneratorAware {

    GroovyPagesTemplateEngine groovyPagesTemplateEngine
    SecurityService securityService
    SystemPropertyService systemPropertyService
    TenantPropertyService tenantPropertyService
    TransitionService transitionService

    void install(String tenantId) {
        tenantPropertyService.setString('FAVICON', linkPublicResource(tenantId, 'brand/favicon.png', false))
        tenantPropertyService.setString('APPICON', linkPublicResource(tenantId, 'brand/appicon.png', false))
    }

    Page getMainPage() {
        return session['_main_page_'] as Page
    }

    void setMainPage(Page page) {
        session['_main_page_'] = page
    }

    /**
     * Returns an instance of a specific Page class.
     *
     * @classOrName Class to instantiate
     * @return an instance of a Page
     */
    public <T> T createPage(Class<T> clazz, Map args = [:]) {
        if (!securityService.isLoggedIn()) {
            fontSize = systemPropertyService.getNumber('FONT_SIZE', true) as Integer
        }

        return Component.createInstance(clazz, clazz.simpleName.uncapitalize(), initialize(args))
    }

    /**
     * Returns an instance of a specific content class.
     *
     * @classOrName Class to instantiate
     * @return an instance of a Content
     */
    public <T> T createContent(Class<T> clazz, Map args = [:]) {
        return Component.createInstance(clazz, null, initialize(args))
    }

    private Map initialize(Map args) {
        /**
         * Defines the style of the icons to use in the application. It's one of the styles available
         * in font awesome and can be one of the following: [fa-solid, fa-regular, fa-light, fa-thin, fa-duotone, fa-brand].
         * Defaults to 'fa-solid'
         *
         * See https://fontawesome.com/how-to-use/on-the-web/referencing-icons/basic-use
         */
        args.iconStyle = tenantPropertyService.getString('ICON_STYLE')
        args.favicon = tenantPropertyService.getString('FAVICON')
        args.appicon = tenantPropertyService.getString('APPICON')

        /** Colors */
        args.primaryTextColor = tenantPropertyService.getString('PRIMARY_TEXT_COLOR')
        args.primaryBackgroundColor = tenantPropertyService.getString('PRIMARY_BACKGROUND_COLOR')
        args.primaryBackgroundColorAlpha = (Double) tenantPropertyService.getNumber('PRIMARY_BACKGROUND_COLOR_ALPHA')
        args.tertiaryTextColor = tenantPropertyService.getString('TERTIARY_TEXT_COLOR')
        args.tertiaryBackgroundColor = tenantPropertyService.getString('TERTIARY_BACKGROUND_COLOR')
        args.secondaryTextColor = tenantPropertyService.getString('SECONDARY_TEXT_COLOR')
        args.secondaryBackgroundColor = tenantPropertyService.getString('SECONDARY_BACKGROUND_COLOR')

        return args
    }

    /**
     * Returns an instance of a ContentBase
     * @return an instance of a ContentBase
     */
    ContentHeader createContent() {
        return createContent(ContentHeader)
    }

    Transition createTransition() {
        return transitionService.createTransition()
    }

    OutputStream getDownloadOutputStream(String pathname, Boolean inline = false) {
        String filename = FileUtils.stripFilename(pathname)
        String contentDisposition = inline ? 'inline' : 'attachment'
        response.setHeader('Content-disposition', contentDisposition + '; filename=' + filename)

        String mimeType = getMimeType(filename)
        response.setContentType(mimeType)

        grailsWebRequest.setRenderView(false)

        return response.outputStream
    }

    void download(String pathname, Boolean inline = false) {
        File file = new File(pathname)

        if (file.exists()) {
            OutputStream out = getDownloadOutputStream(pathname, inline)
            out.write(file.bytes)
            out.close()

        } else {
            response.status = 404
        }
    }

    private String getMimeType(String pathname) {
        File file = new File(pathname)
        FileNameMap fileNameMap = URLConnection.getFileNameMap()
        return fileNameMap.getContentTypeFor(file.getName())
    }

    String getIconStyle() {
        String result = session['iconStyle']

        if (!result) {
            result = tenantPropertyService.getString('ICON_STYLE')
            session['iconStyle'] = result
        }

        return result
    }
}
