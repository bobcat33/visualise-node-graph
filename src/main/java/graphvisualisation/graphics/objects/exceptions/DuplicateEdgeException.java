package graphvisualisation.graphics.objects.exceptions;

import graphvisualisation.data.graphdata.elements.Edge;

public class DuplicateEdgeException extends RuntimeException {
    public DuplicateEdgeException(Edge edge) {
        super("The edge between nodes '" + edge.startNode().toString() + "' and '" + edge.endNode().toString() + "' " +
                "already exists.");
    }
}
