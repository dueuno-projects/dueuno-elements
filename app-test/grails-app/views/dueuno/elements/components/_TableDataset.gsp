<div class="component-table-dataset my-0 bg-white rounded-3 ${c.table.stickyHeader ? 'overflow-y-hidden' : 'overflow-y-visible'} ${c.cssClass}"
     style="${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <table class="table m-0 ${c.table.rowStriped && c.table.body.hasRows() ? 'table-striped' : ''} ${c.table.rowHighlight ? 'table-hover' : ''} ${c.table.rowBorderless ? 'table-borderless' : ''} ${c.table.cssClass}">
        <g:if test="${c.table.displayHeader}">
            <thead>
            <g:each var="row" in="${c.table.header.processedRows}">
                <render:component instance="${row}"/>
            </g:each>
            </thead>
        </g:if>

        <tbody>
        <%-- NO RESULTS --%>
        <g:if test="${c.table.noResults && !c.table.body.hasRows()}">
            <tr class="table-no-results">
                <td><render:icon icon="${c.table.noResultsIcon}"/><br/><render:message
                        code="${c.table.noResultsMessage}"/></td>
            </tr>
        </g:if>

        <%-- RESULTSET --%>
        <g:else>
            <g:each var="row" in="${c.table.body.processedRows}">
                <render:component instance="${row}"/>
            </g:each>
        </g:else>
        </tbody>

        <g:if test="${c.table.displayFooter}">
            <tfoot>
            <g:each var="row" in="${c.table.footer.processedRows}">
                <render:component instance="${row}"/>
            </g:each>
            </tfoot>
        </g:if>

    </table>

</div>