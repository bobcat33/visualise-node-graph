package graphvisualisation.graphics;

import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Group;
import javafx.scene.Scene;

import java.io.FileNotFoundException;

public class ApplicationWindow {
    public static final int WIDTH = Canvas.WIDTH, HEIGHT = Canvas.HEIGHT;

    private final Group root = new Group();
    private final Scene scene;

    public ApplicationWindow() throws InvalidEdgeException, UndefinedNodeException, InvalidFileException, FileNotFoundException {

        scene = new Scene(root, WIDTH, HEIGHT);

        root.getChildren().add(new Canvas());

    }

    public Scene getScene() {

        return scene;

    }


}
