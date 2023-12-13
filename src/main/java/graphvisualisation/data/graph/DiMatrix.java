package graphvisualisation.data.graph;

import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A quick class put together to demonstrate the layout of nodes and their edges as a matrix in the console
 */

public class DiMatrix {

    private final boolean[][] nodes;

    public DiMatrix() throws InvalidFileException, FileNotFoundException {
        nodes = DataLoader.loadFileAsMatrix();
    }

    public int nNodes() {
        return nodes.length;
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
                System.out.print(" " + ((nodes[x][y]) ? "1" : "."));
            }
        }

        System.out.println();
        for (Character c : first) {
            System.out.println(c);
        }

    }
}