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

import dueuno.commons.utils.LogUtils
import dueuno.elements.core.*
import dueuno.elements.exceptions.ElementsException
import dueuno.elements.pages.Shell
import dueuno.elements.pages.ShellConfig
import dueuno.elements.tenants.TenantPropertyService
import dueuno.elements.tenants.TenantService
import grails.gorm.multitenancy.CurrentTenant
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Gianluca Sartori
 */

@Slf4j
@CompileStatic
class ShellService implements WebRequestAware, LinkGeneratorAware {

    @Autowired
    private SystemPropertyService systemPropertyService

    @Autowired
    private TenantService tenantService

    @Autowired
    private TenantPropertyService tenantPropertyService

    @Autowired
    private ApplicationService applicationService

    @Autowired
    private PageService pageService

    void install(String tenantId) {
        tenantService.withTenant(tenantId) {
            tenantPropertyService.setString('LOGO', linkPublicResource(tenantId, '/brand/logo.png', false))
        }
    }

    Shell getShell() {
        Shell shell = pageService.mainPage as Shell
        return shell ?: createShell()
    }

    private Shell createShell() {
        try {
            ShellConfig config = readConfig()
            Map shellArgs = [config: config]
            Shell newShell = pageService.createPage(Shell, shellArgs)
            pageService.mainPage = newShell
            return newShell

        } catch (Exception e) {
            log.error LogUtils.logStackTrace(e)
            throw new ElementsException("Cannot create the shell: ${e.message}", e)
        }
    }

    private ShellConfig readConfig() {
        ShellConfig shellConfig = new ShellConfig()

        // These are not cloned, we will have direct access to the servletContext objects
        shellConfig.features.user = applicationService.userFeatures
        shellConfig.features.main = applicationService.mainFeatures

        // System properties
        shellConfig.display.menu = systemPropertyService.getBoolean('DISPLAY_MENU', true)
        shellConfig.display.menuSearch = systemPropertyService.getBoolean('DISPLAY_MENU_SEARCH', true)
        shellConfig.display.homeButton = systemPropertyService.getBoolean('DISPLAY_HOME_BUTTON', true)
        shellConfig.display.userMenu = systemPropertyService.getBoolean('DISPLAY_USER_MENU', true)

        // Tenant properties
        shellConfig.display.logo = tenantPropertyService.getString('LOGO', true)

        return shellConfig
    }

    void setShellDashboardItem(String controller) {
        setShellDashboardItem(controller, null, -1)
    }

    void setShellDashboardItem(String controller, Integer order) {
        setShellDashboardItem(controller, null, order)
    }

    @CurrentTenant
    @CompileDynamic
    void setShellDashboardItem(String controller, String parent, Integer order = -1) {
        TShellDashboardItem item = TShellDashboardItem.findBy(controller: controller)

        if (item) {
            if (order == -1) order = TShellDashboardItem.count() + 1
            item.order = order

        } else {
            item = new TShellDashboardItem(
                    controller: controller,
                    order: order,
            )
        }

        item.save(flush: true, failOnError: true)
    }

    @CompileDynamic
    List<TShellDashboardItem> listShellDashboardItem() {
        return TShellDashboardItem.findAllByUserOrderByOrder()
    }
}
