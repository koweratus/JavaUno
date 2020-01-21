/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import application.Controller;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class ChatServiceImpl implements ChatService {
    private String name;
    List<String> messages=new ArrayList();
    Controller con;
    public ChatServiceImpl(String name,Controller Con) throws RemoteException{
        con=Con;
        this.name=name;
    }
    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public void send(String message) throws RemoteException {
        System.out.println("This is "+name);
        String temp=name+": "+message;
        System.out.println(temp);
        messages.add(temp);
        con.setTextInTextArea(temp);
    }

    @Override
    public List<String> getAllMessages() throws RemoteException {
        return messages;
    }
    
}
