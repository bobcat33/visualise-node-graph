package graphvisualisation.graphics.objects.exceptions;

import graphvisualisation.data.graph.elements.Node;

public class DuplicateNodeException extends Exception {
    public DuplicateNodeException(Node node) {
        super("The node '" + node.toString() + "' already exists.");
    }
}
