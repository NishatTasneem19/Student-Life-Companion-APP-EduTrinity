package com.example.stu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class CGPACalculatorActivity extends AppCompatActivity {

    private int currentSemester = 1;
    private int totalSemesters = 1;
    private int currentCourseCount = 0;

    private Map<Integer, List<View>> semesterCoursesMap = new HashMap<>();
    private Map<Integer, Double> semesterGPAStore = new HashMap<>();
    private Map<Integer, Double> semesterCreditsStore = new HashMap<>();

    private LinearLayout courseContainer, semesterTabs;
    private TextView tvSemesterCount, tvCGPA, tvTotalCredits, tvCurrentSemester, tvCurrentSemesterGPA;
    private Button btnAddCourse, btnRemoveCourse, btnCalculate, btnRemoveSemester;
    private SharedPreferences sharedPreferences;
    private Button btnPredictor;
    private Button btnTargetCGPA;
    private Button btnDeleteSemesterData;
    private Button btnSummary;
    private Button btnBack;
    private Button btngrade;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgpacalculator);

        sharedPreferences = getSharedPreferences("CGPA_PREFS", Context.MODE_PRIVATE);

        // Back button: goes to the previous activity
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CGPACalculatorActivity.this, AcademicTools.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });


        Button btngrade = findViewById(R.id.btngrade);
        btngrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CGPACalculatorActivity.this, GradeMapping.class);
                startActivity(intent);
            }
        });



        // Initialize views
        courseContainer = findViewById(R.id.courseContainer);
        semesterTabs = findViewById(R.id.semesterTabs);
        tvSemesterCount = findViewById(R.id.tvSemesterCount);
        tvCGPA = findViewById(R.id.tvCGPA);
        tvTotalCredits = findViewById(R.id.tvTotalCredits);
        tvCurrentSemester = findViewById(R.id.tvCurrentSemester);
        tvCurrentSemesterGPA = findViewById(R.id.tvCurrentSemesterGPA);

        btnAddCourse = findViewById(R.id.btnAddCourse);
        btnRemoveCourse = findViewById(R.id.btnRemoveCourse);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnRemoveSemester = findViewById(R.id.btnRemoveSemester);
        btnDeleteSemesterData = findViewById(R.id.btnDeleteSemesterData);
        btnSummary = findViewById(R.id.btnSummary);

        // Load saved data
        loadAllCourses();

        // Semester control buttons
        findViewById(R.id.btnIncreaseSemester).setOnClickListener(v -> addSemester());
        findViewById(R.id.btnDecreaseSemester).setOnClickListener(v -> decreaseSemester());
        btnRemoveSemester.setOnClickListener(v -> removeSemester());

        // Course buttons
        btnAddCourse.setOnClickListener(v -> addCourse());
        btnRemoveCourse.setOnClickListener(v -> removeCourse());

        // Initialize Target CGPA button
        btnTargetCGPA = findViewById(R.id.btnTargetCGPA);
        btnTargetCGPA.setOnClickListener(v -> {
            Intent intent = new Intent(CGPACalculatorActivity.this, TargetCgpaActivity.class);
            StringBuilder gpas = new StringBuilder();
            StringBuilder credits = new StringBuilder();
            for (int sem = 1; sem <= totalSemesters; sem++) {
                double gpa = semesterGPAStore.getOrDefault(sem, 0.0);
                double credit = semesterCreditsStore.getOrDefault(sem, 0.0);
                gpas.append(gpa).append(",");
                credits.append(credit).append(",");
            }
            intent.putExtra("SEM_GPA", gpas.toString());
            intent.putExtra("SEM_CREDITS", credits.toString());
            intent.putExtra("TOTAL_SEMESTERS", totalSemesters);
            startActivity(intent);
        });

        btnSummary.setOnClickListener(v -> { startActivity(new Intent(CGPACalculatorActivity.this, SemesterSummaryActivity.class)); });
        // CGPA Predictor Button
        btnPredictor = findViewById(R.id.btnPredictor);
        btnPredictor.setOnClickListener(v -> {
            Intent intent = new Intent(CGPACalculatorActivity.this, Predictor.class);
            StringBuilder gpas = new StringBuilder();
            StringBuilder credits = new StringBuilder();
            for (int sem = 1; sem <= totalSemesters; sem++) {
                double gpa = semesterGPAStore.getOrDefault(sem, 0.0);
                double credit = semesterCreditsStore.getOrDefault(sem, 0.0);
                gpas.append(gpa).append(",");
                credits.append(credit).append(",");
            }
            intent.putExtra("SEM_GPA", gpas.toString());
            intent.putExtra("SEM_CREDITS", credits.toString());
            intent.putExtra("TOTAL_SEMESTERS", totalSemesters);
            startActivity(intent);
        });

        // Calculate button
        btnCalculate.setOnClickListener(v -> {
            if (!validateSemesterCourses(currentSemester)) return; // stop if validation fails
            calculateCurrentSemesterGPA();
            saveAllCourses();
            Toast.makeText(this, "Courses saved successfully.", Toast.LENGTH_SHORT).show();
        });



        btnDeleteSemesterData.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Semester Data")
                    .setMessage("Are you sure you want to delete all courses for Semester " + currentSemester + "?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteSemesterData(currentSemester))
                    .setNegativeButton("No", null)
                    .show();
        });


    }

    // ----------------- Semester & Course Management -----------------
    private void addSemester() {
        if (totalSemesters >= 17) {
            Toast.makeText(this, "Maximum 17 semesters allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        totalSemesters++;
        tvSemesterCount.setText(String.valueOf(totalSemesters));
        addSemesterTab(totalSemesters);
        semesterCoursesMap.put(totalSemesters, new ArrayList<>());
        switchSemester(totalSemesters);
        saveAllCourses();
    }

    private void decreaseSemester() {
        if (currentSemester > 1) switchSemester(currentSemester - 1);
    }

    private void removeSemester() {
        if (totalSemesters <= 1) return;
        semesterCoursesMap.remove(totalSemesters);
        semesterGPAStore.remove(totalSemesters);
        semesterCreditsStore.remove(totalSemesters);
        removeLastSemesterTab();
        totalSemesters--;
        tvSemesterCount.setText(String.valueOf(totalSemesters));
        if (currentSemester > totalSemesters) switchSemester(totalSemesters);
        saveAllCourses();
    }

    private void addSemesterTab(int semesterNumber) {
        Button tabButton = new Button(this);
        tabButton.setText("Sem " + semesterNumber);
        tabButton.setTag(semesterNumber);
        tabButton.setTextColor(getResources().getColor(android.R.color.white));
        tabButton.setBackgroundResource(R.drawable.course_item_bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 8, 0);
        tabButton.setLayoutParams(params);

        tabButton.setOnClickListener(v -> switchSemester((int) v.getTag()));
        semesterTabs.addView(tabButton);
    }

    private void removeLastSemesterTab() {
        if (semesterTabs.getChildCount() > 0)
            semesterTabs.removeViewAt(semesterTabs.getChildCount() - 1);
    }

    private void switchSemester(int semester) {
        currentSemester = semester;
        tvCurrentSemester.setText("Semester " + currentSemester + " Courses");
        currentCourseCount = 0;

        // Highlight the current semester tab
        for (int i = 0; i < semesterTabs.getChildCount(); i++) {
            Button tab = (Button) semesterTabs.getChildAt(i);
            int tabSem = (int) tab.getTag();
            if (tabSem == semester) {
                tab.setBackgroundResource(R.drawable.semester_tab_active); // darker highlight
            } else {
                tab.setBackgroundResource(R.drawable.semester_tab_normal); // normal
            }
        }

        // Load courses for the current semester
        courseContainer.removeAllViews();
        List<View> courses = semesterCoursesMap.get(semester);
        if (courses != null && !courses.isEmpty()) {
            currentCourseCount = courses.size();
            for (View courseView : courses) {
                courseContainer.addView(courseView);
            }
        }

        // Calculate GPA for current semester
        SemesterResult sr = calculateSemesterGPA(currentSemester);
        if (sr != null) {
            semesterGPAStore.put(currentSemester, sr.gpa);
            tvCurrentSemesterGPA.setText(String.format("Semester GPA: %.2f", sr.gpa));
        } else {
            tvCurrentSemesterGPA.setText("Semester GPA: 0.00");
            semesterCreditsStore.put(currentSemester, 0.0);
        }

        // Update cumulative totals
        tvTotalCredits.setText(String.format("%.2f", calculateTotalCredits()));
        tvCGPA.setText(String.format("%.2f", calculateCumulativeCGPA()));

        // Show individual semester credits
        TextView tvTotalCreditsAll = findViewById(R.id.tvTotalCreditsAll);
        double semesterCredits = semesterCreditsStore.getOrDefault(currentSemester, 0.0);
        tvTotalCreditsAll.setText(String.format("Semester %d Credits: %.2f", currentSemester, semesterCredits));
    }


    // ----------------- Course Management -----------------
    private void addCourse() {
        List<View> currentSemesterCourses = semesterCoursesMap.computeIfAbsent(currentSemester, k -> new ArrayList<>());
        if (currentSemesterCourses.size() >= 6) {
            Toast.makeText(this, "Maximum 6 courses per semester", Toast.LENGTH_SHORT).show();
            return;
        }

        currentCourseCount++;
        LinearLayout courseLayout = new LinearLayout(this);
        courseLayout.setOrientation(LinearLayout.VERTICAL);
        courseLayout.setBackgroundResource(R.drawable.course_item_bg);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 8);
        courseLayout.setPadding(12, 12, 12, 12);
        courseLayout.setLayoutParams(layoutParams);

        TextView tvCourseTitle = new TextView(this);
        tvCourseTitle.setText("Course " + currentCourseCount);
        tvCourseTitle.setTextColor(getResources().getColor(R.color.green));
        tvCourseTitle.setTextSize(16);
        tvCourseTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        courseLayout.addView(tvCourseTitle);

        courseLayout.addView(createRow("Course Code:", R.id.etCourseCode1, InputType.TYPE_CLASS_TEXT, "Enter name"));
        courseLayout.addView(createRow("Credits:", R.id.etCredits1, InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER, "1-15"));
        courseLayout.addView(createSpinnerRow("Grade:", R.id.spinnerGrade1));

        courseContainer.addView(courseLayout);
        currentSemesterCourses.add(courseLayout);
    }

    private LinearLayout createRow(String label, int editTextId, int inputType, String hint) {
        // Create horizontal container for label + EditText
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setPadding(0, 8, 0, 8);

        // Create label (e.g., "Course Code:")
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextColor(getResources().getColor(android.R.color.white));

        // Add consistent right margin so text fields align neatly
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        tvParams.setMarginEnd(16); // add some margin space between label and field
        tv.setLayoutParams(tvParams);

        // Create input field
        EditText et = new EditText(this);
        et.setId(editTextId);
        et.setHint(hint);
        et.setInputType(inputType);

        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2
        );
        et.setLayoutParams(etParams);

        // ✅ Apply auto-uppercase and remove spaces only for Course Code field
        if (label.equals("Course Code:")) {
            // Force uppercase input
            et.setFilters(new android.text.InputFilter[]{ new android.text.InputFilter.AllCaps() });

            // Watch for spaces and remove them automatically
            et.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(android.text.Editable s) {
                    String text = s.toString();
                    // Remove all spaces
                    String noSpaces = text.replaceAll("\\s+", "");
                    if (!text.equals(noSpaces)) {
                        et.setText(noSpaces);
                        et.setSelection(noSpaces.length());
                    }
                }
            });
        }

        // Add label and input field to the row
        row.addView(tv);
        row.addView(et);

        return row;
    }


    private LinearLayout createSpinnerRow(String label, int spinnerId) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextColor(getResources().getColor(R.color.light_text));
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.rightMargin = 8;
        tvLabel.setLayoutParams(labelParams);
        row.addView(tvLabel);

        Spinner spinner = new Spinner(this);
        spinner.setId(spinnerId);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grade_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        row.addView(spinner);

        return row;
    }

    private void removeCourse() {
        List<View> currentSemesterCourses = semesterCoursesMap.get(currentSemester);
        if (currentSemesterCourses == null || currentSemesterCourses.size() <= 1) {
            Toast.makeText(this, "Minimum 1 course per semester", Toast.LENGTH_SHORT).show();
            return;
        }
        View lastCourse = currentSemesterCourses.remove(currentSemesterCourses.size() - 1);
        courseContainer.removeView(lastCourse);
        currentCourseCount--;
        saveAllCourses();
    }

    // ----------------- GPA & Credits Calculation -----------------
    private SemesterResult calculateSemesterGPA(int semester) {
        List<View> courses = semesterCoursesMap.get(semester);
        if (courses == null || courses.isEmpty()) return null;

        double totalGradePoints = 0;
        double totalCredits = 0;

        for (View courseView : courses) {
            EditText etCredits = null;
            Spinner spinnerGrade = null;
            for (int i = 0; i < ((LinearLayout) courseView).getChildCount(); i++) {
                View child = ((LinearLayout) courseView).getChildAt(i);
                if (child instanceof LinearLayout) {
                    LinearLayout row = (LinearLayout) child;
                    for (int j = 0; j < row.getChildCount(); j++) {
                        View rowChild = row.getChildAt(j);
                        if (rowChild instanceof EditText) etCredits = (EditText) rowChild;
                        else if (rowChild instanceof Spinner) spinnerGrade = (Spinner) rowChild;
                    }
                }
            }

            if (etCredits == null || spinnerGrade == null || etCredits.getText().toString().isEmpty() || spinnerGrade.getSelectedItem() == null)
                continue;

            double credits;
            try { credits = Double.parseDouble(etCredits.getText().toString()); }
            catch (NumberFormatException e) { credits = 0; }

            // Enforce credit limits
            if (credits < 1) credits = 1;
            if (credits > 15) credits = 15;

            double gradePoint = getGradePoint(spinnerGrade.getSelectedItem().toString());
            totalGradePoints += gradePoint * credits;
            totalCredits += credits;
        }

        semesterCreditsStore.put(semester, totalCredits);
        if (totalCredits > 0) return new SemesterResult(totalGradePoints / totalCredits, totalCredits);
        return new SemesterResult(0.0, 0.0);
    }

    private void calculateCurrentSemesterGPA() {
        List<View> courses = semesterCoursesMap.get(currentSemester);
        if (courses == null || courses.isEmpty()) {
            Toast.makeText(this, "No courses found for this semester.", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalGradePoints = 0;
        double totalCredits = 0;

        for (View courseView : courses) {
            EditText etCourseName = null;
            EditText etCredits = null;
            Spinner spinnerGrade = null;

            // Find EditText and Spinner dynamically in courseView
            for (int i = 0; i < ((LinearLayout) courseView).getChildCount(); i++) {
                View child = ((LinearLayout) courseView).getChildAt(i);
                if (child instanceof LinearLayout) {
                    LinearLayout row = (LinearLayout) child;
                    for (int j = 0; j < row.getChildCount(); j++) {
                        View rowChild = row.getChildAt(j);
                        if (rowChild instanceof EditText) {
                            if (((EditText) rowChild).getHint().toString().toLowerCase().contains("name"))
                                etCourseName = (EditText) rowChild;
                            else
                                etCredits = (EditText) rowChild;
                        } else if (rowChild instanceof Spinner) {
                            spinnerGrade = (Spinner) rowChild;
                        }
                    }
                }
            }

            // Validate empty fields
            if (etCourseName == null || etCourseName.getText().toString().trim().isEmpty() ||
                    etCredits == null || etCredits.getText().toString().trim().isEmpty() ||
                    spinnerGrade == null || spinnerGrade.getSelectedItem() == null) {
                Toast.makeText(this, "All fields must be filled for every course.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate credits range
            double credits;
            try {
                credits = Double.parseDouble(etCredits.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid credit value.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (credits < 1 || credits > 15) {
                Toast.makeText(this, "Credits must be between 1 and 15.", Toast.LENGTH_SHORT).show();
                return;
            }

            double gradePoint = getGradePoint(spinnerGrade.getSelectedItem().toString());
            totalGradePoints += gradePoint * credits;
            totalCredits += credits;
        }

        // Save semester credits
        semesterCreditsStore.put(currentSemester, totalCredits);

        double semesterGPA = totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
        semesterGPAStore.put(currentSemester, semesterGPA);

        // Update UI
        tvCurrentSemesterGPA.setText(String.format("Semester GPA: %.2f", semesterGPA));
        tvTotalCredits.setText(String.format("%.2f", calculateTotalCredits()));
        tvCGPA.setText(String.format("%.2f", calculateCumulativeCGPA()));

        // Update semester credits display
        TextView tvTotalCreditsAll = findViewById(R.id.tvTotalCreditsAll);
        tvTotalCreditsAll.setText(String.format("Semester %d Credits: %.2f", currentSemester, totalCredits));
    }



    private void deleteSemesterData(int semester) {
        List<View> courses = semesterCoursesMap.get(semester);
        if (courses != null) {
            courses.clear(); // clear all views
            courseContainer.removeAllViews(); // remove from layout
        }

        // Reset GPA and credits
        semesterGPAStore.put(semester, 0.0);
        semesterCreditsStore.put(semester, 0.0);
        tvCurrentSemesterGPA.setText("Semester GPA: 0.00");
        tvTotalCredits.setText(String.format("%.2f", calculateTotalCredits()));
        tvCGPA.setText(String.format("%.2f", calculateCumulativeCGPA()));

        // Save changes
        saveAllCourses();

        Toast.makeText(this, "All courses for Semester " + semester + " deleted.", Toast.LENGTH_SHORT).show();
    }


    private double calculateCumulativeCGPA() {
        double totalPoints = 0;
        double totalCredits = 0;
        for (int sem : semesterGPAStore.keySet()) {
            SemesterResult sr = calculateSemesterGPA(sem);
            if (sr != null) {
                totalPoints += sr.gpa * sr.totalCredits;
                totalCredits += sr.totalCredits;
            }
        }
        return totalCredits > 0 ? totalPoints / totalCredits : 0;
    }

    private double calculateTotalCredits() {
        double totalCredits = 0;
        for (int sem = 1; sem <= totalSemesters; sem++) {
            SemesterResult sr = calculateSemesterGPA(sem);
            if (sr != null) totalCredits += sr.totalCredits;
        }
        return totalCredits;
    }

    private double getGradePoint(String grade) {
        switch (grade) {
            case "A+":  return 4.0;
            case "A":  return 3.75;
            case "A-": return 3.50;
            case "B+": return 3.25;
            case "B": return 3.0;
            case "B-": return 2.75;
            case "C+": return 2.50;
            case "C": return 2.25;
            case "D": return 2.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }

    private void saveAllCourses() {
        List<String> data = new ArrayList<>();
        for (int sem = 1; sem <= totalSemesters; sem++) {
            List<View> courses = semesterCoursesMap.get(sem);
            if (courses == null) continue;
            for (View v : courses) {
                EditText etCode = v.findViewById(R.id.etCourseCode1);
                EditText etCredits = v.findViewById(R.id.etCredits1);
                Spinner spinnerGrade = v.findViewById(R.id.spinnerGrade1);
                if (etCode == null || etCredits == null || spinnerGrade == null) continue;
                String code = etCode.getText().toString();
                String credits = etCredits.getText().toString();
                String grade = spinnerGrade.getSelectedItem() != null ? spinnerGrade.getSelectedItem().toString() : "";
                if (!code.isEmpty() && !credits.isEmpty() && !grade.isEmpty()) {
                    data.add(code + "," + credits + "," + grade + "," + sem);
                }
            }
        }
        sharedPreferences.edit().putString("courses", String.join(";", data)).apply();
    }

    private boolean validateSemesterCourses(int semester) {
        List<View> courses = semesterCoursesMap.get(semester);
        if (courses == null || courses.isEmpty()) {
            Toast.makeText(this, "No courses for this semester.", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (View courseView : courses) {
            EditText etCourseName = null;
            EditText etCredits = null;
            Spinner spinnerGrade = null;

            // Find all child views
            for (int i = 0; i < ((LinearLayout) courseView).getChildCount(); i++) {
                View child = ((LinearLayout) courseView).getChildAt(i);
                if (child instanceof LinearLayout) {
                    LinearLayout row = (LinearLayout) child;
                    for (int j = 0; j < row.getChildCount(); j++) {
                        View rowChild = row.getChildAt(j);
                        if (rowChild instanceof EditText) {
                            if (((EditText) rowChild).getHint().toString().toLowerCase().contains("name"))
                                etCourseName = (EditText) rowChild;
                            else
                                etCredits = (EditText) rowChild;
                        } else if (rowChild instanceof Spinner) {
                            spinnerGrade = (Spinner) rowChild;
                        }
                    }
                }
            }

            // Check empty fields
            if (etCourseName == null || etCourseName.getText().toString().trim().isEmpty() ||
                    etCredits == null || etCredits.getText().toString().trim().isEmpty() ||
                    spinnerGrade == null || spinnerGrade.getSelectedItem() == null) {
                Toast.makeText(this, "All fields must be filled for every course.", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Check credit range
            double credits;
            try { credits = Double.parseDouble(etCredits.getText().toString()); }
            catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid credit value.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (credits < 1 || credits > 15) {
                Toast.makeText(this, "Credits must be between 1 and 15.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // All validations passed
        return true;
    }


    private void loadAllCourses() {
        String savedData = sharedPreferences.getString("courses", "");
        if (savedData.isEmpty()) {
            addSemesterTab(1);
            semesterCoursesMap.put(1, new ArrayList<>());
            return;
        }
        String[] entries = savedData.split(";");
        Map<Integer, List<String[]>> tempMap = new HashMap<>();
        for (String entry : entries) {
            String[] parts = entry.split(",");
            if (parts.length < 4) continue;
            int sem = Integer.parseInt(parts[3]);
            tempMap.computeIfAbsent(sem, k -> new ArrayList<>()).add(parts);
        }
        for (Map.Entry<Integer, List<String[]>> e : tempMap.entrySet()) {
            int sem = e.getKey();
            addSemesterTab(sem);
            semesterCoursesMap.put(sem, new ArrayList<>());
            switchSemester(sem);
            for (String[] course : e.getValue()) {
                addCourse();
                View lastCourse = semesterCoursesMap.get(sem).get(semesterCoursesMap.get(sem).size() - 1);
                EditText etCode = lastCourse.findViewById(R.id.etCourseCode1);
                EditText etCredits = lastCourse.findViewById(R.id.etCredits1);
                Spinner spinnerGrade = lastCourse.findViewById(R.id.spinnerGrade1);
                if (etCode != null) etCode.setText(course[0]);
                if (etCredits != null) etCredits.setText(course[1]);
                if (spinnerGrade != null) {
                    ArrayAdapter adapter = (ArrayAdapter) spinnerGrade.getAdapter();
                    int pos = adapter.getPosition(course[2]);
                    spinnerGrade.setSelection(pos);
                }
            }
            totalSemesters = Math.max(totalSemesters, sem);
        }
        for (int sem = 1; sem <= totalSemesters; sem++) {
            SemesterResult sr = calculateSemesterGPA(sem);
            if (sr != null) {
                semesterGPAStore.put(sem, sr.gpa);
                semesterCreditsStore.put(sem, sr.totalCredits);
            }
        }
        tvCGPA.setText(String.format("%.2f", calculateCumulativeCGPA()));
        tvTotalCredits.setText(String.format("%.2f", calculateTotalCredits()));
        switchSemester(currentSemester);
    }

    private static class SemesterResult {
        double gpa;
        double totalCredits;
        SemesterResult(double gpa, double totalCredits) {
            this.gpa = gpa;
            this.totalCredits = totalCredits;
        }
    }
}
