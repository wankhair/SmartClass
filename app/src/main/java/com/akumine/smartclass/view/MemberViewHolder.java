package com.akumine.smartclass.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface MemberViewHolderClickListener {
        void onMemberItemClick(String memberId);
    }

    private TextView memberName;
    private ClassMember classMember;

    private MemberViewHolderClickListener listener;

    public MemberViewHolder(View itemView, MemberViewHolderClickListener listener) {
        super(itemView);
        memberName = itemView.findViewById(R.id.member_name);
        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    public void bindData(ClassMember classMember, final int position) {
        this.classMember = classMember;

        DatabaseReference tableMember = FirebaseDatabase.getInstance().getReference(User.DB_USER)
                .child(classMember.getMemberId());
        tableMember.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child(User.DB_COLUMN_USERNAME).getValue().toString();
                    memberName.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onMemberItemClick(classMember.getMemberId());
            }
        }
    }
}
