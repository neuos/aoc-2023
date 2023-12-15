object Day15 : Day(15) {
    override val expected = DayResult(1320, 504036, 145, 295719)
    override fun solvePart1(input: Sequence<String>): Any {
        return input.first().split(",").sumOf { it.hashAlgorithm() }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val boxes = Boxes()
        input.first().split(",").forEach {
            if (it.contains("-")) {
                val label = it.substringBefore("-")
                boxes -= Lens(label)
            } else if (it.contains("=")) {
                val label = it.substringBefore("=")
                val focalLength = it.substringAfter("=").toInt()
                boxes += Lens(label, focalLength)
            }
        }
        return boxes.focusingPower()
    }


    data class Lens(val label: String, val focalLength: Int = 0) {
        override fun equals(other: Any?) = other is Lens && other.label == label
        override fun toString() = "$label $focalLength"
        override fun hashCode() = label.hashAlgorithm()
    }

    class Boxes {
        private val boxes = List(256) { mutableListOf<Lens>() }
        private fun get(lens: Lens) = boxes[lens.label.hashAlgorithm()]
        operator fun minusAssign(lens: Lens) {
            get(lens).removeIf { it.label == lens.label }
        }

        operator fun plusAssign(lens: Lens) = get(lens).addOrReplace(lens)
        fun focusingPower() = boxes.mapIndexed { boxIndex, lenses ->
            (boxIndex + 1) * lenses.mapIndexed { lensIndex, lens ->
                (lensIndex + 1) * lens.focalLength
            }.sum()
        }.sum()

        private fun <T> MutableList<T>.addOrReplace(element: T) {
            when (val index = indexOf(element)) {
                -1 -> add(element)
                else -> set(index, element)
            }
        }

    }


    private fun String.hashAlgorithm(): Int {
        var hash = 0
        for (i in indices) {
            hash = ((hash + get(i).code) * 17).rem(256)
        }
        return hash
    }
}

