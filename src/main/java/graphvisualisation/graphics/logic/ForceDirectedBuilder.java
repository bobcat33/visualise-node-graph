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

public class ForceDirectedBuilder implements GraphBuilder {
    private final double repulsionConstant = 10000d; // todo: make scalable based on node size and idealEdgeLength
    private final double edgeRepulsionConstant = 10d;
    private final double sideRepulsionConstant = 1000d;
    private final double springConstant = 1d; // todo: make scalable based on idealEdgeLength
    private final double idealEdgeLength = DrawableNode.MIN_SPACE * 3; // todo: scale based on node size? or graph size
    private final double epsilon = 0.05d; // todo: probably make scalable based on repulsionConstant
    private final double cooling = 0.99999d;
    private final int maxIterations = 1000000000;
    private final int frameDuration = 1;
    private final boolean edgesRepel = false;
    private final boolean graphSidesRepel = true;

    private final boolean animated;

    public ForceDirectedBuilder() {
        this(false);
    }

    public ForceDirectedBuilder(boolean animated) {
        this.animated = animated;
    }

    @Override
    public void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {
        graph.clear();

        System.out.println("Placing nodes.");

        buildInitialGraph(graph, nodes, edges);

        System.out.println("Applying forces.");

        if (animated) {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(frameDuration),
                    new Frame(graph, nodes, edges, timeline)));
            timeline.setCycleCount(maxIterations);
            timeline.play();
        }

        else {
            int t = 0;
            while (t++ < maxIterations && applyForces(graph, nodes, edges, t) > epsilon) {}
            System.out.println("Forces applied.");
        }

    }

    private void buildInitialGraph(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {

        // Create the nodes at random positions around the graph
        for (DrawableNode node : nodes) {
            node.draw();
            node.moveWithinBoundsTo(graph.generatePoint());
        }

        graph.resizeNodes(true, true);

        for (DrawableEdge edge : edges) {
            edge.draw();
        }
    }

    private ArrayList<Point> calcForces(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {
        ArrayList<Point> forces = new ArrayList<>();

        for (DrawableNode node : nodes) {
            Point forceOnNode = new Point(0, 0);
            for (DrawableNode compareNode : nodes) {
                if (!node.equals(compareNode)) {
                    Point force = calcForce(node, compareNode, graph.areConnected(node, compareNode));
                    forceOnNode = forceOnNode.add(force);
                }
            }
            if (edgesRepel) forceOnNode = forceOnNode.add(calcEdgeRepulsion(node, edges));
            if (graphSidesRepel) forceOnNode = forceOnNode.add(calcSideRepulsion(graph, node));
            forces.add(forceOnNode);
        }

        return forces;
    }

    private Point calcEdgeRepulsion(DrawableNode node, ArrayList<DrawableEdge> edges) {
        Point forceOnNode = new Point();
        for (DrawableEdge edge : edges) {
            // If the node is part of the edge don't repel
            if (edge.involves(node)) continue;

            // Get the closest point on the edge relative to the node
            Point start = node.getCentre();
            Point end = edge.closestPointTo(start);

            // If the node is past either end of the edge then don't repel
            if (end == null || start.equals(end)) continue;

            double distance = start.distanceTo(end);
            if (distance <= 10) {
                System.out.println("Distance from node '" + node.name() + "' to line = " + distance);
                continue;
            }

            if (distance >= node.getNodeRadius() && distance <= node.getNodeRadius()) continue;

            forceOnNode = forceOnNode.add(calcRepulsionBetween(end, start, edgeRepulsionConstant));
        }
        return forceOnNode;
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

    private double applyForces(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges, int iteration) {
        ArrayList<Point> forces = calcForces(graph, nodes, edges);

        double maxMove = 0;
        for (int nodeID = 0; nodeID < forces.size(); nodeID++) {
            double amountMoved = moveNode(nodes.get(nodeID), forces.get(nodeID).multiply(calcCooling(iteration)));
            if (amountMoved > maxMove) maxMove = amountMoved;
        }

        return maxMove;
    }

    private Point calcRepulsion(DrawableNode node1, DrawableNode node2) {
        return calcRepulsionBetween(node2.getCentre(), node1.getCentre(), repulsionConstant);
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

    private double moveNode(DrawableNode node, Point vector) {
        Point start = node.getCentre();
        node.moveWithinBoundsTo(node.getCentre().add(vector));
        Point end = node.getCentre();
        return start.getVectorTo(end).magnitude();
    }

    private class Frame implements EventHandler<ActionEvent> {
        private final Graph graph;
        private final ArrayList<DrawableNode> nodes;
        private final ArrayList<DrawableEdge> edges;
        private final Timeline timeline;
        private int t = 0;

        private Frame(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges, Timeline timeline) {
            this.graph = graph;
            this.nodes = nodes;
            this.edges = edges;
            this.timeline = timeline;
        }

        @Override
        public void handle(ActionEvent actionEvent) {

            if (t++ >= maxIterations || applyForces(graph, nodes, edges, t) <= epsilon) {
                System.out.println("Forces applied.");
                timeline.stop();
            }

        }
    }
}
