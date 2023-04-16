package com.crrepa.sdk.OSA_collector.device;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ProgressBar;

import com.crrepa.sdk.OSA_collector.ImageProcessing;
import com.crrepa.sdk.OSA_collector.Math.Fft;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;



import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;



import com.crrepa.sdk.OSA_collector.BloodOxygenData;
import com.crrepa.sdk.OSA_collector.PermissionUtils;
import com.crrepa.sdk.OSA_collector.SleepData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.crrepa.ble.CRPBleClient;
import com.crrepa.ble.conn.CRPBleConnection;
import com.crrepa.ble.conn.CRPBleDevice;
import com.crrepa.ble.conn.bean.CRPAlarmClockInfo;
import com.crrepa.ble.conn.bean.CRPBloodOxygenInfo;
import com.crrepa.ble.conn.bean.CRPContactInfo;
import com.crrepa.ble.conn.bean.CRPFirmwareVersionInfo;
import com.crrepa.ble.conn.bean.CRPFunctionInfo;
import com.crrepa.ble.conn.bean.CRPFutureWeatherInfo;
import com.crrepa.ble.conn.bean.CRPHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPMessageInfo;
import com.crrepa.ble.conn.bean.CRPMovementHeartRateInfo;
import com.crrepa.ble.conn.bean.CRPPeriodTimeInfo;
import com.crrepa.ble.conn.bean.CRPPhysiologcalPeriodInfo;
import com.crrepa.ble.conn.bean.CRPQuickContactConfigInfo;
import com.crrepa.ble.conn.bean.CRPSleepActionInfo;
import com.crrepa.ble.conn.bean.CRPSleepInfo;
import com.crrepa.ble.conn.bean.CRPStepInfo;
import com.crrepa.ble.conn.bean.CRPStepsCategoryInfo;
import com.crrepa.ble.conn.bean.CRPSupportWatchFaceInfo;
import com.crrepa.ble.conn.bean.CRPTempInfo;
import com.crrepa.ble.conn.bean.CRPTodayWeatherInfo;
import com.crrepa.ble.conn.bean.CRPUserInfo;
import com.crrepa.ble.conn.bean.CRPWatchFaceBackgroundInfo;
import com.crrepa.ble.conn.bean.CRPWatchFaceInfo;
import com.crrepa.ble.conn.bean.CRPWatchFaceLayoutInfo;
import com.crrepa.ble.conn.callback.CRPDeviceAlarmClockCallback;
import com.crrepa.ble.conn.callback.CRPDeviceBreathingLightCallback;
import com.crrepa.ble.conn.callback.CRPDeviceDisplayWatchFaceCallback;
import com.crrepa.ble.conn.callback.CRPDeviceFirmwareVersionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceFunctionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceGoalStepCallback;
import com.crrepa.ble.conn.callback.CRPDeviceLanguageCallback;
import com.crrepa.ble.conn.callback.CRPDeviceMetricSystemCallback;
import com.crrepa.ble.conn.callback.CRPDeviceNewFirmwareVersionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceOtherMessageCallback;
import com.crrepa.ble.conn.callback.CRPDevicePeriodTimeCallback;
import com.crrepa.ble.conn.callback.CRPDevicePhysiologcalPeriodCallback;
import com.crrepa.ble.conn.callback.CRPDeviceQuickViewCallback;
import com.crrepa.ble.conn.callback.CRPDeviceSedentaryReminderCallback;
import com.crrepa.ble.conn.callback.CRPDeviceSupportWatchFaceCallback;
import com.crrepa.ble.conn.callback.CRPDeviceTimeSystemCallback;
import com.crrepa.ble.conn.callback.CRPDeviceVersionCallback;
import com.crrepa.ble.conn.callback.CRPDeviceWatchFaceCallback;
import com.crrepa.ble.conn.callback.CRPDeviceWatchFaceLayoutCallback;
import com.crrepa.ble.conn.callback.CRPQuickContactConfigCallback;
import com.crrepa.ble.conn.listener.CRPBleConnectionStateListener;
import com.crrepa.ble.conn.listener.CRPBleECGChangeListener;
import com.crrepa.ble.conn.listener.CRPBleFirmwareUpgradeListener;
import com.crrepa.ble.conn.listener.CRPBloodOxygenChangeListener;
import com.crrepa.ble.conn.listener.CRPBloodPressureChangeListener;
import com.crrepa.ble.conn.listener.CRPContactListener;
import com.crrepa.ble.conn.listener.CRPDeviceBatteryListener;
import com.crrepa.ble.conn.listener.CRPFileTransListener;
import com.crrepa.ble.conn.listener.CRPFindPhoneListener;
import com.crrepa.ble.conn.listener.CRPHeartRateChangeListener;
import com.crrepa.ble.conn.listener.CRPMovementStateListener;
import com.crrepa.ble.conn.listener.CRPPhoneOperationListener;
import com.crrepa.ble.conn.listener.CRPSleepActionChangeListener;
import com.crrepa.ble.conn.listener.CRPSleepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepChangeListener;
import com.crrepa.ble.conn.listener.CRPStepsCategoryChangeListener;
import com.crrepa.ble.conn.listener.CRPTempChangeListener;
import com.crrepa.ble.conn.listener.CRPTransListener;
import com.crrepa.ble.conn.type.CRPBleMessageType;
import com.crrepa.ble.conn.type.CRPDeviceLanguageType;
import com.crrepa.ble.conn.type.CRPDeviceVersionType;
import com.crrepa.ble.conn.type.CRPEcgMeasureType;
import com.crrepa.ble.conn.type.CRPFirmwareUpgradeType;
import com.crrepa.ble.conn.type.CRPHeartRateType;
import com.crrepa.ble.conn.type.CRPHistoryDynamicRateType;
import com.crrepa.ble.conn.type.CRPMetricSystemType;
import com.crrepa.ble.conn.type.CRPPastTimeType;
import com.crrepa.ble.conn.type.CRPStepsCategoryDateType;
import com.crrepa.ble.conn.type.CRPTimeSystemType;
import com.crrepa.ble.conn.type.CRPWatchFaceLayoutType;
import com.crrepa.ble.conn.type.CRPWatchFaceType;
import com.crrepa.ble.conn.type.CRPWeatherId;
import com.crrepa.ble.trans.tp.CRPTpHelper;
import com.crrepa.ble.trans.tp.CRPTpInfo;
import com.crrepa.sdk.OSA_collector.R;
import com.crrepa.sdk.OSA_collector.SampleApplication;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;





public class DeviceActivity extends AppCompatActivity {


    private static final int REQUEST_UPDATEBANDCONFIG = 4;
    private static final String[] PERMISSION_UPDATEBANDCONFIG = new String[]{
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.BLUETOOTH_SCAN",
            "android.permission.BLUETOOTH_CONNECT",
            "android.permission.RECORD_AUDIO",
            "android.permission.STORAGE",
            "android.permission.CAMERA",
            "android.permission.FLASHLIGHT",
            "android.permission.WAKE_LOCK" };




    private static int MICROPHONE_PERMISSION_CODE = 200;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private Runnable mTimer1;
    private Runnable mTimer2;
    private double graph2LastXValue = 5d;
    private LineGraphSeries<DataPoint> mSeries;
    private double graph1LastXValue = 5d;
    private LineGraphSeries<DataPoint> mSeries1;

    TextView a;

    Thread runner;

    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;


    public TextView RO2;


    private EditText headTextView;

    private TextView statusTV;

    private TextView tvrestful;

    private TextView tvlight;

    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;


    private StorageReference mStorage;

    private static final String LOG_TAG = "Record_log";

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;




    private Handler mHandler = new Handler();

    private Button sendDatabtn;

    FirebaseDatabase firebaseDatabase;
    FirebaseDatabase firebaseDatabase2;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;

    BloodOxygenData bloodOxygenData;

    SleepData sleepData;

    private static final String TAG = "DeviceActivity";
    public static final String DEVICE_MACADDR = "device_macaddr";

    private static final String UI_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "crrepa" + File.separator + "ota" + File.separator
            + "eaf49ccb4dbe5df51af35803662867d5.bin";

    ProgressDialog mProgressDialog;
    CRPBleClient mBleClient;
    CRPBleDevice mBleDevice;
    CRPBleConnection mBleConnection;
    boolean isUpgrade = false;

    @BindView(R.id.datetime)
    TextView thedate;


    @BindView(R.id.tv_connect_state)
    TextView tvConnectState;
    @BindView(R.id.tv_firmware_version)
    TextView tvFirmwareVersion;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.tv_step)
    TextView tvStep;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_calorie)
    TextView tvCalorie;
    @BindView(R.id.tv_restful)
    TextView tvRestful;
    @BindView(R.id.tv_light)
    TextView tvLight;
    @BindView(R.id.tv_heart_rate)
    TextView tvHeartRate;
    @BindView(R.id.tv_blood_pressure)
    TextView tvBloodPressure;
    @BindView(R.id.tv_upgrade_state)
    TextView tvUpgradeState;
    @BindView(R.id.btn_ble_connect_state)
    Button btnBleDisconnect;
    @BindView(R.id.tv_blood_oxygen)
    TextView tvBloodOxygen;
    @BindView(R.id.tv_blood_oxygen2)
    TextView tvBloodOxygen2;
    @BindView(R.id.tv_new_firmware_version)
    TextView tvNewFirmwareVersion;


    @BindView(R.id.btn_sendsleep)
    Button btnsendsleep;

    @BindView(R.id.buttono2)
    Button btno2;
    @BindView(R.id.buttono3)
    Button btno3;

    @BindView(R.id.iddb)
    TextView tdb;

    private String bandFirmwareVersion;

    private List<Integer> supportWatchFaceList;

    private GraphView graph;
    private GraphView graph2;

    public static final int REQUEST_CODE_PERMISSION = 1;


    private TextureView textureView;




    // Variables Initialization
    ///private static final String TAG = "OxygenMeasure";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private  Camera camera = null;
   // private static CameraDevice cameraDevice = null;

    private static WakeLock wakeLock = null;

    //Toast
    private Toast mainToast;

    // DataBase
    public String user;

    //ProgressBar
    private ProgressBar ProgO2;
    public int ProgP = 0;
    public int inc = 0;

    //Freq + timer variable
    private static long startTime = 0;
    private double SamplingFreq;

    // SPO2 variables
    private static final double RedBlueRatio = 0;
    double Stdr = 0;
    double Stdb = 0;
    double sumred = 0;
    double sumblue = 0;
    public int o2;

    //Arraylist
    public ArrayList<Double> RedAvgList = new ArrayList<Double>();
    public ArrayList<Double> BlueAvgList = new ArrayList<Double>();
    public int counter = 0;



    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;

    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;




    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        initView();


       // GraphView graph = findViewById(R.id.graph2);
        graph = findViewById(R.id.graph2);

        initGraph(graph);


      //  GraphView graph2 = findViewById(R.id.graph3);
        graph2 = findViewById(R.id.graph3);

        initGraph2(graph2);



        Button btnRecord,btnStop;


        headTextView = findViewById(R.id.Headtext);

        tvrestful = findViewById(R.id.tv_restful);
        tvlight = findViewById(R.id.tv_light);

        statusTV = findViewById(R.id.idTVstatus);

        tdb = findViewById(R.id.iddb);

       // mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
       // mFileName += "/recorded_audio.3gp";


      //  wavClass wavObj = new wavClass(Environment.getExternalStorageDirectory().getPath());



        firebaseDatabase = FirebaseDatabase.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference();

        // below line is used to get reference for our database.
        databaseReference = firebaseDatabase.getReference("BloodOxygenData");
        databaseReference = firebaseDatabase.getReference("SleepData");
        // initializing our object
        // class variable.
        bloodOxygenData = new BloodOxygenData();
        sleepData = new SleepData();

        sendDatabtn = findViewById(R.id.btn_start_measure_blood_oxygen2);



        mProgressDialog = new ProgressDialog(this);
        String macAddr = getIntent().getStringExtra(DEVICE_MACADDR);
        if (TextUtils.isEmpty(macAddr)) {
            finish();
            return;
        }

        mBleClient = SampleApplication.getBleClient(this);
        mBleDevice = mBleClient.getBleDevice(macAddr);
        if (mBleDevice != null && !mBleDevice.isConnected()) {
            connect();
        }
        if(isMicrophonePresent()){
            getMicrophonePermission();
        }




     //   btnRecord = findViewById(R.id.btnRecord);
      //  btnStop = findViewById(R.id.btnStop);
       // btnRecord.setOnClickListener(v -> {
          //  if(CheckPermissions()) {
          //      wavObj.startRecording();
          //  }
         //   if(!CheckPermissions()){
           //     RequestPermissions();
           // }
      //  });
       // btnStop.setOnClickListener(v -> wavObj.stopRecording() );
      //  btnStop.setOnClickListener(v -> wavObj.uploadAudio() );

        preview = findViewById(R.id.preview);
        previewHolder = preview.getHolder();
      //  previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        ProgO2 = findViewById(R.id.O2PB);
        ProgO2.setProgress(0);

        // WakeLock Initialization : Forces the phone to stay On
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        RO2 = findViewById(R.id.O2R);

    }

    //Prevent the system from restarting your activity during certain configuration changes,
    // but receive a callback when the configurations do change, so that you can manually update your activity as necessary.
    //such as screen orientation, keyboard availability, and language

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBleDevice != null) {
            mBleDevice.disconnect();
        }
    }

    void initView() {
        updateStepInfo(0, 0, 0);
        updateSleepInfo(0, 0);
    }

    void connect() {
        mProgressDialog.show();
        mBleConnection = mBleDevice.connect();
        mBleConnection.setConnectionStateListener(new CRPBleConnectionStateListener() {
            @Override
            public void onConnectionStateChange(int newState) {
                Log.d(TAG, "onConnectionStateChange: " + newState);
                int state = -1;
                switch (newState) {
                    case CRPBleConnectionStateListener.STATE_CONNECTED:
                        state = R.string.state_connected;
                        mProgressDialog.dismiss();
                        updateTextView(btnBleDisconnect, getString(R.string.disconnect));
                        testSet();
                        break;
                    case CRPBleConnectionStateListener.STATE_CONNECTING:
                        state = R.string.state_connecting;
                        break;
                    case CRPBleConnectionStateListener.STATE_DISCONNECTED:
                        closeGatt();
                        state = R.string.state_disconnected;
                        mProgressDialog.dismiss();
                        updateTextView(btnBleDisconnect, getString(R.string.connect));
                        break;
                }
                updateConnectState(state);
            }
        });

        mBleConnection.setStepChangeListener(mStepChangeListener);
        mBleConnection.setSleepChangeListener(mSleepChangeListener);
        mBleConnection.setHeartRateChangeListener(mHeartRateChangListener);
        mBleConnection.setBloodPressureChangeListener(mBloodPressureChangeListener);
        mBleConnection.setBloodOxygenChangeListener(mBloodOxygenChangeListener);
        mBleConnection.setFindPhoneListener(mFindPhoneListener);
        mBleConnection.setECGChangeListener(mECGChangeListener, CRPEcgMeasureType.TI);
        mBleConnection.setStepsCategoryListener(mStepsCategoryChangeListener);
        mBleConnection.setSleepActionChangeListener(mSleepActionChangeListener);
        mBleConnection.setMovementStateListener(mMovementStateListener);
        mBleConnection.setTempChangeListener(mTempChangeListener);
        mBleConnection.setContactListener(mContactListener);
     //   mBleConnection.setHrvChangeListener(mHrvChangeListener);
      ///  mBleConnection.setHrvChangeListener(mHrvChangeListener);
    }

    private void closeGatt() {
        if (mBleConnection != null) {
            mBleConnection.close();
        }
    }

    private void testSet() {
        Log.d(TAG, "testSet");

//        mBleConnection.queryTimingMeasureTemp(CRPTempTimeType.TODAY);
//        mBleConnection.queryTimingMeasureTemp(CRPTempTimeType.YESTERDAY);

//        mBleConnection.queryTodayHeartRate(CRPHeartRateType.ALL_DAY_HEART_RATE);
//
//        mBleConnection.syncTime();
//        mBleConnection.queryPastHeartRate();
//        mBleConnection.syncStep();
//        mBleConnection.syncSleep();
//        mBleConnection.syncPastStep(CRPPastTimeType.YESTERDAY_STEPS);
//        mBleConnection.syncPastStep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_STEPS);
//        mBleConnection.syncPastSleep(CRPPastTimeType.YESTERDAY_SLEEP);
//        mBleConnection.syncPastSleep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_SLEEP);
//        mBleConnection.queryStepsCategory(CRPStepsCategoryDateType.TODAY_STEPS_CATEGORY);

//        sendFindBandMessage();
    }

    private void sendFindBandMessage() {
        handler.sendEmptyMessageDelayed(1, 5000);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mBleConnection.findDevice();
                sendFindBandMessage();
            }
        }
    };


    @OnClick(R.id.btn_ble_connect_state)
    public void onConnectStateClick() {
        if (mBleDevice.isConnected()) {
            mBleDevice.disconnect();
        } else {
            connect();
        }
    }


    @OnClick({R.id.btn_query_firmware, R.id.btn_query_battery, R.id.btn_sync_time,
            R.id.btn_send_user_info, R.id.btn_sync_step,
            R.id.btn_sync_sleep, R.id.btn_send_metric_system, R.id.btn_send_time_system,
            R.id.btn_send_quick_view, R.id.btn_send_goal_step, R.id.btn_query_goal_step,
            R.id.btn_find_band, R.id.btn_send_message, R.id.btn_query_time_system,
            R.id.btn_query_metric_system, R.id.btn_query_quick_view, R.id.btn_send_alarm_clock,
            R.id.btn_query_alarm_clock, R.id.btn_send_quickview_time,
            R.id.btn_query_quickview_time, R.id.btn_send_device_language,
            R.id.btn_query_device_language, R.id.btn_music_control,
            R.id.btn_send_other_message, R.id.btn_query_other_message,
            R.id.btn_send_sedentary_reminder, R.id.btn_query_sedentary_reminder,
            R.id.btn_send_display_watch_face, R.id.btn_query_display_watch_face,
            R.id.btn_start_measure_heart_rate, R.id.btn_stop_measure_heart_rate,
            R.id.btn_send_device_version, R.id.btn_query_device_version,
            R.id.btn_start_measure_blood_pressure, R.id.btn_stop_measure_blood_pressure,
            R.id.btn_sync_past_step, R.id.btn_sync_past_sleep, R.id.btn_sync_last_heart_rate,
            R.id.btn_firmware_upgrade, R.id.btn_send_today_weather, R.id.btn_send_future_weather,
            R.id.btn_open_24_hreat_rate, R.id.btn_close_24_hreat_rate,
            R.id.btn_query_today_hreat_rate, R.id.btn_query_yesterday_hreat_rate,
            R.id.btn_start_measure_blood_oxygen, R.id.btn_stop_measure_blood_oxygen,
            R.id.btn_send_device_function, R.id.btn_query_device_function,
            R.id.btn_query_movement_hreat_rate, R.id.btn_send_breathing_light,
            R.id.btn_query_breathing_light, R.id.btn_switch_background,
            R.id.btn_send_watch_face_layout, R.id.btn_query_watch_face_layout,
            R.id.btn_check_firmware, R.id.btn_start_measure_ecg, R.id.btn_stop_measure_ecg,
            R.id.btn_query_last_ecg, R.id.btn_query_today_steps_category,
            R.id.btn_query_yesterday_steps_category, R.id.btn_query_support_watch_face,
            R.id.btn_query_watch_face_store, R.id.btn_query_watch_face,
            R.id.btn_query_device_support_function, R.id.btn_send_physiologcal_period,
            R.id.btn_query_physiologcal_period, R.id.btn_query_sleep_action, R.id.btn_send_band_ui,
            R.id.btn_query_quick_contacts, R.id.btn_send_quick_contacts,
            R.id.btn_delete_quick_contacts, R.id.btn_enable_hrv, R.id.btn_disable_hrv,
            R.id.btn_query_hrv_measure_interval, R.id.btn_query_hrv_measure_count,
            R.id.btn_query_hrv_measure_result,
            R.id.btn_start_measure_blood_oxygen2, R.id.btn_stop_measure_blood_oxygen2,
            R.id.btnRecord,R.id.btnStop,R.id.btnPlay,R.id.btnStopPlay, R.id.btn_sendsleep, R.id.imgshare2, R.id.imgshare3, R.id.buttono2, R.id.buttono3 })
    public void onViewClicked(View view) {

        //wavClass wavObj = new wavClass(Environment.getExternalStorageDirectory().getPath());



        if (!mBleDevice.isConnected()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_query_physiologcal_period:
                mBleConnection.queryPhysiologcalPeriod(new CRPDevicePhysiologcalPeriodCallback() {
                    @Override
                    public void onPhysiologcalPeriod(CRPPhysiologcalPeriodInfo info) {
                        Log.d(TAG, "isMenstrualReminder: " + info.isMenstrualReminder());
                        Log.d(TAG, "isOvulationEndReminder: " + info.isOvulationEndReminder());
                        Log.d(TAG, "isOvulationDayReminder: " + info.isOvulationDayReminder());
                        Log.d(TAG, "isOvulationReminder: " + info.isOvulationReminder());
                        Log.d(TAG, "physiologcalPeriod: " + info.getPhysiologcalPeriod());
                        Log.d(TAG, "startDate: " + info.getStartDate());
                    }
                });
                break;
            case R.id.btn_send_physiologcal_period:
                CRPPhysiologcalPeriodInfo physiologcalPeriodInfo = new CRPPhysiologcalPeriodInfo();
                physiologcalPeriodInfo.setReminderHour(10);
                physiologcalPeriodInfo.setReminderMinute(0);
                physiologcalPeriodInfo.setStartDate(new Date());
                physiologcalPeriodInfo.setMenstrualPeriod(5);
                physiologcalPeriodInfo.setPhysiologcalPeriod(28);
                physiologcalPeriodInfo.setOvulationEndReminder(true);
                physiologcalPeriodInfo.setMenstrualReminder(true);
                physiologcalPeriodInfo.setOvulationReminder(true);
                physiologcalPeriodInfo.setOvulationDayReminder(true);
                mBleConnection.sendPhysiologcalPeriod(physiologcalPeriodInfo);
                break;
            case R.id.btn_query_device_support_function:
                mBleConnection.queryDeviceSupportFunction(new CRPDeviceFunctionCallback() {
                    @Override
                    public void onFunctionChenge(CRPFunctionInfo info) {
                        for (Integer integer : info.getFunctionList()) {
                            Log.d(TAG, "function: " + integer.intValue());
                        }
                    }
                });
                break;
            case R.id.btn_send_device_function:
                List<Integer> functionList = new ArrayList<>();
                functionList.add(CRPFunctionInfo.TIME_VIEW);
                functionList.add(CRPFunctionInfo.STEP_VIEW);
                functionList.add(CRPFunctionInfo.SLEEP_VIEW);
                functionList.add(CRPFunctionInfo.HR_VIEW);
                functionList.add(CRPFunctionInfo.TRAINING_VIEW);
                functionList.add(CRPFunctionInfo.BP_VIEW);
                functionList.add(CRPFunctionInfo.BO_VIEW);
                functionList.add(CRPFunctionInfo.WEATHER_VIEW);
                functionList.add(CRPFunctionInfo.MSG_LIST_VIEW);
                functionList.add(CRPFunctionInfo.MUSIC_PLAYER_VIEW);
                functionList.add(CRPFunctionInfo.CAMERA_VIEW);
                functionList.add(CRPFunctionInfo.OTHER_VIEW);
                CRPFunctionInfo functionInfo = new CRPFunctionInfo();
                functionInfo.setFunctionList(functionList);
                mBleConnection.sendDislpayDeviceFunction(functionInfo);
                break;
            case R.id.btn_query_device_function:
                mBleConnection.queryDisplayDeviceFunction(new CRPDeviceFunctionCallback() {
                    @Override
                    public void onFunctionChenge(CRPFunctionInfo info) {
                        for (Integer integer : info.getFunctionList()) {
                            Log.d(TAG, "function: " + integer.intValue());
                        }
                    }
                });
                break;
            case R.id.btn_query_firmware:
                mBleConnection.queryFrimwareVersion(new CRPDeviceFirmwareVersionCallback() {
                    @Override
                    public void onDeviceFirmwareVersion(String version) {
                        DeviceActivity.this.bandFirmwareVersion = version;
                        updateTextView(tvFirmwareVersion, version);
                    }
                });
                break;
            case R.id.btn_check_firmware:
                mBleConnection.checkFirmwareVersion(
                        new CRPDeviceNewFirmwareVersionCallback() {
                            @Override
                            public void onNewFirmwareVersion(CRPFirmwareVersionInfo info) {
                                String version = info.getVersion();
                                updateTextView(tvNewFirmwareVersion, version);
                                Log.d(TAG, "onNewFirmwareVersion tp: " + info.isTpUpgrade());
                                isUpgrade = true;
                            }

                            @Override
                            public void onLatestVersion() {
                                Log.d(TAG, "onLatestVersion");
                                isUpgrade = false;
                            }
                        },
                        DeviceActivity.this.bandFirmwareVersion,
                        CRPFirmwareUpgradeType.NORMAL_UPGEADE_TYPE);
                break;
            case R.id.btn_firmware_upgrade:
                if (isUpgrade) {
                    mBleConnection.startFirmwareUpgrade(false, mFirmwareUpgradeListener);
                }
                break;
            case R.id.btn_switch_background:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;

                Bitmap bitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.color_wheel, options);

                CRPWatchFaceBackgroundInfo watchFaceBackgroundInfo = new CRPWatchFaceBackgroundInfo(bitmap,
                        CRPWatchFaceLayoutInfo.CompressionType.ORIGINAL);

                mBleConnection.sendWatchFaceBackground(watchFaceBackgroundInfo, new CRPFileTransListener() {
                    @Override
                    public void onTransProgressStarting() {
                        Log.d(TAG, "onTransProgressStarting");
                    }

                    @Override
                    public void onTransProgressChanged(int percent) {
                        Log.d(TAG, "percent: " + percent);
                    }

                    @Override
                    public void onTransCompleted() {
                        Log.d(TAG, "onTransCompleted");
                    }

                    @Override
                    public void onError(int type) {

                    }
                });
                break;
            case R.id.btn_query_battery:
                mBleConnection.subscribeDeviceBattery();
                mBleConnection.setDeviceBatteryListener(new CRPDeviceBatteryListener() {
                    @Override
                    public void onSubscribe(boolean success) {
                        Log.d(TAG, " battery subscribed: " + success);
                    }

                    @Override
                    public void onDeviceBattery(int battery) {
                        updateTextView(tvBattery, battery + "%");
                    }
                });
                mBleConnection.queryDeviceBattery();
                break;
            case R.id.btn_sync_time:
                mBleConnection.syncTime();
                break;
            case R.id.btn_music_control:
                mBleConnection.setPhoneOperationListener(new CRPPhoneOperationListener() {
                    @Override
                    public void onOperationChange(int type) {
                        Log.d(TAG, "onOperationChange: " + type);
                    }
                });
                break;
            case R.id.btn_send_user_info:
                CRPUserInfo userInfo = new CRPUserInfo(
                        75, 178, CRPUserInfo.MALE, 24);
                mBleConnection.sendUserInfo(userInfo);
                break;
            case R.id.btn_sync_step:
                mBleConnection.syncStep();
//                mBleConnection.syncPastStep(CRPPastTimeType.YESTERDAY_STEPS);
                break;
            case R.id.btn_sync_past_step:
                mBleConnection.syncPastStep(CRPPastTimeType.YESTERDAY_STEPS);
                mBleConnection.syncPastStep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_STEPS);
                break;
            case R.id.btn_sync_sleep:
                mBleConnection.syncSleep();


                break;
            case R.id.btn_sync_past_sleep:
                mBleConnection.syncPastSleep(CRPPastTimeType.YESTERDAY_SLEEP);

                mBleConnection.syncPastSleep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_SLEEP);

                break;
            case R.id.btn_send_metric_system:
                mBleConnection.sendMetricSystem(CRPMetricSystemType.METRIC_SYSTEM);
                break;
            case R.id.btn_query_metric_system:
                mBleConnection.queryMetricSystem(new CRPDeviceMetricSystemCallback() {
                    @Override
                    public void onMetricSystem(int type) {
                        Log.d(TAG, "onMetricSystem: " + type);
                    }
                });
                break;
            case R.id.btn_send_time_system:
                mBleConnection.sendTimeSystem(CRPTimeSystemType.TIME_SYSTEM_12);
                break;
            case R.id.btn_query_time_system:
                mBleConnection.queryTimeSystem(new CRPDeviceTimeSystemCallback() {
                    @Override
                    public void onTimeSystem(int type) {
                        Log.d(TAG, "onTimeSystem: " + type);
                    }
                });
                break;
            case R.id.btn_send_quick_view:
                mBleConnection.sendQuickView(false);
                break;
            case R.id.btn_query_quick_view:
                mBleConnection.queryQuickView(new CRPDeviceQuickViewCallback() {
                    @Override
                    public void onQuickView(boolean state) {
                        Log.d(TAG, "onQuickView: " + state);
                    }
                });
                break;
            case R.id.btn_send_goal_step:
                mBleConnection.sendGoalSteps(10000);
                break;
            case R.id.btn_query_goal_step:
                mBleConnection.queryGoalStep(new CRPDeviceGoalStepCallback() {
                    @Override
                    public void onGoalStep(int steps) {
                        Log.d(TAG, "onGoalStep: " + steps);
                    }
                });
                break;
            case R.id.btn_find_band:
                mBleConnection.findDevice();
//                mBleConnection.stopFindPhond();
                break;
            case R.id.btn_send_message:
                CRPMessageInfo messageInfo = new CRPMessageInfo();
//                messageInfo.setMessage("李白：噫籲嚱，危乎高哉！蜀道之難，難於上青天！\n" +
//                        "蠶叢及魚鳧，開國何茫然！\n" +
//                        "爾來四萬八千歲，不與秦塞通人煙。\n" +
//                        "西當太白有鳥道，可以橫絕峨眉巔。\n" +
//                        "地崩山摧壯士死，然後天梯石棧相鉤連。\n" +
//                        "上有六龍回日之高標，下有衝波逆折之回川。\n" +
//                        "黃鶴之飛尚不得過，猿猱欲度愁攀援。\n" +
//                        "青泥何盤盤，百步九折縈巖巒。\n" +
//                        "捫參歷井仰脅息，以手撫膺坐長嘆。\n" +
//                        "問君西遊何時還？畏途巉巖不可攀。\n" +
//                        "但見悲鳥號古木，雄飛雌從繞林間。\n" +
//                        "又聞子規啼夜月，愁空山。\n" +
//                        "蜀道之難,難於上青天，使人聽此凋朱顏！");
                messageInfo.setMessage("test:\uD83D\uDCF9 Incoming video call");
                messageInfo.setType(CRPBleMessageType.MESSAGE_SMS);
                messageInfo.setVersionCode(174);
                messageInfo.setHs(false);
                messageInfo.setSmallScreen(true);
                mBleConnection.sendMessage(messageInfo);
                break;
            case R.id.btn_send_alarm_clock:
                CRPAlarmClockInfo clockInfo = new CRPAlarmClockInfo();
                clockInfo.setId(CRPAlarmClockInfo.FIRST_CLOCK);
                clockInfo.setHour(14);
                clockInfo.setMinute(24);
                clockInfo.setRepeatMode(CRPAlarmClockInfo.SINGLE);
                clockInfo.setDate(new Date());
                clockInfo.setEnable(true);
                mBleConnection.sendAlarmClock(clockInfo);
                break;
            case R.id.btn_query_alarm_clock:
                mBleConnection.queryAllAlarmClock(new CRPDeviceAlarmClockCallback() {
                    @Override
                    public void onAlarmClock(List<CRPAlarmClockInfo> list) {
                        for (CRPAlarmClockInfo alarmClockInfo : list) {
                            Date date = alarmClockInfo.getDate();
                            if (date != null) {
                                Log.d(TAG, "onAlarmClock: " + date.toString());
                            }
                        }
                    }
                });
                break;
            case R.id.btn_send_quickview_time:
                CRPPeriodTimeInfo info = new CRPPeriodTimeInfo();
                info.setStartHour(8);
                info.setStartMinute(0);
                info.setEndHour(20);
                info.setEndMinute(0);
                mBleConnection.sendQuickViewTime(info);
                break;
            case R.id.btn_query_quickview_time:
                mBleConnection.queryQuickViewTime(new CRPDevicePeriodTimeCallback() {
                    @Override
                    public void onPeriodTime(int type, CRPPeriodTimeInfo periodTimeInfo) {
                        int startHour = periodTimeInfo.getStartHour();
                        int startMinute = periodTimeInfo.getStartMinute();
                        int endHour = periodTimeInfo.getEndHour();
                        int endMinute = periodTimeInfo.getEndMinute();
                        Log.d(TAG, "startTime: " + startHour + ":" + startMinute);
                        Log.d(TAG, "endTime: " + endHour + ":" + endMinute);
                    }
                });
                break;
            case R.id.btn_send_device_language:
                mBleConnection.sendDeviceVersion(CRPDeviceVersionType.INTERNATIONAL_EDITION);
                mBleConnection.sendDeviceLanguage(CRPDeviceLanguageType.LANGUAGE_UKRAINIAN);
                break;
            case R.id.btn_query_device_language:
                mBleConnection.queryDeviceLanguage(new CRPDeviceLanguageCallback() {
                    @Override
                    public void onDeviceLanguage(int type, int[] languageArray) {
                        Log.d(TAG, "onDeviceLanguage display language: " + type);
                        for (int i : languageArray) {
                            Log.d(TAG, "onDeviceLanguage languageArray " + i);
                        }
                    }
                });
                break;
            case R.id.btn_send_other_message:
                mBleConnection.sendOtherMessageState(true);
                break;
            case R.id.btn_query_other_message:
                mBleConnection.queryOtherMessageState(new CRPDeviceOtherMessageCallback() {
                    @Override
                    public void onOtherMessage(boolean state) {
                        Log.d(TAG, "onOtherMessage: " + state);
                    }
                });
                break;
            case R.id.btn_send_sedentary_reminder:
                mBleConnection.sendSedentaryReminder(true);
                break;
            case R.id.btn_query_sedentary_reminder:
                mBleConnection.querySedentaryReminder(new CRPDeviceSedentaryReminderCallback() {
                    @Override
                    public void onSedentaryReminder(boolean state) {
                        Log.d(TAG, "onSedentaryReminder: " + state);
                    }
                });
                break;
            case R.id.btn_send_display_watch_face:
                mBleConnection.sendDisplayWatchFace(CRPWatchFaceType.FIRST_WATCH_FACE);
                break;
            case R.id.btn_query_display_watch_face:
                mBleConnection.queryDisplayWatchFace(new CRPDeviceDisplayWatchFaceCallback() {
                    @Override
                    public void onDisplayWatchFace(int type) {
                        Log.d(TAG, "onDisplayWatchFace: " + type);
                    }
                });
                break;
            case R.id.btn_start_measure_heart_rate:
                mBleConnection.startMeasureDynamicRate();
                mBleConnection.startMeasureOnceHeartRate();
                break;
            case R.id.btn_stop_measure_heart_rate:
                mBleConnection.stopMeasureDynamicRtae();
                mBleConnection.stopMeasureOnceHeartRate();
                break;
            case R.id.btn_sync_last_heart_rate:
                mBleConnection.queryLastDynamicRate(CRPHistoryDynamicRateType.FIRST_HEART_RATE);
                mBleConnection.queryLastDynamicRate(CRPHistoryDynamicRateType.SECOND_HEART_RATE);
                mBleConnection.queryLastDynamicRate(CRPHistoryDynamicRateType.THIRD_HEART_RATE);
                mBleConnection.queryMovementHeartRate();
                break;
            case R.id.btn_open_24_hreat_rate:
                mBleConnection.enableTimingMeasureHeartRate(1);
                break;
            case R.id.btn_close_24_hreat_rate:
                mBleConnection.disableTimingMeasureHeartRate();
                break;
            case R.id.btn_query_today_hreat_rate:
                mBleConnection.queryTodayHeartRate(CRPHeartRateType.ALL_DAY_HEART_RATE);
                break;
            case R.id.btn_query_yesterday_hreat_rate:
                mBleConnection.queryPastHeartRate();
                break;
            case R.id.btn_query_movement_hreat_rate:
                mBleConnection.queryMovementHeartRate();
                break;
            case R.id.btn_send_device_version:
                mBleConnection.sendDeviceVersion(CRPDeviceVersionType.INTERNATIONAL_EDITION);
                break;
            case R.id.btn_query_device_version:
                mBleConnection.queryDeviceVersion(new CRPDeviceVersionCallback() {
                    @Override
                    public void onDeviceVersion(int version) {
                        Log.d(TAG, "onDeviceVersion: " + version);
                    }
                });
                break;
            case R.id.btn_start_measure_blood_pressure:
                mBleConnection.startMeasureBloodPressure();
                break;
            case R.id.btn_stop_measure_blood_pressure:
                mBleConnection.stopMeasureBloodPressure();
                break;



            case R.id.btn_send_today_weather:
                CRPTodayWeatherInfo todayWeatherInfo = new CRPTodayWeatherInfo();
                todayWeatherInfo.setCity("Shenzhen");
                todayWeatherInfo.setFestival(" ");
                todayWeatherInfo.setLunar("六月十四");
                todayWeatherInfo.setPm25(0);
                todayWeatherInfo.setTemp(28);
                todayWeatherInfo.setWeatherId(CRPWeatherId.RAINY);
                mBleConnection.sendTodayWeather(todayWeatherInfo);
                break;
            case R.id.btn_send_future_weather:
                CRPFutureWeatherInfo futureWeatherInfo = new CRPFutureWeatherInfo();
                List<CRPFutureWeatherInfo.FutureBean> list = new ArrayList<>();

                for (int i = 0; i < 7; i++) {
                    CRPFutureWeatherInfo.FutureBean bean = new CRPFutureWeatherInfo.FutureBean();
                    bean.setWeatherId(300);
                    bean.setLowTemperature(20 + i);
                    bean.setHighTemperature(30 + i);
                    list.add(bean);
                }
                futureWeatherInfo.setFuture(list);
                mBleConnection.sendFutureWeather(futureWeatherInfo);
                break;
            case R.id.btn_start_measure_blood_oxygen:
                mBleConnection.startMeasureBloodOxygen();
                break;
            case R.id.btn_stop_measure_blood_oxygen:
                mBleConnection.stopMeasureBloodOxygen();
                break;








            case R.id.btn_start_measure_blood_oxygen2:


               /// autobloodoxyen();

              //  startRecorder();
                btnRecordPressed();

                autobloodoxygen.run();

                break;


            case R.id.btn_stop_measure_blood_oxygen2:
                mHandler.removeCallbacks(autobloodoxygen);
                mBleConnection.startMeasureBloodOxygen();
                btnStopPressed();

             //   stopRecorder();


                break;








            case R.id.btn_send_breathing_light:
                mBleConnection.sendBreathingLight(true);
                break;
            case R.id.btn_query_breathing_light:
                mBleConnection.queryBreathingLight(new CRPDeviceBreathingLightCallback() {
                    @Override
                    public void onBreathingLight(boolean enable) {
                        Log.d(TAG, "onBreathingLight: " + enable);
                    }
                });
                break;
            case R.id.btn_send_watch_face_layout:
                CRPWatchFaceLayoutInfo watchFaceLayoutInfo = new CRPWatchFaceLayoutInfo();
                watchFaceLayoutInfo.setTimePosition(CRPWatchFaceLayoutType.WATCH_FACE_TIME_BOTTOM);
                watchFaceLayoutInfo.setTimeTopContent(CRPWatchFaceLayoutType.WATCH_FACE_CONTENT_SLEEP);
                watchFaceLayoutInfo.setTimeBottomContent(CRPWatchFaceLayoutType.WATCH_FACE_CONTENT_STEP);
                int color = ContextCompat.getColor(this, R.color.color_watch_face_text_blue);
                watchFaceLayoutInfo.setTextColor(color);
                watchFaceLayoutInfo.setBackgroundPictureMd5(CRPWatchFaceLayoutType.DEFAULT_WATCH_FACE_BG_MD5);

                mBleConnection.sendWatchFaceLayout(watchFaceLayoutInfo);
                break;
            case R.id.btn_query_watch_face_layout:
                mBleConnection.queryWatchFaceLayout(new CRPDeviceWatchFaceLayoutCallback() {
                    @Override
                    public void onWatchFaceLayoutChange(CRPWatchFaceLayoutInfo info) {
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTimePosition());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTimeTopContent());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTimeBottomContent());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getTextColor());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getBackgroundPictureMd5());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getHeight());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getWidth());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getThumHeight());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getThumWidth());
                        Log.d(TAG, "onWatchFaceLayoutChange: " + info.getCompressionType());
                    }
                });
                break;
            case R.id.btn_start_measure_ecg:
                mBleConnection.startECGMeasure();
                break;
            case R.id.btn_stop_measure_ecg:
                mBleConnection.stopECGMeasure();

                break;
            case R.id.btn_query_last_ecg:
                mBleConnection.queryLastMeasureECGData();
                break;
            case R.id.btn_query_today_steps_category:
                mBleConnection.queryStepsCategory(CRPStepsCategoryDateType.TODAY_STEPS_CATEGORY);
                break;
            case R.id.btn_query_yesterday_steps_category:
                mBleConnection.queryStepsCategory(CRPStepsCategoryDateType.YESTERDAY_STEPS_CATEGORY);
                break;
            case R.id.btn_query_support_watch_face:
                mBleConnection.querySupportWatchFace(new CRPDeviceSupportWatchFaceCallback() {
                    @Override
                    public void onSupportWatchFace(CRPSupportWatchFaceInfo info) {
                        DeviceActivity.this.supportWatchFaceList = info.getSupportWatchFaceList();
                        for (Integer integer : info.getSupportWatchFaceList()) {
                            Log.d(TAG, "onSupportWatchFace: " + integer.intValue());
                        }
                    }
                });
                break;
           // case R.id.btn_query_watch_face_store:
           //     mBleConnection.queryWatchFaceStore(DeviceActivity.this.supportWatchFaceList,
                  //      new CRPDeviceWatchFaceStoreCallback() {
                   //         @Override
                   //         public void onWatchFaceStoreChange(List<CRPWatchFaceInfo> list) {
                    //            Log.d(TAG, "onWatchFaceStoreChange: " + list.size());
                   //         }

                   //         @Override
                    //        public void onError(String msg) {
//
                     //       }
                   //     });
              //  break;
            case R.id.btn_query_watch_face:
                mBleConnection.queryWatchFaceOfID(6, new CRPDeviceWatchFaceCallback() {
                    @Override
                    public void onWatchFaceChange(CRPWatchFaceInfo info) {
                        Log.d(TAG, "onWatchFaceChange: " + info.getPreview());
                   }

                    @Override
                    public void onError(String msg) {

                    }
                });
                break;
            case R.id.btn_query_sleep_action:
                mBleConnection.querySleepAction(0);
                mBleConnection.querySleepAction(1);
                mBleConnection.querySleepAction(2);
                mBleConnection.querySleepAction(3);
                mBleConnection.querySleepAction(4);
                mBleConnection.querySleepAction(5);
                mBleConnection.querySleepAction(6);
                mBleConnection.querySleepAction(7);
                mBleConnection.querySleepAction(8);
                break;
            case R.id.btn_send_band_ui:
                File file = new File(UI_FILE_PATH);
                Log.d(TAG, "file exist: " + file.exists());
                int startIndex = 0x86000;
                int length = 0xa000;
                int deviceStartIndex = 0x886000;

                CRPTpInfo tpInfo = new CRPTpInfo();
                tpInfo.setFile(file);
                tpInfo.setDeviceStartIndex(deviceStartIndex);
                tpInfo.setFirmwareVersion("MOY-NAA3-2.0.0");
                tpInfo.setLength(length);
                tpInfo.setStartIndex(startIndex);

                new CRPTpHelper().start(this.bandFirmwareVersion, new CRPTransListener() {
                    @Override
                    public void onTransProgressStarting() {
                        Log.d(TAG, "onTransProgressStarting");
                    }

                    @Override
                    public void onTransProgressChanged(int percent) {
                        Log.d(TAG, "onTransProgressChanged：" + percent);
                    }

                    @Override
                    public void onTransCompleted() {
                        Log.d(TAG, "onTransCompleted");
                    }

                    @Override
                    public void onError(int type) {
                        Log.d(TAG, "onError: " + type);
                    }
                });
                break;
            case R.id.btn_query_quick_contacts:
                mBleConnection.checkSupportQuickContact(new CRPQuickContactConfigCallback() {
                    @Override
                    public void onQuickContactConfig(CRPQuickContactConfigInfo info) {
                        mQuickContactConfigInfo = info;
                    }
                });
                break;
            case R.id.btn_send_quick_contacts:
                if (mQuickContactConfigInfo == null || !mQuickContactConfigInfo.isSupported()) {
                    return;
                }

                int avatarHeight = mQuickContactConfigInfo.getHeight();
                int avatarWidth = mQuickContactConfigInfo.getWidth();

                for (int i = 0; i < mQuickContactConfigInfo.getCount(); i++) {
                    CRPContactInfo contactInfo = new CRPContactInfo();
                    contactInfo.setAddress(0);
                    contactInfo.setHeight(avatarHeight);
                    contactInfo.setWidth(avatarWidth);
                    contactInfo.setId(i);
                    contactInfo.setNumber("11113333");
                    contactInfo.setName("AAA" + i);
                    mBleConnection.sendContact(contactInfo);
                }


                BitmapFactory.Options avatarOptions = new BitmapFactory.Options();
                avatarOptions.inScaled = false;

                Bitmap avatarBitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.abbreviated, avatarOptions);
                avatarBitmap = changeBitmapSize(avatarBitmap, avatarWidth, avatarHeight);

                mBleConnection.sendContactAvatar(1, avatarBitmap, 30, new CRPFileTransListener() {
                    @Override
                    public void onTransProgressStarting() {

                    }

                    @Override
                    public void onTransProgressChanged(int percent) {
                        Log.d(TAG, "onTransProgressChanged: " + percent);
                    }

                    @Override
                    public void onTransCompleted() {
                        Log.d(TAG, "onTransCompleted");
                    }

                    @Override
                    public void onError(int type) {
                        Log.d(TAG, "onError: " + type);
                    }
                });
                break;
            case R.id.btn_delete_quick_contacts:
                // Delete a contact
                mBleConnection.deleteContact(0);
                mBleConnection.deleteContactAvatar(0);
                // Clear contact
                mBleConnection.clearContact();
                break;
            case R.id.btn_enable_hrv:
                mBleConnection.enableHrvMeasure(1);
                break;
            case R.id.btn_disable_hrv:
                mBleConnection.disableHrvMeasure();
                break;
            case R.id.btn_query_hrv_measure_interval:
                mBleConnection.queryHrvMeasureInterval();
                break;
            case R.id.btn_query_hrv_measure_count:
                mBleConnection.queryHrvMeasureCount(0);
                break;
            case R.id.btn_query_hrv_measure_result:
                mBleConnection.queryHrv(0, 1);
                break;
            default:
                break;
            case R.id.btnRecord:
                //startRecording();
              //  startRecorder();
                btnRecordPressed();

                break;
            case R.id.btnStop:
             // pauseRecording();
               // stopRecorder();
              //  updateTv();
              //  uploadAudio();
                btnStopPressed();


                break;
            case R.id.btnPlay:
             //   playAudio();
                btnPlayPressed();
                break;
            case R.id.btnStopPlay:
                pausePlaying();

                break;

            case R.id.btn_sendsleep:


                mBleConnection.syncPastSleep(CRPPastTimeType.YESTERDAY_SLEEP);

                mBleConnection.syncPastSleep(CRPPastTimeType.DAY_BEFORE_YESTERDAY_SLEEP);




                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                String SleepRestful = tvrestful.getText().toString();
                String SleepLight = tvlight.getText().toString();

                thedate.setText(sdf.format(new Date()));
                String readtime = thedate.getText().toString();




                addDatatoFirebase2(SleepRestful,SleepLight ,readtime );

                break;
            case R.id.imgshare2:

                if (checkPermission1()) {
                    snapshot();
                }


                break;
            case R.id.imgshare3:

                if (checkPermission1()) {
                    snapshot2();
                }

                break;
            case R.id.buttono2:

                ///onResume();

                wakeLock.acquire();

                 camera = Camera.open();

                camera.setDisplayOrientation(90);

                 startTime = System.currentTimeMillis();


                break;
            case R.id.buttono3:

               /// onPause();
                wakeLock.release();
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
                break;
        }
    }

    public Bitmap changeBitmapSize(Bitmap bitmap, int width, int height) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        float scaleWidth = (float) width / bitmapWidth;
        float scaleHeight = (float) height / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmapWidth, bitmapHeight, matrix, false);
    }
    ///private CRPHrvChangeListener mHrvChangeListener = new CRPHrvChangeListener() {

  //  private CRPHrvChangeListener mHrvChangeListener = new CRPHrvChangeListener(){
      //  @Override
       // public void onMeasureInterval(int interval) {
       //     Log.d(TAG, "onMeasureInterval: " + interval);
       // }

      //  @Override
     //   public void onMeasureCount(int dayIndex, int count) {
      //      Log.d(TAG, "onMeasureCount: " + dayIndex + "-" + count);
      //  }

     //   @Override
      //  public void onHrvChange(CRPHrvInfo info) {
      //      Log.d(TAG, "onHrvChange: " + info.getRriList().toString());
      //  }

      // @Override
      //  public void onMeasureResult(int dayIndex, int index, CRPHrvInfo info) {
       //     Log.d(TAG, "onMeasureResult: " + dayIndex + "-" + index);
       //     Log.d(TAG, "onMeasureResult: " + info.getRriList().toString());
      //  }
  //  };

    private CRPQuickContactConfigInfo mQuickContactConfigInfo;

    private CRPContactListener mContactListener = new CRPContactListener() {
        @Override
        public void onSavedSuccess(int id) {
            Log.d(TAG, "onSavedSuccess: " + id);
        }

        @Override
        public void onSavedFail(int id) {
            Log.d(TAG, "onSavedFail: " + id);
        }
    };


    CRPTempChangeListener mTempChangeListener = new CRPTempChangeListener() {
        @Override
        public void onMeasureTemp(float temp) {
            Log.d(TAG, "onMeasureTemp: " + temp);
        }

        @Override
        public void onMeasureTempState(boolean measuring) {
            Log.d(TAG, "onMeasureTempState: " + measuring);
        }

        @Override
        public void onTimingMeasureTemp(CRPTempInfo info) {
            Log.d(TAG, "onTimingMeasureTemp: " + info.getType());
            Log.d(TAG, "onTimingMeasureTemp: " + Arrays.toString(info.getTempList()));
        }
    };

    CRPMovementStateListener mMovementStateListener = new CRPMovementStateListener() {
        @Override
        public void onMeasureState(int state) {
            Log.d(TAG, "onMeasureState: " + state);
        }
    };


    CRPSleepActionChangeListener mSleepActionChangeListener = new CRPSleepActionChangeListener() {
        @Override
        public void onSleepActionChange(CRPSleepActionInfo info) {
            int hour = info.getHour();
            Log.d(TAG, "onSleepActionChange Hour: " + hour);
            List<Integer> actionList = info.getActionList();
            for (Integer integer : actionList) {
                Log.d(TAG, "onSleepActionChange action: " + integer);
            }
        }
    };


    CRPStepsCategoryChangeListener mStepsCategoryChangeListener = new CRPStepsCategoryChangeListener() {
        @Override
        public void onStepsCategoryChange(CRPStepsCategoryInfo info) {
            List<Integer> stepsList = info.getStepsList();
            Log.d(TAG, "onStepsCategoryChange: " + stepsList);
        }
    };

    CRPStepChangeListener mStepChangeListener = new CRPStepChangeListener() {
        @Override
        public void onStepChange(CRPStepInfo info) {
            Log.d(TAG, "time: " + info.getTime());
            updateStepInfo(info.getSteps(), info.getDistance(), info.getCalories());
        }

        @Override
        public void onPastStepChange(int timeType, CRPStepInfo info) {
            Log.d(TAG, "onPastStepChange: " + info.getSteps());

        }
    };

    CRPSleepChangeListener mSleepChangeListener = new CRPSleepChangeListener() {
        @Override
        public void onSleepChange(CRPSleepInfo info) {
            List<CRPSleepInfo.DetailBean> details = info.getDetails();
            if (details == null) {
                return;
            }
            Log.d(TAG, "sleep time: " + info.getTotalTime());
            updateSleepInfo(info.getRestfulTime(), info.getLightTime());
        }

        @Override
        public void onPastSleepChange(int timeType, CRPSleepInfo info) {
            Log.d(TAG, "onPastSleepChange: " + info.getTotalTime());
        }
    };

    CRPHeartRateChangeListener mHeartRateChangListener = new CRPHeartRateChangeListener() {
        @Override
        public void onMeasuring(int rate) {
            Log.d(TAG, "onMeasuring: " + rate);
            updateTextView(tvHeartRate, String.format(getString(R.string.heart_rate), rate));
        }

        @Override
        public void onOnceMeasureComplete(int rate) {
            Log.d(TAG, "onOnceMeasureComplete: " + rate);
        }

        @Override
        public void onMeasureComplete(CRPHistoryDynamicRateType type, CRPHeartRateInfo info) {
            Log.d(TAG, "onMeasureComplete type: " + type.getValue());
            Log.d(TAG, "onMeasureComplete hr list: " + info.getHeartRateList().toString());
        }

        @Override
        public void on24HourMeasureResult(CRPHeartRateInfo info) {
            List<Integer> list = info.getHeartRateList();
            Log.d(TAG, "on24HourMeasureResult: " + list.size());
            Log.d(TAG, "on24HourMeasureResult: " + list.toString());
        }

        @Override
        public void onMovementMeasureResult(List<CRPMovementHeartRateInfo> list) {
            for (CRPMovementHeartRateInfo info : list) {
                if (info != null) {
                    Log.d(TAG, "onMovementMeasureResult: " + info.getStartTime());
                }
            }
        }

    };

    CRPBloodPressureChangeListener mBloodPressureChangeListener = new CRPBloodPressureChangeListener() {
        @Override
        public void onBloodPressureChange(int sbp, int dbp) {
            Log.d(TAG, "sbp: " + sbp + ",dbp: " + dbp);
            updateTextView(tvBloodPressure,
                    String.format(getString(R.string.blood_pressure), sbp, dbp));
        }
    };

    CRPBloodOxygenChangeListener mBloodOxygenChangeListener = new CRPBloodOxygenChangeListener() {

        @Override
        public void onTimingMeasure(int interval) {
            Log.d(TAG, "onTimingMeasure: " + interval);
        }

        @Override
        public void onBloodOxygenChange(int bloodOxygen) {

            updateTextView(tvBloodOxygen,
                    String.format(getString(R.string.blood_oxygen), bloodOxygen));

            updateTextView(tvBloodOxygen2,
                    String.format(getString(R.string.blood_oxygen), bloodOxygen));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            String spo2 = tvBloodOxygen2.getText().toString();
            thedate.setText(sdf.format(new Date()));
            String readtime = thedate.getText().toString();
            tdb.setText(Double.toString(Math.round(soundDb())) + " dB");
            String rdb = tdb.getText().toString();
            graph2LastXValue += 1d;
            mSeries.appendData(new DataPoint(graph2LastXValue,(Math.round(soundDb()))), true, 100000);
            graph1LastXValue += 1d;
            mSeries1.appendData(new DataPoint(graph1LastXValue,(bloodOxygen)), true, 100000);
            addDatatoFirebase(spo2, readtime, rdb );
        }





        @Override
        public void onTimingMeasureResult(CRPBloodOxygenInfo info) {
            Log.d(TAG, "onTimingMeasureResult: " + info.getList().size());
            Log.d(TAG, "onTimingMeasureResult: " + info.getList().toString());
        }
    };


    private void addDatatoFirebase(String spo2, String time, String db) {

        bloodOxygenData.setSpo2(spo2);
        bloodOxygenData.setTime(time);
        bloodOxygenData.setDB(db);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        thedate.setText(sdf.format(new Date()));
        String readtime = thedate.getText().toString();
        String head;
        head = headTextView.getText().toString();
        databaseReference.child("BloodOxygenDataUse").child(head).child(readtime).setValue(bloodOxygenData);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Toast.makeText(DeviceActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(DeviceActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDatatoFirebase2(String SleepRestful, String SleepLight, String time) {
        // below 3 lines of code is used to set
        // data in our object class.
        sleepData.setSleepRestful(SleepRestful);
        sleepData.setSleepLight(SleepLight);
        sleepData.setTime(time);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        //updateTextView(thedate, getString(R.string.thedate));

        // Calendar calendar = Calendar.getInstance();
        //  SimpleDateFormat format = new SimpleDateFormat(" dd/MMM/yyyy_HH:mm:ss ", Locale.getDefault());
        // String timed =  format.format(calendar.getTime());

        //  TextView textView = findViewById(R.id.datetime);
        // textView.setText(timed);
        thedate.setText(sdf.format(new Date()));
        String readtime = thedate.getText().toString();

        String head;
        head = headTextView.getText().toString();



        databaseReference.child("SleepDataUse").child(head).child(readtime).setValue(sleepData);


        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                ///databaseReference.child("BloodOxygenData").child(readtime).setValue(bloodOxygenData);

                // after adding this data we are showing toast message.
                Toast.makeText(DeviceActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(DeviceActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }











    CRPBleECGChangeListener mECGChangeListener = new CRPBleECGChangeListener() {
        @Override
        public void onECGChange(int[] ecg) {
            for (int i = 0; i < ecg.length; i++) {
                Log.d(TAG, "ecg: " + ecg[i]);
            }
        }

        @Override
        public void onMeasureComplete() {
            Log.d(TAG, "onMeasureComplete");
        }

        @Override
        public void onTransCpmplete(Date date) {
            Log.d(TAG, "onTransCpmplete");
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "onCancel");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    };


    private void queryLastMeasureECGData() {
        this.mBleConnection.queryLastMeasureECGData();
    }


    CRPFindPhoneListener mFindPhoneListener = new CRPFindPhoneListener() {
        @Override
        public void onFindPhone() {
            Log.d(TAG, "onFindPhone");
        }

        @Override
        public void onFindPhoneComplete() {
            Log.d(TAG, "onFindPhoneComplete");
        }
    };

    CRPBleFirmwareUpgradeListener mFirmwareUpgradeListener = new CRPBleFirmwareUpgradeListener() {
        @Override
        public void onFirmwareDownloadStarting() {
            Log.d(TAG, "onFirmwareDownloadStarting");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_download_starting));
        }

        @Override
        public void onFirmwareDownloadComplete() {
            Log.d(TAG, "onFirmwareDownloadComplete");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_download_complete));
        }

        @Override
        public void onUpgradeProgressStarting() {
            Log.d(TAG, "onUpgradeProgressStarting");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_starting));
        }

        @Override
        public void onUpgradeProgressChanged(int percent, float speed) {
            Log.d(TAG, "onUpgradeProgressChanged: " + percent);
            String status = String.format(getString(R.string.dfu_status_uploading_part), percent);
            updateTextView(tvUpgradeState, status);
        }

        @Override
        public void onUpgradeCompleted() {
            Log.d(TAG, "onUpgradeCompleted");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_completed));
            isUpgrade = true;
        }

        @Override
        public void onUpgradeAborted() {
            Log.d(TAG, "onUpgradeAborted");
            updateTextView(tvUpgradeState, getString(R.string.dfu_status_aborted));
        }

        @Override
        public void onError(int errorType, String message) {
            Log.d(TAG, "onError: " + errorType);
            updateTextView(tvUpgradeState, message);
            mBleConnection.abortFirmwareUpgrade();
        }
    };


    void updateStepInfo(int step, int distance, int calories) {
        updateTextView(tvStep, String.format(getString(R.string.step), step));
        updateTextView(tvDistance, String.format(getString(R.string.distance), distance));
        updateTextView(tvCalorie, String.format(getString(R.string.calorie), calories));
    }

    void updateSleepInfo(int restful, int light) {
        updateTextView(tvRestful, String.format(getString(R.string.restful), restful));
        updateTextView(tvLight, String.format(getString(R.string.light), light));







    }

    void updateConnectState(final int state) {
        if (state < 0) {
            return;
        }
        updateTextView(tvConnectState, getString(state));
    }

    void updateTextView(final TextView view, final String con) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText(con);
            }
        });
    }

    private Runnable autobloodoxygen = new Runnable() {
        @Override
        public void run() {
            mBleConnection.startMeasureBloodOxygen();
            Toast.makeText(DeviceActivity.this,"Start measure BloodOxygen", Toast.LENGTH_SHORT).show();



            mHandler.postDelayed(this, 1000);

        }
    };

    private void startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record nd store the audio.
        requestPermissions();
        RequestPermissions();

        if (CheckPermissions()) {

            // setbackgroundcolor method will change
            // the background color of text view.


            // we are here initializing our filename variable
            // with the path of the recorded audio file.
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

            mFileName += "/AudioRecording.3gp";





            // below method is used to initialize
            // the media recorder class
            mRecorder = new MediaRecorder();

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // below method is used to set
            // the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            mRecorder.start();
            statusTV.setText("Recording Started");
        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;

        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(DeviceActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE );
    }


    public void playAudio() {


        // for playing our recorded audio
        // we are using media player class.
        mPlayer = new MediaPlayer();
        try {
            // below method is used to set the
            // data source which will be our file name
            mPlayer.setDataSource(mFileName);

            // below method will prepare our media player
            mPlayer.prepare();

            // below method will start our media player.
            mPlayer.start();
            statusTV.setText("Recording Started Playing");
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void pauseRecording() {


        // below method will stop
        // the audio recording.
        mRecorder.stop();

        // below method will release
        // the media recorder class.
        mRecorder.release();
        mRecorder = null;
        statusTV.setText("Recording Stopped");


        uploadAudio();



    }

    public void pausePlaying() {
        // this method will release the media player
        // class and pause the playing of our recorded audio.
        mPlayer.release();
        mPlayer = null;

        statusTV.setText("Recording Play Stopped");
    }

    public void uploadAudio() {


        mProgressDialog.setMessage("Uploading Audio...");
        mProgressDialog.show();

        StorageReference filepath = mStorage.child("Audio").child("new_audio.3gp");

        Uri uri = Uri.fromFile(new File(mFileName));
       // Uri uri = Uri.fromFile(new File(tempWavFile));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mProgressDialog.dismiss();

                statusTV.setText("Uploading Finish");


            }
        });


    }
    public void startRecorder(){

       /// requestPermissions();
        RequestPermissions();


        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
        tdb.setText(Double.toString(Math.round(soundDb())) + " dB");
        //  ref.child("value").setValue(Math.round(soundDb()));
    }
    public double soundDb(){
        return  20 * Math.log10(getAmplitudeEMA() / 1);
    }
    public double getAmplitude() {
        if (mediaRecorder != null)
            return  (mediaRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public void requestPermissions() {
        if (!PermissionUtils.hasSelfPermissions(this, PERMISSION_UPDATEBANDCONFIG)) {
            ActivityCompat.requestPermissions(
                    this, PERMISSION_UPDATEBANDCONFIG, REQUEST_UPDATEBANDCONFIG);
        }
    }

    public void btnRecordPressed(){
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(this, "Recording is started",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void btnStopPressed(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        Toast.makeText(this, "Recording is stopped",Toast.LENGTH_SHORT).show();
    }

    public void btnPlayPressed(){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getRecordingFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            Toast.makeText(this, "Recording is playing",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean isMicrophonePresent(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }else{
            return false;
        }
    }

    private void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MICROPHONE_PERMISSION_CODE);
        }
    }
    private String getRecordingFilePath(){

        String head;
        head = headTextView.getText().toString();

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        //File file = new File(musicDirectory, head + ".mp3");
        File file = new File(musicDirectory, head + ".wav");
        return file.getPath();
    }



    public void initGraph(GraphView graph) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);

        graph.getGridLabelRenderer().setLabelVerticalWidth(100);

        mSeries1 = new LineGraphSeries<>();
        mSeries1.setDrawDataPoints(true);
        mSeries1.setDrawBackground(true);

        graph.addSeries(mSeries1);

    }

    public void initGraph2(GraphView graph2) {
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(5);

        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMinY(0);
        graph2.getViewport().setMaxY(100);

        graph2.getGridLabelRenderer().setLabelVerticalWidth(100);

        // first mSeries is a line
        mSeries = new LineGraphSeries<>();
        mSeries.setDrawDataPoints(true);
        mSeries.setDrawBackground(true);
        graph2.addSeries(mSeries);
    }

    public void snapshot() {
        try {
            graph.takeSnapshotAndShare(this, "exampleGraph", "GraphViewSpo2");
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Permission problem. See log " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void snapshot2() {
        try {
            graph2.takeSnapshotAndShare(this, "exampleGraph", "GraphViewdB");
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Permission problem. See log " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission1() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);

            return false;
        } else {
            return true;
        }
    }
 //   @Override
 //   public void onResume() {
      //  super.onResume();

    //    wakeLock.acquire();

   //     camera = Camera.open();

   //     camera.setDisplayOrientation(90);

   //     startTime = System.currentTimeMillis();
  //  }

    //call back the frames then release the camera + wakelock and Initialize the camera to null
    //Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed. The counterpart to onResume().
    //When activity B is launched in front of activity A,
    // this callback will be invoked on A. B will not be created until A's onPause() returns, so be sure to not do anything lengthy here.
  //  @Override
  //  public void onPause() {
     //   super.onPause();
     //   wakeLock.release();
     //   camera.setPreviewCallback(null);
     //   camera.stopPreview();
     //   camera.release();
     //   camera = null;
  //  }

    //getting frames data from the camera and start the heartbeat process
   // private final Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

    //    /**
      //   * {@inheritDoc}
      //   */
      //  @Override
     //   public void onPreviewFrame(byte[] data, Camera cam) {
            //if data or size == null ****
        //    if (data == null) throw new NullPointerException();
           // Camera.Size size = cam.getParameters().getPreviewSize();
       //    if (size == null) throw new NullPointerException();

            //Atomically sets the value to the given updated value if the current value == the expected value.
       //     if (!processing.compareAndSet(false, true)) return;

            //put width + height of the camera inside the variables
       //     int width = size.width;
       //     int height = size.height;
       //     double RedAvg;
        //    double BlueAvg;

       //     RedAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 1); //1 stands for red intensity, 2 for blue, 3 for green
        //    sumred = sumred + RedAvg;
       //     BlueAvg = ImageProcessing.decodeYUV420SPtoRedBlueGreenAvg(data.clone(), height, width, 2); //1 stands for red intensity, 2 for blue, 3 for green
        //    sumblue = sumblue + BlueAvg;

        //    RedAvgList.add(RedAvg);
         //   BlueAvgList.add(BlueAvg);

       //     ++counter; //countes number of frames in 30 seconds

            //To check if we got a good red intensity to process if not return to the condition and set it again until we get a good red intensity
         //   if (RedAvg < 200) {
         //       inc = 0;
         //       ProgP = inc;
         //       ProgO2.setProgress(ProgP);
         //       processing.set(false);
        //    }

         //   long endTime = System.currentTimeMillis();
         //   double totalTimeInSecs = (endTime - startTime) / 1000d; //to convert time to seconds
         //   if (totalTimeInSecs >= 30) { //when 30 seconds of measuring passes do the following " we chose 30 seconds to take half sample since 60 seconds is normally a full sample of the heart beat

          //      startTime = System.currentTimeMillis();
           //     SamplingFreq = (counter / totalTimeInSecs);
           //     Double[] Red = RedAvgList.toArray(new Double[RedAvgList.size()]);
          //      Double[] Blue = BlueAvgList.toArray(new Double[BlueAvgList.size()]);
           //     double HRFreq = Fft.FFT(Red, counter, SamplingFreq);
           //     double bpm = (int) ceil(HRFreq * 60);

          //      double meanr = sumred / counter;
          //      double meanb = sumblue / counter;

          //      for (int i = 0; i < counter - 1; i++) {

          //          Double bufferb = Blue[i];

          //          Stdb = Stdb + ((bufferb - meanb) * (bufferb - meanb));

           //         Double bufferr = Red[i];

           //         Stdr = Stdr + ((bufferr - meanr) * (bufferr - meanr));

          //      }

           //     double varr = sqrt(Stdr / (counter - 1));
          //      double varb = sqrt(Stdb / (counter - 1));

          //      double R = (varr / meanr) / (varb / meanb);

           //     double spo2 = 100 - 5 * (R);
          //      o2 = (int) (spo2);

          //      if ((o2 < 80 || o2 > 99) || (bpm < 45 || bpm > 200)) {
           //         inc = 0;
           //         ProgP = inc;
           //         ProgO2.setProgress(ProgP);
            //        mainToast = Toast.makeText(getApplicationContext(), "Measurement Failed", Toast.LENGTH_SHORT);
           //         mainToast.show();
           //         startTime = System.currentTimeMillis();
            //        counter = 0;
            //        processing.set(false);
             //       return;
           //     }

          //  }

       //     if (o2 != 0) {
        //        RO2.setText(String.valueOf(o2));

        //    }

        //    if (RedAvg != 0) {
          //      ProgP = inc++ / 34;
          //      ProgO2.setProgress(ProgP);
       //     }

      //      processing.set(false);

      //  }
  //  };

   // private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {


    //    @SuppressLint("LongLogTag")
    //    @Override
      //  public void surfaceCreated(SurfaceHolder holder) {
       //     try {
         //       camera.setPreviewDisplay(previewHolder);
         //       camera.setPreviewCallback(previewCallback);
       //     } catch (Throwable t) {
        //        Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
       //     }
     //   }


     //   @Override
    //    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

       //     Camera.Parameters parameters = camera.getParameters();
       //     parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);


        //    Camera.Size size = getSmallestPreviewSize(width, height, parameters);
        //    if (size != null) {
        //        parameters.setPreviewSize(size.width, size.height);
        //        Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
         //   }

       //     camera.setParameters(parameters);
       //     camera.startPreview();
    //    }


     //   @Override
       // public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
      //  }
  //  };

  //  private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
     //   Camera.Size result = null;

      //  for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
       //     if (size.width <= width && size.height <= height) {
         //       if (result == null) {
         //           result = size;
         //       } else {
         //           int resultArea = result.width * result.height;
         ///           int newArea = size.width * size.height;

         //           if (newArea < resultArea) result = size;
         //       }
       //     }
     //   }

     //   return result;
  //  }


}






