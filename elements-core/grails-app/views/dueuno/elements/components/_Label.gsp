%{--

ATTENTION PLEASE
================

Update the label part of `_TableRow.gsp` each time you update this template

--}%
<span class="component-label w-100 ${c.textAlign} ${c.textWrap} ${c.textStyle} ${c.border ? 'border' : ''}  ${c.userSelect ? 'user-select-text' : ''} ${c.html ? 'html' : ''} ${c.cssClass}"
      style="${c.cssStyleColors}${c.cssStyle}"
      data-21-component="${c.className}"
      data-21-id="${c.id}"
      data-21-properties="${c.propertiesAsJSON}"
      data-21-events="${c.eventsAsJSON}"
><g:if test="${c.html}">${raw(c.prettyHtml)}</g:if>
    <g:else>
        <span ${c.tooltip ? raw('data-bs-custom-class="tooltip" data-bs-toggle="tooltip" data-bs-title="' + render.message(c.tooltip) + '"') : ''}>
        <g:if test="${c.icon}"><render:icon icon="${c.icon}" class="${c.icon && c.text ? 'me-1' : ''}"/></g:if>
        <g:if test="${c.url}"><a href="${c.url}" target="_blank"></g:if><span class="text ${c.verticalAlign}">${c.message(c.text, c.textArgs)}${c.border && !c.text ? raw('&nbsp;') : ''}</span><g:if test="${c.url}"></a></g:if>
        </span>
    </g:else>
</span>
