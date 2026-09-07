package com.example.stu;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DATE_HEADER = 0;
    private static final int TYPE_RECORD_ITEM = 1;

    private List<Object> items;
    private Context context;

    public RecordAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_DATE_HEADER : TYPE_RECORD_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_DATE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.record_item, parent, false);
            return new RecordViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateHeaderViewHolder) {
            ((DateHeaderViewHolder) holder).tvDateHeader.setText((String) items.get(position));
        } else if (holder instanceof RecordViewHolder) {
            ExpenseRecord record = (ExpenseRecord) items.get(position);
            RecordViewHolder vh = (RecordViewHolder) holder;

            vh.tvCategoryName.setText(record.getCategoryName());
            vh.tvAmount.setText(String.valueOf(record.getAmount()));

            // Safe icon loading
            if (record.getIconResId() != 0) {
                try {
                    vh.imgCategoryIcon.setImageResource(record.getIconResId());
                    vh.imgCategoryIcon.setVisibility(View.VISIBLE);
                } catch (Resources.NotFoundException e) {
                    vh.imgCategoryIcon.setVisibility(View.GONE);
                }
            } else {
                vh.imgCategoryIcon.setVisibility(View.GONE);
            }

            // Reset recycled view properties
            ViewCompat.setTranslationX(vh.layout_foreground, 0);
            vh.layout_foreground.setClickable(true);
            vh.layout_foreground.setFocusable(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Object getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateHeader;

        public DateHeaderViewHolder(View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tvDateHeader);
        }
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategoryIcon;
        TextView tvCategoryName, tvAmount;
        View layout_foreground;

        public RecordViewHolder(View itemView) {
            super(itemView);
            imgCategoryIcon = itemView.findViewById(R.id.imgCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            layout_foreground = itemView.findViewById(R.id.layout_foreground);
        }
    }
}
