package com.akumine.smartclass.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Post;
import com.akumine.smartclass.view.PostViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private List<Post> postList;
    private PostViewHolder.PostViewHolderClickListener listener;

    public PostAdapter(@Nullable List<Post> postList,
                       @NonNull PostViewHolder.PostViewHolderClickListener listener) {
        if (postList == null) {
            postList = new ArrayList<>();
        }
        this.postList = postList;
        this.listener = listener;
    }

    public void setData(List<Post> postList) {
        if (postList == null) {
            postList = new ArrayList<>();
        }
        Collections.sort(postList, Post.BY_CREATED_DATE);
        this.postList = postList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.entry_post;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PostViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        holder.bindData(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
