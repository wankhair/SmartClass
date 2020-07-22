package com.akumine.smartclass.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.view.MemberViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberViewHolder> {

    private List<ClassMember> classMemberList;
    private MemberViewHolder.MemberViewHolderClickListener listener;

    public MemberAdapter(@Nullable List<ClassMember> classMemberList,
                         @NonNull MemberViewHolder.MemberViewHolderClickListener listener) {
        if (classMemberList == null) {
            classMemberList = new ArrayList<>();
        }
        this.classMemberList = classMemberList;
        this.listener = listener;
    }

    public void setData(List<ClassMember> classMemberList) {
        if (classMemberList == null) {
            classMemberList = new ArrayList<>();
        }
        Collections.sort(classMemberList, ClassMember.BY_USERNAME);
        this.classMemberList = classMemberList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.entry_member;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MemberViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        holder.bindData(classMemberList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return classMemberList.size();
    }
}
