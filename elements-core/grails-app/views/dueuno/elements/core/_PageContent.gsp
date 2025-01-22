<div id="page-content"
     class="page-content"
     data-21-component="PageContent"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <page:colors component="${c}"/>

    <div id="page-content-scrollbar-box"><div id="page-content-scrollbar"><div id="page-content-scrollbar-content-mirror"></div></div></div>

    <%-- This must be the latest element for the CSS to work property on modals --%>
    <render:componentList instance="${c}"/>

</div>
