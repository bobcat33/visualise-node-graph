package visualiser.data.graphdata;

import visualiser.data.graphdata.elements.Edge;
import visualiser.data.graphdata.elements.Node;

import java.util.ArrayList;

public class GraphData {
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;

    /**
     * Create a GraphData object using the graph's {@link Edge edges}. Nodes are automatically loaded from the edges.
     * @param edges the edges of the graph
     */
    public GraphData(ArrayList<Edge> edges) {
        this(null, edges);
    }

    /**
     * Create a GraphData object using the graph's {@link Edge edges} and {@link Node nodes}. This allows for isolated
     * nodes to exist. Any node in {@code nodes} that is not part of an edge in {@code edges} will be created as an
     * isolated node. The {@code nodes} parameter can contain any number of nodes, any nodes that exist in the edges
     * but not in nodes will be automatically added to the internal array.
     * @param nodes any nodes in the graph, any isolated nodes
     * @param edges the edges in the graph
     */
    public GraphData(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        if (edges == null) throw new InvalidDataException();
        if (nodes == null) this.nodes = new ArrayList<>();
        else this.nodes = nodes;
        getNodesFrom(edges);

        this.edges = edges;
    }

    /**
     * @return the nodes in the graph data, including all isolated nodes and nodes on edges
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * @return the edges in the graph data
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    /**
     * Load all nodes that have not already been loaded from the edges.
     * @param edges the edges to search for unloaded nodes
     */
    private void getNodesFrom(ArrayList<Edge> edges) {
        for (Edge edge : edges) {
            if (!nodes.contains(edge.startNode())) nodes.add(edge.startNode());
            if (!nodes.contains(edge.endNode())) nodes.add(edge.endNode());
        }
    }
}