package graphvisualisation.graphics.objects.exceptions;

import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.graphics.objects.DrawableNode;

public class InvalidEdgeException extends Exception {

    public InvalidEdgeException(Node node1, Node node2) {

        super("The edge between nodes '" + node1.toString() + "' and '" + node2.toString() + "' is impossible.");

    }

    public InvalidEdgeException(DrawableNode node1, DrawableNode node2) {

        super("The edge between nodes " + node1.id() + " and " + node2.id() + " is impossible.");

    }

}
