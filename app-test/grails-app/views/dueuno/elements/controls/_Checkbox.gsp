<g:if test="${c.simple}">
<div class="control-checkbox-simple ${c.cssClass}"
     style="${c.cssStyle}">
    <div class="checkbox simple">
<style>
    .control-checkbox-simple .form-check-input:checked {
        background-color: ${c.primaryBackgroundColor};
        border: none;
    }
</style>
</g:if>
<g:else>
<span class="control-checkbox input-group-text ${c.cssClass}"
      style="${c.cssStyle}"><render:message code="${c.text}" />
    <div class="form-check form-switch">
</g:else>

<input
    class="form-check-input ${c.simple ? '' : 'form-control'}"
    style="${c.cssStyleColors}"
    type="checkbox"
    role="switch"
    data-21-control="${c.className}"
    data-21-id="${c.id}"
    data-21-properties="${c.propertiesAsJSON}"
    data-21-events="${c.eventsAsJSON}"
    data-21-value="${c.valueAsJSON}"
    ${raw(attributes)}
/>

<g:if test="${c.simple}">
</div>
</g:if>
<g:else>
    </div>
</span>
</g:else>
