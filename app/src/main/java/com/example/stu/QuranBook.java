package com.example.stu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuranBook extends AppCompatActivity {

    WebView webView;
    Button btnPrev, btnNext;
    LinearLayout loadingLayout;

    int currentPart = 0;
    int pendingScroll = -1;

    // ⭐ Resume keys
    private static final String LAST_PART_KEY = "last_part";
    private static final String LAST_SCROLL_KEY = "last_scroll";

    String[] parts = {
            "https://forasgit1223.github.io/holybook_assets/quran1.html",
            "https://forasgit1223.github.io/holybook_assets/quran2.html",
            "https://forasgit1223.github.io/holybook_assets/quran3.html",
            "https://forasgit1223.github.io/holybook_assets/quran4.html",
            "https://forasgit1223.github.io/holybook_assets/quran5.html"
    };

    SharedPreferences prefs;
    Gson gson = new Gson();

    List<Bookmark> bookmarks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quranbook);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = findViewById(R.id.webView);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        loadingLayout = findViewById(R.id.loadingLayout);

        prefs = getSharedPreferences("QURAN_APP", MODE_PRIVATE);

        loadBookmarks();
        setupWebView();

        // ⭐ LOAD LAST SESSION DIALOG
        int lastPart = prefs.getInt(LAST_PART_KEY, -1);
        int lastScroll = prefs.getInt(LAST_SCROLL_KEY, 0);

        // If a valid saved part exists, ask the user if they want to resume
        if (lastPart != -1) {
            new AlertDialog.Builder(this)
                    .setTitle("Resume reading?")
                    .setMessage("Do you want to continue from your last read position?")
                    .setPositiveButton("Yes", (d, w) -> {
                        currentPart = lastPart;
                        pendingScroll = lastScroll;
                        loadPart();
                    })
                    .setNegativeButton("No", (d, w) -> {
                        currentPart = 0;
                        pendingScroll = 0;
                        loadPart();
                    })
                    .setCancelable(false)
                    .show();
        } else {
            loadPart();
        }

        btnNext.setOnClickListener(v -> {
            if (currentPart < parts.length - 1) {
                saveScroll();
                currentPart++;
                loadPart();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPart > 0) {
                saveScroll();
                currentPart--;
                loadPart();
            }
        });

        smartCacheCleanup();
    }

    // ⭐ Save position whenever the user minimizes, rotates, or leaves the app
    @Override
    protected void onPause() {
        super.onPause();
        saveSession();
    }

    // ---------------- WEBVIEW ----------------

    private void setupWebView() {
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);

        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);

        webView.clearCache(true);
        webView.clearHistory();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                loadingLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadingLayout.setVisibility(View.GONE);

                if (pendingScroll != -1) {
                    int scroll = pendingScroll;
                    pendingScroll = -1;
                    view.postDelayed(() -> view.scrollTo(0, scroll), 250);
                } else {
                    int saved = prefs.getInt("scroll_" + currentPart, 0);
                    view.postDelayed(() -> view.scrollTo(0, saved), 100);
                }
            }
        });
    }

    // ---------------- LOAD PART ----------------

    private void loadPart() {
        webView.loadUrl(parts[currentPart]);
        updateButtons();
        preloadNext();
    }

    private void preloadNext() {
        if (currentPart < parts.length - 1) {
            new Thread(() -> {
                try {
                    java.net.URL url = new java.net.URL(parts[currentPart + 1]);
                    url.openStream().close();
                } catch (Exception ignored) {}
            }).start();
        }
    }

    private void updateButtons() {
        btnPrev.setVisibility(currentPart == 0 ? View.INVISIBLE : View.VISIBLE);
        btnNext.setVisibility(currentPart == parts.length - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    // ---------------- CACHE CLEANUP ----------------

    private void smartCacheCleanup() {
        webView.postDelayed(() -> {
            webView.clearCache(true);
            deleteCache(getCacheDir());
        }, 2000);
    }

    private void deleteCache(java.io.File dir) {
        if (dir != null && dir.isDirectory()) {
            java.io.File[] children = dir.listFiles();
            if (children != null) {
                for (java.io.File child : children) {
                    if (child.isDirectory()) deleteCache(child);
                    else child.delete();
                }
            }
        }
    }

    // ---------------- MENU ----------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quran_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_bookmark) {
            addBookmark();
            return true;
        }

        if (item.getItemId() == R.id.menu_view_bookmarks) {
            showBookmarks();
            return true;
        }

        if (item.getItemId() == R.id.menu_delete_bookmark) {
            deleteBookmark();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ---------------- ADD BOOKMARK ----------------

    private void addBookmark() {
        android.widget.EditText input = new android.widget.EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Bookmark Name")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) name = "Bookmark";

                    bookmarks.add(new Bookmark(name, currentPart, webView.getScrollY()));
                    saveBookmarks();

                    Toast.makeText(this, "Bookmark saved", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // ---------------- VIEW BOOKMARK ----------------

    private void showBookmarks() {
        if (bookmarks.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("No bookmarks saved")
                    .show();
            return;
        }

        String[] names = new String[bookmarks.size()];
        for (int i = 0; i < bookmarks.size(); i++) {
            names[i] = bookmarks.get(i).name;
        }

        new AlertDialog.Builder(this)
                .setTitle("My Bookmarks")
                .setItems(names, (d, which) -> {
                    Bookmark b = bookmarks.get(which);
                    currentPart = b.part;
                    pendingScroll = b.scrollY;
                    loadPart();
                })
                .show();
    }

    // ---------------- DELETE BOOKMARK ----------------

    private void deleteBookmark() {
        if (bookmarks.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("No bookmarks to delete")
                    .show();
            return;
        }

        String[] names = new String[bookmarks.size()];
        for (int i = 0; i < bookmarks.size(); i++) {
            names[i] = bookmarks.get(i).name;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Bookmark")
                .setItems(names, (d, which) -> {
                    bookmarks.remove(which);
                    saveBookmarks();
                    Toast.makeText(this, "Bookmark deleted", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // ---------------- STORAGE ----------------

    private void saveBookmarks() {
        prefs.edit()
                .putString("bookmarks", gson.toJson(bookmarks))
                .apply();
        saveSession(); // updates session values as well
    }

    private void loadBookmarks() {
        String json = prefs.getString("bookmarks", null);
        if (json != null) {
            Type type = new TypeToken<List<Bookmark>>() {}.getType();
            bookmarks = gson.fromJson(json, type);
        }
    }

    // ---------------- SAVE SCROLL / SESSION ----------------

    private void saveScroll() {
        if (webView != null) {
            prefs.edit()
                    .putInt("scroll_" + currentPart, webView.getScrollY())
                    .apply();
        }
        saveSession();
    }

    // ⭐ Centralized method to save the last session state
    private void saveSession() {
        if (webView != null) {
            prefs.edit()
                    .putInt(LAST_PART_KEY, currentPart)
                    .putInt(LAST_SCROLL_KEY, webView.getScrollY())
                    .apply();
        }
    }
}