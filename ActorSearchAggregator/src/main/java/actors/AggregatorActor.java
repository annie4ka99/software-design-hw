package actors;

import static akka.pattern.Patterns.ask;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import search.SearchAPI;

public class AggregatorActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static class Start {
    }

    public static class Stop {
    }

    public static class APIAnswer {
        private final List<String> answers;
        private final SearchAPI api;

        public APIAnswer(List<String> answers, SearchAPI fromApi) {
            this.answers = answers;
            this.api = fromApi;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public SearchAPI getApi() {
            return api;
        }
    }

    public static class SearchResult {
        private final List<APIAnswer> answers;

        public SearchResult(List<APIAnswer> answers) {
            this.answers = answers;
        }

        public List<APIAnswer> getAnswers() {
            return answers;
        }
    }

    private final String query;
    private final List<SearchAPI> apis;
    private final int resultsNum;
    private final int receiveTimeoutMillis;


    public AggregatorActor(String query, List<SearchAPI> apis, int resultsNum,  int receiveTimeoutMillis) {
        this.query = query;
        this.apis = apis;
        this.resultsNum = resultsNum;
        this.receiveTimeoutMillis = receiveTimeoutMillis;
    }

    public static Props props(String query, List<SearchAPI> apis, int resultsNum, int receiveTimeoutMillis) {
        return Props.create(AggregatorActor.class, query, apis, resultsNum, receiveTimeoutMillis);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Start.class, r -> {
            List<CompletableFuture<APIAnswer>> answers = apis.stream().map(api -> {
                String actorName = api.getName() + "-actor";
                ActorRef actorRef = getContext().actorOf(QueryActor.props(api), actorName);
                log.info("Create child: {}", actorName);
                return ask(actorRef, new QueryActor.Query(query, resultsNum),
                        Duration.ofMillis(receiveTimeoutMillis)).toCompletableFuture()
                        .thenApply(o -> (APIAnswer) o);
            }).collect(Collectors.toList());
            CompletableFuture<Void> allFuturesResult = CompletableFuture.allOf(
                    answers.toArray(CompletableFuture[]::new));
            try {
                allFuturesResult.get(receiveTimeoutMillis, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                log.info("Time is out");
            } catch (Exception ex) {
                getSender().tell(
                        new Status.Failure(ex), getSelf());
                throw ex;
            }
            List<APIAnswer> result = answers
                    .stream()
                    .filter(future -> future.isDone() && !future.isCompletedExceptionally())
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            getSender().tell(new SearchResult(result), getSelf());
        }).match(Stop.class, s -> getContext().stop(getSelf())).build();
    }

    /*@Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(false, DeciderBuilder
                .match(RestartException.class, e -> OneForOneStrategy.restart())
                .match(StopException.class, e -> OneForOneStrategy.stop())
                .match(EscalateException.class, e -> OneForOneStrategy.escalate())
                .build());
    }*/

}
