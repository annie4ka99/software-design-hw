package http;

import handler.CatalogQueryHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

public class RxNettyHttpServer {
    private static CatalogQueryHandler requestHandler = new CatalogQueryHandler();

    public static void main(final String[] args) {
        HttpServer
                .newServer(8080)
                .start((req, resp) -> {

                    String method = req.getDecodedPath().substring(1);

                    Observable<String> response;
                    try {
                        response = requestHandler.handleQuery(method, req.getQueryParameters());
                        resp.setStatus(HttpResponseStatus.OK);
                    } catch (RuntimeException e) {
                        response = Observable.just(e.getMessage());
                        resp.setStatus(HttpResponseStatus.BAD_REQUEST);
                    }

                    return resp.writeString(response);
                })
                .awaitShutdown();
    }
}
