package com.schattenbrot.sensor;

/**
 * App starting point
 *
 */
public class App {
  public static void main(String[] args) {
    Sensor sensor = new Sensor();
    System.out.println(sensor.toString());
    sensor.run();
  }
}
