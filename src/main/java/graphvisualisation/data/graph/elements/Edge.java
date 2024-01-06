package graphvisualisation.data.graph.elements;

import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;

public class Edge {

    private final Node startNode;
    private final Node endNode;
    private final boolean directed;

    public Edge(Node startNode, Node endNode, boolean directed) throws InvalidEdgeException {
        // Ensure that the nodes are different
        if (startNode.equals(endNode))
            throw new InvalidEdgeException(startNode, endNode);

        this.startNode = startNode;
        this.endNode = endNode;
        this.directed = directed;
    }

    public Node startNode() {
        return startNode;
    }

    public Node endNode() {
        return endNode;
    }

    public boolean directed() {
        return directed;
    }
}
