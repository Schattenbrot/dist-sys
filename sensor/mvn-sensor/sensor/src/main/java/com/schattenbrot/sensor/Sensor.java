package com.schattenbrot.sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

/**
 * Sensor implementation
 */
public class Sensor {
  private enum SensorType {
    STORAGE, // 0
    SHOP // 1
  }

  private SensorType type;
  private int value;

  /**
   * Sensor() is the standard constructor for the sensor and initializes the
   * Sensor with default variables if the environment variables weren't found or
   * incorrect.
   */
  public Sensor() {
    try {
      String sensortype = System.getenv("SENSOR_TYPE");
      if (sensortype.equals("STORAGE")) {
        this.type = SensorType.STORAGE;
      } else if (sensortype.equals("SHOP")) {
        this.type = SensorType.SHOP;
      } else {
        System.out.println("Sensor can only be STORAGE or SHOP.\nSets default to STORAGE.");
        this.type = SensorType.STORAGE;
      }
    } catch (Exception e) {
      this.type = SensorType.STORAGE;
    }

    try {
      this.value = Integer.parseInt(System.getenv("SENSOR_VALUE"));
    } catch (Exception e) {
      this.value = 0;
    }
  }

  /**
   * Returns a string representation of the sensor data In following format:
   * 
   * @return the Sensor data in string format.
   */
  public String toString() {
    return "New Sensor:\nType: " + this.type + "\nValue: " + this.value;
  }

  /**
   * Packs the sensor data into JSON format and then sends it to the server with
   * an address of 8080.
   * 
   * @param socket  the socket to use.
   * @param address the target address.
   */
  private void sendData(DatagramSocket socket, InetAddress address) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        JSONObject obj = new JSONObject();
        obj.put("type", type.toString());
        obj.put("value", value);
        byte[] buf = obj.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8080);

        try {
          socket.send(packet);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }, 0, 3000);
  }

  /**
   * The starting point for sending process.
   * <p>
   * Creates first a socket and the target address.
   * <p>
   * Then starts the sendData loop.
   */
  public void run() {
    try {
      DatagramSocket socket = new DatagramSocket();
      InetAddress address = InetAddress.getByName("server");

      this.sendData(socket, address);
    } catch (SocketException e) {
      e.printStackTrace();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }
}
