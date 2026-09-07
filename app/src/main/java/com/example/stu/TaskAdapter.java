package com.example.stu;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskVH> {

    public interface Listener {
        void onEdit(int position);
        void onToggleComplete(int position);
    }

    private final List<Task> tasks;
    private final Listener listener;

    public TaskAdapter(List<Task> tasks, Listener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskVH holder, int position) {
        Task t = tasks.get(position);

        holder.tvTitle.setText(t.title);

        // Hide description if empty
        if (t.description == null || t.description.trim().isEmpty()) {
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(t.description);
        }

        holder.tvCategory.setText(t.category);

        // Strike-through if completed
        holder.tvTitle.setPaintFlags(t.completed
                ? holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        // Reminder icon
        if (t.reminderTime > 0) {
            holder.ivBell.setVisibility(View.VISIBLE);
            Date date = new Date(t.reminderTime);
            holder.tvReminderDate.setText(new SimpleDateFormat("dd MMM", Locale.getDefault()).format(date));
            holder.tvReminderTime.setText("at " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date));
        } else {
            holder.ivBell.setVisibility(View.GONE);
            holder.tvReminderDate.setVisibility(View.GONE);
            holder.tvReminderTime.setVisibility(View.GONE);
        }

        holder.btnComplete.setImageResource(t.completed ? R.drawable.done : R.drawable.circle);

        holder.btnComplete.setOnClickListener(v -> listener.onToggleComplete(position));
        holder.itemView.setOnClickListener(v -> listener.onEdit(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskVH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvCategory, tvReminderDate, tvReminderTime;
        ImageButton btnComplete;
        ImageView ivBell;

        TaskVH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvReminderDate = itemView.findViewById(R.id.tvReminderDate);
            tvReminderTime = itemView.findViewById(R.id.tvReminderTime);
            btnComplete = itemView.findViewById(R.id.btnComplete);
            ivBell = itemView.findViewById(R.id.ivBell);
        }
    }
}
