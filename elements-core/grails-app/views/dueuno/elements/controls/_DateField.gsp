<span class="input-group-text ${c.cssClass}"
      data-td-target="#${c.id}"
      data-td-toggle="datetimepicker">
    <i class="fa-solid fa-fw fa-calendar"></i>
</span>
<input type="text"
       class="control-date-field form-control ${c.textStyle}"
       inputmode="numeric"
       autocomplete="off"
       data-td-target="#${c.id}"
       data-21-control="${c.className}"
       data-21-id="${c.id}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
       ${raw(attributes)}
/>


