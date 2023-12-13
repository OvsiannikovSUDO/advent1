import kotlin.math.min

fun main() {

    fun findVerticalReflection(lines: List<String>, smudge: Int = 0): Int {
        for(i in 1..<lines.size) {
            var diff = 0
            for(j in 0..<min(i,lines.size-i)) {
                for (k in lines[i+j].indices) {
                    if (lines[i + j][k] != lines[i - j - 1][k]) {
                        diff++
                    }
                }
            }
            if (diff == smudge) {
                //"= $i".println()
                return i
            }
        }
        //"= 0".println()
        return 0
    }

    fun findHorizontalReflection(lines: List<String>, smudge: Int = 0): Int {
        val transposed = mutableListOf<String>()
        for(i in lines.first().indices) {
            val s = lines.map { it[i] }.joinToString(separator = "")
            transposed.add(s)
        }
        return findVerticalReflection(transposed, smudge)
    }

    fun part1(input: List<String>): Int {
        val parsed = input.fold(mutableListOf(mutableListOf<String>())) { acc, s ->
            if (s.isEmpty()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(s)
            }
            acc
        }
        return parsed.sumOf { 100 * findVerticalReflection(it) + findHorizontalReflection(it) }
    }

    // part2

    fun part2(input: List<String>): Int {
        val parsed = input.fold(mutableListOf(mutableListOf<String>())) { acc, s ->
            if (s.isEmpty()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(s)
            }
            acc
        }
        return parsed.sumOf { 100 * findVerticalReflection(it, 1) + findHorizontalReflection(it, 1) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println() // 33728
    part2(input).println() // 28235
}
