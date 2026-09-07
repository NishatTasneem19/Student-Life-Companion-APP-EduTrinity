package com.example.stu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class DiaryHistory extends AppCompatActivity {

    private ListView lvHistory;
    private ArrayList<String> datesList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences prefs;
    private static final String PREFS = "DiaryPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaldiary_history);

        lvHistory = findViewById(R.id.lvHistory);
        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        datesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datesList);
        lvHistory.setAdapter(adapter);

        loadDiaryDates();

        // 1 tap -> edit entry
        lvHistory.setOnItemClickListener((parent, view, position, id) -> {
            String dateKey = "entry_" + datesList.get(position);
            String text = prefs.getString(dateKey, "");
            Diary.openDiaryForEdit(DiaryHistory.this, datesList.get(position), text);
        });

        // Long press -> delete entry
        lvHistory.setOnItemLongClickListener((parent, view, position, id) -> {
            String dateKey = "entry_" + datesList.get(position);
            new AlertDialog.Builder(DiaryHistory.this)
                    .setTitle("Delete Entry")
                    .setMessage("Do you want to delete entry for " + datesList.get(position) + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        prefs.edit().remove(dateKey).apply();
                        Toast.makeText(DiaryHistory.this, "Deleted!", Toast.LENGTH_SHORT).show();
                        loadDiaryDates();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    private void loadDiaryDates() {
        Map<String, ?> allEntries = prefs.getAll();
        datesList.clear();
        for (String key : allEntries.keySet()) {
            if (key.startsWith("entry_")) {
                datesList.add(key.replace("entry_", ""));
            }
        }
        Collections.sort(datesList, Collections.reverseOrder());
        adapter.notifyDataSetChanged();
    }
}
