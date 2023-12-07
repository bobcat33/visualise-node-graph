package graphvisualisation.graphics.nodes;

public class UndefinedNodeException extends Exception {

    public UndefinedNodeException(DrawableNode node) {
        super("The node with ID '" + node.getNodeID() + "' has not been defined with DrawableNode#draw()" +
                "and so cannot be accessed.");
    }

}
