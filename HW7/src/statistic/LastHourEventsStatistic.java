package statistic;

import clock.Clock;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.HOURS;

public class LastHourEventsStatistic implements EventsStatistic{
    private final static int MINUTES_PER_HOUR = (int) HOURS.getDuration().toMinutes();

    private final Map<String, Queue<Instant>> statistic;
    private final Clock clock;

    public LastHourEventsStatistic(Clock clock) {
        this.statistic = new HashMap<>();
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        Instant now = clock.now();
        updateStatistic(name, now);
        Queue<Instant> prevStatistic = statistic.getOrDefault(name, new LinkedList<>());
        prevStatistic.add(now);
        statistic.put(name, prevStatistic);
    }

    @Override
    public float getEventStatisticByName(String name) {
        Instant now = clock.now();
        return getRpmAndUpdateStatistic(name, now);
    }

    @Override
    public Map<String, Float> getAllEventStatistic() {
        Instant now = clock.now();
        return statistic.keySet().stream()
                .collect(Collectors.toMap(name -> name, name -> getRpmAndUpdateStatistic(name, now)));
    }

    @Override
    public void printStatistic() {
        Map<String, Float> allEventStatistic = getAllEventStatistic();
        allEventStatistic.forEach((name, rpm) -> System.out.println(name + ": " + rpm + "rpm"));
    }

    private float getRpmAndUpdateStatistic(String name, Instant threshold) {
        return (float)updateStatistic(name, threshold) / MINUTES_PER_HOUR;
    }

    private int updateStatistic(String name, Instant now) {
        if (statistic.containsKey(name)) {
            Instant threshold = now.minus(1, HOURS);
            Queue<Instant> eventStatistic = statistic.get(name);
            while (!eventStatistic.isEmpty() && eventStatistic.peek().isBefore(threshold)) {
                eventStatistic.remove();
            }
            statistic.put(name, eventStatistic);
            return eventStatistic.size();
        } else  {
            return 0;
        }
    }

}
