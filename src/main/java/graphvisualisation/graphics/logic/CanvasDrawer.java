package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.graphics.canvas.Canvas;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

public interface CanvasDrawer {
    void drawTo(Canvas canvas, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException;
    void drawTo(Canvas canvas, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException;
}
