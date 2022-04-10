import clients.AuthorizedHttpAPIClient;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.junit.Assert;
import org.junit.Test;
import search.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.delay;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;

public class SearchAggregatorTest {
    private final List<SearchAPI> apis = List.of(
            new GoogleAPI(String.format("http://localhost:%d/google", PORT), AUTH_TYPE, AUTH_TOKEN),
            new YandexAPI(String.format("http://localhost:%d/yandex", PORT), AUTH_TYPE, AUTH_TOKEN),
            new BingAPI(String.format("http://localhost:%d/bing", PORT), AUTH_TYPE, AUTH_TOKEN)
    );

    private static final int PORT = 32453;
    private static final String AUTH_HEADER = AuthorizedHttpAPIClient.authorizationHeader;
    private static final String AUTH_TYPE = "Bearer";
    private static final String AUTH_TOKEN = "auth";

    @Test
    public void searchTest() {
        withStubServer(PORT, s -> {
            int resultsNum = 5;
            String query = "test";
            List<List<String>> baseResults = new ArrayList<>();
            List<String> finalResults = new ArrayList<>();
            for (SearchAPI api: apis) {
                List<String> apiRes = new ArrayList<>();
                for (int i = 0; i < resultsNum; ++i) {
                    String res = "res" + (i + 1) + "-" + api.getName();
                    apiRes.add(res);
                    finalResults.add(api.getName() + ": " + res);
                }
                baseResults.add(apiRes);
            }
            for (int apiNum = 0; apiNum < apis.size(); ++apiNum) {
                SearchAPI api = apis.get(apiNum);
                List<String> apiRes = baseResults.get(apiNum);
                whenHttp(s)
                        .match(method(Method.GET),
                                withHeader(AUTH_HEADER, AUTH_TYPE + " " + AUTH_TOKEN),
                                startsWithUri("/" + api.getName() + "/" + query))
                        .then(stringContent(String.join(" ", apiRes)));
            }
            List<String> result = SearchAggregator.search(query, apis, resultsNum, 5000);

            HashSet<String> expected = new HashSet<>(finalResults);
            HashSet<String> actual = new HashSet<>(result);
            Assert.assertEquals(expected, actual);
        });

    }

    @Test
    public void searchTestWithServerDelay() {
        withStubServer(PORT, s -> {
            int timeoutMillis = 5000;
            int resultsNum = 5;
            String query = "test";
            List<String> finalResults = new ArrayList<>();
            int delayedAPINum = apis.size() - 1;
            for (int apiNum = 0; apiNum < apis.size(); ++apiNum) {
                SearchAPI api = apis.get(apiNum);
                List<String> apiRes = new ArrayList<>();
                for (int i = 0; i < resultsNum; ++i) {
                    String res = "res" + (i + 1) + "-" + api.getName();
                    apiRes.add(res);
                    if (!(apiNum == delayedAPINum))
                        finalResults.add(api.getName() + ": " + res);
                }
                if (apiNum == delayedAPINum) {
                    whenHttp(s)
                            .match(method(Method.GET),
                                    withHeader(AUTH_HEADER, AUTH_TYPE + " " + AUTH_TOKEN),
                                    startsWithUri("/" + api.getName() + "/" + query))
                            .then(stringContent(String.join(" ", apiRes)),
                                    delay(timeoutMillis + 1000));
                } else {
                    whenHttp(s)
                            .match(method(Method.GET),
                                    withHeader(AUTH_HEADER, AUTH_TYPE + " " + AUTH_TOKEN),
                                    startsWithUri("/" + api.getName() + "/" + query))
                            .then(stringContent(String.join(" ", apiRes)));
                }

            }
            List<String> result = SearchAggregator.search(query, apis, resultsNum, timeoutMillis);

            HashSet<String> expected = new HashSet<>(finalResults);
            HashSet<String> actual = new HashSet<>(result);
            Assert.assertEquals(expected, actual);
        });

    }

    private void withStubServer(int port, Consumer<StubServer> callback) {
        StubServer stubServer = null;
        try {
            stubServer = new StubServer(port).run();
            callback.accept(stubServer);
        } finally {
            if (stubServer != null) {
                stubServer.stop();
            }
        }
    }

}
