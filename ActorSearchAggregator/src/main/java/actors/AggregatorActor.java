package actors;

import static akka.pattern.Patterns.ask;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import search.APIInfo;

public class AggregatorActor extends AbstractActor {
    public static class Start {
    }

    public static class Stop {
    }

    public static class APIAnswer {
        private final List<String> answers;
        private final APIInfo api;

        public APIAnswer(List<String> answers, APIInfo fromApi) {
            this.answers = answers;
            this.api = fromApi;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public APIInfo getApi() {
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
    private final List<APIInfo> apis;
    private final int resultsNum;
    private final int receiveTimeoutMillis;


    public AggregatorActor(String query, List<APIInfo> apis, int resultsNum,  int receiveTimeoutMillis) {
        this.query = query;
        this.apis = apis;
        this.resultsNum = resultsNum;
        this.receiveTimeoutMillis = receiveTimeoutMillis;
    }

    public static Props props(String query, List<APIInfo> apis, int resultsNum, int receiveTimeoutMillis) {
        return Props.create(AggregatorActor.class, query, apis, resultsNum, receiveTimeoutMillis);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Start.class, r -> {
            try {
                List<CompletableFuture<APIAnswer>> answers = apis.stream().map(api -> {
                    String actorName = api.getName() + "-actor";
                    ActorRef actorRef = getContext().actorOf(QueryActor.props(api), actorName);
                    System.out.println("Create child: " + actorName);
                    return ask(actorRef, new QueryActor.Query(query, resultsNum),
                            Duration.ofMillis(receiveTimeoutMillis)).toCompletableFuture()
                            .thenApply(o -> (APIAnswer) o);
                }).collect(Collectors.toList());
//                CompletableFuture<List<Answer>> result = allOf(answers);
                List<APIAnswer> result = getAllCompleted(answers, receiveTimeoutMillis);
                getSender().tell(new SearchResult(result), getSelf());
            } catch (Exception ex) {
                getSender().tell(
                        new Status.Failure(ex), getSelf());
                throw ex;
            }
        }).match(Stop.class, s -> getContext().stop(getSelf())).build();
    }

    private  <T> List<T> getAllCompleted(List<CompletableFuture<T>> futuresList, long timeout) {
        CompletableFuture<Void> allFuturesResult = CompletableFuture.allOf(
                futuresList.toArray(new CompletableFuture[0]));
        try {
            allFuturesResult.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println("Time is out: " + e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            getSender().tell(
                    new Status.Failure(ex), getSelf());
            throw ex;
        }
        return futuresList
                .stream()
                .filter(future -> future.isDone() && !future.isCompletedExceptionally()) // keep only the ones completed
                .map(CompletableFuture::join) // get the value from the completed future
                .collect(Collectors.<T>toList()); // collect as a list
    }

    /*@Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(false, DeciderBuilder
                .match(RestartException.class, e -> OneForOneStrategy.restart())
                .match(StopException.class, e -> OneForOneStrategy.stop())
                .match(EscalateException.class, e -> OneForOneStrategy.escalate())
                .build());
    }*/


    /*CompletableFuture<List<Answer>> result = Arrays.stream(APIS).map(api -> {
                    String actorName = api + "-actor";
                    ActorRef actorRef = getContext().actorOf(QueryActor.props(api), actorName);
                    System.out.println("Create child: " + actorName);
                    return ask(actorRef, new QueryActor.Query(query, resultsNum),
                            Duration.ofMillis(receiveTimeoutMillis)).toCompletableFuture()
                            .thenApply(o -> (Answer) o);
                }).collect(collectingAndThen(
                        toList(),
                        l -> CompletableFuture.allOf(l.toArray(CompletableFuture[]::new))
                                .thenApply(__ -> l.stream()
                                        .map(CompletableFuture::join)
                                        .collect(Collectors.toList()))));*/

    /*public static <T> CompletableFuture<List<T>> allOf(Collection<CompletableFuture<T>> futures) {
        return futures.stream().collect(collectingAndThen(
                toList(),
                l -> CompletableFuture.allOf(l.toArray(new CompletableFuture[0]))
                        .thenApply(__ -> l.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()))));
    }*/

}
