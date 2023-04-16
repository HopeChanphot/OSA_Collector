package com.crrepa.sdk.OSA_collector.scan;


import android.app.ProgressDialog;
import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.crrepa.ble.CRPBleClient;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.listener.CRPBleFirmwareUpgradeListener;
import com.crrepa.ble.scan.bean.CRPScanDevice;
import com.crrepa.ble.scan.callback.CRPScanCallback;
import com.crrepa.ble.trans.upgrade.bean.HSFirmwareInfo;
import com.crrepa.sdk.OSA_collector.LoginActivity;
import com.crrepa.sdk.OSA_collector.PermissionUtils;
import com.crrepa.sdk.OSA_collector.R;
import com.crrepa.sdk.OSA_collector.RegistrationActivity;
import com.crrepa.sdk.OSA_collector.SampleApplication;
import com.crrepa.sdk.OSA_collector.device.DeviceActivity;
import com.crrepa.sdk.OSA_collector.oxp;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanActivity extends AppCompatActivity {
    private static final String TAG = "ScanActivity";
    private static final int SCAN_PERIOD = 10 * 1000;

    private static final int REQUEST_UPDATEBANDCONFIG = 4;
    private static final String[] PERMISSION_UPDATEBANDCONFIG = new String[]{
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.BLUETOOTH_SCAN",
            "android.permission.BLUETOOTH_CONNECT",
            "android.permission.CAMERA",
            "android.permission.FLASHLIGHT",
            "android.permission.WAKE_LOCK",
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.LOCATION_HARDWARE.camera2",
            "android.permission.LOCATION_HARDWARE.camera",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.BLUETOOTH_ADVERTISE",
            "android.hardware.bluetooth_le",
            "android.permission.ACCESS_BACKGROUND_LOCATION"};

    @BindView(R.id.btn_scan_toggle)
    Button scanToggleBtn;
    @BindView(R.id.scan_results)
    RecyclerView scanResults;
    @BindView(R.id.tv_firmware_fix_state)
    TextView tvFirmwareFixState;

    @BindView(R.id.registerb)
    Button registerbtn;
    @BindView(R.id.loginb)
    Button loginbtn;

    @BindView(R.id.buttontest)
    Button buttontest1;

    @BindView(R.id.buttonup)
    Button buttonup;

    private CRPBleClient mBleClient;
    private ScanResultsAdapter mResultsAdapter;
    private boolean mScanState = false;


    private static final String UPGRADE_APP_FILE_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "crrepa" + File.separator + "app_band-hs.bin";
    private static final String UPGRADE_USER_FILE_PATH = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "crrepa" + File.separator + "usr.bin";
    private static final String USER_START_ADDRESS = "23000";
    //    private static final String BAND_ADDRESS = "C1:C4:7C:DE:44:5B";
//    private static final String BAND_ADDRESS = "D9:4D:C2:BB:F3:F4";
    private static final String BAND_ADDRESS = "FB:09:C5:C7:1A:90";

    private Button Btn;

    Uri imageuri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        mBleClient = SampleApplication.getBleClient(this);

        configureResultList();

        requestPermissions();

        //Btn = findViewById(R.id.buttontest);

      //  Btn.setOnClickListener(new View.OnClickListener() {
       //     @Override
       //     public void onClick(View v)
       //     {
          //      test();
        //    }
     //   });
        buttonup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, 1);
            }
        });




    }
    ProgressDialog dialog;



    @Override
    protected void onPause() {
        super.onPause();
        cancelScan();
    }

    @SuppressLint("MissingPermission")
    @OnClick({R.id.btn_scan_toggle, R.id.btn_firmware_fix, R.id.btn_hs_upgrade, R.id.registerb, R.id.loginb, R.id.buttontest})
    public void onViewClicked(View view) {
        if (!mBleClient.isBluetoothEnable()) {
            Intent enableBtIntent;
            enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(enableBtIntent);
            return;
        }

        switch (view.getId()) {
            case R.id.btn_scan_toggle:
                if (mScanState) {
                    cancelScan();
                } else {
                    startScan();
                }
                break;
            case R.id.btn_firmware_fix:

                break;
            case R.id.btn_hs_upgrade:
                CRPBleClient bleClient = SampleApplication.getBleClient(this);
                CRPBleDevice bleDevice = bleClient.getBleDevice(BAND_ADDRESS);
                HSFirmwareInfo upgradeInfo = new HSFirmwareInfo();
                upgradeInfo.setAppFilePath(UPGRADE_APP_FILE_PATH);
                upgradeInfo.setUserFilePath(UPGRADE_USER_FILE_PATH);
                upgradeInfo.setUserStartAddress(USER_START_ADDRESS);


                break;
            case R.id.registerb:
                Intent intent
                        = new Intent(ScanActivity.this,
                        RegistrationActivity.class);
                startActivity(intent);



                break;

            case R.id.loginb:
                intent = new Intent(ScanActivity.this,
                        LoginActivity.class);
                startActivity(intent);



                break;
            case R.id.buttontest:
                Intent intent2
                        = new Intent(ScanActivity.this, oxp.class);
                startActivity(intent2);

                break;

        }

    }

    private void startScan() {
        boolean success;
        if (mBleClient.scanDevice(new CRPScanCallback() {
            @Override
            public void onScanning(final CRPScanDevice device) {
                Log.d(TAG, "address: " + device.getDevice().getAddress());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mResultsAdapter.addScanResult(device);
                    }
                });
            }

            @Override
            public void onScanComplete(List<CRPScanDevice> results) {
                if (mScanState) {
                    mScanState = false;
                    updateButtonUIState();
                }
            }
        }, SCAN_PERIOD)) success = true;
        else success = false;
        if (success) {
            mScanState = true;
            updateButtonUIState();
            mResultsAdapter.clearScanResults();
        }
    }

    private void cancelScan() {
        mBleClient.cancelScan();
    }


    private void configureResultList() {
        scanResults.setHasFixedSize(true);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        scanResults.setLayoutManager(recyclerLayoutManager);
        mResultsAdapter = new ScanResultsAdapter();
        scanResults.setAdapter(mResultsAdapter);
        mResultsAdapter.setOnAdapterItemClickListener(new ScanResultsAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterViewClick(View view) {
                final int childAdapterPosition = scanResults.getChildAdapterPosition(view);
                final CRPScanDevice itemAtPosition = mResultsAdapter.getItemAtPosition(childAdapterPosition);
                onAdapterItemClick(itemAtPosition);
            }
        });
    }


    private void onAdapterItemClick(CRPScanDevice scanResults) {
        final String macAddress = scanResults.getDevice().getAddress();
        mBleClient.cancelScan();

        final Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra(DeviceActivity.DEVICE_MACADDR, macAddress);
        startActivity(intent);
    }


    private void updateButtonUIState() {
        scanToggleBtn.setText(mScanState ? R.string.stop_scan : R.string.start_scan);
    }

    CRPBleFirmwareUpgradeListener mFirmwareUpgradeListener = new CRPBleFirmwareUpgradeListener() {
        @Override
        public void onFirmwareDownloadStarting() {
            Log.d(TAG, "onFirmwareDownloadStarting");
            updateTextView(tvFirmwareFixState, getString(R.string.dfu_status_download_starting));
        }

        @Override
        public void onFirmwareDownloadComplete() {
            Log.d(TAG, "onFirmwareDownloadComplete");
            updateTextView(tvFirmwareFixState, getString(R.string.dfu_status_download_complete));
        }

        @Override
        public void onUpgradeProgressStarting() {
            Log.d(TAG, "onUpgradeProgressStarting");
            updateTextView(tvFirmwareFixState, getString(R.string.dfu_status_starting));
        }

        @Override
        public void onUpgradeProgressChanged(int percent, float speed) {
            Log.d(TAG, "onUpgradeProgressChanged: " + percent);
            String status = String.format(getString(R.string.dfu_status_uploading_part), percent);
            updateTextView(tvFirmwareFixState, status);
        }

        @Override
        public void onUpgradeCompleted() {
            Log.d(TAG, "onUpgradeCompleted");
            updateTextView(tvFirmwareFixState, getString(R.string.dfu_status_completed));
        }

        @Override
        public void onUpgradeAborted() {
            Log.d(TAG, "onUpgradeAborted");
            updateTextView(tvFirmwareFixState, getString(R.string.dfu_status_aborted));
        }

        @Override
        public void onError(int errorType, String message) {
            Log.d(TAG, "onError: " + errorType);
            updateTextView(tvFirmwareFixState, message);
        }
    };

    void updateTextView(final TextView view, final String con) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(con);
            }
        });
    }


    void requestPermissions() {
        if (!PermissionUtils.hasSelfPermissions(this, PERMISSION_UPDATEBANDCONFIG)) {
            ActivityCompat.requestPermissions(
                    this, PERMISSION_UPDATEBANDCONFIG, REQUEST_UPDATEBANDCONFIG);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the required permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, REQUEST_UPDATEBANDCONFIG);
        } else {

        }



    }

    public void test(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // Here we are initialising the progress dialog box
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");

            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            imageuri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final String messagePushID = timestamp;
            Toast.makeText(this, imageuri.toString(), Toast.LENGTH_SHORT).show();

            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(messagePushID + "." + "csv");
            Toast.makeText(this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageuri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // After uploading is done it progress
                        // dialog box will be dismissed
                        dialog.dismiss();
                        Uri uri = task.getResult();
                        String myurl;
                        myurl = uri.toString();
                        Toast.makeText(ScanActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(ScanActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_UPDATEBANDCONFIG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions have been granted, start scanning for BLE devices
             //   startScan();
            } else {
                // Permissions have been denied, show an error message
                Toast.makeText(this, "Location permission is required to scan for BLE devices.", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
