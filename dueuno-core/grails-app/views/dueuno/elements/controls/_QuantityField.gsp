<g:if test="${c.unitOptions && !c.readonly}">
    <button class="control-quantity-field-unit-list btn dropdown-toggle fw-bold" type="button"
            data-bs-toggle="dropdown" aria-expanded="false"
            data-21-unit="${c.defaultUnit}">${c.prettyDefaultUnit}
    </button>
    <ul class="dropdown-menu">
        <g:each var="unit" in="${c.unitOptions}">
            <li><a class="dropdown-item" href="#" data-21-unit="${unit.key}">${unit.value}</a></li>
        </g:each>
    </ul>
</g:if><g:else>
    <span class="control-quantity-field-unit input-group-text" data-21-unit="${c.defaultUnit}">${c.prettyDefaultUnit}</span>
</g:else>
<input type="text"
       class="control-quantity-field form-control ${c.textStyle} ${c.cssClass}"
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