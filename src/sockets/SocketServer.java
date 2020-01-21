package sockets;

//import com.sun.corba.se.impl.io.InputStreamHook;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Controller;
import javafx.application.Platform;
import logic.Card;
import logic.Game;
import sockets.SocketClient;


public class SocketServer implements Runnable{
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    Controller controller;
    Thread t;
    int row;
    private Game game;


    public SocketServer(Controller con) throws IOException, ClassNotFoundException{
        controller=con;
        t=new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Start");
            startRunning();
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startRunning() throws IOException, ClassNotFoundException{
        server=new ServerSocket(12346);
        while (true) {
            waitForConnection();
            setUpStreams();
            whileChatting();
        }
    }

    private void waitForConnection() throws IOException {
        System.out.println("Waiting for someone to connect...");
        connection=server.accept();
        System.out.println("Now connected to "+connection.getInetAddress().getHostName());
    }

    private void setUpStreams() throws IOException {
        output= new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input=new ObjectInputStream(connection.getInputStream());
        System.out.println("Streams are setup!");
    }

    private void whileChatting() throws IOException, ClassNotFoundException {
        System.out.println("You are now connected!");

        do {
            row=(Integer)input.readObject();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.game.run(row);
                }
            });

            System.out.println("Recived from client\nRow: "+row);
        } while (true);

    }

    public void sendRow(int row) throws IOException {
        output.writeObject(row);
        output.flush();
        System.out.println("Server salje "+row);
    }



}
