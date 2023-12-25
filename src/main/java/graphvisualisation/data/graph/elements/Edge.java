package graphvisualisation.data.graph.elements;

public class Edge {

    private final Node node1;
    private final Node node2;
    private final boolean directed;

    public Edge(Node node1, Node node2, boolean directed) {

        this.node1 = node1;
        this.node2 = node2;
        this.directed = directed;

    }

    public Node startNode() {
        return node1;
    }

    public Node endNode() {
        return node2;
    }

    public boolean directed() {
        return directed;
    }

}
