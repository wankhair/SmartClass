package com.akumine.smartclass.classes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.adapter.PostAdapter;
import com.akumine.smartclass.classes.AddPostActivity;
import com.akumine.smartclass.classes.ClickPostActivity;
import com.akumine.smartclass.model.Post;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.view.PostViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment implements View.OnClickListener,
        PostViewHolder.PostViewHolderClickListener {

    private PostAdapter postAdapter;
    private String uid;
    private String classId;

    private Context context;

    public static PostFragment newInstance(String uid, String class_id) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_USER_ID, uid);
        args.putString(Constant.ARGS_CLASS_ID, class_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
            classId = getArguments().getString(Constant.ARGS_CLASS_ID);
        }

        FloatingActionButton fabBtn = view.findViewById(R.id.fab_btn);
        fabBtn.setIcon(R.drawable.ic_edit_white);
        fabBtn.setOnClickListener(this);

        postAdapter = new PostAdapter(null, this);

        RecyclerView recyclerPost = view.findViewById(R.id.recycler_post);
        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(new LinearLayoutManager(context));
        recyclerPost.setAdapter(postAdapter);

        getPostLists();
    }

    private void getPostLists() {
        DatabaseReference tablePost = FirebaseDatabase.getInstance().getReference(Post.DB_POST);
        Query query = tablePost.orderByChild(Post.DB_COLUMN_CLASS_ID).equalTo(classId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Post> postList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post post = snapshot.getValue(Post.class);
                        postList.add(post);
                    }
                    postAdapter.setData(postList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_btn) {
            AddPostActivity.start(context, uid, classId);
        }
    }

    @Override
    public void onPostItemClick(String postId) {
        ClickPostActivity.start(context, uid, postId);
    }
}
