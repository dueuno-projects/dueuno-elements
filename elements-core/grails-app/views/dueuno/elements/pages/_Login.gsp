<!doctype html>
<html lang="${c.locale}">
<head>
    <page:header component="${c}" />
    <asset:stylesheet src="elements/pages/Login.css" />
    <page:colors component="${c}"/>
</head>

<body class="background-image"
      data-21-page="${c.className}"
      data-21-id="${c.id}"
>

<page:loading />

<div id="page-content" class="page-login justify-content-center">

    <div class="page-login-box text-center p-3">
        <img class="page-login-logo py-2"
             src="${raw(c.logoImage)}"
        />
        <div class="page-login-error d-none">
            <render:message code="shell.auth.bad.credentials"/>
        </div>
    </div>

    <render:component instance="${c.loginKeyPress}" />
    <render:component instance="${c.form}" />

</div>

</div>

<page:footer component="${c}" />
<asset:javascript src="elements/pages/Login.js" />
<page:initialize />

</body>
</html>
