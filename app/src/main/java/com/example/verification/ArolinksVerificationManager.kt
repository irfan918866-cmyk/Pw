package com.example.verification

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder
import java.util.UUID
import java.util.concurrent.TimeUnit

object ArolinksVerificationManager {
    private const val PREFS_NAME = "pw_arolinks_verification"
    private const val KEY_VERIFIED_UNTIL = "verified_until_ms"
    private const val KEY_PENDING_TOKEN = "pending_verify_token"
    private const val CALLBACK_BASE_URL = "https://pw.deltaverse.site/study/batches"

    const val TWENTY_FOUR_HOURS_MS = 24 * 60 * 60 * 1000L // 24 hours in milliseconds

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getVerifiedUntil(context: Context): Long {
        return getPrefs(context).getLong(KEY_VERIFIED_UNTIL, 0L)
    }

    fun isUnlocked(context: Context): Boolean {
        val verifiedUntil = getVerifiedUntil(context)
        return System.currentTimeMillis() < verifiedUntil
    }

    fun getRemainingMillis(context: Context): Long {
        val remaining = getVerifiedUntil(context) - System.currentTimeMillis()
        return remaining.coerceAtLeast(0L)
    }

    fun formatRemainingTime(remainingMs: Long): String {
        val totalSeconds = (remainingMs / 1000L).coerceAtLeast(0L)
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L
        return if (hours > 0) {
            String.format("%dh %02dm left", hours, minutes)
        } else {
            String.format("%02dm %02ds left", minutes, seconds)
        }
    }

    fun getPendingToken(context: Context): String? {
        return getPrefs(context).getString(KEY_PENDING_TOKEN, null)
    }

    fun verifyCallbackUrlIfMatch(context: Context, url: String): Boolean {
        val pendingToken = getPendingToken(context)
        if (!pendingToken.isNullOrBlank() && url.contains("verify_token=$pendingToken")) {
            markVerifiedFor24Hours(context)
            return true
        }
        return false
    }

    fun markVerifiedFor24Hours(context: Context) {
        val unlockUntil = System.currentTimeMillis() + TWENTY_FOUR_HOURS_MS
        getPrefs(context).edit()
            .putLong(KEY_VERIFIED_UNTIL, unlockUntil)
            .remove(KEY_PENDING_TOKEN)
            .apply()
    }

    suspend fun createVerificationLink(context: Context): Result<String> = withContext(Dispatchers.IO) {
        val apiToken = BuildConfig.AROLINKS_API_TOKEN.trim()
        if (apiToken.isEmpty() || apiToken == "YOUR_AROLINKS_API_TOKEN") {
            return@withContext Result.failure(
                IllegalStateException("AROLINKS_API_TOKEN is not configured in the AI Studio Secrets panel.")
            )
        }

        val token = "pw_" + UUID.randomUUID().toString().replace("-", "").take(16)
        getPrefs(context).edit().putString(KEY_PENDING_TOKEN, token).apply()

        val callbackUrl = "$CALLBACK_BASE_URL?verify_token=$token"
        val encodedCallback = URLEncoder.encode(callbackUrl, "UTF-8")
        val encodedApi = URLEncoder.encode(apiToken, "UTF-8")

        val apiUrl = "https://arolinks.com/api?api=$encodedApi&url=$encodedCallback&format=json"

        try {
            val request = Request.Builder()
                .url(apiUrl)
                .header("Accept", "application/json")
                .header("User-Agent", "PW-GyanSarthi/1.0")
                .get()
                .build()

            httpClient.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string().orEmpty()
                if (response.isSuccessful && bodyStr.isNotBlank()) {
                    val json = JSONObject(bodyStr)
                    val shortenedUrl = json.optString("shortenedUrl").ifBlank {
                        json.optString("short_url")
                    }
                    if (shortenedUrl.isNotBlank() && shortenedUrl.startsWith("http")) {
                        return@withContext Result.success(shortenedUrl)
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback to Arolinks Quick Link redirect below
        }

        // Fallback to Arolinks Quick Link endpoint
        val quickLink = "https://arolinks.com/st?api=$encodedApi&url=$encodedCallback"
        Result.success(quickLink)
    }
}
