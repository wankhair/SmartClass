package com.akumine.smartclass.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Assignments;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Notification;
import com.akumine.smartclass.model.Submission;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.DatabaseUtil;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddAssignmentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText assignName;
    private EditText assignDesc;
    private TextView assignDate;
    private TextView assignTime;
    private TextView fileSelect;
    private Button btnChooseFile;
    private Button btnAddAssignment;

    private String uid;
    private String filename;
    private String docUrl;
    private String created;
    private String modify;
    private String classId;

    private Uri pdfUri;
//    private DatabaseReference tableAssignment;
//    private DatabaseReference tableNotify;
//    private DatabaseReference tableSubmit;
//    private DatabaseReference tableMember;
//    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    public static void start(Context context, String uid, String classId) {
        Intent intent = new Intent(context, AddAssignmentActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        intent.putExtra(Constant.EXTRA_CLASS_ID, classId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assign);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Assignment");
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

//        storageReference = FirebaseStorage.getInstance().getReference();
//        tableAssignment = FirebaseDatabase.getInstance().getReference(Assignments.DB_ASSIGNMENT);
//        tableNotify = FirebaseDatabase.getInstance().getReference().child(Notification.DB_NOTIFICATION);
//        tableSubmit = FirebaseDatabase.getInstance().getReference().child(Submission.DB_SUBMIT);

        assignName = (EditText) findViewById(R.id.assign_name);
        assignDesc = (EditText) findViewById(R.id.assign_desc);
        assignDate = (TextView) findViewById(R.id.date_picker);
        assignTime = (TextView) findViewById(R.id.time_picker);
        fileSelect = (TextView) findViewById(R.id.file_select);
        btnChooseFile = (Button) findViewById(R.id.btn_choose_file);
        btnAddAssignment = (Button) findViewById(R.id.btn_add_assignment);

        assignDate.setOnClickListener(this);
        assignTime.setOnClickListener(this);
        btnChooseFile.setOnClickListener(this);
        btnAddAssignment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Calendar calendar = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.date_picker:
                getDate(calendar);
                break;
            case R.id.time_picker:
                getTime(calendar);
                break;
            case R.id.btn_choose_file:
                if (PermissionUtil.hasReadPermissionToExternalStorage(this)) {
                    selectFile();
                } else {
                    Toast.makeText(this, "Read Storage Permission Not Granted\nPlease go to Setting", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_assignment:
                if (pdfUri != null) {
                    uploadFile(pdfUri);
                } else {
                    Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                assignDate.setText(String.format(Locale.getDefault(), "%s/%s/%s", dayOfMonth, (monthOfYear + 1), year));
                assignDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void getTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // to parse time into Date
                String time = hourOfDay + ":" + minute;
                Date date = null;
                try {
                    date = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // get date in desired format
                assert date != null;
                assignTime.setText(new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(date));
                assignTime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectFile() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, Constant.REQUEST_CODE_SELECT_FILE);
    }

    private void uploadFile(Uri pdfUri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Adding Assignment...");
        progressDialog.setProgress(0);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        created = dateFormat.format(calendar.getTime());
        modify = created;

        final String name = assignName.getText().toString();
        final String desc = assignDesc.getText().toString();
        final String date = assignDate.getText().toString();
        final String time = assignTime.getText().toString();

        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("Uploads/" + uid + "/" + filename);

        ref.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        docUrl = uri.toString();
                        final String assignId = UUID.randomUUID().toString();

                        Assignments assignments = new Assignments(assignId, name, desc, docUrl, filename, date, time, created, modify, classId);
                        DatabaseUtil.tableAssignmentWithOneChild(assignId).setValue(assignments);
//                                tableAssignment.child(id).setValue(assignments);

//                                tableMember = FirebaseDatabase.getInstance().getReference().child(ClassMember.DB_CLASSMEMBER);
                        DatabaseUtil.tableMember().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ClassMember member = snapshot.getValue(ClassMember.class);
                                        if (member != null && member.getClassId().equals(classId)) {
                                            String memberId = member.getMemberId();

                                            Notification notification = new Notification("Notify Members", uid, assignId);
                                            DatabaseUtil.tableNotificationWithOneChild(memberId).push().setValue(notification);
//                                                    tableNotify.child(memberId).push().setValue(notification);

                                            Submission submission = new Submission(memberId, assignId, Constant.NOT_SUBMITTED);
                                            String id = UUID.randomUUID().toString();
                                            DatabaseUtil.tableSubmissionWithOneChild(id).setValue(submission);
//                                                    tableSubmit.child(id).setValue(submission);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        Toast.makeText(AddAssignmentActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddAssignmentActivity.this, "File not successfully uploaded", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setCancelable(false);
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_SELECT_FILE && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            fileSelect.setText(data.getData().getLastPathSegment());
            fileSelect.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            filename = data.getData().getLastPathSegment();
        } else {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }
}
