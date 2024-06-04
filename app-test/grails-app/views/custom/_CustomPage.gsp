<!DOCTYPE html>
<html lang="${c.locale}">
<head>
    <title>${c.message('app.name')}</title>
    <page:header component="${c}"/>
</head>

<body data-21-page="CustomPage">

<div id="main" class="prevent-select">
    <div class="content-main-wrapper" style="top: 0; height: 100%;">
        <div id="content-main" style="padding-bottom: 0;">

            <render:component instance="${c.content}"/>

        </div>
    </div>
</div>

<%-- Footer --%>
<page:footer component="${c}"/>

</body>
</html>
