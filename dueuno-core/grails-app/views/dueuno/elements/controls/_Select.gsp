<g:select
    class="control-select form-control shadow-3 ${c.actions.hasActions() ? 'has-actions' : ''} ${c.textStyle} ${c.cssClass}"
    style="${c.cssStyleColors}"
    name="${c.id}"
    from="${c.options}"
    optionKey="key"
    optionValue="value"
    data-21-control="${c.className}"
    data-21-id="${c.id}"
    data-21-properties="${c.propertiesAsJSON}"
    data-21-events="${c.eventsAsJSON}"
    data-21-value="${c.valueAsJSON}"
/>

<g:if test="${c.actions.hasActions()}"><!--
<g:each var="action" in="${c.actions.defaultAction}">
    --><render:component instance="${action.link}" properties="[cssClass: 'btn btn-secondary', readonly: c.readonly]" /><!--
</g:each>
<g:each var="action" in="${c.actions.getMenuActions()}">
    --><render:component instance="${action.link}" properties="[cssClass: 'btn btn-secondary', readonly: c.readonly]" /><!--
</g:each>
--></g:if>
