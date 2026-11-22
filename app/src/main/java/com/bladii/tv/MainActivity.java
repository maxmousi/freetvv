package com.bladii.tv;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.FrameLayout;
import java.io.ByteArrayInputStream;

public class MainActivity extends Activity {

    // ===== عدّل هذه القيم =====
    static final String HOME = "https://bladii-sport.live/";
    static final String HOST = "bladii-sport.live";
    // نطاقات الإعلانات المحجوبة (أضف المزيد حسب ما تجده في F12 > Network)
    static final String[] BLOCKED = {
        "doubleclick.net", "googlesyndication.com", "googleadservices.com",
        "adsterra.com", "popads.net", "propellerads.com", "adsboosters.xyz",
        "exoclick.com", "juicyads.com", "clickadu.com", "hilltopads.net",
        "onclickads.net", "popcash.net", "adcash.com", "mgid.com", "taboola.com"
    };
    // ==========================

    private FrameLayout root;
    private WebView web;
    private View customView;
    private WebChromeClient.CustomViewCallback customCb;

    private static final String HIDE_ADS_JS =
        "(function(){var s=document.createElement('style');" +
        "s.innerHTML='[id*=\"google_ads\"],[class*=\"adsbygoogle\"],[id^=\"ad-\"],[class^=\"ad-\"]," +
        "[class*=\"popup\"],[id*=\"popup\"],ins.adsbygoogle{display:none!important}';" +
        "document.head.appendChild(s);})();";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        web = new WebView(this);
        root.addView(web, new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);

        WebSettings st = web.getSettings();
        st.setJavaScriptEnabled(true);
        st.setDomStorageEnabled(true);
        st.setMediaPlaybackRequiresUserGesture(false);
        st.setSupportMultipleWindows(false);
        st.setJavaScriptCanOpenWindowsAutomatically(false);
        st.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        web.requestFocus();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                String h = r.getUrl().getHost();
                if (h != null) {
                    for (String d : BLOCKED) {
                        if (h.equals(d) || h.endsWith("." + d)) {
                            return new WebResourceResponse("text/plain", "utf-8",
                                new ByteArrayInputStream(new byte[0]));
                        }
                    }
                }
                return null;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                // يمنع التحويل إلى مواقع أخرى (إعلانات منبثقة)
                String h = r.getUrl().getHost();
                return h == null || !(h.equals(HOST) || h.endsWith("." + HOST));
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                v.evaluateJavascript(HIDE_ADS_JS, null);
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback cb) {
                if (customView != null) { cb.onCustomViewHidden(); return; }
                customView = view; customCb = cb;
                web.setVisibility(View.GONE);
                root.addView(view, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onHideCustomView() {
                if (customView == null) return;
                root.removeView(customView);
                customView = null;
                web.setVisibility(View.VISIBLE);
                if (customCb != null) customCb.onCustomViewHidden();
            }

            @Override
            public boolean onCreateWindow(WebView v, boolean d, boolean u, android.os.Message m) {
                return false; // حجب النوافذ المنبثقة
            }
        });

        web.loadUrl(HOME);
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            web.getWebChromeClient().onHideCustomView();
        } else if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override protected void onPause() { super.onPause(); web.onPause(); }
    @Override protected void onResume() { super.onResume(); web.onResume(); }
}
