package util

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun List<Char>.parseBinary(one: Char = '1'): Long = mapIndexed { index, c ->
    if (c == one) 1L shl index else 0L
}.reduce { acc, l ->
    acc or l
}

fun isPowerOfTwo(n: Long) = n != 0L && (n and n - 1) == 0L
fun oneBitDiff(a: Long, b: Long): Boolean {
    val diff = a xor b
    return isPowerOfTwo(diff)
}

fun leastCommonMultiple(a: Long, b: Long) = a * b / greatestCommonDivisor(a, b)

tailrec fun greatestCommonDivisor(a: Long, b: Long): Long = if (b == 0L) a
else greatestCommonDivisor(b, a % b)


/**
 * Returns the Int value nearest to this value in direction of positive infinity.
 */
fun Double.nextUpInt() = ceil(this).let { if (it == this) it + 1 else it }.toInt()

/**
 * Returns the Int value nearest to this value in direction of negative infinity.
 */
fun Double.nextDownInt() = floor(this).let { if (it == this) it - 1 else it }.toInt()

/**
 * Returns the next representable floating-point value after this value in direction of positive infinity.
 */
fun solveQuadratic(p: Double, q: Double): Pair<Double, Double> {
    val root = sqrt((p * p) / 4 - q)
    val lower = -p / 2 - root
    val higher = -p / 2 + root
    return lower to higher
}
