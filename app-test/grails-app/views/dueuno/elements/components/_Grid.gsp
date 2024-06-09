<div id="${c.getId()}"
     class="component-grid pe-${c.spacing} ${c.cssClass}"
     style="color: ${c.textColor}; background-color: ${c.backgroundColor}; ${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="row m-0">
        <g:each in="${c.components}">
        <div class="p-0 ${c.colSpecs}">
            <div class="${c.border ? 'shadow rounded-4' : ''} ms-${c.spacing}">
                <div class="${c.border ? 'rounded-4' : ''} my-${c.spacing} overflow-hidden">
                    <render:component instance="${it}" />
                </div>
            </div>
        </div>
        </g:each>
    </div>
</div>


