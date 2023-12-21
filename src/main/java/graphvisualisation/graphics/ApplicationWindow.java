package graphvisualisation.graphics;

import graphvisualisation.data.graph.DiMatrix;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.logic.ForceDirected;
import graphvisualisation.graphics.logic.Randomised;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Group;
import javafx.scene.Scene;

import java.io.FileNotFoundException;

public class ApplicationWindow {
    public static final int WIDTH = 1500, HEIGHT = 700;

    private final Scene scene;

    public ApplicationWindow() throws InvalidEdgeException, UndefinedNodeException, InvalidFileException, FileNotFoundException {

        Canvas canvas = new Canvas(new ForceDirected(), WIDTH, HEIGHT, new DiMatrix());
        Group root = new Group();

        scene = new Scene(root, WIDTH, HEIGHT);

        root.getChildren().add(canvas);

    }

    public Scene getScene() {

        return scene;

    }


}
