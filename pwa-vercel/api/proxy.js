const UPSTREAM_ORIGIN = 'https://pw.deltaverse.site';

function replaceBrandText(text) {
  if (!text || typeof text !== 'string') return text;
  let out = text;
  out = out.replace(/delta\s+verse/gi, 'GyanSarthi');
  out = out.replace(/delta\s+teacher/gi, 'GyanSarthi Teacher');
  out = out.replace(/_byDeltaVerse/gi, '_byGyanSarthi');
  out = out.replace(/(^|[^./a-zA-Z0-9_-])deltaverse(?![a-zA-Z0-9_-]|\.[a-zA-Z0-9])/gi, '$1GyanSarthi');
  return out;
}

const INJECTED_GYANSARTHI_SCRIPT = `<script id="gyansarthi-branding-script">
(function() {
  function replaceBrandText(text) {
    if (!text || typeof text !== 'string') return text;
    var out = text;
    out = out.replace(/delta\\s+verse/gi, 'GyanSarthi');
    out = out.replace(/delta\\s+teacher/gi, 'GyanSarthi Teacher');
    out = out.replace(/_byDeltaVerse/gi, '_byGyanSarthi');
    out = out.replace(/Welcome\\s+to\\s+Delta[a-zA-Z\\s]*/gi, 'Welcome to GyanSarthi');
    out = out.replace(/(^|[^./a-zA-Z0-9_-])deltaverse(?![a-zA-Z0-9_-]|\\.[a-zA-Z0-9])/gi, '$1GyanSarthi');
    return out;
  }

  function hasBrandText(text) {
    if (!text || typeof text !== 'string') return false;
    return /delta\\s*verse|delta\\s+teacher|Welcome\\s+to\\s+Delta/i.test(text);
  }

  // 1. Keep window.__SERVER_INFO__ display name as GyanSarthi
  try {
    function sanitizeServerInfoObj(obj) {
      if (obj && typeof obj === 'object') {
        if (typeof obj.webName === 'string') obj.webName = 'GyanSarthi';
        if (typeof obj.sidebarTitle === 'string') obj.sidebarTitle = 'GyanSarthi';
      }
      return obj;
    }
    if (window.__SERVER_INFO__) sanitizeServerInfoObj(window.__SERVER_INFO__);
    if (!window.__serverInfoPatched) {
      window.__serverInfoPatched = true;
      var _srvInfo = window.__SERVER_INFO__;
      Object.defineProperty(window, '__SERVER_INFO__', {
        configurable: true,
        enumerable: true,
        get: function() { return _srvInfo; },
        set: function(val) { _srvInfo = sanitizeServerInfoObj(val); }
      });
    }
  } catch (e) {}

  // 2. Intercept /api/auth/serverInfo JSON display fields only
  try {
    if (!window.__fetchBrandingPatched && window.fetch) {
      window.__fetchBrandingPatched = true;
      var origFetch = window.fetch;
      window.fetch = function() {
        var args = arguments;
        var reqUrl = '';
        try {
          reqUrl = typeof args[0] === 'string' ? args[0] : (args[0] && args[0].url ? args[0].url : '');
        } catch (e) {}
        return origFetch.apply(this, args).then(function(response) {
          try {
            if (reqUrl && reqUrl.indexOf('/api/auth/serverInfo') !== -1 && response && response.json) {
              var origJson = response.json.bind(response);
              response.json = function() {
                return origJson().then(function(data) {
                  if (data && typeof data === 'object') {
                    data.webName = 'GyanSarthi';
                    data.sidebarTitle = 'GyanSarthi';
                  }
                  return data;
                });
              };
            }
          } catch (e) {}
          return response;
        });
      };
    }
  } catch (e) {}

  // 3. Hide Telegram links & popups
  try {
    var styleId = 'anti-tg-style';
    if (!document.getElementById(styleId)) {
      var style = document.createElement('style');
      style.id = styleId;
      style.textContent = 'a[href*="t.me"], a[href*="telegram"], a[href*="tg:"], a[href*="delta_dv"], [class*="telegram" i], [id*="telegram" i], [aria-label*="Telegram" i], [title*="Telegram" i] { display: none !important; visibility: hidden !important; opacity: 0 !important; pointer-events: none !important; }';
      (document.head || document.documentElement).appendChild(style);
    }
  } catch (e) {}

  function purgeAndBrand() {
    try {
      if (document.title && hasBrandText(document.title)) {
        document.title = replaceBrandText(document.title);
      }
      var root = document.body || document.documentElement;
      if (!root) return;

      // Remove Telegram links
      var tgLinks = root.querySelectorAll ? root.querySelectorAll('a[href*="t.me"], a[href*="telegram"], a[href*="tg:"], a[href*="delta_dv"]') : [];
      for (var i = 0; i < tgLinks.length; i++) {
        var item = tgLinks[i].closest('li, .tg-card, .telegram-banner') || tgLinks[i];
        item.remove();
      }

      // Walk all visible text nodes
      var walker = document.createTreeWalker(
        root,
        NodeFilter.SHOW_TEXT,
        {
          acceptNode: function(node) {
            var p = node.parentNode ? node.parentNode.nodeName : '';
            if (p === 'SCRIPT' || p === 'STYLE' || p === 'NOSCRIPT' || p === 'TEXTAREA' || p === 'CODE' || p === 'PRE') {
              return NodeFilter.FILTER_REJECT;
            }
            var val = node.nodeValue;
            if (val && (hasBrandText(val) || val.trim() === 'DV')) {
              return NodeFilter.FILTER_ACCEPT;
            }
            return NodeFilter.FILTER_SKIP;
          }
        },
        false
      );

      var node;
      var nodes = [];
      while (node = walker.nextNode()) nodes.push(node);
      for (var k = 0; k < nodes.length; k++) {
        var n = nodes[k];
        var v = n.nodeValue;
        if (!v) continue;
        if (v.trim() === 'DV') {
          n.nodeValue = v.replace('DV', 'GS');
        } else {
          var updated = replaceBrandText(v);
          if (updated !== v) n.nodeValue = updated;
        }
      }

      // Update visible attributes only (never src or href)
      var tagged = root.querySelectorAll ? root.querySelectorAll('[alt*="Delta" i], [title*="Delta" i], [aria-label*="Delta" i], [placeholder*="Delta" i]') : [];
      for (var j = 0; j < tagged.length; j++) {
        var el = tagged[j];
        var attrs = ['alt', 'title', 'aria-label', 'placeholder'];
        for (var a = 0; a < attrs.length; a++) {
          var val = el.getAttribute(attrs[a]);
          if (val && hasBrandText(val)) {
            el.setAttribute(attrs[a], replaceBrandText(val));
          }
        }
      }
    } catch (e) {}
  }

  // Listen for theme change messages from parent PWA shell
  window.addEventListener('message', function(e) {
    try {
      if (e.data && e.data.type === 'PW_THEME_CHANGE') {
        var dark = e.data.theme === 'dark';
        if (dark) {
          document.documentElement.classList.add('dark');
          if (document.body) document.body.classList.add('dark');
          var ov = document.getElementById('pw-theme-override');
          if (ov) ov.remove();
        } else {
          document.documentElement.classList.remove('dark');
          if (document.body) document.body.classList.remove('dark');
          var sid = 'pw-theme-override';
          var s = document.getElementById(sid);
          if (!s) {
            s = document.createElement('style');
            s.id = sid;
            s.textContent = 'html:not(.dark), body:not(.dark) { background-color: #F8FAFC !important; color: #0F172A !important; }';
            (document.head || document.documentElement).appendChild(s);
          }
        }
      }
    } catch (err) {}
  });

  purgeAndBrand();
  if (document.documentElement) {
    var obs = new MutationObserver(purgeAndBrand);
    obs.observe(document.documentElement, { childList: true, subtree: true, characterData: true });
  }
  setInterval(purgeAndBrand, 300);
})();
</script>`;

export default async function handler(req, res) {
  if (req.method === 'OPTIONS') {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, rsc, next-router-state-tree, next-router-prefetch, next-url');
    return res.status(200).end();
  }

  try {
    const urlObj = new URL(req.url || '/', 'http://localhost');
    let targetPath = (req.query && req.query.__p) || urlObj.searchParams.get('__p') || urlObj.pathname;
    if (Array.isArray(targetPath)) {
      targetPath = targetPath.join('/');
    }
    if (!targetPath.startsWith('/')) {
      targetPath = '/' + targetPath;
    }

    urlObj.searchParams.delete('__p');
    urlObj.searchParams.delete('subpath');
    const queryStr = urlObj.searchParams.toString();
    const upstreamUrl = `${UPSTREAM_ORIGIN}${targetPath}${queryStr ? '?' + queryStr : ''}`;

    // Forward required headers with upstream Origin & Referer so OriginGuard passes
    const forwardHeaders = {
      'Host': 'pw.deltaverse.site',
      'Origin': UPSTREAM_ORIGIN,
      'Referer': `${UPSTREAM_ORIGIN}/study/batches`,
      'User-Agent': req.headers['user-agent'] || 'Mozilla/5.0 (Linux; Android 14; Pixel 7 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36',
      'Accept': req.headers['accept'] || '*/*',
      'Accept-Language': req.headers['accept-language'] || 'en-US,en;q=0.9'
    };

    const passthroughHeaderKeys = [
      'content-type',
      'cookie',
      'authorization',
      'rsc',
      'next-router-state-tree',
      'next-router-prefetch',
      'next-router-segment-prefetch',
      'next-url',
      'x-nextjs-data'
    ];

    for (const key of passthroughHeaderKeys) {
      if (req.headers[key] !== undefined) {
        forwardHeaders[key] = req.headers[key];
      }
    }

    // Prepare request body for POST / PUT / PATCH
    let bodyData = undefined;
    if (req.method !== 'GET' && req.method !== 'HEAD') {
      if (req.body !== undefined && req.body !== null) {
        if (Buffer.isBuffer(req.body) || typeof req.body === 'string') {
          bodyData = req.body;
        } else if (typeof req.body === 'object') {
          bodyData = JSON.stringify(req.body);
        }
      } else {
        const chunks = [];
        for await (const chunk of req) {
          chunks.push(typeof chunk === 'string' ? Buffer.from(chunk) : chunk);
        }
        if (chunks.length > 0) {
          bodyData = Buffer.concat(chunks);
        }
      }
    }

    const upstreamRes = await fetch(upstreamUrl, {
      method: req.method,
      headers: forwardHeaders,
      body: bodyData,
      redirect: 'manual'
    });

    // Forward status & sanitized headers (stripping x-frame-options & CSP)
    const blockedResponseHeaders = new Set([
      'x-frame-options',
      'content-security-policy',
      'content-security-policy-report-only',
      'content-encoding',
      'transfer-encoding',
      'content-length',
      'strict-transport-security',
      'set-cookie'
    ]);

    upstreamRes.headers.forEach((value, key) => {
      const lowerKey = key.toLowerCase();
      if (blockedResponseHeaders.has(lowerKey)) return;
      if (lowerKey === 'location') {
        const cleanLoc = value.startsWith(UPSTREAM_ORIGIN)
          ? value.slice(UPSTREAM_ORIGIN.length) || '/'
          : value;
        res.setHeader('Location', cleanLoc);
        return;
      }
      res.setHeader(key, value);
    });

    // Rewrite Set-Cookie headers so session cookies persist on the Vercel domain
    const rawCookies = typeof upstreamRes.headers.getSetCookie === 'function'
      ? upstreamRes.headers.getSetCookie()
      : (upstreamRes.headers.get('set-cookie') ? [upstreamRes.headers.get('set-cookie')] : []);

    if (rawCookies.length > 0) {
      const cleanedCookies = rawCookies.map((c) =>
        c.replace(/;\s*Domain=[^;]+/gi, '')
      );
      res.setHeader('Set-Cookie', cleanedCookies);
    }

    // Handle redirects immediately
    if (upstreamRes.status >= 300 && upstreamRes.status < 400) {
      return res.status(upstreamRes.status).end();
    }

    const contentType = (upstreamRes.headers.get('content-type') || '').toLowerCase();

    // 1. Transform /api/auth/serverInfo JSON display name to GyanSarthi
    if (targetPath === '/api/auth/serverInfo' && contentType.includes('application/json')) {
      const rawJson = await upstreamRes.text();
      try {
        const parsed = JSON.parse(rawJson);
        parsed.webName = 'GyanSarthi';
        parsed.sidebarTitle = 'GyanSarthi';
        return res.status(upstreamRes.status).send(JSON.stringify(parsed));
      } catch (_) {
        return res.status(upstreamRes.status).send(replaceBrandText(rawJson));
      }
    }

    // 2. Transform HTML, RSC, or JS chunks to replace Delta Verse -> GyanSarthi
    if (
      contentType.includes('text/html') ||
      contentType.includes('text/x-component') ||
      contentType.includes('javascript')
    ) {
      let text = await upstreamRes.text();

      // Bypass client-side OriginGuard domain check
      text = text.replace(/\\"allowed\\":\[\\"pw\.deltaverse\.site\\"\]/g, '\\"allowed\\":[\\"*\\"]');
      text = text.replace(/"allowed":\["pw\.deltaverse\.site"\]/g, '"allowed":["*"]');
      if (targetPath.includes('layout-')) {
        text = text.replace('if(!o.length)return;', 'if(true)return;');
      }

      // Replace all visible Delta Verse / DeltaVerse text with GyanSarthi
      text = replaceBrandText(text);

      // Inject live DOM GyanSarthi branding & Telegram blocker into HTML pages
      if (contentType.includes('text/html')) {
        if (text.includes('</head>')) {
          text = text.replace('</head>', `${INJECTED_GYANSARTHI_SCRIPT}</head>`);
        } else {
          text = `${INJECTED_GYANSARTHI_SCRIPT}${text}`;
        }
      }

      return res.status(upstreamRes.status).send(text);
    }

    // 3. Binary / other responses passed through untouched
    const arrayBuffer = await upstreamRes.arrayBuffer();
    return res.status(upstreamRes.status).send(Buffer.from(arrayBuffer));
  } catch (error) {
    return res.status(502).json({
      status: 'error',
      message: 'Proxy connection error',
      detail: error?.message || String(error)
    });
  }
}
