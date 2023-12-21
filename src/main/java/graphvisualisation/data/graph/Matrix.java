package graphvisualisation.data.graph;

import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Matrix {
    private final boolean[][] edgeMatrix;

    public Matrix() throws InvalidFileException, FileNotFoundException {
        edgeMatrix = DataLoader.loadFileAsMatrix();
    }

    public int nNodes() {
        return edgeMatrix.length;
    }

    public boolean[][] getEdgeMatrix() {
        return edgeMatrix;
    }

    public int[] getConnectedNodes(int node) {
        // todo: Using hashset here is likely to create overhead, could be improved in the future for efficiency
        HashSet<Integer> connectedNodesSet = new HashSet<>();

        // Find all nodes connected to the node passed in
        for (int x = 0; x < edgeMatrix.length; x++) {
            for (int y = 0; y < edgeMatrix[x].length; y++) {
                if ((x == node || y == node) && edgeMatrix[x][y]) {
                    if (x == node) connectedNodesSet.add(y);
                    else connectedNodesSet.add(x);
                }
            }
        }

        // Convert the set to an int array
        Integer[] connectedNodes = new Integer[connectedNodesSet.size()];
        connectedNodesSet.toArray(connectedNodes);

        int[] nodes = new int[connectedNodes.length];
        for (int i = 0; i < connectedNodes.length; i++) {nodes[i] = connectedNodes[i];}

        return nodes;
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
