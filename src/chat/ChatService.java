/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote {
    String getName() throws RemoteException;
    void send(String message)throws RemoteException;
    List<String> getAllMessages() throws RemoteException;
}
