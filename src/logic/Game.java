package logic;

import application.Controller;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import sockets.SocketClient;
import sockets.SocketServer;
import sun.rmi.runtime.Log;
import xml.ReplayRead;
import xml.XmlHelper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private Deck deck;
    private DeadDeck deadDeck;
    private Player player;
    private ArrayList<AI> ais;
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
    private Thread thread;
    private Thread thread1;
    private boolean replayNeTraje = true;
    List<String> replay = new ArrayList();
    boolean serverOrClient;
    SocketClient socketClient;
    SocketServer socketServer;
    private ArrayList<Card> validDeckk = new ArrayList<>();

    public Game() {

    }


    public Game(Controller controller, int numberOfAIs, int aiSpeed) {
        this.controller = controller;
        this.aiSpeed = aiSpeed;
        deck = new Deck();
        deadDeck = new DeadDeck();
        player = new Player("Player", this);
        ais = new ArrayList<AI>();
        if (numberOfAIs == 1) {
            ais.add(new AI("Computer 1", 1, this));
        } else if (numberOfAIs == 2) {
            ais.add(new AI("Computer 1", 1, this));
            ais.add(new AI("Computer 2", 2, this));
        } else if (numberOfAIs == 3) {
            ais.add(new AI("Computer 1", 3, this));
            ais.add(new AI("Computer 2", 1, this));
            ais.add(new AI("Computer 3", 2, this));
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

        player.initialize();

        player.drawCards(deck.drawCards(numberOfStartingCards, deadDeck));

        for (AI currentAI : ais) {
            currentAI.initialize();
            currentAI.drawCards(deck.drawCards(numberOfStartingCards, deadDeck));
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
        currentPlayer = random.nextInt(ais.size() + 1) + 1;

        counter = 0;

        run(currentPlayer);
    }

    private String m;
    private Message msg = new Message(m);

    public void waiter() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();

                synchronized (msg) {
                    try {
                        System.out.println(name + " waiting to get player:" + getCurrentPlayer() + "turn");
                        if (currentPlayer == 1) {
                            controller.setLabelCurrentPlayer(" It's your turn");
                        } else {
                            controller.setLabelCurrentPlayer("Computer play");
                        }
                        msg.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(name + " waiter thread got notified, Its player:" + getCurrentPlayer() + "turn");
                    if (currentPlayer == 1) {
                        controller.setLabelCurrentPlayer(" It's your turn");
                    } else {
                        controller.setLabelCurrentPlayer("Computer play");
                    }
                    //process the message now
                    System.out.println(name + " processed: " + msg.getMsg());
                }
            }
        });

        thread.start();

    }

    public void notifier() {

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();

                System.out.println(name + " started");
                try {
                    Thread.sleep(100);
                    synchronized (msg) {
                        msg.setMsg(name + " Notifier work done");
                        msg.notify();
                        // msg.notifyAll();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                }
            }

        });
        thread.start();

    }

    public synchronized void run(int currentPlayer) {
        if (running) {
            // notifier();
            //  waiter();
            if (player.getDeckSize() == 0) {
                end(player.getName());
                return;
            }

            for (AI winningAI : ais) {
                if (winningAI.getDeckSize() == 0) {
                    end(winningAI.getName());
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
                    controller.setLabelCurrentPlayer("It's your turn");

                    ArrayList<Card> validDeck = player.getValidCards(lastCard, wishColor, challenge);
                    controller.setValidPlayerDeck(player.getDeck(), validDeck);

                    controller.playerMustChallenge = false;
                    if (challenge && validDeck.size() > 0) {
                        controller.playerMustChallenge = true;
                    }

                    player.turn(lastCard, wishColor, challenge);
                } else {
                    if (running) {
                        AI currentAI = ais.get(currentPlayer - 2);

                        controller.setLabelCurrentPlayer(currentAI.getName() + " plays");

                        controller.setAIDeck(currentAI);

                        try {
                            switch (aiSpeed) {
                                case 1:
                                    Thread.sleep(500);
                                    break;
                                case 2:
                                    Thread.sleep(250);
                                    break;
                                case 3:
                                    Thread.sleep(50);
                                    break;
                                case 4:
                                    Thread.sleep(0);
                                    break;
                                default:
                                    break;
                            }
                        } catch (InterruptedException e) {
                            //ERRORHANDLING
                            e.printStackTrace();
                        }

                        currentAI.turn(lastCard, wishColor, challenge);
                    }
                }
                if (replayNeTraje) {
                    String kolona = "" + currentPlayer;
                    replay.add(kolona);
                }
            } else {
                if (!skipped) {
                    System.out.println("SKIPPED player " + currentPlayer);
                    skipped = true;
                    run(currentPlayer);
                }
            }

            counter++;
        }
    }


    private void determineNextPlayer() {
        if (direction.equals(Direction.RIGHT)) {
            if (currentPlayer == ais.size() + 1) {
                currentPlayer = 1;
            } else {
                currentPlayer++;
            }
        } else {
            if (currentPlayer == 1) {
                currentPlayer = ais.size() + 1;
            } else {
                currentPlayer--;
            }
        }
    }

    private void end(String name) {
        controller.clearAllDecks(ais);
        controller.clearAll();
        System.err.println("Player " + name + " wins!");

        running = false;

        if (currentPlayer == 1) {
            player.win();

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
            player.resetWinsInARow();

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

    public Player getPlayer() {
        return player;
    }

    public ArrayList<AI> getAIs() {
        return ais;
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

        run(currentPlayer);
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

        run(currentPlayer);
    }

    public void stop() {
        running = false;
        System.out.println("STOPPED");
    }

    public void startReplay() throws InterruptedException {
        controller.clearAllDecks(ais);
        replayNeTraje = false;
        ReplayRead replayRead = new ReplayRead(replay, this);
        replayNeTraje = true;

    }

    private void sendDisc(int x) throws IOException {
        if (serverOrClient) {
            socketServer.sendRow(x);
        }
        if (!serverOrClient) {
            socketClient.sendRow(x);
        }
    }

    public void saveXML() {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //Root element +
            Element rootEl = XmlHelper.rootElement(doc);

            Element listMoves = doc.createElement("Moves");
            rootEl.appendChild(listMoves);

            Element move2 = XmlHelper.moveElement(doc, listMoves);

            //Player element
            Element playerEl = doc.createElement("Player");
            playerEl.setAttribute("ID", new Integer(player.getID()).toString());
            move2.appendChild(playerEl);

            //Player name element
            Element playerName = doc.createElement("Name");
            playerName.appendChild(doc.createTextNode(player.getName()));
            playerEl.appendChild(playerName);

            Element playerCardsEl = XmlHelper.playerHandElement(doc, playerEl);
            int counter = 0;
            for (Card card : player.getDeck()) {
                if (counter < 7) {
                    Element cardEl = doc.createElement("Card");
                    playerCardsEl.appendChild(cardEl);

                    XmlHelper.cardColorElement(doc, cardEl, card.getColor());
                    XmlHelper.cardNumberElement(doc, cardEl, card.getType());
                    XmlHelper.cardValueElement(doc, cardEl, card.getValue());
                    counter++;
                } else {
                    break;
                }
            }

            for (Card newPlayerCard : player.getValidCards(lastCard,wishColor,challenge)) {
                Element newMoveEl = XmlHelper.moveElement(doc, listMoves);

                Element playEl = doc.createElement("Player");
                playEl.setAttribute("ID",  new Integer(player.getID()).toString());
                newMoveEl.appendChild(playEl);

                Element playCardsEl = XmlHelper.playerHandElement(doc, playEl);

                Element cardEl2 = doc.createElement("Card");
                playCardsEl.appendChild(cardEl2);


                XmlHelper.cardColorElement(doc, cardEl2, newPlayerCard.getColor());
                XmlHelper.cardNumberElement(doc, cardEl2, newPlayerCard.getType());
                XmlHelper.cardValueElement(doc, cardEl2, newPlayerCard.getValue());
            }

            TransformerFactory trFactory = TransformerFactory.newInstance();
            Transformer transformer = trFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Ime datoteke: ");
            String fileName = scanner.nextLine();

            StreamResult result = new StreamResult(new File(fileName + ".xml"));
            transformer.transform(source, result);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uspješno spremanje");
            alert.setHeaderText(null);
            alert.setContentText("XML datoteka je uspješno spremljena!");

            alert.showAndWait();

        } catch (Exception e) {
        }

    }

    public ArrayList<Card> readDealerCardsFromXML(File f) {
        ArrayList<Card> dealerCards = new ArrayList<>();
        try {
            DocumentBuilder docBuilder = getDB();
            Document docXML = docBuilder.parse(f);
            //root je Blackjack
            org.w3c.dom.Node root = docXML.getDocumentElement();
            NodeList lista = root.getFirstChild().getChildNodes();
            for (int i = 0; i < lista.getLength(); i++) {
                org.w3c.dom.Node cardNode = lista.item(i);
                NodeList listaCards = cardNode.getChildNodes();
                for (int j = 0; j < listaCards.getLength(); j++) {
                    Card dummyCard = new Card();
                    org.w3c.dom.Node card = listaCards.item(j);

                    org.w3c.dom.Node value = card.getFirstChild();
                    org.w3c.dom.Node suit = value.getNextSibling();

                    String color2 = ((Text) suit.getFirstChild()).getWholeText();
                    String value2 = ((Text) value.getFirstChild()).getWholeText();
                    String number2 = ((Text) value.getFirstChild()).getWholeText();
                    dummyCard = XmlHelper.cardFromString(color2, number2, value2);
                    dealerCards.add(dummyCard);
                }
            }

        } catch (Exception e) {
            System.out.println("EXCEPTION U DAELER XML: " + e.getMessage());
        }
        return dealerCards;
    }

    public ArrayList<Card> readFromXML(File f) {
        ArrayList<Card> validDeck = new ArrayList<>();

        try {
            DocumentBuilder docBuild = getDB();
            Document docXML = docBuild.parse(f);
            org.w3c.dom.Node root = docXML.getDocumentElement();
            NodeList lista = root.getLastChild().getChildNodes();

            for (int i = 0; i < lista.getLength(); i++) {
                org.w3c.dom.Node dummyNode = lista.item(i);
                NodeList cardList = dummyNode.getFirstChild().getLastChild().getChildNodes();
                for (int j = 0; j < cardList.getLength(); j++) {

                    Card dummyCard = new Card();

                    org.w3c.dom.Node card = cardList.item(j);
                    org.w3c.dom.Node value = card.getFirstChild();
                    org.w3c.dom.Node suit = value.getNextSibling();
                    org.w3c.dom.Node type = suit.getNextSibling();

                    String color2 = ((Text) suit.getFirstChild()).getWholeText();
                    String value2 = ((Text) type.getFirstChild()).getWholeText();
                    String number2 = ((Text) value.getFirstChild()).getWholeText();
                    dummyCard = XmlHelper.cardFromString(color2, number2, value2);

                    validDeck.add(dummyCard);
                }
                controller.setValidPlayerDeck(player.getDeck(), validDeck);
            }
            return validDeck;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static DocumentBuilder getDB() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder;
    }

}