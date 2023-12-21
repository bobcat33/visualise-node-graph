package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

public class ForceDirectedBuilder implements GraphBuilder {
    @Override
    public void build(Graph graph, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException {
        build(graph, matrix, false);
    }

    @Override
    public void build(Graph graph, Matrix matrix, boolean uniformNodeSize) throws InvalidEdgeException, UndefinedNodeException {

    }
}
