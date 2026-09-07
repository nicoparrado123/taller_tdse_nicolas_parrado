package networking;

import java.net.*;
import java.util.Date;

// Exercise 5.2.1 - UDP time server
public class UDPTimeServer {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(4445);
        System.out.println("UDPTimeServer listening on port 4445...");

        byte[] buffer = new byte[256];
        while (true) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            byte[] response = new Date().toString().getBytes();
            socket.send(new DatagramPacket(response, response.length,
                    request.getAddress(), request.getPort()));
        }
    }
}
