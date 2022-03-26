package nl.fontys.sebivenlo.ranges;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * The range's reason of existence is testing, measuring, finding or avoiding
 * overlaps.
 *
 * <p>
 * The range should comply to the invariant that the start is less or equal it's
 * end. Depending on the type, this can mean start is (left of, before, lower)
 * than end, or equal to end.</p>
 *
 * <p>
 * This range definition is of the <i>half open</i> type. The start is included
 * in the range, the end is not. The mathematical notation for half open ranges
 * of this kind is [start,end), indeed with two different brackets. The square
 * bracket is the closed end, the parenthesis is at the open end.</p>
 *
 * <p>
 * The demarcation and distance units can be of different types, like a dates as
 * demarcation and days between them.</p>
 *
 * @author Pieter van den Hombergh {@code pieter.van.den.hombergh@gmail.com}
 * @param <R> Range, always needed in definition of implementing class since
 * this interface has a recursive generic definition.
 * @param <P> the demarcation point type. Type of start and end.
 * @param <D> the distance dimension. Like days between dates.
 */
public interface Range<R extends Range<R, P, D>, P extends Comparable<? super P>, D extends Comparable<? super D>>
        extends Comparable<R>, Serializable {

    /**
     * Get the start demarcation of this range. Start is part of this range.
     *
     * @return start
     */
    P start();

    /**
     * Get the end demarcation of this range. All points in the range are before
     * (less than) the end of this range.
     *
     * @return end
     */
    P end();

    /**
     * Is a point contained in this range.
     *
     * @param point the point
     * @return true is point not before start and not after end.
     */
    default boolean contains( P point ) {
        return this.start().compareTo(point) <= 0 && this.end().compareTo(point) > 0;
    }

    /**
     * Does this range overlap with another one.
     *
     * @param other to check
     * @return true on overlap with other
     */
    default boolean overlaps( R other ) {
        P firstEnd = min(this.end(), other.end());
        P secondStart = max(this.start(), other.start());
        boolean comp = firstEnd.compareTo(secondStart) > 0;
        return comp;
    }

    /**
     * Obvious maximum function to get the maximum or right most of two
     * comparable elements.
     *
     * @param <Z> type of elements
     * @param a element a
     * @param b element b
     * @return the maximum of a and b
     */
    static <Z extends Comparable<? super Z>> Z max( Z a, Z b ) {
        return a.compareTo( b ) >= 0 ? a : b;
    }

    /**
     * Obvious minimum function to get the minimum or left most of two
     * comparable elements.
     *
     * @param <Z> type of elements
     * @param a element a
     * @param b element b
     * @return the minimum of a and b
     */
    static <Z extends Comparable<? super Z>> Z min( Z a, Z b ) {
        return a.compareTo( b ) <= 0 ? a : b;
    }

    /**
     * Get the length of this range in some unit.The effective operation is (end
     * - start), but since we do not know how to subtract the T-type, that is
     * left to the implementer. The exception thrown on incompatibility of range
     * and unit is also left to the implementer.
     *
     * @return the length
     * @throws RuntimeException when the unit and this range are not compatible
     */
    default D length() {
        return meter().apply( this.start(), this.end() );
    }

//    long length(Object unit);
    /**
     * Compute the length that this and an other range overlap.
     *
     * Compute the distance between the left most end and the right most start.
     *
     *
     * @param other range
     * @return the length of the overlap
     */
    default D overlap( R other ) {
        P firstEnd = min(this.end(), other.end());
        P secondStart = max(this.start(), other.start());

        if (firstEnd.compareTo(secondStart) <= 0) {
            return zero();
        }
        return meter().apply(secondStart, firstEnd);
    }

    /**
     * Does this and other just meet. this.start== other.end or
     * other.start=this.end
     *
     * @param other range
     * @return meet
     */
    default boolean meets( R other ) {
        return max( this.start(), other.start() ).equals( min( this.end(), other
                .end() ) );
    }

    /**
     * Helper to check that join is allowed, or intersection or punchThrough is
     * meaningful.
     *
     * @param other to test
     * @throws IllegalArgumentException when overlap is not possible
     */
    default void checkMeetsOrOverlaps( R other ) {
        if ( !( meets( other ) || overlaps( other ) ) ) {
            throw new IllegalArgumentException( "this range " + this.toString()
                    + " and other " + other
                            .toString()
                    + " do not meet nor overlap" );
        }

    }

    /**
     * Join this range with other range.
     *
     * @param other range to join
     * @return new joined range.
     * @throws IllegalArgumentException when this and other have no points in
     * common, in other words do not overlap or meet.
     */
    default R joinWith( R other ) throws IllegalArgumentException {
        checkMeetsOrOverlaps( other );
        return between( min( this.start(), other.start() ), max( this.end(),
                other.end() ) );
    }

    /**
     * Get the method to determine distances between points.
     *
     * @return a function to compute the distance from a to b.
     */
    BiFunction<P, P, D> meter();

    /**
     * Helper to avoid code duplication.
     *
     * @return the hash code for this object
     */
    default int rangeHashCode() {
        return Objects.hash( start(), end() );
    }

    /**
     * Helper equals. This implementation only tests for equality of the
     * boundary points. Use it to compare a Range instance and a subclass
     * instance.
     *
     * @param obj other to compare
     * @return the truth about
     */
    default boolean rangeEquals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( this == obj ) {
            return true;
        }
        // only if same class
        if ( ( this.getClass() != obj.getClass() ) ) {
            return false;
        }
        Range other = Range.class.cast( obj );
        return Objects.equals( this.start(), other.start() ) && Objects.
                equals( this.end(), other.end() );
    }

    /**
     * Helper for toString.
     *
     * @return the String like [a,b)
     */
    default String rangeToString() {
        return "[" + Objects.toString( start() ) + "," + Objects.toString( end() ) + ")";

    }

    /**
     * Helper to put endpoints or ranges in natural order. The minimum element
     * is put at position 0, the maximum at position 1.
     *
     * @param <Y> type of elements to sort
     * @param a arrays size 2 of input elements
     * @return sorted input array.
     */
    @SuppressWarnings( "unchecked" )
    @SafeVarargs
    static <Y extends Comparable<? super Y>> Y[] minmax( Y... a ) {
        if ( a[0].compareTo( a[1] ) > 0 ) {
            Y y = a[0];
            a[0] = a[1];
            a[1] = y;
        }
        return a;
    }

    /**
     * Create a new Range using part of this range as the input. It is a helper
     * to make some other operations easier to implement.
     *
     * @param startInclusive start of the new range
     * @param endExclusive end of the new range
     * @return new range
     */
    R between( P startInclusive, P endExclusive );

    /**
     * Compare this range with the other range. Only the start points of the
     * ranges are considered.
     *
     * @param other to compare with this
     * @return integer &lt; 0, == 0, or &gt; 0 for this start less, equal, or
     * greater than other start.
     */
    @Override
    default int compareTo( R other ) {
        return this.start().compareTo( other.start() );
    }

    /**
     * Return the intersection between this and other Range as Optional.
     * Optional is empty when there is no overlap.
     *
     * @param other that cuts this range
     * @return an Optional{@code Range<T,U>>} which is not empty when this and
     * cutter have an overlap.
     */
    default Optional<R> intersectWith( R other ) {
        if (this.overlaps(other)) {
            P intStart = max(this.start(), other.start());
            P intEnd = min(this.end(), other.end());
            return Optional.of(between(intStart, intEnd));
        }

        return Optional.empty();
    }

    /**
     * Test if other range is fully contained in this range, that is all points
     * of other are also part of this range.
     *
     * Note the handling of the end value. With same ends, contains is still
     * possible, in other words [ac) contains [bc) and [ac).
     *
     * @param other range
     * @return is the other range completely inside this range?
     */
    default boolean contains( R other ) {
        return this.start().compareTo(other.start()) <= 0 && this.end().compareTo(other.end()) >= 0;
    }

    /**
     * Try to replace this range with another Range, as if it would punch
     * through this range and replaces the knocked out part by it selves.
     *
     * The operation results in a stream that contains the remainders (if any)
     * of this range and the punch, if the punch would be exactly on target,
     * that is within the bounds of this range.For example when this is [a,c)
     * and the punch is [a,b), the result would be [[a-b),[b-c)]. The stream
     * will contain at minimum 1 and at maximum 3 Ranges.
     *
     * @param punch to knockout parts of this range
     * @return a stream, either containing this range on a miss or the parts of
     * this range if there is something left over, and the punch between the
     * parts, or before or after the left over part.
     *
     *
     */
    default Stream<R> punchThrough( R punch ) {
        //TODO A6B Study punchThrough and improve coverage.
        if ( !this.contains( punch ) ) {
            // missed: this
            return Stream.of( self() );
        }
        if ( this.rangeEquals( punch ) ) {
            // full punch: punch
            return Stream.of( punch );
        }
        if ( this.start().equals( punch.start() ) ) {
            // left punch: punch, remainder
            R remainder = between( punch.end(), this.end() );
            return Stream.of( punch, remainder );
        }
        if ( this.end().equals( punch.end() ) ) {
            // right punch: remainder punch
            R remainder = between( this.start(), punch.start() );
            return Stream.of( remainder, punch );
        }
        R leftRemainder = between( this.start(), punch.start() );
        R rightRemainder = between( punch.end(), this.end() );
        return Stream.of( leftRemainder, punch, rightRemainder );
    }

    /**
     * Make this class self-aware. See
     * <a href='http://web.archive.org/web/20130721224442/http:/passion.forco.de/content/emulating-self-types-using-java-generics-simplify-fluent-api-implementation'>emulated
     * self types</a>
     *
     * @return this, cast to the generic type of R.
     */
    @SuppressWarnings( "unchecked" )
    default R self() {
        return (R) this;
    }

    /**
     * For some Computations we need a zero distance. This provides it.
     *
     * @return the zero value.
     */
    D zero();
}
