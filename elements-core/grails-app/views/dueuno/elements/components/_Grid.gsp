<div id="${c.getId()}"
     class="component-grid"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="row mx-0">
        <g:each in="${c.components}">
            <div class="grid-element p-0 ${c.breakpoints}">
            <g:if test="${c.border}">
                <div class="grid-border m-${c.spacing} shadow ${c.cssClass}"
                     style="${c.cssStyleColors}${c.cssStyle}">
                    <div class="grid-border overflow-hidden">
                        <render:component instance="${it}" />
                    </div>
                </div>
            </g:if>
            <g:else>
                <div class="m-${c.spacing} ${c.cssClass}"
                     style="${c.cssStyleColors}${c.cssStyle}">
                    <render:component instance="${it}" />
                </div>
            </g:else>
        </div>
        </g:each>
    </div>
</div>


