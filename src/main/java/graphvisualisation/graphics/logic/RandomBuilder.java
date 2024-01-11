package graphvisualisation.graphics.logic;

import graphvisualisation.graphics.Graph;
import graphvisualisation.graphics.objects.DrawableEdge;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

import java.util.ArrayList;

public class RandomBuilder implements GraphBuilder {

    /**
     * Clears a canvas and populates it with randomly positioned nodes.
     *
     * @param graph
     */
    @Override
    public void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) throws UndefinedNodeException {
        int attempts = 0;
        boolean edgesValid = false;
        int attemptLimit = 4000;
        int maxNodeMovements = 1000;

        while (!edgesValid && attempts < attemptLimit) {
            System.out.println("Generating canvas, attempt " + (attempts + 1));
            graph.clearCanvas();

            graph.resizeNodes(true, false);
            for (DrawableNode node : nodes) node.moveTo(graph.generatePoint());

            // Generate or reposition the nodes to find suitable locations. If, after 1000 attempts, a node could not
            // be properly positioned it is easiest to assume that there is no valid position to move any new nodes to.
            boolean canIterate = true;
            int iterations;
            for (DrawableNode node : nodes) {
                // canIterate is checked afterwards so that the nodes are still positioned randomly even if they are
                // no longer being adjusted
                for (iterations = 0;
                     iterations <= maxNodeMovements && !graph.isValidNode(node) && canIterate;
                     ++iterations) {
                    node.moveTo(graph.generatePoint());
                    if (iterations == maxNodeMovements) {
                        System.out.println("Iterated too many times while trying to position node " + node + ", no longer repositioning any nodes.");
                        canIterate = false;
                    }
                }
            }

            edgesValid = true;
            for (DrawableEdge edge : edges) {
                System.out.println("Creating edge between " + edge.startNode().toString() + " and " + edge.endNode().toString());
                if (graph.intersectsAnyNode(edge)) {
                    edgesValid = false;
                    break;
                }
            }
            attempts++;
        }

        if (edgesValid) System.out.println("Valid graph found after " + attempts + " attempts.");
        else System.out.println("No valid graph was found within the limit of " + attemptLimit + " attempts.");
        graph.draw();
    }
}
