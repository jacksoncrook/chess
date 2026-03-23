package client;

import model.GetGamesResult;
import ui.ClientResult;
import ui.ClientResult.Type;

public abstract class Client {
    protected Type type;
    protected static GetGamesResult gameList;

    public Type getType() {
        return type;
    }

    public abstract ClientResult help();
    public abstract ClientResult eval(String input);
    public ClientResult unknownCommand() {
        ClientResult help = help();
        String unknownCommandMessage = """
                Unknown Command:
               """ + help.message();
        return new ClientResult(type, unknownCommandMessage, help.authData());
    }
}
