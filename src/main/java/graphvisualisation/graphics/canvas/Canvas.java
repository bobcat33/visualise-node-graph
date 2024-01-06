package graphvisualisation.graphics.canvas;

import graphvisualisation.graphics.objects.DrawableEdge;
import graphvisualisation.graphics.objects.DrawableNode;
import graphvisualisation.graphics.objects.WeightedDrawableEdge;
import graphvisualisation.graphics.objects.WeightedDrawableNode;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

public class Canvas extends Parent {

    public void clear() {
        getChildren().clear();
    }

    public boolean exists(DrawableNode node) {
        for (Node child : getChildren())
            if (child instanceof DrawableNode existingNode)
                if (existingNode.equals(node)) return true;
        return false;
    }

    public boolean exists(DrawableEdge edge) {
        for (Node node : getChildren())
            if (node instanceof DrawableEdge existingEdge)
                if (existingEdge.equals(edge)) return true;
        return false;
    }

    public void resetZIndex() {

        ArrayList<DrawableNode> nodes = new ArrayList<>();
        ArrayList<DrawableEdge> edges = new ArrayList<>();
        ArrayList<WeightedDrawableNode.Weight> weights = new ArrayList<>();

        for (Node child : getChildren()) {
            if (child instanceof DrawableNode node) {
                nodes.add(node);
            }
            if (child instanceof DrawableEdge edge) {
                edges.add(edge);
            }
            if (child instanceof WeightedDrawableNode.Weight weight) {
                weights.add(weight);
            }
        }

        getChildren().clear();
        for (DrawableNode node : nodes) {
            getChildren().add(node);
        }
        for (DrawableEdge edge : edges) {
            getChildren().add(edge);
        }
        for (WeightedDrawableNode.Weight weight : weights) {
            getChildren().add(weight);
        }
    }

    public boolean draw(DrawableNode node) {
        boolean nodeExists = exists(node);
        if (!nodeExists) {
            getChildren().add(node);
            if (node instanceof WeightedDrawableNode weightedNode) getChildren().add(weightedNode.getWeight());
        }
        resetZIndex();
        return !nodeExists;
    }

    public void drawNodes(ArrayList<DrawableNode> nodes) {
        for (DrawableNode node : nodes) draw(node);
    }

    public boolean draw(DrawableEdge edge) {
        boolean edgeExists = exists(edge);
        if (!edgeExists) {
            getChildren().add(edge);
            if (edge instanceof WeightedDrawableEdge weightedEdge) getChildren().add(weightedEdge.getWeight());
        }
        resetZIndex();
        return !edgeExists;
    }

    public void drawEdges(ArrayList<DrawableEdge> edges) {
        for (DrawableEdge edge : edges) draw(edge);
    }

    public void draw(ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {
        drawNodes(nodes);
        drawEdges(edges);
    }

    public void draw(WeightedDrawableNode.Weight weight) {
        getChildren().add(weight);
        resetZIndex();
    }

    public void remove(DrawableNode node) {
        while (getChildren().remove(node));
        while ((node instanceof WeightedDrawableNode weightedNode) && getChildren().remove(weightedNode));
    }

    public void remove(DrawableNode[] nodes) {
        for (DrawableNode node : nodes)
            remove(node);
    }

    public void remove(DrawableEdge edge) {
        while (getChildren().remove(edge));
        while ((edge instanceof WeightedDrawableEdge weightedEdge) && getChildren().remove(weightedEdge));
    }

    public void remove(DrawableEdge[] edges) {
        for (DrawableEdge edge : edges)
            remove(edge);
    }

    /**
     * @deprecated used for testing
     */
    public void draw(Shape shape) {
        getChildren().add(shape);
    }
}
