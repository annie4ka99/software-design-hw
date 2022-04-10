package search;

import actors.AggregatorActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static akka.pattern.Patterns.ask;

public class SearchAggregator {
    public static List<String> search(String query, List<SearchAPI> apis, int resultsNum, int timeoutMillis) {
        ActorSystem system = ActorSystem.create("SearchSystem");
        ActorRef aggregatorActorRef = system.actorOf(
                AggregatorActor.props(query, apis,resultsNum, timeoutMillis),
                "aggregator");
        AggregatorActor.SearchResult searchResult = (AggregatorActor.SearchResult)ask(aggregatorActorRef,
                new AggregatorActor.Start(),
                Duration.ofMillis(timeoutMillis + 1000)).toCompletableFuture().join();
        aggregatorActorRef.tell(new AggregatorActor.Stop(), ActorRef.noSender());
        List<String> result = new ArrayList<>();
        for (AggregatorActor.APIAnswer APIAnswer : searchResult.getAnswers()) {
            for (String ans : APIAnswer.getAnswers()) {
                result.add(APIAnswer.getApi().getName() + ": " + ans);
            }
        }
        return result;
    }
}
