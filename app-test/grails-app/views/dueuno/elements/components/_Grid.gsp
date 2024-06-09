<div id="${c.getId()}"
     class="component-grid ${c.cssClass}"
     style="color: ${c.textColor}; background-color: ${c.backgroundColor}; ${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="row">
        <g:each in="${c.components}">
        <div class="grid-element p-${c.spacing} ${c.breakpoints}">
            <div class="${c.border ? 'shadow rounded-4' : ''}">
                <div class="${c.border ? 'rounded-4' : ''} overflow-hidden">
                    <render:component instance="${it}" />
                </div>
            </div>
        </div>
        </g:each>
    </div>
</div>


