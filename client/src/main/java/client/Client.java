package client;

import ui.ClientResult;
import ui.ClientResult.Type;

public abstract class Client {
    private Type type;

    public Type getType() {
        return type;
    }

    public abstract ClientResult help();
    public abstract ClientResult eval(String input);
    public ClientResult unknownCommand() {
        String unknownCommandMessage = """
                Unknown Command:
               """ + help().message();
        return new ClientResult(type, unknownCommandMessage);
    }
}
