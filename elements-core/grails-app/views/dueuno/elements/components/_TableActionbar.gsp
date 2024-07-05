<div class="component-actionbar ${c.actions.hasActions() ? '' : 'd-none'} ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <render:component instance="${c.actions}" />
</div>
