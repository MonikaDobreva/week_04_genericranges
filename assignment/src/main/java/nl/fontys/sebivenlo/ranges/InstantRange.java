package nl.fontys.sebivenlo.ranges;

import java.time.Duration;
import java.time.Instant;
import java.util.function.BiFunction;

public class InstantRange implements Range<InstantRange, Instant, Duration>{
    private final Instant start;
    private final Instant end;

    private InstantRange(Instant[] i) {
        this.start = Range.min(i[0], i[1]);
        this.end = Range.max(i[0], i[1]);
    }

    @Override
    public Instant start() {
        return this.start;
    }

    @Override
    public Instant end() {
        return this.end;
    }

    @Override
    public BiFunction<Instant, Instant, Duration> meter() {
        return Duration::between;
    }

    @Override
    public InstantRange between(Instant startInclusive, Instant endExclusive) {
        return InstantRange.of(startInclusive, endExclusive);
    }

    @Override
    public int hashCode() {
        return rangeHashCode();
    }

    @Override
    @SuppressWarnings( "EqualsWhichDoesntCheckParameterClass" )
    public boolean equals(Object obj) {
        return rangeEquals(obj);
    }

    @Override
    public Duration zero() {
        return Duration.ZERO;
    }

    @Override
    public String toString() {
        return rangeToString();
    }

    public static InstantRange of( Instant start, Instant end ) {
        Instant[] i = {start, end};
        return new InstantRange(i);
    }
}
