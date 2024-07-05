<div id="page-messagebox"
     class="page-messagebox modal ${c.animations ? 'fade' : ''} ${c.cssClass}"
     aria-hidden="true"
     tabindex="-1"
     role="dialog"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <div class="modal-dialog modal-dialog-scrollable">
        <div class="modal-content border-0 rounded-4 shadow">
            <i class="message-icon fa-solid"></i>
            <div class="modal-body p-4">
                <p class="message"></p>
            </div>
            <div id="page-messagebox-footer" class="modal-footer component-form d-flex border-0">
                <render:component instance="${c.cancel}" />
%{--                <div id="page-messagebox-verify" class="flex-fill">--}%
%{--                    <render:component instance="${c.verify}" />--}%
%{--                </div>--}%
                <render:component instance="${c.confirm}" />
            </div>
        </div>
    </div>
</div>

