package networking;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName("localhost");

        byte[] buffer = "Hello, UDP Server!".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 9090);
        socket.send(packet);

        byte[] responseBuffer = new byte[1024];
        DatagramPacket response = new DatagramPacket(responseBuffer, responseBuffer.length);
        socket.receive(response);

        System.out.println("Server response: " + new String(response.getData(), 0, response.getLength()));
        socket.close();
    }
}
