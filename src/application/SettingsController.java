package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.xml.sax.SAXException;
import xml.ReplayRead;
import xml.ReplayWrite;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {
    @FXML
    private Slider sliderNumberOfAIs;
    @FXML
    private Slider sliderNumberOfStartingCards;
    @FXML
    private Slider sliderAISpeed;
    @FXML
    private CheckBox checkBoxRule1;
    @FXML
    private CheckBox checkBoxRule2;
    @FXML
    private CheckBox checkBoxRule3;

    private Stage stage;
    private Controller controller;



    public void init(Stage stage, Controller controller) {
        this.stage = stage;
        this.controller = controller;
        sliderAISpeed.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n < 1.5)
                    return "Slow";
                if (n < 2.5)
                    return "Medium";
                if (n < 3.5)
                    return "Fast";

                return "Very Fast";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Slow":
                        return 1d;
                    case "Medium":
                        return 2d;
                    case "Fast":
                        return 3d;

                    default:
                        return 4d;
                }
            }
        });

        Settings settings = controller.settings;

        sliderNumberOfAIs.setValue((double) settings.getNumberOfAIs());
        sliderNumberOfStartingCards.setValue((double) settings.getNumberOfStartingCards());
        sliderAISpeed.setValue((double) settings.getAiSpeed());
        checkBoxRule1.setSelected(settings.isAllowChallengePlusTwo());
        checkBoxRule2.setSelected(settings.isAllowChallengePlusFourWithTwo());
        checkBoxRule3.setSelected(settings.isAllowChallengePlusFourWithFour());


    }

    public void save() throws TransformerException, XMLStreamException, ParserConfigurationException {
        int numberOfAIs = (int) sliderNumberOfAIs.getValue();
        int numberOfStartingCards = (int) sliderNumberOfStartingCards.getValue();
        int aiSpeed = (int) sliderAISpeed.getValue();

        boolean allowChallengePlusTwo = checkBoxRule1.isSelected();
        boolean allowChallengePlusFourWithTwo = checkBoxRule2.isSelected();
        boolean allowChallengePlusFourWithFour = checkBoxRule3.isSelected();

        controller.settings =
                new Settings(numberOfAIs, numberOfStartingCards, aiSpeed, allowChallengePlusTwo, allowChallengePlusFourWithTwo, allowChallengePlusFourWithFour);
        try {
            controller.settings.save();
            controller.settings.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        stage.close();
    }

}