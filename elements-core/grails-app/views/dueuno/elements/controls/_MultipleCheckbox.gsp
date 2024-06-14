<span class="control-multiple-checkbox ${c.cssClass}"
      style="${c.cssStyle}">
    <input type="hidden"
       data-21-control="${c.getClassName()}"
       data-21-id="${c.getId()}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${raw(attributes)}
       style="${c.cssStyleColors}"
    />
<g:each var="checkbox" in="${c.checkboxes}">
    <div class="control-multiple-checkbox" data-fieldname="${c.getId()}">
        <div>
            <render:component
                    instance="${checkbox.value}"
                    properties="[text: c.message(checkbox.value.optionValue)]"
            />
        </div>
    </div>
</g:each>
<div></div>
</span>
