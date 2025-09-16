if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register(serviceWorkerFilename)
        .then(function(registration) {
            console.log('ServiceWorker registration successful with scope: ', registration.scope);
        })
        .catch(function(error) {
            console.log('ServiceWorker registration failed:', error);
        });
}
