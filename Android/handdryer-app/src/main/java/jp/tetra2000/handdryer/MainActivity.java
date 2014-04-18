package jp.tetra2000.handdryer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private SensorManager mManager;
    private Sensor mProximitySensor;
    private float mProximityRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if(mProximitySensor == null) {
            Toast.makeText(this, getString(R.string.not_supported_device), Toast.LENGTH_LONG).show();
            finish();
        }

        mProximityRange = mProximitySensor.getMaximumRange();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mManager.registerListener(mProximityListenr, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mManager.unregisterListener(mProximityListenr, mProximitySensor);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDryingStateChanged(boolean isDrying) {
        Toast.makeText(this, isDrying+"", Toast.LENGTH_LONG).show();
    }

    private SensorEventListener mProximityListenr = new SensorEventListener() {
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
