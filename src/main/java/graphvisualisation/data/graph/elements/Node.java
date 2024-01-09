package graphvisualisation.data.graph.elements;

public class Node {

    private final int id;
    private final String name;

    public Node(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return id + ":" + name;
    }
}
