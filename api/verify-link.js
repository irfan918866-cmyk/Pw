export default async function handler(req, res) {
  // Enable CORS
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  const apiToken = process.env.AROLINKS_API_TOKEN;
  const targetUrl = req.query.url || (req.body && req.body.url);

  if (!targetUrl) {
    return res.status(400).json({
      status: 'error',
      message: 'Missing callback URL parameter.'
    });
  }

  if (!apiToken || apiToken === 'YOUR_AROLINKS_API_TOKEN') {
    return res.status(500).json({
      status: 'error',
      message: 'AROLINKS_API_TOKEN is not configured in Vercel Environment Variables (Settings -> Environment Variables).'
    });
  }

  try {
    const apiEndpoint = `https://arolinks.com/api?api=${encodeURIComponent(apiToken)}&url=${encodeURIComponent(targetUrl)}&format=json`;
    const response = await fetch(apiEndpoint, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'User-Agent': 'PW-GyanSarthi-PWA/1.0'
      }
    });

    const data = await response.json();

    if (data && (data.shortenedUrl || data.short_url)) {
      return res.status(200).json({
        status: 'success',
        shortenedUrl: data.shortenedUrl || data.short_url
      });
    }

    // Fallback to Arolinks Quick Link endpoint
    const quickLink = `https://arolinks.com/st?api=${encodeURIComponent(apiToken)}&url=${encodeURIComponent(targetUrl)}`;
    return res.status(200).json({
      status: 'success',
      shortenedUrl: quickLink,
      message: data?.message || 'Using direct shortener redirect'
    });
  } catch (error) {
    const fallbackUrl = `https://arolinks.com/st?api=${encodeURIComponent(apiToken)}&url=${encodeURIComponent(targetUrl)}`;
    return res.status(200).json({
      status: 'success',
      shortenedUrl: fallbackUrl
    });
  }
}
