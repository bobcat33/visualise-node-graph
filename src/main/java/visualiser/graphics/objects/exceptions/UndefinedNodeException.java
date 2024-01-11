package visualiser.graphics.objects.exceptions;

import visualiser.graphics.objects.DrawableNode;

public class UndefinedNodeException extends RuntimeException {

    public UndefinedNodeException() {
        this(null);
    }

    public UndefinedNodeException(DrawableNode node) {
        super((
                (node == null)
                        ? "A node was used before it was initialised."
                        : "The node with ID '" + node.id() + "' has either not been defined or has been incorrectly " +
                        "modified and so cannot be accessed."
                )
        );
    }

    public UndefinedNodeException(int nodeID) {
        super("The node with ID '" + nodeID + "' has either not been defined or has been incorrectly " +
                "modified and so cannot be accessed.");
    }

}
