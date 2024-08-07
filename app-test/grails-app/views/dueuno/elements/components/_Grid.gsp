<div id="${c.id}"
     class="component-grid mb-2"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="row g-${c.spacing} ${c.border ? 'my-0' : ''}">
        <g:each var="column" in="${c.components}">
            <div class="grid-column ${column.breakpoints}">
            <g:if test="${c.border}">
                <div class="grid-border shadow ${c.cssClass}"
                     style="${c.cssStyleColors}${c.cssStyle}">
                    <div class="grid-border overflow-hidden">
                        <render:componentList instance="${column}"/>
                    </div>
                </div>
            </g:if>
            <g:else>
                <div class="${c.cssClass}"
                     style="${c.cssStyleColors}${c.cssStyle}">
                    <render:componentList instance="${column}"/>
                </div>
            </g:else>
        </div>
        </g:each>
    </div>
</div>


