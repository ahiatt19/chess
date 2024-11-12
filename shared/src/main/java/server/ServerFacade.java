package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import model.LoginRequest;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public AuthData register(UserData userData) throws Exception {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(LoginRequest loginRequest) throws Exception {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, AuthData.class, null);
    }

    public void logout(String authToken) throws Exception {
        var path = "/session";
        System.out.println("LOGOUT");
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public void clear() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }




    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String header) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            System.out.println("makereq: " + header);

            writeBody(request, http, header);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http, String header) throws IOException {
        if (request != null) {
            System.out.println(header);
            http.addRequestProperty("Content-Type", "application/json");
            if (header != null) {
                http.addRequestProperty("Authorization", header);
            }
            System.out.println("properties: " + http.getRequestProperties());
            String reqData = new Gson().toJson(request);
            System.out.println("REQDATA " + reqData);
            try (OutputStream reqBody = http.getOutputStream()) {
                System.out.println("output stream");
                reqBody.write(reqData.getBytes());
            }
        }
        else if (header != null) {
            http.addRequestProperty("Authorization", header);
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (status != 200) {
            throw new Exception("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
