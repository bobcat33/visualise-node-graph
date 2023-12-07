package graphvisualisation;

import graphvisualisation.graphics.canvas.Canvas;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        stage.setResizable(false);

        Canvas canvas = new Canvas();

        stage.setTitle("TESTING");
        stage.setScene(canvas.getScene());
        stage.show();

    }

}
