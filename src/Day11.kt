import kotlin.math.max
import kotlin.math.min

fun main() {

    fun calcStars(input: List<String>, farSize: Int): Long {
        val doubledRows = input.mapIndexed { idx, it -> if (it.contains('#')) -1 else idx }
            .filter { it >= 0 }
            .toSet()
        val doubledCols = mutableSetOf<Int>()
        for (i in input.first().indices) {
            var isFound = false
            for (j in input.indices) {
                if (input[j][i] == '#') {
                    isFound = true
                    break
                }
            }
            if (!isFound) doubledCols.add(i)
        }

        val stars = input.flatMapIndexed { i, row ->
            row.mapIndexed { j, char -> if (char == '#') j to i else -1 to -1 }
                .filter { it.first >= 0 }
        }
        //"$doubledCols $doubledRows $stars".println()

        var sum = 0L
        for (i in stars.indices) {
            for (j in i + 1..<stars.size) {
                val minX = min(stars[i].first, stars[j].first)
                val maxX = max(stars[i].first, stars[j].first)
                val minY = min(stars[i].second, stars[j].second)
                val maxY = max(stars[i].second, stars[j].second)
                val closest = (maxX - minX) + (maxY - minY) +
                        (farSize-1) * doubledRows.count { it in minY..maxY } +
                        (farSize-1) * doubledCols.count { it in minX..maxX }
                sum += closest
            }
        }
        return sum
    }

    fun part1(input: List<String>): Long {
        return calcStars(input, 2)
    }

    // part2

    fun part2(input: List<String>, farSize: Int): Long {
        return calcStars(input, farSize)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374L)
    check(part2(testInput, 10) == 1030L)
    check(part2(testInput, 100) == 8410L)

    val input = readInput("Day11")
    part1(input).println() // 9627977
    part2(input, 1000000).println() // 644248339497
}
