<g:if test="${c.unitOptions && !c.readonly}">
    <button class="control-quantity-field-unit btn dropdown-toggle fw-bold" type="button"
            data-bs-toggle="dropdown" aria-expanded="false"
            data-21-unit="${c.defaultUnit}">${c.prettyDefaultUnit}
    </button>
    <ul class="dropdown-menu">
        <g:each var="unit" in="${c.unitOptions}">
            <li><a class="dropdown-item" href="#" data-21-unit="${unit.key}">${unit.value}</a></li>
        </g:each>
    </ul>
</g:if><g:else>
    <span class="input-group-text" data-21-unit="${c.defaultUnit}">${c.prettyDefaultUnit}</span>
</g:else>
<input type="${c.keyboardType}"
       class="control-quantity-field form-control ${c.cssClass}"
       style="${c.cssStyleColors}${c.cssStyle}"
       maxlength="${(c.maxSize > 0) ? c.maxSize: ''}"
       step="any"
       data-21-control="${c.getClassName()}"
       data-21-id="${c.getId()}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${raw(attributes)}
/>