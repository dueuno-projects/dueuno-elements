<div id="${c.getId()}"
     class="component-header ${c.cssClass}"
     style="${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="row">
        <div class="component-header-back col-auto">
            <g:if test="${c.hasBackButton}">
                <render:component instance="${c.backButton}"/>
            </g:if>
        </div>

        <div class="component-header-title navbar-text col text-truncate">
            <span class="align-middle h-100">
                <g:if test="${c.icon}">
                    <render:icon icon="${c.icon}"/>
                </g:if>
                <render:message code="${c.title}" args="${c.titleArgs}"/>
            </span>
        </div>

        <div class="component-header-next col-auto">
            <g:if test="${c.hasNextButton}">
                <render:component instance="${c.nextButton}"/>
            </g:if>
        </div>
    </div>
</div>
