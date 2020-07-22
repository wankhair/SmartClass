package com.akumine.smartclass.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Post;
import com.akumine.smartclass.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView postImage;
    private TextView postText;
    private Button btnEditPost;
    private Button btnDeletePost;

    private DatabaseReference tablePost;
    private DatabaseReference tableDeletePost;

    private String uid;
    private String postId;
    private String text;

    public static void start(Context context, String uid, String postId) {
        Intent intent = new Intent(context, ClickPostActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        intent.putExtra(Constant.EXTRA_POST_ID, postId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        uid = intent.getStringExtra(Constant.EXTRA_USER_ID);
        postId = intent.getStringExtra(Constant.EXTRA_POST_ID);

        postImage = (ImageView) findViewById(R.id.edit_post_image);
        postText = (TextView) findViewById(R.id.edit_post_text);
        btnEditPost = (Button) findViewById(R.id.btn_edit_post);
        btnDeletePost = (Button) findViewById(R.id.btn_delete_post);

        btnEditPost.setVisibility(View.INVISIBLE);
        btnDeletePost.setVisibility(View.INVISIBLE);

        tablePost = FirebaseDatabase.getInstance().getReference(Post.DB_POST).child(postId);
        tablePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    text = dataSnapshot.child(Post.DB_COLUMN_TEXT).getValue().toString();
                    String image = dataSnapshot.child(Post.DB_COLUMN_IMAGE_URL).getValue().toString();
                    String user = dataSnapshot.child(Post.DB_COLUMN_USER_ID).getValue().toString();

                    postText.setText(text);
                    Picasso.get().load(image).into(postImage);

                    //if yes, only owner can edit & delete
                    //to set listener
                    checkOwnerOfPost(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkOwnerOfPost(String user) {
        if (uid.equals(user)) {
            btnEditPost.setVisibility(View.VISIBLE);
            btnEditPost.setOnClickListener(this);
            btnDeletePost.setVisibility(View.VISIBLE);
            btnDeletePost.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_edit_post) {
            View viewEditPost = getLayoutInflater().inflate(R.layout.dialog_view_edit_post, null);
            final EditText inputField = (EditText) viewEditPost.findViewById(R.id.post_edit_text);
            inputField.setText(text);

            Button btnUpdate = (Button) viewEditPost.findViewById(R.id.action_update);
            Button btnDismiss = (Button) viewEditPost.findViewById(R.id.action_dismiss);

            final AlertDialog alertDialog = new AlertDialog.Builder(ClickPostActivity.this)
                    .setView(viewEditPost)
                    .setCancelable(false)
                    .create();
            alertDialog.show();

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = inputField.getText().toString();
                    if (!TextUtils.isEmpty(message)) {
                        tablePost.child(Post.DB_COLUMN_TEXT).setValue(message);
                        Toast.makeText(ClickPostActivity.this, "Post Successfully Updated!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(ClickPostActivity.this, "Please enter text", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

        } else if (view.getId() == R.id.btn_delete_post) {

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tableDeletePost = FirebaseDatabase.getInstance().getReference(Post.DB_POST).child(postId);
                            tableDeletePost.removeValue();

                            Toast.makeText(ClickPostActivity.this, "Post Successfully Deleted!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
