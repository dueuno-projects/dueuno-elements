<div class="component-pagination pagination-container text-center mt-1 ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
>
    <div class="component-button btn-group dropup ${c.hasPages() ? '' : 'pagination-total'}">
        <g:if test="${c.hasPages()}">
            <g:if test="${c.requiresPrev()}">
                <render:component instance="${c.goFirst}" properties="[cssClass: 'btn btn-secondary']"/>
                <render:component instance="${c.goPrev}" properties="[cssClass: 'btn btn-secondary']"/>
            </g:if>
            <g:if test="${c.requiresNext()}">
                <render:component instance="${c.goNext}" properties="[cssClass: 'btn btn-secondary']"/>
            </g:if>
        </g:if>
        <button type="button"
                class="btn btn-secondary ${c.hasPages() ? 'dropdown-toggle' : ''}"
                data-bs-toggle="dropdown"
                data-bs-reference="parent"
                aria-haspopup="true"
                aria-expanded="false"
                tabindex="${c.hasPages() ? '0' : '-1'}">
            <g:if test="${c.hasPages()}">
                ${c.prettyPagination}
            </g:if>
            <g:else>
                ${c.prettyTotal}
            </g:else>
        </button>
        <g:if test="${c.hasPages()}">
            <ul class="dropdown-menu dropdown-menu-end" role="menu">
                <li><render:component instance="${c.goMax20}" properties="[cssClass: 'dropdown-item']"/></li>
                <li><render:component instance="${c.goMax50}" properties="[cssClass: 'dropdown-item']"/></li>
            </ul>
        </g:if>
    </div>
</div>
