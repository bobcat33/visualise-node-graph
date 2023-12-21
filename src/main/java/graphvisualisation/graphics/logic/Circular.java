package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @deprecated this has been abandoned and is to be replaced by {@link ForceDirected}
 */
public class Circular implements CanvasDrawer {
    // THIS HAS BEEN ABANDONED AND TO BE REPLACED (HOPEFULLY) BY FORCE DIRECTED

    @Override
    public void drawTo(Canvas canvas, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException {
        drawTo(canvas, matrix, false);
    }

    @Override
    public void drawTo(Canvas canvas, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException {
        boolean[][] edgeMatrix = matrix.getEdgeMatrix();

        ArrayList<Integer> nodes = new ArrayList<>();
        HashMap<Integer, Integer> numConnectionsPerNode = new HashMap<>();

        for (int node = 0; node < edgeMatrix.length; node++) {
            nodes.add(node);
            numConnectionsPerNode.put(node, matrix.getConnectedNodes(node).length);
        }

        // Ensure the nodes are sorted
        Collections.sort(nodes);

        // needs to sort by how many connections per node here

        System.out.println(nodes);

        // If the algorithm failed to find a valid graph then attempt to create using the random method
        System.out.println("WARN: No graph could be find using the circular method - reverting to randomization.");
        new Randomised().drawTo(canvas, matrix, uniformNodeSize);
    }
}
