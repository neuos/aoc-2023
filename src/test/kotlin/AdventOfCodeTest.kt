import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream
import kotlin.test.assertEquals

class AdventOfCodeTest {
    private val testCases = listOf(
        TestCase(Day0, 3, 6, 6, 21),
        TestCase(Day01, 142, 54990, 281, 54473),
    )

    @TestFactory
    fun `All Days`() = testCases.map { testCase ->
        DynamicContainer.dynamicContainer(
            testCase.name,
            Stream.of(
                DynamicTest.dynamicTest("Part 1 example") {
                    assertEquals(testCase.part1Example, testCase.day.part1Example())
                },
                DynamicTest.dynamicTest("Part 1") {
                    assertEquals(testCase.part1, testCase.day.part1())
                },
                DynamicTest.dynamicTest("Part 2 example") {
                    assertEquals(testCase.part2Example, testCase.day.part2Example())
                },
                DynamicTest.dynamicTest("Part 2") {
                    assertEquals(testCase.part2, testCase.day.part2())
                })
        )
    }.toTypedArray()
}

data class TestCase(val day: Day, val part1Example: Any, val part1: Any, val part2Example: Any, val part2: Any) {
    val name: String = day.toString()
}