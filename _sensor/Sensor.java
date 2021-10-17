import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

class Main {
  public static void main(String[] args) {
    System.out.println(System.getenv("WEATHER"));

    try {
      DatagramSocket socket = new DatagramSocket();
      InetAddress address = InetAddress.getByName("server");

      String msg = "uwuff";
      byte[] buf = msg.getBytes();
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 8080);
  
      socket.send(packet);

      socket.close();
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
}