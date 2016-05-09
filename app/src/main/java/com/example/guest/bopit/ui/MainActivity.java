package com.example.guest.bopit.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.guest.bopit.R;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View swipeImage = findViewById(R.id.balloon);
        View swipeParent = findViewById(R.id.gear);

        swipeParent.setOnTouchListener(new SwipeImageTouchListener(swipeImage));

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, mSensorManager.SENSOR_DELAY_NORMAL);
    }


    public static class SwipeImageTouchListener implements View.OnTouchListener{
        private final View swipeView;

        public SwipeImageTouchListener(View swipeView) {
            this.swipeView = swipeView;
        }

        private boolean tracking = false;
        private float startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Rect hitRect = new Rect();
                    swipeView.getHitRect(hitRect);
                    if (hitRect.contains((int) event.getX(), (int) event.getY())) {
                        tracking = true;
                    }
                    startY = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    tracking = false;
                    animateSwipeView(v.getHeight());
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (tracking) {
                        swipeView.setTranslationY(event.getY() - startY);
                    }
                    return true;
            }
            return false;
        }
        private void animateSwipeView(int parentHeight) {
            int quarterHeight = parentHeight / 1;
            float currentPosition = swipeView.getTranslationY();
            float animateTo = 0.7f;
            if (currentPosition < -quarterHeight) {
                animateTo = -parentHeight;
            } else if (currentPosition > quarterHeight) {
                animateTo = parentHeight;
            }
            ObjectAnimator.ofFloat(swipeView, "translationY", currentPosition, animateTo)
                    .setDuration(400)
                    .start();
        }

    }

        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;

            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 100) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        Log.d("SensorEventListener", "shaking");
                        MediaPlayer player = MediaPlayer.create(this, R.raw.alien);
                        Toast.makeText(getApplicationContext(), "Whopa!", Toast.LENGTH_SHORT).show();
                        player.start();
                        last_x = x;
                        last_y = y;
                        last_z = z;

                    }
                }
            }
        }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }

