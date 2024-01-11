package visualiser.data.graphdata;

public class InvalidFileException extends RuntimeException {

    /**
     * "Invalid file - " + reason
     * @param reason the reason the exception was thrown
     */
    public InvalidFileException(String reason) {
        super("Invalid file - " + reason);
    }

    /**
     * "File contains invalid properties at line " + line + " - " + reason
     * @param line the line number in the file that the exception was thrown at
     * @param reason the reason the exception was thrown
     */
    public InvalidFileException(int line, String reason) {
        super("File contains invalid properties at line " + line + " - " + reason);
    }

}
