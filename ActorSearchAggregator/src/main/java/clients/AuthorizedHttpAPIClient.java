package clients;

import exceptions.UnauthorizedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AuthorizedHttpAPIClient implements AuthorizedAPIClient {
    public static final String authorizationHeader = "Authorization";

    public String get(String query, String authType, String authToken) throws UnauthorizedException {
        URL url;
        try {
            url = new URL(query);
        } catch (MalformedURLException e) {
        throw new RuntimeException("Malformed url");
        }
        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(authorizationHeader, authType + " " + authToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new UnauthorizedException("Incorrect authorization");
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to set up connection. HTTP Response Code:" + responseCode);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder responseBuffer = new StringBuilder();
            String inputLine;
            while((inputLine=in.readLine()) != null) {
                responseBuffer.append(inputLine);
                responseBuffer.append('\n');
            }

            in.close();

            return responseBuffer.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
