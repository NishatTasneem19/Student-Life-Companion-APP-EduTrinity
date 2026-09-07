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

public class Bible extends AppCompatActivity {

    private WebView webView;
    private LinearLayout loadingLayout;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "BiblePrefs";
    private static final String SCROLL_KEY = "scrollPosition";
    private static final String BOOKMARK_KEY = "bookmarks_list";

    private static final String GITHUB_URL =
            "https://forasgit1223.github.io/holybook_assets/bible.html";

    private int scrollTargetY = 0;

    private List<biblebookmarks> bookmarks = new ArrayList<>();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = findViewById(R.id.webViewBible);
        loadingLayout = findViewById(R.id.loadingLayout);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadBookmarks();

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        ws.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view,
                                      String url,
                                      android.graphics.Bitmap favicon) {

                loadingLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);

                loadingLayout.setVisibility(View.GONE);

                if (scrollTargetY > 0) {

                    webView.postDelayed(() -> {

                        webView.scrollTo(0, scrollTargetY);
                        scrollTargetY = 0;

                    }, 300);
                }
            }
        });

        int lastScrollY = prefs.getInt(SCROLL_KEY, 0);

        if (lastScrollY > 0) {

            new AlertDialog.Builder(this)
                    .setTitle("Resume Reading?")
                    .setMessage("Continue from your last reading position?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        scrollTargetY = lastScrollY;
                        webView.loadUrl(GITHUB_URL);

                    })
                    .setNegativeButton("No", (dialog, which) -> {

                        scrollTargetY = 0;
                        webView.loadUrl(GITHUB_URL);

                    })
                    .setCancelable(false)
                    .show();

        } else {

            webView.loadUrl(GITHUB_URL);
        }
    }

    // ---------------- MENU ----------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bible_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_add_bookmark) {
            addBookmark();
            return true;
        }

        if (id == R.id.menu_view_bookmarks) {
            showBookmarks();
            return true;
        }

        if (id == R.id.menu_delete_bookmark) {
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

                    String name = input.getText().toString().trim();

                    if (name.isEmpty()) {
                        name = "Bookmark";
                    }

                    bookmarks.add(
                            new biblebookmarks(
                                    name,
                                    webView.getScrollY()
                            )
                    );

                    saveBookmarks();

                    Toast.makeText(
                            this,
                            "Bookmark saved",
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
                    "No bookmarks found",
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

                    biblebookmarks bookmark = bookmarks.get(which);

                    scrollTargetY = bookmark.scrollY;

                    webView.loadUrl(GITHUB_URL);

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
                            "Bookmark deleted",
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

        String json = prefs.getString(BOOKMARK_KEY, null);

        if (json == null) {
            bookmarks = new ArrayList<>();
            return;
        }

        Type type = new TypeToken<List<biblebookmarks>>() {}.getType();

        List<biblebookmarks> loadedBookmarks =
                gson.fromJson(json, type);

        if (loadedBookmarks != null) {
            bookmarks = loadedBookmarks;
        } else {
            bookmarks = new ArrayList<>();
        }
    }

    // ---------------- SAVE READING POSITION ----------------

    @Override
    protected void onPause() {

        super.onPause();

        prefs.edit()
                .putInt(SCROLL_KEY, webView.getScrollY())
                .apply();
    }
}