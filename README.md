# Edgent Android example

Simple Android application using `edgent-android-wrapper` library (please check `mqtt-support` branch).

In case of problems with dependencies, exclude them from library and include once more:

```
implementation ('com.github.ppedrycz:edgent-android-wrapper:master-SNAPSHOT') {
    exclude group: 'org.apache.edgent'
    exclude group: 'com.google'
}

implementation 'org.apache.edgent.android:edgent-providers-direct:1.2.0'
implementation 'org.apache.edgent.android:edgent-android-hardware:1.2.0'
implementation ('org.apache.edgent.android:edgent-android-topology:1.2.0')
implementation ('org.apache.edgent.android:edgent-connectors-mqtt:1.2.0')
```
