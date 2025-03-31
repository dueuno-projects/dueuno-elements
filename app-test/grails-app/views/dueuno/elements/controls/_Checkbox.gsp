<g:if test="${c.simple}">
<div class="control-checkbox-simple ${c.cssClass}">
    <div class="checkbox simple">
<style>
    .control-checkbox-simple .form-check-input:checked {
        background-color: ${c.primaryBackgroundColor};
        border: none;
    }
</style>
</g:if>
<g:else>
<span class="control-checkbox input-group-text ${c.cssClass}"><g:if test="${c.message(c.text)}"><render:message code="${c.text}" /></g:if><g:else>&nbsp;</g:else>
    <div class="form-check form-switch">
</g:else>

<input
    class="form-check-input ${c.simple ? '' : 'form-control'}"
    type="checkbox"
    role="switch"
    data-21-control="${c.className}"
    data-21-id="${c.id}"
    data-21-properties="${c.propertiesAsJSON}"
    data-21-events="${c.eventsAsJSON}"
    data-21-value="${c.valueAsJSON}"
    ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
    ${raw(attributes)}
/>

<g:if test="${c.simple}">
</div>
</g:if>
<g:else>
    </div>
</span>
</g:else>
