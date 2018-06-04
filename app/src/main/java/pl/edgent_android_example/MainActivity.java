package pl.edgent_android_example;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.apache.edgent.android.hardware.SensorStreams;
import org.apache.edgent.android.topology.ActivityStreams;
import org.apache.edgent.function.Consumer;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final TextView textView = findViewById(R.id.example_textview);

        // org.apache.edgent.providers
        DirectProvider provider = new DirectProvider();
        Topology topology = provider.newTopology("My topology");

        Consumer<SensorEvent> consumer = new Consumer<SensorEvent>() {
            @Override
            public void accept(SensorEvent event) {
                textView.setText(String.valueOf(event.values[0]));
            }
        };

        // org.apache.edgent.hardware
        TStream stream = SensorStreams.sensors(topology, sensorManager, Sensor.TYPE_ACCELEROMETER);

        // org.apache.edgent.topology
        ActivityStreams.sinkOnUIThread(this, stream, consumer);

        provider.submit(topology);

    }
}
