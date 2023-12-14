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
        generateRandomCanvas(canvas, edgeMatrix, 0);
    }

    /**
     * Private recursive wrapper of {@link #generateRandomCanvas(Canvas, boolean[][])}
     */
    private static void generateRandomCanvas(Canvas canvas, boolean[][] edgeMatrix, int attempts) throws InvalidEdgeException, UndefinedNodeException {
        System.out.println("Generating canvas, attempt " + (attempts + 1));
        canvas.clear();

        boolean canIterate = true;
        for (int i = 0; i < edgeMatrix.length; i++) {
            int iterations = 0;
            while (canIterate && !canvas.createNode(i, generateCanvasPoint())) {
                if (iterations > 1000) {
                    System.out.println("Iterated too many times while trying to position node " + i + ", no longer repositioning any nodes.");
                    canIterate = false;
                }
                iterations++;
            }
        }

        for (int node1 = 0; node1 < edgeMatrix.length; node1++) {
            for (int node2 = 0; node2 < edgeMatrix.length; node2++) {
                if (edgeMatrix[node1][node2]) {
                    System.out.println("Creating edge between " + node1 + " and " + node2);
                    if (!canvas.createEdge(node1, node2, true)) {
                        // todo: instead this could make automatic adjustments to the parameters, once click and drag
                        //  feature has been made - or could be best to just display "clean graph could not be found"
                        if (attempts > 1000) System.out.println("Attempted over 1000 graphs, finalising with invalid edge.");
                        else {
                            generateRandomCanvas(canvas, edgeMatrix, ++attempts);
                            return;
                        }
                    }
                }
            }
        }

        canvas.draw();
    }

}
