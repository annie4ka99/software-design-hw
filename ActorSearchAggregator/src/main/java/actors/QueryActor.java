package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import clients.AuthorizedAPIClient;
import clients.AuthorizedHttpAPIClient;
import search.APIInfo;

import java.util.List;

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

    private final APIInfo api;
    private final AuthorizedAPIClient apiClient;

    public QueryActor(APIInfo apiInfo) {
        this.api = apiInfo;
        this.apiClient = new AuthorizedHttpAPIClient();
    }

    public static Props props(APIInfo apiInfo) {
        return Props.create(QueryActor.class, apiInfo);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Query.class, q -> {
            String response = apiClient.get(api.getHost()+ '/'+ q.getQuery()
//                            + "c=" + q.getResultsNum()
                    ,
                    api.getAuthType(), api.getAuthToken());
            getSender().tell(new AggregatorActor.APIAnswer(List.of(response.split("\\s+")), api), getSelf());
        }).build();
    }
}
