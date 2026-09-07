package com.example.stu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    public interface ItemCallbacks {
        void onItemClick(int position, DiaryEntry item);
        void onItemLongClick(int position, DiaryEntry item);
    }

    private final List<DiaryEntry> items = new ArrayList<>();
    private final ItemCallbacks callbacks;

    public HistoryAdapter(List<DiaryEntry> data, ItemCallbacks callbacks) {
        if (data != null) items.addAll(data);
        this.callbacks = callbacks;
    }

    public void submitList(List<DiaryEntry> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_moodhistory_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DiaryEntry e = items.get(position);
        h.date.setText(e.timestamp);
        h.mood.setText(e.mood);
        h.desc.setText(e.description);

        h.itemView.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                callbacks.onItemClick(pos, e);
            }
        });

        h.itemView.setOnLongClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                callbacks.onItemLongClick(pos, e);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView date, mood, desc;
        VH(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tvDate);
            mood = itemView.findViewById(R.id.tvMood);
            desc = itemView.findViewById(R.id.tvDesc);
        }
    }
}
