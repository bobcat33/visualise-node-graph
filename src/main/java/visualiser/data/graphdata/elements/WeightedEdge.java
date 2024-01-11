package visualiser.data.graphdata.elements;

import visualiser.graphics.objects.exceptions.InvalidEdgeException;

public class WeightedEdge extends Edge {
    private final String value;

    public WeightedEdge(Node node1, Node node2, boolean directed, String value) throws InvalidEdgeException {
        super(node1, node2, directed);
        this.value = value;
    }

    public String value() {
        return value;
    }
}
