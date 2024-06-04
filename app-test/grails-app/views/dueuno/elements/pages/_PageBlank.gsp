<!doctype html>
<html lang="${c.locale}">
<head>
    <title><render:message code='springSecurity.login.title'/></title>
    <page:header component="${c}" />
    <asset:stylesheet src="elements/pages/PageBlank.css" />
    <page:colors component="${c}"/>

<body data-21-page="${c.getClassName()}"
      data-21-id="${c.getId()}"
>

<div id="page-content">
    <div class="p-3">
        <render:component instance="${c.content}" />
    </div>
</div>

<%-- Footer --%>
<page:footer component="${c}" />

<%-- Custom Page JS --%>
<asset:javascript src="elements/pages/PageBlank.js" />

<%-- Page Init --%>
<page:initialize />

</body>

</html>

