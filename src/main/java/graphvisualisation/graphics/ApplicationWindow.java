package graphvisualisation.graphics;

import graphvisualisation.data.graph.GraphData;
import graphvisualisation.data.graph.elements.Edge;
import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.graphing.ForceDirectedGraph;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.logic.ForceDirectedBuilder;
import graphvisualisation.graphics.objects.DrawableEdge;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.exceptions.DuplicateEdgeException;
import graphvisualisation.graphics.objects.exceptions.DuplicateNodeException;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class ApplicationWindow {
    public static final int WIDTH = 1500, HEIGHT = 700;

    private final Scene scene;

    public ApplicationWindow() throws InvalidFileException, FileNotFoundException, DuplicateNodeException, InvalidEdgeException, DuplicateEdgeException {

        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < 8; i++) nodes.add(new Node(i, Integer.toString(i)));
        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge(nodes.get(0), nodes.get(1), true));
        edges.add(new Edge(nodes.get(0), nodes.get(2), true));
        edges.add(new Edge(nodes.get(0), nodes.get(3), true));
        edges.add(new Edge(nodes.get(0), nodes.get(4), true));
        edges.add(new Edge(nodes.get(1), nodes.get(4), true));
        edges.add(new Edge(nodes.get(5), nodes.get(7), true));
        edges.add(new Edge(nodes.get(5), nodes.get(1), true));

//        Graph graph = new Graph(new RandomBuilder(), WIDTH, HEIGHT, DataLoader.loadGraphData());
//        ForceDirectedGraph graph = new ForceDirectedGraph(WIDTH, HEIGHT, DataLoader.loadGraphData());
        Graph graph = new Graph((visualGraph, graphNodes, graphEdges) -> {
            visualGraph.resizeNodes(true, true);
            Random r = new Random();
            for (DrawableNode node : graphNodes) {
//                double radius = visualGraph.maxNodeRadius();
//                node.moveWithinBoundsTo(new Point(radius * 2 * (node.id() + 1), radius * 2 * r.nextInt(9) + radius));
                node.moveWithinBoundsTo(visualGraph.generatePoint());
            }
            visualGraph.draw();
        }, WIDTH, HEIGHT, nodes, edges);


        Group root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT);
        root.getChildren().add(graph);

        graph.build();

        new Timeline(new KeyFrame(Duration.seconds(3), (actionEvent) -> {
                graph.buildWith(new ForceDirectedBuilder(ForceDirectedBuilder.AnimationType.FULL_ANIMATION, false));
        })).play();
    }

    public Scene getScene() {

        return scene;

    }


}
