package graphvisualisation.graphics.canvas;

import graphvisualisation.graphics.objects.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

public class Canvas extends Parent {
    private boolean frozen = false;
    private final ArrayList<Node> frozenNodes = new ArrayList<>();

    public void setFrozen(boolean frozen) {
        if (frozen) freeze();
        else unfreeze();
    }

    public void freeze() {
        if (frozen) return;
        frozen = true;
        frozenNodes.clear();
        // Store the actual objects of the frozen nodes so that they can be re-added to the canvas when unfrozen
        frozenNodes.addAll(getChildren());
        // Remove all objects from the canvas
        clear();

        // Store a copy of each DrawableNode on the canvas and draw them
        ArrayList<DrawableNode> copiedNodes = new ArrayList<>();
        ArrayList<DrawableEdge> existingEdges = new ArrayList<>();
        for (Node frozenNode : frozenNodes) {
            if (frozenNode instanceof DrawableNode node) {
                DrawableNode copiedNode = node.createCopy();
                copiedNodes.add(copiedNode);
                draw(copiedNode);
            }
            else if (frozenNode instanceof DrawableEdge edge) existingEdges.add(edge);
        }

        // For every existing edge, find its copied nodes and create a copy of the edge connecting the copied nodes
        for (DrawableEdge existingEdge : existingEdges) {
            DrawableNode startNode = null;
            DrawableNode endNode = null;
            for (DrawableNode copiedNode : copiedNodes) {
                if (copiedNode.id() == existingEdge.startNode().id()) startNode = copiedNode;
                else if (copiedNode.id() == existingEdge.endNode().id()) endNode = copiedNode;
            }

            // Draw the copied edge
            draw(existingEdge.createCopyWith(startNode, endNode));
        }
    }

    public void unfreeze() {
        if (!frozen) return;
        frozen = false;
        clear();

        getChildren().addAll(frozenNodes);
        resetZIndex();
        frozenNodes.clear();
    }

    public void clear() {
        getChildren().removeIf(node -> !(node instanceof Dot));
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
        ArrayList<WeightedDrawableNode.Weight> nodeWeights = new ArrayList<>();
        ArrayList<WeightedDrawableEdge.Weight> edgeWeights = new ArrayList<>();

        for (Node child : getChildren()) {
            if (child instanceof DrawableNode node) {
                nodes.add(node);
            }
            if (child instanceof DrawableEdge edge) {
                edges.add(edge);
            }
            if (child instanceof WeightedDrawableNode.Weight weight) {
                nodeWeights.add(weight);
            }
            if (child instanceof WeightedDrawableEdge.Weight weight) {
                edgeWeights.add(weight);
            }
        }

        clear();
        for (DrawableNode node : nodes) {
            getChildren().add(node);
        }
        for (DrawableEdge edge : edges) {
            getChildren().add(edge);
        }
        for (WeightedDrawableNode.Weight weight : nodeWeights) {
            getChildren().add(weight);
        }
        for (WeightedDrawableEdge.Weight weight : edgeWeights) {
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
