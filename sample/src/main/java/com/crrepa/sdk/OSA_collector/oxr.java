package com.crrepa.sdk.OSA_collector;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.crrepa.sdk.OSA_collector.scan.ScanActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class oxr extends AppCompatActivity {


    private String user, Date;
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    Date today = Calendar.getInstance().getTime();
    int O2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oxr);

        Date = df.format(today);
        TextView RO2 = this.findViewById(R.id.O2R);
        ImageButton SO2 = this.findViewById(R.id.SendO2);
        Button bb = this.findViewById(R.id.buttonb);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            O2 = bundle.getInt("O2R");
        //    user = bundle.getString("Usr");
            RO2.setText(String.valueOf(O2));
        }

        bb.setOnClickListener(v -> {
          //  Intent i = new Intent(Intent.ACTION_SEND);
          //  i.setType("message/rfc822");
          //  i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
           // i.putExtra(Intent.EXTRA_SUBJECT, "Health Watcher");
          //  i.putExtra(Intent.EXTRA_TEXT, user + "'s Oxygen Saturation Level " + "\n" + " at " + Date + " is :   " + O2);
          //  try {
          //      startActivity(Intent.createChooser(i, "Send mail..."));
          //  } catch (android.content.ActivityNotFoundException ex) {
        //        Toast.makeText(oxr.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
         //   }
            Intent intent
                    = new Intent(oxr.this,
                    ScanActivity.class);
            startActivity(intent);


        });


    }
}