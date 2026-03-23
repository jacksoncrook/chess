package client;

import com.google.gson.Gson;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData registerRequest) throws Exception {
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(LoginRequest loginRequest) throws Exception {
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    public void logout(LogoutRequest logoutRequest) throws Exception {
        var request = buildRequest("DELETE", "/session", logoutRequest);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void deletePet() throws Exception {
        var request = buildRequest("DELETE", "", null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void deleteAllPets() throws Exception {
        var request = buildRequest("DELETE", "/pet", null);
        sendRequest(request);
    }

    public GetGamesResult listGames(GetGamesRequest getGamesRequest) throws Exception {
        var request = buildRequest("GET", "/game", getGamesRequest);
        var response = sendRequest(request);
        return handleResponse(response, GetGamesResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RequestException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        int status = response.statusCode();
        if (!isSuccessful(status)) {
            if (status / 100 == 5) {
                throw new RequestException("Internal Server Error");
            }

            var body = response.body();
            if (body != null) {
                throw new RequestException(body);
            }

            throw new RequestException(status, "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}