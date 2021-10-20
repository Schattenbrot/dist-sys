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
 * Hello world!
 *
 */
public class App
{
  public String sensorName;
  public int sensorValue;

  public void init() {
      sensorName = System.getenv("SENSOR_NAME");
      sensorValue = Integer.parseInt(System.getenv("SENSOR_VALUE"));
  }

  public String greeting() {
    return "New Sensor: \"" + sensorName + "\" with starting value: " + sensorValue + ".";
  }

  public void sendData(DatagramSocket socket, InetAddress address) {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        JSONObject obj = new JSONObject();
        obj.put("name", sensorName);
        obj.put("value", sensorValue);
        byte[] buf = obj.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8080);

        try {
          socket.send(packet);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }, 0, 1000);
  }

  public void run() {
    try {
      DatagramSocket socket = new DatagramSocket();
      InetAddress address = InetAddress.getByName("server");

      this.sendData(socket, address);
    } catch (SocketException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.exit(2);
    }
  }
  public static void main( String[] args )
  {
    App app = new App();
    app.init();
    System.out.println(app.greeting());
    app.run();
  }
}
