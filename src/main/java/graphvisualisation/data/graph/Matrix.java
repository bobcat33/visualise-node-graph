package graphvisualisation.data.graph;

import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Matrix {
    private final ArrayList<Node> nodes;
    private final boolean[][] edgeMatrix;

    public Matrix() throws InvalidFileException, FileNotFoundException {
        nodes = DataLoader.loadNodes();
        edgeMatrix = DataLoader.loadEdges(nodes);
    }

    public Matrix(ArrayList<Node> nodes, boolean[][] edgeMatrix) {
        this.nodes = nodes;
        this.edgeMatrix = edgeMatrix;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public int nNodes() {
        return edgeMatrix.length;
    }

    public boolean[][] getEdgeMatrix() {
        return edgeMatrix;
    }

    /**
     * Display nodes and their edges as a matrix. "." represents no edge, "1" represents that the two nodes have an
     * edge.
     */
    public void print() {

        ArrayList<Character> first = new ArrayList<>(Arrays.asList('F', 'i', 'r', 's', 't', ' ', 'N', 'o', 'd', 'e'));

        System.out.print("     Second Node\n    ");
        for (int i = 0; i < nNodes(); i++) {
            System.out.print(" " + i);
        }

        for (int x = 0; x < nNodes(); x++) {
            System.out.print("\n" + ((first.size() > 0) ? first.remove(0) : " ") + "  " + x);

            for (int y = 0; y < nNodes(); y++) {
                System.out.print(" " + ((edgeMatrix[x][y]) ? "1" : "."));
            }
        }

        System.out.println();
        for (Character c : first) {
            System.out.println(c);
        }

    }
}