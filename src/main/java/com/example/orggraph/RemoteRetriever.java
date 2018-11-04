package com.example.orggraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.google.gson.Gson;

import graphql.ExecutionInput;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteRetriever<C> implements GraphQLRemoteRetriever<C> {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String url;

    RemoteRetriever(String url) {
        this.url = url;
    }

	@Override
	public CompletableFuture<Map<String, Object>> queryGraphQL(ExecutionInput executionInput, C context) {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();

        Map<String, Object> bodyMap = Map.of("query", executionInput.getQuery(), "variables", executionInput.getVariables());

        String json = gson.toJson(bodyMap);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        try {
            Response response = client.newCall(request).execute();
            HashMap<String, Object> jsonResult = gson.fromJson(response.body().string(), HashMap.class);
            return CompletableFuture.completedFuture(jsonResult);
        } catch(IOException error) {
            System.out.println(error);
            return CompletableFuture.failedFuture(error);
        }
	}
}