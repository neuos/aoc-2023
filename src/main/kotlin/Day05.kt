object Day05 : Day(5) {
    override val expected = DayResult(35L, 324724204L, 46L, "TODO")
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
        val min = reduced.maps.map { it.destinationRange.first + it.offset }
        println(min)
        println(min.min())
        return min.min()
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
    println("Combining $source with $destination")

    val combined = mutableListOf<AlmanacMap>()

    // 1. add all overlaps
    val overlapping = source.flatMap { a ->
        destination.mapNotNull { b ->
            val overlap = a.destinationRange.overlap(b.sourceRange)
            if (overlap.isEmpty()) null else
                AlmanacMap(
                    sourceStart = overlap.first, length = overlap.length, offset = a.offset + b.offset
                )
        }
    }
    // 2. add all parts that are missing from source
    val missingFromSource = destination.flatMap { b ->
        source.filter { a ->
            b.sourceRange.endsBefore(a.destinationRange) || b.sourceRange.startsAfter(a.destinationRange)
        }.map { a ->
            val overlap = a.destinationRange.overlap(b.sourceRange)
            val before = a.sourceRange.first..<overlap.first
            val after = (overlap.last + 1)..a.sourceRange.last
            listOfNotNull(
                if (before.isEmpty()) null else AlmanacMap(
                    sourceStart = before.first, length = before.length, offset = a.offset
                ),
                if (after.isEmpty()) null else AlmanacMap(
                    sourceStart = after.first, length = after.length, offset = a.offset
                )
            )
        }.flatten()
    }
    println("overlapping: $overlapping")
    println("missingFromSource: $missingFromSource")
    val maps = (overlapping + missingFromSource).sortedBy { it.sourceRange.first }
    println("Combined: $maps")
    return maps


    val almanacMaps = splitAt(source, destination)
    println("Split at: $almanacMaps")
    return almanacMaps


    var i = 0
    var j = 0
    while (i < source.size && j < destination.size) {
        val a = source[i]
        val b = destination[j]
        val dstA = a.destinationRange
        val srcB = b.sourceRange
        println("comparing $a = $dstA and $b = $srcB")

        val overlap = dstA.overlap(srcB)
        // cases:
        when {
            overlap.isEmpty() -> {
                when {
                    // a is completely before b -> use a, check next a
                    dstA.endsBefore(srcB) -> {
                        combined += a
                        i++
                    }
                    // a is completely after b -> check next b
                    dstA.startsAfter(srcB) -> {
                        j++
                    }
                }
            }

            else -> {
                // potential part before

                val overlapSrc = overlap.first - a.offset..overlap.last - a.offset
                println("Overlap between $dstA and $srcB: $overlap")
                println("Overlap in source: $overlapSrc")
                val before = a.sourceRange.first..<overlapSrc.first
                if (!before.isEmpty()) {
                    println("before: $before")
                    val beforeMap = AlmanacMap(
                        sourceStart = before.first, length = before.length, offset = a.offset
                    )
                    println("beforeMap: $beforeMap")
                    combined += beforeMap
                }
                // and after
                val after = (overlapSrc.last + 1)..a.sourceRange.last
                if (!after.isEmpty()) {
                    println("after: $after")
                    val afterMap = AlmanacMap(
                        sourceStart = after.first, length = after.length, offset = a.offset
                    )
                    println("afterMap: $afterMap")
                    combined += afterMap
                }

                // part in between
                val between = AlmanacMap(
                    sourceStart = overlapSrc.first, length = overlap.length, offset = a.offset + b.offset
                )
                println("between: $between")
                println("merged offset: ${a.offset} with ${b.offset} to ${between.offset}")
                combined += between
                i++
            }
        }
    }
    if (i < source.size) {
        combined += source.drop(i)
    }

    combined.sortBy { it.sourceRange.start }
    println("Combined: $combined")
    return combined
}

private fun splitAt(source: List<AlmanacMap>, destination: List<AlmanacMap>): List<AlmanacMap> {
    val edges =
        (source.map { it.destinationRange }.flatMap { listOf(it.first, it.last) } + destination.map { it.sourceRange }
            .flatMap { listOf(it.first, it.last) }).sorted().distinct()

    var i = 0
    var j = 0
    var result = mutableListOf<AlmanacMap>()
    do {
        var a = source[i]
        var b = destination[j]


        if (b.sourceRange.last < a.destinationRange.first) {
            // b ends before a starts. continue with next b
            j++
            continue
        }
        if (b.sourceRange.first > a.destinationRange.last) {
            // b starts after a ends
            // a has no overlap with b
            // taking a fully without offset
            val element = AlmanacMap(
                sourceStart = a.destinationRange.first,
                length = (a.destinationRange.first..a.destinationRange.last).length,
                offset = a.offset
            )
            println("no overlap with b: Adding $element")
            result += element
            i++
            continue
        }

        val start = maxOf(a.destinationRange.first, b.sourceRange.first)
        val end = minOf(a.destinationRange.last, b.sourceRange.last)
        if (start > a.destinationRange.first) {

        }


    } while (i <= source.size)
























    println("edges: $edges")
    val maps = buildList {
        for ((left, right) in edges.windowed(2)) {
//            println("Checking $left..$right")
            if (i >= source.size) {
//                println("Reached end of a")
                break
            }
            val a = source[i]
            val b = destination.getOrNull(j)
//            println("i=$i, j=$j")
//            println("a=$a, b=$b")

            val startsInA = left in a.destinationRange.first..<a.destinationRange.last
            val startsInB = b?.let { left in b.sourceRange.first..<b.sourceRange.last } ?: false
            if (startsInA && startsInB) {
                val element = AlmanacMap(
                    sourceStart = left,
                    length = (left..right).length,
                    offset = a.offset + b!!.offset
                )
                println("Starts in a and in b: adding $element")
                add(element)
            }

            val overlapA = (left..right).overlap(a.destinationRange)
            val overlapB = b?.let { (left..right).overlap(b.sourceRange) }

            if (left in a.destinationRange) {
//                println("Overlap with a: $overlapA")
                if (overlapB == null || overlapB.isEmpty()) {
//                    println("No overlap with b -> keep old offset")
                    // block withouth b
                    // keep old offset
                    val element = AlmanacMap(sourceStart = left, length = (left..right).length, offset = a.offset)
//                    println("Adding $element")
                    add(element)
                } else {
//                    println("Overlap with b: $overlapB")
                    // block with b
                    // new offset = old offset + b.offset
                    val element =
                        AlmanacMap(sourceStart = left, length = (left..right).length, offset = a.offset + b.offset)
//                    println("Adding $element")
                    add(element)
                }
            } else {
//                println("No overlap with a")
            }

            if (right == a.destinationRange.last) {
                // end of a block
//                println("End of a block")
                i++
            }
            if (right == b?.sourceRange?.last) {
                // end of b block
//                println("End of b block")
                j++
            }
        }
    }
    return maps




    source.map { a ->
        destination.dropWhile {
            it.sourceRange.endsBefore(a.destinationRange)
        }.takeWhile {
            it.sourceRange.startsAfter(a.destinationRange)
        }.map { b ->
            val overlap = a.destinationRange.overlap(b.sourceRange)
            AlmanacMap(
                sourceStart = overlap.first, length = overlap.length, offset = a.offset + b.offset
            )
        }
    }

}

private fun LongRange.startsAfter(other: LongRange) = first > other.last
private fun LongRange.endsBefore(other: LongRange) = last < other.first

private fun LongRange.without(other: LongRange): List<LongRange> {
    val leading = start..<other.first
    val trailing = (other.last + 1)..last
    return listOf(leading, trailing).filter { !it.isEmpty() }
}

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
    val source = listOf(
        AlmanacMap(79, 14, -5)
    )
    val destination = listOf(
        AlmanacMap(64, 13, 4), AlmanacMap(77, 23, -32)
    )
    val combined = combine(
        source, destination
    )
    println("source:" + source)
    println("source destination " + source.map { it.destinationRange })

    println(combined)
    println("source  82->" + source.map { it.resolve(82) }.firstOrNull { it != null })
    println("combined82->" + combined.map { it.resolve(82) }.firstOrNull { it != null })
//    Combined: [55..58:27, 59..67:31, 79..81:-1, 82..92:-5]

}