package com.schattenbrot.sensor;

/**
 * App starting point
 *
 */
public class App {
  public static void main(String[] args) throws Exception {
    String sensorType = System.getenv("SENSOR_TYPE");
    int sensorValue = Integer.parseInt(System.getenv("SENSOR_VALUE"));

    Sensor sensor = new Sensor(sensorType, sensorValue);
    System.out.println(sensor);
    sensor.run();
  }
}
