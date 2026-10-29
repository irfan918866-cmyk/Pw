# PW GyanSarthi - PWA (Vercel Ready with 24h Arolinks Verification)

Progressive Web App (PWA) for **PW GyanSarthi Study Batches Portal** with a **24-Hour Arolinks Verification Gate**.

## Features Included
- 🔐 **24-Hour Arolinks Verification System**: Users must complete 1-step Arolinks verification to unlock the PWA for 24 hours. After 24 hours, the timer automatically expires and prompts for re-verification.
- 🛡️ **Serverless Token Security (`/api/verify-link`)**: Keeps your `AROLINKS_API_TOKEN` hidden on the server via Vercel Environment Variables.
- 📱 **1-Tap Add to Home Screen**: Includes `manifest.json` with PW icon and auto-install prompt.
- ☀️🌙 **Light & Dark Mode**: Floating toggle with automatic system theme sync and persistence.
- ⚡ **Offline Caching**: Built-in Service Worker (`sw.js`).

---

## How to Deploy to Vercel:

### Step 1: Set Environment Variable on Vercel
In your Vercel Project Dashboard:
1. Go to **Settings** -> **Environment Variables**.
2. Add a new variable:
   - **Key**: `AROLINKS_API_TOKEN`
   - **Value**: *(Your Arolinks API Token)*
3. Click **Save**.

### Step 2: Deploy
- **Using Vercel CLI:**
  ```bash
  cd pwa-vercel
  npx vercel --prod
  ```
- **Or Using GitHub:**
  Import your repository into [vercel.com](https://vercel.com), set Root Directory to `pwa-vercel`, add `AROLINKS_API_TOKEN` in Environment Variables, and click **Deploy**.
