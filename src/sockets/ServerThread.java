/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import application.Controller;
import application.Settings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.Game;
import logic.Player;
import java.util.ArrayList;

public class ServerThread implements Runnable {

    private static int tempId = 1;
    private Socket socket;
    private ObjectInputStream objInStream;
    private ObjectOutputStream objOutStream;
    Controller controller;
    Settings settings;

    public ServerThread(Socket socket) {
        this.socket = socket;

    }

    @Override
    public void run() {

        try {
            objInStream = new ObjectInputStream(socket.getInputStream());
            objOutStream = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("Receving player ... ");
            Player receviedPlayer = (Player) objInStream.readObject();
            System.out.println("Player recevied!");

            receviedPlayer.setID(tempId);
            System.out.println("Playerov novi game" );


//            if (receviedPlayer.isIsNewGame()) {
//                dealer = new Dealer();
//                System.out.println("Poslao sam novog dealera");
//                for (Card card : dealer.getDealerHand()) {
//                    System.out.println(card.toString());
//                }
//            }

            Game game= new Game(controller, settings.getNumberOfAIs(), settings.getAiSpeed());
            game.newGame(settings.getNumberOfStartingCards());

            System.out.println("Sending game to client ...");
            objOutStream.writeObject(game);
            System.out.println("Game has been sent!");

            System.out.println("ID playera je : " + tempId);
            tempId++;
            System.out.println("Trenutni id je : " + tempId);

        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
