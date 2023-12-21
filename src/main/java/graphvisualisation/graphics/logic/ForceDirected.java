package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

public class ForceDirected implements CanvasDrawer {
    @Override
    public void drawTo(Canvas canvas, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException {
        drawTo(canvas, matrix, false);
    }

    @Override
    public void drawTo(Canvas canvas, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException {

    }
}
