<div class="component-separator w-100 ${c.textAlign} ${c.textWrap} ${c.textStyle} ${c.squeeze ? 'squeeze' : ''} ${c.display ? '' : 'd-none'} ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
><g:if test="${c.icon}"><render:icon icon="${c.icon}" force="fa-solid" class="fa-fw"/></g:if><render:message code="${c.text}" /></div>
