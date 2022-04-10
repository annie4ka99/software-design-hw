package search;

import java.util.List;

public class YandexAPI implements SearchAPI{
    private final String host;
    private final String authType;
    private final String authToken;

    public YandexAPI(String host, String authType, String authToken) {
        this.host = host;
        this.authType = authType;
        this.authToken = authToken;
    }

    @Override
    public String getAuthorizationType() {
        return authType;
    }

    @Override
    public String getAuthorizationToken() {
        return authToken;
    }

    @Override
    public String getName() {
        return "yandex";
    }

    @Override
    public String getSearchQueryURL(String query, int resultsNum) {
        return host + "/" + query;
    }

    @Override
    public List<String> parseResponse(String response) {
        return List.of(response.split("\\s"));
    }
}