self.addEventListener('install', function(event) {
    log.debug('Service Worker installed');
    self.skipWaiting();
});

self.addEventListener('activate', function(event) {
    log.debug('Service Worker activated');
});

self.addEventListener('fetch', function(event) {
    event.respondWith(
        fetch(event.request).catch(function() {
            return new Response("Offline mode: resource not available.");
        })
    );
});
