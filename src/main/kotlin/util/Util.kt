package util

/**
 * Combine two maps, if a key is in both maps, the values are merged with the merge function
 */
fun <K, V> Map<K, V>.combine(other: Map<K, V>, merge: (V, V) -> V): Map<K, V> {
    val allKeys = this.keys + other.keys
    return allKeys.associateWith { key ->
        val a = this[key]
        val b = other[key]
        when {
            a == null -> b!!
            b == null -> a
            else -> merge(a, b)
        }
    }
}

/**
 * Chunk a list at the elements where the condition is true
 * The elements where the condition is true are not included in the result
 */
fun <T> List<T>.chunkAt(condition: (T) -> Boolean): List<List<T>> = flatMapIndexed { index, x ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        condition(x) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.chunked(size = 2) { (from, to) -> slice(from..to) }


fun <T> Iterable<T>.allEquals() = firstOrNull()?.let { first -> all { it == first } } ?: true

fun <E> List<E>.pairs(): List<Pair<E, E>> = indices.flatMap { i ->
    (i + 1..<size).map { j ->
        Pair(this[i], this[j])
    }
}

fun <E> List<E>.repeat(count: Int) = (1..count).flatMap { this }
