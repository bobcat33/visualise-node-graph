package visualiser.data.graphdata.elements;

import visualiser.data.graphdata.InvalidDataException;

public class Node {

    private final int id;
    private final String name;

    /**
     * Create a node with an ID and a name, node IDs should be unique.
     * @param id the unique ID of the node
     * @param name the name/label of the node
     * @see WeightedNode
     */
    public Node(int id, String name) {
        if (id < 0) throw new InvalidDataException("Node ID cannot be less than 0");
        this.id = id;
        this.name = name;
    }

    /**
     * @return the unique ID of the node
     */
    public int id() {
        return id;
    }

    /**
     * @return the name/label of the node
     */
    public String name() {
        return name;
    }

    /**
     * @return "id:name"
     */
    @Override
    public String toString() {
        return id + ":" + name;
    }

    /**
     * Returns true if and only if the object is not null, is a {@code Node} object, and has the same ID as this node.
     * @param o the object to compare this node against
     * @return true if the nodes have the same ID
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Node node)) return false;
        return id == node.id;
    }
}
