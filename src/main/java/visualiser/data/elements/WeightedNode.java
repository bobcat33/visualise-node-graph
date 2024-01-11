package visualiser.data.elements;

public class WeightedNode extends Node {
    private final String value;

    /**
     * Create a {@link Node} with a weighted value
     * @param id the unique ID of the node
     * @param name the name/label of the node
     * @param value the value of the weight of the node
     * @see Node
     */
    public WeightedNode(int id, String name, String value) {
        super(id, name);
        this.value = value;
    }

    /**
     * @return the value of the weight of the node
     */
    public String value() {
        return value;
    }

}
