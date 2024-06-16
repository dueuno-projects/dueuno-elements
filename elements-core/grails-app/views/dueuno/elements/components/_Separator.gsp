<div class="component-separator w-100 ${c.textAlign} ${c.textWrap} ${c.squeeze ? 'squeeze' : ''} ${c.cssClass}"
     style="${c.cssStyleColors}${c.cssStyle}"
     data-21-component="${c.getClassName()}"
     data-21-id="${c.getId()}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
><g:if test="${c.icon}"><render:icon icon="${c.icon}" force="fa-solid" class="me-1 fa-fw"/></g:if><render:message code="${c.text}" /></div>
