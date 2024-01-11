package visualiser.application;

import visualiser.graphics.canvas.Point;
import visualiser.graphics.Graph;
import visualiser.graphics.logic.ForceDirectedBuilder;
import visualiser.graphics.logic.GraphBuilder;
import visualiser.graphics.logic.NodeSlider;
import visualiser.graphics.objects.DrawableNode;
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

        // Create the graph with a basic random initializer
        Graph graph = new Graph((visualGraph, graphNodes, graphEdges) -> {
            visualGraph.resizeNodes(true, true);
            for (DrawableNode node : graphNodes) {
                node.moveWithinBoundsTo(visualGraph.generatePoint());
            }
            visualGraph.draw();
        }, WIDTH, HEIGHT-100);

        // Create the force-directed builder
        ForceDirectedBuilder builder = new ForceDirectedBuilder(ForceDirectedBuilder.AnimationType.FULL_ANIMATION, false);
        builder.setEndAction(() -> enableButtons(false));

        // Create the reset builder, slide nodes to a random position
        GraphBuilder randomReset = (visualGraph, graphNodes, graphEdges) -> {
            ArrayList<Point> randomPoints = new ArrayList<>();
            for (int i = 0; i < graphNodes.size(); i++) {
                randomPoints.add(visualGraph.generatePoint());
            }
            new NodeSlider(graphNodes, randomPoints, 500, true, this::enableButtons).start();
        };

        // Set up the reset button event
        resetButton.setOnAction((event) -> {
            disableButtons();
            graph.buildWith(randomReset);
        });

        // Set up the play button event
        playButton.setOnAction((event) -> {
            disableButtons();
            graph.buildWith(builder);
        });

        // Initialise the button positions and sizes
        resetButton.setMinSize(100, 50);
        resetButton.setLayoutX((WIDTH/2d)+20);
        resetButton.setLayoutY(HEIGHT-75);
        playButton.setMinSize(100, 50);
        playButton.setLayoutX((WIDTH/2d) - 120);
        playButton.setLayoutY(HEIGHT-75);

        // Create the scene
        Group root = new Group();
        scene = new Scene(root, WIDTH, HEIGHT);

        // Add graph elements to the scene
        root.getChildren().addAll(graph, playButton, resetButton);

        graph.build();
    }

    public Scene getScene() {
        return scene;
    }

    private void enableButtons() {
        enableButtons(true);
    }

    private void enableButtons(boolean includePlayButton) {
        if (includePlayButton) playButton.setDisable(false);
        resetButton.setDisable(false);
    }

    private void disableButtons() {
        playButton.setDisable(true);
        resetButton.setDisable(true);
    }



}
