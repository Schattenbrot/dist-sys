package com.schattenbrot.sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
// import java.net.UnknownHostException;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;

import org.json.JSONObject;

/**
 * Sensor implementation
 */
public class Sensor {
  public enum SensorType {
    OUT,
    IN
  }

  private SensorType type;
  private int value;

  /**
   * Sensor() is the standard constructor for the sensor and initializes the
   * Sensor with default variables if the environment variables weren't found or
   * incorrect.
   */
  public Sensor(String sensorType, int sensorValue) {
    if (sensorType == "out") {
      this.type = SensorType.OUT;
    } else {
      this.type = SensorType.IN;
    }
    this.value = sensorValue;
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
   * 
   * @param <T>
   */
  public <T> void run() {
    try {
      DatagramSocket socket = new DatagramSocket();

      RetryConfig config = new RetryConfigBuilder().retryIndefinitely().withDelayBetweenTries(2, ChronoUnit.SECONDS)
          .withFixedBackoff().build();
      Callable<InetAddress> callable = () -> {
        return InetAddress.getByName("server");
      };
      Status<InetAddress> status = new CallExecutorBuilder<InetAddress>().config(config).build().execute(callable);
      InetAddress address = status.getResult();
      // InetAddress address = InetAddress.getByName("server");

      this.sendData(socket, address);
    } catch (SocketException e) {
      e.printStackTrace();
      // } catch (UnknownHostException e) {
      // e.printStackTrace();
    }
  }
}
