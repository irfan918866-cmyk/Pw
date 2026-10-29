const CACHE_NAME = 'pw-gyansarthi-v4';
const STATIC_ASSETS = [
  '/',
  '/index.html',
  '/manifest.json',
  '/icons/icon-192.png',
  '/icons/icon-512.png'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(STATIC_ASSETS);
    })
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.map((key) => {
          if (key !== CACHE_NAME) {
            return caches.delete(key);
          }
        })
      );
    })
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);
  const p = url.pathname;

  // Never intercept proxied study portal or API routes
  if (
    p.startsWith('/api/') ||
    p.startsWith('/study') ||
    p.startsWith('/guest') ||
    p.startsWith('/auth') ||
    p.startsWith('/login') ||
    p.startsWith('/live') ||
    p.startsWith('/watch') ||
    p.startsWith('/batch') ||
    p.startsWith('/_next/')
  ) {
    event.respondWith(fetch(event.request));
    return;
  }

  // Network-first for root navigation
  if (event.request.mode === 'navigate') {
    event.respondWith(
      fetch(event.request)
        .then((res) => {
          const copy = res.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put('/index.html', copy));
          return res;
        })
        .catch(() => caches.match('/index.html'))
    );
    return;
  }

  // Cache first for static local PWA assets
  event.respondWith(
    caches.match(event.request).then((cached) => {
      return cached || fetch(event.request).catch(() => cached);
    })
  );
});
