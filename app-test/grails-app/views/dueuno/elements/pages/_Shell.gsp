<!doctype html>
<html lang="${c.locale}">
<head>
    <page:header component="${c}"/>
    <asset:stylesheet src="elements/pages/Shell.css" />
</head>

<body class="${c.isProduction() ? 'prod' : ''}"
      data-21-page="${c.className}"
      data-21-id="${c.id}"
>

    <page:loading />

    <div id="page-shell" class="m-0 p-0">
        <render:component instance="${c.menu}"/>
        <render:component instance="${c.navbar}"/>
        <render:component instance="${c.userMenu}"/>

        <div id="shell-content"
             style="${c.config.display.menu ? '' : 'margin-left: 0 !important;'}"
        >
            <render:component instance="${c.content}"/>
        </div>

    </div>

    <page:footer component="${c}"/>
    <asset:javascript src="elements/pages/Shell.js" />
    <page:initialize />

</body>
</html>