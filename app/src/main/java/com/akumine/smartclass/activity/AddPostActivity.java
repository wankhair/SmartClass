package com.akumine.smartclass.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Notification;
import com.akumine.smartclass.model.Post;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PermissionUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    private String uid;
    private String classId;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String created;
    private EditText postEditText;
    private ImageView postImage;
    private ImageView postAddImage;
    private ImageView sendPost;
    private ProgressDialog progressDialog;

    private Uri targetUri;
    private DatabaseReference tableNotify;
    private DatabaseReference tablePost;
    private DatabaseReference tableClassMember;
    private StorageReference postImagesReference;

    public static void start(Context context, String uid, String classId) {
        Intent intent = new Intent(context, AddPostActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        intent.putExtra(Constant.EXTRA_CLASS_ID, classId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Post");
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
        classId = intent.getStringExtra(Constant.EXTRA_CLASS_ID);

        postImagesReference = FirebaseStorage.getInstance().getReference();
        tablePost = FirebaseDatabase.getInstance().getReference().child(Post.DB_POST);
        tableNotify = FirebaseDatabase.getInstance().getReference().child(Notification.DB_NOTIFICATION);

        postEditText = (EditText) findViewById(R.id.post_text);
        postImage = (ImageView) findViewById(R.id.post_image);
        postAddImage = (ImageView) findViewById(R.id.post_add_image);
        sendPost = (ImageView) findViewById(R.id.send_post);
        postAddImage.setOnClickListener(this);
        sendPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.post_add_image) {
            if (PermissionUtil.hasCameraPermission(this)) {
                // to select image
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
            } else {
                Toast.makeText(this, "Camera Permission Not Granted\nPlease go to Setting", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.send_post) {
            String postText = postEditText.getText().toString();
            if (postText.isEmpty()) {
                postText = "";
            }
            uploadPost(postText);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            targetUri = data.getData();
            postImage.setImageURI(targetUri);
        } else {
            Toast.makeText(AddPostActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPost(final String postText) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading Post...");
        progressDialog.setProgress(0);
        progressDialog.show();

        Calendar date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault());
        saveCurrentDate = currentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        saveCurrentTime = currentTime.format(time.getTime());

        String constructedImageName = saveCurrentDate + saveCurrentTime;

        //to compress the image
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), targetUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        }
        byte[] fileInBytes = stream.toByteArray();

        final StorageReference filepath = postImagesReference.child(Constant.STORAGE_POST_IMAGES)
                .child(targetUri.getLastPathSegment() + constructedImageName + ".jpg");

        filepath.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String id = UUID.randomUUID().toString();
                        String imageUrl = uri.toString();

                        Calendar calendar = Calendar.getInstance();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                        created = dateFormat.format(calendar.getTime());

                        Post post = new Post(id, postText, imageUrl, saveCurrentDate, saveCurrentTime, classId, uid, created);
                        tablePost.child(id).setValue(post);

                        tableClassMember = FirebaseDatabase.getInstance().getReference().child(ClassMember.DB_CLASSMEMBER);
                        tableClassMember.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ClassMember member = snapshot.getValue(ClassMember.class);
                                        assert member != null;
                                        if (member.getClassId().equals(classId)) {
                                            String memberId = member.getMemberId();

                                            Notification notification = new Notification("New Post", uid, classId);
                                            tableNotify.child(memberId).push().setValue(notification);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Toast.makeText(AddPostActivity.this, "Post successfully uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPostActivity.this, "Post not successfully uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    private boolean isPostInformationValidated(String postText) {
        if (targetUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(postText)) {
            Toast.makeText(this, "Please write your post first", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
