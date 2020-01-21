package application;

import chat.ChatClient;
import chat.ChatServer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;
import org.xml.sax.SAXException;
import sockets.SocketClient;
import sockets.SocketServer;
import threads.TimeThread;
import xml.ReplayWrite;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;


public class Controller {
    private final double CARD_HEIGHT = 90.0;
    private final double CARD_WIDTH = 57.0;
    private final double CARD_SPACING_LARGE = 14.0;
    private final double CARD_SPACING_MEDIUM = 0.0;
    private final double CARD_SPACING_SMALL = -25.0;
    private final double CARD_SPACING_ULTRA_SMALL = -35.0;
    private final Point2D AI_1_STARTING_POINT = new Point2D(100.0, 75.0);
    private final javafx.scene.paint.Color COLOR_YELLOW = javafx.scene.paint.Color.web("#FFAA00");
    private final javafx.scene.paint.Color COLOR_RED = javafx.scene.paint.Color.web("#FF5555");
    private final javafx.scene.paint.Color COLOR_BLUE = javafx.scene.paint.Color.web("#5555FD");
    private final javafx.scene.paint.Color COLOR_GREEN = javafx.scene.paint.Color.web("#55AA55");
    public Game game;
    public Test multiplayer;
    public Color chosenWishColor;
    public int drawCounter;
    public Settings settings;
    public boolean playerMustChallenge;
    public TranslateTransition translateTransition;
    public Stage stage;
    public Image icon = new Image("images/icon.png");
    @FXML
    private ImageView iconLastCard;
    @FXML
    private ImageView iconDeck;
    @FXML
    private Label labelCurrentPlayer;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label labelWishColor;
    @FXML
    private Circle circleWishColor;
    @FXML
    private ImageView imageViewWishColor;
    @FXML
    private ImageView backgroundImg;
    @FXML
    private HBox hboxInfo;
    @FXML
    private Label labelInfo;
    @FXML
    private Button buttonInfo;
    @FXML
    private Label labelChallengeCounter;
    @FXML
    private ImageView imageViewDirection;
    @FXML
    private Label labelDirection;
    @FXML
    private Label labelAI1Name;
    @FXML
    private Label labelAI2Name;
    @FXML
    private Label labelAI3Name;
    @FXML
    private Button buttonStart;
    @FXML
    private Button buttonMultiplayer;
    @FXML
    private Button btnXML;
    @FXML
    private Button btnLoadXML;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menu1;
    @FXML
    private MenuItem menuItem1;
    @FXML
    private MenuItem menuItem3;
    @FXML
    private MenuItem menuItemBack;
    @FXML
    private ImageView imageViewLogo;
    @FXML
    private Label labelLogo;
    @FXML
    private Button buttonNewGame;
    @FXML
    private Button buttonSettings;
    @FXML
    private Button btnReplay;
    @FXML
    private Label lblTime;
    @FXML
    private Button btnSendServer;
    @FXML
    private Button btnSendClient;
    @FXML
    private TextArea txtChatArea;
    @FXML
    private TextField txtMessage;
    @FXML
    private Label lblName;

    private int secretCounter;
    private boolean playerHasDrawn;
    private Point2D PLAYER_STARTING_POINT;
    private Point2D PLAYER1_STARTING_POINT;
    private Point2D AI_2_STARTING_POINT;
    private Point2D AI_3_STARTING_POINT;
    private Thread thread;
    private String m;
    private Message msg = new Message(m);
    boolean serverOrClient;
    boolean replayNeTraje = true;
    ReplayWrite Replay = new ReplayWrite();
    List<String> replay = new ArrayList();
    SocketClient socketClient;
    SocketServer socketServer;
    ChatServer server;
    ChatClient client;
    Player player;
    ArrayList<AI> ai;
    private ArrayList<Card> loadedCards = new ArrayList<>();

    public void init() {


        imageViewWishColor.setImage(new Image("/images/circle-all.png"));

        PLAYER_STARTING_POINT = new Point2D(100.0, stage.getScene().getHeight() - 50.0 - CARD_HEIGHT);
        //PLAYER1_STARTING_POINT = new Point2D(100.0, stage.getScene().getHeight() + 75.0 - CARD_HEIGHT);
        AI_2_STARTING_POINT = new Point2D(stage.getScene().getWidth() - CARD_HEIGHT - 30, 70.0);
        AI_3_STARTING_POINT = new Point2D(60.0, 70.0);

        clearAll();
        showNeutralUI();

        settings = new Settings();
        try {
            settings.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    public void startGame() {

        if (game != null) {
            game.stop();
        }

        clearAll();
        displayTime();

        drawCounter = 0;
        playerHasDrawn = false;
        playerMustChallenge = false;
        backgroundImg.setImage(createGameBackground());
        backgroundImg.setVisible(false);
        labelCurrentPlayer.setVisible(true);
        txtChatArea.setVisible(false);
        txtMessage.setVisible(false);
        btnSendClient.setVisible(false);
        btnSendServer.setVisible(false);

        //  labelCurrentPlayer.setText("");

        iconDeck.setImage(createEmptyBackCard());
        iconDeck.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (game.isRunning() && game.getCurrentPlayer() == 1 && !game.isShowingInfo() && !playerHasDrawn && !playerMustChallenge) {
                playerHasDrawn = true;
                playerMustChallenge = false;
                Card drawedCard = game.getDeck().drawCard(game.getDeadDeck());
                ArrayList<Card> allCards = new ArrayList<>();
                allCards.add(drawedCard);
                moveCardFromDeckToPlayer(allCards);


            }
        });

        game = new Game(this, settings.getNumberOfAIs(), settings.getAiSpeed());
        setLabelNames(game.getPlayer(), game.getAIs());
        game.newGame(settings.getNumberOfStartingCards());

        buttonStart.setOnAction(event -> {
            buttonStart.setVisible(false);
            game.start();

        });
        buttonStart.setVisible(true);
    }

    public void starMultiplayer() {
        try {
            ServerOrClient();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (game != null) {
            game.stop();
        }


        clearAll();
        displayTime();

        drawCounter = 0;
        playerHasDrawn = false;
        playerMustChallenge = false;
        backgroundImg.setImage(createGameBackground());
        backgroundImg.setVisible(false);
        labelCurrentPlayer.setVisible(true);
        //  labelCurrentPlayer.setText("");

        iconDeck.setImage(createEmptyBackCard());
        iconDeck.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (game.isRunning() && game.getCurrentPlayer() == 1 && !game.isShowingInfo() && !playerHasDrawn && !playerMustChallenge) {
                playerHasDrawn = true;
                playerMustChallenge = false;
                Card drawedCard = game.getDeck().drawCard(game.getDeadDeck());
                ArrayList<Card> allCards = new ArrayList<>();
                allCards.add(drawedCard);
                moveCardFromDeckToPlayer(allCards);

            }
        });

        game = new Game(this, settings.getNumberOfAIs(), settings.getAiSpeed());
        setLabelNames(game.getPlayer(), game.getAIs());
        game.newGame(settings.getNumberOfStartingCards());

        game.start();

    }

    public void showMainMenu() {
        if (game != null) {
            game.stop();
        }

        clearAll();
        clearPlayerDeck();
        clearAllDecks(game.getAIs());

        showNeutralUI();
    }

    public void showNeutralUI() {
        imageViewLogo.setVisible(true);
        labelLogo.setVisible(true);
        buttonNewGame.setVisible(true);
        buttonSettings.setVisible(true);
        buttonMultiplayer.setVisible(true);

    }

    public void hideNeutralUI() {
        imageViewLogo.setVisible(false);
        labelLogo.setVisible(false);
        buttonNewGame.setVisible(false);
        buttonSettings.setVisible(false);
        buttonMultiplayer.setVisible(false);

    }

    public void setLabelNames(Player player, ArrayList<AI> ais) {
        labelAI2Name.setVisible(false);
        labelAI3Name.setVisible(false);

        labelAI1Name.setText(ais.get(0).getName());
        labelAI1Name.setVisible(true);

        if (ais.size() >= 2) {
            labelAI2Name.setText(ais.get(1).getName());
            labelAI2Name.setVisible(true);
        }

        if (ais.size() == 3) {
            labelAI1Name.setText(ais.get(1).getName());
            labelAI2Name.setText(ais.get(2).getName());
            labelAI3Name.setText(ais.get(0).getName());
            labelAI3Name.setVisible(true);
        }
    }


    public void showCircleWishColor(Color color) {
        hideImageViewWishColor();

        switch (color) {
            case YELLOW:
                circleWishColor.setFill(COLOR_YELLOW);
                circleWishColor.setVisible(true);
                break;
            case RED:
                circleWishColor.setFill(COLOR_RED);
                circleWishColor.setVisible(true);
                break;
            case BLUE:
                circleWishColor.setFill(COLOR_BLUE);
                circleWishColor.setVisible(true);
                break;
            case GREEN:
                circleWishColor.setFill(COLOR_GREEN);
                circleWishColor.setVisible(true);
                break;
            case ALL:
                showImageViewWishColor();
                break;
            default:
                break;
        }

        labelWishColor.setVisible(true);
    }

    public void showImageViewWishColor() {
        hideCircleWishColor();

        imageViewWishColor.setVisible(true);
    }

    public void hideCircleWishColor() {
        labelWishColor.setVisible(false);
        circleWishColor.setVisible(false);
    }

    public void hideImageViewWishColor() {
        imageViewWishColor.setVisible(false);
        circleWishColor.setVisible(false);
    }

    public void hideWishColor() {
        hideCircleWishColor();
        hideImageViewWishColor();
    }

    public void hideInfo() {
        hboxInfo.setVisible(false);
    }

    public void showInfo(String text, int numberOfCards) {
        labelInfo.setText(text);
        buttonInfo.setOnAction(event -> {
            moveCardFromDeckToPlayer(game.getDeck().drawCards(game.getChallengeCounter(), game.getDeadDeck()));
        });

        hboxInfo.setVisible(true);
    }

    public void hideLabelChallengeCounter() {
        labelChallengeCounter.setVisible(false);
    }

    public void showLabelChallengeCounter(String text) {
        labelChallengeCounter.setText(text);
        labelChallengeCounter.setVisible(true);
    }

    public void hideImageViewDirection() {
        imageViewDirection.setVisible(false);
        labelDirection.setVisible(false);
    }

    public void setImageViewDirection(Direction direction) {
        imageViewDirection.setVisible(true);
        labelDirection.setVisible(true);

        if (direction.equals(Direction.RIGHT)) {
            imageViewDirection.setImage(new Image("/images/DIRECTION_RIGHT.png"));
        } else {
            imageViewDirection.setImage(new Image("/images/DIRECTION_LEFT.png"));
        }
    }

    public void setLabelCurrentPlayer(String text) {
        labelCurrentPlayer.setText(text);

    }

    public void setLastCard(Card card) {
        iconLastCard.setImage(createCard(card, true).getImage());
    }

    private Image createEmptyBackCard() {
        return new Image("images/card-back.png");
    }

    private Image createGameBackground() {
        return new Image("images/green.jpg");
    }

    private ImageView createBackCard() {
        ImageView imageView = new ImageView(new Image("images/card-back.png"));
        imageView.setFitHeight(CARD_HEIGHT);
        imageView.setFitWidth(CARD_WIDTH);

        return imageView;
    }

    private ImageView createCard(Card card, boolean valid) {
        ImageView imageView = new ImageView(new Image("images/" + card.getType() + "-" + card.getColor() + ".png"));
        imageView.setFitHeight(CARD_HEIGHT);
        imageView.setFitWidth(CARD_WIDTH);
        imageView.setSmooth(true);

        if (!valid) {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(javafx.scene.paint.Color.TRANSPARENT);
            WritableImage snapshot = imageView.snapshot(parameters, null);

            if (card.getType().equals(CardType.DRAW_FOUR) && card.getType().equals(CardType.WILD)) {
                for (int x = 0; x < snapshot.getWidth(); x++) {
                    for (int y = 0; y < snapshot.getHeight(); y++) {
                        javafx.scene.paint.Color
                                oldColor =
                                snapshot.getPixelReader().getColor(x, y).desaturate().desaturate().brighter();
                        snapshot.getPixelWriter().setColor(x, y, new javafx.scene.paint.Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), oldColor.getOpacity() * 1.0));
                    }
                }
                imageView.setImage(snapshot);
            } else {
                for (int x = 0; x < snapshot.getWidth(); x++) {
                    for (int y = 0; y < snapshot.getHeight(); y++) {
                        javafx.scene.paint.Color
                                oldColor =
                                snapshot.getPixelReader().getColor(x, y).darker().desaturate();
                        snapshot.getPixelWriter().setColor(x, y, new javafx.scene.paint.Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), oldColor.getOpacity() * 1.0));
                    }
                }
                imageView.setImage(snapshot);
            }
        }
        Controller main = this;

        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (game.isRunning() && game.getCurrentPlayer() == 1 || game.getCurrentPlayer() == 2) {
                    if (valid) {
                        if (card.getType().equals(CardType.WILD) || card.getType().equals(CardType.DRAW_FOUR)) {
                            try {
                                FXMLLoader
                                        fxmlLoader =
                                        new FXMLLoader(getClass().getResource("/application/ColorChooser.fxml"));

                                Parent root = (Parent) fxmlLoader.load();
                                Stage newStage = new Stage();
                                newStage.setScene(new Scene(root, 300, 300));
                                newStage.setTitle("Favourite colour");
                                newStage.initOwner(stage);

                                newStage.getIcons().add(icon);

                                ColorChooserController newController = fxmlLoader.getController();
                                newController.init(newStage, main);

                                newStage.initModality(Modality.APPLICATION_MODAL);
                                newStage.setResizable(false);
                                newStage.showAndWait();

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            chosenWishColor = null;
                        }

                        moveCardToDeadDeck(imageView, card, chosenWishColor);
                    }
                }
            }
        });

        return imageView;
    }

    public void moveCardToDeadDeck(ImageView view, Card card, Color newWishColor) {
        Point2D deckPosition = iconLastCard.localToScene(Point2D.ZERO);

        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(500));
        translateTransition.setNode(view);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        translateTransition.setFromX(0);
        translateTransition.setFromY(0);
        translateTransition.setToX(-(view.getX() - deckPosition.getX()));
        translateTransition.setToY(-(view.getY() - deckPosition.getY()));
        translateTransition.setOnFinished(event -> {
            if (game.isRunning()) {
                if (newWishColor != null) {
                    showCircleWishColor(newWishColor);
                } else {
                    hideWishColor();
                }
                Card playedCard = game.getPlayer().playCard(card);

                setPlayerDeck(game.getPlayer().getDeck());
                try {
                    game.playCard(playedCard, newWishColor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        if (game.isRunning()) {
            translateTransition.play();
        }
    }

    /*ovo*/
    public void moveAICardToDeadDeck(AI ai, int currentPlayer, Card card, int cardPosition, Color newWishColor) {
        ObservableList<Node> nodes = mainPane.getChildren();
        ArrayList<Integer> possibleNodes = new ArrayList<Integer>();
        for (int i = 0; i < nodes.size(); i++) {
            Node current = nodes.get(i);
            if (current.getId().contains("ai" + ai.getID())) {
                possibleNodes.add(i);
            }
        }

        ImageView view = (ImageView) mainPane.getChildren().get(possibleNodes.get(cardPosition));
        view.setImage(new Image("images/" + card.getType() + "-" + card.getColor() + ".png"));

        Point2D deckPosition = iconLastCard.localToScene(Point2D.ZERO);

        translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.millis(500));
        translateTransition.setNode(view);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        translateTransition.setFromX(0);
        translateTransition.setFromY(0);
        translateTransition.setToX(-(view.getX() - deckPosition.getX()));
        translateTransition.setToY(-(view.getY() - deckPosition.getY()));
        translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (game.isRunning()) {
                    if (newWishColor != null) {
                        showCircleWishColor(newWishColor);
                    } else {
                        hideWishColor();
                    }
                    Card playedCard = ai.playCard(card);
                    setAIDeck(ai);
                    try {
                        game.playCard(playedCard, newWishColor);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (game.isRunning()) {
            translateTransition.play();
        }
    }

    public void moveCardFromDeckToPlayer(ArrayList<Card> cards) {
        if (game.isRunning()) {
            Point2D deckPosition = iconDeck.localToScene(Point2D.ZERO);

            ImageView view = createCard(cards.get(drawCounter), true);
            view.setId("drawAnimation");
            view.setX(deckPosition.getX());
            view.setY(deckPosition.getY());
            mainPane.getChildren().add(view);

            translateTransition = new TranslateTransition();
            translateTransition.setDuration(Duration.millis(500));
            translateTransition.setNode(view);
            translateTransition.setCycleCount(1);
            translateTransition.setAutoReverse(false);
            translateTransition.setFromX(0);
            translateTransition.setFromY(0);
            translateTransition.setToX(-(view.getX() - getPositionOfRightCard(null)));
            translateTransition.setToY(-(view.getY() - PLAYER_STARTING_POINT.getY()));
            translateTransition.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ObservableList<Node> nodes = mainPane.getChildren();
                    Iterator<Node> iterator = nodes.iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().getId().equals("drawAnimation")) {
                            iterator.remove();
                        }
                    }
                    if (game.isRunning()) {
                        game.getPlayer().drawCard(cards.get(drawCounter));
                        setPlayerDeck(game.getPlayer().getDeck());
                        drawCounter++;
                        playerHasDrawn = false;

                        if (drawCounter < cards.size()) {
                            moveCardFromDeckToPlayer(cards);
                        } else {
                            game.setShowingInfo(false);
                            hideInfo();
                            drawCounter = 0;
                            try {
                                game.draw();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            if (game.isRunning()) {
                translateTransition.play();
            }
        }
    }

    private double getPositionOfRightCard(AI ai) {
        if (ai == null) {
            double maxWidth = stage.getScene().getWidth() - (PLAYER_STARTING_POINT.getX() * 2) - CARD_WIDTH;
            int deckSize = game.getPlayer().getDeckSize();
            if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth) {
                if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth) {
                    if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth) {
                        return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL;
                    } else {
                        return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL;
                    }
                } else {
                    return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM;
                }
            } else {
                return PLAYER_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE;
            }
        }
        //AI 1 (Above Player)
        else {
            double maxWidth = stage.getScene().getWidth() - (AI_1_STARTING_POINT.getX() * 2) - CARD_WIDTH;
            int deckSize = ai.getDeckSize();
            if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth) {
                if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth) {
                    if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth) {
                        return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL;
                    } else {
                        return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL;
                    }
                } else {
                    return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM;
                }
            } else {
                return AI_1_STARTING_POINT.getX() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE;
            }
        }
    }

    private double getPositionOfBottomCard(AI ai) {
        double maxHeight = stage.getScene().getHeight() - ((AI_2_STARTING_POINT.getY() + 40.0) * 2) - CARD_WIDTH;
        int deckSize = ai.getDeckSize();

        if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxHeight) {
            if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxHeight) {
                if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxHeight) {
                    return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL;
                } else {
                    return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL;
                }
            } else {
                return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM;
            }
        } else {
            return AI_2_STARTING_POINT.getY() + ((deckSize + 1) * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE;
        }
    }

    @SuppressWarnings("unused")
    /* ovo */
    public void moveCardFromDeckToAI(AI ai, ArrayList<Card> cards) {
        if (game.isRunning()) {
            Card card = game.getDeck().drawCard(game.getDeadDeck());

            Point2D deckPosition = iconDeck.localToScene(Point2D.ZERO);

            ImageView view = createBackCard();
            view.setId("drawAnimation");
            view.setX(deckPosition.getX());
            view.setY(deckPosition.getY());
            mainPane.getChildren().add(view);

            translateTransition = new TranslateTransition();
            translateTransition.setDuration(Duration.millis(500));
            translateTransition.setNode(view);
            translateTransition.setCycleCount(1);
            translateTransition.setAutoReverse(false);
            translateTransition.setFromX(0);
            translateTransition.setFromY(0);

            switch (ai.getID()) {
                case 1:
                    translateTransition.setToX(-(view.getX() - getPositionOfRightCard(ai)));
                    translateTransition.setToY(-(view.getY() - AI_1_STARTING_POINT.getY()));
                    break;
                case 2:
                    translateTransition.setToX(-(view.getX() - AI_2_STARTING_POINT.getX()));
                    translateTransition.setToY(-(view.getY() - getPositionOfBottomCard(ai)));
                    break;
                case 3:
                    translateTransition.setToX(-(view.getX() - AI_3_STARTING_POINT.getX()));
                    translateTransition.setToY(-(view.getY() - getPositionOfBottomCard(ai)));
                    break;
                default:
                    break;
            }

            translateTransition.setOnFinished(event -> {
                ObservableList<Node> nodes = mainPane.getChildren();
                Iterator<Node> iterator = nodes.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getId().equals("drawAnimation")) {
                        iterator.remove();
                    }
                }

                if (game.isRunning()) {
                    ai.drawCard(cards.get(drawCounter));
                    setAIDeck(ai);
                    drawCounter++;

                    if (drawCounter < cards.size()) {
                        moveCardFromDeckToAI(ai, cards);
                    } else {
                        game.setShowingInfo(false);
                        hideInfo();
                        drawCounter = 0;
                        try {
                            game.draw();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            if (game.isRunning()) {
                translateTransition.play();
            }
        }
    }

    public void clearPlayerDeck() {
        ObservableList<Node> nodes = mainPane.getChildren();
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals("player")) {
                iterator.remove();
            }
        }
    }

    public void setPlayerDeck(ArrayList<Card> deck) {
        clearPlayerDeck();

        int counter = 1;

        for (int i = 0; i < deck.size(); i++) {
            ImageView current = createCard(deck.get(i), true);

            PlayerCardsAnimation(deck, counter, i, current);

            current.setY(PLAYER_STARTING_POINT.getY());

            counter++;
        }
    }

    public void setValidPlayerDeck(ArrayList<Card> deck, ArrayList<Card> validDeck) {
        clearPlayerDeck();

        int counter = 1;

        for (int i = 0; i < deck.size(); i++) {
            Card currentCard = deck.get(i);
            ImageView current;

            if (validDeck.contains(currentCard)) {
                current = createCard(currentCard, true);
                current.setY(PLAYER_STARTING_POINT.getY() - CARD_HEIGHT / 4);
            } else {
                current = createCard(currentCard, false);
                current.setY(PLAYER_STARTING_POINT.getY());
            }

            PlayerCardsAnimation(deck, counter, i, current);

            counter++;
        }
    }

    private void PlayerCardsAnimation(ArrayList<Card> deck, int counter, int i, ImageView current) {
        current.setId("player");

        mainPane.getChildren().add(current);

        if (i == 0) {
            current.setX(AI_1_STARTING_POINT.getX() + CARD_WIDTH);
        } else {
            double maxWidth = stage.getScene().getWidth() - (PLAYER_STARTING_POINT.getX() * 2) - CARD_WIDTH;
            if ((deck.size() * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth) {
                if ((deck.size() * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth) {
                    if ((deck.size() * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth) {
                        current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
                    } else {
                        current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
                    }
                } else {
                    current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
                }
            } else {
                current.setX(PLAYER_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
            }
        }
    }

    public void clearAIDeck(AI ai) {
        ObservableList<Node> nodes = mainPane.getChildren();
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().contains("ai" + ai.getID())) {
                iterator.remove();
            }
        }
    }

    public void clearTwoPlayerDeck(Player players) {
        ObservableList<Node> nodes = mainPane.getChildren();
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().contains("player" + players.getID())) {
                iterator.remove();
            }
        }
    }

    public void setAIDeck(AI ai) {
        clearAIDeck(ai);

        ArrayList<Card> deck = ai.getDeck();

        int counter = 1;

        for (int i = 0; i < deck.size(); i++) {
            ImageView current = createBackCard();

            current.setId("ai" + ai.getID());

            mainPane.getChildren().add(current);

            double maxWidth;
            double maxHeight;
            int deckSize;

            switch (ai.getID()) {
                case 1:
                    maxWidth = stage.getScene().getWidth() - ((AI_1_STARTING_POINT.getX() + 0.0) * 2) - CARD_WIDTH;
                    deckSize = ai.getDeckSize();

                    if (i == 0) {
                        current.setX(AI_1_STARTING_POINT.getX() + CARD_WIDTH);
                    } else {
                        if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth) {
                            if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth) {
                                if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth) {
                                    current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
                                } else {
                                    current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
                                }
                            } else {
                                current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
                            }
                        } else {
                            current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
                        }
                    }

                    current.setY(AI_1_STARTING_POINT.getY());
                    break;

                case 2:
                    maxHeight = stage.getScene().getHeight() - ((AI_2_STARTING_POINT.getY() + 40.0) * 2) - CARD_WIDTH;
                    deckSize = ai.getDeckSize();

                    current.setRotate(-90.0);

                    if (i == 0) {
                        current.setY(AI_2_STARTING_POINT.getY() + CARD_WIDTH);
                    } else {
                        if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxHeight) {
                            if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxHeight) {
                                if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxHeight) {
                                    current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
                                } else {
                                    current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
                                }
                            } else {
                                current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
                            }
                        } else {
                            current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
                        }
                    }

                    current.setX(AI_2_STARTING_POINT.getX());
                    break;

                case 3:
                    maxHeight = stage.getScene().getHeight() - ((AI_3_STARTING_POINT.getY() + 40.0) * 2) - CARD_WIDTH;
                    deckSize = ai.getDeckSize();

                    current.setRotate(90.0);

                    if (i == 0) {
                        current.setY(AI_3_STARTING_POINT.getY() + CARD_WIDTH);
                    } else {
                        if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxHeight) {
                            if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxHeight) {
                                if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxHeight) {
                                    current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
                                } else {
                                    current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
                                }
                            } else {
                                current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
                            }
                        } else {
                            current.setY(AI_3_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
                        }
                    }

                    current.setX(AI_3_STARTING_POINT.getX());
                    break;
                default:
                    break;
            }

            counter++;
        }
    }

    public void setTwoPlayerDeck(Player players) {
        clearTwoPlayerDeck(players);

        ArrayList<Card> deck = players.getDeck();

        int counter = 1;

        for (int i = 0; i < deck.size(); i++) {
            ImageView current = createBackCard();

            current.setId("player" + players.getID());

            mainPane.getChildren().add(current);

            double maxWidth;
            double maxHeight;
            int deckSize;

            switch (players.getID()) {
                case 1:
                    maxWidth = stage.getScene().getWidth() - ((PLAYER1_STARTING_POINT.getX() + 0.0) * 2) - CARD_WIDTH;
                    deckSize = players.getDeckSize();

                    if (i == 0) {
                        current.setX(PLAYER1_STARTING_POINT.getX() + CARD_WIDTH);
                    } else {
                        if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxWidth) {
                            if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxWidth) {
                                if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxWidth) {
                                    current.setX(AI_1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
                                } else {
                                    current.setX(PLAYER1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
                                }
                            } else {
                                current.setX(PLAYER1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
                            }
                        } else {
                            current.setX(PLAYER1_STARTING_POINT.getX() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
                        }
                    }

                    current.setY(PLAYER1_STARTING_POINT.getY());
                    break;

                case 2:
                    maxHeight = stage.getScene().getHeight() - ((PLAYER_STARTING_POINT.getY() + 40.0) * 2) - CARD_WIDTH;
                    deckSize = players.getDeckSize();

                    current.setRotate(-90.0);

                    if (i == 0) {
                        current.setY(PLAYER_STARTING_POINT.getY() + CARD_WIDTH);
                    } else {
                        if ((deckSize * (CARD_WIDTH + CARD_SPACING_LARGE)) > maxHeight) {
                            if ((deckSize * (CARD_WIDTH + CARD_SPACING_MEDIUM)) > maxHeight) {
                                if ((deckSize * (CARD_WIDTH + CARD_SPACING_SMALL)) > maxHeight) {
                                    current.setY(AI_2_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_ULTRA_SMALL)) - CARD_SPACING_ULTRA_SMALL);
                                } else {
                                    current.setY(PLAYER_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_SMALL)) - CARD_SPACING_SMALL);
                                }
                            } else {
                                current.setY(PLAYER_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_MEDIUM)) - CARD_SPACING_MEDIUM);
                            }
                        } else {
                            current.setY(PLAYER_STARTING_POINT.getY() + (counter * (CARD_WIDTH + CARD_SPACING_LARGE)) - CARD_SPACING_LARGE);
                        }
                    }

                    current.setX(PLAYER_STARTING_POINT.getX());
                    break;

                default:
                    break;
            }

            counter++;
        }
    }

    public void clearAllDecks(ArrayList<AI> ais) {
        clearPlayerDeck();

        for (AI currentAI : ais) {
            clearAIDeck(currentAI);
        }
    }

    public void clearAllDecks() {
        clearPlayerDeck();

    }

    public void openSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/application/Settings.fxml"));

            Parent root = (Parent) fxmlLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 600, 400));
            newStage.setTitle("Settings");
            newStage.initOwner(stage);

            newStage.getIcons().add(icon);
            SettingsController newController = fxmlLoader.getController();
            newController.init(newStage, this);

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setResizable(false);
            newStage.showAndWait();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void clearAll() {
        hideNeutralUI();
        hideWishColor();
        hideInfo();
        labelCurrentPlayer.setVisible(false);
        hideLabelChallengeCounter();
        hideImageViewDirection();
        labelAI1Name.setVisible(false);
        labelAI2Name.setVisible(false);
        labelAI3Name.setVisible(false);
        buttonStart.setVisible(false);
        iconDeck.setImage(null);
        iconLastCard.setImage(null);
    }

    public void displayTime() {
        TimeThread timeThread = new TimeThread(lblTime);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater((Runnable) timeThread);

            }
        }, 0, 1000);
    }

    private void ServerOrClient() throws RemoteException, IOException, ClassNotFoundException {
        System.out.println("\n1. Server\n2. Klijent\n");
        Scanner in = new Scanner(System.in);
        int odabir = in.nextInt();
        if (odabir == 1) {
            server = new ChatServer(this);
            btnSendClient.setVisible(false);
            serverOrClient = true;
            socketServer = new SocketServer(this);
        } else {
            client = new ChatClient(this);
            btnSendServer.setVisible(false);
            serverOrClient = false;
            socketClient = new SocketClient(this);
        }
    }

    public void setTextInTextArea(String msg) {
        String temp = txtChatArea.getText();
        temp = temp + "\n" + msg;
        txtChatArea.setText(temp);
    }

    @FXML
    private void OnClickBtnSendServer(ActionEvent event) throws RemoteException {
        String msg = txtMessage.getText();
        txtMessage.clear();
        setTextInTextArea("Klijent: " + msg);
        server.sendMessage(msg);
        List<String> list = server.getAllMessages();
    }

    @FXML
    private void OnClickBtnSednClient(ActionEvent event) throws RemoteException {
        String msg = txtMessage.getText();
        txtMessage.clear();
        setTextInTextArea("Server: " + msg);
        client.sendMessage(msg);
        List<String> list = client.getAllMessages();
    }

    private void sendDisc(int row) throws IOException {
        if (serverOrClient) {
            socketServer.sendRow(row);
        }
        if (!serverOrClient) {
            socketClient.sendRow(row);
        }
    }

    @FXML
    private void OnClickBtnReplay(ActionEvent event) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
        //ReplayRead ReplayRead=new ReplayRead();
        game.startReplay();
    }

    @FXML
    public void saveXML() {

        game.saveXML();

    }
    @FXML
    private void onLoadClick() {

        FileChooser choser = new FileChooser();
        choser.setTitle("ODABERI DATOTEKU");
        File load = choser.showOpenDialog(stage);//choser.showOpenDialog(lblName.getScene().getWindow());
        if (load != null) {
            clearPlayerDeck();

            loadedCards= game.readFromXML(load);
       /*     ArrayList<Card> validDeck = player.getValidCards(lastCard, wishColor, challenge);
            setValidPlayerDeck(getDeck(), validDeck);*/



        }
    }

}