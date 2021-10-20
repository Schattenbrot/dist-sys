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

  public String toJson() {
    return String.format("{\"name\":\"%s\",\"value\":%d}", sensorName, sensorValue);
  }

  public void init() {
    sensorName = System.getenv("SENSOR_NAME");
    sensorValue = Integer.parseInt(System.getenv("SENSOR_VALUE"));
  }

  public String greeting() {
    return "New Sensor: \"" + sensorName + "\" with starting value: " + sensorValue + ".";
  }

  public void run() {

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName("server");

            JSONObject obj = new JSONObject();
            obj.put("name", sensorName);
            obj.put("value", sensorValue);
            String json = obj.toString();
            // String json = toJson();
            byte[] buf = json.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8080);

            socket.send(packet);
        } catch (SocketException e) {
          e.printStackTrace();
          System.exit(1);
        } catch (UnknownHostException e) {
          e.printStackTrace();
          System.exit(2);
        } catch (IOException e) {
          e.printStackTrace();
          System.exit(3);
        }
      }
    }, 0, 2000);
  }
  public static void main( String[] args )
  {

    App app = new App();
    app.init();
    System.out.println(app.greeting());
    app.run();
  }
}
