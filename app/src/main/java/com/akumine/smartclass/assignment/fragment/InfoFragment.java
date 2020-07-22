package com.akumine.smartclass.assignment.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akumine.smartclass.R;
import com.akumine.smartclass.classes.MainClassActivity;
import com.akumine.smartclass.model.Assignments;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InfoFragment extends Fragment implements View.OnClickListener {

    private TextView assignInfoTitle;
    private TextView assignInfoDesc;
    private TextView dueDateInfo;
    private TextView docName;
    private TextView datePicker;
    private TextView timePicker;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnCancel;
    private Button btnUpdate;
    private Button btnDownload;
    private ImageView imageQr;
    private LinearLayout layoutDateTime;
    private LinearLayout layoutEditDelete;
    private LinearLayout layoutCancelUpdate;
    private LinearLayout layoutDownload;
    private LinearLayout layoutViewQr;
    private EditText assignEditTitle;
    private EditText assignEditDesc;
    private String assignmentName;
    private String assignmentDescription;
    private String documentUrl;
    private String documentName;
    private String date;
    private String time;
    private String createDate;

    private String uid;
    private String classId;
    private String assignId;

    private Context context;

    public static InfoFragment newInstance(String uid, String class_id, String assignId) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_USER_ID, uid);
        args.putString(Constant.ARGS_CLASS_ID, class_id);
        args.putString(Constant.ARGS_ASSIGN_ID, assignId);
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
        return inflater.inflate(R.layout.fragment_info_assign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
            classId = getArguments().getString(Constant.ARGS_CLASS_ID);
            assignId = getArguments().getString(Constant.ARGS_ASSIGN_ID);
        }

        assignInfoTitle = view.findViewById(R.id.assign_info_title);
        assignInfoDesc = view.findViewById(R.id.assign_info_desc);
        assignEditTitle = view.findViewById(R.id.assign_edit_title);
        assignEditDesc = view.findViewById(R.id.assign_edit_desc);
        dueDateInfo = view.findViewById(R.id.due_date_info);
        datePicker = view.findViewById(R.id.date_picker);
        timePicker = view.findViewById(R.id.time_picker);
        docName = view.findViewById(R.id.document_name);
        btnDownload = view.findViewById(R.id.btn_download);
        imageQr = view.findViewById(R.id.image_qr);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnUpdate = view.findViewById(R.id.btn_update);
        layoutDateTime = view.findViewById(R.id.layout_date_time);
        layoutEditDelete = view.findViewById(R.id.layout_edit_delete);
        layoutCancelUpdate = view.findViewById(R.id.layout_cancel_update);
        layoutDownload = view.findViewById(R.id.layout_download);
        layoutViewQr = view.findViewById(R.id.layout_view_qr);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        datePicker.setOnClickListener(this);
        timePicker.setOnClickListener(this);
        btnDownload.setOnClickListener(this);

        String role = PreferenceUtil.getRole(context);

        if (role != null) {
            if (role.equals(Constant.ROLE_LECTURER)) {
                layoutEditDelete.setVisibility(View.VISIBLE);
            } else if (role.equals(Constant.ROLE_STUDENT)) {
                layoutDownload.setVisibility(View.VISIBLE);
            }
        }

        getDataFromDB();
    }

    private void getDataFromDB() {
        DatabaseReference tableAssignment = FirebaseDatabase.getInstance().getReference(Assignments.DB_ASSIGNMENT).child(assignId);
        tableAssignment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    assignId = dataSnapshot.child(Assignments.DB_COLUMN_ID).getValue().toString();
                    assignmentName = dataSnapshot.child(Assignments.DB_COLUMN_NAME).getValue().toString();
                    assignmentDescription = dataSnapshot.child(Assignments.DB_COLUMN_DESC).getValue().toString();
                    documentUrl = dataSnapshot.child(Assignments.DB_COLUMN_DOC_NAME).getValue().toString();
                    documentName = dataSnapshot.child(Assignments.DB_COLUMN_DOC_URL).getValue().toString();
                    date = dataSnapshot.child(Assignments.DB_COLUMN_DATE).getValue().toString();
                    time = dataSnapshot.child(Assignments.DB_COLUMN_TIME).getValue().toString();
                    createDate = dataSnapshot.child(Assignments.DB_COLUMN_CREATED).getValue().toString();
                    classId = dataSnapshot.child(Assignments.DB_COLUMN_CLASS_ID).getValue().toString();

                    assignInfoTitle.setText(assignmentName);
                    assignInfoDesc.setText(assignmentDescription);
                    dueDateInfo.setText(date + "  " + time);
                    datePicker.setText(date);
                    timePicker.setText(time);
                    docName.setText(documentName);
                    assignEditTitle.setText(assignmentName);
                    assignEditDesc.setText(assignmentDescription);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        Calendar calendar = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.btn_edit:
                setEditVisibility();
                break;
            case R.id.btn_cancel:
                setCancelVisibility();
                break;
            case R.id.btn_delete:
                showDeleteDialog();
                break;
            case R.id.btn_update:
                updateAssignmentInformation(calendar);
                break;
            case R.id.date_picker:
                getDate(calendar);
                break;
            case R.id.time_picker:
                getTime(calendar);
                break;
            case R.id.btn_download:
                performQrGenerationAndDownloadFile();
                break;
        }
    }

    private void setEditVisibility() {
        layoutEditDelete.setVisibility(View.GONE);
        assignInfoTitle.setVisibility(View.GONE);
        assignInfoDesc.setVisibility(View.GONE);
        dueDateInfo.setVisibility(View.GONE);

        assignEditTitle.setVisibility(View.VISIBLE);
        assignEditDesc.setVisibility(View.VISIBLE);
        layoutDateTime.setVisibility(View.VISIBLE);
        layoutCancelUpdate.setVisibility(View.VISIBLE);
    }

    private void setCancelVisibility() {
        layoutEditDelete.setVisibility(View.VISIBLE);
        assignInfoTitle.setVisibility(View.VISIBLE);
        assignInfoDesc.setVisibility(View.VISIBLE);
        dueDateInfo.setVisibility(View.VISIBLE);

        assignEditTitle.setVisibility(View.GONE);
        assignEditDesc.setVisibility(View.GONE);
        layoutDateTime.setVisibility(View.GONE);
        layoutCancelUpdate.setVisibility(View.GONE);

        Toast.makeText(context, "You Cancelled Edit", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Delete Assignment")
                .setMessage("Are you sure you want to delete this assignment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference deleteAssignment = FirebaseDatabase.getInstance().getReference(Assignments.DB_ASSIGNMENT).child(assignId);
                        deleteAssignment.removeValue();

                        MainClassActivity.start(context, uid, classId);
                        getActivity().finish();

                        Toast.makeText(context, "Assignment Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void updateAssignmentInformation(Calendar calendar) {
        String assignTitle = assignEditTitle.getText().toString();
        String assignDesc = assignEditDesc.getText().toString();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String modify = dateFormat.format(calendar.getTime());

        Assignments assignments = new Assignments(assignId, assignTitle, assignDesc,
                documentUrl, documentName,
                datePicker.getText().toString(),
                timePicker.getText().toString(),
                createDate, modify, classId);

        DatabaseReference editAssignment = FirebaseDatabase.getInstance().getReference(Assignments.DB_ASSIGNMENT).child(assignId);
        editAssignment.setValue(assignments);

        Toast.makeText(context, "Assignment Information Updated", Toast.LENGTH_SHORT).show();

        layoutEditDelete.setVisibility(View.VISIBLE);
        assignInfoTitle.setVisibility(View.VISIBLE);
        assignInfoDesc.setVisibility(View.VISIBLE);
        dueDateInfo.setVisibility(View.VISIBLE);

        assignEditTitle.setVisibility(View.GONE);
        assignEditDesc.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);
        layoutCancelUpdate.setVisibility(View.GONE);
    }

    private void getDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                datePicker.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void getTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay + ":" + minute;

                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date date = null;
                try {
                    date = fmt.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                String formattedTime = null;
                if (date != null) {
                    formattedTime = fmtOut.format(date);
                }

                timePicker.setText(formattedTime);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void performQrGenerationAndDownloadFile() {
        if (PermissionUtil.hasWritePermissionToExternalStorage(context)) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(documentUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(documentName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverRoaming(false);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir("/SmartClass/documents", documentName);
            downloadManager.enqueue(request);

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(classId + "/" + assignId + "/" + uid,
                        BarcodeFormat.QR_CODE, 300, 300);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageQr.setImageBitmap(bitmap);
                layoutViewQr.setVisibility(View.VISIBLE);

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

                addImageToGallery(outFile.getAbsolutePath(), context);

            } catch (WriterException | IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(context, "File Downloaded and QR Code Generated", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Write to Storage Permission Not Granted\nPlease go to Setting", Toast.LENGTH_SHORT).show();
        }
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
