<div id="${c.id}"
     class="component-header ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
>
    <div class="component-header-box">
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
                    <render:message code="${c.text}" args="${c.textArgs}"/>
                </span>
            </div>

            <div class="component-header-next col-auto">
                <g:if test="${c.hasNextButton}">
                    <render:component instance="${c.nextButton}"/>
                </g:if>
            </div>
        </div>
    </div>
</div>
