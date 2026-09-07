package com.example.stu;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoodInsightsActivity extends AppCompatActivity {

    private static final Map<String, Integer> MOOD_COLORS = new LinkedHashMap<String, Integer>() {{
        put("Happy", Color.parseColor("#FFD700"));
        put("Sad", Color.parseColor("#4682B4"));
        put("Angry", Color.parseColor("#FF4500"));
        put("Anxious", Color.parseColor("#8A2BE2"));
        put("Calm", Color.parseColor("#3CB371"));
        put("Loved", Color.parseColor("#FF69B4"));
        put("Depressed", Color.parseColor("#708090"));
        put("Sick", Color.parseColor("#A52A2A"));
        put("Crying", Color.parseColor("#1E90FF"));
        put("Excited", Color.parseColor("#FF8C00"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodinsights);

        BarChart chart = findViewById(R.id.moodChart);
        TextView legendText = findViewById(R.id.legendText);
        TextView dateText = findViewById(R.id.dateText);

        List<MoodEntry> last7Days = MoodStore.getLastNDays(this, 7);

        if (last7Days.isEmpty()) {
            chart.setNoDataText("No mood entries found for last 7 days.");
            legendText.setText("");
            dateText.setText("");
            return;
        }

        // Current date range (last 7 days)
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date today = new Date();
        Date weekAgo = new Date(today.getTime() - 6L*24*60*60*1000); // last 7 days
        dateText.setText("From " + sdf.format(weekAgo) + " to " + sdf.format(today));

        // Count moods
        Map<String, Integer> moodCount = new HashMap<>();
        for (MoodEntry entry : last7Days) {
            moodCount.put(entry.mood, moodCount.getOrDefault(entry.mood, 0) + 1);
        }

        // Prepare chart entries
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int i = 0;
        for (String mood : MOOD_COLORS.keySet()) {
            if (moodCount.containsKey(mood)) {
                entries.add(new BarEntry(i, moodCount.get(mood)));
                labels.add(mood);
                colors.add(MOOD_COLORS.get(mood));
                i++;
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(colors);
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        dataSet.setDrawValues(false); // remove values on bars

        chart.setData(barData);
        chart.setFitBars(true);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0);

        // X-axis labels
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < labels.size()) return labels.get((int) value);
                return "";
            }
        });
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-30f);

        // Manual legend
        SpannableString finalLegend;
        StringBuilder sb = new StringBuilder();
        List<int[]> colorPositions = new ArrayList<>();
        int pos = 0;

        for (String mood : labels) {
            String line = "■ " + mood + " (" + moodCount.get(mood) + ")\n";

            sb.append(line);
            colorPositions.add(new int[]{pos, pos + 1, MOOD_COLORS.get(mood)});
            pos += line.length();
        }

        finalLegend = new SpannableString(sb.toString());
        for (int[] arr : colorPositions) {
            finalLegend.setSpan(new ForegroundColorSpan(arr[2]), arr[0], arr[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        legendText.setText(finalLegend);
        chart.getLegend().setEnabled(false); // disable chart built-in legend
        chart.invalidate();
    }
}
