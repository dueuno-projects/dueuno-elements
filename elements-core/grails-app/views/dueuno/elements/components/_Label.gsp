<span class="component-label w-100 ${c.textAlign} ${c.textWrap} ${c.textStyle} ${c.border ? 'border' : ''} ${c.cssClass}"
      style="${c.cssStyleColors}${c.cssStyle}"
      data-21-component="${c.className}"
      data-21-id="${c.id}"
      data-21-properties="${c.propertiesAsJSON}"
      data-21-events="${c.eventsAsJSON}"
><g:if test="${c.html}">${raw(c.prettyHtml)}</g:if>
    <g:else>
        <g:if test="${c.icon}"><render:icon icon="${c.icon}" class="${c.icon ? 'me-2' : ''}"/></g:if>
        <g:if test="${c.url}"><a href="${c.url}"></g:if><span>${c.text}${c.border && !c.text ? raw('&nbsp;') : ''}</span><g:if test="${c.url}"></a></g:if>
    </g:else>
</span>
