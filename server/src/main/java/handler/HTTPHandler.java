package handler;
import io.javalin.http.Context;
import com.google.gson.Gson;
import io.javalin.http.Handler;
import kotlin.NotImplementedError;

public abstract class HTTPHandler implements Handler {
    public HTTPRequest fromJson(Context context) {
        return new Gson().fromJson(context.body(), HTTPRequest.class);
    }

    public boolean authorize(HTTPRequest request) {
        throw new NotImplementedError();
    }

    public abstract String toJson();
}
