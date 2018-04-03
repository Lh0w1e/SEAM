package com.app.seam.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.app.seam.Login.LoginForm;
import com.app.seam.R;

public class Splash extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView mImage;

    private int progressStatus = 0;

    private Animation goRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        mImage = findViewById(R.id.splash_image);

        goRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        mImage.startAnimation(goRotate);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 20;
                    try {
                        Thread.sleep(500);
                        progressBar.setProgress(progressStatus);
                        if (progressStatus == 100) {
                            startActivity(new Intent(Splash.this, LoginForm.class));
                            finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
