package graphvisualisation.graphics.logic;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.graphics.graphing.Graph;
import graphvisualisation.graphics.objects.DrawableEdge;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

import java.util.ArrayList;

public interface GraphBuilder {
    void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) throws InvalidEdgeException, UndefinedNodeException;
}
