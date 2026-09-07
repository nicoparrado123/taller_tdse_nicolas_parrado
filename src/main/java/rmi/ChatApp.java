package rmi;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

// Exercise 6.4.1 - RMI Chat
// Each instance publishes itself and connects to the remote peer.
public class ChatApp implements ChatPeer {

    private final String username;

    public ChatApp(String username) {
        this.username = username;
    }

    @Override
    public void receiveMessage(String from, String message) {
        System.out.println("[" + from + "]: " + message);
    }

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.print("Your username: ");
        String username = sc.nextLine().trim();

        System.out.print("Local port for RMI registry: ");
        int localPort = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Remote IP: ");
        String remoteHost = sc.nextLine().trim();

        System.out.print("Remote port: ");
        int remotePort = Integer.parseInt(sc.nextLine().trim());

        // Publish local object
        ChatApp app = new ChatApp(username);
        ChatPeer stub = (ChatPeer) UnicastRemoteObject.exportObject(app, 0);
        Registry localRegistry = LocateRegistry.createRegistry(localPort);
        localRegistry.rebind("chat", stub);
        System.out.println("Listening on port " + localPort + ". Waiting for remote peer...");

        // Connect to remote peer (retry until available)
        ChatPeer remote = null;
        while (remote == null) {
            try {
                Registry remoteRegistry = LocateRegistry.getRegistry(remoteHost, remotePort);
                remote = (ChatPeer) remoteRegistry.lookup("chat");
                System.out.println("Connected to " + remoteHost + ":" + remotePort);
            } catch (Exception e) {
                System.out.println("Remote not ready, retrying in 2s...");
                Thread.sleep(2000);
            }
        }

        System.out.println("Chat started. Type your messages (empty line to quit):");
        String line;
        while (!(line = sc.nextLine()).isEmpty()) {
            remote.receiveMessage(username, line);
        }

        System.out.println("Bye.");
        UnicastRemoteObject.unexportObject(app, true);
    }
}
