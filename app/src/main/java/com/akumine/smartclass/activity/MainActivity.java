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
import com.akumine.smartclass.model.Classes;
import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PreferenceUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        getUserInterface();
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

    private void getUserInterface() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Please Wait!!");
        progressDialog.setMessage("Loading Interface for User ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference(User.DB_USER).child(uid);
        tableUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child(User.DB_COLUMN_ROLE).getValue().toString();

                    bottomNavigationView.getMenu().clear();

                    //load the UI based on the ic_user role
                    if (role.equals(Constant.ROLE_LECTURER)) {
                        bottomNavigationView.inflateMenu(R.menu.nav_item_lecturer);
                    } else if (role.equals(Constant.ROLE_STUDENT)) {
                        bottomNavigationView.inflateMenu(R.menu.nav_item_student);
                    }

                    progressDialog.dismiss();

                    PreferenceUtil.setRole(MainActivity.this, role);
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

    public void displaySelectedScreen(int id) {
        if (id == R.id.nav_classes) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, ClassFragment.newInstance(uid),
                            ClassFragment.TAG)
                    .commit();
        } else if (id == R.id.nav_news_feed) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, NewsFeedFragment.newInstance(uid),
                            NewsFeedFragment.TAG)
                    .commit();
        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, ProfileFragment.newInstance(uid),
                            ProfileFragment.TAG)
                    .commit();
        }
    }

    private void logOut() {
        // to remove device token when ic_user sign out
        DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child(User.DB_USER);
        tableUser.child(uid).child(User.DB_COLUMN_DEVICE_TOKEN).setValue(null);
        // to sign out from firebase
        FirebaseAuth.getInstance().signOut();
        // to go back to login activity
        LoginActivity.start(MainActivity.this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "You cancelled scanning", Toast.LENGTH_SHORT).show();
            } else {
                hold = result.getContents();
                startJoinClassActivity(hold);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startJoinClassActivity(final String hold) {
        DatabaseReference tableClass = FirebaseDatabase.getInstance().getReference(Classes.DB_CLASS).child(hold);
        tableClass.addValueEventListener(new ValueEventListener() {
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
}
