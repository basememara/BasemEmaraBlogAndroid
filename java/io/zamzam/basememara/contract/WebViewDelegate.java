package io.zamzam.basememara.contract;

import android.webkit.WebView;

/**
 * Created by basem on 7/6/15.
 */
public interface WebViewDelegate {

    public boolean shouldOverrideUrlLoading(WebView view, String url);

}
