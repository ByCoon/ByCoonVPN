package com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.R;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core.ApplicationClass;

public class TermsActivity extends AppCompatActivity {
    private CheckBox checkBox;
    private AppCompatButton declineButton,acceptButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        initialize();
        listeners();
    }
    private void initialize(){
        checkBox = findViewById(R.id.terms_checkbox);
        declineButton = findViewById(R.id.decline_terms_button);
        acceptButton = findViewById(R.id.accept_terms_button);
    }
    private void listeners(){
        declineButton.setOnClickListener(view -> finishAffinity());
        acceptButton.setOnClickListener(view -> {
            if (checkBox.isChecked()){
                ApplicationClass.setStringPreference("Terms","Accepted",TermsActivity.this);
                Intent intent = new Intent(TermsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(this, "please tick the checkbox first", Toast.LENGTH_SHORT).show();
            }
        });
    }
}