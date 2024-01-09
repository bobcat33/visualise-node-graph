package graphvisualisation.graphics.graphing;

import graphvisualisation.data.graph.GraphData;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.logic.ForceDirectedBuilder;
import graphvisualisation.graphics.objects.exceptions.DuplicateEdgeException;
import graphvisualisation.graphics.objects.exceptions.DuplicateNodeException;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

import java.io.FileNotFoundException;

public class ForceDirectedGraph extends Graph {

    public ForceDirectedGraph(double width, double height) throws InvalidFileException, FileNotFoundException, InvalidEdgeException, UndefinedNodeException, DuplicateNodeException, DuplicateEdgeException {
        super(new ForceDirectedBuilder(ForceDirectedBuilder.AnimationType.FULL_ANIMATION), width, height);
    }

    public ForceDirectedGraph(double width, double height, GraphData graphData) throws InvalidEdgeException, UndefinedNodeException, DuplicateNodeException, DuplicateEdgeException {
        super(new ForceDirectedBuilder(ForceDirectedBuilder.AnimationType.FULL_ANIMATION), width, height, graphData);
    }
}
