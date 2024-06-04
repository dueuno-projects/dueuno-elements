<div class="component-table mt-1 ${c.cssClass}"
     style="${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <render:component instance="${c.title}" properties="[cssClass: 'd-none']" />
    <render:component instance="${c.filters}" />
    <render:component instance="${c.dataset}" />
    <g:if test="${c.displayPagination}">
        <render:component instance="${c.pagination}" />
    </g:if>
</div>
