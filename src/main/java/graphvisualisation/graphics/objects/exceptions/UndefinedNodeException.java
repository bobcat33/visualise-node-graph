package graphvisualisation.graphics.objects.exceptions;

import graphvisualisation.graphics.objects.DrawableNode;

public class UndefinedNodeException extends Exception {

    public UndefinedNodeException(DrawableNode node) {
        super((
                (node == null)
                        ? "A node was used before it was initialised."
                        : "The node with ID '" + node.getNodeID() + "' has either not been defined or has been incorrectly " +
                        "modified and so cannot be accessed."
                )
        );
    }

}
