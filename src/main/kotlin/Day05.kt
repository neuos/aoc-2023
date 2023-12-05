object Day05 : Day(5) {
    override val expected = DayResult(35L, 324724204L, 46L, 104070862)
    override fun solvePart1(input: Sequence<String>): Long {
        val lines = input.toList()
        val chunked = chunkAt(lines) { it.isBlank() }
        val seedLine = chunked[0].single()
        val seeds = seedLine.parseSeeds()
        println("seeds: $seeds")
        val entries = chunked.drop(1).parseAlmanac()

        val mapped = seeds.map { seed ->
            var x = seed
            entries.forEach {
                x = it.resolve(x)
            }
            println("Resolved $seed to $x")
            x
        }
        println("mapped: $mapped")
        return mapped.min()
    }

    private fun reduceEntry(from: AlmanacEntry, to: AlmanacEntry): AlmanacEntry {
        println("Reducing ${from.name} and ${to.name}")
        val combined = combine(from.maps, to.maps)
        val almanacEntry = AlmanacEntry("${from.from}-to-${to.to}", combined)
        println("Reduced to $almanacEntry")
        println("82 resolves to ${almanacEntry.resolve(82)}")
        return almanacEntry
    }

    private fun <T> chunkAt(lines: List<T>, condition: (T) -> Boolean): List<List<T>> {
        val result = lines.flatMapIndexed { index, x ->
            when {
                index == 0 || index == lines.lastIndex -> listOf(index)
                condition(x) -> listOf(index - 1, index + 1)
                else -> emptyList()
            }
        }.windowed(size = 2, step = 2) { (from, to) -> lines.slice(from..to) }
        return result
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val lines = input.toList()
        val chunked = chunkAt(lines) { it.isBlank() }
        val seedLine = chunked[0].single()
        val seedRanges = seedLine.parseSeeds().chunked(2).map { (start, length) ->
            (start..<start + length)
        }.sortedBy { it.first }

        val seedEntry = AlmanacEntry("seed", seedRanges.map { AlmanacMap(it.first, it.length, 0) })
        val entries = listOf(seedEntry) + chunked.drop(1).parseAlmanac()


        println("entries: ${entries.joinToString("\n") { it.toString() }}")
        val reduced = entries.reduce { a, b -> reduceEntry(a, b) }

//        println("seeds: $seedRanges")
        println("reduced: $reduced")
        val min = reduced.maps.map { it.sourceRange.first to it.destinationRange.first }
        println(min)
        println(min.minBy { it.second })
        return min.minOf { it.second }
    }

    private fun String.parseSeeds() = split(":")[1].split(' ').filter { it.isNotBlank() }.map { it.toLong() }

    private fun List<List<String>>.parseAlmanac(): List<AlmanacEntry> = map { entry ->
        val name = entry[0].split(":")[0]
        val maps = entry.drop(1).map { line ->
            val (destinationStart, sourceStart, length) = line.split(' ').map { it.toLong() }
            AlmanacMap(sourceStart = sourceStart, length = length, offset = destinationStart - sourceStart)
        }.sortedBy { it.sourceRange.first }
        AlmanacEntry(name, maps)
    }
}


private fun combine(source: List<AlmanacMap>, destination: List<AlmanacMap>): List<AlmanacMap> {
    return source.flatMap { a ->
        println("combining $a (${a.destinationRange}) with $destination")
        val relevant = destination.filter { a.destinationRange.overlaps(it.sourceRange) }
        println("relevant: $relevant")
        if (relevant.isEmpty()) return@flatMap listOf(a)
        val overlapping = relevant.map { b ->
            val overlap = a.destinationRange.overlap(b.sourceRange)
            AlmanacMap(
                sourceStart = overlap.first - a.offset, length = overlap.length, offset = a.offset + b.offset
            )
        }
        println("overlapping: $overlapping")

        val without = a.sourceRange.without(overlapping.map { it.sourceRange })
        println("without: $without")
        val diff = without.map {
            AlmanacMap(
                sourceStart = it.first, length = it.length, offset = a.offset
            )
        }
        println("diff: $diff")
        val almanacMaps = overlapping + diff
        println("Combined $a with $relevant to $almanacMaps")

        almanacMaps
    }.sortedBy { it.sourceRange.first }.also {
        println("Combined: $it")
    }.also { maps ->
        // none of the ranges should overlap
        maps.forEach { a ->
            maps.filter { it != a }.forEach { b ->
                if (a.sourceRange.overlaps(b.sourceRange)) {
                    error("Overlap: $a and $b")
                }
            }
        }
    }
}

private fun LongRange.without(
    others: List<LongRange>
): List<LongRange> {
    var difference = listOf(this)
    others.forEach { o ->
        difference = difference.flatMap { it.without(o) }
    }
    return difference
}


private fun LongRange.startsAfter(other: LongRange) = first > other.last
private fun LongRange.endsBefore(other: LongRange) = last < other.first

private fun LongRange.without(other: LongRange): List<LongRange> {
    val leading = start..<other.first
    val trailing = (other.last + 1)..last
    return listOf(leading, trailing).filter { !it.isEmpty() }
}

private fun LongRange.overlaps(sourceRange: LongRange) = !overlap(sourceRange).isEmpty()

private fun LongRange.overlap(other: LongRange): LongRange {
    val start = maxOf(first, other.first)
    val end = minOf(last, other.last)
    return start..end
}

private val LongRange.length: Long
    get() = last - first + 1


data class AlmanacEntry(val name: String, val maps: List<AlmanacMap>) {
    fun resolve(value: Long): Long {
        return maps.map { it.resolve(value) }.firstOrNull { it != null } ?: return value
    }

    val from = name.split('-').first()
    val to = name.split('-').last()
}

data class AlmanacMap(private val sourceStart: Long, val length: Long, val offset: Long) {
    val sourceRange = sourceStart..<(sourceStart + length)
    val destinationRange = sourceStart + offset..<(sourceStart + offset + length)
    override fun toString(): String {
        return "${sourceRange.first}..${sourceRange.last}:$offset"

    }

    fun resolve(value: Long) = if (value in sourceRange) {
        value + offset
    } else {
        null
    }
}

fun main() {
    println((1..9L).without(listOf(1..3L, 5..7L)))


    println(Day05.part2Example())
//    println((1..30L).overlap(10L..20))
//
//    val c = combine(listOf(AlmanacMap(1, 30, 1)), listOf(AlmanacMap(10, 10, 2)))
//    println(c)
//    println("12->" + c.map { it.resolve(12) })

//    Combining [55..58:-9, 59..67:-5, 79..92:-5] with [45..63:36, 64..76:4, 77..99:-32]
//    val source = listOf(
//        AlmanacMap(55, 4, -9),
//        AlmanacMap(59, 9, -5),
//        AlmanacMap(79, 14, -5)
//    )
//    val destination = listOf(
//        AlmanacMap(45, 19, 36),
//        AlmanacMap(64, 13, 4),
//        AlmanacMap(77, 23, -32)
//    )
//        val source = listOf (
//            AlmanacMap(79, 14, -5)
//            )
//    val destination = listOf(
//        AlmanacMap(64, 13, 4), AlmanacMap(77, 23, -32)
//    )
//    val combined = combine(
//        source, destination
//    )
//    println("source:" + source)
//    println("source destination " + source.map { it.destinationRange })
//
//    println(combined)
//    println("source  82->" + source.map { it.resolve(82) }.firstOrNull { it != null })
//    println("combined82->" + combined.map { it.resolve(82) }.firstOrNull { it != null })
//    Combined: [55..58:27, 59..67:31, 79..81:-1, 82..92:-5]

}