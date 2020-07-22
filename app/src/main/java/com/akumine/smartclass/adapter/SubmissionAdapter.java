package com.akumine.smartclass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Submission;
import com.akumine.smartclass.view.SubmissionViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionViewHolder> {

    private List<Submission> submissionList;
    private SubmissionViewHolder.SubmissionViewHolderClickListener listener;
    private Context context;

    public SubmissionAdapter(Context context,
                             @Nullable List<Submission> submissionList,
                             @NonNull SubmissionViewHolder.SubmissionViewHolderClickListener listener) {
        if (submissionList == null) {
            submissionList = new ArrayList<>();
        }
        this.context = context;
        this.submissionList = submissionList;
        this.listener = listener;
    }

    public void setData(List<Submission> submissionList) {
        if (submissionList == null) {
            submissionList = new ArrayList<>();
        }
        this.submissionList = submissionList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.entry_submission;
    }

    @NonNull
    @Override
    public SubmissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new SubmissionViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionViewHolder holder, int position) {
        holder.bindData(submissionList.get(position));
    }

    @Override
    public int getItemCount() {
        return submissionList.size();
    }
}
