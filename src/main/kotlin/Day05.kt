import Day05.RangeExtensions.length
import Day05.RangeExtensions.overlap
import Day05.RangeExtensions.overlaps
import Day05.RangeExtensions.without

object Day05 : Day(5) {
    override val expected = DayResult(35L, 324724204L, 46L, 104070862L)
    override fun solvePart1(input: Sequence<String>): Long {
        val (seeds, entries) = input.parseInput()

        return seeds.minOf { seed ->
            entries.fold(seed) { x, entry ->
                entry.resolve(x)
            }
        }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val (seeds, entries) = input.parseInput()

        val seedRanges = seeds.chunked(2).map { (start, length) ->
            (start..<start + length)
        }.sortedBy { it.first }

        val seedEntry = AlmanacEntry("seed", seedRanges.map { AlmanacMap(it.first, it.length, 0) })
        val allEntries = listOf(seedEntry) + entries
        val combined = allEntries.reduce { a, b -> a.combine(b) }
        return combined.maps.minOf { it.destinationRange.first }
    }

    private fun Sequence<String>.parseInput(): Pair<List<Long>, List<AlmanacEntry>> {
        val chunked = toList().chunkAt { it.isBlank() }
        val seeds = chunked[0].single().split(":")[1].split(' ').filter { it.isNotBlank() }.map { it.toLong() }
        val entries = chunked.drop(1).map { entry ->
            AlmanacEntry.fromLines(entry)
        }
        return seeds to entries
    }

    private fun <T> List<T>.chunkAt(condition: (T) -> Boolean): List<List<T>> = flatMapIndexed { index, x ->
        when {
            index == 0 || index == lastIndex -> listOf(index)
            condition(x) -> listOf(index - 1, index + 1)
            else -> emptyList()
        }
    }.chunked(size = 2) { (from, to) -> slice(from..to) }

    private object RangeExtensions {
        fun LongRange.without(others: List<LongRange>): List<LongRange> =
            others.fold(listOf(this)) { difference, range ->
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


    data class AlmanacEntry(val name: String, val maps: List<AlmanacMap>) {
        fun resolve(value: Long): Long {
            return maps.map { it.resolve(value) }.firstOrNull { it != null } ?: return value
        }

        fun combine(with: AlmanacEntry) = AlmanacEntry("$from-to-${with.to}", maps.combine(with.maps))

        private val from = name.split('-').first()
        private val to = name.split('-').last()

        companion object {

            fun fromLines(entry: List<String>): AlmanacEntry = AlmanacEntry(name = entry[0].split(":")[0],
                maps = entry.drop(1).map { AlmanacMap.fromLine(it) }.sortedBy { it.sourceRange.first })

            private fun List<AlmanacMap>.combine(with: List<AlmanacMap>): List<AlmanacMap> {
                fun AlmanacMap.getOverlapping(others: List<AlmanacMap>) = others.map { other ->
                    val overlap = this.destinationRange.overlap(other.sourceRange)
                    AlmanacMap(
                        sourceStart = overlap.first - this.offset,
                        length = overlap.length,
                        offset = this.offset + other.offset
                    )
                }

                fun AlmanacMap.getDifference(others: List<AlmanacMap>) =
                    sourceRange.without(others.map { it.sourceRange }).map {
                        AlmanacMap(
                            sourceStart = it.first, length = it.length, offset = offset
                        )
                    }

                return flatMap { map ->
                    val relevant = with.filter { map.destinationRange.overlaps(it.sourceRange) }
                    if (relevant.isEmpty()) return@flatMap listOf(map)
                    val overlapping = map.getOverlapping(relevant)
                    val diff = map.getDifference(overlapping)
                    overlapping + diff
                }.sortedBy { it.sourceRange.first }
            }
        }
    }

    data class AlmanacMap(private val sourceStart: Long, val length: Long, val offset: Long) {
        val sourceRange = sourceStart..<(sourceStart + length)
        val destinationRange = sourceStart + offset..<(sourceStart + offset + length)
        override fun toString() = "${sourceRange.first}..${sourceRange.last}:$offset"

        fun resolve(value: Long) = if (value in sourceRange) value + offset else null

        companion object {
            fun fromLine(line: String): AlmanacMap {
                val (destinationStart, sourceStart, length) = line.split(' ').map { it.toLong() }
                return AlmanacMap(sourceStart = sourceStart, length = length, offset = destinationStart - sourceStart)
            }
        }
    }
}


