package pl.edgent_android_example;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.apache.edgent.android.hardware.SensorStreams;
import org.apache.edgent.android.topology.ActivityStreams;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.function.Consumer;
import org.apache.edgent.function.Function;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create Edgent topology
        final DirectProvider provider = new DirectProvider();
        final Topology topology = provider.newTopology("My topology");

        // create Edgent stream using Android sensors
        final SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final TStream<SensorEvent> stream = SensorStreams.sensors(topology, sensorManager, Sensor.TYPE_ACCELEROMETER);

        // register Android ui thread consumer
        final TextView textView = findViewById(R.id.example_textview);
        final Consumer<SensorEvent> uiThreadConsumer = new Consumer<SensorEvent>() {
            @Override
            public void accept(SensorEvent event) {
                textView.setText(String.valueOf(event.values[0]));
            }
        };
        ActivityStreams.sinkOnUIThread(this, stream, uiThreadConsumer);

        // register MQTT consumer
        final Function<SensorEvent, byte[]> mqttSerializer = new Function<SensorEvent, byte[]>() {
            @Override
            public byte[] apply(SensorEvent event) {
                return ByteBuffer.allocate(4).putFloat(event.values[0]).array();
            }
        };
        MqttStreams mqttStreams = new MqttStreams(topology, "tcp://broker.hivemq.com:1883", "52033_client");
        mqttStreams.publish(stream, of("52033_topic"), mqttSerializer, of(0), of(false));

        // run Edgent topology with both consumers
        provider.submit(topology);

    }

    private <T> Function<SensorEvent, T> of(final T value) {
        return new Function<SensorEvent, T>() {
            @Override
            public T apply(SensorEvent sensorEvent) {
                return value;
            }
        };
    }

}
