package graphvisualisation.application;

import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.Graph;
import graphvisualisation.graphics.logic.ForceDirectedBuilder;
import graphvisualisation.graphics.logic.GraphBuilder;
import graphvisualisation.graphics.logic.NodeSlider;
import graphvisualisation.graphics.objects.DrawableNode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.util.ArrayList;

public class ApplicationWindow {
    public static final int WIDTH = 1500, HEIGHT = 700;

    private final Scene scene;
    private final Button resetButton = new Button("Reset");
    private final Button playButton = new Button("Play");

    public ApplicationWindow() {

        Graph graph = new Graph((visualGraph, graphNodes, graphEdges) -> {
            visualGraph.resizeNodes(true, true);
//            Random r = new Random();
            for (DrawableNode node : graphNodes) {
//                double radius = visualGraph.maxNodeRadius();
//                node.moveWithinBoundsTo(new Point(radius * 2 * (node.id() + 1), radius * 2 * r.nextInt(9) + radius));
                node.moveWithinBoundsTo(visualGraph.generatePoint());
//                node.setHoverAction(null);
            }
            visualGraph.draw();
        }, WIDTH, HEIGHT-100);

        ForceDirectedBuilder builder = new ForceDirectedBuilder(ForceDirectedBuilder.AnimationType.FULL_ANIMATION, false);
        builder.setEndAction(() -> {
            resetButton.setDisable(false);
        });

        GraphBuilder randomReset = (visualGraph, graphNodes, graphEdges) -> {
            ArrayList<Point> randomPoints = new ArrayList<>();
            for (int i = 0; i < graphNodes.size(); i++) {
                randomPoints.add(visualGraph.generatePoint());
            }
            new NodeSlider(graphNodes, randomPoints, 500, true, this::enableButtons).start();
        };

        resetButton.setOnAction((event) -> {
            disableButtons();
            graph.buildWith(randomReset);
        });

        playButton.setOnAction((event) -> {
            disableButtons();
            graph.buildWith(builder);
        });

        Group root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT);

        root.getChildren().addAll(graph, playButton, resetButton);

        resetButton.setMinSize(100, 50);
        resetButton.setLayoutX((WIDTH/2d)+20);
        resetButton.setLayoutY(HEIGHT-75);
        playButton.setMinSize(100, 50);
        playButton.setLayoutX((WIDTH/2d) - 120);
        playButton.setLayoutY(HEIGHT-75);

        graph.build();
    }

    public Scene getScene() {
        return scene;
    }

    private void enableButtons() {
        playButton.setDisable(false);
        resetButton.setDisable(false);
    }

    private void disableButtons() {
        playButton.setDisable(true);
        resetButton.setDisable(true);
    }



}
