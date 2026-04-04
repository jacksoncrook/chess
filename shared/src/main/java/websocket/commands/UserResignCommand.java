package websocket.commands;

import java.util.Objects;

public class UserResignCommand extends UserGameCommand {
    public String teamColor;

    public UserResignCommand(String authToken, int gameID, String teamColor) {
        super(CommandType.RESIGN, authToken, gameID);
        this.teamColor = teamColor;
    }

    public String getTeamColor() {
        return this.teamColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserResignCommand that)) {
            return false;
        }
        return Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID()) &&
                Objects.equals(getTeamColor(), that.getTeamColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID(), getTeamColor());
    }
}
