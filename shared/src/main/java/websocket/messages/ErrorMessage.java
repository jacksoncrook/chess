package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

    public String message() {
        return errorMessage;
    }
}
