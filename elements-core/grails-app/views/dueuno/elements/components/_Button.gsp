<div class="component-button btn-group ${c.stretch ? 'flex-fill' : ''} ${c.cssClass}"
     style="${c.maxWidth > 0 ? 'max-width: ' + c.maxWidth + 'px;' : ''} ${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.primary ? 'primary' : ''}
>
    <g:if test="${c.defaultAction}">
        <render:component instance="${c.defaultAction.link}" properties="[cssClass: 'component-button-default-action btn btn-secondary text-nowrap' + (c.group ? '' : ' w-100'), textColor: c.textColor, backgroundColor: c.backgroundColor]" />
    </g:if>

    <g:if test="${c.tailAction}">
        <render:component instance="${c.tailAction.link}" properties="[cssClass: 'component-button-tail-action btn btn-secondary text-nowrap', textColor: c.textColor, backgroundColor: c.backgroundColor]" />
    </g:if>

    <g:if test="${!c.group && c.getMenuActions()}">
        <button type="button" ${c.readonly ? 'disabled tabindex="-1"' : ''}
            class="btn btn-secondary dropdown-toggle dropdown-toggle-split"
            style="color: ${c.textColor}; background-color: ${c.backgroundColor};"
            data-bs-toggle="dropdown" data-bs-reference="parent" aria-expanded="false">
        </button>

        <ul class="dropdown-menu" role="menu">
            <g:each var="action" in="${c.getMenuActions()}">
                <g:if test="${action.separator}">
                    <li role="separator">
                        <hr class="dropdown-divider">
                    </li>
                </g:if>
                <g:else>
                    <li><render:component instance="${action.link}" properties="[cssClass: 'dropdown-item', iconClass: 'fa-fw']" /></li>
                </g:else>
            </g:each>
        </ul>
    </g:if>
    <g:elseif test="${c.getMenuActions()}">
        <g:each var="action" in="${c.getMenuActions()}">
            <g:if test="${!action.separator}">
                <render:component instance="${action.link}" properties="[cssClass: 'btn btn-secondary text-nowrap', readonly: c.readonly]" />
            </g:if>
        </g:each>
    </g:elseif>

</div>
