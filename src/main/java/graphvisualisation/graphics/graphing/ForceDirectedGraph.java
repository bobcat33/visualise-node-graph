package graphvisualisation.graphics.graphing;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.data.storage.InvalidFileException;
import graphvisualisation.graphics.logic.ForceDirectedBuilder;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;
import graphvisualisation.graphics.objects.exceptions.UndefinedNodeException;

import java.io.FileNotFoundException;

public class ForceDirectedGraph extends Graph {

    public ForceDirectedGraph(double width, double height) throws InvalidFileException, FileNotFoundException, InvalidEdgeException, UndefinedNodeException {
        super(new ForceDirectedBuilder(), width, height);
    }

    public ForceDirectedGraph(double width, double height, Matrix matrix) throws InvalidEdgeException, UndefinedNodeException {
        super(new ForceDirectedBuilder(), width, height, matrix);
    }
}
