import clients.AuthorizedHttpAPIClient;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.junit.Assert;
import org.junit.Test;
import search.APIInfo;
import search.SearchAggregator;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;

public class SearchAggregatorTest {
    public final List<APIInfo> apis = List.of(
            new APIInfo("http://localhost:32453/google", "Bearer", "auth", "Google"),
            new APIInfo("http://localhost:32453/yandex", "Bearer", "auth", "Yandex"),
            new APIInfo("http://localhost:32453/bing", "Bearer", "auth", "Bing")
    );

    private static final int PORT = 32453;
    private static final String AUTH_HEADER = AuthorizedHttpAPIClient.authorizationHeader;
    private static final String AUTH_TYPE = "Bearer";
    private static final String AUTH_TOKEN = "auth";

    @Test
    public void searchTest() {
        withStubServer(PORT, s -> {
            whenHttp(s)
                    .match(method(Method.GET),
                            withHeader(AUTH_HEADER, AUTH_TYPE + " " + AUTH_TOKEN),
                            startsWithUri("/google/test"))
                    .then(stringContent("res1G res2G res3G res4G res5G"));
            whenHttp(s)
                    .match(method(Method.GET),
                            withHeader(AUTH_HEADER, AUTH_TYPE + " " + AUTH_TOKEN),
                            startsWithUri("/yandex/test"))
                    .then(stringContent("res1Y res2Y res3Y res4Y res5Y"));
            whenHttp(s)
                    .match(method(Method.GET),
                            withHeader(AUTH_HEADER, AUTH_TYPE + " " + AUTH_TOKEN),
                            startsWithUri("/bing/test"))
                    .then(stringContent("res1B res2B res3B res4B res5B"));
            List<String> result = SearchAggregator.search("test", apis);

            HashSet<String> expected = new HashSet<>(List.of(
                    "Google: res1G", "Google: res2G", "Google: res3G", "Google: res4G", "Google: res5G",
                    "Yandex: res1Y", "Yandex: res2Y", "Yandex: res3Y", "Yandex: res4Y", "Yandex: res5Y",
                    "Bing: res1B", "Bing: res2B", "Bing: res3B", "Bing: res4B", "Bing: res5B"
            ));
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
