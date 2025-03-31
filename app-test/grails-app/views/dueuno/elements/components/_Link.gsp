<a class="component-link ${c.textAlign} ${c.textWrap} ${c.border ? 'border' : ''} ${c.cssClass}"
   data-21-component="${c.className}"
   data-21-id="${c.id}"
   data-21-properties="${c.propertiesAsJSON}"
   data-21-events="${c.eventsAsJSON}"
   href="${dev.ifDevelopment() { c.devUrl } }"
   ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
   ${c.tooltip ? raw('data-bs-custom-class="tooltip" data-bs-toggle="tooltip" data-bs-title="' + c.message(c.tooltip) + '"') : ''}
   ${raw(attributes)}
><g:if test="${c.html}">${raw(c.prettyHtml)}</g:if>
<g:elseif test="${c.components}"><render:componentList instance="${c}" /></g:elseif>
<g:else>
    <g:if test="${c.image}"><i><asset:image src="${c.image}" class="${c.text ? 'me-1' : ''} ${c.imageClass}"/></i></g:if>
    <g:elseif test="${c.icon}"><render:icon icon="${c.icon}" class="${c.text ? 'me-1' : ''} ${c.iconClass}"/></g:elseif>
    <g:if test="${c.text}"><span class="${c.textStyle}">${c.message(c.text, c.textArgs)}</span></g:if>
</g:else>
</a>