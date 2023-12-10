package graphvisualisation;

import graphvisualisation.graphics.ApplicationWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        stage.setResizable(false);

        ApplicationWindow window = new ApplicationWindow();

        stage.setTitle("TESTING");
        stage.setScene(window.getScene());
        stage.show();

    }

}
