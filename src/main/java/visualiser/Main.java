package visualiser;

import visualiser.application.ApplicationWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setResizable(false);

        ApplicationWindow window = new ApplicationWindow();

        stage.setTitle("Force Directed Graph Visualisation");
        stage.setScene(window.getScene());
        stage.show();
    }

}
