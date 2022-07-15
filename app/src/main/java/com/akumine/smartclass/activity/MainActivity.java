package com.akumine.smartclass.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akumine.smartclass.R;
import com.akumine.smartclass.fragment.ClassFragment;
import com.akumine.smartclass.fragment.NewsFeedFragment;
import com.akumine.smartclass.fragment.ProfileFragment;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.DatabaseUtil;
import com.akumine.smartclass.util.PreferenceUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static String hold;
    private BottomNavigationView bottomNavigationView;
    private String uid;

    public static void start(Context context, String uid) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.EXTRA_USER_ID, uid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uid = bundle.getString(Constant.EXTRA_USER_ID);
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.main_nav);

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Please Wait!!");
        progressDialog.setMessage("Loading Interface for User ...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DatabaseUtil.tableUserWithOneChild(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    bottomNavigationView.getMenu().clear();

                    String role = dataSnapshot.child(User.ROLE).getValue().toString();
                    //load the UI based on the user role
                    if (role.equals(Constant.ROLE_LECTURER)) {
                        bottomNavigationView.inflateMenu(R.menu.nav_item_lecturer);
                    } else if (role.equals(Constant.ROLE_STUDENT)) {
                        bottomNavigationView.inflateMenu(R.menu.nav_item_student);
                    }

                    // save role in shared preference
                    PreferenceUtil.setRole(MainActivity.this, role);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                displaySelectedScreen(id);
                return true;
            }
        });

        displaySelectedScreen(R.id.nav_classes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout_btn) {
            logOut();
            return true;
        }
        return false;
    }

    public void displaySelectedScreen(int id) {
        switch (id) {
            case R.id.nav_classes:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, ClassFragment.newInstance(uid),
                                ClassFragment.TAG)
                        .commit();
                break;
            case R.id.nav_news_feed:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, NewsFeedFragment.newInstance(uid),
                                NewsFeedFragment.TAG)
                        .commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, ProfileFragment.newInstance(uid),
                                ProfileFragment.TAG)
                        .commit();
                break;
        }
    }

    private void logOut() {
        // to remove device token when user sign out
        DatabaseUtil.tableUserWithTwoChild(uid, User.DEVICE_TOKEN).removeValue();
        // to sign out from firebase
        FirebaseAuth.getInstance().signOut();
        // go back to login activity
        LoginActivity.start(MainActivity.this);
        finish();
    }

    // get result scan qr code from the ClassFragment.java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "You cancelled scanning", Toast.LENGTH_SHORT).show();
            } else {
                hold = result.getContents();

                DatabaseUtil.tableClassWithOneChild(hold).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            JoinClassActivity.start(MainActivity.this, uid, hold);
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
