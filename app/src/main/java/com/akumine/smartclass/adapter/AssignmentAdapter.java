package com.akumine.smartclass.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Assignments;
import com.akumine.smartclass.view.AssignmentViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentViewHolder> {

    private List<Assignments> assignmentsList;
    private AssignmentViewHolder.AssignmentViewHolderClickListener listener;

    public AssignmentAdapter(@Nullable List<Assignments> assignmentsList,
                             @NonNull AssignmentViewHolder.AssignmentViewHolderClickListener listener) {
        if (assignmentsList == null) {
            assignmentsList = new ArrayList<>();
        }
        this.assignmentsList = assignmentsList;
        this.listener = listener;
    }

    public void setData(List<Assignments> assignmentsList) {
        if (assignmentsList == null) {
            assignmentsList = new ArrayList<>();
        }
        Collections.sort(assignmentsList, Assignments.BY_CREATED_DATE);
        this.assignmentsList = assignmentsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.entry_assignment;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new AssignmentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        holder.bindData(assignmentsList.get(position));
    }

    @Override
    public int getItemCount() {
        return assignmentsList.size();
    }
}
