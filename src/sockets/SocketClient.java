package sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Controller;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import logic.AI;
import logic.Card;
import logic.Color;
import logic.Game;


public class SocketClient implements Runnable{
    Controller controller;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    int row;
    Thread t;
    boolean turn=true;
    private Game game;

    public SocketClient(Controller con) throws IOException, ClassNotFoundException{
        controller=con;
        t=new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        try {
            startRunning();
        } catch (IOException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startRunning() throws IOException, ClassNotFoundException {
        connectToServer();
        setupStream();
        whileChatting();
    }

    private void connectToServer() throws IOException {
        System.out.println("Attempting connection...");
        connection=new Socket("localhost", 12346);
        System.out.println("Connected to "+connection.getInetAddress().getHostName());
    }

    private void setupStream() throws IOException {
        output=new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input=new ObjectInputStream(connection.getInputStream());
        System.out.println("Streams are setup!");
    }

    private void whileChatting() throws IOException, ClassNotFoundException {
        do {

            row=(Integer)input.readObject();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.game.run(row);

                }
            });
            System.out.println("recived from server\n"+row);


        } while (true);

    }
    public void sendRow(int row) throws IOException {
        output.writeObject(row);
        output.flush();
        System.out.println("Klijent salje "+row);
    }



}
