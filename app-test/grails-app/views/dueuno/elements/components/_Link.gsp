<a class="component-link ${c.cssClass}"
   style="${c.textColor ? 'color: ' + c.textColor + ';': ''} ${c.backgroundColor ? 'background-color: ' + c.backgroundColor + ';' : ''} ${c.cssStyle}"
   data-21-component="${c.className}"
   data-21-id="${c.getId()}"
   data-21-properties="${c.propertiesAsJSON}"
   data-21-events="${c.eventsAsJSON}"
   ${raw(dev.ifDevelopment() { ' href="/' + c.controller + '/' + c.action + '"'}) }
   ${raw(attributes)}
><g:if test="${c.html}">${raw(c.prettyHtml)}</g:if>
    <g:else>
        <g:if test="${c.image}"><i aria-hidden="true"><asset:image src="${c.image}" class="${c.text ? 'me-2' : ''} ${c.imageClass}" style="${c.imageStyle}"/></i></g:if>
        <g:elseif test="${c.icon}"><render:icon icon="${c.icon}" class="${c.text ? 'me-2' : ''} ${c.iconClass} loading-off" style="${c.iconStyle}"/></g:elseif>
        <g:if test="${c.text}"><span class="${c.textClass}" style="${c.textStyle}">${c.text}</span></g:if>
    </g:else>
</a>