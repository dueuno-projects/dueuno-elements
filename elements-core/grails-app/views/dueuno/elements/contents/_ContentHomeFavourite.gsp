<div class="shell-content-card">
    <a href="#/"
            data-21-id="${feature.link.getId()}"
            data-21-component="${feature.link.className}"
            data-21-properties="${feature.link.propertiesAsJSON}"
            data-21-events="${feature.link.eventsAsJSON}"
    >
        <div class="card overflow-hidden bg-gradient shadow"
             style="color: ${c.primaryTextColor}; background-color: ${c.primaryBackgroundColor};">
            <render:icon
                    icon="${feature.link.icon}"
                    class="icon-back fa-fw"
            />
            <render:icon
                    icon="${feature.link.icon}"
                    class="fa-fw"
            />
            <div class="p-4 text-white text-shadow-1">
                <h2 class="card-title text-truncate"><render:icon icon="${feature.link.icon}" class="me-3" /><render:message code="shell.${feature.link.controller}"/></h2>
                <p class="card-text fw-light">
                    <g:if test="${c.message('shell.' + feature.link.controller + '.help') != 'shell.' + feature.link.controller + '.help'}">
                        <render:message code="shell.${feature.link.controller}.help" />
                    </g:if>
                    <g:else>
                        <dev:ifDisplayHints>
                            shell.${feature.link.controller}.help
                        </dev:ifDisplayHints>
                    </g:else>
                </p>
            </div>
        </div>
    </a>
</div>