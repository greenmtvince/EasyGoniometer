package com.quantrian.easygoniometer.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.quantrian.easygoniometer.R;
import com.quantrian.easygoniometer.data.ReadingContract;
import com.quantrian.easygoniometer.data.ReadingDbHelper;
import com.quantrian.easygoniometer.ui.DashboardActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GoniometerActivity extends AppCompatActivity implements SensorEventListener {
    //Fields to run the app
    private TextView texty;
    private Float startMeasure;
    private Float stopMeasure;
    private Float currentAngle;
    private int flexion;
    private int extension;
    private int step;
    private long dateSec;
    private ImageView illustration;

    //fields for Taking a measurement
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goniometer);
        overridePendingTransition(R.transition.slide_in,R.transition.slide_out);

        texty = findViewById(R.id.textView);
        illustration = findViewById(R.id.iv_goniometerInstructions);

        //Hide all the stuff at the top.  People should be able to mash anywhere on the view to
        //advance to the next measurement.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();

        //
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Initialize the steps
        step =-1;

        //Make the whole View clickable.
        ConstraintLayout goniometerLayout = findViewById(R.id.goniometerLayout);
        goniometerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBtn(view);
            }
        });

    }

    // Get readings from accelerometer and magnetometer.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Not used.  Required method.
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,  mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this);
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);
        // "mRotationMatrix" now has up-to-date information.
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        // "mOrientationAngles" now has up-to-date information.

        for(int i = 0; i < 3; i++) {
            mOrientationAngles[i] = (float)(Math.toDegrees(mOrientationAngles[i]));
        }
        currentAngle= mOrientationAngles[1];
        Log.d("READING", "Clicked: "+currentAngle+" degrees." + mOrientationAngles[1] +" raw ");
    }

    public void onClickBtn(View v) {
        updateOrientationAngles();
        if(step ==-1){
            texty.setText(R.string.goniometer_first);
            illustration.setImageResource(R.drawable.patientlayingthigh);
            step=0;
        }
        else if (step == 0) {
            startMeasure = currentAngle;
            texty.setText(R.string.goniometer_second);
            step = 1;
            illustration.setImageResource(R.drawable.patientlayingshin);
        } else if (step == 1) {
            stopMeasure = currentAngle;
            step=2;
            Float bob = Math.abs(stopMeasure-startMeasure);
            texty.setText(R.string.goniometer_third);
            Log.d("CALC", "onClickBtn: "+ stopMeasure+" - " +startMeasure+" = "+ bob);
            extension = Math.round(bob);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            illustration.setImageResource(R.drawable.legbentthigh);
        } else if (step==2){
            texty.setText(R.string.goniometer_fourth);
            startMeasure = currentAngle;
            illustration.setImageResource(R.drawable.legbentshin);
            step=3;
        } else if (step==3){
            //Replace with code to launch dashboard activity
            step=-1;
            stopMeasure = currentAngle;
            Float bob = Math.abs(stopMeasure-startMeasure);
            flexion = Math.round(bob);
            //texty.setText("Flexion: "+flexion+ " Extension: "+ extension);
            Log.d("CALC", "onClickBtn: "+ stopMeasure+" - " +startMeasure+" = "+ bob);
            addNewReading();
            returnToDashboard();
        }


    }

    private long addNewReading(){
        Date date = Calendar.getInstance().getTime();
        //SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //dateString = iso8601Format.format(date);

        dateSec=date.getTime()/1000;

        ContentValues cv = new ContentValues();

        cv.put(ReadingContract.ReadingEntry.COLUMN_FLEXION,flexion);
        cv.put(ReadingContract.ReadingEntry.COLUMN_EXTENSION,extension);
        cv.put(ReadingContract.ReadingEntry.COLUMN_DATE,dateSec);

        ReadingDbHelper dbHelper = new ReadingDbHelper(this);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        return database.insert(ReadingContract.ReadingEntry.TABLE_NAME, null,cv);
    }

    private void returnToDashboard(){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("EXTENSION",extension);
        intent.putExtra("FLEXION",flexion);
        intent.putExtra("DATE",dateSec);
        //I don't want two (or more) dashboard activities in the stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //User should not be able to navigate back to adding a measurement
        finish();
    }

}
