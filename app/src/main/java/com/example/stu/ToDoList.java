package com.example.stu;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class ToDoList extends AppCompatActivity {

    private ImageButton btnAddTask;
    private RecyclerView recycler;
    private ImageView bgEmpty, bgTasks;
    private TextView tvCompletedHeader;

    private final ArrayList<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private SharedPreferences prefs;

    private static final String[] CATEGORIES = {
            "Study", "Exam", "Assignment", "Project", "Lab Submission"
    };

    private NotificationAlarmScheduler notificationAlarmScheduler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        btnAddTask = findViewById(R.id.btnAddTask);
        recycler = findViewById(R.id.recyclerTasks);
        tvCompletedHeader = findViewById(R.id.tvCompletedHeader);
        bgEmpty = findViewById(R.id.bgEmpty);
        bgTasks = findViewById(R.id.bgTasks);

        prefs = getSharedPreferences("ToDoPrefs", MODE_PRIVATE);
        notificationAlarmScheduler = new NotificationAlarmScheduler(this);

        // Notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "todo_channel",
                    "To-Do Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Reminders for To-Do tasks");
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(channel);
        }

        adapter = new TaskAdapter(tasks, new TaskAdapter.Listener() {
            @Override
            public void onEdit(int position) {
                showAddOrEditDialog(position);
            }

            @Override
            public void onToggleComplete(int position) {
                Task t = tasks.get(position);
                t.completed = !t.completed;
                resortTasks();
                saveTasks();
                refreshBackground();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // Swipe left to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView rv,
                                  RecyclerView.ViewHolder vh,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getAdapterPosition();
                tasks.remove(pos);
                adapter.notifyItemRemoved(pos);
                saveTasks();
                refreshBackground();
            }
        }).attachToRecyclerView(recycler);

        btnAddTask.setOnClickListener(v -> showAddOrEditDialog(-1));

        loadTasks();
        resortTasks();
        refreshBackground();
    }

    private void showAddOrEditDialog(int editPosition) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_task, null);

        EditText etTitle = view.findViewById(R.id.etTaskTitle);
        EditText etDesc = view.findViewById(R.id.etTaskDescription);
        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ImageButton btnSetReminder = view.findViewById(R.id.btnSetReminder);
        TextView tvReminderPreview = view.findViewById(R.id.tvReminderPreview);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        spinnerCategory.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                CATEGORIES
        ));

        final Calendar[] picked = {null};

        if (editPosition >= 0) {
            Task t = tasks.get(editPosition);
            etTitle.setText(t.title);
            etDesc.setText(t.description);
            spinnerCategory.setSelection(
                    Math.max(Arrays.asList(CATEGORIES).indexOf(t.category), 0)
            );

            if (t.reminderTime > 0) {
                picked[0] = Calendar.getInstance();
                picked[0].setTimeInMillis(t.reminderTime);
                tvReminderPreview.setText(
                        DateFormat.getMediumDateFormat(this).format(picked[0].getTime())
                                + " " +
                                DateFormat.getTimeFormat(this).format(picked[0].getTime())
                );
            }
        }

        btnSetReminder.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();

            new DatePickerDialog(this, (dp, y, m, d) -> {

                new TimePickerDialog(
                        this,
                        (tp, hour, minute) -> {
                            Calendar c = Calendar.getInstance();
                            c.set(y, m, d, hour, minute, 0);
                            picked[0] = c;
                            tvReminderPreview.setText(
                                    DateFormat.getMediumDateFormat(this).format(c.getTime())
                                            + " " +
                                            DateFormat.getTimeFormat(this).format(c.getTime())
                            );
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(this) // ✅ SYSTEM DEFAULT
                ).show();

            }, now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                etTitle.setError("Required");
                return;
            }

            if (!TextUtils.isEmpty(desc)) {
                int words = desc.trim().split("\\s+").length;
                if (words > 50) {
                    etDesc.setError("Max 50 words");
                    return;
                }
            }

            String category = spinnerCategory.getSelectedItem().toString();
            long reminderMillis = picked[0] == null ? 0 : picked[0].getTimeInMillis();

            if (editPosition >= 0) {
                Task t = tasks.get(editPosition);
                t.title = title;
                t.description = desc;
                t.category = category;
                t.reminderTime = reminderMillis;
            } else {
                Task t = new Task(title, desc, category, reminderMillis, false);
                t.timestamp = System.currentTimeMillis();
                tasks.add(0, t);
            }

            resortTasks();
            saveTasks();
            refreshBackground();

            if (reminderMillis > 0) {
                scheduleExactAlarm(title, category, reminderMillis);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void scheduleExactAlarm(String title, String category, long reminderMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                Toast.makeText(this,
                        "Please allow exact alarms in system settings",
                        Toast.LENGTH_LONG).show();
                startActivity(new Intent(
                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                return;
            }
        }

        ReminderItem reminderItem = new ReminderItem(
                reminderMillis,
                (int) System.currentTimeMillis(),
                title,
                category
        );
        notificationAlarmScheduler.schedule(reminderItem);
    }

    private void resortTasks() {
        List<Task> incomplete = new ArrayList<>();
        List<Task> completed = new ArrayList<>();

        for (Task t : tasks) {
            if (t.completed) completed.add(t);
            else incomplete.add(t);
        }

        incomplete.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
        completed.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));

        tasks.clear();
        tasks.addAll(incomplete);
        tasks.addAll(completed);

        adapter.notifyDataSetChanged();
    }

    private void refreshBackground() {
        if (tasks.isEmpty()) {
            bgEmpty.setVisibility(View.VISIBLE);
            bgTasks.setVisibility(View.GONE);
            tvCompletedHeader.setVisibility(View.GONE);
        } else {
            bgEmpty.setVisibility(View.GONE);
            bgTasks.setVisibility(View.VISIBLE);

            boolean hasCompleted = false;
            for (Task t : tasks) {
                if (t.completed) {
                    hasCompleted = true;
                    break;
                }
            }
            tvCompletedHeader.setVisibility(
                    hasCompleted ? View.VISIBLE : View.GONE
            );
        }
    }

    private void saveTasks() {
        try {
            JSONArray arr = new JSONArray();
            for (Task t : tasks) {
                JSONObject o = new JSONObject();
                o.put("title", t.title);
                o.put("description", t.description);
                o.put("category", t.category);
                o.put("reminderTime", t.reminderTime);
                o.put("completed", t.completed);
                o.put("timestamp", t.timestamp);
                arr.put(o);
            }
            prefs.edit().putString("tasks", arr.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        tasks.clear();
        String json = prefs.getString("tasks", "[]");

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Task t = new Task(
                        o.optString("title"),
                        o.optString("description"),
                        o.optString("category"),
                        o.optLong("reminderTime"),
                        o.optBoolean("completed")
                );
                t.timestamp = o.optLong("timestamp");
                tasks.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }
}
