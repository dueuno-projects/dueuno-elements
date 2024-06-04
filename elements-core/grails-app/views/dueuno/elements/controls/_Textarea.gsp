<div class="control-textarea w-100 ${c.maxSize > 0 ? 'maxlength' : ''} ${c.cssClass}"
     style="${c.textColor ? 'color: ' + c.textColor + ';': ''} ${c.backgroundColor ? 'background-color: ' + c.backgroundColor + ';' : ''} ${c.cssStyle}"
    ><div for="${c.getId()}" data-max="${c.maxSize}"></div>
    <textarea class="form-control h-100 ${c.monospace ? 'font-monospace' : ''}"
              style="${c.textColor ? 'color: ' + c.textColor + ';': ''} ${c.backgroundColor ? 'background-color: ' + c.backgroundColor + ';' : ''}"
              maxlength="${(c.maxSize > 0) ? c.maxSize : ''}"
              data-21-control="${c.getClassName()}"
              data-21-id="${c.getId()}"
              data-21-properties="${c.propertiesAsJSON}"
              data-21-events="${c.eventsAsJSON}"
              data-21-value="${c.valueAsJSON}"
              ${raw(attributes)}
    >${c.value}</textarea>
</div>
