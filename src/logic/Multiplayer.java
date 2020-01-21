package logic;

import application.Controller;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Random;

public class Multiplayer extends Game implements Runnable {
    private Deck deck;
    private DeadDeck deadDeck;
    private ArrayList<Player> players;
    private int gameCount;
    private int challengeCounter;
    private int currentPlayer;
    private Card lastCard;
    private Color wishColor;
    private boolean challenge;
    private Direction direction;
    private Controller controller;
    private boolean lastPlayerDraw;
    private boolean skipped;
    private int counter;
    private boolean running;
    private boolean showingInfo;
    private int aiSpeed;


    public Multiplayer() {

    }

    public Multiplayer(Controller controller,int numberOfPlayers) {
        this.controller = controller;

        deck = new Deck();
        deadDeck = new DeadDeck();
        players = new ArrayList<Player>();
        if (numberOfPlayers == 2) {
            players.add(new Player("Player 1", this, 1));
            players.add(new Player("Player 2", this, 2));
        }


        gameCount = 0;
        challengeCounter = 0;
    }


    public void newGame(int numberOfStartingCards) {

        deck = new Deck();
        deck.shuffle();
        deadDeck = new DeadDeck();
        gameCount++;
        challengeCounter = 0;
        lastCard = null;
        wishColor = null;
        challenge = false;
        direction = Direction.RIGHT;
        controller.setImageViewDirection(Direction.RIGHT);
        lastPlayerDraw = false;
        skipped = false;
        showingInfo = false;

        for (Player currentPlayer : players) {
            currentPlayer.initialize();
            currentPlayer.drawCards(deck.drawCards(numberOfStartingCards, deadDeck));
        }


        deadDeck.add(deck.drawCard(deadDeck));
        lastCard = deadDeck.getCards().get(deadDeck.getCards().size() - 1);

        controller.setLastCard(lastCard);
        if (lastCard.getType().equals(CardType.WILD)) {
            wishColor = Color.ALL;
            controller.chosenWishColor = wishColor;
            controller.showCircleWishColor(wishColor);
        } else if (lastCard.getType().equals(CardType.DRAW_FOUR)) {
            wishColor = Color.ALL;
            controller.chosenWishColor = wishColor;
            controller.showCircleWishColor(wishColor);
            challenge = true;
            challengeCounter = 4;
        } else if (lastCard.getType().equals(CardType.DRAW_TWO)) {
            challenge = true;
            challengeCounter = 2;
        }
    }

    public int getGameCount() {
        return gameCount;
    }

    public void start() {
        running = true;
        Random random = new Random();
        currentPlayer = random.nextInt(players.size()+1)+1 ;

        counter = 0;

        run();
    }




    public synchronized void run() {
        if (running) {
            // notifier();
            //  waiter();

            for (Player winningPlayer : players) {
                if (winningPlayer.getDeckSize() == 0) {
                    end(winningPlayer.getName());
                    return;
                }
            }

            System.out.println("ROUND: " + counter / 4 + 1);

            if (lastCard.getType().equals(CardType.REVERSE) && !lastPlayerDraw) {
                if (direction.equals(Direction.RIGHT)) {
                    direction = Direction.LEFT;
                    controller.setImageViewDirection(Direction.LEFT);

                } else {
                    direction = Direction.RIGHT;
                    controller.setImageViewDirection(Direction.RIGHT);
                }
            }

            determineNextPlayer();

            System.out.println("Player " + currentPlayer + "'s turn");

            if (skipped || !lastCard.getType().equals(CardType.SKIP)) {
                if (currentPlayer == 1) {
                    Player currentPlayers = players.get(currentPlayer);
                    controller.setLabelCurrentPlayer(currentPlayers.getName() + " plays");
                    ArrayList<Card> validDeck = currentPlayers.getValidCards(lastCard, wishColor, challenge);
                    controller.setValidPlayerDeck(currentPlayers.getDeck(), validDeck);
                    currentPlayers.turn(lastCard,wishColor,challenge);

                    controller.playerMustChallenge = false;
                    if (challenge && validDeck.size() > 0) {
                        controller.playerMustChallenge = true;
                    }

                    currentPlayers.turn(lastCard, wishColor, challenge);
                }
                else {
                    if (currentPlayer == 2) {
                        Player currentPlayers = players.get(currentPlayer);
                        controller.setLabelCurrentPlayer(currentPlayers.getName() + " plays");
                        ArrayList<Card> validDeck = currentPlayers.getValidCards(lastCard, wishColor, challenge);
                        controller.setValidPlayerDeck(currentPlayers.getDeck(), validDeck);
                        currentPlayers.turn(lastCard,wishColor,challenge);

                        controller.playerMustChallenge = false;
                        if (challenge && validDeck.size() > 0) {
                            controller.playerMustChallenge = true;
                        }

                        currentPlayers.turn(lastCard, wishColor, challenge);
                    }
                }
            } else {
                if (!skipped) {
                    System.out.println("SKIPPED player " + currentPlayer);
                    skipped = true;
                    run();
                }
            }

            counter++;
        }
    }





    private void determineNextPlayer() {
        if (direction.equals(Direction.RIGHT)) {
            if (currentPlayer == players.size()+1) {
                currentPlayer = 1;
            } else {
                currentPlayer++;
            }
        } else {
            if (currentPlayer == 2) {
                currentPlayer =  2;
            } else {
                currentPlayer--;
            }
        }
    }

    private void end(String name) {
        controller.clearAllDecks();
        controller.clearAll();
        System.err.println("Player " + name + " wins!");

        running = false;

        if (currentPlayer == 1) {
           // players.win();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Victory!");
            alert.setHeaderText("");
            alert.setContentText("You won!");
            alert.initOwner(controller.stage);
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(controller.icon);
            alert.show();

            controller.showNeutralUI();

        } else {
           // player.resetWinsInARow();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Defeat!");
            alert.setHeaderText("");
            alert.setContentText(name + " has won.");
            alert.initOwner(controller.stage);
            Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(controller.icon);
            alert.show();

            controller.showNeutralUI();
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public DeadDeck getDeadDeck() {
        return deadDeck;
    }

    public int getChallengeCounter() {
        return challengeCounter;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }


    public boolean isRunning() {
        return running;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public Controller getController() {
        return controller;
    }

    public boolean isShowingInfo() {
        return showingInfo;
    }

    public void setShowingInfo(boolean showingInfo) {
        this.showingInfo = showingInfo;
    }

    public void draw() throws InterruptedException {
        challenge = false;
        challengeCounter = 0;
        lastPlayerDraw = true;
        controller.hideLabelChallengeCounter();

        run();
    }

    public void playCard(Card card, Color wishColor) throws InterruptedException {
        deadDeck.add(card);
        lastCard = card;
        this.wishColor = wishColor;

        if (card.getType().equals(CardType.DRAW_TWO)) {
            challenge = true;
            challengeCounter += 2;
            controller.showLabelChallengeCounter("Loser pulls " + challengeCounter + " cards");
        } else if (card.getType().equals(CardType.DRAW_FOUR)) {
            challenge = true;
            challengeCounter += 4;
            controller.showLabelChallengeCounter("Loser pulls " + challengeCounter + " cards");
        } else {
            challenge = false;
            challengeCounter = 0;
            controller.hideLabelChallengeCounter();
        }

        lastPlayerDraw = false;
        skipped = false;
        controller.setLastCard(lastCard);

        System.out.println("new lastCard: " + lastCard);
        System.out.println("new wishColor: " + this.wishColor);
        System.out.println("new challenge: " + challenge);
        System.out.println("new challengeCounter: " + challengeCounter);

        run();
    }

    public void stop() {
        running = false;
        System.out.println("STOPPED");
    }
}