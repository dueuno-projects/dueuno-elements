<g:if test="${c.icon || c.prefix}">
    <span class="input-group-text"><g:if test="${c.icon}"><render:icon icon="${c.icon}" force="fa-solid" class="fa-fw"/></g:if>${c.prefix}</span>
</g:if>
<input type="text"
       class="control-money-field form-control ${c.textStyle} ${c.cssClass}"
       inputmode="${c.inputMode}"
       maxlength="${(c.maxSize > 0) ? c.maxSize: ''}"
       step="any"
       data-21-control="${c.className}"
       data-21-id="${c.id}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
       ${raw(attributes)}
/>