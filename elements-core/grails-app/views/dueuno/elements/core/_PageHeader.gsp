<!doctype html>
<meta charset="utf-8" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />

<link rel="apple-touch-icon" type="image/png" sizes="180x180" href="${c.linkPublicResource('brand/appicon.png')}"/>
<link rel="icon" type="image/png" sizes="64x64" href="${c.linkPublicResource('brand/favicon.png')}"/>

<asset:stylesheet src="elements/includes.css" media="screen"/>
<g:each var="elementsImplementation" in="${c.elementsRegistry}">
    <asset:stylesheet src="${elementsImplementation}.css" media="screen"/>
</g:each>

<%-- Application Specific Stylesheet --%>
<asset:assetPathExists src="custom/application.css">
    <asset:stylesheet src="custom/application.css"/>
</asset:assetPathExists>

<%-- Page Specific Stylesheets --%>
<asset:assetPathExists src="custom/${controllerName + '-' + actionName}.css">
    <asset:stylesheet src="custom/${controllerName + '-' + actionName}.css"/>
</asset:assetPathExists>

<asset:stylesheet src="elements/main.css" media="screen"/>

<style>
    :root {
        --elements-font-size: ${c.fontSize ?: '17'}px;
    }
</style>

<div id="loading-screen-page" class="fade-in"><div><i class="fa-solid fa-circle-notch fa-spin"></i></div></div>