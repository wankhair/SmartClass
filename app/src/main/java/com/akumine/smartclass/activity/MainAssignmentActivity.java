package com.akumine.smartclass.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.akumine.smartclass.R;
import com.akumine.smartclass.adapter.PagerAdapter;
import com.akumine.smartclass.fragment.InfoAssignFragment;
import com.akumine.smartclass.fragment.SubmissionFragment;
import com.akumine.smartclass.model.Notification;
import com.akumine.smartclass.model.Submission;
import com.akumine.smartclass.util.Constant;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainAssignmentActivity extends AppCompatActivity {

    public static String hold;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String uid;
    private String classId;
    private String assignId;

    private DatabaseReference tableNotify;
    private DatabaseReference tableSubmit;

    private int[] tabIcons = {
            R.drawable.ic_submit_file,
            R.drawable.ic_info
    };

    public static void start(Context context, String uid, String classId, String assignId) {
        Intent intent = new Intent(context, MainAssignmentActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        intent.putExtra(Constant.EXTRA_CLASS_ID, classId);
        intent.putExtra(Constant.EXTRA_ASSIGN_ID, assignId);
        context.startActivity(intent);
    }

    public static void start(Activity activity, String classId, String assignId, String memberId) {
        Intent intent = new Intent(activity, MainAssignmentActivity.class);
        intent.putExtra(Constant.EXTRA_CLASS_ID, classId);
        intent.putExtra(Constant.EXTRA_ASSIGN_ID, assignId);
        intent.putExtra(Constant.EXTRA_MEMBER_ID, memberId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_assign);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Assignment Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        uid = intent.getStringExtra(Constant.EXTRA_USER_ID);
        classId = intent.getStringExtra(Constant.EXTRA_CLASS_ID);
        assignId = intent.getStringExtra(Constant.EXTRA_ASSIGN_ID);

        tableNotify = FirebaseDatabase.getInstance().getReference().child(Notification.DB_NOTIFICATION);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SubmissionFragment.newInstance(uid, assignId), "Submission");
        adapter.addFragment(InfoAssignFragment.newInstance(uid, classId, assignId), "Info");

        viewPager = findViewById(R.id.tab_view_pager_assign);
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout_assign);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled scanning", Toast.LENGTH_SHORT).show();
            } else {
                //to split the data in qr code
                hold = result.getContents();
                String from_intent = MainAssignmentActivity.hold;
                String[] split = from_intent.split("/");
                final String classId = split[0];
                final String assignId = split[1];
                final String memberId = split[2];

                //to confirm the submission and update db
                final Submission submission = new Submission(memberId, assignId, "Submitted");

                tableSubmit = FirebaseDatabase.getInstance().getReference(Submission.DB_SUBMIT);
                tableSubmit.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Submission submission1 = snapshot.getValue(Submission.class);
                                assert submission1 != null;
                                if (submission1.getAssignId().equals(assignId)
                                        && submission1.getMemberId().equals(memberId)) {
                                    String id = snapshot.getKey();
                                    if (id != null) {
                                        tableSubmit.child(id).setValue(submission);
                                    }

                                    Notification notification = new Notification("Confirm Submission", uid, assignId);
                                    tableNotify.child(memberId).push().setValue(notification);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                MainAssignmentActivity.start(this, classId, assignId, memberId);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
