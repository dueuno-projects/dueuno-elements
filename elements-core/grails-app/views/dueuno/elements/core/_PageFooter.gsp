<%-- Modal --%>
<render:component instance="${c.modal}" />

<%-- MessageBox --%>
<render:component instance="${c.messageBox}" />

<%-- KeyPress --%>
<render:component instance="${c.keyPress}"/>

<%-- JavaScript Helpers --%>
<script>
    const _21_ = {
        app: {
            url: "${g.createLink(absolute:true, uri:"/")}",
            path: "${page.contextPath()}",
            tenant: "${tenant.current()}",
        },
        <g:if test="${c}">
        user: {
            username: "${security.username()}",
            language: "${c.locale}",
            decimalFormat: "${c.decimalFormat}",
            prefixedUnit: ${c.prefixedUnit},
            invertedMonth: ${c.invertedMonth},
            twelveHours: ${c.twelveHours},
            firstDaySunday: ${c.firstDaySunday},
            fontSize: ${c.fontSize},
            animations: ${c.animations},
        },
        </g:if>
        log: {
            error: ${dev.logError()},
            debug: ${dev.logDebug()},
            trace: ${dev.logTrace()},
        },
    }
</script>

<script>
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register("${asset.assetPath(src: 'elements/app-serviceworker.js')}")
            .then(function(registration) {
                console.log('ServiceWorker registration successful with scope: ', registration.scope);
            })
            .catch(function(error) {
                console.log('ServiceWorker registration failed:', error);
            });
    }
</script>

<%-- Elements Javascript --%>
<asset:javascript src="elements/includes.js"/>

<g:each var="elementsImplementation" in="${c.elementsRegistry}">
    <asset:javascript src="${elementsImplementation}.js"/>
</g:each>

<%-- Plugins Specific Javascript --%>
<asset:assetPathExists src="plugin.js">
    <asset:javascript src="plugin.js"/>
</asset:assetPathExists>

<%-- Application Specific Javascript --%>
<asset:assetPathExists src="application.js">
    <asset:javascript src="application.js"/>
</asset:assetPathExists>
