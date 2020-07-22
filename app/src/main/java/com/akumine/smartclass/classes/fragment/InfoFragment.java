package com.akumine.smartclass.classes.fragment;

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

import com.akumine.smartclass.MainActivity;
import com.akumine.smartclass.R;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PermissionUtil;
import com.akumine.smartclass.util.PreferenceUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class InfoFragment extends Fragment implements View.OnClickListener {

    private String[] list = {"Select number :", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
    private TextView classInfoTitle;
    private TextView classInfoDesc;
    private TextView classMembers;
    private EditText classEditTitle;
    private EditText classEditDesc;
    private Spinner spinnerMember;
    private ImageView imageQr;
    private LinearLayout layoutViewQr;
    private LinearLayout layoutSpinner;
    private LinearLayout layoutEditDelete;
    private LinearLayout layoutCancelUpdate;
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

    private DatabaseReference deleteClass;
    private DatabaseReference deleteClassMember;

    private boolean isQrGenerated = false;

    private Context context;

    public static InfoFragment newInstance(String uid, String class_id) {
        InfoFragment fragment = new InfoFragment();
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

        classInfoTitle = view.findViewById(R.id.class_info_title);
        classInfoDesc = view.findViewById(R.id.class_info_desc);
        classMembers = view.findViewById(R.id.class_members);
        classEditTitle = view.findViewById(R.id.class_edit_title);
        classEditDesc = view.findViewById(R.id.class_edit_desc);
        spinnerMember = view.findViewById(R.id.spinner_member);
        layoutViewQr = view.findViewById(R.id.layout_view_qr);
        layoutSpinner = view.findViewById(R.id.layout_spinner);
        layoutEditDelete = view.findViewById(R.id.layout_edit_delete);
        layoutCancelUpdate = view.findViewById(R.id.layout_cancel_update);
        imageQr = view.findViewById(R.id.image_qr);
        btnGenerateQr = view.findViewById(R.id.btn_generate_qr);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnUpdate = view.findViewById(R.id.btn_update);

        String role = PreferenceUtil.getRole(context);

        if (role != null && role.equals(Constant.ROLE_LECTURER)) {
            btnGenerateQr.setVisibility(View.VISIBLE);
            layoutEditDelete.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMember.setAdapter(adapter);

        setupUserInterface(adapter);
    }

    private void setupUserInterface(final ArrayAdapter<String> adapter) {
        DatabaseReference tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
        tableClass.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    classId = dataSnapshot.child(Classes.DB_COLUMN_ID).getValue().toString();
                    className = dataSnapshot.child(Classes.DB_COLUMN_NAME).getValue().toString();
                    classDesc = dataSnapshot.child(Classes.DB_COLUMN_DESC).getValue().toString();
                    createDate = dataSnapshot.child(Classes.DB_COLUMN_CREATED).getValue().toString();
                    currentUser = dataSnapshot.child(Classes.DB_COLUMN_CURRENT_USER).getValue().toString();
                    maxUser = dataSnapshot.child(Classes.DB_COLUMN_MAX_USER).getValue().toString();

                    classInfoTitle.setText(className);
                    classInfoDesc.setText(classDesc);
                    classMembers.setText(currentUser + " Members");
                    classEditTitle.setText(className);
                    classEditDesc.setText(classDesc);

                    int position = adapter.getPosition(maxUser);
                    spinnerMember.setSelection(position);

                    initializeButtonOnClickListener();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initializeButtonOnClickListener() {
        btnGenerateQr.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_generate_qr:
                performQrGeneration();
                break;
            case R.id.btn_edit:
                setEditVisibility();
                break;
            case R.id.btn_cancel:
                setCancelVisibility();
                break;
            case R.id.btn_delete:
                showDeleteDialogPopup();
                break;
            case R.id.btn_update:
                updateClassInformation();
                break;
        }
    }

    //------------------------Local Method----------------------------------//

    private void performQrGeneration() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(classId, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageQr.setImageBitmap(bitmap);
            layoutViewQr.setVisibility(View.VISIBLE);
            isQrGenerated = true;

            FileOutputStream outputStream;
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/SmartClass/pictures");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = "SmartClass" + System.currentTimeMillis() + ".jpg";
            File outFile = new File(directory, fileName);
            outputStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            if (PermissionUtil.hasWritePermissionToExternalStorage(context)) {
                addImageToGallery(outFile.getAbsolutePath(), context);
            } else {
                Toast.makeText(context, "Write to Storage Permission Not Granted\nPlease go to Setting", Toast.LENGTH_SHORT).show();
            }

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "QR Code Generated", Toast.LENGTH_SHORT).show();
    }

    private void setEditVisibility() {
        classInfoTitle.setVisibility(View.GONE);
        classInfoDesc.setVisibility(View.GONE);
        classMembers.setVisibility(View.GONE);
        btnGenerateQr.setVisibility(View.GONE);
        if (isQrGenerated) {
            layoutViewQr.setVisibility(View.GONE);
        }
        layoutEditDelete.setVisibility(View.GONE);

        classEditTitle.setVisibility(View.VISIBLE);
        classEditDesc.setVisibility(View.VISIBLE);
        layoutSpinner.setVisibility(View.VISIBLE);
        layoutCancelUpdate.setVisibility(View.VISIBLE);
    }

    private void setCancelVisibility() {
        classInfoTitle.setVisibility(View.VISIBLE);
        classInfoDesc.setVisibility(View.VISIBLE);
        classMembers.setVisibility(View.VISIBLE);
        btnGenerateQr.setVisibility(View.VISIBLE);
        if (isQrGenerated) {
            layoutViewQr.setVisibility(View.VISIBLE);
        }
        layoutEditDelete.setVisibility(View.VISIBLE);

        classEditTitle.setVisibility(View.GONE);
        classEditDesc.setVisibility(View.GONE);
        layoutSpinner.setVisibility(View.GONE);
        layoutCancelUpdate.setVisibility(View.GONE);

        Toast.makeText(context, "You Cancelled Edit", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialogPopup() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
                        deleteClass.removeValue();

                        deleteClassMember = FirebaseDatabase.getInstance().getReference(ClassMember.DB_CLASSMEMBER).child(classId);
                        deleteClassMember.removeValue();

                        MainActivity.start(context, uid);

                        Toast.makeText(context, "Class Successfully Deleted!", Toast.LENGTH_SHORT).show();
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

    private void updateClassInformation() {
        final String classTitle = classEditTitle.getText().toString();
        final String classDesc = classEditDesc.getText().toString();
        String selected = String.valueOf(spinnerMember.getSelectedItem());

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String modify = dateFormat.format(calendar.getTime());

        Classes classes = new Classes(classId, classTitle, classDesc, uid, createDate, modify, currentUser, selected);

        DatabaseReference updateClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
        updateClass.setValue(classes);

        Toast.makeText(context, "Class Information Updated", Toast.LENGTH_SHORT).show();

        classInfoTitle.setVisibility(View.VISIBLE);
        classInfoDesc.setVisibility(View.VISIBLE);
        classMembers.setVisibility(View.VISIBLE);
        btnGenerateQr.setVisibility(View.VISIBLE);
        layoutViewQr.setVisibility(View.VISIBLE);
        layoutEditDelete.setVisibility(View.VISIBLE);

        classEditTitle.setVisibility(View.GONE);
        classEditDesc.setVisibility(View.GONE);
        layoutSpinner.setVisibility(View.GONE);
        layoutCancelUpdate.setVisibility(View.GONE);
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
