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
import com.akumine.smartclass.adapter.AssignmentAdapter;
import com.akumine.smartclass.activity.AddAssignmentActivity;
import com.akumine.smartclass.activity.MainAssignmentActivity;
import com.akumine.smartclass.model.Assignments;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PreferenceUtil;
import com.akumine.smartclass.view.AssignmentViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AssignmentFragment extends Fragment implements View.OnClickListener,
        AssignmentViewHolder.AssignmentViewHolderClickListener {

    private static final String TAG = "AssignmentFragment";

    private String uid;
    private String classId;

    private AssignmentAdapter assignmentAdapter;

    private Context context;

    public static AssignmentFragment newInstance(String uid, String class_id) {
        AssignmentFragment fragment = new AssignmentFragment();
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
        return inflater.inflate(R.layout.fragment_assign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
            classId = getArguments().getString(Constant.ARGS_CLASS_ID);
        }

        String role = PreferenceUtil.getRole(context);

        FloatingActionButton fabBtn = view.findViewById(R.id.fab_btn);
        fabBtn.setIcon(R.drawable.ic_add_white);
        fabBtn.setOnClickListener(this);

        if (role != null) {
            if (role.equals(Constant.ROLE_LECTURER)) {
                fabBtn.setVisibility(View.VISIBLE);
            } else if (role.equals(Constant.ROLE_STUDENT)) {
                fabBtn.setVisibility(View.GONE);
            }
        }

        assignmentAdapter = new AssignmentAdapter(null, this);
        RecyclerView recyclerAssign = view.findViewById(R.id.recycler_assign);
        recyclerAssign.setHasFixedSize(true);
        recyclerAssign.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAssign.setAdapter(assignmentAdapter);

        setupAssignment();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_btn) {
            AddAssignmentActivity.start(context, uid, classId);
        }
    }

    private void setupAssignment() {
        DatabaseReference tableAssignment = FirebaseDatabase.getInstance().getReference(Assignments.DB_ASSIGNMENT);
        Query query = tableAssignment.orderByChild(Assignments.DB_COLUMN_CLASS_ID).equalTo(classId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Assignments> assignmentsList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Assignments assignments = snapshot.getValue(Assignments.class);
                        assignmentsList.add(assignments);
                    }

                    if (assignmentsList.isEmpty()) {
                        Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                    }
                    assignmentAdapter.setData(assignmentsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void OnAssignmentItemClick(String classId, String assignId) {
        MainAssignmentActivity.start(context, uid, classId, assignId);
    }
}
