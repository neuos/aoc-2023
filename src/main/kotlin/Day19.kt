import util.chunkAt
import util.pairs
import java.math.BigInteger

object Day19 : Day(19) {
    override val expected = DayResult(19114, 432788, 167409079868000, "TODO")

    data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun sum() = x + m + a + s

        companion object {
            fun parse(input: String): Part {
                // {x=787,m=2655,a=1222,s=2876}
                val (x, m, a, s) = input.substringAfter("{").substringBefore("}").split(",")
                val xVal = x.substringAfter("=").toInt()
                val mVal = m.substringAfter("=").toInt()
                val aVal = a.substringAfter("=").toInt()
                val sVal = s.substringAfter("=").toInt()
                return Part(xVal, mVal, aVal, sVal)
            }
        }
    }

    sealed class Outcome {
        data object Accept : Outcome()
        data object Reject : Outcome()
        data class Continue(val name: String) : Outcome()
    }

    data class Rule(
        private val variable: Char, private val value: Int, private val comp: Char,
//        private val compare: (Int, Int) -> Boolean,
        private val trueResult: String, private val falseResult: String
    ) {
        companion object {
            fun parse(input: String): Rule {

                val variable = input[0]

                val comp = input[1]

                val value = input.substring(2).substringBefore(':').toInt()

                val trueResult = input.substringAfter(':').substringBefore(',')
                val falseResult = input.substringAfter(',')

                val rule = Rule(variable, value, comp, trueResult, falseResult)
//                println("Parsed $input to $rule")
                return rule
            }
        }

        private fun getVariable(part: Part): Int {
            return when (variable) {
                'x' -> part.x
                'm' -> part.m
                'a' -> part.a
                's' -> part.s
                else -> throw IllegalArgumentException("Unknown variable $variable")
            }
        }

        private fun compare(a: Int, b: Int): Boolean {
            return when (comp) {
                '<' -> a < b
                '>' -> a > b
                else -> throw IllegalArgumentException("Unknown comparison $comp")
            }
        }

        fun evaluate(part: Part): Outcome {
            val result = if (compare(getVariable(part), value)) trueResult else falseResult
            return when {
                result == "A" -> Outcome.Accept
                result == "R" -> Outcome.Reject
                result.contains(':') -> parse(result).evaluate(part)
                else -> Outcome.Continue(result)
            }
        }

        override fun toString(): String {
            return "$variable ${if (compare(1, 2)) "<" else ">"} $value ? $trueResult : '$falseResult'"
        }

        private fun RangedPart.replaceVariable(range: IntRange) = when (variable) {
            'x' -> copy(x = range)
            'm' -> copy(m = range)
            'a' -> copy(a = range)
            's' -> copy(s = range)
            else -> error("Unknown variable $variable")
        }

        private fun RangedPart.getVariable() = when (variable) {
            'x' -> x
            'm' -> m
            'a' -> a
            's' -> s
            else -> error("Unknown variable $variable")
        }

        fun evaluate(part: RangedPart): Map<RangedPart, Outcome> {
            val range = part.getVariable()

            val (trueRange, falseRange) = when (comp) {
                '<' -> range.first..<value to (value)..range.last
                '>' -> value + 1..range.last to range.first..value
                else -> error("Unknown comparison $comp")
            }

            val truePart = part.replaceVariable(trueRange)
            val falsePart = part.replaceVariable(falseRange)
            return evaluateResult(trueRange, truePart, trueResult) + evaluateResult(falseRange, falsePart, falseResult)
        }

        private fun evaluateResult(trueRange: IntRange, truePart: RangedPart, result: String) = when {
            trueRange.isEmpty() -> emptyMap()
            result == "A" -> mapOf(truePart to Outcome.Accept)
            result == "R" -> mapOf(truePart to Outcome.Reject)
            result.contains(':') -> parse(result).evaluate(truePart)
            else -> mapOf(truePart to Outcome.Continue(result))
        }
    }


    override fun solvePart1(input: Sequence<String>): Any {

        val (ruleBlock, partBlock) = input.toList().chunkAt { it.isBlank() }
        val rules = ruleBlock.associate { line ->
            val name = line.substringBefore("{")
            val ruleString = line.substringAfter("{").substringBefore("}")
            val rule = Rule.parse(ruleString)
            name to rule
        }
        return partBlock.map {
            Part.parse(it)
        }.sumOf {
            println(it)
            var res: Outcome = Outcome.Continue("in")
            while (res is Outcome.Continue) {
                val rule = rules[res.name] ?: error("No rule ${res.name}")
                res = rule.evaluate(it)
                println("$it on $rule is $res")
            }
            println("$it is $res")
            when (res) {
                Outcome.Accept -> it.sum()
                else -> 0
            }
        }
    }

    data class RangedPart(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) : Comparable<RangedPart> {
        fun combinations(): Long = x.count().toLong() * m.count() * a.count() * s.count()
        fun overlaps(part: RangedPart): Boolean {
            return x.overlaps(part.x) && m.overlaps(part.m) && a.overlaps(part.a) && s.overlaps(part.s)
        }

        override fun compareTo(other: RangedPart) =
            compareValuesBy(this, other, { it.x.first }, { it.x.last }, { it.m.first }, { it.m.last }, { it.a.first }, { it.a.last }, { it.s.first }, { it.s.last })
    }

    private fun IntRange.intersect(other: IntRange): IntRange {
        val start = maxOf(first, other.first)
        val end = minOf(last, other.last)
        return if (start > end) IntRange.EMPTY
        else start..end
    }

    private fun combine(a: RangedPart, b: RangedPart): List<RangedPart> {
        fun combineS(a: RangedPart, b: RangedPart): List<RangedPart> {
            val intersectionS = a.s.intersect(b.s)
            if (intersectionS.isEmpty()) return listOf(a, b)

            val aWithout = a.s.intersect(intersectionS)
            val bWithout = b.s.intersect(intersectionS)

            return listOf(
                a.copy(s = intersectionS), b.copy(s = intersectionS)
            ) + a.copy(s = aWithout) + b.copy(s = bWithout)
        }

        fun combineA(a: RangedPart, b: RangedPart): List<RangedPart> {
            val intersectionA = a.a.intersect(b.a)
            if (intersectionA.isEmpty()) return listOf(a, b)

            val aWithout = a.a.intersect(intersectionA)
            val bWithout = b.a.intersect(intersectionA)

            return combineS(
                a.copy(a = intersectionA), b.copy(a = intersectionA)
            ) + a.copy(a = aWithout) + b.copy(a = bWithout)
        }

        fun combineM(a: RangedPart, b: RangedPart): List<RangedPart> {
            val intersectionM = a.m.intersect(b.m)
            if (intersectionM.isEmpty()) return listOf(a, b)

            val aWithout = a.m.intersect(intersectionM)
            val bWithout = b.m.intersect(intersectionM)

            return combineA(
                a.copy(m = intersectionM), b.copy(m = intersectionM)
            ) + a.copy(m = aWithout) + b.copy(m = bWithout)
        }

        fun combineX(a: RangedPart, b: RangedPart): List<RangedPart> {
            val intersectionX = a.x.intersect(b.x)
            if (intersectionX.isEmpty()) return listOf(a, b)

            val aWithout = a.x.intersect(intersectionX)
            val bWithout = b.x.intersect(intersectionX)

            return combineM(
                a.copy(x = intersectionX), b.copy(x = intersectionX)
            ) + a.copy(x = aWithout) + b.copy(x = bWithout)
        }

        return combineX(a, b)
    }


    private fun List<RangedPart>.combineOverlaps() {
//        fold() { acc, part ->
//            val overlaps = acc.filter { it.overlaps(part) }
//            val nonOverlaps = acc.filter { !it.overlaps(part) }
//            val combined = overlaps.fold(listOf(part)) { combined, overlap ->
//                combined.flatMap { combine(it, overlap) }
//            }
//            nonOverlaps + combined
//        }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val (ruleBlock, _) = input.toList().chunkAt { it.isBlank() }
        val rules = ruleBlock.associate { line ->
            val name = line.substringBefore("{")
            val ruleString = line.substringAfter("{").substringBefore("}")
            val rule = Rule.parse(ruleString)
            name to rule
        }

        val startRule = rules["in"] ?: error("No start rule")
        val max = 4000
        val initialRange = RangedPart(0..max, 0..max, 0..max, 0..max)
        val outcomeMap = startRule.evaluate(initialRange).toMutableMap()
        println(outcomeMap.size)

        while (outcomeMap.any { it.value is Outcome.Continue }) {
            val continues = outcomeMap.entries.filter { it.value is Outcome.Continue }
                .map { it.key to it.value as Outcome.Continue }
            outcomeMap -= continues.map { it.first }.toSet()
            continues.forEach { (part, outcome) ->
                val rule = rules[outcome.name] ?: error("No rule ${outcome.name}")
                outcomeMap += rule.evaluate(part)
            }
            println("Outcome map size ${outcomeMap.size}")
        }

        println(outcomeMap.size)

        val accepted = outcomeMap.filter { it.value is Outcome.Accept }.map { it.key }.sorted()

        var total = accepted.sumOf {
            it.combinations()
        }.toBigInteger()
        println("Total with duplicates $total")
        val overlap = accepted.pairs().sumOf { (a, b) ->
            overlap(a, b)
        }
        println("Overlap $overlap")
        total -= overlap
        println("Total without duplicates $total")
        return total


        accepted.forEach {
            println(it)
        }

        return accepted.sumOf {
            it.combinations()
        }

    }

    private fun overlap(a: RangedPart, b: RangedPart): BigInteger {
        val x = a.x.intersect(b.x).size().toBigInteger()
        val m = a.m.intersect(b.m).size().toBigInteger()
        val s = a.s.intersect(b.s).size().toBigInteger()
        val a = a.a.intersect(b.a).size().toBigInteger()
        return x * m * s * a
    }
}

private fun IntRange.size(): Int {
    if (isEmpty()) return 0
    val size = last - first + 1
    assert(size >= 0) { "Negative size $size of $this" }
    return size
}

private fun IntRange.overlaps(other: IntRange) = first <= other.last && other.first <= last


/*
20596819717950 -> 146981810669484
 6143262631800 -> 194472605600784
15353877894096 -> 179118727706688
 8288751454830 -> 224332817058968
35354502624552 -> 224801198486126
21867569686040 -> 239812400964780
14508113433828 -> 225304287530952
26612597310456 -> 213940780580256
16811954826222 -> 197128825754034
 8184443439460 -> 188944382314574
*/
//167578630387434
//167409079868000
//188944382314574