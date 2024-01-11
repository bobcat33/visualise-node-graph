package graphvisualisation.graphics.logic;

import graphvisualisation.graphics.canvas.Point;
import graphvisualisation.graphics.objects.DrawableNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import java.util.ArrayList;

public class NodeSlider {
    private final Timeline timeline;
    private final int slideDuration;
    private final EndAction endAction;
    private final boolean withinBounds;

    public NodeSlider(ArrayList<DrawableNode> nodes, ArrayList<Point> endPoints, int durationMillis) {
        this(nodes, endPoints, durationMillis, false, null);
    }

    public NodeSlider(ArrayList<DrawableNode> nodes, ArrayList<Point> endPoints, int durationMillis, boolean withinBounds) {
        this(nodes, endPoints, durationMillis, withinBounds, null);
    }

    public NodeSlider(ArrayList<DrawableNode> nodes, ArrayList<Point> endPoints, int durationMillis, EndAction endAction) {
        this(nodes, endPoints, durationMillis, false, endAction);
    }
    public NodeSlider(ArrayList<DrawableNode> nodes, ArrayList<Point> endPoints, int durationMillis, boolean withinBounds, EndAction endAction) {
        this.endAction = endAction;
        this.withinBounds = withinBounds;
        slideDuration = durationMillis;
        timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1), new SlideFrame(nodes, endPoints)));
        timeline.setCycleCount(durationMillis);
    }

    public void start() {
        timeline.play();
    }

    public void pause() {
        timeline.pause();
    }

    private class SlideFrame implements EventHandler<ActionEvent> {
        private final ArrayList<DrawableNode> nodes;
        private final ArrayList<Point> endPoints;
        private int frameNumber = 0;

        private SlideFrame(ArrayList<DrawableNode> nodes, ArrayList<Point> endPoints) {
            this.nodes = nodes;
            this.endPoints = endPoints;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            double remainingFrames = slideDuration - frameNumber;
            double distanceMultiplier = 1d / remainingFrames;
            for (int i = 0; i < nodes.size(); i++) {
                DrawableNode node = nodes.get(i);
                Point nodeCentre = node.getCentre();
                Point endPoint = endPoints.get(i);
                double distance = nodeCentre.distanceTo(endPoint);
                double travelDistance = distance * distanceMultiplier;
                Point normalisedVector = nodeCentre.getVectorTo(endPoint).normalize();
                Point movementVector = normalisedVector.multiply(travelDistance);
                Point movePoint = nodeCentre.add(movementVector);
                if (!withinBounds) node.moveTo(movePoint);
                else node.moveWithinBoundsTo(movePoint);
            }
            if (++frameNumber == slideDuration) {
                frameNumber = 0;
                if (endAction != null) endAction.handle();
            }
        }
    }
}
