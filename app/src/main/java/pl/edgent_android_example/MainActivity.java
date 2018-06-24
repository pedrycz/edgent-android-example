package pl.edgent_android_example;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.apache.edgent.function.Consumer;

import java.util.concurrent.TimeUnit;

import pl.edu.agh.edgentandroidwrapper.Topology.MappingTopology;
import pl.edu.agh.edgentandroidwrapper.collector.SensorDataCollector;
import pl.edu.agh.edgentandroidwrapper.samplingrate.SamplingRate;
import pl.edu.agh.edgentandroidwrapper.task.EdgentTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.example_textview);
        final Consumer<SensorEvent> uiThreadConsumer = (Consumer<SensorEvent>) event -> {
            textView.setText(String.valueOf(event.values[0]));
        };

        EdgentTask task = EdgentTask.builder()
                .sensorManager((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                .sensorDataCollector(SensorDataCollector.builder()
                        .sensor(Sensor.TYPE_ACCELEROMETER)
                        .samplingRate(SamplingRate.builder()
                                .timeUnit(TimeUnit.MICROSECONDS)
                                .value(1000000)
                                .build())
                        .simpleTopology(MappingTopology.builder()
                                .name("Mapping topology")
                                .tag("first value")
                                .mapper(event -> (double) event.values[0])
                                .build()
                        )
                        .activity(MainActivity.this)
                        .consumer(() -> uiThreadConsumer)
                        .build())
                .build();

        task.start();

    }

}
