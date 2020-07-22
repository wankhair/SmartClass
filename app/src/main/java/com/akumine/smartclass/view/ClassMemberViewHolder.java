package com.akumine.smartclass.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.ClassMember;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        DatabaseReference tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(classId);
        tableClass.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String className = dataSnapshot.child(Classes.DB_COLUMN_NAME).getValue().toString();
                    final String lecId = dataSnapshot.child(Classes.DB_COLUMN_LECTURER_ID).getValue().toString();
                    final String currentUser = dataSnapshot.child(Classes.DB_COLUMN_CURRENT_USER).getValue().toString();
                    final String maxUser = dataSnapshot.child(Classes.DB_COLUMN_MAX_USER).getValue().toString();

                    DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(lecId);
                    tableUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String username = dataSnapshot.child(User.DB_COLUMN_USERNAME).getValue().toString();

                                classTitle.setText(className);
                                classMember.setText(currentUser + "/" + maxUser);
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
