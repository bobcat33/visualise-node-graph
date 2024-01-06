package graphvisualisation.data.storage;

import graphvisualisation.data.graph.Matrix;
import graphvisualisation.data.graph.elements.Edge;
import graphvisualisation.data.graph.elements.Node;
import graphvisualisation.data.graph.elements.WeightedEdge;
import graphvisualisation.data.graph.elements.WeightedNode;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class DataLoader {
    private static final File nodeFile = new File("src/main/java/graphvisualisation/data/storage/Nodes.txt");
    private static final File edgeFile = new File("src/main/java/graphvisualisation/data/storage/Edges.txt");
    private static final String baseDelimiter = ";";

    public static int getNumberOfNodes() throws FileNotFoundException, InvalidFileException {

        Scanner fileScanner = new Scanner(edgeFile);

        HashSet<Integer> nodes = new HashSet<>();
        int x, y;

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {

            String[] line = fileScanner.nextLine().split(baseDelimiter);
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
            String[] line = fileScanner.nextLine().split(baseDelimiter);
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

    // todo: require specified file paths / File objects
    public static Matrix loadMatrix() throws FileNotFoundException, InvalidFileException {
        ArrayList<Node> nodes = loadNodes();
        return new Matrix(nodes, loadEdges(nodes));
    }

    public static ArrayList<Node> loadNodes() throws FileNotFoundException, InvalidFileException {
        Scanner fileScanner = new Scanner(nodeFile);

        ArrayList<Node> nodes = new ArrayList<>();

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            lineNum++;
            String line = fileScanner.nextLine();

            String[] values = line.split(baseDelimiter);

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

    public static ArrayList<Edge> loadEdges(ArrayList<Node> nodes) throws FileNotFoundException, InvalidFileException {
        Scanner fileScanner = new Scanner(edgeFile);

        int nextID = getMaxID(nodes) + 1;

        // Defaults if values are not set in the first line
        Boolean directed = false; // true if directed, false if undirected, null if mixed
        ArrayList<Boolean> mixedDirections = null; // for each edge true if directed, false if not
        boolean weighted = true; // true if some edges may contain weights, false if there are no weights

        String nodeDelimiter = null, directedDelimiter = null, undirectedDelimiter = null, weightDelimiter = null;

        ArrayList<String[]> loadedValues = new ArrayList<>();
        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            lineNum++;
            String line = fileScanner.nextLine();
            boolean isEdgeDefinition = true;
            switch (lineNum) {
                case 1 -> {
                    // Line 1: Either follow "directed/undirected/mixed:weighted/unweighted" or declare the delimiter
                    // for nodes and node weights
                    isEdgeDefinition = false;
                    String[] options = line.split(":");
                    boolean isInvalidOptions = true;
                    if (options.length == 2) {
                        isInvalidOptions = false;
                        switch (options[0].toLowerCase()) {
                            case "directed" -> directed = true;
                            case "undirected" -> directed = false;
                            case "mixed" -> {
                                directed = null;
                                mixedDirections = new ArrayList<>();
                            }
                            default -> isInvalidOptions = true;
                        }
                        switch (options[1].toLowerCase()) {
                            case "weighted" -> weighted = true;
                            case "unweighted" -> weighted = false;
                            default -> isInvalidOptions = true;
                        }
                    }
                    if (isInvalidOptions) {
                        nodeDelimiter = line;
                        weightDelimiter = line;
                    }
                }
                case 2 -> {
                    // Line 2: If using default values then the required delimiters have already been defined
                    if (weightDelimiter != null) break;

                    // Otherwise: If directed/undirected then the delimiter for the nodes, if mixed then the
                    // delimiter for directed nodes
                    isEdgeDefinition = false;
                    if (directed == null) directedDelimiter = line;
                    else nodeDelimiter = line;
                }
                case 3 -> {
                    // Line 3: If using default values then the required delimiters have already been defined
                    if (weightDelimiter != null || (directed != null && !weighted)) break;

                    isEdgeDefinition = false;
                    if (directed == null) {
                        // Directed and undirected delimiters cannot be the same
                        if (directedDelimiter.equals(line)) throw new InvalidFileException(lineNum);
                        undirectedDelimiter = line;
                    }
                    else weightDelimiter = line;

                    // todo: could just put continue at the end of each case
                    // If mixed then the delimiter for undirected nodes
                    // if not mixed and weighted then the delimiter between nodes and the edge weight
                    // if not mixed and unweighted then will be the first edge definition
                }
                case 4 -> {
                    // Line 4: Only used if the edges are mixed directions and weighted - used to
                    // define the weight delimiter
                    if (weighted && weightDelimiter == null) {
                        isEdgeDefinition = false;
                        weightDelimiter = line;
                    }
                }
            }
            if (!isEdgeDefinition) continue;

            String[] lineParts;

            if (directed == null) {
                // Mixed
                if (directedDelimiter == null || undirectedDelimiter == null) throw new InvalidFileException();
                Boolean directedLine = calcDirectedMixed(line, directedDelimiter, undirectedDelimiter);
                if (directedLine == null) throw new InvalidFileException(lineNum);
                mixedDirections.add(directedLine);
                lineParts = line.split((directedLine) ? directedDelimiter : undirectedDelimiter);
            } else {
                // Directed/Undirected
                if (line.indexOf(nodeDelimiter) <= 0) throw new InvalidFileException(lineNum);
                lineParts = line.split(nodeDelimiter);
            }

            if (lineParts.length < 2) throw new InvalidFileException(lineNum);
            String secondPart = condenseLineParts(lineParts, 1);


            if (weighted) {
                // Weighted
                String[] secondParts = secondPart.split(weightDelimiter);
                if (secondParts.length == 0) throw new InvalidFileException(lineNum);
                else if (secondParts.length == 1) lineParts = new String[]{lineParts[0], secondPart};
                else lineParts = new String[]{lineParts[0], secondParts[0], condenseLineParts(secondParts, 1)};
            } else {
                // Unweighted
                lineParts = new String[]{lineParts[0], secondPart};
            }

            if (lineParts[0].equals(lineParts[1])) throw new InvalidFileException(lineNum);

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
        fileScanner.close();

        if (directed == null) return createEdges(nodes, loadedValues, mixedDirections);
        return createEdges(nodes, loadedValues, directed);
    }

    private static String condenseLineParts(String[] parts, int startIndex) {
        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) builder.append(parts[i]);
        return builder.toString();
    }

    private static Boolean calcDirectedMixed(String line, String directedDelimiter, String undirectedDelimiter) {
        int indexDirected = line.indexOf(directedDelimiter);
        int indexUndirected = line.indexOf(undirectedDelimiter);
        // If neither delimiter was found, or either starts at 0 (meaning there is no content before it)
        if (
                (indexDirected == -1 && indexUndirected == -1)
                || indexDirected == 0
                || indexUndirected == 0
        ) return null;
        // If one of the delimiters incorporates the other, like "-" and "->", then take whichever is longer
        // This means that 1-2 and 1->2 would give different results
        if (indexDirected == indexUndirected) return directedDelimiter.length() > undirectedDelimiter.length();
        return indexDirected != -1;
    }

    private static int getMaxID(ArrayList<Node> nodes) {
        int maxID = -1;
        for (Node node : nodes) {
            if (node.id() > maxID) maxID = node.id();
        }
        return maxID;
    }

    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, boolean directed) throws InvalidFileException {
        return createEdges(nodes, loadedValues, directed, null);
    }

    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, ArrayList<Boolean> mixedDirections) throws InvalidFileException {
        if (mixedDirections == null) throw new InvalidFileException();
        return createEdges(nodes, loadedValues, true, mixedDirections);
    }

    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, boolean directed, ArrayList<Boolean> mixedDirections) throws InvalidFileException {
        if (mixedDirections != null && loadedValues.size() != mixedDirections.size()) throw new InvalidFileException();

        ArrayList<Edge> edges = new ArrayList<>();
        for (int lineNum = 0; lineNum < loadedValues.size(); lineNum++) {
            String[] line = loadedValues.get(lineNum);
            if (line.length > 3) throw new InvalidFileException();

            Node node1 = null, node2 = null;
            for (Node node : nodes) {
                if (node.name().equals(line[0])) node1 = node;
                if (node.name().equals(line[1])) node2 = node;
            }
            if (node1 == null || node2 == null || node1.equals(node2)) throw new InvalidFileException();

            boolean edgeDirected = directed;
            if (mixedDirections != null) {
                edgeDirected = mixedDirections.get(lineNum);
            }

            try {
                if (line.length == 3) edges.add(new WeightedEdge(node1, node2, edgeDirected, line[2]));
                else edges.add(new Edge(node1, node2, edgeDirected));
            } catch (InvalidEdgeException e) {
                throw new InvalidFileException();
            }
        }

        return edges;
    }
}

