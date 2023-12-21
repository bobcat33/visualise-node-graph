package graphvisualisation.graphics.canvas;

import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.Edge;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;

public class Canvas extends Parent {

    private final double width, height;

    public Canvas(double width, double height) throws InvalidEdgeException, UndefinedNodeException {
        this.width = width;
        this.height = height;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public void clear() {
        getChildren().removeIf(canvasObject -> canvasObject instanceof DrawableNode || canvasObject instanceof Edge);
    }


    public boolean exists(DrawableNode node) {
        for (Node child : getChildren())
            if (child instanceof DrawableNode existingNode)
                if (existingNode.equals(node)) return true;
        return false;
    }

    public boolean exists(Edge edge) {
        for (Node node : getChildren())
            if (node instanceof Edge existingEdge)
                if (existingEdge.equals(edge)) return true;
        return false;
    }

    public boolean draw(DrawableNode node) {
        boolean nodeExists = exists(node);
        if (!nodeExists) getChildren().add(node);
        return !nodeExists;
    }

    public void drawNodes(ArrayList<DrawableNode> nodes) {
        for (DrawableNode node : nodes) draw(node);
    }

    public boolean draw(Edge edge) {
        boolean edgeExists = exists(edge);
        if (!edgeExists) getChildren().add(edge);
        return !edgeExists;
    }

    public void drawEdges(ArrayList<Edge> edges) {
        for (Edge edge : edges) draw(edge);
    }

    public boolean remove(DrawableNode node) {
        int numFound = 0;
        while (getChildren().remove(node)) {numFound++;}
        return numFound > 0;
    }

    public void remove(DrawableNode[] nodes) {
        for (DrawableNode node : nodes)
            remove(node);
    }

    public boolean remove(Edge edge) {
        int numFound = 0;
        while (getChildren().remove(edge)) {numFound++;}
        return numFound > 0;
    }

    public void remove(Edge[] edges) {
        for (Edge edge : edges)
            remove(edge);
    }
}
