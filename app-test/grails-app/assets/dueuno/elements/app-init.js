if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register("${asset.assetPath(src: 'elements/app-service-worker.js')}")
        .then(function(registration) {
            log.debug('ServiceWorker registration successful with scope: ', registration.scope);
        })
        .catch(function(error) {
            log.debug('ServiceWorker registration failed:', error);
        });
}