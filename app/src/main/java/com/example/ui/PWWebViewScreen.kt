package com.example.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.RenderProcessGoneDetail
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R

private const val DEFAULT_URL = "https://pw.deltaverse.site/study/batches"
private val PW_HEADERS = mapOf(
    "Referer" to "https://pw.deltaverse.site/",
    "Origin" to "https://pw.deltaverse.site"
)

private const val REMOVE_TELEGRAM_JS = """
(function() {
    function purgeTelegram() {
        try {
            // 1. Remove all Telegram links
            var tgLinks = document.querySelectorAll('a[href*="t.me"], a[href*="telegram"], a[href*="tg:"], a[href*="delta_dv"]');
            for (var i = 0; i < tgLinks.length; i++) {
                var link = tgLinks[i];
                var item = link.closest('li, .tg-card, .telegram-banner') || link;
                item.remove();
            }

            // 2. Remove any modal, dialog, popup, or overlay mentioning Telegram
            var popups = document.querySelectorAll('[role="dialog"], dialog, [class*="modal"], [class*="popup"], [class*="Dialog"], div[class*="fixed"], div[class*="absolute"]');
            for (var j = 0; j < popups.length; j++) {
                var popup = popups[j];
                var text = (popup.innerText || popup.textContent || '').toLowerCase();
                if (text.indexOf('telegram') !== -1 || (text.indexOf('join') !== -1 && text.indexOf('community') !== -1) || text.indexOf('delta_dv') !== -1) {
                    popup.remove();
                    // Clean up backdrops & overlays
                    var backdrops = document.querySelectorAll('[class*="backdrop"], div[class*="fixed"][class*="inset-0"]');
                    for (var k = 0; k < backdrops.length; k++) {
                        var b = backdrops[k];
                        if (!b.innerText || b.innerText.trim().length === 0) {
                            b.remove();
                        }
                    }
                    if (document.body) document.body.style.overflow = 'auto';
                    if (document.documentElement) document.documentElement.style.overflow = 'auto';
                }
            }

            // 3. Remove buttons or spans dedicated to Telegram
            var buttons = document.querySelectorAll('button, a, div[role="button"]');
            for (var m = 0; m < buttons.length; m++) {
                var btn = buttons[m];
                var bText = (btn.innerText || btn.textContent || '').toLowerCase().trim();
                if (bText === 'join telegram' || bText.indexOf('join telegram') !== -1 || bText.indexOf('telegram community') !== -1 || bText.indexOf('telegram channel') !== -1) {
                    var container = btn.closest('[role="dialog"], [class*="modal"], div[class*="fixed"]') || btn;
                    container.remove();
                    if (document.body) document.body.style.overflow = 'auto';
                }
            }
        } catch (e) {}
    }

    // Hide via CSS immediately
    try {
        var styleId = 'anti-tg-style';
        if (!document.getElementById(styleId)) {
            var style = document.createElement('style');
            style.id = styleId;
            style.textContent = 'a[href*="t.me"], a[href*="telegram"], a[href*="tg:"], a[href*="delta_dv"], [class*="telegram" i], [id*="telegram" i], [aria-label*="Telegram" i], [title*="Telegram" i] { display: none !important; visibility: hidden !important; opacity: 0 !important; pointer-events: none !important; }';
            (document.head || document.documentElement).appendChild(style);
        }
    } catch(e) {}

    // Override window.open to block telegram URLs
    try {
        if (!window.__tgPatched) {
            window.__tgPatched = true;
            var origOpen = window.open;
            window.open = function(url, target, features) {
                if (url && (url.indexOf('t.me') !== -1 || url.indexOf('telegram') !== -1 || url.indexOf('tg:') !== -1)) {
                    return null;
                }
                return origOpen.call(this, url, target, features);
            };
        }
    } catch(e) {}

    function applyGyanSarthiBranding() {
        try {
            if (document.title && /delta\s*verse/i.test(document.title)) {
                document.title = document.title.replace(/delta\s*verse/gi, 'GyanSarthi');
            }
            var root = document.body || document.documentElement;
            if (!root) return;
            var walker = document.createTreeWalker(
                root,
                NodeFilter.SHOW_TEXT,
                {
                    acceptNode: function(node) {
                        var p = node.parentNode ? node.parentNode.nodeName : '';
                        if (p === 'SCRIPT' || p === 'STYLE' || p === 'NOSCRIPT' || p === 'TEXTAREA') {
                            return NodeFilter.FILTER_REJECT;
                        }
                        if (node.nodeValue && /delta\s*verse/i.test(node.nodeValue)) {
                            return NodeFilter.FILTER_ACCEPT;
                        }
                        return NodeFilter.FILTER_SKIP;
                    }
                },
                false
            );
            var node;
            var toReplace = [];
            while (node = walker.nextNode()) {
                toReplace.push(node);
            }
            for (var i = 0; i < toReplace.length; i++) {
                toReplace[i].nodeValue = toReplace[i].nodeValue.replace(/delta\s*verse/gi, 'GyanSarthi');
            }
            var tagged = root.querySelectorAll ? root.querySelectorAll('img[alt*="Delta" i], img[title*="Delta" i], [aria-label*="Delta" i], [placeholder*="Delta" i]') : [];
            for (var j = 0; j < tagged.length; j++) {
                var el = tagged[j];
                var attrs = ['alt', 'title', 'aria-label', 'placeholder'];
                for (var a = 0; a < attrs.length; a++) {
                    var attrName = attrs[a];
                    var val = el.getAttribute(attrName);
                    if (val && /delta\s*verse/i.test(val)) {
                        el.setAttribute(attrName, val.replace(/delta\s*verse/gi, 'GyanSarthi'));
                    }
                }
            }
        } catch (e) {}
    }

    // Execute purge & branding immediately
    purgeTelegram();
    applyGyanSarthiBranding();

    // Attach MutationObserver
    if (!window.__tgObserver && (document.documentElement || document.body)) {
        window.__tgObserver = new MutationObserver(function() {
            purgeTelegram();
            applyGyanSarthiBranding();
        });
        window.__tgObserver.observe(document.documentElement || document.body, {
            childList: true,
            subtree: true
        });
    }
})();
"""

private fun getThemeJs(dark: Boolean): String {
    return if (dark) {
        "(function() { try { document.documentElement.classList.add('dark'); if (document.body) document.body.classList.add('dark'); localStorage.setItem('theme', 'dark'); localStorage.setItem('theme-mode', 'dark'); var ov = document.getElementById('pw-theme-override'); if (ov) ov.remove(); } catch(e) {} })();"
    } else {
        "(function() { try { document.documentElement.classList.remove('dark'); if (document.body) document.body.classList.remove('dark'); localStorage.setItem('theme', 'light'); localStorage.setItem('theme-mode', 'light'); var sid = 'pw-theme-override'; var s = document.getElementById(sid); if (!s) { s = document.createElement('style'); s.id = sid; s.textContent = 'html:not(.dark), body:not(.dark) { background-color: #F8FAFC !important; color: #0F172A !important; } .dark { display: revert; }'; (document.head || document.documentElement).appendChild(s); } } catch(e) {} })();"
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PWWebViewScreen(
    initialUrl: String = DEFAULT_URL,
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf("GyanSarthi") }
    var progress by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Fullscreen video state
    var customView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }

    // File chooser callback
    var filePathCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }

    // File chooser launcher
    val fileChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results: Array<Uri>? = when {
                data?.data != null -> arrayOf(data.data!!)
                data?.clipData != null -> {
                    val clip = data.clipData!!
                    Array(clip.itemCount) { i -> clip.getItemAt(i).uri }
                }
                else -> null
            }
            filePathCallback?.onReceiveValue(results)
        } else {
            filePathCallback?.onReceiveValue(null)
        }
        filePathCallback = null
    }

    // Double-tap back to exit toast handling
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        when {
            customView != null -> {
                // Exit fullscreen video
                customViewCallback?.onCustomViewHidden()
                customView = null
                customViewCallback = null
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            webViewInstance?.canGoBack() == true -> {
                webViewInstance?.goBack()
            }
            else -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackPressTime < 2000) {
                    activity?.finish()
                } else {
                    lastBackPressTime = currentTime
                    Toast.makeText(context, "Press back again to exit PW", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Cleanup customView on dispose
    DisposableEffect(Unit) {
        onDispose {
            customViewCallback?.onCustomViewHidden()
            webViewInstance?.destroy()
        }
    }

    // Update theme when isDarkMode changes
    LaunchedEffect(isDarkMode, webViewInstance) {
        webViewInstance?.evaluateJavascript(getThemeJs(isDarkMode), null)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Full screen web view container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            if (hasError) {
                OfflineErrorView(
                    errorMessage = errorMessage,
                    onRetry = {
                        hasError = false
                        isLoading = true
                        webViewInstance?.loadUrl(currentUrl, PW_HEADERS)
                    }
                )
            } else {
                // Android WebView
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            setLayerType(View.LAYER_TYPE_HARDWARE, null)
                            overScrollMode = View.OVER_SCROLL_NEVER

                            // Setup Settings
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                mediaPlaybackRequiresUserGesture = false
                                javaScriptCanOpenWindowsAutomatically = true
                                setGeolocationEnabled(true)
                                cacheMode = WebSettings.LOAD_DEFAULT
                                allowFileAccess = true
                                allowContentAccess = true
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                defaultTextEncodingName = "utf-8"
                                userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 7 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    safeBrowsingEnabled = false
                                }
                            }

                            // Enable Cookies
                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)

                            // WebChromeClient for Fullscreen Video, Progress, File Chooser
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    progress = newProgress
                                    isLoading = newProgress < 100
                                    if (newProgress > 25) {
                                        view?.evaluateJavascript(REMOVE_TELEGRAM_JS, null)
                                    }
                                    super.onProgressChanged(view, newProgress)
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (!title.isNullOrBlank()) {
                                        val clean = title.replace("Delta Verse", "GyanSarthi", ignoreCase = true)
                                            .replace("DeltaVerse", "GyanSarthi", ignoreCase = true)
                                        if (clean.isNotBlank()) {
                                            pageTitle = clean
                                        }
                                    }
                                    super.onReceivedTitle(view, title)
                                }

                                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                    if (customView != null) {
                                        callback?.onCustomViewHidden()
                                        return
                                    }
                                    customView = view
                                    customViewCallback = callback
                                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                }

                                override fun onHideCustomView() {
                                    customView = null
                                    customViewCallback?.onCustomViewHidden()
                                    customViewCallback = null
                                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                }

                                override fun onPermissionRequest(request: PermissionRequest?) {
                                    try {
                                        request?.grant(request.resources)
                                    } catch (_: Exception) {
                                        super.onPermissionRequest(request)
                                    }
                                }

                                override fun onShowFileChooser(
                                    webView: WebView?,
                                    newFilePathCallback: ValueCallback<Array<Uri>>?,
                                    fileChooserParams: FileChooserParams?
                                ): Boolean {
                                    filePathCallback?.onReceiveValue(null)
                                    filePathCallback = newFilePathCallback

                                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "*/*"
                                    }
                                    return try {
                                        fileChooserLauncher.launch(intent)
                                        true
                                    } catch (_: Exception) {
                                        filePathCallback = null
                                        false
                                    }
                                }
                            }

                            // WebViewClient
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    currentUrl = url

                                    val lowerUrl = url.lowercase()
                                    // Block Telegram links completely
                                    if (lowerUrl.contains("t.me") || lowerUrl.contains("telegram") || lowerUrl.startsWith("tg:")) {
                                        return true
                                    }

                                    // Handle non-web schemes
                                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                        return try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            ctx.startActivity(intent)
                                            true
                                        } catch (_: Exception) {
                                            true
                                        }
                                    }

                                    val host = Uri.parse(url).host
                                    if (host?.contains("deltaverse.site") == true) {
                                        view?.loadUrl(url, PW_HEADERS)
                                        return true
                                    }
                                    return false
                                }

                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                    hasError = false
                                    view?.evaluateJavascript(REMOVE_TELEGRAM_JS, null)
                                    view?.evaluateJavascript(getThemeJs(isDarkMode), null)
                                }

                                override fun onPageCommitVisible(view: WebView?, url: String?) {
                                    super.onPageCommitVisible(view, url)
                                    view?.evaluateJavascript(REMOVE_TELEGRAM_JS, null)
                                    view?.evaluateJavascript(getThemeJs(isDarkMode), null)
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    if (url != null) {
                                        currentUrl = url
                                    }
                                    view?.evaluateJavascript(REMOVE_TELEGRAM_JS, null)
                                    view?.evaluateJavascript(getThemeJs(isDarkMode), null)
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    if (request?.isForMainFrame == true) {
                                        hasError = true
                                        errorMessage = error?.description?.toString() ?: "Network Connection Error"
                                    }
                                }

                                override fun onRenderProcessGone(
                                    view: WebView?,
                                    detail: RenderProcessGoneDetail?
                                ): Boolean {
                                    view?.let {
                                        (it.parent as? ViewGroup)?.removeView(it)
                                        it.destroy()
                                    }
                                    webViewInstance = null
                                    hasError = true
                                    errorMessage = "Display recovered. Tap Try Again to reload."
                                    return true
                                }
                            }

                            loadUrl(initialUrl, PW_HEADERS)
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        webViewInstance = view
                    }
                )
            }

            // Subtle Linear Progress Indicator at the top
            if (isLoading && progress in 1..99) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .align(Alignment.TopCenter),
                    color = Color(0xFF6366F1),
                    trackColor = Color.Transparent
                )
            }

            // Floating Light / Dark Mode Switcher Button
            Surface(
                shape = CircleShape,
                color = if (isDarkMode) Color(0xFF1E2230) else Color.White,
                shadowElevation = 6.dp,
                border = BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF374151) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp)
                    .size(46.dp)
            ) {
                IconButton(
                    onClick = {
                        onToggleTheme()
                        val toastText = if (isDarkMode) "Light Mode ☀️" else "Dark Mode 🌙"
                        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFF4F46E5),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Fullscreen Custom View (for Video lectures / HTML5 player)
        customView?.let { cView ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                factory = { _ ->
                    FrameLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        (cView.parent as? ViewGroup)?.removeView(cView)
                        addView(
                            cView,
                            FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                }
            )
        }
    }
}



@Composable
fun OfflineErrorView(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Connection Problem",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (errorMessage.isNotBlank()) errorMessage else "Unable to load PW Batches. Please check your internet or Wi-Fi connection and tap Retry.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Again")
                }
            }
        }
    }
}
