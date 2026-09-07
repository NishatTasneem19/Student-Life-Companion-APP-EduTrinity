package com.example.stu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoodHistory extends AppCompatActivity implements HistoryAdapter.ItemCallbacks {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodhistory);

        recyclerView = findViewById(R.id.historyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter(DiaryStore.load(this), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<DiaryEntry> fresh = DiaryStore.load(this);
        adapter.submitList(fresh);
    }


    @Override
    public void onItemClick(int position, DiaryEntry item) {
        Intent i = new Intent(this, mood.class);
        i.putExtra("is_edit", true);
        i.putExtra("edit_index", position);
        i.putExtra("edit_mood", item.mood);
        i.putExtra("edit_desc", item.description);
        startActivity(i);
    }


    @Override
    public void onItemLongClick(int position, DiaryEntry item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry?")
                .setMessage(item.timestamp + "\nMood: " + item.mood)
                .setPositiveButton("Delete", (d, w) -> {
                    DiaryStore.deleteAt(this, position);
                    adapter.submitList(DiaryStore.load(this));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
