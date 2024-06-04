<!doctype html>
<html lang="en">
<head>
    <meta name="layout" content="osShell"/>
    <meta charset="UTF-8">
    <title>HTML Table Test</title>
</head>
<body>

<p>
    <a href="index">OS Elements Table</a>
</p>

<table class="table">
    <thead>
    <tr>
    <g:each var="column" in="${columns}">
        <th>${column}</th>
    </g:each>
    </tr>
    </thead>
    <tbody>
    <g:each var="record" in="${recordset}">
        <tr>
            <g:each var="key" in="${record.keySet()}">
            <td>${record[key]}</td>
            </g:each>
        </tr>
    </g:each>
    </tbody>
</table>
</div>


</body>
</html>