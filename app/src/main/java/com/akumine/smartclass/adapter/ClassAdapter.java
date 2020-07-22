package com.akumine.smartclass.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.view.ClassViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassViewHolder> {

    private List<Classes> classesList;
    private ClassViewHolder.ClassViewHolderClickListener listener;

    public ClassAdapter(@Nullable List<Classes> classesList,
                        @NonNull ClassViewHolder.ClassViewHolderClickListener listener) {
        if (classesList == null) {
            classesList = new ArrayList<>();
        }
        this.classesList = classesList;
        this.listener = listener;
    }

    public void setData(List<Classes> classesList) {
        if (classesList == null) {
            classesList = new ArrayList<>();
        }
        Collections.sort(classesList, Classes.BY_CREATED_DATE);
        this.classesList = classesList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.entry_class;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ClassViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClassViewHolder holder, int position) {
        holder.bindData(classesList.get(position));
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }
}
