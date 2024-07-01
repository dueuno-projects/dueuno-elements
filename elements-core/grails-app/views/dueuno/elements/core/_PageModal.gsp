<div id="page-modal"
     class="page-modal modal ${c.animations ? 'fade' : ''} ${c.cssClass}"
     aria-hidden="true"
     tabindex="-1"

     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content border-0 rounded-4 shadow">
            <div id="page-modal-header"
                 class="modal-header"
                 style="background-color: ${c.tertiaryBackgroundColor}"
            >
                <render:component instance="${c.closeButton}" />
            </div>
            <div id="page-modal-body" class="modal-body"></div>
        </div>
    </div>
</div>
