package util

object RangeExtensions {
    fun LongRange.without(others: List<LongRange>): List<LongRange> = others.fold(listOf(this)) { difference, range ->
        difference.flatMap { it.without(range) }
    }

    fun LongRange.without(other: LongRange): List<LongRange> {
        val leading = start..<other.first
        val trailing = (other.last + 1)..last
        return listOf(leading, trailing).filter { !it.isEmpty() }
    }

    fun LongRange.overlaps(sourceRange: LongRange) = !overlap(sourceRange).isEmpty()
    fun LongRange.overlap(other: LongRange): LongRange {
        val start = maxOf(first, other.first)
        val end = minOf(last, other.last)
        return start..end
    }

    val LongRange.length: Long get() = last - first + 1
}
