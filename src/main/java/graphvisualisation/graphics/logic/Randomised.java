package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.DiMatrix;
import graphvisualisation.data.graph.Matrix;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

public class Randomised implements CanvasDrawer {

    /**
     * Clears a canvas and populates it with randomly positioned nodes.
     * @param canvas tbe canvas to be drawn to
     * @param edgeMatrix a directed graph matrix of the nodes and their edges
     */
    @Override
    public void drawTo(Canvas canvas, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException {
        drawTo(canvas, matrix, false);
    }

    /**
     * Clears a canvas and populates it with randomly positioned nodes.
     * @param canvas tbe canvas to be drawn to
     * @param edgeMatrix a directed graph matrix of the nodes and their edges
     * @param uniformNodeSize if true all nodes will have the same size equal to the largest node, if false all nodes
     *                        are sized individually based on the width of their ID text.
     */
    @Override
    public void drawTo(Canvas canvas, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException {
        boolean[][] edgeMatrix = matrix.getEdgeMatrix();
        boolean isDirectional = matrix instanceof DiMatrix;
        int attempts = 0;
        boolean edgesValid = false;
        int attemptLimit = 4000;
        int maxNodeMovements = 1000;

        while (!edgesValid && attempts < attemptLimit) {
            System.out.println("Generating canvas, attempt " + (attempts + 1));
            canvas.clear();

            // If all nodes must be created with uniform size, create them all first. Later they will just be moved
            // rather than recreated.
            if (uniformNodeSize) {
                for (int i = 0; i < edgeMatrix.length; i++) {
                    canvas.createNode(i);
                }
                canvas.resizeNodes(true, false);
            }

            // Generate or reposition the nodes to find suitable locations. If, after 1000 attempts, a node could not
            // be properly positioned it is easiest to assume that there is no valid position to move any new nodes to.
            boolean canIterate = true;
            int iterations;
            for (int i = 0; i < edgeMatrix.length; i++) {
                // canIterate is checked afterwards so that the nodes are still positioned randomly even if they are
                // no longer being adjusted
                for (iterations = 0;
                     iterations <= maxNodeMovements && !canvas.createNode(i, canvas.generatePoint()) && canIterate;
                     ++iterations) {
                    if (iterations == maxNodeMovements) {
                        System.out.println("Iterated too many times while trying to position node " + i + ", no longer repositioning any nodes.");
                        canIterate = false;
                    }
                }
            }

            edgesValid = true;
            for (int node1 = 0; node1 < edgeMatrix.length; node1++) {
                for (int node2 = 0; node2 < edgeMatrix.length; node2++) {
                    if (edgeMatrix[node1][node2]) {
                        System.out.println("Creating edge between " + node1 + " and " + node2);
                        if (!canvas.createEdge(node1, node2, isDirectional)) {
                            // todo: instead this could make automatic adjustments to the parameters, once click and drag
                            //  feature has been made - or could be best to just display "clean graph could not be found"
                            edgesValid = false;
                        }
                    }
                }
            }
            attempts++;
        }

        if (edgesValid) System.out.println("Valid graph found after " + attempts + " attempts.");
        else System.out.println("No valid graph was found within the limit of " + attemptLimit + " attempts.");
        canvas.draw();
    }
}
