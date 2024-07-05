<div class="component-table mb-1 ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <render:component instance="${c.title}" properties="[cssClass: 'd-none']" />
    <render:component instance="${c.filters}" />
    <render:component instance="${c.dataset}" />

<!--    <div class="component-table-scrollbar"><div></div></div>-->

    <g:if test="${c.displayPagination}">
        <render:component instance="${c.pagination}" />
    </g:if>
</div>
