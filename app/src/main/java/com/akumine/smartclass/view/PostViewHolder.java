package com.akumine.smartclass.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Post;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.DatabaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface PostViewHolderClickListener {
        void onPostItemClick(String postId);
    }

    private Post post;
    private ImageView postProfileImage;
    private ImageView imagePost;
    private TextView username;
    private TextView date;
    private TextView time;
    private TextView postText;

    private PostViewHolderClickListener listener;

    public PostViewHolder(View itemView, PostViewHolderClickListener listener) {
        super(itemView);
        postProfileImage = itemView.findViewById(R.id.post_profile_image);
        username = itemView.findViewById(R.id.post_username);
        date = itemView.findViewById(R.id.post_date);
        time = itemView.findViewById(R.id.post_time);
        postText = itemView.findViewById(R.id.post_text);
        imagePost = itemView.findViewById(R.id.post_image);
        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    public void bindData(Post post) {
        this.post = post;
        String postUserId = post.getUserId();

//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(postUserId);
        DatabaseUtil.tableUserWithOneChild(postUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String lecName = dataSnapshot.child(User.USERNAME).getValue().toString();
                    String image = dataSnapshot.child(User.IMAGE).getValue().toString();

                    username.setText(lecName);
                    Picasso.get().load(image).into(postProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        date.setText(post.getDate());
        time.setText(post.getTime());

        if (!TextUtils.isEmpty(post.getText())) {
            postText.setText(post.getText());
        } else {
            postText.setVisibility(View.GONE);
        }

        if (post.getImageUrl() != null) {
            Picasso.get().load(post.getImageUrl()).into(imagePost);
        } else {
            imagePost.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onPostItemClick(post.getId());
            }
        }
    }
}
