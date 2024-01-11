package visualiser.graphics.canvas;

import visualiser.graphics.objects.*;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;

public class Canvas extends Parent {
    private boolean frozen = false;
    private boolean freezing = false;
    private final ArrayList<Node> frozenNodes = new ArrayList<>();

    /**
     * Freeze the canvas if it is currently unfrozen, unfreeze if it is frozen.
     * @see #setFrozen(boolean)
     * @see #freeze()
     * @see #unfreeze()
     */
    public void toggleFrozen() {
        setFrozen(!frozen);
    }

    /**
     * @param frozen true to freeze the canvas, false to unfreeze
     * @see #toggleFrozen()
     * @see #freeze()
     * @see #unfreeze()
     */
    public void setFrozen(boolean frozen) {
        if (frozen) freeze();
        else unfreeze();
    }

    /**
     * Freeze the canvas. Store all elements currently on the canvas, create copies of every node, edge and weight.
     * Remove all elements from the canvas and add the copies. Cannot freeze if already frozen. Once frozen all
     * draw/remove method calls will apply only to the stored frozen nodes and will be drawn/removed on the canvas
     * when unfrozen. While frozen both {@link #exists(DrawableNode)} and {@link #exists(DrawableEdge)} will refer
     * to the stored frozen nodes and not the nodes shown on the canvas.
     * @see #toggleFrozen()
     * @see #setFrozen(boolean)
     * @see #unfreeze()
     */
    public void freeze() {
        if (frozen || freezing) return;
        freezing = true;
        frozenNodes.clear();
        // Store the actual objects of the frozen nodes so that they can be re-added to the canvas when unfrozen
        frozenNodes.addAll(getChildren());
        // Remove all objects from the canvas
        getChildren().clear();

        // Store a copy of each DrawableNode on the canvas and draw them
        ArrayList<DrawableNode> copiedNodes = new ArrayList<>();
        ArrayList<DrawableEdge> existingEdges = new ArrayList<>();
        for (Node frozenNode : frozenNodes) {
            if (frozenNode instanceof DrawableNode node) {
                DrawableNode copiedNode = node.createCopy();
                copiedNodes.add(copiedNode);
                draw(copiedNode, true);
            }
            else if (frozenNode instanceof DrawableEdge edge) existingEdges.add(edge);
        }

        // For every existing edge, find its copied nodes and create a copy of the edge connecting the copied nodes
        for (DrawableEdge existingEdge : existingEdges) {
            // Draw the copied edge
            draw(existingEdge.createCopyWith(copiedNodes), true);
        }
        frozen = true;
        freezing = false;
    }

    /**
     * Unfreeze the canvas, all copied elements are removed and replaced by the stored elements.
     * @see #toggleFrozen()
     * @see #setFrozen(boolean)
     * @see #freeze()
     */
    public void unfreeze() {
        if (!frozen || freezing) return;

        // Set up canvas before setting frozen to false
        getChildren().clear();
        getChildren().addAll(frozenNodes);
        frozenNodes.clear();
        resetZIndex();

        frozen = false;
    }

    /**
     * Remove all elements from the canvas.
     */
    public void clear() {
        if (frozen) frozenNodes.clear();
        else getChildren().clear();
    }

    /**
     * If frozen get the frozen elements, if unfrozen get a copy of the result of {@link #getChildren()}. Should not be
     * used to modify the canvas elements.
     * @return stored frozen elements if frozen, otherwise a copy of the result of {@link #getChildren()}
     */
    private ArrayList<Node> getCanvasElements() {
        if (frozen) return frozenNodes;
        return new ArrayList<>(getChildren());
    }

    /**
     * If a node exists in the canvas. Checks frozen elements instead of the canvas if the canvas is frozen.
     * @param node the node to search for
     * @return true if the node is in the canvas or, if the canvas is frozen, true if the node is in the
     * frozen elements
     */
    public boolean exists(DrawableNode node) {
        for (Node element : getCanvasElements())
            if (element instanceof DrawableNode existingNode)
                if (existingNode.equals(node)) return true;
        return false;
    }

    /**
     * If an edge exists in the canvas. Checks frozen elements instead of the canvas if the canvas is frozen.
     * @param edge the edge to search for
     * @return true if the edge is in the canvas or, if the canvas is frozen, true if the edge is in the
     * frozen elements
     */
    public boolean exists(DrawableEdge edge) {
        for (Node element : getCanvasElements())
            if (element instanceof DrawableEdge existingEdge)
                if (existingEdge.equals(edge)) return true;
        return false;
    }

    /**
     * Reset the order of all elements on the canvas, whether the canvas is frozen or not. The order of elements
     * from bottom to top is as follows:
     * <br/>nodes -> edges -> node weights -> edge weights
     */
    private void resetZIndex() {
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

        getChildren().clear();
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

    /**
     * Draw a node to the canvas. If the canvas is frozen, instead the node will be stored and drawn when the canvas is
     * unfrozen. If the node already exists then nothing will happen.
     * @param node the node to draw to the canvas
     * @return true if the node was successfully drawn to the canvas, false if the node already existed on the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     * @see #drawNodes(ArrayList)
     * @see #draw(ArrayList, ArrayList)
     */
    public boolean draw(DrawableNode node) {
        return draw(node, false);
    }

    /**
     * Draw a node to the canvas. If the canvas is frozen, instead the node will be stored and drawn when the canvas is
     * unfrozen. If the node already exists then nothing will happen.
     * @param node the node to draw to the canvas
     * @param force set to true to override the check for if {@link #freeze()} is still executing
     * @return true if the node was successfully drawn to the canvas, false if the node already existed on the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     * @see #draw(DrawableNode)
     */
    private boolean draw(DrawableNode node, boolean force) {
        if (freezing && !force)
            throw new ConcurrentFreezeActionException("draw node \"" + node.toString() + "\" to canvas");
        boolean nodeExists = exists(node);
        if (!nodeExists) {
            if (frozen) frozenNodes.add(node);
            else getChildren().add(node);
            if (node instanceof WeightedDrawableNode weightedNode) {
                if (frozen) frozenNodes.add(weightedNode.getWeight());
                else getChildren().add(weightedNode.getWeight());
            }
        }
        if (!frozen) resetZIndex();
        return !nodeExists;
    }

    /**
     * Draw multiple nodes to the canvas. If the canvas is frozen, instead the nodes will be stored and drawn when the
     * canvas is unfrozen. Only nodes that do not already exist on the canvas will be added.
     * @param nodes the nodes to draw to the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     * @see #draw(DrawableNode)
     * @see #draw(ArrayList, ArrayList)
     */
    public void drawNodes(ArrayList<DrawableNode> nodes) {
        for (DrawableNode node : nodes) draw(node);
    }

    /**
     * Draw an edge to the canvas. If the canvas is frozen, instead the edge will be stored and drawn when the canvas
     * is unfrozen. If the edge already exists then nothing will happen.
     * @param edge the edge to draw to the canvas
     * @return true if the edge was successfully drawn to the canvas, false if the edge already existed on the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     * @see #drawEdges(ArrayList)
     * @see #draw(ArrayList, ArrayList)
     */
    public boolean draw(DrawableEdge edge) {
        return draw(edge, false);
    }

    /**
     * Draw an edge to the canvas. If the canvas is frozen, instead the edge will be stored and drawn when the canvas
     * is unfrozen. If the edge already exists then nothing will happen.
     * @param edge the edge to draw to the canvas
     * @param force set to true to override the check for if {@link #freeze()} is still executing
     * @return true if the edge was successfully drawn to the canvas, false if the edge already existed on the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     * @see #draw(DrawableEdge)
     */
    private boolean draw(DrawableEdge edge, boolean force) {
        if (freezing && !force)
            throw new ConcurrentFreezeActionException("draw edge \"" + edge.toString() + "\" to canvas");
        boolean edgeExists = exists(edge);
        if (!edgeExists) {
            if (frozen) frozenNodes.add(edge);
            else getChildren().add(edge);
            if (edge instanceof WeightedDrawableEdge weightedEdge) {
                if (frozen) frozenNodes.add(weightedEdge.getWeight());
                else getChildren().add(weightedEdge.getWeight());
            }
        }
        if (!frozen) resetZIndex();
        return !edgeExists;
    }

    /**
     * Draw multiple edges to the canvas. If the canvas is frozen, instead the edges will be stored and drawn when the
     * canvas is unfrozen. Only edges that do not already exist on the canvas will be added.
     * @param edges the edges to draw to the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     * @see #draw(DrawableEdge)
     * @see #draw(ArrayList, ArrayList)
     */
    public void drawEdges(ArrayList<DrawableEdge> edges) {
        for (DrawableEdge edge : edges) draw(edge);
    }

    /**
     * Draw multiple nodes and multiple edges to the canvas. If the canvas is frozen, instead the nodes and edges will
     * be stored and drawn when the canvas is unfrozen. Only nodes and edges that do not already exist on the canvas
     * will be added.
     * @param nodes the nodes to draw to the canvas, can be null
     * @param edges the edges to draw to the canvas, can be null
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     * @see #drawNodes(ArrayList)
     * @see #drawEdges(ArrayList)
     */
    public void draw(ArrayList<DrawableNode> nodes, ArrayList<DrawableEdge> edges) {
        if (nodes != null) drawNodes(nodes);
        if (edges != null) drawEdges(edges);
    }

    /**
     * Remove a node from the canvas. If the canvas is frozen, instead the node will be removed from the stored nodes
     * and will not be drawn when the canvas is unfrozen. Will remove all instances of the node and all weights
     * associated with the node.
     * @param node the node to remove from the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     */
    public void remove(DrawableNode node) {
        if (freezing) throw new ConcurrentFreezeActionException("remove node \"" + node.toString() + "\" from canvas");
        if (frozen) {
            while (frozenNodes.remove(node));
            if ((node instanceof WeightedDrawableNode weightedNode)) while (frozenNodes.remove(weightedNode));
        }
        else {
            while (getChildren().remove(node));
            if ((node instanceof WeightedDrawableNode weightedNode)) while (getChildren().remove(weightedNode));
        }
    }

    /**
     * Remove an edge from the canvas. If the canvas is frozen, instead the edge will be removed from the stored edges
     * and will not be drawn when the canvas is unfrozen. Will remove all instances of the edge and all weights
     * associated with the edge.
     * @param edge the edge to remove from the canvas
     * @throws ConcurrentFreezeActionException if called while {@link #freeze()} is still executing
     */
    public void remove(DrawableEdge edge) {
        if (freezing) throw new ConcurrentFreezeActionException("remove edge \"" + edge.toString() + "\" from canvas");
        if (frozen) {
            while (frozenNodes.remove(edge));
            if ((edge instanceof WeightedDrawableEdge weightedEdge)) while (frozenNodes.remove(weightedEdge));
        }
        else {
            while (getChildren().remove(edge));
            if ((edge instanceof WeightedDrawableEdge weightedEdge)) while (getChildren().remove(weightedEdge));
        }
    }
}
