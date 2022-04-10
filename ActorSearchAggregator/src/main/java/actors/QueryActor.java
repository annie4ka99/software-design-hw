package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import clients.AuthorizedAPIClient;
import clients.AuthorizedHttpAPIClient;
import search.SearchAPI;

public class QueryActor extends AbstractActor {
    public static class Query {
        private final String query;
        private final int resultsNum;


        public Query(String query, int resultsNum){
            this.query = query;
            this.resultsNum = resultsNum;
        }

        public String getQuery() {
            return query;
        }

        public int getResultsNum() {
            return resultsNum;
        }
    }

    private final SearchAPI api;
    private final AuthorizedAPIClient apiClient;

    public QueryActor(SearchAPI apiInfo) {
        this.api = apiInfo;
        this.apiClient = new AuthorizedHttpAPIClient();
    }

    public static Props props(SearchAPI apiInfo) {
        return Props.create(QueryActor.class, apiInfo);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Query.class, q -> {
            String response = apiClient.get(api.getSearchQueryURL(q.getQuery(), q.getResultsNum()),
                    api.getAuthorizationType(), api.getAuthorizationToken());
            getSender().tell(new AggregatorActor.APIAnswer(api.parseResponse(response), api), getSelf());
        }).build();
    }
}
