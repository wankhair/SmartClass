package com.akumine.smartclass.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.adapter.PostAdapter;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Post;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.view.PostViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment implements PostViewHolder.PostViewHolderClickListener {

    public static final String TAG = "NewsFeedFragment";

    private RecyclerView recyclerPost;
    private PostAdapter postAdapter;

    private String uid;

    private Context context;

    public static NewsFeedFragment newInstance(String uid) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_USER_ID, uid);
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
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
        }

        postAdapter = new PostAdapter(null, this);

        recyclerPost = view.findViewById(R.id.recycler_post);
        recyclerPost.setHasFixedSize(true);
        recyclerPost.setLayoutManager(new LinearLayoutManager(context));
        recyclerPost.setAdapter(postAdapter);

        getClassIdFromClassMember();
    }

    private void getClassIdFromClassMember() {
        DatabaseReference tableClassMember = FirebaseDatabase.getInstance().getReference().child(ClassMember.DB_CLASSMEMBER);
        Query queryClassMember = tableClassMember.orderByChild(ClassMember.DB_COLUMN_MEMBER_ID).equalTo(uid);
        queryClassMember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ClassMember classMember = snapshot.getValue(ClassMember.class);
                        assert classMember != null;
                        String classId = classMember.getClassId();

                        getPostItems(classId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getPostItems(String classId) {
        DatabaseReference tablePost = FirebaseDatabase.getInstance().getReference().child(Post.DB_POST);
        Query queryPost = tablePost.orderByChild(Post.DB_COLUMN_CLASS_ID).equalTo(classId);
        queryPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Post> postList = new ArrayList<>();
                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                        Post post = snapshot1.getValue(Post.class);
                        postList.add(post);
                    }
                    if (postList.isEmpty()) {
                        Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
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
    public void onPostItemClick(String postId) {
        //do nothing since ic_student only can view
    }
}
