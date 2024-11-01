package com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Locale;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.api.APIVpnProfile;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.api.IOpenVPNAPIService;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.api.IOpenVPNStatusCallback;
import com.bycoonvpn.vpnproxy.vpnservers.vpn.unblock.secure.core.ApplicationClass;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity implements Handler.Callback {
    private final int SERVICE_REQUEST_CODE = 100;
    private final int PERMISSION_REQUEST_CODE = 200;
    private DrawerLayout drawerLayout;
    private View navIncludedView;
    private ImageView hamburger;
    private CircularProgressIndicator vpnProgress;
    private ConstraintLayout vpnButton;
    private ImageView vpnButtonConnect;
    private TextView vpnButtonDisconnect;
    private TextView timerText;
    private boolean vpnStart = false;
    protected IOpenVPNAPIService mService = null;
    private Handler mHandler;
    private int checkedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        backDispatcher();
        listeners();
    }
    private void initialize(){
        drawerLayout = findViewById(R.id.drawer_layout);
        navIncludedView = findViewById(R.id.navigation_included_view);
        hamburger = findViewById(R.id.hamburger);
        vpnProgress = findViewById(R.id.vpn_progress);
        vpnButtonConnect = findViewById(R.id.vpn_button_connect);
        vpnButtonDisconnect = findViewById(R.id.vpn_button_disconnect);
        vpnButton = findViewById(R.id.vpn_button);
        timerText = findViewById(R.id.timer_text);
    }
    private void backDispatcher(){
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    exitDialog();
                }
            }
        });
    }
    private void exitDialog(){
        new MaterialAlertDialogBuilder(MainActivity.this,R.style.Custom)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit ByCoon VPN?")
                .setPositiveButton("Exit", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finishAffinity();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
    private void themeDialog(){
        String[] singleItems = new String[]{"System Default", "Light", "Dark"};
        checkedItem = ApplicationClass.getIntPreference(MainActivity.this,"Theme");
        new MaterialAlertDialogBuilder(MainActivity.this,R.style.Custom)
                .setTitle("Select Theme")
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Select", (dialog, which) -> {
                    dialog.dismiss();
                    ApplicationClass.setIntPreference(MainActivity.this,"Theme",checkedItem);
                    if (checkedItem == 0){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }else if (checkedItem == 1){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }else if (checkedItem == 2){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                })
                .setSingleChoiceItems(singleItems, checkedItem, (dialog, which) -> checkedItem = which)
                .show();
    }
    private void listeners(){
        hamburger.setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            }else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        vpnButton.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkPermission()) {
                requestMultiplePermissions.launch(new String[]{Manifest.permission.POST_NOTIFICATIONS});
                return;
            }
            Intent intent = VpnService.prepare(this);
            if (intent != null){
                startActivityForResult(intent,PERMISSION_REQUEST_CODE);
            }else {
                onActivityResult(PERMISSION_REQUEST_CODE, Activity.RESULT_OK, null);
            }
        });
        navIncludedView.findViewById(R.id.change_theme_drawer).setOnClickListener(view -> themeDialog());
        navIncludedView.findViewById(R.id.rate_us_drawer).setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()))));
        navIncludedView.findViewById(R.id.share_drawer).setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="+ getPackageName());
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent, "Share via");
            startActivity(sendIntent);
        });
        findViewById(R.id.privacy_icon).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,PrivacyActivity.class);
            startActivity(intent);
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean checkPermission(){
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED;
    }
    private final ActivityResultLauncher<String[]> requestMultiplePermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkPermission()){
                boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS);
                if (shouldShowRationale){
                    rationaleDialog();
                }else {
                    settingsDialog();
                }
            }
        }
    });
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void rationaleDialog(){
        new MaterialAlertDialogBuilder(MainActivity.this,R.style.Custom)
                .setTitle("Permission Required")
                .setMessage("We need Notifications permission for working of ByCoon VPN. Please consider granting this permission.")
                .setPositiveButton("Grant", (dialogInterface, i) -> {
                    requestMultiplePermissions.launch(new String[]{Manifest.permission.POST_NOTIFICATIONS});
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
    private void settingsDialog(){
        new MaterialAlertDialogBuilder(MainActivity.this,R.style.Custom)
                .setTitle("Permission Required")
                .setMessage("We need Notifications permission for working of ByCoon VPN. Please grant this permission from app settings screen.")
                .setPositiveButton("Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
    private void stopVpn(){
        try {
            mService.disconnect();
        } catch (RemoteException ignored) {}
    }
    private void bindService() {
        Intent intent = new Intent(IOpenVPNAPIService.class.getName());
        intent.setPackage(getPackageName());
        this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mHandler = new Handler(this);
        bindService();
    }
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IOpenVPNAPIService.Stub.asInterface(service);
            try {
                Intent i = mService.prepare(getPackageName());
                if (i!=null) {
                    startActivityForResult(i, SERVICE_REQUEST_CODE);
                } else {
                    onActivityResult(SERVICE_REQUEST_CODE, Activity.RESULT_OK,null);
                }
            } catch (RemoteException ignored) {

            }
        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SERVICE_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                mService.registerStatusCallback(mCallback);
            } catch (RemoteException ignored) {}
        }else if (requestCode == PERMISSION_REQUEST_CODE && resultCode == RESULT_OK){
            if (vpnStart){
                stopVpn();
            }else {
                startVpn();
            }
        }
    }
    private final IOpenVPNStatusCallback mCallback = new IOpenVPNStatusCallback.Stub() {
        @Override
        public void newStatus(String uuid, String state, String message, String level) {
            Log.e("VPNMY", "onActivityResult: " + state);
            Message msg = Message.obtain(mHandler, 100, state);
            msg.sendToTarget();
        }
    };
    @SuppressLint("SetTextI18n")
    @Override
    public boolean handleMessage(@NonNull Message message) {
        if(message.what == 100) {
            String messageText = (String) message.obj;
            if (messageText.equals("NOPROCESS")){
                timerText.setText("00:00:00");
                vpnStart = false;
                vpnButtonConnect.setVisibility(View.VISIBLE);
                vpnButtonDisconnect.setVisibility(View.INVISIBLE);
                vpnProgress.setIndeterminate(false);
                vpnProgress.setProgress(0);
            }
            if (messageText.equals("VPN_GENERATE_CONFIG")){
                vpnStart = true;
                vpnButtonDisconnect.setVisibility(View.VISIBLE);
                vpnButtonConnect.setVisibility(View.INVISIBLE);
                vpnProgress.setIndeterminate(true);
            }
            if (messageText.equals("CONNECTED")){
                vpnStart = true;
                vpnButtonDisconnect.setVisibility(View.VISIBLE);
                vpnButtonConnect.setVisibility(View.INVISIBLE);
                vpnProgress.setIndeterminate(false);
                vpnProgress.setProgress(100);
            }
        }
        return true;
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeReceiver);
        super.onPause();
    }
    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(timeReceiver, new IntentFilter("com.example.UPDATE_TIME"));
        super.onResume();
    }
    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long elapsedTime = intent.getLongExtra("elapsedTime", 0);
            updateTimer(elapsedTime);
        }
    };
    private void updateTimer(long elapsedTime) {
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / 1000) / 60) % 60;
        int hours = (int) ((elapsedTime / 1000) / 3600);
        timerText.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds));
    }
    private void copyOvpnFromAssetsToTempFile(String assetFileName, File tempFile) throws IOException {
        InputStream assetInputStream = getAssets().open(assetFileName);
        OutputStream tempFileOutputStream = new FileOutputStream(tempFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = assetInputStream.read(buffer)) > 0) {
            tempFileOutputStream.write(buffer, 0, length);
        }
        assetInputStream.close();
        tempFileOutputStream.close();
    }
    private void startVpn() {
        try {
            File localFile = File.createTempFile("vpn_config", ".ovpn", getCacheDir());
            copyOvpnFromAssetsToTempFile("freeopenvpn.ovpn", localFile);
            try (InputStream conf = new FileInputStream(localFile);
                 InputStreamReader isr = new InputStreamReader(conf);
                 BufferedReader br = new BufferedReader(isr)) {
                StringBuilder configBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    configBuilder.append(line).append("\n");
                }
                String config = configBuilder.toString();
                APIVpnProfile profile = mService.addNewVPNProfile("USA", false, config);
                mService.startProfile(profile.mUUID);
                mService.startVPN(config);
            } catch (IOException | RemoteException ignored) {

            }
        } catch (IOException ignored) {

        }
    }
}