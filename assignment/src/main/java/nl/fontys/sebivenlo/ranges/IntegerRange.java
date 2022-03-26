package nl.fontys.sebivenlo.ranges;

import java.util.function.BiFunction;

/**
 * Simple integer based range. This is the first leaf class and it is used to
 * test the default methods in Range.
 *
 *
 * @author Pieter van den Hombergh {@code pieter.van.den.hombergh@gmail.com}
 */
public class IntegerRange implements Range<IntegerRange, Integer, Integer> {
    private final Integer start;
    private final Integer end;

    private IntegerRange(Integer a, Integer b) {
        Integer[] i = Range.minmax( a, b );

        this.start = i[0];
        this.end = i[1];
    }

    @Override
    public Integer start() {
        return this.start;
    }

    @Override
    public Integer end() {
        return this.end;
    }

    @Override
    public BiFunction<Integer, Integer, Integer> meter() {
        return (a,b)-> b - a;
    }

    @Override
    public IntegerRange between( Integer start, Integer end ) {
        return of(start, end);
    }

    // since the methods hashCode, equals and toString are defined in Object,
    // you cannot overwrite them in an interface. The best you can do is invoke the methods
    // of the interface or use the new java record type, finalized in Java 16.
    @Override
    public int hashCode() {
        return rangeHashCode();
    }

    @Override
    @SuppressWarnings( "EqualsWhichDoesntCheckParameterClass" )
    public boolean equals( Object obj ) {
        return rangeEquals( obj );
    }

    @Override
    public String toString() {
        return rangeToString();
    }

    @Override
    public Integer zero() {
        return 0;
    }

    /**
     * ConvenienceFactory.
     *
     * @param start of range
     * @param end of range
     * @return the range
     */
    public static IntegerRange of( Integer start, Integer end ) {
        return new IntegerRange(start, end);
    }

}
