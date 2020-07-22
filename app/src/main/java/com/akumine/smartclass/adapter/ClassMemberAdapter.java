package com.akumine.smartclass.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.view.ClassMemberViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassMemberAdapter extends RecyclerView.Adapter<ClassMemberViewHolder> {

    private List<ClassMember> classMemberList;
    private ClassMemberViewHolder.ClassMemberViewHolderClickListener listener;

    public ClassMemberAdapter(@Nullable List<ClassMember> classMemberList,
                              @NonNull ClassMemberViewHolder.ClassMemberViewHolderClickListener listener) {
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
        Collections.sort(classMemberList, ClassMember.BY_JOIN_DATE);
        this.classMemberList = classMemberList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.entry_class;
    }

    @NonNull
    @Override
    public ClassMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ClassMemberViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClassMemberViewHolder holder, int position) {
        holder.bindData(classMemberList.get(position));
    }

    @Override
    public int getItemCount() {
        return classMemberList.size();
    }
}
