package com.akumine.smartclass.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PreferenceUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "ProfileFragment";

    private LinearLayout containerProfile;
    private CircleImageView profileImage;
    private TextView profileTextName;
    private TextView profileTextEmail;
    private TextView profileTextPhoneNo;

    private LinearLayout containerEditProfile;
    private CircleImageView profileEditImage;
    private EditText profileEditName;
    private EditText profileEditEmail;
    private EditText profileEditPhoneNo;

    private Button btnEditProfile;
    private Button btnUpdateProfile;

    private Uri filePath;
    private DatabaseReference tableUser;
    private StorageReference storageReference;

    private String uid;
    private String role;
    private String image = "none";

    private Context context;

    public static ProfileFragment newInstance(String uid) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        containerProfile = (LinearLayout) view.findViewById(R.id.container_profile);
        profileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        profileTextName = (TextView) view.findViewById(R.id.profile_text_name);
        profileTextEmail = (TextView) view.findViewById(R.id.profile_text_email);
        profileTextPhoneNo = (TextView) view.findViewById(R.id.profile_text_phone_no);

        containerEditProfile = (LinearLayout) view.findViewById(R.id.container_edit_profile);
        profileEditImage = (CircleImageView) view.findViewById(R.id.profile_edit_image);
        profileEditName = (EditText) view.findViewById(R.id.profile_edit_name);
        profileEditEmail = (EditText) view.findViewById(R.id.profile_edit_email);
        profileEditPhoneNo = (EditText) view.findViewById(R.id.profile_edit_phone_no);

        btnEditProfile = (Button) view.findViewById(R.id.profile_edit_btn);
        btnUpdateProfile = (Button) view.findViewById(R.id.profile_update_btn);

        btnEditProfile.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        profileEditImage.setOnClickListener(this);

        role = PreferenceUtil.getRole(context);
    }

    @Override
    public void onStart() {
        super.onStart();

        tableUser = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(uid);
        tableUser.keepSynced(true);
        tableUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child(User.DB_COLUMN_USERNAME).getValue().toString();
                    String email = dataSnapshot.child(User.DB_COLUMN_EMAIL).getValue().toString();
                    String imageUrl = dataSnapshot.child(User.DB_COLUMN_IMAGE).getValue().toString();
                    String phone = dataSnapshot.child(User.DB_COLUMN_PHONE).getValue().toString();
                    String role = dataSnapshot.child(User.DB_COLUMN_ROLE).getValue().toString();

                    image = imageUrl;
                    profileTextName.setText(username);
                    profileEditName.setText(username);
                    profileTextEmail.setText(email);
                    profileEditEmail.setText(email);
                    profileTextPhoneNo.setText(phone);
                    profileEditPhoneNo.setText(phone);

                    if (!image.equals("none")) {
                        Picasso.get().load(image).into(profileImage);
                        Picasso.get().load(image).into(profileEditImage);
                    } else {
                        Picasso.get().load(R.drawable.user_vector).into(profileImage);
                        Picasso.get().load(R.drawable.user_vector).into(profileEditImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_edit_btn:
                containerProfile.setVisibility(View.GONE);
                btnEditProfile.setVisibility(View.GONE);
                containerEditProfile.setVisibility(View.VISIBLE);
                btnUpdateProfile.setVisibility(View.VISIBLE);
                break;
            case R.id.profile_update_btn:
                String username = profileEditName.getText().toString();
                String email = profileEditEmail.getText().toString();
                String phoneNo = profileEditPhoneNo.getText().toString();

                if (isProfileInformationValidated(username, email, phoneNo)) {
                    if (filePath != null) {
                        //update db with new image
                        updateProfileInformation(username, email, phoneNo, role);
                    } else {
                        //update db with current image
                        User user = new User(uid, username, email, role, image, phoneNo);
                        tableUser.setValue(user);
                    }
                    Toast.makeText(context, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();
                }

                containerProfile.setVisibility(View.VISIBLE);
                btnEditProfile.setVisibility(View.VISIBLE);
                containerEditProfile.setVisibility(View.GONE);
                btnUpdateProfile.setVisibility(View.GONE);
                break;
            case R.id.profile_edit_image:
                Intent intentPickImage = new Intent();
                intentPickImage.setType("image/*");
                intentPickImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentPickImage, Constant.REQUEST_CODE_PICK_IMAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }

    private boolean isProfileInformationValidated(String name, String email, String pNo) {
        View viewToFocus = null;

        if (name.isEmpty()) {
            profileEditName.setError(Constant.ERROR_USERNAME_EMPTY);
            viewToFocus = profileEditName;
        } else {
            profileEditName.setError(null);
        }

        if (email.isEmpty()) {
            profileEditEmail.setError(Constant.ERROR_EMAIL_EMPTY);
            viewToFocus = profileEditEmail;
        } else {
            profileEditEmail.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            profileEditEmail.setError(Constant.ERROR_EMAIL_INVALID);
            viewToFocus = profileEditEmail;
        } else {
            profileEditEmail.setError(null);
        }

        if (pNo.isEmpty()) {
            profileEditPhoneNo.setError(Constant.ERROR_PHONE_NUM_EMPTY);
            viewToFocus = profileEditPhoneNo;
        } else {
            profileEditPhoneNo.setError(null);
        }

        String pattern = "^[+]?[0-9-]{10,13}$";
        if (!pNo.matches(pattern)) {
            profileEditPhoneNo.setError(Constant.ERROR_PHONE_NUM_INVALID);
            viewToFocus = profileEditPhoneNo;
        } else {
            profileEditPhoneNo.setError(null);
        }

        if (viewToFocus != null) {
            viewToFocus.requestFocus();
            return false;
        }
        return true;
    }

    private void updateProfileInformation(final String name, final String email, final String pNo, final String userRole) {
        //to compress image
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        }
        byte[] fileInBytes = stream.toByteArray();

        final StorageReference reference = storageReference.child("User Image/" + uid);
        reference.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        image = uri.toString();

                        User user = new User(uid, name, email, userRole, image, pNo);
                        tableUser.setValue(user);
                    }
                });
            }
        });
    }
}
