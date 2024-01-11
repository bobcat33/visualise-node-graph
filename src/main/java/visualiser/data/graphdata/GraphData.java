package visualiser.data.graphdata;

import visualiser.data.graphdata.elements.Edge;
import visualiser.data.graphdata.elements.Node;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GraphData {
    private final ArrayList<Node> nodes;
    private final ArrayList<Edge> edges;

    public GraphData() throws InvalidFileException, FileNotFoundException {
        nodes = DataLoader.loadNodes();
        edges = DataLoader.loadEdges(nodes);
    }

    public GraphData(ArrayList<Node> nodes, ArrayList<Edge> edges) {
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