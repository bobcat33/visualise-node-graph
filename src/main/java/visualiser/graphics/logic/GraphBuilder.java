package visualiser.graphics.logic;

import visualiser.graphics.Graph;
import visualiser.graphics.objects.DrawableEdge;
import visualiser.graphics.objects.DrawableNode;

import java.util.ArrayList;

public interface GraphBuilder {
    void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges);
}
