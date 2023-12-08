package graphvisualisation.graphics.canvas;

import graphvisualisation.graphics.nodes.DrawableNode;
import graphvisualisation.graphics.nodes.Edge;
import graphvisualisation.graphics.nodes.InvalidEdgeException;
import graphvisualisation.graphics.nodes.UndefinedNodeException;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Line;

public class Canvas {

    public static final int WIDTH = 600, HEIGHT = 600;

    private final Scene scene;
    private final Group root = new Group();
    private final Group canvas = new Group();

    public Canvas() throws UndefinedNodeException, InvalidEdgeException {

        // Just creating nodes for testing purposes, this will later be automated by converting a matrix of nodes into
        // a map, but that's a headache for later
        DrawableNode node1 = new DrawableNode(10);
        DrawableNode node2 = new DrawableNode(100);
        node1.setCentre(150, 150);
        node2.setCentre(150, 450);

        Point nodePos = node1.getPosition();
        Point nodeCen = node1.getCentre();

        Line lineX = new Line(nodeCen.getX(), nodePos.getY(), nodeCen.getX(), nodePos.getY() + node1.getNodeWidth());
        Line lineY = new Line(nodePos.getX(), nodeCen.getY(), nodePos.getX() + node1.getNodeWidth(), nodeCen.getY());

        canvas.getChildren().addAll(node1, node2, new Edge(node1, node2), lineX, lineY);

        root.getChildren().add(canvas);

        scene = new Scene(root, WIDTH, HEIGHT);


    }

    public Scene getScene() {

        return scene;

    }
}
