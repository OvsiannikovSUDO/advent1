import kotlin.math.min

fun main() {
    val reg = "\\W+".toRegex()

    fun getNumberOfWins(s: String): Int {
        val parts = s.split(':', '|')
        val win = parts[1].trim()
                .split(reg)
                .map { it.toInt() }
                .toSet()
        return parts[2].trim()
                .split(reg)
                .map { it.toInt() }
                .count { win.contains(it) }
    }

    fun part1(input: List<String>): Int {
        return input.map { s -> getNumberOfWins(s) }
                .filter { it > 0 }
                .sumOf { 1 shl (it-1) }
    }

    // part2

    fun part2(input: List<String>): Int {
        val copies = mutableMapOf<Int, Int>()
        input.forEachIndexed { idx, s ->
            val cards = copies.computeIfAbsent(idx) {1}
            val win = getNumberOfWins(s)
            for (i in idx + 1..min(idx + win, input.size-1)) {
                copies[i] = (copies[i] ?:1) + cards
            }
        }
        return copies.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println() // 20855
    part2(input).println() // 5489600
}
