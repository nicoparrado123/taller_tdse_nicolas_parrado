package networking;

import java.net.*;

// Exercise 5.2.1 - UDP time client: updates every 5s, retains last value if server is down
public class UDPTimeClient {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(2000); // 2s timeout so we don't block forever
        InetAddress address = InetAddress.getByName("127.0.0.1");

        String lastTime = "(no time received yet)";

        while (true) {
            try {
                byte[] request = new byte[1];
                socket.send(new DatagramPacket(request, request.length, address, 4445));

                byte[] buffer = new byte[256];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                lastTime = new String(response.getData(), 0, response.getLength());
            } catch (SocketTimeoutException e) {
                System.out.println("Server unreachable, last known time: " + lastTime);
                Thread.sleep(5000);
                continue;
            }

            System.out.println("Server time: " + lastTime);
            Thread.sleep(5000);
        }
    }
}
