package com.akumine.smartclass.view;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Assignments;

public class AssignmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface AssignmentViewHolderClickListener {
        void OnAssignmentItemClick(String classId, String assignId);
    }

    private Assignments assignments;
    private TextView assignTitle;
    private TextView date;
    private TextView time;

    private AssignmentViewHolderClickListener listener;

    public AssignmentViewHolder(View itemView, AssignmentViewHolderClickListener listener) {
        super(itemView);
        assignTitle = itemView.findViewById(R.id.view_assign_title);
        date = itemView.findViewById(R.id.assign_date);
        time = itemView.findViewById(R.id.assign_time);
        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    public void bindData(Assignments assignments) {
        this.assignments = assignments;
        assignTitle.setText(assignments.getAssignmentName());
        date.setText(assignments.getDate());
        time.setText(assignments.getTime());
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.OnAssignmentItemClick(assignments.getClassId(), assignments.getId());
            }
        }
    }
}
