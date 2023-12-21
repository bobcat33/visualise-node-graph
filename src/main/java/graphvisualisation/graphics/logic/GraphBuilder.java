package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

public interface GraphBuilder {
    void build(Graph graph, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException;
    void build(Graph graph, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException;
}
