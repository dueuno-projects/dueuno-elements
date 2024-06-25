<div id="${c.getId()}"
     class="component-form p-3 pt-2 pb-4 ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <form class="row gx-2 grid" autocomplete="${c.autocomplete ? 'on' : 'off'}">
        <dev:ifDisplayHints>
            <g:if test="${c.keyFields}">
                <div class="alert alert-info" role="alert">
                    <p>Form '${c.getId()}' ${c.validate ? 'validating on \'' + c.validate.simpleName + '\'' : ''}</p>
                    <ul>
                        <g:each in="${c.keyFields}">
                            <li><render:icon icon="fa-solid fa-key"/> (${it.value.getClass().simpleName}) ${it.getId()} = ${it.value.toString()}</li>
                        </g:each>
                    </ul>
                </div>
            </g:if>
        </dev:ifDisplayHints>

        <form:renderFields instance="${c}"/>
    </form>
</div>

