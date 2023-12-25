package graphvisualisation.data.graph.elements;

public class WeightedEdge extends Edge {
    private final String value;

    public WeightedEdge(Node node1, Node node2, boolean directed, String value) {
        super(node1, node2, directed);
        this.value = value;
    }

    public String value() {
        return value;
    }
}
