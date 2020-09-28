package com.akumine.smartclass.assignment.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.adapter.SubmissionAdapter;
import com.akumine.smartclass.model.Notification;
import com.akumine.smartclass.model.Submission;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PermissionUtil;
import com.akumine.smartclass.util.PreferenceUtil;
import com.akumine.smartclass.view.SubmissionViewHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

public class SubmissionFragment extends Fragment implements View.OnClickListener,
        SubmissionViewHolder.SubmissionViewHolderClickListener {

    private SubmissionAdapter submissionAdapter;
    private ImageView checked;
    private ImageView unchecked;
    private TextView subChecked;
    private TextView subUnchecked;
    private FloatingActionButton fabBtn;
    private Button btnConfirmAll;

    private String uid;
    private String assignId;
    private String role;

    private DatabaseReference tableSubmit;
    private DatabaseReference tableNotify;

    private Context context;

    public static SubmissionFragment newInstance(String uid, String assignId) {
        SubmissionFragment fragment = new SubmissionFragment();
        Bundle args = new Bundle();
        args.putString(Constant.ARGS_USER_ID, uid);
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
        return inflater.inflate(R.layout.fragment_submission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            uid = getArguments().getString(Constant.ARGS_USER_ID);
            assignId = getArguments().getString(Constant.ARGS_ASSIGN_ID);
        }

        fabBtn = view.findViewById(R.id.fab_btn);
        fabBtn.setIcon(R.drawable.qr_code_icon);
        checked = view.findViewById(R.id.checked);
        unchecked = view.findViewById(R.id.unchecked);
        subChecked = view.findViewById(R.id.sub_checked);
        subUnchecked = view.findViewById(R.id.sub_unchecked);
        btnConfirmAll = view.findViewById(R.id.btn_confirm_all);
        fabBtn.setOnClickListener(this);
        btnConfirmAll.setOnClickListener(this);

        fabBtn.setVisibility(View.GONE);
        checked.setVisibility(View.GONE);
        unchecked.setVisibility(View.GONE);
        subChecked.setVisibility(View.GONE);
        subUnchecked.setVisibility(View.GONE);
        btnConfirmAll.setVisibility(View.GONE);

        tableSubmit = FirebaseDatabase.getInstance().getReference(Submission.DB_SUBMIT);
        tableNotify = FirebaseDatabase.getInstance().getReference().child(Notification.DB_NOTIFICATION);

        role = PreferenceUtil.getRole(context);

        setUiBasedOnRole(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_btn:
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
                break;
            case R.id.btn_confirm_all:
                tableSubmit.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Submission submission1 = snapshot.getValue(Submission.class);
                                if (submission1 != null && submission1.getAssignId().equals(assignId)) {
                                    if (submission1.getStatus().equals("Not Submitted")) {
                                        String id = snapshot.getKey();
                                        String memberId = submission1.getMemberId();
                                        if (id != null) {
                                            tableSubmit.child(id).child(Submission.DB_COLUMN_STATUS).setValue("Submitted");
                                        }

                                        Notification notification = new Notification("Confirm Submission", uid, assignId);
                                        tableNotify.child(memberId).push().setValue(notification);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                break;
        }
    }

    private void setUiBasedOnRole(View view) {
        if (role.equals(Constant.ROLE_LECTURER)) {
            fabBtn.setVisibility(View.VISIBLE);
            btnConfirmAll.setVisibility(View.VISIBLE);

            submissionAdapter = new SubmissionAdapter(context, null, this);

            RecyclerView recyclerSubmit = view.findViewById(R.id.recycler_submission);
            recyclerSubmit.setLayoutManager(new LinearLayoutManager(context));
            recyclerSubmit.setAdapter(submissionAdapter);

            tableSubmit.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<Submission> submissionList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Submission submission = snapshot.getValue(Submission.class);
                            if (submission != null && submission.getAssignId().equals(assignId)) {
                                submissionList.add(submission);
                            }
                        }

                        if (submissionList.isEmpty()) {
                            Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                        }
                        submissionAdapter.setData(submissionList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } else if (role.equals(Constant.ROLE_STUDENT)) {
            tableSubmit.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Submission submission = snapshot.getValue(Submission.class);
                            if (submission != null && submission.getMemberId().equals(uid)) {
                                if (submission.getStatus().equals("Not Submitted")) {
                                    unchecked.setVisibility(View.VISIBLE);
                                    subUnchecked.setVisibility(View.VISIBLE);

                                } else if (submission.getStatus().equals("Submitted")) {
                                    checked.setVisibility(View.VISIBLE);
                                    subChecked.setVisibility(View.VISIBLE);
                                }
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

    @Override
    public void onSubmissionItemClick(int position) {
        //do nothing as for now
    }
}
