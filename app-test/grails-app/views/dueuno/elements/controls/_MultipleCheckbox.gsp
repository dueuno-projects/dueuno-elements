<span class="control-multiple-checkbox ${c.cssClass}">
    <input type="hidden"
       data-21-control="${c.className}"
       data-21-id="${c.id}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
       ${raw(attributes)}
    />
<g:each var="checkbox" in="${c.checkboxes}">
    <div class="control-multiple-checkbox" data-fieldname="${c.id}">
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
