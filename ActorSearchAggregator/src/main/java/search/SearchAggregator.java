package search;

import actors.AggregatorActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static akka.pattern.Patterns.ask;

public class SearchAggregator {
    public static List<String> search(String query, List<APIInfo> apis) {
        ActorSystem system = ActorSystem.create("SearchSystem");
        ActorRef aggregatorActorRef = system.actorOf(
                AggregatorActor.props(query, apis,5, 5000),
                "aggregator");
        AggregatorActor.SearchResult searchResult = (AggregatorActor.SearchResult)ask(aggregatorActorRef,
                new AggregatorActor.Start(),
                Duration.ofMillis(5000)).toCompletableFuture().join();
        List<String> result = new ArrayList<>();
        for (AggregatorActor.APIAnswer APIAnswer : searchResult.getAnswers()) {
            for (String ans : APIAnswer.getAnswers()) {
                result.add(APIAnswer.getApi().getName() + ": " + ans);
            }
        }
        return result;
    }
}
