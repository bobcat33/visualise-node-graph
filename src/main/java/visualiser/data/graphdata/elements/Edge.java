package visualiser.data.graphdata.elements;

import visualiser.graphics.objects.exceptions.InvalidEdgeException;

public class Edge {

    private final Node startNode;
    private final Node endNode;
    private final boolean directed;

    /**
     * Create an Edge between two Nodes.
     * @param startNode the node at the start of the edge
     * @param endNode the node at the end of the edge
     * @param directed true if the edge is directed, false otherwise
     * @throws InvalidEdgeException if the nodes are equal (an edge cannot be made from one node to itself)
     * or if either node is null
     * @see WeightedEdge
     */
    public Edge(Node startNode, Node endNode, boolean directed) throws InvalidEdgeException {
        if (startNode == null || endNode == null) throw new InvalidEdgeException();
        // Ensure that the nodes are different
        if (startNode.equals(endNode))
            throw new InvalidEdgeException(startNode, endNode);

        this.startNode = startNode;
        this.endNode = endNode;
        this.directed = directed;
    }

    /**
     * @return the Node at the start of the edge
     */
    public Node startNode() {
        return startNode;
    }

    /**
     * @return the Node at the end of the edge
     */
    public Node endNode() {
        return endNode;
    }

    /**
     * @return true if the edge is directed, false otherwise
     */
    public boolean directed() {
        return directed;
    }

    /**
     * Compares this edge with the specified object. The argument must not be null and must be an {@code Edge} object.
     * To return true they must both have same edge type (directed/undirected) and if directed then the start and
     * end nodes must be the same, if undirected then the start and end nodes must either be the same or opposing.
     * <br/>The rules are as follows:
     * <ul>
     *     <li>a -> b == a -> b&nbsp; = &nbsp;true</li>
     *     <li>a -> b == b -> a&nbsp; = &nbsp;false</li>
     *     <li>a -> b == a --- b&nbsp; = &nbsp;false</li>
     *     <li>a --- b == a --- b&nbsp; = &nbsp;true</li>
     *     <li>a --- b == b --- a&nbsp; = &nbsp;true</li>
     * </ul>
     * Where -> denotes a directed edge (left to right) and --- denotes an undirected edge.
     * @param o the object to compare this edge against
     * @return true if the given object represents an {@code Edge} equivalent to this edge, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Edge edge)) return false;
        boolean sameEdgeType = directed == edge.directed;
        boolean sameStartEnd = startNode.equals(edge.startNode) && endNode.equals(edge.endNode);
        boolean oppositeStartEnd = startNode.equals(edge.endNode) && endNode.equals(edge.startNode);
        boolean directedSameNodes = directed && edge.directed && sameStartEnd;
        boolean nonDirectedSameNodes = !directed && !edge.directed
                && (sameStartEnd || oppositeStartEnd);

        return sameEdgeType && (directedSameNodes || nonDirectedSameNodes);
    }
}
