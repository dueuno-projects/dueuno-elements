<div class="component-filters container-fluid p-0 ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="d-flex mb-1">

        <div class="component-filters-buttons ${c.controls ? '' : 'd-none'} btn-group me-1">
            <button class="component-filters-toggle btn btn-secondary dropdown-toggle ${c.isFolded() ? 'collapsed' : ''} border-0 rounded-3"
                    type="button"
                    data-bs-toggle="collapse">
                <i class="fa-solid fa-filter"></i>
                <span class="component-filters-counter ${c.isFiltering ? '' : 'd-none'}">${c.values.size()}</span>
            </button>
            <div class="component-filters-search collapse ${c.isFolded() ? '' : 'show'} collapse-horizontal text-nowrap gx-0">
                <render:component
                        instance="${c.searchButton}"
                        properties="[cssClass: (c.isFiltering ? 'btn btn-secondary rounded-0' : 'btn btn-secondary rounded-start-0 rounded-end-3')]"/><g:if
                    test="${c.isFiltering}"><render:component
                        instance="${c.resetButton}"
                        properties="[cssClass: 'component-filters-search-reset btn btn-secondary rounded-start-0 rounded-end-3']"/>
            </g:if>
            </div>
        </div>

        <render:component instance="${c.actionbar}" />
    </div>

    <div class="component-filters-box component-form collapse ${c.isFolded() ? '' : 'show'} bg-white border-0 rounded-3 mb-1">
        <form class="row gx-2 p-3 pt-1">

            <render:componentList instance="${c}" />

        </form>
    </div>

</div>
