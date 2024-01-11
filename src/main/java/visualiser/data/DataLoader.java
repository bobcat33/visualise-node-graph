package visualiser.data;

import visualiser.data.elements.Edge;
import visualiser.data.elements.Node;
import visualiser.data.elements.WeightedEdge;
import visualiser.data.elements.WeightedNode;
import visualiser.data.exceptions.InvalidFileException;
import visualiser.graphics.objects.exceptions.InvalidEdgeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataLoader {
    private static final File nodeFile = new File("src/main/java/visualiser/data/storage/Nodes.txt");
    private static final File edgeFile = new File("src/main/java/visualiser/data/storage/Edges.txt");
    private static final String defaultDelimiter = ";";
    private static final String commentPrefix = "//";

    // todo: Make non-static and initialise with file names / file objects or default files

    /**
     * Create a new {@link GraphData} object using data from Nodes.txt and Edges.txt. If the Nodes.txt file is not
     * found node data will be loaded from the Edges.txt file only.
     * @return the GraphData object created
     */
    public static GraphData loadGraphData() {
        ArrayList<Node> nodes;
        try {
            nodes = loadNodes();
        } catch (FileNotFoundException ignored) {
            nodes = new ArrayList<>();
        }
        return new GraphData(nodes, loadEdges(nodes));
    }

    /**
     * Load an array of {@link Node Nodes} from the Nodes.txt file.
     * @return a new ArrayList of Nodes
     */
    public static ArrayList<Node> loadNodes() throws FileNotFoundException {
        Scanner fileScanner;
        fileScanner = new Scanner(nodeFile);

        ArrayList<Node> nodes = new ArrayList<>();

        String nodeDelimiter = null;

        int dataLineNum = 0;
        int fileLineNum = 0;
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            fileLineNum++;
            if (line.startsWith(commentPrefix)) continue;
            
            dataLineNum++;
            if (dataLineNum == 1) {
                try {
                    // If the line contains the pattern "\"(.*)\"" then set the delimiter to the contents of the quotes
                    nodeDelimiter = loadDelimiter(1, line);
                    // And skip the line
                    continue;
                } catch (InvalidFileException e) {
                    // If the line doesn't contain the pattern "\"(.*)\"" then set the delimiter to the default
                    nodeDelimiter = defaultDelimiter;
                }
            }

            String[] values = line.split(nodeDelimiter, 2);
            if (values.length == 0)
                throw new InvalidFileException(fileLineNum, "No data could be found using delimiter \"" + nodeDelimiter + "\"");

            // Remove whitespace
            trimParts(values);

            // Get the name of the node and validate that it doesn't already exist
            String name = values[0];
            if (name.equals("")) throw new InvalidFileException(fileLineNum, "Empty node name");
            for (Node node : nodes) {
                if (node.name().equals(name)) throw new InvalidFileException(fileLineNum, "Duplicate of node \"" + name + "\"");
            }

            // Check if the node is weighted and create a new Node or WeightedNode as appropriate
            if (values.length == 2) {
                nodes.add(new WeightedNode(fileLineNum-1, name, values[1]));
            } else {
                nodes.add(new Node(fileLineNum-1, name));
            }
        }
        fileScanner.close();

        return nodes;
    }

    /**
     * Load edges from the Edges.txt file, use this method for data without any predefined nodes.
     * @return an ArrayList of {@link Edge Edges}
     */
    public static ArrayList<Edge> loadEdges() {
        return loadEdges(null);
    }

    /**
     * Load edges from the Edges.txt file with a set of predefined nodes. Any new nodes found in the Edges.txt file
     * will be created and added to the nodes array.
     * @param nodes the predefined {@link Node Nodes} to be added to with new nodes and read from for existing nodes
     * @return an ArrayList of {@link Edge Edges}
     */
    public static ArrayList<Edge> loadEdges(ArrayList<Node> nodes) {
        // If the nodes have not been defined then initialise as a new array
        if (nodes == null) nodes = new ArrayList<>();

        // Ensure the file exists
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(edgeFile);
        } catch (FileNotFoundException e) {
            throw new InvalidFileException("Edge file does not exist.");
        }

        // Load the greatest node ID from the predefined nodes, this is used to ensure that no two nodes are created
        // with the same ID and is more efficient than constant lookups to fill ID gaps. It is accepted that IDs are
        // not sequential. They do, however, have to be above 0.
        int nextID = getMaxID(nodes) + 1;

        // Defaults if values are not set in the first line
        Boolean directed = false; // true if directed, false if undirected, null if mixed
        ArrayList<Boolean> mixedDirections = null; // for each edge true if directed, false if not
        boolean weighted = true; // true if some edges may contain weights, false if there are no weights

        String nodeDelimiter = null, directedDelimiter = null, undirectedDelimiter = null, weightDelimiter = null;

        ArrayList<String[]> loadedValues = new ArrayList<>();
        int dataLineNum = 0;
        int fileLineNum = 0;
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            fileLineNum++;
            if (line.startsWith(commentPrefix)) continue;

            dataLineNum++;
            if (dataLineNum <= 4) switch (dataLineNum) {
                case 1 -> {
                    // Line 1: Either follow "directed/undirected/mixed:weighted/unweighted" or declare the delimiter
                    // for nodes and node weights
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

                    boolean isEdgeDefinition = false;
                    if (isInvalidOptions) {
                        // If the first line defines a delimiter then use it, otherwise use the default delimiter
                        String value;
                        try {
                            value = loadDelimiter(fileLineNum, line);
                        } catch (InvalidFileException e) {
                            value = defaultDelimiter;
                            isEdgeDefinition = true;
                        }
                        nodeDelimiter = value;
                        weightDelimiter = value;
                    }

                    if (!isEdgeDefinition) continue;
                }
                case 2 -> {
                    // Line 2: If using default values then the required delimiters have already been defined
                    if (weightDelimiter != null) break;

                    String value = loadDelimiter(fileLineNum, line);

                    // Otherwise: If directed/undirected then the delimiter for the nodes, if mixed then the
                    // delimiter for directed nodes
                    if (directed == null) directedDelimiter = value;
                    else nodeDelimiter = value;

                    continue;
                }
                case 3 -> {
                    // Line 3: If using default values then the required delimiters have already been defined
                    if (weightDelimiter != null || (directed != null && !weighted)) break;

                    String value = loadDelimiter(fileLineNum, line);

                    // If mixed then set the delimiter for undirected edges
                    if (directed == null) {
                        // Directed and undirected delimiters cannot be the same
                        if (directedDelimiter.equals(value))
                            throw new InvalidFileException(fileLineNum, "Directed and undirected delimiters cannot be the same");
                        undirectedDelimiter = value;
                    }
                    // If directed then set the delimiter for edge weights
                    else weightDelimiter = value;

                    continue;
                }
                case 4 -> {
                    // Line 4: Only used if the edges are mixed directions and weighted - used to
                    // define the weight delimiter
                    if (weighted && weightDelimiter == null) {
                        weightDelimiter = loadDelimiter(fileLineNum, line);
                        continue;
                    }
                }
            }

            String[] lineParts;

            if (directed == null) {
                // Mixed
                if (directedDelimiter == null || undirectedDelimiter == null) throw new InvalidFileException(((directedDelimiter == null) ? "Directed" : "Undirected") + " delimiter has not been defined.");
                // Get whether line is directed, undirected or invalid
                Boolean directedLine = calcDirectedMixed(line, directedDelimiter, undirectedDelimiter);
                if (directedLine == null) throw new InvalidFileException(fileLineNum, "Could not find valid edge delimiter");
                // Store the direction so it can be used in createEdges
                mixedDirections.add(directedLine);
                // Get the line parts from the line
                lineParts = line.split((directedLine) ? directedDelimiter : undirectedDelimiter, 2);
            } else {
                // Directed/Undirected
                if (line.indexOf(nodeDelimiter) <= 0) throw new InvalidFileException(fileLineNum, "Could not find valid edge delimiter");
                // Get hte line parts from the line
                lineParts = line.split(nodeDelimiter, 2);
            }

            // Ensure proper number of parts
            if (lineParts.length < 2) throw new InvalidFileException(fileLineNum, "Only one node defined");

            // If the graph is weighted then get the weight if one exists
            if (weighted) {
                String[] secondParts = lineParts[1].split(weightDelimiter, 2);
                if (secondParts.length == 0) throw new InvalidFileException(fileLineNum, "Only one node defined");
                else if (secondParts.length == 2)
                    lineParts = new String[]{lineParts[0], secondParts[0], secondParts[1]};
            }

            // Remove whitespace
            trimParts(lineParts);

            // If the node names are the same or empty
            if (lineParts[0].equals(lineParts[1]) || lineParts[0].equals("") || lineParts[1].equals(""))
                throw new InvalidFileException(fileLineNum, ((lineParts[0].equals(lineParts[1])) ? "Nodes on an edge cannot be the same" : "Empty node name"));

            // Search the existing nodes for if the node already exists, if it doesn't then create one and add it to the array
            int node1 = -1, node2 = -1;
            for (Node node : nodes) {
                if (node.id() < 0) throw new InvalidFileException(fileLineNum, "The predefined node \"" + node.name() + "\" has an invalid ID \"" + node.id() + "\", ID must be 0 or greater.");
                if (node.name().equals(lineParts[0])) node1 = node.id();
                if (node.name().equals(lineParts[1])) node2 = node.id();
            }

            if (node1 == -1) {
                nodes.add(new Node((node1 = nextID++), lineParts[0]));
                System.out.println("Node created '" + lineParts[0] + "' with id " + node1);
            }
            if (node2 == -1) {
                nodes.add(new Node((node2 = nextID++), lineParts[1]));
                System.out.println("Node created '" + lineParts[1] + "' with id " + node2);
            }

            // Ensure that the IDs are different
            if (node1 == node2) throw new InvalidFileException(fileLineNum, "An edge cannot be made from one node to itself");

            loadedValues.add(lineParts);
        }
        fileScanner.close();

        // If the graph is mixed then use the mixed directions to create edges, otherwise just use directed
        if (directed == null) return createEdges(nodes, loadedValues, mixedDirections);
        return createEdges(nodes, loadedValues, directed);
    }

    /**
     * Calculate the direction of a line if it is a mixed graph.
     * @param line the line to find the direction of
     * @param directedDelimiter the delimiter for a directed line
     * @param undirectedDelimiter the delimiter for an undirected line
     * @return true if the line is directed, false if undirected, null if invalid
     */
    private static Boolean calcDirectedMixed(String line, String directedDelimiter, String undirectedDelimiter) {
        int indexDirected = line.indexOf(directedDelimiter);
        int indexUndirected = line.indexOf(undirectedDelimiter);
        // If neither delimiter was found, or either starts at 0 (meaning there is no content before it)
        if (
                (indexDirected == -1 && indexUndirected == -1)
                || indexDirected == 0
                || indexUndirected == 0
        ) return null;
        if (indexDirected == -1) return false;
        if (indexUndirected == -1) return true;

        // If both delimiters are used exist in the file:
        // If one of the delimiters incorporates the other, like "-" and "->", then take whichever is longer
        // This means that 1-2 and 1->2 would give different results
        if (indexDirected == indexUndirected) return directedDelimiter.length() > undirectedDelimiter.length();
        // If both delimiters are used but at different locations in the line take whichever comes first
        return indexDirected < indexUndirected;
    }

    /**
     * Get the maximum node ID among nodes in an ArrayList.
     * @param nodes the nodes to search
     * @return the greatest ID among the nodes
     */
    private static int getMaxID(ArrayList<Node> nodes) {
        int maxID = -1;
        for (Node node : nodes) {
            if (node.id() > maxID) maxID = node.id();
        }
        return maxID;
    }

    /**
     * Remove leading and trailing whitespace from all strings in an array.
     * @param parts the array to trim
     */
    private static void trimParts(String[] parts) {
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
    }

    /**
     * Load the delimiter from a value the follows the pattern "\"(.*)\"".
     * @param lineNum the line number to be used if an InvalidFileException is thrown
     * @param value the value to be loaded from
     * @return the value stripped of the quotes that were surrounding it
     * @throws InvalidFileException if the value does not start and end with quotes
     */
    private static String loadDelimiter(int lineNum, String value) throws InvalidFileException {
        if (!value.startsWith("\"") || !value.endsWith("\""))
            throw new InvalidFileException(lineNum, "Line is defining a delimiter but not surrounded by quotes (\"\")");
        return value.replaceAll("\"(.*)\"", "$1");
    }

    /**
     * Create {@link Edge edges} from processed data from the Edges.txt file for a directed/undirected graph using
     * {@link #createEdges(ArrayList, ArrayList, boolean, ArrayList) createEdges()}.
     * @param nodes the nodes used for creating the edges
     * @param loadedValues the processed data from a file
     * @param directed true if the edges are directed, false otherwise
     * @return the {@link Edge edges} created
     * @see #createEdges(ArrayList, ArrayList, ArrayList)
     */
    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, boolean directed) {
        return createEdges(nodes, loadedValues, directed, null);
    }

    /**
     * Create {@link Edge edges} from processed data from the Edges.txt file for a graph with a mix of directed and
     * undirected edges {@link #createEdges(ArrayList, ArrayList, boolean, ArrayList) createEdges()}.
     * @param nodes the nodes used for creating the edges
     * @param loadedValues the processed data from a file
     * @param mixedDirections whether each edge in the {@code loadedValues} is directed or not, must be the same
     *                        length as {@code loadedValues}
     * @return the {@link Edge edges} created
     * @see #createEdges(ArrayList, ArrayList, boolean)
     */
    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, ArrayList<Boolean> mixedDirections) {
        if (mixedDirections == null) throw new InvalidFileException("Cannot create edges with invalid directions");
        return createEdges(nodes, loadedValues, true, mixedDirections);
    }

    /**
     * Create {@link Edge edges} from processed data from the Edges.txt file for a graph with either a mix of directed
     * and undirected edges or a graph of all directed/undirected edges.
     * @param nodes the nodes used for creating the edges
     * @param loadedValues the processed data from a file
     * @param directed true if the edges are directed, false otherwise. This value is ignored if mixedDirections is
     *                 not null
     * @param mixedDirections whether each edge in the {@code loadedValues} is directed or not, must be the same
     *                        length as {@code loadedValues}. If this parameter is null then the value in parameter
     *                        {@code directed} will be used for each node instead
     * @return the {@link Edge edges} created
     * @see #createEdges(ArrayList, ArrayList, boolean)
     * @see #createEdges(ArrayList, ArrayList, ArrayList)
     */
    private static ArrayList<Edge> createEdges(ArrayList<Node> nodes, ArrayList<String[]> loadedValues, boolean directed, ArrayList<Boolean> mixedDirections) {
        if (mixedDirections != null && loadedValues.size() != mixedDirections.size()) throw new InvalidFileException("Cannot create edges with invalid directions");

        ArrayList<Edge> edges = new ArrayList<>();
        for (int lineNum = 0; lineNum < loadedValues.size(); lineNum++) {
            String[] line = loadedValues.get(lineNum);
            if (line.length > 3) throw new InvalidFileException("Line data invalid for data line " + lineNum);

            Node node1 = null, node2 = null;
            for (Node node : nodes) {
                if (node.name().equals(line[0])) node1 = node;
                if (node.name().equals(line[1])) node2 = node;
            }
            if (node1 == null || node2 == null || node1.equals(node2)) throw new InvalidFileException((node1 == null || node2 == null) ? "Undefined node" : "Nodes on an edge cannot be the same");

            boolean edgeDirected = directed;
            if (mixedDirections != null) {
                edgeDirected = mixedDirections.get(lineNum);
            }

            try {
                if (line.length == 3) edges.add(new WeightedEdge(node1, node2, edgeDirected, line[2]));
                else edges.add(new Edge(node1, node2, edgeDirected));
            } catch (InvalidEdgeException e) {
                throw new InvalidFileException("A created edge was not valid");
            }
        }

        return edges;
    }
}

