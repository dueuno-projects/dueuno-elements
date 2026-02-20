<!doctype html>
<html lang="${c.locale}">
<head>
    <%-- Header --%>
    <page:header component="${c}" />

    <%-- Custom Page CSS --%>
    <asset:stylesheet src="elements/pages/PageWebsocket.css" />

    <%-- Colors --%>
    <page:colors component="${c}"/>
</head>

<body data-21-page="${c.className}"
      data-21-id="${c.id}"
>

    <page:loading />

    <div id="page-content">
        <div class="p-3">
            <render:component instance="${c.content}" />
        </div>
    </div>

    <%-- Footer --%>
    <page:footer component="${c}" />

    <%-- Custom Page JS --%>
    <asset:javascript src="elements/pages/PageWebsocket.js" />

    <%-- Page Init --%>
    <page:initialize />

</body>
</html>

