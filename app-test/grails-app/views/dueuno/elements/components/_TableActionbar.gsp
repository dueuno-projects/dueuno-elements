<div class="component-actionbar ${c.actions.hasActions() ? '' : 'd-none'} ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
>
    <render:component instance="${c.actions}" />
</div>
