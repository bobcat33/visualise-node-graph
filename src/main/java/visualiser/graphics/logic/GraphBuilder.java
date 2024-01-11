package visualiser.graphics.logic;

import visualiser.graphics.Graph;
import visualiser.graphics.objects.DrawableEdge;
import visualiser.graphics.objects.DrawableNode;

import java.util.ArrayList;

public interface GraphBuilder {
    /**
     * Build a {@link Graph graph} by manipulating the {@link DrawableNode nodes} and {@link DrawableEdge edges} within.
     * @param graph the {@link Graph graph} that is being built
     * @param nodes the {@link DrawableNode nodes} that exist on the graph
     * @param edges the {@link DrawableEdge edges} that exist on the graph
     */
    void build(Graph graph, ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges);
}
