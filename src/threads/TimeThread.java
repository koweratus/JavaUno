package threads;

import javafx.scene.control.Label;

import java.time.LocalTime;

public class TimeThread implements Runnable {
    Label lblTime;

    public TimeThread(Label lblTime){
        this.lblTime = lblTime;
    }

    @Override
    public void run() {
        try {
            LocalTime currentTime = LocalTime.now();
            lblTime.setText(currentTime.withNano(0).toString());
        } catch (Exception e) {
            System.out.println("Problem with time thread");
            System.out.println(e.getMessage());
        }
    }
}
