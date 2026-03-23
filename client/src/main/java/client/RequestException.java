package client;

public class RequestException extends Exception {
    private int errorCode;

    public RequestException(String message) {
        super(message);
    }
    public RequestException(int errorCode, String message) {
        this.errorCode = errorCode;
    }

    public int code() {
        return errorCode;
    }
}
