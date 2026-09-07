package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatPeer extends Remote {
    void receiveMessage(String from, String message) throws RemoteException;
}
