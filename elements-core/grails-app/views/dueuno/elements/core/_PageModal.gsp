<div id="page-modal"
     class="page-modal modal ${c.animations ? 'fade' : ''} ${c.cssClass}"
     tabindex="-1"

     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content border-0 rounded-4 shadow">
            <div id="loading-screen-modal" class=""><div><i class="fa-solid fa-circle-notch fa-spin"></i></div></div>
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
