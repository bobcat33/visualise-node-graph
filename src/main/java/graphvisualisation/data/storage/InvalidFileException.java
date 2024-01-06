package graphvisualisation.data.storage;

public class InvalidFileException extends Exception {

    public InvalidFileException() {
        super("File contains invalid properties.");
    }

    public InvalidFileException(int line) {
        super("File contains invalid properties at line " + line + ".");
    }

    // todo: add reason

}
