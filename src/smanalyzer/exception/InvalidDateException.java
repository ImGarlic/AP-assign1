package smanalyzer.exception;

public class InvalidDateException extends Exception {

    public InvalidDateException(String reason) {
        super("Invalid date: " + reason);
    }

}