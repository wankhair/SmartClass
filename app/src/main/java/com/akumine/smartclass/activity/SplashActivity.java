package com.akumine.smartclass.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.akumine.smartclass.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        ImageView splashImage = (ImageView) findViewById(R.id.splash_image);
        TextView splashText = (TextView) findViewById(R.id.splah_text);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_transition);
        splashImage.startAnimation(animation);
        splashText.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIfUserAlreadyLogin();
            }
        }, 2 * 1000); // wait for 2 seconds
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    private void checkIfUserAlreadyLogin() {
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    MainActivity.start(SplashActivity.this, currentUser.getUid());
                } else {
                    LoginActivity.start(SplashActivity.this);
                }
                finish();
            }
        };
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }
}
