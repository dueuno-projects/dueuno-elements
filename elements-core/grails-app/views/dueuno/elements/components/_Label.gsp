%{-- Keep the _TableRow.gsp inline Label component aligned with this _Label.gsp template --}%
<span class="component-label w-100 ${c.textAlign} ${c.textWrap} ${c.textStyle} ${c.border ? 'border' : ''}  ${c.userSelect ? 'user-select-auto' : ''} ${c.html ? 'html' : ''} ${c.cssClass}"
      style="${c.cssStyleColors}${c.cssStyle}"
      ${c.tooltip ? raw('data-bs-custom-class="tooltip" data-bs-toggle="tooltip" data-bs-title="' + c.message(c.tooltip) + '"') : ''}
      data-21-component="${c.className}"
      data-21-id="${c.id}"
      data-21-properties="${c.propertiesAsJSON}"
      data-21-events="${c.eventsAsJSON}"
><g:if test="${c.html}">${raw(c.prettyHtml)}</g:if>
    <g:else>
        <g:if test="${c.icon}"><render:icon icon="${c.icon}" class="${c.icon ? 'me-1' : ''}"/></g:if>
        <g:if test="${c.url}"><a href="${c.url}" target="_blank"></g:if><span class="${c.verticalAlign}">${c.text}${c.border && !c.text ? raw('&nbsp;') : ''}</span><g:if test="${c.url}"></a></g:if>
    </g:else>
</span>
