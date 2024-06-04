<!doctype html>
<html lang="${c.locale}">
<head>
    <title><render:message code='springSecurity.login.title'/></title>
    <page:header component="${c}" />
    <link rel="apple-touch-icon" type="image/png" sizes="180x180" href="${c.linkPublicResource('DEFAULT', 'brand/appicon.png')}"/>
    <link rel="icon" type="image/png" sizes="64x64" href="${c.linkPublicResource('DEFAULT', 'brand/favicon.png')}"/>
    <asset:stylesheet src="elements/pages/Login.css" />
    <page:colors component="${c}"/>

    <style>
        body, html {
            height: 100%;
            margin: 0;
        }

        .background-image {
            background-color: ${c.backgroundImage ? 'transparent' : c.backgroundColor};
            background-image: url('${raw(c.backgroundImage)}');
            height: 100%;
            background-position: center;
            background-repeat: no-repeat;
            background-size: cover;
        }

        .page-login-footer a {
            color: ${c.primaryBackgroundColor};
        }
        </style>
</head>


<body class="background-image"
      data-21-page="${c.getClassName()}"
      data-21-id="${c.getId()}"
>

<div id="page-content" class="page-login justify-content-center">

    <div class="page-login-box text-center p-3">

        <div class="page-login-wheel d-none"></div>

        <img class="page-login-logo py-2"
             src="${raw(c.logoImage)}"
        />

        <div class="page-login-error d-none"
             style="color: #cc0000;"><render:message code="shell.auth.bad.credentials"/></>
        </div>

        <render:component instance="${c.form}" />

        <div class="page-login-footer mb-3 text-center">
            <g:if test='${c.copy}'><div class="mb-3">${raw(c.copy)}</div></g:if>
            <g:if test='${c.register}'><div><render:component instance="${c.registerLink}" /></div></g:if>
            <g:if test='${c.passwordRecovery}'><div><render:component instance="${c.passwordRecoveryLink}" /></div></g:if>
        </div>

    </div>

</div>


<%-- Footer --%>
<page:footer component="${c}" />

<%-- Custom Page JS --%>
<asset:javascript src="elements/pages/Login.js" />

<%-- Page Init --%>
<page:initialize />

</body>

</html>

