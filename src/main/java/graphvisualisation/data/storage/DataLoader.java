package graphvisualisation.data.storage;

import graphvisualisation.data.graph.DiMatrix;
import graphvisualisation.data.graph.Matrix;
import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.data.graph.elements.WeightedNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class DataLoader {
    private static final File nodeFile = new File("src/main/java/graphvisualisation/data/storage/Nodes.txt");
    private static final File edgeFile = new File("src/main/java/graphvisualisation/data/storage/Edges.txt");
    private static final String delimiter = ";";

    public static int getNumberOfNodes() throws FileNotFoundException, InvalidFileException {

        Scanner fileScanner = new Scanner(edgeFile);

        HashSet<Integer> nodes = new HashSet<>();
        int x, y;

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {

            String[] line = fileScanner.nextLine().split(delimiter);
            lineNum++;

            if (line.length != 2) throw new InvalidFileException(lineNum);

            try {
                x = Integer.parseInt(line[0]);
                y = Integer.parseInt(line[1]);
            } catch (NumberFormatException e) {
                throw new InvalidFileException(lineNum);
            }

            nodes.add(x);
            nodes.add(y);

        }

        return nodes.size();

    }

    @Deprecated
    public static HashMap<Integer, ArrayList<Integer>> asAdjacencyMap(int size) throws FileNotFoundException, InvalidFileException {

        Scanner fileScanner = new Scanner(edgeFile);

        HashMap<Integer, ArrayList<Integer>> adjacencies = new HashMap<>();
        for (int i = 0; i < size; i++) adjacencies.put(i, new ArrayList<>());

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            String[] line = fileScanner.nextLine().split(delimiter);
            lineNum++;

            int x = -1, y = -1;

            try {
                x = Integer.parseInt(line[0]);
                y = Integer.parseInt(line[1]);
            } catch (NumberFormatException ignored) {}

            if (x < 0 || x > size-1 || y < 0 || y > size-1) throw new InvalidFileException(lineNum);

            if (!adjacencies.containsKey(x)) throw new InvalidFileException(lineNum);

            ArrayList<Integer> successors = adjacencies.get(x);
            int i;
            for (i = 0; i < successors.size(); i++) {
                if (y < successors.get(i)) break;
            }
            successors.add(i, y);

        }

        return adjacencies;
    }

    @Deprecated
    public static HashMap<Integer, ArrayList<Integer>> loadFileAsAdjacencyMap() throws FileNotFoundException, InvalidFileException {
        return asAdjacencyMap(getNumberOfNodes());
    }

    public static Matrix loadMatrix() throws FileNotFoundException, InvalidFileException {
        ArrayList<Node> nodes = loadNodes();
        return new Matrix(nodes, loadEdges(nodes));
    }

    public static DiMatrix loadDiMatrix() throws InvalidFileException, FileNotFoundException {
        ArrayList<Node> nodes = loadNodes();
        return new DiMatrix(nodes, loadEdges(nodes));
    }

    public static ArrayList<Node> loadNodes() throws FileNotFoundException, InvalidFileException {
        Scanner fileScanner = new Scanner(nodeFile);

        ArrayList<Node> nodes = new ArrayList<>();

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            lineNum++;
            String line = fileScanner.nextLine();

            String[] values = line.split(delimiter);

            String name = values[0];
            for (Node node : nodes) {
                if (node.name().equals(name)) throw new InvalidFileException(lineNum);
            }

            StringBuilder value = new StringBuilder();

            if (values.length > 1) {
                for (int i = 1; i < values.length; i++) {
                    value.append(values[i]);
                }
                nodes.add(new WeightedNode(lineNum-1, name, value.toString()));
            } else {
                nodes.add(new Node(lineNum-1, name));
            }
        }

        return nodes;
    }

    public static boolean[][] loadEdges(ArrayList<Node> nodes) throws FileNotFoundException, InvalidFileException {
        Scanner fileScanner = new Scanner(edgeFile);

        int nextID = getMaxID(nodes) + 1;

        ArrayList<String[]> loadedValues = new ArrayList<>();
        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            lineNum++;
            String[] lineParts = fileScanner.nextLine().split(delimiter);

            if (lineParts.length < 2 || lineParts[0].equals(lineParts[1])) throw new InvalidFileException(lineNum);

            int node1 = -1, node2 = -1;
            for (Node node : nodes) {
                if (node.name().equals(lineParts[0])) node1 = node.id();
                if (node.name().equals(lineParts[1])) node2 = node.id();
            }

            if (node1 < 0) {
                nodes.add(new Node((node1 = nextID++), lineParts[0]));
                System.out.println("Node created '" + lineParts[0] + "' with id " + node1);
            }
            if (node2 < 0) {
                nodes.add(new Node((node2 = nextID++), lineParts[1]));
                System.out.println("Node created '" + lineParts[1] + "' with id " + node2);
            }

            if (node1 == node2) throw new InvalidFileException(lineNum);

            loadedValues.add(lineParts);
        }

        return createEdgeMatrix(nodes, loadedValues);
    }

    private static int getMaxID(ArrayList<Node> nodes) {
        int maxID = -1;
        for (Node node : nodes) {
            if (node.id() > maxID) maxID = node.id();
        }
        return maxID;
    }

    private static boolean[][] createEdgeMatrix(ArrayList<Node> nodes, ArrayList<String[]> loadedValues) throws InvalidFileException {
        boolean[][] edgeMatrix = new boolean[nodes.size()][nodes.size()];
        for (String[] line : loadedValues) {
            int node1 = -1, node2 = -1;
            for (Node node : nodes) {
                if (node.name().equals(line[0])) node1 = node.id();
                if (node.name().equals(line[1])) node2 = node.id();
            }

            if (node1 == node2 || node1 < 0 || node2 < 0) throw new InvalidFileException();

            edgeMatrix[node1][node2] = true;
        }

        return edgeMatrix;
    }

    /*private static boolean[][] toEdgeMatrix(ArrayList<ArrayList<Boolean>> matrix) {
        boolean[][] arrayMatrix = new boolean[matrix.size()][matrix.get(0).size()];

        for (int x = 0; x < arrayMatrix.length; x++) {
            for (int y = 0; y < arrayMatrix[x].length; y++) {
                arrayMatrix[x][y] = matrix.get(x).get(y);
            }
        }

        return arrayMatrix;
    }*/
}

