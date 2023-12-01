import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

class AdventOfCodeTest {
    companion object {
        private val days = Day::class.sealedSubclasses.map { it.objectInstance!! }.sorted()
        private fun testcase(name: String, expected: Any?, actual: Any) = DynamicTest.dynamicTest(name) {
            Assumptions.assumeFalse { expected == null }
            assertEquals(expected, actual)
        }
    }

    @TestFactory
    fun `All Days`() = days.map {
        DynamicContainer.dynamicContainer(
            it.toString(), listOf(
                testcase("Part 1 example", it.expected.part1Example, it.part1Example()),
                testcase("Part 1", it.expected.part1, it.part1()),
                testcase("Part 2 example", it.expected.part2Example, it.part2Example()),
                testcase("Part 2", it.expected.part2, it.part2())
            )
        )
    }
}