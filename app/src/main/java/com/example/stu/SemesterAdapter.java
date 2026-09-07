package com.example.stu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {

    private List<Semester> semesterList;

    public SemesterAdapter(List<Semester> semesterList) {
        this.semesterList = semesterList;
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_semester_adapter, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        Semester semester = semesterList.get(position);

        holder.tvSemesterTitle.setText("Semester " + semester.semesterNumber);

        StringBuilder coursesText = new StringBuilder();
        for (Course c : semester.courses) {
            coursesText.append(c.name)
                    .append(" (").append(c.credits).append(" cr) - ")
                    .append(c.grade).append("\n");
        }
        holder.tvCourses.setText(coursesText.toString().trim());

        holder.tvSemesterGPA.setText("Semester GPA: " + String.format("%.2f", semester.semesterGPA));
        holder.tvCumulativeCGPA.setText("Cumulative CGPA: " + String.format("%.2f", semester.cumulativeCGPA));

        // New
        holder.tvSemesterCredits.setText("Credits this semester: " + semester.semesterCredits);
        holder.tvTotalCredits.setText("Total credits so far: " + semester.totalCreditsSoFar);
    }

    @Override
    public int getItemCount() {
        return semesterList.size();
    }

    public static class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSemesterTitle, tvCourses, tvSemesterGPA, tvCumulativeCGPA, tvSemesterCredits, tvTotalCredits;





        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemesterTitle = itemView.findViewById(R.id.tvSemesterTitle);
            tvCourses = itemView.findViewById(R.id.tvCourses);
            tvSemesterGPA = itemView.findViewById(R.id.tvSemesterGPA);
            tvCumulativeCGPA = itemView.findViewById(R.id.tvCumulativeCGPA);
            tvSemesterCredits = itemView.findViewById(R.id.tvSemesterCredits);
            tvTotalCredits = itemView.findViewById(R.id.tvTotalCredits);
        }
    }
}
