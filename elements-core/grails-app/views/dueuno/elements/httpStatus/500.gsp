<div class="err-500">
    <div id="page-content"
         data-21-component="PageContent"
         data-21-properties="{}"
         data-21-events="{}"
    >
        <style>
        #err-production {
            width: 100% !important;
            text-align: center;
            padding: 60px 20px !important;
        }

        #err-production i {
            font-size: 10em;
            padding-bottom: 20px;
        }
        </style>
        <div id="err-production">
            <render:icon icon="${icon ?: 'fa-solid fa-face-tired'}"/><br/>
            <g:if test="${message}">
                <p>${message}</p>
            </g:if>
            <g:else>
                <p class="fw-bold fs-1"><render:message code="default.system.error"/></p>
                <p class="fs-4"><render:message code="default.system.error.explain"/></p>
            </g:else>
        </div>
        <g:if env="development">
            <div id="err-development">
            <g:if test="${Throwable.isInstance(exception)}">
                <g:renderException exception="${exception}"/>
            </g:if>
            <g:elseif test="${request.getAttribute('jakarta.servlet.error.exception')}">
                <g:renderException exception="${request.getAttribute('jakarta.servlet.error.exception')}"/>
            </g:elseif>
            </div>
        </g:if>
    </div>
</div>
