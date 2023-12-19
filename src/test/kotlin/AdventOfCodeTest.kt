import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals

class AdventOfCodeTest {
    companion object {
        private val days = Day::class.sealedSubclasses.map { it.objectInstance!! }.sorted()
        private fun testcase(name: String, expected: Any?, function: () -> Any) = DynamicTest.dynamicTest(name) {
            Assumptions.assumeFalse { expected == null }
            assertEquals(expected.toString(), function().toString())
        }
    }

    @TestFactory
    fun `All Days`() = days.map { it.testContainer() }

    @TestFactory
    fun Today() = days.last().testContainer()

    private fun Day.testContainer() = DynamicContainer.dynamicContainer(
        toString(), listOf(testcase("Part 1 example", expected.part1Example) { part1Example() },
            testcase("Part 1", expected.part1) { part1() },
            testcase("Part 2 example", expected.part2Example) { part2Example() },
            testcase("Part 2", expected.part2) { part2() })
    )
}