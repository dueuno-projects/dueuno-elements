<g:if test="${c.shell.config.display.menu}">
    <div id="shell-menu"
        class="shell-menu offcanvas-xxl offcanvas-start ${c.animations ? '' : 'no-slide'} text-light z-3"
        tabindex="-1"
        data-21-component="${c.className}"
        data-21-id="${c.id}"
        data-21-properties="${c.propertiesAsJSON}"
        data-21-events="${c.eventsAsJSON}"
    >
        <g:if test="${c.shell.config.display.menuSearch}">
            <div id="shell-menu-search" class="offcanvas-header p-0" role="search">
                <div class="input-group ps-1">
                    <button type="button" id="shell-navbar-menu-close"
                            class="btn btn-secondary"
                            data-bs-dismiss="offcanvas"
                            data-bs-target="#shell-menu"
                            aria-label="Close">
                        <i class="fa-solid fa-circle-xmark"></i>
                    </button>
                    <span id="shell-menu-search-icon" class="input-group-text d-none d-lg-flex"><i
                            class="fa-solid fa-magnifying-glass"></i></span>
                    <input class="form-control text-light ps-0" type="search"
                           placeholder="${c.message('shell.menu.search.placeholder')}"
                           aria-label="Search">
                </div>
            </div>
        </g:if>

        <nav class="offcanvas-body">
            <ul id="shell-menu-items"
                class="nav nav-pills flex-column text-truncate pt-2 mb-auto">
                <g:each var="item" in="${c.listItems()}">
                    <g:if test="${item.hasSubitems()}">
                        <li class="nav-item ms-0 mt-4">
                            <span class="nav-link"><dev:ifDisplayHints>[${item.order}]&nbsp;</dev:ifDisplayHints>${item.link.text}</span>
                        </li>
                    </g:if>
                    <g:else>
                        <li class="nav-item mb-1">
                            <render:component instance="${item.link}"
                                            properties="[cssClass: 'nav-link text-truncate', iconClass: 'fa-fw', ]"
                                            data-bs-dismiss="offcanvas"
                                            data-bs-target="#shell-menu"
                            />
                            <dev:ifDisplayHints><span>&nbsp;[${item.order}]</span></dev:ifDisplayHints>
                        </li>
                    </g:else>
                </g:each>
            </ul>
        </nav>
    </div>
</g:if>