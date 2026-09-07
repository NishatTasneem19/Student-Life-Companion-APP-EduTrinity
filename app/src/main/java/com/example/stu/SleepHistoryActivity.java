package com.example.stu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SleepHistoryActivity extends AppCompatActivity {

    ListView listView;
    SharedPreferences sharedPreferences;
    ArrayList<SleepRecord> records;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleephistory);

        listView = findViewById(R.id.listView);
        sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        loadAndDisplayRecords();

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showEditDeleteDialog(position);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDisplayRecords();
    }

    private void loadAndDisplayRecords() {
        sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("records", null);
        Type type = new TypeToken<ArrayList<SleepRecord>>(){}.getType();

        if(json != null) {
            records = gson.fromJson(json, type);
            if (records == null) records = new ArrayList<>();
        } else {
            records = new ArrayList<>();
        }

        ArrayList<String> displayList = new ArrayList<>();
        for(SleepRecord r : records) {
            float duration = r.getDuration();
            if(duration <= 0 || duration > 24) {
                duration = (float) SleepActivity.calculateDuration(r.getStart(), r.getEnd());
            }

            String qualityEmoji;
            if (duration < 5.0f) qualityEmoji = "⚠️ Very Low";
            else if (duration < 7.0f) qualityEmoji = "😴 Needs Improvement";
            else if (duration <= 10.0f) qualityEmoji = "🌙 Good";
            else qualityEmoji = "💤 Excessive";

            displayList.add(r.getDate() + " | 🛌 " + r.getStart() + " - ☀️ " + r.getEnd() +
                    " | ⏱️ " + String.format("%.2f", duration) + " hours | " + qualityEmoji);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                String item = displayList.get(position);

                if(item.contains("⚠️")) text.setTextColor(Color.RED);
                else if(item.contains("😴")) text.setTextColor(0xFFFFA500);
                else if(item.contains("🌙")) text.setTextColor(Color.GREEN);
                else if(item.contains("💤")) text.setTextColor(Color.BLUE);
                else text.setTextColor(Color.WHITE);

                return view;
            }
        };

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showEditDeleteDialog(int position) {
        if (records == null || records.size() <= position) return;
        SleepRecord recordToEdit = records.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Action on Record: " + recordToEdit.getDate());

        String[] options = {"Edit Record", "Delete Record"};

        builder.setItems(options, (dialog, which) -> {
            if (which == 0) showEditRecordDialog(position, recordToEdit);
            else confirmDelete(position);
        });
        builder.show();
    }

    private void confirmDelete(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this sleep record?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    records.remove(position);
                    saveRecordsToStorage();
                    loadAndDisplayRecords();
                    Toast.makeText(this, "Record deleted successfully. 🗑️", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditRecordDialog(int position, SleepRecord recordToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_record, null);
        builder.setView(dialogView);

        TextView editDate = dialogView.findViewById(R.id.editDate);
        TextView editStart = dialogView.findViewById(R.id.editStart);
        TextView editEnd = dialogView.findViewById(R.id.editEnd);

        editDate.setText(recordToEdit.getDate());
        editStart.setText(recordToEdit.getStart());
        editEnd.setText(recordToEdit.getEnd());

        editDate.setOnClickListener(v -> showDatePicker(editDate, recordToEdit));
        editStart.setOnClickListener(v -> showTimePicker(editStart, true, recordToEdit));
        editEnd.setOnClickListener(v -> showTimePicker(editEnd, false, recordToEdit));

        builder.setPositiveButton("Save Changes", (dialog, id) -> {
            String newStart = editStart.getText().toString();
            String newEnd = editEnd.getText().toString();

            double newDurationDouble = SleepActivity.calculateDuration(newStart, newEnd);
            float newDuration = (float) newDurationDouble;

            if (newDuration <= 0 || newDuration > 24) {
                Toast.makeText(this, "Error: Invalid new time entered.", Toast.LENGTH_LONG).show();
                return;
            }

            recordToEdit.setDate(editDate.getText().toString());
            recordToEdit.setStart(newStart);
            recordToEdit.setEnd(newEnd);
            recordToEdit.setDuration(newDuration);

            saveRecordsToStorage();
            loadAndDisplayRecords();
            Toast.makeText(this, "Record updated successfully. 🔄", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    private void saveRecordsToStorage() {
        sharedPreferences = getSharedPreferences("SleepData", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("records"); // remove old key
        editor.putString("records", new Gson().toJson(records));
        editor.commit(); // ✅ synchronous save
    }

    private void showDatePicker(TextView textView, SleepRecord record) {
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(record.getDate()));
        } catch (Exception e) {}

        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(newDate.getTime());
            textView.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void showTimePicker(TextView textView, boolean isStart, SleepRecord record) {
        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
            String timeString = textView.getText().toString();
            if (!timeString.isEmpty()) c.setTime(sdf.parse(timeString));
        } catch (Exception e) {}

        TimePickerDialog tp = new TimePickerDialog(this, (view, h, m) -> {
            String time = String.format(Locale.US, "%02d:%02d %s",
                    (h==0||h==12)?12:(h%12), m, (h>=12)?"PM":"AM");
            textView.setText(time);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
        tp.show();
    }
}
