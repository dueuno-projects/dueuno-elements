<div class="control-textarea w-100 ${c.maxSize > 0 ? 'maxlength' : ''} ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
    ><div for="${c.id}" data-max="${c.maxSize}"></div>
    <textarea class="form-control h-100 ${c.textStyle}"
              style="${c.cssStyleColors}${c.cssStyle}"
              maxlength="${(c.maxSize > 0) ? c.maxSize : ''}"
              data-21-control="${c.className}"
              data-21-id="${c.id}"
              data-21-properties="${c.propertiesAsJSON}"
              data-21-events="${c.eventsAsJSON}"
              data-21-value="${c.valueAsJSON}"
              ${raw(attributes)}
    >${c.value}</textarea>
</div>
