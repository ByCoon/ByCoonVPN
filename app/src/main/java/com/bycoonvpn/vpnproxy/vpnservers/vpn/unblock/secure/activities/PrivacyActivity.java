package com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.R;

public class PrivacyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        findViewById(R.id.back_button_privacy).setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }
}