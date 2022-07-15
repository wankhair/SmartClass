package com.akumine.smartclass.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.DatabaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ClassMemberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface ClassMemberViewHolderClickListener {
        void onClassItemClick(String classId);
    }

    private String classId;
    private TextView classTitle;
    private TextView classMember;
    private TextView lecName;

    private ClassMemberViewHolderClickListener listener;

    public ClassMemberViewHolder(View itemView, ClassMemberViewHolderClickListener listener) {
        super(itemView);
        classTitle = itemView.findViewById(R.id.view_class_title);
        classMember = itemView.findViewById(R.id.class_members);
        lecName = itemView.findViewById(R.id.view_lec_name);
        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    public void bindData(ClassMember classMemberList) {
        classId = classMemberList.getClassId();

//        DatabaseReference tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
        DatabaseUtil.tableClassWithOneChild(classId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String className = dataSnapshot.child(Classes.CLASS_NAME).getValue().toString();
                    final String lecId = dataSnapshot.child(Classes.LECTURER_ID).getValue().toString();
                    final String currentUser = dataSnapshot.child(Classes.CURRENT_USER).getValue().toString();
                    final String maxUser = dataSnapshot.child(Classes.MAX_USER).getValue().toString();

//                    DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(lecId);
                    DatabaseUtil.tableUserWithOneChild(lecId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String username = dataSnapshot.child(User.USERNAME).getValue().toString();

                                classTitle.setText(className);
                                classMember.setText(String.format(Locale.getDefault(), "%s/%s", currentUser, maxUser));
                                //classMember.setText(currentUser + "/" + maxUser);
                                lecName.setText(username);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
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
                listener.onClassItemClick(classId);
            }
        }
    }
}
