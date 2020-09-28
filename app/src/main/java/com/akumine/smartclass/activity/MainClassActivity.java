package com.akumine.smartclass.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.akumine.smartclass.R;
import com.akumine.smartclass.adapter.PagerAdapter;
import com.akumine.smartclass.fragment.AssignmentFragment;
import com.akumine.smartclass.fragment.InfoClassFragment;
import com.akumine.smartclass.fragment.MemberFragment;
import com.akumine.smartclass.fragment.PostFragment;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PreferenceUtil;
import com.google.android.material.tabs.TabLayout;

public class MainClassActivity extends AppCompatActivity {

    private static final String TAG = "MainClassActivity";

    private TabLayout tabLayout;
    private String role;

    private int[] tabIcon = {
            R.drawable.ic_tasks_list,
            R.drawable.ic_info,
            R.drawable.ic_multiple_users,
            R.drawable.ic_post
    };

    public static void start(Context context, String uid, String classId) {
        Intent intent = new Intent(context, MainClassActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        intent.putExtra(Constant.EXTRA_CLASS_ID, classId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_class);

        Intent intent = getIntent();
        String uid = intent.getStringExtra(Constant.EXTRA_USER_ID);
        String classId = intent.getStringExtra(Constant.EXTRA_CLASS_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Class Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        role = PreferenceUtil.getRole(this);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AssignmentFragment.newInstance(uid, classId), "Assignment");
        adapter.addFragment(InfoClassFragment.newInstance(uid, classId), "Info");
        adapter.addFragment(MemberFragment.newInstance(classId), "Members");
        if (role.equals(Constant.ROLE_LECTURER)) {
            adapter.addFragment(PostFragment.newInstance(uid, classId), "Post");
        }

        ViewPager viewPager = findViewById(R.id.tab_view_pager);
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcon[0]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(tabIcon[1]);
        tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).setIcon(tabIcon[2]);
        tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_IN);
        if (role.equals(Constant.ROLE_LECTURER)) {
            tabLayout.getTabAt(3).setIcon(tabIcon[3]);
            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.colorLightGray), PorterDuff.Mode.SRC_IN);
        }

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
}
