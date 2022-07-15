package com.akumine.smartclass.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Assignments;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.model.Submission;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.DatabaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class JoinClassActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView classInfoTitle;
    private TextView classInfoDesc;
    private Button btnJoinClass;

    private String uid;
    private String classId;
    private int count = 0;

//    private DatabaseReference tableMember;
//    private DatabaseReference tableClass;
//    private DatabaseReference tableSubmit;

    public static void start(Context context, String uid, String classId) {
        Intent intent = new Intent(context, JoinClassActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        intent.putExtra(Constant.EXTRA_CLASS_ID, classId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Join Class");
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

        classInfoTitle = findViewById(R.id.class_info_title);
        classInfoDesc = findViewById(R.id.class_info_desc);
        btnJoinClass = findViewById(R.id.btn_join_class);
        btnJoinClass.setVisibility(View.GONE);
        btnJoinClass.setOnClickListener(this);

        checkClassMember();
        setupUserInterface();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_join_class) {
            addNewMember();
            countMember();
            setAssignmentForNewMember();
            Toast.makeText(JoinClassActivity.this, "You has joined the class", Toast.LENGTH_SHORT).show();
            // go back to main activity
            MainActivity.start(JoinClassActivity.this, uid);
        }
    }

    private void checkClassMember() {
//        DatabaseReference tableCheckMember = FirebaseDatabase.getInstance().getReference().child(ClassMember.DB_CLASSMEMBER);
        DatabaseUtil.tableMember().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isMemberAlreadyJoin = false;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ClassMember member = snapshot.getValue(ClassMember.class);
                        assert member != null;
                        if (member.getClassId().equals(classId)) {
                            if (member.getMemberId().equals(uid)) {
                                isMemberAlreadyJoin = true;
                            }
                        }
                    }
                }

                if (isMemberAlreadyJoin) {
                    btnJoinClass.setVisibility(View.GONE);
                    Toast.makeText(JoinClassActivity.this, "You already joined the class", Toast.LENGTH_SHORT).show();
                } else {
                    btnJoinClass.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setupUserInterface() {
//        tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
        DatabaseUtil.tableClassWithOneChild(classId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String className = dataSnapshot.child(Classes.CLASS_NAME).getValue().toString();
                    String classDesc = dataSnapshot.child(Classes.CLASS_DESC).getValue().toString();

                    classInfoTitle.setText(className);
                    classInfoDesc.setText(classDesc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addNewMember() {
//        DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child(User.DB_USER).child(uid);
        DatabaseUtil.tableUserWithOneChild(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child(User.USERNAME).getValue().toString();

                Calendar calendar = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String joined = dateFormat.format(calendar.getTime());

                ClassMember classMember = new ClassMember(uid, username, classId, joined);
                DatabaseUtil.tableMember().push().setValue(classMember);
//                tableMember = FirebaseDatabase.getInstance().getReference().child(ClassMember.DB_CLASSMEMBER);
//                tableMember.push().setValue(classMember);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void countMember() {
//        DatabaseReference tableCountMember = FirebaseDatabase.getInstance().getReference().child(ClassMember.DB_CLASSMEMBER);
        DatabaseUtil.tableMember().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ClassMember member = snapshot.getValue(ClassMember.class);
                        assert member != null;
                        if (member.getClassId().equals(classId)) {
                            count++;
                        }
                    }

                    DatabaseUtil.tableClassWithTwoChild(classId, Classes.CURRENT_USER)
                            .setValue(String.valueOf(count));
//                    tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
//                    tableClass.child(Classes.DB_COLUMN_CURRENT_USER).setValue(String.valueOf(count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setAssignmentForNewMember() {
//        tableSubmit = FirebaseDatabase.getInstance().getReference().child(Submission.DB_SUBMIT);

//        DatabaseReference tableAssignment = FirebaseDatabase.getInstance().getReference().child(Assignments.DB_ASSIGNMENT);
        DatabaseUtil.tableAssignment().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Assignments assignments = snapshot.getValue(Assignments.class);
                        assert assignments != null;
                        if (assignments.getClassId().equals(classId)) {
                            String id = UUID.randomUUID().toString();

                            Submission submission = new Submission(uid, assignments.getId(), Constant.NOT_SUBMITTED);
                            DatabaseUtil.tableSubmissionWithOneChild(id).setValue(submission);
//                            tableSubmit.child(id).setValue(submission);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
