package com.example.stu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RecordsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecordAdapter adapter;
    SharedPreferences sharedPreferences;
    List<Object> displayList = new ArrayList<>();

    TextView tvBudgetIncome, tvExpensesValue, tvBalanceValue;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // ✅ Initialize views
        tvBudgetIncome = findViewById(R.id.tvIncomeValue);
        tvExpensesValue = findViewById(R.id.tvExpensesValue);
        tvBalanceValue = findViewById(R.id.tvBalanceValue);

        // ✅ Navigation clicks


        // ✅ RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("ExpensesData", MODE_PRIVATE);

        loadRecords();  // also updates summary
        setupSwipe();
    }

    /** 🔹 Loads all records, groups by date, and updates adapter */
    private void loadRecords() {
        displayList.clear();
        String json = sharedPreferences.getString("records", "");

        if (!json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                Map<String, List<ExpenseRecord>> grouped = new TreeMap<>(Collections.reverseOrder());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    ExpenseRecord rec = new ExpenseRecord(
                            obj.getString("categoryName"),
                            obj.getInt("iconResId"),
                            obj.getDouble("amount"),
                            obj.getString("type"),
                            obj.getString("dateAdded")
                    );

                    if (!grouped.containsKey(rec.getDateAdded())) {
                        grouped.put(rec.getDateAdded(), new ArrayList<>());
                    }
                    grouped.get(rec.getDateAdded()).add(rec);
                }

                for (String date : grouped.keySet()) {
                    displayList.add(date); // date header
                    displayList.addAll(grouped.get(date));
                }

                if (adapter == null) {
                    adapter = new RecordAdapter(this, displayList);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // ✅ always refresh totals
        updateSummary();
    }

    /** 🔹 Handles swipe-to-delete */
    private void setupSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Object item = adapter.getItem(position);

                if (item instanceof ExpenseRecord) {
                    ExpenseRecord record = (ExpenseRecord) item;
                    if (deleteRecord(record)) {
                        Toast.makeText(RecordsActivity.this, "Deleted successfully 😊", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecordsActivity.this, "Failed to delete item", Toast.LENGTH_SHORT).show();
                    }
                }
                loadRecords(); // refresh list + totals
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    /** 🔹 Delete a record from SharedPreferences */
    private boolean deleteRecord(ExpenseRecord record) {
        try {
            String recordsJson = sharedPreferences.getString("records", "");
            if (recordsJson.isEmpty()) return false;

            JSONArray jsonArray = new JSONArray(recordsJson);
            JSONArray newArray = new JSONArray();
            boolean recordFound = false;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                ExpenseRecord rec = new ExpenseRecord(
                        obj.getString("categoryName"),
                        obj.getInt("iconResId"),
                        obj.getDouble("amount"),
                        obj.getString("type"),
                        obj.getString("dateAdded")
                );

                if (!isSameRecord(rec, record)) {
                    newArray.put(obj);
                } else {
                    recordFound = true;
                }
            }

            if (recordFound) {
                sharedPreferences.edit().putString("records", newArray.toString()).apply();
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 🔹 Check if two records are identical */
    private boolean isSameRecord(ExpenseRecord r1, ExpenseRecord r2) {
        if (r1 == null || r2 == null) return false;
        return r1.getCategoryName().equals(r2.getCategoryName())
                && r1.getDateAdded().equals(r2.getDateAdded())
                && Double.compare(r1.getAmount(), r2.getAmount()) == 0
                && r1.getIconResId() == r2.getIconResId()
                && r1.getType().equals(r2.getType());
    }

    /** 🔹 Recalculate total expenses from all records */
    private double calculateTotalAmount() {
        double total = 0.0;
        String json = sharedPreferences.getString("records", "");
        if (!json.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    total += obj.getDouble("amount");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return total;
    }

    /** 🔹 Updates income, expenses, and balance TextViews */
    private void updateSummary() {
        // Monthly budget (income)
        SharedPreferences budgetPrefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        String monthlyBudgetStr = budgetPrefs.getString("monthly_budget", "0");
        double monthlyBudget;
        try {
            monthlyBudget = Double.parseDouble(monthlyBudgetStr);
        } catch (NumberFormatException e) {
            monthlyBudget = 0.0;
        }

        // Expenses + balance
        double totalExpenses = calculateTotalAmount();
        double balance = monthlyBudget - totalExpenses;

        // Update UI
        tvBudgetIncome.setText(String.valueOf(monthlyBudget));
        tvExpensesValue.setText(String.valueOf(totalExpenses));
        tvBalanceValue.setText(String.valueOf(balance));

        if (balance >= 0) {
            tvBalanceValue.setTextColor(Color.parseColor("#10B981")); // blue
        } else {
            tvBalanceValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    /** 🔹 Refresh when coming back from Add/Edit screens */
    @Override
    protected void onResume() {
        super.onResume();
        loadRecords(); // reload list + update totals
    }
}

