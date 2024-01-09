package graphvisualisation.graphics.logic;

import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.objects.DrawableEdge;
import graphvisualisation.graphics.objects.DrawableNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class ForceDirectedBuilder implements GraphBuilder {
    private final double repulsionConstant = 10000d; // todo: make scalable based on node size and idealEdgeLength
    private final double sideRepulsionConstant = 1000d;
    private final double springConstant = 1d; // todo: make scalable based on idealEdgeLength
    private final double nodeCollisionForce = 100d;
    private final double idealEdgeLength = DrawableNode.MIN_SPACE * 3; // todo: scale based on node size? or graph size
    private final double epsilon = 0.05d; // todo: probably make scalable based on repulsionConstant
    private final double cooling = 0.99999d;
    private final int maxIterations = 1000000000;
    private final int frameDuration = 1;
    private final int slideDuration = 3000;
    private final boolean sidesRepel = true;
    private final Random random = new Random();

    private final AnimationType animationType;
    private final boolean drawInitialGraph;

    public enum AnimationType {
        FULL_ANIMATION,
        SLIDE_TO_END,
        NONE
    }

    public ForceDirectedBuilder() {
        this(AnimationType.NONE);
    }

    public ForceDirectedBuilder(AnimationType animationType) {
        this(animationType, true);
    }

    public ForceDirectedBuilder(AnimationType animationType, boolean drawInitialRandomGraph) {
        this.animationType = animationType;
        this.drawInitialGraph = drawInitialRandomGraph;
    }

    @Override
    public void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {
        System.out.println("Placing nodes.");

        if (drawInitialGraph) buildInitialGraph(graph, nodes, edges);

        System.out.println("Applying forces.");

        if (animationType.equals(AnimationType.FULL_ANIMATION)) {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(frameDuration),
                    new FullFrame(graph, nodes, timeline)));
            timeline.setCycleCount(maxIterations);
            timeline.play();
        }

        else {
            ArrayList<Point> startSnapshot = null;
            if (animationType.equals(AnimationType.SLIDE_TO_END)) {
                startSnapshot = graph.getNodePositionSnapshot();
                graph.freezeCanvas();
            }

            int t = 0;
            while (t++ < maxIterations && applyForces(graph, nodes, t) > epsilon) {}
            System.out.println("Forces applied.");

            if (animationType.equals(AnimationType.SLIDE_TO_END)) {
                System.out.println("Sliding nodes.");
                ArrayList<Point> endSnapshot = graph.getNodePositionSnapshot();
                graph.returnNodesToSnapshot(startSnapshot);
                graph.unfreezeCanvas();
                slideNodesTo(endSnapshot, nodes);
            }
        }

    }

    private void buildInitialGraph(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {
        graph.resizeNodes(true, true);

        // Create the nodes at random positions around the graph
        for (DrawableNode node : nodes) {
            node.draw();
            node.moveWithinBoundsTo(graph.generatePoint());
        }

        for (DrawableEdge edge : edges) edge.draw();
    }

    private ArrayList<Point> calcForces(Graph graph, ArrayList<DrawableNode> nodes) {
        ArrayList<Point> forces = new ArrayList<>();

        for (DrawableNode node : nodes) {
            Point forceOnNode = new Point(0, 0);
            for (DrawableNode compareNode : nodes) {
                if (!node.equals(compareNode)) {
                    Point force = calcForce(node, compareNode, graph.areConnected(node, compareNode));
                    forceOnNode = forceOnNode.add(force);
                }
            }
            if (sidesRepel) forceOnNode = forceOnNode.add(calcSideRepulsion(graph, node));
            forces.add(forceOnNode);
        }

        return forces;
    }

    private Point calcSideRepulsion(Graph graph, DrawableNode node) {
        double graphHeight = graph.height();
        double graphWidth = graph.width();
        Point centre = node.getCentre();

        Point top = new Point(centre.getX(), 0);
        Point bottom = new Point(centre.getX(), graphHeight);
        Point left = new Point(0, centre.getY());
        Point right = new Point(graphWidth, centre.getY());

        Point force = new Point();
        force = force.add(calcRepulsionBetween(top, centre, sideRepulsionConstant));
        force = force.add(calcRepulsionBetween(bottom, centre, sideRepulsionConstant));
        force = force.add(calcRepulsionBetween(left, centre, sideRepulsionConstant));
        force = force.add(calcRepulsionBetween(right, centre, sideRepulsionConstant));

        return force;
    }

    private Point calcRepulsionBetween(Point start, Point end, double repulsionConstant) {
        double distance = start.distanceTo(end);
        return start.getVectorTo(end).normalize().multiply(repulsionConstant/(distance*distance));
    }

    private double applyForces(Graph graph, ArrayList<DrawableNode> nodes, int iteration) {
        ArrayList<Point> forces = calcForces(graph, nodes);

        double maxMove = 0;
        for (int nodeID = 0; nodeID < forces.size(); nodeID++) {
            double amountMoved = moveNode(nodes.get(nodeID), forces.get(nodeID).multiply(calcCooling(iteration)));
            if (amountMoved > maxMove) maxMove = amountMoved;
        }

        return maxMove;
    }

    private Point createRandomForce() {
        return new Point((random.nextInt(2) * 2 - 1) * nodeCollisionForce, (random.nextDouble(2) * 2 - 1) * nodeCollisionForce);
    }

    private Point calcRepulsion(DrawableNode node1, DrawableNode node2) {
        if (node1.intersects(node2)) return createRandomForce();
        return calcRepulsionBetween(node2.getCentre(), node1.getCentre(), repulsionConstant);
    }

    private Point calcSpring(DrawableNode node1, DrawableNode node2) {
        if (node1.intersects(node2)) return createRandomForce();
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

    private double moveNode(DrawableNode node, Point vector) {
        Point start = node.getCentre();
        node.moveWithinBoundsTo(start.add(vector));
        Point end = node.getCentre();
        return start.getVectorTo(end).magnitude();
    }

    private void slideNodesTo(ArrayList<Point> endPoints, ArrayList<DrawableNode> nodes) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), new SlideFrame(nodes, endPoints)));
        timeline.setCycleCount(slideDuration);
        timeline.play();
    }

    private class SlideFrame implements EventHandler<ActionEvent> {
        private final ArrayList<DrawableNode> nodes;
        private final ArrayList<Point> endPoints;
        private int frameNumber = 0;

        private SlideFrame(ArrayList<DrawableNode> nodes, ArrayList<Point> endPoints) {
            this.nodes = nodes;
            this.endPoints = endPoints;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            double remainingFrames = slideDuration - frameNumber;
            double distanceMultiplier = 1d / remainingFrames;
            for (int i = 0; i < nodes.size(); i++) {
                DrawableNode node = nodes.get(i);
                Point nodeCentre = node.getCentre();
                Point endPoint = endPoints.get(i);
                double distance = nodeCentre.distanceTo(endPoint);
                double travelDistance = distance * distanceMultiplier;
                Point normalisedVector = nodeCentre.getVectorTo(endPoint).normalize();
                Point movementVector = normalisedVector.multiply(travelDistance);
                node.moveTo(nodeCentre.add(movementVector));
            }
            if (++frameNumber == slideDuration) {
                System.out.println("Sliding complete.");
                for (int i = 0; i < nodes.size(); i++) {
                    DrawableNode node = nodes.get(i);
                    Point endPoint = endPoints.get(i);
                    System.out.println(node.toString() + ": " + node.getCentre() + " - " + endPoint);
                }
            }
        }
    }

    private class FullFrame implements EventHandler<ActionEvent> {
        private final Graph graph;
        private final ArrayList<DrawableNode> nodes;
        private final Timeline timeline;
        private int t = 0;

        private FullFrame(Graph graph, ArrayList<DrawableNode> nodes, Timeline timeline) {
            this.graph = graph;
            this.nodes = nodes;
            this.timeline = timeline;
        }

        @Override
        public void handle(ActionEvent actionEvent) {

            if (t++ >= maxIterations || applyForces(graph, nodes, t) <= epsilon) {
                System.out.println("Forces applied.");
                timeline.stop();
                graph.unfreezeCanvas();
            }

        }
    }
}
