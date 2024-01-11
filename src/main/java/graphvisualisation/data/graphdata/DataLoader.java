package graphvisualisation.data.graphdata;

import graphvisualisation.data.graphdata.elements.Edge;
import graphvisualisation.data.graphdata.elements.Node;
import graphvisualisation.data.graphdata.elements.WeightedEdge;
import graphvisualisation.data.graphdata.elements.WeightedNode;
import graphvisualisation.graphics.objects.exceptions.InvalidEdgeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataLoader {
    private static final File nodeFile = new File("src/main/java/graphvisualisation/data/storage/Nodes.txt");
    private static final File edgeFile = new File("src/main/java/graphvisualisation/data/storage/Edges.txt");
    private static final String baseDelimiter = ";";

    // todo: require specified file paths / File objects
    public static GraphData loadGraphData() {
        ArrayList<Node> nodes = loadNodes();
        return new GraphData(nodes, loadEdges(nodes));
    }

    public static ArrayList<Node> loadNodes() {
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(nodeFile);
        } catch (FileNotFoundException e) {
            throw new InvalidFileException();
        }

        ArrayList<Node> nodes = new ArrayList<>();

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            lineNum++;
            String line = fileScanner.nextLine();

            String[] values = line.split(baseDelimiter, 2);
            if (values.length == 0) throw new InvalidFileException(lineNum);

            // Remove whitespace
            trimParts(values);

            String name = values[0];
            if (name.equals("")) throw new InvalidFileException(lineNum);
            for (Node node : nodes) {
                if (node.name().equals(name)) throw new InvalidFileException(lineNum);
            }

            if (values.length == 2) {
                nodes.add(new WeightedNode(lineNum-1, name, values[1]));
            } else {
                nodes.add(new Node(lineNum-1, name));
            }
        }
        fileScanner.close();

        return nodes;
    }

    public static ArrayList<Edge> loadEdges(ArrayList<Node> nodes) {
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(edgeFile);
        } catch (FileNotFoundException e) {
            throw new InvalidFileException();
        }

        int nextID = getMaxID(nodes) + 1;

        // Defaults if values are not set in the first line
        Boolean directed = false; // true if directed, false if undirected, null if mixed
        ArrayList<Boolean> mixedDirections = null; // for each edge true if directed, false if not
        boolean weighted = true; // true if some edges may contain weights, false if there are no weights

        String nodeDelimiter = null, directedDelimiter = null, undirectedDelimiter = null, weightDelimiter = null;

        ArrayList<String[]> loadedValues = new ArrayList<>();
        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            // todo: allow default delimiters if they arent specified in the file
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
                        String value = loadDelimiter(lineNum, line);
                        nodeDelimiter = value;
                        weightDelimiter = value;
                    }
                }
                case 2 -> {
                    // Line 2: If using default values then the required delimiters have already been defined
                    if (weightDelimiter != null) break;

                    String value = loadDelimiter(lineNum, line);

                    // Otherwise: If directed/undirected then the delimiter for the nodes, if mixed then the
                    // delimiter for directed nodes
                    isEdgeDefinition = false;
                    if (directed == null) directedDelimiter = value;
                    else nodeDelimiter = value;
                }
                case 3 -> {
                    // Line 3: If using default values then the required delimiters have already been defined
                    if (weightDelimiter != null || (directed != null && !weighted)) break;

                    String value = loadDelimiter(lineNum, line);

                    isEdgeDefinition = false;
                    if (directed == null) {
                        // Directed and undirected delimiters cannot be the same
                        if (directedDelimiter.equals(value)) throw new InvalidFileException(lineNum);
                        undirectedDelimiter = value;
                    }
                    else weightDelimiter = value;

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
                        weightDelimiter = loadDelimiter(lineNum, line);
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
                lineParts = line.split((directedLine) ? directedDelimiter : undirectedDelimiter, 2);
            } else {
                // Directed/Undirected
                if (line.indexOf(nodeDelimiter) <= 0) throw new InvalidFileException(lineNum);
                lineParts = line.split(nodeDelimiter, 2);
            }

            if (lineParts.length < 2) throw new InvalidFileException(lineNum);

            if (weighted) {
                String[] secondParts = lineParts[1].split(weightDelimiter, 2);
                if (secondParts.length == 0) throw new InvalidFileException(lineNum);
                else if (secondParts.length == 2)
                    lineParts = new String[]{lineParts[0], secondParts[0], secondParts[1]};
            }

            // Remove whitespace
            trimParts(lineParts);

            // If the node names are the same or empty
            if (lineParts[0].equals(lineParts[1]) || lineParts[0].equals("") || lineParts[1].equals("")) throw new InvalidFileException(lineNum);

            int node1 = -1, node2 = -1;
            for (Node node : nodes) {
                if (node.name().equals(lineParts[0])) node1 = node.id();
                if (node.name().equals(lineParts[1])) node2 = node.id();
            }

            if (node1 < -1 || node2 < -1) throw new InvalidFileException(lineNum);

            if (node1 == -1) {
                nodes.add(new Node((node1 = nextID++), lineParts[0]));
                System.out.println("Node created '" + lineParts[0] + "' with id " + node1);
            }
            if (node2 == -1) {
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
        if (indexDirected == -1) return false;
        if (indexUndirected == -1) return true;
        return indexDirected < indexUndirected;
    }

    private static int getMaxID(ArrayList<Node> nodes) {
        int maxID = -1;
        for (Node node : nodes) {
            if (node.id() > maxID) maxID = node.id();
        }
        return maxID;
    }

    private static void trimParts(String[] parts) {
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
    }

    private static String loadDelimiter(int lineNum, String value) throws InvalidFileException {
        if (!value.startsWith("\"") || !value.endsWith("\"")) throw new InvalidFileException(lineNum);
        return value.replaceAll("\"(.*)\"", "$1");
    }

    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, boolean directed) {
        return createEdges(nodes, loadedValues, directed, null);
    }

    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, ArrayList<Boolean> mixedDirections) {
        if (mixedDirections == null) throw new InvalidFileException();
        return createEdges(nodes, loadedValues, true, mixedDirections);
    }

    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, boolean directed, ArrayList<Boolean> mixedDirections) {
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

