package com.sanjay.bsebmath10;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.Locale;

public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.addJavascriptInterface(this, "Android");
        setContentView(web);
        web.loadUrl("file:///android_asset/index.html");
        Reminder.schedule(this);
    }

    // Re-render so the reminder card reflects permission changes made in system settings.
    @Override
    protected void onResume() {
        super.onResume();
        web.evaluateJavascript("typeof render === 'function' && render()", null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        web.evaluateJavascript("typeof render === 'function' && render()", null);
    }

    @JavascriptInterface
    public String getReminder() {
        SharedPreferences p = Reminder.prefs(this);
        if (!p.getBoolean("on", false)) return "";
        return String.format(Locale.US, "%02d:%02d", p.getInt("hour", 19), p.getInt("minute", 0));
    }

    @JavascriptInterface
    public void setReminder(int hour, int minute) {
        Reminder.prefs(this).edit().putBoolean("on", true).putInt("hour", hour).putInt("minute", minute).apply();
        Reminder.cancel(this);
        Reminder.schedule(this);
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            runOnUiThread(() -> requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1));
        }
    }

    @JavascriptInterface
    public void cancelReminder() {
        Reminder.prefs(this).edit().putBoolean("on", false).apply();
        Reminder.cancel(this);
    }

    @JavascriptInterface
    public boolean notificationsAllowed() {
        if (Build.VERSION.SDK_INT < 24) return true;
        return ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).areNotificationsEnabled();
    }

    @JavascriptInterface
    public boolean isNight() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    @JavascriptInterface
    public void share(String text) {
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(send, null));
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
