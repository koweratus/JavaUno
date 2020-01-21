package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.Game;
import logic.Message;

import java.io.IOException;


public class Main2 extends Application {

    protected static final String userHome = System.getProperty("user.home");

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws InterruptedException, IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainGUI.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 650);

            stage.setResizable(true);
            stage.setTitle("UNO");
            stage.setScene(scene);
            stage.setResizable(false);

            Controller controller = loader.getController();
            controller.setStage(stage);
            controller.init();

            stage.getIcons().add(new Image("images/icon.png"));
            stage.show();
            Reflection.main();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}