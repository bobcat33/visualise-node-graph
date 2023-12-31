package graphvisualisation.data.graph.elements;

public class WeightedNode extends Node {
    private final String value;

    public WeightedNode(int id, String name, String value) {
        super(id, name);
        this.value = value;
    }

    public String value() {
        return value;
    }

}
