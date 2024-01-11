package graphvisualisation.graphics.logic;

import graphvisualisation.graphics.Graph;
import graphvisualisation.graphics.objects.DrawableEdge;
import graphvisualisation.graphics.objects.DrawableNode;

import java.util.ArrayList;

public interface GraphBuilder {
    void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges);
}
