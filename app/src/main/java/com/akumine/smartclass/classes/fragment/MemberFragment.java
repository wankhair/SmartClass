package com.akumine.smartclass.classes.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.adapter.MemberAdapter;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.view.MemberViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberFragment extends Fragment implements MemberViewHolder.MemberViewHolderClickListener {

    private MemberAdapter memberAdapter;

    private String classId;

    private Context context;

    public static MemberFragment newInstance(String class_id) {
        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            classId = getArguments().getString(Constant.ARGS_CLASS_ID);
        }

        memberAdapter = new MemberAdapter(null, this);
        RecyclerView recyclerMember = view.findViewById(R.id.recycler_member);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2,
                GridLayoutManager.VERTICAL, false);
        recyclerMember.setLayoutManager(gridLayoutManager);
        recyclerMember.setAdapter(memberAdapter);

        setupClassMember();
    }

    private void setupClassMember() {
        DatabaseReference tableMember = FirebaseDatabase.getInstance().getReference(ClassMember.DB_CLASSMEMBER);
        Query query = tableMember.orderByChild(ClassMember.DB_COLUMN_CLASS_ID).equalTo(classId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<ClassMember> classMemberList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ClassMember classMember = snapshot.getValue(ClassMember.class);
                        classMemberList.add(classMember);
                    }

                    if (classMemberList.isEmpty()) {
                        Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
                    }
                    memberAdapter.setData(classMemberList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onMemberItemClick(String memberId) {
        View viewDetailMemberList = getLayoutInflater().inflate(R.layout.dialog_view_member_details, null);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(viewDetailMemberList)
                .setCancelable(true)
                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();

        TextView memberDetailTitle = (TextView) viewDetailMemberList.findViewById(R.id.member_details_title);
        memberDetailTitle.setPaintFlags(memberDetailTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        final CircleImageView memberImage = (CircleImageView) viewDetailMemberList.findViewById(R.id.details_member_image);
        final TextView memberName = (TextView) viewDetailMemberList.findViewById(R.id.details_member_name);
        final TextView memberEmail = (TextView) viewDetailMemberList.findViewById(R.id.details_member_email);
        final TextView memberPhoneNo = (TextView) viewDetailMemberList.findViewById(R.id.details_member_phone_no);
        final TextView memberRole = (TextView) viewDetailMemberList.findViewById(R.id.details_member_role);

        DatabaseReference tableMemberDetail = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(memberId);
        tableMemberDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child(User.DB_COLUMN_USERNAME).getValue().toString();
                    String email = dataSnapshot.child(User.DB_COLUMN_EMAIL).getValue().toString();
                    String imageUrl = dataSnapshot.child(User.DB_COLUMN_IMAGE).getValue().toString();
                    String phone = dataSnapshot.child(User.DB_COLUMN_PHONE).getValue().toString();
                    String userRole = dataSnapshot.child(User.DB_COLUMN_ROLE).getValue().toString();

                    Picasso.get().load(imageUrl).into(memberImage);
                    memberName.setText(username);
                    memberEmail.setText(email);
                    memberPhoneNo.setText(phone);
                    memberRole.setText(userRole);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
