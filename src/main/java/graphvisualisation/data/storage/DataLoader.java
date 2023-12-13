package graphvisualisation.data.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

// todo: at the moment unable to load nodes with ID less than 0, fix pls
// todo: duplicate code can be rewritten

public class DataLoader {
    private static final File file = new File("src/main/java/graphvisualisation/data/storage/Edges.txt");

    public static int getNumberOfNodes() throws FileNotFoundException, InvalidFileException {

        Scanner fileScanner = new Scanner(file);

        HashSet<Integer> nodes = new HashSet<>();
        int x, y;

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {

            String[] line = fileScanner.nextLine().split(" ");
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

    public static boolean[][] asMatrix(int size) throws FileNotFoundException, InvalidFileException {

        Scanner fileScanner = new Scanner(file);

        boolean[][] matrix = new boolean[size][size];

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            String[] line = fileScanner.nextLine().split(" ");
            lineNum++;

            int x = -1, y = -1;

            try {
                x = Integer.parseInt(line[0]);
                y = Integer.parseInt(line[1]);
            } catch (NumberFormatException ignored) {}

            if (x < 0 || x > size-1 || y < 0 || y > size-1) throw new InvalidFileException(lineNum);

            matrix[x][y] = true;

        }

        return matrix;

    }

    public static boolean[][] loadFileAsMatrix() throws FileNotFoundException, InvalidFileException {
        return asMatrix(getNumberOfNodes());
    }

    public static HashMap<Integer, ArrayList<Integer>> asAdjacencyMap(int size) throws FileNotFoundException, InvalidFileException {

        Scanner fileScanner = new Scanner(file);

        HashMap<Integer, ArrayList<Integer>> adjacencies = new HashMap<>();
        for (int i = 0; i < size; i++) adjacencies.put(i, new ArrayList<>());

        int lineNum = 0;
        while (fileScanner.hasNextLine()) {
            String[] line = fileScanner.nextLine().split(" ");
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

    public static HashMap<Integer, ArrayList<Integer>> loadFileAsAdjacencyMap() throws FileNotFoundException, InvalidFileException {
        return asAdjacencyMap(getNumberOfNodes());
    }

}
