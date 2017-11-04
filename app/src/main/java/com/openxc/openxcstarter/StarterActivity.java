package com.openxc.openxcstarter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.openxcplatform.openxcstarter.R;
import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.AcceleratorPedalPosition;
import com.openxc.measurements.BaseMeasurement;
import com.openxc.measurements.BrakePedalStatus;
import com.openxc.measurements.FuelConsumed;
import com.openxc.measurements.FuelLevel;
import com.openxc.measurements.HeadlampStatus;
import com.openxc.measurements.HighBeamStatus;
import com.openxc.measurements.IgnitionStatus;
import com.openxc.measurements.Latitude;
import com.openxc.measurements.Longitude;
import com.openxc.measurements.Odometer;
import com.openxc.measurements.ParkingBrakeStatus;
import com.openxc.measurements.SteeringWheelAngle;
import com.openxc.measurements.TorqueAtTransmission;
import com.openxc.measurements.TransmissionGearPosition;
import com.openxc.measurements.TurnSignalStatus;
import com.openxc.measurements.VehicleButtonEvent;
import com.openxc.measurements.VehicleDoorStatus;
import com.openxc.measurements.VehicleSpeed;
import com.openxc.measurements.WindshieldWiperStatus;


public class StarterActivity extends Activity {
    private static final String TAG = "StarterActivity";

    private VehicleManager mVehicleManager;
    private TextView mEngineSpeedView;
    private TextView acceleratorPedalPositionView;
    private TextView baseMeasurementView;
    private TextView fuelConsumedView;
    private TextView headLampView;
    private TextView highBeamView;
    private TextView ignitionView;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView odometerView;
    private TextView parkingBrakeView;
    private TextView steeringWheelView;
    private TextView torqueAtView;
    private TextView transmissionGearView;
    private TextView turnSignalView;
    private TextView vehicleButtonView;
    private TextView vehicleDoorView;
    private TextView vehicleSpeedView;
    private TextView windshieldWiperView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        // grab a reference to the engine speed text object in the UI, so we can
        // manipulate its value later from Java code
        mEngineSpeedView = (TextView) findViewById(R.id.engine_speed);
        acceleratorPedalPositionView =  (TextView) findViewById(R.id.accelerator_pedal_position);
        highBeamView = (TextView) findViewById(R.id.high_beam_status);
        // baseMeasurementView = (TextView) findViewById(R.id.base_measurement);
    }

    @Override
    public void onPause() {
        super.onPause();
        // When the activity goes into the background or exits, we want to make
        // sure to unbind from the service to avoid leaking memory
        if(mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            // Remember to remove your listeners, in typical Android
            // fashion.
            mVehicleManager.removeListener(EngineSpeed.class,
                    mSpeedListener);
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // When the activity starts up or returns from the background,
        // re-connect to the VehicleManager so we can receive updates.
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /* This is an OpenXC measurement listener object - the type is recognized
     * by the VehicleManager as something that can receive measurement updates.
     * Later in the file, we'll ask the VehicleManager to call the receive()
     * function here whenever a new EngineSpeed value arrives.
     */
    EngineSpeed.Listener mSpeedListener = new EngineSpeed.Listener() {
        @Override
        public void receive(Measurement measurement) {
            // When we receive a new EngineSpeed value from the car, we want to
            // update the UI to display the new value. First we cast the generic
            // Measurement back to the type we know it to be, an EngineSpeed.
            final EngineSpeed speed = (EngineSpeed) measurement;
            // In order to modify the UI, we have to make sure the code is
            // running on the "UI thread" - Google around for this, it's an
            // important concept in Android.
            StarterActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    // Finally, we've got a new value and we're running on the
                    // UI thread - we set the text of the EngineSpeed view to
                    // the latest value
                    mEngineSpeedView.setText("Engine speed (RPM): "
                            + speed.getValue().doubleValue());
                }
            });
        }
    };

    AcceleratorPedalPosition.Listener acceleratorPedalListener = new AcceleratorPedalPosition.Listener() {
        @Override
        public void receive(Measurement measurement) {
            final AcceleratorPedalPosition acceleratorPedalPosition = (AcceleratorPedalPosition) measurement;
            StarterActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    acceleratorPedalPositionView.setText("Accelerator Pedal Position: "
                            + acceleratorPedalPosition.getValue().toString());
                }
            });
        }
    };

//    BaseMeasurement.Listener baseMeasurementListener = new BaseMeasurement.Listener() {
//        @Override
//        public void receive(Measurement measurement) {
//            final BaseMeasurement baseMeasurement = (BaseMeasurement) measurement;
//            StarterActivity.this.runOnUiThread(new Runnable() {
//                public void run() {
//                    baseMeasurementView.setText("Base Measurement: "
//                            + baseMeasurement.getValue().toString());
//                }
//            });
//        }
//    };

    HighBeamStatus.Listener highBeamStatusListener = new HighBeamStatus.Listener() {
        @Override
        public void receive(Measurement measurement) {
            final HighBeamStatus highBeamStatusListener = (HighBeamStatus) measurement;
            StarterActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    highBeamView.setText("High Beam Status: "
                            + highBeamStatusListener.getValue().toString());
                }
            });
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is
        // established, i.e. bound.
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();

            // We want to receive updates whenever the EngineSpeed changes. We
            // have an EngineSpeed.Listener (see above, mSpeedListener) and here
            // we request that the VehicleManager call its receive() method
            // whenever the EngineSpeed changes
            mVehicleManager.addListener(EngineSpeed.class, mSpeedListener);
            mVehicleManager.addListener(AcceleratorPedalPosition.class, acceleratorPedalListener);
            //mVehicleManager.addListener(BaseMeasurement.class, baseMeasurementListener);

        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starter, menu);
        return true;
    }
}