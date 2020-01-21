package application;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.Color;

public class ColorChooserController {

    @FXML
    private Rectangle rectYellow;
    @FXML
    private Rectangle rectRed;
    @FXML
    private Rectangle rectBlue;
    @FXML
    private Rectangle rectGreen;

    public void init(Stage stage, Controller controller) {
        rectYellow.setOnMouseClicked(event -> {
            controller.chosenWishColor = Color.YELLOW;
            stage.close();
        });

        rectRed.setOnMouseClicked(event -> {
            controller.chosenWishColor = Color.RED;
            stage.close();
        });

        rectBlue.setOnMouseClicked(event -> {
            controller.chosenWishColor = Color.BLUE;
            stage.close();
        });

        rectGreen.setOnMouseClicked(event -> {
            controller.chosenWishColor = Color.GREEN;
            stage.close();
        });

        stage.setOnCloseRequest(event -> event.consume());
    }
}