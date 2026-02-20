<div class="component-progress-bar ${c.indefinite ? 'in' : ''}definite ${c.cssClass}"
     data-21-component="${c.className}"
     data-21-id="${c.id}"
     data-21-properties="${c.propertiesAsJSON}"
     data-21-events="${c.eventsAsJSON}"
     ${c.cssStyleColors ? raw('style="' + c.cssStyleColors + '"') : ''}
>
    <div class="progress-line"></div>
    <div class="progress-subline progress-inc"></div>
    <div class="progress-subline progress-dec"></div>
    <div
            class="progress-bar"
            role="progressbar"
            aria-valuenow="${c.now}"
            aria-valuemin="${c.min}"
            aria-valuemax="${c.max}"
            style="${"width: " + c.nowCalc + "%"}"
    >
        <span class="progress-label">${c.prettyNowCalc}%</span>
    </div>
</div>
