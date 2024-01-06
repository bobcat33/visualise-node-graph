package graphvisualisation.graphics;

import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.graphing.ForceDirectedGraph;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.logic.RandomBuilder;
import graphvisualisation.graphics.objects.exceptions.DuplicateEdgeException;
import graphvisualisation.graphics.objects.exceptions.DuplicateNodeException;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import javafx.scene.Group;
import javafx.scene.Scene;

import java.io.FileNotFoundException;

public class ApplicationWindow {
    public static final int WIDTH = 1500, HEIGHT = 700;

    private final Scene scene;

    public ApplicationWindow() throws InvalidFileException, FileNotFoundException, DuplicateNodeException, InvalidEdgeException, DuplicateEdgeException {

//        Graph graph = new Graph(new RandomBuilder(), WIDTH, HEIGHT, DataLoader.loadMatrix());
        ForceDirectedGraph graph = new ForceDirectedGraph(WIDTH, HEIGHT, DataLoader.loadMatrix());
        Group root = new Group();

        scene = new Scene(root, WIDTH, HEIGHT);

        root.getChildren().add(graph);

    }

    public Scene getScene() {

        return scene;

    }


}
