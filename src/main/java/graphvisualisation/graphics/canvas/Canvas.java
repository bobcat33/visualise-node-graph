package graphvisualisation.graphics.canvas;

import graphvisualisation.graphics.nodes.DrawableNode;
import graphvisualisation.graphics.nodes.Edge;
import graphvisualisation.graphics.nodes.InvalidEdgeException;
import graphvisualisation.graphics.nodes.UndefinedNodeException;
import javafx.scene.Group;
import javafx.scene.Scene;

public class Canvas {

    public static final int WIDTH = 600, HEIGHT = 600;

    private final Scene scene;
    private final Group root = new Group();
    private final Group canvas = new Group();

    public Canvas() throws UndefinedNodeException, InvalidEdgeException {

        DrawableNode node1 = new DrawableNode(10);
        node1.draw();
        DrawableNode node2 = new DrawableNode(100);
        node2.draw();
        node1.setCentre(150, 150);
        node2.setCentre(450, 450);

//        node1.redrawMaintainCentre();

        canvas.getChildren().addAll(node1, node2, new Edge(node1, node2));

        root.getChildren().add(canvas);

        scene = new Scene(root, WIDTH, HEIGHT);


    }

    public Scene getScene() {

        return scene;

    }
}
