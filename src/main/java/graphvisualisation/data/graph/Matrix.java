package graphvisualisation.data.graph;

import graphvisualisation.data.graph.elements.Edge;
import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Matrix {
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;

    public Matrix() throws InvalidFileException, FileNotFoundException {
        nodes = DataLoader.loadNodes();
        edges = DataLoader.loadEdges(nodes);
    }

    public Matrix(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}