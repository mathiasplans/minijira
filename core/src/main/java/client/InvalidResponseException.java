package client;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException() {
        super();
    }

    public InvalidResponseException(String message) {
        super(message);
    }

    public InvalidResponseException(String message, Throwable cause){
        super(message, cause);
    }

    public InvalidResponseException(Throwable cause){
        super(cause);
    }
}
