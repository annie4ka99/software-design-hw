package clients;

public interface AuthorizedAPIClient {
    String get(String query, String authType, String authToken);
}
