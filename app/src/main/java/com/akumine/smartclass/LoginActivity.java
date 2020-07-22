package com.akumine.smartclass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akumine.smartclass.model.User;
import com.akumine.smartclass.util.Constant;
import com.akumine.smartclass.util.PermissionUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference tableUser;
    private ProgressDialog progressDialog;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        loginEmailText = (EditText) findViewById(R.id.login_email);
        loginPassText = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginRegBtn = (Button) findViewById(R.id.login_reg_btn);
        loginBtn.setOnClickListener(this);
        loginRegBtn.setOnClickListener(this);

        PermissionUtil.requestPermissionsOnRuntime(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                // to hide soft keyboard upon clicking button
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                final String email = loginEmailText.getText().toString();
                final String password = loginPassText.getText().toString();

                if (isLoginCredentialsValidated(email, password)) {
                    //to get device token when user login
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    String token = instanceIdResult.getToken();
                                    performLogin(email, password, token);
                                }
                            });
                }
                break;
            case R.id.login_reg_btn:
                RegisterActivity.start(LoginActivity.this);
                break;
        }
    }

    private boolean isLoginCredentialsValidated(String email, String password) {
        View viewToFocus = null;

        if (email.isEmpty()) {
            loginEmailText.setError(Constant.ERROR_EMAIL_EMPTY);
            viewToFocus = loginEmailText;
        } else {
            loginEmailText.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmailText.setError(Constant.ERROR_EMAIL_INVALID);
            viewToFocus = loginEmailText;
        } else {
            loginEmailText.setError(null);
        }

        if (password.isEmpty()) {
            loginPassText.setError(Constant.ERROR_PASSWORD_EMPTY);
            viewToFocus = loginPassText;
        } else {
            loginPassText.setError(null);
        }

        if (viewToFocus != null) {
            viewToFocus.requestFocus();
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password, final String token) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String currentUid = firebaseAuth.getCurrentUser().getUid();

                    tableUser = FirebaseDatabase.getInstance().getReference().child(User.DB_USER);
                    tableUser.child(currentUid).child(User.DB_COLUMN_DEVICE_TOKEN).setValue(token);

                    MainActivity.start(LoginActivity.this, currentUid);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}
