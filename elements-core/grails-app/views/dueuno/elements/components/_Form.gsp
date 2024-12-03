<div id="${c.id}"
     class="component-form p-3 pt-2 ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <form class="row gx-2 grid" autocomplete="${c.autocomplete ? 'on' : 'off'}">
        <dev:ifDisplayHints>
            <g:if test="${c.keyFields}">
                <div class="dev-hints p-1 px-2 mt-1" role="alert">
                    <g:each in="${c.keyFields}">
                        <div><render:icon icon="fa-solid fa-key"/> (${it.component.valueType}) ${it.component.getId()} =
                            ${it.component.valueType == 'TEXT' ? '"' + it.component.value.toString() + '"' : it.component.value.toString()}
                        </div>
                    </g:each>
                </div>
            </g:if>
        </dev:ifDisplayHints>

        <render:componentList instance="${c}"/>
    </form>
</div>

