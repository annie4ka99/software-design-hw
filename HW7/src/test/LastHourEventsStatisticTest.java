package test;

import clock.SettableClock;
import org.junit.Assert;
import org.junit.Test;
import statistic.EventsStatistic;
import statistic.LastHourEventsStatistic;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static java.time.temporal.ChronoUnit.HOURS;

public class LastHourEventsStatisticTest {
    private static final int MINUTES_PER_HOUR = (int) HOURS.getDuration().toMinutes();
    private static final float DELTA = 1e-6f;

    private final Instant initTime = Instant.now();
    private final SettableClock clock = new SettableClock(initTime);
    private final String event1 = "event1";
    private final String event2 = "event2";
    private final String event3 = "event3";

    private EventsStatistic eventStatistic;

    @Test
    public void getEventStatisticByNameTest() {
        clock.setNow(initTime);
        eventStatistic = new LastHourEventsStatistic(clock);

        incEvent(event1, timeAfter(0, 0));
        incEvent(event1, timeAfter(0, 1));
        incEvent(event1, timeAfter(0, 2));

        Assert.assertEquals(getEventStatistic(event1, timeAfter(0,3)),
                calcRPM(3), DELTA);

        incEvent(event3, timeAfter(0, 3));
        incEvent(event1, timeAfter(0,4));
        incEvent(event2, timeAfter(0,5));
        incEvent(event2, timeAfter(0,6));

        Assert.assertEquals(getEventStatistic(event1, timeAfter(0,7)),
                calcRPM(4), DELTA);
        Assert.assertEquals(getEventStatistic(event2, timeAfter(0,8)),
                calcRPM(2), DELTA);


        Assert.assertEquals(getEventStatistic(event1, timeAfter(1,3)),
                calcRPM(1), DELTA);
        Assert.assertEquals(getEventStatistic(event3, timeAfter(1, 4)),
                calcRPM(0), DELTA);
        Assert.assertEquals(getEventStatistic(event2, timeAfter(1,6)),
                calcRPM(1), DELTA);
    }

    @Test
    public void getAllEventStatisticTest() {
        clock.setNow(initTime);
        eventStatistic = new LastHourEventsStatistic(clock);

        incEvent(event1, timeAfter(0, 0));
        incEvent(event2, timeAfter(0,1));
        incEvent(event1, timeAfter(0, 2));
        incEvent(event2, timeAfter(0,3));
        incEvent(event2, timeAfter(0,4));
        incEvent(event3, timeAfter(1, 1));

        Assert.assertTrue(compareAllStatistics(getAllEventStatistic(timeAfter(1, 2)), Map.of(
                event1, calcRPM(1),
                event2, calcRPM(2),
                event3, calcRPM(1))));
    }


    private void incEvent(String name, Instant now) {
        clock.setNow(now);
        eventStatistic.incEvent(name);
    }

    private float getEventStatistic(String name, Instant now) {
        clock.setNow(now);
        return eventStatistic.getEventStatisticByName(name);
    }

    private Map<String, Float> getAllEventStatistic(Instant now) {
        clock.setNow(now);
        return eventStatistic.getAllEventStatistic();
    }

    private float calcRPM(int timesPerHour) {
        return (float) timesPerHour / MINUTES_PER_HOUR;
    }

    private Instant timeAfter(int hours, int minutes) {
        return initTime.plus(Duration.ofHours(hours)).plus(Duration.ofMinutes(minutes));
    }

    private boolean compareAllStatistics (Map<String, Float> actual, Map<String, Float> expected) {
        if (actual.size() != expected.size())
            return false;
        for (Map.Entry<String, Float> entry: expected.entrySet()) {
            Float val = actual.get(entry.getKey());
            if (val == null || Math.abs(val - entry.getValue()) > DELTA)
                return false;
        }
        return true;
    }
}
