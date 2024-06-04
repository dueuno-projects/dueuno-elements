%{-- If you change this file you need to change also the class 'Transition.renderContent()' --}%
<div id="page-content"
     class="page-content"
     data-21-component="PageContent"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
>
    <page:colors component="${c}"/>
    <render:componentList instance="${c}"/>
</div>
