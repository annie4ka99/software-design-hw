package search;

import java.util.List;

public interface SearchAPI {
    public String getAuthorizationType();

    public String getAuthorizationToken();

    public String getName();

    public String getSearchQueryURL(String query, int resultsNum);

    public List<String> parseResponse(String response);
}
