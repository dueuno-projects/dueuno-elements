<span class="input-group-text ${c.cssClass}"
      style="${c.cssStyle}"
      data-td-target="#${c.id}"
      data-td-toggle="datetimepicker">
    <i class="fa-solid fa-fw fa-clock"></i>
</span>
<input type="text"
       class="control-time-field form-control ${c.textStyle}"
       style="${c.cssStyleColors}"
       inputmode="numeric"
       autocomplete="off"
       data-td-target="#${c.id}"
       data-21-control="${c.className}"
       data-21-id="${c.id}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${raw(attributes)}
/>


