package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.DiMatrix;
import graphvisualisation.data.graph.Matrix;
import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;

public class ForceDirectedBuilder implements GraphBuilder {
    private final double repulsionConstant = 10000d; // todo: make scalable based on node size and idealEdgeLength
    private final double springConstant = 1d; // todo: make scalable based on idealEdgeLength
    private final double idealEdgeLength = DrawableNode.MIN_SPACE * 3; // todo: scale based on node size? or graph size
    private final double epsilon = 0.05d; // todo: probably make scalable based on repulsionConstant
    private final double cooling = 0.99999d;
    private final int maxIterations = 1000000000;
    private final int frameDuration = 1;

    private final boolean animated;

    public ForceDirectedBuilder() {
        this(false);
    }

    public ForceDirectedBuilder(boolean animated) {
        this.animated = animated;
    }

    @Override
    public void build(Graph graph, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException {
        build(graph, matrix, false);
    }

    @Override
    public void build(Graph graph, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException {
        graph.clear();

        System.out.println("Placing nodes.");

        ArrayList<DrawableNode> nodes = buildInitialGraph(graph, matrix, uniformNodeSize);

        System.out.println("Applying forces.");

        if (animated) {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(frameDuration), new Frame(graph, nodes, timeline)));
            timeline.setCycleCount(maxIterations);
            timeline.play();
        }

        else {
            int t = 0;
            while (t++ < maxIterations && applyForces(graph, nodes, t) > epsilon) {}
            System.out.println("Forces applied.");
        }

    }

    private ArrayList<DrawableNode> buildInitialGraph(Graph graph, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException {
        boolean[][] edgeMatrix = matrix.getEdgeMatrix();
        boolean directed = matrix instanceof DiMatrix;
        int maxNodeMovements = 1000;

        ArrayList<DrawableNode> nodes = new ArrayList<>();

        // Create the nodes at random positions around the graph
        for (Node node : matrix.getNodes()) {
            nodes.add(graph.drawNode(node, graph.generatePoint()));
        }

        if (uniformNodeSize) graph.resizeNodes(true, true);

        // Ensure that all nodes are on the canvas and don't clip the canvas borders
        boolean canRepositionNodes = true;
        for (DrawableNode node : nodes) {
            for (int iterations = 0;
                 canRepositionNodes && iterations <= maxNodeMovements && !graph.isWithinBounds(node);
                 ++iterations) {
                graph.moveNode(node, graph.generatePoint(), false);
                if (iterations == maxNodeMovements) {
                    System.out.println("Iterated too many times while trying to position node " + node.getNodeID() + ", no longer repositioning any nodes.");
                    canRepositionNodes = false;
                }
            }
        }


        for (int x = 0; x < edgeMatrix.length; x++) {
            for (int y = 0; y < edgeMatrix[x].length; y++) {
                if (edgeMatrix[x][y]) graph.drawEdge(x, y, directed);
            }
        }

        return nodes;
    }

    private double applyForces(Graph graph, ArrayList<DrawableNode> nodes, int iteration) {
        ArrayList<Point> forces = new ArrayList<>();

        for (DrawableNode node : nodes) {
            Point forceOnNode = new Point(0, 0);
            for (DrawableNode compareNode : nodes) {
                if (!node.equals(compareNode)) {
                    Point force = calcForce(node, compareNode, graph.areConnected(node, compareNode));
                    forceOnNode = forceOnNode.add(force);
                }
            }
            forces.add(forceOnNode);
        }

        double maxMove = 0;
        for (int nodeID = 0; nodeID < forces.size(); nodeID++) {
            double amountMoved = moveNode(graph, nodes.get(nodeID), forces.get(nodeID).multiply(calcCooling(iteration)));
            if (amountMoved > maxMove) maxMove = amountMoved;
        }

        return maxMove;
    }

    private Point calcRepulsion(DrawableNode node1, DrawableNode node2) {
        Point start = node1.getCentre();
        Point end = node2.getCentre();

        double distance = end.distanceTo(start);

        return end.getVectorTo(start).normalize().multiply(repulsionConstant/(distance*distance));
    }

    private Point calcSpring(DrawableNode node1, DrawableNode node2) {
        Point start = node1.getCentre();
        Point end = node2.getCentre();

        double distance = end.distanceTo(start) / idealEdgeLength;
        double logDistance = Math.log(distance);


        return start.getVectorTo(end).normalize().multiply(springConstant * logDistance);
    }

    private Point calcForce(DrawableNode node1, DrawableNode node2, boolean connected) {
        if (connected) return calcSpring(node1, node2);
        return calcRepulsion(node1, node2);
    }

    private double calcCooling(double iteration) {
        return Math.pow(cooling, iteration);
    }

    private double moveNode(Graph graph, DrawableNode node, Point vector) {
        Point start = node.getCentre();
        graph.moveNode(node, node.getCentre().add(vector), true);
        Point end = node.getCentre();
        return start.getVectorTo(end).magnitude();
    }

    private class Frame implements EventHandler<ActionEvent> {
        private final Graph graph;
        private final ArrayList<DrawableNode> nodes;
        private final Timeline timeline;
        private int t = 0;

        private Frame(Graph graph, ArrayList<DrawableNode> nodes, Timeline timeline) {
            this.graph = graph;
            this.nodes = nodes;
            this.timeline = timeline;
        }

        @Override
        public void handle(ActionEvent actionEvent) {

            if (t++ >= maxIterations || applyForces(graph, nodes, t) <= epsilon) {
                System.out.println("Forces applied.");
                timeline.stop();
            }

        }
    }
}
