<div class="component-table-dataset-wrapper bg-white rounded-3">
    <div class="component-table-dataset mt-0 ${c.table.stickyHeader ? 'overflow-y-hidden' : 'overflow-y-visible'} ${c.cssClass}"
         data-21-component="${c.className}"
         data-21-id="${c.id}"
         data-21-properties="${c.propertiesAsJSON}"
         data-21-events="${c.eventsAsJSON}"
         ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
    >
        <table class="table m-0 ${c.table.rowStriped && c.table.body.hasRows() ? 'table-striped' : ''} ${c.table.rowHighlight ? 'table-hover' : ''} ${c.table.rowBorderless ? 'table-borderless' : ''} ${c.table.cssClass}">
            <g:if test="${c.table.hasHeader}">
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
                    <td>
                        <g:if test="${c.table.noResultsIcon}">
                            <render:icon icon="${c.table.noResultsIcon}"/>
                            <br/>
                        </g:if>
                        <div><render:message code="${c.table.noResultsMessage}"/></div>
                    </td>
                </tr>
            </g:if>

            <%-- RESULTSET --%>
            <g:else>
                <g:each var="row" in="${c.table.body.processedRows}">
                    <render:component instance="${row}"/>
                </g:each>
            </g:else>
            </tbody>

            <g:if test="${c.table.hasFooter}">
                <tfoot>
                <g:each var="row" in="${c.table.footer.processedRows}">
                    <render:component instance="${row}"/>
                </g:each>
                </tfoot>
            </g:if>

        </table>

    </div>
</div>