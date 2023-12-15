object Day15 : Day(15) {
    override val expected = DayResult(1320, 504036, 145, 295719)
    override fun solvePart1(input: Sequence<String>): Any {
        return input.first().split(",").sumOf { it.hashAlgorithm() }
    }

    data class Lens(val label: String, val focalLength: Int = 0) {
        override fun equals(other: Any?) = other is Lens && other.label == label
        override fun toString() = "$label $focalLength"
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val boxes = List(256) { mutableListOf<Lens>() }
        input.first().split(",").forEach {
            if (it.contains("-")) {
                val label = it.substringBefore("-")
                val hash = label.hashAlgorithm()
                val removed = boxes[hash].removeIf { it.label == label }
//                assert(removed)
            } else if (it.contains("=")) {
                val label = it.substringBefore("=")
                val focalLength = it.substringAfter("=").toInt()
                val hash = label.hashAlgorithm()
                boxes[hash].addOrReplace(Lens(label, focalLength))
            }

            println("After $it:")
            boxes.forEachIndexed { index, lenses -> if (lenses.isNotEmpty()) println("Box $index: $lenses") }
        }
        return boxes.mapIndexed { boxIndex, lenses ->
            lenses.mapIndexed { lensIndex, lens ->
                (boxIndex+1) * (lensIndex+1) * lens.focalLength
            }.sum()
        }.sum()
    }
}

private fun MutableList<Day15.Lens>.addOrReplace(lens: Day15.Lens) {
    val index = indexOf(lens)
    if (index == -1) {
        add(lens)
    } else {
        set(index, lens)
    }
}

private fun String.hashAlgorithm(): Int {
    var hash = 0
    for (i in indices) {
        hash = ((hash + get(i).code) * 17).rem(256)
    }
    return hash
}
