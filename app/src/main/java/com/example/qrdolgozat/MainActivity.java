package com.example.qrdolgozat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private Button scanBtn, kiirBtn;
    private TextView out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = findViewById(R.id.scan);
        kiirBtn = findViewById(R.id.kiir);
        out = findViewById(R.id.ki);
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

        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult iresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(iresult != null) {
            if (iresult.getContents() == null) Toast.makeText(this, "You exited the scanner", Toast.LENGTH_SHORT).show();
            else{
                out.setText(iresult.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}