package visualiser.graphics.objects.exceptions;

import visualiser.data.elements.Edge;

public class DuplicateEdgeException extends RuntimeException {
    public DuplicateEdgeException(Edge edge) {
        super("The edge between nodes '" + edge.startNode().toString() + "' and '" + edge.endNode().toString() + "' " +
                "already exists.");
    }
}
