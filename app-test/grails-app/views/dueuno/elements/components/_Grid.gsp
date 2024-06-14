<div id="${c.getId()}"
     class="component-grid ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="row mx-0">
        <g:each in="${c.components}">
            <div class="grid-element p-0 ${c.breakpoints}">
            <g:if test="${c.border}">
                <div class="m-${c.spacing} shadow rounded-4">
                    <div class="rounded-4 overflow-hidden">
                        <render:component instance="${it}" />
                    </div>
                </div>
            </g:if>
            <g:else>
                <div class="m-${c.spacing}">
                    <render:component instance="${it}" />
                </div>
            </g:else>
        </div>
        </g:each>
    </div>
</div>


