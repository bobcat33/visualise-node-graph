package graphvisualisation.graphics.objects.exceptions;

import graphvisualisation.data.graphdata.elements.Node;

public class DuplicateNodeException extends RuntimeException {
    public DuplicateNodeException(Node node) {
        super("The node '" + node.toString() + "' already exists.");
    }
}
