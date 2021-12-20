package statistic;

import java.util.Map;

public interface EventsStatistic {
    void incEvent(String name);
    float getEventStatisticByName(String name);
    Map<String, Float> getAllEventStatistic();
    void printStatistic();
}