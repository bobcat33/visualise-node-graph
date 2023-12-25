package graphvisualisation.graphics.objects;

import graphvisualisation.data.graph.elements.WeightedNode;
import graphvisualisation.graphics.graphing.Graph;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class WeightedDrawableNode extends DrawableNode {
    public static final double
            WEIGHTED_CONTENT_BORDER_WIDTH = BORDER_WIDTH,
            WEIGHTED_CONTENT_PADDING = 30d,
            WEIGHTED_CONTENT_FONT_SIZE = FONT_SIZE/2;

    private final String value;
    private final Weight weight;

    public WeightedDrawableNode(Graph graph, WeightedNode node) {
        super(graph, node);
        this.value = node.value();

        this.weight = new Weight();

        border.hoverProperty().addListener((observable, oldValue, newValue) -> weight.setVisible(newValue));

        textID.hoverProperty().addListener((observable, oldValue, newValue) -> weight.setVisible(newValue));

        graph.addNodeWeight(weight);
    }

    public class Weight extends StackPane {
        private Weight() {
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

            hoverProperty().addListener((observable, oldValue, newValue) -> this.setVisible(newValue));

            getChildren().addAll(textBorder, text);
        }
    }
}