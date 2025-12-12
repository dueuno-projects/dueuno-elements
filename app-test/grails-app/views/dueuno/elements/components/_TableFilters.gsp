<div class="component-filters container-fluid p-0 ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
>
    <div class="d-flex mb-1">

        <div class="component-filters-buttons ${c.controls ? '' : 'd-none'} btn-group me-1">
            <button class="component-filters-toggle btn btn-secondary dropdown-toggle ${c.isFolded() ? 'collapsed' : ''} border-0"
                    type="button"
                    data-bs-toggle="collapse">
                <i class="fa-solid fa-filter"></i>
                <span class="component-filters-counter ${c.isFiltering ? '' : 'd-none'}">${c.values.size()}</span>
            </button>
            <div class="component-filters-search collapse ${c.isFolded() ? '' : 'show'} collapse-horizontal text-nowrap gx-0">
                <render:component
                        instance="${c.searchButton}"
                        properties="[cssClass: (c.isFiltering ? 'btn btn-secondary rounded-0' : 'btn btn-secondary rounded-start-0')]"/><g:if
                    test="${c.isFiltering}"><render:component
                        instance="${c.resetButton}"
                        properties="[cssClass: 'component-filters-search-reset btn btn-secondary rounded-start-0']"/>
            </g:if>
            </div>
        </div>

        <render:component instance="${c.actionbar}" />
    </div>

    <div class="component-filters-box component-form collapse ${c.isFolded() ? '' : 'show'}">
        <form class="row gx-2">

            <render:componentList instance="${c}" />

        </form>
    </div>

</div>
