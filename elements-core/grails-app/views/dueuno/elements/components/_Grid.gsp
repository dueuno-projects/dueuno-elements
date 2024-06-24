<div id="${c.getId()}"
     class="component-grid mb-2"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="row g-${c.spacing}">
        <g:each var="column" in="${c.components}">
            <div class="grid-column ${column.breakpoints}">
            <g:if test="${c.border}">
                <div class="grid-border shadow ${c.cssClass}"
                     style="${c.cssStyleColors}${c.cssStyle}">
                    <div class="grid-border overflow-hidden">
                        <render:component instance="${column.component}" />
                    </div>
                </div>
            </g:if>
            <g:else>
                <div class="${c.cssClass}"
                     style="${c.cssStyleColors}${c.cssStyle}">
                    <render:component instance="${column.component}" />
                </div>
            </g:else>
        </div>
        </g:each>
    </div>
</div>


