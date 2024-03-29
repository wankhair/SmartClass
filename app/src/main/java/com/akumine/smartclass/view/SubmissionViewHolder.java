package com.akumine.smartclass.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.akumine.smartclass.R;
import com.akumine.smartclass.model.Submission;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.DatabaseUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubmissionViewHolder extends RecyclerView.ViewHolder {

    public interface SubmissionViewHolderClickListener {
        void onSubmissionItemClick(int position);
    }

    private CircleImageView profileImage;
    private TextView memberName;
    private TextView status;

    private SubmissionViewHolderClickListener listener;

    public SubmissionViewHolder(View itemView, SubmissionViewHolderClickListener listener) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profile_image);
        memberName = itemView.findViewById(R.id.member_name);
        status = itemView.findViewById(R.id.status);
        this.listener = listener;
    }

    public void bindData(Submission submission) {
        String memberId = submission.getMemberId();

//        DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(memberId);
        DatabaseUtil.tableUserWithOneChild(memberId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child(User.USERNAME).getValue().toString();
                    String image = dataSnapshot.child(User.IMAGE).getValue().toString();

                    memberName.setText(username);
                    Picasso.get().load(image).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        if (submission.getStatus().equals(Constant.SUBMITTED)) {
            status.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPositive));
        } else {
            status.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorNegative));
        }
        status.setText(submission.getStatus());
    }
}
