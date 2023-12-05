object Day05 : Day(5) {
    override val expected = DayResult(35, "TODO", "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Int {
        val lines = input.toList()
        val chunked = chunkAt(lines) { it.isBlank() }
        val seeds = chunked[0].single().split(":")[1].split(' ').filter { it.isNotBlank() }.map { it.toInt() }
        println("seeds: $seeds")
        val entries = chunked.drop(1).map {
            val name = it[0].split(":")[0]
            val maps = it.drop(1).map {
                val (destinationStart, sourceStart, length) = it.split(' ').map { it.toInt() }
                AlmanacMap(offset = destinationStart - sourceStart, sourceStart = sourceStart, length = length)
            }
            AlmanacEntry(name, maps)
        }

        val reduced = entries.asReversed().reduce { a, b -> resolveEntry(b, a) }

        println("seeds: $seeds")
        println("reduced: $reduced")

        val mapped =  seeds.map {seed->
            var x: Int = seed
            entries.forEach {
             x = it.resolve(x)
            }
            println("Resolved $seed to $x")
            x
        }
        println("mapped: $mapped")
        return mapped.min()
    }

    private fun resolveEntry(from: AlmanacEntry, to: AlmanacEntry): AlmanacEntry {
        println("Resolving ${from.name} and ${to.name}")
        val combined = from.maps.combine(to.maps)
        return AlmanacEntry("${from.from}-to-${to.to}", combined)
    }

    private fun <T> chunkAt(lines: List<T>, condition: (T) -> Boolean): List<List<T>> {
        val result = lines
            .flatMapIndexed { index, x ->
                when {
                    index == 0 || index == lines.lastIndex -> listOf(index)
                    condition(x) -> listOf(index - 1, index + 1)
                    else -> emptyList()
                }
            }
            .windowed(size = 2, step = 2) { (from, to) -> lines.slice(from..to) }
        return result
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }
}

private fun List<AlmanacMap>.combine(other: List<AlmanacMap>): List<AlmanacMap> {
    println("Combining $this and $other")
    val combined = this.flatMap { a ->
        other.flatMap { b ->
            val overlap = a.sourceRange.overlap(b.sourceRange)
            println("a: $a, b: $b, overlap: $overlap")
            if (overlap != null) {
                val rest = a.sourceRange.without(overlap)
                listOf(
                    AlmanacMap(
                        offset = a.offset + b.offset,
                        sourceStart = overlap.first,
                        length = overlap.length
                    )
                ) + rest.map { AlmanacMap(offset = a.offset, sourceStart = it.first, length = it.length) }
            } else emptyList()
        }
    }
    println("Combined: $combined")
    return combined
}

private fun IntRange.without(other: IntRange): List<IntRange> {
    val leading = start..<other.first
    val trailing = (other.last + 1)..last
    return listOf(leading, trailing).filter { !it.isEmpty() }
}

private fun IntRange.overlap(other: IntRange): IntRange? {
    val start = maxOf(start, other.first)
    val end = minOf(last, other.last)
    if (start > end) return null
    return start..end
}

private val IntRange.length: Int
    get() = last - first + 1


data class AlmanacEntry(val name: String, val maps: List<AlmanacMap>) {
    fun resolve(value: Int): Int {
        val map = maps.firstOrNull { it.sourceRange.contains(value) } ?: return value
        return value + map.offset
    }

    val from = name.split('-').first()
    val to = name.split('-').last()
}

data class AlmanacMap(val offset: Int, val sourceStart: Int, val length: Int) {
    val sourceRange = sourceStart..<(sourceStart + length)
    fun toFullString(): String {
        return sourceRange.map() { source ->
            val destination = source + offset
            "$source -> $destination"
        }.toString()
    }
}