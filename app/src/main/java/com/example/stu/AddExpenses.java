package com.example.stu;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenses extends AppCompatActivity {

    LinearLayout layoutBeauty, layoutBusiness, layoutCar, layoutClothing, layoutCrafts,
            layoutDonation, layoutEducation, layoutElectronics, layoutFood, layoutFruits,
            layoutGaming, layoutGifts, layoutHealth, layoutHousing, layoutInternet, layoutKids,
            layoutLoan, layoutParties, layoutPets, layoutPhone, layoutRepairs, layoutSelfCare,
            layoutShopping, layoutSocial, layoutSports, layoutTax, layoutTransport, layoutTravel,
            layoutVeg, layoutArts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());

        // Initialize layouts
        layoutBeauty = findViewById(R.id.layoutBeauty);
        layoutBusiness = findViewById(R.id.layoutBusiness);
        layoutCar = findViewById(R.id.layoutCar);
        layoutClothing = findViewById(R.id.layoutClothing);
        layoutCrafts = findViewById(R.id.layoutCrafts);
        layoutDonation = findViewById(R.id.layoutDonation);
        layoutEducation = findViewById(R.id.layoutEducation);
        layoutElectronics = findViewById(R.id.layoutElectronics);
        layoutFood = findViewById(R.id.layoutFood);
        layoutFruits = findViewById(R.id.layoutFruits);
        layoutGaming = findViewById(R.id.layoutGaming);
        layoutGifts = findViewById(R.id.layoutGifts);
        layoutHealth = findViewById(R.id.layoutHealth);
        layoutHousing = findViewById(R.id.layoutHousing);
        layoutInternet = findViewById(R.id.layoutInternet);
        layoutKids = findViewById(R.id.layoutKids);
        layoutLoan = findViewById(R.id.layoutLoan);
        layoutParties = findViewById(R.id.layoutParties);
        layoutPets = findViewById(R.id.layoutPets);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutRepairs = findViewById(R.id.layoutRepairs);

        layoutShopping = findViewById(R.id.layoutShopping);
        layoutSocial = findViewById(R.id.layoutSocial);
        layoutSports = findViewById(R.id.layoutSports);
        layoutTax = findViewById(R.id.layoutTax);
        layoutTransport = findViewById(R.id.layoutTransport);
        layoutTravel = findViewById(R.id.layoutTravel);
        layoutVeg = findViewById(R.id.layoutVeg);


        // Set click listeners
        layoutBeauty.setOnClickListener(v -> showAddExpenseDialog("Beauty", R.drawable.beauty));
        layoutBusiness.setOnClickListener(v -> showAddExpenseDialog("Business", R.drawable.business));
        layoutCar.setOnClickListener(v -> showAddExpenseDialog("Car", R.drawable.car));
        layoutClothing.setOnClickListener(v -> showAddExpenseDialog("Clothing", R.drawable.clothing));
        layoutCrafts.setOnClickListener(v -> showAddExpenseDialog("Crafts", R.drawable.crafts));
        layoutDonation.setOnClickListener(v -> showAddExpenseDialog("Donation", R.drawable.donation));
        layoutEducation.setOnClickListener(v -> showAddExpenseDialog("Education", R.drawable.education));
        layoutElectronics.setOnClickListener(v -> showAddExpenseDialog("Electronics", R.drawable.electronics));
        layoutFood.setOnClickListener(v -> showAddExpenseDialog("Food", R.drawable.foodt));
        layoutFruits.setOnClickListener(v -> showAddExpenseDialog("Fruits", R.drawable.fruits));
        layoutGaming.setOnClickListener(v -> showAddExpenseDialog("Gaming", R.drawable.gaming));
        layoutGifts.setOnClickListener(v -> showAddExpenseDialog("Gifts", R.drawable.gifts));
        layoutHealth.setOnClickListener(v -> showAddExpenseDialog("Health", R.drawable.health));
        layoutHousing.setOnClickListener(v -> showAddExpenseDialog("Housing", R.drawable.housing));
        layoutInternet.setOnClickListener(v -> showAddExpenseDialog("Internet", R.drawable.internet));
        layoutKids.setOnClickListener(v -> showAddExpenseDialog("Kids", R.drawable.kids));
        layoutLoan.setOnClickListener(v -> showAddExpenseDialog("Loan", R.drawable.loan));
        layoutParties.setOnClickListener(v -> showAddExpenseDialog("Parties", R.drawable.entertainment));
        layoutPets.setOnClickListener(v -> showAddExpenseDialog("Pets", R.drawable.pets));
        layoutPhone.setOnClickListener(v -> showAddExpenseDialog("Phone", R.drawable.phone));
        layoutRepairs.setOnClickListener(v -> showAddExpenseDialog("Repairs", R.drawable.repairs));

        layoutShopping.setOnClickListener(v -> showAddExpenseDialog("Shopping", R.drawable.shopping));
        layoutSocial.setOnClickListener(v -> showAddExpenseDialog("Social", R.drawable.social));
        layoutSports.setOnClickListener(v -> showAddExpenseDialog("Sports", R.drawable.sports));
        layoutTax.setOnClickListener(v -> showAddExpenseDialog("Tax", R.drawable.tax));
        layoutTransport.setOnClickListener(v -> showAddExpenseDialog("Transport", R.drawable.transportation));
        layoutTravel.setOnClickListener(v -> showAddExpenseDialog("Travel", R.drawable.travel));
        layoutVeg.setOnClickListener(v -> showAddExpenseDialog("Veg", R.drawable.vegetables));

    }

    private void showAddExpenseDialog(String categoryName, int iconResId) {
        Dialog dialog = new Dialog(AddExpenses.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_dialog_expense);
        dialog.setCancelable(true);

        EditText etExpense = dialog.findViewById(R.id.etDialogExpense);
        ImageButton btnCancel = dialog.findViewById(R.id.btnDialogCancel);
        ImageButton btnConfirm = dialog.findViewById(R.id.btnDialogConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String amountStr = etExpense.getText().toString().trim();

            if (!amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    String date = new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(new Date());

                    int safeIconResId = 0;
                    try {
                        getResources().getDrawable(iconResId);
                        safeIconResId = iconResId;
                    } catch (Resources.NotFoundException ignored) {}

                    ExpenseRecord newRecord = new ExpenseRecord(
                            categoryName,
                            safeIconResId,
                            amount,
                            "expense",
                            date
                    );

                    SharedPreferences prefs = getSharedPreferences(
                            "ExpensesData",
                            MODE_PRIVATE
                    );

                    String json = prefs.getString("records", "");
                    JSONArray jsonArray = json.isEmpty()
                            ? new JSONArray()
                            : new JSONArray(json);

                    JSONObject obj = new JSONObject();
                    obj.put("categoryName", newRecord.getCategoryName());
                    obj.put("iconResId", newRecord.getIconResId());
                    obj.put("amount", newRecord.getAmount());
                    obj.put("type", newRecord.getType());
                    obj.put("dateAdded", newRecord.getDateAdded());

                    jsonArray.put(obj);
                    prefs.edit().putString("records", jsonArray.toString()).apply();

                    Toast.makeText(
                            AddExpenses.this,
                            "Expense saved!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialog.dismiss();

                    // ✅ GO BACK TO PERSONAL MANAGEMENT PAGE
                    finish();

                } catch (Exception e) {
                    Toast.makeText(
                            AddExpenses.this,
                            "Error saving expense.",
                            Toast.LENGTH_SHORT
                    ).show();
                    e.printStackTrace();
                }
            } else {
                etExpense.setError("Please enter an amount");
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
