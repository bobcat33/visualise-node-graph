package graphvisualisation.data.graphdata;

public class InvalidFileException extends RuntimeException {

    public InvalidFileException() {
        super("File contains invalid properties.");
    }

    public InvalidFileException(int line) {
        super("File contains invalid properties at line " + line + ".");
    }

    // todo: add reason

}
