package com.example.qrdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button scanBtn, kiirBtn, koorBtn;
    private TextView out;
    private ImageView qrIV;
    private double latitude;
    private double longitude;
    private Timer timer;
    private LocationListener ll;
    private LocationManager lm;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        scanBtn.setOnClickListener(view -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            intentIntegrator.setPrompt("QR Code reader by PD");
            intentIntegrator.setCameraId(0);
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.setBarcodeImageEnabled(true);
            intentIntegrator.initiateScan();
        });
        kiirBtn.setOnClickListener(view -> {
            timer = new Timer();
            Toast.makeText(this, "yeee", Toast.LENGTH_SHORT).show();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    try {
                        CodeWrite.write(out.getText().toString(),longitude,latitude);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
            Toast.makeText(this, "naah", Toast.LENGTH_SHORT).show();
            timer.schedule(tt,0);
            timer.cancel();
        });
        koorBtn.setOnClickListener(view -> {
            String text = String.format("%f,%f",longitude, latitude);
            if (text.isEmpty()) Toast.makeText(this, "Haven't found the location", Toast.LENGTH_SHORT).show();
            else {
                MultiFormatWriter mfw = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = mfw.encode(text, BarcodeFormat.QR_CODE, 500,500);
                    BarcodeEncoder bce = new BarcodeEncoder();
                    Bitmap bitmap = bce.createBitmap(bitMatrix);
                    qrIV.setImageBitmap(bitmap);
                }catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void initialize(){
        scanBtn = findViewById(R.id.scan);
        kiirBtn = findViewById(R.id.kiir);
        koorBtn = findViewById(R.id.koor);
        qrIV = findViewById(R.id.qr);
        out = findViewById(R.id.ki);
        lm = (LocationManager) getSystemService(/*Context.*/LOCATION_SERVICE);
        ll = location -> {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions,0);
            }
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions,1);
            }
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult iresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(iresult != null) {
            if (iresult.getContents() == null) Toast.makeText(this, "You exited the scanner", Toast.LENGTH_SHORT).show();
            else{
                out.setText(iresult.getContents());
                try {
                    Uri url = Uri.parse(iresult.getContents());
                    Intent intent = new Intent(Intent.ACTION_VIEW,url);
                    startActivity(intent);
                }catch (Exception e){
                    Log.d(e.toString(),"?");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: gpsControl(); break;
            case 1: fileWriteControl(); break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void fileWriteControl() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No read/write external storage permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void gpsControl() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No GPS permission", Toast.LENGTH_SHORT).show();
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
    }

}