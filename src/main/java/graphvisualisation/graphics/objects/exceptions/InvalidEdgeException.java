package graphvisualisation.graphics.objects.exceptions;

import graphvisualisation.graphics.objects.DrawableNode;

public class InvalidEdgeException extends Exception {

    public InvalidEdgeException(DrawableNode node1, DrawableNode node2) {

        super("The edge between nodes " + node1.getNodeID() + " and " + node2.getNodeID() + " is impossible.");

    }

}
