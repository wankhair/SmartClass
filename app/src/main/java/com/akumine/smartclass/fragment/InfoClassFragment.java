package com.akumine.smartclass.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akumine.smartclass.activity.MainActivity;
import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.DatabaseUtil;
import com.akumine.smartclass.util.PermissionUtil;
import com.akumine.smartclass.util.PreferenceUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InfoClassFragment extends Fragment implements View.OnClickListener {

    private String[] list = {"Select number :", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
    private LinearLayout containerInfoClass;
    private LinearLayout containerEditInfoClass;
    private LinearLayout containerImageQR;
    private LinearLayout containerEditDeleteBtn;
    private LinearLayout containerCancelUpdateBtn;
    private TextView classInfoTitle;
    private TextView classInfoDesc;
    private TextView classMembers;
    private EditText classEditTitle;
    private EditText classEditDesc;
    private Spinner spinnerMember;
    private ImageView imageQr;
    private Button btnGenerateQr;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnCancel;
    private Button btnUpdate;
    private String uid;
    private String classId;
    private String className;
    private String classDesc;
    private String createDate;
    private String currentUser;
    private String maxUser;

//    private DatabaseReference deleteClass;
//    private DatabaseReference deleteClassMember;

    private boolean isQrGenerated = false;

    private Context context;

    public static InfoClassFragment newInstance(String uid, String class_id) {
        InfoClassFragment fragment = new InfoClassFragment();
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
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
            classId = getArguments().getString(Constant.ARGS_CLASS_ID);
        }

        containerInfoClass = view.findViewById(R.id.container_info_class);
        containerEditInfoClass = view.findViewById(R.id.container_edit_info_class);
        containerImageQR = view.findViewById(R.id.container_qr_image);
        containerEditDeleteBtn = view.findViewById(R.id.container_edit_delete_btn);
        containerCancelUpdateBtn = view.findViewById(R.id.container_cancel_update_btn);
        classInfoTitle = view.findViewById(R.id.class_info_title);
        classInfoDesc = view.findViewById(R.id.class_info_desc);
        classMembers = view.findViewById(R.id.class_members);
        classEditTitle = view.findViewById(R.id.class_edit_title);
        classEditDesc = view.findViewById(R.id.class_edit_desc);
        spinnerMember = view.findViewById(R.id.spinner_member);
        imageQr = view.findViewById(R.id.image_qr);
        btnGenerateQr = view.findViewById(R.id.btn_generate_qr);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnGenerateQr.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        String role = PreferenceUtil.getRole(context);

        if (role.equals(Constant.ROLE_LECTURER)) {
            containerImageQR.setVisibility(View.VISIBLE);
            containerEditDeleteBtn.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMember.setAdapter(adapter);

        getClassDetails(adapter);
    }

    private void getClassDetails(final ArrayAdapter<String> adapter) {
//        DatabaseReference tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
        DatabaseUtil.tableClassWithOneChild(classId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    classId = dataSnapshot.child(Classes.ID).getValue().toString();
                    className = dataSnapshot.child(Classes.CLASS_NAME).getValue().toString();
                    classDesc = dataSnapshot.child(Classes.CLASS_DESC).getValue().toString();
                    createDate = dataSnapshot.child(Classes.CREATED).getValue().toString();
                    currentUser = dataSnapshot.child(Classes.CURRENT_USER).getValue().toString();
                    maxUser = dataSnapshot.child(Classes.MAX_USER).getValue().toString();

                    classInfoTitle.setText(className);
                    classInfoDesc.setText(classDesc);
                    classMembers.setText(String.format(Locale.getDefault(), "%s Members", currentUser));
                    //classMembers.setText(currentUser + " Members");
                    classEditTitle.setText(className);
                    classEditDesc.setText(classDesc);

                    int position = adapter.getPosition(maxUser);
                    spinnerMember.setSelection(position);
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
            case R.id.btn_generate_qr:
                if (PermissionUtil.hasWritePermissionToExternalStorage(context)) {
                    performQrGeneration();
                } else {
                    Toast.makeText(context, "Write to Storage Permission Not Granted\nPlease go to Setting", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_edit:
                setEditMode();
                break;
            case R.id.btn_cancel:
                setNormalMode();
                break;
            case R.id.btn_delete:
                showDeleteDialogPopup();
                break;
            case R.id.btn_update:
                updateClassInformation();
                break;
        }
    }

    private void performQrGeneration() {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(classId, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            imageQr.setVisibility(View.VISIBLE);
            imageQr.setImageBitmap(bitmap);
            isQrGenerated = true;

            String fileName = "SmartClass" + System.currentTimeMillis() + ".jpg";

            File publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File dir = new File(publicDirectory.getAbsolutePath() + "/SmartClass/pictures");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File outFile = new File(dir, fileName);
            if (!outFile.exists()) {
                FileOutputStream outputStream = new FileOutputStream(outFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                addImageToGallery(outFile.getAbsolutePath(), context);
            }
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "QR Code Generated", Toast.LENGTH_SHORT).show();
    }

    private void setEditMode() {
        containerInfoClass.setVisibility(View.GONE);
        if (isQrGenerated) {
            containerImageQR.setVisibility(View.GONE);
        }
        containerEditDeleteBtn.setVisibility(View.GONE);

        containerEditInfoClass.setVisibility(View.VISIBLE);
        containerCancelUpdateBtn.setVisibility(View.VISIBLE);
    }

    private void setNormalMode() {
        containerInfoClass.setVisibility(View.VISIBLE);
        containerImageQR.setVisibility(View.VISIBLE);
        containerEditDeleteBtn.setVisibility(View.VISIBLE);

        containerEditInfoClass.setVisibility(View.GONE);
        containerCancelUpdateBtn.setVisibility(View.GONE);
    }

    private void showDeleteDialogPopup() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseUtil.tableClassWithOneChild(classId).removeValue();
                        DatabaseUtil.tableMemberWithOneChild(classId).removeValue();
//                        deleteClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
//                        deleteClass.removeValue();
//                        deleteClassMember = FirebaseDatabase.getInstance().getReference(ClassMember.DB_CLASSMEMBER).child(classId);
//                        deleteClassMember.removeValue();

                        MainActivity.start(context, uid);

                        Toast.makeText(context, "Class Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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

    private void updateClassInformation() {
        String classTitle = classEditTitle.getText().toString();
        String classDesc = classEditDesc.getText().toString();
        String selected = String.valueOf(spinnerMember.getSelectedItem());

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String modify = dateFormat.format(calendar.getTime());

        Classes classes = new Classes(classId, classTitle, classDesc, uid, createDate, modify, currentUser, selected);

        DatabaseUtil.tableClassWithOneChild(classId).setValue(classes);
//        DatabaseReference updateClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
//        updateClass.setValue(classes);

        Toast.makeText(context, "Class Information Updated", Toast.LENGTH_SHORT).show();

        setNormalMode();
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private static void addImageToGallery(final String filePath, final Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
