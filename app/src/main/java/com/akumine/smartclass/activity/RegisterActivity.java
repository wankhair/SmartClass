package com.akumine.smartclass.activity;

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

import com.akumine.smartclass.R;
import com.akumine.smartclass.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText regEmailText;
    private EditText regPassText;
    private Button regBtn;
    private Button regLoginBtn;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        regEmailText = (EditText) findViewById(R.id.reg_email);
        regPassText = (EditText) findViewById(R.id.reg_password);
        regBtn = (Button) findViewById(R.id.reg_btn);
        regLoginBtn = (Button) findViewById(R.id.reg_login_btn);
        regBtn.setOnClickListener(this);
        regLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reg_btn:
                // to hide soft keyboard upon clicking button
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String email = regEmailText.getText().toString();
                String password = regPassText.getText().toString();

                if (isRegisterCredentialsValidated(email, password)) {
                    performRegistration(email, password);
                }
                break;
            case R.id.reg_login_btn:
                LoginActivity.start(RegisterActivity.this);
                break;
        }
    }

    private boolean isRegisterCredentialsValidated(String email, String password) {
        View viewToFocus = null;

        if (email.isEmpty()) {
            regEmailText.setError(Constant.ERROR_EMAIL_EMPTY);
            viewToFocus = regEmailText;
        } else {
            regEmailText.setError(null);
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            regEmailText.setError(Constant.ERROR_EMAIL_INVALID);
            viewToFocus = regEmailText;
        } else {
            regEmailText.setError(null);
        }

        if (password.isEmpty()) {
            regPassText.setError(Constant.ERROR_PASSWORD_EMPTY);
            viewToFocus = regPassText;
        } else {
            regPassText.setError(null);
        }

        String pass_pattern = "((?=.*\\d)(?=.*[a-zA-Z]).{6,20})";
        if (!password.matches(pass_pattern)) {
            regPassText.setError(Constant.ERROR_PASSWORD_TOO_WEAK);
            viewToFocus = regPassText;
        } else {
            regPassText.setError(null);
        }

        if (viewToFocus != null) {
            viewToFocus.requestFocus();
            return false;
        }
        return true;
    }

    private void performRegistration(final String email, String password) {
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String currentUid = firebaseAuth.getCurrentUser().getUid();

                            UserInfoActivity.start(RegisterActivity.this, currentUid, email);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Cannot Register", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}
