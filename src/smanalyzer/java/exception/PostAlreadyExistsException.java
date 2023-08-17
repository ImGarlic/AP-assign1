package smanalyzer.java.exception;

public class PostAlreadyExistsException extends Exception{
    int ID;

    public PostAlreadyExistsException(int ID) {
        this.ID = ID;
    }

    @Override
    public String getMessage() {
        String message = String.format("Post of ID %d already exists in the collection.", ID);
        return message;
    }
}
