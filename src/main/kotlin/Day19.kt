import util.chunkAt

object Day19 : Day(19) {
    override val expected = DayResult(19114, 432788, "TODO", "TODO")

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
        private val variable: (Part) -> Int,
        private val value: Int,
        private val compare: (Int, Int) -> Boolean,
        private val trueResult: String,
        private val falseResult: String
    ) {
        companion object {
            fun parse(input: String): Rule {

                val variable = when (input[0]) {
                    'x' -> Part::x
                    'm' -> Part::m
                    'a' -> Part::a
                    's' -> Part::s
                    else -> throw IllegalArgumentException("Unknown variable ${input[0]}")
                }

                val comp = when (input[1]) {
                    '<' -> { a: Int, b: Int -> a < b }
                    '>' -> { a: Int, b: Int -> a > b }
                    else -> throw IllegalArgumentException("Unknown comparator ${input[1]}")
                }

                val value = input.substring(2).substringBefore(':').toInt()

                val trueResult = input.substringAfter(':').substringBefore(',')
                val falseResult = input.substringAfter(',')

                val rule = Rule(variable, value, comp, trueResult, falseResult)
//                println("Parsed $input to $rule")
                return rule
            }
        }

        fun evaluate(part: Part): Outcome {
            val result = if (compare(variable(part), value)) trueResult else falseResult
            return when {
                result == "A" -> Outcome.Accept
                result == "R" -> Outcome.Reject
                result.contains(':') -> parse(result).evaluate(part)
                else -> Outcome.Continue(result)
            }
        }

        override fun toString(): String {
            return "$variable ${if(compare(1,2))"<" else ">"} $value ? $trueResult : '$falseResult'"
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
            when(res){
                Outcome.Accept -> it.sum()
                else -> 0
            }
        }


    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }
}