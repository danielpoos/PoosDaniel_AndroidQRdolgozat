package com.example.qrdolgozat;

import android.os.Environment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CodeWrite {
    public static void write(String text, double longitude, double latitude) throws IOException {
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        String formattedTime = format.format(time);
        String row = String.format("%s,%s,%f,%f", text, formattedTime, longitude, latitude);
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
            File file = new File(Environment.getExternalStorageDirectory(), "scannedCodes.csv");
            BufferedWriter wr = new BufferedWriter(new FileWriter(file, true));
            wr.append(row);
            wr.append(System.lineSeparator());
            wr.close();
        }
    }
}
