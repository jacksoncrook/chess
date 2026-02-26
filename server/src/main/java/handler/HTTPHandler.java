package handler;
import io.javalin.http.Context;
import com.google.gson.Gson;
import kotlin.NotImplementedError;

public abstract class HTTPHandler {
    public HTTPRequest fromJson(Context context) {
        return new Gson().fromJson(context.body(), HTTPRequest.class);
    }

    public boolean authorize(HTTPRequest request) {
        throw new NotImplementedError();
    }

    public abstract String toJson();
}
