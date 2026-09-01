package networking;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(9090);
        System.out.println("UDP Server listening on port 9090...");

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received: " + received);

        byte[] response = ("Echo: " + received).getBytes();
        DatagramPacket reply = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());
        socket.send(reply);

        socket.close();
    }
}
