package smanalyzer.java.exception;

public class InvalidMenuOptionException extends Exception {

    public InvalidMenuOptionException() {
        super("Please select 1-6.");
    }
    
}
