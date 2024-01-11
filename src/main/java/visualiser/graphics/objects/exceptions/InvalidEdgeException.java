package visualiser.graphics.objects.exceptions;

import visualiser.data.elements.Node;
import visualiser.graphics.objects.DrawableNode;

public class InvalidEdgeException extends RuntimeException {

    public InvalidEdgeException() {
        super("An edge was impossible.");
    }

    public InvalidEdgeException(Node node1, Node node2) {

        super("The edge between nodes '" + node1.toString() + "' and '" + node2.toString() + "' is impossible.");

    }

    public InvalidEdgeException(DrawableNode node1, DrawableNode node2) {

        super("The edge between nodes " + node1.id() + " and " + node2.id() + " is impossible.");

    }

}
