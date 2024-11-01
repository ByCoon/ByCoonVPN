package com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.R;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core.ApplicationClass;
import com.google.android.material.progressindicator.LinearProgressIndicator;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private LinearProgressIndicator splashProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initialize();
        backDispatcher();
        splashProgress();
    }
    private void initialize(){
        splashProgress = findViewById(R.id.splash_progress);
    }
    private void backDispatcher(){
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        });
    }
    private void splashProgress(){
        ValueAnimator animator = ValueAnimator.ofInt(0, splashProgress.getMax());
        animator.setDuration(3000);
        animator.addUpdateListener(animation -> splashProgress.setProgress((Integer)animation.getAnimatedValue()));
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent;
                if (ApplicationClass.getStringPreference("Terms", SplashActivity.this).isEmpty()){
                    intent = new Intent(SplashActivity.this,TermsActivity.class);
                }else {
                    intent = new Intent(SplashActivity.this,MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        });
        animator.start();
    }
}