package hexlet.code;

public class NoSuchUserException extends RuntimeException {
//    public NoSuchUserException(Throwable cause) {
//        super(cause);
//    }

    public NoSuchUserException(String message) {
        super(message);
    }
}
