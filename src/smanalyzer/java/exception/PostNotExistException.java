package smanalyzer.java.exception;

public class PostNotExistException extends Exception{
    int ID;

    public PostNotExistException(int ID) {
        this.ID = ID;
    }

    @Override
    public String getMessage() {
        String message = String.format("Post of ID %d does not exist in the collection.", ID);
        return message;
    }
}
