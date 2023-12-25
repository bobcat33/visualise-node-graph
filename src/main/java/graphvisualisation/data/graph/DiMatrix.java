package graphvisualisation.data.graph;

import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.data.storage.InvalidFileException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

// todo: likely need to remove this class in favour of defining each edge as directed or not
public class DiMatrix extends Matrix {

    public DiMatrix() throws InvalidFileException, FileNotFoundException {}

    public DiMatrix(ArrayList<Node> nodes, boolean[][] edgeMatrix) {
        super(nodes, edgeMatrix);
    }

    public DiMatrix(Matrix matrix) {
        super(matrix.getNodes(), matrix.getEdgeMatrix());
    }

}