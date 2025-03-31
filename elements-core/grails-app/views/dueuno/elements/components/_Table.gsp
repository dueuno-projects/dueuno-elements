<div class="component-table mb-1 ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
>
    <render:component instance="${c.title}" />
    <render:component instance="${c.filters}" />
    <render:component instance="${c.dataset}" />

    <g:if test="${c.hasPagination}">
        <render:component instance="${c.pagination}" />
    </g:if>
</div>
