<g:if test="${c.icon || c.prefix}">
    <span class="input-group-text"><g:if test="${c.icon}"><render:icon icon="${c.icon}" force="fa-solid" class="fa-fw"/></g:if>${c.prefix}</span>
</g:if>
<input type="${c.inputMode}"
       class="control-text-field form-control ${c.textStyle} ${c.cssClass}"
       style="${c.cssStyleColors}${c.cssStyle}"
       inputmode="${c.inputMode}"
       placeholder="${c.message(c.placeholder)}"
       maxlength="${(c.maxSize > 0) ? c.maxSize: ''}"
       autocapitalize="none"
       autocomplete="${c.autocomplete ? 'on' : 'off'}"
       data-21-control="${c.className}"
       data-21-id="${c.id}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${raw(attributes)}
>
<g:if test="${c.actions.hasActions()}"><!--
    <g:each var="action" in="${c.actions.defaultAction}">
        --><render:component instance="${action.link}" properties="[cssClass: 'btn btn-secondary', readonly: c.readonly]" /><!--
    </g:each>
    <g:each var="action" in="${c.actions.getMenuActions()}">
        --><render:component instance="${action.link}" properties="[cssClass: 'btn btn-secondary', readonly: c.readonly]" /><!--
    </g:each>
    --></g:if>
