package smanalyzer.exception;

public class InvalidPostException extends Exception {

    public InvalidPostException(String reason) {
        super("Invalid post: " + reason);
    }
}
