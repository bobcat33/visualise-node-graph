package visualiser.graphics.canvas;

public class ConcurrentFreezeActionException extends RuntimeException {
    /**
     * "Attempted to " + action + " during freezing process."
     * @param action action that happened during freezing process
     */
    public ConcurrentFreezeActionException(String action) {
        super("Attempted to " + action + " during freezing process.");
    }
}
