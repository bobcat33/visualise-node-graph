package visualiser.data.elements;

import visualiser.graphics.objects.exceptions.InvalidEdgeException;

public class WeightedEdge extends Edge {
    private final String value;

    /**
     * Create a weighted version of an {@link Edge} between two nodes.
     * @param startNode the node at the start of the edge
     * @param endNode the node at the end of the edge
     * @param directed true if the edge is directed, false otherwise
     * @param value the value of the weighted edge
     * @throws InvalidEdgeException if the nodes are equal (an edge cannot be made from one node to itself)
     * or if either node is null
     * @see Edge
     */
    public WeightedEdge(Node startNode, Node endNode, boolean directed, String value) throws InvalidEdgeException {
        super(startNode, endNode, directed);
        this.value = value;
    }

    /**
     * @return the value of the weighted edge
     */
    public String value() {
        return value;
    }
}
