package com.example.stu;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MoodChartActivity extends AppCompatActivity {

    BarChart barChart;
    MoodStorage moodStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_chart);

        barChart = findViewById(R.id.barChart);
        moodStorage = new MoodStorage(this);

        showMoodChart();
    }

    private void showMoodChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6); // Last 7 days

        for (int i = 0; i < 7; i++) {
            String date = sdf.format(calendar.getTime());
            float moodValue = moodStorage.getMoodForDate(date); // 1-5 scale
            entries.add(new BarEntry(i, moodValue));
            dates.add(date);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Mood (Last 7 Days)");
        dataSet.setColors(Color.parseColor("#FFA726"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        barChart.animateY(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45);

        Description desc = new Description();
        desc.setText("Mood Tracking (Last 7 Days)");
        barChart.setDescription(desc);
    }
}
