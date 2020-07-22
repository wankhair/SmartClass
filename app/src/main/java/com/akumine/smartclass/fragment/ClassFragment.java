package com.akumine.smartclass.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.adapter.ClassAdapter;
import com.akumine.smartclass.adapter.ClassMemberAdapter;
import com.akumine.smartclass.classes.AddClassActivity;
import com.akumine.smartclass.classes.MainClassActivity;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PermissionUtil;
import com.akumine.smartclass.util.PreferenceUtil;
import com.akumine.smartclass.view.ClassMemberViewHolder;
import com.akumine.smartclass.view.ClassViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

public class ClassFragment extends Fragment implements View.OnClickListener,
        ClassMemberViewHolder.ClassMemberViewHolderClickListener, ClassViewHolder.ClassViewHolderClickListener {

    public static final String TAG = "ClassFragment";

    private FloatingActionButton fabButton;
    private RecyclerView recyclerClass;
    private ClassAdapter classAdapter;
    private ClassMemberAdapter memberAdapter;

    private DatabaseReference tableClass;

    private String uid;
    private String role;

    private Context context;

    public static ClassFragment newInstance(String uid) {
        ClassFragment fragment = new ClassFragment();
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
        return inflater.inflate(R.layout.fragment_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
        }

        tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS);

        fabButton = view.findViewById(R.id.fab_btn);
        fabButton.setOnClickListener(this);

        recyclerClass = view.findViewById(R.id.recycler_class);
        recyclerClass.setLayoutManager(new LinearLayoutManager(context));

        role = PreferenceUtil.getRole(context);
        if (role.equals(Constant.ROLE_LECTURER)) {
            getLecturer();

        } else if (role.equals(Constant.ROLE_STUDENT)) {
            getStudent();
        }
    }

    private void getLecturer() {
        fabButton.setIcon(R.drawable.ic_add_white);

        classAdapter = new ClassAdapter(null, this);
        recyclerClass.setAdapter(classAdapter);

        Query query = tableClass.orderByChild(Classes.DB_COLUMN_LECTURER_ID).equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Classes> classesList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Classes classes = snapshot.getValue(Classes.class);
                        classesList.add(classes);
                    }
                    classAdapter.setData(classesList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getStudent() {
        fabButton.setIcon(R.drawable.qr_code_icon);

        memberAdapter = new ClassMemberAdapter(null, this);
        recyclerClass.setAdapter(memberAdapter);

        DatabaseReference tableClassMember = FirebaseDatabase.getInstance().getReference().child(ClassMember.DB_CLASSMEMBER);
        Query query = tableClassMember.orderByChild(ClassMember.DB_COLUMN_MEMBER_ID).equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final List<ClassMember> classMemberList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ClassMember member = snapshot.getValue(ClassMember.class);
                        classMemberList.add(member);

                        Query query1 = tableClass.orderByChild(Classes.DB_COLUMN_CREATED);
                        query1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                memberAdapter.setData(classMemberList);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
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
        int id = view.getId();
        if (id == R.id.fab_btn) {
            if (role.equals(Constant.ROLE_LECTURER)) {
                AddClassActivity.start(context, uid);
            } else if (role.equals(Constant.ROLE_STUDENT)) {
                if (PermissionUtil.hasCameraPermission(context)) {
                    IntentIntegrator integrator = new IntentIntegrator(getActivity());
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    integrator.setPrompt("Scan QR Code");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(false);
                    integrator.setOrientationLocked(false);
                    integrator.initiateScan();
                } else {
                    Toast.makeText(context, "Camera Permission Not Granted\nPlease go to Setting", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClassItemClick(String classId) {
        MainClassActivity.start(context, uid, classId);
    }
}
