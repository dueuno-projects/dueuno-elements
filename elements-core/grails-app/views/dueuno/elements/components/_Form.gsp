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
            <g:if test="${c.validate || c.keyFields}">
                <div class="dev-hints p-1 px-2 mt-1" role="alert">
                    <table>
                        <g:if test="${c.validate}">
                            <tr>
                                <td class="align-top pe-1">validate</td>
                                <td>${c.validate.simpleName}</td>
                            </tr>
                        </g:if>
                        <g:if test="${c.keyFields}">
                            <tr>
                                <td class="align-top pe-1">keyFields</td>
                                <td>
                                    <g:each in="${c.keyFields}">
                                        <div>- ${it.component.valueType} ${it.component.getId()} =
                                            ${it.component.value.toString()}
                                        </div>
                                    </g:each>
                                </td>
                            </tr>
                        </g:if>
                    </table>
                </div>
            </g:if>
        </dev:ifDisplayHints>

        <render:componentList instance="${c}"/>
    </form>
</div>

