package com.akumine.smartclass.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClassViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface ClassViewHolderClickListener {
        void onClassItemClick(String classId);
    }

    private String classId;
    private TextView classTitle;
    private TextView classMember;
    private TextView lecName;

    private ClassViewHolderClickListener listener;

    public ClassViewHolder(View itemView, ClassViewHolderClickListener listener) {
        super(itemView);
        classTitle = itemView.findViewById(R.id.view_class_title);
        classMember = itemView.findViewById(R.id.class_members);
        lecName = itemView.findViewById(R.id.view_lec_name);
        this.listener = listener;
        itemView.setOnClickListener(this);
    }

    public void bindData(Classes classes) {
        classId = classes.getId();
        classTitle.setText(classes.getClassName());
        classMember.setText(classes.getCurrentUser() + "/" + classes.getMaxUser());

        DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(classes.getLecturerId());
        tableUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child(User.DB_COLUMN_USERNAME).getValue().toString();
                    lecName.setText(name);
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
