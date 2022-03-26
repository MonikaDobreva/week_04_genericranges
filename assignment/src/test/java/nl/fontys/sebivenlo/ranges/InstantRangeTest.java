package nl.fontys.sebivenlo.ranges;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class InstantRangeTest extends RangeTestBase<InstantRange, Instant, Duration> {

    RangeTestDataFactory<InstantRange, Instant, Duration> daf;
    Instant[] points = {
            Instant.EPOCH.plus(42, ChronoUnit.HOURS),
            Instant.EPOCH.plus(51, ChronoUnit.HOURS),
            Instant.EPOCH.plus(55, ChronoUnit.HOURS),
            Instant.EPOCH.plus(1023, ChronoUnit.HOURS),
            Instant.EPOCH.plus(1610, ChronoUnit.HOURS),
            Instant.EPOCH.plus(2840, ChronoUnit.HOURS)
    };

    @Override
    RangeTestDataFactory helper() {
        if (null == daf) {
            daf = new RangeTestDataFactory<>(points) {
                @Override
                InstantRange createRange(Instant start, Instant end) {
                    return InstantRange.of(start, end);
                }

                @Override
                Duration distance(Instant a, Instant b) {
                    return Duration.between(a, b);
                }
            };
        }
        return daf;
    }
}
