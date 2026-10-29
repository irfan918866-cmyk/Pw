package com.example.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.verification.ArolinksVerificationManager
import kotlinx.coroutines.launch

@Composable
fun ArolinksVerificationGate(
    isDarkMode: Boolean,
    hasExpiredPreviously: Boolean,
    onVerificationSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isGeneratingLink by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var activeVerificationUrl by remember { mutableStateOf<String?>(null) }

    if (activeVerificationUrl != null) {
        ArolinksVerifierWebView(
            verificationUrl = activeVerificationUrl!!,
            onCancel = {
                activeVerificationUrl = null
            },
            onVerified = {
                activeVerificationUrl = null
                Toast.makeText(
                    context,
                    "Verification Complete! Unlocked for 24 Hours.",
                    Toast.LENGTH_LONG
                ).show()
                onVerificationSuccess()
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isDarkMode) {
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F1117)),
                            radius = 1200f
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFEEF2FF), Color(0xFFF8FAFC)),
                            radius = 1200f
                        )
                    }
                )
                .statusBarsPadding()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF181C26) else Color.White
                ),
                border = BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF6366F1).copy(alpha = 0.35f) else Color(0xFFE0E7FF)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glowing Logo Ring
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899))
                                )
                            )
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pw_logo),
                            contentDescription = "PW GyanSarthi Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFEF4444).copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isDarkMode) Color(0xFFF87171) else Color(0xFFDC2626),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasExpiredPreviously) {
                                    "24H ACCESS EXPIRED — VERIFY AGAIN"
                                } else {
                                    "VERIFICATION REQUIRED"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFFF87171) else Color(0xFFDC2626),
                                letterSpacing = 0.4.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Pw GyanSarthi Access",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Complete a quick 1-step Arolinks verification to unlock all Study Batches, Video Lectures & Notes for 24 Hours on your phone.",
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // 3 Steps Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDarkMode) Color(0xFF0F1117).copy(alpha = 0.65f) else Color(0xFFF1F5F9),
                        border = BorderStroke(
                            1.dp,
                            if (isDarkMode) Color(0xFF2D3748) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            VerificationStepRow(
                                number = "1",
                                text = "Tap Verify Now button below",
                                isDarkMode = isDarkMode
                            )
                            HorizontalDivider(
                                color = if (isDarkMode) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.06f)
                            )
                            VerificationStepRow(
                                number = "2",
                                text = "Complete the short Arolinks verification page",
                                isDarkMode = isDarkMode
                            )
                            HorizontalDivider(
                                color = if (isDarkMode) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.06f)
                            )
                            VerificationStepRow(
                                number = "3",
                                text = "Automatically unlock Pw GyanSarthi for 24 Hours",
                                isDarkMode = isDarkMode
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Verify Now CTA Button
                    Button(
                        onClick = {
                            errorMessage = null
                            isGeneratingLink = true
                            scope.launch {
                                val result = ArolinksVerificationManager.createVerificationLink(context)
                                isGeneratingLink = false
                                result.onSuccess { shortUrl ->
                                    activeVerificationUrl = shortUrl
                                }.onFailure { err ->
                                    errorMessage = err.message ?: "Unable to generate verification link."
                                }
                            }
                        },
                        enabled = !isGeneratingLink,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("verify_now_button")
                    ) {
                        if (isGeneratingLink) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Generating Verification Link...",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verify Now — Unlock 24 Hours",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFF87171),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Access automatically stays unlocked for 24 hours after verification.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun VerificationStepRow(
    number: String,
    text: String,
    isDarkMode: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isDarkMode) Color(0xFF6366F1).copy(alpha = 0.2f) else Color(0xFFE0E7FF)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color(0xFF818CF8) else Color(0xFF4F46E5)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun ArolinksVerifierWebView(
    verificationUrl: String,
    onCancel: () -> Unit,
    onVerified: () -> Unit
) {
    val context = LocalContext.current
    var progress by remember { mutableIntStateOf(0) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    BackHandler {
        if (webViewRef?.canGoBack() == true) {
            webViewRef?.goBack()
        } else {
            onCancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1117))
            .statusBarsPadding()
    ) {
        // Top Verification Bar
        Surface(
            color = Color(0xFF181C26),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Complete 24h Verification",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "App unlocks automatically on completion",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.testTag("close_verifier_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Verification",
                        tint = Color.White
                    )
                }
            }
        }

        if (progress in 1..99) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = Color(0xFF10B981),
                trackColor = Color.Transparent
            )
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        javaScriptCanOpenWindowsAutomatically = false
                        setSupportMultipleWindows(false)
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString =
                            "Mozilla/5.0 (Linux; Android 14; Pixel 7 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            safeBrowsingEnabled = false
                        }
                    }

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            progress = newProgress
                            super.onProgressChanged(view, newProgress)
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false
                            if (ArolinksVerificationManager.verifyCallbackUrlIfMatch(ctx, url)) {
                                onVerified()
                                return true
                            }
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                return true
                            }
                            return false
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            if (url != null && ArolinksVerificationManager.verifyCallbackUrlIfMatch(ctx, url)) {
                                onVerified()
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            if (url != null && ArolinksVerificationManager.verifyCallbackUrlIfMatch(ctx, url)) {
                                onVerified()
                            }
                        }

                        override fun onRenderProcessGone(
                            view: WebView?,
                            detail: android.webkit.RenderProcessGoneDetail?
                        ): Boolean {
                            view?.let {
                                (it.parent as? ViewGroup)?.removeView(it)
                                it.destroy()
                            }
                            webViewRef = null
                            onCancel()
                            return true
                        }
                    }

                    loadUrl(verificationUrl)
                    webViewRef = this
                }
            },
            update = { view ->
                webViewRef = view
            }
        )
    }
}
