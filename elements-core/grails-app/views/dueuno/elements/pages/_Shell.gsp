<!doctype html>
<html lang="${c.locale}">
<head>
    <page:header component="${c}"/>
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
    <page:initialize />

</body>
</html>