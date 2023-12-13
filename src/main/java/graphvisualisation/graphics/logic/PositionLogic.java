package graphvisualisation.graphics.logic;

import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

import java.util.Random;

public class PositionLogic {

    private static final Random random = new Random();

    public static Point generateCanvasPoint() {
        return generateRandomPoint(0, 0, Canvas.WIDTH, Canvas.HEIGHT);
    }

    public static Point generateRandomPoint(double min, double max) {
        return generateRandomPoint(min, min, max, max);
    }

    public static Point generateRandomPoint(double minX, double minY, double maxX, double maxY) {
        return new Point(random.nextDouble(maxX - minX) + minX, random.nextDouble(maxY - minY) + minY);
    }


    /**
     * Clears a canvas and populates it with randomly positioned nodes.
     * @param canvas tbe canvas to be drawn to
     * @param edgeMatrix a directed graph matrix of the nodes and their edges
     */
    public static void generateRandomCanvas(Canvas canvas, boolean[][] edgeMatrix) throws InvalidEdgeException, UndefinedNodeException {
        canvas.clear();

        for (int i = 0; i < edgeMatrix.length; i++) {
            canvas.createNode(i, generateCanvasPoint());
        }

        for (int node1 = 0; node1 < edgeMatrix.length; node1++) {
            for (int node2 = 0; node2 < edgeMatrix.length; node2++) {
                if (edgeMatrix[node1][node2]) canvas.createEdge(node1, node2, true);
            }
        }

        canvas.draw();
    }

}
