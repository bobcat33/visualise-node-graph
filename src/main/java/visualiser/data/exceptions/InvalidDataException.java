package visualiser.data.exceptions;

public class InvalidDataException extends RuntimeException {
    /**
     * "Attempted to create data with invalid properties."
     */
    public InvalidDataException() {
        super("Attempted to create data with invalid properties.");
    }

    /**
     * @param reason the error message
     */
    public InvalidDataException(String reason) {
        super(reason);
    }
}
