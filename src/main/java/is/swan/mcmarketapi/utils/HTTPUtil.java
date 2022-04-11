package is.swan.mcmarketapi.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import is.swan.mcmarketapi.Token;
import is.swan.mcmarketapi.request.Error;
import is.swan.mcmarketapi.request.Response;
import okhttp3.*;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.net.URI;

public class HTTPUtil {

    private static final OkHttpClient client = new OkHttpClient.Builder().build();
    private static final Gson GSON = new Gson();

    private static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    public static <V> Response<V> get(String url, Token token) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token.toString())
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Call call = client.newCall(request);
            okhttp3.Response response = call.execute();
            if (response.isSuccessful()) {
                return getResponse(response);
            } else {
                return new Response<V>("").setError(new Error(String.valueOf(response.code()), response.message()));
            }
        } catch (IOException e) {
            return new Response<V>("").setError(new Error(e.getClass().getName(), e.getMessage()));
        }
    }

    public static <V> Response<V> post(String url, String body, Token token) {
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token.toString())
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try {
            Call call = client.newCall(request);
            okhttp3.Response response = call.execute();
            if (response.isSuccessful()) {
                return getResponse(response);
            } else {
                return new Response<V>("").setError(new Error(String.valueOf(response.code()), response.message()));
            }
        } catch (IOException e) {
            return new Response<V>("").setError(new Error(e.getClass().getName(), e.getMessage()));
        }
    }

    public static <V> Response<V> delete(String url, Token token) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token.toString())
                .delete()
                .build();

        try {
            Call call = client.newCall(request);
            okhttp3.Response response = call.execute();
            if (response.isSuccessful()) {
                return getResponse(response);
            } else {
                return new Response<V>("").setError(new Error(String.valueOf(response.code()), response.message()));
            }
        } catch (IOException e) {
            return new Response<V>("").setError(new Error(e.getClass().getName(), e.getMessage()));
        }
    }

    public static <V> Response<V> patch(String url, String body, Token token) {
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token.toString())
                .patch(requestBody)
                .build();

        try {
            Call call = client.newCall(request);
            okhttp3.Response response = call.execute();
            if (response.isSuccessful()) {
                return getResponse(response);
            } else {
                return new Response<V>("").setError(new Error(String.valueOf(response.code()), response.message()));
            }
        } catch (IOException e) {
            return new Response<V>("").setError(new Error(e.getClass().getName(), e.getMessage()));
        }
    }

    private static <V> Response<V> getResponse(okhttp3.Response httpResponse) {
        Response<V> response = null;
        try {
            response = new Response<>(httpResponse.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (httpResponse.headers("Retry-After").size() > 0) {
            response.setRatelimited(true);
            response.setMillisecondsToWait(Integer.parseInt(httpResponse.headers("Retry-After").get(0)));
        } else {
            try {
                JsonElement jsonElement = GSON.fromJson(httpResponse.body().string(), JsonElement.class);
                String result = jsonElement.getAsJsonObject().get("result").getAsString();

                if (result.equals("error")) {
                    String errorJson = jsonElement.getAsJsonObject().get("error").getAsJsonObject().toString();
                    Error error = GSON.fromJson(errorJson, Error.class);

                    response.setError(error);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}