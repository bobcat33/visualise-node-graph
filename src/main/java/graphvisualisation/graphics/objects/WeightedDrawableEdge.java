package graphvisualisation.graphics.objects;

import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class WeightedDrawableEdge extends DrawableEdge {
    public static final double
            WEIGHTED_CONTENT_BORDER_WIDTH = DrawableNode.BORDER_WIDTH,
            WEIGHTED_CONTENT_PADDING = 30d,
            WEIGHTED_CONTENT_FONT_SIZE = DrawableNode.FONT_SIZE/2,
            HOVER_MASK_WIDTH = Arrow.WIDTH;

    private final String value;
    private final Weight weight;
    private final Polygon hoverMask = new Polygon();
    private HoverAction hoverAction;

    public WeightedDrawableEdge(DrawableNode startNode, DrawableNode endNode, boolean directed, String value) throws InvalidEdgeException, UndefinedNodeException {
        this(startNode, endNode, directed, value, (edge, isHovering) -> {
            if (isHovering) edge.setColour(Color.DARKBLUE);
            else edge.setColour(Color.BLACK);
        });
    }

    public WeightedDrawableEdge(DrawableNode startNode, DrawableNode endNode, boolean directed, String value, HoverAction hoverAction) throws InvalidEdgeException, UndefinedNodeException {
        super(startNode, endNode, directed);
        this.value = value;
        this.hoverAction = hoverAction;

        this.weight = new Weight(this);


        hoverMask.setStrokeWidth(0);
        hoverMask.setFill(Color.TRANSPARENT);
        connectHoverMask();
        getChildren().add(hoverMask);

        this.weight.setListener((ignored1, ignored2, isHovered) -> weight.setVisible(isHovered));

        hoverMask.hoverProperty().addListener((ignored1, ignored2, isHovered) -> {
            weight.setVisible(isHovered);
            handleHover(isHovered);
        });
    }

    @Override
    public WeightedDrawableEdge createCopyWith(DrawableNode startNode, DrawableNode endNode) {
        return createWeightedCopyWith(startNode, endNode, value, hoverAction);
    }

    @Override
    public void reconnect() {
        super.reconnect();
        connectHoverMask();
    }

    private void connectHoverMask() {
        hoverMask.getPoints().clear();

        Point u = getNormalisedLineVector();

        Point lineEnd = endNode.getCentre().sub(u.multiply(endNode.getNodeRadius()));
        Point lineStart = startNode.getCentre().add(u.multiply(startNode.getNodeRadius()));
        Point vectorHalfWidth = new Point(u.getY(), -u.getX()).multiply(HOVER_MASK_WIDTH/2);
        Point startTop = lineStart.sub(vectorHalfWidth);
        Point startBottom = lineStart.add(vectorHalfWidth);
        Point endTop = lineEnd.sub(vectorHalfWidth);
        Point endBottom = lineEnd.add(vectorHalfWidth);

        addHoverMaskPoint(startTop);
        addHoverMaskPoint(startBottom);
        addHoverMaskPoint(endBottom);
        addHoverMaskPoint(endTop);
    }

    private void addHoverMaskPoint(Point point) {
        hoverMask.getPoints().add(point.getX());
        hoverMask.getPoints().add(point.getY());
    }

    public String value() {
        return value;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setHoverAction(HoverAction hoverAction) {
        this.hoverAction = hoverAction;
    }

    private void handleHover(boolean isHovering) {
        if (hoverAction != null) hoverAction.handle(this, isHovering);
    }

    public class Weight extends StackPane {
        private final WeightedDrawableEdge edge;
        private Weight(WeightedDrawableEdge edge) {
            this.edge = edge;

            setVisible(false);

            // Set up the text box for storing the value
            Text text = new Text(value);
            text.setFont(new Font(WEIGHTED_CONTENT_FONT_SIZE));

            Rectangle textBorder = new Rectangle(
                    0 + WEIGHTED_CONTENT_BORDER_WIDTH/2,
                    0 + WEIGHTED_CONTENT_BORDER_WIDTH/2,
                    text.getLayoutBounds().getWidth() + WEIGHTED_CONTENT_PADDING*2 + WEIGHTED_CONTENT_BORDER_WIDTH,
                    text.getLayoutBounds().getHeight() + WEIGHTED_CONTENT_PADDING*2 + WEIGHTED_CONTENT_BORDER_WIDTH);

            textBorder.setFill(Color.WHITE);
            textBorder.setStroke(Color.BLACK);
            textBorder.setStrokeWidth(WEIGHTED_CONTENT_BORDER_WIDTH);

            getChildren().addAll(textBorder, text);
        }

        private void setListener(ChangeListener<Boolean> hoverListener) {
            hoverProperty().addListener(hoverListener);
        }

        public String value() {
            return value;
        }

        public WeightedDrawableEdge edge() {
            return edge;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (o instanceof WeightedDrawableEdge compareEdge) return edge.equals(compareEdge);
            if (o instanceof Weight edgeWeight) return edge.equals(edgeWeight.edge);
            return false;
        }
    }

    public interface HoverAction {
        void handle(WeightedDrawableEdge edge, boolean isHovering);
    }
}
