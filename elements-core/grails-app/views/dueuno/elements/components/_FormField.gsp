<div class="component-form-field ${c.cols} ${c.help ? 'help' : ''} ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <g:if test="${c.component.getClassName() != 'HiddenField'}">
        <label for="${c.component.getId()}" class="form-label text-truncate overflow-x-hidden ${dev.displayHints() == 'true' ? 'dev' : ''} ${c.displayLabel ? '' : 'd-none'}">
            <g:if test="${dev.displayHints() == 'true'}">
                <span><render:message code="${c.label}" args="${c.labelArgs}" />&nbsp;(${c.component.getId()}, ${c.id})</span><i class="${c.nullable ? 'd-none' : ''}"> *</i>
            </g:if>
            <g:elseif test="${c.message(c.label)}">
                <span><render:message code="${c.label}" args="${c.labelArgs}" /></span><i class="${c.nullable ? 'd-none' : ''}"> *</i>
            </g:elseif>
            <g:else>
                <span>&nbsp;</span>
            </g:else>
        </label>
    </g:if>

    <div class="input-group ${c.displayLabel ? '' : 'mt-2'}"
         ${raw(c.component.getContainerAttributes())}
         ${c.rows ? raw('style="' + c.rows + '"') : ''}
    >
        <render:component id="${c.component.getId()}" instance="${c.component}" properties="[:]" />
        <g:if test="${c.help}">
            <button class="component-help btn btn-secondary" type="button"
                    data-bs-toggle="collapse" data-bs-target="#${c.id}-help">
                <i class="fa-solid fa-circle-question"></i>
            </button>
        </g:if>
    </div>

    <g:if test="${c.component.getClassName() != 'HiddenField'}">
        <div class="error-message p-1 pb-0 d-none">
            <i class="fa-solid fa-fw fa-circle-exclamation pe-2"></i>
            <span>Error!</span>
        </div>

        <g:if test="${c.help}">
            <div id="${c.id}-help" class="collapse">
                <div class="help-message p-2 pb-3">
                    <render:message code="${c.help}" args="${c.helpArgs}"/>
                </div>
            </div>
        </g:if>
    </g:if>

</div>

