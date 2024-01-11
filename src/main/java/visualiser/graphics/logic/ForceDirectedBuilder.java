package visualiser.graphics.logic;

import visualiser.graphics.canvas.Point;
import visualiser.graphics.Graph;
import visualiser.graphics.objects.DrawableEdge;
import visualiser.graphics.objects.DrawableNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class ForceDirectedBuilder implements GraphBuilder {
    // todo: made all constants modifiable
    private static final double
            REPULSION_CONSTANT = 10000d,
            SIDE_REPULSION_CONSTANT = 1000d,
            SPRING_CONSTANT = 1d, // todo: make scalable based on idealEdgeLength
            NODE_COLLISION_FORCE = 1d,
            IDEAL_EDGE_LENGTH = DrawableNode.MIN_SPACE * 3, // todo: scale based on node size or graph size
            EPSILON = 0.05d, // todo: probably make scalable based on repulsionConstant
            COOLING = 0.99999d;
    private static final int
            MAX_ITERATIONS = 1000000000,
            FRAME_DURATION = 1,
            SLIDE_DURATION = 3000;
    private static final boolean sidesRepel = true;

    private final Random random = new Random();
    private final AnimationType animationType;
    private final boolean drawInitialGraph;

    private boolean canBuild = true;
    private EndAction endAction;

    /**
     * The type of animations for the force-directed graph building algorithm.
     * <ul>
     *     <li>{@link #FULL_ANIMATION} - Fully animated forces.</li>
     *     <li>{@link #SLIDE_TO_END} - Slide to the end position.</li>
     *     <li>{@link #NONE} - No animation.</li>
     * </ul>
     */
    public enum AnimationType {
        /** Fully animated graph drawing. Animates the forces from start to finish.
         * @see AnimationType*/
        FULL_ANIMATION,
        /** Partially animated graph drawing. Animates a smooth movement directly from the start position to the end
         * result.
         * @see AnimationType*/
        SLIDE_TO_END,
        /** No animation. Produces the end result immediately.
         * @see AnimationType*/
        NONE
    }

    /**
     * Create a new force-directed builder which will have no animation and will draw the initial random graph.
     */
    public ForceDirectedBuilder() {
        this(AnimationType.NONE);
    }

    /**
     * Create a new force-directed builder which will draw the initial random graph.
     * @param animationType the type of {@link AnimationType animation} to use when building the graph using the
     *                      force-directed algorithm
     */
    public ForceDirectedBuilder(AnimationType animationType) {
        this(animationType, true);
    }

    /**
     * Create a new force-directed builder.
     * @param animationType the type of {@link AnimationType animation} to use when building the graph using the
     *                      force-directed algorithm
     * @param drawInitialRandomGraph if true the builder will randomise the nodes' positions before applying the
     *                               algorithm, if false the algorithm will be applied immediately without moving
     *                               the nodes first
     */
    public ForceDirectedBuilder(AnimationType animationType, boolean drawInitialRandomGraph) {
        this.animationType = animationType;
        this.drawInitialGraph = drawInitialRandomGraph;
    }

    /**
     * Build a graph using the force-directed algorithm. This method will only execute if the previous build has
     * completed, this includes animated builds.
     * @param graph the {@link Graph graph} that is being built
     * @param nodes the {@link DrawableNode nodes} that exist on the graph
     * @param edges the {@link DrawableEdge edges} that exist on the graph
     */
    @Override
    public void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {
        if (!canBuild) return;
        canBuild = false;
        System.out.println("Placing nodes.");

        if (drawInitialGraph) buildInitialGraph(graph, nodes, edges);

        System.out.println("Applying forces.");

        if (animationType.equals(AnimationType.FULL_ANIMATION)) {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(FRAME_DURATION),
                    new FullFrame(graph, nodes, timeline)));
            timeline.setCycleCount(MAX_ITERATIONS);
            timeline.play();
        }

        else {
            ArrayList<Point> startSnapshot = null;
            if (animationType.equals(AnimationType.SLIDE_TO_END)) {
                startSnapshot = graph.getNodePositionSnapshot();
                graph.freezeCanvas();
            }

            int t = 0;
            while (t++ < MAX_ITERATIONS && applyForces(graph, nodes, t) > EPSILON) {}
            System.out.println("Forces applied.");

            if (animationType.equals(AnimationType.SLIDE_TO_END)) {
                System.out.println("Sliding nodes.");
                ArrayList<Point> endSnapshot = graph.getNodePositionSnapshot();
                graph.returnNodesToSnapshot(startSnapshot);
                graph.unfreezeCanvas();
                slideNodesTo(endSnapshot, nodes);
            } else stoppedRunning();
        }

    }

    private void stoppedRunning() {
        canBuild = true;
        if (endAction != null) endAction.handle();
    }

    /**
     * @return true if an animation or build is currently running, false otherwise
     */
    public boolean isRunning() {
        return !canBuild;
    }

    /**
     * Set the event handler for when the algorithm/animation finishes.
     * @param endAction the {@link EndAction} to be stored
     */
    public void setEndAction(EndAction endAction) {
        this.endAction = endAction;
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

    private ArrayList<Point> calcForces(Graph graph, ArrayList<DrawableNode> nodes, int iteration) {
        ArrayList<Point> forces = new ArrayList<>();

        for (DrawableNode node : nodes) {
            Point forceOnNode = new Point(0, 0);
            for (DrawableNode compareNode : nodes) {
                if (!node.equals(compareNode)) {
                    Point force = calcForce(node, compareNode, graph.areConnected(node, compareNode), iteration);
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
        force = force.add(calcRepulsionBetween(top, centre, SIDE_REPULSION_CONSTANT));
        force = force.add(calcRepulsionBetween(bottom, centre, SIDE_REPULSION_CONSTANT));
        force = force.add(calcRepulsionBetween(left, centre, SIDE_REPULSION_CONSTANT));
        force = force.add(calcRepulsionBetween(right, centre, SIDE_REPULSION_CONSTANT));

        return force;
    }

    private Point calcRepulsionBetween(Point start, Point end, double repulsionConstant) {
        double distance = start.distanceTo(end);
        return start.getVectorTo(end).normalize().multiply(repulsionConstant/(distance*distance));
    }

    private double applyForces(Graph graph, ArrayList<DrawableNode> nodes, int iteration) {
        ArrayList<Point> forces = calcForces(graph, nodes, iteration);

        double maxMove = 0;
        for (int nodeID = 0; nodeID < forces.size(); nodeID++) {
            double amountMoved = moveNode(nodes.get(nodeID), forces.get(nodeID).multiply(calcCooling(iteration)));
            if (amountMoved > maxMove) maxMove = amountMoved;
        }

        return maxMove;
    }

    private Point createRandomForce(int iteration) {
        // todo: instead of this it might be an idea to add epsilon to 0s to prevent dividing by 0 instead of generating random values
        return new Point((random.nextInt(2) * 2 - 1) * NODE_COLLISION_FORCE /iteration, (random.nextDouble(2) * 2 - 1) * NODE_COLLISION_FORCE /iteration);
    }

    private Point calcRepulsion(DrawableNode node1, DrawableNode node2, int iteration) {
        if (needsSavedFromCollision(node1, node2)) return createRandomForce(iteration);
        return calcRepulsionBetween(node2.getCentre(), node1.getCentre(), REPULSION_CONSTANT);
    }

    private boolean needsSavedFromCollision(DrawableNode node1, DrawableNode node2) {
        return node1.getCentre().equals(node2.getCentre());
    }

    private Point calcSpring(DrawableNode node1, DrawableNode node2, int iteration) {
        if (needsSavedFromCollision(node1, node2)) return createRandomForce(iteration);
        Point start = node1.getCentre();
        Point end = node2.getCentre();

        double distance = end.distanceTo(start) / IDEAL_EDGE_LENGTH;
        double logDistance = Math.log(distance);


        return start.getVectorTo(end).normalize().multiply(SPRING_CONSTANT * logDistance);
    }

    private Point calcForce(DrawableNode node1, DrawableNode node2, boolean connected, int iteration) {
        if (connected) return calcSpring(node1, node2, iteration);
        return calcRepulsion(node1, node2, iteration);
    }

    private double calcCooling(double iteration) {
        return Math.pow(COOLING, iteration);
    }

    private double moveNode(DrawableNode node, Point vector) {
        Point start = node.getCentre();
        node.moveWithinBoundsTo(start.add(vector));
        Point end = node.getCentre();
        return start.getVectorTo(end).magnitude();
    }

    private void slideNodesTo(ArrayList<Point> endPoints, ArrayList<DrawableNode> nodes) {
        new NodeSlider(nodes, endPoints, SLIDE_DURATION, () -> {
            System.out.println("Sliding complete.");
            stoppedRunning();
        }).start();
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

            if (t++ >= MAX_ITERATIONS || applyForces(graph, nodes, t) <= EPSILON) {
                System.out.println("Forces applied.");
                timeline.stop();
                graph.unfreezeCanvas();
                stoppedRunning();
            }

        }
    }
}
