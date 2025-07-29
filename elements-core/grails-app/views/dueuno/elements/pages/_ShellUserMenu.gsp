<div id="shell-user-menu"
     class="shell-user-menu offcanvas ${c.animations ? '' : 'no-slide'} offcanvas-end"
     tabindex="-1"

     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>

    <div class="offcanvas-header">
        <p class="offcanvas-title no-wrap fw-bold">
            <span>${c.title}</span>
        </p>
        <button type="button" class="btn btn-secondary"
                data-bs-dismiss="offcanvas"
                data-bs-target="#shell-user-menu"
                aria-label="Close">
            <i class="fa-solid fa-circle-xmark"></i>
        </button>
    </div>

    <div id="shell-user-menu-items" class="offcanvas-body">

        <nav>
            <ul class="nav nav-pills flex-column">
                <g:each var="item" in="${c.listAllItems()}">
                    <g:if test="${item.display}">
                        <g:if test="${item.separator}">
                            <li class="nav-item">
                                <hr>
                            </li>
                        </g:if>
                        <g:else>
                            <li class="nav-item">
                                <render:component instance="${item.link}" properties="[cssClass: 'nav-link']" />
                                <%--dev:ifDisplayHints><span class="badge rounded-pill text-secondary bg-light">${item.order}</span></dev:ifDisplayHints>--%>
                            </li>
                        </g:else>
                    </g:if>
                </g:each>
            </ul>
        </nav>

    </div>

</div>
