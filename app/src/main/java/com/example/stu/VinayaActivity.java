package com.example.stu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
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

public class VinayaActivity extends AppCompatActivity {

    private WebView webView;
    private LinearLayout loadingLayout;

    private SharedPreferences prefs;

    private static final String PREFS_NAME = "VinayaPrefs";
    private static final String SCROLL_KEY = "scrollPosition";
    private static final String BOOKMARK_KEY = "bookmarks_list";

    // Point directly to the Android assets directory
    private static final String ASSET_URL = "file:///android_asset/Vinaya.html";

    private int scrollTargetY = 0;

    private List<VinayaBookmark> bookmarks = new ArrayList<>();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinaya);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = findViewById(R.id.webViewVinaya);
        loadingLayout = findViewById(R.id.loadingLayout);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadBookmarks();

        setupWebView();

        int lastScrollY = prefs.getInt(SCROLL_KEY, 0);

        if (lastScrollY > 0) {

            new AlertDialog.Builder(this)
                    .setTitle("Resume Reading?")
                    .setMessage("Start from your last reading position?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        scrollTargetY = lastScrollY;
                        webView.loadUrl(ASSET_URL);

                    })
                    .setNegativeButton("No", (dialog, which) -> {

                        scrollTargetY = 0;
                        webView.loadUrl(ASSET_URL);

                    })
                    .setCancelable(false)
                    .show();

        } else {

            webView.loadUrl(ASSET_URL);

        }
    }

    // ---------------- WEBVIEW ----------------

    private void setupWebView() {

        WebSettings ws = webView.getSettings();

        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);

        // Required to allow file access to the asset folder on newer Android versions
        ws.setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view,
                                      String url,
                                      android.graphics.Bitmap favicon) {

                if (loadingLayout != null) {
                    loadingLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (loadingLayout != null) {
                    loadingLayout.setVisibility(View.GONE);
                }

                if (scrollTargetY > 0) {

                    int target = scrollTargetY;
                    scrollTargetY = 0;

                    webView.postDelayed(() ->
                                    webView.scrollTo(0, target),
                            400);

                }
            }
        });
    }

    // ---------------- MENU ----------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Add Bookmark");
        menu.add("View Bookmarks");
        menu.add("Delete Bookmark");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String title = item.getTitle().toString();

        if (title.equals("Add Bookmark")) {

            addBookmark();
            return true;

        }

        if (title.equals("View Bookmarks")) {

            showBookmarks();
            return true;

        }

        if (title.equals("Delete Bookmark")) {

            deleteBookmark();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    // ---------------- ADD BOOKMARK ----------------

    private void addBookmark() {

        EditText input = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Bookmark Name")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {

                    String name =
                            input.getText().toString().trim();

                    if (name.isEmpty()) {
                        name = "Bookmark";
                    }

                    bookmarks.add(
                            new VinayaBookmark(
                                    name,
                                    webView.getScrollY()
                            )
                    );

                    saveBookmarks();

                    Toast.makeText(
                            this,
                            "Bookmark Saved",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------- VIEW BOOKMARKS ----------------

    private void showBookmarks() {

        if (bookmarks.isEmpty()) {

            Toast.makeText(
                    this,
                    "No bookmarks saved",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String[] names = new String[bookmarks.size()];

        for (int i = 0; i < bookmarks.size(); i++) {

            names[i] = bookmarks.get(i).name;

        }

        new AlertDialog.Builder(this)
                .setTitle("My Bookmarks")
                .setItems(names, (dialog, which) -> {

                    VinayaBookmark bookmark =
                            bookmarks.get(which);

                    scrollTargetY = bookmark.scrollY;

                    webView.loadUrl(ASSET_URL);

                })
                .show();
    }

    // ---------------- DELETE BOOKMARK ----------------

    private void deleteBookmark() {

        if (bookmarks.isEmpty()) {

            Toast.makeText(
                    this,
                    "No bookmarks to delete",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String[] names = new String[bookmarks.size()];

        for (int i = 0; i < bookmarks.size(); i++) {

            names[i] = bookmarks.get(i).name;

        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Bookmark")
                .setItems(names, (dialog, which) -> {

                    bookmarks.remove(which);

                    saveBookmarks();

                    Toast.makeText(
                            this,
                            "Bookmark Deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .show();
    }

    // ---------------- STORAGE ----------------

    private void saveBookmarks() {

        String json = gson.toJson(bookmarks);

        prefs.edit()
                .putString(BOOKMARK_KEY, json)
                .apply();
    }

    private void loadBookmarks() {

        String json =
                prefs.getString(BOOKMARK_KEY, null);

        if (json != null) {

            Type type =
                    new TypeToken<List<VinayaBookmark>>() {
                    }.getType();

            bookmarks =
                    gson.fromJson(json, type);
        }
    }

    // ---------------- SAVE READING POSITION ----------------

    @Override
    protected void onPause() {
        super.onPause();

        prefs.edit()
                .putInt(
                        SCROLL_KEY,
                        webView.getScrollY()
                )
                .apply();
    }
}