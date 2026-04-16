package dev.tetra2000.handdryer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private SensorManager mManager;
    private Sensor mProximitySensor;
    private float mProximityRange;

    private SoundPool mDryerSound;
    private int mDryerSoundId;
    private int mStreamId;

    private ImageView mDryerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mDryerImage = findViewById(R.id.imageView);

        mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if(mProximitySensor == null) {
            Toast.makeText(this, getString(R.string.not_supported_device), Toast.LENGTH_LONG).show();
            finish();
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mProximityRange = mProximitySensor.getMaximumRange();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mDryerSound = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDryerSoundId = mDryerSound.load(this, R.raw.dryer, 1);

        mManager.registerListener(mProximityListener, mProximitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mManager.unregisterListener(mProximityListener, mProximitySensor);

        mDryerSound.unload(R.raw.dryer);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDryerSound.release();

        super.onDestroy();
    }


    private void onDryingStateChanged(boolean isDrying) {
        if(isDrying) {
            // play sound
            mStreamId = mDryerSound.play(mDryerSoundId, 1.0f, 1.0f, 0, -1, 1.0f);
            // change image
            mDryerImage.setImageResource(R.drawable.drying);
        }
        else {
            // stop sound if playing
            if (mStreamId!=0)
                mDryerSound.stop(mStreamId);

            // change image
            mDryerImage.setImageResource(R.drawable.normal);
        }
    }

    private SensorEventListener mProximityListener = new SensorEventListener() {
        private boolean isDrying = false;

        @Override
        public void onSensorChanged(SensorEvent event) {
            boolean near = event.values[0] < mProximityRange;
            if(near != isDrying) {
                // state changed
                isDrying = near;
                onDryingStateChanged(isDrying);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
