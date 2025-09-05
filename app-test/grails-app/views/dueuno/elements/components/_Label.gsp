%{--
================
ATTENTION PLEASE
================

When modifying this template, please update also:
 - The Label components embedded in `_TableRow.gsp`
 - The `_Link.gsp` template

--}%
<span class="component-label w-100 ${c.textAlign} ${c.textWrap} ${c.textStyle} ${c.tag ? 'tag' : ''}  ${c.userSelect ? 'user-select-text' : ''} ${c.html ? 'html' : ''} ${c.cssClass}"
      data-21-component="${c.className}"
      data-21-id="${c.id}"
      data-21-properties="${c.propertiesAsJSON}"
      data-21-events="${c.eventsAsJSON}"
      ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
>
    <g:if test="${c.html}">${raw(c.prettyHtml)}</g:if>
    <g:else>
        <g:if test="${c.tooltip}"><span ${raw('data-bs-custom-class="tooltip" data-bs-toggle="tooltip" data-bs-title="' + c.message(c.tooltip) + '"')}></g:if>
        <g:if test="${c.image}"><i><asset:image src="${c.image}" class="${c.text ? 'me-1' : ''} ${c.imageClass}"/></i></g:if>
        <g:elseif test="${c.icon}"><render:icon icon="${c.icon}" class="${c.icon && c.text ? 'me-1' : ''} ${c.iconFixedWidth ? 'fa-fw' : ''}"/></g:elseif>
        <g:if test="${c.url}"><a href="${c.url}" target="_blank"></g:if><span class="text ${c.verticalAlign}">${c.text}${c.tag && !c.text ? raw('&nbsp;') : ''}</span><g:if test="${c.url}"></a></g:if>
        <g:if test="${c.tooltip}"></span></g:if>
    </g:else>
</span>
