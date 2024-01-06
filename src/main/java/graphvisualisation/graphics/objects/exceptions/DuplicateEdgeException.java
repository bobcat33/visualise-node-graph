package graphvisualisation.graphics.objects.exceptions;

import graphvisualisation.data.graph.elements.Edge;

public class DuplicateEdgeException extends Exception {
    public DuplicateEdgeException(Edge edge) {
        super("The edge between nodes '" + edge.startNode().toString() + "' and '" + edge.endNode().toString() + "' " +
                "already exists.");
    }
}
