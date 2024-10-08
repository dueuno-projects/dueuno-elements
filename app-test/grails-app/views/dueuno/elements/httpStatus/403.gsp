<div id="err-404">
    data-21-component="PageContent"
    data-21-properties="{}"
    data-21-events="{}"
    >
        <style>
        #err-not-found {
            width: 100% !important;
            text-align: center;
            padding: 60px 20px !important;
        }

        #err-not-found i {
            font-size: 10em;
            padding-bottom: 20px;
        }
        </style>

        <div id="err-not-found">
            <render:icon icon="${icon ?: 'fa-solid fa-face-dizzy'}"/><br/>
            <g:if test="${message}">
                <p>${message}</p>
            </g:if>
            <g:else>
                <p class="fw-bold fs-1"><render:message code="default.system.access.denied"/></p>
                <p class="fs-4"><render:message code="default.system.access.denied.explain"/></p>
            </g:else>
        </div>
    </div>
</div>