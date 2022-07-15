package com.akumine.smartclass.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.DatabaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UserInfoActivity";

    private StorageReference storageReference;

    private ImageView profileImage;
    private EditText profileUsername;
    private EditText profilePhoneNo;
    private RadioGroup role;
    private Button submit;
    private Uri filePath;

    private String image = "none";
    private String uid;
    private String email;

    public static void start(Context context, String uid, String email) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        intent.putExtra(Constant.EXTRA_EMAIL, email);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Setup");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uid = bundle.getString(Constant.EXTRA_USER_ID);
            email = bundle.getString(Constant.EXTRA_EMAIL);
        }

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileUsername = (EditText) findViewById(R.id.profile_username);
        profilePhoneNo = (EditText) findViewById(R.id.profile_phone_no);
        role = (RadioGroup) findViewById(R.id.role);
        submit = (Button) findViewById(R.id.submit);
        profileImage.setOnClickListener(this);
        submit.setOnClickListener(this);

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                String name = profileUsername.getText().toString();
                String pNo = profilePhoneNo.getText().toString();
                int selectId = role.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectId);

                if (isUserInformationValidated(name, pNo)) {
                    if (filePath == null) {
                        //save data into db without user profile picture
                        User user = new User(uid, name, email, radioButton.getText().toString(), image, pNo);
                        DatabaseUtil.tableUserWithOneChild(uid).setValue(user);
                    } else {
                        //save data and image url into db
                        updateUserInformation(name, pNo, radioButton);
                    }
                    Toast.makeText(UserInfoActivity.this, "User information updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.profile_image:
                Intent intentPickImage = new Intent();
                intentPickImage.setType("image/*");
                intentPickImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentPickImage, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
        }
    }

    private boolean isUserInformationValidated(String name, String pNo) {
        View viewToFocus = null;

        if (name.isEmpty()) {
            profileUsername.setError(Constant.ERROR_USERNAME_EMPTY);
            viewToFocus = profileUsername;
        } else {
            profileUsername.setError(null);
        }

        if (pNo.isEmpty()) {
            profilePhoneNo.setError(Constant.ERROR_PHONE_NUM_EMPTY);
            viewToFocus = profilePhoneNo;
        } else {
            profilePhoneNo.setError(null);
        }

        String pattern = "^[+]?[0-9-]{10,13}$";
        if (!pNo.matches(pattern)) {
            profilePhoneNo.setError(Constant.ERROR_PHONE_NUM_INVALID);
            viewToFocus = profilePhoneNo;
        } else {
            profilePhoneNo.setError(null);
        }

        if (viewToFocus != null) {
            viewToFocus.requestFocus();
            return false;
        }
        return true;
    }

    private void updateUserInformation(final String name, final String pNo, final RadioButton radioButton) {
        // compress image
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        }
        byte[] fileInBytes = stream.toByteArray();

        // store image into firebase
        final StorageReference reference = storageReference.child("User Image/" + uid);
        reference.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        image = uri.toString();

                        User user = new User(uid, name, email, radioButton.getText().toString(), image, pNo);
                        DatabaseUtil.tableUserWithOneChild(uid).setValue(user);

                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if (task.isSuccessful()) {
                                            //add device token
                                            String deviceToken = task.getResult().getToken();
                                            DatabaseUtil.tableUserWithTwoChild(uid, User.DEVICE_TOKEN).setValue(deviceToken);
                                        } else {
                                            Log.w(TAG, "getInstanceId failed", task.getException());
                                        }

                                    }
                                });
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(UserInfoActivity.this, "Required to fill in all information", Toast.LENGTH_SHORT).show();
    }
}
