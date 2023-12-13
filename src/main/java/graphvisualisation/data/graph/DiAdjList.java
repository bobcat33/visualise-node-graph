package graphvisualisation.data.graph;

import graphvisualisation.data.storage.DataLoader;
import graphvisualisation.data.storage.InvalidFileException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A quick class put together to demonstrate the layout of the nodes as an edge list in the console
 */

public class DiAdjList {
    private final HashMap<Integer, ArrayList<Integer>> adjacencies;

    public DiAdjList() throws InvalidFileException, FileNotFoundException {
        adjacencies = DataLoader.loadFileAsAdjacencyMap();
    }

    /**
     * Display all nodes and their successors as a list. The values on the left are the nodes, the values after the "->"
     * are their successors. If a node has no successors it is marked with "/" instead.
     */
    public void print() {
        for (Map.Entry<Integer, ArrayList<Integer>> entry : adjacencies.entrySet()) {
            Integer node = entry.getKey();
            ArrayList<Integer> successors = entry.getValue();

            System.out.print(node + " ");

            if (successors.size() > 0) {
                System.out.print("-> " + successors.remove(0));
                for (Integer successor : successors) {
                    System.out.print("," + successor);
                }
                System.out.println();
            } else {
                System.out.println("/");
            }
        }
    }
}
