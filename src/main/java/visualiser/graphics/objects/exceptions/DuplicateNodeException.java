package visualiser.graphics.objects.exceptions;

import visualiser.data.elements.Node;

public class DuplicateNodeException extends RuntimeException {
    public DuplicateNodeException(Node node) {
        super("The node '" + node.toString() + "' already exists.");
    }
}
