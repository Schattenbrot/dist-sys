package com.schattenbrot.sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hello world!
 *
 */
public class App 
{

  public void run() {

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("server");

            String msg = "uwuff";
            byte[] buf = msg.getBytes();
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
    String sensorName = System.getenv("SENSOR_NAME");
    String sensorValue = System.getenv("SENSOR_VALUE");
    System.out.println("New Sensor: \"" + sensorName + "\" with starting value: " + sensorValue + ".");

    new App().run();
  }
}
