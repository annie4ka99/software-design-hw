package search;

public class APIInfo {
    private final String host;
    private final String authType;
    private final String authToken;
    private final String name;

    public APIInfo(String host, String authType, String authToken, String name) {
        this.host = host;
        this.authType = authType;
        this.authToken = authToken;
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public String getAuthType() {
        return authType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getName() {
        return name;
    }
}
