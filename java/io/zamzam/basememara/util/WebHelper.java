package io.zamzam.basememara.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.common.base.Strings;

import io.zamzam.basememara.R;
import io.zamzam.basememara.contract.AlertDialogDelegate;
import io.zamzam.basememara.contract.WebViewDelegate;

/**
 * Created by basem on 7/7/15.
 */
public class WebHelper {

    public static AlertDialog openUrl(final Context context, String url, final String title, final WebViewDelegate webViewDelegate, final AlertDialogDelegate alertDialogDelegate, boolean enableBack, boolean enableOpenInBrowser, final int metaId) {
        final ProgressDialog spinnerDialog = AlertHelper.createSpinnerDialog(context);
        final WebView webView = new WebView(context);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(webView)
                .setPositiveButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        if (enableBack) {
            dialogBuilder.setNegativeButton(context.getString(R.string.back), null); // Will override later
        }

        if (enableOpenInBrowser) {
            dialogBuilder.setNeutralButton(context.getString(R.string.open_in_browser), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    openBrowser(context, webView.getUrl());
                    dialog.dismiss();
                }
            });
        } else if (alertDialogDelegate != null) {
            dialogBuilder.setNeutralButton(alertDialogDelegate.getNeutralText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    alertDialogDelegate.onNeutralClick(dialog, metaId);
                }
            });
        }

        final AlertDialog dialog = dialogBuilder.create();

        if (enableBack) {
            // Override button click to keep open after web view back button pressed
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface d) {
                    Button backButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (webView.canGoBack()) {
                                webView.goBack();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (webViewDelegate != null) {
                    webViewDelegate.shouldOverrideUrlLoading(view, url);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                spinnerDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                spinnerDialog.dismiss();
            }
        });
        webView.loadUrl(url);

        dialog.show();

        return dialog;
    }
    public static AlertDialog openUrl(final Context context, String url, final String title, final WebViewDelegate webViewDelegate, final AlertDialogDelegate alertDialogDelegate, boolean enableBack, boolean enableOpenInBrowser) {
        return openUrl(context, url, title, webViewDelegate, alertDialogDelegate, enableBack, enableOpenInBrowser, 0);
    }

    public static AlertDialog openUrl(final Context context, String url, final String title, final WebViewDelegate webViewDelegate, boolean enableBack, boolean enableOpenInBrowser) {
        return openUrl(context, url, title, webViewDelegate, null, enableBack, enableOpenInBrowser);
    }

    public static AlertDialog openUrl(final Context context, String url, final String title, final WebViewDelegate webViewDelegate, final AlertDialogDelegate alertDialogDelegate, boolean enableBack) {
        return openUrl(context, url, title, webViewDelegate, alertDialogDelegate, enableBack, false);
    }

    public static AlertDialog openUrl(Context context, String url) {
        return openUrl(context, url, true, true);
    }

    public static AlertDialog openUrl(Context context, String url, String title) {
        return openUrl(context, url, title, true, true);
    }

    public static AlertDialog openUrl(final Context context, String url, WebViewDelegate webViewDelegate) {
        return openUrl(context, url, webViewDelegate, true, true);
    }

    public static AlertDialog openUrl(Context context, String url, boolean enableBack, boolean enableOpenInBrowser) {
        return openUrl(context, url, null, null, enableBack, enableOpenInBrowser);
    }

    public static AlertDialog openUrl(Context context, String url, String title, boolean enableBack, boolean enableOpenInBrowser) {
        return openUrl(context, url, title, null, enableBack, enableOpenInBrowser);
    }

    public static AlertDialog openUrl(final Context context, String url, WebViewDelegate webViewDelegate, boolean enableBack, boolean enableOpenInBrowser) {
        return openUrl(context, url, null, webViewDelegate, enableBack, enableOpenInBrowser);
    }

    public static AlertDialog openHtml(final Context context, String html, final String title, final WebViewDelegate webViewDelegate, final AlertDialogDelegate alertDialogDelegate, boolean enableBack, final int metaId) {
        final ProgressDialog spinnerDialog = AlertHelper.createSpinnerDialog(context);
        final WebView webView = new WebView(context);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(webView)
                .setPositiveButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        if (enableBack) {
            dialogBuilder.setNegativeButton(context.getString(R.string.back), null); // Will override later
        }

        if (alertDialogDelegate != null) {
            dialogBuilder.setNeutralButton(alertDialogDelegate.getNeutralText(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    alertDialogDelegate.onNeutralClick(dialog, metaId);
                }
            });
        }

        final AlertDialog dialog = dialogBuilder.create();

        if (enableBack) {
            // Override button click to keep open after web view back button pressed
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface d) {
                    Button backButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (webView.canGoBack()) {
                                webView.goBack();
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (webViewDelegate != null) {
                    webViewDelegate.shouldOverrideUrlLoading(view, url);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                spinnerDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                spinnerDialog.dismiss();
            }
        });
        webView.loadDataWithBaseURL(String.valueOf(context.getResources().getText(R.string.base_url)),
                html, "text/html", "utf-8", null);

        dialog.show();

        return dialog;
    }

    public static void openBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void email(Context context, String subject, String body, String mailTo) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (!Strings.isNullOrEmpty(mailTo)) {
            intent.putExtra(Intent.EXTRA_TEXT, body);
        }

        if (!Strings.isNullOrEmpty(mailTo)) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo});
        }

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void email(Context context, String subject, String body) {
        email(context, subject, body, null);
    }

    public static void email(Context context, String subject) {
        email(context, subject, null, null);
    }

}
