<span class="input-group-text ${c.cssClass}"
      style="${c.cssStyle}"
      data-td-target="#${c.getId()}"
      data-td-toggle="datetimepicker">
    <i class="fa-solid fa-fw fa-clock"></i>
</span>
<input type="text"
       class="control-time-field form-control"
       style="${c.cssStyleColors}"
       autocomplete="off"
       data-td-target="#${c.getId()}"
       data-21-control="${c.getClassName()}"
       data-21-id="${c.getId()}"
       data-21-properties="${c.propertiesAsJSON}"
       data-21-events="${c.eventsAsJSON}"
       data-21-value="${c.valueAsJSON}"
       ${raw(attributes)}
/>


